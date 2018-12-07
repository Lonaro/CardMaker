package michal.cardmaker.presenter.viewholder;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import michal.cardmaker.R;

public class ReverseSelectViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView address;
    private TextView message;
    public ConstraintLayout reverse_layout;
    public ConstraintLayout reverse_delete_layout;

    public ImageView delete_button;

    public ReverseSelectViewHolder(@NonNull View view) {
        super(view);
        this.name = view.findViewById(R.id.reverse_rw_item_name);
        this.address = view.findViewById(R.id.reverse_rw_item_address);
        this.message = view.findViewById(R.id.reverse_rw_item_message);
        this.reverse_layout = view.findViewById(R.id.reverse_all_layout);
        this.reverse_delete_layout = view.findViewById(R.id.delete_reverse_cross);
        this.delete_button = view.findViewById(R.id.template_reverse_delete);
    }

    public void setData(String n, String a, String m) {
        name.setText(n);
        address.setText(a);
        message.setText(m);
    }
}
