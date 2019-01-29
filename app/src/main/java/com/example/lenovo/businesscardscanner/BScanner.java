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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.HashMap;
import java.util.List;

public class BScanner extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    final String TAG = "Contacts";
    EditText n,l,pn,e,p;
    Button btnsave,view;
    Spinner spin1,spin2;
    String status2 = "Sync is OFF";
    String status = "Sync is ON";
    DBHandler myDB;
    ImageView iv1;
    String value;
    final int REQUEST_CODE_GALLERY  = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bscanner);
        myDB = new DBHandler(this);
        n = (EditText) findViewById(R.id.name);
        l = (EditText) findViewById(R.id.lastname);
        pn = (EditText) findViewById(R.id.cpnum);
        e = (EditText) findViewById(R.id.email);
        p = (EditText) findViewById(R.id.position);
        spin1 = (Spinner)findViewById(R.id.company);
        spin2 = (Spinner) findViewById(R.id.country);
        btnsave =(Button) findViewById(R.id.save1);
        view = (Button) findViewById(R.id.view);
        iv1 = (ImageView) findViewById(R.id.iv1);
        btnsave.setOnClickListener(this);
        viewAll();

        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
             value = extras.getString("x");
        }

        n.setText(value);
        /* Getting ImageURI from Gallery from Main Activity */
        Uri selectedImgUri = getIntent().getData();
        if (selectedImgUri != null) {
            Log.e("Gallery ImageURI", "" + selectedImgUri);
            String[] selectedImgPath = { MediaStore.Images.Media.DATA };
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
        if (camera_img_bitmap != null)
        {
            iv1.setImageBitmap(camera_img_bitmap);
        }




        String[] Company = new String[]{
                "Company",
                "ALT Cladding, Inc.",
                "UNILAB",
                "JP MORGAN",
                "NOVOLAND",
                "AYALA LAND",
                "CAMELLA",
                "SMDC",
                "MDC"

        };
        List<String> list = new ArrayList<>(Arrays.asList(Company));
        ArrayAdapter<String> adap = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,Company)
        {
            @Override
            public boolean isEnabled(int position)
            {
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0)
                {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else
                {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin1.setAdapter(adap);



        n.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                n.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                l.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                pn.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                e.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
                p.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });



    }





    @Override
    public void onClick(View view)
    {
        byte[] ne = imageViewToByte(iv1);
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            if(!emptyValidate(n, l, pn, e,p))
            {

                String fname = n.getText().toString();
                String lname = l.getText().toString();
                String phone = pn.getText().toString();
                String email = e.getText().toString();
                String position = p.getText().toString();
                String company = spin1.getSelectedItem().toString();
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
                        e.getText().toString(), p.getText().toString(), spin1.getSelectedItem().toString(),
                        spin2.getSelectedItem().toString(),status,imageViewToByte(iv1));

                PostResponseAsyncTask task1 = new PostResponseAsyncTask(this,
                        postData, new AsyncResponse()
                {
                    @Override
                    public void processFinish(String s)
                    {
                        Log.d(TAG,s);
                        if(s.contains("ErrorInsert"))
                        {
                            Toast.makeText(BScanner.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(BScanner.this, "Data inserted", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(),
                                    Home.class);
                            startActivity(in);
                        }
                    }
                });
                task1.execute("http://172.16.50.127/ALT/contacts.php");
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Fill out all the fields",
                        Toast.LENGTH_LONG).show();
            }
        }
        else


        if (myDB.insertData(n.getText().toString(), l.getText().toString(), pn.getText().toString(),
                e.getText().toString(), p.getText().toString(), spin1.getSelectedItem().toString(),
                spin2.getSelectedItem().toString(),status2,imageViewToByte(iv1)))
        {

            Toast.makeText(BScanner.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),Home.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(BScanner.this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
        }

    }


    private byte[] imageViewToByte(ImageView iv1)
    {
            Bitmap bitmap = ((BitmapDrawable)iv1.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }
            else
            {
                Toast.makeText(this, "You dont have permission to access the file", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode,resultCode,data);

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
            Bitmap bm=((BitmapDrawable)iv1.getDrawable()).getBitmap();
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
    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void viewAll()
    {
        final    List<String> names = myDB.getAllValues();
        final    ArrayAdapter<String> da = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Cursor cursor = myDB.getAllData();
                if (cursor.getCount() == 0)
                {
                    showMessage("Error", "NO DATA TO DISPLAY");
                }
                else
                    {
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

    private boolean emptyValidate(EditText n, EditText l, EditText pn, EditText e, EditText p)
    {
        String fn = n.getText().toString();
        String ln = l.getText().toString();
        String pnum = pn.getText().toString();
        String email = e.getText().toString();
        String pos = p.getText().toString();
        return (fn.isEmpty() && ln.isEmpty() && pnum.isEmpty() && email.isEmpty() && pos.isEmpty());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
