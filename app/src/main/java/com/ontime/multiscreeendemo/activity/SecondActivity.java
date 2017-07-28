package com.ontime.multiscreeendemo.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ontime.multiscreeendemo.R;
import com.ontime.multiscreeendemo.broadcastReciever.MyBroadcastReciever;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ontime.multiscreeendemo.R.id.tv_count_time;

public class SecondActivity extends AutoLayoutActivity {

    private MyCount myCount;
    //鞋子缩放动画
    private ScaleAnimation scaleAnimation;
    //鞋子
    private ImageView ivShoe;
    //鞋子动画集
    private AnimationSet animationSet;

    private ImageView ivQrCode;

    private String result ;
    private String inputLine;

    private TextView tvCountTime;
    private Thread thread;


    private Bitmap bitmap;

    private Runnable mRunnable;

    private Timer timer;

    private Timer timer1;

    private Boolean flag = false;

    private MyBroadcastReciever myBroadcastReciever;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String str = msg.obj.toString()+"";
                    //Log.i("SecondActivity",str);
                    try {
                        JSONObject jObj = new JSONObject(str);
                        String result = jObj.optString("data");
                        if(result.equals("true")){
                           // Log.i("03003c31++++++",str);
                            startAnimationSet();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SecondActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    flag = (Boolean) msg.obj;
                    if(flag == true){
                        tvCountTime.setVisibility(View.VISIBLE);
                    }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.activity_second);


        myCount = new MyCount(20*1000,1000);
        tvCountTime = (TextView) findViewById(R.id.tv_count_time);
        ivQrCode = (ImageView) findViewById(R.id.iv_qrcode);
        ivShoe = (ImageView) findViewById(R.id.iv_shoes);
        tvCountTime.setVisibility(View.GONE);
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                myCount.start();
                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = true;
                handler.sendMessage(message);
            }
        },5000);


        try {
            bitmap = createQRCode("http://139.224.104.4:8082/ISHWT/First?param="+"03003c31"+"&type=0");
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ivQrCode.setImageBitmap(bitmap);
        ivQrCode.setScaleType(ImageView.ScaleType.FIT_XY);

        ivQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationSet();
            }
        });
        timer = new Timer();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                            String target = "http://139.224.104.4:8082/ISHWT/Second?param=" + "03003c31" + "&type=1";
//                            OkHttpClient client;
//                            InputStreamReader in = null;
//                            BufferedReader buffer = null;
//                            StringBuilder builder = null;
//                            try {
//                                URL url = new URL(target);
//                                client = new OkHttpClient();
//                                Request request = new Request.Builder().url(url).build();
//                                Response response = client.newCall(request).execute();
//                                if (response.isSuccessful()) {
//                                    in = new InputStreamReader(response.body().byteStream());
//                                }else{
//                                    Toast.makeText(SecondActivity.this, "网络出现问题", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                buffer = new BufferedReader(in);
//                                builder = new StringBuilder();
//                                while ((inputLine = buffer.readLine()) != null) {
//                                    builder.append(inputLine);
//                                }
//                                result = builder.toString();
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } finally {
//                                if (in != null) {
//                                    try {
//                                        in.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (buffer != null) {
//                                    try {
//                                        buffer.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                            Message message = handler.obtainMessage();
//                            message.what = 1;
//                            message.obj = result;
//                            handler.sendMessage(message);
                        RequestParams requestParams = new RequestParams(target);
                        x.http().get(requestParams, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.i("SecondActivity",result);
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                message.obj = result;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                Toast.makeText(SecondActivity.this, "无网络连接！", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });

                    }
                }, 600, 600);
            }
        };
        thread = new Thread(mRunnable);
        thread.start();

        myBroadcastReciever = new MyBroadcastReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myBroadcastReciever,intentFilter);
    }




    //生成二
    public static Bitmap createQRCode(String str) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 600, 600);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }else{
                    pixels[y * width + x] = 0xffffffff; //-1 相当于0xffffffff 白色
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void startAnimationSet(){
        //初始化动画集
        animationSet = new AnimationSet(true);
        //鞋子缩放动画设置
        scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f);
        //缩放动画持续时长
        scaleAnimation.setDuration(1500);
        //将缩放动画添加至动画集中
        animationSet.addAnimation(scaleAnimation);
        //加载平移动画
        Animation translateAnimation = AnimationUtils.loadAnimation(SecondActivity.this,R.anim.translate);

        translateAnimation.setDuration(1500);
        //将平移动画加入至动画集中
        animationSet.addAnimation(translateAnimation);

        ivShoe.startAnimation(animationSet);
    }




    class MyCount extends CountDownTimer{
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCountTime.setText(millisUntilFinished/1000+"");

        }

        @Override
        public void onFinish() {
            handler.removeCallbacks(mRunnable);
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
            SecondActivity.this.finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("SecondActivity","OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("SecondActivity","onStop");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("SecondActivity","onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("SecondActivity","onRestart");
    }

    @Override
    protected void onDestroy() {
        if(thread!=null){
            thread = null;
        }
        Log.i("SecondActivity","onDestroy");
        myCount.cancel();
        if(bitmap !=null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        timer.cancel();

        if(timer!=null){
            timer = null;
        }

        if(myCount!=null){
            myCount = null;
        }
        super.onDestroy();

        if(timer1!=null){
            timer1 = null;
        }

        unregisterReceiver(myBroadcastReciever);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("SecondActivity","onStart");
    }



}

