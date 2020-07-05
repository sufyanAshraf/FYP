package com.example.camera_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.google.common.util.concurrent.ListenableFuture;
import com.ligl.android.widget.iosdialog.IOSDialog;

import static java.lang.Boolean.parseBoolean;

public class MainActivity extends AppCompatActivity {

    ImageButton torchBtn;

    private String info, name, message, ID, city;
    ProgressDialog pd;

    //Save to FILE
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private static final int REQUEST_WRITE_PERMISSION = 1;

    // name of model
    String model_name = "model_1.tflite";
    String file_path;
    Boolean IsReplica = false;
    double long_min, long_max,  lat_min, lat_max;

    Preview preview;
    ImageCapture imageCapture;
    Camera camera;
    ExecutorService cameraExecutor;
    CameraControl cameraControl;
    CameraInfo cameraInfo;
    PreviewView viewFinder;

    static boolean isTorchOn = false;
    SharedPreferences sharePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Database.getInstance(this).initialize_server();

        if (allPermissionsGranted()) {
            startCamera();
            turnGPD();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        torchBtn = findViewById(R.id.camera_torch_button);
        torchBtn.setImageResource(R.drawable.flash_off);

        sharePreferences = getSharedPreferences("flash_info", Context.MODE_PRIVATE);
        String temp = sharePreferences.getString("isFlashOn", null);
        if(temp != null){
            isTorchOn = parseBoolean(temp);
            if(isTorchOn){
                torchBtn.setImageResource(R.drawable.flash_on);
            }
        }

        viewFinder = findViewById(R.id.viewFinder);

        findViewById(R.id.camera_capture_button).setOnClickListener(it -> { takePhoto(); });

        torchBtn.setOnClickListener(it -> toggleTorch());
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera(){
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                preview = new Preview.Builder().build();

                imageCapture = new ImageCapture.Builder().setTargetResolution(new Size(500, 400))
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();
//                imageCapture = (new ImageCapture.Builder()).build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                try {
                    cameraProvider.unbindAll();

                    try {
                        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                        cameraControl = camera.getCameraControl();
                        cameraInfo = camera.getCameraInfo();
                        if (isTorchOn){cameraControl.enableTorch(true);}
                    } catch (Exception exc) {
                        Log.e("Main", "Use case binding failed", exc);
                    }

                    if (preview != null) {
                        preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera != null ? cameraInfo : null));
                    }
                } catch (Exception exc) {
                    Log.e("Main", "error in take photo", exc);
                }

                viewFinder.setPreferredImplementationMode(PreviewView.ImplementationMode.TEXTURE_VIEW);

                if (preview != null) {
                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider(cameraInfo));
                }

            } catch (Exception exc) {
                Log.e("Main", "camera provider", exc);
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void toggleTorch() {
        if (cameraInfo.getTorchState().getValue() != null && cameraInfo.getTorchState().getValue() == TorchState.ON) {
            cameraControl.enableTorch(false);
            isTorchOn = false;
            torchBtn.setImageResource(R.drawable.flash_off);
        } else {
            cameraControl.enableTorch(true);
            isTorchOn =true;
            torchBtn.setImageResource(R.drawable.flash_on);
        }
    }

    private void takePhoto() {

        if (imageCapture != null) {
            progressDialogBox();
            pd.setCanceledOnTouchOutside(false);
            file_path = getOutputDirectory()+"/a.jpg";
            file = new File(file_path);

            ImageCapture.OutputFileOptions outputOptions = (new ImageCapture.OutputFileOptions.Builder(file)).build();
            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback(){
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                    Thread work = new Thread(new LoadModel());
                    work.start();
                    try {
                        work.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "ok"+ID, Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                public void onError(@NonNull ImageCaptureException exc) {
                    Log.e("Main", "Photo capture failed: " + exc.getMessage(), exc);
                    Toast.makeText(MainActivity.this, exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
            startCamera();
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
        if(requestCode == REQUEST_WRITE_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use external storage without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use gps without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
            turnGPD();
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 5);
        }
        if(requestCode == 5) {
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Internet access id required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    public void turnGPD(){
        final LocationManager manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            new IOSDialog.Builder(this)
                    .setTitle("Turn Gps")
                    .setMessage("App required Gps to work, please turn on")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "App required Gps to work", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).show();
        }
    }


    public final File getOutputDirectory() {
        File[] files = getExternalMediaDirs();
        File file = files[0];
        if (file != null) {
            File newfile = new File(file, "folder");
            newfile.mkdirs();
            file = newfile;
        } else {
            file = null;
        }

        File mediaDir = file;
        if (mediaDir != null && mediaDir.exists()) {
            file = mediaDir;
        } else {
            file = getFilesDir();
        }
        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.putString("isFlashOn", Boolean.toString(isTorchOn));
        editor.apply();
    }

    private void progressDialogBox(){
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading...");
        pd.show();
    }

    public void displayInfo(){
        pd.dismiss();
        if(Integer.parseInt(ID) == 0){
            Suggestion_dialogbox dialogbox = new Suggestion_dialogbox();
            dialogbox.show(getSupportFragmentManager(),null);
            return;
        }
        if(IsReplica) {
            name = "Replica of "+name;
            new IOSDialog.Builder(this)
                    .setTitle(name)
                    .setNegativeButton("Cancel", null).show();
            return;
        }

        new IOSDialog.Builder(this)
                .setTitle(name)
                .setMessage(message +"...")
                .setPositiveButton("Details", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i=new Intent(MainActivity.this, Info_activity.class);
                        i.putExtra("Heading", name);
                        i.putExtra("ID", ID);
                        i.putExtra("msg", message);
                        i.putExtra("City", city);
                        i.putExtra("Info", info);

                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }

    private boolean replicaCheck(){
        Location_tracker gt = new Location_tracker(getApplicationContext());
        Location l = gt.getLocation();
        if( l == null){
            Toast.makeText(getApplicationContext(),"Unable to connect with GPS",Toast.LENGTH_SHORT).show();
        }else {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            lon = Double.parseDouble(new DecimalFormat("##.###").format(lon));
            lat = Double.parseDouble(new DecimalFormat("##.###").format(lat));
            
            return IsReplica(lat, lon);
        }
        return true;
    }
	
	private boolean IsReplica(double lat, double lon){
		if ((long_min <= lon && long_max >= lon ) && (lat_min <= lat && lat_max >= lat)){
            return false;
		}
		return true;
	}

    public void retrive(){
        Database.getInstance(this).getData(ID, new Database.Callback<Database.myObject>() {
            @Override
            public void callbackFunctionSuccess(Database.myObject result) {
                if (result.ID != -1) {
                    name = result.Name;
                    message = result.Message;
                    city = result.City;
                    info = result.Information;
                    long_min = result.Long_min;
                    long_max = result.Long_max;
                    lat_min = result.Lat_min;
                    lat_max = result.Lat_max;

                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect ID ", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(ID)  != 0) { IsReplica = replicaCheck();}
                displayInfo();
            }
            @Override
            public void callbackFunctionFailure() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class LoadModel implements Runnable{

        @Override
        public void run() {
            Model model = new Model(model_name, MainActivity.this);
            ID = model.predict(file_path);

            ID ="1";
            if (Integer.parseInt(ID)  != 0) {
                retrive();
            }

            file.delete();
            file = null;
        }
    }

}
