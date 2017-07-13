package com.bapm.bzys.newBzys_store.activity;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.util.GlideUtils;
import com.bapm.bzys.newBzys_store.widget.SmoothImageButton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;

public class SpaceImageDetailActivity extends Activity {
	private int mLocationX;
	private int mLocationY;
	private int mWidth;
	private int mHeight;
	private SmoothImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		imageButton = (SmoothImageButton) findViewById(R.id.iv_qrcode);
		mLocationX = getIntent().getIntExtra("locationX", 0);
		mLocationY = getIntent().getIntExtra("locationY", 0);
		mWidth = getIntent().getIntExtra("width", 0);
		mHeight = getIntent().getIntExtra("height", 0);

		imageButton.setScaleType(ScaleType.FIT_CENTER);
		imageButton.setBackgroundColor(getResources().getColor(android.R.color.white));
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		// ImageLoader.getInstance().displayImage(mDatas.get(mPosition),
		// imageView);
		// Imag
		if (getIntent().hasExtra("imageUrl")) {
//			AsyncImageLoader.getInstance(this).downloadImage(getIntent().getStringExtra("imageUrl"), imageButton,
//					new AsyncImageLoader.ImageCallback() {
//						@Override
//						public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
//							if (bitmap != null) {
//								imageView.setImageBitmap(bitmap);
//							}
//						}
//					});
			if (getIntent().getStringExtra("imageUrl")==null||getIntent().getStringExtra("imageUrl").equals("")) {
				GlideUtils.displayNative(imageButton, R.mipmap.qrcode_default);
			} else {
				GlideUtils.display(imageButton,getIntent().getStringExtra("imageUrl"));
			}
		}

	}
	public void close(View v){
		this.finish();
	}
	@Override
	public void onBackPressed() {
		imageButton.setOnTransformListener(new SmoothImageButton.TransformListener() {
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					finish();
				}
			}
		});
		imageButton.transformOut();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}
}
