package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blg.rtu.frmFunction.bean.DoorInfo;
import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F1.Data_F1;
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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;



public class F_1_0 extends FrmParent {

	private Spinner spinner;
	private Spinner spinner2;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;
	private ArrayAdapter<SpinnerVO> spinnerAdapter2;
	private RequestQueue queue;
	private TextView tv_jiaquan ;
	private TextView tv_open ;
	private TextView tv_close ;
	private TextView tv_stop ;
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

	private DoorInfo doorInfo ;
	private DoorStatus doorStatus ;
	public String currentID = "" ;
	public boolean isServering = false ;
	public boolean netServerErr = false ;
	public Callback.Cancelable httpGet ;
	private long seconds30 = 30 * 1000 ;
	private long minute10 = 10 * 60 * 1000 ;
	private long minute5 = 5 * 60 * 1000 ;
	private long minute2 = 2 * 60 * 1000 ;
	private long minute30 = 30 * 60 * 1000 ;
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

		spinner = (Spinner)view.findViewById(R.id.spinner_doorList);
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
		tv_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ToastUtils.show(act, "点击开门");
				//if (Util.checkIsHasLearned(act)) {
				if (true) {
					setProgressVisible(1);
					if (SharepreferenceUtils.getIsWifi(act)) {
						httpGet.cancel();
						setCommand(1);
					}else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						}else {
							//doorContralServer(currentID, "F1", "2");
							doorContralServer("0102030405", "F1", "2");
						}
					}
				}
			}
		});
		pb_open = (ProgressBar) view.findViewById(R.id.pb_open);

		tv_close = (TextView) view.findViewById(R.id.tv_close) ;
		tv_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门");
				//if (Util.checkIsHasLearned(act)) {
				if (true) {
					setProgressVisible(2);
					if (SharepreferenceUtils.getIsWifi(act)) {
						httpGet.cancel();
						setCommand(2);
					}else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						}else {
							//doorContralServer(currentID, "F1", "1");
							doorContralServer("0102030405", "F1", "1");
						}
					}
				}
			}
		});
		pb_close = (ProgressBar) view.findViewById(R.id.pb_close);

		tv_stop = (TextView) view.findViewById(R.id.tv_stop) ;
		tv_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止");
				//if (Util.checkIsHasLearned(act)) {
				if (true) {
					setProgressVisible(3);
					if (SharepreferenceUtils.getIsWifi(act)) {
						httpGet.cancel();
						setCommand(3);
					}else {
						if (getCurrentIDIsempty()) {
							ToastUtils.show(act, "没有可操作的门！");
						}else {
							//doorContralServer(currentID, "F1", "3");
							doorContralServer("0102030405", "F1", "3");
						}
					}

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
	public boolean getCurrentIDIsempty() {
		if (spinnerAdapter1.isEmpty()) {
			return true ;
		}else {
			currentID = spinnerAdapter1.getItem(0).getName() ;
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
			// TODO Auto-generated method stub

		}

		@Override
		public void onValueSelected(int arg0, SliceValue value) {
			// TODO Auto-generated method stub
			if (arg0 == 1) {
				Toast.makeText(act, "门打开的角度: " +(int) value.getValue()+"度", Toast.LENGTH_SHORT).show();
			}else if (arg0 == 2) {
				Toast.makeText(act, "门闭合的角度: " +(int) value.getValue()+"度", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 获取数据
	 */
	private void setPieChartData(){
			values.clear();
		for (int i = 0; i < data.length; ++i) {
			SliceValue sliceValue = new SliceValue((float) data[i], colors[i]);//这里的颜色是我写了一个工具类 是随机选择颜色的
			values.add(sliceValue);
		}
	}

	/**
	 * 初始化
	 */
	private void initPieChart() {
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
				for (int i = 0; i < arr.length; i++) {
					spinnerAdapter1.add(new SpinnerVO(i + "", arr[i]));
					spinnerAdapter1.notifyDataSetChanged();
				}
			}
		}
	}

	private void putSpinnerValue1(){
	/*	spinnerAdapter1.add(new SpinnerVO("0", "1号门")) ;*/
		spinnerAdapter1.add(new SpinnerVO("0", "0102030405")) ;
		//updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
	}
	private void putSpinnerValue2(){
		spinnerAdapter2.add(new SpinnerVO("0", "服务通信")) ;
		spinnerAdapter2.add(new SpinnerVO("1", "Wifi通信")) ;
	}

	public void setCurrentID(String s) {
		this.currentID = s ;
	}

	private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (!SharepreferenceUtils.getIsWifi(act)) {
				if (parent.getId() == spinner.getId()) {
					currentID = parent.getSelectedItem().toString();
					act.frgTool.f_1_2.setCurrentPosition(position);
					act.frgTool.f_1_2.setCurrentID(currentID);
				}
			}else {
				ToastUtils.show(act, "请注意：当前通信类型为WIFI");
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	private class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner2.getId()){
				if (position == 0) {
					//ToastUtils.show(act, "通信对象：服务器");
					SharepreferenceUtils.saveIsWifi(act, false);
					act.connectWifiAndServer() ;
				}else {
					//ToastUtils.show(act, "通信对象：WIFI");
					SharepreferenceUtils.saveIsWifi(act, true);
					act.connectWifiAndServer() ;
				}
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}


	public void doorContralServer(final String dtuId, String code, String flag) {
		isServering = true ;
		String url = "http://39.106.112.210:8090/door/door/state.act?" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		requestParams.addBodyParameter("code", code);
		requestParams.addBodyParameter("flag", flag);
		LogUtils.e("门控制服务", requestParams.toString());
		httpGet = x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if (act.delayMillis != seconds30) {
					act.delayMillis = seconds30 ;
				}
				netServerErr = false ;
				isServering = false ;
				setProgressVisible(0) ;
				JSONObject jsonResult = null;

				try {
					jsonResult = new JSONObject(result);
					String returnDtuId = jsonResult.getString("dtuId");
					if ("null".equals(returnDtuId) || "".equals(returnDtuId) || null == returnDtuId) {
						ToastUtils.show(act, "产品ID为空，数据未知!");
					}else {
						if (dtuId.equals(returnDtuId)) {
							String code = jsonResult.getString("succ");
							if (code.equals("1")) {
								Gson gson = new Gson();
								String data = jsonResult.getString("rltState");
								doorStatus = gson.fromJson(data,DoorStatus.class);
								if(null != doorStatus) {
									act.updateConnectedStatus(true);
									displayServiceData(doorStatus) ;
									ToastUtils.show(act, "服务获取数据成功");
								}else {
									ToastUtils.show(act, "服务获取数据为空！");
								}
							}else {
								String msg = jsonResult.getString("error") ;
								if (msg.equals("设备尚未上线，命令发送失败！")) {
									ToastUtils.show(act, "服务获取数据失败："+ "门锁设备未上线！");
									act.delayMillis = minute10 ; //设备未上线，10分钟后再试
								}else if (msg.equals("超时")) {
									ToastUtils.show(act, "服务获取数据失败："+ "门锁设备回复数据超时！");
									act.delayMillis = minute2 ; //设备回复超时，2分钟后再试
								}

							}
						}else {
							ToastUtils.show(act, "服务获取数据返回地址与请求地址不一致!");
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				isServering = false ;
				netServerErr = true ;
				act.delayMillis = minute30 ; //服务异常，30分钟后再试
				ToastUtils.show(act, "服务获取数据错误："+ex.getMessage());
				LogUtils.e("onError", "请求失败");
				setProgressVisible(0) ;

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
				isServering = false ;
				setProgressVisible(0) ;
			}

			@Override
			public void onFinished() {
				isServering = false ;
				setProgressVisible(0) ;
			}
		});
		//get.cancel();
	}
	public void displayServiceData(DoorStatus doorStatus) {
		//甲醛浓度
		if (!checkNull(doorStatus.getHcho())){
			tv_jiaquan.setText(doorStatus.getHcho()+"");
		}else {
			tv_jiaquan.setText("未知!");
		}

		if (!checkNull(doorStatus.getDoorState())) {
			setDoorButtonImg(doorStatus.getDoorState()) ; //门控制按钮状态
		}else {
			setDoorButtonImg(0) ;
		}

		if (!checkNull(doorStatus.getAngle())) {
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
		if (positon== 2) {
			tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
			tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
		}else if (positon == 1) {
			tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
			tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
		}else if (positon == 3) {
			tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
			tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
		}else {
			tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
		}
	}

	private void setPieChart(int open){
		int close = 0 ;
		if (open <= 180) {
			close = 180 - open ;
			data[1] = open ;
			data[2] = close ;
			setPieChartData() ;
			initPieChart() ;
		}else {
			data[1] = 0 ;
			data[2] = 180 ;
			setPieChartData() ;
			initPieChart() ;
			ToastUtils.show(act, "门角度超出范围:" + open);
		}
	}

	private boolean checkNull(Object o) {
		if (o == null) {
			return true ;
		}
		return "null".equals(o.toString()) ;
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

	private void setCommand(int command) {
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
		super.receiveRtuData(d) ;
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_green(this.act), null);
		
		Data_F1 data = (Data_F1)d.subData ;
		if (data != null) {
			displayWifiData(data) ;
		}else {
			ToastUtils.show(act, "F1接收数据为空");
		}
	}

	private void displayWifiData(Data_F1 data) {
		//甲醛浓度
		if (data.getJiaQuan() == 0) {
			tv_jiaquan.setText("0.000") ;
		}else {
			tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree(
					(data.getJiaQuan() / 1000) + "" + (data.getJiaQuan() % 1000)));
		}

		setDoorButtonImg(data.getDoorStatus()) ; //门控制按钮状态
		setPieChart(data.getDoorOpen()) ; //门开关角度
		setDoorPowerImg(data.isNormalPower() ? 0 : 1) ; //电池欠压
		setDoorAlarmImg(data.isDoorNormal() ? 0 : 1) ; //门关门故障

		act.frgTool.f_1_1.displayWifiData(data);//显示第二页数据
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