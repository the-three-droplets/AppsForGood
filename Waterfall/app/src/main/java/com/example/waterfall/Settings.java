package com.example.waterfall;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings extends AppCompatActivity {

    private static final String FILE_NAME = "settings.txt";
    private EditText timeIntervalEdit;
    private EditText waterTotalEdit;
    private ToggleButton voiceNotif;
    private ToggleButton phoneNotif;
    private TimePicker start_awakeTime;
    private TimePicker end_awakeTime;

    private String ideal_waterTotal;
    private String max_timeInterval;

    private boolean notif_phoneStatus;
    private boolean notif_voiceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.ic_home:
                        Intent openHome = new Intent(Settings.this, MainActivity.class);
                        startActivity(openHome);
                        break;

                    case R.id.ic_notifications:
                        break;

                    case R.id.ic_goals:
                        Intent openGoals = new Intent(Settings.this, Goals.class);
                        startActivity(openGoals);
                        break;

                    case R.id.ic_chart:
                        Intent openStats = new Intent(Settings.this, Statistics.class);
                        startActivity(openStats);
                        break;
                }

                return false;
            }
        });

        Menu bottomNavBar = bottomNavigation.getMenu();
        MenuItem item = bottomNavBar.getItem(1);
        item.setChecked(true);

        voiceNotif = (ToggleButton) findViewById(R.id.sound_notifpic);
        phoneNotif = (ToggleButton) findViewById(R.id.phone_notifpic);
        start_awakeTime = (TimePicker) findViewById(R.id.awake_startEdit);
        end_awakeTime = (TimePicker) findViewById(R.id.awake_endEdit);

        final Resources res = getResources();

        voiceNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundDrawable(res.getDrawable(R.drawable.ic_voicenotif_aqua));
                }
                else {
                    buttonView.setBackgroundDrawable(res.getDrawable(R.drawable.ic_voicenotif_grey));
                }
            }
        });

        phoneNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundDrawable(res.getDrawable(R.drawable.ic_phonenotif_aqua));
                }
                else {
                    buttonView.setBackgroundDrawable(res.getDrawable(R.drawable.ic_phonenotif_grey));

                }
            }
        });

        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetooth_button);


        timeIntervalEdit = (EditText) findViewById(R.id.time_interval_edit);
        waterTotalEdit = (EditText) findViewById(R.id.water_total_edit);

        Button saveBTN = (Button) findViewById(R.id.set_button);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings(v);
            }
        });

        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            max_timeInterval = fields[0];
            ideal_waterTotal = fields[1];
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
            timeIntervalEdit.setText(max_timeInterval);
            waterTotalEdit.setText(ideal_waterTotal);
            phoneNotif.setChecked(notif_phoneStatus);
            voiceNotif.setChecked(notif_voiceStatus);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings(View v) {

        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            String timeInterval = timeIntervalEdit.getText().toString();
            String waterTotal = waterTotalEdit.getText().toString();
            String phoneStatus;
            String voiceStatus;
            String start_hour = Integer.toString(start_awakeTime.getHour());
            String start_minute = Integer.toString(start_awakeTime.getMinute());
            String end_hour = Integer.toString(end_awakeTime.getHour());
            String end_minute = Integer.toString(end_awakeTime.getMinute());
            if (phoneNotif.isChecked()) {
                phoneStatus = "on";
            }
            else {
                phoneStatus = "off";
            }
            if (voiceNotif.isChecked()) {
                voiceStatus = "on";
            }
            else {
                voiceStatus = "off";
            }
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write((timeInterval + "," + waterTotal + "," + voiceStatus + "," + phoneStatus + "," + start_hour + "," + start_minute + "," + end_hour + "," + end_minute + "," + fields[8]).getBytes());
                Toast.makeText(this, "Saved setting to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ((App) getApplicationContext()).updateSettings();
    }

}
