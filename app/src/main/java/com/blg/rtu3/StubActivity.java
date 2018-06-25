package com.blg.rtu3;

import android.os.RemoteException;
import android.util.Log;

import com.blg.rtu.aidl.ActivityAidl;
import com.blg.rtu.aidl.RemoteParcel;

import com.blg.rtu.protocol.RtuData ;
import com.blg.rtu.protocol.p206.Code206;

public class StubActivity extends ActivityAidl.Stub{
	
	private static String tag = StubActivity.class.getName() ;
	
	private static MainActivity mAct ;
	
	private static StubActivity instance ;
	
	private StubActivity(){
	}
	
	/**
	 * 创建单例，只有主Activity创建时，Activity才创建本单例
	 * @return
	 */
	public static StubActivity createSingle(MainActivity mAct){
		/*
		 * 这样不行，当MainActivity重新启动，这个对象好象还有值，不死掉
		if(instance == null){
			instance = new StubActivity();
			.....
		}
		*/
		instance = new StubActivity() ;
		StubActivity.mAct = mAct ;
		return instance ;
	}
	
	/**
	 * 得到唯一实例，当主Activity未创建本单例时，此方法返回的实例为空
	 * @return
	 */
	public static StubActivity getInstance(){
		return instance ;
	}

	/**
	 * 网络连接上了
	 */
	@Override
	public void netConnected() {
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				mAct.setNetConnectedStatus(true) ;
			}
		});
	}

	/**
	 * 网络连接断了
	 */
	@Override
	public void netDisconnect() {
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				mAct.setNetConnectedStatus(false) ;
			}
		});
	}

	
	/**
	 * 在server端的Activity代理，通过回调的方式向根Activity端传来了命令结果数据
	 */
	@Override
	public void rtuCommandResult(RemoteParcel parcel) throws RemoteException {
		//Log(MessageFormat.format("rect[bottom={0},top={1},left={2},right={3},test1={4},test2={5}]", rect.bottom,rect.top,rect.left,rect.right,rect.data.test1, rect.data.test2));
		final RtuData data = parcel.packet.data ;
		Log.i(tag, data.toString()) ;
		Log.i("StubActivity", "在server端的Activity代理，通过回调的方式向根Activity端传来了命令结果数据") ;
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				if(data.dataCode != null && !data.dataCode.equals("")){
					if(data.dataCode.equals(Code206.cd_10) || data.dataCode.equals(Code206.cd_50)){
						//设置或查询终端地址
						mAct.frgTool.f_01_100.receiveRtuData(data) ;
						mAct.frgTool.fragment_loopq02.receiveRtuData(Code206.cd_50,data) ;
					}else
					if(data.dataCode.equals(Code206.cd_44) || data.dataCode.equals(Code206.cd_74)){
						//设置或查询终端地址
						mAct.frgTool.f_01_100.receiveRtuData(data) ;
						//mAct.frgTool.fragment_loopq02.receiveRtuData(Code206.cd_50,data) ;
					}else
					if(data.dataCode.equals(Code206.cd_F1)){
						//门控制返回数据
						mAct.frgTool.f_1_0.receiveRtuData(data) ;
						mAct.frgTool.f_1_1.receiveRtuData(data) ;
					}else
					if(data.dataCode.equals(Code206.cd_E2) || data.dataCode.equals(Code206.cd_F2)) {
						//设置或查询电源采集校准系数
						mAct.frgTool.f_1_0.receiveRtuData(data);
						mAct.frgTool.f_1_1.receiveRtuData(data);
					}else
					if(data.dataCode.equals(Code206.cd_E3) || data.dataCode.equals(Code206.cd_F3)){
						//设置或查询仪表系数
						mAct.frgTool.f_1_0.receiveRtuData(data);
						mAct.frgTool.f_1_1.receiveRtuData(data);
					}else
					if(data.dataCode.equals(Code206.cd_16) || data.dataCode.equals(Code206.cd_56)){
						//设置剩余水量报警值或查询遥测终端的剩余水量和报警值
						mAct.frgTool.f_04_110.receiveRtuData(data) ;
					}
				}
				//mAct.frgTool.fragment_ch04.setRtuData(data) ;
				mAct.getSoundAlert().playMessage() ;
			}
		});
	}

	/**
	 * 收到rtu非协议数据
	 */
	@Override
	public void rtuNoPtotocolData(RemoteParcel parcel) throws RemoteException {
		final byte[] bytes = parcel.packet.byteDatas ;
		//Log.i(tag, new String(bytes)) ;
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				mAct.frgTool.fragment_np01.receiveNoProtocolData(bytes) ;
			}
		});
	}

	/**
	 * 在server端的Activity代理，通过回调的方式向根Activity端 回传来了已经发送的命令数据
	 */
	@Override
	public void commandSendedCallBack(RemoteParcel parcel) throws RemoteException {
		final RtuData data = parcel.packet.data ;
		Log.i(tag, data.toString()) ;
		Log.i("StubActivity", "在server端的Activity代理，通过回调的方式向根Activity端 回传来了已经发送的命令数据") ;
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				mAct.frgTool.fragment_ch04.setSendBackCommandData(data) ;
				
				if(data.dataCode != null && !data.dataCode.equals("")){
					if(data.dataCode.equals(Code206.cd_10) || data.dataCode.equals(Code206.cd_50)){
						//设置或查询终端地址
						mAct.frgTool.f_01_100.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_44) || data.dataCode.equals(Code206.cd_74)){
						//设置或查询终端地址
						mAct.frgTool.f_01_100.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_F1)){
						//设置门控制
						mAct.frgTool.f_1_0.commandSendedCallBack() ;
					}
					if(data.dataCode.equals(Code206.cd_F2)){
						//附加功能设置门控制
						mAct.frgTool.f_1_2.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_E3) || data.dataCode.equals(Code206.cd_F3)){
						//设置或查询仪表系数
						mAct.frgTool.f_1_2.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_16) || data.dataCode.equals(Code206.cd_56)){
						//设置遥测终端剩余水量报警值,查询遥测终端的剩余水量和报警值
						mAct.frgTool.f_04_110.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_F0)){
						//循环查询关键参数
						mAct.frgTool.fragment_loopq01.commandSendedCallBack() ;
					}
					
				}
			}
		});
	}
	   
	/**
	 * 后台服务启动自动查询命令
	 */
	@Override
	public void autoQueryCommand(final String code){
		if(code != null && !code.equals("")){
			mAct.mHandler.post(new Runnable(){
				@Override
				public void run() {
					if(code.equals(Code206.cd_B1)){
						//查询水表历史数据
						mAct.frgTool.f_05_040.autoQuery() ;
					}else
					if(code.equals(Code206.cd_ED)){
						//查询日志信息
						mAct.frgTool.f_05_060.autoQuery() ;
					}else{
						//未实现的
						Log.e(tag, "未实现的自动查询命令" + code) ;
					}
				}
			});
		}
	}
	
	/**
	 * 后台服务启动自动设置命令
	 */
	@Override
	public void autoSetCommand(final String code){
		if(code != null && !code.equals("")){
			mAct.mHandler.post(new Runnable(){
				@Override
				public void run() {
					if(code.equals(Code206.cd_44)){//设置遥测终端、中继站地址
						mAct.frgTool.f_01_100.autoSet() ;
					}else
					if(code.equals(Code206.cd_11)){//设置遥测终端、中继站时钟
						mAct.frgTool.f_01_020.autoSet() ;
					}else
				/*	if(code.equals(Code206.cd_12)){//设置遥测终端工作模式
						mAct.frgTool.f_01_030.autoSet() ;
					}else*/
					if(code.equals(Code206.cd_90)){//复位遥测终端参数和状态 
						mAct.frgTool.f_01_050.autoSet() ;
					}else
					if(code.equals(Code206.cd_91)){//清空遥测终端历史数据单元 
						mAct.frgTool.f_01_060.autoSet() ;
					}else
					if(code.equals(Code206.cd_DF)){//设置DTU工作模式
						mAct.frgTool.f_02_010.autoSet() ;
					}else
					if(code.equals(Code206.cd_DA)){//设置GPRS接入点
						mAct.frgTool.f_02_040.autoSet() ;
					}else
					if(code.equals(Code206.cd_DC)){//设置中心网址
						mAct.frgTool.f_02_050.autoSet() ;
					}else
					if(code.equals(Code206.cd_F8)){//设置上报起始时间
						mAct.frgTool.f_03_090.autoSet() ;
					}else
					if(code.equals(Code206.cd_D6)){//设置上报协议配置
						mAct.frgTool.f_02_110.autoSet() ;
					}else
					if(code.equals(Code206.cd_96)){//设置上报协议配置
						mAct.frgTool.f_02_080.autoSet() ;
					}else
					if(code.equals(Code206.cd_15)){//设置遥测终端本次充值量
						mAct.frgTool.f_04_100.autoSet() ;
					}else
					if(code.equals(Code206.cd_16)) {//设置遥测终端剩余水量报警值
						mAct.frgTool.f_04_110.autoSet();
					}
					else{
						//未实现的
						Log.e(tag, "未实现的自动设置命令" + code) ;
					}
				}
			});
		}
	}
	
	/**
	 * 在server端的Activity代理，通过回调的方式向根Activity端传来了主动上报数据
	 */
	@Override
	public void rtuReportData(RemoteParcel parcel) throws RemoteException {
		final RtuData data = parcel.packet.data ;
		Log.i(tag, data.toString()) ;
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				mAct.frgTool.fragment_ch04.setRtuData(data) ;
			}
		});
	}

	/**
	 * 在server端的Activity代理，通过回调的方式向根Activity端传来了个改RTU ID命令成功后的新的RTU ID
	 */
	@Override
	public void newRtuId(String id) throws RemoteException {
		//当前，此处没有业务逻辑
	}
	
	/**
	 * 向界面通知自动查询状态
	 * @param status
	 * @return
	 */
	@Override
	public void notifyAutoQueryStatus(final String status){
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				//mAct.frgTool.fragment_ch02.setAutoQueryStatus(status) ;
			}
		});
		
	}
	/**
	 * 向界面通知自动设置状态
	 * @param status
	 * @return
	 */
	@Override
	public void notifyAutoSetStatus(final String status){
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				//mAct.frgTool.fragment_ch03.setAutoSetStatus(status) ;
			}
		});
		
	}

}
