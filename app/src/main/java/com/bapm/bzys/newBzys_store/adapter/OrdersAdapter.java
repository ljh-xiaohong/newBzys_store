package com.bapm.bzys.newBzys_store.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.interf.ImgOrderStaueListenr;
import com.bapm.bzys.newBzys_store.interf.TvOrderStaueListenr;
import com.bapm.bzys.newBzys_store.model.Order;
import com.bapm.bzys.newBzys_store.util.AsyncImageLoader;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullListView;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullListViewAdapter;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullViewHolder;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 外订单列表设配器
 * Created by fs-ljh on 2017/4/14.
 */

public class OrdersAdapter extends BaseAdapter {
    // 定义Context
    private LayoutInflater mInflater;
    private List<Order> list;
    private Context context;
    private ImgOrderStaueListenr imgOrderStaueListenr;
    private TvOrderStaueListenr tvOrderStaueListenr;
    Order goods;

    public OrdersAdapter(Context context, List<Order> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder view;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_order_right_item, null);
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        if (list != null && list.size() > 0) {
            goods = list.get(position);
            view.tvNumber.setText(goods.getNumericalOrder());
            view.tvOrderName.setText(goods.getConsumerNickName());
            view.tvOrderNumber.setText(goods.getOrderNumber());
            view.tvOrderDate.setText(goods.getCreateTime());
            view.tvOrderCounts.setText(goods.getNumbers());
//            view.tvOrderMoney.setText(goods.getPrices());
            //判断是否为已收款 10为已收款
            if (goods.getOrderFormStatus().equals("10")) {
                view.btnOrderReceipt.setVisibility(View.GONE);
                view.imgReceipt.setVisibility(View.VISIBLE);
            }
            view.btnOrderReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvOrderStaueListenr.callback(goods.getID());
                }
            });

            view.zListView.setAdapter(new NestFullListViewAdapter<Order.CofdsBean>(R.layout.fragment_order_list_item, goods.getCofds()) {
                @Override
                public void onBind(int pos, Order.CofdsBean cofdsBean, NestFullViewHolder holder) {
                    setData(holder, cofdsBean);
                }
            });

//            view.zListView.setAdapter(adapter);
//adapter.setAttentionClickListener(new ImgOrderStaueListenr() {
//    @Override
//    public void callback(String id, String status) {
//        imgOrderStaueListenrs.callback(id, status,adapter);
//    }
//});
        }
        return convertView;
    }

    private void setData(NestFullViewHolder holder, final Order.CofdsBean cofdsBean) {
        holder.setText(R.id.tv_order_list_name, cofdsBean.getGoodsName());
        holder.setText(R.id.tv_order_list_id, cofdsBean.getGoodsNo());
        holder.setText(R.id.tv_order_list_number, cofdsBean.getNumber());
//        holder.setText(R.id.tv_order_list_money,cofdsBean.getPrice());
        if (cofdsBean.getNeed() != null && !cofdsBean.getNeed().equals("")) {
            holder.setText(R.id.tv_order_list_remark, cofdsBean.getNeed());
        }
        if (cofdsBean.getOrderFormDetailsStatus() != null && !cofdsBean.getOrderFormDetailsStatus().equals("0")) {
            if (cofdsBean.getOrderFormDetailsStatus().equals("7")) {
                holder.setImageResource(R.id.img_order_staue, R.mipmap.handled);
            } else if (cofdsBean.getOrderFormDetailsStatus().equals("8")) {
                holder.setImageResource(R.id.img_order_staue, R.mipmap.removed);
            } else {
                holder.setVisible(R.id.img_order_staue, false);
            }
            if (cofdsBean.getGoodsSalesFlag().equals("3")) {
                holder.setVisible(R.id.img_order_statue, true);
            }
        }
        //加载图片
        AsyncImageLoader.getInstance(context).downloadImage(cofdsBean.getPicThum(), (ImageView) holder.getView(R.id.img_order_list), true, new AsyncImageLoader.ImageCallback() {
            @Override
            public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        //判断是否收款，未收款才监听
        if (!goods.getOrderFormStatus().equals("10")) {
            holder.setOnClickListener(R.id.iv_goods_type_arrow_down, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgOrderStaueListenr.callback(cofdsBean.getID(), "3");
                }
            });
            holder.setOnTouchListener(R.id.order_layout, new View.OnTouchListener() {
                int fristX = 0;
                int currentX = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            fristX = (int) event.getX();
                            // 触摸按下时的操作
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            // 触摸移动时的操作
                            break;
                        case MotionEvent.ACTION_UP:
                            // 触摸抬起时的操作
                            currentX = (int) event.getX();
                               /*
                               * 判断初始状态，如果是6未处理，左滑是撤单，右滑是处理
                               * 如果是8已撤单，则向左滑不回调，右滑回调
                               * 如果是7已处理，则向右滑不回调，左滑回调
                               * */
                            if (cofdsBean.getOrderFormDetailsStatus().equals("6")) {
                                if (fristX - currentX > 50) {
                                    if (cofdsBean != null) {
                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "8");
                                    }
                                }
                                if (fristX - currentX < -50) {
                                    if (cofdsBean != null) {
                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "7");
                                    }
                                }
                            } else if (cofdsBean.getOrderFormDetailsStatus().equals("8")) {
                                if (fristX - currentX < -50) {
                                    if (cofdsBean != null) {
                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "6");
                                    }
                                }
                            } else if (cofdsBean.getOrderFormDetailsStatus().equals("7")) {
                                if (fristX - currentX > 50) {
                                    if (cofdsBean != null) {
                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "6");
                                    }
                                }
                            }

                            break;
                    }
                    return true;
                }
            });
        }
    }

    public void setAttentionClickListener(ImgOrderStaueListenr imgOrderStaueListenr) {
        this.imgOrderStaueListenr = imgOrderStaueListenr;
    }

    public void setTvOrderStaueListenrClickListener(TvOrderStaueListenr tvOrderStaueListenr) {
        this.tvOrderStaueListenr = tvOrderStaueListenr;

    }


    static class ViewHolder {
        @BindView(R.id.img_order_number)
        ImageView imgOrderNumber;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_order_name)
        TextView tvOrderName;
        @BindView(R.id.tv_order_number)
        TextView tvOrderNumber;
        @BindView(R.id.btn_order_receipt)
        TextView btnOrderReceipt;
        @BindView(R.id.title_layout)
        AutoRelativeLayout titleLayout;
        @BindView(R.id.zListView)
        NestFullListView zListView;
        @BindView(R.id.img_receipt)
        ImageView imgReceipt;
        @BindView(R.id.tv_order_date)
        TextView tvOrderDate;
        @BindView(R.id.tv_order_money)
        TextView tvOrderMoney;
        @BindView(R.id.tv_order_counts)
        TextView tvOrderCounts;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
