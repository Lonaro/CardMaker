package michal.cardmaker.presenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.presenter.viewholder.HistoryPostcardViewHolder;

public class HistoryPostcardAdapter extends RecyclerView.Adapter<HistoryPostcardViewHolder> {

    ArrayList<String> history_postcards;
    private Context context;

    public HistoryPostcardAdapter(ArrayList<String> history_postcards, Context context) {
        this.history_postcards = history_postcards;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryPostcardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_history_postcard_item, viewGroup, false);

        return new HistoryPostcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPostcardViewHolder historyPostcardViewHolder, int i) {
        Bitmap postcard = BitmapFactory.decodeFile(history_postcards.get(i));
        Log.d("Weszlo", "TAK");
        historyPostcardViewHolder.imageView.setImageBitmap(postcard);

        historyPostcardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(i) {
                    default: Toast.makeText(context, "Wybrano " + i, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return history_postcards.size();
    }
}
