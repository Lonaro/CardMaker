package michal.cardmaker.presenter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import michal.cardmaker.R;

public class HistoryPostcardViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public HistoryPostcardViewHolder(View view) {
        super(view);
        this.imageView = view.findViewById(R.id.template_history_postcard_image);
    }


}
