package michal.cardmaker.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import michal.cardmaker.R;
import michal.cardmaker.presenter.listener.BorderSettingsFragmentListener;
import michal.cardmaker.presenter.listener.InsertTextFragmentListener;
import michal.cardmaker.presenter.listener.ResetItemFragmentListener;
import michal.cardmaker.presenter.listener.ResetTextFragmentListener;
import michal.cardmaker.presenter.listener.StickerFragmentListener;
import michal.cardmaker.presenter.TemplateSinglePhotoPresenter;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;
import michal.cardmaker.view.fragment.BorderSettingFragment;
import michal.cardmaker.view.fragment.EditTextFragment;
import michal.cardmaker.view.fragment.InsertTextFragment;
import michal.cardmaker.view.fragment.SeekBarsFragment;
import michal.cardmaker.view.fragment.StickerFragment;

public class TemplateSinglePhoto extends AppCompatActivity implements StickerFragmentListener, BorderSettingsFragmentListener, InsertTextFragmentListener, ResetItemFragmentListener, ResetTextFragmentListener {

    private static final int TEMPLATE_NUMBER = 0;

    private ImageView background;
    private ImageView photo;
    private ImageView item;
    private ImageButton merge_button;
    private ImageButton add_item_button;
    private ImageButton borderSettingsButton;
    private ImageButton add_text_button;
    private ImageButton clear_button;
    private Button size_button;
    private ImageButton share_button;
    private TextView insertedText;

    private StickerFragment stickerFragment;
    private SeekBarsFragment seekBarsFragment;
    private BorderSettingFragment borderSettingFragment;
    private InsertTextFragment insertTextFragment;
    private EditTextFragment editTextFragment;

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
    private static final String PREFERENCES_VERTICAL_ORIENTATION = "VERTICAL_ORIENTATION";

    private SharedPreferences preferences;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;

    private float xCorItem, yCorItem;
    private float xCorText, yCorText;
    float move_x_item;
    float move_y_item;
    int actual_sticker;
    public String actual_font;
    int border_margin_text;

    float move_x_text;
    float move_y_text;

    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_DRAG = 1;
    int mTouchMode = TOUCH_MODE_NONE;

    private boolean VERTICAL_ORIENTATION = true;

    private int sticker;

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
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
        clear_button = findViewById(R.id.clear_button);
        size_button = findViewById(R.id.size_button);
        share_button = findViewById(R.id.share_button);

        loadData();

        stickerFragment = new StickerFragment();
        seekBarsFragment = new SeekBarsFragment(item, this);
        borderSettingFragment = new BorderSettingFragment(this, border_margin_text);
        insertTextFragment = new InsertTextFragment(this);
        editTextFragment = new EditTextFragment(this);


        Intent intent = getIntent();

        if (intent == null && intent.getData() == null) {
            photo.setImageResource(R.drawable.camera);
            photo.setBackgroundColor(R.color.colorAccent);
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

                        templateSinglePhotoPresenter.setFragment(TemplateSinglePhoto.this, editTextFragment);
                        //editTextFragment.setValues((int)(insertedText.getScaleX()*50), (int)insertedText.getRotation(), (int)insertedText.getCurrentTextColor());

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
                        //seekBarsFragment.setValues((int)(item.getScaleX()*100), (int)item.getRotation());
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

        size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(TemplateSinglePhoto.this);
                builder.setTitle("Choose a format:");

                // add a list
                String[] animals = {"A6 (800x1200)", "A5 (1200x1800)", "A4 (1800x2700)", "A3 (2700x4050)"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: size_button.setText("A6"); break;
                            case 1: size_button.setText("A5"); break;
                            case 2: size_button.setText("A4"); break;
                            case 3: size_button.setText("A3"); break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        merge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmapPostcard = merge();

                String postcardSize = String.valueOf(size_button.getText());
                int width;
                int height;


                switch(postcardSize) {
                    case "A6": {
                        if(bitmapPostcard.getHeight() < bitmapPostcard.getWidth())
                        {
                            width = 1200;
                            height = 800;
                        } else {
                            width = 800;
                            height = 1200;
                        }
                        break;
                    }
                    case "A5": {
                        if(bitmapPostcard.getHeight() < bitmapPostcard.getWidth())
                        {
                            width = 1800;
                            height = 1200;
                        } else {
                            width = 1200;
                            height = 1800;
                        }
                        break;
                    }
                    case "A4": {
                        if(bitmapPostcard.getHeight() < bitmapPostcard.getWidth())
                        { // 210x297
                            width = 2700;
                            height = 1800;
                        } else {
                            width = 1800;
                            height = 2700;
                        }
                        break;
                    }
                    case "A3": {
                        if(bitmapPostcard.getHeight() < bitmapPostcard.getWidth())
                        {
                            width = 4050;
                            height = 2700;
                        } else {
                            width = 2700;
                            height = 4050;
                        }
                        break;
                    }
                    default:{
                        if(bitmapPostcard.getHeight() < bitmapPostcard.getWidth())
                        {
                            width = 1800;
                            height = 1200;
                        } else {
                            width = 1200;
                            height = 1800;
                        }
                    }
                }

                templateSinglePhotoPresenter.savePostcard(TemplateSinglePhoto.this, bitmapPostcard, width, height);
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int border_color = preferences.getInt(PREFERENCES_BORDER_COLOR, Color.rgb(13,71,161));
                background.setBackgroundColor(border_color);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
                int first_margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics()));
                params.setMargins(first_margin, first_margin, first_margin, first_margin);
                photo.setLayoutParams(params);
                photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                photo.setImageResource(R.drawable.camera);

