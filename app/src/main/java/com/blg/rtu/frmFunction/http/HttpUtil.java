package com.blg.rtu.frmFunction.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.blg.rtu.frmFunction.bean.BaseVo;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Request;

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static HttpUtil single;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static HttpUtil getInstance()
    {
        if (single == null) {
            single = new HttpUtil();
        }
        return single;
    }

    private void sendError(final String paramString, final Exception paramException, final HttpCallback paramHttpCallback)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("接口地址为");
        localStringBuilder.append(paramString);
        localStringBuilder.append(".....请求异常");
        Log.e("HttpUtil", localStringBuilder.toString());
        this.handler.post(new Runnable()
        {
            public void run()
            {
                paramHttpCallback.onError(paramString, paramException);
            }
        });
    }

    private void sendMessage(final String paramString, final Object paramObject, final HttpCallback paramHttpCallback)
    {
        this.handler.post(new Runnable()
        {
            public void run()
            {
                paramHttpCallback.onSuccess(paramString, paramObject);
            }
        });
    }

    private void toLoginActivity() {}

    private void verifyDatas(String paramString1, String paramString2, Type paramType, HttpCallback paramHttpCallback)
    {
        if (1003 == ((BaseVo)JsonUtil.getModelFromJSON(paramString2, BaseVo.class)).getCode()) {
            EventManager.getInstance().notify(null, "appExit");
        }
        if (paramType != null)
        {
            sendMessage(paramString1, JsonUtil.getModelFromJSON(paramString2, paramType), paramHttpCallback);
            return;
        }
        sendMessage(paramString1, paramString2, paramHttpCallback);
    }

    public void get(final String paramString, final Type paramType, final HttpCallback paramHttpCallback)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        localStringBuilder.append("");
        Log.e("接口请求URL", localStringBuilder.toString());
        HttpRequest.getInstance().get(paramString, null, new HttpRequestCallback()
        {
            public void onFailure(Request paramAnonymousRequest, Exception paramAnonymousException)
            {
                HttpUtil.this.sendError(paramString, paramAnonymousException, paramHttpCallback);
            }

            public void onSuccess(String paramAnonymousString)
            {
                Log.e("接口响应", paramAnonymousString);
                try
                {
                    HttpUtil.this.verifyDatas(paramString, paramAnonymousString, paramType, paramHttpCallback);
                    return;
                }
                catch (Exception e)
                {
                    HttpUtil.this.sendError(paramString, e, paramHttpCallback);
                }
            }
        });
    }

    public void post(final String paramString, JSONObject paramJSONObject, final HttpCallback paramHttpCallback)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        localStringBuilder.append("");
        Log.e("接口请求URL", localStringBuilder.toString());
        if (paramJSONObject != null) {
            Log.e("接口请求参数", paramJSONObject.toString());
        }
        HttpRequest.getInstance().post(paramString, paramJSONObject, new HttpRequestCallback()
        {
            public void onFailure(Request paramAnonymousRequest, Exception paramAnonymousException)
            {
                HttpUtil.this.sendError(paramString, paramAnonymousException, paramHttpCallback);
            }

            public void onSuccess(String paramAnonymousString)
            {
                Log.e("接口响应", paramAnonymousString);
                try
                {
                    HttpUtil.this.verifyDatas(paramString, paramAnonymousString, null, paramHttpCallback);
                    return;
                }
                catch (Exception e)
                {
                    HttpUtil.this.sendError("", e, paramHttpCallback);
                }
            }
        });
    }

    public void post(final String paramString, JSONObject paramJSONObject, final Type paramType, final HttpCallback paramHttpCallback)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        localStringBuilder.append("");
        Log.e("接口请求URL", localStringBuilder.toString());
        if (paramJSONObject != null) {
            Log.e("接口请求参数", paramJSONObject.toString());
        }
        try
        {
            paramJSONObject.put("timestamp", new Date().getTime() / 1000L);
            HttpRequest.getInstance().post(paramString, paramJSONObject, new HttpRequestCallback()
            {
                public void onFailure(Request paramAnonymousRequest, Exception paramAnonymousException)
                {
                    HttpUtil.this.sendError(paramString, null, paramHttpCallback);
                }

                public void onSuccess(String paramAnonymousString)
                {
                    Log.e("接口响应", paramAnonymousString);
                    try
                    {
                        HttpUtil.this.verifyDatas(paramString, paramAnonymousString, paramType, paramHttpCallback);
                        return;
                    }
                    catch (Exception e)
                    {
                        HttpUtil.this.sendError("", e, paramHttpCallback);
                    }
                }
            });
            return;
        }
        catch (Exception localException)
        {
            for (;;) {}
        }
    }

    public void upload(final String paramString1, String paramString2, HashMap<String, Object> paramHashMap, final Type paramType, final HttpCallback paramHttpCallback)
    {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramString1);
        ((StringBuilder)localObject).append("");
        Log.e("接口请求URL", ((StringBuilder)localObject).toString());
        if (paramHashMap == null) {
            return;
        }
        Log.e("接口请求参数", paramHashMap.toString());
        localObject = paramString2;
        if (TextUtils.isEmpty(paramString2)) {
            localObject = "pictureFile";
        }
        HttpRequest.getInstance().upload(paramString1, (String)localObject, paramHashMap, new HttpRequestCallback()
        {
            public void onFailure(Request paramAnonymousRequest, Exception paramAnonymousException)
            {
                HttpUtil.this.sendError(paramString1, paramAnonymousException, paramHttpCallback);
            }

            public void onSuccess(String paramAnonymousString)
            {
                Log.e("接口响应", paramAnonymousString);
                try
                {
                    HttpUtil.this.verifyDatas(paramString1, paramAnonymousString, paramType, paramHttpCallback);
                    return;
                }
                catch (Exception e)
                {
                    HttpUtil.this.sendError(paramString1, e, paramHttpCallback);
                }
            }
        });
    }
}
