package michal.cardmaker.view.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.presenter.decoration.GridSpacingItemDecoration;
import michal.cardmaker.presenter.adapter.HistoryPostcardAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView historyPostcardRecyclerView;
    private HistoryPostcardAdapter historyPostcardAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private TextView emptyListTextView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            historyPostcardRecyclerView = view.findViewById(R.id.recyclerview_history_postcard);

            emptyListTextView = view.findViewById(R.id.history_empty_list);


            File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker");
            if (!direct.exists()) {
                Toast.makeText(getContext(), "Brak pocztowek", Toast.LENGTH_SHORT);
            } else {

                if (direct.isDirectory())
                {
                    File[] listFile;
                    ArrayList<String> f = new ArrayList<String>();// list of file paths
                    listFile = direct.listFiles();


                    for (int i = 0; i < listFile.length; i++)
                    {
                        String fileExt = (listFile[i].getAbsolutePath().substring(listFile[i].getAbsolutePath().length()-4));
                        if(fileExt.equals(".jpg"))
                        {
                            f.add(listFile[i].getAbsolutePath());
                        }
                    }
                    if(f.size() == 0)
                    {
                        emptyListTextView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        emptyListTextView.setVisibility(View.INVISIBLE);
                        emptyListTextView.refreshDrawableState();
                    }

                    mLayoutManager = new GridLayoutManager(getActivity(),2);
                    historyPostcardRecyclerView.setLayoutManager(mLayoutManager);
                    historyPostcardRecyclerView.addItemDecoration(new GridSpacingItemDecoration(20, true));
                    historyPostcardAdapter = new HistoryPostcardAdapter(f, getContext(), view);
                    historyPostcardRecyclerView.setAdapter(historyPostcardAdapter);
                }

            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    5);
        }


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.templatehistory_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.nav_delete_postcard:
            {
                historyPostcardAdapter.showDelete();
                break;
            }

        }

        return true;
    }



}
