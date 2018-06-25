package com.blg.rtu.protocol.p206;

import android.util.Log;

import com.blg.rtu.protocol.Action;
import com.blg.rtu.protocol.DriverRtu;
import com.blg.rtu.protocol.RtuCommand;
import com.blg.rtu.protocol.p206.F1.Answer_F1;
import com.blg.rtu.protocol.p206.F1.Write_F1;
import com.blg.rtu.protocol.p206.F2.Answer_F2;
import com.blg.rtu.protocol.p206.F2.Write_F2;
import com.blg.rtu.protocol.p206.F3.Answer_F3;
import com.blg.rtu.protocol.p206.F3.Write_F3;
import com.blg.rtu.protocol.p206.cd02.Confirm_02;
import com.blg.rtu.protocol.p206.cd02.Data_02;
import com.blg.rtu.protocol.p206.cd02.LinkTest_02;
import com.blg.rtu.protocol.p206.cd02.Write_02;
import com.blg.rtu.protocol.p206.cd10_50.Answer_10_50;
import com.blg.rtu.protocol.p206.cd10_50.Read_50;
import com.blg.rtu.protocol.p206.cd10_50.Write_10;
import com.blg.rtu.protocol.p206.cd11_51.Answer_11_51;
import com.blg.rtu.protocol.p206.cd11_51.Write_11;
import com.blg.rtu.protocol.p206.cd44_74.Answer_44;
import com.blg.rtu.protocol.p206.cd44_74.Answer_74;
import com.blg.rtu.protocol.p206.cd44_74.Read_74;
import com.blg.rtu.protocol.p206.cd44_74.Write_44;
import com.blg.rtu.protocol.p206.cdF0.Answer_F0;
import com.blg.rtu.protocol.p206.common.CodeProtocol;
import com.blg.rtu.protocol.p206.common.ControlProtocol;
import com.blg.rtu.protocol.p206.common.HeadProtocol;
import com.blg.rtu.protocol.p206.common.RtuIdProtocol;
import com.blg.rtu.protocol.p206.common.TailProtocol;
import com.blg.rtu.protocol.p206.util.Constant;
import com.blg.rtu.util.ByteUtil;
import com.blg.rtu.util.StringValueForServer;

import java.util.HashMap;


public class Driver206 extends DriverRtu {
	
	private static String tag = Driver206.class.getName() ;

