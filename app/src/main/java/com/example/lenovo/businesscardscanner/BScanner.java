package com.example.lenovo.businesscardscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BScanner extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    final String TAG = "Contacts";
    EditText n, l, pn, e, p , c;
    Button btnsave, view;
    Spinner spin1, spin2;
    String status2 = "Sync is OFF";
    String status = "Sync is ON";
    DBHandler myDB;
    TextRecognizer detector;
    ImageView iv1;
    String v1;
    String v2;
    SparseArray<TextBlock> origTextBlocks;
    final int REQUEST_CODE_GALLERY = 999;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bscanner);
        myDB = new DBHandler(this);

        n = (EditText) findViewById(R.id.namescanner);
        l = (EditText) findViewById(R.id.lastname);
        listView = (ListView) findViewById(R.id.listView);
        // recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        pn = (EditText) findViewById(R.id.cpnum);
        e = (EditText) findViewById(R.id.email);
        p = (EditText) findViewById(R.id.position);
        c = (EditText) findViewById(R.id.company);
        spin2 = (Spinner) findViewById(R.id.country);
        btnsave = (Button) findViewById(R.id.save1);
        view = (Button) findViewById(R.id.view);
        iv1 = (ImageView) findViewById(R.id.iv1);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        Intent intent = getIntent();
        String cropedImgUri = intent.getStringExtra("data");
        if (cropedImgUri != null) {
            Uri resultUri = Uri.parse(cropedImgUri);
            iv1.setImageURI(resultUri);
            // Toast.makeText(this, "preview", Toast.LENGTH_SHORT).show();
            //get Drawable
            BitmapDrawable bitmapDrawable = (BitmapDrawable) iv1.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            if (!detector.isOperational()) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else {
                if (detector.isOperational() && bitmap != null) {

                    Frame localFrame = new Frame.Builder().setBitmap(bitmap).build();
                    origTextBlocks = detector.detect(localFrame);
                    ArrayList localArrayList = new ArrayList();
                    for (int i = 0; i < origTextBlocks.size(); i++)
                        localArrayList.add(this.origTextBlocks.valueAt(i));
                    Collections.sort(localArrayList, new Comparator() {


                        public int compare(Object paramTextBlock1, Object paramTextBlock2) {
                            TextBlock paramTextBlocknew1 = (TextBlock) paramTextBlock1;
                            TextBlock paramTextBlocknew2 = (TextBlock) paramTextBlock2;

                            int i = paramTextBlocknew1.getBoundingBox().top - paramTextBlocknew2.getBoundingBox().top;
                            int j = paramTextBlocknew2.getBoundingBox().left - paramTextBlocknew2.getBoundingBox().left;
                            if (i != 0)
                                return i;
                            return j;
                        }
                    });
                    StringBuilder localStringBuilder2 = new StringBuilder();
                    Iterator localIterator = localArrayList.iterator();

                    while (localIterator.hasNext()) {
                        TextBlock localTextBlock = (TextBlock) localIterator.next();
                        if ((localTextBlock == null) || (localTextBlock.getValue() == null))
                            continue;
                        localStringBuilder2.append(localTextBlock.getValue());
                        localStringBuilder2.append(" ");
                    }
                    //localStringBuilder2 = all text was scanned  //how about with this code
                    // it works when i wrote all the ()+ or need to split it? try
                    //cant do with spaces 0914 231 12312) Much better the number is 123456789
                    //cause the number here in Philippines is like this +639173314432 or 09173314432 or (+63)9173314432 wow alot or like this 0917 331 4432 lol
                    String woords = localStringBuilder2.toString();
                    String words[] = woords.split(" ");
                    validator(woords);
                    StringBuilder builder = new StringBuilder();
                    StringBuilder builder1 = new StringBuilder();
                    StringBuilder builder2 = new StringBuilder();
                    for (String e : words) {
                        if (e.contains("@")) {
                            builder.append(e);

                          //  this.e.setText(builder.toString());
                        }
                    }
                    for (String p : words) {
                        if (p.contains("+63") && p.length() >= 11 && p.length() <= 15) {
                            builder1.append(p);
                            builder1.append("\n");
                            this.pn.setText(builder1.toString());
                        }
                    }




                    // SparseArray<TextBlock> textBlocks = detector.detect(frame);


                } else {
                    n.setText("Could not set up the detector!");
                }
            }
        }

        btnsave.setOnClickListener(this);


        e.setSingleLine(false);
        e.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        // n.setText(value);
        /* Getting ImageURI from Gallery from Main Activity */
        Uri selectedImgUri = getIntent().getData();
        if (selectedImgUri != null) {
            Log.e("Gallery ImageURI", "" + selectedImgUri);
            String[] selectedImgPath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImgUri,
                    selectedImgPath, null, null, null);
            cursor.moveToFirst();

            int indexCol = cursor.getColumnIndex(selectedImgPath[0]);
            String imgPath = cursor.getString(indexCol);
            cursor.close();
            iv1.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        }

        /* Getting ImageBitmap from Camera from Main Activity */
        Intent intent_camera = getIntent();
        Bitmap camera_img_bitmap = (Bitmap) intent_camera
                .getParcelableExtra("BitmapImage");
        if (camera_img_bitmap != null) {
            iv1.setImageBitmap(camera_img_bitmap);
        }




        String[] Country = new String[]{
                "Country",
                "Malaysia",
                "United States",
                "Indonesia",
                "France",
                "Italy",
                "Singapore",
                "New Zealand",
                "India"

        };





        List<String> list1 = new ArrayList<>(Arrays.asList(Country));
        ArrayAdapter<String> adap1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Country) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adap1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin2.setAdapter(adap1);


        n.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                n.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                l.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                pn.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                e.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                p.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Intent z = getIntent();
        String x = z.getStringExtra("n");
        n.setText(x);



    }


    private static final String EMAIL_PATTERN =
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+" ;



    private static final String Company_PATTERN =
            "(?:^|\\s)(?:Corporation|Corp|Inc|Incorporated|Company|LTD|PLLC|P\\.C)\\.?$";


    private static final String NAME_PATTERN =
            "([A-Za-z]+)";





    public void validator(String recognizeText) {

        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Pattern cpattern = Pattern.compile(Company_PATTERN);
        Pattern namePattern = Pattern.compile(NAME_PATTERN);


        String possibleEmail, possibleCompany, possibleName;
        possibleEmail = possibleCompany = possibleName = "";

        Matcher matcher;

        String[] words = recognizeText.split("\\r?\\n");

        for (String word : words) {
            //try to determine is the word an email by running a pattern check.
            matcher = emailPattern.matcher(word);
            if (matcher.find()) {
                possibleEmail = possibleEmail + word + " ";
                continue;
            }

            //try to determine is the word a phone number by running a pattern check.
            matcher = cpattern.matcher(word);
            if (matcher.find()) {
                possibleCompany = possibleCompany + word + " ";
                continue;
            }

            //try to determine is the word a name by running a pattern check.
            matcher = namePattern.matcher(word);
            if (matcher.find()) {
                possibleName = possibleName + word + " ";
                continue;
            }


        }
        n.setText(possibleName);
        e.setText(possibleEmail);
        c.setText(possibleCompany);


    }

    @Override
    public void onClick(View view)
    {
        byte[] ne = imageViewToByte(iv1);
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            if (!emptyValidate(n, l, pn, e, p)) {

                String fname = n.getText().toString();
                String lname = l.getText().toString();
                String phone = pn.getText().toString();
                String email = e.getText().toString();
                String position = p.getText().toString();
                String company = c.getText().toString();
                String country = spin2.getSelectedItem().toString();
                String status1 = status;


                HashMap<String, String> postData = new HashMap<>();
                postData.put("txtFname", fname);
                postData.put("txtLname", lname);
                postData.put("txtPhone", phone);
                postData.put("txtEmail", email);
                postData.put("txtPosition", position);
                postData.put("txtCompany", company);
                postData.put("txtCountry", country);
                postData.put("txtStatus", status1);

                myDB.insertData(n.getText().toString(), l.getText().toString(), pn.getText().toString(),
                        e.getText().toString(), p.getText().toString(), c.getText().toString(),
                        spin2.getSelectedItem().toString(), status, imageViewToByte(iv1));

                PostResponseAsyncTask task1 = new PostResponseAsyncTask(this,
                        postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Log.d(TAG, s);
                        if (s.contains("ErrorInsert")) {
                            Toast.makeText(BScanner.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BScanner.this, "Data inserted", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(),
                                    Home.class);
                            startActivity(in);
                        }
                    }
                });
                task1.execute("http://172.16.50.127/ALT/contacts.php");
            } else {
                Toast.makeText(getApplicationContext(), "Fill out all the fields",
                        Toast.LENGTH_LONG).show();
            }
        } else if (myDB.insertData(n.getText().toString(), l.getText().toString(), pn.getText().toString(),
                e.getText().toString(), p.getText().toString(), c.getText().toString(),
                spin2.getSelectedItem().toString(), status2, imageViewToByte(iv1))) {

            Toast.makeText(BScanner.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
        } else {
            Toast.makeText(BScanner.this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
        }

    }


    private byte[] imageViewToByte(ImageView iv1) {
        Bitmap bitmap = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "You dont have permission to access the file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.iv1);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv1.setImageURI(data.getData());
            Bitmap bm = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
            saveImageFile(bm);
        }
    }

    public String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "BCS Photos");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
