package michal.cardmaker.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

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
        intent.putExtra("outputX", CropUtils.dip2px(activity, width));
        intent.putExtra("outputY", CropUtils.dip2px(activity, height));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivity(intent);
    }
}
