package michal.cardmaker.presenter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import michal.cardmaker.view.ReverseMergePostcard;
import michal.cardmaker.view.ReverseToMergeSelection;

public class ReverseSelectAdapter extends RecyclerView.Adapter<ReverseViewHolder>{

    private Context context;
    private ArrayList<ReverseModel> reverses;
    private String postcard_path;
    private View view;

    private boolean delete = false;

    public ReverseSelectAdapter(Context context, ArrayList<ReverseModel> reverses, String postcard_path) {
        this.context = context;
        this.reverses = reverses;
        this.postcard_path = postcard_path;
    }


    @Override
    public ReverseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_reverse_item, viewGroup, false);
        this.view = viewGroup;
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


        reverseViewHolder.reverse_layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReverseMergePostcard.class);

            intent.putExtra("PATH_POSTCARD", postcard_path);
            intent.putExtra("PATH_REVERSE", reverses.get(i).getPath());
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
