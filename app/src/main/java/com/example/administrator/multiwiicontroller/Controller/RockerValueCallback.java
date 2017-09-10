package com.example.administrator.multiwiicontroller.Controller;

import com.example.administrator.multiwiicontroller.RC.Channel;

/**
 * Created by Administrator on 2017/9/8.
 */
public interface RockerValueCallback {
    void outputValue(Channel verticalValue, Channel horizontalValue);
}
