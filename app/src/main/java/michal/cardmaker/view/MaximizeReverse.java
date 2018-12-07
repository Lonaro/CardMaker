package michal.cardmaker.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import michal.cardmaker.R;


public class MaximizeReverse extends Activity {

    ImageView reverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximize_reverse);
        reverse = findViewById(R.id.maximizeReverse);

        Intent intent = getIntent();

        String reversePath = intent.getStringExtra("REVERSE");
        Bitmap reverseBitmap = BitmapFactory.decodeFile(reversePath);

        reverse.setImageBitmap(reverseBitmap);

    }
}
