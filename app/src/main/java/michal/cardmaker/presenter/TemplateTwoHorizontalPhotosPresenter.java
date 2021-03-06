package michal.cardmaker.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TemplateTwoHorizontalPhotosPresenter extends TemplatePresenter {

    public TemplateTwoHorizontalPhotosPresenter(Activity activity) {
        super(activity);
    }

    public Canvas mergePhotoVertical(ImageView photo, Canvas postcard, ImageView background, int x_start, int y_start) {
        LinearLayout.LayoutParams background_params = (LinearLayout.LayoutParams) photo.getLayoutParams();
        Bitmap bitmapPhoto = Bitmap.createScaledBitmap(((BitmapDrawable)photo.getDrawable()).getBitmap(), background.getWidth()/2 - background_params.leftMargin - background_params.rightMargin, background.getHeight()-background_params.topMargin-background_params.bottomMargin, false);
        postcard.drawBitmap(bitmapPhoto, x_start, y_start, null);

        return postcard;
    }

    public Canvas mergePhotoHorizontal(ImageView photo, Canvas postcard, ImageView background, int x_start, int y_start) {
        LinearLayout.LayoutParams background_params = (LinearLayout.LayoutParams) photo.getLayoutParams();
        Bitmap bitmapPhoto = Bitmap.createScaledBitmap(((BitmapDrawable)photo.getDrawable()).getBitmap(), background.getWidth() - background_params.leftMargin - background_params.rightMargin, background.getHeight()/2-background_params.topMargin-background_params.bottomMargin, false);
        postcard.drawBitmap(bitmapPhoto, x_start, y_start, null);

        return postcard;
    }
}
