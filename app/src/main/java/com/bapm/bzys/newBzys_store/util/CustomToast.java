package com.bapm.bzys.newBzys_store.util;

import android.content.Context;
import android.widget.Toast;
/**
 *
 * 
 * @author 小洪
 */
public class CustomToast {
	private static Toast mToast;

	public static void showToast(Context mContext, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_LONG);
		}
//		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

	public static void showToast(Context mContext, int resId) {
		showToast(mContext, mContext.getResources().getString(resId));
	}

}
