package com.blg.rtu3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blg.rtu.aidl.ServiceAidl;
import com.blg.rtu.frmChannel.helpCh1.ChBusi_01_Operate;
import com.blg.rtu.server.net.NetManager;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.MyTimeTask;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.util.ResourceUtils;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.SoundAlert;
import com.blg.rtu.util.StringValueForActivity;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu3.receiver.JPushActivity;
import com.blg.rtu3.server.LocalServer;
import com.blg.rtu3.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


@SuppressLint("HandlerLeak")
public class MainActivity  extends Activity { 
	public static MainActivity instance = null ;
	public ChBusi_01_Operate chb;
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.blg.rtu3.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "EXTRA_ALERT";

	private ViewPager mPager;// Tab页卡
	private List<View> listPages; // Tab页面列表
	
	private View pageView_fourth;// Tab第0页
	private View pageView_second;// Tab第1页
	private View pageView_main;// Tab第2页
	private View pageView_third;// Tab第3页
	
	public Boolean tcpConnected = false;
	public Boolean serverConnected = false;
	public TextView tcpConnectStatus;
	public TextView connectDoorId;
	public TextView tv_connectType;

	public TextView switchFun ;
	public TextView switchOnce ;
	public TextView switchQuery ;
	public TextView switchLog ;
	
	public TextView rtuAssiName ;
	private TextView tvProductID ;
	
	public ScrollView func_scrollView ;
	
	//通道选择界面实体
	public LinearLayout chLine_01, chLine_02, chLine_03, chLine_04;
	
	//非协议数据界面实体
	public LinearLayout npFragmentLinear_02, npFragmentLinear_03 ;
	
	private MainBroadcastReceiver broadcastReceiver ;
	
	//声音
	private SoundAlert soundAlert ;
	//退出计时
	private long mExitTime;
	
	//fragment工具类对象
	public FragmentTool frgTool ;
	
	//fragment回调类对象
	private FragmentCallback frgCallback; 
	
	//mainActivity的帮助类对象，以设置不属于fragment的界面元素的事件监听器
	private MainActivityHelp mainHelp ;
	
    
	//activity的桩，其代理注册到server端去了
	public StubActivity mActivityStub ;
	//server的代理持有者，server代理的桩在server端
	public ServerProxyHandler mServerProxyHandler ;

	//private boolean wifiOpenStatus = false ;
	private WifiManager wifiManager ;
	//滚动条图片
	private ImageView scrollbar ;
	//滚动条初始偏移量
	private int offset = 0 ;
	//当前页编号
	private int currIndex = 0 ;
	// 滚动条宽度
    private int bmpW ;
    //一倍滚动量
    private int doubleRoll ;

	private AutoScrollTextView scrollTextView ;
	private JPushActivity mJPush ;
	public int delay = 0 ;

