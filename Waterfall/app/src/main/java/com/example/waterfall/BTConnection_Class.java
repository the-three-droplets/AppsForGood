package com.example.waterfall;
//import com.example.waterfall.App;
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

    // In Hours
    private int maxTime;

    //Data
    private double rawWeight;
    private double oldOfficial_waterLevel;
    private double new_waterLevel;
    private long oldTime_sinceStart;
    private long newTime_sinceStart;
    private double timeSince_lastDrink;
    private double today_waterDrank;
    private double[] pastWaterLevels;
    private int counter;
    private double official_waterLevel;
    // Constructor

    /**
     * Makes an instance of the BT Connection Class
     * @param context the context of the application
     * @param maxTime the maximum time between drinks in minutes
     */
    public BTConnection_Class(Context context, int maxTime) {
        this.context = context;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevices = new SparseArray<BluetoothDevice>();
        this.maxTime = maxTime;

        oldOfficial_waterLevel = 12;
        oldTime_sinceStart = 0;
        official_waterLevel = 12;
        // Initialize old values with firebase

        pastWaterLevels = new double[4];
        counter = 0;
    }

    /**
     * Updates the maximum time between drinks
     * @param maxTime maximum time between drinks
     */
    public void updateTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Gets the list of discovered BLE devices (after BLE scan)
     * @return list of discovered BLE devices
     */
    public SparseArray<BluetoothDevice> getDevices() {
        return mDevices;
    }

    /**
     * Resets the amount of water drank during the day (for new day)
     */
    public void resetDay() {
        today_waterDrank = 0;
    }

    /**
     * Clears the list of BLE devices
     */
    public void clearDeviceList() {
        mDevices.clear();
    }

    /**
     * Starts BLE scan
     */
    public void startScan() {
        Log.d(CLASS_TAG, "startScan: Called");
        mBTAdapter.startLeScan(this);
        mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Searching for Water Bottle..."));
        mHandler.postDelayed(mStopRunnable,2500);
        mHandler.sendEmptyMessage(MSG_DISMISS);
    }

    /**
     * Stops BLE scan
     */
    public void stopScan() {
        Log.d(CLASS_TAG, "stopScan: Called");
        mBTAdapter.stopLeScan(this);
    }

    /**
     * When a device is found from a BLE scan, this method adds the device found to the list of devices if its name is "Waterfall"
     * @param device the BLE device found
     * @param rssi Not pertinent to this code, required from interface
     * @param scanRecord Not pertinent to this code, required from interface
     */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(CLASS_TAG,"onLeScan: Device Found");
        if (DEVICE_NAME.equals(device.getName())) {
            Log.d(CLASS_TAG, "onLeScan: Waterfall found");
            mDevices.put(device.hashCode(), device);
        }
    }

    /**
     * A runnable that stops the BLE scan (for hander)
     */
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    /**
     * A runnable that starts the BLE scan (for handler)
     */
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    /**
     * Initiates a GATT connection with a given BLE device
     * @param device the BLE device to connect to
     */
    public void connect(BluetoothDevice device) {
        mConnectedGatt = device.connectGatt(context,true, mGattCallback);
    }

    /**
     * Initializes the activity for this class to pass data to (not too relevant due to certain changes we made)
     * @param activityContext the context of the acticity
     * @param medInterface the interface to facilitate data transfer
     */
    public void activityChange(Context activityContext, Medium medInterface) {
        Log.d(CLASS_TAG, "Context and interface initialized.");
        mInterface = medInterface;

        mProgress = new ProgressDialog(activityContext);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
    }

    /**
     * Listener for events related to GATT connection
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * Method is called when GATT connection state changes.
         * @param gatt the GATT server connection object
         * @param status the status of the connection
         * @param newState the new state of the connection
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(CLASS_TAG, "onConnectionStateChange: Device connected.");
                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to Water Bottle..."));
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
                    mInterface.isActivated(false);
                    gatt.disconnect();
                }
            }
        }

        /**
         * When a service is discovered, this method will call a method to access the data of the service.
         * @param gatt the GATT server connection object
         * @param status the status of the connection
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Retrieving Data..."));
            readSensor(gatt);
        }

        /**
         * Accesses the data of the service using the UUID
         * @param gatt the GATT server connection object
         */
        private void readSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic = gatt.getService(WEIGHT_SERVICE).getCharacteristic(WEIGHT_DATA_CHAR);
            gatt.readCharacteristic(characteristic);
            mHandler.sendEmptyMessage(MSG_DISMISS);
        }

        /**
         * When a characteristic is read, the handler is called to process and potentially pass data to the activity
         * @param gatt the GATT server connection object
         * @param characteristic the characteristic being read
         * @param status the status of the GATT connection
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (WEIGHT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_WEIGHT,characteristic));
            }
            mHandler.sendEmptyMessage(MSG_DISMISS);
            mInterface.isActivated(true);
            setNotifySensor(gatt);
        }

        /**
         * Makes the app detect changes in the data sent by the BLE device and automatically read the characteristic
         * @param gatt the GATT server connection object
         */
        private void setNotifySensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic = gatt.getService(WEIGHT_SERVICE).getCharacteristic(WEIGHT_DATA_CHAR);
            gatt.setCharacteristicNotification(characteristic, true);
//            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
//            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(desc);
        }

        /**
         * When the characteristic is changed, the handler is called to process and potentially pass the data to the activity
         * @param gatt the GATT server connection object
         * @param characteristic the characteristic that was changed
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (WEIGHT_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_WEIGHT,characteristic));
            }
        }

    };

    /**
     * The handler that handles data transfer between this class and the activity
     */
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
                        oldTime_sinceStart = newTime_sinceStart;
                        rawWeight = Double.parseDouble(processedData[0]);
                        newTime_sinceStart = Long.parseLong(processedData[1]);

                        timeSince_lastDrink += newTime_sinceStart - oldTime_sinceStart;

                        if (timeSince_lastDrink > maxTime * 60) {
                            ((App) context).notifyUser((int) today_waterDrank);
                        }
                        pastWaterLevels[counter] = new_waterLevel;

                        //old_waterLevel = new_waterLevel;

                        new_waterLevel = rawWeight / 28.35;

                        boolean consistent = true;
                        for (int i = 0; i < 3; i ++) {
                            if (Math.abs(pastWaterLevels[i] - pastWaterLevels[i + 1]) > 0.35) {
                                consistent = false;
                            }
                        }

                        if (consistent) {
                            oldOfficial_waterLevel = official_waterLevel;
                            official_waterLevel = new_waterLevel;
                        }

                        if (Math.abs(official_waterLevel - oldOfficial_waterLevel) > 0.5) {
                            // Drink was taken
                            Log.d(CLASS_TAG, "Firebase");
                            ((App) context).pushWeight(newTime_sinceStart, new_waterLevel);

                            today_waterDrank += Math.abs(official_waterLevel - oldOfficial_waterLevel);

                            timeSince_lastDrink = 0;
                        }

                        String packagedData = Double.toString(today_waterDrank) + "," + Double.toString(timeSince_lastDrink);
                        Log.d(CLASS_TAG, "Packagjned Data: " + packagedData);

                        counter = (counter + 1) % 4;

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
