package com.bapm.bzys.newBzys_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	public void toLogin(View v){
//		if (DadanPreference.getInstance(this).getTicket()==null) {
			Intent loginIntent = new Intent(this.getContext(), LoginActivity.class);
			startActivity(loginIntent);
//		}else{
//			Intent loginIntent = new Intent(this.getContext(), MainActivity.class);
//			startActivity(loginIntent);
//		}
	}
	public void toRegist(View v){
		Intent registIntent = new Intent(this.getContext(),RegistActivity.class);
		startActivity(registIntent);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
