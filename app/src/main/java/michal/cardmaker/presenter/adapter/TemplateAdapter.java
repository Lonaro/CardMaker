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
import michal.cardmaker.view.CropActivity;
import michal.cardmaker.view.TemplateFourPhotos;
import michal.cardmaker.view.TemplateTwoHorizontalPhotos;
import michal.cardmaker.view.TemplateTwoPlusOneHorizontal;
import michal.cardmaker.view.TemplateTwoPlusOneVertical;
import michal.cardmaker.view.TemplateTwoVerticalPhotos;
import michal.cardmaker.presenter.viewholder.TemplateViewHolder;
import michal.cardmaker.view.TemplateSinglePhoto;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateViewHolder> {

    private int[] templates;
    private Context context;

    public TemplateAdapter(int[] templates, Context context) {
        this.templates = templates;
        this.context = context;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View imageView = LayoutInflater.from(context).inflate(R.layout.recyclerview_template_item, viewGroup, false);

        return new TemplateViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder templateViewHolder, final int i) {
        templateViewHolder.imageView.setImageResource(templates[i]);
        
        templateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                switch(i) {
                    case 0:
                        intent = new Intent(context, TemplateSinglePhoto.class);
                        context.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(context, TemplateTwoVerticalPhotos.class);
                        context.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(context, TemplateTwoHorizontalPhotos.class);
                        context.startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(context, TemplateTwoPlusOneHorizontal.class);
                        context.startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(context, TemplateTwoPlusOneVertical.class);
                        context.startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(context, TemplateFourPhotos.class);
                        context.startActivity(intent);
                        break;
                    default: Toast.makeText(context, "Zły wybór (" + i + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.length;
    }
}
