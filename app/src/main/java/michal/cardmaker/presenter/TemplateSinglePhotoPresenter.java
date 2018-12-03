package michal.cardmaker.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TemplateSinglePhotoPresenter extends TemplatePresenter{

    public TemplateSinglePhotoPresenter(Activity activity) {
        super(activity);
    }


    public Canvas mergePhotoSingle(ImageView photo, Canvas postcard, Bitmap background, int x_start, int y_start) {
        RelativeLayout.LayoutParams background_params = (RelativeLayout.LayoutParams) photo.getLayoutParams();
        Bitmap bitmapPhoto = Bitmap.createScaledBitmap(((BitmapDrawable)photo.getDrawable()).getBitmap(), background.getWidth() - background_params.leftMargin - background_params.rightMargin, background.getHeight()-background_params.topMargin-background_params.bottomMargin, false);
        //postcard.drawBitmap(bitmapPhoto, (background.getWidth()-bitmapPhoto.getWidth())/2, (background.getHeight()-bitmapPhoto.getHeight())/2, null);
        postcard.drawBitmap(bitmapPhoto, x_start, y_start, null);

        return postcard;
    }


}
