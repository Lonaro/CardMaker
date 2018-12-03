package michal.cardmaker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import michal.cardmaker.R;

public class ReverseMergePostcard extends Activity {

    String postcard_path;
    String reverse_path;

    ImageView postcardMerge;
    ImageView reverseMerge;
    Button exportPDF;

    Bitmap postcard_bitmap;
    Bitmap reverse_bitmap;

    float page_width;
    float page_height;

    boolean boolean_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_merge_postcard);

        Intent intent = getIntent();
        postcard_path = intent.getStringExtra("PATH_POSTCARD");
        reverse_path = intent.getStringExtra("PATH_REVERSE");
        exportPDF = findViewById(R.id.button_export_to_pdf);

        postcardMerge = findViewById(R.id.merge_postcard);
        reverseMerge = findViewById(R.id.merge_reverse);

        postcard_bitmap = BitmapFactory.decodeFile(postcard_path);
        reverse_bitmap = BitmapFactory.decodeFile(reverse_path);

        postcardMerge.setImageBitmap(postcard_bitmap);
        reverseMerge.setImageBitmap(reverse_bitmap);

        setPageSize();

        exportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savePdf();
            }
        });
    }

    //String[] animals = {"A6 (800x1200)", "A5 (1200x1800)", "A4 (1800x2700)", "A3 (2700x4050)"};

    void setPageSize() {
        int tmp_width = postcard_bitmap.getWidth();
        int tmp_heigth = postcard_bitmap.getHeight();

        if(tmp_width > tmp_heigth)
        {
            if(tmp_width == 1200 || tmp_width == 1800 || tmp_width == 2700)
            {
                page_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 297,
                        getResources().getDisplayMetrics());
                page_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 210,
                        getResources().getDisplayMetrics());
            }
            else if (tmp_width == 4050)
            {
                page_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 420,
                        getResources().getDisplayMetrics());
                page_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 297,
                        getResources().getDisplayMetrics());
            }

        }
        else
        {
            if(tmp_width == 1200 || tmp_width == 1800 || tmp_width == 2700)
            {
                page_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 210,
                        getResources().getDisplayMetrics());
                page_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 297,
                        getResources().getDisplayMetrics());
            }
            else if (tmp_width == 4050)
            {
                page_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 297,
                        getResources().getDisplayMetrics());
                page_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 420,
                        getResources().getDisplayMetrics());
            }
        }

        postcard_bitmap = Bitmap.createScaledBitmap(postcard_bitmap, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, postcard_bitmap.getWidth()/10,
                getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, postcard_bitmap.getHeight()/10,
                getResources().getDisplayMetrics()), false);

    }

    void savePdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int)page_width, (int)page_height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        float center_width = (page_width - postcard_bitmap.getWidth())/2;
        float center_height = (page_height - postcard_bitmap.getHeight())/2;
        canvas.drawBitmap(postcard_bitmap, center_width,center_height, null);
        Log.d("Postcard_size", String.valueOf(postcard_bitmap.getWidth()) + " " + String.valueOf(postcard_bitmap.getHeight()));

        document.finishPage(page);


        pageInfo = new PdfDocument.PageInfo.Builder((int)page_width, (int)page_height, 2).create();
        page = document.startPage(pageInfo);

        reverse_bitmap = Bitmap.createScaledBitmap(reverse_bitmap, postcard_bitmap.getWidth(), postcard_bitmap.getHeight(), false);
        canvas = page.getCanvas();
        canvas.drawBitmap(reverse_bitmap, center_width,center_height, null);

        document.finishPage(page);


        File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker/Pdf");
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Pictures/CardMaker/Pdf");
            wallpaperDirectory.mkdirs();
        }

        String path = "";
        Date currentTime;
        DateFormat dateFormat;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        currentTime = Calendar.getInstance().getTime();
        path = "/sdcard/Pictures/CardMaker/Pdf/";

        try {
            document.writeTo(new FileOutputStream(path + dateFormat.format(currentTime).toString()+".pdf"));
            //btn_convert.setText("Check PDF");
            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Something  wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(getBaseContext(), "Save to documents", Toast.LENGTH_SHORT).show();
    }
}
