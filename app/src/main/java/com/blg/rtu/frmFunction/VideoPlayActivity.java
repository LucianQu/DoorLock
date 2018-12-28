package com.blg.rtu.frmFunction;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audio.AudioJni;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.util.permission.PermissionHelper;
import com.blg.rtu.util.permission.PermissionInterface;
import com.blg.rtu3.R;
import com.xuanyuanxing.camera.VideoPlayTool;
import com.xuanyuanxing.camera.XuanYuanXingP2PTool;
import com.xuanyuanxing.engine.ClientP2pListener;
import com.xuanyuanxing.engine.GetVideoSharpnessCallBack;
import com.xuanyuanxing.engine.PlayVideoCallBack;
import com.xuanyuanxing.ui.XuanYuanSurfaceView;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 播放视频
 */
public class VideoPlayActivity extends Activity implements PlayVideoCallBack, ClientP2pListener, OnClickListener
,PermissionInterface {
    private final static String TAG = VideoPlayActivity.class.getSimpleName();
    //新修改的硬解码 属性
    private XuanYuanSurfaceView mSurface = null;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private MediaCodec mCodec;
    private final String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private final int TIME_INTERNAL = 30;
    private int video_width = 352;
    private int video_height = 240;
    private boolean mbExit = false;

    //新修改的硬解码

    boolean isExit = false;
    static public VideoPlayActivity instance = null;
    ImageButton video_pre;
    private ImageView screenShotImg;
    private Button settingBtn = null;
    private ImageButton Bimg_list = null;
    private ImageButton Bimg_talk = null;

    private TextView TPlayInfo;
    private boolean isRecordShow = false;
    boolean isRecordVideoFirst = false;//录视频要先写video
    private boolean isTalkShow = false;
    private boolean isListShow = false;

    LinearLayout tool_lin;
    /**
     * 音视频接口
     */
    VideoPlayTool videoTool = null;
    /**
     * ptp 连接接口
     */
    XuanYuanXingP2PTool p2PTool;
    MyHandle handle = new MyHandle(this);

    private String[] permissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PermissionHelper mPermissionHelper;

    public void setVideoWidthAndHeight(int q) {
        if (q == 1) {
            //高1280.000000  720.000000
            video_width = 1280;
            video_height = 720;
        } else if (q == 3) {
            //中
            video_width = 640;
            video_height = 480;
        } else {
            //5低
            video_width = 352;
            video_height = 240;
        }
        if (videoTool != null) {
            videoTool.setWidthAndHeight(video_width, video_height);
        }
        handle.sendEmptyMessage(10042);
    }

    /**
     * PTP连接成功，开始播放视频
     */
    private void requsePlayVideo() {
        // StartDeBug();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (p2PTool != null) {
                    p2PTool.GetIPNCStreamCtrl(new GetVideoSharpnessCallBack() {
                        @Override
                        public void getVideoSharpnessCallBack(int res, int q) {
                            //获取结果 res
                            if (res >= 0) {
                                //q=1 高清 3中 5低
                                setVideoWidthAndHeight(q);
                                initDecoder();
                                videoBufs.clear();
                                isFist = Boolean.TRUE;
                                boolean plsyRes = videoTool.playVieo(p2PTool.GetavIndex(), p2PTool.GetavSID(), "", 0, p2PTool.GetavUID());
                                if (plsyRes == Boolean.FALSE) {
                                    handle.sendEmptyMessage(1001);
                                }
                            }

                        }
                    });
                }


            }
        }).start();

    }

    /**
     * 初始化硬解码
     */
    public void initDecoder() {
        try {
            if (mCodec == null) {
                mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            }

        } catch (Exception e) {
        }
        try {
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                    video_width, video_height);
            mCodec.configure(mediaFormat, mSurface.getHolder().getSurface(),
                    null, 0);
            mCodec.start();
        } catch (Exception ex) {

        }

    }

    /**
     * 关闭硬解码
     */
    public void closeDecoder() {
        Log.e("ttt", "closeDecoder");
        if (mCodec != null) {
            try {
                mCodec.stop();
                mCodec.release();
                mCodec = null;
            } catch (Exception ex) {

            }
        }
    }

    /**
     * PTP 连接状态监听
     *
     * @param state
     */
    @Override
    public void P2pState(int state) {
        if (state == 2) {
            videoTool = new VideoPlayTool(VideoPlayActivity.this, VideoPlayActivity.this);
            // 新修改的硬解码 初始化解码器 //在子线程解码就必须在子线程初始化
            requsePlayVideo();
        } else {
            Log.e("ttt", "当前状态" + state);
            //-6 密码错误 其他 都连接失败
            if (state == -6) {
                // Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                // Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                showAlarmDialog("密码错误");
            } else if (state < 0) {
                // Toast.makeText(this,"连接失败",Toast.LENGTH_SHORT).show();
                p2PTool.Stop(); //断开连接
                showAlarmDialog("连接失败,请确认是否连接上设备的wifi和密码是否正确!");
            }
        }

    }

    public void showAlarmDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //BlgActivity.this.finish();
                    }
                })
                .show();
    }

    public void changeVideoSize() {
//        int videoWidth = video_width;
//        Log.e("ggg","videoWidth = "+videoWidth);
//        int videoHeight = video_height;
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.surfaceview_rl);
//int surfaceWidth=getWindowManager().getDefaultDisplay().getWidth();
//        int surfaceHeight =mSurface.getHeight();
//        Log.e("ggg","surfaceHeight---"+surfaceHeight);
//        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
//        float max;
//        if (getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            //竖屏模式下按视频宽度计算放大倍数值
//            max = Math.max((float) videoWidth / (float) surfaceWidth,(float) videoHeight / (float) surfaceHeight);
//        } else{
//            //横屏模式下按视频高度计算放大倍数值
//            max = Math.max(((float) videoWidth/(float) surfaceHeight),(float) videoHeight/(float) surfaceWidth);
//        }
//        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
//        videoWidth = (int) Math.ceil((float) videoWidth / max);
//        videoHeight = (int) Math.ceil((float) videoHeight / max);
        //Log.e("ggg","videoWidth = "+videoWidth+"--- videoHeight"+videoHeight);
        RelativeLayout.LayoutParams params = null;
        int saveHeight;
        int saveWidth = layout.getWidth();
