package com.bapm.bzys.newBzys_store.fragment;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.adapter.OrderTypeAdapter;
import com.bapm.bzys.newBzys_store.model.OrderType;


public class FragmentOrderDetail extends Fragment {
	private ArrayList<OrderType> list;
	private GridView listView;
	private OrderTypeAdapter orderTypeAdapter;
	private OrderType orderType;
	private ProgressBar progressBar;
	private String typename;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_order_type, null);
		progressBar=(ProgressBar) view.findViewById(R.id.progressBar);
		listView = (GridView) view.findViewById(R.id.listView);
		typename=getArguments().getString("typename");
		((TextView)view.findViewById(R.id.toptype)).setText(typename);
		GetTypeList();
		orderTypeAdapter=new OrderTypeAdapter(getActivity(), list);
		listView.setAdapter(orderTypeAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				
			}
		});
		
		return view;
	}
	private void GetTypeList() {
		list=new ArrayList<OrderType>();
		for(int i=1;i<35;i++){
			orderType=new OrderType(String.valueOf(i), typename);
			list.add(orderType);
		}	
		progressBar.setVisibility(View.GONE);
	}
}
