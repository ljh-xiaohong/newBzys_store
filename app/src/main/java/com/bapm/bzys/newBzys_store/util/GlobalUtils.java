package com.bapm.bzys.newBzys_store.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import com.lidroid.xutils.http.client.DefaultSSLSocketFactory;
import com.lidroid.xutils.http.client.RetryHandler;
import com.lidroid.xutils.http.client.entity.GZipDecompressingEntity;

import android.content.Context;
import android.text.TextUtils;

public class GlobalUtils {

	public static HttpClient getAndroidHttpClient(Context context,int connTimeout,String userAgent, int retryTimes) throws IOException, CertificateException, KeyStoreException, NoSuchProviderException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException {
		
		AbstractHttpClient httpClient = null;
		// 设置请求控制参数
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setTimeout(params, connTimeout);
		HttpConnectionParams.setSoTimeout(params, connTimeout);
		HttpConnectionParams.setConnectionTimeout(params, connTimeout);

		if (TextUtils.isEmpty(userAgent)) {
			userAgent = System.getProperty("http.agents","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36");
		}
		HttpProtocolParams.setUserAgent(params, userAgent);
		// 设置最大的连接数
		ConnManagerParams.setMaxConnectionsPerRoute(params,new ConnPerRouteBean(10));
		ConnManagerParams.setMaxTotalConnections(params, 10);

		HttpConnectionParams.setTcpNoDelay(params, true); // 关闭Socket缓冲

		HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);// 本方法与setTcpNoDelay冲突

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));
//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		schemeRegistry.register(new Scheme("https",new EasySSLSocketFactory(), 443));
//		schemeRegistry.register(new Scheme("https",new EasySSLSocketFactory(), 8443));
		
//		AssetManager am = context.getAssets(); 
//		InputStream ins = null;
//		try {
//			ins = am.open("rmwx.bzys.cn.cer");
//			// 读取证书
//			CertificateFactory cerFactory = CertificateFactory.getInstance("X.509"); // 问1
//			Certificate cer = cerFactory.generateCertificate(ins);
//			// 创建一个证书库，并将证书导入证书库
//			KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC"); // 问2
//			keyStore.load(null, null);
//			keyStore.setCertificateEntry("trust", cer);
//			// 把咱的证书库作为信任证书库
//			SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
//			schemeRegistry.register(new Scheme("https", socketFactory, 443));
//		} finally {
//			ins.close();
//		}
		
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
		httpClient.setHttpRequestRetryHandler(new RetryHandler(retryTimes));
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(org.apache.http.HttpRequest httpRequest,HttpContext httpContext)throws org.apache.http.HttpException, IOException {
				if (!httpRequest.containsHeader("Accept-Encoding")) {
					httpRequest.addHeader("Accept-Encoding", "gzip");
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext httpContext)
					throws org.apache.http.HttpException, IOException {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GZipDecompressingEntity(
									response.getEntity()));
							return;
						}
					}
				}
			}
		});
		return httpClient;
	}
}
