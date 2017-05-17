package com.bapm.bzys.newBzys_store.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.adapter.AccountChildAdapter;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.model.AccountChild;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.SimpleHeader;
import com.bapm.bzys.newBzys_store.widget.ZrcListView;
import com.bapm.bzys.newBzys_store.widget.ZrcListView.OnItemClickListener;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialogListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAccountChild extends BaseActivity implements Function,OnClickListener,OnItemClickListener{
	private FunctionManager manager;
	private Button btn_add_account;
	private ZrcListView listView;
	private AccountChildAdapter adapter;
	private List<AccountChild> childs;
	private LinearLayout   layout_menu_home;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_child);
		manager = this.init(this.getContext());
		manager.registFunClass(ActivityAccountChild.class);
		initView();
		initData();
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		layout_menu_home = (LinearLayout) findViewById(R.id.layout_menu_home);
		layout_menu_home.setOnClickListener(this);
		btn_add_account = (Button) findViewById(R.id.btn_add_account);
		btn_add_account.setOnClickListener(this);
		
		listView = (ZrcListView) findViewById(R.id.zListView);
		childs = new ArrayList<AccountChild>();
		adapter = new AccountChildAdapter(this,childs);
//		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
//		float density = getResources().getDisplayMetrics().density;
//		listView.setFirstTopOffset((int) (50 * density));
		listView.setDividerHeight(2);
		listView.setDivider(new ColorDrawable(getResources().getColor(R.color.black)));
		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
//		SimpleHeader header = new SimpleHeader(this);
//		header.setTextColor(0xff0066aa);
//		header.setCircleColor(0xff33bbee);
//		listView.setHeadable(header);

//		// 设置加载更多的样式（可选）
//		SimpleFooter footer = new SimpleFooter(this);
//		footer.setCircleColor(0xff33bbee);
//		listView.setFootable(footer);

		// 设置列表项出现动画（可选）
		listView.setItemAnimForTopIn(R.anim.topitem_in);
		listView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
//		listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
//			@Override
//			public void onStart() {
//				refresh();
//			}
//		});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		setRefresh();
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
		listView.hiddenRight(listView.mPreItemView);
		Map<String, String> params = new HashMap<String, String>();
		manager.childList(params, this);
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		listView.setRefreshSuccess("加载成功"); // 通知加载成功
		listView.stopLoadMore();
		switch (requstCode) {
		case DadanUrl.USER_CHILD_DEL_URL_CODE:
			listView.refresh();
			break;
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
		default:
			break;
		}
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
	public void onFaile(int requestCode, int status, String msg) {
		listView.setRefreshFail("加载失败");
		listView.stopLoadMore();
		Log.i(LoginActivity.class.toString(),msg);
//		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
		if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
	@Override
	public void onSuccess(int requstCode, JSONArray result) {
		listView.setRefreshSuccess("加载成功"); // 通知加载成功
		listView.stopLoadMore();
		switch (requstCode) {
		case DadanUrl.USER_CHILD_LIST_URL_CODE:{
			try {
				childs.clear();
				adapter.notifyDataSetChanged();
				for(int i=0;i<result.length();i++){
					JSONObject jsonObj = result.getJSONObject(i);
					String id = jsonObj.optString("ID");
					String subname = jsonObj.optString("SubName");
					String suphone = jsonObj.optString("SubPhone");
					AccountChild child = new AccountChild();
					child.setId(id);
					child.setName(subname);
					child.setPhone(suphone);
					childs.add(child);
					adapter.notifyDataSetChanged();
					listView.setRefreshSuccess("加载成功"); // 通知加载成功
					listView.stopLoadMore();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				
			}
			break;
		}
		case DadanUrl.USER_CHILD_DEL_URL_CODE:{
			listView.refresh();
			break;
		}
		default:
			break;
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
								if(childs.size()>index){
									AccountChild accountChild = childs.get(index);
									JSONObject params = new JSONObject();
									params.put("id", accountChild.getId());
									manager.delAccountChild(params, ActivityAccountChild.this);
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
					public void callback(String[] array) {

					}
				},"取消");
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_account:{
			Intent addChildIntent = new Intent(this,ActivityAccountChildAdd.class);
			startActivityForResult(addChildIntent,200);
			break;
		}
			case R.id.layout_menu_home:
				this.finish();
				break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initData();
	}
	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id) {
		AccountChild child = childs.get(position);
		Intent intent = new Intent(this,ActivityAccountChildAdd.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("child", child);
		intent.putExtras(bundle);
		startActivityForResult(intent,200);
	}
	private void refresh() {
		initData();
	}
}
