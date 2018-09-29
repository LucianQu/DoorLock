package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.cd10_50.Data_10_50;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.ImageUtil;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.ToastUtils;

import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.LogUtils;

public class F_01_010  extends FrmParent {
	private final static int requestLen_1 = 10 ;
	private final static int requestLen_2 = 10 ;

	private TextView title ;
	private EditText item01 ;
	private EditText item02 ;
	private ImageView btnSet1 ;
	private ImageView btnRead ;

	private boolean receiveWifiData = false ;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = Code206.cd_50 ;
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
		View view = inflater.inflate(R.layout.f_func_01_010, container, false);
		
		title = (TextView)view.findViewById(R.id.f_01_010_Title) ;
		funcFrm = (FrameLayout)view.findViewById(R.id.f_01_010_Frm) ;
		cover = (LinearLayout)view.findViewById(R.id.f_01_010_Load) ;
		
		item01 = (EditText)view.findViewById(R.id.func_01_010_item01);
		item02 = (EditText)view.findViewById(R.id.func_01_010_item02);

		item01.setFilters(new InputFilter[]{new InputFilter.LengthFilter(requestLen_1)});
		item02.setFilters(new InputFilter[]{new InputFilter.LengthFilter(requestLen_2)});

		String str = Preferences.getInstance().getString(Constant.func_vk_01_010_01) ;
		if(!str.equals(Constant.errorStr)){
			item01.setText(str); 
		}
		
		str = Preferences.getInstance().getString(Constant.func_vk_01_010_02) ;
		if(!str.equals(Constant.errorStr)){
			item02.setText(str); 
		}
		item01.addTextChangedListener(new MyTextWatcher(Constant.func_vk_01_010_01));
		item02.addTextChangedListener(new MyTextWatcher(Constant.func_vk_01_010_02));

		btnSet1 = (ImageView)view.findViewById(R.id.btn_set);
		btnRead = (ImageView)view.findViewById(R.id.btn_read);
		resultDt = (TextView)view.findViewById(R.id.resultDatetime);
		//设置监听器
		title.setOnClickListener(titleClickLisn) ;

