package com.example.lenovo.businesscardscanner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import java.util.HashMap;
import java.util.List;

public class Registration extends AppCompatActivity implements View.OnClickListener{
    final String TAG = "Registration";
    EditText etName, etUsername, etEmail , etPhone;
    Button btn1,view;
    DBHandler2 myDB1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        myDB1 = new DBHandler2(this);
        view = (Button) findViewById(R.id.view);
        btn1 = (Button) findViewById(R.id.reg1);
        etName = (EditText) findViewById(R.id.name);
        etUsername = (EditText) findViewById(R.id.username);
        etEmail = (EditText) findViewById(R.id.email);
        etPhone = (EditText) findViewById(R.id.cpnum);
        btn1.setOnClickListener(this);
        viewAll();
    }

    @Override
    public void onClick(View view) {
            if (!emptyValidate(etName, etUsername, etEmail, etPhone)) {
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                HashMap<String, String> postData = new HashMap<>();
                postData.put("txtName", name);
                postData.put("txtUsername", username);
                postData.put("txtEmail", email);
                postData.put("txtPhone", phone);
                PostResponseAsyncTask task1 = new PostResponseAsyncTask(this,
                        postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Log.d(TAG, s);
                        if (s.contains("ErrorInsert")) {
                            Toast.makeText(Registration.this, "Data not inserted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Registration.this, "Data inserted", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(),
                                    Camera.class);
                            startActivity(in);
                        }
                    }
                });
                task1.execute("http://172.16.50.146/ALT/registration.php");
                if (myDB1.insertData(etName.getText().toString(), etUsername.getText().toString(), etEmail.getText().toString(),
                        etPhone.getText().toString())) {

                    Toast.makeText(Registration.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),
                            Camera.class);
                    startActivity(in);
                } else {
                    Toast.makeText(Registration.this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
                }
        }
        else{
            Toast.makeText(getApplicationContext(), "Fill out all the fields",
                    Toast.LENGTH_LONG).show();
        }
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
        final List<String> names = myDB1.getAllValues();
        final ArrayAdapter<String> da = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = myDB1.getAllData();
                if (cursor.getCount() == 0) {
                    showMessage("Error", "NO DATA TO DISPLAY");
                }
                else {
                    StringBuffer buffer = new StringBuffer();
                    while (cursor.moveToNext()) {
                        buffer.append("ID: " + cursor.getString(0) + "\n");
                        buffer.append("Name: " + cursor.getString(1) + "\n");
                        buffer.append("Username: " + cursor.getString(2) + "\n");
                        buffer.append("Email: " + cursor.getString(3) + "\n");
                        buffer.append("PhoneNo: " + cursor.getString(4) + "\n\n");


                        String name = cursor.getString(1);
                        names.add(name);
                    }
                    Toast.makeText(Registration.this, "", Toast.LENGTH_SHORT).show();
                    showMessage("Showing " + cursor.getCount() + "record/s", buffer.toString());
                }

            }

        });
    }


    private boolean emptyValidate(EditText etName,
                                  EditText etUsername,
                                  EditText etEmail,
                                  EditText etPhone
    ){
        String name = etName.getText().toString();
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        return (name.isEmpty() && username.isEmpty()&& email.isEmpty()&& phone.isEmpty());
    }
    }

