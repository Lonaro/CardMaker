package michal.cardmaker.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import michal.cardmaker.R;
import michal.cardmaker.model.ReverseModel;
import michal.cardmaker.presenter.adapter.ReverseAdapter;
import michal.cardmaker.presenter.adapter.ReverseSelectAdapter;

public class ReverseToMergeSelection extends AppCompatActivity {

    String postcard_path;
    RecyclerView reverse_select_rv;
    ReverseSelectAdapter reverseSelectAdapter;
    TextView emptyReverseSelectTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_to_merge_selection);

        Intent intent = getIntent();
        postcard_path = intent.getStringExtra("PATH");

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            reverse_select_rv = findViewById(R.id.reverse_select_recyclerview);
            emptyReverseSelectTextView = findViewById(R.id.reverse_select_empty_list);

            File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker/Reverses");
            if (!direct.exists()) {
                Toast.makeText(getBaseContext(), "Brak pocztowek", Toast.LENGTH_SHORT);
            }
            else {

                if (direct.isDirectory())
                {
                    File[] listFile;
                    ArrayList<ReverseModel> reverses = new ArrayList<ReverseModel>();

                    listFile = direct.listFiles();

                    if(listFile.length == 0)
                    {
                        emptyReverseSelectTextView.setVisibility(View.VISIBLE);
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
                        emptyReverseSelectTextView.setVisibility(View.INVISIBLE);
                        emptyReverseSelectTextView.refreshDrawableState();
                    }

                    reverseSelectAdapter = new ReverseSelectAdapter(getBaseContext(), reverses, postcard_path);
                    reverse_select_rv.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    reverse_select_rv.setAdapter(reverseSelectAdapter);
                }

            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    5);
        }
    }
}
