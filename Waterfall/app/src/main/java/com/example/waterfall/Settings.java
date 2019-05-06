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
import android.widget.ImageButton;
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
    EditText timeIntervalEdit;
    EditText waterTotalEdit;

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

        ToggleButton soundNotif = (ToggleButton) findViewById(R.id.sound_notifpic);
        ToggleButton phoneNotif = (ToggleButton) findViewById(R.id.phone_notifpic);
        final Resources res = getResources();

        soundNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
            String current_timeInterval = fields[0];
            String current_waterTotal = fields[1];
            timeIntervalEdit.setText(current_timeInterval);
            waterTotalEdit.setText(current_waterTotal);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings(View v) {
        String timeInterval = timeIntervalEdit.getText().toString();
        String waterTotal = waterTotalEdit.getText().toString();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write((timeInterval + "," + waterTotal).getBytes());

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
    }

}
