package com.bapm.bzys.newBzys_store.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
/**
 * 记录用户名，密码之类的首选项
 */
/**
 * 记录用户名，密码之类的首选项
 */
public class DadanPreference {
	private static DadanPreference preference = null;
	private SharedPreferences sharedPreference;
	private String packageName = "";
	private final String TICKET= "TICKET";
	private final String QI_NIU_TOKEN= "QI_NIU_TOKEN";
	private Context context;
	public static synchronized DadanPreference getInstance(Context context){
		if(preference == null)
			preference = new DadanPreference(context);
		return preference;
	}
	
	public DadanPreference(Context context){
		this.context = context;
		packageName = context.getPackageName() + "_preferences";
		sharedPreference = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
	}

	public void setLong(String key, long value){
        Editor editor = sharedPreference.edit();
        editor.putLong(key, value);
        editor.commit();
	}

	public long getLong(String key){
	    return sharedPreference.getLong(key, 0);
	}
	
	public void setString(String key, String value){
		Editor editor = sharedPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getString(String key){
		return sharedPreference.getString(key, "");
	}
	
	public void setBoolean(String key, boolean value){
		Editor editor = sharedPreference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public boolean getBoolean(String key){
		return sharedPreference.getBoolean(key, false);
	}
	
	public void setQNToken(String id){
		Editor editor = sharedPreference.edit();
		editor.putString(QI_NIU_TOKEN, id);
		editor.commit();
	}
	
	public String getQNToken(){
		return sharedPreference.getString(QI_NIU_TOKEN, "-1");
	}
	public boolean hasQNToken(){
		return sharedPreference.contains(QI_NIU_TOKEN);
	}
	public void removeQNToken(){
		Editor editor = sharedPreference.edit();
		editor.putString(QI_NIU_TOKEN, "-1");
		editor.commit();
	}

	
	public void setTicket(String id){
		Editor editor = sharedPreference.edit();
		editor.putString(TICKET, id);
		editor.commit();
	}
	
	public String getTicket(){
		return sharedPreference.getString(TICKET, "-1");
	}
	public boolean hasTicket(){
		return sharedPreference.contains(TICKET);
	}
	public void removeTicket(){
		Editor editor = sharedPreference.edit();
		editor.putString(TICKET, "-1");
		editor.commit();
	}
	private String  serialize(Object obj){
		try {
			long startTime = System.currentTimeMillis();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			String serStr = byteArrayOutputStream.toString("ISO-8859-1");
			serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
			objectOutputStream.close();
			byteArrayOutputStream.close();
			Log.d("serial", "serialize str =" + serStr);
			long endTime = System.currentTimeMillis();
			Log.d("serial", "序列化耗时为:" + (endTime - startTime));
			return serStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 反序列化对象
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private <T>T diserialization(String str,Class<T> cls){
		long startTime = System.currentTimeMillis();
		try {
			String redStr = java.net.URLDecoder.decode(str, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			T t = (T) objectInputStream.readObject();
			objectInputStream.close();
			byteArrayInputStream.close();
			long endTime = System.currentTimeMillis();
			Log.d("serial", "反序列化耗时为:" + (endTime - startTime));
			return t;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
