package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.cdEF.Data_EF;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.ImageUtil;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;

import java.util.ArrayList;
import java.util.List;

public class F_1_0 extends FrmParent {

	private Spinner item01;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;

	private TextView tv_jiaquan ;
	private TextView tv_open ;
	private TextView tv1;
	private TextView item02 ;

	private ImageView btnRead ;
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = Code206.cd_EF ;
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

		item01 = (Spinner)view.findViewById(R.id.spinner_doorList);
		spinnerAdapter1 = new ArrayAdapter<SpinnerVO>(this.act, R.layout.spinner_style, new ArrayList<SpinnerVO>());
		this.putSpinnerValue1();
		spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item);
		// 将adapter 添加到spinner中
		item01.setAdapter(spinnerAdapter1);
		item01.setOnItemSelectedListener(new SpinnerSelectedListener());

		tv_jiaquan = (TextView) view.findViewById(R.id.tv_jiaquan) ;
		tv_jiaquan.setText("0.001");

		tv_open = (TextView) view.findViewById(R.id.tv_open) ;
		tv_open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击开门");
			}
		});
		//title = (TextView)view.findViewById(R.id.f_01_040_Title) ;
		//funcFrm = (FrameLayout)view.findViewById(R.id.f_01_040_Frm) ;
		//cover = (LinearLayout)view.findViewById(R.id.f_01_040_Load) ;
		
		//tv1 = (TextView)view.findViewById(R.id.func_01_040_item01);
		//item02 = (TextView)view.findViewById(R.id.func_01_040_item02);
		
		
		String str = Preferences.getInstance().getString(Constant.func_vk_01_040_01) ;
		if(!str.equals(Constant.errorStr)){
			tv1.setText(str);
		}
		
		str = Preferences.getInstance().getString(Constant.func_vk_01_040_02) ;
		if(!str.equals(Constant.errorStr)){
			item02.setText(str); 
		}
		
		//btnRead = (ImageView)view.findViewById(R.id.btn_read);
		//resultDt = (TextView)view.findViewById(R.id.resultDatetime);
		
		//设置监听器
		//title.setOnClickListener(titleClickLisn) ;
		//btnRead.setOnClickListener(btnReadLisn);
		
		//str = Preferences.getInstance().getString(Constant.func_vk_01_040_dt) ;
		//if(!str.equals(Constant.errorStr)){
			//this.resultDt.setText(str) ;
		//}

		return view ;
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
			if(parent.getId() == item01.getId()){

			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
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
	
	/**
	 * 查询或设置命令发送前检查出问题后的回调
	 */
	@Override
	public void commandHasProblem(){
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_problem(this.act), null);
	}
	
	/**
	 * 命令已经成功发送给后台服务后的回调
	 */
	@Override
	public void commandSended(){
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_blue(this.act), null);
	}
	
	/**
	 * 命令已经由后台服务通过网络成功发送后的回调
	 */
	@Override
	public void commandSendedCallBack(){
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_red(this.act), null);
	}
	/**
	 * 功能项右侧图标复原
	 */
	@Override
	public void resetLabelImg(){
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_gray(this.act), null);
	}

	/**
	 * 收到数据
	 * @param d
	 */
	@Override
	public void receiveRtuData(RtuData d){
		super.receiveRtuData(d) ;
		//this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_green(this.act), null);
		
		Data_EF sd = (Data_EF)d.subData ;
		
		String str1 = sd.getHard1() + "." + sd.getHard2() + "." + sd.getHard3() ;
		String str2 = sd.getSoft1() + "." + sd.getSoft2() + "." + sd.getSoft3() ;
		
		tv1.setText(str1) ;
		item02.setText(str2) ;
		
		Preferences.getInstance().putString(Constant.func_vk_01_040_01, str1) ;
		Preferences.getInstance().putString(Constant.func_vk_01_040_02, str2) ;
		
		Preferences.getInstance().putString(Constant.func_vk_01_040_dt, this.resultDt.getText().toString()) ;
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