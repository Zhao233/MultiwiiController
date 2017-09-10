package com.example.administrator.multiwiicontroller.ScanDevice;

import android.content.Context;
import android.os.Handler;
import android.os.health.TimerStat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/26.
 */

public class TimerProgressBar extends ProgressBar{
    private int timeInterval; //after this timeInterval,the progress wii add one

    public TimerProgressBar(Context context) {
        super(context);
    }

    public TimerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimerProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTimeInterval(int timeInterval){
        this.timeInterval = timeInterval;
    }

    public void show(){
        setProgress(0);
        count();
    }
    private void count(){
        new Handler().postDelayed(() -> {
            addOne();

            if(getProgress() != getMax()) {
                count();
            }
        },timeInterval*1000);
    }
    /*
    this one might be better

    public void show(int newTimeInterval){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addOne();

                if(getProgress() != getMax()) {
                    show(timeInterval);
                }
            }
        },newTimeInterval);
    }*/

    public void addOne(){
        super.setProgress(getProgress()+1);
    }
}
