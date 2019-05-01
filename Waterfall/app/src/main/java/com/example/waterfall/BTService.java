package com.example.waterfall;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.UUID;

public class BTService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final UUID WEIGHT_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID WEIGHT_DATA_CHAR = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter mBTAdapter;
    private BluetoothGatt mConnectedGatt;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
