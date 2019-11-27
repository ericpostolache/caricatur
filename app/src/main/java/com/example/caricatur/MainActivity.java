package com.example.caricatur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.content.FileProvider;



public class MainActivity extends Activity {

    static int REQUEST_TAKE_PHOTO = 1;
    static int GALLERY_REQUEST_CODE = 1;
    static int INTENT_TYPE = 0;
    private ImageView imageView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && INTENT_TYPE == 1) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }  else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && INTENT_TYPE == 2) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        } else {
            Toast.makeText(this, "You haven't picked an Image",Toast.LENGTH_LONG).show();
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("Error occurred while creating the File\n");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                INTENT_TYPE = 2;
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
//
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }





    private final ThreadLocal<Button> takePictureButton = new ThreadLocal<Button>();

    private Button importFromGalleryButton;
    private Button takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*takePictureButton.set((Button) findViewById(R.id.button_image));
        imageView = (ImageView) findViewById(R.id.imageview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }*/

        imageView = (ImageView) findViewById(R.id.imageview);
        importFromGalleryButton = (Button) findViewById(R.id.button_image_from_gallery);
        takePicture = (Button) findViewById(R.id.button_image);
        takePicture.setOnClickListener(click -> dispatchTakePictureIntent());
        importFromGalleryButton.setOnClickListener(click ->  {
            getPicFromGallery();
        });
    }

    private void getPicFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        INTENT_TYPE = 1;
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
    }
}
