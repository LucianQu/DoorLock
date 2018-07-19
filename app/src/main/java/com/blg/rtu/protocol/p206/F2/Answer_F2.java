package com.blg.rtu.protocol.p206.F2;

import android.util.Log;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.common.ControlProtocol;
import com.blg.rtu.protocol.p206.common.ProtocolSupport;
import com.blg.rtu.util.ByteUtil;

public class Answer_F2 extends ProtocolSupport{

	private static String tag = Answer_F2.class.getName() ;

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
		Data_F2 subD = new Data_F2() ;
		d.setSubData(subD) ;

		subD.setJiaQuan(ByteUtil.bytes2Int_an(new byte[]{b[index++],b[index++],b[index++],0},0));
		subD.setDoorStatus(b[index++]& 0xff);
		subD.setDoorOpen(b[index++] & 0xff);
		subD.setLockFlag(b[index++]& 0xff);
		subD.setLockStatus(b[index++]& 0xff);
		subD.setPowerFlag(b[index++]& 0xff);
		subD.setPowerStatus(b[index++]& 0xff);
		subD.setAlarmFlag(b[index++]& 0xff);
		subD.setAlarmStaus(b[index++]& 0xff);

		byte lockStatus = (byte) subD.getLockStatus() ;
		if ((lockStatus & 0x04) == 0x04) {
			subD.setHasPower(true);
		}else {
			subD.setHasPower(false);
		}
		if ((lockStatus & 0x02) == 0x02) {
			subD.setLockInitPosition(true);
		}else {
			subD.setLockInitPosition(false);
		}
		if ((lockStatus & 0x01) == 0x01) {
			subD.setOpenLock(true);
		}else {
			subD.setOpenLock(false);
		}
		byte powerStatus = (byte) subD.getPowerStatus() ;
		if ((powerStatus & 0x04) == 0x04) {
			subD.setDoorNormal(false);
		}else {
			subD.setDoorNormal(true);
		}
		if ((powerStatus & 0x02) == 0x02) {
			subD.setNormalCurrent(false);
		}else {
			subD.setNormalCurrent(true);
		}
		if ((powerStatus & 0x01) == 0x01) {
			subD.setNormalPower(false);
		}else {
			subD.setNormalPower(true);
		}
		return d;
	}
	
}
