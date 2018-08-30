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
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.cdCA_DA.Data_CA_DA;
import com.blg.rtu.util.AppUtils;
import com.blg.rtu.util.DialogAlarm;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.SharepreferenceUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class F_1_3 extends FrmParent {

	private View view ;

	private ImageView flag ;
	private EditText npInput ;
	private ImageView sendBtn ;
	private ImageView clearBtn ;

	private TextView ipPortSend ;
	private EditText ipInput ;
	private EditText portInput ;

	private TextView wifiNameSend ;
	private EditText wifiName ;
	private EditText wifiPassword ;

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
		tvLearn.setEnabled(true);
		tvLearn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isLearning = true ;
				ToastUtils.show(act,"门学习");
				act.frgTool.f_01_010.queryCommand();

			}
		});

		npInput = (EditText)view.findViewById(R.id.npInput) ;
		wifiName = (EditText)view.findViewById(R.id.edt_name) ;
		wifiPassword = (EditText)view.findViewById(R.id.edt_password) ;
		wifiNameSend = (TextView) view.findViewById(R.id.tv_name_password) ;
		wifiNameSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SharepreferenceUtils.getIsWifi(act)) {
					if (act.tcpConnected) {
						setCommand();
					}else {
						ToastUtils.show(act, "Wifi未连接设备，无法设置");
					}
				}else {
					ToastUtils.show(act, "当前非Wifi连接，无法设置");
				}
			}
		});

		ipInput = (EditText)view.findViewById(R.id.edt_ip) ;
		portInput = (EditText)view.findViewById(R.id.edt_port) ;
		ipPortSend = (TextView) view.findViewById(R.id.tv_ip_port_send) ;
		ipPortSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if (checkBeforeSet()) {
                    new DialogConfirm().showDialog(act,
                            act.getResources().getString(R.string.txtConfirmSend1) ,
                            new DialogConfirm.CallBackInterface(){
                                @Override
                                public void dialogCallBack(Object o) {
                                    if((Boolean)o){
                                        String ip_Value = ipInput.getText().toString() ;
                                        String port = portInput.getText().toString() ;
                                        String message = "http://"+ip_Value+":"+port;
                                        postMessage1(message) ;
                                        //ToastUtils.show(act, "已发送");
                                    }else{
                                        //ToastUtils.show(act, "已取消");
                                    }
                                }
                            }) ;
                }
			}
		});

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

    private boolean checkBeforeSet() {
        boolean corrent = false ;
        String ip_Value = ipInput.getText().toString() ;
        if (ip_Value.length() < 7 || "".equals(ip_Value)) {
            new DialogAlarm().showDialog(act, "IP地址格式错误，请重新输入");
            return corrent ;
        }
        String port = portInput.getText().toString() ;
        if ("".equals(port)) {
            new DialogAlarm().showDialog(act, "端口号为空，请重新输入");
            return corrent ;
        }
        if (Integer.parseInt(port) > 65535) {
            new DialogAlarm().showDialog(act, "端口号范围0-65535，请重新输入");
            return corrent ;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(ip_Value);
        boolean ipAddress = mat.find();

        if (ipAddress){
            String ips[] = ip_Value.split("\\.");
            if(ips.length==4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip)<0||Integer.parseInt(ip)>255){
                            new DialogAlarm().showDialog(act, "IP地址范围错误，请重新输入");
                            return false;
                        }
                    }
                }catch (Exception e){
                    new DialogAlarm().showDialog(act, "IP地址解析异常，请重新输入");
                    return false;
                }
                corrent = true ;
                return corrent;
            }else{
                new DialogAlarm().showDialog(act, "IP地址格式错误，请重新输入");
                return false;
            }
        }else {
            new DialogAlarm().showDialog(act, "IP地址格式/范围错误，请重新输入");
        }
        return corrent ;
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
			String url =act.mIpPort +  "/door/door/pushAdver.act";
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
							ToastUtils.show(act, "发送成功!");
						}else {
							ToastUtils.show(act, "发送失败!");
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
		//this.sendRtuCommand(new CommandCreator().cd_DA(null), false) ;
	}
	
	/**
	 * 设置命令
	 */
	@Override
	protected void setCommand(){
		String name = wifiName.getText().toString() ;
		String user = wifiPassword.getText().toString() ;
		if (!name.equals("") && !user.equals("")) {
			this.sendRtuCommand(new CommandCreator().cd_DA(name, user, "", null), false);
		}else {
			ToastUtils.show(act,"数据为空，请补全!");
		}
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
		Data_CA_DA sd = (Data_CA_DA)d.subData ;
		wifiName.setText(sd.getName()) ;
		wifiPassword.setText(sd.getUser()) ;
		ToastUtils.show(act, "修改成功，请重新连接Wifi热点");
		act.frgTool.f_1_0.afterChangeWifiNameSuccess();
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