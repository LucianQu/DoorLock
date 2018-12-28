package com.blg.rtu.frmFunction;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.util.DialogConfirm;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.LogUtils;
import com.xuanyuanxing.camera.XuanYuanXingP2PTool;
import com.xuanyuanxing.engine.ClientP2pListener;

import java.util.ArrayList;
import java.util.List;

public class F_1_1 extends FrmParent implements ClientP2pListener, AddPop1Window.Choice {
	XuanYuanXingP2PTool p2PTool;
	private EditText edt_user ;
	private EditText edt_password ;

	private AddPop1Window popWindow;
	private TextView tv_list ;
	private List<String> deviceIdList = new ArrayList<String>() ;
	private List<String> devicePwList = new ArrayList<String>() ;
	private String id ;
	private String pw ;
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

//		SharepreferenceUtils.saveJkDeviceId(act,"RNSZ5LG57U4X89J9111A-6UH2B3TYDTC8ZWF2111A");
//		SharepreferenceUtilsrepreferenceUtils.saveJkDevicePw(act,"a12345678-a12345678");
//		updateSpinnerValue(SharepreferenceUtils.getJkDeviceId(act));
	}

	public void requestSuccess() {
		connect();
	}

	private int positionId = 0 ;
	@Override
	public void senddata(String msg) {
		positionId = Integer.parseInt(msg) ;
		LogUtils.e("Lucian-->当前选择Position", positionId+"");
		if (true) {
			if (deviceIdList.size() > 0) {
					id = deviceIdList.get(positionId);
					pw = devicePwList.get(positionId) ;
					edt_user.setText(id);
					edt_password.setText(pw);
					String deviceID = SharepreferenceUtils.getJkDeviceId(act) ;
					String password = SharepreferenceUtils.getJkDevicePw(act) ;
					LogUtils.e("Lucian-->选择的监控id", id);
					LogUtils.e("Lucian-->选择的监控密码", pw);
					Log.e("Lucian--->","设备列表：" +deviceID) ;
					Log.e("Lucian--->","密码列表：" +password) ;
				}
			}
	}

	@Override
	public void longClick(final int position) {
		new DialogConfirm().showDialog(act,
				act.getResources().getString(R.string.deleteId2) ,
				new DialogConfirm.CallBackInterface(){
					@Override
					public void dialogCallBack(Object o) {
						if((Boolean)o){
							if (positionId == position) {
								LogUtils.e("Lucian-->移除的ID为当前选择Position", position+"");

							}
							String deviceID = SharepreferenceUtils.getJkDeviceId(act) ;
							String password = SharepreferenceUtils.getJkDevicePw(act) ;
							LogUtils.e("Lucian-->设备列表", deviceID);
							LogUtils.e("Lucian-->密码列表", password);
							String[] listId = deviceID.split("-");
							String[] listPassword = password.split("-");
							deviceIdList.remove(position) ;

							int postion1 = listId.length -position -1 ;
							LogUtils.e("Lucian-->position", postion1 + "");
							if (deviceIdList.size() > 0) {
								String ids = "";
								String pws = "";
								for (int j = 0; j < listId.length; j++) {
									if (j == 0) {
										if (j != postion1) {
											ids = listId[0];
											pws = listPassword[0];
										}else {
											LogUtils.e("Lucian-->移除设备ID", listId[postion1]);
											LogUtils.e("Lucian-->移除设备密码", listPassword[postion1]);
											ToastUtils.show(act, "删除监控设备：" + listId[postion1]);
										}
									} else {
										if (j != postion1) {
											if (ids.equals("")) {
												ids = listId[j];
												pws = listPassword[j];
											}else {
												ids = ids + "-" + listId[j];
												pws = pws + "-" + listPassword[j];
											}
										}else {
											LogUtils.e("Lucian-->移除设备ID", listId[postion1]);
											LogUtils.e("Lucian-->移除设备密码", listPassword[postion1]);
											ToastUtils.show(act, "删除门地址：" + listId[postion1]);
										}
									}
								}
								LogUtils.e("Lucian-->设备列表", ids);
								LogUtils.e("Lucian-->密码列表", pws);
								SharepreferenceUtils.saveJkDeviceId(act, ids);
								SharepreferenceUtils.saveJkDevicePw(act, pws);
							}else {
								SharepreferenceUtils.saveJkDeviceId(act, "");
								SharepreferenceUtils.saveJkDevicePw(act, "");
							}
							updateSpinnerValue(SharepreferenceUtils.getJkDeviceId(act));
						}else{
						}
					}
				}) ;
	}


	private int deviceNum = 0 ;
	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			String[] arr = data.split("-") ;
			deviceIdList.clear();
			if (arr.length > 0) {
				deviceNum = arr.length ;
				for (int i = 0; i < arr.length; i++) {
					deviceIdList.add(arr[arr.length -i-1]) ;
				}
			}
			popWindow = new AddPop1Window(getActivity(), deviceIdList);
			popWindow.setChoice(this);
			String passwordStr = SharepreferenceUtils.getJkDevicePw(act) ;
			String[] arrPw = passwordStr.split("-") ;
			devicePwList.clear();
			if (arrPw.length > 0) {
				for (int i = 0; i < arrPw.length; i++) {
					devicePwList.add(arrPw[arrPw.length -i-1]) ;
				}
			}
		}else {
		}

	}

	@Override
	public View onCreateView(
			final LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_login, container, false);

		tv_list = (TextView) view.findViewById(R.id.tv_list) ;
		popWindow = new AddPop1Window(getActivity(), deviceIdList);
		popWindow.setChoice(this);
		tv_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popWindow.showPopupWindow(tv_list);
			}
		});
		edt_user = (EditText) view.findViewById(R.id.et_user) ;

		edt_password = (EditText) view.findViewById(R.id.et_pwd) ;
		id = SharepreferenceUtils.getJkDeviceIdLast(act);
		pw = SharepreferenceUtils.getJkDevicePwLast(act) ;
        edt_user.setText(id);
        edt_password.setText(pw);

		Button login = view.findViewById(R.id.btn_login) ;
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (act.checkSelfPermission(Manifest.permission
                            .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                }else {
					// TODO: 2018/12/28
					addDeviceId();
                    connect();
                }
            }
		});

		return view ;
	}

	//摄像头默认用户
	private String defaultUser = "admin";
	//摄像头密码
	//private String pwd = "a12345678";
	//摄像头uid
	//private String uuid = "RNSZ5LG57U4X89J9111A";
	//private String uuid = "6UH2B3TYDTC8ZWF2111A";
	private void connect() {
	    if (edt_user.getText().toString().equals("")) {
            showAlarmDialog("请输入用户名");
            return;
        }else {
	        id = edt_user.getText().toString() ;
        }
        if (edt_password.getText().toString().equals("")) {
	        showAlarmDialog("请输入密码");
	        return;
        }else {
	        pw = edt_password.getText().toString() ;
        }

		p2PTool = new XuanYuanXingP2PTool(id, defaultUser, pw, "测试摄像机");
		//设置连接监听
		p2PTool.setClientP2pListener(this);
		//开启连接
		p2PTool.Start();
	}

	private void  addDeviceId() {
		String ids = SharepreferenceUtils.getJkDeviceId(act) ;
		String pws = SharepreferenceUtils.getJkDevicePw(act) ;
		if (TextUtils.isEmpty(ids)) {
			SharepreferenceUtils.saveJkDeviceId(act,id);
			SharepreferenceUtils.saveJkDevicePw(act,pw);
		}else {
			if (!ids.contains(id)) {
				SharepreferenceUtils.saveJkDeviceId(act,ids+"-"+id);
				SharepreferenceUtils.saveJkDevicePw(act,pws+"-"+pw);
			}else {
				String[] listId = ids.split("-");
				String[] listPassword = pws.split("-");
				boolean isChange = false ;
				for (int i = 0; i < listId.length; i++) {
					if (listId[i].equals(id)) {
						if (!listPassword[i].equals(pw)) {
							listPassword[i] = pw ;
							isChange = true ;
						}
					}
				}
				if (isChange) {
					String ids1 = "";
					String pws1 = "";
					for (int j = 0; j < listId.length; j++) {
						if (j == 0) {
							ids1 = listId[0];
							pws1 = listPassword[0];
						} else {
							ids1 = ids1 + "-" + listId[j];
							pws1 = pws1 + "-" + listPassword[j];
						}
					}
					SharepreferenceUtils.saveJkDeviceId(act, ids1);
					SharepreferenceUtils.saveJkDevicePw(act, pws1);
				}
			}
		}

		updateSpinnerValue(SharepreferenceUtils.getJkDeviceId(act));
		SharepreferenceUtils.saveJkDeviceIdLast(act,id);
		SharepreferenceUtils.saveJkDevicePwLast(act,pw);
	}

	@Override
	public void P2pState(int state) {
		if (state == 2) {//成功
			//showAlarmDialog("连接成功");
			addDeviceId();
			Intent intent = new Intent(act, VideoPlayActivity.class);
			intent.putExtra("UID",id) ;
			intent.putExtra("PW",pw) ;
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