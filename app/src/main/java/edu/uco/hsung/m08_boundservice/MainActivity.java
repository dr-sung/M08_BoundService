package edu.uco.hsung.m08_boundservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ServiceConnection serviceConnection;
    private Messenger messenger;
    private TextView display;
    private TextView timeDisplay;

    class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadBoundService.DOWNLOAD_RESPONSE:
                    String result = msg.getData().getString("ResponseData");
                    display.setText(result);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.display);
        timeDisplay = (TextView) findViewById(R.id.time_display);
        
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.setText("Download started....");
                Message msg = Message.obtain(null, DownloadBoundService.DOWNLOAD);
                msg.replyTo = new Messenger(new ResponseHandler());
                Bundle b = new Bundle();
                b.putString("FILENAME", "A great movie.mpg");
                msg.setData(b);

                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Button timeButton = (Button) findViewById(R.id.time_button);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDisplay.setText("Current time: " + new java.util.Date());
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messenger = null;
            }
        };

        // bind to the service
        bindService(new Intent(this, DownloadBoundService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

}
