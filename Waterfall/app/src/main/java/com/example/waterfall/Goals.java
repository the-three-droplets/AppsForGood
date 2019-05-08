package com.example.waterfall;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Goals extends AppCompatActivity {

    private Button btn_startRecord;
    private Button btn_stopRecord;
    private Button btn_testPlay;
    private Button btn_testStop;

    private MediaRecorder audioRecorder;
    private MediaPlayer audioPlayer;

    private static final String FILE_NAME = "settings.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navbarBottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.ic_home:
                        Intent openHome = new Intent(Goals.this, MainActivity.class);
                        startActivity(openHome);
                        break;

                    case R.id.ic_notifications:
                        Intent openNotifs = new Intent(Goals.this, Settings.class);
                        startActivity(openNotifs);
                        break;

                    case R.id.ic_goals:
                        break;

                    case R.id.ic_chart:
                        Intent openStats = new Intent(Goals.this, Statistics.class);
                        startActivity(openStats);
                        break;
                }

                return false;
            }
        });

        Menu bottomNavBar = bottomNavigation.getMenu();
        MenuItem item = bottomNavBar.getItem(2);
        item.setChecked(true);

        btn_startRecord = (Button) findViewById(R.id.start_record);
        btn_stopRecord = (Button) findViewById(R.id.stop_record);
        btn_testPlay = (Button) findViewById(R.id.play_test);
        btn_testStop = (Button) findViewById(R.id.stop_test);

        btn_startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((App) getApplicationContext()).savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/voice_recording.3gp";
                setupAudioRecorder();
                Log.d("StartRecordButton","File Path: " + ((App) getApplicationContext()).savePath);
                try {
                    audioRecorder.prepare();
                    audioRecorder.start();
                } catch (IOException e) {
                    Log.d("Audio","Does not pass try statement. Error: " + e);
                    e.printStackTrace();
                }

                Toast.makeText(Goals.this, "Recording...", Toast.LENGTH_SHORT).show();
            }
        });

        btn_stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.stop();
                savePath();
            }
        });

        btn_testPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer = new MediaPlayer();
                try {
                    audioPlayer.setDataSource(((App) getApplicationContext()).savePath);
                    audioPlayer.prepare();
                    audioPlayer.start();
                } catch (IOException e) {
                    Log.d("TestPlaybutton", "Does not pass try statement. Error: " + e);
                    e.printStackTrace();
                }

                Toast.makeText(Goals.this, "Playing...", Toast.LENGTH_SHORT).show();
            }
        });

        btn_testStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPlayer != null) {
                    audioPlayer.stop();
                    audioPlayer.release();
                    setupAudioRecorder();
                }
            }
        });

    }

    private void setupAudioRecorder() {
        Log.d("SetUpMethod", "Called");
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setOutputFile(((App) getApplicationContext()).savePath);
    }

    private void savePath() {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String[] fields = br.readLine().split(",");
            FileOutputStream fos = null;
            try {

                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write((fields[0] + "," + fields[1] + "," + fields[2] + "," + fields[3] + "," + fields[4] + "," + fields[5] + "," + fields[6] + "," + fields[7] + "," + ((App) getApplicationContext()).savePath).getBytes());
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

    }
}
