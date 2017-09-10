package com.example.administrator.multiwiicontroller.Controller;

import com.example.administrator.multiwiicontroller.RC.Channel;
import com.example.administrator.multiwiicontroller.RC.Data;

/**
 * Created by Administrator on 2017/8/29.
 */

public interface ValueCallBack{
    Data makeData(Channel channel_throt, Channel channel_Yaw, Channel channel_Roll, Channel channel_Pitch);
}
