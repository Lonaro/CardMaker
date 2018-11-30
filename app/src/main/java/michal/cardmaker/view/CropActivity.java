package michal.cardmaker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import michal.cardmaker.R;
import michal.cardmaker.TemplateTwoVerticalPhotos;
import michal.cardmaker.presenter.cropViewLibrary.CropLayout;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;

public class CropActivity extends AppCompatActivity {

    private CropLayout mCropLayout;
    private Button mDoneButton;
    private Button mResetButton;
    private Button mMaxButton;
    private int template_number;
    private int photo_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        template_number = intent.getIntExtra("TEMPLATE_NUMBER", 0);
        photo_number = intent.getIntExtra("PHOTO_NUMBER", 0);
        Uri sourceUri = intent.getData();
        int outputX = intent
                .getIntExtra("outputX", CropUtils.dip2px(this, 200));
        int outputY = intent
                .getIntExtra("outputY", CropUtils.dip2px(this, 200));
        String outputFormat = intent.getStringExtra("outputFormat");


        mDoneButton = this.findViewById(R.id.crop_done);
        mResetButton = this.findViewById(R.id.crop_reset);
        mMaxButton = this.findViewById(R.id.crop_maximize);

        mDoneButton.setOnClickListener(mOnClickListener);
        mResetButton.setOnClickListener(mOnClickListener);
        mMaxButton.setOnClickListener(mOnClickListener);

        // bellow
        mCropLayout = (CropLayout) this.findViewById(R.id.crop);
        mCropLayout.setOnCropListener(mOnCropListener);
        mCropLayout.startCropImage(sourceUri, outputX, outputY);
        mCropLayout.setOutputFormat(outputFormat);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.crop_done: {
                    mCropLayout.requestCropResult();
                    break;
                }
                case R.id.crop_reset: {
                    mCropLayout.resetImagePosition();
                    break;
                }
                case R.id.crop_maximize: {
                    mCropLayout.maximizeImage();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private CropLayout.OnCropListener mOnCropListener = new CropLayout.OnCropListener() {

        @Override
        public void onCropResult(Uri data) {
            Intent intent;

            switch(template_number){
                case 0:
                    intent = new Intent(CropActivity.this, TemplateSinglePhoto.class);
                    break;
                case 1:
                    intent = new Intent(CropActivity.this, TemplateTwoVerticalPhotos.class);
                    break;
                default:
                    intent = new Intent(CropActivity.this, TemplateSinglePhoto.class);
            }
            intent.putExtra("PHOTO_NUMBER", photo_number);
            intent.setData(data);
            startActivity(intent);
        }

        @Override
        public void onCropFailed(String errmsg) {

        }

        @Override
        public void onLoadingStateChanged(boolean isLoading) {
            if (mDoneButton != null) {
                mDoneButton.setEnabled(!isLoading);
            }
        }
    };
}
