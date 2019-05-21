package com.example.waterfall;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class App extends Application {

    public BTConnection_Class mBTClass;
    public static final String CHANNEL_ID = "Bharath";
    private BluetoothAdapter mBTAdapter;

    private static final String CLASS_TAG = "App";
    private long count;
    private long number;
    private int dataFire = 0;

    // Data Format: max time (in hours) between drinks, ideal daily water, voice, phone, start awake hour, start awake minute, end awake hour, end awake minute, audio path
    private static final String originalSettings = "1,64,off,on,8,30,20,30,null";
    private static final String FILE_NAME = "settings.txt";

    private String max_timeInterval;
    private boolean notif_phoneStatus;
    private boolean notif_voiceStatus;
    private int start_awakeHour;
    private int start_awakeMinute;
    private int end_awakeHour;
    private int end_awakeMinute;
    private int dailyGoal;

    public String savePath = "";
    private MediaPlayer audioPlayer;


    @Override
    public void onCreate() {
        super.onCreate();

        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            // Runs only the first time the app is opened
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
                outputStream.write(originalSettings.getBytes());
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Comments", "First time");

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        // Gets the data from the saved preferences file and convert it into useable information
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            max_timeInterval = fields[0];
            dailyGoal = Integer.parseInt(fields[1]);
            if (fields[2].equals("on")) {
                notif_voiceStatus = true;
            }
            else {
                notif_voiceStatus = false;
            }
            if (fields[3].equals("on")) {
                notif_phoneStatus = true;
            }
            else {
                notif_phoneStatus = false;
            }
            start_awakeHour = Integer.parseInt(fields[4]);
            start_awakeMinute = Integer.parseInt(fields[5]);
            end_awakeHour = Integer.parseInt(fields[6]);
            end_awakeMinute = Integer.parseInt(fields[7]);
            savePath = fields[8];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initializes bluetooth
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter != null) {
            Log.d("App", "Bluetooth Adapter Initialized");
            mBTClass = new BTConnection_Class(this, Integer.parseInt(max_timeInterval));
        }
        else {
            Toast.makeText(this, "Device does not support bluetooth.", Toast.LENGTH_SHORT).show();
        }

        createNotificationChannel();
    }

    /**
     * Notifies the user based on the chosen method(s) if the current time is valid
     */
    public void notifyUser(int currentDayDrank) {
        // Gets the current time and converts it into useable information
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String timeString = timeFormat.format(currentTime.getTime());
        String[] processedTime = timeString.split(":");
        int currentHour = Integer.parseInt(processedTime[0]);
        int currentMinute = Integer.parseInt(processedTime[1]);

        // Makes sure user isn't notified when they are asleep
        if (currentHour < start_awakeHour || currentHour > end_awakeHour) {
            return;
        }

        if (currentHour == start_awakeHour && currentMinute < start_awakeMinute) {
            return;
        }

        if (currentHour == end_awakeHour && currentMinute > end_awakeMinute) {
            return;
        }

        // Resets the daily water drank at midnight (new day)
        if (currentHour == 0 && currentMinute < Integer.parseInt(max_timeInterval) * 60) {
            mBTClass.resetDay();
        }

        // Runs when phone notification is active
        if (notif_phoneStatus) {
            int amountLeft = dailyGoal - currentDayDrank;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.threedroplets)
                    .setContentTitle("Waterfall")
                    .setContentText("Please drink some water. You're almost there, only " + amountLeft + "ounces left! It has been more than " + max_timeInterval + " minute(s) since your last drink.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

            managerCompat.notify(8, builder.build());
        }

        // Runs when voice notification is active
        if (notif_voiceStatus) {
            if (savePath.equals("null")) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.threedroplets)
                        .setContentTitle("Waterfall")
                        .setContentText("You have not recorded someone's voice.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                managerCompat.notify(8, builder.build());

            }
            else {
                audioPlayer = new MediaPlayer();
                try {
                    audioPlayer.setDataSource(savePath);
                    audioPlayer.prepare();
                    audioPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method updates the data when a change is made to the settings file
     */
    public void updateSettings() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            max_timeInterval = fields[0];
            if (fields[2].equals("on")) {
                notif_voiceStatus = true;
            }
            else {
                notif_voiceStatus = false;
            }
            if (fields[3].equals("on")) {
                notif_phoneStatus = true;
            }
            else {
                notif_phoneStatus = false;
            }
            start_awakeHour = Integer.parseInt(fields[4]);
            start_awakeMinute = Integer.parseInt(fields[5]);
            end_awakeHour = Integer.parseInt(fields[6]);
            end_awakeHour = Integer.parseInt(fields[7]);
            savePath = fields[8];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBTClass.updateTime(Integer.parseInt(max_timeInterval));
    }

    /**
     * Makes a notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, "87", NotificationManager.IMPORTANCE_HIGH);
            notifChannel.setDescription("Waterfall Notification Service");

            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public void pushWeight(long time, double ounces) {
//        count++;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Weight");
        Log.d("MainActivityCount1", Long.toString(time));
        String timeString = Long.toString(time);
        myRef.child(timeString).setValue(ounces);
//        counter();
    }

    public void getData(String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.d("MainActivityValue",child.getValue().toString());
                    Log.d("MainActivityReturn",Integer.toString(dataFire));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });
    }

//    public void counter() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference countRef = database.getReference("Count");
//        countRef.child("1").setValue(count);
//    }
}
