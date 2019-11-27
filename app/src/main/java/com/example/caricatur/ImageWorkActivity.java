package com.example.caricatur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageWorkActivity extends Activity {
    /* image to caricaturize */
    public ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_work_activity);
        imageView = (ImageView) findViewById(R.id.imageview);
        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            loadImageFromStorage((String) this.getIntent().getExtras().get("path"));
        }

    }

    /* loads image received from main activity */
    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
