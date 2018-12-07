package michal.cardmaker.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import michal.cardmaker.R;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ReverseCreator extends AppCompatActivity {

    ConstraintLayout recipientLayout;
    ConstraintLayout messageLayout;
    ImageButton saveReverse;
    TextView recipientSumUp;
    TextView messageSumUp;

    EditText recipientName;
    EditText recipientAddress;
    EditText recipientPostcode;
    EditText recipientCity;
    EditText recipientCountry;
    ImageButton recipientAddValidate;

    EditText messageInput;
    Spinner fontFamily;
    Spinner fontSize;
    ImageButton changeColor;
    ImageButton messageAddValidate;

    ImageButton alignToTop;
    ImageButton alignToBottom;
    ImageButton alignToCenterV;
    ImageButton alignToLeft;
    ImageButton alignToRight;
    ImageButton alignToCenterH;

    int vertical = Gravity.TOP;
    int horizontal = Gravity.LEFT;

    ImageView reverseLine;
    int default_color;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revers_creator);

        recipientLayout = findViewById(R.id.reverse_recipient_all);
        messageLayout = findViewById(R.id.reverse_message_all);
        saveReverse = findViewById(R.id.reverse_save);
        recipientSumUp = findViewById(R.id.recipient_values);
        messageSumUp = findViewById(R.id.reverse_message_value);

        recipientName = findViewById(R.id.reverse_recipient_name);
        recipientAddress = findViewById(R.id.reverse_recipient_address);
        recipientPostcode = findViewById(R.id.reverse_recipient_postcode);
        recipientCity = findViewById(R.id.reverse_recipient_city);
        recipientCountry = findViewById(R.id.reverse_recipient_country);
        recipientAddValidate = findViewById(R.id.reverse_recipient_validate);

        messageInput = findViewById(R.id.reverse_message_input);
        fontFamily = findViewById(R.id.reverse_fontListSpinner);
        fontSize = findViewById(R.id.reverse_font_size);
        changeColor = findViewById(R.id.reverse_message_change_color);
        messageAddValidate = findViewById(R.id.reverse_message_validate);

        alignToBottom = findViewById(R.id.reverse_align_to_bottom);
        alignToTop = findViewById(R.id.reverse_align_to_top);
        alignToCenterV = findViewById(R.id.reverse_align_to_center_v);
        alignToLeft = findViewById(R.id.reverse_align_to_left);
        alignToRight = findViewById(R.id.reverse_align_to_right);
        alignToCenterH = findViewById(R.id.reverse_align_to_center_h);

        alignToCenterH.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.CENTER_HORIZONTAL | vertical);
            horizontal = Gravity.CENTER_HORIZONTAL;
        });

        alignToLeft.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.LEFT | vertical);
            horizontal = Gravity.LEFT;
        });

        alignToRight.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.RIGHT | vertical);
            horizontal = Gravity.RIGHT;
        });

        alignToTop.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.TOP | horizontal);
            vertical = Gravity.TOP;
        });

        alignToCenterV.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.CENTER_VERTICAL | horizontal);
            vertical = Gravity.CENTER_VERTICAL;
        });

        alignToBottom.setOnClickListener(v -> {
            messageSumUp.setGravity(Gravity.BOTTOM | horizontal);
            vertical = Gravity.BOTTOM;
        });

        reverseLine = findViewById(R.id.reverse_line);

        default_color = R.color.colorBlack;

        String [] font_list = {"Arial", "Comics Sans", "Segoe"};
        Typeface[] fonts = {ResourcesCompat.getFont(this, R.font.arial),
                ResourcesCompat.getFont(this, R.font.comics_sans),
                ResourcesCompat.getFont(this, R.font.segoe)};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, font_list);
        fontFamily.setAdapter(dataAdapter);
        fontFamily.setSelection(0);

        fontFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageSumUp.setTypeface(fonts[position]);

                //resetTextFragmentListener.setActualFont(font_list[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String [] size_list = {"Small", "Medium", "Large"};
        int [] size_list_values = {14, 22, 30};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, size_list);
        fontSize.setAdapter(sizeAdapter);


        fontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageSumUp.setTextSize(size_list_values[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        recipientAddValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient_values = recipientName.getText() + "\n" + recipientAddress.getText() + "\n" + recipientPostcode.getText() + "\n" + recipientCity.getText() + "\n" + recipientCountry.getText();
                recipientSumUp.setBackground(null);
                recipientSumUp.setText(recipient_values);
            }
        });

        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });


        recipientSumUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientLayout.setVisibility(View.VISIBLE);
                recipientLayout.setClickable(true);
                recipientLayout.setEnabled(true);

                recipientAddValidate.setVisibility(View.VISIBLE);
                recipientAddValidate.setClickable(true);
                recipientAddValidate.setEnabled(true);

                messageLayout.setVisibility(View.INVISIBLE);
                messageLayout.setClickable(false);
                messageLayout.setEnabled(false);

                changeColor.setVisibility(View.INVISIBLE);
                changeColor.setClickable(false);
                changeColor.setEnabled(false);

                messageAddValidate.setVisibility(View.INVISIBLE);
                messageAddValidate.setClickable(false);
                messageAddValidate.setEnabled(false);
            }
        });


        messageAddValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(messageInput.getText());

                messageSumUp.setBackground(null);
                messageSumUp.setText(message);

            }
        });

        messageSumUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientLayout.setVisibility(View.INVISIBLE);
                recipientLayout.setClickable(false);
                recipientLayout.setEnabled(false);

                recipientAddValidate.setVisibility(View.INVISIBLE);
                recipientAddValidate.setClickable(false);
                recipientAddValidate.setEnabled(false);

                messageLayout.setVisibility(View.VISIBLE);
                messageLayout.setClickable(true);
                messageLayout.setEnabled(true);

                changeColor.setVisibility(View.VISIBLE);
                changeColor.setClickable(true);
                changeColor.setEnabled(true);

                messageAddValidate.setVisibility(View.VISIBLE);
                messageAddValidate.setClickable(true);
                messageAddValidate.setEnabled(true);
            }
        });

        saveReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = 3000 / (float)findViewById(R.id.reverse_frame).getWidth();

                Log.d("Scale_test", String.valueOf(scale));

                Bitmap bitmapReverse = Bitmap.createBitmap(3000, 2000, Bitmap.Config.ARGB_8888);
                bitmapReverse.eraseColor(getResources().getColor(R.color.colorWhite));

                Canvas canvasReverse = new Canvas(bitmapReverse);

                Bitmap drawableBitmap = ((BitmapDrawable)reverseLine.getDrawable()).getBitmap();
                Bitmap line = Bitmap.createScaledBitmap(drawableBitmap, (int)(drawableBitmap.getWidth()*scale), (int)(drawableBitmap.getHeight()*scale), false);
                canvasReverse.drawBitmap(line, reverseLine.getX()*scale, reverseLine.getY()*scale, null);

                messageSumUp.setBackgroundColor(getResources().getColor(R.color.colorWhiteTransparent));
                messageSumUp.buildDrawingCache();
                Bitmap messageBitmap = Bitmap.createBitmap(messageSumUp.getDrawingCache());
                Bitmap message = Bitmap.createScaledBitmap(messageBitmap, (int) (messageBitmap.getWidth() * scale), (int) (messageBitmap.getHeight() * scale), false);
                canvasReverse.drawBitmap(message, (int)messageSumUp.getX()*scale, (int)messageSumUp.getY()*scale, null);

                recipientSumUp.setBackgroundColor(getResources().getColor(R.color.colorWhiteTransparent));
                recipientSumUp.buildDrawingCache();
                Bitmap recipientBitmap = Bitmap.createBitmap(recipientSumUp.getDrawingCache());
                Bitmap recipient = Bitmap.createScaledBitmap(recipientBitmap, (int)(recipientSumUp.getWidth()*scale), (int)(recipientSumUp.getHeight()*scale), false);
                canvasReverse.drawBitmap(recipient, (int)recipientSumUp.getX()*scale, (int)recipientSumUp.getY()*scale, null);

                String path = "";
                Date currentTime;
                DateFormat dateFormat;

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    File direct = new File(Environment.getExternalStorageDirectory() + "/Pictures/CardMaker/Reverses");
                    if (!direct.exists()) {
                        File wallpaperDirectory = new File("/sdcard/Pictures/CardMaker/Reverses");
                        wallpaperDirectory.mkdirs();
                    }

                    dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    currentTime = Calendar.getInstance().getTime();
                    path = "/sdcard/Pictures/CardMaker/Reverses/";

                    File file = new File(path+ dateFormat.format(currentTime).toString() + ".jpg");
                    try {
                        OutputStream out = null;
                        out = new FileOutputStream(file);

                        bitmapReverse.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("FRAGMENT", "REVERSE");

                    String nameResult = String.valueOf(recipientName.getText());
                    String addressResult = String.valueOf(recipientAddress.getText());
                    String messageResult = String.valueOf(messageSumUp.getText());

                    try {
                        BufferedWriter buf = new BufferedWriter(new FileWriter(path+dateFormat.format(currentTime).toString()+".txt"));
                        buf.append(nameResult + "\n" + addressResult + "\n" + messageResult);
                        buf.close();
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }

                    startActivity(intent);

                }
                else
                {
                    ActivityCompat.requestPermissions(ReverseCreator.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }



            }
        });


    }

    @Override
    public void onBackPressed() {
        if(messageSumUp.getText().length() == 0 && recipientSumUp.getText().length() == 0)
        {
            Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            goToMainActivity.putExtra("FRAGMENT", "REVERSE");
            startActivity(goToMainActivity);
        }
        else
        {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
            alertBox.setMessage(R.string.exit_from_templates);
            alertBox.setPositiveButton("Yes", (dialog, which) -> {
                Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                goToMainActivity.putExtra("FRAGMENT", "REVERSE");
                startActivity(goToMainActivity);
            });
            alertBox.setNegativeButton("No", null);
            alertBox.create().show();
        }
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, default_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                default_color = color;
                messageSumUp.setTextColor(default_color);
            }
        });
        colorPicker.show();
    }
}
