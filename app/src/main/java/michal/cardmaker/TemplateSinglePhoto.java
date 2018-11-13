package michal.cardmaker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import michal.cardmaker.presenter.TemplateSinglePhotoPresenter;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;

public class TemplateSinglePhoto extends AppCompatActivity {

    private ImageView background;
    private ImageView photo;
    private Button merge_button;
    private TemplateSinglePhotoPresenter templateSinglePhotoPresenter;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    Bitmap cs;
    Canvas comboImage;
    ImageView mergePhoto;

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_single_photo);

        templateSinglePhotoPresenter = new TemplateSinglePhotoPresenter(TemplateSinglePhoto.this);

        photo = findViewById(R.id.temp_single_photo);
        merge_button = findViewById(R.id.merge_button);
        background = findViewById(R.id.background);

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
                templateSinglePhotoPresenter.startCropper(REQUEST_CODE_CAMERA, data);
                break;
            case REQUEST_CODE_ALBUM:
                templateSinglePhotoPresenter.startCropper(REQUEST_CODE_ALBUM, data);
                break;
            default:
                break;
        }
    }
}
