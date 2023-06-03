package com.gamingz.simplealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import java.util.Calendar;
import android.content.Context;
public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    EditText editText;
    Button set;
    Button delete;
    Button stop;
    TextView remainingTime;
    String timeEntered;
    int hourEntered;
    int minEntered;
    int diffMin;
    int diffHour;
    Thread thread = null;
    public void ButtonClick(){

        timeEntered = editText.getText().toString();
        String[] parts = timeEntered.split(":");
        try {
            hourEntered = Integer.parseInt(parts[0]);
            minEntered = Integer.parseInt(parts[1]);
            Calendar calendarInitial = Calendar.getInstance();
            int hourInitial = calendarInitial.get(Calendar.HOUR_OF_DAY); // 24-hour format
            int minuteInitial = calendarInitial.get(Calendar.MINUTE);

            int flag=1;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Alarm has been set", Toast.LENGTH_SHORT).show();
                }
            });
            while(flag==1){
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
                int minute = calendar.get(Calendar.MINUTE);

                if(hourEntered<hour){
                    diffHour = 24+(hourEntered-hour);
                }
                else{
                    diffHour = hourEntered - hour;
                }

                diffMin = minEntered - minute;

                if(minute==0){
                    minuteInitial=0;
                }

                int totalMinute = (diffHour*60)-(minute-minEntered);
                if(totalMinute!=0 && minute==minuteInitial){

                    int hourCalc = totalMinute/60;
                    int minuteCalc = totalMinute%60;

                    String minToast = "Remaining: "+minuteCalc+" minute(s)";
                    String hourToast = "Remaining: "+hourCalc+" hour(s) and "+minuteCalc+" minute(s)";

                    // When time entered is less than real time (when timer is set for next day)
                    if(minuteCalc<0){
                        int totalMinuteNew = (24*60)+totalMinute;
                        int hourCalcNew = totalMinuteNew/60;
                        int minuteCalcNew = totalMinuteNew%60;
                        String minToastNew = "Remaining: "+minuteCalcNew+" minute(s)";
                        String hourToastNew = "Remaining: "+hourCalcNew+" hour(s) and "+minuteCalcNew+" minute(s)";
                        if(hourCalcNew==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    remainingTime.setText(minToastNew);
                                    remainingTime.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    remainingTime.setText(hourToastNew);
                                    remainingTime.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    }
                    // When timer is set for same day
                    else if(hourCalc==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                remainingTime.setText(minToast);
                                remainingTime.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                remainingTime.setText(hourToast);
                                remainingTime.setVisibility(View.VISIBLE);
                            }
                        });


                    }
                    minuteInitial++;

                }
                String timeReal = (hour+":"+minute).toString();

                if((hourEntered==hour) && (minEntered==minute)){
                    if(player!=null){
                        player.release();
                        player = null;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Time is up", Toast.LENGTH_SHORT).show();
                            stop.setVisibility(View.VISIBLE);
                            remainingTime.setVisibility(View.INVISIBLE);
                            thread=null;
                        }
                    });
                    play();
                    flag=0;
                }
                else{
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        }
        catch (Exception e ){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Invalid", Toast.LENGTH_LONG).show();
                    thread=null;
                }
            });

        }

    }
    public void play(){
        player = MediaPlayer.create(this, R.raw.radar);
        player.setLooping(true);
        player.start();
    }
    public void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.timeInput);
        set = findViewById(R.id.btnDone);

        remainingTime = findViewById(R.id.remTime);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(thread==null){
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            timeEntered = editText.getText().toString();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            ButtonClick();
                        }
                    };
                    thread = new Thread(runnable);
                    thread.start();

                }
                else{
                    Toast.makeText(MainActivity.this, "Active alarm detected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete = findViewById(R.id.btnDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thread!=null){
                    thread.interrupt();
                    thread=null;
                    Toast.makeText(MainActivity.this, "Alarm has been deleted", Toast.LENGTH_SHORT).show();
                    remainingTime.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(MainActivity.this, "No alarm detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop = findViewById(R.id.btnStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                stop.setVisibility(View.INVISIBLE);
            }
        });







    }


}