	//测控终端的工作模式，RTU驱动里，一般不用全局变量，
	//但是这里存所要设置的工作模式可以这样做。
	//根据协议定义，工作模式要从主动上报数据的确认帧中下发，所以这里存储一下
	private static HashMap<String, Integer> rtuWilSetModels = new HashMap<String, Integer>() ;
	/**
	 * 分析RTU数据
	 * @param b
	 * @return  
	 */
	@SuppressWarnings("unused")
	@Override
	public Action analyseData(byte[] b) {
		String dataHex = null ;
		try {
			dataHex = ByteUtil.bytes2Hex(b, false) ;
			// 检查数据头合法性
			int dataLen = new HeadProtocol().checkHeadAndGetDataLen(b) ;
			// 检查数据尾合法性
			// 因为功能码为ED的命令CRC验证不正确，硬件方总说没有问题，所以此处把CRC校验去除了
			new TailProtocol().checkTail(b, dataLen, false) ;

			//进行控制域分析
			ControlProtocol ca = new ControlProtocol().parseControl(b) ;
			
			//进行RTU 地址域分析
			int index = Constant.Site_RTUID ;
			if(ca.hasDIVS){
				index += 1 ;
			}
			String[] strs = new RtuIdProtocol().parseRtuId_2(b, index, index + Constant.Bits_RTU_ID - 1) ;
			this.rtuId = strs[0] ;
			this.rtuId_hex = strs[1] ;
			
			//得到数据中的功能码
			index = Constant.Site_Code ;
			if(ca.hasDIVS){
				index += 1 ;
			}
			this.dataCode = new CodeProtocol().parseCode(b, index) ;

			
			Action action = Action.nullAction() ;
			
			if(this.dataCode == null){
				Log.e(tag, "出错，未能得到RTU数据中的功能码，不能分析数据。") ;
				action.append(Action.noAction) ;
			}
			if(this.dataCode.equalsIgnoreCase(Code206.cd_02)){
				//有关RTU 链路检测
				LinkTest_02 parse = new LinkTest_02() ;
				this.upData = parse.parse(rtuId, b, ca, dataCode) ;
				if(StringValueForServer.protocolEnableClockSyn == 1){ 
					if(parse.getClockDifference_minute_abs() > StringValueForServer.protocolSynClockDeviate){
						//进行时钟同步
						action.append(Action.synchronizeClock) ;
					}
				}
				if(this.upData != null){
					//得到从上报数据中分析出来的RTU ID
					Data_02 subD = (Data_02)this.upData.getSubData() ;
					String status = subD.getStatus() ;
					if(status != null && status.equals(Constant.LinkTest_F0)){
						//上线（登录）
						this.rtuId = this.upData.getRtuId() ;
						this.downData = new Confirm_02().create(Code206.cd_02, Constant.Down_ControlFunCode_0, rtuId , subD.getOraData(), this.getRtupassword()) ;
						this.commandCode = Code206.cd_02 ;
						action.append(Action.remoteConfirmAction) ;
					}else if(status != null && status.equals(Constant.LinkTest_F1)){
						//下线(退出登录)
					}else if(status != null && status.equals(Constant.LinkTest_F2)){
						//在线保持
					}else if(status != null && status.equals(Constant.LinkTest_unknow)){
						//不能识别的状态
					}else{
						//status为空
					}
				}
			}else			
			if(this.dataCode.equalsIgnoreCase(Code206.cd_10)){
				//应答 - 设置遥测终端、中继站地址
				this.upData = new Answer_10_50().parse(rtuId, b, ca, dataCode) ;
				action.append(Action.changeRtuIdAction) ;
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_44)){
				//应答 - 设置遥测终端、中继站地址
				this.upData = new Answer_44().parse(rtuId, b, ca, dataCode) ;
				action.append(Action.changeRtuIdAction) ;
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_50)){
				//应答 - 查询遥测终端、中继站地址
				this.upData = new Answer_10_50().parse(rtuId, b, ca, dataCode) ;
				//this.upData = new Answer_74().parse(rtuId, b, ca, dataCode) ;
				action.append(Action.commandReadRtuIdResultAction) ;
				action.append(Action.commandResultAction) ;
				
			}else	
			if(this.dataCode.equalsIgnoreCase(Code206.cd_74)){
				//应答 - 查询遥测终端、中继站地址
				this.upData = new Answer_74().parse(rtuId, b, ca, dataCode) ;
				action.append(Action.commandReadRtuIdResultAction) ;
				action.append(Action.commandResultAction) ;
				
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_11)){
				//应答 - 设置遥测终端、中继站时钟
				this.upData = new Answer_11_51().parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_51)){
				//应答 - 查询遥测终端、中继站时钟
				Answer_11_51 answer = new Answer_11_51() ;
				this.upData = answer.parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
				/*
				 不自动设置，手动设置，以使人员能知道时钟不正确
				if(StringValueForServer.protocolEnableClockSyn == 1){ 
					if(answer.getClockDifference_minute_abs() > StringValueForServer.protocolSynClockDeviate){
						//进行时钟同步
						action.append(Action.synchronizeClock) ;
					}
				}
				*/
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_F1)){
				///应答 - 门控制
				this.upData = new Answer_F1().parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_F2)){
				//应答 - 设置电源采集校准系数
				this.upData = new Answer_F2().parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_F3)){
				//应答 - 设置仪表系数
				this.upData = new Answer_F3().parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
			}else
			if(this.dataCode.equalsIgnoreCase(Code206.cd_F0)){
				//应答 - 查询关键参数
				this.upData = new Answer_F0().parse(rtuId, b, ca, this.dataCode);
				action.append(Action.commandResultAction) ;
			}

			
			if(this.upData != null){
				//得到从上报数据中分析出来的RTU ID
				//this.rtuId = this.upData.getRtuId() ;
				
				//把十六进制ID放入
				this.upData.setRtuId_hex(this.rtuId_hex) ;
				//把上行数据的十六进制放入数据中
				this.upData.setHex(dataHex) ;
				
			}
			return action == null?Action.noAction:action ;
			
		} catch (Exception e) {
			if(e == null){
				this.error = "出错， 数据：" + dataHex;
				
			}else{
				if(e.getMessage() != null){
					Log.e(tag, e.getMessage());
					this.error = e.getMessage()+ " 数据：" + dataHex;
				}else{
					this.error = "出错， 数据：" + dataHex;
				}	
			}
			Log.e(tag, "数据：" + dataHex);
			return Action.errorAction;
		}
	}
	
	/**
	 * 构造针对RTU(测控终端)的命令
	 * @param command 命令
	 * @return
	 */
	@Override
	public Action createCommand(RtuCommand command) {
		try {
			HashMap<String, Object> params = command.getParams() ;
			//params可以为空，如查终端地址
//			if(params == null || params.size() == 0){
//				this.error = "出错，命令中参数集合为空，不能构建任何命令！" ;
//				Log.e(tag, error) ;
//				return Action.errorAction ;
//			}
			this.rtuId = command.getRtuId() ;
			if(this.rtuId == null || this.rtuId == null){
				this.error = "出错，命令中RTU ID为空，不能构建任何命令！" ;
				Log.e(tag, error) ;
				return Action.errorAction ;
			}
			
			this.commandCode = command.getCommandCode() ;
			if(this.commandCode == null || this.commandCode.trim().equals("")){
				this.error = "出错，命令中功能码为空，不能构建任何命令！" ;
				Log.e(tag, error) ;
				return Action.errorAction ;
			}
			
			String password = this.getRtupassword();
			
			Action activ = Action.nullAction() ;

			if(this.commandCode.equalsIgnoreCase(Code206.cd_02)){
				//链路检测
				this.downData = new Write_02().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else 
			if(this.commandCode.equalsIgnoreCase(Code206.cd_10)){
				//设置遥测终端、中继站地址
				this.downData = new Write_10().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else 
			if(this.commandCode.equalsIgnoreCase(Code206.cd_44)){
				//设置遥测终端、中继站地址
				this.downData = new Write_44().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else 
			if(this.commandCode.equalsIgnoreCase(Code206.cd_50)){
				//查询遥测终端、中继站地址
				this.downData = new Read_50().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else 
			if(this.commandCode.equalsIgnoreCase(Code206.cd_74)){
				//查询遥测终端、中继站地址
				this.downData = new Read_74().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else 
			if(this.commandCode.equalsIgnoreCase(Code206.cd_11)){
				//设置遥测终端、中继站时钟
				this.downData = new Write_11().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else
			if(this.commandCode.equalsIgnoreCase(Code206.cd_F1)){
				//设置：门控制
				this.downData = new Write_F1().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else
			if(this.commandCode.equalsIgnoreCase(Code206.cd_F2)){
				//附加功能;门设置
				this.downData = new Write_F2().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else
			if(this.commandCode.equalsIgnoreCase(Code206.cd_F3)){
				//附加功能：门设置
				this.downData = new Write_F3().create(this.commandCode, Constant.Down_ControlFunCode_0, this.rtuId , params, password) ;
				activ = Action.remoteCommandAction ;
			}else
			{
				this.error = "出错，命令中功能码(" + this.commandCode + ")不能被识别！" ;
				Log.e(tag, error) ;
				activ = Action.unknownAction ;
			}
			return activ == null?Action.noAction:activ ;
		} catch (Exception e) {
			Log.e(tag, e.getMessage());
			this.error = e.getMessage();
			return Action.errorAction;
		}
	}
	

	/**
	 * 直接创建设置时钟命令
	 * @throws  
	 */
	@Override
	public byte[] createSetClockOrderDirect(String rtuId) {
		try {
			return new Write_11().create(Code206.cd_11, Constant.Down_ControlFunCode_0, rtuId , null, this.getRtupassword()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	

	/**
	 * 得到RTU 密码与密码
	 * @return
	 */
	private String getRtupassword(){
		String passHex =  StringValueForServer.getDefaultPasswordHex() ;
		if(passHex != null){
			passHex = passHex.replaceAll(" ", "") ;
		}
		if(passHex == null || passHex.equals("")){
			passHex = com.blg.rtu.util.Constant.defaultPasswordHex ;
		}
		return passHex ;
	}

}
