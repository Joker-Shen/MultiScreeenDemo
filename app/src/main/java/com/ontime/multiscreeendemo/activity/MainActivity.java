package com.ontime.multiscreeendemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ontime.multiscreeendemo.R;
import com.ontime.multiscreeendemo.bean.MyApplication;
import com.ontime.multiscreeendemo.broadcastReciever.MyBroadcastReciever;
import com.zhy.autolayout.AutoLayoutActivity;


import java.util.Arrays;

import static com.ontime.multiscreeendemo.R.id.tv_count_time;

public class MainActivity extends AutoLayoutActivity {

    //鞋子平移动画
    private ScaleAnimation scaleAnimation;
    //鞋子
    private ImageView ivShoe;

    private VideoView videoView;
    //鞋子动画集
    private AnimationSet animationSet;
    //二维码
    private ImageView ivQrCode;
    //设备ID
    private String deviceID;

    private TextView tvCountTime;

    private BluetoothAdapter mBluetoothAdapter;

    private static  final int REQUEST_ENABLE_BT  = 1;

    private MyBroadcastReciever myBroadcastReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.activity_main);


        ivQrCode = (ImageView) findViewById(R.id.iv_qrcode);
        ivShoe = (ImageView) findViewById(R.id.iv_shoes);
        videoView = (VideoView) findViewById(R.id.videoView);
        tvCountTime = (TextView) findViewById(tv_count_time);

        //使用此检查确定Ble是否支持在设备上
//        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
//            Toast.makeText(this, "此设备不支持BLE！", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this, "此设备支持BLE~", Toast.LENGTH_SHORT).show();
//        }

        //获取BluetoothAdapter;
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        //确保蓝牙在设备上可开启
        if(mBluetoothAdapter == null|!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }

        //广播过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver,intentFilter);

        bleAllScan();

        //获得窗口管理器进而获得屏幕宽度和高度
        WindowManager manager = this.getWindowManager();
        int width = manager.getDefaultDisplay().getWidth();
        int height = manager.getDefaultDisplay().getHeight();
        Log.i("Width",width+"");
        Log.i("Height",height+"");
        //视频资源uri
        Uri uri = Uri.parse( "android.resource://com.ontime.multiscreeendemo/"+R.raw.nike);

        //设置视频控制器
        //videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                    videoView.start();
            }
        });

        //设置视频路径
        videoView.setVideoURI(uri);

        //去除边框，全屏播放视频
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        videoView.setLayoutParams(layoutParams);
        //开始播放视频
        videoView.start();

        myBroadcastReciever = new MyBroadcastReciever();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myBroadcastReciever,intentFilter1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(myBroadcastReciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String bleStateStr = null;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        bleStateStr = "STATE_OFF 手机蓝牙已关闭";
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        bleStateStr = "STATE_TURNING_OFF 手机蓝牙正在关闭";
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bleStateStr = "STATE_ON 手机蓝牙已开启";
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        bleStateStr = "STATE_TURNING_ON 手机蓝牙正在开启";
                        break;
                }
                Toast.makeText(MainActivity.this, bleStateStr, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void bleAllScan(){

        mBluetoothAdapter.startLeScan(mLeScanCallback);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bleStopScan();
//            }
//        },20*1000);
    }


    public void bleStopScan(){
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }



    //扫描回调的接口
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("scanRecord", Arrays.toString(scanRecord));
            byte[] myRecord = Arrays.copyOfRange(scanRecord,12,16);
            //Log.i("myRecord", Arrays.toString(myRecord));

            String strFirst = new MyApplication().getFirstActivity_DeviceId();
            String strSecond = new MyApplication().getSecondAcitivty_DeviceId();

            byte[] byteArrayFirst = new byte[strFirst.length()/2];
            for(int i=0;i<byteArrayFirst.length;i++){
                String subStr = strFirst.substring(2*i,2*i+2);
                byteArrayFirst[i] = (byte) Integer.parseInt(subStr,16);
            }
            byte[] byteArraySecond = new byte[strFirst.length()/2];
            for(int i=0;i<byteArraySecond.length;i++){
                String subStr = strSecond.substring(2*i,2*i+2);
                byteArraySecond[i] = (byte) Integer.parseInt(subStr,16);
            }
//            byte[] byteArrayTest = new byte[strFirst.length()/2];
//            for(int i=0;i<byteArraySecond.length;i++){
//                String subStr = StrTest.substring(2*i,2*i+2);
//                byteArrayTest[i] = (byte) Integer.parseInt(subStr,16);
//            }

//            if( Arrays.equals(myRecord,byteArray)){
//                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//                startActivity(intent);
//                bleStopScan();
//                MainActivity.this.finish();
//
//            }
//
//            if( Arrays.equals(myRecord,byteArrayTest)){
//                Intent intent = new Intent(MainActivity.this,FirstActivity.class);
//                startActivity(intent);
//                bleStopScan();
//                MainActivity.this.finish();
//            }


            //03003C75  绿色鞋子
            if( Arrays.equals(myRecord,byteArrayFirst)){
                Intent intent = new Intent(MainActivity.this,FirstActivity.class);
                startActivity(intent);
                bleStopScan();
                MainActivity.this.finish();
            }

            //03003c30   蓝色鞋子
            if( Arrays.equals(myRecord,byteArraySecond)){
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                bleStopScan();
                MainActivity.this.finish();
            }
//            Log.i("扫描记录", Arrays.toString(scanRecord));
//            Log.i("Tag","搜索到设备： 设备名 = "+deviceName+"，设备Mac地址 = "+deviceAddr+",信号强度 ="+rssi+",uuid ="+uuids);
        }


    };




    public void startAnimationSet(){
        //初始化动画集
        animationSet = new AnimationSet(false);
        //鞋子缩放动画设置
        scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f);
        //缩放动画持续时长
        scaleAnimation.setDuration(1500);
        //将缩放动画添加至动画集中
        animationSet.addAnimation(scaleAnimation);
        //加载平移动画
        Animation translateAnimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate);
        //TranslateAnimation translateAnimation = new TranslateAnimation(0,-100,0,150);
        //设置平移动画时长
        translateAnimation.setDuration(1500);

        //将平移动画加入至动画集中
        animationSet.addAnimation(translateAnimation);
        ivShoe.startAnimation(animationSet);

        //animationSet.setFillAfter(true);
        //animationSet.setFillBefore(true);
    }
}
