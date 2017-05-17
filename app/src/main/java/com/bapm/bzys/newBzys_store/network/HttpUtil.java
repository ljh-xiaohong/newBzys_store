package com.bapm.bzys.newBzys_store.network;
import android.util.Log;

import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.util.JsonValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * 忽略Https证书是否正确的Https Post请求工具类
 */
public class HttpUtil {
    private static final String DEFAULT_CHARSET = "UTF-8"; // 默认字符集
    private static final String _GET = "GET"; // GET
    private static final String _POST = "POST";// POST
    
	public final static int ST_SUCCESS = 0;
	public final static int ST_PARAM_FAIL    = 1;
	public final static int ST_RESPONSE_FAILE = 2;
	public final static int ST_NETWORK_FAILE = 3;
	public final static int ST_ACCOUNT_OTHER_LOGIN_FAILE = 401;
	/**
	 * 健值对参数请求方式
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String url,final Map<String, String> params,final DadanHandler handler){
		getRequst(requestCode,url,params, handler);
	}
	/**
	 * 健值对参数请求方式
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String url,JSONObject params,final DadanHandler handler){
		postRequst(requestCode,url, params, handler);
	}
	
    /**
     * 初始化http请求参数
     * @throws IOException 
     */
    private static HttpURLConnection initHttp(String url, String method, Map<String, String> headers) throws IOException {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
        // 连接超时
        http.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(25000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        http.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    /**
     * 初始化https请求参数
     */
    private static HttpsURLConnection initHttps(String url, String method, Map<String, String> headers)throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象  
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL _url = new URL(url);
        HttpsURLConnection https = (HttpsURLConnection) _url.openConnection();
        // 设置域名校验
        https.setHostnameVerifier(new TrustAnyHostnameVerifier());
        https.setSSLSocketFactory(ssf);
        // 连接超时
        https.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间
        https.setReadTimeout(25000);
        https.setRequestMethod(method);
//		https.setRequestProperty("Content-Type", "application/json");
//		https.setRequestProperty("accept", "*/*");
//		https.setRequestProperty("connection", "Keep-Alive");
//		https.addRequestProperty("Accept-Charset", "UTF-8");
//		https.addRequestProperty("User-Agent", "***SDK/1.0");
//		https.setRequestProperty("Cache-Control", "no-cache");
		if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
            	https.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
//        https.setDoOutput(true);
        https.setDoInput(true);
        https.connect();
        return https;
    }

    /**
     * get请求
     */
    public static void getRequst(final int requestCode,final String url, final Map<String, String> params,final DadanHandler handler) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
			       StringBuffer bufferRes = null;
			        try {
			            HttpURLConnection http = null;
			            Map<String, String> headers = new HashMap<String, String>();
						if(DadanPreference.getInstance(handler.getContext()).hasTicket()){
							headers.put("Authorization","BasicAuth " + DadanPreference.getInstance(handler.getContext()).getTicket());
						}
			            if (isHttps(url)) {
			                http = initHttps(initParams(url, params), _GET, headers);
			            } else {
			                http = initHttp(initParams(url, params), _GET, headers);
			            }
			            int statusCode = http.getResponseCode();
						Log.e("statusCode",statusCode+"");
						if(statusCode==HttpStatus.SC_OK){
				            InputStream in = http.getInputStream();
				            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
				            String valueString = null;
				            bufferRes = new StringBuffer();
				            while ((valueString = read.readLine()) != null) {
				                bufferRes.append(valueString);
				            }
				            in.close();
				            if (http != null) {
				                http.disconnect();// 关闭连接
				            }
				            String value = bufferRes.toString();
				            if(!new JsonValidator().validate(value)){
								handler.onFaile(requestCode,ST_NETWORK_FAILE,"数据格式异常");
								return;
							}
							Object json = new JSONTokener(value).nextValue();
							if(json instanceof JSONObject){
							    JSONObject jsonObject = (JSONObject)json;
								if(handler!=null){
									handler.onSuccess(requestCode,jsonObject);					
								}
							}else if (json instanceof JSONArray){
								JSONArray jsonArray = (JSONArray)json;
								if(handler!=null){
									handler.onSuccess(requestCode,jsonArray);					
								}
							}
			            }else if(statusCode==HttpStatus.SC_UNAUTHORIZED){
							handler.onFaile(statusCode,ST_ACCOUNT_OTHER_LOGIN_FAILE, String.valueOf(statusCode));
						}else{
							handler.onFaile(requestCode,ST_NETWORK_FAILE, String.valueOf(statusCode));
						}
			        } catch (Exception e) {
			        	handler.onFaile(requestCode,ST_NETWORK_FAILE, String.valueOf(e));
			        }
			}
    	}).start();
    }
    /**
     * post请求
     */
    public static void postRequst(final int requestCode,final String url, final JSONObject params,final DadanHandler handler) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
		            HttpURLConnection http = null;
		            Map<String, String> headers = new HashMap<String, String>();
					if(DadanPreference.getInstance(handler.getContext()).hasTicket()){
						headers.put("Authorization","BasicAuth " + DadanPreference.getInstance(handler.getContext()).getTicket());
					}
					headers.put("Content-Type", "application/json");
		            if (isHttps(url)) {
		                http = initHttps(url, _POST, headers);
		            } else {
		                http = initHttp(url, _POST, headers);
		            }
		            OutputStream out = http.getOutputStream();
		            out.write((params.toString()).getBytes(DEFAULT_CHARSET));
		            out.flush();
		            out.close();
		            int statusCode = http.getResponseCode();
					Log.e("statusCode",statusCode+"");
		            if(statusCode==HttpStatus.SC_OK){
		            	InputStream in = http.getInputStream();
			            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
			            String valueString = null;
			            StringBuffer  bufferRes = new StringBuffer();
			            while ((valueString = read.readLine()) != null) {
			                bufferRes.append(valueString);
			            }
			            in.close();
			            if (http != null) {
			                http.disconnect();// 关闭连接
			            }
			            if(!new JsonValidator().validate(bufferRes.toString())){
							handler.onFaile(requestCode,ST_NETWORK_FAILE,"数据格式异常");
							return;
						}
						Object json = new JSONTokener(bufferRes.toString()).nextValue();
						if(json instanceof JSONObject){
						    JSONObject jsonObject = (JSONObject)json;
							if(handler!=null){
								handler.onSuccess(requestCode,jsonObject);					
							}
						}else if (json instanceof JSONArray){
							JSONArray jsonArray = (JSONArray)json;
							if(handler!=null){
								handler.onSuccess(requestCode,jsonArray);					
							}
						}
			            if (http != null) {
			                http.disconnect();// 关闭连接
			            }
		            }else if(statusCode==HttpStatus.SC_UNAUTHORIZED){
						handler.onFaile(statusCode,ST_ACCOUNT_OTHER_LOGIN_FAILE, String.valueOf(statusCode));
					}else{
						handler.onFaile(requestCode,ST_NETWORK_FAILE, String.valueOf(statusCode));
					}
		        } catch (Exception e) {
		        	handler.onFaile(requestCode,ST_NETWORK_FAILE, "network is eror");
		        }
			}
    	}).start();
    }
    /**
     * 初始化参数
     */
    public static String initParams(String url, Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        }
        sb.append(map2Url(params));
        return sb.toString();
    }
    /**
     * map转url参数
     */
    public static String map2Url(Map<String, String> paramToMap) {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            } else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (null != value && !"".equals(value.trim())) {
                try {
                    url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return url.toString();
    }

    /**
     * 检测是否https
     */
    private static boolean isHttps(String url) {
        return url.startsWith("https");
    }

    /**
     * 不进行主机名确认
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 信任所有主机 对于任何证书都不做SSL检测
     * 安全验证机制，而Android采用的是X509验证
     */
    private static class MyX509TrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        	System.out.println(authType);
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        	System.out.println(authType);
        }
    }
}