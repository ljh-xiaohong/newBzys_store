package com.bapm.bzys.newBzys.adapter;



import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.model.Advert;
import com.bapm.bzys.newBzys.util.AsyncImageLoader;
import com.bapm.bzys.newBzys.widget.ZrcListView;

import java.util.List;

public class AdvertAdapter extends BaseAdapter {

	// 定义Context
	private LayoutInflater inflater;
	private List<Advert> list;
	private Context context;

	public AdvertAdapter(Context context, List<Advert> list) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_advert_list_item, null, false);
			holder = new ViewHolder();
			holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
			holder.iv_qrcode = (ImageView) convertView.findViewById(R.id.iv_qrcode);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.tvRightItem = (TextView) convertView.findViewById(R.id.tvRightItem);
			holder.btn_pause= (Button)convertView.findViewById(R.id.btn_pause);
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
		if(parent instanceof ZrcListView){
			LayoutParams lp2 = new LayoutParams(((ZrcListView)parent).getRightViewWidth(position), LayoutParams.MATCH_PARENT);
			holder.item_right.setLayoutParams(lp2);
		}
		holder.id.setText(String.valueOf(position));
		holder.name.setText(list.get(position).getName());
		holder.iv_qrcode.setImageResource(R.mipmap.qrcode_default);
		holder.tv_no.setText(list.get(position).getNo());
		AsyncImageLoader.getInstance(context).downloadImage(list.get(position).getUrl(), holder.iv_qrcode, true,new AsyncImageLoader.ImageCallback() {
			@Override
			public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
				if(bitmap!=null){
					imageView.setImageBitmap(bitmap);
				}
			}
		});
		if(list.get(position).getStatus().contains("暂停")){
			holder.btn_pause.setVisibility(View.VISIBLE);
			holder.iv_qrcode.setVisibility(View.GONE);
		}else{
			holder.btn_pause.setVisibility(View.GONE);
			holder.iv_qrcode.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	private static class ViewHolder {
		RelativeLayout item_left;
		RelativeLayout item_right;
		TextView id,name,number,tvRightItem,tv_no;
		ImageView iv_qrcode;
		Button btn_pause;
	}
}