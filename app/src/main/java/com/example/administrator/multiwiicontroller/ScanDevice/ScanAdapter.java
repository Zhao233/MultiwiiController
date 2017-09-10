package com.example.administrator.multiwiicontroller.ScanDevice;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.multiwiicontroller.R;

import java.util.ArrayList;
import android.view.LayoutInflater;

/**
 * Created by Administrator on 2017/7/25.
 */

public class ScanAdapter extends BaseAdapter{
    Context context;
    public ArrayList<BluetoothDevice> devices;

    LayoutInflater inflater;

    public ScanAdapter(Context newContext){
        context = newContext;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_device_scan,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(devices.get(position).getName());
        viewHolder.address.setText(devices.get(position).getAddress());

        return convertView;
    }
}

class ViewHolder{
    public TextView name;
    public TextView address;
}
