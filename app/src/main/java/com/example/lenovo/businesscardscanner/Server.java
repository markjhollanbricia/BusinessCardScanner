package com.example.lenovo.businesscardscanner;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class Server extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "Server";
    EditText etServer, etUsername, etPass;
    Button btnTest,btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        etServer = (EditText) findViewById(R.id.server);
        etUsername = (EditText) findViewById(R.id.username);
        etPass = (EditText) findViewById(R.id.pass);
        btnTest = (Button) findViewById(R.id.test);
        btnSubmit = (Button) findViewById(R.id.submit);
        btnTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            if (!emptyValidate(etServer, etUsername, etPass)) {

                String server = etServer.getText().toString();
                String username = etUsername.getText().toString();
                String pass = etPass.getText().toString();


                HashMap<String, String> postData = new HashMap<>();
                postData.put("txtServer", server);
                postData.put("txtUsername", username);
                postData.put("txtPass", pass);


                PostResponseAsyncTask task1 = new PostResponseAsyncTask(this,
                        postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Log.d(TAG, s);
                        if (s.contains("ErrorInsert")) {
                            Toast.makeText(Server.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Server.this, "Connected Successfully", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(),
                                    Registration.class);
                            startActivity(in);
                        }
                    }
                });
                task1.execute("http://172.16.50.127/ALT/connection.php");

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Fill out all the fields",
                        Toast.LENGTH_LONG).show();
            }
        }



        else{
        Toast.makeText(getApplicationContext(),"Connection Failed!" ,
                Toast.LENGTH_LONG).show();
    }}
    private boolean emptyValidate(EditText etServer,
                                  EditText etUsername,
                                  EditText etPass

    ){
        String name = etServer.getText().toString();
        String username = etUsername.getText().toString();
        String pass = etPass.getText().toString();


        return (name.isEmpty() && username.isEmpty()&& pass.isEmpty());
    }
}
