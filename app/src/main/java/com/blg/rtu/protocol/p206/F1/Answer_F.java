package com.blg.rtu.protocol.p206.F1;

import android.util.Log;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.common.ControlProtocol;
import com.blg.rtu.protocol.p206.common.ProtocolSupport;
import com.blg.rtu.util.ByteUtil;

public class Answer_F extends ProtocolSupport{

	private static String tag = Answer_F.class.getName() ;

	/**
	 * 解析上行数据 
	 * @param rtuId RTU ID
	 * @param b 上行数据
	 * @param cp 控制域解析对象
	 * @return
	 * @throws Exception
	 */
	public RtuData parse(String rtuId, byte[] b, ControlProtocol cp, String dataCode) throws Exception {
		RtuData d = new RtuData() ;
		int index = this.parseUpDataHead(rtuId, b, cp, dataCode, d);
		this.doParse(b, index, d, cp) ;

		Log.i(tag, "分析<控制门命令>应答: 设备 ID=" + rtuId + " 数据：" + d.getSubData().toString());
		return d;
	}
	private RtuData doParse(byte[] b, int index, RtuData d, ControlProtocol cp) throws Exception {
		Data_F subD = new Data_F() ;
		d.setSubData(subD) ;

		subD.setDoorOpen(b[index++]);
		subD.setJiaQuan(ByteUtil.bytes2Int_an(new byte[]{b[index++],b[index++],0,0},0));
		subD.setDoorAlarmPower(b[index++]);
		subD.setDoorAlarmClose(b[index++]);
		subD.setLockStatus(b[index++]);
		subD.setLockInit(b[index++]);
		subD.setLockAlarm(b[index++]);
		subD.setLockPower(b[index++]);
		return d;
	}
	
}
