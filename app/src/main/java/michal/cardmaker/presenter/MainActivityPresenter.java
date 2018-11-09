package michal.cardmaker.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import michal.cardmaker.R;

public class MainActivityPresenter {

    public void setFragment(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_navigation, fragment);
        fragmentTransaction.commit();
    }
}
