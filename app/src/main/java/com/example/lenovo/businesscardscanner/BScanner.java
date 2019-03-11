package com.example.lenovo.businesscardscanner;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import java.io.OutputStream;
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
    EditText n, pn, e, p , c;
    Button btnsave, view;
    Spinner spin2;
    String status2 = "Sync is OFF";
    String status = "Sync is ON";
    DBHandler myDB;
    TextRecognizer detector;
    ImageView iv1;
    String v1;
    String a;
    TextView openContacts;
    SparseArray<TextBlock> origTextBlocks;
    final int REQUEST_CODE_GALLERY = 999;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bscanner);
        myDB = new DBHandler(this);
        n = (EditText) findViewById(R.id.name);
        listView = (ListView) findViewById(R.id.listView);
        pn = (EditText) findViewById(R.id.cpnum);
        e = (EditText) findViewById(R.id.email);
        p = (EditText) findViewById(R.id.position);
        c = (EditText) findViewById(R.id.company);
        spin2 = (Spinner) findViewById(R.id.country);
        btnsave = (Button) findViewById(R.id.save1);
        view = (Button) findViewById(R.id.view);
        iv1 = (ImageView) findViewById(R.id.iv1);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        openContacts = (Button) findViewById(R.id.save2);
        btnsave.setOnClickListener(this);


        GalleryImage();
        cbCountry();
        getEditData();
        TextDetectorCropImage();
        CaptureImage();
        openContacts();

        e.setSingleLine(false);
        e.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);



    }

    public void openContacts()
    {
        openContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addToContacts();
            }
        });
    }

    public void CaptureImage()
    {
        /* Getting ImageBitmap from Camera from Main Activity */
        Intent intent_camera = getIntent();
        Bitmap camera_img_bitmap = (Bitmap) intent_camera
                .getParcelableExtra("BitmapImage");
        if (camera_img_bitmap != null) {
            iv1.setImageBitmap(camera_img_bitmap);
        }

    }
    public void GalleryImage()
    {
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
    }
    public void TextDetectorCropImage() {

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
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";

                    String lines = "";
                    String words = "";

                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here

                        TextBlock tBlock = textBlocks.valueAt(index);
                        TextBlock tBlockk = textBlocks.valueAt(0);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";

                        validator(blocks);
                        for (Text line : tBlockk.getComponents()) {
                            //extract scanned text lines here
                            lines =  line.getValue() + "\n";

                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() ;
                            }
                        }
                        for (Text line : tBlockk.getComponents()) {
                            //extract scanned text lines here
                            lines =  line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() ;

                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        n.setText("Scan Failed: Found nothing to scan");
                    } else {


                        n.setText(n.getText() + lines + "\n");


                    }
                } else {
                    n.setText("Could not set up the detector!");
                }
            }
        }
    }

    public void getEditData()
    {
        Intent intent = getIntent();
        a = intent.getStringExtra("name");
        String b = intent.getStringExtra("phonenumber");
        String c1 = intent.getStringExtra("email");
        String d = intent.getStringExtra("position");
        String e1 = intent.getStringExtra("company");

        n.setText(a);
        pn.setText(b);
        c.setText(c1);
        p.setText(d);
        e.setText(e1);
    }

    public void cbCountry()
    {

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
    }
    private static final String EMAIL_PATTERN =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])" ;

    private static final String Company_PATTERN =
            "(?:^|\\s)(?:Corporation|Corp|Inc|Incorporated|Company|LTD|PLLC|P\\.C)\\.?$";

    private static final String NAME_PATTERN =
            "^[a-zA-Z][.][15]$";

    private static final String Phone_PATTERN =
            "(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";

    private static final String Position_PATTERN =
            "(?:^|\\s)(?:Manager|Account Manager|IT Analyst|CONSULTANT|Executive|Consultant|Editor|SalesP\\.C)\\.?$";



    public void validator(String recognizeText) {
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Pattern cpattern = Pattern.compile(Company_PATTERN);
        Pattern namePattern = Pattern.compile(NAME_PATTERN);
        Pattern phonePattern = Pattern.compile(Phone_PATTERN);
        Pattern posPattern = Pattern.compile(Position_PATTERN);

        String possibleEmail, possibleCompany, possibleName, possiblePhone, possiblePosition;


        possibleEmail = possibleCompany = possiblePhone = possiblePosition = possibleName = "";

        Matcher matcher;

        String[] words = recognizeText.split("\\r?\\n");

        for (String word : words) {

            matcher = emailPattern.matcher(word);
            if (matcher.find()) {
                possibleEmail = possibleEmail + word + " ";
                continue;
            }
            matcher = cpattern.matcher(word);
            if (matcher.find()) {
                possibleCompany = possibleCompany + word + " ";
                continue;
            }
            matcher = namePattern.matcher(word);
            if (matcher.find()) {
                possibleName = possibleName + word + " ";
                continue;

            }
            matcher = phonePattern.matcher(word);
            if (matcher.find()) {
                possiblePhone = possiblePhone + word + " ";
                continue;

            }

            matcher = posPattern.matcher(word);
            if (matcher.find()) {
                possiblePosition = possiblePosition + word + " ";
                continue;

            }

        }




      pn.setText(possiblePhone);
    //  n.setText(possibleName);
        e.setText(possibleEmail);
        c.setText(possibleCompany);
        p.setText(possiblePosition);


    }

    @Override
    public void onClick(View view)
    {



            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


                String fname = n.getText().toString();
                String phone = pn.getText().toString();
                String email = e.getText().toString();
                String position = p.getText().toString();
                String company = c.getText().toString();
                String country = spin2.getSelectedItem().toString();
                String status1 = status;


                HashMap<String, String> postData = new HashMap<>();
                postData.put("txtFname", fname);
                postData.put("txtPhone", phone);
                postData.put("txtEmail", email);
                postData.put("txtPosition", position);
                postData.put("txtCompany", company);
                postData.put("txtCountry", country);
                postData.put("txtStatus", status1);

                myDB.insertData(n.getText().toString(), pn.getText().toString(),
                        e.getText().toString(), p.getText().toString(), c.getText().toString(),
                        spin2.getSelectedItem().toString(), status2);

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

            } else if (myDB.insertData(n.getText().toString(), pn.getText().toString(),
                    e.getText().toString(), p.getText().toString(), c.getText().toString(),
                    spin2.getSelectedItem().toString(), status2)) {

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






    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void addToContacts(){

        // Creates a new Intent to insert a contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        //Checks if we have the name, email and phone number...
        if(n.getText().length() > 0 && ( pn.getText().length() > 0 || e.getText().length() > 0 )){
            //Adds the name...
            intent.putExtra(ContactsContract.Intents.Insert.NAME, n.getText());

            //Adds the email...
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, e.getText());
            //Adds the email as Work Email
            //intent .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);

            //Adds the phone number...
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, pn.getText());
            //Adds the phone number as Work Phone
          //  intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

            //starting the activity...
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
        }


    }


}

