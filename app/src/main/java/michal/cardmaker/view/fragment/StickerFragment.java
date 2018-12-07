package michal.cardmaker.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michal.cardmaker.R;
import michal.cardmaker.presenter.decoration.LinearHorizontalItemDecoration;
import michal.cardmaker.presenter.adapter.ItemAdapter;

public class StickerFragment extends Fragment{

    private RecyclerView stickerRecyclerView;
    public ItemAdapter stickerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public StickerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sticker, container,false);

        // TYMACZASOWE DANE
        int[] stickers_data = {R.drawable.smile_item, R.drawable.sun, R.drawable.palm_tree,
                R.drawable.wave};

        stickerRecyclerView = view.findViewById(R.id.recyclerview_stickers);
        stickerAdapter = new ItemAdapter(stickers_data, getContext());

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        stickerRecyclerView.setLayoutManager(mLayoutManager);
        stickerRecyclerView.addItemDecoration(new LinearHorizontalItemDecoration(10));
        stickerRecyclerView.setAdapter(stickerAdapter);

        return view;
    }
}
