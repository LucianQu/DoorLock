package com.blg.rtu.frmFunction;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blg.rtu.frmFunction.base.BaseActivity;
import com.blg.rtu.frmFunction.bean.BaseVo;
import com.blg.rtu.frmFunction.bean.FamilyVo;
import com.blg.rtu.frmFunction.bean.RoomVo;
import com.blg.rtu.frmFunction.bean.UidBean;
import com.blg.rtu.frmFunction.dialog.DialogMaker;
import com.blg.rtu.frmFunction.http.HttpCallback;
import com.blg.rtu.frmFunction.http.HttpUtil;
import com.blg.rtu.frmFunction.util.DataUtil;
import com.blg.rtu.frmFunction.util.HandlerUtils;
import com.blg.rtu.frmFunction.util.StrUtil;
import com.blg.rtu.frmFunction.util.StringUtil;
import com.blg.rtu.frmFunction.util.ThreadPoolUtils;
import com.blg.rtu.frmFunction.util.Urls;
import com.blg.rtu.frmFunction.view.CountDownProgress;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuanyuanxing.camera.XuanYuanXingP2PTool;
import com.xuanyuanxing.domain.St_SWifiAp;
import com.xuanyuanxing.engine.ClientP2pListener;
import com.xuanyuanxing.engine.GetDeviceIdCallback;
import com.xuanyuanxing.engine.GetWifiCallBack;
import com.xuanyuanxing.engine.LanSearchCallBack;
import com.xuanyuanxing.engine.SetWifiCallBack;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/*import com.elzj.camera.R;
import com.elzj.camera.util.DataUtil;
import com.elzj.camera.view.CountDownProgress;
import com.fuchun.common.activity.base.BaseActivity;
import com.fuchun.common.config.ResultCode;
import com.fuchun.common.config.Urls;
import com.fuchun.common.util.*;
import com.fuchun.common.util.http.HttpCallback;
import com.fuchun.common.util.http.HttpUtil;*/

/**
 * Created by IntelliJ IDEA.
 * User: xuzhou
 * Date: 2018/9/12
 * Time: 18:06
 */
public class CameraAddActivity extends BaseActivity implements CountDownProgress.OnonTickListener{

    private static final String TAG = CameraAddActivity.class.getSimpleName();

    public final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //标题
    private TextView tvTitle;
    //图片
    ImageView iv_icon1;
    private Context mContext ;
    private CameraAddActivity activity;
    private RelativeLayout rlyContent;

    private TextView tvSubmit;
    private TextView tvSubmit2;
    private TextView tvSubmit3;
    private TextView tvSubmit4;
    //步骤1
    private View step1View;
    //步骤2
    private View step2View;
    //步骤3
    private View step3View;
    //步骤4
    private View step4View;

    private LinearLayout llyWifi;

    private LinearLayout llyCountDown;

    private CountDownProgress countDownProgress;

    private TextView tvRefresh;

    private ListView listView;
    //灯
    private ImageView ivSoundLight;

    private EditText etAccount, etPwd, etDeviceName;
    //wifi
    private TextView tvWifis;
    private TextView tvSelectRoom;
    //设备uuid
    private String uuid;
    private String UID = "";
    //WiFi名称
    private String account;
    //密码
    private String pwd;
    //设备id
    private String deviceId;
    //步骤
    private int step;
    //是否已正在搜索设备
    private boolean isSearchUuid = false;
    //是否搜索到设备
    private boolean isSearchSuccess = false;
    //是否正在配网
    private boolean isConfigInternet = false;
    //
    private boolean isPresent = false;
    private int currState = -1;
    //轩辕星
    private XuanYuanXingP2PTool nClientP2P;
    private byte mode = 0x01;
    private byte enctype = 0x01;
    //WiFi列表
    //  private ArrayList<St_SWifiAp> wifis;
    private ArrayList<St_SWifiAp> wifis;
    //房间列表
    private List<RoomVo> roomVoList;
    //房间id
    private long roomId;

    private CustomListAdapter adapter;

    private boolean isSendWifi = false;
    private String saveSSid;
    private Boolean isReceiveWifiRes = false;

    private ConnectivityInfoReceiver connectivityInfoReceiver;

    //摄像头默认用户
    private String defaultUser = "admin";
    //摄像头默认密码
    private String defaultPwd = "antsmartlife365";

