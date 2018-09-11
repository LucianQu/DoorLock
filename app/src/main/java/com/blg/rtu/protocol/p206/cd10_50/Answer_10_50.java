package com.blg.rtu.protocol.p206.cd10_50;

import android.util.Log;

import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.common.ControlProtocol;
import com.blg.rtu.protocol.p206.common.ProtocolSupport;
import com.blg.rtu.util.ByteUtil;

//设置、查询遥测终端地址，中继站地址

public class Answer_10_50 extends ProtocolSupport{

	private static String tag = Answer_10_50.class.getName() ;
	
	/**
	 * 解析上行数据 
	 * @param rtuId RTU ID
	 * @param b 上行数据
	 * @param cp 控制域解析对象
	 * @param dataCode 数据功能吗
	 * @return
	 * @throws Exception
	 */
	public RtuData parse(String rtuId, byte[] b, ControlProtocol cp, String dataCode) throws Exception {
		RtuData d = new RtuData() ;
		int index = this.parseUpDataHead(rtuId, b, cp, dataCode, d);
		this.doParse(b, index, d, cp) ;

		Log.i(tag, "分析<遥测终端或中继站地址>: RTU ID=" + rtuId + " 数据：" + d.getSubData().toString());
		return d;
	}

	private void doParse(byte[] b, int index, RtuData d, ControlProtocol cp) throws Exception {
		Data_10_50 subD = new Data_10_50() ;
		d.setSubData(subD) ;
		try {
			String password = ByteUtil.bytes2Hex(new byte[]{b[index++],b[index++]}, false) ;
			/*SharepreferenceUtils.saveComPassword(MainActivity.instance,password);
			LogUtils.e("保存通信密码", password);*/
			subD.setPassWord(password);
		}catch (Exception e) {
		}
		/*String[] ss = new RtuIdProtocol().parseRtuId_1(b, index , (index + Constant.Bits_RTU_ID - 1)) ;
		subD.setPassWord(ss[0]) ;*/
	}
}