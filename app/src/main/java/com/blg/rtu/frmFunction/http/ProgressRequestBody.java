package com.blg.rtu.frmFunction.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody{
    private BufferedSink bufferedSink;
    private final ProgressListener progressListener;
    private final RequestBody requestBody;

    public ProgressRequestBody(RequestBody paramRequestBody, ProgressListener paramProgressListener)
    {
        this.requestBody = paramRequestBody;
        this.progressListener = paramProgressListener;
    }

    private Sink sink(Sink paramSink)
    {
        return new ForwardingSink(paramSink)
        {
            long bytesWritten = 0L;
            long contentLength = 0L;

            public void write(Buffer paramAnonymousBuffer, long paramAnonymousLong)
                    throws IOException, IllegalStateException
            {
                super.write(paramAnonymousBuffer, paramAnonymousLong);
                if (this.contentLength == 0L) {
                    this.contentLength = ProgressRequestBody.this.contentLength();
                }
                this.bytesWritten += paramAnonymousLong;
                ProgressListener listener = ProgressRequestBody.this.progressListener;
                paramAnonymousLong = this.bytesWritten;
                long l = this.contentLength;
                boolean bool;
                if (this.bytesWritten == this.contentLength) {
                    bool = true;
                } else {
                    bool = false;
                }
                listener.onProgress(paramAnonymousLong, l, bool);
            }
        };

    }

    public long contentLength()
            throws IOException
    {
        return this.requestBody.contentLength();
    }

    public MediaType contentType()
    {
        return this.requestBody.contentType();
    }

    public void writeTo(BufferedSink paramBufferedSink)
            throws IOException, IllegalStateException
    {
        if (this.bufferedSink == null) {
            this.bufferedSink = Okio.buffer(sink(paramBufferedSink));
        }
        this.requestBody.writeTo(this.bufferedSink);
        this.bufferedSink.flush();
    }
}
