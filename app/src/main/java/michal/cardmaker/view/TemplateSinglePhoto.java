package michal.cardmaker.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import michal.cardmaker.R;
import michal.cardmaker.presenter.MainActivityPresenter;
import michal.cardmaker.presenter.TemplateSinglePhotoPresenter;
import michal.cardmaker.presenter.adapter.ItemAdapter;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;
import michal.cardmaker.presenter.cropViewLibrary.GeometryMathUtils;

public class TemplateSinglePhoto extends AppCompatActivity {

    private ImageView background;
    private ImageView photo;
    private Button merge_button;
    private Button add_item_button;

    private SeekBar seekBar_rotate;
    private SeekBar seekBar_scale;

    private StickerFragment stickerFragment;

    private TemplateSinglePhotoPresenter templateSinglePhotoPresenter;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    Bitmap cs;
    Canvas comboImage;
    ImageView item;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;

    //***************************************************************
    private float xCoOrdinate, yCoOrdinate;
    float move_x;
    float move_y;


    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_DRAG = 1;
    int mTouchMode = TOUCH_MODE_NONE;

    float mLastX = 0;
    float mLastY = 0;


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
        seekBar_rotate = findViewById(R.id.seekBar_rotate);
        seekBar_scale = findViewById(R.id.seekBar_scale);

        stickerFragment = new StickerFragment();
        setFragment(this, stickerFragment);

        seekBar_rotate.setMax(360);
        seekBar_rotate.setProgress(180);
        seekBar_scale.setMax(200);

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

        seekBar_rotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                item.setRotation(progress-180);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                item.setScaleX((float)(progress)/100.f);
                item.setScaleY((float)(progress)/100.f);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        item.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = TOUCH_MODE_DRAG;

                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();

                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == TOUCH_MODE_DRAG) {
                            move_x = event.getRawX() + xCoOrdinate;
                            move_y =  event.getRawY() + yCoOrdinate;
                            v.animate().x(move_x).y(move_y).setDuration(0).start();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        photo.setEnabled(true);
                        photo.setClickable(true);
                        background.setEnabled(true);
                        background.setClickable(true);
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_POINTER_UP:
                        mTouchMode = TOUCH_MODE_NONE;
                        break;
                }


                return true;
            }

            private float getDistance(MotionEvent event) {
                return GeometryMathUtils.getDistance(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
            }

            private void getCenter(PointF point, MotionEvent event) {
                float x = mLastX + event.getX(1);
                float y = mLastY + event.getY(1);
                point.set(x / 2, y / 2);
            }

            private float getDegrees(MotionEvent event) {
                return GeometryMathUtils.getDegrees(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
            }
        });



        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item.setVisibility(View.VISIBLE);
                item.setClickable(true);
                item.setEnabled(true);
                item.setImageResource(R.drawable.sun);

            }
        });

        merge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap pho = ((BitmapDrawable)photo.getDrawable()).getBitmap();
                Bitmap back = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);



                Bitmap item_1 = ((BitmapDrawable)item.getDrawable()).getBitmap();


                Bitmap b = Bitmap.createScaledBitmap(item_1, (int)(item_1.getWidth()*item.getScaleX()), (int)(item_1.getHeight()*item.getScaleX()), false);
                Matrix mat = new Matrix();
                mat.postRotate(item.getRotation(), b.getWidth()/2, b.getHeight()/2);
                mat.postTranslate(item.getX() + item.getWidth()/2-b.getWidth()/2, item.getY() + item.getHeight()/2-b.getHeight()/2);
                item.setImageMatrix(mat);
                //Bitmap r = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mat, true);


                back.eraseColor(getResources().getColor(R.color.colorPrimary));

                Log.d("Backgroud_size", String.valueOf(back.getWidth()) + " " + String.valueOf(back.getHeight()));
                Log.d("Photo_size", String.valueOf(pho.getWidth()) + " " + String.valueOf(pho.getHeight()));

                int width = back.getWidth();
                int height = back.getHeight();

                cs = Bitmap.createBitmap(width, height, back.getConfig());

                comboImage = new Canvas(cs);

                comboImage.drawBitmap(back, 0f, 0f, null);
                comboImage.drawBitmap(pho, (width-pho.getWidth())/2, (height-pho.getHeight())/2, null);

                if(item.getVisibility() == View.VISIBLE)
                {
                    comboImage.drawBitmap(b, mat , null);
                    Log.d("Item_1", String.valueOf(item.getX() + item.getWidth()/2) + " " + String.valueOf(item.getY() + item.getHeight()/2));
                }

                Toast.makeText(getApplicationContext(), "Zmergowano", Toast.LENGTH_SHORT).show();

                //mergePhoto.setImageBitmap(cs);

                save_merged();
            }
        });
    }

    public void setFragment(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout2, fragment);
        fragmentTransaction.commit();
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

        if(ContextCompat.checkSelfPermission(TemplateSinglePhoto.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_CAMERA, data, photo.getWidth()-20, photo.getHeight()-20);
                    break;
                case REQUEST_CODE_ALBUM:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_ALBUM, data, photo.getWidth()-20, photo.getHeight()-20);

                    break;
                default:
                    break;
            }
        }
        else
        {
            ActivityCompat.requestPermissions(TemplateSinglePhoto.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
}
