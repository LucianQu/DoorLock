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
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F1.Data_F1;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.DataTranslateUtils;
import com.blg.rtu3.utils.LogUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
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
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = Code206.cd_F4 ;
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
		// 将adapter 添加到spinner中
		spinner.setAdapter(spinnerAdapter1);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

		tv_jiaquan = (TextView) view.findViewById(R.id.tv_jiaquan) ;
		tv_jiaquan.setText("0.001");

		tv_open = (TextView) view.findViewById(R.id.tv_open) ;
		tv_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ToastUtils.show(act, "点击开门");
				setProgressVisible(1) ;
				doorContralServer("123456789012","F1","2") ;
			}
		});
		pb_open = (ProgressBar) view.findViewById(R.id.pb_open);

		tv_close = (TextView) view.findViewById(R.id.tv_close) ;
		tv_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ToastUtils.show(act, "点击关门");
				setProgressVisible(2) ;
				doorContralServer("123456789012","F1","1") ;
			}
		});
		pb_close = (ProgressBar) view.findViewById(R.id.pb_close);

		tv_stop = (TextView) view.findViewById(R.id.tv_stop) ;
		tv_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ToastUtils.show(act, "点击停止");
				setProgressVisible(3) ;
				doorContralServer("123456789012","F1","3") ;
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

	private void putSpinnerValue1(){
		spinnerAdapter1.add(new SpinnerVO("0", "1号门")) ;
		spinnerAdapter1.add(new SpinnerVO("1", "2号门")) ;
	}

	private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner.getId()){

			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}


	private void doorContralServer(final String dtuId, String code, String flag) {
		String url = "http://39.106.112.210:7090/door/door/state.act?" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		requestParams.addBodyParameter("code", code);
		requestParams.addBodyParameter("flag", flag);
		LogUtils.e("门控制服务", requestParams.toString());
		x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
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
								String data = jsonResult.getString("data");
								doorStatus = gson.fromJson(data,DoorStatus.class);
								if(null != doorStatus) {
									displayData(doorStatus) ;
								}else {
									ToastUtils.show(act, "返回数据为空！");
								}
							}else {
								ToastUtils.show(act, "请求失败："+ jsonResult.getString("error"));

							}
						}else {
							ToastUtils.show(act, "返回数据ID与请求ID不一致!");
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtils.show(act, "请求失败!");
				LogUtils.e("onError", "请求失败");
				setProgressVisible(0) ;
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
	private void displayData(DoorStatus doorStatus) {
		//甲醛浓度
		if (!checkNull(doorStatus.getHcho())){
			tv_jiaquan.setText(doorStatus.getHcho()+"");
		}else {
			tv_jiaquan.setText("未知!");
		}

		if (!checkNull(doorStatus.getDoorState())) {
			if (doorStatus.getDoorState()== 2) {
				tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
				tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
				tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			}else if (doorStatus.getDoorState()== 1) {
				tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
				tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
				tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			}else if (doorStatus.getDoorState()== 3) {
				tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_red_bg));
				tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
				tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			}else {
				tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
				tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
				tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			}
		}else {
			tv_stop.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_open.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
			tv_close.setBackground(getResources().getDrawable(R.drawable.tv_selected_bg));
		}

		if (!checkNull(doorStatus.getAngle())) {
			setPieChart(doorStatus.getAngle()) ;
		}else {
			setPieChart(0);
		}

		if (doorStatus.getWarnStates()[0] == 1) {
			imgDoorPower.setImageResource(R.mipmap.ic_circle_red);
		}else if (doorStatus.getWarnStates()[0] == 0){
			imgDoorPower.setImageResource(R.mipmap.ic_circle_green);
		}else {
			imgDoorPower.setImageResource(R.mipmap.ic_circle_gray1);
		}

		if (doorStatus.getWarnStates()[2] == 1) {
			imgDoorAlarm.setImageResource(R.mipmap.ic_circle_red);
		}else if (doorStatus.getWarnStates()[2] == 0) {
			imgDoorAlarm.setImageResource(R.mipmap.ic_circle_green);
		}else {
			imgDoorPower.setImageResource(R.mipmap.ic_circle_gray1);
		}

		act.frgTool.f_1_1.displayData(doorStatus);//显示第二页数据
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
			int jq = data.getJiaQuan() ;
			tv_jiaquan.setText(DataTranslateUtils.dataFloatWithThree((jq/1000) + "." +(jq%1000)));

		}else {
			ToastUtils.show(act, "F1接收数据为空");
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