		btnRead.setOnClickListener(btnReadLisn);
		str = Preferences.getInstance().getString(Constant.func_vk_01_010_dt) ;
		if(!str.equals(Constant.errorStr)){
			this.resultDt.setText(str) ;
		}
		return view ;
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
		return false ;
	}

	/**
	 * 查询命令
	 */
	@Override
	public void queryCommand(){
		this.sendRtuCommand(new CommandCreator().cd_50(), true) ;
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
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_problem(this.act), null);
	}
	
	/**
	 * 命令已经成功发送给后台服务后的回调
	 */
	@Override
	public void commandSended(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_blue(this.act), null);
	}
	/**
	 * 命令已经由后台服务通过网络成功发送后的回调
	 */
	@Override
	public void commandSendedCallBack(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_red(this.act), null); 
	}
	/**
	 * 功能项右侧图标复原
	 */
	@Override
	public void resetLabelImg(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item001(this.act), null, ImageUtil.getTitlRightImg_gray(this.act), null); 
	}

	public boolean getRecieveWifiData() {
		return receiveWifiData ;
	}

	public void setReceiveWifiData(boolean is) {
		receiveWifiData = is ;
	}
	/**
	 * 收到数据
	 * @param d
	 */
	@Override
	public void receiveRtuData(RtuData d){
		//hideLoadCover();
		//super.receiveRtuData(d) ;
		receiveWifiData = true ;
		act.cancelQuery50();
		LogUtils.e("Lucian-->接收Wifi数据标志", receiveWifiData + "");
		try {
			String deviceID = SharepreferenceUtils.getDeviceId(act) ;
			String password = SharepreferenceUtils.getPassword(act) ;
			LogUtils.e("Lucian-->已存储设备列表", deviceID);
			LogUtils.e("Lucian-->已存储密码列表", password);
			Data_10_50 sd = (Data_10_50)d.subData ;
			LogUtils.e("Lucian-->50H", sd.toString());
			if (null != d && null != sd) {
				item01.setText(d.getRtuId() + "");
				item02.setText(sd.getPassWord());

				if (!"".equals(deviceID)) {
					if (deviceID.contains(d.getRtuId())) {
						LogUtils.e("Lucian-->存储ID不为空","包含该ID");
						SharepreferenceUtils.saveHasLearn(act, true);
						LogUtils.e("Lucian-->存储ID不为空","包含该ID，获取密码");
						String[] listId = deviceID.split("-");
						String[] listPassword = SharepreferenceUtils.getPassword(act).split("-");
						for (int i = 0; i < listId.length; i++) {
							if (d.getRtuId().equals(listId[i])) {
								SharepreferenceUtils.saveComPassword(act, listPassword[i]);
								LogUtils.e("Lucian-->点击学习按钮","已经学习过，获取地址密码 " +listPassword[i]);
							}
						}
						act.setDoorId(d.getRtuId());
						act.frgTool.f_1_0.setCommand(0);

					} else {//连接设备未学习，存储过ID
						LogUtils.e("Lucian-->存储ID不为空","但没有存储该ID");
						learningClick(d,sd,deviceID,password) ;
						//ToastUtils.show(act, "该设备未学习，请先学习！");
					}
				} else {//连接设备未学习，未存储过ID
					LogUtils.e("Lucian-->存储ID为空","第一次学习");
					learningClick(d,sd,deviceID,password) ;
					//ToastUtils.show(act, "该设备未学习，请先学习！");
				}

			}
		}catch (Exception e) {
			ToastUtils.show(act, e.getMessage());
		}
		LogUtils.e("Lucian-->最新设备列表", SharepreferenceUtils.getDeviceId(act));
		LogUtils.e("Lucian-->最新密码列表", SharepreferenceUtils.getPassword(act));
		act.frgTool.f_1_0.updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
		act.frgTool.f_1_2.updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
		act.frgTool.f_1_0.startTimer();
	}

	private void learningClick(RtuData d,Data_10_50 sd,String deviceID, String password) {
		if (act.frgTool.f_1_3.learningClick()) {
			LogUtils.e("Lucian-->点击学习按钮","开始学习");
			act.frgTool.f_1_3.onceComReceiveTrue = true ;
			SharepreferenceUtils.saveHasLearn(act, true);
			act.frgTool.f_1_3.setLearningClickStatus(false);
			if (!"".equals(deviceID)) {
				String[] listId = deviceID.split("-");
				String[] listPassword = SharepreferenceUtils.getPassword(act).split("-");
				if (deviceID.contains(d.getRtuId())) {
					LogUtils.e("Lucian-->点击学习按钮","非第一次学习，包含该ID，已经学习过，不在学习");
					ToastUtils.show(act, "设备已经学习过，不在学习!");
					int position = -1;
					for (int i = 0; i < listId.length; i++) {
						if (d.getRtuId().equals(listId[i])) {
							SharepreferenceUtils.saveComPassword(act,listPassword[i]);
							LogUtils.e("Lucian-->点击学习按钮","已经学习过，获取地址密码 " +listPassword[i]);
							position = i;
						}
					}
							/*if (position != -1) {
								String ids = "";
								String pws = "";
								for (int j = 0; j < listId.length; j++) {
									if (j == 0) {
										ids = listId[0];
										pws = listPassword[0];
									} else {
										ids = ids + "-" + listId[j];
										pws = pws + "-" + listPassword[j];
									}
								}
								SharepreferenceUtils.saveDeviceId(act, ids);
								SharepreferenceUtils.savePassword(act, pws);
							}*/
				} else {
					LogUtils.e("Lucian-->点击学习按钮","非第一次学习，不包含该ID，学习成功"+" 地址" +  d.getRtuId()+" 密码" +  sd.getPassWord());
					SharepreferenceUtils.saveComPassword(act,sd.getPassWord());
					SharepreferenceUtils.saveDeviceId(act, deviceID + "-" + d.getRtuId());
					SharepreferenceUtils.savePassword(act, password + "-" + sd.getPassWord());
				}
			} else {
				LogUtils.e("Lucian-->点击学习按钮","第一次学习，学习成功"+" 地址" +  d.getRtuId()+" 密码" +  sd.getPassWord());
				SharepreferenceUtils.saveComPassword(act,sd.getPassWord());
				SharepreferenceUtils.saveDeviceId(act, d.getRtuId());
				SharepreferenceUtils.savePassword(act, sd.getPassWord());
			}
			act.setDoorId(d.getRtuId());
			act.frgTool.f_1_0.setCommand(0);
		} else {
			LogUtils.e("Lucian-->未点击学习按钮","请先点击");
			SharepreferenceUtils.saveComPassword(act,sd.getPassWord());
			SharepreferenceUtils.saveHasLearn(act, false);
			ToastUtils.show(act, "该设备未学习，请先学习！");
		}
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