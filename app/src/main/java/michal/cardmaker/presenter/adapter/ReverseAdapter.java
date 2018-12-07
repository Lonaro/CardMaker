package michal.cardmaker.presenter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.model.ReverseModel;
import michal.cardmaker.presenter.viewholder.ReverseViewHolder;
import michal.cardmaker.view.MaximizeReverse;

public class ReverseAdapter extends RecyclerView.Adapter<ReverseViewHolder>{

    private Context context;
    private ArrayList<ReverseModel> reverses;
    private View view;

    private boolean delete = false;

    public ReverseAdapter(Context context, ArrayList<ReverseModel> reverses, View v) {
        this.context = context;
        this.reverses = reverses;
        this.view = v;
    }


    @Override
    public ReverseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_reverse_item, viewGroup, false);

        return new ReverseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReverseViewHolder reverseViewHolder, int i) {
        ReverseModel reverse = reverses.get(i);
        reverseViewHolder.setData(reverse.getName(), reverse.getAddress(), reverse.getFragmentMessage());

        if(delete)
        {
            reverseViewHolder.reverse_delete_layout.setVisibility(View.VISIBLE);
            reverseViewHolder.reverse_delete_layout.setClickable(true);
        }
        else
        {
            reverseViewHolder.reverse_delete_layout.setVisibility(View.INVISIBLE);
            reverseViewHolder.reverse_delete_layout.setClickable(false);
        }

        reverseViewHolder.delete_button.setOnClickListener(v -> {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(context);
            alertBox.setMessage(R.string.delete_item_communicate);
            alertBox.setPositiveButton("Yes", (dialog, which) -> {
                File direct = new File(reverses.get(i).getPath());
                direct.delete();

                direct = new File(reverses.get(i).getPath().substring(0,reverses.get(i).getPath().length()-4)+".txt");
                direct.delete();

                reverses.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, reverses.size());

                if(reverses.size() == 0)
                {
                    view.findViewById(R.id.reverse_empty_list).setVisibility(View.VISIBLE);
                }

            });
            alertBox.setNegativeButton("No", null);
            alertBox.create().show();

        });


        reverseViewHolder.reverse_layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MaximizeReverse.class);

            intent.putExtra("REVERSE", reverses.get(i).getPath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reverses.size();
    }

    public void showDelete() {
        delete = !delete;
        notifyDataSetChanged();
    }
}