//        if(video_width==352)
//        {
//          //  --- videoHeight491
//       params = new RelativeLayout.LayoutParams(
//               saveWidth, 491);
//            saveHeight =491;
//        }else if(video_width==1280){
//            params = new RelativeLayout.LayoutParams(
//                    saveWidth, 405);
//            saveHeight =405;
//        }else{
//            params = new RelativeLayout.LayoutParams(
//                    saveWidth, 540);
//            saveHeight =540;
//           // videoWidth = 720--- videoHeight540
//        }
//        params.leftMargin = 0;
//
//        params.topMargin = (layout.getHeight()/2-saveHeight/2);
//        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//        mSurface.setLayoutParams(params);

    }


    private static class MyHandle extends Handler {
        private WeakReference<VideoPlayActivity> mVideoPlayActivity;

        public MyHandle(VideoPlayActivity videoPlayActivity) {
            mVideoPlayActivity = new WeakReference<VideoPlayActivity>(videoPlayActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10042:

                    mVideoPlayActivity.get().changeVideoSize();
                    break;
                case 1066:

                    break;
                case 200:

                    mVideoPlayActivity.get().Bimg_list.setEnabled(true);
                    break;
                case 1001:
                    Toast.makeText(mVideoPlayActivity.get(), "播放视频失败请重试", Toast.LENGTH_SHORT).show();
                    mVideoPlayActivity.get().finish();
                    break;
                case 1002:
                    mVideoPlayActivity.get().finish();
                    break;
                case 1003:
                    Toast.makeText(mVideoPlayActivity.get(), "AudioListStart avSendIOCtrl fail!" + msg.arg1, Toast.LENGTH_LONG).show();

                    break;
                case 1004:

                    break;
                case 100:
                    int ret = msg.arg1;
                    Toast toast = Toast.makeText(mVideoPlayActivity.get(), "AudioTalkStart avSendIOCtrl fail!" + ret, Toast.LENGTH_LONG);
                    toast.show();
                    break;
                case 19:
                    String str = (String) msg.obj;
                    if (!TextUtils.isEmpty(str)) {
                        mVideoPlayActivity.get().TPlayInfo.setText(str);
                    }
                    break;
            }
        }
    }

    public void returnAction(View v) {
        isExit = true;
        StopAll();
    }

    /**
     * 开启语音
     */
    private void startList() {
        if (videoTool == null) {
            return;
        }
        isListShow = true;
        Bimg_list.setEnabled(false);
        handle.sendEmptyMessageDelayed(200, 500);
        Bimg_list.setImageDrawable(getResources().getDrawable(R.drawable.img_list_bu));
        int ret = videoTool.AudioListStart();
        if (ret != 0) {
            Toast toast = Toast.makeText(VideoPlayActivity.this, "AudioListStart avSendIOCtrl fail!" + ret, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * 关闭监听
     */
    private void closeList() {
        if (videoTool == null) {
            return;
        }
        isListShow = false;
        Bimg_list.setEnabled(false);
        handle.sendEmptyMessageDelayed(200, 500);
        Bimg_list.setImageDrawable(getResources().getDrawable(R.drawable.img_list));
        if (videoTool != null) {
            videoTool.AudioListStop();
        }

    }

    /**
     * 开启监听
     */
    private void startTalk() {
        if (videoTool == null) {
            return;
        }
        isTalkShow = true;
        Bimg_talk.setImageDrawable(getResources().getDrawable(R.drawable.img_talk_bu));
        videoTool.AudioTalkStart(new VideoPlayTool.AudioTalkStartActionCallBack() {
            @Override
            public void startRet(int ret) {
                if (ret != 0) {
                    Message msg = Message.obtain();
                    msg.what = 100;
                    msg.arg1 = ret;
                    handle.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 开启权限
     */
    private void checkPermission() {


/*
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            //  Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            //Log.e("ttt", "checkPermission: 已经授权！");
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public int getPermissionsRequestCode() {
        return 200;
    }

    @Override
    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public void requestPermissionsSuccess() {
        /*if (shouldShowRequestPermissionRationale( Manifest.permission
                .WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "请相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void requestPermissionsFail() {
        ToastUtils.show(VideoPlayActivity.this,"权限申请失败···");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mPermissionHelper = new PermissionHelper(this, this);
                mPermissionHelper.requestPermissions();
            }
        }
    }


    private void stopTalk() {
        isTalkShow = false;
        Bimg_talk.setImageDrawable(getResources().getDrawable(R.drawable.img_talk));
        videoTool.AudioTalkStop();
    }


    boolean isFist = true;

    public boolean onFrame(byte[] buf, int offset, int length) {

        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        int inputBufferIndex = mCodec.dequeueInputBuffer(100);
        //Log.e("Media", "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            int mCount = 0;
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                    * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }
        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 100);
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        //Log.e("Media", "onFrame end");
        return true;
    }

    public class VideoThread extends Thread {
        //截图
        //boolean isJieTu =true;
        @Override
        public void run() {
            super.run();

            while (isExit == Boolean.FALSE) {

                byte[] buf = null;
                if (videoBufs.size() > 0) {
                    synchronized (this) {
                        if (videoBufs.size() > 0) {
                            buf = videoBufs.get(0);
                            videoBufs.remove(0);
                        }
                    }
                } else {
                    SystemClock.sleep(10);
                    continue;
                }


                // Log.e("ggg","准备播放3");
                if (buf == null) {
                    SystemClock.sleep(10);
                    continue;
                } else {
                    if (mCodec == null) {
                        SystemClock.sleep(10);
                        continue;
                    }


                    boolean flag = false;
                    try {
                        flag = onFrame(buf, 0, buf.length);
                    } catch (Exception ex) {

                    }

                    while (!flag && !mbExit) {
                        try {
                            flag = onFrame(buf, 0, buf.length);
                        } catch (Exception ex) {

                            break;
                        }
                        SystemClock.sleep(10);
                    }

                    // saveVideo(buf);
                }
                // SystemClock.sleep(20);
            }
            // Log.e("ttt","close");

        }
    }


    List<byte[]> videoBufs = new ArrayList<>();


    //摄像头默认用户
    private String defaultUser = "admin";
    //摄像头密码
    private String pwd = "123456789a";
    //摄像头uid
    private String uuid = "LWEWZ36UZNVJNBTU111A";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.play_new);
        //checkPermission();
        mSurface =(XuanYuanSurfaceView) findViewById(R.id.surfaceview);

        //ff
        instance = this;
        Bundle bundle = getIntent().getExtras();
        //deviceInfo = (DeviceInfo) bundle.getSerializable("deviceInfo");
        //初始化PTP连接
        //p2PTool = new XuanYuanXingP2PTool(deviceInfo.getUuId(), defaultUser, pwd, deviceInfo.getName());
        String uuid = getIntent().getStringExtra("UID") ;
        String pw = getIntent().getStringExtra("PW") ;
        p2PTool = new XuanYuanXingP2PTool(uuid, defaultUser, pw, "测试摄像机");
        //设置连接监听
        p2PTool.setClientP2pListener(this);
        //开启连接
        p2PTool.Start();


        screenShotImg = findViewById(R.id.screen_img);
        TPlayInfo = findViewById(R.id.TextPlayInfo);
        tool_lin = findViewById(R.id.tool_lin);

        video_pre = findViewById(R.id.preIb);
        video_pre.setOnClickListener(this);
        settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setVisibility(View.GONE);
        settingBtn.setOnClickListener(this);
        //打开视频声音 监听
        Bimg_list = findViewById(R.id.list);
        Bimg_list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkVideo();

                Bimg_list.setEnabled(false);
                Bimg_talk.setEnabled(false);
                if (isListShow == false) {
                    if (isTalkShow) {
                        //如果正在对讲 就关闭对讲
                        stopTalk();
                    }
                }
                isListShow = !isListShow;
                if (isListShow) {
                    //监听
                    startList();
                } else {
                    Bimg_list.setImageDrawable(getResources().getDrawable(R.drawable.img_list));
                    videoTool.AudioListStop();
                }

                Bimg_talk.setEnabled(true);
                Bimg_list.setEnabled(true);
            }

            // }
        });
        Bimg_talk = findViewById(R.id.talk);
        Bimg_talk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                System.out.println("Bimg_talk");
                checkVideo();
//对讲
                Bimg_list.setEnabled(false);
                Bimg_talk.setEnabled(false);
                isTalkShow = !isTalkShow;
                if (isTalkShow) {
                    if (isListShow) {
                        //对讲的时候 如果正在监听要先关闭监听
                        closeList();
                    }
                    startTalk();

                } else {
                    stopTalk();
                }
                Bimg_talk.setEnabled(true);
                Bimg_list.setEnabled(true);
            }
        });

        VideoThread videoThrea = new VideoThread();
        videoThrea.start();
    }


    private void checkVideo() {
        if (p2PTool.isOnline() == false || videoTool == null) {

            Toast.makeText(VideoPlayActivity.this, p2PTool.GetavUID() + "还未连接视频", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingBtn:
               /* Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("uuid", deviceInfo.getUuId());
                startActivity(intent);*/
                break;
            case R.id.preIb:
                video_pre.setVisibility(View.GONE);
                requsePlayVideo();
                break;
        }
    }

    /**
     * 关闭所有正在进行的视频音频录像
     */
    public void StopAll() {
        //取消定时器
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
        if (videoTool != null) {
            videoTool.StopAll();
        }
        SystemClock.sleep(800);
        finish();
    }

    /**
     * 停止所有的视频和音频流
     */
    public void StopVideo() {
        video_pre.setVisibility(View.VISIBLE);
        //取消定时器
        new Thread(new Runnable() {
            @Override
            public void run() {
                closeDecoder();
                videoBufs.clear();
                isFist = Boolean.TRUE;
            }
        }).start();
        if (videoTool != null) {
            videoTool.StopAll();
        }

    }

    /**
     * 关闭所有的链接
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExit = true;
        p2PTool.setClientP2pListener(null);
        p2PTool.Stop();//退出界面要关闭连接
        SystemClock.sleep(200);
    }


    @Override
    public void onBackPressed() {
        StopAll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (tool_lin.getVisibility() == View.VISIBLE) {
                tool_lin.setVisibility(View.GONE);
            } else {
                tool_lin.setVisibility(View.VISIBLE);
            }
        }
        result = super.onTouchEvent(event);
        return result;
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    public void onPause() {
        if (isExit == false) {
            StopVideo();
        }

//        if(isListShow){
//            new Thread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            videoTool.AudioListStop();
//                        }
//                    }
//            ).start();
//        }
        super.onPause();
        System.out.println("onPause");
    }


    @Override
    public void timeoutConnection() {
        Log.e("ttt", "timeoutConnection:");
        Toast toast = Toast.makeText(VideoPlayActivity.this, "超时连接!", Toast.LENGTH_LONG);
        toast.show();
        finish();

    }

    @Override
    public void playVideoFail() {

    }


    @Override
    public void playVideo(byte[] videoBuf, byte[] frameInfo) {
        //如果在录像
        if (isFist == Boolean.TRUE) {
            if ((videoBuf[4] & 0x1F) != 0x07 && (videoBuf[4] & 0x1F) != 0x05 && (videoBuf[4] & 0x1F) != 0x08) {
                return;
            } else {
                isFist = false;
            }

        }
        if (isRecordShow) {
            try {
                AudioJni.Mp4WriteVideoFrame(videoBuf, videoBuf.length);

                if (isRecordVideoFirst == Boolean.FALSE) {
                    isRecordVideoFirst = Boolean.TRUE;//已经写了video
                }
            } catch (Exception ex) {

            }

        }

        if (videoBufs == null) {
            videoBufs = new ArrayList<>();
        }
        videoBufs.add(videoBuf);


    }

    @Override
    public void playAudio(byte[] audioBuf) {
        //录像模式 并且已经先写了视频
        if (isRecordShow && isRecordVideoFirst) {
            try {
                if (audioBuf != null) {

                    int res = AudioJni.Mp4WriteAudioFrame(audioBuf, audioBuf.length);
                    Log.e("ttt", "res=" + res + "Mp4WriteAudioFrame size " + audioBuf.length);
                }
            } catch (Exception ex) {

            }

        }

    }

}




