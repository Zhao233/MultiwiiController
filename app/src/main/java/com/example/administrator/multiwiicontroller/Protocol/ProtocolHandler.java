package com.example.administrator.multiwiicontroller.Protocol;

/**
 * Created by Administrator on 2017/8/4.
 */

public class ProtocolHandler extends Protocol implements IProtocol{
    public boolean isMultiwii(byte[] message_get){
        super.version = message_get[0];
        int multiType = message_get[1];

        switch (super.version){
            case 220 :break;
            case 230 :break;
            case 240 :break;
            default:super.version = Integer.MIN_VALUE;
        }

        switch (multiType){
            case 0 : super.multiType = "TRL"; break;
            case 1 : super.multiType = "QUAD_P"; break;
            case 3 : super.multiType = "QUAD_X"; break;
            default: super.multiType = "noType";
        }

        if( super.version != Integer.MAX_VALUE && super.multiType != "noType"){
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean checkSum(String message) {
        return false;
    }

    @Override
    public boolean checkCRC(String message) {
        return false;
    }

    @Override
    public String checkSum() {
        return null;
    }

    @Override
    public String checkCRC() {
        return null;
    }
}
