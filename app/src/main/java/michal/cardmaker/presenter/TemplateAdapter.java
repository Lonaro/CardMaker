package michal.cardmaker.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import michal.cardmaker.R;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateViewHolder> {

    private int[] templates;

    public TemplateAdapter(int[] templates) {
        this.templates = templates;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View imageView = new ImageView(viewGroup.getContext());//LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_template_item, viewGroup, false);

        return new TemplateViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder templateViewHolder, int i) {
        templateViewHolder.imageView.setImageResource(templates[i]);
    }

    @Override
    public int getItemCount() {
        return templates.length;
    }
}
