package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F1.Data_F1;
import com.blg.rtu.protocol.p206.F2.Data_F2;
import com.blg.rtu.protocol.p206.F3.Data_F3;
import com.blg.rtu.server.net.NetManager;
import com.blg.rtu.util.DialogAlarm;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.MyTimeTask;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.util.Util;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.DataTranslateUtils;
import com.blg.rtu3.utils.LogUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;



public class F_1_0 extends FrmParent implements AddPopWindow.Choice{

	private Spinner spinner2;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;
	private ArrayAdapter<SpinnerVO> spinnerAdapter2;
	private TextView tv_jiaquan ;
	private TextView tv_openValue ;
	private TextView tv_open ;
	private TextView tv_close ;
	private TextView tv_stop ;
	private TextView tv_door_status ;
	private ProgressBar pb_open ;
	private ProgressBar pb_close ;
	private ProgressBar pb_stop ;

	private PieChartView pieChart;
	private PieChartData pieChardata;
	List<SliceValue> values = new ArrayList<SliceValue>();
	private int[] data = {135,0,180,45};
	private int[] colors = {Color.parseColor("#ffffff"),Color.parseColor("#FF4040"),Color.parseColor("#CDC9C9"),Color.parseColor("#ffffff")};

	private ImageView imgDoorPower ;
	private ImageView imgDoorAlarm ;

	private DoorStatus doorStatus ; //门状态
	public String currentID = "" ; //当前门ID
	public String currentPassword = "" ; //当前门ID
	public Callback.Cancelable httpGet ;  //网络请求
	public Callback.Cancelable httpGet1 ;  //网络请求
	public String currentCom = "0" ; //当前命令
	private String currentAfn = "" ; //当前功能码
	public boolean isFirst = true ; //是否初始请求
	public  int  doorNum = 0; //APP存储门ID数量
	private boolean receiveOpenClose = false ;
	public boolean clickDeviceId = false ; //判断是否手动点击设备ID
	private boolean endReqFlag = true ; //结束数据请求
	private int receiveStopNum = 0 ; //接收门停止计数
	public boolean isQuerySeverEnable = true ;
	public int wifiServer = 0 ;
	private int openCloseStop = 0 ;
	private boolean onceComReceiveTrue  = false;
	private boolean deviceNetStatus = false ;

