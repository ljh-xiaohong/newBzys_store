package com.bapm.bzys.newBzys_store.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.model.Goods;
import com.bapm.bzys.newBzys_store.util.AsyncImageLoader;
import com.bapm.bzys.newBzys_store.util.GlideUtils;
import com.bapm.bzys.newBzys_store.widget.ZrcListView;

import java.text.NumberFormat;
import java.util.List;


public class GoodsAdapter extends BaseAdapter {
	// 定义Context
		private LayoutInflater mInflater;
	    private List<Goods> list;
	    private Context context;
		public GoodsAdapter(Context context,List<Goods> list){
			mInflater=LayoutInflater.from(context);
			this.list=list;
			this.context=context;
		}
		
		@Override
		public int getCount() {
			if(list!=null&&list.size()>0)
				return list.size();
			else
			    return 0;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder view;
			if(convertView==null){
				view=new ViewHolder();
				convertView=mInflater.inflate(R.layout.activity_goods_item, null);
				view.layout_opera = (LinearLayout) convertView.findViewById(R.id.layout_opera);
				view.goods_icon=(ImageView)convertView.findViewById(R.id.goods_icon);
				view.tv_id = (TextView)convertView.findViewById(R.id.id);
				view.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
				view.tv_no=(TextView)convertView.findViewById(R.id.tv_no);
				view.tv_status_name=(TextView)convertView.findViewById(R.id.tv_status_name);
				view.tv_price=(TextView)convertView.findViewById(R.id.tv_price);
				view.tv_unit=(TextView)convertView.findViewById(R.id.tv_unit);
				view.tv_zan=(TextView)convertView.findViewById(R.id.tv_zan);
				view.tv_up=(ImageView)convertView.findViewById(R.id.tv_up);
				view.tv_up_text=(TextView)convertView.findViewById(R.id.tv_up_text);
				view.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
				view.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
				convertView.setTag(view);
			}else{
				view=(ViewHolder) convertView.getTag();
			}
			if(parent instanceof ZrcListView){
				LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				view.item_left.setLayoutParams(lp1);
				LayoutParams lp2 = new LayoutParams(((ZrcListView)parent).getRightViewWidth(position), LayoutParams.MATCH_PARENT);
				view.item_right.setLayoutParams(lp2);
			}
			if(position==0){
				view.tv_up.setVisibility(View.GONE);
				view.tv_up_text.setVisibility(View.GONE);
			}else{
				view.tv_up.setVisibility(View.VISIBLE);
				view.tv_up_text.setVisibility(View.VISIBLE);
			}
			NumberFormat nf=NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			if(list!=null&&list.size()>0){
				Goods goods=list.get(position);
//				view.goods_icon.setBackgroundResource(R.drawable.shuji);
				view.goods_icon.setImageResource(R.mipmap.qrcode_default);
				view.tv_id.setText(String.valueOf(position));
				view.tv_title.setText(goods.getName());
				view.tv_no.setText(goods.getNo());
				view.tv_status_name.setText(goods.getStatusName());
				view.tv_price.setText(nf.format(goods.getPrice())+"");
				view.tv_zan.setText(goods.getZan());
				if(goods.getUnit().contains("/"))
					view.tv_unit.setText(goods.getUnit());
				else if(goods.getUnit()!=null&&!goods.getUnit().equals(""))
					view.tv_unit.setText("/"+goods.getUnit());
				else
					view.tv_unit.setText("");
//				AsyncImageLoader.getInstance(context).downloadImage(goods.getUrl(), view.goods_icon, true, new AsyncImageLoader.ImageCallback() {
//					@Override
//					public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
//						if(bitmap!=null){
//							imageView.setImageBitmap(bitmap);
//						}
//					}
//				});
				if (goods.getUrl()==null||goods.getUrl().equals("")) {
					GlideUtils.displayNative(view.goods_icon, R.mipmap.qrcode_default);
				} else {
					GlideUtils.display(view.goods_icon,goods.getUrl());
				}
			}
	        return convertView;
		}
		private class ViewHolder{
			RelativeLayout item_left;
			RelativeLayout item_right;
			private LinearLayout layout_opera;
			private ImageView goods_icon;	
			private TextView tv_id;
			private TextView tv_title;
			private TextView tv_no;
			private TextView tv_status_name;
			private TextView tv_price;
			private TextView tv_unit;
			private TextView tv_zan;
			private ImageView tv_up;
			private TextView tv_up_text;
		}
		
}
