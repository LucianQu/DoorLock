package com.blg.rtu.frmFunction.http;

import okhttp3.Request;

public abstract class HttpRequestCallback {
    public abstract void onFailure(Request paramRequest, Exception paramException);

    public void onProgress(long paramLong1, long paramLong2, boolean paramBoolean) {}

    public abstract void onSuccess(String paramString);
}
