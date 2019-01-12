package com.blg.rtu.frmFunction.http;

public abstract class HttpCallback {
    public abstract void onError(String paramString, Exception paramException);

    public abstract void onSuccess(String paramString, Object paramObject);
}
