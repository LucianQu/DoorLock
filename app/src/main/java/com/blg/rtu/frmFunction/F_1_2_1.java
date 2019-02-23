package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F2.Data_F2;
import com.blg.rtu.server.net.NetManager;
import com.blg.rtu.util.DialogAlarm;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.MyTimeTask;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.util.Util;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
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
import lecho.lib.hellocharts.model.SliceValue;


public class F_1_2_1 extends FrmParent implements AddPopWindow.Choice{

	private Spinner spinner2;

	private ArrayAdapter<SpinnerVO> spinnerAdapter2;
	List<SliceValue> values = new ArrayList<SliceValue>();

	private DoorStatus doorStatus ; //门状态
	public String currentID = "" ; //当前门ID
	public String currentPassword = "" ; //当前门ID
	public Callback.Cancelable httpGet ;  //网络请求
	HttpUtils http ;
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
	private boolean onceComReceiveTrue  = false;
	private boolean deviceNetStatus = false ;

	private TextView tv_windowList;
	private AddPopWindow popWindow;
	private List<String> windowList = new ArrayList<String>() ;
	private List<String> passwordList = new ArrayList<String>() ;
	private int positionId = 0 ;
	private MyTimeTask task ;
	private boolean taskStatus = false ;
	private DialogConfirm dialogConfirm ;
	private boolean isShowing = false ;
	private HashMap<String , String> passErrorHashMap = new HashMap<String, String>() ;
	private TextView tv_open1;
	private TextView tv_close1;
	private TextView tv_stop1;

	private TextView tv_open2;
	private TextView tv_close2;
	private TextView tv_stop2;

	private ProgressBar pb_open1 ;
	private ProgressBar pb_close1 ;
	private ProgressBar pb_stop1 ;

