package com.bapm.bzys.newBzys.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.model.AccountChild;
import com.bapm.bzys.newBzys.widget.ZrcListView;

import java.util.List;

public class AccountChildAdapter extends BaseAdapter {

	// 定义Context
	private LayoutInflater inflater;
	private List<AccountChild> list;
	private Context context;

	public AccountChildAdapter(Context context, List<AccountChild> childs) {
		this.list = childs;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_account_child_list_item, null, false);
			holder = new ViewHolder();
			holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);
			holder.tvRightItem = (TextView) convertView.findViewById(R.id.tvRightItem);
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
		holder.phone.setText(list.get(position).getPhone());
//		holder.tvRightItem.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				listView.hiddenRight(listView.mPreItemView);
//				if (myHandler != null) {
//					Message msg = myHandler.obtainMessage();
//					msg.arg1 = 0;
//					msg.arg2 = position;
//					myHandler.sendMessageDelayed(msg, 300);
//				}
//			}
//		});
		return convertView;
	}
	private static class ViewHolder {
		RelativeLayout item_left;
		RelativeLayout item_right;
		TextView id,name,phone,tvRightItem;
	}
}