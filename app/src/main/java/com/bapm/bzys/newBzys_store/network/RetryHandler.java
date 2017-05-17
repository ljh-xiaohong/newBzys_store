package com.bapm.bzys.newBzys_store.network;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

import com.lidroid.xutils.util.LogUtils;

public class RetryHandler implements HttpRequestRetryHandler {  //需要实现HttpRequestRetryHandler

    private static final int RETRY_SLEEP_INTERVAL = 500;

    private static HashSet<Class<?>> exceptionWhiteList = new HashSet<Class<?>>();
    private static HashSet<Class<?>> exceptionBlackList = new HashSet<Class<?>>();
    static {
        exceptionWhiteList.add(NoHttpResponseException.class);
        exceptionWhiteList.add(UnknownHostException.class);
        exceptionWhiteList.add(SocketException.class);

        exceptionBlackList.add(InterruptedIOException.class);
        exceptionBlackList.add(SSLHandshakeException.class);
    }

    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int retriedTimes, HttpContext context) {
        boolean retry = true;

        if (exception == null || context == null) {
            return false;
        }

        Object isReqSent = context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = isReqSent == null ? false : (Boolean) isReqSent;

        if (retriedTimes > maxRetries) {
            retry = false;
        } else if (exceptionBlackList.contains(exception.getClass())) {
            retry = false;
        } else if (exceptionWhiteList.contains(exception.getClass())) {
            retry = true;
        } else if (!sent) {
            retry = true;
        }

        if (retry) {
            try {
                Object currRequest = context.getAttribute(ExecutionContext.HTTP_REQUEST);
                if (currRequest != null) {
                //这里只允许GET请求的重试，因为在一般访问中POST重试会造成重复提交问题，因此不宜使用
                    if (currRequest instanceof HttpRequestBase) {
                        HttpRequestBase requestBase = (HttpRequestBase) currRequest;
                        retry = "GET".equals(requestBase.getMethod());
                    } else if (currRequest instanceof RequestWrapper) {
                        RequestWrapper requestWrapper = (RequestWrapper) currRequest;
                        retry = "GET".equals(requestWrapper.getMethod());
                    }
                } else {
                    retry = false;
                    LogUtils.e("retry error, curr request is null");
                }
            } catch (Throwable e) {
                retry = false;
                LogUtils.e("retry error", e);
            }
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_INTERVAL); // sleep a while and retry http request again.
        }

        return retry;
    }

}