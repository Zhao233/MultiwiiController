package com.example.administrator.multiwiicontroller.Protocol;

/**
 * Created by Administrator on 2017/8/4.
 */

public interface IProtocol {
    boolean checkSum(String message);
    boolean checkCRC(String message);

    String checkSum();
    String checkCRC();

}
