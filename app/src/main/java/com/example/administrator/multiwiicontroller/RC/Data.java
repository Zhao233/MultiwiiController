package com.example.administrator.multiwiicontroller.RC;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/8/12.
 */


/* RC command data roll/pitch/Yaw/throttle/aux1/aux2/aux3/aux4 */
public class Data {
    private ArrayList<Character> messageList = new ArrayList<>();

    private char[] data;

    public char[] setData(Channel channel_throt, Channel channel_Yaw, Channel channel_Roll, Channel channel_Pitch){
        return setData(channel_throt,channel_Yaw,channel_Roll,channel_Pitch);
    }
    private char[] setData(Channel... channels){
        for(Channel channel : channels){
            messageList.add(channel.getCharValue());
        }

        data = listToArray(messageList);

        return data;
    }

    public char[] getData(){
        return data;
    }

    public char[] listToArray(List<Character> list){
        char[] temp = new char[list.size()];

        for(int i = 0; i < list.size(); i++){
            temp[i] = list.get(i);
        }

        return temp;
    }

    public void clearMessage(){
        messageList.clear();

        data = listToArray(messageList);
    }
}
