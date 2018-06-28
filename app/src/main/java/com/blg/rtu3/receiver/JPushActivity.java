package com.blg.rtu3.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;



public class JPushActivity {

    private static final String TAG = "JPushActivity";
    private static Context mContext;
    public static boolean isForeground = true;//接收到信息是否传递给Activity
    private String receiveResult;

    public JPushActivity() {
    }

    public JPushActivity(Context context) {
        this.mContext = context;
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    /**
     * 注册信息接收
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        //LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, filter);
    }

    /**
     * 设置接收到信息是否向下传递给Activity
     */
    public void setIsForeground(boolean isForeground) {
        this.isForeground = isForeground;
    }

    /**
     * 停止Push信息
     */
    public void stopPush() {
        JPushInterface.stopPush(mContext);
    }

    /**
     * 重启Push
     */
    public void resumePush() {
        JPushInterface.resumePush(mContext);
    }

    /**
     * 初始化推送服务，不初始化，无法接收到信息
     */
    public void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(mContext);
    }

    /**
     * 取消注册接收服务
     */
    public void unregisterReceiver() {
        //LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

    /**
     * 信息接收器，接收到信息后的处理
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String message = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + message + "\n");
                if (!(null == extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Toast.makeText(mContext, showMsg.toString(), Toast.LENGTH_SHORT).show();
                receiveResult = showMsg.toString();
            }
        }
    }

    /**
     * 获取接收到的信息
     */
    public String getReceiveResult() {
        return receiveResult;
    }

    /**
     * 为设备设置标签
     */
    public static void setTag(String tag) {
        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!isValidTagAndAlias(sTagItme)) {
                Log.e(TAG, "error_tag_gs_empty");
                return;
            }
            tagSet.add(sTagItme);
        }
        // 调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));
    }

    /**
     * 为设备设置别名
     */
    public void setAlias(String alias) {
        // 检查 alias 的有效性
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        if (!isValidTagAndAlias(alias)) {
            Log.e(TAG, "error_alias_empty");
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }


    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    private final static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(mContext, (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(mContext, null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    /**
     * 设置别名的回调函数
     */
    private final static TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String LogUtilss;
            switch (code) {
                case 0:
                    LogUtilss = "Set tag and alias success";
                    Log.i(TAG, LogUtilss);
                    break;

                case 6002:
                    LogUtilss = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, LogUtilss);
                    if (isConnected(mContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    LogUtilss = "Failed with errorCode = " + code;
                    Log.e(TAG, LogUtilss);
            }

        }

    };
    /**
     * 设置标签回调函数
     */
    private final static TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String LogUtilss;
            switch (code) {
                case 0:
                    LogUtilss = "Set tag and alias success";
                    Log.i(TAG, LogUtilss);
                    break;

                case 6002:
                    LogUtilss = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, LogUtilss);
                    if (isConnected(mContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    LogUtilss = "Failed with errorCode = " + code;
                    Log.e(TAG, LogUtilss);
            }

        }

    };

    /**
     * 检测设备是否联网
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }
}
