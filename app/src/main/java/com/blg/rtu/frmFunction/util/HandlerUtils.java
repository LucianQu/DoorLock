package com.blg.rtu.frmFunction.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;

public abstract class HandlerUtils<T> extends Handler {
    private WeakReference<T> reference ;
    public HandlerUtils(T paramT) {
        this.reference = new WeakReference(paramT) ;
        checkStatic() ;
    }

    public HandlerUtils(T paramT, Callback paramCallback) {
        super(paramCallback);
        this.reference = new WeakReference(paramT) ;
        checkStatic();
    }

    public HandlerUtils(T paramT, Looper paramLooper)
    {
        super(paramLooper);
        this.reference = new WeakReference(paramT);
        checkStatic();
    }

    public HandlerUtils(T paramT, Looper paramLooper, Callback paramCallback)
    {
        super(paramLooper, paramCallback);
        this.reference = new WeakReference(paramT);
        checkStatic();
    }

    private void checkStatic()
    {
        Class localClass = getClass();
        if ((!Modifier.isStatic(localClass.getModifiers())) && (localClass.getName().indexOf('$') > 0)) {
            throw new RuntimeException("handler not static");
        }
    }

    public final void dispatchMessage(Message paramMessage)
    {
        super.dispatchMessage(paramMessage);
    }

    public final void handleMessage(Message paramMessage)
    {
        T localObject = this.reference.get();
        if (localObject != null) {
            handleMessage(localObject, paramMessage);
        }
    }

    public abstract void handleMessage(T paramT, Message paramMessage);
}
