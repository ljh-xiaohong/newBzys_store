package com.bapm.bzys.newBzys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.model.GoodsType;
import com.bapm.bzys.newBzys.widget.ZrcListView;

import java.util.List;

public class GoodsTypeAdapter extends BaseAdapter {

	// 定义Context
	private LayoutInflater inflater;
	private List<GoodsType> list;
	private Context context;

	public GoodsTypeAdapter(Context context, List<GoodsType> list) {
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_goods_type_list_item, null, false);
			holder = new ViewHolder();
			holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.item_right = (LinearLayout) convertView.findViewById(R.id.item_right);
			holder.layout_up = (LinearLayout) convertView.findViewById(R.id.layout_up);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.btn_index = (Button) convertView.findViewById(R.id.btn_index);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.tvRightItem = (TextView) convertView.findViewById(R.id.tvRightItem);
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		int rightWidth = context.getResources().getDimensionPixelSize(R.dimen.activity_goods_type_operate_unit_width);
		if(position==0){
			((ZrcListView)parent).setItemRightWidths(position, rightWidth*2);
			holder.layout_up.setVisibility(View.GONE);
		}else{
			((ZrcListView)parent).setItemRightWidths(position, rightWidth*3);
			holder.layout_up.setVisibility(View.VISIBLE);
		}
		LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
		if(parent instanceof ZrcListView){
			LayoutParams lp2 = new LayoutParams(((ZrcListView)parent).getRightViewWidth(position), LayoutParams.MATCH_PARENT);
			holder.item_right.setLayoutParams(lp2);
		}
		holder.id.setText(String.valueOf(position));
		holder.name.setText(list.get(position).getName());
		holder.number.setText("共"+list.get(position).getCount()+"件");
		holder.btn_index.setText(String.valueOf(position+1));
		switch (position%4) {
		case 0:
			holder.btn_index.setBackgroundResource(R.mipmap.type_red_icon);
			break;
		case 1:
			holder.btn_index.setBackgroundResource(R.mipmap.type_yellow_icon);
			break;
		case 2:
			holder.btn_index.setBackgroundResource(R.mipmap.type_blue_icon);
			break;
		case 3:
			holder.btn_index.setBackgroundResource(R.mipmap.type_green_icon);
			break;
		default:
			break;
		}
		return convertView;
	}
	private static class ViewHolder {
		RelativeLayout item_left;
		LinearLayout item_right;
		LinearLayout layout_up;
		TextView id,name,number,tvRightItem;
		Button btn_index;
	}
}