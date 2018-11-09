package michal.cardmaker.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class TemplateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;

    public TemplateViewHolder(View imageView) {
        super(imageView);
        this.imageView = (ImageView) imageView;
    }
}
