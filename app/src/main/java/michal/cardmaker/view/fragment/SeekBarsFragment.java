package michal.cardmaker.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import michal.cardmaker.R;
import michal.cardmaker.presenter.listener.ResetItemFragmentListener;


public class SeekBarsFragment extends Fragment {

    private SeekBar seekBar_rotate;
    private SeekBar seekBar_scale;

    private ImageButton button_reset_item;
    private ImageButton button_clear_item;

    private TextView stickerRotationValue;
    private TextView stickerScaleValue;

    private ImageView item;

    ImageView sticker;

    ResetItemFragmentListener resetItemFragmentListener;

    public SeekBarsFragment(){

    }

    @SuppressLint("ValidFragment")
    public SeekBarsFragment(ImageView s, Context context) {
        sticker = s;
        resetItemFragmentListener = (ResetItemFragmentListener) context;


    }

    @Override
    public void onResume() {
        super.onResume();



//        seekBar_rotate.setMax(360);
//        seekBar_rotate.setProgress(180);
//
//        seekBar_scale.setMax(200);
//        seekBar_scale.setProgress(100);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_seek_bars, container, false);

        seekBar_rotate = view.findViewById(R.id.seekBar_rotate);
        seekBar_scale = view.findViewById(R.id.seekBar_scale);

        button_reset_item = view.findViewById(R.id.button_reset_item);
        button_clear_item = view.findViewById(R.id.button_clear_item);

        stickerRotationValue = view.findViewById(R.id.sticker_rotation_value);
        stickerScaleValue = view.findViewById(R.id.sticker_scale_value);

        item = getActivity().findViewById(R.id.item);

        stickerRotationValue.setText("0°");
        stickerScaleValue.setText("x1");

        seekBar_rotate.setMax(360);
        seekBar_rotate.setProgress(180);
        seekBar_scale.setMax(200);
        seekBar_scale.setProgress(100);


        seekBar_rotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sticker.setRotation(progress - 180);
                stickerRotationValue.setText(String.valueOf(progress - 180)+"°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sticker.setScaleX((float) (progress) / 100.f);
                sticker.setScaleY((float) (progress) / 100.f);
                stickerScaleValue.setText("x"+String.valueOf(progress / 100.f));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button_reset_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetItem();


            }
        });

        button_clear_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearItem();
                resetItemFragmentListener.changeFragmentOnItemReset();
            }
        });

        return view;

    }


    public void resetItem() {
        sticker.setScaleX(1);
        sticker.setScaleY(1);
        sticker.setRotation(0);
        sticker.setX(0);
        sticker.setY(0);
        seekBar_rotate.setProgress(180);
        seekBar_scale.setProgress(100);
        stickerRotationValue.setText("0°");
        stickerScaleValue.setText("x1");
    }

    public void clearItem() {
        sticker.setScaleX(1);
        sticker.setScaleY(1);
        sticker.setRotation(0);
        sticker.setX(0);
        sticker.setY(0);
        seekBar_rotate.setProgress(180);
        seekBar_scale.setProgress(100);
        sticker.setEnabled(false);
        sticker.setClickable(false);
        sticker.setVisibility(View.INVISIBLE);
        stickerRotationValue.setText("0°");
        stickerScaleValue.setText("x1");
    }

    public void setValues(int scale, int rotation) {

        seekBar_rotate.setProgress(rotation + 180);
        seekBar_scale.setProgress(scale);
    }
}
