package com.blg.rtu.frmFunction.http;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody{

    private BufferedSource bufferedSource;
    private final ProgressListener progressListener;
    private final ResponseBody responseBody;

    public ProgressResponseBody(ResponseBody paramResponseBody, ProgressListener paramProgressListener)
    {
        this.responseBody = paramResponseBody;
        this.progressListener = paramProgressListener;
    }

    private Source source(Source paramSource)
    {
        return new ForwardingSource(paramSource)
        {
            long totalBytesRead = 0L;

            public long read(Buffer paramAnonymousBuffer, long paramAnonymousLong)
                    throws IOException
            {
                long l1 = super.read(paramAnonymousBuffer, paramAnonymousLong);
                long l2 = this.totalBytesRead;
                if (l1 != -1L) {
                    paramAnonymousLong = l1;
                } else {
                    paramAnonymousLong = 0L;
                }
                this.totalBytesRead = (l2 + paramAnonymousLong);
                ProgressListener listener = ProgressResponseBody.this.progressListener;
                paramAnonymousLong = this.totalBytesRead;
                l2 = ProgressResponseBody.this.responseBody.contentLength();
                boolean bool;
                if (l1 == -1L) {
                    bool = true;
                } else {
                    bool = false;
                }
                listener.onProgress(paramAnonymousLong, l2, bool);
                return l1;
            }
        };
    }

    public long contentLength()
    {
        return this.responseBody.contentLength();
    }

    public MediaType contentType()
    {
        return this.responseBody.contentType();
    }

    public BufferedSource source()
    {
        if (this.bufferedSource == null) {
            this.bufferedSource = Okio.buffer(source(this.responseBody.source()));
        }
        return this.bufferedSource;
    }
}
