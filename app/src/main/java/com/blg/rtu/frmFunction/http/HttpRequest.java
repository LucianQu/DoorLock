package com.blg.rtu.frmFunction.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {
    private static final String TAG = "HttpRequest";
    private static volatile HttpRequest instance;
    private final String AUTHORIZATION = "Cookie";
    private final String CERTIFICATE = "server.pem";
    private final String LANGUAGE = "language";
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private final String USERAGENT = "User-Agent";
    private final String VERSION_CODE = "versionCode";
    private Request.Builder builder;
    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30L, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).build();
    private X509TrustManager trustManager = new X509TrustManager()
    {
        public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
                throws CertificateException
        {}

        public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
                throws CertificateException
        {}

        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    };

    private void get(String paramString, final HttpRequestCallback paramHttpRequestCallback)
    {
        Request request = getRequestBuilder().url(paramString).build();
        this.client.newCall(request).enqueue(new Callback()
        {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
            {
                HttpRequest.this.onFailure(paramAnonymousCall.request(), paramAnonymousIOException, paramHttpRequestCallback);
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
            {
                try
                {
                    String re = paramAnonymousResponse.body().string();
                    HttpRequest.this.onSuccess(re, paramHttpRequestCallback);
                    return;
                }
                catch (IOException e)
                {
                    HttpRequest.this.onFailure(paramAnonymousCall.request(), e, paramHttpRequestCallback);
                }
            }
        });
    }

    public static HttpRequest getInstance()
    {
        if (instance == null) {
            try
            {
                if (instance == null) {
                    instance = new HttpRequest();
                }
            }
            finally {}
        }
        return instance;
    }

    public static String nullToStr(Object paramObject)
    {
        if ((paramObject != null) && (!paramObject.toString().equals("null"))) {
            return paramObject.toString().trim();
        }
        return "";
    }


    private Request.Builder getRequestBuilder()
    {
        this.builder = new Request.Builder();
        this.builder.addHeader("versionCode", nullToStr(Integer.valueOf(AppUtil.getVerCode())));
        this.builder.removeHeader("Cookie");
        Object localObject = nullToStr(PreferencesUtil.getString(ComApplication.getContext(), "token"));
        this.builder.addHeader("Cookie", (String)localObject);
        this.builder.removeHeader("User-Agent").addHeader("User-Agent", getUserAgent());
        localObject = LocaleUtils.getUserLocale(ComApplication.getContext());
        Request.Builder localBuilder = this.builder;
        if (localObject != null) {
            localObject = ((Locale)localObject).getLanguage();
        } else {
            localObject = "";
        }
        localBuilder.addHeader("language", (String)localObject);
        return this.builder;
    }

    private SSLSocketFactory getSSLSocketFactory(Context paramContext, String paramString)
    {
        if (paramContext == null) {
            return null;
        }
        try
        {
            InputStream inputStream = paramContext.getResources().getAssets().open(paramString);
            Certificate localCertificate = CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry(paramString, localCertificate);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        }
        catch (Exception e)
        {
            Log.e("HttpRequest", paramContext.toString());
        }
        return null;
    }

    private static String getUserAgent()
    {
        String str = "" ;
        StringBuffer stringBuffer  ;
        if (Build.VERSION.SDK_INT >= 17) {}
        try
        {
            str = WebSettings.getDefaultUserAgent(ComApplication.getContext());
        }
        catch (Exception localException)
        {
        }
        str = System.getProperty("http.agent");
        stringBuffer = new StringBuffer();
        int j = 0 ;
        int i = 0 ;
        j = str.length();
        i = 0;
        while (i < j)
        {
            char c = str.charAt(i);
            if ((c > '\037') && (c < '')) {
                stringBuffer.append(c);
            } else {
                stringBuffer.append(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
            }
            i += 1;
        }
        return stringBuffer.toString();
    }

    private void handleCookies(String paramString, Response paramResponse)
    {
        if (("https://api.365uj.com/api/user/login".equals(paramString)) || ("https://api.365uj.com/api/user/create".equals(paramString)))
        {
            Iterator<String> iterator = paramResponse.headers("Set-Cookie").iterator();
            while (iterator.hasNext())
            {
                String str = (String)iterator.next();
                if ((!TextUtils.isEmpty(str)) && (str.startsWith("login_user_center="))) {
                    PreferencesUtil.putString(ComApplication.getContext(), "token", str.split(";")[0]);
                }
            }
        }
    }

    private void onFailure(Request paramRequest, Exception paramException, HttpRequestCallback paramHttpRequestCallback)
    {
        if (paramHttpRequestCallback != null) {
            paramHttpRequestCallback.onFailure(paramRequest, paramException);
        }
    }

    private void onProgress(long paramLong1, long paramLong2, boolean paramBoolean, HttpRequestCallback paramHttpRequestCallback)
    {
        if (paramHttpRequestCallback != null) {
            paramHttpRequestCallback.onProgress(paramLong1, paramLong2, paramBoolean);
        }
    }

    private void onSuccess(String paramString, HttpRequestCallback paramHttpRequestCallback)
    {
        if (paramHttpRequestCallback != null) {
            paramHttpRequestCallback.onSuccess(paramString);
        }
    }

    public void get(String paramString, JSONObject paramJSONObject, HttpRequestCallback paramHttpRequestCallback)
    {
        Object localObject = paramString;
        if (paramJSONObject != null)
        {
            localObject = new StringBuilder(paramString);
            if (!paramString.contains("?")) {
                ((StringBuilder)localObject).append("?");
            }
            Iterator<String> iterator = paramJSONObject.keys();
            while (iterator.hasNext())
            {
                String str1 = (String)iterator.next();
                String str2 = paramJSONObject.optString(str1);
                if (!TextUtils.isEmpty(str2))
                {
                    ((StringBuilder)localObject).append("&");
                    ((StringBuilder)localObject).append(str1);
                    ((StringBuilder)localObject).append("=");
                    ((StringBuilder)localObject).append(nullToStr(str2));
                }
            }
            localObject = ((StringBuilder)localObject).toString();
        }
        get((String)localObject, paramHttpRequestCallback);
    }

    public void post(final String paramString, JSONObject paramJSONObject, final HttpRequestCallback paramHttpRequestCallback)
    {
        JSONObject localJSONObject = paramJSONObject;
        if (paramJSONObject == null) {
            localJSONObject = new JSONObject();
        }
        RequestBody requestBody = RequestBody.create(this.MEDIA_TYPE_JSON, localJSONObject.toString());
        this.client.newCall(getRequestBuilder().url(paramString).post(requestBody).build()).enqueue(new Callback()
        {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
            {
                HttpRequest.this.onFailure(paramAnonymousCall.request(), paramAnonymousIOException, paramHttpRequestCallback);
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
            {
                try
                {
                    HttpRequest.this.handleCookies(paramString, paramAnonymousResponse);
                    String body = paramAnonymousResponse.body().string();
                    HttpRequest.this.onSuccess(body, paramHttpRequestCallback);
                    return;
                }
                catch (IOException e)
                {
                    HttpRequest.this.onFailure(paramAnonymousCall.request(), e, paramHttpRequestCallback);
                }
            }
        });
    }

    public void upload(String paramString1, String paramString2, HashMap<String, Object> paramHashMap, final HttpRequestCallback paramHttpRequestCallback)
    {
        MultipartBody.Builder localBuilder = new MultipartBody.Builder();
        localBuilder.setType(MultipartBody.FORM);
        Iterator<Map.Entry<String, Object>> iterator = paramHashMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry localEntry = (Map.Entry)iterator.next();
            Object localObject = localEntry.getValue();
            if ((localObject instanceof File))
            {
                localObject = (File)localObject;
                if (paramString2 != null) {
                    localBuilder.addFormDataPart(paramString2, ((File)localObject).getName(), RequestBody.create(this.MEDIA_TYPE_STREAM, (File)localObject));
                } else {
                    localBuilder.addFormDataPart((String)localEntry.getKey(), ((File)localObject).getName(), RequestBody.create(this.MEDIA_TYPE_STREAM, (File)localObject));
                }
            }
            else
            {
                localBuilder.addFormDataPart((String)localEntry.getKey(), localObject.toString());
            }
        }
        MultipartBody multipartBody = localBuilder.build();
        Request request = getRequestBuilder().url(paramString1).post(multipartBody).build();
        this.client.newCall(request).enqueue(new Callback()
        {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
            {
                HttpRequest.this.onFailure(paramAnonymousCall.request(), paramAnonymousIOException, paramHttpRequestCallback);
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
                    throws IOException
            {
                try
                {
                    String result = paramAnonymousResponse.body().string();
                    HttpRequest.this.onSuccess(result, paramHttpRequestCallback);
                    return;
                }
                catch (IOException e)
                {
                    HttpRequest.this.onFailure(paramAnonymousCall.request(), e, paramHttpRequestCallback);
                }
            }
        });
    }
}