	private ProgressBar pb_open2 ;
	private ProgressBar pb_close2 ;
	private ProgressBar pb_stop2 ;
	private TextView tvData1 ;
	private TextView tvData2 ;

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
	}
	private void setTimer(){
		task =new MyTimeTask(2000, new TimerTask() {
			@Override
			public void run() {
				if (!SharepreferenceUtils.getIsWifi(act)) {
					if (!getCurrentIDIsempty()) {
						if (isQuerySeverEnable) {
							if (!"".equals(currentID)) {
								queryServerStatus(currentID);
								doorContralServer(currentID, "F2", "0", "0");
							}
						}
					}
				}else {
					if (act.tcpConnected && SharepreferenceUtils.getHasLearn(act)) {
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
		View view = inflater.inflate(R.layout.f_1_02, container, false);
		tv_windowList = (TextView) view.findViewById(R.id.tv_doorList) ;
		popWindow = new AddPopWindow(getActivity(), windowList);
		popWindow.setChoice(this);

		/*SharepreferenceUtils.saveHasLearn(act, true);
		SharepreferenceUtils.saveWindowDeviceId(act,"0102030401-0102030402-0102030403-0102030404");
		SharepreferenceUtils.saveWindowPassword(act,"1111-2222-3333-4444");
		updateSpinnerValue(SharepreferenceUtils.getWindowDeviceId(act));*/

		tv_windowList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popWindow.showPopupWindow(tv_windowList);
			}
		});
		tv_windowList.setEnabled(false);

		pb_open1 = (ProgressBar) view.findViewById(R.id.pb_open1);
		pb_open2 = (ProgressBar) view.findViewById(R.id.pb_open2);
		pb_close1 = (ProgressBar) view.findViewById(R.id.pb_close1);
		pb_close2 = (ProgressBar) view.findViewById(R.id.pb_close2);
		pb_stop1 = (ProgressBar) view.findViewById(R.id.pb_stop1);
		pb_stop2 = (ProgressBar) view.findViewById(R.id.pb_stop2);
		tv_open1 = (TextView) view.findViewById(R.id.tv_open1) ;
		tvData1 = (TextView) view.findViewById(R.id.tv_data1) ;
		tvData2 = (TextView) view.findViewById(R.id.tv_data2) ;
		tv_open1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dealOpen();
			}
		});

		tv_close1 = (TextView) view.findViewById(R.id.tv_close1) ;
		tv_close1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dealClose();
			}
		});
		tv_stop1 = (TextView) view.findViewById(R.id.tv_stop1) ;
		tv_stop1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dealStop();
			}
		});

		tv_open2 = (TextView) view.findViewById(R.id.tv_open2) ;
		tv_open2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		tv_close2 = (TextView) view.findViewById(R.id.tv_close2) ;
		tv_close2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		tv_stop2 = (TextView) view.findViewById(R.id.tv_stop2) ;
		tv_stop2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

        spinner2 = (Spinner)view.findViewById(R.id.spinner_communication);
        spinnerAdapter2 = new ArrayAdapter<SpinnerVO>(this.act, R.layout.spinner_style, new ArrayList<SpinnerVO>());
        spinnerAdapter2.setDropDownViewResource(R.layout.spinner_item);
        this.putSpinnerValue2();
        spinner2.setAdapter(spinnerAdapter2);
        spinner2.setOnItemSelectedListener(new SpinnerSelectedListener2());
		return view ;
	}

	public void setSpinner2SelectWifi() {
		if (spinner2.getSelectedItemPosition() != 2) {
			spinner2.setSelection(2, true);
		}
	}

	private void dealOpen() {
		if (Util.checkIsHasLearned(act)) {
			stopTimer();
			endReqFlag = true ;
			receiveStopNum = 0 ;
			setProgressVisible(1);
			setBtnBackground(1, 2);
			setBtnBackground(2, 0);
			setBtnBackground(3, 1);
			currentCom = "1";
			currentAfn = "F2";
			receiveOpenClose = false ;
			onceComReceiveTrue = false ;
			endReqFlag = false ;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(1);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的窗！");
						} else {
							isQuerySeverEnable = false ;
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
				}
			},700) ;
			onceComCheckIsReceive() ;
			handler.removeCallbacks(operatorTimeOverResetStatus);
			handler.postDelayed(operatorTimeOverResetStatus, 30000) ;
		}
	}

	private void dealClose() {
		if (Util.checkIsHasLearned(act)) {
			stopTimer();
			receiveStopNum = 0 ;
			setProgressVisible(2);
			setBtnBackground(1, 0);
			setBtnBackground(2, 2); //
			setBtnBackground(3, 1);
			currentCom = "2";
			currentAfn = "F2";
			receiveOpenClose = false ;
			endReqFlag = false ;
			onceComReceiveTrue = false ;

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(2);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的窗！");
						} else {
							isQuerySeverEnable = false ;
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
				}
			},700) ;
			onceComCheckIsReceive() ;
			handler.removeCallbacks(operatorTimeOverResetStatus);
			handler.postDelayed(operatorTimeOverResetStatus, 30000) ;
		}
	}

	private void dealStop() {
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
			currentAfn = "F2";
			onceComReceiveTrue = false ;
			endReqFlag = false ;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(3);
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的窗！");
						} else {
							handler.removeCallbacksAndMessages(null);
							doorContralServer(currentID, currentAfn, currentCom,"1");
						}
					}
				}
			},700) ;

			onceComCheckIsReceive() ;
			handler.removeCallbacks(operatorTimeOverResetStatus);
			handler.postDelayed(operatorTimeOverResetStatus, 10000) ;
		}
	}

	@Override
	public void senddata(String msg) {
		positionId = Integer.parseInt(msg) ;
		LogUtils.e("Lucian-->当前选择Position", positionId+"");
		if (!SharepreferenceUtils.getIsWifi(act) && wifiServer == 1) {
			deviceNetStatus = false ;
			if (windowList.size() > 0) {
				num++ ;
				LogUtils.e("Lucian-->点击item次数", num + "");
				if (num > 0) {
					clickDeviceId = true;
					if (!SharepreferenceUtils.getIsWifi(act) ) {
						startTimer();
					}
					isFirst = true ;
					currentID = windowList.get(positionId);
					currentPassword = passwordList.get(positionId) ;
					SharepreferenceUtils.saveComPassword(act, currentPassword);
					String deviceID = SharepreferenceUtils.getWindowDeviceId(act) ;
					String password = SharepreferenceUtils.getWindowPassword(act) ;
					LogUtils.e("Lucian-->选择的窗户地址", currentID);
					LogUtils.e("Lucian-->选择的窗户密码", currentPassword);
					Log.e("Lucian--->","设备列表：" +deviceID) ;
					Log.e("Lucian--->","密码列表：" +password) ;
					if (deviceID.contains(currentID)) {
						SharepreferenceUtils.saveHasLearn(act,true);
					}else {
						SharepreferenceUtils.saveHasLearn(act,false);
						ToastUtils.show(act,"该设备未学习，请先学习!");
					}

					act.delay = 5 ;
					act.updateConnectedStatus(false);

                    tv_windowList.setText(currentID);
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
				act.getResources().getString(R.string.deleteId1) ,
				new DialogConfirm.CallBackInterface(){
					@Override
					public void dialogCallBack(Object o) {
						if((Boolean)o){
							if (positionId == position) {
								LogUtils.e("Lucian-->移除的ID为当前选择Position", position+"");
								initStatus() ;
							}
							if (!passErrorHashMap.isEmpty()) {
								if (passErrorHashMap.containsKey(windowList.get(position))) {
									passErrorHashMap.remove(windowList.get(position)) ;
									LogUtils.e("Lucian-->移除密码列表中的设备ID", windowList.get(position));
								}
							}
							String deviceID = SharepreferenceUtils.getWindowDeviceId(act) ;
							String password = SharepreferenceUtils.getWindowPassword(act) ;
							LogUtils.e("Lucian-->设备列表", deviceID);
							LogUtils.e("Lucian-->密码列表", password);
							String[] listId = deviceID.split("-");
							String[] listPassword = password.split("-");
							windowList.remove(position) ;

							int postion1 = listId.length -position -1 ;
							LogUtils.e("Lucian-->position", postion1 + "");
							if (windowList.size() > 0) {
								String ids = "";
								String pws = "";
								for (int j = 0; j < listId.length; j++) {
									if (j == 0) {
										if (j != postion1) {
											ids = listId[0];
											pws = listPassword[0];
										}else {
											LogUtils.e("Lucian-->移除设备ID", listId[postion1]);
											LogUtils.e("Lucian-->移除设备密码", listPassword[postion1]);
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
											LogUtils.e("Lucian-->移除设备ID", listId[postion1]);
											LogUtils.e("Lucian-->移除设备密码", listPassword[postion1]);
											ToastUtils.show(act, "删除门地址：" + listId[postion1]);
										}
									}
								}
								LogUtils.e("Lucian-->设备列表", ids);
								LogUtils.e("Lucian-->密码列表", pws);
								SharepreferenceUtils.saveWindowDeviceId(act, ids);
								SharepreferenceUtils.saveWindowPassword(act, pws);
							}else {
								SharepreferenceUtils.saveWindowDeviceId(act, "");
								SharepreferenceUtils.saveWindowPassword(act, "");
							}
							updateSpinnerValue(SharepreferenceUtils.getWindowDeviceId(act));
						}else{
						}
					}
				}) ;
	}

	public void initStatus() {
		currentID = "" ;
		stopTimer();
		act.setDoorId("---");
		isFirst = true ;
		clickDeviceId = false;
		tv_windowList.setText("");
		tv_windowList.setHint("请选择窗地址");
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
		setBtnBackground(0, 0);
		if (SharepreferenceUtils.getIsWifi(act)) {
			if (act.tcpConnected) {
				NetManager.getInstance().toggleConnectRemote(false);
				act.tcpConnected = false;
			}
		}
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
			handler.postDelayed(queryF1OnceTask, 2500) ;
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
							ToastUtils.show(act, "命令超时重发");
							LogUtils.e("超时","命令超时重发");
							setCommand(2);
						} else if ((currentCom.equals("1"))) {
							ToastUtils.show(act, "命令超时重发");
							LogUtils.e("超时","命令超时重发");
							setCommand(1);
						} else if (currentCom.equals("3")) {
							ToastUtils.show(act, "命令超时重发");
							LogUtils.e("超时","命令超时重发");
							setCommand(3);
						}else {
							setCommand(0);
						}
					}
				}else {
					ToastUtils.show(act, "命令超时重发");
					LogUtils.e("超时","命令超时重发");
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
			//currentCom = "0" ;
			setCommand(0);
		}else {
			handler.postDelayed(onceServerReq, 500) ;
		}
		onceComCheckIsReceive() ;
	}

	public boolean getCurrentIDIsempty() {
		if (windowList.size() == 0) {
			return true ;
		}else {
			return false ;
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
			if (null == this.http) {
				http = new HttpUtils();
			}
			http.configCurrentHttpCacheExpiry(1000 * 5);
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
					//LogUtils.e("Lucian-->服务请求","成功"+Util.getCurrentTime());
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
										ToastUtils.show(act, "服务获取数据失败：" + "窗设备回复数据超时！");
										//act.second30 = minute2; //设备回复超时，2分钟后再试
									}else if (msg.contains("重新学习")){
										LogUtils.e("Lucian-->重新学习", returnDtuId);
										if (!passErrorHashMap.containsKey(returnDtuId)) {
											LogUtils.e("Lucian-->放入键值对", returnDtuId);
											passErrorHashMap.put(returnDtuId,"1") ;
										}
										tv_windowList.setText("");
										tv_windowList.setHint("请选择窗地址");
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
					LogUtils.e("Lucian-->onError", "请求失败"+arg0.getMessage());
				}
			});
		} catch (Exception e) {
			String msg = null;
			if (e instanceof InvocationTargetException) {
				Throwable targetEx = ((InvocationTargetException) e)
						.getTargetException();
				if (targetEx != null) {
					msg = targetEx.getMessage();
					LogUtils.e("Lucian-->onError", "请求失败"+msg) ;
				}
			} else {
				msg = e.getMessage();
				LogUtils.e("Lucian-->onError", "请求失败"+msg) ;
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
						act.getResources().getString(R.string.passwordErrorWin),
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
						act.getResources().getString(R.string.passwordErrorWin),
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
		httpGet = x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if (result.equals("1")) {
					//LogUtils.e("Lucian-->----->接收到服务器返回数据", "-----> 在线" );
					act.requestServeice = true;
					if (!deviceNetStatus && !passErrorHashMap.containsKey(dtuId)) {
						//LogUtils.e("Lucian-->是否包含ID", dtuId + !passErrorHashMap.containsKey(dtuId));
						startTimer();
						deviceNetStatus = true;
						setBtnIsEnable(true);
						setBtnBackground(4, 0);
						act.updateConnectedStatus(true);
					}
				}else {
					//LogUtils.e("Lucian-->----->接收到服务器返回数据", "-----> 不在线" );
					act.requestServeice = false ;
					if (deviceNetStatus) {
							deviceNetStatus = false;
							setBtnIsEnable(false);

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
				tv_open1.setEnabled(true);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_open1.setEnabled(false);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_open1.setEnabled(false);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 2) {
			if (status == 1) {
				tv_close1.setEnabled(true);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_close1.setEnabled(false);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_close1.setEnabled(false);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 3) {
			if (status == 1) {
				tv_stop1.setEnabled(true);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_stop1.setEnabled(false);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_stop1.setEnabled(false);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 4) {
			setProgressVisible(0) ;
			tv_open1.setEnabled(true);
			tv_close1.setEnabled(true);
			tv_stop1.setEnabled(true);
			tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
		}else {
			setProgressVisible(0) ;
			tv_open1.setEnabled(false);
			tv_close1.setEnabled(false);
			tv_stop1.setEnabled(false);
			tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
		}
	}

	private void setProgressVisible(int position){
		if (position == 1) {
			pb_open1.setVisibility(View.VISIBLE);
			pb_close1.setVisibility(View.INVISIBLE);
			pb_stop1.setVisibility(View.INVISIBLE);
		}else if (position == 2) {
			pb_open1.setVisibility(View.INVISIBLE);
			pb_close1.setVisibility(View.VISIBLE);
			pb_stop1.setVisibility(View.INVISIBLE);
		}else if (position == 3) {
			pb_open1.setVisibility(View.INVISIBLE);
			pb_close1.setVisibility(View.INVISIBLE);
			pb_stop1.setVisibility(View.VISIBLE);
		}else {
			pb_open1.setVisibility(View.INVISIBLE);
			pb_close1.setVisibility(View.INVISIBLE);
			pb_stop1.setVisibility(View.INVISIBLE);
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



	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			//spinnerAdapter1.clear();
			String[] arr = data.split("-") ;
			windowList.clear();
			if (arr.length >= 1) {
				doorNum = arr.length ;
				for (int i = 0; i < arr.length; i++) {
					windowList.add(arr[arr.length -i-1]) ;
				}
			}
			popWindow = new AddPopWindow(getActivity(), windowList);
			popWindow.setChoice(this);
            String passwordStr = SharepreferenceUtils.getWindowPassword(act) ;
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

	public void enableWindowList() {
		tv_windowList.setEnabled(true);
	}


	private long num =0;
	public void initDeviceConnect() {
		setBtnBackground(0,0); //初始化按钮状态灰色，不使能
	}

	public void initSpinner2Position() {
		spinner2.setSelection(0);
	}
	private class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner2.getId()){
				/*fragment_04.setRtuData(new DoorStatus(),null);*/

				if (position == 1 || position == 2) {
					if (SharepreferenceUtils.getIsDoor(act)) {
						SharepreferenceUtils.saveIsDoor(act, false);
						act.frgTool.f_1_0.initStatus();
						act.frgTool.f_1_0.initSpinner2Position();
					}
				}
				if (position == 1) {//服务器
					tv_windowList.setText("");
					tv_windowList.setHint("请选择窗地址");
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
						new DialogAlarm().showDialog(act, "窗设备地址为空，请先学习！\n学习步骤：\n1、手机Wifi连接到门热点\n2、APP通信类型选择Wifi通信\n3、连接到Wifi后到<副页面3>进行窗学习!");
					}else {
						act.connectWifiAndServer();
					}
					tv_windowList.setEnabled(true);
					isQuerySeverEnable = true ;
				}else if (position == 2){//wifi
					initStatus();
					if (act.tcpConnected) {
						act.tcpConnected = false ;
						NetManager.getInstance().toggleConnectRemote(false);
					}
					LogUtils.e("Lucian-->窗地址为空","重新选择wifi通道");
					wifiServer = 2 ;
					act.requestServeice = false ;
					isFirst = true ;
					act.frgTool.f_01_010.setReceiveWifiData(false);
					SharepreferenceUtils.saveIsWifi(act, true);
					tv_windowList.setEnabled(false);
					act.connectWifiAndServer() ;
					isQuerySeverEnable = false ;
				}/*else {
					initStatus();
					if (SharepreferenceUtils.getIsWifi(act)) {
						wifiServer = 0 ;
						initStatus();
						if (act.tcpConnected) {
							NetManager.getInstance().toggleConnectRemote(false);
							act.tcpConnected = false;
						}
						tv_windowList.setEnabled(false);
						isQuerySeverEnable = false ;
					}else {
						tv_windowList.setText("");
						tv_windowList.setHint("请选择门地址");
						initStatus();
						if (act.tcpConnected) {
							act.tcpConnected = false ;
							NetManager.getInstance().toggleConnectRemote(false);
						}
						wifiServer = 0 ;
						act.requestServeice = false ;
						act.frgTool.f_01_010.setReceiveWifiData(false);
						act.requestServeice = false ;
						tv_windowList.setEnabled(false);
						isQuerySeverEnable = false ;
					}
				}*/
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void pintServiceData(DoorStatus doorStatus) {
		LogUtils.e("Lucian-->请求成功结束时间", Util.getCurrentTime());
		LogUtils.e("Lucian-->接收到服务器返回数据",
				"\n甲醛浓度：" + doorStatus.getHcho()+
						"\n窗状态：" + doorStatus.getDoorState()+
						"\n窗角度：" + doorStatus.getAngle()+
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

	}




	/**
	 * 设置门控制按键状态
	 * @param positon
	 */
	private void setDoorButtonImg(int positon) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		LogUtils.e("门状态：",""+positon);
		if (positon== 1) {

			receiveStopNum = 0 ;
			receiveOpenClose = true ;

		}else if (positon == 2) {
			receiveStopNum = 0 ;
			receiveOpenClose = true ;
		}else if (positon == 3) {
			receiveStopNum ++ ;

			if (receiveStopNum >= 3 || receiveOpenClose || currentCom.equals("3") ||
					(currentCom.equals("1") && SharepreferenceUtils.getIsWifi(act)
					|| (currentCom.equals("2") && SharepreferenceUtils.getIsWifi(act)))) {
				receiveOpenClose = false ;
				if (currentCom.equals("1")&& currentCom.equals("2")&&currentCom.equals("3")) {
					ToastUtils.show(act, "控制命令执行完毕!");
				}
				setBtnIsEnable(true);
				endReqFlag = true ;
				LogUtils.e("收到停止命令","复位");
				act.delay = 5;
				isQuerySeverEnable = true ;
				handler.removeCallbacks(operatorTimeOverResetStatus);
				currentCom = "0" ;
				startTimer();
				//handler.postDelayed(testMode, 30000) ;
			}

		}else {

		}
	}

	public void setBtnIsEnable(boolean isEnable) {
		if (isEnable) {
			tv_open1.setEnabled(true);
			tv_close1.setEnabled(true);
			tv_stop1.setEnabled(true);
			setBtnBackground(4,0);
		}else {
			tv_open1.setEnabled(false);
			tv_close1.setEnabled(false);
			tv_stop1.setEnabled(false);
			setBtnBackground(0,0);

		}
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
	}
	
	/**
	 * 设置命令
	 */
	@Override
	protected void setCommand(){

	}

	public void setCommand(int command) {
		this.sendRtuCommand(new CommandCreator().cd_F_2(command,null), false);
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

	public void setFun1AllGreen() {
		tv_open1.setEnabled(true);
		tv_stop1.setEnabled(true);
		tv_close1.setEnabled(true);
		setBtnBackground(1, 1); //绿
		setBtnBackground(2, 1); //绿
		setBtnBackground(3, 1); //绿
	}
	public void setFun2AllGreen() {
		tv_open2.setEnabled(true);
		tv_stop2.setEnabled(true);
		tv_close2.setEnabled(true);
		setBtnBackground(4, 1); //绿
		setBtnBackground(5, 1); //绿
		setBtnBackground(6, 1); //绿
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
			LogUtils.e("F_1_2_1","使能按钮");
			isFirst = false ;
			setBtnBackground(4,0);
			//setFun1AllGreen();
			//setFun2AllGreen();
		}
		Data_F2 data = (Data_F2)d.subData ;
		LogUtils.e("F_1_2_1", "控制命令："+currentCom+"数据中控制命令："+data.getControlFlag());
		//if (currentCom.equals(data.getControlFlag()+"")) {
		if (true) {
			if (data != null) {
				displayWifiData(data) ;
				tvData1.setText(data.getDoorOpen()+"");
			} else {
				ToastUtils.show(act, "F1接收数据为空");
			}

			if (!endReqFlag) {
				queryF1Once();
			}else {
				if (currentCom.equals("1")&& currentCom.equals("2")&&currentCom.equals("3")) {
					ToastUtils.show(act, "停止连续发送");
				}
			}
		}else {
			if (currentCom.equals("1")&& currentCom.equals("2")&&currentCom.equals("3")) {
				ToastUtils.show(act, "发送命令" + currentCom +
						"和接收到的命令" + data.getControlFlag() +
						"  不一致，停止发送");
			}
		}
	}

	public void displayWifiData(Object data1) {
		if (data1 instanceof Data_F2) {
			Data_F2 data = (Data_F2)data1;
			setDoorButtonImg(data.getDoorStatus()) ; //门控制按钮状态
		}
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