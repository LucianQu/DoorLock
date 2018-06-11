package com.blg.rtu.protocol.p206.cd92_93;

import android.util.Log;
import com.blg.rtu.protocol.RtuData;
import com.blg.rtu.protocol.p206.common.ProtocolSupport;
import com.blg.rtu.protocol.p206.common.ControlProtocol;

public class Answer_93  extends ProtocolSupport{
	
	private static String tag = Answer_93.class.getName() ;

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
		
		Log.i(tag, "分析<遥控关闭水泵或阀门/闸门>应答: RTU ID=" + rtuId + " 数据：" + d.getSubData().toString());
		return d;
	}
	private void doParse(byte[] b, int index, RtuData d, ControlProtocol cp) throws Exception {
		Data_93 dd = new Data_93() ;
		d.setSubData(dd) ;
		
		byte db = b[index] ;
		dd.setNum(db & 0x0F) ;
		if(((db & 0xF0) >> 4) == 0xA){
			dd.setSuccess(true) ;
		}else{
			dd.setSuccess(false) ;
		}

	}
}
