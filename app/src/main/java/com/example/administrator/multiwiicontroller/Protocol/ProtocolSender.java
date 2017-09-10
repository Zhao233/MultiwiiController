package com.example.administrator.multiwiicontroller.Protocol;

import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.example.administrator.multiwiicontroller.BlutoothService.BlueToothService;
import com.example.administrator.multiwiicontroller.RC.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/5.
 */

public class ProtocolSender{
    /**a message is include these things
     * <preamble>,<direction>,<size>,<command>,<data>...,<crc>
     * preamble is a header of the message,
     * direction is mean that, this message is come from FC board(<) or send to the FC board(>)
     *size: the length of the data
     * data: if this message is a request, then data is 00, if it's a command, the content is decided by your command */
    private BlueToothService.MyBinder blueToothBinder;

    private ArrayList<Character> message_char = new ArrayList<>();
    private char[] message;
    private char command;

    public static final int
            /*request*/
            GETIDENT = 1,
            GETSTATUS = 2,
            GETIMUDATA = 3;

    public ProtocolSender(BlueToothService.MyBinder binder){
        /*add the header into the message*/
        message_char.add((char)0x24);
        message_char.add((char)0x4d);
        message_char.add((char)0x3c);

        blueToothBinder = binder;
    }
    public void setBlueToothBinder(BlueToothService.MyBinder binder){
        this.blueToothBinder = binder;
    }

    public void setCommand(char newCommand){
        command = newCommand;
    }

    public Character getDirection(){
        return message_char.get(2);
    }
    public Character getSize(){
        try{
            return message_char.get(3);
        } catch (IndexOutOfBoundsException e){
            Log.e("getCommand:",e.getLocalizedMessage());
            return null;
        }
    }
    public Character getCommand(){
        try{
            return message_char.get(4);
        } catch (IndexOutOfBoundsException e){
            Log.e("getCommand:",e.getLocalizedMessage());
            return null;
        }
    }
    public char[] getData(){
        try{
            char[] temp = new char[message_char.size()-1-5];

            for(int i = 5; i < message_char.size() - 1; i++){
                temp[i-5] = message_char.get(i);
            }

            return temp;
        } catch (IndexOutOfBoundsException e){
            return null;
        } catch (NegativeArraySizeException e){
            return null;
        }
    }
    public Character getCRC(){
        try{
            return message_char.get(message_char.size() - 1);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }


    public char[] StringToByteArray(String message){
        char[] byteMessage = new char[message.length()];

        for(int i = 0; i < message.length(); i++){
            byteMessage[i] = message.charAt(i);
        }

        return byteMessage;
    }

    public char[] listToArray(List<Character> list){
        char[] temp = new char[list.size()];

        for(int i = 0; i < list.size(); i++){
            temp[i] = list.get(i);
        }

        return temp;
    }

    public void makeControlMessage(Data data){
        addSizeToMessage(data.getData());
        addCommandToMessage();
        addDataArrayToMessage(data.getData());
        addCRCtoMessage(command,data);

        message = listToArray(message_char);
        clear();
    }
    public void addSizeToMessage(char[] newData){
        message_char.add((char)newData.length);
    }
    public void addCommandToMessage(){
        message_char.add(command);
    }
    public void addDataArrayToMessage(char[] newData){
        for(char charData : newData){
            message_char.add(charData);
        }
    }
    public void addCRCtoMessage(char command, Data newData){
        message_char.add(getChecksum((char)newData.getData().length, command, newData.getData()));
    }

    private char getChecksum(char length, char cmd, char[] mydata){
        char checksum=0;
        checksum ^= length;
        checksum ^= cmd;
        for(int i=0;i<length;i++)
        {
            checksum ^= mydata[i];
        }
        return checksum;
    }

    public void sendMessage(){
        blueToothBinder.SendMessage(message);

        message = null;
    }

    public void clear(){
        for(int i = 2; i < message_char.size(); i++){
            message_char.remove(i);
        }
    }
}