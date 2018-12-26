package com.blg.rtu.util;

import android.content.Context;
import android.content.res.Resources;

public class DisplayUtil {

    private DisplayUtil(){}

    public static int dp2px(Context context, int dp) {
        // mdpi 1dp=1px
        // hdpi 1dp=1.5px
        // xhdpi 720*1280 1dp=2px
        // xxhdpi 1080*1920 1dp=3px
        // xxxhdpi 1440*2560 1dp=4px
        //获得资源
        Resources resources = context.getResources();
        // 1个dp或sp等于多少个像素点  算是密度 也可以算是dp与像素的比率
        float density = resources.getDisplayMetrics().scaledDensity;
        // 6.5-->6
        // 6.5+0.5=7
        float px = density * dp + 0.5F;
        return (int) px;
    }
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param context
     * @param pxValue
     * @return
     */
     public static int px2dip(Context context, float pxValue) {
             final float scale = context.getResources().getDisplayMetrics().density;  
             return (int) (pxValue / scale + 0.5f);
     }

     /**
      * 将dip或dp值转换为px值，保证尺寸大小不变
      * @param dipValue
      * @param scale
      * @return
      */
     public static int dip2px(Context context, float dipValue) {
             final float scale = context.getResources().getDisplayMetrics().density;  
             return (int) (dipValue * scale + 0.5f);
     }

     /**
      * 将px值转换为sp值，保证文字大小不变
      * @param pxValue
      * @param fontScale
      * @return
      */
     public static int px2sp(Context context, float pxValue) {
             final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
             return (int) (pxValue / fontScale + 0.5f);
     }

     /**
      * 将px值转换为sp值，保证文字大小不变
      * @param spValue
      * @param fontScale
      * @return
      */
     public static int sp2px(Context context, float spValue) {
             final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
             return (int) (spValue * fontScale + 0.5f);
     }
}