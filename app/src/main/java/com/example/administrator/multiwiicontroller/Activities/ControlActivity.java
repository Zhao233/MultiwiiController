package com.example.administrator.multiwiicontroller.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.administrator.multiwiicontroller.BlutoothService.BlueToothService;
import com.example.administrator.multiwiicontroller.Controller.Rocker;
import com.example.administrator.multiwiicontroller.Protocol.ProtocolSender;
import com.example.administrator.multiwiicontroller.R;
import com.example.administrator.multiwiicontroller.RC.Channel;

/**
 * Created by Administrator on 2017/8/12.
 */

public class ControlActivity extends Activity {
    public static final int THROT_BUTTON = 1;
    public static final int NON_THROT_BUTTON = 0;

    Rocker leftRocker;
    Rocker rightRocker;

    Button border_left;
    Button border_right;

    Button parameterAdjust;
    Button options;

    ProgressBar throtValue;
    ProgressBar yawValue;
    ProgressBar rollValue;
    ProgressBar pitchValue;

    ProtocolSender sender;

    Intent serviceIntent;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (BlueToothService.MyBinder) service;
            blueToothService = binder.getService();

            initBluetooth();
            startService(serviceIntent);

            if(!binder.IsBLEeabled()){
                binder.EnableBluetooth();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            blueToothService = null;
        }
    };
    BlueToothService.MyBinder binder;
    BlueToothService blueToothService;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        /*serviceIntent = new Intent(this, BlueToothService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);//bind the BluetoothService*/

        leftRocker = (Rocker) findViewById(R.id.leftCotrol);
        rightRocker = (Rocker) findViewById(R.id.rightCotrol);

        border_left = (Button) findViewById(R.id.leftBorder);
        border_right = (Button) findViewById(R.id.rightBorder);

        parameterAdjust = (Button) findViewById(R.id.adjustButton);
        options = (Button) findViewById(R.id.option);

        throtValue = (ProgressBar) findViewById(R.id.throtProgress);
        yawValue = (ProgressBar) findViewById(R.id.yawProgress);
        rollValue = (ProgressBar) findViewById(R.id.rollProgress);
        pitchValue = (ProgressBar) findViewById(R.id.pitchProgress);

        setProgressBars(throtValue, yawValue, rollValue, pitchValue);

        options.setOnClickListener(e -> {
            Intent toOptions = new Intent(ControlActivity.this, ControllerOption.class);
            Bundle bundle = new Bundle();
            bundle.putBinder("blueToothBinder", binder);
            toOptions.putExtra("bundle", bundle);

            startActivity(toOptions);
        });

        new Handler().postDelayed(() -> {
            WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

            int[] locations = new int[2];
            int left,width,top,height;

            leftRocker.setCenter();
            border_left.getLocationOnScreen(locations);
            left = locations[0];
            top = locations[1];
            width = border_left.getWidth();
            height = border_left.getHeight();
            leftRocker.setBorder(left,width,top,height);

            Log.i("leftBorder: ", "left:"+String.valueOf(left)+
                                  ",width:"+ String.valueOf(width)+
                                  ",bottom:"+ String.valueOf(top)+
                                  ",height:"+ String.valueOf(height));

            rightRocker.setCenter();
            border_right.getLocationOnScreen(locations);
            left = locations[0];
            top = locations[1];
            width = border_right.getWidth();
            height = border_right.getHeight();
            rightRocker.setBorder(left,width,top,height);

            Log.i("rightBorder: ", "left:"+String.valueOf(left)+
                    ",width:"+ String.valueOf(width)+
                    ",bottom:"+ String.valueOf(top)+
                    ",height:"+ String.valueOf(height));

            leftRocker.setWindwoManager(windowManager);
            rightRocker.setWindwoManager(windowManager);

            leftRocker.startListen();
            rightRocker.startListen();
        }, 500);

        /*leftRocker.setCallback((verticalValue, horizontalValue) -> {
            setBarValue(verticalValue, horizontalValue, THROT_BUTTON);
        });

        rightRocker.setCallback(((verticalValue, horizontalValue) -> {
            setBarValue(verticalValue, horizontalValue, NON_THROT_BUTTON);
        }));*/
    }

   public void initBluetooth(){
        BluetoothManager bluetoothManager= (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if(binder != null) {
            binder.InitBluetooth(this, bluetoothManager);
        }
    }

    public void setBarValue(Channel channel_1, Channel channel_2, int whitch){
        int value_1 = channel_1.getValue() - 1000;
        int value_2 = channel_2.getValue() - 1000;

        if(whitch == 1){//Throt and Yaw
            throtValue.setProgress(value_1);
            yawValue.setProgress(value_2);

        } else {//Roll and Pitch
            rollValue.setProgress(value_1);
            pitchValue.setProgress(value_2);
        }
    }

    public void setProgressBars(ProgressBar... bars){
        for(ProgressBar bar : bars){
            bar.setMax(1000);
        }
    }
}
