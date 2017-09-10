package com.example.administrator.multiwiicontroller.Protocol;

/**
 * Created by Administrator on 2017/8/4.
 */

/*<preamble>,<direction>,<size>,<command>,<data>...,<crc>*/

public class Protocol{
    public int version;
    public String multiType;

    public static final String HEADER_TOMULTIWII = "$M<";
    public static final String HEADER_FROMMULTIWII = "$M>";

    /**/
    public static final int version_2_3 = 230;
    public static final int version_2_4 = 240;

    public static final char
            /*command*/          /*message id*/
            MSP_IDENT                =0x64,
            MSP_STATUS               =0x65,
            MSP_RAW_IMU              =0x66,
            MSP_SERVO                =0x67,
            MSP_MOTOR                =0x68,
            MSP_RC                   =0x69,
            MSP_RAW_GPS              =0x6A;
            /*MSP_COMP_GPS             =,
            MSP_ATTITUDE             =108,
            MSP_ALTITUDE             =109,
            MSP_ANALOG               =110,
            MSP_RC_TUNING            =111,
            MSP_PID                  =112,
            MSP_BOX                  =113,
            MSP_MISC                 =114,
            MSP_MOTOR_PINS           =115,
            MSP_BOXNAMES             =116,
            MSP_PIDNAMES             =117,
            MSP_SERVO_CONF           =120,

            MSP_SET_RAW_RC           =200,
            MSP_SET_RAW_GPS          =201,
            MSP_SET_PID              =202,
            MSP_SET_BOX              =203,
            MSP_SET_RC_TUNING        =204,
            MSP_ACC_CALIBRATION      =205,
            MSP_MAG_CALIBRATION      =206,
            MSP_SET_MISC             =207,
            MSP_RESET_CONF           =208,
            MSP_SELECT_SETTING       =210,
            MSP_SET_HEAD             =211, // Not used
            MSP_SET_SERVO_CONF       =212,
            MSP_SET_MOTOR            =214,

            MSP_BIND                 =241,

            MSP_EEPROM_WRITE         =250,

            MSP_DEBUGMSG             =253,
            MSP_DEBUG                =254;*/

    private int magx;
    private int magy;
    private int magz;
}
