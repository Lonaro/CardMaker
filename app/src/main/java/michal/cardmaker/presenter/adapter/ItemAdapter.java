package michal.cardmaker.presenter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michal.cardmaker.R;
import michal.cardmaker.presenter.StickerFragmentListener;
import michal.cardmaker.presenter.viewholder.ItemViewHolder;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private int[] stickers;
    private Context context;
    public int sticker = -1;


    private StickerFragmentListener stickerFragmentListener;

    public ItemAdapter(int [] stickers, Context context){
        this.stickers = stickers;
        this.context = context;

        stickerFragmentListener = (StickerFragmentListener) context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_sticker_item, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.imageView.setImageResource(stickers[i]);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sticker = stickers[i];
                stickerFragmentListener.onFragmentInteraction(sticker);
            }
        });

        Log.d("Sticker_id", String.valueOf(i));
    }

    @Override
    public int getItemCount() {
        return stickers.length;
    }

}
