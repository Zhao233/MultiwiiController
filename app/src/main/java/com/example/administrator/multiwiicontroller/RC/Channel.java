package com.example.administrator.multiwiicontroller.RC;

/**
 * Created by Administrator on 2017/8/4.
 */

public class Channel {
    private int value;
    private static  final int maxValue = 2000;
    private static final int minValue = 1000;
    private int midValue;

    public int getValue() {
        return value;
    }
    public byte[] getByteValue(){
        byte[] temp = new byte[2];
        temp[1] = (byte) value;//temp[1] : 4 bits in the low order
        temp[0] = (byte) (value >> 8);//temp[0] 4 bits in the high order

        return temp;
    }

    public char getCharValue(){
        return (char)value;
    }

    public void setValue(int value) {
        if(value >= 1000 && value <= 2000) {
            this.value = value;
        } else {
            if(value < 1000){
                this.value = 1000;
            } else {
                this.value = 2000;
            }
        }
    }
    public int getMaxValue() {
        return maxValue;
    }
    public int getMinValue() {
        return minValue;
    }
    public int getMidValue() {
        return midValue;
    }
    public void setMidValue(int midValue) {
        this.midValue = midValue;
    }
}