/*
    public void viewAll() {
        final List<String> names = myDB.getAllValues();
        final ArrayAdapter<String> da = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = myDB.getAllData();
                if (cursor.getCount() == 0) {
                    showMessage("Error", "NO DATA TO DISPLAY");
                } else {
                    StringBuffer buffer = new StringBuffer();
                    while (cursor.moveToNext()) {
                        buffer.append("ID: " + cursor.getString(0) + "\n");
                        buffer.append("Firstname: " + cursor.getString(1) + "\n");
                        buffer.append("Lastname: " + cursor.getString(2) + "\n");
                        buffer.append("ContactNo: " + cursor.getString(3) + "\n");
                        buffer.append("Email: " + cursor.getString(4) + "\n");
                        buffer.append("Position: " + cursor.getString(5) + "\n");
                        buffer.append("Company: " + cursor.getString(6) + "\n");
                        buffer.append("Country: " + cursor.getString(7) + "\n");
                        buffer.append("Status: " + "Sync is OFF" + "\n\n");
                        String name = cursor.getString(1);
                        names.add(name);
                    }
                    Toast.makeText(BScanner.this, "you selected view", Toast.LENGTH_SHORT).show();
                    showMessage("Showing " + cursor.getCount() + "record/s", buffer.toString());

                }

            }

        });
    }
    */

    private boolean emptyValidate(EditText n, EditText l, EditText pn, EditText e, EditText p) {
        String fn = n.getText().toString();
        String ln = l.getText().toString();
        String pnum = pn.getText().toString();
        String email = e.getText().toString();
        String pos = p.getText().toString();

        return (fn.isEmpty() && ln.isEmpty() && pnum.isEmpty() && email.isEmpty() && pos.isEmpty());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
