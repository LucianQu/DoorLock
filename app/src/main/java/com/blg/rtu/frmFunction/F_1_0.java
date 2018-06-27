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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blg.rtu.frmFunction.bean.DoorInfo;
import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F1.Data_F1;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.DataTranslateUtils;
import com.blg.rtu3.utils.LogUtils;
import com.google.gson.Gson;

import org.apache.http.protocol.RequestContent;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.body.RequestBody;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class F_1_0 extends FrmParent {

	private Spinner spinner;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;

	private TextView tv_jiaquan ;
	private TextView tv_open ;
	private TextView tv_close ;
	private TextView tv_stop ;

	private PieChartView pieChart;
	private PieChartData pieChardata;
	List<SliceValue> values = new ArrayList<SliceValue>();
	private int[] data = {135,80,100,45};
	private int[] colors = {Color.parseColor("#ffffff"),Color.parseColor("#FF4040"),Color.parseColor("#CDC9C9"),Color.parseColor("#ffffff")};

	private ImageView imgDoorPower ;
	private ImageView imgDoorAlarm ;

	private TextView tv1;
	private TextView item02 ;

	private ImageView btnRead ;
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
				ToastUtils.show(act, "点击开门");
				doorContralServer("123456789012","F1","2") ;
				messSend("滑新波") ;
				//setCommand(1) ;
			}
		});

		tv_close = (TextView) view.findViewById(R.id.tv_close) ;
		tv_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门");
				doorContralServer("123456789012","F1","滑新波") ;
				//setCommand(0) ;
			}
		});
		tv_stop = (TextView) view.findViewById(R.id.tv_stop) ;
		tv_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止");
				doorContralServer("123456789012","F1","3") ;
				//setCommand(2) ;
			}
		});

		pieChart = (PieChartView) view.findViewById(R.id.pie_chart);
		pieChart.setOnValueTouchListener(selectListener);//设置点击事件监听

		imgDoorPower = (ImageView) view.findViewById(R.id.img_door_power) ;
		imgDoorAlarm = (ImageView) view.findViewById(R.id.img_door_alarm) ;

		setPieChartData();
		initPieChart();


		String str = Preferences.getInstance().getString(Constant.func_vk_01_040_01) ;
		if(!str.equals(Constant.errorStr)){
			tv1.setText(str);
		}
		
		str = Preferences.getInstance().getString(Constant.func_vk_01_040_02) ;
		if(!str.equals(Constant.errorStr)){
			item02.setText(str); 
		}

		return view ;
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

	/**
	 * 获取功能卡列表
	 */
	private void doorContralServer(String dtuId, String code, String flag) {
		String url = "http://bc8986a4.ngrok.io/door/door/state.act?" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		requestParams.addBodyParameter("code", code);
		requestParams.addBodyParameter("flag", flag);
		LogUtils.e("门控制服务", requestParams.toString());
		x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonResult = null;
				try {
					jsonResult = new JSONObject(result);
					String code = jsonResult.getString("succ");
					if (code.equals("1")) {
						Gson gson = new Gson();
						String data = jsonResult.getString("data");
						doorStatus = gson.fromJson(data,DoorStatus.class);
						if (null != doorStatus) {
							ToastUtils.show(act, doorStatus.getAngle()+doorStatus.getDoorState());
						}
					}else {
							ToastUtils.show(act, "请求失败！");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtils.e("onError", "查询失败");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}
	/**
	 * 获取功能卡列表
	 */
	private void messSend(String mess) {
		String url = "http://bc8986a4.ngrok.io/door/door/pushAdver.actmess="+mess ;

		RequestParams requestParams = new RequestParams(url);
		LogUtils.e("广告发送", requestParams.toString());
		requestParams.addBodyParameter("mess", mess);
		LogUtils.e("广告发送", requestParams.toString());
		x.http().post(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonResult = null;
				try {
					jsonResult = new JSONObject(result);
					String code = jsonResult.getString("succ");
					if (code.equals("1")) {
						ToastUtils.show(act, "广告发送成功!");
					}else {
						ToastUtils.show(act, "广告发送失败！");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtils.e("onError", "服务异常");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
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