    private static class MyHandler extends HandlerUtils<CameraAddActivity> {
        public MyHandler(CameraAddActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(CameraAddActivity activity, Message msg) {
            if (activity == null) // WeakReference could be GC'ed early
                return;
            // TODO: 2019/1/15 handler
            switch (msg.what) {
                case 1:
                    Log.e("Lucian--->handler 1连接:","connect") ;
                    activity.connect();
                    break;
                case 2:
                    //没有扫描到设备
                    DialogMaker.dismissProgressDialog();
                    activity.showAlertDialog(R.string.str_no_search_device, null);
                    break;
                case 3:
                    //扫描到设备
                    DialogMaker.dismissProgressDialog();
                    activity.configureSuccess();
                    break;
                case 4:
                    // 连接WiFi超时
                    activity.stopConfigCountDown();
                    activity.showAlertDialog(R.string.str_configure_internet_failure, activity.dialogOnClickListener);
                    break;
                case 5:
                    //设置wifi成功
                    activity.setWifiSuccess();
                    activity.TimerHandler.postDelayed(activity.deviceStatusTimerRun, 3000);
                    break;
                case 6:
                    activity.showWifiAlert();
                    break;
                case 7:
                    //查询服务器上线了
                    activity.setWifiSuccess();
                    break;
                case 8:
                    //查询设备在线
                    activity.getDeviceStatus() ;
                    break;
                case 9:
                    //设置wifi失败
                    ToastUtils.show(activity, "设置wifi失败！");
                    activity.backUid();
                    break;
            }

        }
    }

    private MyHandler handlerUtil = new MyHandler(this);