                if(item.isEnabled())
                {
                    seekBarsFragment.clearItem();
                }

                if(insertedText.isEnabled())
                {
                    editTextFragment.clearText();
                }

                FrameLayout frameLayout = findViewById(R.id.frameLayout2);
                frameLayout.removeAllViews();

            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmapPostcard = merge();

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapPostcard, "Postcard from CardMaker", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share postcard"));
            }
        });
    }

    private void loadData() {

        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        border_margin_text = preferences.getInt(PREFERENCES_BORDER_MARGIN, 30);

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




        if(preferences.contains(PREFERENCES_VERTICAL_ORIENTATION)) {
            VERTICAL_ORIENTATION = preferences.getBoolean(PREFERENCES_VERTICAL_ORIENTATION, true);
            if (!VERTICAL_ORIENTATION) {
                RelativeLayout photoAll = findViewById(R.id.frame_template);
                ConstraintLayout.LayoutParams fullPhoto = (ConstraintLayout.LayoutParams) photoAll.getLayoutParams();
                int layout_height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, Resources.getSystem().getDisplayMetrics()));
                fullPhoto.height = layout_height;
                fullPhoto.width = 0;
                fullPhoto.dimensionRatio = "V, 2:3";
                photoAll.setLayoutParams(fullPhoto);

                photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

            } else {
                RelativeLayout photoAll = findViewById(R.id.frame_template);
                ConstraintLayout.LayoutParams fullPhoto = (ConstraintLayout.LayoutParams) photoAll.getLayoutParams();
                fullPhoto.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                fullPhoto.height = 0;
                fullPhoto.dimensionRatio = "H, 3:2";
                photoAll.setLayoutParams(fullPhoto);
                photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            preferences.edit().remove(PREFERENCES_VERTICAL_ORIENTATION).commit();
        }

    }

    private Bitmap merge() {

        //Bitmap bitmapPostcard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap bitmapPostcard = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapPostcard.eraseColor(((ColorDrawable)background.getBackground()).getColor());

        Canvas canvasPostcard = new Canvas(bitmapPostcard);
        RelativeLayout.LayoutParams background_params = (RelativeLayout.LayoutParams) photo.getLayoutParams();

        // Drawing photo
        templateSinglePhotoPresenter.mergePhotoSingle(photo, canvasPostcard, bitmapPostcard, background_params.leftMargin, background_params.topMargin);

        if(item.getVisibility() == View.VISIBLE)
        {
            templateSinglePhotoPresenter.mergeItem(item, canvasPostcard);
        }

        if(insertedText.getVisibility() == View.VISIBLE)
        {
            templateSinglePhotoPresenter.mergeText(insertedText, canvasPostcard);
        }

        return bitmapPostcard;

    }


    private void saveData() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putInt(PREFERENCES_BORDER_MARGIN, params.bottomMargin);
        preferencesEditor.putInt(PREFERENCES_BORDER_COLOR, ((ColorDrawable)background.getBackground()).getColor());

        if(item.getVisibility() == View.VISIBLE)
        {
            preferencesEditor.putFloat(PREFERENCES_ITEM_X, item.getX());
            preferencesEditor.putFloat(PREFERENCES_ITEM_Y, item.getY());
            preferencesEditor.putFloat(PREFERENCES_ITEM_SCALE, item.getScaleX());
            preferencesEditor.putFloat(PREFERENCES_ITEM_ROTATION, item.getRotation());
            preferencesEditor.putInt(PREFERENCES_ITEM_IMAGE, actual_sticker);
        }

        if(insertedText.getVisibility() == View.VISIBLE)
        {
            preferencesEditor.putFloat(PREFERENCES_TEXT_X, insertedText.getX());
            preferencesEditor.putFloat(PREFERENCES_TEXT_Y, insertedText.getY());
            preferencesEditor.putFloat(PREFERENCES_TEXT_SCALE, insertedText.getScaleX());
            preferencesEditor.putFloat(PREFERENCES_TEXT_ROTATION, insertedText.getRotation());
            preferencesEditor.putString(PREFERENCES_TEXT_FONT, actual_font);
            preferencesEditor.putInt(PREFERENCES_TEXT_COLOR, insertedText.getCurrentTextColor());
            preferencesEditor.putString(PREFERENCES_TEXT_VALUE, String.valueOf(insertedText.getText()));
        }

        preferencesEditor.putBoolean(PREFERENCES_VERTICAL_ORIENTATION, VERTICAL_ORIENTATION);

        preferencesEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.nav_rotate_template: {
                if (VERTICAL_ORIENTATION) {
                    RelativeLayout photoAll = findViewById(R.id.frame_template);
                    ConstraintLayout.LayoutParams fullPhoto = (ConstraintLayout.LayoutParams) photoAll.getLayoutParams();
                    int layout_height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, Resources.getSystem().getDisplayMetrics()));
                    fullPhoto.height = layout_height;
                    fullPhoto.width = 0;
                    fullPhoto.dimensionRatio = "V, 2:3";
                    photoAll.setLayoutParams(fullPhoto);
                    photo.setImageResource(R.drawable.camera);
                    photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    VERTICAL_ORIENTATION = false;
                } else {
                    RelativeLayout photoAll = findViewById(R.id.frame_template);
                    ConstraintLayout.LayoutParams fullPhoto = (ConstraintLayout.LayoutParams) photoAll.getLayoutParams();
                    fullPhoto.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                    fullPhoto.height = 0;
                    fullPhoto.dimensionRatio = "H, 3:2";
                    photoAll.setLayoutParams(fullPhoto);
                    photo.setImageResource(R.drawable.camera);
                    photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    VERTICAL_ORIENTATION = true;

                }
                if(item.getVisibility() == View.VISIBLE)
                {
                    seekBarsFragment.clearItem();
                }

                if(insertedText.getVisibility() == View.VISIBLE)
                {
                    editTextFragment.clearText();
                }
            }
        }

        return true;
    }

    public void onSelectAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    @Override
    public void onBackPressed() {

        if(item.isEnabled() || insertedText.isEnabled() || photo.getDrawable().getConstantState() != getResources().getDrawable( R.drawable.camera).getConstantState()) {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(TemplateSinglePhoto.this);
            alertBox.setMessage("Are you sure to exit?");
            alertBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToMainActivity);
                }
            });
            alertBox.setNegativeButton("No", null);
            alertBox.create().show();
        }
        else
        {
            Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToMainActivity);
        }

        //super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        int photo_number = data.getIntExtra("PHOTO_NUMBER", 0);
        if(ContextCompat.checkSelfPermission(TemplateSinglePhoto.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            RelativeLayout.LayoutParams backgroundParams = (RelativeLayout.LayoutParams) photo.getLayoutParams();

            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_CAMERA, data, background.getWidth()-backgroundParams.leftMargin*2, background.getHeight()-backgroundParams.topMargin*2, TEMPLATE_NUMBER, photo_number);
                    Log.d("First_size", String.valueOf(background.getWidth()-backgroundParams.leftMargin*2) + " " + String.valueOf(background.getHeight()-backgroundParams.topMargin*2));
                    break;
                case REQUEST_CODE_ALBUM:
                    templateSinglePhotoPresenter.startCropper(REQUEST_CODE_ALBUM, data, background.getWidth()-backgroundParams.leftMargin*2, background.getHeight()-backgroundParams.topMargin*2, TEMPLATE_NUMBER, photo_number);
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
        templateSinglePhotoPresenter.setFragment(this, editTextFragment);
    }

    @Override
    public void changeFragmentOnItemReset() {
        templateSinglePhotoPresenter.setFragment(this, stickerFragment);
    }

    @Override
    public void changeFragmentOnTextReset() {
        insertTextFragment.clearText();
        templateSinglePhotoPresenter.setFragment(this, insertTextFragment);
        insertTextFragment.clearText();
    }

    @Override
    public void setActualFont(String font) {
        actual_font = font;
    }
}
