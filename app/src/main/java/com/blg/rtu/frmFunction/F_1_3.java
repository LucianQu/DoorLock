package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.util.AppUtils;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class F_1_3 extends FrmParent {

	private View view ;

	private ImageView flag ;
	private EditText npInput ;
	private ImageView sendBtn ;
	private ImageView clearBtn ;

	private TextView tvLearn ;
	private boolean isLearning = false;

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
		View view = inflater.inflate(R.layout.f_1_03, container, false);

		tvLearn = (TextView) view.findViewById(R.id.tv_door_learn) ;
		tvLearn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isLearning = true ;
				ToastUtils.show(act,"门学习");
			}
		});

		npInput = (EditText)view.findViewById(R.id.npInput) ;
		sendBtn = (ImageView)view.findViewById(R.id.npSend) ;
		clearBtn = (ImageView)view.findViewById(R.id.npClear) ;
		sendBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				sendNoProtocolData() ;
			}
		}) ;

		clearBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				clearNoProtocolData() ;
			}
		}) ;


		return view ;
	}

	private void sendNoProtocolData(){
		//if(StringValueForActivity.noProtocolSendNeedConfirm){
			new DialogConfirm().showDialog(act,
					act.getResources().getString(R.string.txtConfirmSend) ,
					new DialogConfirm.CallBackInterface(){
						@Override
						public void dialogCallBack(Object o) {
							if((Boolean)o){
								doSendNoProtocolData() ;

							}else{
							}
						}
					}) ;
		/*}else{
			doSendNoProtocolData() ;
		}*/
	}

	private void doSendNoProtocolData(){

		if (AppUtils.checkNetworkType(act) != AppUtils.TYPE_NET_WORK_DISABLED) {
			String str = this.npInput.getText().toString() ;
			if(str.equals("")){
				Toast.makeText(act, "发送内容不能为空，请输入内容！", Toast.LENGTH_SHORT).show() ;
			}else{
				str = str.trim() ;
				if(str.equals("")){
					Toast.makeText(act, "发送内容不能为空，请输入内容！", Toast.LENGTH_SHORT).show() ;
				}else{
					postMessage1(str) ;
				}
			}
		}else{
			Toast.makeText(act, "网络未连接，不能发送消息！", Toast.LENGTH_SHORT).show() ;
		}
	}

	private  void postMessage1(String mess){//请求参数个数不确定，可变长参数,可变长参数放在最后一个
		try {
			String url = "http://39.106.112.210:7090/door/door/pushAdver.act";
			RequestParams params = new RequestParams();
			params.addBodyParameter("mess",mess);
			final HttpUtils http = new HttpUtils();
			http.configCurrentHttpCacheExpiry(1000 * 5);
			http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack() {
				@Override
				public void onStart() {
					LogUtils.e("广播推送","开始");
				}

				@Override
				public void onLoading(long total, long current,
									  boolean isUploading) {
					LogUtils.e("广播推送","加载");
				}
				@Override
				public void onSuccess(ResponseInfo arg0) {
					LogUtils.e("广播推送","成功");
					JSONObject jsonResult = null;
					try {
						jsonResult = new JSONObject(arg0.result.toString());
						String code = jsonResult.getString("succ");
						if (code.equals("1")) {
							ToastUtils.show(act, "消息发送成功!");
						}else {
							ToastUtils.show(act, "消息发送失败!");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(HttpException arg0,String arg1) {
					ToastUtils.show(act, "消息发送失败!");
				}
			});
		} catch (Exception e) {
			String msg = null;
			if (e instanceof InvocationTargetException) {
				Throwable targetEx = ((InvocationTargetException) e)
						.getTargetException();
				if (targetEx != null) {
					msg = targetEx.getMessage();
					ToastUtils.show(act, "消息发送异常！");
				}
			} else {
				msg = e.getMessage();
				ToastUtils.show(act, "消息发送异常！");
			}
			e.printStackTrace();
		}
	}

	private void clearNoProtocolData(){
		new DialogConfirm().showDialog(act,
				act.getResources().getString(R.string.txtConfirmClear) ,
				new DialogConfirm.CallBackInterface(){
					@Override
					public void dialogCallBack(Object o) {
						if((Boolean)o){
							npInput.setText("") ;
						}else{
						}
					}
				}) ;
	}

	public boolean learningClick() {
		return isLearning ;
	}

	public void setLearningClickStatus(boolean isLearning) {
		this.isLearning = isLearning ;
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