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
            socket = new Socket(params[0].toString(), 8536);
            close = false;
            parent.setVisibility(false);

            DataInputStream reader = new DataInputStream(socket.getInputStream());
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            int width = 0, height = 0;
            int size = width * height, color;
            int[] colors = new int[size];
            while (width != -1) {
                width = reader.readInt();
                if (width > 0) {
                    height = reader.readInt();
                    int oldSize = size;
                    size = height*width;
                    if(oldSize != size) colors = new int[size];
                    for (int i = 0; i < size; i++) {
                        color = reader.readInt();
                        colors[i] = color;
                        if(i%4000 == 0)
                        {
                            if (size - i > 4000)
                                for (int j = i+4000; j > i; j--)
                                    colors[j] = 0;
                            parent.setDebugMessage("Complited:" + i/4000);
                            parent.setImage(colors, width, height);
                        }
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
        parent.setVisibility(true);
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
