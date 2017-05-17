package com.bapm.bzys.newBzys_store.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.interf.ImgOrderStaueListenr;
import com.bapm.bzys.newBzys_store.model.Order;
import com.bapm.bzys.newBzys_store.util.AsyncImageLoader;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 内订单列表设配器
 * Created by fs-ljh on 2017/4/14.
 */

public class OrdersListAdapter extends BaseAdapter {
    // 定义Context
    private LayoutInflater mInflater;
    private List<Order.CofdsBean> list;
    private Context context;
    private String status;
    private ViewDragHelper mDragger;
    private ImgOrderStaueListenr imgOrderStaueListenr;
    Order.CofdsBean cofds;

    public OrdersListAdapter(Context context, List<Order.CofdsBean> list, String status) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.status = status;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder view;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_order_list_item, null);
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
//        initScroll();

        if (list != null && list.size() > 0) {
            cofds = list.get(position);
            view.tvOrderListName.setText(cofds.getGoodsName());
            view.tvOrderListId.setText(cofds.getGoodsNo());
            view.tvOrderListNumber.setText(cofds.getNumber());
//            view.tvOrderListMoney.setText(cofds.getPrice());
            if (cofds.getNeed() != null && !cofds.getNeed().equals("")) {
                view.tvOrderListRemark.setText(cofds.getNeed());
            }
            if (cofds.getOrderFormDetailsStatus() != null && !cofds.getOrderFormDetailsStatus().equals("0")) {
                if (cofds.getOrderFormDetailsStatus().equals("7")) {
                    view.imgOrderStaue.setImageResource(R.mipmap.handled);
                } else if (cofds.getOrderFormDetailsStatus().equals("8")) {
                    view.imgOrderStaue.setImageResource(R.mipmap.removed);
                } else {
                    view.imgOrderStaue.setVisibility(View.GONE);
                }
                if (cofds.getGoodsSalesFlag().equals("3")) {
                    view.imgOrderStatue.setVisibility(View.VISIBLE);
                }
            }
            //加载图片
            AsyncImageLoader.getInstance(context).downloadImage(cofds.getPicThum(), view.imgOrderList, true, new AsyncImageLoader.ImageCallback() {
                @Override
                public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
            //判断是否收款，未收款才监听

            if (!status.equals("10")) {
                view.ivGoodsTypeArrowDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgOrderStaueListenr.callback(cofds.getID(), "3");
                    }
                });

//                parent.setOnTouchListener(new View.OnTouchListener() {
//                    int fristX = 0;
//                    int currentX = 0;
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        switch (event.getAction()) {
//
//                            case MotionEvent.ACTION_DOWN:
//                                fristX = (int) event.getX();
//                                // 触摸按下时的操作
//                                break;
//                            case MotionEvent.ACTION_MOVE:
//                                // 触摸移动时的操作
//                               break;
//                            case MotionEvent.ACTION_UP:
//                                // 触摸抬起时的操作
//                                currentX = (int) event.getX();
//                               /*
//                               * 判断初始状态，如果是6未处理，左滑是撤单，右滑是处理
//                               * 如果是8已撤单，则向左滑不回调，右滑回调
//                               * 如果是7已处理，则向右滑不回调，左滑回调
//                               * */
//                                if (cofds.getOrderFormDetailsStatus().equals("6")) {
//                                    if (fristX - currentX > 50) {
//                                        if (cofds != null) {
//                                            imgOrderStaueListenr.callback(cofds.getID(), "8");
//                                        }
//                                    }
//                                    if (fristX - currentX < -50) {
//                                        if (cofds != null) {
//                                            imgOrderStaueListenr.callback(cofds.getID(), "7");
//                                        }
//                                    }
//                                } else if (cofds.getOrderFormDetailsStatus().equals("8")) {
//                                    if (fristX - currentX < -50) {
//                                        if (cofds != null) {
//                                            imgOrderStaueListenr.callback(cofds.getID(), "6");
//                                        }
//                                    }
//                                } else if (cofds.getOrderFormDetailsStatus().equals("7")) {
//                                    if (fristX - currentX > 50) {
//                                        if (cofds != null) {
//                                            imgOrderStaueListenr.callback(cofds.getID(), "6");
//                                        }
//                                    }
//                                }
//
//                                break;
//                        }
//                        return true;
//                    }
//                });
                notifyDataSetChanged();
            }
        }
        return convertView;
    }

    public void setAttentionClickListener(ImgOrderStaueListenr imgOrderStaueListenr) {
        this.imgOrderStaueListenr = imgOrderStaueListenr;

    }

    private void initScroll(final ViewGroup parent) {
        mDragger = ViewDragHelper.create(parent, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == parent.getChildAt(0);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return child.getTop();
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //mAutoBackView手指释放时可以自动回去
//                if (releasedChild == mDragView&&(dis<50&&dis>-50))
//                {
                mDragger.settleCapturedViewAt(parent.getLeft(), parent.getTop());
                parent.invalidate();
//                }else{
//                    ImageView img= (ImageView) releasedChild.;
//                   if (dis>-50){
//                       img.setImageResource(R.mipmap.removed);
//                       mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
//                       invalidate();
//                   }else{
//                       img.setImageResource(R.mipmap.handled);
//                       mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
//                       invalidate();
//                   }

//                }
            }
        });
    }


    static class ViewHolder {
        @BindView(R.id.img_order_list)
        ImageView imgOrderList;
        @BindView(R.id.img_order_staue)
        ImageView imgOrderStaue;
        @BindView(R.id.tv_order_list_name)
        TextView tvOrderListName;
        @BindView(R.id.iv_goods_type_arrow_down)
        ImageView ivGoodsTypeArrowDown;
        @BindView(R.id.lay_goods_type_arrow_down)
        AutoLinearLayout layGoodsTypeArrowDown;
        @BindView(R.id.tv_order_list_id)
        TextView tvOrderListId;
        @BindView(R.id.tv_order_list_number)
        TextView tvOrderListNumber;
        @BindView(R.id.tv_order_list_money)
        TextView tvOrderListMoney;
        @BindView(R.id.tv_order_list_remark)
        TextView tvOrderListRemark;
        @BindView(R.id.img_order_statue)
        ImageView imgOrderStatue;
        @BindView(R.id.order_layout)
        AutoRelativeLayout orderLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
