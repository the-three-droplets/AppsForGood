package com.example.waterfall;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notifManagerCompat;

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

        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetooth_button);

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openBluetoothPage = new Intent(MainActivity.this, BT.class);
                startActivity(openBluetoothPage);
            }
        });

        notifManagerCompat = NotificationManagerCompat.from(this);

        ImageButton droplets = (ImageButton) findViewById(R.id.three_droplets);

        ProgressBar progressCircle = (ProgressBar) findViewById(R.id.circle_progress);

        TextView percentageDrank = (TextView) findViewById(R.id.text_percent_drank);
        TextView fractionDrank = (TextView) findViewById(R.id.text_fraction_drank);

        sendNotification();
    }

    public void sendNotification() {
        Notification notif = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.threedroplets)
                .setContentTitle("HI")
                .setContentText("DRINK AWAY")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notifManagerCompat.notify(1, notif);
    }


}
