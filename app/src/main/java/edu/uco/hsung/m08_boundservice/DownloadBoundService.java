package edu.uco.hsung.m08_boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;

public class DownloadBoundService extends Service {

    public final static int DOWNLOAD = 1;
    public final static int DOWNLOAD_RESPONSE = 2;

    private Handler serviceHandler;
    private Messenger messenger;

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceHandler = new Handler(thread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
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
                        bundleResponse.putString("ResponseData",
                                filename + ": download complete at " + new java.util.Date());
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
        };

        messenger = new Messenger(serviceHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
