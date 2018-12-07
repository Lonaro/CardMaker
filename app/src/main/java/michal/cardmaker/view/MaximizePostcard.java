package michal.cardmaker.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import michal.cardmaker.R;

public class MaximizePostcard extends AppCompatActivity {

    ImageView postcard;
    String photoPath;

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximize_postcard);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("PHOTO");
        Bitmap postcatdBitmap = BitmapFactory.decodeFile(photoPath);
        postcard = findViewById(R.id.maximizePostcard);
        postcard.setImageBitmap(postcatdBitmap);

    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_postcard_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_share_postcard:
            {
                Bitmap bitmapPostcard = ((BitmapDrawable)postcard.getDrawable()).getBitmap();

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapPostcard, "Postcard from CardMaker", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share postcard"));
            }
            case R.id.nav_merge_postcard_with_reverse:
            {
                Intent intent = new Intent(getBaseContext(), ReverseToMergeSelection.class);
                intent.putExtra("PATH", photoPath);
                startActivity(intent);
            }
        }

        return true;
    }
}
