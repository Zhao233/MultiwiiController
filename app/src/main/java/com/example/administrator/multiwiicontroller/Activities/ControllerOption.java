package com.example.administrator.multiwiicontroller.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.administrator.multiwiicontroller.BlutoothService.BlueToothService;
import com.example.administrator.multiwiicontroller.R;
import com.example.administrator.multiwiicontroller.ScanDevice.ScanAdapter;
import com.example.administrator.multiwiicontroller.ScanDevice.TimerProgressBar;

/**
 * Created by Administrator on 2017/9/4.
 */

public class ControllerOption extends Activity{
    private AlertDialog bluetoothOptionDialog;
    TimerProgressBar bar;
    Button scanDevice;
    ListView bluetoothDevices;
    ScanAdapter bluetoothScanAdapter;

    public ListView listView;
    public ArrayAdapter<String> adapter;

    private BlueToothService.MyBinder binder;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.options_controller);

        binder = (BlueToothService.MyBinder)getIntent().getBundleExtra("bundle").get("blueToothBinder");

        bluetoothOptionDialog = new AlertDialog.Builder(this).create();

        listView = (ListView) findViewById(R.id.optionList);

        adapter = new ArrayAdapter(this, R.layout.item_controloption);

        adapter.add("蓝牙设置");
        adapter.add("飞控设置");

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id)-> {
            switch (position){
                case 0 : showDialog();
            }
        });
    }

    public void showDialog(){
        if(bluetoothOptionDialog == null) {
            bluetoothOptionDialog = new AlertDialog.Builder(this).create();
            bluetoothOptionDialog.setTitle("选择需要连接的蓝牙设备");
        }

        bluetoothOptionDialog.show();
        bluetoothOptionDialog.getWindow().setContentView(R.layout.dialog_bluetooth_option);

        if(bar == null || scanDevice == null || bluetoothDevices == null) {
            bluetoothDevices = (ListView) bluetoothOptionDialog.findViewById(R.id.bluetoothdevices);
            bar = (TimerProgressBar) bluetoothOptionDialog.findViewById(R.id.scanProgress);
            scanDevice = (Button) bluetoothOptionDialog.findViewById(R.id.scanButton);

            bar.setMax(3);
            bar.setTimeInterval(1);

            bluetoothScanAdapter = new ScanAdapter(this);
            bluetoothScanAdapter.devices = binder.getDevices();

            scanDevice.setOnClickListener(e -> {
                bar.show();
                binder.ScanDevice();
            });

            bluetoothDevices.setOnItemClickListener(((parent, view, position, id) -> {
                binder.SetDevice(binder.getDevices().get(position));
            }));
        }
    }
}

/*public class LeftRightSlideActivity extends Activity {
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button button = (Button)findViewById(R.id.button1);
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(LeftRightSlideActivity.this, SlideSecondActivity.class);
            startActivity(intent);
            //设置切换动画，从右边进入，左边退出
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    });
}
}
*/
