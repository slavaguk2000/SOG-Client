package com.example.myapplication;

import android.os.AsyncTask;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionTask extends AsyncTask {

    Socket socket;
    FullscreenActivity parent;
    boolean close = true;

    ConnectionTask(FullscreenActivity parent)
    {
        this.parent = parent;
    }

    @Override
    protected String doInBackground(Object[] params) {
        try {
            socket = new Socket(params[0].toString(), 4242);
            close = false;
            parent.setVisibility(false);

            DataInputStream reader = new DataInputStream(socket.getInputStream());
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            int width = 0, height;
            while (width != -1) {
                width = reader.readInt();
                if (width > 0) {
                    height = reader.readInt();
                    int size = width * height;
                    int[] colors = new int[size];
                    int color;
                    for (int i = 0; i < size; i++) {
                        color = reader.readInt();
                        colors[i] = color;
                    }
                    parent.setImage(colors, width, height);
                }
                else parent.setBlackImage();
            }
        }catch(UnknownHostException ex){
            System.out.println("UnknownHostException");
        }catch(IOException ex){
            System.out.println("IOException");
            if (!close) parent.connectToServer();
        }catch (Exception ex){
            System.out.println("Exception");
        }
        return null;
    }

    public void closeSocket()
    {
        close = true;
        try {
            socket.close();
            parent.setVisibility(true);
        }catch(IOException ex){}
    }
}
