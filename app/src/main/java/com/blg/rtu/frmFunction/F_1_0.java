package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.app.FragmentManager;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blg.rtu.frmChannel.ChFragment_04;
import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F1.Data_F1;
import com.blg.rtu.protocol.p206.F2.Data_F2;
import com.blg.rtu.protocol.p206.F3.Data_F3;
import com.blg.rtu.util.DialogAlarm;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;



public class F_1_0 extends FrmParent {

	private ReSpinner spinner;
	private Spinner spinner2;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;
	private ArrayAdapter<SpinnerVO> spinnerAdapter2;
	private RequestQueue queue;
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
	public Callback.Cancelable httpGet ;  //网络请求
	public String currentCom = "0" ; //当前命令
	private int reSendNum = 0 ; //重发次数
	private String currentAfn = "" ; //当前功能码
	private boolean isFirst = true ; //是否初始请求
	public  int  doorNum = 0; //APP存储门ID数量
	private boolean clickStop = false ;
	private boolean receiveOpenClose = false ;
	public boolean clickDeviceId = false ; //判断是否手动点击设备ID
	private ChFragment_04 fragment_04 ;
	private boolean endReqFlag = true ; //结束数据请求
	private int sendServerReqNum = 0 ; //发送服务请求次数
	private int sendWifiReqNum = 0 ; //发送服务请求次数
	private int receiveServerDataNum = 0 ; //APP打开后，接收服务数据次数
	private int receiveWifiDataNum = 0 ; //APP打开后，接收wifi数据次数
	private int receiveStopNum = 0 ; //接收门停止计数
	private String currentClick = "" ;
	private int lastDoorDit = 0 ;
	private boolean isClickButton = false ;
	private int openCloseStop = 0 ;
	private int stopNum = 0 ;
	private boolean onceComReceiveTrue  = false;
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

	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_1_00, container, false);
		queue = Volley.newRequestQueue(getActivity());

		spinner = (ReSpinner)view.findViewById(R.id.spinner_doorList);
		spinnerAdapter1 = new ArrayAdapter<SpinnerVO>(this.act, R.layout.spinner_style, new ArrayList<SpinnerVO>());
		this.putSpinnerValue1();
		spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(spinnerAdapter1);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

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
					isClickButton = true ;
					receiveStopNum = 0 ;
					setProgressVisible(1);
					setBtnBackground(1, 2);
					setBtnBackground(2, 0);
					setBtnBackground(3, 1);
					currentClick ="1" ;
					currentCom = "1";
					currentAfn = "F1";
					act.delay = 30;
					reSendNum = 20;

					receiveOpenClose = false ;
					endReqFlag = true ;
					if (SharepreferenceUtils.getIsWifi(act)) {
						if (null != httpGet) {
							httpGet.cancel();
						}
						setCommand(1);
						handler.removeCallbacks(twoOpenWifiData);
						handler.removeCallbacks(twoCloseWifiData);
						handler.removeCallbacks(stopTask);
						handler.postDelayed(twoOpenWifiData, 500) ;
						handler.removeCallbacks(queryIsReceiveWifiData);
						handler.postDelayed(queryIsReceiveWifiData, 15000) ;
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						} else {
							doorContralServer(currentID, currentAfn, currentCom);
							handler.removeCallbacks(queryF1StopTask);
							handler.postDelayed(queryF1StopTask, 30000) ;
						}
					}
					endReqFlag = false ;
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
					isClickButton = true ;
					receiveStopNum = 0 ;
					setProgressVisible(2);
					currentClick ="2" ;
					setBtnBackground(1, 0);
					setBtnBackground(2, 2); //
					setBtnBackground(3, 1);
					currentCom = "2";
					currentAfn = "F1";
					reSendNum = 20;
					receiveOpenClose = false ;
					act.delay = 30;
					endReqFlag = true ;
					if (SharepreferenceUtils.getIsWifi(act)) {
						if (null != httpGet) {
							httpGet.cancel();
						}
						setCommand(2);
						handler.removeCallbacks(twoOpenWifiData);
						handler.removeCallbacks(twoCloseWifiData);
						handler.removeCallbacks(stopTask);
						handler.postDelayed(twoCloseWifiData, 500) ;
						handler.removeCallbacks(queryIsReceiveWifiData);
						handler.postDelayed(queryIsReceiveWifiData, 15000) ;
					} else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						} else {

							doorContralServer(currentID, currentAfn, currentCom);
							handler.removeCallbacks(queryF1StopTask);
							handler.postDelayed(queryF1StopTask, 15000) ;
						}
					}
					endReqFlag = false ;
				}
			}
		});
		pb_close = (ProgressBar) view.findViewById(R.id.pb_close);

		tv_stop = (TextView) view.findViewById(R.id.tv_stop) ;
		tv_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.checkIsHasLearned(act)) {
					/*if (clickStop) {*/
						isClickButton = true ;
						receiveStopNum = 0 ;
						setProgressVisible(3);
						currentClick ="3" ;
						setBtnBackground(3, 2);
						setBtnBackground(1,1); //复位
						setBtnBackground(2,1);//复位
					    stopNum = 0 ;
						endReqFlag = true ;
						if (currentCom.equals("1") || currentCom.equals("2")) {
							receiveOpenClose = false;
							reSendNum = -1 ;

						}
						currentCom = "3";
						currentAfn = "F1";
						if (SharepreferenceUtils.getIsWifi(act)) {
							if (null != httpGet) {
								httpGet.cancel();
							}
							handler.removeCallbacks(queryWifiTask);
							setCommand(3);
							handler.removeCallbacks(twoOpenWifiData);
							handler.removeCallbacks(twoCloseWifiData);
							handler.removeCallbacks(stopTask);
							handler.postDelayed(stopTask, 500) ;
                            handler.removeCallbacks(queryIsReceiveWifiData);
							handler.postDelayed(queryIsReceiveWifiData, 15000) ;
						} else {
							if (getCurrentIDIsempty()) {
								ToastUtils.show(act, "没有可操作的门！");
							} else {
								doorContralServer(currentID, currentAfn, currentCom);
								handler.postDelayed(onceReqServer, 100) ;
							}
						}
					endReqFlag = false ;
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

		FragmentManager fm = act.getFragmentManager();
		fragment_04 = (ChFragment_04)fm.findFragmentById(R.id.chFragment_04) ;

		return view ;
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

	private Runnable queryF1Task = new Runnable() {
		@Override
		public void run() {
			if (reSendNum > 0) {
				reSendNum-- ;
				if (!getCurrentIDIsempty()) {
					doorContralServer(currentID, currentAfn, "0");
				}
				handler.postDelayed(queryF1Task, 1000);
			}else {
				if (reSendNum < 0) {
					handler.removeCallbacks(queryF1Task);
					setBtnIsEnable(true);
					act.delay = 5;
				}else if (reSendNum == 0) {
					handler.postDelayed(queryF1Task, 1000);
					reSendNum--;
				}

			}
		}
	};

	private Runnable queryF1StopTask = new Runnable() {
		@Override
		public void run() {
			if (!endReqFlag) {
				receiveOpenClose = false;
				currentCom = "0";
				setBtnIsEnable(true);
				endReqFlag = true;
			}
		}
	};

	private Runnable stopTask = new Runnable() {
		@Override
		public void run() {
			if (openCloseStop != 3 && currentCom.equals("3")) {
				setCommand(3);
				handler.postDelayed(stopTask, 500) ;
			}else {
				handler.removeCallbacks(stopTask);
			}
		}
	};
	private void onceComCheckIsReceive() {
		onceComReceiveTrue = false ;
		handler.removeCallbacks(queryF1OnceTask);
		handler.postDelayed(queryF1OnceTask, 3000) ;
	}

	private Runnable queryF1OnceTask = new Runnable() {
		@Override
		public void run() {
			if (!onceComReceiveTrue) {
				if (SharepreferenceUtils.getIsWifi(act)) {
					setCommand(0);
				}else {
					doorContralServer(currentID, currentAfn, "0");
				}
				onceComCheckIsReceive() ;
			}else {
				handler.removeCallbacks(queryF1OnceTask);
			}
		}
	};

	private void queryF1Once() {
		if (SharepreferenceUtils.getIsWifi(act)) {
			setCommand(0);
		}else {
			doorContralServer(currentID, currentAfn, "0");
		}
		onceComCheckIsReceive() ;
	}

	private Runnable onceReqServer = new Runnable() {
		@Override
		public void run() {
			stopNum++ ;
			doorContralServer(currentID, currentAfn, currentCom);
			if (currentCom.equals("3") && !endReqFlag) {
				if (stopNum < 4) {
					handler.postDelayed(onceReqServer, 100);
				}else {
					handler.removeCallbacks(onceReqServer);
				}
			}
		}
	};

	public boolean getCurrentIDIsempty() {
		if (spinnerAdapter1.isEmpty()) {
			return true ;
		}else {
			if (getCurrentPosition() != -1) {
				currentID = spinnerAdapter1.getItem(getCurrentPosition()).getName() ;
			}
			return false ;
		}
	}

	public int getCurrentPosition() {
		if (spinnerAdapter1.isEmpty()) {
			return -1 ;
		}else {
			return spinner.getSelectedItemPosition() ;
		}
	}

	public void setCurrentPosition(int position) {
		if (!spinnerAdapter1.isEmpty()) {
			spinner.setSelection(position);
		}
	}


	public void doorContralServer(final String dtuId, String code, String flag) {
		LogUtils.e("请求开始时间", Util.getCurrentTime());
		LogUtils.e("主循环间隔：", (act.delay)+ "秒");
		String url = "http://39.106.112.210:8090/door/door/state.act?" ;
		//String url = "http://d573b440.ngrok.io/door/door/state.act?" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		requestParams.addBodyParameter("code", code);
		requestParams.addBodyParameter("flag", flag);
        if (fragment_04 != null) {
            fragment_04.setRtuData(null, requestParams.toString(),null,null,++sendServerReqNum);
        }
		LogUtils.e("门控制服务", requestParams.toString());
		httpGet = x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				onceComReceiveTrue = true ;
				setProgressVisible(0) ;
				if (currentCom.equals("3")) {
					handler.removeCallbacks(onceReqServer);
				}
				JSONObject jsonResult = null;
				if (!"".equals(result)) {
					try {
						jsonResult = new JSONObject(result);
						String returnDtuId = jsonResult.getString("dtuId");
						if (null == returnDtuId || "null".equals(returnDtuId) || "".equals(returnDtuId)) {
							//ToastUtils.show(act, "产品ID为空，数据未知!");
						} else {
							if (dtuId.equals(returnDtuId)) {
								String code = jsonResult.getString("succ");
								if (code.equals("1")) {

									Gson gson = new Gson();
									String data = jsonResult.getString("rltState");
									doorStatus = gson.fromJson(data, DoorStatus.class);
									if (null != doorStatus) {
										if (isFirst) {
											isFirst = false ;
											setBtnIsEnable(true);
											setBtnBackground(4,0);
										}else {
										}
										act.updateConnectedStatus(true);
										displayServiceData(doorStatus);
										if (fragment_04 != null) {
											fragment_04.setRtuData(doorStatus, null,null,null,++receiveServerDataNum);
										}
										pintServiceData(doorStatus);
										if (!endReqFlag) {
											queryF1Once();
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
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				onceComReceiveTrue = true ;
				setProgressVisible(0) ;
				if (ex.getMessage().contains("failed to connect to")) {
					act.updateConnectedStatus(false);
				}
				LogUtils.e("onError", "请求失败"+ex.getMessage());
				if (ex instanceof HttpException) { // 网络错误
					HttpException httpEx = (HttpException) ex;
					int responseCode = httpEx.getCode();
					String responseMsg = httpEx.getMessage();
					String errorResult = httpEx.getResult();
					// ...
				} else { // 其他错误
					// ...
				}
			}

			@Override
			public void onCancelled(CancelledException cex) {
				setProgressVisible(0) ;
			}

			@Override
			public void onFinished() {
				setProgressVisible(0) ;
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

	public void updateSpinnerValue(List<String> list) {
		int i = 0 ;
		spinnerAdapter1.clear();
		for (String str : list) {
			spinnerAdapter1.add(new SpinnerVO(i++ + "", str));
		}
		spinnerAdapter1.notifyDataSetChanged();
	}
	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			spinnerAdapter1.clear();
			String[] arr = data.split("-") ;
			if (arr.length >= 1) {
				doorNum = arr.length ;
				for (int i = 0; i < arr.length; i++) {
					spinnerAdapter1.add(new SpinnerVO(i + "", arr[arr.length -i-1]));
					spinnerAdapter1.notifyDataSetChanged();
				}
			}
		}else {
		}
	}

	private void putSpinnerValue1(){
		SharepreferenceUtils.saveHasLearn(act, true);
		SharepreferenceUtils.saveDeviceId(act,"0102030406-0102030407-0102030408-0102030409");
		SharepreferenceUtils.savePassword(act,"0102-0102-0102-0102");
		updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
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
	private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (!SharepreferenceUtils.getIsWifi(act)) {
				if (parent.getId() == spinner.getId()) {
					num++ ;
					LogUtils.e("点击item次数", num + "");
					if (num > 1) {
						clickDeviceId = true;
					}
					isFirst = true ;
					currentID = parent.getSelectedItem().toString();
					act.frgTool.f_1_2.setCurrentPosition(position);
					act.frgTool.f_1_2.setCurrentID(currentID);
					act.updateConnectedStatus(false);
					LogUtils.e("选择的门锁地址", currentID);
					act.delay = 5 ;
					initDeviceConnect() ;
					act.setDoorId(currentID);
				}
			}else {
				ToastUtils.show(act, "请注意：当前通信类型为WIFI");
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void initDeviceConnect() {
		setBtnBackground(0,0); //初始化按钮状态灰色，不使能
		setDoorButtonImg(3);
		setPieChart(0);
		act.postQuery();
	}

	private class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner2.getId()){
				/*fragment_04.setRtuData(new DoorStatus(),null);*/
				if (position == 1) {
					act.requestServeice = true ;
					setBtnBackground(0, 0);
					isFirst = true;
					SharepreferenceUtils.saveIsWifi(act, false);
					if (getCurrentIDIsempty()) {
						new DialogAlarm().showDialog(act, "门设备地址为空，请先学习！\n学习步骤：\n1、手机Wifi连接到门热点\n2、APP通信类型选择Wifi通信\n3、连接到Wifi后到<副页面3>进行门学习!");
					}else {
						act.connectWifiAndServer();
					}
					spinner.setEnabled(true);

				}else if (position == 2){
					act.requestServeice = false ;
					setBtnBackground(0,0);
					setBtnIsEnable(false) ;
					isFirst = true ;
					act.frgTool.f_01_010.setReceiveWifiData(false);
					SharepreferenceUtils.saveIsWifi(act, true);
					spinner.setEnabled(false);
					act.connectWifiAndServer() ;
				}
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
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
			//setBtnBackground(3, 1);
			clickStop = true ;
			receiveOpenClose = true ;
			tv_door_status.setText("开");
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_green_bg));
		}else if (positon == 2) {
			receiveStopNum = 0 ;
			//setBtnBackground(3, 1);
			clickStop = true ;
			openCloseStop = 2;
			tv_door_status.setText("关");
			receiveOpenClose = true ;
			tv_door_status.setBackground(act.getResources().getDrawable(R.drawable.tv_selected_red_bg));
		}else if (positon == 3) {
			clickStop = false ;
			//setBtnBackground(3, 3);
			receiveStopNum ++ ;
			openCloseStop = 3 ;
			if (receiveStopNum == 3 || receiveOpenClose || currentCom.equals("3") ||
					(currentCom.equals("1") && SharepreferenceUtils.getIsWifi(act)
					|| (currentCom.equals("2") && SharepreferenceUtils.getIsWifi(act)))) {
				receiveOpenClose = false ;
				currentCom = "0" ;
				setBtnIsEnable(true);
				endReqFlag = true ;
				act.delay = 5;
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
		/*if (currentClick.equals("2")) {
			if (open < lastDoorDit) {
				setDoorDit(open) ;
			}
		}else if (currentClick.equals("1")) {
			if (open > lastDoorDit) {
				setDoorDit(open) ;
			}
		}else if (currentClick.equals("3")){
			if (open == lastDoorDit) {
				setDoorDit(open) ;
			}
		}else {
			if (open == lastDoorDit) {
				setDoorDit(open) ;
			}
		}*/
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
		lastDoorDit = open ;
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

	protected void setCommand(int command) {
		if (fragment_04 != null) {
			fragment_04.setRtuData(null, null,null,command+"",++receiveWifiDataNum);
		}
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

	private Runnable queryWifiTask =  new Runnable() {
		@Override
		public void run() {
			if (act.tcpConnected) {
				setCommand(0);
			}
		}
	};
	private Runnable queryIsReceiveWifiData =  new Runnable() {
		@Override
		public void run() {
			if (!endReqFlag) {
				isClickButton = false ;
				if (currentCom.equals("1")) {
                    ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(1,1);
                }else if (currentCom.equals("2")) {
                    ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(2,1);
                }else if (currentCom.equals("3")) {
					setBtnBackground(3,1);
                    ToastUtils.show(act,"设备回复超时，请再次操作!");
                }
				//setCommand(0);

			}
		}
	};

	private Runnable twoOpenWifiData =  new Runnable() {
		@Override
		public void run() {
			if ((openCloseStop != 1 && currentCom.equals("1"))) {
				setCommand(1);
				handler.postDelayed(twoOpenWifiData, 1000) ;
			}else {
				handler.removeCallbacks(twoOpenWifiData);
			}
		}
	};
	private Runnable twoCloseWifiData =  new Runnable() {
		@Override
		public void run() {
			if ((openCloseStop != 2 && currentCom.equals("2"))) {
				setCommand(2);
				handler.postDelayed(twoCloseWifiData, 1000) ;
			}else {
				handler.removeCallbacks(twoCloseWifiData);
			}
		}
	};

	/**
	 * 收到数据
	 * @param d
	 */
	@Override
	public void receiveRtuData(RtuData d){
		onceComReceiveTrue = true ;
		//super.receiveRtuData(d) ;
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_green(this.act), null);
		setProgressVisible(0) ;
		isClickButton = false ;
		if (isFirst) {
			isFirst = false ;
			setBtnBackground(4,0);
		}

		Data_F1 data = (Data_F1)d.subData ;
		if (data != null) {
			displayWifiData(data) ;
			if (fragment_04 != null) {
				fragment_04.setRtuData(null, null,data,null,++receiveWifiDataNum);
			}
		}else {
			ToastUtils.show(act, "F1接收数据为空");
		}

		if (!endReqFlag) {
			queryF1Once();
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