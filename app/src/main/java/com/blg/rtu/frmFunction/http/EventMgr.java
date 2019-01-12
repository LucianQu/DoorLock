package com.blg.rtu.frmFunction.http;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventMgr<T> {
    private ConcurrentLinkedQueue<WeakReference<T>> mListenerList = new ConcurrentLinkedQueue();
    private ReferenceQueue<T> mListenerReferenceQueue = new ReferenceQueue();

    public void clear()
    {
        this.mListenerList.clear();
    }

    public void register(T paramT)
    {
        if (paramT == null) {
            return;
        }
        try
        {
            for (;;)
            {
                Reference re = this.mListenerReferenceQueue.poll();
                if (re == null) {
                    break;
                }
                this.mListenerList.remove(re);
            }
            Object localObject = this.mListenerList.iterator();
            while (((Iterator)localObject).hasNext()) {
                if (((WeakReference)((Iterator)localObject).next()).get() == paramT) {
                    return;
                }
            }
            WeakReference weakReference = new WeakReference(paramT, this.mListenerReferenceQueue);
            this.mListenerList.add(weakReference);
            return;
        }
        finally {}
    }

    public int size()
    {
        return this.mListenerList.size();
    }

    public void startNotify(INotifyCallback paramINotifyCallback)
    {
        try
        {
            Iterator localIterator = this.mListenerList.iterator();
            while (localIterator.hasNext())
            {
                Object localObject = ((WeakReference)localIterator.next()).get();
                if (localObject == null) {
                    localIterator.remove();
                } else {
                    paramINotifyCallback.onNotify(localObject);
                }
            }
            return;
        }
        finally {}
    }

    public void unregister(T paramT)
    {
        if (paramT == null) {
            return;
        }
        try
        {
            Iterator localIterator = this.mListenerList.iterator();
            while (localIterator.hasNext())
            {
                WeakReference localWeakReference = (WeakReference)localIterator.next();
                if (localWeakReference.get() == paramT)
                {
                    this.mListenerList.remove(localWeakReference);
                    return;
                }
            }
            return;
        }
        finally {}
    }

    public static abstract interface INotifyCallback<T>
    {
        public abstract void onNotify(T paramT);
    }
}
