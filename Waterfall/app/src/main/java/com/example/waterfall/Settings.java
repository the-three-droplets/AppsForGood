package com.example.waterfall;

import android.content.Intent;
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
    private EditText start_awakeHourEdit;
    private EditText start_awakeMinuteEdit;
    private EditText end_awakeHourEdit;
    private EditText end_awakeMinuteEdit;

    private String ideal_waterTotal;
    private String max_timeInterval;

    private boolean notif_phoneStatus;
    private boolean notif_voiceStatus;

    final Resources res = getResources();

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
        start_awakeHourEdit = (EditText) findViewById(R.id.awake_startHourEdit);
        start_awakeMinuteEdit = (EditText) findViewById(R.id.awake_startMinuteEdit);
        end_awakeHourEdit = (EditText) findViewById(R.id.awake_endHourEdit);
        end_awakeMinuteEdit = (EditText) findViewById(R.id.awake_endMinuteEdit);

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


        timeIntervalEdit = (EditText) findViewById(R.id.time_interval_edit);
        waterTotalEdit = (EditText) findViewById(R.id.water_total_edit);

        Button saveBTN = (Button) findViewById(R.id.set_button);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timeIntervalEdit.getText().toString().isEmpty() && !waterTotalEdit.getText().toString().isEmpty() && !start_awakeHourEdit.getText().toString().isEmpty() && !start_awakeMinuteEdit.getText().toString().isEmpty() && !end_awakeHourEdit.getText().toString().isEmpty() && !end_awakeMinuteEdit.getText().toString().isEmpty()) {
                    saveSettings(v);
                }
                else {
                    Toast.makeText(Settings.this, "You did not enter a valid value for one of the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Access current settings in shared preferences
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
            start_awakeHourEdit.setText(fields[4]);
            start_awakeMinuteEdit.setText(fields[5]);
            end_awakeHourEdit.setText(fields[6]);
            end_awakeMinuteEdit.setText(fields[7]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the changes made by user to the settings file
     * @param v the view of the activity
     */
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
            String start_hour = start_awakeHourEdit.getText().toString();
            String start_minute = start_awakeMinuteEdit.getText().toString();
            String end_hour = end_awakeHourEdit.getText().toString();
            String end_minute = end_awakeMinuteEdit.getText().toString();
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
