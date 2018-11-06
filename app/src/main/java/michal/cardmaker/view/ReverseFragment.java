package michal.cardmaker.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michal.cardmaker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReverseFragment extends Fragment {


    public ReverseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reverse, container, false);
    }

}
