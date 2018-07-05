package com.blg.rtu.frmFunction;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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
		tv_open1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击开门1");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(1);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(1,1) ;
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F2", "1");
                        }
                    }
                }
			}
		});



		tv_close1 = (TextView) view.findViewById(R.id.tv_close1) ;
		tv_close1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门1");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(2);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(1,2) ;
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F2", "2");
                        }
                    }
                }
			}
		});
		tv_stop1 = (TextView) view.findViewById(R.id.tv_stop1) ;
		tv_stop1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止1");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(3);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(1,3);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F2", "3");
                        }
                    }
                }
			}
		});

		tv_open2 = (TextView) view.findViewById(R.id.tv_open2) ;
		tv_open2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击开门2");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(4);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,1);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F3", "1");
                        }
                    }
                }
			}
		});
		tv_close2 = (TextView) view.findViewById(R.id.tv_close2) ;
		tv_close2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击关门2");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(5);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,2);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F3", "2");
                        }
                    }
                }
			}
		});
		tv_stop2 = (TextView) view.findViewById(R.id.tv_stop2) ;
		tv_stop2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.show(act, "点击停止2");
                if (Util.checkIsHasLearned(act)) {
                    setProgressVisible(6);
                    if (SharepreferenceUtils.getIsWifi(act)) {
                        setCommand(2,3);
                    }else {
                        if (getCurrentIDIsempty()) {
                            ToastUtils.show(act, "没有可操作的门！");
                        }else {
                            act.frgTool.f_1_0.doorContralServer(currentID, "F3", "3");
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


	public void updateSpinnerValue(String data) {
		if (!"".equals(data)) {
			spinnerAdapter1.clear();

			String[] arr = data.split("-") ;
			if (arr.length >= 1) {
				for (int i = 0; i < arr.length; i++) {
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

	private void putSpinnerValue1(){
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