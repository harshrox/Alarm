package com.gamingz.simplealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import java.util.Calendar;
import android.content.Context;
public class MainActivity extends AppCompatActivity {


    EditText editText;
    Button button;
//    TextView result;
    String timeEntered;
    int hourEntered;
    int minEntered;
    int diffMin;
    int diffHour;
    public void ButtonClick(String time){

        timeEntered = editText.getText().toString();
        String[] parts = timeEntered.split(":");
        try {
            hourEntered = Integer.parseInt(parts[0]);
            minEntered = Integer.parseInt(parts[1]);
        }
        catch (Exception e ){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

        Calendar calendarInitial = Calendar.getInstance();
        int hourInitial = calendarInitial.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minuteInitial = calendarInitial.get(Calendar.MINUTE);

        int timeLoop=minEntered-minuteInitial;

        int flag=1;
        while(flag==1){
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
            int minute = calendar.get(Calendar.MINUTE);

            diffHour = hourEntered - hour;
            diffMin = minEntered - minute;

            int totalMinute = (diffHour*60)-(minute-minEntered);
            if(totalMinute!=0 && diffMin==timeLoop){

                int hourCalc = totalMinute/60;
                int minuteCalc = totalMinute%60;

                String minToast = minuteCalc+" minute(s) remaining";
                String hourToast = hourCalc+" hour(s) and "+minuteCalc+" minutes remaining";
                if(hourCalc==0){
                    Toast.makeText(this, minToast, Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, hourToast, Toast.LENGTH_LONG).show();
                }
                timeLoop--;

            }
            String timeReal = (hour+":"+minute).toString();

            if((hourEntered==hour) && (minEntered==minute)){
                Toast.makeText(this, "Time is up", Toast.LENGTH_LONG).show();
//                result.setVisibility(View.VISIBLE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.timeInput);
        button = findViewById(R.id.btnDone);
//        result = findViewById(R.id.txtResult);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeEntered = editText.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                ButtonClick(timeEntered);

            }
        });


    }


}