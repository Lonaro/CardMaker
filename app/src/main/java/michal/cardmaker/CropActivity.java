package michal.cardmaker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import michal.cardmaker.presenter.cropViewLibrary.CropLayout;
import michal.cardmaker.presenter.cropViewLibrary.CropUtils;

public class CropActivity extends AppCompatActivity {

    private CropLayout mCropLayout;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        Uri sourceUri = intent.getData();
        int outputX = intent
                .getIntExtra("outputX", CropUtils.dip2px(this, 200));
        int outputY = intent
                .getIntExtra("outputY", CropUtils.dip2px(this, 200));
        String outputFormat = intent.getStringExtra("outputFormat");

        mDoneButton = (Button) this.findViewById(R.id.done);
        mDoneButton.setOnClickListener(mOnClickListener);

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
                case R.id.done: {
                    mCropLayout.requestCropResult();
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
            Intent intent = new Intent(CropActivity.this, TemplateSinglePhoto.class);
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
