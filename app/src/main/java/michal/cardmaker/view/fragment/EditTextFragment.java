package michal.cardmaker.view.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import michal.cardmaker.R;
import yuku.ambilwarna.AmbilWarnaDialog;

public class EditTextFragment extends Fragment {

    SeekBar seekBarTextScale;
    SeekBar seekBarTextRotation;
    TextView insertedText;
    Spinner fontListSpinner;
    Button changeColor;
    int default_color;

    public EditTextFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        seekBarTextRotation.setMax(360);
        seekBarTextRotation.setProgress(180);

        seekBarTextScale.setMax(60);
        seekBarTextScale.setProgress(30);

        default_color = Color.BLACK;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_text, container, false);

        seekBarTextRotation = view.findViewById(R.id.seekBarTextRotation);
        seekBarTextScale = view.findViewById(R.id.seekBarTextScale);
        insertedText = getActivity().findViewById(R.id.text);
        changeColor = view.findViewById(R.id.changeColorButton);
        fontListSpinner = view.findViewById(R.id.fontListSpinner);

        seekBarTextRotation.setMax(360);
        seekBarTextRotation.setProgress(180);

        seekBarTextScale.setMax(100);
        seekBarTextScale.setProgress(50);

        String [] font_list = {"Arial", "Comics Sans", "Segoe"};
        Typeface [] fonts = {ResourcesCompat.getFont(getContext(), R.font.arial),
                            ResourcesCompat.getFont(getContext(), R.font.comic),
                            ResourcesCompat.getFont(getContext(), R.font.segoepr)};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, font_list);
        fontListSpinner.setAdapter(dataAdapter);

        fontListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                insertedText.setTypeface(fonts[position]);
                insertedText.setPivotX(insertedText.getMeasuredWidth()/2);
                insertedText.setPivotY(insertedText.getMeasuredHeight()/2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        seekBarTextRotation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                insertedText.setRotation(progress - 180);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarTextScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                insertedText.setScaleX((float) (progress) / 10.f);
                insertedText.setScaleY((float) (progress) / 10.f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return view;
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), default_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                default_color = color;
                insertedText.setTextColor(default_color);
            }
        });
        colorPicker.show();
    }
}
