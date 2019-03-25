package com.example.waterfall;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {

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

    }

}
