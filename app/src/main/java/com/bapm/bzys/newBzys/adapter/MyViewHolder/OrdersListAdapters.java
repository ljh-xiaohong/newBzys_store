package com.bapm.bzys.newBzys.adapter.MyViewHolder;


import android.content.Context;

import com.bapm.bzys.newBzys.model.Order;

import java.util.List;

/**
 * 内订单列表设配器
 * Created by fs-ljh on 2017/4/14.
 */

public class OrdersListAdapters extends CommonAdapter<Order.CofdsBean> {
    public OrdersListAdapters(Context context, List<Order.CofdsBean> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(MyViewHolder helper, Order.CofdsBean item) {

//        helper.setText(cofds.getGoodsName());
//                view.tvOrderListName.setText();
//        view.tvOrderListId.setText(cofds.getGoodsNo());
//        view.tvOrderListNumber.setText(cofds.getNumber());
//        view.tvOrderListMoney.setText(cofds.getPrice());
//        if (cofds.getNeed() != null && !cofds.getNeed().equals("")) {
//            view.tvOrderListRemark.setText(cofds.getNeed());
//        }
    }

    // 定义Context

}
