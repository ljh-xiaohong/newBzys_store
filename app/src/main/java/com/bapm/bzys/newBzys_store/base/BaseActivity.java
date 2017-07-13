package com.bapm.bzys.newBzys_store.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.widget.DadanArcDialog;
import com.bapm.bzys.newBzys_store.widget.SelectPicPopupWindow;

public class BaseActivity extends FragmentActivity{
	public String[] proj = { MediaColumns.DATA };
	/**
	 * 选择图片的返回码
	 */
	public final static int SELECT_IMAGE_RESULT_CODE = 200; 
	/**
	 * 当前选择的图片的路径
	 */
	public String mImagePath;
	/**
	 * 自定义的PopupWindow
	 */
	private SelectPicPopupWindow menuWindow;
	/**
	 * Fragment回调接口
	 */
	public OnFragmentResult mOnFragmentResult;
	private Activity context;
	public DadanArcDialog loadDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		ActivityManager.getInstance().pushOneActivity(context);
		loadDialog = new DadanArcDialog(this);
		loadDialog.setCancelable(false);
		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
		//透明导航栏  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}
	public void back(View v){
		this.finish();
	}
	public Activity getContext() {
		return context;
	}
	public void setContext(Activity context) {
		this.context = context;
	}
	
	public void setOnFragmentResult(OnFragmentResult onFragmentResult){
		mOnFragmentResult = onFragmentResult;
	}
	
	/**
	 * 回调数据给Fragment的接口
	 */
	public interface OnFragmentResult{
		void onResult(String mImagePath);
	}
	
	/**
	 * 拍照或从图库选择图片(Dialog形式)
	 */
	public void showPictureDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "拍摄照片", "选择照片", "取消" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0://拍照  
							takePhoto();
							break;
						case 1://相册选择图片
							pickPhoto();
							break;
						case 2://取消
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}
	/**
	 * 检查相机权限，如果不能打开相机则抛出异常
	 */
	public static void checkCameraPermissions() throws IOException {
		try {
			Camera camera = Camera.open();
			if (camera != null) {
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
			throw new IOException();
		}
	}
	/**
	 * 拍照或从图库选择图片(PopupWindow形式)
	 */
	public void showPicturePopupWindow(){
		menuWindow = new SelectPicPopupWindow(this, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 隐藏弹出窗口
				menuWindow.dismiss();
				switch (v.getId()) {
				case R.id.takePhotoBtn:// 拍照
					takePhoto();
					break;
				case R.id.pickPhotoBtn:// 相册选择图片
					pickPhoto();
					break;
				case R.id.cancelBtn:// 取消
					break;
				default:
					break;
				}
			}
		});  
		menuWindow.showAtLocation(findViewById(R.id.choose_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		//检查相机权限
		try {
			checkCameraPermissions();
		} catch (IOException e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("照相权限被禁止，无法使用该功能!");
			builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setCancelable(false);
			builder.create().show();

		}
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			/**
			 * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
			 */
			//获取与应用相关联的路径
			String imageFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA); 
			//根据当前时间生成图片的名称
			String timestamp = "/"+formatter.format(new Date())+".jpg"; 
			File imageFile = new File(imageFilePath,timestamp);// 通过路径创建保存文件
			mImagePath = imageFile.getAbsolutePath();
			Uri imageFileUri = Uri.fromFile(imageFile);// 获取文件的Uri
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (currentapiVersion < 24) {
				// 从文件中创建uri
				intent.putExtra(MediaStore.EXTRA_OUTPUT,imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
			} else {
				//兼容android7.0 使用共享文件的形式
				ContentValues contentValues = new ContentValues(1);
				contentValues.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
				Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			}
			startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE);
		} else {
			Toast.makeText(this, "内存卡不存在!", Toast.LENGTH_LONG).show();
		}
	}
	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE);
	}
}
