package com.example.waterfall;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class Bluetooth extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver receiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(bluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("receiver1","STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("receiver1", "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("receiver1", "STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("receiver1", "STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(bluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d("receiver2", "Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d("receiver2","Discoverability Enabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d("receiver2","Discoverability Enabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d("receiver2","Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d("receiver2","Connected");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final Switch btntoggleBT = (Switch) findViewById(R.id.toggle_bluetooth_button);
        Button btntoggleBTDiscoverability = (Button) findViewById(R.id.toggle_discoverable_button);

        btntoggleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    Log.d("toggleBluetooth", "Device does not have bluetooth capabilities");
                }
                if (!bluetoothAdapter.isEnabled()) {
                    btntoggleBT.setText(R.string.turnOnBT);
                    Intent enable_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enable_bluetooth);

                    /*IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(receiver1, bluetooth_intent);*/
                }
                if (bluetoothAdapter.isEnabled()) {
                    btntoggleBT.setText(R.string.turnOffBT);
                    bluetoothAdapter.disable();

                    /*IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(receiver1, bluetooth_intent);*/
                }
            }
        });

        btntoggleBTDiscoverability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivity(discoverableIntent);

                IntentFilter a = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(receiver2, a);
            }
        });

        ConstraintLayout.LayoutParams listViewParams = new ConstraintLayout.LayoutParams(0,0);

    }


}
