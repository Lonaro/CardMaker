package michal.cardmaker.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ReverseCreator extends Activity {

    ConstraintLayout recipientLayout;
    ConstraintLayout messageLayout;
    Button addMessage;
    Button addRecipient;
    Button saveReverse;
    TextView recipientSumUp;
    TextView messageSumUp;

    EditText recipientName;
    EditText recipientAddress;
    EditText recipientPostcode;
    EditText recipientCity;
    EditText recipientCountry;
    Button recipientAddValidate;

    EditText messageInput;
    Spinner fontFamily;
    Spinner fontSize;
    Button changeColor;
    Button messageAddValidate;

    ImageView reverseLine;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revers_creator);

        recipientLayout = findViewById(R.id.reverse_recipient_all);
        messageLayout = findViewById(R.id.reverse_message_all);
        addMessage = findViewById(R.id.reverse_add_message);
        addRecipient = findViewById(R.id.reverse_add_recipient);
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
        changeColor = findViewById(R.id.reverse_message_change_color);
        messageAddValidate = findViewById(R.id.reverse_message_validate);

        reverseLine = findViewById(R.id.reverse_line);



        recipientAddValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient_values = recipientName.getText() + "\n" + recipientAddress.getText() + "\n" + recipientPostcode.getText() + "\n" + recipientCity.getText() + "\n" + recipientCountry.getText();
                recipientSumUp.setText(recipient_values);
            }
        });


        addRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientLayout.setVisibility(View.VISIBLE);
                recipientLayout.setClickable(true);
                recipientLayout.setEnabled(true);

                messageLayout.setVisibility(View.INVISIBLE);
                messageLayout.setClickable(false);
                messageLayout.setEnabled(false);
            }
        });


        messageAddValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(messageInput.getText());


                messageSumUp.setText(message);

            }
        });

        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipientLayout.setVisibility(View.INVISIBLE);
                recipientLayout.setClickable(false);
                recipientLayout.setEnabled(false);

                messageLayout.setVisibility(View.VISIBLE);
                messageLayout.setClickable(true);
                messageLayout.setEnabled(true);
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
}
