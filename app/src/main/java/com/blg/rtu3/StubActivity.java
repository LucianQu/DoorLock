package com.blg.rtu3;

import android.os.RemoteException;
import android.util.Log;

import com.blg.rtu.aidl.ActivityAidl;
import com.blg.rtu.aidl.RemoteParcel;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.Code206;
import com.blg.rtu3.utils.LogUtils;

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
				mAct.setWifiConnectedStatus(true) ;
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
				mAct.setWifiConnectedStatus(false) ;
			}
		});
	}

	/**
	 * 在server端的Activity代理，通过回调的方式向根Activity端传来了命令结果数据
	 */
	@Override
	public void rtuCommandResult(RemoteParcel parcel) throws RemoteException {
		final RtuData data = parcel.packet.data ;
		Log.i(tag, data.toString()) ;
		Log.i("StubActivity", "在server端的Activity代理，通过回调的方式向根Activity端传来了命令结果数据") ;
		mAct.mHandler.post(new Runnable(){
			@Override
			public void run() {
				if(data.dataCode != null && !data.dataCode.equals("")){
					if(data.dataCode.equals(Code206.cd_50)){
						//设置或查询终端地址 lucian
						mAct.frgTool.f_01_010.receiveRtuData(data);
						//mAct.setDeviceID(data.getRtuId());
					}else if(data.dataCode.equals(Code206.cd_F1)){
						//门控制返回数据
						mAct.frgTool.f_1_0.receiveRtuData(data) ;
						mAct.frgTool.f_1_1.receiveRtuData(data) ;
					}else if(data.dataCode.equals(Code206.cd_F2)) {
						//附加功能开关1
						mAct.frgTool.f_1_0.receiveRtuData(data);
						mAct.frgTool.f_1_1.receiveRtuData(data);
					}else if( data.dataCode.equals(Code206.cd_F3)){
						//附加功能开关2
						mAct.frgTool.f_1_0.receiveRtuData(data);
						mAct.frgTool.f_1_1.receiveRtuData(data);
					}else if(data.dataCode.equals(Code206.cd_CA) || data.dataCode.equals(Code206.cd_DA)){
						//设置或查询GPRS接入点
						mAct.frgTool.f_1_3.receiveRtuData(data) ;
					}
				}
				//mAct.frgTool.fragment_ch04.setRtuData(data) ;
				//mAct.getSoundAlert().playMessage() ;
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
				//mAct.frgTool.fragment_np01.receiveNoProtocolData(bytes) ;
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
				//mAct.frgTool.fragment_ch04.setSendBackCommandData(data) ; qls
				
				if(data.dataCode != null && !data.dataCode.equals("")){
					if(data.dataCode.equals(Code206.cd_50)){
						//设置或查询终端地址
					}else
					if(data.dataCode.equals(Code206.cd_F1)){
						//设置门控制
						//mAct.frgTool.f_1_0.commandSendedCallBack() ;
					}
					if(data.dataCode.equals(Code206.cd_F2)){
						//附加功能设置门控制
						//mAct.frgTool.f_1_2.commandSendedCallBack() ;
					}else
					if(data.dataCode.equals(Code206.cd_F3)){
						//附加功能设置门控制
						//mAct.frgTool.f_1_2.commandSendedCallBack() ;
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
					if(code.equals(Code206.cd_50)){
						LogUtils.e("StubActivity后台服务启动，自动查询CD_50","开始查询");
						mAct.frgTool.f_01_010.queryCommand();
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
					if(code.equals(Code206.cd_F1)){//设置遥测终端、中继站地址

					}else{
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
				//mAct.frgTool.fragment_ch04.setRtuData(data) ;
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
