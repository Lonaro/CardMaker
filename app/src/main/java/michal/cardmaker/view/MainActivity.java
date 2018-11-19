package michal.cardmaker.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import michal.cardmaker.R;
import michal.cardmaker.presenter.MainActivityPresenter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;


    // Fragments on activity
    private TemplateFragment templateFragment;
    private ReverseFragment reverseFragment;
    private HistoryFragment historyFragment;
    private SettingsFragment settingsFragment;


    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    // Presenters
    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrame = findViewById(R.id.frame_navigation);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        templateFragment = new TemplateFragment();
        reverseFragment = new ReverseFragment();
        historyFragment = new HistoryFragment();
        settingsFragment = new SettingsFragment();

        mainActivityPresenter = new MainActivityPresenter();
        mainActivityPresenter.setFragment(MainActivity.this, templateFragment);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            Log.i("Permission_write", "First");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.i("Permission_write", "Second");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                Log.i("Permission_write", "Third");
            }
        } else {
            // Permission has already been granted
            Log.i("Permission_write", "Fourth");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.nav_template: {
                        mainActivityPresenter.setFragment(MainActivity.this, templateFragment);
                        return true;
                    }

                    case R.id.nav_reverse: {
                        mainActivityPresenter.setFragment(MainActivity.this, reverseFragment);
                        return true;
                    }

                    case R.id.nav_history: {
                        mainActivityPresenter.setFragment(MainActivity.this, historyFragment);
                        return true;
                    }

                    case R.id.nav_settings: {
                        mainActivityPresenter.setFragment(MainActivity.this, settingsFragment);
                        return true;
                    }

                    default: return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_navigation, fragment);
        fragmentTransaction.commit();

    }
}