	private TextView tv_doorList ;
	private AddPopWindow popWindow;
	private List<String> doorList = new ArrayList<String>() ;
	private List<String> passwordList = new ArrayList<String>() ;
	private int positionId = 0 ;
	private MyTimeTask task ;
	private boolean taskStatus = false ;
	private DialogConfirm dialogConfirm ;
	private boolean isShowing = false ;
	private boolean enableOnlinequery = true ;
	private HashMap<String , String> passErrorHashMap = new HashMap<String, String>() ;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = null ;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cntFrmOpened = false ;
		loading = false ;
		//setTimer() ;
	}
	private void setTimer(){
		task =new MyTimeTask(2000, new TimerTask() {
			@Override
			public void run() {
				LogUtils.e("Lucian--->timer", "查询类型--->" + (SharepreferenceUtils.getIsWifi(act)? "WIFI":"服务"));
				if (!SharepreferenceUtils.getIsWifi(act)) {
					if (!getCurrentIDIsempty()) {
						if (isQuerySeverEnable) {
							if (!"".equals(currentID)) {
								queryServerStatus(currentID);
								doorContralServer(currentID, "F1", "0", "0");
							}
						}
					}
				}else {
					if (act.tcpConnected) {
						currentCom = "0" ;
						setCommand(0);
					}
				}
			}
		});
	}
	public void stopTimer(){
		LogUtils.e("Lucian--->","停止定时器");
		if (taskStatus) {
			taskStatus = false;
			task.stop();
		}
	}
	public void startTimer(){
		if (!taskStatus) {
            LogUtils.e("Lucian--->","开始定时器");
			setTimer() ;
			taskStatus = true;
			task.start();
		}else {
			stopTimer() ;
            LogUtils.e("Lucian--->","开始定时器");
			setTimer() ;
			taskStatus = true;
			task.start();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_1_00, container, false);
		tv_doorList = (TextView) view.findViewById(R.id.tv_doorList) ;
		popWindow = new AddPopWindow(getActivity(), doorList);
		popWindow.setChoice(this);
		/*SharepreferenceUtils.saveHasLearn(act, true);
		SharepreferenceUtils.saveDeviceId(act,"0102030406-0102030407-0102030408-0102030409");
		SharepreferenceUtils.savePassword(act,"2a23-5348-0102-0102");*/
		updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
		tv_doorList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popWindow.showPopupWindow(tv_doorList);
			}
		});
		tv_doorList.setEnabled(false);
		spinner2 = (Spinner)view.findViewById(R.id.spinner_communication);
		spinnerAdapter2 = new ArrayAdapter<SpinnerVO>(this.act, R.layout.spinner_style, new ArrayList<SpinnerVO>());
		spinnerAdapter2.setDropDownViewResource(R.layout.spinner_item);
		this.putSpinnerValue2();
		spinner2.setAdapter(spinnerAdapter2);
		spinner2.setOnItemSelectedListener(new SpinnerSelectedListener2());

		tv_jiaquan = (TextView) view.findViewById(R.id.tv_jiaquan) ;
		tv_jiaquan.setText("---");

		tv_open = (TextView) view.findViewById(R.id.tv_open) ;
		tv_openValue = (TextView) view.findViewById(R.id.tv_openValue) ;
		tv_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.checkIsHasLearned(act)) {
					stopTimer();
					endReqFlag = true ;
					receiveStopNum = 0 ;
					setProgressVisible(1);
					setBtnBackground(1, 2);
					setBtnBackground(2, 0);
					setBtnBackground(3, 1);
					currentCom = "1";
					currentAfn = "F1";
					receiveOpenClose = false ;
					openCloseStop = 0 ;
					onceComReceiveTrue = false ;
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(1);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						} else {
							isQuerySeverEnable = false ;
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
					endReqFlag = false ;
					onceComCheckIsReceive() ;
					handler.removeCallbacks(operatorTimeOverResetStatus);
					handler.postDelayed(operatorTimeOverResetStatus, 30000) ;
				}
			}
		});
		pb_open = (ProgressBar) view.findViewById(R.id.pb_open);
		tv_door_status = (TextView) view.findViewById(R.id.tv_door_status) ;
		tv_close = (TextView) view.findViewById(R.id.tv_close) ;
		tv_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.checkIsHasLearned(act)) {
					stopTimer();
					receiveStopNum = 0 ;
					setProgressVisible(2);
					setBtnBackground(1, 0);
					setBtnBackground(2, 2); //
					setBtnBackground(3, 1);
					currentCom = "2";
					currentAfn = "F1";
					receiveOpenClose = false ;
					endReqFlag = true ;
					openCloseStop = 0 ;
					onceComReceiveTrue = false ;
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(2);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						} else {
							isQuerySeverEnable = false ;
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
					endReqFlag = false ;
					onceComCheckIsReceive() ;
					handler.removeCallbacks(operatorTimeOverResetStatus);
					handler.postDelayed(operatorTimeOverResetStatus, 30000) ;
				}
			}
		});
		pb_close = (ProgressBar) view.findViewById(R.id.pb_close);
		tv_stop = (TextView) view.findViewById(R.id.tv_stop) ;
		tv_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.checkIsHasLearned(act)) {
					endReqFlag = true ;
					stopTimer();
					receiveStopNum = 0 ;
					setProgressVisible(3);
					setBtnBackground(3, 2);
					setBtnBackground(1,1); //复位
					setBtnBackground(2,1);//复位
					if (currentCom.equals("1") || currentCom.equals("2")) {
						receiveOpenClose = false;
					}
					currentCom = "3";
					currentAfn = "F1";
					openCloseStop = 0 ;
					onceComReceiveTrue = false ;
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(3);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						} else {
							handler.removeCallbacksAndMessages(null);
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
					endReqFlag = false ;
					onceComCheckIsReceive() ;
					handler.removeCallbacks(operatorTimeOverResetStatus);
					handler.postDelayed(operatorTimeOverResetStatus, 10000) ;
				}
			}
		});
		pb_stop= (ProgressBar) view.findViewById(R.id.pb_stop);
		pieChart = (PieChartView) view.findViewById(R.id.pie_chart);
		pieChart.setOnValueTouchListener(selectListener);//设置点击事件监听
		imgDoorPower = (ImageView) view.findViewById(R.id.img_door_power) ;
		imgDoorAlarm = (ImageView) view.findViewById(R.id.img_door_alarm) ;
		setPieChartData();
		initPieChart();
		return view ;
	}
	@Override
	public void senddata(String msg) {
		positionId = Integer.parseInt(msg) ;
		if (!SharepreferenceUtils.getIsWifi(act) && wifiServer == 1) {
			deviceNetStatus = false ;
			if (doorList.size() > 0) {
				num++ ;
				LogUtils.e("点击item次数", num + "");
				if (num > 0) {
					clickDeviceId = true;
					if (!SharepreferenceUtils.getIsWifi(act) ) {
						startTimer();
					}
					isFirst = true ;
					currentID = doorList.get(positionId);
					currentPassword = passwordList.get(positionId) ;
					act.frgTool.f_1_2.setCurrentPosition(positionId);
					act.frgTool.f_1_2.setCurrentID(currentID);
					act.delay = 5 ;
					act.updateConnectedStatus(false);
					LogUtils.e("选择的门锁地址", currentID);
                    tv_doorList.setText(currentID);
					initDeviceConnect() ;
					//deviceNetStatus = false ;
					act.setDoorId(currentID);
				}
			}
		}else {
			ToastUtils.show(act, "请注意：当前通信类型为WIFI");
		}
	}
	@Override
	public void longClick(final int position) {
		new DialogConfirm().showDialog(act,
				act.getResources().getString(R.string.deleteId) ,
				new DialogConfirm.CallBackInterface(){
					@Override
					public void dialogCallBack(Object o) {
						if((Boolean)o){
							if (positionId == position) {
								initStatus() ;
							}
							if (!passErrorHashMap.isEmpty()) {
								if (passErrorHashMap.containsKey(doorList.get(position))) {
									passErrorHashMap.remove(doorList.get(position)) ;
								}
							}
							String deviceID = SharepreferenceUtils.getDeviceId(act) ;
							String password = SharepreferenceUtils.getPassword(act) ;
							LogUtils.e("设备列表", deviceID);
							LogUtils.e("密码列表", password);
							String[] listId = deviceID.split("-");
							String[] listPassword = password.split("-");
							doorList.remove(position) ;
							int postion1 = listId.length -position -1 ;
							LogUtils.e("position", postion1 + "");
							if (doorList.size() > 0) {
								String ids = "";
								String pws = "";
								for (int j = 0; j < listId.length; j++) {
									if (j == 0) {
										if (j != postion1) {
											ids = listId[0];
											pws = listPassword[0];
										}else {
											ToastUtils.show(act, "删除门地址：" + listId[postion1]);
										}
									} else {
										if (j != postion1) {
											if (ids.equals("")) {
												ids = listId[j];
												pws = listPassword[j];
											}else {
												ids = ids + "-" + listId[j];
												pws = pws + "-" + listPassword[j];
											}
										}else {
											ToastUtils.show(act, "删除门地址：" + listId[postion1]);
										}
									}
								}
								LogUtils.e("设备列表", ids);
								LogUtils.e("密码列表", pws);
								SharepreferenceUtils.saveDeviceId(act, ids);
								SharepreferenceUtils.savePassword(act, pws);
							}else {
								SharepreferenceUtils.saveDeviceId(act, "");
								SharepreferenceUtils.savePassword(act, "");
							}
							updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
							act.frgTool.f_1_2.putSpinnerValue1();
						}else{
						}
					}
				}) ;
	}

	private void initStatus() {
		currentID = "" ;
		stopTimer();
		act.setDoorId("---");
		isFirst = true ;
		clickDeviceId = false;
		tv_doorList.setHint("请选择门地址");
        deviceNetStatus = false ;
		currentPassword = "0000" ;
		positionId = 0 ;
		receiveOpenClose = false;
		currentCom = "0";
		setBtnIsEnable(false);
		endReqFlag = true;
		isQuerySeverEnable = true ;
		handler.removeCallbacksAndMessages(null);
		act.updateConnectedStatus(false);

		setDoorDit(0);
		tv_door_status.setText("停");
		tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_bg));
		tv_jiaquan.setText("---");
		setBtnBackground(0, 0);

	}
	public void removeHandler() {
		handler.removeCallbacksAndMessages(null);
		handler = null ;
	}
	public MyHandler handler = new MyHandler(act) ;
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
	private Runnable operatorTimeOverResetStatus =  new Runnable() {
		@Override
		public void run() {
			ToastUtils.show(act, "设备回复超时，请再次操作!");
			setProgressVisible(0) ;
			receiveOpenClose = false;
			currentCom = "0";
			setBtnIsEnable(true);
			endReqFlag = true;
			isQuerySeverEnable = true ;
			startTimer();//超时开始普通查询
		}
	};

	private void onceComCheckIsReceive() {
		onceComReceiveTrue = false ;
		handler.removeCallbacks(queryF1OnceTask);
		if (SharepreferenceUtils.getIsWifi(act)) {
			handler.postDelayed(queryF1OnceTask, 1500) ;
		}else {
			handler.postDelayed(queryF1OnceTask, 2500) ;
		}

	}

	private Runnable queryF1OnceTask = new Runnable() {
		@Override
		public void run() {
			if (!onceComReceiveTrue) {
				if (SharepreferenceUtils.getIsWifi(act)) {
					if (act.tcpConnected) {
						if ((currentCom.equals("2"))) {
							setCommand(2);
						} else if ((currentCom.equals("1"))) {
							setCommand(1);
						} else if (currentCom.equals("3")) {
							setCommand(3);
						}else {
							setCommand(0);
						}
					}
				}else {
					doorContralServer(currentID, currentAfn, currentCom,"1");
				}
				onceComCheckIsReceive() ;
			}else {
				handler.removeCallbacks(queryF1OnceTask);
			}
		}
	};

	private Runnable onceServerReq = new Runnable() {
		@Override
		public void run() {
			doorContralServer(currentID, currentAfn, currentCom, "1");
		}
	};

	private void queryF1Once() {
		if (SharepreferenceUtils.getIsWifi(act)) {
			currentCom = "0" ;
			setCommand(0);
		}else {
			handler.postDelayed(onceServerReq, 500) ;
		}
		onceComCheckIsReceive() ;
	}

	public boolean getCurrentIDIsempty() {
		if (doorList.size() == 0) {
			return true ;
		}else {
			return false ;
		}
	}

	public int getCurrentPosition() {
		if (doorList.size() == 0) {
			return -1 ;
		}else {
			return positionId ;
		}
	}

	public void setCurrentPosition(int position) {
		if (doorList.size() != 0) {
			this.positionId = position ;
			currentPassword = passwordList.get(position) ;
			tv_doorList.setText(doorList.get(position));
		}
	}

	public   void doorContralServer(final String dtuId, final String code, final String flag, String tp){//请求参数个数不确定，可变长参数,可变长参数放在最后一个
		try {

			String url = act.mIpPort + "/door/door/state.act?";
			final com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
			params.addBodyParameter("dtuId",dtuId);
			params.addBodyParameter("tp",tp);
			params.addBodyParameter("code",code);
			params.addBodyParameter("flag",flag);
			params.addBodyParameter("password",currentPassword);
			final HttpUtils http = new HttpUtils();
			http.configCurrentHttpCacheExpiry(1000 * 5);
			LogUtils.e("-->门控制服务", url +"dtuId="+
                    dtuId+"&code=" +code+"&tp="+tp+"&flag="+flag+"&password="+currentPassword);
			LogUtils.e("--->请求开始时间", Util.getCurrentTime());

			http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack() {
				@Override
				public void onStart() {
					//LogUtils.e("---->服务请求","开始---->"+Util.getCurrentTime());
					if (!flag.equals("0")) {
						//ToastUtils.show(act, Util.getCurrentTime());
					}
				}

				@Override
				public void onLoading(long total, long current,
									  boolean isUploading) {
					//LogUtils.e("----->服务请求","加载"+Util.getCurrentTime());
				}
				@Override
				public void onSuccess(ResponseInfo arg0) {
					LogUtils.e("服务请求","成功"+Util.getCurrentTime());
					onceComReceiveTrue = true ;
					setProgressVisible(0) ;


					JSONObject jsonResult = null;
					try {
						jsonResult = new JSONObject(arg0.result.toString());
						String returnDtuId = jsonResult.getString("dtuId");
						if (null == returnDtuId || "null".equals(returnDtuId) || "".equals(returnDtuId)) {
							//ToastUtils.show(act, "产品ID为空，数据未知!");
						} else {
							if (currentID.equals(returnDtuId)) {
								String code = jsonResult.getString("succ");
								if (code.equals("1")) {
									Gson gson = new Gson();
									String data = jsonResult.getString("rltState");
									doorStatus = gson.fromJson(data, DoorStatus.class);
									if (null != doorStatus) {
										if (isFirst) {
											isFirst = false ;
										/*	setBtnIsEnable(true);
											setBtnBackground(4,0);
											startTimer();*/
										}else {
										}
										if (deviceNetStatus) {
											act.updateConnectedStatus(true);
											displayServiceData(doorStatus);
											pintServiceData(doorStatus);
											if (!endReqFlag) {
												queryF1Once();
											}
										}
									} else {
										//ToastUtils.show(act, "服务获取数据为空！");
									}
								} else {
									String msg = jsonResult.getString("error");
									if (msg.contains("设备尚未上线，命令发送失败！")) {
										ToastUtils.show(act, "服务获取数据失败：" + "门锁设备未上线！");
										act.updateConnectedStatus(false);
										//act.second30 = minute10 ; //设备未上线，10分钟后再试
									} else if (msg.contains("超时")) {
										ToastUtils.show(act, "服务获取数据失败：" + "门锁设备回复数据超时！");
										//act.second30 = minute2; //设备回复超时，2分钟后再试
									}else if (msg.contains("重新学习")){
										LogUtils.e("重新学习", returnDtuId);
										if (!passErrorHashMap.containsKey(returnDtuId)) {
											LogUtils.e("放入键值对", returnDtuId);
											passErrorHashMap.put(returnDtuId,"1") ;
										}
										if (httpGet != null) {
											httpGet.cancel();
										}
										tv_doorList.setText("请选择门地址");
										initStatus() ;
										showAlarm() ;
									}
								}
							} else {
								//ToastUtils.show(act, "服务获取数据返回地址与请求地址不一致!");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String arg1) {
					onceComReceiveTrue = true ;
					setProgressVisible(0) ;
					if (arg0.getMessage().contains("failed to connect to")) {
						act.updateConnectedStatus(false);
					}
					LogUtils.e("onError", "请求失败"+arg0.getMessage());
				}
			});
		} catch (Exception e) {
			String msg = null;
			if (e instanceof InvocationTargetException) {
				Throwable targetEx = ((InvocationTargetException) e)
						.getTargetException();
				if (targetEx != null) {
					msg = targetEx.getMessage();
					LogUtils.e("onError", "请求失败"+msg) ;
				}
			} else {
				msg = e.getMessage();
				LogUtils.e("onError", "请求失败"+msg) ;
			}
			setProgressVisible(0) ;
			e.printStackTrace();
		}
	}

	private void showAlarm() {
		if (!isShowing) {
			isShowing = true ;
			if (null == dialogConfirm) {
				dialogConfirm = new DialogConfirm() ;
				dialogConfirm.showDialog(act,
						act.getResources().getString(R.string.passwordError),
						new DialogConfirm.CallBackInterface() {
							@Override
							public void dialogCallBack(Object o) {
								if ((Boolean) o) {
									isShowing = false ;
								} else {
									isShowing = false ;
								}
							}
						});
			}else {
				dialogConfirm.showDialog(act,
						act.getResources().getString(R.string.passwordError),
						new DialogConfirm.CallBackInterface() {
							@Override
							public void dialogCallBack(Object o) {
								if ((Boolean) o) {
									isShowing = false ;
								} else {
									isShowing = false ;
								}
							}
						});
			}
		}
	}

	public void queryServerStatus(final String dtuId) {
		String url =act.mIpPort +  "/door/door/online.act" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		LogUtils.e("---->查询当前门是否在线", requestParams.toString());
		LogUtils.e("---->请求开始时间", Util.getCurrentTime());
		httpGet = x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if (result.equals("1")) {
					LogUtils.e("----->接收到服务器返回数据", "-----> 在线" );
					act.requestServeice = true;
					if (!deviceNetStatus && !passErrorHashMap.containsKey(dtuId)) {
						LogUtils.e("是否包含ID", dtuId + !passErrorHashMap.containsKey(dtuId));
						startTimer();
						deviceNetStatus = true;
						setBtnIsEnable(true);
						setBtnBackground(4, 0);
						act.updateConnectedStatus(true);
					}
				}else {
					LogUtils.e("----->接收到服务器返回数据", "-----> 不在线" );
					act.requestServeice = false ;
					if (deviceNetStatus) {
							deviceNetStatus = false;
							setBtnIsEnable(false);
							setDoorDit(0);
							tv_door_status.setText("停");
							tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_bg));
							tv_jiaquan.setText("---");
							setBtnBackground(0, 0);
							act.updateConnectedStatus(false);
					}
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

	private void setBtnBackground(int position, int status) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (position == 1) {
			if (status == 1) {
				tv_open.setEnabled(true);
				tv_open.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_open.setEnabled(false);
				tv_open.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_open.setEnabled(false);
				tv_open.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 2) {
			if (status == 1) {
				tv_close.setEnabled(true);
				tv_close.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_close.setEnabled(false);
				tv_close.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_close.setEnabled(false);
				tv_close.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 3) {
			if (status == 1) {
				tv_stop.setEnabled(true);
				tv_stop.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_stop.setEnabled(false);
				tv_stop.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_stop.setEnabled(false);
				tv_stop.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 4) {
			setProgressVisible(0) ;
			tv_open.setEnabled(true);
			tv_close.setEnabled(true);
			tv_stop.setEnabled(true);
			tv_open.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_close.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_stop.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
		}else {
			setProgressVisible(0) ;
			tv_open.setEnabled(false);
			tv_close.setEnabled(false);
			tv_stop.setEnabled(false);
			tv_open.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_close.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_stop.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
		}
	}

	private void setProgressVisible(int position){
		if (position == 1) {
			pb_open.setVisibility(View.VISIBLE);
			pb_close.setVisibility(View.INVISIBLE);
			pb_stop.setVisibility(View.INVISIBLE);
		}else if (position == 2) {
			pb_open.setVisibility(View.INVISIBLE);
			pb_close.setVisibility(View.VISIBLE);
			pb_stop.setVisibility(View.INVISIBLE);
		}else if (position == 3) {
			pb_open.setVisibility(View.INVISIBLE);
			pb_close.setVisibility(View.INVISIBLE);
			pb_stop.setVisibility(View.VISIBLE);
		}else {
			pb_open.setVisibility(View.INVISIBLE);
			pb_close.setVisibility(View.INVISIBLE);
			pb_stop.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 监听事件
	 */
	private PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {
		@Override
		public void onValueDeselected() {
		}
		@Override
		public void onValueSelected(int arg0, SliceValue value) {
		}
	};

	/**
	 * 获取数据
	 */
	private void setPieChartData(){
		values.clear();
		setOpenCloseValue(data[1]);
		for (int i = 0; i < data.length; ++i) {
			SliceValue sliceValue = new SliceValue((float) data[i], colors[i]);//这里的颜色是我写了一个工具类 是随机选择颜色的
			values.add(sliceValue);
		}
	}

	private void setOpenCloseValue(int value) {
		tv_openValue.setText(value+"度");
	}

	/**
	 * 初始化
	 */
	private void initPieChart() {
		if (!isAdded()) {
			this.onAttach(act);
		}
		pieChardata = new PieChartData();
		pieChardata.setHasLabels(false);//显示表情
		pieChardata.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
		pieChardata.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
		pieChardata.setHasCenterCircle(false);//是否是环形显示
		pieChardata.setValues(values);//填充数据
		pieChardata.setCenterCircleScale(1f);//设置环形的大小级别
		pieChardata.setSlicesSpacing(0);//设置间隔为0

		pieChart.setPieChartData(pieChardata);
		pieChart.setValueSelectionEnabled(false);//设置是否选中值
		pieChart.setAlpha(1f);//设置透明度
		pieChart.setCircleFillRatio(1f);//设置饼图大小
		pieChart.setViewportCalculationEnabled(true);//设置饼图自动适应大小
		pieChart.setChartRotationEnabled(false);

	}

	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			//spinnerAdapter1.clear();
			String[] arr = data.split("-") ;
			doorList.clear();
			if (arr.length >= 1) {
				doorNum = arr.length ;
				for (int i = 0; i < arr.length; i++) {
					doorList.add(arr[arr.length -i-1]) ;
				}
			}
			popWindow = new AddPopWindow(getActivity(), doorList);
			popWindow.setChoice(this);
            String passwordStr = SharepreferenceUtils.getPassword(act) ;
            String[] arrPw = passwordStr.split("-") ;
            passwordList.clear();
            if (arrPw.length >= 1) {
                for (int i = 0; i < arrPw.length; i++) {
                    passwordList.add(arrPw[arrPw.length -i-1]) ;
                }
            }
		}else {
		}

	}

	private void putSpinnerValue2(){
		spinnerAdapter2.add(new SpinnerVO("0", "请选择")) ;
		spinnerAdapter2.add(new SpinnerVO("1", "服务通信")) ;
		spinnerAdapter2.add(new SpinnerVO("2", "Wifi通信")) ;
	}

	public void setCurrentID(String s) {
		this.currentID = s ;
	}
	private long num =0;
	public void initDeviceConnect() {
		setBtnBackground(0,0); //初始化按钮状态灰色，不使能
		setDoorButtonImg(3);
		setPieChart(0);
		tv_jiaquan.setText("---");
	}

	private class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner2.getId()){
				/*fragment_04.setRtuData(new DoorStatus(),null);*/
				if (position == 1) {//服务器
					tv_doorList.setText("请选择门地址");
					wifiServer = 1 ;
					initStatus();
					act.requestServeice = true ;
					if (act.tcpConnected) {
						NetManager.getInstance().toggleConnectRemote(false);
						act.tcpConnected = false;
					}
					isFirst = true;
					SharepreferenceUtils.saveIsWifi(act, false);
					if (getCurrentIDIsempty()) {
						spinner2.setSelection(2);
						new DialogAlarm().showDialog(act, "门设备地址为空，请先学习！\n学习步骤：\n1、手机Wifi连接到门热点\n2、APP通信类型选择Wifi通信\n3、连接到Wifi后到<副页面3>进行门学习!");
					}else {
						act.connectWifiAndServer();
					}
					tv_doorList.setEnabled(true);
					isQuerySeverEnable = true ;
				}else if (position == 2){//wifi
					initStatus();
					if (act.tcpConnected) {
						act.tcpConnected = false ;
						NetManager.getInstance().toggleConnectRemote(false);
					}
					LogUtils.e("门地址为空","重新选择wifi通道");
					wifiServer = 2 ;
					act.requestServeice = false ;
					isFirst = true ;
					act.frgTool.f_01_010.setReceiveWifiData(false);
					SharepreferenceUtils.saveIsWifi(act, true);
					tv_doorList.setEnabled(false);
					act.connectWifiAndServer() ;
					isQuerySeverEnable = false ;
				}else {
					initStatus();
					if (SharepreferenceUtils.getIsWifi(act)) {
						wifiServer = 0 ;
						initStatus();
						if (act.tcpConnected) {
							NetManager.getInstance().toggleConnectRemote(false);
							act.tcpConnected = false;
						}
						tv_doorList.setEnabled(false);
						isQuerySeverEnable = false ;
					}else {
						tv_doorList.setText("请选择门地址");
						initStatus();
						if (act.tcpConnected) {
							act.tcpConnected = false ;
							NetManager.getInstance().toggleConnectRemote(false);
						}
						wifiServer = 0 ;
						act.requestServeice = false ;
						act.frgTool.f_01_010.setReceiveWifiData(false);
						act.requestServeice = false ;
						tv_doorList.setEnabled(false);
						isQuerySeverEnable = false ;
					}
				}
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void afterChangeWifiNameSuccess() {
		setBtnBackground(0,0);
		setBtnIsEnable(false) ;
		act.frgTool.f_01_010.setReceiveWifiData(false);
		act.tcpConnected = false ;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				act.connectWifiAndServer() ;
			}
		}, 2000) ;
	}

	public void pintServiceData(DoorStatus doorStatus) {
		LogUtils.e("请求成功结束时间", Util.getCurrentTime());
		LogUtils.e("接收到服务器返回数据",
				"\n甲醛浓度：" + doorStatus.getHcho()+
						"\n门状态：" + doorStatus.getDoorState()+
						"\n门角度：" + doorStatus.getAngle()+
						"\n锁标记：" + doorStatus.getLockMark()+
						"\n锁状态" + doorStatus.getLockState()+
						"\n锁状态数组：" + doorStatus.getLockStates()+
						"\n电源标记：" + doorStatus.getPowerMark()+
						"\n报警状态：" + doorStatus.getWarnState()+
						"\n报警状态数组：" + doorStatus.getWarnStates() +
						"\n报警状态：" + doorStatus.getWarnMark()+"\n"

		);
	}
	public void displayServiceData(DoorStatus doorStatus) {
		//甲醛浓度
		if (!checkIsNull(doorStatus.getHcho())){
			tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree(doorStatus.getHcho()+""));
		}else {
			tv_jiaquan.setText("---");
		}

		if (!checkIsNull(doorStatus.getDoorState())) {
			setDoorButtonImg(doorStatus.getDoorState()) ; //门控制按钮状态
		}else {
			setDoorButtonImg(0) ;
		}

		if (!checkIsNull(doorStatus.getAngle())) {
			setPieChart(doorStatus.getAngle()) ; //门开关角度
		}else {
			setPieChart(0);
		}
		if(null != doorStatus.getWarnStates() && doorStatus.getWarnStates().length >= 1) {
			setDoorPowerImg(doorStatus.getWarnStates()[0]) ; //电池欠压
		}

		if(null != doorStatus.getWarnStates()&& doorStatus.getWarnStates().length >= 3) {
			setDoorAlarmImg(doorStatus.getWarnStates()[2]) ; //门关门故障
		}

		act.frgTool.f_1_1.displayServiceData(doorStatus);//显示第二页数据
	}

	/**
	 * 设置门故障状态
	 * @param positon
	 */
	private void setDoorAlarmImg(int positon) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (positon == 1) {
			imgDoorAlarm.setImageResource(R.mipmap.ic_circle_red);
		} else if (positon == 0) {
			imgDoorAlarm.setImageResource(R.mipmap.ic_circle_green);
		} else {
			imgDoorAlarm.setImageResource(R.mipmap.ic_circle_gray1);
		}
	}

	/**
	 * 设置门欠压状态
	 * @param positon
	 */
	private void setDoorPowerImg(int positon) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (positon == 1) {
			imgDoorPower.setImageResource(R.mipmap.ic_circle_red);
		}else if (positon == 0){
			imgDoorPower.setImageResource(R.mipmap.ic_circle_green);
		}else {
			imgDoorPower.setImageResource(R.mipmap.ic_circle_gray1);
		}
	}

	/**
	 * 设置门控制按键状态
	 * @param positon
	 */
	private void setDoorButtonImg(int positon) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (positon== 1) {
			openCloseStop = 1 ;
			receiveStopNum = 0 ;
			receiveOpenClose = true ;
			tv_door_status.setText("开");
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_green_bg));
		}else if (positon == 2) {
			receiveStopNum = 0 ;
			openCloseStop = 2;
			tv_door_status.setText("关");
			receiveOpenClose = true ;
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_red_bg));
		}else if (positon == 3) {
			receiveStopNum ++ ;
			openCloseStop = 3 ;
			if (receiveStopNum >= 3 || receiveOpenClose || currentCom.equals("3") ||
					(currentCom.equals("1") && SharepreferenceUtils.getIsWifi(act)
					|| (currentCom.equals("2") && SharepreferenceUtils.getIsWifi(act)))) {
				receiveOpenClose = false ;
				currentCom = "0" ;
				setBtnIsEnable(true);
				endReqFlag = true ;
				act.delay = 5;
				isQuerySeverEnable = true ;
				handler.removeCallbacks(operatorTimeOverResetStatus);
				startTimer();
			}
			tv_door_status.setText("停");
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_bg));
		}else {
			tv_door_status.setText("停");
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_bg));
		}
	}

	public void setBtnIsEnable(boolean isEnable) {
		if (isEnable) {
			tv_open.setEnabled(true);
			tv_close.setEnabled(true);
			tv_stop.setEnabled(true);
			setBtnBackground(4,0);
		}else {
			tv_open.setEnabled(false);
			tv_close.setEnabled(false);
			tv_stop.setEnabled(false);
			setBtnBackground(0,0);
		}
	}

	private void setPieChart(int open){
		setDoorDit(open) ;
	}

	private void setDoorDit(int open) {
		int close = 0 ;
		if (open <= 180 && open >=0) {
			close = 180 - open ;
			data[0] = 135;
			data[1] = open ;
			data[2] = close ;
			data[3] = 45 ;
			setPieChartData() ;
			initPieChart() ;
		}else {
			data[0] = 135;
			data[1] = 0 ;
			data[2] = 180 ;
			data[3] = 45 ;
			setPieChartData() ;
			initPieChart() ;
			ToastUtils.show(act, "门角度超出范围:" + open);
		}
		setOpenCloseValue(data[1]);

	}

	private boolean checkIsNull(Object obj) {
		if (obj == null ||"".equals(obj)  || "-1".equals(obj.toString())) {
			return true ;
		}else {
			return  false ;
		}
	}

	/**
	 * 查询命令前进行检查
	 * @return
	 */
	@Override
	protected boolean checkBeforeQuery(boolean showDialog){
		return true ;
	}
	
	/**
	 * 设置命令前进行检查
	 * @return
	 */
	@Override
	protected boolean checkBeforeSet(boolean showDialog){
		return true ;
	}
	/**
	 * 查询命令
	 */
	@Override
	protected void queryCommand(){
		//CoreThread.getInstance().newRtuId(F_01_100.getInstance().getRtuSelectedItem().replaceAll(" ", ""));
		//this.sendRtuCommand(new CommandCreator().cd_EF(null), false) ;
	}
	
	/**
	 * 设置命令
	 */
	@Override
	protected void setCommand(){

	}

	public void setCommand(int command) {
		/*if (fragment_04 != null) {
			fragment_04.setRtuData(null, null,null,command+"",++receiveWifiDataNum);
		}*/
		this.sendRtuCommand(new CommandCreator().cd_F_1(command,null), false);
	}
	
	/**
	 * 查询或设置命令发送前检查出问题后的回调
	 */
	@Override
	public void commandHasProblem(){
	}
	
	/**
	 * 命令已经成功发送给后台服务后的回调
	 */
	@Override
	public void commandSended(){
	}
	
	/**
	 * 命令已经由后台服务通过网络成功发送后的回调
	 */
	@Override
	public void commandSendedCallBack(){
	}
	/**
	 * 功能项右侧图标复原
	 */
	@Override
	public void resetLabelImg(){
	}


	/**
	 * 收到数据
	 * @param d
	 */
	@Override
	public void receiveRtuData(RtuData d){
		onceComReceiveTrue = true ;
		setProgressVisible(0) ;
		if (isFirst) {
			isFirst = false ;
			setBtnBackground(4,0);
		}
		Data_F1 data = (Data_F1)d.subData ;
		if (currentCom.equals(data.getControlFlag()+"")) {
			if (data != null) {
				displayWifiData(data);
			} else {
				ToastUtils.show(act, "F1接收数据为空");
			}
			if (!endReqFlag) {
				queryF1Once();
			}
		}
	}
	public void displayWifiData(Object data1) {
		if (data1 instanceof Data_F1) {
			Data_F1 data = (Data_F1)data1;
			//甲醛浓度
			if (data.getJiaQuan() == 0) {
				tv_jiaquan.setText("0.000") ;
			}else {
				if (data.getJiaQuan() > 100000) {
					ToastUtils.show(act, "甲醛值超出范围");
				}else {
					tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree((data.getJiaQuan() / 1000) + "." + (data.getJiaQuan() % 1000))
					);
				}
			}
			setDoorButtonImg(data.getDoorStatus()) ; //门控制按钮状态
			setPieChart(data.getDoorOpen()) ; //门开关角度
			setDoorPowerImg(data.isNormalPower() ? 0 : 1) ; //电池欠压
			setDoorAlarmImg(data.isDoorNormal() ? 0 : 1) ; //门关门故障

			act.frgTool.f_1_1.displayWifiData(data);//显示第二页数据
		}else if (data1 instanceof Data_F2) {
			Data_F2 data = (Data_F2)data1;
			//甲醛浓度
			if (data.getJiaQuan() == 0) {
				tv_jiaquan.setText("0.000") ;
			}else {
				if (data.getJiaQuan() > 100000) {
					ToastUtils.show(act, "甲醛值超出范围");
				}else {
					tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree((data.getJiaQuan() / 1000) + "." + (data.getJiaQuan() % 1000))
					);
				}
			}
			setDoorButtonImg(data.getDoorStatus()) ; //门控制按钮状态
			setPieChart(data.getDoorOpen()) ; //门开关角度
			setDoorPowerImg(data.isNormalPower() ? 0 : 1) ; //电池欠压
			setDoorAlarmImg(data.isDoorNormal() ? 0 : 1) ; //门关门故障

			act.frgTool.f_1_1.displayWifiData2(data);//显示第二页数据
		}else if (data1 instanceof Data_F3) {
			Data_F3 data = (Data_F3)data1;
			//甲醛浓度
			if (data.getJiaQuan() == 0) {
				tv_jiaquan.setText("0.000") ;
			}else {
				if (data.getJiaQuan() > 100000) {
					ToastUtils.show(act, "甲醛值超出范围");
				}else {
					tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree((data.getJiaQuan() / 1000) + "." + (data.getJiaQuan() % 1000))
					);
				}
			}

			setDoorButtonImg(data.getDoorStatus()) ; //门控制按钮状态
			setPieChart(data.getDoorOpen()) ; //门开关角度
			setDoorPowerImg(data.isNormalPower() ? 0 : 1) ; //电池欠压
			setDoorAlarmImg(data.isDoorNormal() ? 0 : 1) ; //门关门故障

			act.frgTool.f_1_1.displayWifiData3(data);//显示第二页数据
		}

	}
	/**
	 * 导出设置数据
	 */
	@Override
	public void outSetData(Vo2Xml vo){
	}
	/**
	 * 导入设置数据
	 */
	@Override
	public void inSetData(Vo2Xml vo){
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (task != null) {
            task.stop();
        }
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}