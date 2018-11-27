package michal.cardmaker.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import michal.cardmaker.R;
import michal.cardmaker.view.CropActivity;

public class TemplateSinglePhotoPresenter {

    private Activity activity;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;


    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private Uri mCameraImageUri;

    public TemplateSinglePhotoPresenter(Activity activity) {
        this.activity = activity;
    }

    public void startCropper(int requestCode, Intent data, int width, int height) {
        Uri uri = null;
        if (requestCode == REQUEST_CODE_CAMERA) {
            uri = mCameraImageUri;
        } else if (data != null && data.getData() != null) {
            uri = data.getData();
        } else {
            return;
        }
        Intent intent = new Intent(activity, CropActivity.class);
        intent.setData(uri);

        Log.d("Size_push", String.valueOf(width) + " " + String.valueOf(height));

        intent.putExtra("outputX", width);//CropUtils.dip2px(activity, width));
        intent.putExtra("outputY", height); //CropUtils.dip2px(activity, height));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        Log.d("Second_size", String.valueOf(width) + " " + String.valueOf(height));
        activity.startActivity(intent);
    }

    public void setFragment(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout2, fragment);
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    public Canvas mergeItem(ImageView item, Canvas postcard) {
        Bitmap item_1 = ((BitmapDrawable) item.getDrawable()).getBitmap();
        Bitmap b = Bitmap.createScaledBitmap(item_1, (int) (item_1.getWidth() * item.getScaleX()), (int) (item_1.getHeight() * item.getScaleX()), false);
        Matrix mat = new Matrix();
        mat.postRotate(item.getRotation(), b.getWidth() / 2, b.getHeight() / 2);
        mat.postTranslate(item.getX() + item.getWidth() / 2 - b.getWidth() / 2, item.getY() + item.getHeight() / 2 - b.getHeight() / 2);
        item.setImageMatrix(mat);
        postcard.drawBitmap(b, mat , null);

        return postcard;
    }

    public Canvas mergeText(TextView insertedText, Canvas postcard) {
        insertedText.buildDrawingCache();
        Bitmap insertedText_1 = Bitmap.createBitmap(insertedText.getDrawingCache());
        Bitmap b = Bitmap.createScaledBitmap(insertedText_1, (int) (insertedText_1.getWidth() * insertedText.getScaleX()), (int) (insertedText_1.getHeight() * insertedText.getScaleX()), false);
        Matrix mat = new Matrix();
        mat.postRotate(insertedText.getRotation(), b.getWidth() / 2, b.getHeight() / 2);
        mat.postTranslate(insertedText.getX() + insertedText.getWidth() / 2 - b.getWidth() / 2, insertedText.getY() + insertedText.getHeight() / 2 - b.getHeight() / 2);
        postcard.drawBitmap(b, mat, null);

        return postcard;
    }

    public Canvas mergePhoto(ImageView photo, Canvas postcard, ImageView background) {
        RelativeLayout.LayoutParams background_params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
        Bitmap bitmapPhoto = Bitmap.createScaledBitmap(((BitmapDrawable)photo.getDrawable()).getBitmap(), background.getWidth()-background_params.leftMargin*2, background.getHeight()-background_params.topMargin*2, false);
        postcard.drawBitmap(bitmapPhoto, (background.getWidth()-bitmapPhoto.getWidth())/2, (background.getHeight()-bitmapPhoto.getHeight())/2, null);

        return postcard;
    }

    public void savePostcard(Activity activity, Bitmap postcard) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker");
            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/Pictures/CardMaker/");
                wallpaperDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/Pictures/CardMaker/"), "merged.jpg");
            try {
                OutputStream out = null;
                out = new FileOutputStream(file);

                postcard.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                Toast.makeText(activity.getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
}
