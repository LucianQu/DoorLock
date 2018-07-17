package com.blg.rtu3.utils;

import java.util.Locale;

/**
 * 类注释：数据转换类
 * Created by sujingtai on 2017/6/6 0006 下午 2:04
 */

public class DataTranslateUtils {
    public static String dataFloatWithOne(String data){
        if (data!=null){
            float num= Float.parseFloat(data);
           /* DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p=decimalFormat.format(num);//format 返回的是字符串*/
            return String.format(Locale.getDefault(),"%.1f",num);
        }else
            return null;

    }
    public static String dataFloatWithTwo(String data){
        if (data!=null){
            float num= Float.parseFloat(data);
           /* DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p=decimalFormat.format(num);//format 返回的是字符串*/
            return String.format(Locale.getDefault(),"%.2f",num);
        }else
            return null;

    }
    public static String dataFloatWithThree(String data){
        if (data!=null){
            float num= Float.parseFloat(data);
            /*DecimalFormat decimalFormat=new DecimalFormat(".000");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p=decimalFormat.format(num);//format 返回的是字符串*/
            return String.format(Locale.getDefault(),"%.3f",num);
        }else
            return null;

    }
}
