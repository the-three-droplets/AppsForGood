package com.example.waterfall;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import java.util.UUID;

public class BTConnection_Class implements BluetoothAdapter.LeScanCallback {

    private static final String DEVICE_NAME = "Waterfall";
    private static final String CLASS_TAG = "BTClass";

    // For weight sensor
    private static final UUID WEIGHT_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID WEIGHT_DATA_CHAR = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBTAdapter;
    private BluetoothGatt mConnectedGatt;
    private SparseArray<BluetoothDevice> mDevices;

    private Context context;

    private ProgressDialog mProgress;

    private Medium mInterface;

    //Data
    private double rawWeight;
    private double old_waterLevel;
    private double new_waterLevel;
    private double oldTime_sinceStart;
    private double newTime_sinceStart;
    private double timeSince_lastDrink;
    private double today_waterDrank;


    // Constructor

    public BTConnection_Class(Context context) {
        this.context = context;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevices = new SparseArray<BluetoothDevice>();

        old_waterLevel = 12;
        oldTime_sinceStart = 0;
        // Initialize old values with firebase
    }

    public SparseArray<BluetoothDevice> getDevices() {
        return mDevices;
    }

    public void clearDeviceList() {
        mDevices.clear();
    }

    public void startScan() {
        Log.d(CLASS_TAG, "startScan: Called");
        mBTAdapter.startLeScan(this);
        mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Searching for Base..."));
        mHandler.postDelayed(mStopRunnable,2500);
        mHandler.sendEmptyMessage(MSG_DISMISS);
    }

    public void stopScan() {
        Log.d(CLASS_TAG, "stopScan: Called");
        mBTAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(CLASS_TAG,"onLeScan: Device Found");
        if (DEVICE_NAME.equals(device.getName())) {
            Log.d(CLASS_TAG, "onLeScan: Waterfall found");
            mDevices.put(device.hashCode(), device);
        }
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    public void connect(BluetoothDevice device) {
        mConnectedGatt = device.connectGatt(context,true, mGattCallback);
    }

    public void activityChange(Context activityContext, Medium medInterface) {
        Log.d(CLASS_TAG, "Context and interface initialized.");
        mInterface = medInterface;

        mProgress = new ProgressDialog(activityContext);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(CLASS_TAG, "onConnectionStateChange: Device connected.");
                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
                gatt.discoverServices();
            }
            else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(CLASS_TAG, "onConnectionStateChange: Device disconnected.");
                mHandler.sendEmptyMessage(MSG_DISMISS);
            }
            else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(CLASS_TAG, "onConnectionStateChange: Gatt connection not successful.");
                Log.d(CLASS_TAG, "onConnectionStateChange: Status is " + status);
                mHandler.sendEmptyMessage(MSG_DISMISS);
                if (status == BluetoothGatt.GATT_FAILURE) {
                    Log.d(CLASS_TAG, "onConnectionStateChange: Gatt connection failed.");
                    gatt.disconnect();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Retrieving Data..."));
            readSensor(gatt);
        }

        private void readSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic = gatt.getService(WEIGHT_SERVICE).getCharacteristic(WEIGHT_DATA_CHAR);
            gatt.readCharacteristic(characteristic);
            mHandler.sendEmptyMessage(MSG_DISMISS);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (WEIGHT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_WEIGHT,characteristic));
            }
            mHandler.sendEmptyMessage(MSG_DISMISS);
            setNotifySensor(gatt);
        }

        private void setNotifySensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic = gatt.getService(WEIGHT_SERVICE).getCharacteristic(WEIGHT_DATA_CHAR);
            gatt.setCharacteristicNotification(characteristic, true);
//            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
//            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(desc);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (WEIGHT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_WEIGHT,characteristic));
            }
        }

    };

    private static final int MSG_WEIGHT = 101;
    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            BluetoothGattCharacteristic characteristic;
            switch (msg.what) {
                case MSG_WEIGHT:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        return;
                    }
                    String rawData = new String(characteristic.getValue());
                    if (rawData.isEmpty()) {
                        Log.d(CLASS_TAG, "String is empty");
                    }
                    Log.d(CLASS_TAG, "Received string is " + rawData);
                    Log.d(CLASS_TAG, "String length is " + rawData.length());

                    String[] processedData = rawData.split(",");
                    for (int i = 0; i < processedData.length; i ++) {
                        Log.d(CLASS_TAG, "Processed Data: " + processedData[i]);
                    }
                    try {

                        rawWeight = Double.parseDouble(processedData[0]);
                        //newTime_sinceStart = Double.parseDouble(processedData[1]);

                        //timeSince_lastDrink += newTime_sinceStart - oldTime_sinceStart;

                        old_waterLevel = new_waterLevel;

                        new_waterLevel = rawWeight / 28.35;

                        if (new_waterLevel - old_waterLevel < -0.5) {
                            // Drink was taken
                            // Send data to firebase

                            today_waterDrank += old_waterLevel - new_waterLevel;

                            //timeSince_lastDrink = 0;
                        }

                        String packagedData = Double.toString(today_waterDrank);
                        Log.d(CLASS_TAG, "Packaged Data: " + packagedData);

                        mInterface.sendData(packagedData);
                    }
                    catch (Exception e) {
                        Log.d(CLASS_TAG, "Does not pass try statement. Error: " + e);
                        return;
                    }
                    break;
                case MSG_PROGRESS:
                    Log.d(CLASS_TAG, "Progress Dialog case is called.");
                    if (mProgress == null) {
                        Log.d(CLASS_TAG, "Progress Dialog is null.");
                    }
                    mProgress.setMessage((String) msg.obj);
                    mProgress.show();
                    break;
                case MSG_DISMISS:
                    mProgress.hide();
                    break;
                /*case MSG_CLEAR:
                    clearDisplayValues();
                    break;*/
            }
        }
    };

}