	public boolean requestServeice = true ;
	public String mIpPort = "http://47.107.34.32:8090" ;
	private MyTimeTask task ;
	private boolean taskStatus = false ;

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
		//JpushUtils.setTag("admin");//
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String extras = intent.getStringExtra(KEY_EXTRAS);
				if (extras != null) {
					LogUtils.e("消息接收-extras", extras);
					if (extras.contains("http") && extras.contains(".") && extras.contains(":")) {
						if (mIpPort.equals(extras)) {
							ToastUtils.show(MainActivity.this, "远程接收服务地址,相同,无需更改!");
						}else {
							mIpPort = extras ;
							SharepreferenceUtils.saveIpPort(MainActivity.this,extras);
							ToastUtils.show(MainActivity.this, "远程接收服务地址,地址已更改!");
						}

					}else {
						SharepreferenceUtils.saveMessage(MainActivity.this, extras);
						scrollTextView.setText(extras);
						scrollTextView.init(getWindowManager());
					}
				}else {
					ToastUtils.show(MainActivity.this, "推送消息为空！");
				}
			}
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.e("Connect Service:","Start") ;
			ServiceAidl serverProxy = ServiceAidl.Stub.asInterface(service);//mSercice
			mServerProxyHandler = ServerProxyHandler.createSingle(serverProxy, MainActivity.this) ;
			try {
				serverProxy.registerActivityProxy(mActivityStub);
			} catch (RemoteException e) {
				Toast.makeText(MainActivity.this, "启动、绑定、连接后台服务时出错：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		public void onServiceDisconnected(ComponentName className) {
			mServerProxyHandler.onServiceDisconnected(className) ;
		}
	};

	private static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) instance
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info= connectivityManager.getActiveNetworkInfo();
		if (info!= null
				&& info.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}


	private String getWifiIp(){
		WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		int port = wifiInfo.getNetworkId() ;
		if(ipAddress==0)return "";
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
				+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
	}

	public Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case Constant.msg_main_message_long: {  
            	//消息
               	Bundle b = msg.getData();
               	String message = b.getString(Constant.msg_key_string1) ;
               	if(message != null){
               		Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show() ;
               	}
            }
            case Constant.msg_main_message_short: {  
            	//消息
               	Bundle b = msg.getData();
               	String message = b.getString(Constant.msg_key_string1) ;
               	if(message != null){
               		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show() ;
               	}
            }
            break;  

            case Constant.msg_main_receiveSm: {
			}
            break;  
            default:  
                super.handleMessage(msg);  
            }  
        }  
    };
	public MyHandler handler = new MyHandler(this) ;
	/*private Runnable queryF1Task = new Runnable() {
		@Override
		public void run() {
			if (null != frgTool.f_1_0.httpGet) {
				frgTool.f_1_0.httpGet.cancel();
			}
			handler.removeCallbacks(queryF1Task);
			if (!frgTool.f_1_0.getCurrentIDIsempty()) {
				if (requestServeice && frgTool.f_1_0.isQuerySeverEnable) {
					//frgTool.f_1_0.doorContralServer(frgTool.f_1_0.currentID, "F1", "0");
				}else {
					if (tcpConnected) {
						frgTool.f_1_0.setCommand(0);
					}
				}
			}
			if (delay == 5) {
				postDelay5s() ;
			}else {
				postDelay30s() ;
			}

		}
	};*/

	private Runnable query50Task = new Runnable() {
		@Override
		public void run() {
			if (!frgTool.f_01_010.getRecieveWifiData()) {
				if (SharepreferenceUtils.getIsWifi(instance) && tcpConnected) {
					frgTool.f_01_010.queryCommand();
					handler.postDelayed(query50Task, 2000);
				}else {
					handler.removeCallbacks(query50Task);
				}
			}
		}
	};

	public void cancelQuery50() {
		handler.removeCallbacks(query50Task);
	}
	/*public void cancelQueryf1() {
		handler.removeCallbacks(queryF1Task);
	}*/

/*	private void postDelay5s() {
		handler.postDelayed(queryF1Task, 2000);
	}
	private void postDelay30s() {
		handler.postDelayed(queryF1Task, 30000);
	}*/
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StringValueForActivity.initOnlyOnce(this) ;
		instance = this ;
		Preferences.initInstance(this) ;
		this.broadcastReceiver = new MainBroadcastReceiver(this) ;
		this.broadcastReceiver.registerAndReceive() ;
		this.setContentView(R.layout.activity_main);
		this.createView();
		this.soundAlert = new SoundAlert(this) ;
		mIpPort = SharepreferenceUtils.getIpPort(this) ;
		LogUtils.e("当前服务IP端口", mIpPort);

        this.frgTool = new FragmentTool(this) ;
        this.mActivityStub = StubActivity.createSingle(this)  ;
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
        	wifiManager.setWifiEnabled(true);
        }
        this.bindService(new Intent(MainActivity.this, LocalServer.class), mConnection, Context.BIND_AUTO_CREATE);
		mJPush = new JPushActivity(this) ;
		mJPush.initJPush();//初始化极光推送
		mJPush.registerMessageReceiver();//注册信息接收器
		mJPush.setTag("admin1,admin2");//为设备设置标签
		mJPush.setAlias("doorlock");//为设备设置别名
		registerMessageReceiver();
		setTimer() ;
		//ToastUtils.show(instance, "网络是否为Wifi"+isWifi()+"");
	}

	private void setTimer(){
		task =new MyTimeTask(3000, new TimerTask() {
			@Override
			public void run() {
				if (isWifi()) {
					if (!SharepreferenceUtils.getIsWifi(instance)) {
						if (getWifiIp().contains("192.168.4")) ; {

						}
					}
				}
			}
		});
		StartTimer() ;
	}

	private void stopTimer(){
    	if (taskStatus) {
    		taskStatus = false ;
			task.stop();
		}
	}

	private void StartTimer(){
		if (!taskStatus) {
			taskStatus = true ;
			task.start();
		}
	}


