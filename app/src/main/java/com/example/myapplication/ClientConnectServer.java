package com.example.myapplication;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class ClientConnectServer {

    private static String SERVER_IP; //server IP address
    private static int SERVER_PORT;;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // singleton
    private static ClientConnectServer tcpClientSingltone = null;


    /*
    get instance of the client connect server
     */
    public static ClientConnectServer getInstance()
    {
        if (tcpClientSingltone == null) {
            tcpClientSingltone = new ClientConnectServer();
        }
        return tcpClientSingltone;
    }

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public static void setIpAndPort(String IP, int port) {
        SERVER_IP = IP;
        SERVER_PORT = port;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mBufferOut = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                }

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }
}