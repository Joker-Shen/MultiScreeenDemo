package com.ontime.multiscreeendemo.broadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by shgl1hz1 on 2017/7/26.
 */

public class MyBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean success = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state == NetworkInfo.State.CONNECTED){
            //Toast.makeText(context, "Wifi已连接", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "无网络连接", Toast.LENGTH_SHORT).show();
        }
    }
}
