package com.blg.rtu.frmFunction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.cdD4.Data_D4;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.ImageUtil;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.server.CoreThread;

public class F_02_100 extends FrmParent {

	private TextView title ;
	private TextView item01 ;
	
	private ImageView btnRead ;
	
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = Code206.cd_D4;
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cntFrmOpened = false ;
		loading = false ;
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_func_02_100, container, false);
		
		title = (TextView)view.findViewById(R.id.f_02_100_Title);
		funcFrm = (FrameLayout)view.findViewById(R.id.f_02_100_Frm) ;
		cover = (LinearLayout)view.findViewById(R.id.f_02_100_Load) ;
		
		item01 = (TextView)view.findViewById(R.id.func_02_100_item1) ;
		
		String str = Preferences.getInstance().getString(Constant.func_vk_02_100_01) ;
		if(!str.equals(Constant.errorStr)) {
			item01.setText(str);
		}
		
		btnRead = (ImageView)view.findViewById(R.id.btn_read);
		resultDt = (TextView) view.findViewById(R.id.resultDatetime) ;
		
		title.setOnClickListener(titleClickLisn) ;
		btnRead.setOnClickListener(btnReadLisn) ;
		
		str = Preferences.getInstance().getString(Constant.func_vk_02_100_dt) ;
		if(!str.equals(Constant.errorStr)) {
			this.resultDt.setText(str);
		}
		return view ;
	}

	/**
	 * 查询命令前进行检查
	 * @return
	 */
	@Override
	protected boolean checkBeforeQuery(boolean showDialog) {
		return true;
	}

	@Override
	protected boolean checkBeforeSet(boolean showDialog) {
		return false;
	}
	
	/**
	 * 查询命令
	 */
	@Override
	protected void queryCommand() {
		CoreThread.getInstance().newRtuId(F_01_100.getInstance().getRtuSelectedItem().replaceAll(" ", ""));
		this.sendRtuCommand(new CommandCreator().cd_D4(null), false) ;
	}

	@Override
	protected void setCommand() {
	}
	
	/**
	 * 查询或设置命令发送前检查出问题后的回调
	 */
	@Override
	protected void commandHasProblem() {
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item002(this.act), null, ImageUtil.getTitlRightImg_problem(this.act), null);
		
	}
	/**
	 * 命令已经成功发送给后台服务后的回调
	 */
	@Override
	protected void commandSended() {
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item002(this.act), null, ImageUtil.getTitlRightImg_blue(this.act), null) ;
	}

	@Override
	public void commandSendedCallBack() {
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item002(this.act), null, ImageUtil.getTitlRightImg_red(this.act), null);
	}

	@Override
	public void resetLabelImg() {
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item002(this.act), null, ImageUtil.getTitlRightImg_gray(this.act), null);
	}
	
	
	
	@Override
	public void receiveRtuData(RtuData d) {
		super.receiveRtuData(d);
		String simIccID;
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item002(this.act), null, ImageUtil.getTitlRightImg_green(this.act), null);
		Object subD = d.subData;
		if(subD != null) {
			if(subD instanceof Data_D4) {
				Data_D4 sd = (Data_D4)subD ;
				simIccID = sd.getSimIccId();
				if(simIccID != null) {
					item01.setText(simIccID);
				}else{
					item01.setText("");
				}
			}
		}
		Preferences.getInstance().putString(Constant.func_vk_02_100_dt, this.resultDt.getText().toString()) ;
	}



	@Override
	public void outSetData(Vo2Xml vo) {
	}

	@Override
	public void inSetData(Vo2Xml vo) {
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	public void onResume() {
		super.onResume();
	}



	@Override
	public void onPause() {
		super.onPause();
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
