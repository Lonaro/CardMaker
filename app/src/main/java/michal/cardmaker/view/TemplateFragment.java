package michal.cardmaker.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import michal.cardmaker.R;
import michal.cardmaker.TemplateSinglePhoto;
import michal.cardmaker.presenter.GridSpacingItemDecoration;
import michal.cardmaker.presenter.TemplateAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateFragment extends Fragment {

    private RecyclerView recyclerView;
    private TemplateAdapter templateAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected GridLayoutManager mCurrentLayoutManagerType;


    public TemplateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_template, container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_template);

        // TYMACZASOWE DANE
        int[] templates_data = {R.drawable.template1, R.drawable.template1, R.drawable.template1,
                R.drawable.template1, R.drawable.template1, R.drawable.template1,
                R.drawable.template1, R.drawable.template1, R.drawable.template1,
                R.drawable.template1};

        templateAdapter = new TemplateAdapter(templates_data, getContext());

        mLayoutManager = new GridLayoutManager(getActivity(),2 );
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,20,false));
        recyclerView.setAdapter(templateAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    }
