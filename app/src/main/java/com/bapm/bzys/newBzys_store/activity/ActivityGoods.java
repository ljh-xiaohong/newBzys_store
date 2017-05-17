package com.bapm.bzys.newBzys_store.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.adapter.GoodsAdapter;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.Goods;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.SimpleHeader;
import com.bapm.bzys.newBzys_store.widget.ZrcListView;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialogListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		setRefresh();
		adapter =new GoodsAdapter(this, goodses);
		listView.setDividerHeight(1);
		// 设置列表项出现动画（可选）
		listView.setAdapter(adapter);
	}
	/*
   * 设置下拉刷新
   * */
	SwipeRefreshLayout swipeRefreshView;
	private void setRefresh() {
		// 不能在onCreate中设置，这个表示当前是刷新状态，如果一进来就是刷新状态，SwipeRefreshLayout会屏蔽掉下拉事件
		//swipeRefreshLayout.setRefreshing(true);

		// 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
		// 设置下拉进度的背景颜色，默认就是白色的
		swipeRefreshView= (SwipeRefreshLayout) findViewById(R.id.srl);
		swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
		// 设置下拉进度的主题颜色
		swipeRefreshView.setColorSchemeResources(R.color.edt_hint_orange, R.color.commit_orange);

		// 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
		swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {

				// 这里是主线程
				// 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
				// TODO 获取数据
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Map<String, String> params = new HashMap<String, String>();
						initData();
						// 加载完数据设置为不刷新状态，将下拉进度收起来
						swipeRefreshView.setRefreshing(false);
					}
				}, 1200);
			}
		});
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
					 CustomToast.showToast(this, message);
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
					 CustomToast.showToast(this, message);
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
				if (result.length()>0) {
					types.clear();
					goodsMap.clear();
					goodses.clear();
					showToolsView(types);
					adapter.notifyDataSetChanged();
					for (int i = 0; i < result.length(); i++) {
						JSONObject goodsTypeJson = result.getJSONObject(i);
						String goodsTypeName = goodsTypeJson.getString("GoodsTypeName");
						JSONArray goodsArray = goodsTypeJson.getJSONArray("GoodsInfo");
						List<Goods> goodsList = new ArrayList<Goods>();
						for (int j = 0; j < goodsArray.length(); j++) {
							JSONObject goodsJson = goodsArray.getJSONObject(j);
							String id = goodsJson.optString("ID");
							String name = goodsJson.optString("GoodsName");
							String no = goodsJson.optString("GoodsNo");
							String flag = goodsJson.optString("GoodsSaleFlag");
							Double price = goodsJson.optDouble("GoodsSalesPrice");
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
						goodsMap.put(goodsTypeName, goodsList);
						if (goodses.size() == 0 && typeName == null) {
							goodses.addAll(goodsList);
							typeName = goodsTypeName;
						} else if (goodsTypeName.equals(typeName)) {
							goodses.clear();
							goodses.addAll(goodsList);
						}
					}
					showToolsView(types);
				}else{
					CustomToast.showToast(this,"目前没有商品");
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
	public void onFaile(int requestCode, int status, String msg) {
//		 CustomToast.showToast(this, msg);
		if(requestCode==HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
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
