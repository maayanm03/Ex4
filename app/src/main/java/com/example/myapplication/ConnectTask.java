package com.example.myapplication;

import android.os.AsyncTask;

public class ConnectTask extends AsyncTask<String, String, ClientConnectServer> {

    private ClientConnectServer mTcpClient;

    /*
    constructor to get a TCPClient object
     */
    public ConnectTask(ClientConnectServer CCServer){
        this.mTcpClient = CCServer;
    }

    /*
    run the tcp client
     */
    @Override
    protected ClientConnectServer doInBackground(String... message) {
        this.mTcpClient.run();
        return null;
    }

}