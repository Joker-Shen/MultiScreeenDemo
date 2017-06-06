package com.ontime.multiscreeendemo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AutoLayoutActivity {
    //鞋子平移动画
    private ScaleAnimation scaleAnimation;


    private TextView tvPlace;

    //鞋子
    private ImageView ivShoe;

    private ImageView ivPhone;

    //手机渐现动画
    private AlphaAnimation alphaAnimation;

    //弹窗
    private AlertDialog dialog;

    //弹窗需要渲染的view
    private View inflateView;

    //弹窗显式的EditText
    private EditText etDialogView;

    //视频布局
    private RelativeLayout videoLayout;

    //静态鞋子布局
    private RelativeLayout showLayout;


    private VideoView videoView;

    //鞋子动画集
    private AnimationSet animationSet;
    private ImageView ivQrCode;
    private String deviceID;
    private String result ;
    private String inputLine;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String str = msg.obj.toString()+"";
                    Log.i("result+++++++",str);
                    try {
                        JSONObject jObj = new JSONObject(str);
                        String result = jObj.optString("data");
                        if(result.equals("true")){
                            startAnimationSet();
                        }
                        //Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        tvPlace = (TextView) findViewById(R.id.tv_sentence);
        ivQrCode = (ImageView) findViewById(R.id.iv_qrcode);
        ivShoe = (ImageView) findViewById(R.id.iv_shoes);
        //ivPhone = (ImageView) findViewById(R.id.ivPhone);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
        showLayout = (RelativeLayout) findViewById(R.id.shoe_layout);


        showLayout.setVisibility(View.INVISIBLE);

        //获得窗口管理器进而获得屏幕宽度和高度
        WindowManager manager = this.getWindowManager();
        int width = manager.getDefaultDisplay().getWidth();
        int height = manager.getDefaultDisplay().getHeight();
        Log.i("Width",width+"");
        Log.i("Height",height+"");
        //视频资源uri
        Uri uri = Uri.parse( "android.resource://com.ontime.shoedemo/"+R.raw.nike );

        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoLayout.setVisibility(View.INVISIBLE);
                showLayout.setVisibility(View.VISIBLE);
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
        //每600ms轮询一下服务器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String target = "http://139.224.104.4:8082/ISHWT/Second?param="+deviceID+"&type=1";
                        Log.i("target",target);
                        try {
                            URL url = new URL(target);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            InputStreamReader in = new InputStreamReader(conn.getInputStream());
                            BufferedReader buffer = new BufferedReader(in);
                            while((inputLine = buffer.readLine())!= null){
                                //Log.i("inputLIne",inputLine);
                                result = inputLine;
                            }
                            in.close();
                            conn.disconnect();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = result;
                        handler.sendMessage(message);
                    }
                }).start();

            }
        },600,600);
    }

    public void click(View view){
        //先将手机图片设为可见的
        //ivPhone.setVisibility(View.VISIBLE);
        startAnimationSet();
    }


    //透明按钮点击弹窗
    public void showDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        inflateView = LayoutInflater.from(this).inflate(R.layout.dialog_view,null);
        etDialogView = (EditText) inflateView.findViewById(R.id.etDeviceID);
        builder.setTitle("请输入设备ID:")
                .setView(inflateView)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deviceID = etDialogView.getText().toString();
                        //Log.i("deviceID",deviceID);
                        if(deviceID.equals("")){
                            Toast.makeText(MainActivity.this, "请输入deviceID!", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                Bitmap bitmap = createQRCode("http://139.224.104.4:8082/ISHWT/First?param="+deviceID+"&type=0");
                                ivQrCode.setImageBitmap(bitmap);
                                ivQrCode.setScaleType(ImageView.ScaleType.FIT_XY);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }


    //生成二维码
    public static Bitmap createQRCode(String str) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 500, 500);
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
        animationSet = new AnimationSet(false);

        //鞋子缩放动画设置
        scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f);
        //缩放动画持续时长
        scaleAnimation.setDuration(3000);
        //将缩放动画添加至动画集中
        animationSet.addAnimation(scaleAnimation);

        //加载平移动画
        Animation translateAnimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate);
        //TranslateAnimation translateAnimation = new TranslateAnimation(0,-100,0,150);
        //设置平移动画时长
        translateAnimation.setDuration(3000);
        //将平移动画加入至动画集中
        animationSet.addAnimation(translateAnimation);
        ivShoe.startAnimation(animationSet);
        //为手机设置透明度渐现动画
        alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(2000);
        //ivPhone.startAnimation(alphaAnimation);

        //animationSet.setFillAfter(true);
        //animationSet.setFillBefore(true);

        //监听动画集的播放
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //在动画播放完毕后手机图案设置为不可见
                // ivPhone.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
