package com.bapm.bzys.newBzys_store.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.AccountChild;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAccountChildAdd extends BaseActivity implements Function,OnClickListener{
	private FunctionManager manager;
	private EditText ed_name;
	private EditText ed_account;
	private EditText ed_pwd;
	private TextView tv_title;
	private Button btn_sure;
	private AccountChild child;
	private String prephone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_child_add);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityAccountChildAdd.class);
		initView();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		ed_name    = (EditText) findViewById(R.id.ed_name);
		ed_account = (EditText) findViewById(R.id.ed_account);
		ed_pwd     = (EditText) findViewById(R.id.ed_pwd);
		tv_title   = (TextView) findViewById(R.id.tv_title);
		btn_sure   = (Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(this);
		ed_name.setFocusable(true);
		ed_name.setFocusableInTouchMode(true);
		ed_name.requestFocus();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	public void initData(){
		if(getIntent().hasExtra("child")){
			loadDialog.show();
			child = (AccountChild) getIntent().getSerializableExtra("child");
			Map<String,String> params = new HashMap<String, String>();
			params.put("id", child.getId());
			manager.detailAccountChild(params, this);
			tv_title.setText("子帐号编辑");
		}else{
			tv_title.setText("新增子帐号");
		}
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		switch (requstCode) {
			case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
				if (result.optString("LogionCode").equals("1")) {
					DadanPreference.getInstance(this).setTicket(result.optString("Ticket"));
					initData();
				}else if(result.optString("LogionCode").equals("-1")){
					Intent intent=new Intent(this,LoginActivity.class);
					intent.putExtra("LogionCode","-1");
					startActivity(intent);
					ActivityManager.getInstance().finishAllActivity();
				}
				break;
		case DadanUrl.USER_CHILD_ADD_URL_CODE:{
			btn_sure.setEnabled(true);
			handleChildAdd(result);
			return;
		}
		case DadanUrl.USER_CHILD_DETAIL_URL_CODE:{
			String subname = result.optString("Name");
			String suphone = result.optString("SubUser1");
			String suPwd = result.optString("SubPassword");
			ed_name.setText(subname);
			ed_account.setText(suphone);
			ed_pwd.setText(suPwd);
			return;
		}
		default:
			break;
		}
		loadDialog.dismiss();
	}

	@Override
	public void onFaile(int requestCode, int status, String msg) {
		Log.i(LoginActivity.class.toString(),msg);
//		 CustomToast.showToast(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode==HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
		loadDialog.dismiss();
	}
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:{
			btn_sure.setEnabled(false);
			try {
				JSONObject params = new JSONObject();
				if(child!=null){
					params.put("ID", child.getId());
				}
				if (CommonUtil.isNull(ed_account.getText().toString())){
					btn_sure.setEnabled(true);
					CustomToast.showToast(ActivityAccountChildAdd.this,"账号不能为空");
				}else if (CommonUtil.isNull(ed_pwd.getText().toString())){
					btn_sure.setEnabled(true);
					CustomToast.showToast(ActivityAccountChildAdd.this,"密码不能为空");
				}else{
					loadDialog.show();
					params.put("Name", ed_name.getText().toString());
					params.put("SubUser1",ed_account.getText().toString());
					params.put("SubPassword", ed_pwd.getText().toString());
					manager.addAccountChild(params, this);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		default:
			break;
		}
	}
	public void handleChildAdd(JSONObject result){
		try {
			int code = result.optInt(Constants.CODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(code==Constants.ADD_FAIL){
				 CustomToast.showToast(this, message);
			}
			else if(code==Constants.NETWORK_SUCCESS){
				 CustomToast.showToast(this, message);
				this.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void back(View v) {
		this.finish();
	}
}
