package michal.cardmaker.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import michal.cardmaker.R;
import michal.cardmaker.view.CropActivity;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;

public class TemplateSinglePhotoPresenter {

    private Activity activity;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_ALBUM = 2;
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
}
