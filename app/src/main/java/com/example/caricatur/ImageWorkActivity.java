package com.example.caricatur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

public class ImageWorkActivity extends Activity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    /* image to caricaturize */
    private ImageView imageView;
    List<Filter> filters;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_work_activity);
        imageView = findViewById(R.id.imageview);
        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            loadImageFromStorage((String) this.getIntent().getExtras().get("path"));
        }

    }

    /* loads image received from main activity */
    private void loadImageFromStorage(String path) {

        try {
            File file = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            applyRandomFilter(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* applies a random filter from list */
    private void applyRandomFilter(Bitmap input) {

        Random rand = new Random();

        input = input.copy( Bitmap.Config.ARGB_8888 , true);
        filters = FilterPack.getFilterPack(getBaseContext());
        Filter filter = filters.get(rand.nextInt(filters.size()));
        Bitmap output = filter.processFilter(input);
        output = output.copy( Bitmap.Config.ARGB_8888 , true);

        imageView.setImageBitmap(output);
    }
}
