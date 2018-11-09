package michal.cardmaker.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
