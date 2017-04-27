package com.bapm.bzys.newBzys.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.adapter.GoodsAdapter;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.model.Goods;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.HttpUtil;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.Constants;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.ZrcListView;
import com.bapm.bzys.newBzys.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialogListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityGoods extends BaseActivity implements Function,OnClickListener{
	private TextView toolsTextViews[];
	private View views[];
	private LayoutInflater inflater;
	private LinearLayout   layout_menu_home;

	private FunctionManager manager;
	private List<String> types;
	private List<Goods> goodses = new ArrayList<Goods>();
	private Map<String,List<Goods>> goodsMap;
	private String typeName;

	private ZrcListView listView;
	private GoodsAdapter adapter;

	private Button btn_add;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);
		manager = this.init(this);
		manager.registFunClass(ActivityGoods.class);
		initUI();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initUI(){
		listView = (ZrcListView) findViewById(R.id.zListView);
		btn_add  = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		layout_menu_home = (LinearLayout) findViewById(R.id.layout_menu_home);
		layout_menu_home.setOnClickListener(this);
		types = new ArrayList<String>();
		goodsMap = new HashMap<String, List<Goods>>();
		inflater = LayoutInflater.from(this);

		adapter =new GoodsAdapter(this, goodses);
		listView.setDividerHeight(1);
		// 设置列表项出现动画（可选）
		listView.setItemAnimForTopIn(R.anim.topitem_in);
		listView.setItemAnimForBottomIn(R.anim.bottomitem_in);
		listView.setAdapter(adapter);
	}
	public void initData(){
		Map<String, String> params = new HashMap<String, String>();
		manager.goodsList(params, this);
	}
	public void sortUp(View v){
		listView.hiddenRight(listView.mPreItemView);
		try{
			View parent = (View) v.getParent().getParent();
			TextView indexView = (TextView) parent.findViewById(R.id.id);
			if(indexView!=null){
				int index = Integer.valueOf(indexView.getText().toString());
				if(goodses.size()>index&&index>0){
					Goods item = goodses.get(index);
					JSONObject params = new JSONObject();
					params.put("minId", item.getId());
					params.put("maxId", goodses.get(index-1).getId());
					manager.exchangeGoods(params, this);
				}else{
					listView.hiddenRight(listView.mPreItemView);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {

		}
	}
	public void delete(final View v){
		listView.hiddenRight(listView.mPreItemView);
		new MyDialog(this.getContext()).callback(MyDialog.TYPE_INFO, "您确定要删除吗？",
				new MyDialogListener() {
					@Override
					public void callback(String[] array) {
						try{
							View parent = (View) v.getParent();
							TextView indexView = (TextView) parent.findViewById(R.id.id);
							if(indexView!=null){
								int index = Integer.valueOf(indexView.getText().toString());
								if(goodses.size()>index&&index>=0){
									Goods item = goodses.get(index);
									JSONObject params = new JSONObject();
									params.put("id", item.getId());
									manager.deleteGoods(params, ActivityGoods.this);
								}else{
									listView.hiddenRight(listView.mPreItemView);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {

						}
					}
				}, "确定",
				new MyDialogListener() {
					@Override
					public void callback(String[] array) {}
				},"取消");
	}
	/**
	 * 动态生成显示items中的textview
	 */
	private void showToolsView(List<String> types) {
		LinearLayout toolsLayout = (LinearLayout) findViewById(R.id.tools);
		int size = toolsLayout.getChildCount();
        for( int i = 0; i < size; i++){
        	toolsLayout.removeViewAt(0);
        }
		toolsTextViews = new TextView[types.size()];
		views = new View[types.size()];
		for (int i = 0; i < types.size(); i++) {
			View view = inflater.inflate(R.layout.activity_goods_type_name_list_item, null);
			view.setTag(i);
			view.setOnClickListener(toolsItemListener);
			TextView tv_type_name = (TextView) view.findViewById(R.id.tv_type_name);
			tv_type_name.setText(types.get(i));
			toolsLayout.addView(view);
			toolsTextViews[i] = tv_type_name;
			views[i] = view;
		}
		if(typeName==null){
			changeTextColor(0);
		}if(types.indexOf(typeName)==-1&&types.size()>0){
			changeTextColor(0);
			goodses.clear();
			goodses.addAll(goodsMap.get(types.get(0)));
			adapter.notifyDataSetChanged();
		}else{
			int index = types.indexOf(typeName);
			if(index<types.size()&&index>-1)
				changeTextColor(index);
		}
	}
	private OnClickListener toolsItemListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			changeTextColor(tag);
			String typeName = types.get(tag);
			goodses.clear();
			goodses.addAll(goodsMap.get(typeName));
			adapter.notifyDataSetChanged();
		}
	};
	public void onItemClick(View v){
		listView.hiddenRight(listView.mPreItemView);
		View parent = (View) v.getParent().getParent();
		TextView indexView = (TextView) parent.findViewById(R.id.id);
		if(indexView!=null){
			int index = Integer.valueOf(indexView.getText().toString());
			Goods goods = goodses.get(index);
			Intent intent = new Intent(this,ActivityGoodsAdd.class);
			Bundle bundle = getIntent().getExtras();
			if( bundle ==null){
				 bundle = new Bundle();
			}
			bundle.putSerializable("goods", goods);
			intent.putExtras(bundle);
			startActivityForResult(intent,200);
		}
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200){
        	initData();
        	if(data.getBooleanExtra("addNext", false)){
        		Intent intent = new Intent(this,ActivityGoodsAdd.class);
        		intent.putExtra("type_id", data.getStringExtra("type_id"));
        		intent.putExtra("type_name", data.getStringExtra("type_name"));
        		intent.putExtra("status_id", data.getStringExtra("status_id"));
        		intent.putExtra("status_name", data.getStringExtra("status_name"));
        		intent.putExtra("unit", data.getStringExtra("unit"));
        		startActivityForResult(intent,200);
        	}
        }
    }
	/**
	 * 改变textView的颜色
	 *
	 * @param id
	 */
	private void changeTextColor(int position) {
		if(position>=toolsTextViews.length)
			return;
		for (int i = 0; i < toolsTextViews.length; i++) {
			if (i != position) {
				toolsTextViews[i].setBackgroundColor(0xfff9f9f9);
				toolsTextViews[i].setTextColor(0xff333333);
				View parent = (View)toolsTextViews[i].getParent();
				parent.findViewById(R.id.iv_left).setVisibility(View.GONE);
			}
		}
		if(toolsTextViews.length==0)
			return;
		toolsTextViews[position].setBackgroundResource(android.R.color.white);
		toolsTextViews[position].setTextColor(getResources().getColor(R.color.red));
		typeName=toolsTextViews[position].getText().toString();
		View parent = (View)toolsTextViews[position].getParent();
		parent.findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:{
			Intent addIntent = new Intent(this,ActivityGoodsAdd.class);
			addIntent.putExtra("typeName", typeName);
			startActivityForResult(addIntent,200);
			break;
		}
		case R.id.layout_menu_home:{
			this.finish();
			break;
		}
		default:
			break;
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
		case DadanUrl.GOODS_SORT_CHANGE_URL_CODE:{
			try {
				int code = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(code==Constants.NETWORK_SUCCESS){
					initData();
				}else{
					listView.hiddenRight(listView.mPreItemView);
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		}
		case DadanUrl.GOODS_DELETE_URL_CODE:{
			try {
				int code = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(code==Constants.NETWORK_SUCCESS){
					initData();
				}else{
					listView.hiddenRight(listView.mPreItemView);
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		switch (requstCode) {
		case DadanUrl.GOODS_LIST_URL_CODE:{
			try {
				types.clear();
				goodsMap.clear();
				goodses.clear();
				showToolsView(types);
				adapter.notifyDataSetChanged();
				for(int i=0;i<result.length();i++){
					JSONObject goodsTypeJson = result.getJSONObject(i);
					String goodsTypeName = goodsTypeJson.getString("GoodsTypeName");
					JSONArray goodsArray = goodsTypeJson.getJSONArray("GoodsInfo");
					List<Goods> goodsList = new ArrayList<Goods>();
					for(int j=0;j<goodsArray.length();j++){
						JSONObject goodsJson = goodsArray.getJSONObject(j);
						String id = goodsJson.optString("ID");
						String name = goodsJson.optString("GoodsName");
						String no = goodsJson.optString("GoodsNo");
						String flag = goodsJson.optString("GoodsSaleFlag");
						String price = goodsJson.optString("GoodsSalesPrice");
						String statusName = goodsJson.optString("GoodsSaleName");
						String unit = goodsJson.optString("GoodsCountedBy");
						String url = goodsJson.optString("PicUrl");
						String goodsTypeID = goodsJson.optString("GoodsTypeID");
						String goodsTypeNmae = goodsJson.optString("GoodsTypeNmae");
						String desc = goodsJson.getString("GoodsDescription");
						String zan = goodsJson.getString("Zan");
						Goods goods = new Goods();
						goods.setId(id);
						goods.setName(name);
						goods.setNo(no);
						goods.setFlag(flag);
						goods.setPrice(price);
						goods.setUrl(url);
						goods.setUnit(unit);
						goods.setZan(zan);
						goods.setStatusName(statusName);
						goods.setGoodsTypeName(goodsTypeNmae);
						goods.setDesc(desc);
						goods.setGoodsTypeID(goodsTypeID);
						goodsList.add(goods);
					}
					types.add(goodsTypeName);
					goodsMap.put(goodsTypeName,goodsList);
					if(goodses.size()==0&&typeName==null){
						goodses.addAll(goodsList);
						typeName = goodsTypeName;
					}else if(goodsTypeName.equals(typeName)){
						goodses.clear();
						goodses.addAll(goodsList);
					}
				}
				showToolsView(types);
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
	public void onFaile(int requestCode, int status, String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		if(requestCode==HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
	public void back(View v){
		this.finish();
	}
	/**
	 * 点击底部菜单事件
	 * @param v
	 */
	public void clickMenu(View v){
		listView.hiddenRight(listView.mPreItemView);
		Intent intent=new Intent(this,MainActivity.class);
		intent.putExtra("isOrder","order");
		startActivity(intent);
		ActivityManager.getInstance().finishAllActivity();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		manager.unregistFunctionClass(ActivityGoods.class);
	}
}
