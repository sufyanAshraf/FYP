package com.example.camera_activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Model {

    private static final int RESULTS_TO_SHOW = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private final Interpreter.Options interpreterOptions = new Interpreter.Options();
    private Interpreter interpreter;
    private List<String> labelList;

    private String model_name;
    private int X_diamension = 224;
    private int Y_diamension = 224;
    private int Size = 3;
    Context context;
    String result;
    private int[] Values;

    private ByteBuffer imgBuffer = null;
    private float[][] labelProbArray = null;
//    private String[] topConfidence = null;
    private String[] topLables = null;

    // priority queue
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    public Model(String model_name, Context context ) {
        this.context = context;
        this.model_name = model_name;

        Values = new int[X_diamension * Y_diamension];

        try{
            interpreter = new Interpreter(load_Model(), interpreterOptions);
            labelList = Load_Labels();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        imgBuffer = ByteBuffer.allocateDirect( 4 * X_diamension * Y_diamension * Size);
        imgBuffer.order(ByteOrder.nativeOrder());
        labelProbArray = new float[1][labelList.size()];
        topLables = new String[RESULTS_TO_SHOW];
//        topConfidence = new String[RESULTS_TO_SHOW];
    }
    //predict landmark
    public  String predict(String f){
        Bitmap bitmap = getResizedBitmap(BitmapFactory.decodeFile(f), X_diamension, Y_diamension);
        converter(bitmap);
        interpreter.run(imgBuffer, labelProbArray);
        Top_Labels();
        bitmap.recycle();
        return result;
    }

    // loads model
    private MappedByteBuffer load_Model() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(model_name);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // return the top result
    private void Top_Labels() {
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
        }
        result = topLables[2];
    }

    // converts bitmap to byte array
    private void converter(Bitmap bitmap) {
        if (imgBuffer == null) { return; }
        imgBuffer.rewind();
        bitmap.getPixels(Values, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < X_diamension; ++i) {
            for (int j = 0; j < Y_diamension; ++j) {
                final int val = Values[pixel++];
                imgBuffer.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgBuffer.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
    }

    // resizes bitmap to given dimensions
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    // loads the labels
    private List<String> Load_Labels() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(context.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

}