    private class ConnectivityInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!isSendWifi || isReceiveWifiRes) {
                    //ToastUtils.show(mContext, "没发送wifi请求 获取已经收到结果 返回");
                    //还没发送wifi请求 获取已经收到结果 返回
                    return;
                }
                if (isWifi(mContext)) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    if (!TextUtils.isEmpty(ssid) && ssid.equals(saveSSid)) {
                        //ToastUtils.show(mContext, "同一个wifi 返回");
                        //如果还是同一个wifi不管他
                        return;
                    } else {
                        setWifiCallBack.setWifiRes(0);
                    }

                } else {
                    //成功
                    setWifiCallBack.setWifiRes(0);
                }

            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CameraAddActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_add);
        mContext = CameraAddActivity.this ;
        initData();
        initView();
        activity = this;
    }

    @Override
    public void initView() {
        tvTitle = findView(R.id.tv_title);
        tvTitle.setText(R.string.str_ai_camera);
        //图片
        iv_icon1 = findView(R.id.iv_icon1);
        iv_icon1.setImageResource(R.mipmap.ic_camera_gray);
        // 返回按钮
        TextView tvBack = findView(R.id.tv_left);
        tvBack.setOnClickListener(onClickListener);

        rlyContent = findView(R.id.rly_content);
        rlyContent.setOnClickListener(onClickListener);

        step1View = findView(R.id.lly_step1);
        step2View = findView(R.id.lly_step2);
        step3View = findView(R.id.lly_step3);
        step4View = findView(R.id.lly_step4);

        ivSoundLight = step1View.findViewById(R.id.iv_sound_light);

        etAccount = step3View.findViewById(R.id.et_account);
        etPwd = step3View.findViewById(R.id.et_pwd);
        etAccount.addTextChangedListener(textWatcher);
        etPwd.addTextChangedListener(textWatcher);

        tvWifis = step3View.findViewById(R.id.tv_wifis);
        tvWifis.setOnClickListener(onClickListener);

        etDeviceName = step4View.findViewById(R.id.et_device_name);
        etDeviceName.setText(R.string.str_ai_camera);

        tvSelectRoom = step4View.findViewById(R.id.tv_select_room);
        tvSelectRoom.setOnClickListener(onClickListener);

        //提交1
        tvSubmit = findView(R.id.tv_submit);
        tvSubmit.setOnClickListener(onClickListener);

        //提交2
        tvSubmit2 = findView(R.id.tv_submit2);
        tvSubmit2.setOnClickListener(onClickListener);

        //提交3
        tvSubmit3 = findView(R.id.tv_submit3);
        tvSubmit3.setOnClickListener(onClickListener);

        //提交4
        tvSubmit4 = findView(R.id.tv_submit4);
        tvSubmit4.setOnClickListener(onClickListener);

        llyWifi = findView(R.id.lly_wifi);
        tvRefresh = findView(R.id.tv_refresh);
        tvRefresh.setOnClickListener(onClickListener);

        listView = findView(R.id.listView);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setAdapter(adapter);

        llyCountDown = findView(R.id.lly_count_down);
        llyCountDown.setOnClickListener(onClickListener);

        countDownProgress = findViewById(R.id.pro_countDown);

        startSoundLight(ivSoundLight);
        registerConnectivityInfoReceiver();
    }

    @Override
    public void initData() {
        wifis = new ArrayList<>();
        adapter = new CustomListAdapter();
        FamilyVo familyVo = DataUtil.getInstance().getCurrentFamily();
        if (null == familyVo || null == familyVo.getRooms() || familyVo.getRooms().size() == 0) {
            return;

        }
        // roomVoList = familyVo.getRooms();

        Map wifi = DataUtil.getInstance().getWifi(CameraAddActivity.this);
        if (wifi != null) {
            try {
                etAccount.setText((CharSequence) wifi.get("ssid"));
                etPwd.setText((CharSequence) wifi.get("password"));
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public void onResume() {
        Map wifi = DataUtil.getInstance().getWifi(CameraAddActivity.this);
        if (wifi != null) {
            etAccount.setText((CharSequence) wifi.get("ssid"));
            etPwd.setText((CharSequence) wifi.get("password"));
        }
        super.onResume();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_left:
                    onBackPressed();
                    break;
                case R.id.rly_content:
                    llyWifi.setVisibility(View.GONE);
                    break;
                case R.id.tv_submit:
                    step = 1;
                    changeStep();
                    break;
                case R.id.tv_submit2:
                    search();
                    break;
                case R.id.tv_submit3:
                    submit();
                    break;
                case R.id.tv_select_room:

                    selectRoom();
                    break;
                case R.id.tv_submit4:
                    bindDevice();
                    break;
                case R.id.tv_refresh:
                    refreshWifi();
                    break;
                case R.id.tv_wifis:
                    showWifis();
                    break;
                default:
            }
        }
    };

    private void backUid() {
        Intent intent = new Intent() ;
        intent.putExtra("UID",UID) ;
        setResult(0xa5, intent);
        Log.e("Lucian--->返回", "UID:"+UID) ;
        finish();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            St_SWifiAp wifiap = (St_SWifiAp)wifis.get(i - listView.getHeaderViewsCount());
            etAccount.setText(wifiap.ssid);
            llyWifi.setVisibility(View.GONE);
        }
    };

    @Override
    public void onBackPressed() {
        if (!isConfigInternet && step > 0) {
            if (step == 2) {
                llyWifi.setVisibility(View.GONE);
            }
            step--;
            changeStep();

        } else {
            super.onBackPressed();
        }
    }

    /**
     * 账号和密码监听
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isEnable = etAccount.getText().length() > 0
                    && etPwd.getText().length() > 0;
            tvSubmit3.setEnabled(isEnable);
        }
    };

    /**
     * 配置
     */
    private void configureSuccess() {
        step = 2;
        changeStep();
        Log.e("Lucian--->获取wifi列表:","开始") ;
        nClientP2P.setGetWifiCallBack(getWifiCallBack);
        nClientP2P.setSetWifiCallBack(setWifiCallBack);
        //getWifiList();
        refreshWifi();
    }

    /**
     * 设置Wi-Fi成功
     */
    private void setWifiSuccess() {
        stopConfigCountDown();
        step = 3;
        changeStep();
        getDeviceStatus();
        //loadRoom();
    }

    /**
     * 提交
     */
    private void submit() {
        account = etAccount.getEditableText().toString();
        pwd = etPwd.getEditableText().toString();
        showConnAlert();
    }

    /**
     * 切换步骤
     */
    private void changeStep() {
        if (step == 0) {
            tvTitle.setText(R.string.str_ai_camera);
            step1View.setVisibility(View.VISIBLE);
            step2View.setVisibility(View.GONE);
            step3View.setVisibility(View.GONE);
            step4View.setVisibility(View.GONE);
        } else if (step == 1) {
            tvTitle.setText(R.string.str_connect_internet);
            step1View.setVisibility(View.GONE);
            step2View.setVisibility(View.VISIBLE);
            step3View.setVisibility(View.GONE);
            step4View.setVisibility(View.GONE);
        } else if (step == 2) {
            tvTitle.setText(R.string.str_connect_internet);
            step1View.setVisibility(View.GONE);
            step2View.setVisibility(View.GONE);
            step3View.setVisibility(View.VISIBLE);
            step4View.setVisibility(View.GONE);
        } else if (step == 3) {
            tvTitle.setText(R.string.str_modify);
            step1View.setVisibility(View.GONE);
            step2View.setVisibility(View.GONE);
            step3View.setVisibility(View.GONE);
            step4View.setVisibility(View.VISIBLE);
            if (nClientP2P != null) {
                nClientP2P.setClientP2pListener(null);
                nClientP2P.Stop();
                nClientP2P = null;
            }
        }
    }



    // TODO: 2019/1/15 搜索
    /**
     * 搜索
     */
    private void search() {
        if (!isSearchUuid ) {
            if (isSearchSuccess) {
                Log.e(TAG, "已连上 ");
                ToastUtils.show(CameraAddActivity.this, "已连上！");
            }else {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();

                if (!ssid.contains("AI-M2")) {
                    ToastUtils.show(CameraAddActivity.this, R.string.str_device_no_connect_ap);
                    return;
                }
                Log.e("Lucian----SSID",ssid) ;

                //如果当前没有在搜索状态  就去搜索
                isSearchUuid = true;
                isSearchSuccess = false;
                DialogMaker.showProgressDialog(CameraAddActivity.this, getString(R.string.str_searching));
                ThreadPoolUtils.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Lucian--->开始扫描","") ;
                        searchUuid();
                    }
                }) ;
            }
        } else {
                Log.e(TAG, "已经在扫描 ");
                ToastUtils.show(CameraAddActivity.this, "已经在扫描！");
        }
    }

    /**
     * 搜索uuid
     *//*
    private void searchUuid() {
        XuanYuanXingP2PTool.mLanSearch(new LanSearchCallBack() {
            @Override
            public void mLanSearchUUid(String uuid) {
                Log.e("Lucian---->搜索到UUID", uuid) ;
                isSearchSuccess = true;
                CameraAddActivity.this.uuid = uuid;
                handlerUtil.sendEmptyMessage(1);
            }

            @Override
            public void mLanSearchFinsh() {
                isSearchUuid = false;
                if (isSearchSuccess) {
                    //成功
                    Log.e("Lucian--->searchUuid", "扫描到设备 ");
                } else {
                    //没有扫描到
                    Log.e("Lucian--->searchUuid", "没有扫描到设备 ");
                    handlerUtil.sendEmptyMessage(2);
                }
            }
        });
    }*/

    static String defuuid = "UZUBW272DCRVNZBY111A";

    // TODO: 2019/1/23 searchuuid
    private void searchUuid()
    {
        XuanYuanXingP2PTool.Init();
        XuanYuanXingP2PTool.mLanSearch(new LanSearchCallBack()
        {
            @Override
            public void mLanSearchUUid(com.xuanyuanxing.domain.DeviceInfo deviceInfo) {
                isSearchSuccess = true ;
                uuid = deviceInfo.getUuId();
                Log.e("Lucian--->扫描UUID成功", uuid) ;
                if (defuuid.equals(deviceInfo.getUuId()))
                {
                    if (!StringUtil.isEmpty(deviceInfo.getDeviceNo()))
                    {
                        if ("0101".equals(deviceInfo.getDeviceNo().substring(0, 4)))
                        {
                            Log.e("Lucian--->Device", "DeviceNO:"+deviceInfo.getDeviceNo()) ;
                            Log.e("Lucian--->设备号前四位为0101", "开始连接") ;

                            handlerUtil.sendEmptyMessage(1);
                            return;
                        }
                        Log.e("Lucian--->设备号前四位不为0101", "扫描失败") ;
                        handlerUtil.sendEmptyMessage(2);
                        return;
                    }
                    Log.e("Lucian--->没有注册过服务", "开始连接") ;
                    handlerUtil.sendEmptyMessage(1);
                    return;
                }
                Log.e("Lucian--->DevNo不等于默认", "扫描失败") ;
                handlerUtil.sendEmptyMessage(2);
            }

            public void mLanSearchFinsh()
            {
                isSearchUuid = false ;
                if (isSearchSuccess)
                {
                    Log.e(TAG, "扫描到设备 ");
                    return;
                }
                Log.e(TAG, "没有扫描到设备 ");
                handlerUtil.sendEmptyMessage(2);
            }

        });
    }

    /**
     * 搜索WiFi
     */
    private void searchWifi() {
        ThreadPoolUtils.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                nClientP2P.GetIPNCInfo();
                //getWifiList();
            }
        });
    }


    /**
     * 刷新wifi
     */
    private void refreshWifi() {
        DialogMaker.showProgressDialog(CameraAddActivity.this, getString(R.string.str_searching));
        searchWifi();
    }

    /**
     * 展示wifi
     */
    private void showWifis() {
        showKeyboard(false);
        if (wifis.size() > 0) {
            showWifiAlert();
        } else {
            refreshWifi();
        }

    }

    /**
     * 展示WiFi
     */
    private void showWifiAlert() {
        if (step == 2) {
            llyWifi.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 获取设备id
     */
    private void getDeviceId() {
        ////DialogMaker.showProgressDialog(this, getString(R.string.str_processing));
        ThreadPoolUtils.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                // 恢复出厂 测试用
                //nClientP2P.restoreFactory();
                //获取设备id
                nClientP2P.getDeviceID(new GetDeviceIdCallback() {
                    @Override
                    public void DeviceId(String deviceId) {
                        Log.e("Lucian--->获取设备ID", "ID:"+deviceId) ;
                        CameraAddActivity.this.deviceId = deviceId;
                        handlerUtil.sendEmptyMessage(3);
                    }
                });
                //获取wifi列表
                // nClientP2P.GetIPNCInfo();

            }
        });
    }

    /**
     * 获取WiFi名称
     */
    // TODO: 2019/1/16 wifi列表
    private void getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.startScan();  //开始扫描AP
        ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();
        Log.e("Lucian-->wifi列表", "个数："+list.size());
        if (list == null) {
            Toast.makeText(this, "当前周围无WiFi", Toast.LENGTH_LONG).show();
        } else {
            wifis.clear();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Log.e("Lucian-->wifi BSSID()", wifiInfo.getBSSID());
            for (int i = 0; i < list.size(); i++) {
                ScanResult scanResult = list.get(i);
                String wifiName = scanResult.SSID;
                Log.e("Lucian-->wifiName->{}", wifiName);
                Log.e("Lucian-->BSSID", scanResult.BSSID);
                if (!wifiInfo.getBSSID().equals(scanResult.BSSID)) {
                    Log.e("Lucian-->wifiName add", wifiName);
                    //wifis.add(wifiName);
                }
            }
            //removeDuplicate(wifis);
        }

        handlerUtil.sendEmptyMessage(6);
        ///DialogMaker.dismissProgressDialog();
    }


    private static void removeDuplicate(List<String> list) {
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }

    /**
     * 启动配置进度
     */
    private void startConfigCountDown() {
        isConfigInternet = true;
        llyCountDown.setVisibility(View.VISIBLE);

        countDownProgress.setCountdownTime(30 * 1000);
        countDownProgress.startCountDownTime(new CountDownProgress.OnCountdownFinishListener() {
            @Override
            public void countdownFinished() {
                handlerUtil.sendEmptyMessage(4);
                isConfigInternet = false;
                //重新链接
                connect();
            }
        },this);
    }

    @Override
    public void onTickListener() {
        //ToastUtils.show(this,"计时完成");
        Log.e("Lucian--->","倒计时中...") ;
    }

    private void stopConfigCountDown() {
        isConfigInternet = false;
        countDownProgress.stopCountDown();
        llyCountDown.setVisibility(View.GONE);
        TimerHandler.removeCallbacks(deviceStatusTimerRun);//调用此方法，以关闭此定时器
    }

    /**
     * 设置Wi-Fi
     */
    private void setWifi() {
        startConfigCountDown();
        ThreadPoolUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                saveSSid = wifiInfo.getSSID();//保存当前连接的wifi名称
                isSendWifi = true;

                Map<String, String> wifiMap = new HashMap<>();
                wifiMap.put("ssid", account);
                wifiMap.put("password", pwd);
                LogUtils.e("Lucian---->设置摄像头连接wifi名称和密码", account + "---"+pwd);
                Log.e("add wifi", String.valueOf(DataUtil.getInstance().addWifi(CameraAddActivity.this, wifiMap)));
                nClientP2P.SetIPNCWifiInfo(account, pwd, mode, enctype);
            }
        });
    }

    /**
     * 展示连接tips
     */
    private void showConnAlert() {
        new AlertDialog.Builder(this).setTitle(R.string.str_tip).setMessage(R.string.str_device_conn_tips).setPositiveButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setWifi();
            }
        }).setNegativeButton(R.string.str_cancel, null).show();
    }

    /**
     * 连接
     */
    public void connect() {
        if (nClientP2P != null) {
            nClientP2P.Stop();
            nClientP2P = null;
        }
        nClientP2P = new XuanYuanXingP2PTool(uuid
                , defaultUser
                , defaultPwd, "摄像机");
        nClientP2P.setClientP2pListener(listener);
        nClientP2P.Start();
    }

    ClientP2pListener listener = new ClientP2pListener() {
        @Override
        public void P2pState(int state) {
            currState = state;
            if (state == 2) {
                if (!isPresent) {
                    isPresent = true;
                    Log.e("Lucian--->p2p连接成功", "下步获取设备ID") ;
                    getDeviceId();
                }
            } else {
                Log.e(TAG, "当前状态" + state);
                //-6 密码错误 其他 都连接失败
                if (state == -6) {
                    DialogMaker.dismissProgressDialog();
                    showAlertDialog(R.string.str_device_pwd_error, null);
                } else if (state < 0) {
                    DialogMaker.dismissProgressDialog();
                    nClientP2P.Stop(); //断开连接
                    showAlertDialog(R.string.str_device_connect_error, null);
                }
            }
        }
    };


    GetWifiCallBack getWifiCallBack = new GetWifiCallBack() {

        @Override
        public void wifiInfo(List<St_SWifiAp> WifiSt, boolean isOver) {
            if (WifiSt != null) {
                wifis.clear();
                for (St_SWifiAp wifi : WifiSt) {
                    if (!TextUtils.isEmpty(wifi.ssid)) {
                        wifis.add(wifi);
                    }
                }
                handlerUtil.sendEmptyMessage(6);
            }
            DialogMaker.dismissProgressDialog();
        }
    };

    SetWifiCallBack setWifiCallBack = new SetWifiCallBack() {

        @Override
        public void setWifiRes(int i) {

            if (isReceiveWifiRes) {
                return;
            }
            Log.e("Lucian--->设置wifi结果：" ,i +"") ;
            isReceiveWifiRes = true;
            if (i == 0) {
                ToastUtils.show(mContext,"设置wifi成功");
                // TODO: 2019/1/16
                handlerUtil.sendEmptyMessageDelayed(8,10000);
            } else {
                handlerUtil.sendEmptyMessage(9);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nClientP2P != null) {
            nClientP2P.setClientP2pListener(null);
            nClientP2P.Stop();
            nClientP2P = null;
        }

        unregisterConnectivityInfoReceiver();
    }

    /**
     * 展示提示dialog
     *
     * @param msg 消息
     */
    public void showAlertDialog(int msg, DialogInterface.OnClickListener dialogOnClickListener) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("确定", dialogOnClickListener).show();
        } catch (Exception e) {

        }
    }

    DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            toMainActivity();
        }
    };

    private String[] titles;

    /**
     * 展示选择房间dialog
     */
    public void selectRoom() {
        if (roomVoList == null || roomVoList.size() == 0) {

        } else {
            new AlertDialog.Builder(activity).setTitle(R.string.str_select_room_tips).setItems(titles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    roomId = roomVoList.get(i).getTid();
                    tvSelectRoom.setText(titles[i]);
                }
            }).show();
        }
    }

    // TODO: 2019/1/21 获取设备在线状态
    public void getDeviceStatus() {
        final Type type = new TypeToken<BaseVo<Integer>>() {
        }.getType();
        LogUtils.e("Lucian_deviceId", "设备ID："+deviceId);
        RequestParams requestParams = new RequestParams(Urls.DEVICE_CONNECTION_STATUS1+deviceId);
        LogUtils.e("Lucian---->deviceStatus", requestParams.toString());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    LogUtils.e("Lucian---->result", result);
                    JSONObject jsonResult = new JSONObject(result) ;
                    int code = jsonResult.getInt("code") ;
                    if (code == 200) {
                        if (jsonResult.getInt("result") == 1) {
                            ToastUtils.showLong(CameraAddActivity.this, "摄像头未在线，网络可能未配置成功");
                        }else {
                            ToastUtils.showLong(CameraAddActivity.this, "摄像头在线");
                            getDeviceUid() ;
                        }
                    }else {
                        ToastUtils.showLong(CameraAddActivity.this, jsonResult.getString("message"));
                    }
                }catch (Exception e){

                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    // TODO: 2019/1/21 获取设备在线状态
    public void getDeviceUid() {
        final Type type = new TypeToken<BaseVo<UidBean.ResultBean>>() {
        }.getType();
        RequestParams requestParams = new RequestParams(Urls.DEVICE_GET_UID+this.deviceId);
        //requestParams.addBodyParameter("dtuId", dtuId);
        LogUtils.e("Lucian---->uid", requestParams.toString());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    LogUtils.e("Lucian---->result", result);
                    JSONObject jsonResult = new JSONObject(result) ;
                    int code = jsonResult.getInt("code") ;
                    if (code == 200) {
                        Gson gson = new Gson() ;
                        String data = jsonResult.getString("result") ;
                        if (!data.equals("")) {
                            UidBean.ResultBean  resultBean = gson.fromJson(data, UidBean.ResultBean.class) ;
                            UID = resultBean.getUid() ;
                            Log.e("Lucian-->获取设备ID", UID) ;
                        }
                    }else {
                        ToastUtils.showLong(CameraAddActivity.this, jsonResult.getString("message"));
                    }
                    backUid();
                }catch (Exception e){
                    backUid();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                backUid();
            }
            @Override
            public void onCancelled(CancelledException cex) {
                backUid();
            }
            @Override
            public void onFinished() {
                backUid();
            }
        });
    }



    public void loadRoom() {
        final Type type = new TypeToken<BaseVo<List<RoomVo>>>() {
        }.getType();
        RequestParams requestParams = new RequestParams(Urls.USER_ROOM_URL);
        //requestParams.addBodyParameter("dtuId", dtuId);
        LogUtils.e("Lucian---->loadRoom", requestParams.toString());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    LogUtils.e("Lucian---->result", result);
                    JSONObject jsonResult = new JSONObject(result) ;
                    int code = jsonResult.getInt("code") ;
                    if (code == 200) {
                        Gson gson = new Gson() ;
                        String data = jsonResult.getString("result") ;
                        if (!data.equals("")) {
                            roomVoList = gson.fromJson(data, type) ;
                            titles = new String[roomVoList.size()];
                            for (int i = 0; i < roomVoList.size(); i++) {
                                titles[i] = roomVoList.get(i).getName();
                            }
                            roomId = roomVoList.get(0).getTid();
                            tvSelectRoom.setText(titles[0]);
                        }
                    }else {
                        ToastUtils.showLong(CameraAddActivity.this, code+"--"+jsonResult.getString("message"));
                    }
                }catch (Exception e){

                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }


    /*public void loadRoom() {

        Type type = new TypeToken<BaseVo<List<RoomVo>>>() {
        }.getType();
        HttpUtil.getInstance().get(Urls.USER_ROOM_URL, type, new HttpCallback() {
            @Override
            public void onSuccess(String url, Object object) {
                if (object == null) {
                    return;
                }
                BaseVo<List<RoomVo>> baseVo = (BaseVo<List<RoomVo>>) object;
                if (ResultCode.SUCCESS == baseVo.getCode()) {
                    if (null != baseVo.getResult()) {
                        roomVoList = baseVo.getResult();
                        titles = new String[roomVoList.size()];
                        for (int i = 0; i < roomVoList.size(); i++) {
                            titles[i] = roomVoList.get(i).getName();
                        }
                        roomId = roomVoList.get(0).getTid();
                        tvSelectRoom.setText(titles[0]);
                    }
                }
            }

            @Override
            public void onError(String url, Exception e) {
            }
        });
    }*/

    /**
     * 判断是wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 注册网络信息广播
     */
    private void registerConnectivityInfoReceiver() {
        connectivityInfoReceiver = new ConnectivityInfoReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_CHANGE_ACTION);
        filter.setPriority(1000);
        registerReceiver(connectivityInfoReceiver, filter);
    }

    /**
     * 取消注册网络信息广播
     */
    private void unregisterConnectivityInfoReceiver() {
        unregisterReceiver(connectivityInfoReceiver);
    }


    /**
     * 开启View闪烁效果
     */
    private void startSoundLight(View view) {
        if (null == view) {
            return;
        }

        Animation alphaAnimation = new AlphaAnimation(1, 0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(alphaAnimation);
    }

    private void toMainActivity() {
        //MainActivity.start(this, 0);
        finish();
    }

    /**
     * 绑定设备
     */
  /*  private void bindDevice() {
        String deviceName = etDeviceName.getText().toString();
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtils.showLong(CameraAddActivity.this, R.string.str_please_input_device_name);
            return;
        } else if (roomId <= 0) {
            ToastUtils.showLong(CameraAddActivity.this, R.string.str_please_select_room);
            return;
        } else if (deviceName.length() > 10) {
            ToastUtils.showLong(CameraAddActivity.this, R.string.str_input_device_name_max_length);
            return;
        }


        Type type = new TypeToken<BaseVo>() {
        }.getType();


        RequestDataBase param = new RequestDataBase(this);
        param.put("deviceId", deviceId); // 设备id
        param.put("uid", uuid); // 名称
        param.put("timeZone", TimeZone.getDefault().getID());//时区
        param.put("deviceName", deviceName);// 名称
        param.put("roomId", roomId);
        param.put("city", "未知");
        param.put("lat", "未知");
        param.put("lon", "未知");
        PositionVo positionVo = DataUtil.getInstance().getPosition(CameraAddActivity.this);
        if (positionVo != null) {
            param.put("city", positionVo.getCity());
            param.put("lat", positionVo.getLatitude());
            param.put("lon", positionVo.getLongitude());
        }

        DialogMaker.showProgressDialog(this, getString(R.string.str_submitting));
        HttpUtil.getInstance().post(Urls.DEVICE_ADD_URL, param, type, new HttpCallback() {
            @Override
            public void onSuccess(String url, Object object) {
                DialogMaker.dismissProgressDialog();
                BaseVo baseVo = (BaseVo) object;
                if (ResultCode.SUCCESS == baseVo.getCode()) {
                    toMainActivity();
                    ToastUtils.show(CameraAddActivity.this, R.string.str_add_success);
                } else {
                    ToastUtils.show(CameraAddActivity.this, baseVo.getMessage());
                }
            }

            @Override
            public void onError(String url, Exception e) {
                DialogMaker.dismissProgressDialog();
                ToastUtil.showToast(CameraAddActivity.this, R.string.str_submit_failure);
            }
        });
    }*/

    public void bindDevice() {

        String deviceName = etDeviceName.getText().toString();
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtils.show(CameraAddActivity.this, R.string.str_please_input_device_name);
            return;
        } else if (roomId <= 0) {
            ToastUtils.show(CameraAddActivity.this, R.string.str_please_select_room);
            return;
        } else if (deviceName.length() > 10) {
            ToastUtils.show(CameraAddActivity.this, R.string.str_input_device_name_max_length);
            return;
        }


        final Type type = new TypeToken<BaseVo>() {
        }.getType();

        RequestParams requestParams = new RequestParams(Urls.DEVICE_ADD_URL);

        requestParams.addBodyParameter("deviceNo", deviceId); // 设备id
        requestParams.addBodyParameter("uid", uuid); // 名称
        requestParams.addBodyParameter("timeZone", TimeZone.getDefault().getID());//时区
        requestParams.addBodyParameter("deviceName", deviceName);// 名称
        requestParams.addParameter("roomId", roomId);
        requestParams.addBodyParameter("city", "未知");
        requestParams.addBodyParameter("lat", "未知");
        requestParams.addBodyParameter("lon", "未知");
        /*PositionVo positionVo = DataUtil.getInstance().getPosition(CameraAddActivity.this);
        if (positionVo != null) {
            requestParams.addParameter("city", positionVo.getCity());
            requestParams.addParameter("lat", positionVo.getLatitude());
            requestParams.addParameter("lon", positionVo.getLongitude());
        }*/
        DialogMaker.showProgressDialog(this, getString(R.string.str_submitting));

        LogUtils.e("Lucian---->bindDevice", requestParams.toString());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    LogUtils.e("Lucian---->result", result);
                    DialogMaker.dismissProgressDialog();
                    JSONObject jsonResult = new JSONObject(result) ;
                    int code = jsonResult.getInt("code") ;
                    if (code == 200) {
                        ToastUtils.show(CameraAddActivity.this, R.string.str_add_success);
                    }else {
                        ToastUtils.show(CameraAddActivity.this, jsonResult.getString("message"));
                    }
                }catch (Exception e){

                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                DialogMaker.dismissProgressDialog();
                ToastUtils.show(CameraAddActivity.this, R.string.str_submit_failure);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }



    public class CustomListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public CustomListAdapter() {
            inflater = LayoutInflater.from(CameraAddActivity.this);
        }

        public int getCount() {
            return wifis.size();
        }

        public Object getItem(int position) {
            return wifis.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_wifi, null);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            St_SWifiAp wifi = wifis.get(position);
            holder.tvTitle.setText(StrUtil.nullToStr(wifi.ssid));

            /*String wifi_name = wifis.get(position);
            holder.tvTitle.setText(nullToStr(wifi_name));*/

            return convertView;
        }
    }

    public static String nullToStr(Object paramObject)
    {
        if ((paramObject != null) && (!paramObject.toString().equals("null"))) {
            return paramObject.toString().trim();
        }
        return "";
    }

    static class ViewHolder {
        TextView tvTitle;
    }


    Handler TimerHandler = new Handler();                   //创建一个Handler对象
    //创建一个runnable对象
    Runnable deviceStatusTimerRun = new Runnable() {
        @Override
        public void run() {
            //要做的事情这里
            //handlerUtil.sendEmptyMessage(1);
            loadDataDevice();
        }
    };


    private void loadDataDevice() {
        Type type = new TypeToken<BaseVo<Integer>>() {
        }.getType();
        HttpUtil.getInstance().get((Urls.DEVICE_CONNECTION_STATUS1+deviceId), type, new HttpCallback() {
            @Override
            public void onSuccess(String url, Object object) {
                BaseVo<Integer> baseVo = (BaseVo<Integer>) object;
                int status = baseVo.getResult();
                LogUtils.e("Lucian---->loadDataDevice", "status:"+ status);
                if (status == 1) {
                    LogUtils.e("Lucian---->deviceStatusTimerRun", "onSuccess");
                    TimerHandler.postDelayed(deviceStatusTimerRun, 3000);
                } else {
                    LogUtils.e("Lucian---->handlerUtil.sendEmptyMessage(7)", "onSuccess");
                    handlerUtil.sendEmptyMessage(7);
                }
            }

            @Override
            public void onError(String url, Exception e) {
                LogUtils.e("Lucian---->deviceStatusTimerRun", "error");
                TimerHandler.postDelayed(deviceStatusTimerRun, 3000);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        TimerHandler.removeCallbacks(deviceStatusTimerRun);//调用此方法，以关闭此定时器
    }

    // TODO: 2019/1/16 loaddatadevice
   /* public void loadDataDevice() {

        final Type type = new TypeToken<BaseVo<Integer>>() {
        }.getType();

        RequestParams requestParams = new RequestParams(String.format(Urls.DEVICE_CONNECTION_STATUS, deviceId));

       // requestParams.addBodyParameter("deviceId", deviceId); // 设备id

        LogUtils.e("Lucian---->loadDataDevice", requestParams.toString());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    LogUtils.e("Lucian---->result", result);
                    JSONObject jsonResult = new JSONObject(result) ;
                    int code = jsonResult.getInt("result") ;
                    if (code == 1) {
                        TimerHandler.postDelayed(deviceStatusTimerRun, 3000);
                    } else {
                        handlerUtil.sendEmptyMessage(7);
                    }
                }catch (Exception e){

                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                TimerHandler.postDelayed(deviceStatusTimerRun, 3000);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }*/
}