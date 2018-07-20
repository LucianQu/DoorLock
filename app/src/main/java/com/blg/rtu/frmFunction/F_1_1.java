package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.F1.Data_F1;
import com.blg.rtu.protocol.p206.F2.Data_F2;
import com.blg.rtu.protocol.p206.F3.Data_F3;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;

public class F_1_1 extends FrmParent {


	private TextView tvLockStatus;

	private ImageView imgLockInit ;
	private ImageView imgLockAlarm ;
	private ImageView imgLockPower ;


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
		View view = inflater.inflate(R.layout.f_1_01, container, false);

		tvLockStatus = (TextView) view.findViewById(R.id.tv_lock_status) ;

		imgLockInit = (ImageView) view.findViewById(R.id.img_lock_init) ;
		imgLockAlarm = (ImageView) view.findViewById(R.id.img_lock_alarm) ;
		imgLockPower = (ImageView) view.findViewById(R.id.img_lock_power) ;


		return view ;
	}

	public void displayServiceData(DoorStatus doorStatus) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (null != doorStatus) {
			//锁状态
			if (null != doorStatus.getLockStates() && doorStatus.getLockStates().length >= 3) {
				if (doorStatus.getLockStates()[0] == 1) {
					tvLockStatus.setText("开锁");
				} else if (doorStatus.getLockStates()[0] == 0) {
					tvLockStatus.setText("关锁");
				} else {
					tvLockStatus.setText("未知");
				}
			}
			//锁原点
			if (null != doorStatus.getLockStates() && doorStatus.getLockStates().length >= 2) {
				if (doorStatus.getLockStates()[1] == 1) {
					imgLockInit.setImageResource(R.mipmap.ic_circle_green);
				} else if (doorStatus.getLockStates()[1] == 0) {
					imgLockInit.setImageResource(R.mipmap.ic_circle_red);
				} else {
					imgLockInit.setImageResource(R.mipmap.ic_circle_gray1);
				}
			}
			//锁报警
		/*if (doorStatus.getLockStates()[0] == 1) {
			imgLockAlarm
		}else if (doorStatus.getLockStates()[0] == 0) {
			imgLockAlarm
		}else {
			imgLockAlarm
		}*/
			//锁电源
			if (null != doorStatus.getLockStates() && doorStatus.getLockStates().length >= 1) {
				if (doorStatus.getLockStates()[0] == 1) {
					imgLockPower.setImageResource(R.mipmap.ic_circle_green);
				} else if (doorStatus.getLockStates()[0] == 0) {
					imgLockPower.setImageResource(R.mipmap.ic_circle_red);
				} else {
					imgLockPower.setImageResource(R.mipmap.ic_circle_gray1);
				}
			}
		}

	}

	public void displayWifiData(Data_F1 data) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (null != data) {
			//锁状态
			if (data.isOpenLock()) {
				tvLockStatus.setText("开锁");
			}else{
				tvLockStatus.setText("关锁");
			}
			//锁原点
			if (data.isLockInitPosition()) {
				imgLockInit.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockInit.setImageResource(R.mipmap.ic_circle_red);
			}
			//锁报警
			//锁电源

			if (data.isHasPower()) {
				imgLockPower.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockPower.setImageResource(R.mipmap.ic_circle_red);
			}
		}
	}

	public void displayWifiData2(Data_F2 data) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (null != data) {
			//锁状态
			if (data.isOpenLock()) {
				tvLockStatus.setText("开锁");
			}else{
				tvLockStatus.setText("关锁");
			}
			//锁原点
			if (data.isLockInitPosition()) {
				imgLockInit.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockInit.setImageResource(R.mipmap.ic_circle_red);
			}
			//锁报警
			//锁电源

			if (data.isHasPower()) {
				imgLockPower.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockPower.setImageResource(R.mipmap.ic_circle_red);
			}
		}
	}

	public void displayWifiData3(Data_F3 data) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (null != data) {
			//锁状态
			if (data.isOpenLock()) {
				tvLockStatus.setText("开锁");
			}else{
				tvLockStatus.setText("关锁");
			}
			//锁原点
			if (data.isLockInitPosition()) {
				imgLockInit.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockInit.setImageResource(R.mipmap.ic_circle_red);
			}
			//锁报警
			//锁电源

			if (data.isHasPower()) {
				imgLockPower.setImageResource(R.mipmap.ic_circle_green);
			}else{
				imgLockPower.setImageResource(R.mipmap.ic_circle_red);
			}
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
		//super.receiveRtuData(d) ;
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