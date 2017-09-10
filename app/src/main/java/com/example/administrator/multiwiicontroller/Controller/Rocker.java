package com.example.administrator.multiwiicontroller.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.administrator.multiwiicontroller.RC.Channel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/11.
 */

public class Rocker extends Button {
    Context context;

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    ImageView dragView;

    private Bitmap imageBitmap;
    private int toCenter_x,toCenter_y;

    private int statusHeight;

    private int moveX, moveY;

    private int borderValue_x,borderValue_y;//x: the beginning of the view, y: the top of the view
    private int borderWidth, borderHeight;

    private int isThrot; // 1 : this is a throt button, else is a general button

    private int centerValue_x, minValue_x, maxnValue_x;

    private int centerValue_y, minValue_y, maxValue_y;

    private int centerX_move, centerY_move;

    private int horizontalValue;
    private int verticalValue;

    private double ChInterval_Vertical;
    private double ChInterval_Horizontal;

    private int total_Vertical;
    private int total_Horizontal;

    private int adjustValue_Vertical;
    private int adjustValue_Horizontal;

    int x,y; // the location while finger press down

    private Channel channel_horizontal;
    private Channel channel_vertical;

    Handler handler;
    Bundle bundle;
    Message message;

    TimerTask timerTask;
    Timer timer;

