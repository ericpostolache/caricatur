package com.example.caricatur;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.Random;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Contour;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ImageWorkActivity extends Activity {

    static {
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
                filterList.get(i).setImageBitmap(applyRandomFilter(i + 1));
                int finalI = i;
                filterList.get(i).setOnClickListener(click -> {
                    imageView.setImageBitmap(originalImageBitmap);
                    imageView.setImageBitmap(applyRandomFilterOnImage(finalI + 1));
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        final Button save = (Button) findViewById(R.id.save);
        final Button reset = (Button) findViewById(R.id.reset);
        final TextView tv_saved = (TextView) findViewById(R.id.tv_saved);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the image from drawable resource as drawable object
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                // Save image to gallery
                MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "Bird",
                        "Image of bird"
                );

                tv_saved.setText("Image saved to gallery.\n");

                CountDownTimer timer = new CountDownTimer(1500, 500) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        tv_saved.setVisibility(View.INVISIBLE);

                        goToMainActivity();
                    }
                }.start();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the image from drawable resource as drawable object
                imageView.setImageBitmap(originalImageBitmap);
            }
        });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_MESSAGE, "Main Activity");
        startActivity(intent);
    }

    private Bitmap applyRandomFilterOnImage(int i) {
        Bitmap input = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        input = input.copy(Bitmap.Config.ARGB_8888, true);
        filters = FilterPack.getFilterPack(getBaseContext());
        Filter filter = filters.get(i);
        Bitmap output = filter.processFilter(input);
        output = output.copy(Bitmap.Config.ARGB_8888, true);

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
        Bitmap input = ((BitmapDrawable) filterList.get(i - 1).getDrawable()).getBitmap();

        input = input.copy(Bitmap.Config.ARGB_8888, true);
        filters = FilterPack.getFilterPack(getBaseContext());
        Filter filter = filters.get(i);
        Bitmap output = filter.processFilter(input);
        output = output.copy(Bitmap.Config.ARGB_8888, true);

        return output;
    }

    private void faceDetection(Bitmap input) {

        input = input.copy(Bitmap.Config.ARGB_8888, true);

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
                .setMode(FaceDetector.SELFIE_MODE)
                .build();

        FaceDetector faceDetectorContours = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                .setMode(FaceDetector.SELFIE_MODE)
                .build();

        if (!faceDetector.isOperational()) {
            Toast.makeText(ImageWorkActivity.this, "Error detecting face",
                    Toast.LENGTH_LONG).show();

            return;
        }

        Frame frame = new Frame.Builder().setBitmap(input).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        SparseArray<Face> facesContours = faceDetectorContours.detect(frame);


        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            Face thisFaceContours = facesContours.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            ArrayList<Landmark> landmarks = new ArrayList<>(thisFace.getLandmarks());
            ArrayList<Contour> contours = new ArrayList<>(thisFaceContours.getContours());
            Log.d("IMG WORK ACTIVITY", "Found this number of landmarks: " + landmarks.size());
            Log.d("IMG WORK ACTIVITY", "Found this number of contours: " + contours.size());

            for (Contour contour : contours) {
                if (contour.getType() == Contour.LEFT_EYE) {
                    imageView.setImageBitmap(adjustContourColor(contour));
                }
                if (contour.getType() == Contour.RIGHT_EYE) {
                    imageView.setImageBitmap(adjustContourColor(contour));
                }


            }


            // OCHI PENTRU OCHI SI DINTE PENTRU DINTE
            Random r = new Random();
            boolean isLeftOpen = r.nextInt(2) % 2 == 1;
            boolean isRightOpen = r.nextInt(2) % 2 == 1;

            Landmark leftEye = null;
            Landmark rightEye = null;
            for (Landmark landmark : landmarks) {

                if (landmark.getType() == Landmark.LEFT_EYE) {
                    leftEye = landmark;
                }

                if (landmark.getType() == Landmark.RIGHT_EYE) {
                    rightEye = landmark;
                }

            }

            if (leftEye != null && rightEye != null) {
                float distance = Math.abs(leftEye.getPosition().x - rightEye.getPosition().x);
                Log.d("IMG WORK ACTIVIty", Float.toString(distance));

                overlayEyeBitmap(tempCanvas, isLeftOpen, leftEye.getPosition().x, leftEye.getPosition().y, distance);
                overlayEyeBitmap(tempCanvas, isRightOpen, rightEye.getPosition().x, rightEye.getPosition().y, distance);

                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

            }


//            for (Landmark l : landmarks) {
//                int cx = (int) (l.getPosition().x * 1);
//                int cy = (int) (l.getPosition().y * 1);
//                //tempCanvas.drawCircle(cx, cy, 10, myRectPaint);
//                Log.d("IMG WORK ACTIVITY", FaceLandmarks.convert(l.getType()).toString() + " at " + l.getPosition());
//                /* Positions of facial features + Enum class */
//            }

        }

        //imageView.setImageBitmap(input);
        //imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private static void overlayEyeBitmap(Canvas canvas, boolean eyeClosed, float cx, float cy, float eye_distance) {
        float radius = eye_distance / 3;

        // draw the eye's background circle with appropriate color
        Paint paintFill = new Paint();
        paintFill.setStyle(Paint.Style.FILL);
        if (eyeClosed)
            paintFill.setColor(Color.YELLOW);
        else
            paintFill.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paintFill);

        // draw a black border around the eye
        Paint paintStroke = new Paint();
        paintStroke.setColor(Color.BLACK);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(5);
        canvas.drawCircle(cx, cy, radius, paintStroke);

        if (eyeClosed)
            // draw horizontal line across closed eye
            canvas.drawLine(cx - radius, cy, cx + radius, cy, paintStroke);
        else {
            // draw big off-center pupil on open eye
            paintFill.setColor(Color.BLACK);
            float cxPupil = cx - 10;
            float cyPupil = cy + 10;
            canvas.drawCircle(cxPupil, cyPupil, radius / 2, paintFill);
        }
    }

    private Bitmap adjustContourColor(Contour contour) {
        Bitmap input = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bmOut = input.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bmOut);

        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //myPaint.setColor(Color.parseColor("#99ff0000"));
        myPaint.setColor(getColorWithAlpha(Color.RED, 0.4f));

        Path path = new Path();
        path.moveTo(contour.getPositions()[0].x, contour.getPositions()[0].y);

        for (PointF point : contour.getPositions()) {
            path.lineTo(point.x, point.y);
            Log.d("IMG WORK ACTIVITY", "SET PIXELS: " + (int) point.x + " " + (int) point.y);
        }

        path.close();

        canvas.drawPath(path, myPaint);

        Log.d("IMG WORK ACTIVITY", bmOut.getWidth() + " " + bmOut.getHeight());

        return bmOut;
    }


    // GETS TRANSPARENT COLOR loooool
    private static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }
}
