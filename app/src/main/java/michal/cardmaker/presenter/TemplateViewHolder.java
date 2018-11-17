package michal.cardmaker.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import michal.cardmaker.R;

public class TemplateViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;

    public TemplateViewHolder(View imageView) {
        super(imageView);
        this.imageView = imageView.findViewById(R.id.template_item_image);
    }
}
