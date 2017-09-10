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
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.multiwiicontroller.BlutoothService.BlueToothService;
import com.example.administrator.multiwiicontroller.Protocol.ProtocolSender;
import com.example.administrator.multiwiicontroller.R;
import com.example.administrator.multiwiicontroller.ScanDevice.ScanAdapter;
import com.example.administrator.multiwiicontroller.ScanDevice.TimerProgressBar;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ScanDeviceActivity extends Activity{
    //<preamble>,<direction>,<size>,<command>
    byte[] message = {0x24/*$*/,0x4D/*M*/, 0x3C/*<*/, 0x00, 0x64, 0x64};
    byte[] message2 = {'$','M','<','0',100,100};


    private Context context;
    private Handler handler;
    Intent serviceIntent;

    ListView deviceList;
    ScanAdapter adapter;
    Button scanDevice;
    TimerProgressBar scanProgress;
    Button sendButton;

    BlueToothService blueToothService;
    BlueToothService.MyBinder op;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            op = (BlueToothService.MyBinder) service;
            blueToothService = op.getService();

            adapter.devices = op.getDevices();
            deviceList.setAdapter(adapter);

            initBluetooth();
            startService(serviceIntent);

            if(!op.IsBLEeabled()){
                op.EnableBluetooth();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            blueToothService = null;
        }
    };

    ArrayList<String> DeviceName;

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_scan);
        DeviceName = new ArrayList<>();

        /*system widgets*/
        context = this;
        handler = new Handler(){//update the list
            @Override
            public void handleMessage(Message message){
                if(message.what == 0) {
                    adapter.notifyDataSetChanged();
                }
            }
        };
        serviceIntent = new Intent(ScanDeviceActivity.this, BlueToothService.class);

        /*Bluetooth Service*/
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);//bind the BluetoothService

        /*Activity widgets*/
        deviceList = (ListView) findViewById(R.id.deviceList);
        scanDevice = (Button) findViewById(R.id.scanButton);
        sendButton = (Button) findViewById(R.id.send);
        scanProgress = (TimerProgressBar) findViewById(R.id.scanProgress);

        adapter = new ScanAdapter(this);
        adapter.notifyDataSetChanged();

        /*config the widgets*/
        scanProgress.setMax(3);
        scanProgress.setTimeInterval(1);

        scanDevice.setOnClickListener(e -> {
            if(op != null) {
                op.ScanDevice();
                scanProgress.show();
            }
        });

        sendButton.setOnClickListener(e -> {
            if(op != null){
                op.SendMessage();
            }
        });

        deviceList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) ->{
            if( op.ConnectDevice(adapter.devices.get(position)) ){
                Toast.makeText(context,"connected to the"+adapter.devices.get(position).getName(),Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"connect fail",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initBluetooth(){
        BluetoothManager bluetoothManager= (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if(op != null) {
            op.InitBluetooth(this, bluetoothManager);
        }
    }
}
