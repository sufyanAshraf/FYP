package com.example.camera_activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ligl.android.widget.iosdialog.IOSDialog;

public class Info_activity extends AppCompatActivity {

    private String info;
    private String heading, msg, city, id;
    TextView heading_field, info_field;
    private String near_msg, near_name;
    private String  fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        heading_field = findViewById(R.id.heading);
        info_field = findViewById(R.id.info);

        heading = getIntent().getStringExtra("Heading");
        id = getIntent().getStringExtra("ID");
        city = getIntent().getStringExtra("City");
        msg = getIntent().getStringExtra("msg");
        info = getIntent().getStringExtra("Info");
        display();

        Button cuisine = findViewById(R.id.cusine);
        cuisine.setText("Try cuisine of "+city);
        findViewById(R.id.near).setOnClickListener(it -> ShowNearest());
        cuisine.setOnClickListener(it -> showItems());

    }
    private void display(){
        heading_field.setText(heading);
        TextView a = findViewById(R.id.mess);
        a.setText(msg);

        info_field.setText(info);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    public void ShowNearest(){

        String _id = id;
        String _city = city;
        if (_id.length() > 0 && _city.length() > 0) {
            Database.getInstance(this).getNearLandmark(_id, _city, new Database.Callback<Database.ObjectNear>() {
                @Override
                public void callbackFunctionSuccess(Database.ObjectNear result) {
                    if (result.Name.trim().length() > 0) {
                        near_name = result.Name;
                        near_msg = result.Message;

                        new IOSDialog.Builder(Info_activity.this)
                                .setTitle(near_name)
                                .setMessage(near_msg +"...")
                                .setNegativeButton("Cancel", null).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect ID ", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void callbackFunctionFailure() {
                    Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_LONG).show();
                }
            });
        }
        else { Toast.makeText(getApplicationContext(), "system problem", Toast.LENGTH_LONG).show(); }
    }

    public void showItems(){

        if (city.trim().length() > 0) {

            Database.getInstance(this).getFoodData(city.trim(), new Database.Callback<Database.foodObject>() {
                @Override
                public void callbackFunctionSuccess(Database.foodObject result) {
                    if (result.id != -1) {
                        int resultID =result.id;
                        fname = result.Name;
                        Dialog  dialog= new Dialog(Info_activity.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                        dialog.setContentView(R.layout.image_dialog_box);
                        Button btn = (Button)dialog.findViewById(R.id.close);
                        btn.setEnabled(true);

                        btn.setOnClickListener(it -> {dialog.cancel();});
                        ImageView imgView = dialog.findViewById(R.id.imgView);
//                        imgView.setImageResource(R.drawable.tikka);
                        TextView txt=dialog.findViewById(R.id.Imgmessage);
                        txt.setText("you should try "+fname+".");

                        Database.getInstance(Info_activity.this).getFoodImage( fname, new Database.Callback<Bitmap>() {
                            @Override
                            public void callbackFunctionSuccess(Bitmap result1) {
                                if (result1 != null) {
                                    imgView.setImageBitmap(result1);
                                    dialog.show();
                                }
                            }
                            @Override
                            public void callbackFunctionFailure() {
                                Toast.makeText(getApplicationContext(), "Unable to Load image(s)", Toast.LENGTH_LONG).show();
                            }
                        });



                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect ID ", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void callbackFunctionFailure() {
                    Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_LONG).show();
                }
            });
        }
        else { Toast.makeText(getApplicationContext(), "system problem", Toast.LENGTH_LONG).show(); }
    }

}
