package michal.cardmaker.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
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

public class ReverseMergePostcard extends AppCompatActivity{

    String postcard_path;
    String reverse_path;

    ImageView postcardMerge;
    ImageView reverseMerge;
    ImageButton exportPDF;
    ImageButton sharePDF;

    Bitmap postcard_bitmap;
    Bitmap reverse_bitmap;

    float page_width;
    float page_height;

    boolean boolean_save;

    String path;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_merge_postcard);

        Intent intent = getIntent();
        postcard_path = intent.getStringExtra("PATH_POSTCARD");
        reverse_path = intent.getStringExtra("PATH_REVERSE");
        exportPDF = findViewById(R.id.button_export_to_pdf);
        sharePDF = findViewById(R.id.button_export_to_pdf_share);

        postcardMerge = findViewById(R.id.merge_postcard);
        reverseMerge = findViewById(R.id.merge_reverse);

        postcard_bitmap = BitmapFactory.decodeFile(postcard_path);
        reverse_bitmap = BitmapFactory.decodeFile(reverse_path);

        postcardMerge.setImageBitmap(postcard_bitmap);
        reverseMerge.setImageBitmap(reverse_bitmap);

        setPageSize();

        exportPDF.setOnClickListener(view -> savePdf());
        sharePDF.setOnClickListener(view -> sharePdf());
    }

    private void sharePdf() {

        savePdf();

        File outputFile = new File(path,fileName);
        //Uri uri = Uri.fromFile(outputFile);
        Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", outputFile);

        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.setType("application/pdf");
        intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent1.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent1, "Share postcard"));

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

    @SuppressLint({"SdCardPath", "SimpleDateFormat"})
    void savePdf() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

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

        Date currentTime;
        DateFormat dateFormat;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        currentTime = Calendar.getInstance().getTime();
        path = "/sdcard/Pictures/CardMaker/Pdf/";
        fileName = dateFormat.format(currentTime).toString()+".pdf";

        try {
            document.writeTo(new FileOutputStream(path + fileName));
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
