package michal.cardmaker.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import michal.cardmaker.R;


public class SeekBarsFragment extends Fragment {

    private SeekBar seekBar_rotate;
    private SeekBar seekBar_scale;
    ImageView sticker;

    public SeekBarsFragment(){

    }

    @SuppressLint("ValidFragment")
    public SeekBarsFragment(ImageView s) {
        sticker = s;
    }

    @Override
    public void onResume() {
        super.onResume();
        seekBar_rotate.setMax(360);
        seekBar_rotate.setProgress(180);

        seekBar_scale.setMax(200);
        seekBar_scale.setProgress(100);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_seek_bars, container, false);

        seekBar_rotate = view.findViewById(R.id.seekBar_rotate);
        seekBar_scale = view.findViewById(R.id.seekBar_scale);

        seekBar_rotate.setMax(360);
        seekBar_rotate.setProgress(180);
        seekBar_scale.setMax(200);
        seekBar_scale.setMax(100);


        seekBar_rotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sticker.setRotation(progress - 180);
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
}
