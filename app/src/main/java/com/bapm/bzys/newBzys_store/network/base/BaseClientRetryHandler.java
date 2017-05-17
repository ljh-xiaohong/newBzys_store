package com.bapm.bzys.newBzys_store.network.base;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * http重新尝试连接：主要用于完成尝试重新连接
 * @author liguorui
 *
 */
public class BaseClientRetryHandler implements HttpRequestRetryHandler {
	private int max_retry_count;// 最大尝试连接的次数
	public BaseClientRetryHandler(int maxretryCount) {
		this.max_retry_count = maxretryCount;
	}
	private static HashSet<Class<? extends IOException>> exceptionWhiteList = new HashSet<Class<? extends IOException>>();
	private static HashSet<Class<? extends IOException>> exceptionBlackList = new HashSet<Class<? extends IOException>>();

	static {
		exceptionWhiteList.add(NoHttpResponseException.class);
		exceptionWhiteList.add(UnknownHostException.class);
		exceptionWhiteList.add(SocketException.class);
		exceptionBlackList.add(SSLException.class);
		exceptionBlackList.add(InterruptedIOException.class);
		exceptionBlackList.add(SocketTimeoutException.class);
	}

	public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
		if (executionCount > max_retry_count){
			return false;
		}
		if (exceptionBlackList.contains(exception.getClass())){
			return false;
		}
		if (exceptionWhiteList.contains(exception.getClass())){
			return true;
		}
		HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
		if (!idempotent) {
			return true;
		}

		Boolean b = (Boolean) context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sent = (b != null && b.booleanValue());
		if (!sent) {
			return true;
		}
		return false;
	}
}
