package com.example.administrator.multiwiicontroller.BlutoothService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/28.
 */

public class BlueToothService extends Service {
    private final IBinder binder = new MyBinder();

    private final int SCAN_PERIOD = 3000;

    private boolean isScanning = false; //the ending signal of the timerProgressBar

    Context context;
    private Handler handler;

    public final String UUID_1 = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public final String UUID_2 = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public BluetoothAdapter bluetoothAdapter;
    public BluetoothManager bluetoothManager;

    public BluetoothDevice savedDevice;

    public BluetoothGatt bluetoothGatt;
    private              BluetoothGattCallback             gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int state, int newState){ // 连接成功后启动服务发现
            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.i("A","启动服务发现：" + bluetoothGatt.discoverServices());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){// 发现服务的回调
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.i("A", "成功发现服务");

                //成功发现服务后可以调用相应方法得到该BLE设备的所有服务，并且打印每一个服务的UUID和每个服务下各个特征的UUID
                List<BluetoothGattService> supportedGattServices = bluetoothGatt.getServices();
                for(int i = 0; i < supportedGattServices.size(); i++){
                    Log.i("A", "BluetoothGattService UUID = " + supportedGattServices.get(i).getUuid());

                    List<BluetoothGattCharacteristic> listGattCharacteristic = supportedGattServices.get(i).getCharacteristics();
                    for(int j = 0; j < listGattCharacteristic.size(); j++){
                        int charaProp = listGattCharacteristic.get(i).getProperties();

                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            Log.e("nihao","gattCharacteristic的UUID为:"+listGattCharacteristic.get(i).getUuid());
                            Log.e("nihao","gattCharacteristic的属性为:  可读");
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            Log.e("nihao","gattCharacteristic的UUID为:"+listGattCharacteristic.get(i).getUuid());
                            Log.e("nihao","gattCharacteristic的属性为:  可写");
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.e("nihao","gattCharacteristic的UUID为:"+listGattCharacteristic.get(i).getUuid()+listGattCharacteristic.get(i));
                            Log.e("nihao","gattCharacteristic的属性为:  具备通知属性");
                        }

                        Log.i("A", "BluetoothGattCharacteristic UUID = " + listGattCharacteristic.get(j).getUuid());
                    }
                }
            } else {
                Log.e("E", "服务发现失败，错误代码为：" + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){// 写操作
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.i("A", "写入成功" + characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){// 读操作
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.i("A", "读取成功" + characteristic.getValue());
                characteristic.getStringValue(0);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){// 数据返回的回调（此处接受BLE设备返回的数据）
            Log.i("recived data", Arrays.toString(characteristic.getValue()));
            Log.i("recived data", String.valueOf(characteristic.getValue()));
        }
    };

    BluetoothGattService bluetoothGattService;
    BluetoothGattCharacteristic bluetoothGattCharacteristic;
    BluetoothLeScanner scanner;

    ArrayList<BluetoothDevice> scanResult;

    public BlueToothService(){}

    @Override
    public void onCreate(){
        scanResult = new ArrayList<>();
    }

    public void enableBluetooth(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if( !bluetoothAdapter.isEnabled() ){
                bluetoothAdapter.enable();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanDevice(){
        isScanning = true;

        scanner = bluetoothAdapter.getBluetoothLeScanner();

        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                if(!scanResult.contains(result.getDevice())) {
                    scanResult.add(result.getDevice()); //add the device that got by the scanner
                    if(handler != null) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(scanCallback);

                isScanning = false;
                Toast.makeText(context,"Scan complete",Toast.LENGTH_SHORT).show();
            }
        }, SCAN_PERIOD); //stop scanning SCAN_PERIOD seconds later

        scanner.startScan(scanCallback);
    }

    public boolean connectDevice(BluetoothDevice remoteDevice){
        try{
            Log.i("connectting Ble's name", remoteDevice.getName());
            Log.i("connectting Ble's add", remoteDevice.getAddress());

            bluetoothGatt = remoteDevice.connectGatt(context, false, gattCallback);

            bluetoothGattService = bluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));

            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }
        catch (NullPointerException e){
            Log.e("connectError:", "the connecting device is null");

            for(BluetoothGattService service : bluetoothGatt.getServices()){
                if(service.getUuid().equals(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"))){
                    bluetoothGattService = service;

                    for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                        if(characteristic.getUuid().equals(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"))){
                            bluetoothGattCharacteristic = characteristic;
                        }
                    }
                }
            }

            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }

        for(BluetoothGattDescriptor dp:bluetoothGattCharacteristic.getDescriptors()){
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(dp);
        }

        if(bluetoothGatt != null && bluetoothGattService != null && bluetoothGattCharacteristic!= null){
            return true;
        } else {
            return false;
        }
    }

    public void sendMessage(char[] message){

        bluetoothGattCharacteristic.setValue(String.valueOf(message));

        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        getMessage();

        bluetoothGattCharacteristic.getProperties();
    }

    public void sendMessage(){
        char[] m = {'$', 'M', '<', 0x00, 'd', 'd'};

        bluetoothGattCharacteristic.setValue(String.valueOf(m));

        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        getMessage();

        bluetoothGattCharacteristic.getProperties();

    }

    public void getMessage(){
        bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);// the read result is showing in the gattCallback
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    public class MyBinder extends Binder {
        public BlueToothService getService() {
            return BlueToothService.this;
        }
        public ArrayList<BluetoothDevice> getDevices(){
            return scanResult;
        }

        public void InitBluetooth(Context newContext, BluetoothManager newManager){
            context = newContext;
            bluetoothManager = newManager;

            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        public void setHandler(Handler newHandler){
            handler = newHandler;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void ScanDevice(){
            scanDevice();
        }
        public boolean ConnectDevice(BluetoothDevice device){
            return connectDevice(device);
        }
        public void ConnectDevice(){
            try{
                connectDevice(savedDevice);
            } catch (NullPointerException e){
                Log.e("error","no savedDevice");

                savedDevice = null;
            }

        }
        public void SendMessage(char[] message){
            sendMessage(message);
        }
        public void SendMessage(){
            sendMessage();
        }
        public boolean IsBLEeabled(){
            return bluetoothAdapter.isEnabled();
        }
        public void EnableBluetooth(){
            enableBluetooth();
        }
        public void SetDevice(BluetoothDevice device){
            savedDevice = device;
        }

    }
    @Override
    public void onDestroy() {

        // 当调用者退出(即使没有调用unbindService)或者主动停止服务时会调用
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // 当调用者退出(即使没有调用unbindService)或者主动停止服务时会调用
        System.out.println("调用者退出了");
        return super.onUnbind(intent);
    }
}

