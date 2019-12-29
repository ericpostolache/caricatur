package com.example.caricatur;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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

        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            loadImageFromStorage((String) this.getIntent().getExtras().get("path"));
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
}
