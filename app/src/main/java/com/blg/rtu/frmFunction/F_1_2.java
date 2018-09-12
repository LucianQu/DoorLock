package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.blg.rtu.frmFunction.bean.DoorStatus;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu.protocol.p206.CommandCreator;
import com.blg.rtu.protocol.p206.F2.Data_F2;
import com.blg.rtu.protocol.p206.F3.Data_F3;
import com.blg.rtu.util.MyTimeTask;
import com.blg.rtu.util.SharepreferenceUtils;
import com.blg.rtu.util.SpinnerVO;
import com.blg.rtu.util.ToastUtils;
import com.blg.rtu.util.Util;
import com.blg.rtu.vo2xml.Vo2Xml;
import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;
import com.blg.rtu3.utils.LogUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class F_1_2 extends FrmParent {

	private Spinner spinner;
	private ArrayAdapter<SpinnerVO> spinnerAdapter1;

	private TextView tv_open1;
	private TextView tv_close1;
	private TextView tv_stop1;

	private TextView tv_open2;
	private TextView tv_close2;
	private TextView tv_stop2;

    private ProgressBar pb_open1 ;
    private ProgressBar pb_close1 ;
    private ProgressBar pb_stop1 ;

    private ProgressBar pb_open2 ;
    private ProgressBar pb_close2 ;
    private ProgressBar pb_stop2 ;
    private DoorStatus doorStatus ;
    public String currentID = "" ;
	private String currentCom = "" ;
	private int reSendNum = 0 ;
	private String currentAfn = "" ;
	private String mDtuId ;
	private MyTimeTask taskWifiQueryTimer;
	private boolean taskWifiTimerStatus = false ;
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
		//setTimer() ;
	}

	private void setTimer(){
		taskWifiQueryTimer =new MyTimeTask(1000, new TimerTask() {
			@Override
			public void run() {
				if (SharepreferenceUtils.getIsWifi(act)) {
					if (currentAfn.equals("F2") && currentCom.equals("1")) {
						setCommand(1,1) ;
					}else if (currentAfn.equals("F2") && currentCom.equals("2")) {
						setCommand(1,2) ;
					}else if (currentAfn.equals("F2") && currentCom.equals("3")) {
						setCommand(1,3) ;
					}else if (currentAfn.equals("F3") && currentCom.equals("1")) {
						setCommand(2,1) ;
					}else if (currentAfn.equals("F3") && currentCom.equals("2")) {
						setCommand(2,2) ;
					}else if (currentAfn.equals("F3") && currentCom.equals("3")) {
						setCommand(2,2) ;
					}
				}
			}
		});
	}

	public void stopWifiFunTimer(){
		if (taskWifiTimerStatus) {
			taskWifiTimerStatus = false;
			taskWifiQueryTimer.stop();
		}
	}
	public void startWifiFunTimer(){
		if (!taskWifiTimerStatus) {
			taskWifiTimerStatus = true;
			taskWifiQueryTimer.start();
		}
	}

	public F_1_2.MyHandler handler = new F_1_2.MyHandler(act) ;
	public class MyHandler extends Handler {
		private final WeakReference<MainActivity> mActivty;
		private MyHandler(MainActivity mActivty) {
			this.mActivty = new WeakReference<MainActivity>(mActivty);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
	private boolean endReqFlag = true ; //结束数据请求
	private Runnable wifiTimeOverResetStatus =  new Runnable() {
		@Override
		public void run() {
			if (!endReqFlag) {
				if (currentAfn.equals("F2") && currentCom.equals("1")) {
					ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(1,1);
				}else if (currentAfn.equals("F2") && currentCom.equals("2")) {
					ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(2,1);
				}else if (currentAfn.equals("F2") && currentCom.equals("3")) {
					setBtnBackground(3,1);
					ToastUtils.show(act,"设备回复超时，请再次操作!");
				}else if (currentAfn.equals("F3") && currentCom.equals("1")) {
					ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(4,1);
				}else if (currentAfn.equals("F3") && currentCom.equals("2")) {
					ToastUtils.show(act,"设备未回复超时，请再次操作!");
					setBtnBackground(5,1);
				}else if (currentAfn.equals("F3") && currentCom.equals("3")) {
					setBtnBackground(6,1);
					ToastUtils.show(act,"设备回复超时，请再次操作!");
				}
			}
		}
	};

	private Runnable serverTimeOverResetStatus = new Runnable() {
		@Override
		public void run() {
			if (!endReqFlag ) {

			}
		}
	};

	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_1_02, container, false);

		spinner = (Spinner)view.findViewById(R.id.spinner_doorList);
		spinnerAdapter1 = new ArrayAdapter<SpinnerVO>(this.act, R.layout.spinner_style, new ArrayList<SpinnerVO>());
		this.putSpinnerValue1();
		spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item);
		// 将adapter 添加到spinner中
		spinner.setAdapter(spinnerAdapter1);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

        pb_open1 = (ProgressBar) view.findViewById(R.id.pb_open1);
        pb_open2 = (ProgressBar) view.findViewById(R.id.pb_open2);
        pb_close1 = (ProgressBar) view.findViewById(R.id.pb_close1);
        pb_close2 = (ProgressBar) view.findViewById(R.id.pb_close2);
        pb_stop1 = (ProgressBar) view.findViewById(R.id.pb_stop1);
        pb_stop2 = (ProgressBar) view.findViewById(R.id.pb_stop2);

		tv_open1 = (TextView) view.findViewById(R.id.tv_open1) ;
		tv_open1.setEnabled(false);
		tv_open1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击开门1");
                if (Util.checkIsHasLearned(act)) {
					currentCom = "1" ;
					currentAfn = "F2" ;
					setOpenCloseStatus(1, 1) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
						setCommand(1,1) ;
						handler.removeCallbacks(wifiTimeOverResetStatus);
						handler.postDelayed(wifiTimeOverResetStatus, 15000) ;
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
							//act.frgTool.f_1_0.doorContralServer("0102030405", "F2", "1");
                        }
                    }
                }
			}
		});



		tv_close1 = (TextView) view.findViewById(R.id.tv_close1) ;
		tv_close1.setEnabled(false);
		tv_close1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门1");
                if (Util.checkIsHasLearned(act)) {
					currentCom = "2" ;
					currentAfn = "F2" ;
					setOpenCloseStatus(1, 2) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(1,2) ;
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {

							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
                        }
                    }
                }
			}
		});
		tv_stop1 = (TextView) view.findViewById(R.id.tv_stop1) ;
		tv_stop1.setEnabled(false);
		tv_stop1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止1");
				if (Util.checkIsHasLearned(act)) {
					currentCom = "3" ;
					currentAfn = "F2" ;
					setOpenCloseStatus(1, 3) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(1,3);
                    }else {
						if (getCurrentIDIsempty()) {
							//if (false) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {

							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
                        }
                    }
                }
			}
		});

		tv_open2 = (TextView) view.findViewById(R.id.tv_open2) ;
		tv_open2.setEnabled(false);
		tv_open2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击开门2");
                if (Util.checkIsHasLearned(act)) {
					currentCom = "1" ;
					currentAfn = "F3" ;
					setOpenCloseStatus(2, 1) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,1);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {

							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
                        }
                    }
                }
			}
		});
		tv_close2 = (TextView) view.findViewById(R.id.tv_close2) ;
		tv_close2.setEnabled(false);
		tv_close2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门2");
                if (Util.checkIsHasLearned(act)) {
					currentCom = "2" ;
					currentAfn = "F3" ;
					setOpenCloseStatus(2, 2) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,2);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {

							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
                        }
                    }
                }
			}
		});
		tv_stop2 = (TextView) view.findViewById(R.id.tv_stop2) ;
		tv_stop2.setEnabled(false);
		tv_stop2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止2");
                if (Util.checkIsHasLearned(act)) {
					currentCom = "3" ;
					currentAfn = "F3" ;
					setOpenCloseStatus(2, 3) ;
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,3);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {

							reSendNum = 2 ;
							doorContralServer(currentID, currentAfn, currentCom);
                        }
                    }
                }
			}
		});


		return view ;
	}

    public boolean getCurrentIDIsempty() {
        if (spinnerAdapter1.isEmpty()) {
            return true ;
        }else {
            currentID = spinnerAdapter1.getItem(0).getName() ;
            return false ;
        }
    }

    private void setProgressVisible(int position){
        if (position == 1) {
            pb_open1.setVisibility(View.VISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }else if (position == 2) {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.VISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }else if (position == 3) {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.VISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }else if (position == 4) {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.VISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }else if (position == 5) {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.VISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }else if (position == 6) {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.VISIBLE);
        }else {
            pb_open1.setVisibility(View.INVISIBLE);
            pb_close1.setVisibility(View.INVISIBLE);
            pb_stop1.setVisibility(View.INVISIBLE);
            pb_open2.setVisibility(View.INVISIBLE);
            pb_close2.setVisibility(View.INVISIBLE);
            pb_stop2.setVisibility(View.INVISIBLE);
        }
    }

    private void setOpenCloseStatus(int position, int status) {
		if (position == 1 && status == 1) {
			setProgressVisible(1);
			setBtnBackground(1, 2); //红
			setBtnBackground(2, 0);//灰
			setBtnBackground(3, 1); //绿
		}else if (position == 1 && status == 2) {
			setProgressVisible(2);
			setBtnBackground(1, 0);
			setBtnBackground(2, 2);
			setBtnBackground(3, 1);
		}else if (position == 1 && status == 3) {
			setProgressVisible(3);
			setBtnBackground(1, 1);
			setBtnBackground(2, 1);
			setBtnBackground(3, 2);
		}else if (position == 2 && status == 1) {
			setProgressVisible(4);
			setBtnBackground(4, 2); //红
			setBtnBackground(5, 0);//灰
			setBtnBackground(6, 1); //绿
		}else if (position == 2 && status == 2) {
			setProgressVisible(5);
			setBtnBackground(4, 0);
			setBtnBackground(5, 2);
			setBtnBackground(6, 1);
		}else if (position == 2 && status == 3) {
			setProgressVisible(6);
			setBtnBackground(4, 1);
			setBtnBackground(5, 1);
			setBtnBackground(6, 2);
		}
	}

	private void setBtnBackground(int position, int status) {
		if (!isAdded()) {
			this.onAttach(act);
		}
		if (position == 1) {
			if (status == 1) {
				tv_open1.setEnabled(true);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_open1.setEnabled(false);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_open1.setEnabled(false);
				tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 2) {
			if (status == 1) {
				tv_close1.setEnabled(true);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_close1.setEnabled(false);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_close1.setEnabled(false);
				tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 3) {
			if (status == 1) {
				tv_stop1.setEnabled(true);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_stop1.setEnabled(false);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_stop1.setEnabled(false);
				tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 7) {
			setProgressVisible(0) ;
			tv_open1.setEnabled(true);
			tv_close1.setEnabled(true);
			tv_stop1.setEnabled(true);
			tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
		}else if (position == 4) {
			if (status == 1) {
				tv_open2.setEnabled(true);
				tv_open2.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_open2.setEnabled(false);
				tv_open2.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_open2.setEnabled(false);
				tv_open2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 5) {
			if (status == 1) {
				tv_close2.setEnabled(true);
				tv_close2.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_close2.setEnabled(false);
				tv_close2.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_close2.setEnabled(false);
				tv_close2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else if (position == 6) {
			if (status == 1) {
				tv_stop2.setEnabled(true);
				tv_stop2.setBackground(act.getResources().getDrawable(R.mipmap.btn_green1));
			}else if (status == 2) {
				tv_stop2.setEnabled(false);
				tv_stop2.setBackground(act.getResources().getDrawable(R.mipmap.btn_red));
			}else {
				tv_stop2.setEnabled(false);
				tv_stop2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			}
		}else {
			setProgressVisible(0) ;
			tv_open1.setEnabled(false);
			tv_close1.setEnabled(false);
			tv_stop1.setEnabled(false);
			tv_open2.setEnabled(false);
			tv_close2.setEnabled(false);
			tv_stop2.setEnabled(false);
			tv_open1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_close1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_stop1.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_open2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_close2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
			tv_stop2.setBackground(act.getResources().getDrawable(R.mipmap.btn_gray));
		}
	}


	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			spinnerAdapter1.clear();
			String[] arr = data.split("-") ;
			if (arr.length >= 1) {
				for (int i = arr.length -1; i >= 0; i--) {
					spinnerAdapter1.add(new SpinnerVO(i + "", arr[i]));
					spinnerAdapter1.notifyDataSetChanged();
				}
			}
		}
	}
	public void updateSpinnerValue(List<String> list) {
		int i = 0 ;
		spinnerAdapter1.clear();
		for (String str : list) {
			spinnerAdapter1.add(new SpinnerVO(i++ + "", str));
		}
		spinnerAdapter1.notifyDataSetChanged();
	}

	public void putSpinnerValue1(){
		updateSpinnerValue(SharepreferenceUtils.getDeviceId(act));
	}

	public void setCurrentPosition(int position) {
        if (!spinnerAdapter1.isEmpty()) {
            spinner.setSelection(position);
        }
    }
    public void setCurrentID(String s) {
        this.currentID = s ;
    }

	private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(parent.getId() == spinner.getId()){
                currentID = parent.getSelectedItem().toString();
                act.frgTool.f_1_0.setCurrentID(currentID);
                act.frgTool.f_1_0.setCurrentPosition(position);
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void doorContralServer(String dtuId, String code, String flag) {
		LogUtils.e("请求开始时间", Util.getCurrentTime());
		LogUtils.e("请求间隔：", (act.delay )+"秒");
		String url = act.mIpPort + "/door/door/state.act?" ;
		//String url = "http://1bdf2aff.ngrok.io/door/door/state.act?" ;
		RequestParams requestParams = new RequestParams(url);
		requestParams.addBodyParameter("dtuId", dtuId);
		requestParams.addBodyParameter("code", code);
		requestParams.addBodyParameter("flag", flag);
		requestParams.addBodyParameter("password",act.frgTool.f_1_0.currentPassword);
		LogUtils.e("门控制服务", requestParams.toString());
		x.http().get(requestParams, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				LogUtils.e("请求成功结束时间", Util.getCurrentTime());
				setProgressVisible(0) ;
				JSONObject jsonResult = null;
				if (!"".equals(result)) {
					try {
						jsonResult = new JSONObject(result);
						String returnDtuId = jsonResult.getString("dtuId");
						if (null == returnDtuId || "null".equals(returnDtuId) || "".equals(returnDtuId)) {
							ToastUtils.show(act, "产品ID为空，数据未知!");
						} else {
							if (mDtuId.equals(returnDtuId)) {
								String code = jsonResult.getString("succ");
								if (code.equals("1")) {
									Gson gson = new Gson();
									String data = jsonResult.getString("rltState");
									doorStatus = gson.fromJson(data, DoorStatus.class);
									if (null != doorStatus) {
										//act.updateConnectedStatus(true);
										//act.frgTool.f_1_0.displayServiceData(doorStatus);
										//act.frgTool.f_1_0.pintServiceData(doorStatus);
										ToastUtils.show(act, "附加功能-获取服务数据成功");
									} else {
										ToastUtils.show(act, "附加功能-获取服务数据为空");
									}
								} else {
									String msg = jsonResult.getString("error");
									if (msg.equals("设备尚未上线，命令发送失败！")) {
										ToastUtils.show(act, "附加功能-" + "设备未上线！");
									} else if (msg.contains("超时")) {
										ToastUtils.show(act, "附加功能-" + "设备回复数据超时！");
									}

								}
							} else {
								ToastUtils.show(act, "服务获取数据返回地址与请求地址不一致!");
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				setProgressVisible(0) ;
			}

			@Override
			public void onCancelled(CancelledException cex) {
				setProgressVisible(0) ;
			}

			@Override
			public void onFinished() {
				setProgressVisible(0) ;
			}
		});
		//get.cancel();
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

    private void setCommand(int type,int command) {
        if (type == 1) {
            this.sendRtuCommand(new CommandCreator().cd_F_2(command, null), false);
        }else {
            this.sendRtuCommand(new CommandCreator().cd_F_3(command, null), false);
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
		setProgressVisible(0);
		Object subD = d.subData ;
		if(subD != null){
			if(subD instanceof Data_F2){
				Data_F2 data = (Data_F2) subD ;
				act.frgTool.f_1_0.displayWifiData(data) ;
				ToastUtils.show(act, "显示辅加功能1回传Wifi数据!");
			}else if (subD instanceof Data_F3) {
				Data_F3 data = (Data_F3) subD ;
				act.frgTool.f_1_0.displayWifiData(data) ;
				ToastUtils.show(act, "显示辅加功能2回传Wifi数据!");
			}
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