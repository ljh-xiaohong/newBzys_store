package com.bapm.bzys.newBzys_store.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.Constants;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.MyDialogListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.TELEPHONY_SERVICE;


public class FragmentHome extends Fragment implements OnClickListener,Function{
	private FunctionManager manager;
	private View view;// 需要返回的布局
	private TextView tv_store_name;
	private RelativeLayout layout_sitemap;//商品大类
	private RelativeLayout layout_archive;//商品管理
	private RelativeLayout layout_bullhorn;//订单管理
	private RelativeLayout layout_account;//子账号
	private RelativeLayout layout_client_profile;//基本资料
	private RelativeLayout layout_book_information;//帮助与反馈
	private ImageView btn_exit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if (view == null) {// 优化View减少View的创建次数
			view = inflater.inflate(R.layout.activity_home, null);
			initView();
		}
		manager = this.init(this.getContext());
		manager.registFunClass(FragmentHome.class);
        initData();
		return view;
	}

	private void initData() {
		Map<String,String> params = new HashMap<String, String>();
		manager.getEnterpriseDetail(params, this);
	}

	@Override
	public FunctionManager init(Context context) {
		return new FunctionManager(context);
	}
	public void initView(){
		tv_store_name = (TextView) view.findViewById(R.id.tv_store_name);
		layout_sitemap = (RelativeLayout) view.findViewById(R.id.layout_sitemap);
		layout_archive = (RelativeLayout) view.findViewById(R.id.layout_archive);
		layout_bullhorn  = (RelativeLayout) view.findViewById(R.id.layout_bullhorn);
		layout_account   = (RelativeLayout) view.findViewById(R.id.layout_account);
		layout_client_profile = (RelativeLayout) view.findViewById(R.id.layout_client_profile);
		layout_book_information = (RelativeLayout) view.findViewById(R.id.layout_book_information);
		btn_exit = (ImageView) view.findViewById(R.id.btn_exit);
		layout_sitemap.setOnClickListener(this);
		layout_archive.setOnClickListener(this);
		layout_bullhorn.setOnClickListener(this);
		layout_account.setOnClickListener(this);
		layout_client_profile.setOnClickListener(this);
		layout_book_information.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_sitemap:{
			Intent siteMap = new Intent(this.getContext(),ActivityGoodsType.class);
			startActivity(siteMap);
			break;
		}
		case R.id.layout_archive:{
			Intent archiveMap = new Intent(this.getContext(),ActivityGoods.class);
			startActivity(archiveMap);
			break;
		}
		case R.id.layout_bullhorn:{
			Intent siteMap = new Intent(this.getContext(),ActivityAdvert.class);
			startActivity(siteMap);
			break;
		}
		case R.id.layout_account:{
			Intent child = new Intent(this.getActivity(),ActivityAccountChild.class);
			startActivity(child);
			break;
		}
		case R.id.layout_client_profile:{
			Intent intent=new Intent(this.getActivity(),ShopInfoActivity.class);
			intent.putExtra("wherefrom","FragmentHome");
			startActivity(intent);
			break;
			}
		case R.id.layout_book_information:{
			Intent child = new Intent(this.getActivity(),ActivityBookInformation.class);
			startActivity(child);
			break;
			}
		case R.id.btn_exit:{
			new MyDialog(this.getContext()).callback(MyDialog.TYPE_INFO, "您确定要退出吗？",
					new MyDialogListener() {
						@Override
						public void callback(String[] array) {
							Map<String, String> params = new HashMap<String, String>();
							manager.exit(params, FragmentHome.this);
							Intent intent = new Intent(FragmentHome.this.getActivity(),LoginActivity.class);
							startActivity(intent);
							DadanPreference.getInstance(FragmentHome.this.getActivity()).removeTicket();
							ActivityManager.getInstance().finishAllActivity();
						}
					}, "确定", 
					new MyDialogListener() {
						@Override
						public void callback(String[] array) {

						}
					},"取消");
			break;
		}
		default:
			break;
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.unregistFunctionClass(FragmentHome.class);
	}
	@Override
	public void onSuccess(int requstCode, JSONObject result) {
		switch (requstCode) {
			case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
				if (result.optString("LogionCode").equals("1")) {
					DadanPreference.getInstance(getActivity()).setTicket(result.optString("Ticket"));
					initData();
				}else if(result.optString("LogionCode").equals("-1")){
					Intent intent=new Intent(getContext(),LoginActivity.class);
					intent.putExtra("LogionCode","-1");
					startActivity(intent);
				}
				break;
		case DadanUrl.EXIT_URL_CODE:{
			try {
				int code = result.optInt(Constants.CODE_KEY);
				String message = result.getString(Constants.MESSAGE_KEY);
				if(code==Constants.NETWORK_SUCCESS){
//					Intent intent = new Intent(FragmentHome.this.getActivity(),LoginActivity.class);
//					startActivity(intent);
//					DadanPreference.getInstance(FragmentHome.this.getActivity()).removeTicket();
//					FragmentHome.this.getActivity().finish();
				}else{
					 CustomToast.showToast(FragmentHome.this.getContext(), message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
		case DadanUrl.ENTERPRISE_DETAIL_URL_CODE:{
			if(result.has("CompanyName")&&(!result.optString("CompanyName").equals("null")&&!result.optString("CompanyName").equals(""))){
				String storeName = result.optString("CompanyName");
				tv_store_name.setText(storeName);
			}else{
				 CustomToast.showToast(getActivity(),"请先填写店铺资料！！！");
				Intent intent=new Intent(this.getActivity(),ShopInfoActivity.class);
				intent.putExtra("wherefrom","RegistActivity");
				startActivity(intent);
				getActivity().finish();
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
		Log.e("requestCode",requestCode+"");
		Log.e("status",status+"");
		Log.e("msg",msg+"");
//		 CustomToast.showToast(getActivity(),msg);
		if(requestCode== HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE||requestCode==233){
			Map<String, String> params = new HashMap<String, String>();
			params.put("DEVICE_ID", ((TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE)).getDeviceId());
			manager.loginAgain(params, this);
		}
	}
}
