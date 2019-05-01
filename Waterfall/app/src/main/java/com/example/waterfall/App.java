package com.example.waterfall;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.util.Log;

public class App extends Application {

    public BTConnection_Class mBTClass;
    public static final String CHANNEL_ID = "Bharath";
    private BluetoothAdapter mBTAdapter;

    private static final String CLASS_TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter != null) {
            Log.d("App", "Bluetooth Adapter Initialized");
            mBTClass = new BTConnection_Class(this);
        }

        createNotificationChannel();

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, "87", NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.setDescription("Idk man pls work");

            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();


    }
}
