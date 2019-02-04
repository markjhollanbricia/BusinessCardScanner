package com.example.lenovo.businesscardscanner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private static final int CAMER_PERMISION_CODE = 200;
    private static final int STORGE_PERMISION_CODE = 400;
    private static final int IMG_PICK_GALLARY_CODE = 1000;
    private static final int IMG_PICK_CAMERA_CODE = 1001;
    TextBlock textBlock;
    Uri imgUri;
    String camerPermission[];
    String storgePermission[];
    ImageButton cam;

    DBHandler myDB;
    EditText search;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;
    //private static final int REQUEST_IMAGE_CAPTURE = 101;
   // private static final int REQUEST_GALLERY = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        camerPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storgePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cam = (ImageButton)findViewById(R.id.imageButton2);

        search = (EditText) findViewById(R.id.editText);
        myDB = new DBHandler(this);

        final ListView listView = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this,R.layout.row,mList);
        listView.setAdapter(mAdapter);

        Cursor cursor = myDB.getAllData();
        mList.clear();
        while(cursor.moveToNext()) {
            final int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String company = cursor.getString(6);
            byte[] image = cursor.getBlob(8);
            String status = cursor.getString(9);

            mList.add(new Model(id, name, company, image, status));
        }
            mAdapter.notifyDataSetChanged();
            if (mList.size() == 0) {
                Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();
            }
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    return false;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Intent intent = new Intent(getApplicationContext(), BScanner.class);
                    intent.putExtra("x","");

                    startActivity(intent);

                }
            });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.commommenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==R.id.itm1)
        {
            Toast.makeText(this,"More is clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, edit.class));
        }
        else if(id==R.id.itm2)
        {
            Toast.makeText(this,"Synchronization is clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Camera.class));
        }
        else if(id==R.id.Sort)
        {
            Toast.makeText(this,"Sort by is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.Edit)
        {
            Toast.makeText(this,"Edit groups is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.Move)
        {
            Toast.makeText(this,"Move to group is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.Import)
        {
            Toast.makeText(this,"Import contacts is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.Export)
        {
            Toast.makeText(this,"Export contacts is clicked", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void Cam(View view)
    {
        if (!chickCameraPermision()) {
            requestCameraPermision();
        } else {
            pickCamera();
        }
    }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            if (resultCode==RESULT_OK)
            {
                // Toast.makeText(this, "result 1 ok", Toast.LENGTH_SHORT).show();
                if (requestCode==IMG_PICK_GALLARY_CODE)
                {
                    CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
                }
                if (requestCode==IMG_PICK_CAMERA_CODE)
                {
                    CropImage.activity(imgUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                }
                if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                {

                    CropImage.ActivityResult result=CropImage.getActivityResult(data);


                    if (resultCode==RESULT_OK) {
                        // Toast.makeText(this, "result 2 ok", Toast.LENGTH_SHORT).show();
                        Uri resultUri = result.getUri();
                        Log.d("ffddf","go requrdt"+resultUri);
                        Intent intent = new Intent(this,BScanner.class);
                        intent.putExtra("data",resultUri.toString());
                        startActivity(intent);
                        finish();


                        //get Drawable

                    }
                    else if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                        Exception error = result.getError();
                        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMER_PERMISION_CODE:
                if (grantResults.length>0)
                {
                    boolean cameraAc=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean wirteEX = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAc&&wirteEX)
                    {
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this, "refused", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case STORGE_PERMISION_CODE:
                if (grantResults.length>0)
                {

                    boolean wirteEX = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (wirteEX)
                    {
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this, "refused", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }
    private void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "NewPic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(cameraIntent, IMG_PICK_CAMERA_CODE   );

    }
    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMG_PICK_GALLARY_CODE);
    }
    private void requestStorgePermision() {
        ActivityCompat.requestPermissions(this, storgePermission, STORGE_PERMISION_CODE);
    }

    private boolean chickStorgePermision() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermision() {
        ActivityCompat.requestPermissions(this, camerPermission, CAMER_PERMISION_CODE);

    }

    private boolean chickCameraPermision() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public void Register(View view)
    {
        if (!chickStorgePermision()) {
            requestStorgePermision();
        } else {
            pickGallery();
        }

    }

}
