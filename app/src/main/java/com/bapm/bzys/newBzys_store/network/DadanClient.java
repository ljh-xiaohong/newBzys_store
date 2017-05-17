package com.bapm.bzys.newBzys_store.network;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.util.GlobalUtils;
import com.bapm.bzys.newBzys_store.util.JsonValidator;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import android.util.Log;
public class DadanClient{
	public final static int ST_SUCCESS = 0;
	public final static int ST_PARAM_FAIL    = 1;
	public final static int ST_RESPONSE_FAILE = 2;
	public final static int ST_NETWORK_FAILE = 3;
	public final static int ST_ACCOUNT_OTHER_LOGIN_FAILE = 4;
	/**
	 * 健值对参数请求方式
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String url,final Map<String, String> params,final DadanHandler handler){
		request(requestCode,url, params,null, handler);
	}

	/**
	 * json参数请求方式
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String url,final JSONObject params,final DadanHandler handler){
		request(requestCode,url, params,null, handler);
	}
	/**
	 * 健值对参数请求方式，带文件上传
	 * @param url
	 * @param params
	 * @param function
	 */
	/**
	 * 健值对请求方式，支持GET、POST请求方式
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String baseUrl,final Map<String, String> params,final File file,final DadanHandler handler){
		new Thread(new Runnable() {
			@Override
			public void run() {
					try {
						/**
						 * 封装请求方式
						 */
						String url = baseUrl;
						if(params!=null){
							StringBuffer strBuffer = new StringBuffer();
							strBuffer.append(url);
							strBuffer.append("?");
							for (Entry<String, String> e : params.entrySet()) { 
								if (e.getValue()!=null) {
									strBuffer.append(e.getKey());
									strBuffer.append("=");
									strBuffer.append(URLEncoder.encode(e.getValue()));
									strBuffer.append("&");
								}
							}
							strBuffer.deleteCharAt(strBuffer.length()-1); 
							url = strBuffer.toString();
						}
						HttpClient httpclient = GlobalUtils.getAndroidHttpClient(handler.getContext(),6000,null,3);
						httpclient.getParams().setParameter("charset", "UTF-8");
						httpclient.getParams().setParameter("Content-Type", "application/x-www-form-urlencoded");
						
						HttpGet httpGet = new HttpGet(url);
						if(DadanPreference.getInstance(handler.getContext()).hasTicket()){
							httpGet.setHeader("Authorization","BasicAuth " + DadanPreference.getInstance(handler.getContext()).getTicket());
						}
						HttpResponse response = httpclient.execute(httpGet);
						if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
							String value = EntityUtils.toString(response.getEntity()).toString();
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
							
						}else if(response.getStatusLine().getStatusCode()==HttpStatus.SC_UNAUTHORIZED){
							handler.onFaile(requestCode,ST_ACCOUNT_OTHER_LOGIN_FAILE, String.valueOf(response.getStatusLine().getStatusCode()));
						}else{
							handler.onFaile(requestCode,ST_NETWORK_FAILE, String.valueOf(response.getStatusLine().getStatusCode()));
						}
					} catch (ClientProtocolException e) {
						handler.onFaile(requestCode,ST_NETWORK_FAILE, "network is eror");
					} catch (IOException e) {
						handler.onFaile(requestCode,ST_NETWORK_FAILE, "network is eror");
					} catch (JSONException e) {
						handler.onFaile(requestCode, ST_PARAM_FAIL, "result cannot parse to JsonObject");
					} catch (KeyManagementException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					} catch (UnrecoverableKeyException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					} catch (CertificateException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					} catch (KeyStoreException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					} catch (NoSuchProviderException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					} catch (NoSuchAlgorithmException e1) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
					}
			}
		}).start();
	}
	/**
	 * json参数请求方式，带文件上传
	 * @param url
	 * @param params
	 * @param function
	 */
	public static void request(final int requestCode,final String url,final JSONObject params,final File file,final DadanHandler handler){
		new Thread(new Runnable() {	
			@Override
			public void run() {
				try {
					HttpClient httpclient = GlobalUtils.getAndroidHttpClient(handler.getContext(),6000,null,3);
					httpclient.getParams().setParameter("charset", "UTF-8");
					httpclient.getParams().setParameter("Content-Type", "application/json");
					HttpPost httpPost = new HttpPost(url);
					httpPost.setHeader("Content-Type", "application/json");
					if(DadanPreference.getInstance(handler.getContext()).hasTicket()){
						httpPost.setHeader("Authorization","BasicAuth " + DadanPreference.getInstance(handler.getContext()).getTicket());
					}
					Log.i("net","url:"+url);//打印请求的URL
					MultipartEntity multipartEntity = new MultipartEntity(); //文件传输

					try{
						/**
						 * 文件上传
						 */
						if(file!=null){
						    ContentBody contentFile = new FileBody(file);
						    multipartEntity.addPart("file", contentFile); // <input type="file" name="userfile" />  对应的
						    if(params!=null){
						    	multipartEntity.addPart("params",new StringBody(params.toString(), Charset.forName(HTTP.UTF_8)));
						    }
						    httpPost.setEntity(multipartEntity);
						}else{
						    /**
						     * 文本数据上传
						     */
							Log.i("net","Params:"+params.toString());//打印请求的参数
							if(params!=null){
								httpPost.setEntity(new StringEntity(params.toString(),"UTF-8"));
							}
						}
					}catch (final UnsupportedEncodingException e) {
						handler.onFaile(requestCode,ST_PARAM_FAIL, "params is erro");
						return;
					}
					
					final HttpResponse response = httpclient.execute(httpPost);
					if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						String value = EntityUtils.toString(response.getEntity()).toString();
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
						
					}else{
						handler.onFaile(requestCode,response.getStatusLine().getStatusCode(), "网络错误!");
					}
				} catch (ClientProtocolException e) {
					handler.onFaile(requestCode,ST_NETWORK_FAILE, "network is eror");
				} catch (IOException e) {
					handler.onFaile(requestCode,ST_NETWORK_FAILE, "network is eror");
				} catch (KeyManagementException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (UnrecoverableKeyException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (CertificateException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (KeyStoreException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (NoSuchProviderException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (NoSuchAlgorithmException e1) {
					handler.onFaile(requestCode,ST_PARAM_FAIL, "证书错误");
				} catch (JSONException e) {
					handler.onFaile(requestCode, ST_PARAM_FAIL, "result cannot parse to JsonObject");
				}
			}
		}).start();
	}
}
