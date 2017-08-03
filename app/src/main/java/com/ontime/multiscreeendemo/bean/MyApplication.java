package com.ontime.multiscreeendemo.bean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;

import org.xutils.x;

/**
 * Created by shgl1hz1 on 2017/7/19.
 */

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{
    private static  String FirstActivity_DeviceId = "03003c75";
    private static  String SecondAcitivty_DeviceId = "03003c30";
    //    private static  String TestId = "03003c4d";

    public String getFirstActivity_DeviceId() {
        return FirstActivity_DeviceId;
    }

    public void setFirstActivity_DeviceId(String firstActivity_DeviceId) {
        FirstActivity_DeviceId = firstActivity_DeviceId;
    }

    public String getSecondAcitivty_DeviceId() {
        return SecondAcitivty_DeviceId;
    }

    public void setSecondAcitivty_DeviceId(String secondAcitivty_DeviceId) {
        SecondAcitivty_DeviceId = secondAcitivty_DeviceId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Intent intent = new Intent(this,getTopActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public Class getTopActivity(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String className = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        Class cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  cls;
    }
}
