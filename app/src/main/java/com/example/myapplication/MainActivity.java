package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    // class members
    EditText IPText;
    EditText PortText;

    @Override
    /*
    creates new activity
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    deals with click on the connect button
     */
    public void onClick(View v) {
        // gets the IP
        IPText = (EditText)findViewById(R.id.IPText);
        String IpStr = IPText.getText().toString();

        // gets the port
        PortText = (EditText)findViewById(R.id.PortText);
        String PortStr = PortText.getText().toString();

        // set the ip and the port of the client connect to server
        ClientConnectServer.setIpAndPort(IpStr, Integer.parseInt(PortStr));
        // get the instance of the client connect to server
        ClientConnectServer clientConnectServer = ClientConnectServer.getInstance();
        // create the task and
        ConnectTask connectTask = new ConnectTask(clientConnectServer);
        connectTask.execute();

        // start the joystick activity
        startActivity(new Intent(MainActivity.this, Joystick.class));
    }
}
