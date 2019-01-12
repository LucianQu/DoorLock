package com.blg.rtu.frmFunction.http;

public class EventManager {
    private static volatile EventManager instance;
    private EventMgr<OnNotifyListener> listenerMrg = new EventMgr();

    public static EventManager getInstance()
    {
        if (instance == null) {
            try
            {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
            finally {}
        }
        return instance;
    }

    public void notify(final Object paramObject, final String paramString)
    {
        this.listenerMrg.startNotify(new EventMgr.INotifyCallback()
        {
            @Override
            public void onNotify(Object paramT) {
                OnNotifyListener listener = (OnNotifyListener) paramT ;
                if (listener != null) {
                    listener.onNotify(paramObject, paramString);
                }
            }

        });
    }

    public void registerListener(OnNotifyListener paramOnNotifyListener)
    {
        this.listenerMrg.register(paramOnNotifyListener);
    }

    public void unRegisterListener(OnNotifyListener paramOnNotifyListener)
    {
        this.listenerMrg.unregister(paramOnNotifyListener);
    }

    public static abstract interface OnNotifyListener
    {
        public abstract void onNotify(Object paramObject, String paramString);
    }
}
