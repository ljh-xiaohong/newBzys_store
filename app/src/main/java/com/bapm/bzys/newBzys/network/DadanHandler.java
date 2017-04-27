package com.bapm.bzys.newBzys.network;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bapm.bzys.newBzys.network.bean.Result;
import com.bapm.bzys.newBzys.network.function.interf.Function;

public class DadanHandler extends Handler{
	public static final int HANDLER_RESULT_SUCCESS_JSONOBJECT = 1;
	public static final int HANDLER_RESULT_SUCCESS_JSONOARRAY = 2;
	public static final int HANDLER_RESULT_FAIL    = 0;
	private Function function;
	private Context context;
	public DadanHandler(Function function, Context context){
		this.function = function;
		this.context = context;
	}
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Result result = (Result) msg.obj;
		switch (msg.what) {
		case HANDLER_RESULT_SUCCESS_JSONOBJECT:
			function.onSuccess(result.getRequestCode(),result.getResultJson());
			break;
		case HANDLER_RESULT_SUCCESS_JSONOARRAY:
			function.onSuccess(result.getRequestCode(),result.getResultArray());
			break;
		case HANDLER_RESULT_FAIL:
			function.onFaile(result.getRequestCode(), result.getStatus(), result.getMsg());
			break;
		default:
			break;
		}
	}
	public void onSuccess(int requestCode,JSONObject result){
		Message message = this.obtainMessage();
		Result obj = new Result();
		obj.setRequestCode(requestCode);
		obj.setResultJson(result);
		message.what=HANDLER_RESULT_SUCCESS_JSONOBJECT;
		message.obj=obj;
		this.sendMessage(message);
	}
	public void onSuccess(int requestCode,JSONArray result){
		Message message = this.obtainMessage();
		Result obj = new Result();
		obj.setRequestCode(requestCode);
		obj.setResultArray(result);
		message.what=HANDLER_RESULT_SUCCESS_JSONOARRAY;
		message.obj=obj;
		this.sendMessage(message);
	}
	public void onFaile(int requestCode,int status,String msg){
		Message message = this.obtainMessage();
		Result obj = new Result();
		obj.setRequestCode(requestCode);
		obj.setMsg(msg);
		obj.setStatus(status);
		message.what=HANDLER_RESULT_FAIL;
		message.obj=obj;
		this.sendMessage(message);
	}
	public Context getContext() {
		return context;
	}
}
