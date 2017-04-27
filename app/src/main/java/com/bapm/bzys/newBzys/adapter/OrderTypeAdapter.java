package com.bapm.bzys.newBzys.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys.model.OrderType;

import java.util.ArrayList;

public class OrderTypeAdapter extends BaseAdapter {
	// 定义Context
	private LayoutInflater inflater;
	private ArrayList<OrderType> list;
	private Context context;
	private OrderType type;

	public OrderTypeAdapter(Context context, ArrayList<OrderType> list) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		if (list != null && list.size() > 0)
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
		final OrderHold orderHold;
		if (convertView == null) {
			orderHold = new OrderHold();
			convertView = inflater.inflate(R.layout.activity_order_list_type_item,null);
			orderHold.tv_type_no   = (TextView) convertView.findViewById(R.id.tv_type_no);
			orderHold.tv_type_name = (TextView) convertView.findViewById(R.id.tv_type_name);
			convertView.setTag(orderHold);
		} else {
			orderHold = (OrderHold) convertView.getTag();
		}
		if (list != null && list.size() > 0) {
			type = list.get(position);
			orderHold.tv_type_name.setText(type.getTypeName());
			orderHold.tv_type_no.setText(String.valueOf(position+1));
		}

		return convertView;
	}

	private class OrderHold {
		private TextView tv_type_no;
		private TextView tv_type_name;
	}
}
