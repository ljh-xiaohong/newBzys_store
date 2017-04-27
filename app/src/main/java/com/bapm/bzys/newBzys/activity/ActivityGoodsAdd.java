package com.bapm.bzys.newBzys.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.model.Goods;
import com.bapm.bzys.newBzys.model.GoodsStatus;
import com.bapm.bzys.newBzys.model.GoodsType;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.AsyncImageLoader;
import com.bapm.bzys.newBzys.util.AsyncImageLoader.ImageCallback;
import com.bapm.bzys.newBzys.util.Constants;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.ImageUtils;
import com.bapm.bzys.newBzys.widget.dialog.ActionSheet;
import com.bapm.bzys.newBzys.widget.dialog.ActionSheet.MenuItemClickListener;
import com.bapm.bzys.newBzys.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialogListener;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

	private Button btn_sure;
	private Button btn_continue;
	
	private Map<String, GoodsType> typeMap;
	private List<String> typeNames;;
	
	private Map<String, GoodsStatus> statusMap;
	private List<String> statusNames;;
	private Goods goods;
	private boolean addNext = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.ActionSheetStyleIOS7);
		setContentView(R.layout.activity_goods_add);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityGoodsAdd.class);
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
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		findViewById(R.id.layout_goods_type).setOnClickListener(this);
		findViewById(R.id.layout_status).setOnClickListener(this);
	}
	public void initData(){
		statusMap = new HashMap<String, GoodsStatus>();
		statusNames = new ArrayList<String>();
		
		typeMap = new HashMap<String, GoodsType>();
		typeNames = new ArrayList<String>();
		
		if(getIntent().hasExtra("goods")){
			Goods goods = (Goods) getIntent().getSerializableExtra("goods");
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", goods.getId());
			manager.goodsDetail(params, this);
			tv_title.setText("商品编辑");
			btn_continue.setVisibility(View.GONE);

		}else{
			tv_title.setText("商品新增");
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
		this.finish();
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
			break;
		}
		case DadanUrl.GOODS_DETAIL_URL_CODE:{
			String id = result.optString("ID");
			String name = result.optString("GoodsName");
			String no = result.optString("GoodsNo");
			String flag = result.optString("GoodsSalesFlag");
			String price = result.optString("GoodsSalesPrice");
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
			ed_price.setText(goods.getPrice());;
			ed_unit.setText(goods.getUnit());
			ed_type.setText(goods.getGoodsTypeName());
			tv_type_id.setText(goods.getGoodsTypeID());
			ed_status.setText(goods.getStatusName());
			tv_status_id.setText(goods.getFlag());
			if(goods.getDesc()!=null)
				ed_desc.setText(goods.getDesc());
			AsyncImageLoader.getInstance(this).downloadImage(goods.getUrl(), iv_img, new ImageCallback() {
				@Override
				public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
					if(bitmap!=null){
						iv_opera_img.setVisibility(View.GONE);
						iv_img.setVisibility(View.VISIBLE);
						iv_img.setImageBitmap(bitmap);
					}else{
						iv_opera_img.setVisibility(View.VISIBLE);
						iv_img.setVisibility(View.GONE);
					}
				}
			});
			break;
		}
		case DadanUrl.QI_NIU_TOKEN_URL_CODE:{
			try {
				int code = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(code==Constants.NETWORK_SUCCESS){
					String token = result.getString(Constants.QN_TOKEN_KEY);
					DadanPreference.getInstance(this).setQNToken(token);;
				}else{
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		default:
			break;
		}
	}

	@Override
	public void onFaile(int requestCode, int status, String msg) {
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
		switch (requstCode) {
		case DadanUrl.GOODS_DIC_STATUS_URL_CODE:{
			try {
				statusNames.clear();
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
					}
					statusNames.add(name);
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
				typeMap.clear();
				for(int i=0;i<result.length();i++){
					JSONObject jsonObj = result.getJSONObject(i);
					String id = jsonObj.optString("ID");
					String name = jsonObj.optString("GoodsTypeNmae");
					GoodsType type = new GoodsType();
					type.setId(id);
					type.setName(name);
					typeNames.add(name);
					typeMap.put(name, type);
					if(ed_type.getText().toString().equals(name)){
						ed_type.setText(name);
						tv_type_id.setText(id);
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
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_goods_type:{
			String[] typesName = new String[typeNames.size()];
			for(int i=0;i<typeNames.size();i++){
				typesName[i] = typeNames.get(i);
			}
			ActionSheet menuView = new ActionSheet(this);
			menuView.setSelectItem(ed_type.getText().toString());
			menuView.addItems(typesName);
			menuView.setItemClickListener(new MenuItemClickListener() {
				@Override
				public void onItemClick(int itemPosition) {
					ed_type.setText(typeNames.get(itemPosition));
					tv_type_id.setText(typeMap.get(typeNames.get(itemPosition)).getId());
				}
			});
			menuView.setCancelableOnTouchMenuOutside(true);
			menuView.showMenu();
			break;
		}
		case R.id.layout_status:{
			String[] statusName = new String[statusNames.size()];
			for(int i=0;i<statusNames.size();i++){
				statusName[i] = statusNames.get(i);
			}
			ActionSheet menuView = new ActionSheet(this);
			menuView.setSelectItem(ed_status.getText().toString());
			menuView.addItems(statusName);
			menuView.setItemClickListener(new MenuItemClickListener() {
				@Override
				public void onItemClick(int itemPosition) {
					ed_status.setText(statusNames.get(itemPosition));
					tv_status_id.setText(statusMap.get(statusNames.get(itemPosition)).getId());
				}
			});
			menuView.setCancelableOnTouchMenuOutside(true);
			menuView.showMenu();
			break;
		}
		case R.id.btn_sure:{
			addNext = false;
			if(tv_status_id.equals("")){
				Toast.makeText(this, "请选择销售状态", Toast.LENGTH_SHORT).show();
				return;
			}
			if((imageKey==null||imageKey.equals(""))&&goods==null){
				Toast.makeText(this, "请先上传商品图片", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject params = new JSONObject();
				if(goods!=null){
					params.put("ID", goods.getId());
				}
				params.put("GoodsTypeID", tv_type_id.getText().toString());
				params.put("GoodsSalesFlag",tv_status_id.getText().toString());
				params.put("GoodsName", ed_name.getText().toString());
				params.put("GoodsNo", ed_no.getText().toString());
				params.put("GoodsCountedBy", ed_unit.getText().toString());
				params.put("GoodsSalesPrice", ed_price.getText().toString());
				params.put("GoodsDescription", ed_desc.getText().toString());
				params.put("QiniuKey", imageKey);
				manager.addGoods(params, this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.btn_continue:{
			addNext = true;
			if(tv_status_id.equals("")){
				Toast.makeText(this, "请选择销售状态", Toast.LENGTH_SHORT).show();
				return;
			}
			if((imageKey==null||imageKey.equals(""))&&goods==null){
				Toast.makeText(this, "请先上传商品图片", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject params = new JSONObject();
				if(goods!=null){
					params.put("ID", goods.getId());
				}
				params.put("GoodsTypeID", tv_type_id.getText().toString());
				params.put("GoodsSalesFlag",tv_status_id.getText().toString());
				params.put("GoodsName", ed_name.getText().toString());
				params.put("GoodsNo", ed_no.getText().toString());
				params.put("GoodsCountedBy", ed_unit.getText().toString());
				params.put("GoodsSalesPrice", ed_price.getText().toString());
				params.put("GoodsDescription", ed_desc.getText().toString());
				params.put("QiniuKey", imageKey);
				manager.addGoods(params, this);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.iv_img:{
			showPicturePopupWindow();
			break;
		}
		case R.id.iv_opera_img:{
			showPicturePopupWindow();
			break;
		}
		default:
			break;
		}
	}
	public void handleGoodsAdd(JSONObject result){
		try {
			int code = result.optInt(Constants.CODE_KEY);
			String message = result.getString(Constants.MESSAGE_KEY);
			if(code==Constants.NETWORK_SUCCESS){
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
		}
	}
	public void uploadHeader(final String filePath){
		//调用压缩图片的方法，返回压缩后的图片path
		String targetPath = this.getCacheDir().getAbsolutePath()+"compressPic.jpg";
	    final String compressImage = ImageUtils.compressImage(filePath, targetPath, 10);
	    final File compressedPic = new File(compressImage);

		loadDialog.setTitle("图片上传中...");
		loadDialog.show();
		Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .recorder(null)  // recorder分片上传时，已上传片记录器。默认null
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
		                Toast.makeText(ActivityGoodsAdd.this, "图片上传失败", Toast.LENGTH_SHORT).show();
		                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
		             }
		             Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
		            }
		        } , null);
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
		                Toast.makeText(ActivityGoodsAdd.this, "图片上传失败", Toast.LENGTH_SHORT).show();
		                //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
		             }
		             Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
		            }
		        } , null);;
	   }
	}
}
