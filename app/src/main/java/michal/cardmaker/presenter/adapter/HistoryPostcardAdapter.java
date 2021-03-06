package michal.cardmaker.presenter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.presenter.viewholder.HistoryPostcardViewHolder;
import michal.cardmaker.view.MaximizePostcard;

public class HistoryPostcardAdapter extends RecyclerView.Adapter<HistoryPostcardViewHolder> {

    ArrayList<String> history_postcards;
    private Context context;
    private View view;

    private boolean delete = false;

    public HistoryPostcardAdapter(ArrayList<String> history_postcards, Context context, View view) {
        this.history_postcards = history_postcards;
        this.context = context;
        this.view = view;
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

        historyPostcardViewHolder.imageView.setImageBitmap(postcard);

        if(delete)
        {
            historyPostcardViewHolder.constraintLayout.setVisibility(View.VISIBLE);
            historyPostcardViewHolder.constraintLayout.setClickable(true);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) historyPostcardViewHolder.imageView.getLayoutParams();
            params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, Resources.getSystem().getDisplayMetrics());
            params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, Resources.getSystem().getDisplayMetrics());
            historyPostcardViewHolder.imageView.setLayoutParams(params);
        }
        else
        {
            historyPostcardViewHolder.constraintLayout.setVisibility(View.INVISIBLE);
            historyPostcardViewHolder.constraintLayout.setClickable(false);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) historyPostcardViewHolder.imageView.getLayoutParams();
            params.rightMargin = 0;
            params.topMargin = 0;
            historyPostcardViewHolder.imageView.setLayoutParams(params);
        }


        historyPostcardViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MaximizePostcard.class);

                intent.putExtra("PHOTO", history_postcards.get(i));
                context.startActivity(intent);

            }
        });

        historyPostcardViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBox = new AlertDialog.Builder(context);
                alertBox.setMessage(R.string.delete_item_communicate);
                alertBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("File_path", history_postcards.get(i));
                        File direct = new File(history_postcards.get(i));
                        direct.delete();
                        history_postcards.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, history_postcards.size());

                        if(history_postcards.size() == 0)
                        {
                            view.findViewById(R.id.history_empty_list).setVisibility(View.VISIBLE);
                        }

                    }
                });
                alertBox.setNegativeButton("No", null);
                alertBox.create().show();

            }
        });
    }

    public void showDelete()
    {
        delete = !delete;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return history_postcards.size();
    }
}
