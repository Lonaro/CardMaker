package michal.cardmaker.view.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.model.ReverseModel;
import michal.cardmaker.presenter.adapter.HistoryPostcardAdapter;
import michal.cardmaker.presenter.adapter.ReverseAdapter;
import michal.cardmaker.presenter.decoration.GridSpacingItemDecoration;
import michal.cardmaker.view.ReverseCreator;


public class ReverseFragment extends Fragment {

    private ImageButton addReverseButton;
    private Context context;

    private RecyclerView reverseRecyclerView;
    private ReverseAdapter reverseAdapter;
    private TextView emptyListTextView;

    public ReverseFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ReverseFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_reverse, container, false);

        addReverseButton = view.findViewById(R.id.reverse_add_button);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            reverseRecyclerView = view.findViewById(R.id.reverse_recyclerview);
            emptyListTextView = view.findViewById(R.id.reverse_empty_list);


            File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker/Reverses");
            if (!direct.exists()) {
                Toast.makeText(getContext(), "Brak pocztowek", Toast.LENGTH_SHORT);
            }
            else {

                if (direct.isDirectory())
                {
                    File[] listFile;
                    ArrayList<ReverseModel> reverses = new ArrayList<ReverseModel>();

                    listFile = direct.listFiles();

                    if(listFile.length == 0)
                    {
                        emptyListTextView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        String fileName;
                        String name;
                        String address;
                        String message;
                        BufferedReader buf;


                        for (int i = 0; i < listFile.length; i++)
                        {
                            fileName = listFile[i].getAbsolutePath();

                            if(fileName.substring(fileName.length()-3).equals("txt")) {

                                try {
                                    buf = new BufferedReader(new FileReader(fileName));
                                    name = buf.readLine();
                                    address = buf.readLine();
                                    message = "";
                                    String line;
                                    while((line = buf.readLine()) != null) {
                                        message += line;
                                    }
                                    buf.close();

                                    reverses.add(new ReverseModel(name, address, message, fileName.substring(0, fileName.length()-4)+".jpg"));
                                }
                                catch (IOException e) {
                                    Log.e("Exception", "File write failed: " + e.toString());
                                }
                            }
                        }
                        emptyListTextView.setVisibility(View.INVISIBLE);
                        emptyListTextView.refreshDrawableState();
                    }

                    reverseAdapter = new ReverseAdapter(context, reverses, view);
                    reverseRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    reverseRecyclerView.setAdapter(reverseAdapter);
                }

            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    5);
        }

        addReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReverseCreator.class);
                context.startActivity(intent);
            }
        });

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
                reverseAdapter.showDelete();
                break;
            }

        }

        return true;
    }
}
