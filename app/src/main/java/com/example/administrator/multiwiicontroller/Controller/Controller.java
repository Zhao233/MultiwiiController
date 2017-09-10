package com.example.administrator.multiwiicontroller.Controller;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.administrator.multiwiicontroller.BlutoothService.BlueToothService;
import com.example.administrator.multiwiicontroller.Protocol.ProtocolSender;
import com.example.administrator.multiwiicontroller.RC.Channel;
import com.example.administrator.multiwiicontroller.RC.Data;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/31.
 */

public class Controller implements ValueCallBack{
    private final int MODE_1 = 0, MODE2 = 1, MODE_3 = 2, MODE_4 = 3;
    public int mode;

    public int time = 20;

    Handler handler;

    public Runnable runnable;

    private Channel channel_Throt;
    private Channel channel_Yaw;
    private Channel channel_Roll;
    private Channel channel_Pitch;

    private Rocker rocker_left;
    private Rocker rocker_right;

    private Data data;
    private ProtocolSender sender;

    TimerTask timerTask;
    Timer timer;

    public Controller(Rocker rocker_left, Rocker rocker_right, BlueToothService.MyBinder binder){
        this.rocker_left = rocker_left;
        this.rocker_right = rocker_right;

        sender.setBlueToothBinder(binder);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                getChannelsFromRocker(channel_Throt,channel_Yaw, rocker_left);
                getChannelsFromRocker(channel_Pitch,channel_Roll, rocker_right);

                makeData(channel_Throt, channel_Yaw, channel_Roll, channel_Pitch);

                sender.makeControlMessage(data);
            }
        };

        timer = new Timer();
    }

    public void getChannelsFromRocker(Channel channel_1, Channel channel_2, Rocker rocker){
        getChannelsFromRocker(0, channel_1, channel_2, rocker);
    }
    public void getChannelsFromRocker(int mode, Channel channel_1, Channel channel_2, Rocker rocker){
        switch (mode){
            case 0 : channel_1 = rocker.getChannel_vertical();
                     channel_2 = rocker.getChannel_horizontal();
                     break;
        }
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public void sendControlMessage(){
       timer.schedule(timerTask,0,time);
    }

    public void stopSend(){
        timer.cancel();
    }

    @Override
    public Data makeData(Channel channel_throt, Channel channel_Yaw, Channel channel_Roll, Channel channel_Pitch) {
        this.data.setData(channel_throt,channel_Yaw,channel_Roll,channel_Pitch);
        return data;
    }
}
