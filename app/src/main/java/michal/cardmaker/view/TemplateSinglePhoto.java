package michal.cardmaker.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import michal.cardmaker.R;
import michal.cardmaker.presenter.BorderSettingsFragmentListener;
import michal.cardmaker.presenter.InsertTextFragmentListener;
import michal.cardmaker.presenter.StickerFragmentListener;
import michal.cardmaker.presenter.TemplateSinglePhotoPresenter;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;
import michal.cardmaker.view.fragment.BorderSettingFragment;
import michal.cardmaker.view.fragment.EditTextFragment;
import michal.cardmaker.view.fragment.InsertTextFragment;
import michal.cardmaker.view.fragment.SeekBarsFragment;
import michal.cardmaker.view.fragment.StickerFragment;

public class TemplateSinglePhoto extends AppCompatActivity implements StickerFragmentListener, BorderSettingsFragmentListener, InsertTextFragmentListener {

    private ImageView background;
    private ImageView photo;
    private Button merge_button;
    private Button add_item_button;
    private Button borderSettingsButton;
    private Button add_text_button;

    private TextView insertedText;

    private StickerFragment stickerFragment;
    private SeekBarsFragment seekBarsFragment;
    private BorderSettingFragment borderSettingFragment;
    private InsertTextFragment insertTextFragment;

    private TemplateSinglePhotoPresenter templateSinglePhotoPresenter;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final String PREFERENCES_NAME = "myPreferences";
    private static final String PREFERENCES_BORDER_MARGIN = "BORDER_MARGIN";
    private SharedPreferences preferences;

    Bitmap cs;
    Canvas comboImage;
    ImageView item;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;

    private float xCorItem, yCorItem;
    private float xCorText, yCorText;
    float move_x_item;
    float move_y_item;

    float move_x_text;
    float move_y_text;

    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_DRAG = 1;
    int mTouchMode = TOUCH_MODE_NONE;

    private int sticker;

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
        borderSettingsButton = findViewById(R.id.border_button);
        add_text_button = findViewById(R.id.add_text_button);
        insertedText = findViewById(R.id.text);

        insertedText.setVisibility(View.INVISIBLE);
        insertedText.setClickable(false);
        insertedText.setEnabled(false);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        int border_margin_text = preferences.getInt(PREFERENCES_BORDER_MARGIN, 30);

        stickerFragment = new StickerFragment();
        seekBarsFragment = new SeekBarsFragment(item);
        borderSettingFragment = new BorderSettingFragment(this, border_margin_text);
        insertTextFragment = new InsertTextFragment(this);


