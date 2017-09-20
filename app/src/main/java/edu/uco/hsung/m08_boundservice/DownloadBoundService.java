package edu.uco.hsung.m08_boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class DownloadBoundService extends Service {

    public final static int DOWNLOAD = 1;
    public final static int DOWNLOAD_RESPONSE = 2;

    class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch(msg.what) {
                case DOWNLOAD:
                    // incoming data
                    String filename = msg.getData().getString("FILENAME");

                    try {
                        Thread.sleep(7000); // faking it's time consuming work
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message response = Message.obtain(null, DOWNLOAD_RESPONSE);
                    Bundle bundleResponse = new Bundle();
                    bundleResponse.putString("ResponseData", filename+new java.util.Date());
                    response.setData(bundleResponse);

                    try {
                        msg.replyTo.send(response);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    }

    private Messenger messenger = new Messenger(new DownloadHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
