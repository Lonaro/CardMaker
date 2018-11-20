package michal.cardmaker.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    BorderSettingsFragmentListener borderSettingsFragmentListener;

    public BorderSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_border_setting, container, false);

        borderButtonColor = view.findViewById(R.id.border_color_button);
        borderSizeValue = view.findViewById(R.id.border_size_value);
        borderSizeUp = view.findViewById(R.id.increment_border_value);
        borderSizeDown = view.findViewById(R.id.decrement_border_value);
        borderSizeValue.setText(String.valueOf(10));

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
