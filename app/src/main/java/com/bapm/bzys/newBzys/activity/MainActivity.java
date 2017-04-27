package com.bapm.bzys.newBzys.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.base.BaseActivity;
import com.bapm.bzys.newBzys.network.DadanUrl;
import com.bapm.bzys.newBzys.network.function.interf.Function;
import com.bapm.bzys.newBzys.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys.util.ActivityManager;
import com.bapm.bzys.newBzys.util.Constants;
import com.bapm.bzys.newBzys.util.DadanPreference;
import com.bapm.bzys.newBzys.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys.widget.dialog.MyDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity  implements Function{
	private FunctionManager manager;
	// 布局管理器
	private FragmentManager fragManager;

	private FragmentHome  fragHome;
	private FragmentOrder fragOrder;

	// 主页
	private ImageView iv_menu_home;
	private TextView tv_menu_home;

	// 订单
	private ImageView iv_menu_order;
	private TextView tv_menu_order;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = this.init(this.getContext());
		manager.registFunClass(MainActivity.class);
		// 初始化组件
		initViews();
		// 默认先点中第一个“首页”
		if (getIntent().getStringExtra("isOrder")!=null&&getIntent().getStringExtra("isOrder").equals("order")) {
			clickMenu(findViewById(R.id.layout_menu_order));
		}else {
			clickMenu(findViewById(R.id.layout_menu_home));
		}
	}

	private void initViews() {
		// 布局管理器
		fragManager = getSupportFragmentManager();

		iv_menu_home = (ImageView)findViewById(R.id.iv_menu_home);
		tv_menu_home = (TextView)findViewById(R.id.tv_menu_home);

		iv_menu_order = (ImageView)findViewById(R.id.iv_menu_order);
		tv_menu_order = (TextView)findViewById(R.id.tv_menu_order);
	}
	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	/**
	 * 点击底部菜单事件
	 * @param v
	 */
	public void clickMenu(View v){
		FragmentTransaction trans = fragManager.beginTransaction();
		int vID = v.getId();
		// 设置menu样式
		setMenuStyle(vID);
		// 隐藏所有的fragment
		hideFrament(trans);
		// 设置Fragment
		setFragment(vID,trans);
		trans.commit();
	}
	/**
	 * 隐藏所有的fragment(编程初始化状态)
	 * @param trans
	 */
	private void hideFrament(FragmentTransaction trans) {
		if(fragHome!=null){
			trans.hide(fragHome);
		}
		if(fragOrder!=null){
			trans.hide(fragOrder);
		}
	}
	/**
	 * 设置menu样式
	 * @param vID
	 * @param trans
	 */
	private void setMenuStyle(int id) {
		// 主页
		if(id==R.id.layout_menu_home){
			iv_menu_home.setImageDrawable(getResources().getDrawable(R.mipmap.menu_home_click));
			tv_menu_home.setTextColor(getResources().getColor(R.color.menu_click));
		}
		else {
			iv_menu_home.setImageDrawable(getResources().getDrawable(R.mipmap.menu_home_nomarl));
			tv_menu_home.setTextColor(getResources().getColor(R.color.menu_nomarl));
		}
		// 订单
		if(id==R.id.layout_menu_order){
			iv_menu_order.setImageDrawable(getResources().getDrawable(R.mipmap.menu_order_click));
			tv_menu_order.setTextColor(getResources().getColor(R.color.menu_click));
		}else {
			iv_menu_order.setImageDrawable(getResources().getDrawable(R.mipmap.menu_order_nomarl));
			tv_menu_order.setTextColor(getResources().getColor(R.color.menu_nomarl));
		}
	}

	/**
	 * 设置Fragment
	 * @param vID
	 * @param trans
	 */
	private void setFragment(int vID,FragmentTransaction trans) {
		switch (vID) {
			case R.id.layout_menu_home:
				if(fragHome==null){
					fragHome = new FragmentHome();
					trans.add(R.id.content,fragHome);
				}else{
					trans.show(fragHome);
				}
				break;
			case R.id.layout_menu_order:
				if(fragOrder==null){
					fragOrder = new FragmentOrder();
					trans.add(R.id.content,fragOrder);
				}else{
					trans.show(fragOrder);
				}
				break;
			default:
				break;
		}
	}
	@Override
	public void onBackPressed() {
		new MyDialog(this.getContext()).callback(MyDialog.TYPE_INFO, "您确定要退出登录吗？",
				new MyDialogListener() {
					@Override
					public void callback(String[] array) {
						Map<String, String> params = new HashMap<String, String>();
						manager.exit(params, MainActivity.this);

						Intent intent = new Intent(MainActivity.this,LoginActivity.class);
						startActivity(intent);
						DadanPreference.getInstance(MainActivity.this).removeTicket();
						ActivityManager.getInstance().finishAllActivity();
					}
				}, "确定",
				new MyDialogListener() {
					@Override
					public void callback(String[] array) {

					}
				},"取消");
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		switch (requstCode) {
			case DadanUrl.EXIT_URL_CODE:{
				try {
					int code = result.optInt(Constants.CODE_KEY);
					String message = result.getString(Constants.MESSAGE_KEY);
					if(code==Constants.NETWORK_SUCCESS){
//					Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//					startActivity(intent);
//					DadanPreference.getInstance(MainActivity.this).removeTicket();
//					MainActivity.this.finish();
					}else{
						Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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

	}
	@Override
	public void onFaile(int requestCode, int status, String msg) {
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
	}
}