/*	public void postQuery() {
		handler.removeCallbacks(queryF1Task);
        if (!frgTool.f_1_0.getCurrentIDIsempty()) {
            if (frgTool.f_1_0.doorNum == 1 || frgTool.f_1_0.clickDeviceId) {
				if (!frgTool.f_1_0.getCurrentIDIsempty()) {
					frgTool.f_1_0.doorContralServer(frgTool.f_1_0.currentID, "F1", "0","0");
				}
				if (delay == 5) {
					postDelay5s() ;
				}else {
					postDelay30s() ;
				}
            }
        }
	}*/

	public void connectWifiAndServer() {
		if (SharepreferenceUtils.getIsWifi(MainActivity.this)) {
			updateConnectedType(1);
			if (this.tcpConnected) {
				updateConnectedStatus(true);
				handler.removeCallbacks(query50Task);
				handler.postDelayed(query50Task, 2000);
			}else {
				updateConnectedStatus(false);
				LogUtils.e("Lucian--->wifi连接","--->开始连接");
				waitServerStartedAndToConnectNet("192.168.4.1", 60009) ; //wifi连接
				//waitServerStartedAndToConnectNet("10.10.100.254", 8899) ; //有人模块
			}
		}else {
			updateConnectedType(2);
			updateConnectedStatus(false);
			if (!frgTool.f_1_0.getCurrentIDIsempty()) {
				if (frgTool.f_1_0.doorNum == 1 || frgTool.f_1_0.clickDeviceId) {
					frgTool.f_1_0.startTimer();
				}
			}
		}
	}

	/**
	 * 等待后台服务启动后，生成了代理对象，进行连接网络
	 * @param url
	 * @param port
	 */
	private void waitServerStartedAndToConnectNet(final String url, final int port){
		mHandler.postDelayed(new Runnable(){
			public void run(){
				if(mServerProxyHandler == null){
					//递归循环
					waitServerStartedAndToConnectNet(url, port) ;
				}else{
					doToConnectNet(url, port) ;
				}
			}
		}, 500) ;
	}



	/**
	 * 后台服务已经启动，连接网络
	 * @param url
	 * @param port
	 */
	private void doToConnectNet(String url, int port){
		mServerProxyHandler.startAndConnectTcpServer(url, port) ;
		mHandler.postDelayed(new Runnable(){
			public void run(){
				if(!mServerProxyHandler.isTcpConnected()){
					if (SharepreferenceUtils.getIsWifi(MainActivity.this)) {
						Toast.makeText(MainActivity.this, "手机未与设备Wifi连接，请注意！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, StringValueForActivity.tcpConnectTimeout) ;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消广播接收注册
		broadcastReceiver.unRegisterBroadcastReceiver() ;
        unregisterReceiver(mMessageReceiver);
		//activity绑定了server，在activity退出前要取消绑定，
		//否则android系统会报错，然后android系统替你完成取消绑定
		if(mActivityStub != null){
			unbindService(mConnection);
		}

		ServerProxyHandler.getInstance().stopServer() ;
		frgTool.f_1_0.removeHandler();
		handler.removeCallbacksAndMessages(null);
		handler = null ;
		instance = null ;
		if (task != null) {
			task.stop();
		}
		finish();
	}

	public class MyHandler extends Handler {
		private final WeakReference<MainActivity> mActivty;
		private MyHandler(MainActivity mActivty) {
			this.mActivty = new WeakReference<MainActivity>(mActivty);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}

	/**
	 * 初始化ViewPager 
	 */
	private void createView() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		LayoutInflater mInflater = this.getLayoutInflater();
		scrollbar = (ImageView)findViewById(R.id.scrollbar) ;
		pageView_main = mInflater.inflate(R.layout.activity_main_function_page, null) ;
		pageView_second = mInflater.inflate(R.layout.activity_main_loopquery_page, null) ;
		pageView_fourth = mInflater.inflate(R.layout.activity_main_noprotocol_page, null) ;
		pageView_third = mInflater.inflate(R.layout.activity_main_channel_page, null) ;

		rtuAssiName = (TextView) findViewById(R.id.rtuAssiName) ;
		String message = SharepreferenceUtils.getMessage(MainActivity.this) ;
		scrollTextView = (AutoScrollTextView) findViewById(R.id.ScrollNotice) ;
		if (!"".equals(message)) {
			scrollTextView.setText(message);
		}
		scrollTextView.init(getWindowManager());
		scrollTextView.startScroll();

		listPages = new ArrayList<View>();
		listPages.add(pageView_main);
		listPages.add(pageView_second);
		listPages.add(pageView_third);
		listPages.add(pageView_fourth);

		tcpConnectStatus = (TextView) findViewById(R.id.tcpConnectStatus1);
		connectDoorId = (TextView) findViewById(R.id.tv_connectDoorId);
		tv_connectType = (TextView) findViewById(R.id.tv_connectType);
		tvProductID = (TextView) findViewById(R.id.productID);
		tvProductID.setText(getSoftVersion(instance));
		switchFun = (TextView) findViewById(R.id.switchFun) ;
		switchOnce = (TextView) findViewById(R.id.switchOnce) ;
		switchQuery = (TextView) findViewById(R.id.switchQuery) ;
		switchLog = (TextView) findViewById(R.id.switchLog) ;
		
		//获取滚动条的宽度
		bmpW = BitmapFactory.decodeResource(this.getResources(), R.drawable.scrollbar).getWidth();
		//为了获取屏幕宽度，新建一个DisplayMetrics对象
		DisplayMetrics displayMetrics = new DisplayMetrics() ;
		//将当前窗口的一些信息放在DisplayMetrics类中
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics) ;
		//得到屏幕的宽度
		int screenW = displayMetrics.widthPixels ;
		//计算出滚动条初始的偏移量
		offset = (screenW / 4 - bmpW) / 2 ;
		//计算出切换一个界面时，滚动条的位移量
		doubleRoll = offset * 2 + bmpW ;
		
		Matrix matrix = new Matrix() ;
		matrix.postTranslate(offset, 0) ;
		//将滚动条的初始位置设置成与左边界间隔一个offset
		scrollbar.setImageMatrix(matrix) ;
		
		mPager.setAdapter(new ViewPagerAdapter(listPages));//绑定适配器
		mPager.setCurrentItem(0);//设置viewPager的初始界面
		defaultAnimation(0) ;
		mPager.setOnPageChangeListener(new FlipPageChangeListener());//切换界面监听
		
		switchQuery.setOnClickListener(new clickLableListener()) ; 
		switchOnce.setOnClickListener(new clickLableListener()) ; 
		switchFun.setOnClickListener(new clickLableListener()) ;
		switchLog.setOnClickListener(new clickLableListener()) ;
		
		//////////////////////////////////////////////////////////////
		//功能子页
	func_scrollView = (ScrollView) pageView_main.findViewById(R.id.f_func_scrollView) ;
		chLine_04 = (LinearLayout) pageView_third.findViewById(R.id.chLine_04) ;
		
		chLine_04.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				pageView_channel_OnLongClick() ;
				return false;
			}
		}) ;

        ////////////////////////////////////////////////////////
        //帮助类
        //实例化fragment回调类对象
        this.frgCallback = new FragmentCallback(this) ;
        //实例化主界面设置帮助类对象
        this.mainHelp = new MainActivityHelp(this, pageView_fourth, pageView_second, pageView_main, pageView_third) ;
        //this.mainHelp = new MainActivityHelp(this, pageView_fourth, pageView_second, pageView_main) ;
        //执行初始设置
        this.mainHelp.onCreateView() ;
	}

	
	/**
	 * 最小面部分界面最大小化，其他部分界面隐藏
	 */
	public void pageView_channel_OnLongClick(){
			frgTool.fragment_ch04.setRtuDatasListViewHeight(ResourceUtils.getXmlDef(this, R.dimen.ch_rtuDataListViewHeight_big)) ;
	}
	
	/**
	 * 响铃
	 * @return
	 */
	public SoundAlert getSoundAlert() {
		return soundAlert;
	}

	public void setDoorId(String id) {
		connectDoorId.setText(id);
	}
	
	/**
	 * 得到fragment的工具类对象
	 * @return
	 */
	public FragmentTool getFrgTool() {
		return frgTool;
	}
	
	/**
	 * 得到fragment的回调工具类对象
	 * @return
	 */
	public MainActivityHelp getMainSetHelp() {
		return mainHelp;
	}
	
	/**
	 * 得到fragment的回调工具类对象
	 * @return
	 */
	public FragmentCallback getFrgCallback() {
		return frgCallback;
	}
	
	/**
	 * 得到Activity的桩
	 * @return
	 */
	public StubActivity getActivityStub(){
		return this.mActivityStub ;
	}
	/**
	 * 得到后台服务代理持有者，当没有启动后台服务时，返回空
	 * @return
	 */
	public ServerProxyHandler getServerProxyHandler(){
		return ServerProxyHandler.getInstance() ;
	}
	
	/**
	 * 设置网络连接状态
	 * @param isConnected
	 */
	public void setWifiConnectedStatus(boolean isConnected) {
	    if (this.tcpConnected != isConnected) {
            if (SharepreferenceUtils.getIsWifi(MainActivity.this)) {
                updateConnectedType(1);
                this.tcpConnected = isConnected;
                if (this.tcpConnected) {
                    //网络已经连接
					frgTool.f_1_0.isFirst = true ;
                    LogUtils.e("wifi连接通知", "已连接");
                    updateConnectedStatus(true);
					handler.removeCallbacks(query50Task);
                    handler.postDelayed(query50Task, 2000);
                } else {
                    frgTool.f_1_0.setBtnIsEnable(false);
                    updateConnectedStatus(false);
                }
            }
        }
	}

	public void updateConnectedType(int data) {
		if (data == 1) {
			tv_connectType.setText("Wifi连接状态:");
		}else {
			tv_connectType.setText("服务器连接状态:");
		}
	}

	public void updateConnectedStatus(boolean isConnect) {
		if (isConnect) {
			tcpConnectStatus.setText(this.getResources().getString(R.string.connected));
			tcpConnectStatus.setTextColor(Color.RED);
		}else {
			tcpConnectStatus.setText(this.getResources().getString(R.string.noConnected));
			tcpConnectStatus.setTextColor(Color.parseColor("#f0eff5"));
		}
	}

	
	/**
	 * ViewPager适配器
	 */
	public class ViewPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public ViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	public class clickLableListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.switchQuery:
				mPager.setCurrentItem(0);//设置viewPager的初始界面
				defaultAnimation(0) ;
				break ;
			case R.id.switchOnce:
				mPager.setCurrentItem(1) ;
				defaultAnimation(1) ;
				break ;
			case R.id.switchFun:
				mPager.setCurrentItem(2) ;
				defaultAnimation(2) ;
				break ;
			case R.id.switchLog:
				mPager.setCurrentItem(3) ;
				defaultAnimation(3) ;
				break ;
			default :
				mPager.setCurrentItem(2) ;
				defaultAnimation(2) ;
				break ;
			}
		}
		
	}

	/**
	 *  页卡切换监听
	 *  横隔栏结构如下
	 *  offset bmpW offset offset bmpW offset offset bmpW offset 
	 */
	public class FlipPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			MainActivity.this.frgTool.pageViewSelected(arg0) ;
			   Animation animation = null;
	            switch (arg0) {
	                case 0:
	                        /**
	                         * TranslateAnimation的四个属性分别为
	                         * float fromXDelta 动画开始的点离当前View X坐标上的差值 
	                         * float toXDelta 动画结束的点离当前View X坐标上的差值 
	                         * float fromYDelta 动画开始的点离当前View Y坐标上的差值 
	                         * float toYDelta 动画开始的点离当前View Y坐标上的差值
	                        **/
	                        animation = new TranslateAnimation(doubleRoll, 0, 0, 0);
	                        switchQuery.setTextColor(Color.parseColor("#4c79c5")) ;
	            			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
	            			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
	            			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
	                    break;
	                case 1:
	                        animation = new TranslateAnimation(doubleRoll + offset, doubleRoll, 0, 0);
	                        switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
	            			switchOnce.setTextColor(Color.parseColor("#4c79c5")) ;
	            			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
	            			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
	                    break;
	                case 2:
                        animation = new TranslateAnimation(2*doubleRoll + offset, 2*doubleRoll, 0, 0);
                        switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
            			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
            			switchFun.setTextColor(Color.parseColor("#4c79c5")) ;
            			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
                    break;
	                case 3:
                        animation = new TranslateAnimation(3*doubleRoll + offset, 3*doubleRoll, 0, 0);
                        switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
            			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
            			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
            			switchLog.setTextColor(Color.parseColor("#4c79c5")) ;
                    break;
	            }
	          //arg0为切换到的页的编码
	            currIndex = arg0;
	            // 将此属性设置为true可以使得图片停在动画结束时的位置
	            animation.setFillAfter(true);
	            //动画持续时间，单位为毫秒
	            animation.setDuration(200);
	            //滚动条开始动画
	            scrollbar.startAnimation(animation);
			
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	public void defaultAnimation(int arg0) {
		Animation animation = null ;
		switch(arg0) {
		case 0:
			animation = new TranslateAnimation(doubleRoll , 0 , 0 ,0) ;
			switchQuery.setTextColor(Color.parseColor("#4c79c5")) ;
			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
			break ;
		case 1:
			animation = new TranslateAnimation(doubleRoll + offset, doubleRoll , 0 , 0) ;
			switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchOnce.setTextColor(Color.parseColor("#4c79c5")) ;
			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
			
			break ;
		case 2:
			animation = new TranslateAnimation(2 * doubleRoll + offset, 2 * doubleRoll, 0, 0) ;
			switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
			switchFun.setTextColor(Color.parseColor("#4c79c5")) ;
			switchLog.setTextColor(Color.parseColor("#b2b2b2")) ;
			break ;
		 case 3:
             animation = new TranslateAnimation(3*doubleRoll + offset, 3*doubleRoll, 0, 0);
             switchQuery.setTextColor(Color.parseColor("#b2b2b2")) ;
 			switchOnce.setTextColor(Color.parseColor("#b2b2b2")) ;
 			switchFun.setTextColor(Color.parseColor("#b2b2b2")) ;
 			switchLog.setTextColor(Color.parseColor("#4c79c5")) ;
         break;
		}
		currIndex = arg0 ;
		animation.setFillAfter(true) ;
		animation.setDuration(200) ;
		scrollbar.startAnimation(animation) ;
	}
	//得到软件版本信息
	public String getSoftVersion(Activity at){
		PackageManager pm = at.getPackageManager();
		String pkName = this.getPackageName() ;
		try {
			PackageInfo pi = pm.getPackageInfo(pkName, 0);
			return pi.versionName ;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "" ;
	}

}