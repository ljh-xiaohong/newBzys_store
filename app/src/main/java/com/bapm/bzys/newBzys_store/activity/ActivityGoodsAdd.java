package com.bapm.bzys.newBzys_store.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.AdvertList;
import com.bapm.bzys.newBzys_store.model.Goods;
import com.bapm.bzys.newBzys_store.model.GoodsStatus;
import com.bapm.bzys.newBzys_store.model.GoodsType;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.AsyncImageLoader;
import com.bapm.bzys.newBzys_store.util.CommonUtil;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.util.GlideUtils;
import com.bapm.bzys.newBzys_store.widget.ImageUtils;
import com.bapm.bzys.newBzys_store.widget.dialog.ActionSheet;
import com.bumptech.glide.Glide;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.zhy.autolayout.AutoRelativeLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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


public class ActivityGoodsAdd extends BaseActivity implements Function,OnClickListener{
	private FunctionManager manager;
	private ImageView iv_opera_img;
	private ImageView iv_img;
	private String imageKey;
	private TextView ed_type;
	private TextView tv_type_id;
	private EditText ed_name;
	private EditText ed_no;
	private EditText ed_price;
	private EditText ed_unit;
	private EditText ed_desc;
	private TextView tv_desc_count;
	private TextView tv_title;
	private TextView  ed_status;
	private TextView tv_status_id;
	private AutoRelativeLayout layout_desc;
	private AutoRelativeLayout layout_name;
	private AutoRelativeLayout layout_no;
	private AutoRelativeLayout layout_price;
	private AutoRelativeLayout layout_unit;

	private Button btn_sure;
	private Button btn_continue;
	
	private Map<String, GoodsType> typeMap;
	private List<String> typeNames;
	private List<String> typeIds;

	private Map<String, GoodsStatus> statusMap;
	private List<String> statusNames;
	private List<String> statusIds;
	private Goods goods;
	private boolean addNext = false;
	private InputMethodManager imm ;
	private String typeName;
	private String eDstatus;
	private int who=0;
	private String tvTypeId;
	private String tvStatusId;

