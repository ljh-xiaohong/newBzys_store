package com.bapm.bzys.newBzys_store.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.Advert;
import com.bapm.bzys.newBzys_store.model.AdvertStatus;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.dialog.ActionSheet;
import com.bapm.bzys.newBzys_store.widget.dialog.ActionSheet.MenuItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
	private String eDstatus;
	private List<String> statusIds;
	private String tvStatusId;
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
		statusIds = new ArrayList<String>();
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
			btn_sure.setEnabled(true);
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
//		 CustomToast.showToast(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode==HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
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
				statusIds.clear();
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
						eDstatus=name;
						tvStatusId=id;
					}else if(advert==null&&names.size()==0){
						ed_status.setText(name);
						tv_status_id.setText(id);
					}
					names.add(name);
					statusIds.add(id);
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
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActivityAdvertAdd.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			showToolsView(names);
			break;
		}
		case R.id.btn_sure:{
			btn_sure.setEnabled(false);
			try {
				JSONObject params = new JSONObject();
				if(advert!=null){
					params.put("ID", advert.getId());
				}
				if (CommonUtil.isNull(ed_no.getText().toString())){
					btn_sure.setEnabled(true);
					CustomToast.showToast(ActivityAdvertAdd.this,"编号不能为空");
				}else if (CommonUtil.isNull(ed_name.getText().toString())) {
					btn_sure.setEnabled(true);
					CustomToast.showToast(ActivityAdvertAdd.this,"名称不能为空");
				} else{
					loadDialog.show();
					params.put("PromotionName", ed_name.getText().toString());
					params.put("PromotionNo", ed_no.getText().toString());
					params.put("PromotionStatus", tv_status_id.getText().toString());
					manager.addAdvert(params, this);
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

	private View views[];
	private View popupView;
	private PopupWindow window;
	/**
	 * 动态生成显示items中的textview
	 */
	private void showToolsView(List<String> types) {
		LayoutInflater inflater = LayoutInflater.from(this);
		popupView = getLayoutInflater().inflate(R.layout.select_layout_popwindow, null);
		LinearLayout tools= (LinearLayout) popupView.findViewById(R.id.tools);
		int size = tools.getChildCount();
		for (int i = 0; i < size; i++) {
			tools.removeViewAt(0);
		}
		views = new View[types.size()];
		for (int i = 0; i < types.size(); i++) {
			View view = inflater.inflate(R.layout.select_item, null);
			view.setTag(i);
			view.setOnClickListener(toolsItemListener);
			ImageView select_img = (ImageView) view.findViewById(R.id.select_img);
			TextView select_tv = (TextView) view.findViewById(R.id.select_tv);
			select_tv.setText(types.get(i));
			tools.addView(view);
			views[i] = view;
		}
			if(eDstatus==null){
				changeTextColor(0);
			}if(types.indexOf(eDstatus)==-1&&types.size()>0){
				changeTextColor(0);
			}else{
				int index = types.indexOf(eDstatus);
				if(index<types.size()&&index>-1)
					changeTextColor(index);
			}


		window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/2);
		//设置动画
		window.setAnimationStyle(R.style.popup_window_anim);
		// 设置背景颜色
		window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));
		//设置可以获取焦点
		window.setFocusable(true);
		//设置可以触摸弹出框以外的区域
		window.setOutsideTouchable(true);
		// 更新popupwindow的状态
		window.update();
		backgroundAlpha(0.6f);
		//添加pop窗口关闭事件
		window.setOnDismissListener(new ActivityAdvertAdd.poponDismissListener());
		// 以下拉的方式显示，并且可以设置显示的位置
		window.showAtLocation(findViewById(R.id.choose_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

	}
	/**
	 * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
	 * @author cg
	 *
	 */
	class poponDismissListener implements PopupWindow.OnDismissListener{
		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			backgroundAlpha(1f);
		}
	}
	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		getWindow().setAttributes(lp);
	}
	/**
	 * 改变textView的颜色
	 */
	private void changeTextColor(int position) {
		if (views.length < 0)
			return;
		for (int i = 0; i < views.length; i++) {
			if (i == position) {
				((TextView) views[i].findViewById(R.id.select_tv)).setTextColor(getResources().getColor(R.color.edt_hint_orange));
				((ImageView) views[i].findViewById(R.id.select_img)).setVisibility(View.VISIBLE);
			} else {
				((TextView) views[i].findViewById(R.id.select_tv)).setTextColor(getResources().getColor(R.color.black3));
				((ImageView) views[i].findViewById(R.id.select_img)).setVisibility(View.GONE);
			}
		}
	}
	private View.OnClickListener toolsItemListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			changeTextColor(tag);
			ed_status.setText(names.get(tag));
			tv_status_id.setText(statusIds.get(tag));
			tvStatusId=statusIds.get(tag);
			eDstatus=names.get(tag);
			window.dismiss();
		}
	};

	public void handleGoodsTypeAdd(JSONObject result){
		try {
			int code = result.optInt(Constants.CODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(code==Constants.ADD_FAIL){
				 CustomToast.showToast(this, message);
			}
			else if(code==Constants.NETWORK_SUCCESS){
				 CustomToast.showToast(this, message);
				this.setResult(200);
				this.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
