package com.example.waterfall;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.ic_home:
                        break;

                    case R.id.ic_notifications:
                        Intent openNotifs = new Intent(MainActivity.this, Notifications.class);
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

        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetooth_button);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enabledisableBluetooth();
            }
        });

        ProgressBar progressCircle = (ProgressBar) findViewById(R.id.circle_progress);
        TextView percentageDrank = (TextView) findViewById(R.id.text_percent_drank);
        TextView fractionDrank = (TextView) findViewById(R.id.text_fraction_drank);
    }

    public void enabledisableBluetooth() {
        if (bluetoothAdapter == null) {
            //enabledisableBluetooth: Device does not support bluetooth"
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enable_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enable_bluetooth);

            IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //registerReceiver(, bluetooth_intent);
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

            IntentFilter bluetooth_intent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //registerReceiver(, bluetooth_intent);
        }

    }
}