	private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
	private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.ActionSheetStyleIOS7);
		setContentView(R.layout.activity_goods_add);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityGoodsAdd.class);
		loadDialog.show();
		initView();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_opera_img   = (ImageView) findViewById(R.id.iv_opera_img);
		iv_img   = (ImageView) findViewById(R.id.iv_img);
		ed_name        = (EditText) findViewById(R.id.ed_name);
		ed_no          = (EditText) findViewById(R.id.ed_no);
		ed_price       = (EditText) findViewById(R.id.ed_price);
		ed_unit        = (EditText) findViewById(R.id.ed_unit);
		ed_desc = (EditText) findViewById(R.id.ed_desc);
		tv_desc_count = (TextView) findViewById(R.id.tv_desc_count);
		ed_type    = (TextView) findViewById(R.id.ed_goods_type);
		tv_type_id = (TextView) findViewById(R.id.tv_goods_type_id);
		ed_status  = (TextView) findViewById(R.id.ed_status);
		tv_status_id = (TextView)findViewById(R.id.tv_status_id);
		btn_sure       = (Button) findViewById(R.id.btn_sure);
		btn_continue = (Button) findViewById(R.id.btn_continue);
		layout_desc = (AutoRelativeLayout) findViewById(R.id.layout_desc);
		layout_name = (AutoRelativeLayout) findViewById(R.id.layout_name);
		layout_no = (AutoRelativeLayout) findViewById(R.id.layout_no);
		layout_price = (AutoRelativeLayout) findViewById(R.id.layout_price);
		layout_unit = (AutoRelativeLayout) findViewById(R.id.layout_unit);
		layout_desc.setOnClickListener(this);
		layout_name.setOnClickListener(this);
		layout_no.setOnClickListener(this);
		layout_price.setOnClickListener(this);
		layout_unit.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		btn_continue.setOnClickListener(this);
		iv_img.setOnClickListener(this);
		iv_opera_img.setOnClickListener(this);
		ed_desc.addTextChangedListener(new TextWatcher() {
			  private CharSequence temp;
			  private int selectionStart;
			  private int selectionEnd;
			  private int num = 50;
			  @Override
			  public void onTextChanged(CharSequence s, int start, int before,int count) {
			    temp = s;
			    System.out.println("s="+s);
			  }
			  @Override
			  public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			  @Override
			  public void afterTextChanged(Editable s) {
			    tv_desc_count.setText(s.length()+"/"+num);
			    selectionStart = ed_desc.getSelectionStart();
			    selectionEnd = ed_desc.getSelectionEnd();
			    if (temp.length() > num) {
			      s.delete(selectionStart - 1, selectionEnd);
			      int tempSelection = selectionStart;
			      ed_desc.setText(s);
			      ed_desc.setSelection(tempSelection);//设置光标在最后
			    }
			  }
			});
		ed_name.setFocusable(true);
		ed_name.setFocusableInTouchMode(true);
		ed_name.requestFocus();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(ed_name, InputMethodManager.RESULT_SHOWN);
		findViewById(R.id.layout_goods_type).setOnClickListener(this);
		findViewById(R.id.layout_status).setOnClickListener(this);
	}
	public void initData(){
		statusMap = new HashMap<String, GoodsStatus>();
		statusNames = new ArrayList<String>();
		statusIds = new ArrayList<String>();

		typeMap = new HashMap<String, GoodsType>();
		typeNames = new ArrayList<String>();
		typeIds = new ArrayList<String>();

		if(getIntent().hasExtra("goods")){
			Goods goods = (Goods) getIntent().getSerializableExtra("goods");
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", goods.getId());
			manager.goodsDetail(params, this);
			tv_title.setText("商品编辑");
			btn_continue.setVisibility(View.GONE);

		}else{
			tv_title.setText("商品新增");
			ed_status.setText("上架");
			tv_status_id.setText("1");
			if(getIntent().hasExtra("typeName")){
				String typeName = getIntent().getStringExtra("typeName");
				if(typeName==null||typeName.equals("")){
					ed_type.setText("请选择");
				}else{
					ed_type.setText(getIntent().getStringExtra("typeName"));
				}
			}else{
				ed_type.setText("请选择");
			}
		}
		if(getIntent().hasExtra("type_id")){
			tv_type_id.setText(getIntent().getStringExtra("type_id"));
		}
		if(getIntent().hasExtra("type_name")){
			ed_type.setText(getIntent().getStringExtra("type_name"));
		}
//		if(getIntent().hasExtra("status_id")){
//			tv_status_id.setText(getIntent().getStringExtra("status_id"));
//		}
//		if(getIntent().hasExtra("status_name")){
//			ed_status.setText(getIntent().getStringExtra("status_name"));
//		}
		if(getIntent().hasExtra("unit")){
			ed_unit.setText(getIntent().getStringExtra("unit"));
		}
		Map<String, String> params = new HashMap<String, String>();
		manager.goodsStatusList(params, this);
		manager.goodsTypeList(params, this);
		manager.token(params, this);
	}
	public void back(View v){
		imm.hideSoftInputFromWindow(ed_desc.getWindowToken(), 0);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		isCancelled = true;
		loadDialog.dismiss();
		super.onBackPressed();
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
		case DadanUrl.GOODS_ADD_OR_UPDATE_URL_CODE:{
			handleGoodsAdd(result);
			btn_sure.setEnabled(true);
			btn_continue.setEnabled(true);
			break;
		}
		case DadanUrl.GOODS_DETAIL_URL_CODE:{
			String id = result.optString("ID");
			String name = result.optString("GoodsName");
			String no = result.optString("GoodsNo");
			String flag = result.optString("GoodsSalesFlag");
			Double price = result.optDouble("GoodsSalesPrice");
			String statusName = result.optString("GoodsSaleName");
			String unit = result.optString("GoodsCountedBy");
			String url = result.optString("PicUrl");
			String goodsTypeID = result.optString("GoodsTypeID");
			String goodsTypeNmae = result.optString("GoodsTypeNmae");
			String desc = result.optString("GoodsDescription");
			if(goods==null)
			goods = new Goods();
			goods.setId(id);
			goods.setName(name);
			goods.setNo(no);
			goods.setFlag(flag);
			goods.setPrice(price);
			goods.setUrl(url);
			goods.setUnit(unit);
			goods.setStatusName(statusName);
			goods.setGoodsTypeName(goodsTypeNmae);
			goods.setGoodsTypeID(goodsTypeID);
			goods.setDesc(desc);
			goods.setGoodsTypeID(goodsTypeID);
			ed_name.setText(goods.getName());
			ed_no.setText(goods.getNo());
			NumberFormat nf=NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			ed_price.setText(nf.format(goods.getPrice())+"");
			ed_unit.setText(goods.getUnit());
			ed_type.setText(goods.getGoodsTypeName());
			tv_type_id.setText(goods.getGoodsTypeID());
			ed_status.setText(goods.getStatusName());
			tv_status_id.setText(goods.getFlag());
			if(goods.getDesc()!=null)
				ed_desc.setText(goods.getDesc());
			//加载图片
			if (goods.getUrl()==null||goods.getUrl().equals("")) {
				iv_opera_img.setVisibility(View.VISIBLE);
				iv_img.setVisibility(View.GONE);
				GlideUtils.displayNative(iv_img, R.mipmap.qrcode_default);
			} else {
				iv_opera_img.setVisibility(View.GONE);
				iv_img.setVisibility(View.VISIBLE);
				GlideUtils.display(iv_img,goods.getUrl());
			}
			break;
		}
		case DadanUrl.QI_NIU_TOKEN_URL_CODE:{
			try {
				int code = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(code==Constants.NETWORK_SUCCESS){
					String token = result.getString(Constants.QN_TOKEN_KEY);
					DadanPreference.getInstance(this).setQNToken(token);
				}else{
					 CustomToast.showToast(this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
		switch (requstCode) {
		case DadanUrl.GOODS_DIC_STATUS_URL_CODE:{
			try {
				statusNames.clear();
				statusIds.clear();
				statusMap.clear();
				for(int i=0;i<result.length();i++){
					JSONObject jsonObj = result.getJSONObject(i);
					String id = jsonObj.optString("ID");
					String name = jsonObj.optString("DictionaryName");
					GoodsStatus child = new GoodsStatus();
					child.setId(id);
					child.setName(name);

					if(goods==null&&statusNames.size()==0&&!getIntent().hasExtra("status_id")){
						ed_status.setText(name);
						tv_status_id.setText(id);
						eDstatus=name;
						tvStatusId=id;
					}
					statusNames.add(name);
					statusIds.add(id);
					statusMap.put(name, child);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				
			}
			break;
		}
		case DadanUrl.GOODS_TYPE_LIST_URL_CODE:{
			try {
				typeNames.clear();
				typeIds.clear();
				typeMap.clear();
				for(int i=0;i<result.length();i++){
					JSONObject jsonObj = result.getJSONObject(i);
					String id = jsonObj.optString("ID");
					String name = jsonObj.optString("GoodsTypeNmae");
					GoodsType type = new GoodsType();
					type.setId(id);
					type.setName(name);
					typeNames.add(name);
					typeIds.add(id);
					typeMap.put(name, type);
					if(ed_type.getText().toString().equals(name)){
						ed_type.setText(name);
						tv_type_id.setText(id);
						typeName=name;
						tvTypeId=id;
					}
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
		loadDialog.dismiss();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_goods_type:{
			imm.hideSoftInputFromWindow(ed_name.getWindowToken(), 0);
			who=1;
			if (typeNames.size()>0) {
				showToolsView(typeNames);
			}else{
				CustomToast.showToast(this, "请选择先到商品大类去新增");
			}
			break;
		}
		case R.id.layout_status:{
			imm.hideSoftInputFromWindow(ed_name.getWindowToken(), 0);
			who=2;
			showToolsView(statusNames);
			break;
		}
		case R.id.btn_sure:{
			btn_sure.setEnabled(false);
			imm.hideSoftInputFromWindow(ed_name.getWindowToken(), 0);
			addNext = false;
			if(tv_status_id.equals("")){
				 CustomToast.showToast(this, "请选择销售状态");
				return;
			}
			if((imageKey==null||imageKey.equals(""))&&goods==null){
				CustomToast.showToast(this, "请先上传商品图片");
				btn_sure.setEnabled(true);
				return;
			}
			try {
				JSONObject params = new JSONObject();
				if(goods!=null){
					params.put("ID", goods.getId());
				}
				if (CommonUtil.isNull(tv_type_id.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"请选择商品大类");
				}else if (CommonUtil.isNull(ed_name.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"名称不能为空");
				}else if(CommonUtil.isNull(ed_no.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"编号不能为空");
				}else if(CommonUtil.isNull(ed_price.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"售价不能为空");
				}else {
					loadDialog.show();
					params.put("GoodsTypeID", tv_type_id.getText().toString());
					params.put("GoodsSalesFlag", tv_status_id.getText().toString());
					params.put("GoodsName", ed_name.getText().toString());
					params.put("GoodsNo", ed_no.getText().toString());
					params.put("GoodsCountedBy", ed_unit.getText().toString());
					params.put("GoodsSalesPrice", ed_price.getText().toString());
					params.put("GoodsDescription", ed_desc.getText().toString());
					params.put("QiniuKey", imageKey);
					params.put("AHigh", iv_img.getHeight());
					params.put("AWidth", iv_img.getWidth());
					manager.addGoods(params, this);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.btn_continue:{
			btn_continue.setEnabled(false);
			addNext = true;
			if(tv_status_id.equals("")){
				 CustomToast.showToast(this, "请选择销售状态");
				return;
			}
			if((imageKey==null||imageKey.equals(""))&&goods==null){
				 CustomToast.showToast(this, "请先上传商品图片");
				btn_continue.setEnabled(true);
				return;
			}
			try {
				JSONObject params = new JSONObject();
				if(goods!=null){
					params.put("ID", goods.getId());
				}
				if (CommonUtil.isNull(tv_type_id.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"请选择商品大类");
				}else if (CommonUtil.isNull(ed_name.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"名称不能为空");
				}else if(CommonUtil.isNull(ed_no.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"编号不能为空");
				}else if(CommonUtil.isNull(ed_price.getText().toString())){
					btn_continue.setEnabled(true);
					CustomToast.showToast(ActivityGoodsAdd.this,"售价不能为空");
				}else {
					loadDialog.show();
					params.put("GoodsTypeID", tv_type_id.getText().toString());
					params.put("GoodsSalesFlag", tv_status_id.getText().toString());
					params.put("GoodsName", ed_name.getText().toString());
					params.put("GoodsNo", ed_no.getText().toString());
					params.put("GoodsCountedBy", ed_unit.getText().toString());
					params.put("GoodsSalesPrice", ed_price.getText().toString());
					params.put("GoodsDescription", ed_desc.getText().toString());
					params.put("QiniuKey", imageKey);
					params.put("AHigh", iv_img.getHeight());
					params.put("AWidth", iv_img.getWidth());
					manager.addGoods(params, this);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.iv_img:{
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			imm.hideSoftInputFromWindow(ed_name.getWindowToken(), 0);
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED)
			{
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_CALL_PHONE2);
			}else {
				showPicturePopupWindow();
			}

			break;
		}
		case R.id.iv_opera_img:{
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			imm.hideSoftInputFromWindow(ed_name.getWindowToken(), 0);
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED)
			{
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_CALL_PHONE2);
			}else {
				showPicturePopupWindow();
			}
			break;
		}
			case R.id.layout_desc:
				ed_desc.setFocusable(true);
				ed_desc.setFocusableInTouchMode(true);
				ed_desc.requestFocus();
				imm.showSoftInput(ed_desc, InputMethodManager.RESULT_SHOWN);
				break;
			case R.id.layout_name:
				ed_name.setFocusable(true);
				ed_name.setFocusableInTouchMode(true);
				ed_name.requestFocus();
				imm.showSoftInput(ed_name, InputMethodManager.RESULT_SHOWN);
				break;
			case R.id.layout_no:
				ed_no.setFocusable(true);
				ed_no.setFocusableInTouchMode(true);
				ed_no.requestFocus();
				ed_no.setCursorVisible(true);
				imm.showSoftInput(ed_no, InputMethodManager.RESULT_SHOWN);
				break;
			case R.id.layout_price:
				ed_price.setFocusable(true);
				ed_price.setFocusableInTouchMode(true);
				ed_price.requestFocus();
				imm.showSoftInput(ed_price, InputMethodManager.RESULT_SHOWN);
				break;
			case R.id.layout_unit:
				ed_unit.setFocusable(true);
				ed_unit.setFocusableInTouchMode(true);
				ed_unit.requestFocus();
				imm.showSoftInput(ed_unit, InputMethodManager.RESULT_SHOWN);
				break;
		default:
			break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{

		if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
		{
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				showPicturePopupWindow();
			} else
			{
				// Permission Denied
				CustomToast.showToast(ActivityGoodsAdd.this,"请允许打开读取存储权限，否则图片无法读取上传");
			}
		}


		if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
		{
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				showPicturePopupWindow();
			} else
			{
				// Permission Denied
				CustomToast.showToast(ActivityGoodsAdd.this,"请允许打开读取存储权限，否则图片无法读取上传");
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
		if (who==1){
			if(typeName==null){
				changeTextColor(0);
			}if(types.indexOf(typeName)==-1&&types.size()>0){
				changeTextColor(0);
			}else{
				int index = types.indexOf(typeName);
				if(index<types.size()&&index>-1)
					changeTextColor(index);
			}
		}else{
			if (eDstatus==null){
				changeTextColor(0);
			}if(types.indexOf(eDstatus)==-1&&types.size()>0){
				changeTextColor(0);
			}else{
				int index = types.indexOf(eDstatus);
				if(index<types.size()&&index>-1)
					changeTextColor(index);
			}
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
		window.setOnDismissListener(new ActivityGoodsAdd.poponDismissListener());
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
			if (who==1){
				ed_type.setText(typeNames.get(tag));
				tv_type_id.setText(typeIds.get(tag));
				tvTypeId=typeIds.get(tag);
				typeName=typeNames.get(tag);
			}else{
				ed_status.setText(statusNames.get(tag));
				tv_status_id.setText(statusIds.get(tag));
				tvStatusId=statusIds.get(tag);
				eDstatus=statusNames.get(tag);
			}
			window.dismiss();
		}
	};

	public void handleGoodsAdd(JSONObject result){
		try {
			int code = result.optInt(Constants.CODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(code==Constants.NETWORK_SUCCESS){
				CustomToast.showToast(this, message);
				Intent intent = new Intent();
				intent.putExtra("addNext",addNext);
				intent.putExtra("type_id", tv_type_id.getText().toString());
				intent.putExtra("type_name", ed_type.getText().toString());
				intent.putExtra("status_id", tv_status_id.getText().toString());
				intent.putExtra("status_name", ed_status.getText().toString());
				intent.putExtra("unit", ed_unit.getText().toString());
				this.setResult(200, intent);
				this.finish();
			}else{
				 CustomToast.showToast(this, message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE_RESULT_CODE && resultCode == RESULT_OK) {
			String imagePath = "";
			Uri uri = null;
			if (data != null && data.getData() != null) {// 有数据返回直接使用返回的图片地址
				uri = data.getData();
				Cursor cursor = getContentResolver().query(uri,proj, null,
						null, null);
				if (cursor == null) {
					uri = ImageUtils.getUri(this, data);
				}
				imagePath = ImageUtils.getFilePathByFileUri(this, uri);
			} else {// 无数据使用指定的图片路径
				imagePath = mImagePath;
			}
			uploadHeader(imagePath);
//			uploadHeaders(imagePath);
		}
	}
//	public byte[] Bitmap2Bytes(Bitmap bm) {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		bm.compress(Bitmap.CompressFormat.PNG, 65, baos);
//		return baos.toByteArray();
//	}
//		File file =new File(Environment.getExternalStorageDirectory(),"Store");
//	public void uploadHeaders(final String filePath){
//		//调用压缩图片的方法，返回压缩后的图片path
////		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
////		String name = file + File.separator + fileName;
//		Bitmap bitmap = ImageUtils.getBitmapByPath(filePath);
//
//		loadDialog.setTitle("图片上传中...");
//		loadDialog.show();
//		Configuration config = new Configuration.Builder()
//				.chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
//				.putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
//				.connectTimeout(10) // 链接超时。默认10秒
//				.responseTimeout(60) // 服务器响应超时。默认60秒
//				.recorder(null)  // recorder分片上传时，已上传片记录器。默认null
////                .recorder(recorder keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
//				.zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
//				.build();
//		//重用uploadManager。一般地，只需要创建一个uploadManager对象
//		UploadManager uploadManager = new UploadManager(config);
//		String token = DadanPreference.getInstance(this).getQNToken();
//		if (file.exists()) {
//			uploadManager.put(Bitmap2Bytes(bitmap), null,token,new UpCompletionHandler() {
//				@Override
//				public void complete(String key, ResponseInfo info, JSONObject res) {
//					loadDialog.dismiss();
////					compressedPic.deleteOnExit();
//					//res包含hash、key等信息，具体字段取决于上传策略的设置
//					if(info.isOK()){
//						Log.i("qiniu", "Upload Success");
//						imageKey = res.optString("key");
////				ImageUtils.cutQualityImage(this, name, bitmap, 65);
//						//获取图片缩略图，避免OOM
////		    			Bitmap bitmap = ImageUtils.getImageThumbnail(filePath, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5);
//						String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//						String name = file + File.separator + fileName;
//						Bitmap bitmap = ImageUtils.getBitmapByPath(filePath);
//						try {
//							ImageUtils.cutQualityImage(ActivityGoodsAdd.this, name, bitmap, 65);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						iv_opera_img.setVisibility(View.GONE);
//						iv_img.setVisibility(View.VISIBLE);
//						Glide.with(ActivityGoodsAdd.this).load(filePath).into(iv_img);
////		    			iv_img.setImageBitmap(bitmap);
//					}else{
//						Log.i("qiniu", "Upload Fail");
//						CustomToast.showToast(ActivityGoodsAdd.this, "图片上传失败");
//						//如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
//					}
//					Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
//				}
//			} , null);
//		}else{//直接上传
//			uploadManager.put(Bitmap2Bytes(bitmap), null,token,new UpCompletionHandler() {
//				@Override
//				public void complete(String key, ResponseInfo info, JSONObject res) {
//					loadDialog.dismiss();
//					//res包含hash、key等信息，具体字段取决于上传策略的设置
//					if(info.isOK()){
//						Log.i("qiniu", "Upload Success");
//						imageKey = res.optString("key");
//						//获取图片缩略图，避免OOM
//						String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//						String name = file + File.separator + fileName;
//						Bitmap bitmap = ImageUtils.getBitmapByPath(filePath);
//						try {
//							ImageUtils.cutQualityImage(ActivityGoodsAdd.this, name, bitmap, 65);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
////		    			Bitmap bitmap = ImageUtils.getImageThumbnail(filePath, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5);
//						iv_opera_img.setVisibility(View.GONE);
//						iv_img.setVisibility(View.VISIBLE);
//						Glide.with(ActivityGoodsAdd.this).load(filePath).into(iv_img);
////		    			iv_img.setImageBitmap(bitmap);
//					}else{
//						Log.i("qiniu", "Upload Fail");
//						CustomToast.showToast(ActivityGoodsAdd.this, "图片上传失败");
//						//如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
//					}
//					Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
//				}
//			} , null);;
//		}
//	}
private volatile boolean isCancelled = false;
	public void uploadHeader(final String filePath){
		//调用压缩图片的方法，返回压缩后的图片path
		String targetPath = this.getCacheDir().getAbsolutePath()+"compressPic.jpg";
	    final String compressImage = ImageUtils.compressImage(filePath, targetPath, 100);
	    final File compressedPic = new File(compressImage);
		loadDialog.setTitle("图片上传中...");
		loadDialog.show();
		String dirPath =  this.getCacheDir().getAbsolutePath();
				Recorder recorder = null;
		try {
			recorder = new FileRecorder(dirPath);
		}catch (Exception e){
		}
//默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
//避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
		KeyGenerator keyGen = new KeyGenerator(){
			public String gen(String key, File file){
				// 不必使用url_safe_base64转换，uploadManager内部会处理
				// 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
				return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
			}
		};
		Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .recorder(recorder,keyGen)  // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
		//重用uploadManager。一般地，只需要创建一个uploadManager对象
		UploadManager uploadManager = new UploadManager(config);
		String token = DadanPreference.getInstance(this).getQNToken();
	    if (compressedPic.exists()) {

		    uploadManager.put(compressedPic, null,token,new UpCompletionHandler() {
		        @Override
		        public void complete(String key, ResponseInfo info, JSONObject res) {
		        	loadDialog.dismiss();
		        	compressedPic.deleteOnExit();
		            //res包含hash、key等信息，具体字段取决于上传策略的设置
		             if(info.isOK()){
		                Log.i("qiniu", "Upload Success");
		                imageKey = res.optString("key");
		              //获取图片缩略图，避免OOM
		    			Bitmap bitmap = ImageUtils.getImageThumbnail(filePath, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5);
		    			iv_opera_img.setVisibility(View.GONE);
		    			iv_img.setVisibility(View.VISIBLE);
		    			iv_img.setImageBitmap(bitmap);
		             }else{
		                Log.i("qiniu", "Upload Fail");
		                 CustomToast.showToast(ActivityGoodsAdd.this, "图片上传失败");
		                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
		             }
		             Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
		            }
		        } , new UploadOptions(null, null, false,
					new UpProgressHandler(){
						public void progress(String key, double percent){
							Log.e("qiniu", key + ": " + percent);
						}
					}, new UpCancellationSignal(){
				public boolean isCancelled(){
					return isCancelled;
				}
			}));
	   }else{//直接上传
		    uploadManager.put(new File(filePath), null,token,new UpCompletionHandler() {
		        @Override
		        public void complete(String key, ResponseInfo info, JSONObject res) {
		        	loadDialog.dismiss();
		            //res包含hash、key等信息，具体字段取决于上传策略的设置
		             if(info.isOK()){
		                Log.i("qiniu", "Upload Success");
		                imageKey = res.optString("key");
		              //获取图片缩略图，避免OOM
		    			Bitmap bitmap = ImageUtils.getImageThumbnail(filePath, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5, ImageUtils.getWidth(ActivityGoodsAdd.this) / 3 - 5);
		    			iv_opera_img.setVisibility(View.GONE);
		    			iv_img.setVisibility(View.VISIBLE);
		    			iv_img.setImageBitmap(bitmap);
		             }else{
		                Log.i("qiniu", "Upload Fail");
		                 CustomToast.showToast(ActivityGoodsAdd.this, "图片上传失败");
		                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
		             }
		             Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
		            }
		        } , new UploadOptions(null, null, false,
					new UpProgressHandler(){
						public void progress(String key, double percent){
							Log.e("qiniu", key + ": " + percent);
						}
					}, new UpCancellationSignal(){
				public boolean isCancelled(){
					return isCancelled;
				}
			}));
	   }
	}
}
