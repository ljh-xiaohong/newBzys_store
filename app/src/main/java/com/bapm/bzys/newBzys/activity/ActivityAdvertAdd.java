package com.bapm.bzys.newBzys.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.model.Advert;
import com.bapm.bzys.newBzys.model.AdvertStatus;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.Constants;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.dialog.ActionSheet;
import com.bapm.bzys.newBzys.widget.dialog.ActionSheet.MenuItemClickListener;
import com.bapm.bzys.newBzys.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialogListener;

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

public class ActivityAdvertAdd extends BaseActivity implements Function,OnClickListener{
	private FunctionManager manager;
	private EditText ed_name;
	private EditText ed_no;
	private TextView tv_title;
	private TextView  ed_status;
	private TextView tv_status_id;
	private Map<String, AdvertStatus> map;
	private List<String> names;
	private Button btn_sure;
	private Advert advert;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advert_add);
		setTheme(R.style.ActionSheetStyleIOS7);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityAdvertAdd.class);
		initView();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		ed_name      = (EditText) findViewById(R.id.ed_name);
		ed_no 	     = (EditText) findViewById(R.id.ed_no);
		tv_title     = (TextView) findViewById(R.id.tv_title);
		ed_status    = (TextView) findViewById(R.id.ed_status);
		tv_status_id = (TextView) findViewById(R.id.tv_status_id);
		
		btn_sure   = (Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(this);
		findViewById(R.id.layout_status).setOnClickListener(this);
		ed_name.setFocusable(true);
		ed_name.setFocusableInTouchMode(true);
		ed_name.requestFocus();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	public void initData(){
		map = new HashMap<String, AdvertStatus>();
		names = new ArrayList<String>();
		
		if(getIntent().hasExtra("advert")){
			advert = (Advert) getIntent().getSerializableExtra("advert");
			ed_name.setText(advert.getName());
			ed_no.setText(advert.getNo());
			ed_status.setText(advert.getStatus());
			tv_status_id.setText(advert.getStatusId());
			tv_title.setText("推广编辑");
		}else{
			tv_title.setText("新增推广");
		}
		Map<String, String> params = new HashMap<String, String>();
		manager.advertStatusList(params, this);
	}
	public void back(View v){
		this.finish();
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
		case DadanUrl.ADVER_ADD_OR_UPDATE_URL_CODE:{
			handleGoodsTypeAdd(result);
			break;

		}
		default:
			break;
		}
	}

	@Override
	public void onFaile(int requestCode, int status, String msg) {
		loadDialog.dismiss();
		Log.i(LoginActivity.class.toString(),msg);
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode==HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		loadDialog.dismiss();
		switch (requstCode) {
		case DadanUrl.ADVER_DIC_URL_CODE:{
			try {
				names.clear();
				map.clear();
				for(int i=0;i<result.length();i++){
					JSONObject jsonObj = result.getJSONObject(i);
					String id = jsonObj.optString("ID");
					String name = jsonObj.optString("DictionaryName");
					String code = jsonObj.optString("DictionaryCode");
					String level = jsonObj.optString("DictionaryLevel");
					String desc = jsonObj.optString("DictionaryDescription");
					String status = jsonObj.optString("DictionaryStatus");
					AdvertStatus advertStatus = new AdvertStatus();
					advertStatus.setId(id);
					advertStatus.setName(name);
					advertStatus.setLevel(level);
					advertStatus.setCode(code);
					advertStatus.setStatus(status);
					advertStatus.setDesc(desc);

					if(advert!=null&&advert.getStatus().equals(name)){
						ed_status.setText(name);
						tv_status_id.setText(id);
					}else if(advert==null&&names.size()==0){
						ed_status.setText(name);
						tv_status_id.setText(id);
					}
					names.add(name);
					map.put(name, advertStatus);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				
			}
			break;
		}
		default:
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_status:{
			String[] stateNames = new String[names.size()];
			for(int i=0;i<names.size();i++){
				stateNames[i] = names.get(i);
			}
			ActionSheet menuView = new ActionSheet(this);
			menuView.setSelectItem(ed_status.getText().toString());
			menuView.addItems(stateNames);
			menuView.setItemClickListener(new MenuItemClickListener() {
				@Override
				public void onItemClick(int itemPosition) {
					ed_status.setText(names.get(itemPosition));
					tv_status_id.setText(map.get(names.get(itemPosition)).getId());
				}
			});
			menuView.setCancelableOnTouchMenuOutside(true);
			menuView.showMenu();
			break;
		}
		case R.id.btn_sure:{
			try {
				loadDialog.show();
				JSONObject params = new JSONObject();
				if(advert!=null){
					params.put("ID", advert.getId());
				}
				params.put("PromotionName", ed_name.getText().toString());
				params.put("PromotionNo", ed_no.getText().toString());
				params.put("PromotionStatus", tv_status_id.getText().toString());
				manager.addAdvert(params, this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
			if(code==Constants.ADD_FAIL){
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			}
			else if(code==Constants.NETWORK_SUCCESS){
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				this.setResult(200);
				this.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
