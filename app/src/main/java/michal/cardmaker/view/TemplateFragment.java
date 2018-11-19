package michal.cardmaker.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michal.cardmaker.R;
import michal.cardmaker.presenter.GridSpacingItemDecoration;
import michal.cardmaker.presenter.adapter.TemplateAdapter;


public class TemplateFragment extends Fragment {

    private RecyclerView recyclerView;
    private TemplateAdapter templateAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

        public TemplateFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_template, container,false);

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_template);

            // TYMACZASOWE DANE
            int[] templates_data = {R.drawable.template1, R.drawable.template2, R.drawable.template3,
                    R.drawable.template4, R.drawable.template5, R.drawable.template6};

            templateAdapter = new TemplateAdapter(templates_data, getContext());

            mLayoutManager = new GridLayoutManager(getActivity(),2 );
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(20, true));
            recyclerView.setAdapter(templateAdapter);

            return view;
        }
    }
