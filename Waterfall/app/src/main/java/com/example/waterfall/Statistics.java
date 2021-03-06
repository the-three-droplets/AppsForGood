package com.example.waterfall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

public class
Statistics extends AppCompatActivity {

    // Data
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    //Overrides the onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Switch between bottom navigation tabs
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ic_home:
                        Intent openHome = new Intent(Statistics.this, MainActivity.class);
                        startActivity(openHome);
                        break;

                    case R.id.ic_notifications:
                        Intent openNotifs = new Intent(Statistics.this, Settings.class);
                        startActivity(openNotifs);
                        break;

                    case R.id.ic_goals:
                        Intent openGoals = new Intent(Statistics.this, Goals.class);
                        startActivity(openGoals);
                        break;

                    case R.id.ic_chart:
                        break;
                }
                return false;
            }
        });

        Menu bottomNavBar = bottomNavigation.getMenu();
        MenuItem item = bottomNavBar.getItem(3);
        item.setChecked(true);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        // Set up tabs for the graphs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(R.string.hourly_header);
        tabLayout.getTabAt(1).setText(R.string.daily_header);
        tabLayout.getTabAt(2).setText(R.string.weekly_header);

        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetooth_button);
    }


    // Create hourly, daily, and weekly fragments
    private void setupViewPager(ViewPager viewpager) {
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        Menu bottomNavBar = bottomNavigation.getMenu();
        MenuItem item = bottomNavBar.getItem(3);
        if(item.isChecked() == true) {
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new HourlyFragment());
            adapter.addFragment(new DailyFragment());
            adapter.addFragment(new WeeklyFragment());
            viewpager.setAdapter(adapter);
        }
    }
}
