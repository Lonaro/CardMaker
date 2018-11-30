package michal.cardmaker.presenter.viewholder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import michal.cardmaker.R;

public class HistoryPostcardViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ImageButton imageButton;
    public ConstraintLayout constraintLayout;

    public HistoryPostcardViewHolder(View view) {
        super(view);
        this.imageView = view.findViewById(R.id.template_history_postcard_image);
        this.imageButton = view.findViewById(R.id.template_history_postcard_delete);
        this.constraintLayout = view.findViewById(R.id.delete_postcard_cross);
    }


}
