package michal.cardmaker.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import michal.cardmaker.R;
import michal.cardmaker.presenter.BorderSettingsFragmentListener;


public class BorderSettingFragment extends Fragment {

    Button borderButtonColor;
    Button borderSizeUp;
    Button borderSizeDown;
    TextView borderSizeValue;
    int sizeValue;
    Context context;

    BorderSettingsFragmentListener borderSettingsFragmentListener;

    public BorderSettingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BorderSettingFragment(Context c, int size) {
        context = c;
        sizeValue = (int) (size / context.getResources().getDisplayMetrics().density);//Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, Resources.getSystem().getDisplayMetrics()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_border_setting, container, false);

        borderButtonColor = view.findViewById(R.id.border_color_button);
        borderSizeValue = view.findViewById(R.id.border_size_value);
        borderSizeUp = view.findViewById(R.id.increment_border_value);
        borderSizeDown = view.findViewById(R.id.decrement_border_value);
        borderSizeValue.setText(String.valueOf(sizeValue));

        borderSettingsFragmentListener = (BorderSettingsFragmentListener) getContext();

        borderSizeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(String.valueOf(borderSizeValue.getText())) < 20){
                    borderSizeValue.setText(String.valueOf(Integer.parseInt(String.valueOf(borderSizeValue.getText()))+1));
                    borderSettingsFragmentListener.changeBorderSize(Integer.parseInt(String.valueOf(borderSizeValue.getText())));
                }

            }
        });

        borderSizeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(String.valueOf(borderSizeValue.getText())) > 0)
                {
                    borderSizeValue.setText(String.valueOf(Integer.parseInt(String.valueOf(borderSizeValue.getText()))-1));
                    borderSettingsFragmentListener.changeBorderSize(Integer.parseInt(String.valueOf(borderSizeValue.getText())));
                }

            }
        });

        return view;
    }

}
