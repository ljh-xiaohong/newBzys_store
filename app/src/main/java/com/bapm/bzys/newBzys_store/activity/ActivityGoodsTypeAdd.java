package com.bapm.bzys.newBzys_store.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.GoodsType;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ActivityGoodsTypeAdd extends BaseActivity implements Function,OnClickListener{
	private FunctionManager manager;
	private TextView tv_title;
	private EditText ed_name;
	private Button btn_sure;
	private ImageButton btn_clear;
	private GoodsType goodsType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_type_add);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityGoodsTypeAdd.class);
		initView();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		ed_name    = (EditText) findViewById(R.id.ed_name);
		btn_clear  = (ImageButton) findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(this);
		btn_sure   = (Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(this);
		ed_name.setFocusable(true);
		ed_name.setFocusableInTouchMode(true);
		ed_name.requestFocus();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	public void initData(){
		if(getIntent().hasExtra("goodsType")){
			goodsType = (GoodsType) getIntent().getSerializableExtra("goodsType");
			ed_name.setText(goodsType.getName());
			tv_title.setText("商品大类编辑");
		}else{
			tv_title.setText("新增商品大类");
		}
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		loadDialog.dismiss();
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
		case DadanUrl.GOODS_TYPE_ADD_OR_UPDATE_URL_CODE:{
			btn_sure.setEnabled(true);
			handleGoodsTypeAdd(result);
		}
		default:
			break;
		}
	}

	@Override
	public void onFaile(int requestCode, int status, String msg) {
		loadDialog.dismiss();
		Log.i(LoginActivity.class.toString(),msg);
//		 CustomToast.showToast(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		loadDialog.dismiss();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:{
			btn_sure.setEnabled(false);
			try {
				JSONObject params = new JSONObject();
				if(goodsType!=null){
					params.put("ID", goodsType.getId());
				}
				if (CommonUtil.isNull(ed_name.getText().toString())){
					btn_sure.setEnabled(true);
					CustomToast.showToast(ActivityGoodsTypeAdd.this,"名称不能为空");
				}else {
					loadDialog.show();
					params.put("GoodsTypeNmae", ed_name.getText().toString());
					manager.addGoodsType(params, this);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.btn_clear:{
			ed_name.setText("");
			break;
		}
		default:
			break;
		}
	}
	public void handleGoodsTypeAdd(JSONObject result){
		try {
			int code = result.optInt(Constants.CODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(code==Constants.NETWORK_SUCCESS){
				 CustomToast.showToast(this, message);
				this.finish();
			}else{
				 CustomToast.showToast(this, message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
