package com.example.lenovo.businesscardscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.AlphabeticIndex;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextBlock;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements TextWatcher {

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final int STORAGE_PERMISSION_CODE = 400;
    private static final int IMG_PICK_GALLERY_CODE = 1000;
    private static final int IMG_PICK_CAMERA_CODE = 1001;
    Uri imgUri;
    String cameraPermission[];
    String storgePermission[];
    String n;
    ImageButton cam;
    DBHandler myDB;
    EditText search;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storgePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cam = (ImageButton) findViewById(R.id.imageButton2);
        listView = (ListView) findViewById(R.id.listView);
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(this);
        myDB = new DBHandler(this);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        registerForContextMenu(listView);

        Notification();
        Click();
        updateRecordList();
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Cursor c = myDB.getIdData();
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogUpdate(Home.this, arrID.get(position));
                        }
                        if (i == 1) {
                            Cursor c = myDB.getIdData();
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    //----------------
/*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select an Action");
        menu.add(0,v.getId(),0,"Delete");
        menu.add(0,v.getId(),0,"Edit");
    }*/
    private void updateRecordList() {
        try {
            Cursor cursor = myDB.getAllData();
            mList.clear();

            while (cursor.moveToNext()) {
                //  int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String company = cursor.getString(5);
                //  byte[] image = cursor.getBlob(7);
                String status = cursor.getString(7);
                mList.add(new Model(name, company, status));

            }

            mAdapter.notifyDataSetChanged();
            if (mList.size() == 0) {
                Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDialogUpdate(Activity activity, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update);
        dialog.setTitle("Update");

        final EditText neym = dialog.findViewById(R.id.edtname);
        final EditText pone = dialog.findViewById(R.id.edtcpnum);
        final EditText em = dialog.findViewById(R.id.edtemail);
        final EditText po = dialog.findViewById(R.id.edtposition);
        final EditText co = dialog.findViewById(R.id.edtcompany);
        final EditText cnn = dialog.findViewById(R.id.edtautoTextView);
        Button b = dialog.findViewById(R.id.up1);

        SQLiteDatabase x = myDB.getReadableDatabase();
        Cursor cursor = x.rawQuery(String.format("SELECT * FROM   tblholder   WHERE contactid=%d", position), null);
        mList.clear();

        while (cursor.moveToNext()) {
            //  int id = cursor.getInt(0);
            String name = cursor.getString(1);
            neym.setText(name);
            String cpn = cursor.getString(2);
            pone.setText(cpn);
            String emm = cursor.getString(3);
            em.setText(emm);
            String pos = cursor.getString(4);
            po.setText(pos);
            String company = cursor.getString(5);
            co.setText(company);
            String country = cursor.getString(6);
            cnn.setText(country);
            //  byte[] image = cursor.getBlob(7);
            String status = cursor.getString(7);
            mList.add(new Model(name, company, status));

        }


        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myDB.update(
                        neym.getText().toString().trim(), pone.getText().toString().trim(),
                        em.getText().toString().trim(), po.getText().toString().trim(),
                        co.getText().toString().trim(), cnn.getText().toString().trim(), position);
                dialog.dismiss();
                Toast.makeText(Home.this, "Update Successfully", Toast.LENGTH_SHORT).show();


                updateRecordList();
            }
        });
    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Home.this);
        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    myDB.deletedata(idRecord);
                    Toast.makeText(Home.this, "Delete Successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    public void Click() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor res = myDB.getAllData();
                //res.moveToFirst();
                res.moveToPosition(position);
                Intent intent = new Intent(Home.this, BScanner.class);
                intent.putExtra("id", res.getInt(0));
                intent.putExtra("name", res.getString(1));
                intent.putExtra("phonenumber", res.getString(3));
                intent.putExtra("email", res.getString(4));
                intent.putExtra("position", res.getString(5));
                intent.putExtra("company", res.getString(6));
                startActivity(intent);
            }
        });
    }

    void Notification() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Business Card Scanner")
                    .setContentText("Sync Data");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commommenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itm1) {
            Toast.makeText(this, "More is clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, edit.class));
        } else if (id == R.id.itm2) {
            Toast.makeText(this, "Synchronization is clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Camera.class));
        } else if (id == R.id.Sort) {
            Toast.makeText(this, "Sort by is clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Edit) {
            Toast.makeText(this, "Edit groups is clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Move) {
            Toast.makeText(this, "Move to group is clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            Toast.makeText(this, "Import contacts is clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Export) {
            Toast.makeText(this, "Export contacts is clicked", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void Cam(View view) {
        if (!clickCameraPermission()) {
            requestCameraPermission();
        } else {
            pickCamera();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            // Toast.makeText(this, "result 1 ok", Toast.LENGTH_SHORT).show();
            if (requestCode == IMG_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMG_PICK_CAMERA_CODE) {
                CropImage.activity(imgUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    // Toast.makeText(this, "result 2 ok", Toast.LENGTH_SHORT).show();
                    Uri resultUri = result.getUri();
                    Log.d("ffddf", "go requrdt" + resultUri);
                    Intent intent = new Intent(this, BScanner.class);
                    intent.putExtra("data", resultUri.toString());
                    startActivity(intent);
                    finish();


                    //get Drawable

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    Toast.makeText(this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAc = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeEX = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAc && writeEX) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "refused", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0) {

                    boolean wirteEX = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (wirteEX) {
                        pickGallery();
                    } else {
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
        startActivityForResult(cameraIntent, IMG_PICK_CAMERA_CODE);

    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMG_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storgePermission, STORAGE_PERMISSION_CODE);
    }

    private boolean clickStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_PERMISSION_CODE);

    }

    private boolean clickCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public void Register(View view) {
        if (!clickStoragePermission()) {
            requestStoragePermission();
        } else {
            pickGallery();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.mAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}