package com.example.waterfall;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "Bluetooth";
    public ArrayList<BluetoothDevice> BTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvUnpairedDevices;
    Switch btn_toggleBT;

    BluetoothAdapter BTAdapter;

    BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BTAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BTAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "receiver1L STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "receiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "receiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "receiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "receiver2: ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDevices.add(device);
                Log.d(TAG, "receiver2: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, BTDevices);
                lvUnpairedDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //Bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "receiver3: BOND_BONDED");
                }
                //Creating a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "receiver3: BOND_BONDING");
                }
                //Destroying a bond
                else if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "receiver3: BOND_NONE");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Is Called");
        super.onDestroy();
        unregisterReceiver(receiver1);
        unregisterReceiver(receiver2);
        unregisterReceiver(receiver3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btn_toggleBT = (Switch) findViewById(R.id.toggleBT_btn);
        lvUnpairedDevices = (ListView) findViewById(R.id.lvUnpairedDevices);

        if (BTAdapter == null) {
            btn_toggleBT.setChecked(false);
        }
        else if (BTAdapter.isEnabled()) {
            btn_toggleBT.setChecked(true);
        }
        else if (!BTAdapter.isEnabled()) {
            btn_toggleBT.setChecked(false);
        }

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver3, filter);

        btn_toggleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnToggleBT_onClick: toggling bluetooth");
                toggleBT();
            }
        });

        lvUnpairedDevices.setOnItemClickListener(Bluetooth.this);
    }

    public void toggleBT() {
        if (BTAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            btn_toggleBT.setChecked(false);
        }
        else if (!BTAdapter.isEnabled()) {
            Log.d(TAG, "toggleBT: enabling bluetooth");
            Toast.makeText(getApplicationContext(), "Enabling Bluetooth", Toast.LENGTH_SHORT).show();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver1, BTIntent);

            btn_toggleBT.setChecked(true);
        }
        else if (BTAdapter.isEnabled()) {
            Log.d(TAG, "toggleBT: disabling bluetooth");
            Toast.makeText(getApplicationContext(), "Disabling Bluetooth", Toast.LENGTH_SHORT).show();
            BTAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver1, BTIntent);

            btn_toggleBT.setChecked(false);
        }
    }


    public void btnScan(View view) {
        Log.d(TAG, "btnScan: Looking for unpaired devices");
        Toast.makeText(getApplicationContext(), "Looking for unpaired devices", Toast.LENGTH_SHORT).show();

        if (BTAdapter.isDiscovering()) {
            BTAdapter.cancelDiscovery();
            Log.d(TAG, "btnScan: Canceling discovery");

            BTAdapter.startDiscovery();
            IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver2, discoverIntent);
        }
        if (!BTAdapter.isDiscovering()) {
            BTAdapter.startDiscovery();
            IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver2, discoverIntent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BTAdapter.cancelDiscovery();

        Log.d(TAG, "OnItemClick: Clicked on a device");
        String deviceName = BTDevices.get(position).getName();
        String deviceAddress = BTDevices.get(position).getAddress();
        Log.d(TAG, "OnItemClick: deviceName = " + deviceName);
        Log.d(TAG, "OnItemClick: deviceAddress = " + deviceAddress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "OnItemClick: Trying to pair with " + deviceName);
            Toast.makeText(getApplicationContext(), "Trying to pair with " + deviceName, Toast.LENGTH_SHORT).show();
            BTDevices.get(position).createBond();
        }

    }
}
