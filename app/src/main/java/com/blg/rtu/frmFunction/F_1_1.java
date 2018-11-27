package com.blg.rtu.frmFunction;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.util.permission.PermissionHelper;
import com.blg.rtu.util.permission.PermissionInterface;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.xuanyuanxing.camera.VideoPlayTool;
import com.xuanyuanxing.camera.XuanYuanXingP2PTool;
import com.xuanyuanxing.engine.ClientP2pListener;

public class F_1_1 extends FrmParent implements ClientP2pListener {
	XuanYuanXingP2PTool p2PTool;
	private EditText edt_user ;
	private EditText edt_password ;

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

	public void requestSuccess() {
		connect();
	}

	@Override
	public View onCreateView(
			final LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_login, container, false);

		edt_user = (EditText) view.findViewById(R.id.et_user) ;

		edt_password = (EditText) view.findViewById(R.id.et_pwd) ;

        edt_user.setText(uuid);
        edt_password.setText(pwd);

		Button login = view.findViewById(R.id.btn_login) ;
		login.setOnClickListener(new View.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.M)
			@Override
			public void onClick(View view) {
				if (shouldShowRequestPermissionRationale( Manifest.permission
                .WRITE_EXTERNAL_STORAGE)) {
					new DialogConfirm().showDialog(act,
							getResources().getString(R.string.quanxian) ,
							new DialogConfirm.CallBackInterface(){
								@Override
								public void dialogCallBack(Object o) {
									if((Boolean)o){
										act.requestPermissions();
									}else{
									}
								}
							}) ;
        		}else {
					connect();
				}
			}
		});

		return view ;
	}

	//摄像头默认用户
	private String defaultUser = "admin";
	//摄像头密码
	private String pwd = "a12345678";
	//摄像头uid
	private String uuid = "6UH2B3TYDTC8ZWF2111A";
	private void connect() {
	    if (edt_user.getText().toString().equals("")) {
            showAlarmDialog("请输入用户名");
            return;
        }else {
	        uuid = edt_user.getText().toString() ;
        }
        if (edt_password.getText().toString().equals("")) {
	        showAlarmDialog("请输入密码");
	        return;
        }else {
	        pwd = edt_password.getText().toString() ;
        }

		p2PTool = new XuanYuanXingP2PTool(uuid, defaultUser, pwd, "测试摄像机");
		//设置连接监听
		p2PTool.setClientP2pListener(this);
		//开启连接
		p2PTool.Start();
	}

	@Override
	public void P2pState(int state) {
		if (state == 2) {//成功
			//showAlarmDialog("连接成功");
			Intent intent = new Intent(act, VideoPlayActivity.class);
			intent.putExtra("UID",uuid) ;
			intent.putExtra("PW",pwd) ;
			startActivity(intent);
		} else {
			Log.e("Lucian-->连接视频", "当前状态" + state);
			//-6 密码错误 其他 都连接失败
			if (state == -6) {
				// Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
				// Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
				showAlarmDialog("密码错误");
			} else if (state < 0) {
				// Toast.makeText(this,"连接失败",Toast.LENGTH_SHORT).show();
				p2PTool.Stop(); //断开连接
				showAlarmDialog("连接失败,请确认是否连接上设备的wifi和密码是否正确!");
			}
		}
	}

	public void showAlarmDialog(String msg) {
		new AlertDialog.Builder(act)
				.setTitle("提示")
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						//BlgActivity.this.finish();
					}
				})
				.show();
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