    public Rocker(Context context) {
        super(context, null);

        init(context);
    }
    public Rocker(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }
    public Rocker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }
    public Rocker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    public void init(Context context){
        this.context = context;

        statusHeight = getStatusHeight(context);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        channel_horizontal = new Channel();
        channel_vertical = new Channel();

        bundle = new Bundle();
        message = new Message();
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public int isThrot() {
        return isThrot;
    }
    public void setThrot(int throt) {
        isThrot = throt;
    }
    public float getCenterValue_x() {
        return centerValue_x;
    }
    public void setCenterValue_x(int centerValue_x) {
        this.centerValue_x = centerValue_x;
    }
    public float getCenterValue_y() {
        return centerValue_y;
    }
    public void setCenterValue_y(int centerValue_y) {
        this.centerValue_y = centerValue_y;
    }
    public void setHorizontalValue(int horizontalValue) {
        this.horizontalValue = horizontalValue;
    }
    public void setVerticalValue(int verticalValue) {
        this.verticalValue = verticalValue;
    }

    public int setHorizontalValue() {
        return 1500 + (int)( ((layoutParams.x + getWidth()/2) - centerValue_x) * ChInterval_Horizontal );
        //channel_horizontal.setValue(horizontalValue);
    }
    public int setVerticalValue() {
        return 1500 + (int)( (centerValue_y - (layoutParams.y + getHeight()/2)) * ChInterval_Vertical );
        //channel_vertical.setValue(verticalValue);
    }

    public Channel getChannel_vertical(){
        return channel_vertical;
    }
    public Channel getChannel_horizontal(){
        return channel_horizontal;
    }

    public int getHorizontalValue(){
        return horizontalValue;
    }
    public int getVerticalValue(){
        return verticalValue;
    }

    public int getDistance_x(int position_x){
        int distance;

        distance = position_x - centerValue_x;

        if(distance > 0){
            return distance;
        } else {
            return -distance;
        }
    }
    public int getDistance_y(int mode, int position_y){ //if the button is throt button, the beginning is on the buttom
        float distance;

        if(mode == 0) { // 0 is a general button
            distance = position_y - centerValue_y;
        } else { // else is a throt button
            distance = position_y - minValue_y;
        }

        if(distance > 0){
            return (int)distance;
        } else {
            return (int)-distance;
        }
    }
    public int getDistance(int position_x, int position_y){
        return (int)Math.sqrt( Math.pow(getDistance_x(position_x),2) + Math.pow(getDistance_y(0,position_y), 2));
    }

    public void setCenter(){
        int[] loactions = new int[2];
        super.getLocationOnScreen(loactions);

        centerValue_x = loactions[0] + getWidth()/2;
        centerValue_y = loactions[1] + getHeight()/2 - statusHeight;
    }

    public void setBorder(int border_x,int width, int border_y, int height){//left, width, top, height
        borderValue_x = border_x;
        borderValue_y = border_y;
        borderWidth = width;
        borderHeight = height;

        total_Vertical = Math.abs(centerValue_y - border_y + statusHeight);
        total_Horizontal = Math.abs(border_x - centerValue_x);

        ChInterval_Horizontal = 500.00/total_Horizontal;
        ChInterval_Vertical = 500.00/total_Vertical;
    }

    public void setAdjustValue_Vertical(int adjust){
        if(adjust > 0){
            if(adjust + verticalValue > 2000){
                adjustValue_Vertical = 2000 - verticalValue;
            }
        } else {
            if(adjust + verticalValue < 1000){
                adjustValue_Vertical = 1000 -verticalValue;
            }
        }
    }
    public void setAdjustValue_Horizontal(){}

    public void setWindwoManager(WindowManager manager){
        this.windowManager = manager;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                setCenter();

                x = (int)event.getRawX();
                y = (int)event.getRawY() - statusHeight;

                toCenter_x = x - centerValue_x;
                toCenter_y = y - centerValue_y;

                if(dragView == null && layoutParams == null) { //first touch this button
                    layoutParams = new WindowManager.LayoutParams();

                    this.setDrawingCacheEnabled(true); //开启绘图缓冲
                    imageBitmap = Bitmap.createBitmap(getDrawingCache()); //获取缓存，绘制bitmap
                    this.destroyDrawingCache();//释放缓存，避免重复镜像

                    createImage(imageBitmap);
                    windowManager.addView(dragView, layoutParams);
                } else {
                    windowManager.addView(dragView, layoutParams);
                }

                this.setVisibility(INVISIBLE);

                break;

            case MotionEvent.ACTION_MOVE :
                moveX = (int)event.getRawX();
                moveY = (int)event.getRawY();

                onDrag(moveX, moveY);
                break;

            case MotionEvent.ACTION_UP :
                stopDrag();
                break;
        }
        return true;
    }

    public void createImage(Bitmap bitmap){
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = centerValue_x  - getWidth()/2;
        layoutParams.y = centerValue_y - getHeight()/2;
        layoutParams.alpha = 0.55f;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;

        dragView = new ImageView(context);

        dragView.setImageBitmap(bitmap);
    }

    public void onDrag(int moveX, int moveY){
        layoutParams.x = moveX - toCenter_x - getWidth()/2;
        layoutParams.y = moveY - toCenter_y - getHeight()/2 - statusHeight;

        centerX_move = moveX - toCenter_x ;
        centerY_move = moveY - toCenter_y - statusHeight;

        if(getDistance(centerX_move, centerY_move) > borderWidth/2){ //while button move over the border
            int dx = (int)(( (double)getDistance_x(centerX_move)/getDistance(centerX_move,centerY_move) ) * (borderWidth/2) );
            int dy = (int)(( (double)getDistance_y(0,centerY_move)/getDistance(centerX_move,centerY_move) ) * (borderWidth/2) );

            if(centerX_move < centerValue_x) {
                layoutParams.x = centerValue_x - dx - getWidth()/2;
            } else {
                layoutParams.x = centerValue_x + dx - getWidth()/2;
            }

            if(centerY_move < centerValue_y) {
                layoutParams.y = centerValue_y - dy - getWidth()/2;
            } else {
                layoutParams.y = centerValue_y + dy - getWidth()/2;
            }
        }

        Log.i("location+_X_Y: ", String.valueOf(moveX)+", "+String.valueOf(moveY));
        Log.i("buttonCenter_X_Y: ", String.valueOf(centerX_move)+", "+ String.valueOf(centerY_move));
        Log.i("disTocentre", String.valueOf(getDistance(centerX_move, centerY_move)));
        Log.i("layoutParams_X_Y: ", String.valueOf(layoutParams.x)+", "+ String.valueOf(layoutParams.y));

        Log.i("Chvalue_X_Y: ", String.valueOf(getHorizontalValue())+", "+ String.valueOf(getVerticalValue()));

        windowManager.updateViewLayout(dragView, layoutParams);
    }

    public void stopDrag(){
        layoutParams.x = centerValue_x - getWidth()/2;
        layoutParams.y = centerValue_y + getHeight()/2;

        windowManager.removeView(dragView);
        this.setVisibility(VISIBLE);
    }

    public void startListen(){
        layoutParams.x = centerValue_x - getWidth()/2;
        layoutParams.y = centerValue_y - getHeight()/2;

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                bundle.putInt("verticalValue", setVerticalValue());
            }
        };

        timer.schedule(timerTask,0,20);
    }

     /*获取状态栏高度*/ /**Done*/
    private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
