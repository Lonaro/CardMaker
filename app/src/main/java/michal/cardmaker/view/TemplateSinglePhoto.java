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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;

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
    private ImageView item;

    private TextView insertedText;

    private StickerFragment stickerFragment;
    private SeekBarsFragment seekBarsFragment;
    private BorderSettingFragment borderSettingFragment;
    private InsertTextFragment insertTextFragment;

    private TemplateSinglePhotoPresenter templateSinglePhotoPresenter;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final String PREFERENCES_NAME = "myPreferences";
    private static final String PREFERENCES_BORDER_MARGIN = "BORDER_MARGIN";
    private static final String PREFERENCES_BORDER_COLOR = "BORDER_COLOR";

    private static final String PREFERENCES_ITEM_X= "ITEM_X";
    private static final String PREFERENCES_ITEM_Y = "ITEM_Y";
    private static final String PREFERENCES_ITEM_SCALE = "ITEM_SCALE";
    private static final String PREFERENCES_ITEM_ROTATION = "ITEM_ROTATION";
    private static final String PREFERENCES_ITEM_IMAGE = "ITEM_IMAGE";

    private static final String PREFERENCES_TEXT_X= "TEXT_X";
    private static final String PREFERENCES_TEXT_Y = "TEXT_Y";
    private static final String PREFERENCES_TEXT_SCALE = "TEXT_SCALE";
    private static final String PREFERENCES_TEXT_ROTATION = "TEXT_ROTATION";
    private static final String PREFERENCES_TEXT_FONT = "TEXT_FONT";
    private static final String PREFERENCES_TEXT_COLOR = "TEXT_COLOR";
    private static final String PREFERENCES_TEXT_VALUE = "TEXT_VALUE";



    private SharedPreferences preferences;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;

    private float xCorItem, yCorItem;
    private float xCorText, yCorText;
    float move_x_item;
    float move_y_item;
    int actual_sticker;
    public String actual_font;

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

        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        int border_margin_text = preferences.getInt(PREFERENCES_BORDER_MARGIN, 30);

        stickerFragment = new StickerFragment();
        seekBarsFragment = new SeekBarsFragment(item);
        borderSettingFragment = new BorderSettingFragment(this, border_margin_text);
        insertTextFragment = new InsertTextFragment(this);

        if(preferences.contains(PREFERENCES_BORDER_MARGIN))
        {
            int border_margin = preferences.getInt(PREFERENCES_BORDER_MARGIN, 30);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
            params.setMargins(border_margin, border_margin, border_margin, border_margin);
            photo.setLayoutParams(params);

            preferences.edit().remove(PREFERENCES_BORDER_MARGIN).commit();

        }
        else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
            int first_margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics()));
            params.setMargins(first_margin, first_margin, first_margin, first_margin);
            photo.setLayoutParams(params);
            photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        if(preferences.contains(PREFERENCES_BORDER_COLOR))
        {
            int border_color = preferences.getInt(PREFERENCES_BORDER_COLOR, Color.rgb(13,71,161));
            background.setBackgroundColor(border_color);
            preferences.edit().remove(PREFERENCES_BORDER_COLOR).commit();
        }

        if(preferences.contains(PREFERENCES_ITEM_X))
        {
            item.setEnabled(true);
            item.setClickable(true);
            item.setVisibility(View.VISIBLE);

            item.setScaleX(preferences.getFloat(PREFERENCES_ITEM_SCALE, 1));
            item.setScaleY(preferences.getFloat(PREFERENCES_ITEM_SCALE, 1));
            item.setX((int)preferences.getFloat(PREFERENCES_ITEM_X, 500));
            item.setY((int)preferences.getFloat(PREFERENCES_ITEM_Y, 500));
            item.setRotation(preferences.getFloat(PREFERENCES_ITEM_ROTATION, 0));
            item.setImageResource(preferences.getInt(PREFERENCES_ITEM_IMAGE, R.drawable.smile_item));
            actual_sticker = preferences.getInt(PREFERENCES_ITEM_IMAGE, R.drawable.smile_item);

            preferences.edit().remove(PREFERENCES_ITEM_SCALE).commit();
            preferences.edit().remove(PREFERENCES_ITEM_X).commit();
            preferences.edit().remove(PREFERENCES_ITEM_Y).commit();
            preferences.edit().remove(PREFERENCES_ITEM_ROTATION).commit();
            preferences.edit().remove(PREFERENCES_ITEM_IMAGE).commit();
        }
        else
        {
            item.setClickable(false);
            item.setEnabled(false);
            item.setVisibility(View.INVISIBLE);
        }

        if(preferences.contains(PREFERENCES_TEXT_X))
        {
            insertedText.setClickable(true);
            insertedText.setEnabled(true);
            insertedText.setVisibility(View.VISIBLE);

            insertedText.setScaleX(preferences.getFloat(PREFERENCES_TEXT_SCALE, 1));
            insertedText.setScaleY(preferences.getFloat(PREFERENCES_TEXT_SCALE, 1));
            insertedText.setX((int)preferences.getFloat(PREFERENCES_TEXT_X, 500));
            insertedText.setY((int)preferences.getFloat(PREFERENCES_TEXT_Y, 500));
            insertedText.setRotation(preferences.getFloat(PREFERENCES_TEXT_ROTATION, 0));

            String fontName = (preferences.getString(PREFERENCES_TEXT_FONT, "arial")).replaceAll("\\s+","_").toLowerCase();
            Typeface fontStyle = ResourcesCompat.getFont(this, getResources().getIdentifier(fontName, "font", getPackageName()));
            insertedText.setTypeface(fontStyle);
            insertedText.setTextColor(preferences.getInt(PREFERENCES_TEXT_COLOR, 0));
            actual_font = fontName;

            insertedText.setText(preferences.getString(PREFERENCES_TEXT_VALUE, ""));

            preferences.edit().remove(PREFERENCES_TEXT_X).commit();
            preferences.edit().remove(PREFERENCES_TEXT_Y).commit();
            preferences.edit().remove(PREFERENCES_TEXT_SCALE).commit();
            preferences.edit().remove(PREFERENCES_TEXT_ROTATION).commit();
            preferences.edit().remove(PREFERENCES_TEXT_FONT).commit();
            preferences.edit().remove(PREFERENCES_TEXT_COLOR).commit();
            preferences.edit().remove(PREFERENCES_TEXT_VALUE).commit();
        }
        else
        {
            insertedText.setClickable(false);
            insertedText.setEnabled(false);
            insertedText.setVisibility(View.INVISIBLE);
        }

        // setFragment(this, stickerFragment);
        Log.d("ITEM_CONFIG", String.valueOf(item.getX()) + " " + String.valueOf(item.getY()) + " " + String.valueOf(item.getScaleX()) + " " + String.valueOf(item.getRotation()));
        Log.d("TEXT_CONFIG", String.valueOf(insertedText.getX()) + " " + String.valueOf(insertedText.getY()) + " " + String.valueOf(insertedText.getScaleX()) + " " + String.valueOf(insertedText.getRotation()));
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
                        templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, insertTextFragment);

                        xCorText = v.getX() - event.getRawX();
                        yCorText = v.getY() - event.getRawY();

                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == TOUCH_MODE_DRAG) {
                            move_x_text = event.getRawX() + xCorText;
                            move_y_text =  event.getRawY() + yCorText;
                            //v.animate().x(move_x_text).y(move_y_text).setDuration(0).start();
                            Log.d("CorText", String.valueOf(move_x_text) + " " + String.valueOf(move_y_text));
                            insertedText.setX(move_x_text);
                            insertedText.setY(move_y_text);
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
                        templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, seekBarsFragment);
                        xCorItem = v.getX() - event.getRawX();
                        yCorItem = v.getY() - event.getRawY();

                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode == TOUCH_MODE_DRAG) {
                            move_x_item = event.getRawX() + xCorItem;
                            move_y_item =  event.getRawY() + yCorItem;
                            //v.animate().x(move_x_item).y(move_y_item).setDuration(0).start();
                            Log.d("CorItem", String.valueOf(move_x_item) + " " + String.valueOf(move_y_item));
                            item.setX(move_x_item);
                            item.setY(move_y_item);
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

                Bitmap bitmapPostcard = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
                bitmapPostcard.eraseColor(((ColorDrawable)background.getBackground()).getColor());

                Canvas canvasPostcard = new Canvas(bitmapPostcard);

                // Drawing photo
                templateSinglePhotoPresenter.mergePhoto(photo, canvasPostcard, background);

                if(item.getVisibility() == View.VISIBLE)
                {
                    templateSinglePhotoPresenter.mergeItem(item, canvasPostcard);
                }

                if(insertedText.getVisibility() == View.VISIBLE)
                {
                    templateSinglePhotoPresenter.mergeText(insertedText, canvasPostcard);
                }

                templateSinglePhotoPresenter.savePostcard(TemplateSinglePhoto.this, bitmapPostcard);
            }
        });
    }


    private void saveData() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putInt(PREFERENCES_BORDER_MARGIN, params.bottomMargin);
        preferencesEditor.putInt(PREFERENCES_BORDER_COLOR, ((ColorDrawable)background.getBackground()).getColor());

        if(item.getVisibility() == View.VISIBLE)
        {
            //Log.d("ITEM_CONFIG_BEFORE", String.valueOf(item.getX()) + " " + String.valueOf(item.getY()) + " " + String.valueOf(item.getScaleX()) + " " + String.valueOf(item.getRotation()));
            preferencesEditor.putFloat(PREFERENCES_ITEM_X, item.getX());
            preferencesEditor.putFloat(PREFERENCES_ITEM_Y, item.getY());
            preferencesEditor.putFloat(PREFERENCES_ITEM_SCALE, item.getScaleX());
            preferencesEditor.putFloat(PREFERENCES_ITEM_ROTATION, item.getRotation());
            preferencesEditor.putInt(PREFERENCES_ITEM_IMAGE, actual_sticker);
        }

        if(insertedText.getVisibility() == View.VISIBLE)
        {
            Log.d("TEXT_CONFIG_BEFORE", String.valueOf(insertedText.getX()) + " " + String.valueOf(insertedText.getY()) + " " + String.valueOf(insertedText.getScaleX()) + " " + String.valueOf(insertedText.getRotation()));
            preferencesEditor.putFloat(PREFERENCES_TEXT_X, insertedText.getX());
            preferencesEditor.putFloat(PREFERENCES_TEXT_Y, insertedText.getY());
            preferencesEditor.putFloat(PREFERENCES_TEXT_SCALE, insertedText.getScaleX());
            preferencesEditor.putFloat(PREFERENCES_TEXT_ROTATION, insertedText.getRotation());
            preferencesEditor.putString(PREFERENCES_TEXT_FONT, actual_font);
            preferencesEditor.putInt(PREFERENCES_TEXT_COLOR, insertedText.getCurrentTextColor());
            preferencesEditor.putString(PREFERENCES_TEXT_VALUE, String.valueOf(insertedText.getText()));
        }

        preferencesEditor.commit();
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

            RelativeLayout.LayoutParams backgroundParams = (RelativeLayout.LayoutParams) photo.getLayoutParams();

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
        actual_sticker = sticker;

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
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
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
