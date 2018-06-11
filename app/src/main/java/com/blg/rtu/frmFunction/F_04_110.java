package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.blg.rtu.protocol.p206.cd16_56.Data_16;
import com.blg.rtu.protocol.p206.cd16_56.Data_56;
import com.blg.rtu.protocol.p206.cd16_56.Param_16;
import com.blg.rtu.util.Constant;
import com.blg.rtu.util.DialogAlarm;
import com.blg.rtu.util.ImageUtil;
import com.blg.rtu.util.Preferences;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.server.CoreThread;

public class F_04_110  extends FrmParent {
	
	private final static int requestLen_6 = 6 ; 

	private TextView title ;

	private EditText item01  ;
	private TextView item02 ;
	
	

	private ImageView btnSet ;
	private ImageView btnRead ;
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (MainActivity)activity ;
		this.queryCommandCode = Code206.cd_56 ;
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
		View view = inflater.inflate(R.layout.f_func_04_110, container, false);

		title = (TextView)view.findViewById(R.id.f_04_110_Title) ;
		funcFrm = (FrameLayout)view.findViewById(R.id.f_04_110_Frm) ;
		cover = (LinearLayout)view.findViewById(R.id.f_04_110_Load) ;
		
		item01 = (EditText)view.findViewById(R.id.func_04_110_item01);
		item01.setFilters(new InputFilter[]{new InputFilter.LengthFilter(requestLen_6)});
		
		item02 = (TextView)view.findViewById(R.id.func_04_110_item02);
		
		
		String str = Preferences.getInstance().getString(Constant.func_vk_04_110_01) ;
		if(!str.equals(Constant.errorStr)){
			item01.setText(str); 
		}
		
		str = Preferences.getInstance().getString(Constant.func_vk_04_110_02) ;
		if(!str.equals(Constant.errorStr)){
			item02.setText(str); 
		}
		
		item01.addTextChangedListener(new MyTextWatcher(Constant.func_vk_04_110_01));
		
		btnSet = (ImageView)view.findViewById(R.id.btn_set);
		btnRead = (ImageView)view.findViewById(R.id.btn_read);
		
		resultDt = (TextView)view.findViewById(R.id.resultDatetime);
		
		//设置监听器
		title.setOnClickListener(titleClickLisn) ;
		btnSet.setOnClickListener(btnSetLisn);
		btnRead.setOnClickListener(btnReadLisn);
		
		str = Preferences.getInstance().getString(Constant.func_vk_04_110_dt) ;
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
		String value = item01.getText().toString() ;//整数部分

		if(value == null || value.equals("")){
			if(showDialog)new DialogAlarm().showDialog(act, "剩余水量报警值必须填写！") ;
			return false ;
		} 
		
		int v = Integer.valueOf(value) ;
		if(v < 0 || v > 99999999){
			if(showDialog)new DialogAlarm().showDialog(act, "剩余水量报警值必须是0~999999的数字！") ;
			return false ;
		}

		return true ;
	}
	
	/**
	 * 查询命令
	 */
	@Override
	protected void queryCommand(){
		CoreThread.getInstance().newRtuId(F_01_100.getInstance().getRtuSelectedItem().replaceAll(" ", ""));
		this.sendRtuCommand(new CommandCreator().cd_56(null), false) ;
	}
	
	/**
	 * 设置命令
	 */
	@Override
	protected void setCommand(){
		String value = item01.getText().toString() ;//整数部分

		Param_16 p = new Param_16() ;
		if(value == null || value.equals("")){
			p.setWaterRemainAlarm_0to999999(0) ;
		}else{
			p.setWaterRemainAlarm_0to999999(Integer.valueOf(value)) ;
		}
		CoreThread.getInstance().newRtuId(F_01_100.getInstance().getRtuSelectedItem().replaceAll(" ", ""));
		this.sendRtuCommand(new CommandCreator().cd_16(p, null), false) ;
	}
	
	/**
	 * 查询或设置命令发送前检查出问题后的回调
	 */
	@Override
	public void commandHasProblem(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item007(this.act), null, ImageUtil.getTitlRightImg_problem(this.act), null);
	}
	
	/**
	 * 命令已经成功发送给后台服务后的回调
	 */
	@Override
	public void commandSended(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item007(this.act), null, ImageUtil.getTitlRightImg_blue(this.act), null);
	}
	
	/**
	 * 命令已经由后台服务通过网络成功发送后的回调
	 */
	@Override
	public void commandSendedCallBack(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item007(this.act), null, ImageUtil.getTitlRightImg_red(this.act), null); 
	}
	/**
	 * 功能项右侧图标复原
	 */
	@Override
	public void resetLabelImg(){
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item007(this.act), null, ImageUtil.getTitlRightImg_gray(this.act), null); 
	}

	/**
	 * 收到数据
	 * @param d
	 */
	@Override
	public void receiveRtuData(RtuData d){
		super.receiveRtuData(d) ;
		this.title.setCompoundDrawables(ImageUtil.getTitlLeftImg_item007(this.act), null, ImageUtil.getTitlRightImg_green(this.act), null); 
//		super.scrollTo(this.btnRead) ;
		Object subD = d.subData ;
		if(subD != null){
			if(subD instanceof Data_56){
				Data_56 sd = (Data_56)subD ;
				item01.setText(sd.getWaterRemainAlarm() + "") ;
				item02.setText(sd.getWaterRemain() + "") ;
			}else if(subD instanceof Data_16){
				Data_16 sd = (Data_16)subD ;
				item01.setText(sd.getWaterRemainAlarm() + "") ;
				item02.setText("") ;
			}
		}
		Preferences.getInstance().putString(Constant.func_vk_04_110_dt, this.resultDt.getText().toString()) ;
	}
	
	/**
	 * 导出设置数据
	 */
	public void outSetData(Vo2Xml vo) {
		vo.setV_04_110_item01(item01.getText().toString()) ;
	}
	/**
	 * 导入设置数据
	 */
	public void inSetData(Vo2Xml vo) {
		item01.setText(vo.getV_04_110_item01()) ;
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