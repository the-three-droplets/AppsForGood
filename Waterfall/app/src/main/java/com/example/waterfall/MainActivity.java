package com.example.waterfall;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements Medium {

    private static final String FILE_NAME = "settings.txt";

    private ProgressBar progressCircle;
    private TextView tv_percentageDrank;
    private TextView tv_fractionDrank;
    private TextView tv_leftText;
    private TextView tv_rightText;
    // ImageButton bluetoothButton;

    SparseArray<BluetoothDevice> devices;
    //private ProgressDialog mProgress;

    private String ideal_waterTotal;
    private String max_timeInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminate(true);

        ((App) getApplicationContext()).mBTClass.activityChange(this,this);

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.ic_home:
                        break;

                    case R.id.ic_notifications:
                        Intent openNotifs = new Intent(MainActivity.this, Settings.class);
                        startActivity(openNotifs);
                        break;

                    case R.id.ic_goals:
                        Intent openGoals = new Intent(MainActivity.this, Goals.class);
                        startActivity(openGoals);
                        break;

                    case R.id.ic_chart:
                        Intent openStats = new Intent(MainActivity.this, Statistics.class);
                        startActivity(openStats);
                        break;
                }

                return false;
            }
        });

        Menu bottomNavBar = bottomNavigation.getMenu();
        MenuItem item = bottomNavBar.getItem(0);
        item.setChecked(true);

        //bluetoothButton = (ImageButton) findViewById(R.id.bluetooth_button);

        /*bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openBluetoothPage = new Intent(MainActivity.this, BT.class);
                startActivity(openBluetoothPage);
            }
        }); */

        // ImageButton droplets = (ImageButton) findViewById(R.id.three_droplets);
        progressCircle = (ProgressBar) findViewById(R.id.circle_progress);
        progressCircle.setIndeterminate(false);

        tv_percentageDrank = (TextView) findViewById(R.id.text_percent_drank);
        tv_fractionDrank = (TextView) findViewById(R.id.text_fraction_drank);
        tv_leftText = (TextView) findViewById(R.id.left_text);
        tv_rightText = (TextView) findViewById(R.id.right_text);

        devices = new SparseArray<BluetoothDevice>();

        //mProgress = new ProgressDialog(this);
        //mProgress.setIndeterminate(true);
        //mProgress.setCancelable(false);


        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            max_timeInterval = fields[0];
            ideal_waterTotal = fields[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.top_navbar));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        devices = ((App) getApplicationContext()).mBTClass.getDevices();

        for (int i = 0; i < devices.size(); i ++) {
            BluetoothDevice device = devices.valueAt(i);
            menu.add(0,devices.keyAt(i),0,device.getName());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_icon:
                //Nothing
                return true;
            case R.id.action_scan:
                ((App) getApplicationContext()).mBTClass.clearDeviceList();
                ((App) getApplicationContext()).mBTClass.startScan();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invalidateOptionsMenu();
                    }
                }, 3000);
                return true;
            default:
                BluetoothDevice device = devices.get(item.getItemId());
                ((App) getApplicationContext()).mBTClass.connect(device);
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void sendData(final String str) {
        Handler medHandler = new Handler(Looper.getMainLooper());
        medHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] unpackagedData = str.split(",");
                for (int i = 0; i < unpackagedData.length; i ++) {
                    Log.d("MainActivity", "Data Received: " + unpackagedData[i]);
                }
                tv_percentageDrank.setText(Integer.toString((int) (Double.parseDouble(unpackagedData[0])/Integer.parseInt(ideal_waterTotal) * 100)) + "%");
                progressCircle.setProgress((int)(Double.parseDouble(unpackagedData[0])/Integer.parseInt(ideal_waterTotal) * 100));
                tv_fractionDrank.setText(Integer.toString((int)Double.parseDouble(unpackagedData[0])) + " oz drank out of " + ideal_waterTotal + " oz");
                tv_leftText.setText(Integer.toString((int)(Double.parseDouble(unpackagedData[1])/60)) + " minutes since last drink.");
                //Use Firebase to set Right Text
            }
        });
    }
}
