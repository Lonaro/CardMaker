package michal.cardmaker.presenter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class LinearHorizontalItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;
    private int displayMode;


    public LinearHorizontalItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect, RecyclerView.LayoutManager layoutManager, int position, int itemCount) {

        outRect.left = spacing;
        outRect.right = position == itemCount - 1 ? spacing : 0;
        outRect.top = spacing;
        outRect.bottom = spacing;
    }


}