        if(preferences.contains(PREFERENCES_BORDER_MARGIN))
        {
            int border_margin = preferences.getInt(PREFERENCES_BORDER_MARGIN, 30);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) photo.getLayoutParams();
            params.setMargins(border_margin, border_margin, border_margin, border_margin);
            photo.setLayoutParams(params);
            preferences.edit().remove(PREFERENCES_BORDER_MARGIN).commit();
        }
        else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) photo.getLayoutParams();
            int first_margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics()));
            params.setMargins(first_margin, first_margin, first_margin, first_margin);
            photo.setLayoutParams(params);
            photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // setFragment(this, stickerFragment);

        item.setClickable(false);
        item.setEnabled(false);
        item.setVisibility(View.INVISIBLE);

        insertedText.setClickable(false);
        insertedText.setEnabled(false);
        insertedText.setVisibility(View.INVISIBLE);


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
                saveData();
                onSelectAlbum();
            }
        });

        borderSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, borderSettingFragment);
            }
        });

        add_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, insertTextFragment);
            }
        });

        insertedText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = TOUCH_MODE_DRAG;

                        xCorText = v.getX() - event.getRawX();
                        yCorText = v.getY() - event.getRawY();

                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == TOUCH_MODE_DRAG) {
                            move_x_text = event.getRawX() + xCorText;
                            move_y_text =  event.getRawY() + yCorText;
                            v.animate().x(move_x_text).y(move_y_text).setDuration(0).start();
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
        });


        item.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = TOUCH_MODE_DRAG;

                        xCorItem = v.getX() - event.getRawX();
                        yCorItem = v.getY() - event.getRawY();

                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == TOUCH_MODE_DRAG) {
                            move_x_item = event.getRawX() + xCorItem;
                            move_y_item =  event.getRawY() + yCorItem;
                            v.animate().x(move_x_item).y(move_y_item).setDuration(0).start();
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


        });



        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, stickerFragment);

            }
        });

        merge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FrameLayout.LayoutParams background_params = (FrameLayout.LayoutParams) photo.getLayoutParams();
                Bitmap p = ((BitmapDrawable)photo.getDrawable()).getBitmap();

                Bitmap pho = Bitmap.createScaledBitmap(p, background.getWidth()-background_params.leftMargin*2, background.getHeight()-background_params.topMargin*2, false);

                Bitmap back = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);

                Log.d("Background_margins", String.valueOf(background_params.topMargin) + " " + String.valueOf(background_params.leftMargin) + " " + String.valueOf(background_params.bottomMargin) + " " + String.valueOf(background_params.rightMargin));


                back.eraseColor(getResources().getColor(R.color.colorPrimary));

                Log.d("Background_size", String.valueOf(back.getWidth()) + " " + String.valueOf(back.getHeight()));
                Log.d("Photo_size", String.valueOf(pho.getWidth()) + " " + String.valueOf(pho.getHeight()));

                int width = back.getWidth();
                int height = back.getHeight();

                cs = Bitmap.createBitmap(width, height, back.getConfig());

                comboImage = new Canvas(cs);

                comboImage.drawBitmap(back, 0f, 0f, null);
                comboImage.drawBitmap(pho, (width-pho.getWidth())/2, (height-pho.getHeight())/2, null);

                if(item.getVisibility() == View.VISIBLE)
                {
                    Bitmap item_1 = ((BitmapDrawable) item.getDrawable()).getBitmap();
                    Bitmap b = Bitmap.createScaledBitmap(item_1, (int) (item_1.getWidth() * item.getScaleX()), (int) (item_1.getHeight() * item.getScaleX()), false);
                    Matrix mat = new Matrix();
                    mat.postRotate(item.getRotation(), b.getWidth() / 2, b.getHeight() / 2);
                    mat.postTranslate(item.getX() + item.getWidth() / 2 - b.getWidth() / 2, item.getY() + item.getHeight() / 2 - b.getHeight() / 2);
                    item.setImageMatrix(mat);
                    comboImage.drawBitmap(b, mat , null);
                    Log.d("Item_1", String.valueOf(item.getX() + item.getWidth()/2) + " " + String.valueOf(item.getY() + item.getHeight()/2));
                }

                if(insertedText.getVisibility() == View.VISIBLE)
                {
                    insertedText.buildDrawingCache();
                    Bitmap insertedText_1 = Bitmap.createBitmap(insertedText.getDrawingCache());
                    Bitmap b = Bitmap.createScaledBitmap(insertedText_1, (int) (insertedText_1.getWidth() * insertedText.getScaleX()), (int) (insertedText_1.getHeight() * insertedText.getScaleX()), false);
                    Matrix mat = new Matrix();
                    mat.postRotate(insertedText.getRotation(), b.getWidth() / 2, b.getHeight() / 2);
                    mat.postTranslate(insertedText.getX() + insertedText.getWidth() / 2 - b.getWidth() / 2, insertedText.getY() + insertedText.getHeight() / 2 - b.getHeight() / 2);

                    //Bitmap textAftrMat = Bitmap.createBitmap(insertedText_1, (int)insertedText.getX(), (int)insertedText.getY(), insertedText_1.getWidth(), insertedText_1.getHeight(), mat, false);


                    //insertedText_1.setImageMatrix(mat);
                    comboImage.drawBitmap(b, mat, null);
                    Log.d("Item_1", String.valueOf(item.getX() + item.getWidth()/2) + " " + String.valueOf(item.getY() + item.getHeight()/2));
                }

                Toast.makeText(getApplicationContext(), "Zmergowano", Toast.LENGTH_SHORT).show();

                //mergePhoto.setImageBitmap(cs);

                save_merged();
            }
        });
    }


    private void saveData() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) photo.getLayoutParams();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putInt(PREFERENCES_BORDER_MARGIN, params.bottomMargin);
        preferencesEditor.commit();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int border_margin = savedInstanceState.getInt("BORDER_MARGIN");

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) photo.getLayoutParams();
        params.setMargins(border_margin, border_margin, border_margin, border_margin);
        photo.setLayoutParams(params);
    }

    private void save_merged() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TemplateSinglePhoto.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            //MediaStore.Images.Media.insertImage(getContentResolver(), cs, "merged.jpg", "merged photo");
            //Toast.makeText(getApplicationContext(), "Zapisano", Toast.LENGTH_SHORT).show();
            File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker");
            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/Pictures/CardMaker/");
                wallpaperDirectory.mkdirs();
            }

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(new File("/sdcard/Pictures/CardMaker/"), "merged.jpg");
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

            FrameLayout.LayoutParams backgroundParams = (FrameLayout.LayoutParams) photo.getLayoutParams();

            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_CAMERA, data, background.getWidth()-backgroundParams.leftMargin*2, background.getHeight()-backgroundParams.topMargin*2);
                    Log.d("First_size", String.valueOf(background.getWidth()-backgroundParams.leftMargin*2) + " " + String.valueOf(background.getHeight()-backgroundParams.topMargin*2));
                    break;
                case REQUEST_CODE_ALBUM:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_ALBUM, data, background.getWidth()-backgroundParams.leftMargin*2, background.getHeight()-backgroundParams.topMargin*2);
                    Log.d("First_size", String.valueOf(background.getWidth()-backgroundParams.leftMargin*2) + " " + String.valueOf(background.getHeight()-backgroundParams.topMargin*2));
                    break;
                default:
                    break;
            }
            photo.setBackgroundColor(Color.TRANSPARENT);
            photo.setBackground(null);
        }
        else
        {
            ActivityCompat.requestPermissions(TemplateSinglePhoto.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onFragmentInteraction(int s) {
        this.sticker = s;

        item.setVisibility(View.VISIBLE);
        item.setClickable(true);
        item.setEnabled(true);
        item.setImageResource(sticker);

        templateSinglePhotoPresenter.setFragment(this, seekBarsFragment);

        item.setRotation(0);
        item.setScaleX(1);
        item.setScaleY(1);
        item.setX(background.getX() + background.getWidth()/2 - item.getWidth()/2);
        item.setY(background.getY() + background.getHeight()/2 - item.getHeight()/2);

        Log.d("Sticker_click", String.valueOf(sticker));


    }

    @Override
    public void changeBorderSize(int size) {
        if(size >= 0)
        {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) photo.getLayoutParams();
            int real_size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, Resources.getSystem().getDisplayMetrics()));
            params.setMargins(real_size, real_size, real_size, real_size);
//            photo.setScaleY(params.height/(params.height-real_size*2));
//            photo.setScaleX(1.f);
//            Log.d("Y_SCALE", String.valueOf((photo.getHeight()/(float)(photo.getHeight()-real_size*2))));
            photo.setLayoutParams(params);
        }

    }

    @Override
    public void sendTextView() {
        templateSinglePhotoPresenter.setFragment(this, new EditTextFragment());
    }
}
