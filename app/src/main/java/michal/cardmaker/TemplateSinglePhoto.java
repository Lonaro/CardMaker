package michal.cardmaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import michal.cardmaker.presenter.ResizeAnimation;
import michal.cardmaker.presenter.TemplateSinglePhotoPresenter;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;

public class TemplateSinglePhoto extends AppCompatActivity {

    private ImageView background;
    private ImageView photo;
    private Button merge_button;
    private Button add_item_button;
    private TemplateSinglePhotoPresenter templateSinglePhotoPresenter;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    Bitmap cs;
    Canvas comboImage;
    ImageView item;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;

    //***************************************************************
    private float xCoOrdinate, yCoOrdinate;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    float scale = 1;
    float globalScale;
    float dx;
    float dy;
    float r;
    //***************************************************************

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_single_photo);

        templateSinglePhotoPresenter = new TemplateSinglePhotoPresenter(TemplateSinglePhoto.this);

        photo = findViewById(R.id.temp_single_photo);
        merge_button = findViewById(R.id.merge_button);
        background = findViewById(R.id.background);
        item = findViewById(R.id.item);
        add_item_button = findViewById(R.id.add_item_button);


        item.setClickable(false);
        item.setEnabled(false);
        item.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();

        if (intent == null && intent.getData() == null) {
            photo.setImageResource(R.drawable.camera);
        }
        else
        {
            InputStream input = null;
            try {
                if (intent != null && intent.getData() != null) {
                    input = getContentResolver().openInputStream(intent.getData());
                    photo.setImageBitmap(BitmapFactory.decodeStream(input));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                CropUtils.closeSilently(input);
            }
        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAlbum();
            }
        });



        item.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float move_x = 0;
                float move_y = 0;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();
                        mode = DRAG;

                        photo.setClickable(false);
                        background.setClickable(false);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);

                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        photo.setClickable(true);
                        background.setClickable(true);
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(mode == DRAG) {
                            //v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                            move_x = event.getRawX() + xCoOrdinate;
                            move_y =  event.getRawY() + yCoOrdinate;
                            v.animate().x(move_x).y(move_y).setDuration(0).start();

                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);

                            if(newDist > 100f) {
                                scale = (newDist / oldDist);

                                //Log.d("Scale", String.valueOf(newDist) + " " + String.valueOf(oldDist) + " " + String.valueOf(scale));
                                //item.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
                            }
                            if(lastEvent != null && event.getPointerCount() == 2) {
                                newRot = rotation(event);
                                r = newRot - d;

                                v.animate().rotation(r).setInterpolator(new LinearInterpolator()).setDuration(0).start();
                                Log.d("Rotation", String.valueOf(d) + " | " + String.valueOf(newRot) + " | " + String.valueOf(r));
                                item.setRotation(r);

                            }
                        }
                        break;

                }

                return true;
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            /**
             * Calculate the mid point of the first two fingers
             */
            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }

            /**
             * Calculate the degree to be rotated by.
             *
             * @param event
             * @return Degrees
             */
            private float rotation(MotionEvent event) {
                double delta_x = (event.getX(0) - event.getX(1));
                double delta_y = (event.getY(0) - event.getY(1));
                double radians = Math.atan2(delta_y, delta_x);
                return (float) Math.toDegrees(radians);
            }
        });



        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item.setVisibility(View.VISIBLE);
                item.setClickable(true);
                item.setEnabled(true);
                item.setImageResource(R.drawable.smile_item);

            }
        });

        merge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap pho = ((BitmapDrawable)photo.getDrawable()).getBitmap();
                Bitmap back = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
                back.eraseColor(getResources().getColor(R.color.colorPrimary));

                int width = back.getWidth();
                int height = back.getHeight();

                cs = Bitmap.createBitmap(width, height, back.getConfig());

                comboImage = new Canvas(cs);

                comboImage.drawBitmap(back, 0f, 0f, null);
                comboImage.drawBitmap(pho, (width-pho.getWidth())/2, (height-pho.getHeight())/2, null);

                Toast.makeText(getApplicationContext(), "Zmergowano", Toast.LENGTH_SHORT).show();

                //mergePhoto.setImageBitmap(cs);

                save_merged();
            }
        });
    }

    private void save_merged() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TemplateSinglePhoto.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            //MediaStore.Images.Media.insertImage(getContentResolver(), cs, "merged.jpg", "merged photo");
            //Toast.makeText(getApplicationContext(), "Zapisano", Toast.LENGTH_SHORT).show();

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, "merged.jpg");
            try {
                OutputStream out = null;
                out = new FileOutputStream(file);

                cs.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                Toast.makeText(getApplicationContext(), "Zapisano", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            ActivityCompat.requestPermissions(TemplateSinglePhoto.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void onSelectAlbum() {


        if (Build.VERSION.SDK_INT <= 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_ALBUM);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_ALBUM);
        }

//		if (Build.VERSION.SDK_INT < 19) {
//			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("image/*");
//			startActivityForResult(intent, REQUEST_CODE_ALBUM);
//		} else {
//			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			intent.setType("image/*");
//			startActivityForResult(intent, REQUEST_CODE_ALBUM);
//		}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                templateSinglePhotoPresenter.startCropper(REQUEST_CODE_CAMERA, data, 280, 180);
                break;
            case REQUEST_CODE_ALBUM:
                templateSinglePhotoPresenter.startCropper(REQUEST_CODE_ALBUM, data, 280, 180);
                break;
            default:
                break;
        }
    }
}
