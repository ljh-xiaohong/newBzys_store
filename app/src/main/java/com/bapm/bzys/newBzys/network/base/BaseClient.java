package com.bapm.bzys.newBzys.network.base;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class BaseClient {
	private AbstractHttpClient httpClient;
	// 请求失败后，尝试连接次数
	public static final int DEFAULT_RETIES_COUNT = 5;
	// 设置最大连接数
	public final static int MAX_TOTAL_CONNECTIONS = 100;
	// 设置获取连接的最大等待时间
	public final static int WAIT_TIMEOUT = 30000;
	// 设置每个路由最大连接数
	public final static int MAX_ROUTE_CONNECTIONS = 100;
	// 设置连接超时时间
	public final static int CONNECT_TIMEOUT = 10000;
	// 设置读取超时时间
	public final static int READ_TIMEOUT = 10000;
	/**
	 * 构造方法，调用初始化方法
	 */
	public BaseClient() {
		initHttpClient();
	}
	/**
	 * 初始化客户端参数
	 */
	private void initHttpClient() {
		//http的参数
		HttpParams httpParams = new BasicHttpParams();
		//设置最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams,MAX_TOTAL_CONNECTIONS);
		//设置获取连接的最大等待时间
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
		//设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		// 设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		// 设置读取超时时间
		HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));//设置端口80
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));//设置端口443
		//就是管理SchemeRegistry的
		ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
		//创建http重新连接的handler
		httpClient.setHttpRequestRetryHandler(new BaseClientRetryHandler(DEFAULT_RETIES_COUNT));
	}
}
