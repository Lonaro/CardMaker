package michal.cardmaker.presenter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import michal.cardmaker.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public ItemViewHolder(View view) {
        super(view);
        this.imageView = view.findViewById(R.id.sticker_item);
    }
}
