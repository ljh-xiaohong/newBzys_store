package com.bapm.bzys.newBzys_store.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CountDownTimerUtils;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.util.PhoneFormatCheckUtils;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialogListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends BaseActivity implements Function,TextWatcher{
	private FunctionManager manager;
	private EditText ed_phone;
	private EditText ed_store_name;
	private EditText ed_code;
	private EditText ed_pwd;
	private EditText ed_referee;
	private Button btn_login;
	private Button btn_check;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_store);
		ed_phone      = (EditText) findViewById(R.id.ed_phone);
		ed_store_name = (EditText) findViewById(R.id.ed_store_name);
		ed_code       = (EditText) findViewById(R.id.ed_code);
		ed_pwd        = (EditText) findViewById(R.id.ed_pwd);
		ed_referee    = (EditText) findViewById(R.id.ed_referee);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_check = (Button) findViewById(R.id.btn_check);
		manager = this.init(this.getContext());
		manager.registFunClass(RegistActivity.class);
		
		ed_store_name.addTextChangedListener(this);
		ed_phone.addTextChangedListener(this);
		ed_code.addTextChangedListener(this);
		ed_pwd.addTextChangedListener(this);
	}
	public void getCode(View view){
		String phone = ed_phone.getText().toString();
		if(!CommonUtil.checkPhoneNum(phone)){
			CustomToast.showToast(this,"请正确输入手机号!");
		}else {
			loadDialog.show();
			Map<String, String> params = new HashMap<String, String>();
			params.put("phone", phone);
			manager.getCode(params, this);
		}
	}
	public void regist(View v){
		if(CommonUtil.isNull(ed_store_name.getText().toString())){
			CustomToast.showToast(this,"名称不能为空");
		}else if(!CommonUtil.checkPhoneNum(ed_phone.getText().toString())){
			CustomToast.showToast(this,"请正确输入手机号！");
		}else if(CommonUtil.isNull(ed_code.getText().toString())){
			CustomToast.showToast(this,"验证码不能为空");
		}else if(CommonUtil.isNull(ed_pwd.getText().toString())){
			CustomToast.showToast(this,"密码不能为空");
		}else {
			loadDialog.show();
			try {
				JSONObject params = new JSONObject();
				params.put("ValidateCode", ed_code.getText().toString());
				params.put("Name", ed_store_name.getText().toString());
				params.put("GalleryName", ed_store_name.getText().toString());
				params.put("Phone", ed_phone.getText().toString());
				params.put("Password", ed_pwd.getText().toString());
				params.put("Referrer", ed_referee.getText().toString());
				manager.regist(params, this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		loadDialog.dismiss();
		switch (requstCode) {
			case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
				if (result.optString("LogionCode").equals("1")) {
					DadanPreference.getInstance(this).setTicket(result.optString("Ticket"));
				}else if(result.optString("LogionCode").equals("-1")){
					Intent intent=new Intent(this,LoginActivity.class);
					intent.putExtra("LogionCode","-1");
					startActivity(intent);
					ActivityManager.getInstance().finishAllActivity();
				}
				break;
		case DadanUrl.GET_CODE_REQUEST_CODE:{
			try{
				int loginCode = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(loginCode==Constants.NETWORK_SUCCESS){
					CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btn_check, 60000, 1000);
					mCountDownTimerUtils.start();
					 CustomToast.showToast(this, message);
				}else{
					 CustomToast.showToast(this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		}
		case DadanUrl.STORE_REGIST_URL_CODE:{
			try{
				int loginCode = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(loginCode==Constants.NETWORK_SUCCESS){
					new MyDialog(this.getContext()).
					callback(MyDialog.TYPE_INFO,message,
							new MyDialogListener() {
								@Override
								public void callback(String[] array) {
									JSONObject params = new JSONObject();
									try {
										params.put("Phone", ed_phone.getText().toString());
										params.put("Password", ed_pwd.getText().toString());
										manager.login(params, RegistActivity.this);
									} catch (JSONException e) {

									}
								}
							}, "确定", 
							null,null);
				}else{
					 CustomToast.showToast(this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
			case DadanUrl.USER_LOGIN_REQUEST_CODE:{
				handleLogin(result);
			}
		default:
			break;
		}
	}
	public void handleLogin(JSONObject result){
		try {
			int loginCode = result.optInt(Constants.LOGIONCODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(loginCode==Constants.NETWORK_SUCCESS){
				String ticket = result.optString(Constants.TICKET_KEY);
				DadanPreference.getInstance(this).setTicket(ticket);
				Intent intent=new Intent(RegistActivity.this,ShopInfoActivity.class);
				intent.putExtra("wherefrom","RegistActivity");
				startActivity(intent);
				RegistActivity.this.finish();
			}else{
				 CustomToast.showToast(this, message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void back(View v){
		Intent intent=new Intent(RegistActivity.this,LoginActivity.class);
		startActivity(intent);
		ActivityManager.getInstance().finishAllActivity();
	}
	@Override
	public void onFaile(int requestCode, int status, String msg) {
		loadDialog.dismiss();
		Log.i(RegistActivity.class.toString(),msg);
//		 CustomToast.showToast(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		manager.unregistFunctionClass(RegistActivity.class);
	}
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		loadDialog.dismiss();
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	@Override
	public void afterTextChanged(Editable s) {
//		String phone = ed_phone.getText().toString();
//		String storeName = ed_store_name.getText().toString();
//		String code = ed_code.getText().toString();
//		String pwd = ed_pwd.getText().toString();
//		if(phone.equals("")){
//			btn_login.setClickable(false);
//			btn_login.setBackgroundResource(R.drawable.btn_gray);
//		}else if(storeName.equals("")){
//			btn_login.setClickable(false);
//			btn_login.setBackgroundResource(R.drawable.btn_gray);
//		}else if(code.equals("")){
//			btn_login.setClickable(false);
//			btn_login.setBackgroundResource(R.drawable.btn_gray);
//		}else if(pwd.equals("")){
//			btn_login.setClickable(false);
//			btn_login.setBackgroundResource(R.drawable.btn_gray);
//		}else{
//			btn_login.setClickable(true);
//			btn_login.setBackgroundResource(R.drawable.btn_normal);
//		}
	}
}
