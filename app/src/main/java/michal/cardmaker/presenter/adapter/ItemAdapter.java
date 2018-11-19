package michal.cardmaker.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import michal.cardmaker.R;
import michal.cardmaker.presenter.viewholder.ItemViewHolder;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private int[] stickers;
    private Context context;

    public ItemAdapter(int [] stickers, Context context){
        this.stickers = stickers;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View imageView = LayoutInflater.from(context).inflate(R.layout.recyclerview_sticker_item, viewGroup, false);

        return new ItemViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.imageView.setImageResource(stickers[i]);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

//                switch(i) {
//                    case 0:
//                        intent = new Intent(context, TemplateSinglePhoto.class);
//                        context.startActivity(intent);
//                        break;
//                    default:
                    Toast.makeText(context, "Wybrałeś naklejke numer" + i, Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickers.length;
    }
}
