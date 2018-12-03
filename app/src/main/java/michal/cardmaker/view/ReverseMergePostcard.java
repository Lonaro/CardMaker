package michal.cardmaker.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import michal.cardmaker.R;

public class ReverseMergePostcard extends Activity {

    String postcard_path;
    String reverse_path;

    ImageView postcardMerge;
    ImageView reverseMerge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_merge_postcard);

        Intent intent = getIntent();
        postcard_path = intent.getStringExtra("PATH_POSTCARD");
        reverse_path = intent.getStringExtra("PATH_REVERSE");

        postcardMerge = findViewById(R.id.merge_postcard);
        reverseMerge = findViewById(R.id.merge_reverse);

        Bitmap postcard_bitmap = BitmapFactory.decodeFile(postcard_path);
        Bitmap reverse_bitmap = BitmapFactory.decodeFile(reverse_path);

        postcardMerge.setImageBitmap(postcard_bitmap);
        reverseMerge.setImageBitmap(reverse_bitmap);
    }
}
