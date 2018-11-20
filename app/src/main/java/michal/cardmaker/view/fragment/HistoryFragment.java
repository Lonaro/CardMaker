package michal.cardmaker.view.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import michal.cardmaker.R;
import michal.cardmaker.presenter.GridSpacingItemDecoration;
import michal.cardmaker.presenter.adapter.HistoryPostcardAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView historyPostcardRecyclerView;
    private HistoryPostcardAdapter historyPostcardAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            historyPostcardRecyclerView = view.findViewById(R.id.recyclerview_history_postcard);


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
                        f.add(listFile[i].getAbsolutePath());
                    }

                    Log.d("Postcard_size", String.valueOf(f.size()));
                    Log.d("Postcard_size", String.valueOf(f.get(0)));

                    mLayoutManager = new GridLayoutManager(getActivity(),2 );
                    historyPostcardRecyclerView.setLayoutManager(mLayoutManager);
                    historyPostcardRecyclerView.addItemDecoration(new GridSpacingItemDecoration(20, true));
                    historyPostcardAdapter = new HistoryPostcardAdapter(f, getContext());
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

}
