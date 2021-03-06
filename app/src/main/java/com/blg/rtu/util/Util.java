package com.blg.rtu.util;

import com.blg.rtu3.MainActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	/**
	 * 补充字符串达到要求的长度
	 * @param s
	 * @param supStr
	 * @param requestLen
	 * @return
	 */
	public static String supplyString(String s, String supStr, int requestLen){
		if(s == null || (supStr == null || supStr.equals("")) || requestLen <= 0){
			return s ;
		}
		
		int len = s.length() ;
		if(len >= requestLen){
			return s ;
		}else{
			for(int i = 0 ; i < (requestLen - len); i++){
				s = supStr + s ;
			}
		}
		return s ;
	}

	public static boolean checkIsHasLearned(MainActivity act){
		boolean ishaslearn = SharepreferenceUtils.getHasLearn(act) ;
		if (ishaslearn) {
			return true ;
		}else {
			ToastUtils.show(act, "该设备未学习，请先学习再操作!");
			return false ;
		}
	}
	
	public static String formatFloat2(float f){
		DecimalFormat df = new DecimalFormat("###.00"); 
		return df.format(f) ;
	}
	public static String getCurrentTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss:SSSS");// HH:mm:ss
//获取当前时间
		Date date = new Date(System.currentTimeMillis());
		return simpleDateFormat.format(date) ;
	}
	
//	/**
//	 * 字节数组转换成十六进制的字符串 
//	 *  converts a byte array to a hex string consisting of
//	 * two-digit hex values for each byte in the array
//	 * 
//	 * @param b byte[]
//	 * @param hasBlank 16进制是否用空格分隔
//	 * @return String
//	 */
//	public static String byte2Hex(byte[] b, int startIndex, int length, boolean hasBlank) throws Exception {
//		String rString = "";
//		String temp = "";
//		int total = startIndex + length ;
//		try {
//			for (int i = startIndex; i < total; i++) {
//				int c = b[i];
//				temp = Integer.toHexString(c & 0XFF);
//				if (temp.length() == 1) {
//					temp = "0" + temp;
//				}
//				if(hasBlank){
//					if (i == 0) {
//						rString += temp;
//					} else {
//						rString += " " + temp;
//					}
//				}else{
//					rString += temp;
//				}
//			}
//		} catch (Exception e) {
//			throw new Exception("字节数组转换成十六进制的字符串出错！", null);
//		}
//		return rString;
//	}

}
