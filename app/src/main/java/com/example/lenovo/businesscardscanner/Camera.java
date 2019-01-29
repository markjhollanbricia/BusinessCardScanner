package com.example.lenovo.businesscardscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

public class Camera extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_GALLERY = 1;
    ImageView mimageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mimageView = (ImageView) findViewById(R.id.iv1);


    }

    public void takePicture(View view) {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
        if (resCode == RESULT_OK)
        {
            if (reqCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    /* Passing BITMAP to the Second Activity */
                    Intent IntentCamera = new Intent(this, BScanner.class);
                    IntentCamera.putExtra("BitmapImage", photo);
                    startActivity(IntentCamera);
                }
            } else if (reqCode == REQUEST_GALLERY) {
                if (data != null) {
                    Uri selectedImgUri = data.getData();
                    /* Passing ImageURI to the Second Activity */
                    Intent IntentGallery = new Intent(this, BScanner.class);
                    IntentGallery.setData(selectedImgUri);
                    startActivity(IntentGallery);
                }
            }
        }
    }
}
