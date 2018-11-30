package michal.cardmaker.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import michal.cardmaker.presenter.ResetTextFragmentListener;
import michal.cardmaker.view.TemplateSinglePhoto;
import yuku.ambilwarna.AmbilWarnaDialog;

public class EditTextFragment extends Fragment {

    SeekBar seekBarTextScale;
    SeekBar seekBarTextRotation;
    TextView insertedText;
    Spinner fontListSpinner;
    Button changeColor;
    Button button_reset_text;
    Button button_clear_text;
    int default_color;

    ResetTextFragmentListener resetTextFragmentListener;

    public EditTextFragment() {

    }

    @SuppressLint("ValidFragment")
    public EditTextFragment(Context context) {
        resetTextFragmentListener = (ResetTextFragmentListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();


//        seekBarTextRotation.setMax(360);
//        seekBarTextRotation.setProgress(180);
//
//        seekBarTextScale.setMax(60);
//        seekBarTextScale.setProgress(30);
//
//        default_color = Color.BLACK;
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
        button_reset_text = view.findViewById(R.id.button_reset_text);
        button_clear_text = view.findViewById(R.id.button_clear_text);

        seekBarTextRotation.setMax(360);
        seekBarTextRotation.setProgress(180);

        seekBarTextScale.setMax(100);
        seekBarTextScale.setProgress(50);

//        default_color = Color.BLACK;

        String [] font_list = {"Arial", "Comics Sans", "Segoe"};
        Typeface [] fonts = {ResourcesCompat.getFont(getContext(), R.font.arial),
                            ResourcesCompat.getFont(getContext(), R.font.comics_sans),
                            ResourcesCompat.getFont(getContext(), R.font.segoe)};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, font_list);
        fontListSpinner.setAdapter(dataAdapter);

        fontListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                insertedText.setTypeface(fonts[position]);
                insertedText.setPivotX(insertedText.getMeasuredWidth()/2);
                insertedText.setPivotY(insertedText.getMeasuredHeight()/2);

                resetTextFragmentListener.setActualFont(font_list[position]);
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
                insertedText.setScaleX((float) (progress) / 50.f);
                insertedText.setScaleY((float) (progress) / 50.f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        button_reset_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetText();
            }
        });

        button_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();

                resetTextFragmentListener.changeFragmentOnTextReset();
            }
        });

        return view;
    }

    public void clearText()  {
        insertedText.setScaleX(1);
        insertedText.setScaleY(1);
        insertedText.setRotation(0);
        insertedText.setX(0);
        insertedText.setY(0);
        insertedText.setTextColor(Color.BLACK);
        default_color = Color.BLACK;
        seekBarTextRotation.setProgress(180);
        seekBarTextScale.setProgress(50);
        insertedText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.arial));
        fontListSpinner.setSelection(0);

        insertedText.setEnabled(false);
        insertedText.setClickable(false);
        insertedText.setVisibility(View.INVISIBLE);
    }

    public void resetText() {
        insertedText.setScaleX(1);
        insertedText.setScaleY(1);
        insertedText.setRotation(0);
        insertedText.setX(0);
        insertedText.setY(0);
        insertedText.setTextColor(Color.BLACK);
        default_color = Color.BLACK;
        seekBarTextRotation.setProgress(180);
        seekBarTextScale.setProgress(50);
        insertedText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.arial));
        fontListSpinner.setSelection(0);
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

    public void setValues(int scale, int rotation, int color) {
        seekBarTextRotation.setProgress(rotation + 180);
        seekBarTextScale.setProgress(scale);
        default_color = color;
    }
}
