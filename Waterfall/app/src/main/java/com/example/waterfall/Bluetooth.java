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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity implements AdapterView.OnItemClickListener {

    BluetoothAdapter bluetoothAdapter;
    public ArrayList<BluetoothDevice> BTdevices;
    public DeviceListAdapter deviceListAdapter;
    ListView BTdeviceList;

    private final BroadcastReceiver receiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(bluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(Bluetooth.this, "State off",Toast.LENGTH_SHORT).show();
                        Log.d("receiver1","STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(Bluetooth.this, "State Turning Off",Toast.LENGTH_SHORT).show();
                        Log.d("receiver1", "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(Bluetooth.this, "State On",Toast.LENGTH_SHORT).show();

                        Log.d("receiver1", "STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(Bluetooth.this, "State Turning On",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Bluetooth.this, "Discoverability Enabled",Toast.LENGTH_SHORT).show();
                        Log.d("receiver2", "Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(Bluetooth.this, "Discoverability Enabled. Able to receive connections.",Toast.LENGTH_SHORT).show();
                        Log.d("receiver2","Discoverability Enabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(Bluetooth.this, "Discoverability Enabled. Not able to receive connections.",Toast.LENGTH_SHORT).show();
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

    private final BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("receiver3", "ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTdevices.add(device);
                Log.d("receiver3", device.getName() + ":" + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, BTdevices);
                BTdeviceList.setAdapter(deviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver receiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d("receiver4", "BOND_BONDED");
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("receiver4", "BOND_BONDING");
                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d("receiver4", "BOND_NONE");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver1);
        unregisterReceiver(receiver2);
        unregisterReceiver(receiver3);
        unregisterReceiver(receiver4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTdevices = new ArrayList<>();
        BTdeviceList = (ListView) findViewById(R.id.device_list);

        final Switch btntoggleBT = (Switch) findViewById(R.id.toggle_bluetooth_button);
        if (bluetoothAdapter == null) {
            btntoggleBT.setChecked(false);
        }
        else if (bluetoothAdapter.isEnabled()) {
            btntoggleBT.setChecked(true);
        }
        else if (!bluetoothAdapter.isEnabled()) {
            btntoggleBT.setChecked(false);
        }
        Button btntoggleBTDiscoverability = (Button) findViewById(R.id.toggle_discoverable_button);

        btntoggleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBluetooth(v);
            }
        });

        btntoggleBTDiscoverability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDiscoverable(v);
                discoverScan(v);
            }
        });

        BTdeviceList.setOnItemClickListener(Bluetooth.this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver4, filter);
    }

    public void toggleBluetooth(View view) {
        if (bluetoothAdapter == null) {
            Log.d("toggleBluetooth", "Device does not have bluetooth capabilities");
            Toast.makeText(Bluetooth.this, "Device does not have bluetooth capabilities", Toast.LENGTH_SHORT).show();
        }
        else if (!bluetoothAdapter.isEnabled()) {
            Intent enable_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enable_bluetooth);

            IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver1, bluetooth_intent);
        }
        else if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

            IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver1, bluetooth_intent);
        }
    }

    public void toggleDiscoverable(View v) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);

        IntentFilter a = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver2, a);
    }

    public void discoverScan(View v) {
        Log.d("DiscoverScan", "Looking for unpaired devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d("DiscoverScan", "Canceling discovery");

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver3, discoverIntent);
        }

        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver3, discoverIntent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothAdapter.cancelDiscovery();
        Log.d("onItemClick", "You clicked on a device");
        String deviceName = BTdevices.get(i).getName();
        String deviceAddress = BTdevices.get(i).getAddress();

        Log.d("onItemClick", "Device Name: " + deviceName);
        Log.d("onItemClick", "Device Address: " + deviceAddress);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d("onItemClick", "Trying to pair with " + deviceName);
            BTdevices.get(i).createBond();
        }
    }


}
