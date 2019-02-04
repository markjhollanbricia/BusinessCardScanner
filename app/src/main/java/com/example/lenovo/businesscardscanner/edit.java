package com.example.lenovo.businesscardscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class edit extends AppCompatActivity {
private Button Button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Button1 = (Button) findViewById(R.id.ButtonRate);
        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit.this, rate.class);
                startActivity(intent);
            }
        });

    }
}
