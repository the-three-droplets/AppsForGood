package com.example.waterfall;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class App extends Application {

    public BTConnection_Class mBTClass;
    public static final String CHANNEL_ID = "Bharath";
    private BluetoothAdapter mBTAdapter;

    private static final String CLASS_TAG = "App";
    private long count;
    private long number;
    private int dataFire = 0;

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

    public void pushWeight(double ounces) {
        count++;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Weight");
        Log.d("MainActivityCount1", Long.toString(count));
        String countString = Long.toString(count);
        myRef.child(countString).setValue(ounces);
        counter();
    }

    public int getData(String location, int number) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);
        final String key = Integer.toString(number);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().equals(key)) {
                        Log.d("MainActivityValue",child.getValue().toString());
                        dataFire = Integer.parseInt(child.getValue().toString());
                        Log.d("MainActivityReturn",Integer.toString(dataFire));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });
        return dataFire;
    }

    public void counter() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference countRef = database.getReference("Count");
        countRef.child("1").setValue(count);
    }
}
