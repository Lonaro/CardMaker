package michal.cardmaker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

        View imageView = new ImageView(viewGroup.getContext());//LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_template_item, viewGroup, false);

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
