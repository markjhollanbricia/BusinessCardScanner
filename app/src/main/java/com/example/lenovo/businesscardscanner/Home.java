package com.example.lenovo.businesscardscanner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Home extends AppCompatActivity {
    ImageButton cam;
    ImageView iv;
    DBHandler myDB;
    ArrayList<Model> mList;



    RecordListAdapter mAdapter = null;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_GALLERY = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        cam = (ImageButton)findViewById(R.id.imageButton2);
        iv = (ImageView) findViewById(R.id.iv1);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==R.id.itm1)
        {
            Toast.makeText(this,"More is clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Camera.class));
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
            Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (imageTakeIntent.resolveActivity(getPackageManager()) != null)
            {
                startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

        @Override
        protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data)
        {
            if (resCode == RESULT_OK)
            {
                if (reqCode == REQUEST_IMAGE_CAPTURE)
                {
                    if (data != null)
                    {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        /* Passing BITMAP to the Second Activity */
                        Intent IntentCamera = new Intent(this, BScanner.class);
                        IntentCamera.putExtra("BitmapImage", photo);
                        startActivity(IntentCamera);
                    }
                }
                else if (reqCode == REQUEST_GALLERY)
                {
                   Uri imageUri = data.getData();
                   InputStream inputStream;
                   try
                   {
                       inputStream = getContentResolver().openInputStream(imageUri);
                       Bitmap image = BitmapFactory.decodeStream(inputStream);

                       iv.setImageBitmap(image);
                   }
                   catch(FileNotFoundException e)

                    {
                        e.printStackTrace();
                        Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        }


    public void Register(View view)
    {
       Intent i = new Intent(Intent.ACTION_PICK);

        File PD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String PDPATH = PD.getPath();
        Uri data = Uri.parse(PDPATH);

        i.setDataAndType(data,"image/*");
        startActivityForResult(i,REQUEST_GALLERY);

    }

}
