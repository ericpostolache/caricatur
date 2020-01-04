package com.example.caricatur;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageWorkActivity extends Activity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    /* image to caricaturize */
    private ImageView imageView;
    private ImageView filter1;
    private ImageView filter2;
    private ImageView filter3;
    private ImageView filter4;
    private ImageView filter5;
    private ImageView filter6;
    private ImageView filter7;
    private ImageView filter8;
    private ImageView filter9;
    private ImageView filter10;

    List<Filter> filters;
    List<ImageView> filterList = new ArrayList<>();
    Bitmap originalImageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_work_activity);
        imageView = findViewById(R.id.imageview);
        filter1 = findViewById(R.id.filter1);
        filter2 = findViewById(R.id.filter2);
        filter3 = findViewById(R.id.filter3);
        filter4 = findViewById(R.id.filter4);
        filter5 = findViewById(R.id.filter5);
        filter6 = findViewById(R.id.filter6);
        filter7 = findViewById(R.id.filter7);
        filter8 = findViewById(R.id.filter8);
        filter9 = findViewById(R.id.filter9);
        filter10 = findViewById(R.id.filter10);

        filterList.add(filter1);
        filterList.add(filter2);
        filterList.add(filter3);
        filterList.add(filter4);
        filterList.add(filter5);
        filterList.add(filter6);
        filterList.add(filter7);
        filterList.add(filter8);
        filterList.add(filter9);
        filterList.add(filter10);


        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            loadImageFromStorage((String) this.getIntent().getExtras().get("path"));
        }

        for (int i = 0; i < 10; i++) {
            try {
                filterList.get(i).setImageResource(R.drawable.image_filter);
                filterList.get(i).setImageBitmap(applyRandomFilter(i+1));
                int finalI = i;
                filterList.get(i).setOnClickListener(click -> {
                    imageView.setImageBitmap(originalImageBitmap);
                    imageView.setImageBitmap(applyRandomFilterOnImage(finalI +1));
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap applyRandomFilterOnImage(int i) {
        Bitmap input = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        input = input.copy( Bitmap.Config.ARGB_8888 , true);
        filters = FilterPack.getFilterPack(getBaseContext());
        Filter filter = filters.get(i);
        Bitmap output = filter.processFilter(input);
        output = output.copy( Bitmap.Config.ARGB_8888 , true);

        return output;
    }

    /* loads image received from main activity */
    private void loadImageFromStorage(String path) {

        try {
            File file = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            imageView.setImageBitmap(b);
            originalImageBitmap = b;
            faceDetection(b);
//            applyRandomFilter(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    /* applies a random filter from list */
//    private void applyRandomFilter(Bitmap input) {
//
//        Random rand = new Random();
//
//        input = input.copy( Bitmap.Config.ARGB_8888 , true);
//        filters = FilterPack.getFilterPack(getBaseContext());
//        Filter filter = filters.get(rand.nextInt(filters.size()));
//        Bitmap output = filter.processFilter(input);
//        output = output.copy( Bitmap.Config.ARGB_8888 , true);
//
//        imageView.setImageBitmap(output);
//    }

    /* applies a random filter from list */
    private Bitmap applyRandomFilter(int i) throws FileNotFoundException {
        Bitmap input = ((BitmapDrawable) filterList.get(i-1).getDrawable()).getBitmap();

        input = input.copy( Bitmap.Config.ARGB_8888 , true);
        filters = FilterPack.getFilterPack(getBaseContext());
        Filter filter = filters.get(i);
        Bitmap output = filter.processFilter(input);
        output = output.copy( Bitmap.Config.ARGB_8888 , true);

        return output;
    }

    private void faceDetection(Bitmap input) {

        input = input.copy( Bitmap.Config.ARGB_8888 , true);

        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(input.getWidth(), input.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(input, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if(!faceDetector.isOperational()) {
            Toast.makeText(ImageWorkActivity.this, "Error detecting face",
                    Toast.LENGTH_LONG).show();

            return;
        }

        Frame frame = new Frame.Builder().setBitmap(input).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

        }
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }
}
