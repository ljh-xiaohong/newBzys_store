package com.bapm.bzys.newBzys_store.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.model.AdvertList;
import com.bapm.bzys.newBzys_store.model.Order;
import com.bapm.bzys.newBzys_store.network.DadanUrl;
import com.bapm.bzys.newBzys_store.network.HttpUtil;
import com.bapm.bzys.newBzys_store.network.function.interf.Function;
import com.bapm.bzys.newBzys_store.network.function.interf.FunctionManager;
import com.bapm.bzys.newBzys_store.util.ActivityManager;
import com.bapm.bzys.newBzys_store.util.CustomToast;
import com.bapm.bzys.newBzys_store.util.DadanPreference;
import com.bapm.bzys.newBzys_store.util.GlideUtils;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullListView;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullListViewAdapter;
import com.bapm.bzys.newBzys_store.view.nestlistview.NestFullViewHolder;
import com.bapm.bzys.newBzys_store.widget.DadanArcDialog;
import com.bapm.bzys.newBzys_store.widget.dialog.TipsDialog;
import com.google.gson.Gson;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.TELEPHONY_SERVICE;


public class FragmentOrder extends Fragment implements Function, View.OnClickListener {
    @BindView(R.id.layout_title_bar)
    AutoRelativeLayout layoutTitleBar;
    @BindView(R.id.tools)
    AutoLinearLayout tools;
    @BindView(R.id.tools_scrlllview)
    ScrollView toolsScrlllview;
    @BindView(R.id.zListView)
    NestFullListView zListView;
    @BindView(R.id.scroll_right)
    ScrollView scrollRight;
    @BindView(R.id.srl)
    SwipeRefreshLayout swipeRefreshView;
    @BindView(R.id.btn_top)
    ImageButton btnTop;
    @BindView(R.id.lay_tips)
    AutoRelativeLayout layTips;
    @BindView(R.id.delect_tips)
    ImageButton delectTips;
    @BindView(R.id.choose_layout)
    AutoRelativeLayout chooseLayout;
    Unbinder unbinder;
    private View view;// 需要返回的布局
    private FunctionManager manager;
    private DadanArcDialog loadDialog;
    private int scrollY = 0;// 标记上次滑动位置
    private View contentView;
    /*
    * 左边列表
    * */
    private List<AdvertList> types = new ArrayList<AdvertList>();
    private LayoutInflater inflater;
    private View views[];
    private TextView tv_type_name;
    private TextView tv_type_number;
    private ImageView iv_left;

    /*
    * 右边列表
    * */
//    private OrdersAdapter adapter;
    private List<Order> orders = new ArrayList<Order>();
    private String orderDetailsId;
//    private View orderLayoutPopwindow;

    private int currentList = 0;
    int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {// 优化View减少View的创建次数
            view = inflater.inflate(R.layout.fragment_order, null);
        }
        manager = this.init(this.getContext());
        manager.registFunClass(FragmentOrder.class);
        loadDialog = new DadanArcDialog(getActivity());
        loadDialog.setCancelable(false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        manager.getadvertList(params, this);
    }

    private void initData(Map<String, String> params) {
        loadDialog.show();
        manager.getOrderList(params, this);
    }


    private void initView() {
        setSrollView();
        setRefresh();
        delectTips.setOnClickListener(this);
        btnTop.setOnClickListener(this);
        if (i != 0) {
            zListView.removedCahce();
        }
        zListView.setAdapter(new NestFullListViewAdapter<Order>(R.layout.fragment_order_right_item, orders) {
            @Override
            public void onBind(int pos, Order order, NestFullViewHolder holder) {
                setOrderData(holder, order);
                i = 1;
            }
        });
        loadDialog.dismiss();
    }

    /*
    * 设置下拉刷新
    * */
    private void setRefresh() {
        // 不能在onCreate中设置，这个表示当前是刷新状态，如果一进来就是刷新状态，SwipeRefreshLayout会屏蔽掉下拉事件
        //swipeRefreshLayout.setRefreshing(true);

        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(R.color.edt_hint_orange, R.color.commit_orange);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                // TODO 获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                     Map<String, String> params = new HashMap<String, String>();
//                     params.put("intPromotionID", types.get(currentList).getID() + "");
//                     initData(params);
                        onResume();
                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshView.setRefreshing(false);
                    }
                }, 1200);
            }
        });
    }

    private void setSrollView() {
        if (contentView == null) {
            contentView = scrollRight.getChildAt(0);
        }
        scrollRight.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }


            private void handleStop(Object view) {

                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();

                doOnBorderListener();
            }
        });
    }

    /**
     * ScrollView 的顶部，底部判断：
     * http://blog.csdn.net/qq_21376985
     * <p/>
     * 其中getChildAt表示得到ScrollView的child View， 因为ScrollView只允许一个child
     * view，所以contentView.getMeasuredHeight()表示得到子View的高度,
     * getScrollY()表示得到y轴的滚动距离，getHeight()为scrollView的高度。
     * 当getScrollY()达到最大时加上scrollView的高度就的就等于它内容的高度了啊~
     *
     * @param
     */
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollRight.getScrollY()
                + scrollRight.getHeight()) {
            btnTop.setVisibility(View.VISIBLE);
        }
        // 顶部判断
        else if (scrollRight.getScrollY() < 30) {
            btnTop.setVisibility(View.GONE);
        } else if (scrollRight.getScrollY() > 30) {
            btnTop.setVisibility(View.VISIBLE);
        }

    }

    private void setOrderData(NestFullViewHolder holder, final Order order) {
        holder.setText(R.id.tv_number, order.getNumericalOrder());
        holder.setText(R.id.tv_order_name, order.getConsumerNickName());
        holder.setText(R.id.tv_order_number, "订单号：" + order.getOrderNumber());
        holder.setText(R.id.tv_order_date, order.getCreateTime().replace(" ","\n"));
        holder.setText(R.id.tv_order_counts, "总件数：" + order.getNumbers());
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        holder.setText(R.id.tv_order_money, "¥" + nf.format(order.getPrices()));
        //判断是否为已收款 10为已收款
        if (order.getOrderFormStatus().equals("10")) {
            holder.setVisible(R.id.btn_order_receipt, false);
            holder.setVisible(R.id.img_receipt, true);
        }
        holder.setOnClickListener(R.id.btn_order_receipt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("order.getID()", "order.getID()");
                final JSONObject params = new JSONObject();
                try {
                    params.put("orderId", order.getID());
                    new TipsDialog.Builder(getActivity()).setTitle("提示").setMessage("是否确认为已收款，确认后无法进行修改，请核准后点击确认。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    loadDialog.show();
                                    manager.OrderReceipt(params, FragmentOrder.this);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } catch (JSONException e) {
                }
            }
        });
        ((NestFullListView) holder.getView(R.id.zListView)).setAdapter(new NestFullListViewAdapter<Order.CofdsBean>(R.layout.fragment_order_list_item, order.getCofds()) {
            @Override
            public void onBind(int pos, Order.CofdsBean cofdsBean, NestFullViewHolder holder) {
                setData(order.getOrderFormStatus(), holder, cofdsBean);
            }
        });
    }

    private void setData(final String pos, final NestFullViewHolder holder, final Order.CofdsBean cofdsBean) {
        holder.setText(R.id.tv_order_list_name, cofdsBean.getGoodsName());
        holder.setText(R.id.tv_order_list_id, "编号：" + cofdsBean.getGoodsNo());
        holder.setText(R.id.tv_order_list_number, "数量：" + cofdsBean.getNumber());
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        holder.setText(R.id.tv_order_list_money, nf.format(cofdsBean.getPrice()));
        if (cofdsBean.getNeed() != null && !cofdsBean.getNeed().equals("")) {
            holder.setText(R.id.tv_order_list_remark, "需求：" + cofdsBean.getNeed());
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
            } else {
                holder.setVisible(R.id.img_order_statue, false);
            }
        }
        //加载图片
        if (cofdsBean.getPicThum() == null || cofdsBean.getPicThum().equals("")) {
            GlideUtils.displayNative((ImageView) holder.getView(R.id.img_order_list), R.mipmap.qrcode_default);
        } else {
            GlideUtils.display((ImageView) holder.getView(R.id.img_order_list), cofdsBean.getPicThum());
        }
//        AsyncImageLoader.getInstance(getActivity()).downloadImage(cofdsBean.getPicThum(), (ImageView) holder.getView(R.id.img_order_list), true, new AsyncImageLoader.ImageCallback() {
//            @Override
//            public void onImageLoaded(ImageView imageView, Bitmap bitmap, String imageUrl) {
//                if (bitmap != null) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            }
//        });
        //判断是否收款，未收款才监听
        if (!pos.equals("10")) {
            holder.setOnClickListener(R.id.lay_goods_type_arrow_down, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopwindow();
                    orderDetailsId = cofdsBean.getID();
                    Log.e("OrderFormDetailsStatus", cofdsBean.getOrderFormDetailsStatus());
                }
            });
            holder.getConvertView().setFocusable(true);
            holder.getConvertView().setOnTouchListener(new View.OnTouchListener() {
                int fristX = 0;
                int currentX = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            fristX = (int) event.getX();
                            // 触摸按下时的操作
                            break;
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
//                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "8");
                                        loadDialog.show();
                                        JSONObject params = new JSONObject();
                                        try {
                                            params.put("orderDetailsId", cofdsBean.getID());
                                            params.put("statusId", "8");
                                            manager.OrderStatusChange(params, FragmentOrder.this);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("fristX - currentX > 50", "8--------》" + "fristX - currentX > 50");
                                    }
                                }
                                if (fristX - currentX < -50) {
                                    if (cofdsBean != null) {
//                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "7");
                                        loadDialog.show();
                                        JSONObject params = new JSONObject();
                                        try {
                                            params.put("orderDetailsId", cofdsBean.getID());
                                            params.put("statusId", "7");
                                            manager.OrderStatusChange(params, FragmentOrder.this);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("fristX - currentX > 50", "7--------》" + "fristX - currentX > 50");
                                    }
                                }
                            } else if (cofdsBean.getOrderFormDetailsStatus().equals("8")) {
                                if (fristX - currentX < -50) {
                                    if (cofdsBean != null) {
//                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "6");
                                        loadDialog.show();
                                        JSONObject params = new JSONObject();
                                        try {
                                            params.put("orderDetailsId", cofdsBean.getID());
                                            params.put("statusId", "6");
                                            manager.OrderStatusChange(params, FragmentOrder.this);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("fristX - currentX < -50", "6--------》" + "fristX - currentX < -50");
                                    }
                                }
                            } else if (cofdsBean.getOrderFormDetailsStatus().equals("7")) {
                                if (fristX - currentX > 50) {
                                    if (cofdsBean != null) {
//                                        imgOrderStaueListenr.callback(cofdsBean.getID(), "6");
                                        loadDialog.show();
                                        JSONObject params = new JSONObject();
                                        try {
                                            params.put("orderDetailsId", cofdsBean.getID());
                                            params.put("statusId", "6");
                                            manager.OrderStatusChange(params, FragmentOrder.this);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("fristX - currentX > 50", "6--------》" + "fristX - currentX > 50");
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

    Button btnHandled;
    Button btnRemoved;
    Button btnSelled;
    Button btnCencel;
    private View popupView;
    private PopupWindow window;

    private void initPopwindow() {
        popupView = getActivity().getLayoutInflater().inflate(R.layout.order_layout_popwindow, null);
        window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置动画
        window.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        //设置可以获取焦点
        window.setFocusable(true);
        //设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // 更新popupwindow的状态
        window.update();
        backgroundAlpha(0.6f);
        //添加pop窗口关闭事件
        window.setOnDismissListener(new poponDismissListener());
        // 以下拉的方式显示，并且可以设置显示的位置
        window.showAtLocation(view.findViewById(R.id.choose_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        btnHandled = (Button) popupView.findViewById(R.id.btn_handled);
        btnRemoved = (Button) popupView.findViewById(R.id.btn_removed);
        btnSelled = (Button) popupView.findViewById(R.id.btn_selled);
        btnCencel = (Button) popupView.findViewById(R.id.btn_cencel);
        btnHandled.setOnClickListener(this);
        btnRemoved.setOnClickListener(this);
        btnSelled.setOnClickListener(this);
        btnCencel.setOnClickListener(this);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public FunctionManager init(Context context) {
        return new FunctionManager(context);
    }

    @Override
    public void onSuccess(int requstCode, JSONObject result) {
        loadDialog.dismiss();
        Map<String, String> params = new HashMap<String, String>();
        switch (requstCode) {
            case DadanUrl.USER_LOGIN_AGAIN_REQUEST_CODE:
                if (result.optString("LogionCode").equals("1")) {
                    DadanPreference.getInstance(getActivity()).setTicket(result.optString("Ticket"));
                    onResume();
                } else if (result.optString("LogionCode").equals("-1")) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("LogionCode", "-1");
                    startActivity(intent);
                    ActivityManager.getInstance().finishAllActivity();
                }
                break;
            case DadanUrl.ORDER_STATUS_CHANGE_CODE:
                params.put("intPromotionID", types.get(currentList).getID() + "");
                initData(params);
                CustomToast.showToast(getActivity(), result.optString("Message"));
                break;
            case DadanUrl.ORDER_RECEIPT_CODE:
                params.put("intPromotionID", types.get(currentList).getID() + "");
                initData(params);
                CustomToast.showToast(getActivity(), result.optString("Message"));
                break;
            case DadanUrl.COMMODITY_STATUS_CHANGE_CODE:
                params.put("intPromotionID", types.get(currentList).getID() + "");
                initData(params);
                CustomToast.showToast(getActivity(), result.optString("Message"));
                break;
            default:
                break;

        }
    }

    @Override
    public void onSuccess(int requstCode, JSONArray result) {
        Gson gosn = new Gson();
        switch (requstCode) {
            //获取左列表成功
            case DadanUrl.GET_ADVERT_LIST_CODE:
                try {
                    types.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject adverListJson = result.getJSONObject(i);
                        AdvertList adverList = gosn.fromJson(adverListJson.toString(), AdvertList.class);
                        types.add(adverList);
                    }
                    if (result.length() > 0) {
                        showToolsView(types);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("intPromotionID", types.get(currentList).getID() + "");
                        initData(params);
                    } else {
                        initView();
//                        CustomToast.showToast(getActivity(),"目前没有订单");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
                break;
            //获取右列表
            case DadanUrl.GET_ORDER_LIST_CODE:
                try {
                    orders.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject orderListJson = result.getJSONObject(i);
                        Order orderList = gosn.fromJson(orderListJson.toString(), Order.class);
                        orders.add(orderList);
                    }
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFaile(int requestCode, int status, String msg) {
        loadDialog.dismiss();
        if (requestCode == HttpUtil.ST_ACCOUNT_OTHER_LOGIN_FAILE || requestCode == 233 || requestCode == 232) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("DEVICE_ID", ((TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE)).getDeviceId());
            manager.loginAgain(params, this);
        }
//        CustomToast.makeText(getActivity(), msg, CustomToast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_handled:
                try {
                    window.dismiss();
                    loadDialog.show();
                    JSONObject params = new JSONObject();
                    params.put("orderDetailsId", orderDetailsId);
                    params.put("statusId", "7");
                    manager.OrderStatusChange(params, FragmentOrder.this);
                } catch (JSONException e) {
                }
                break;
            case R.id.btn_removed:
                try {
                    window.dismiss();
                    loadDialog.show();
                    JSONObject params = new JSONObject();
                    params.put("statusId", "8");
                    params.put("orderDetailsId", orderDetailsId);
                    manager.OrderStatusChange(params, FragmentOrder.this);
                } catch (JSONException e) {
                }
                break;
            case R.id.btn_selled:
                try {
                    window.dismiss();
                    loadDialog.show();
                    JSONObject params = new JSONObject();
                    params.put("orderDetailsId", orderDetailsId);
                    manager.CommodityStatusChange(params, FragmentOrder.this);
                } catch (JSONException e) {
                }
                break;
            case R.id.btn_cencel:
                window.dismiss();
                break;
            case R.id.btn_top:
                scrollRight.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollRight.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
//                        scrollRight.fullScroll(ScrollView.FOCUS_UP);//滚动到顶部
//                        需要注意的是，该方法不能直接被调用
//                        因为Android很多函数都是基于消息队列来同步，所以需要一部操作，
//                        addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll， view可能还没有显示出来，所以会失败
//                                应该通过handler在新线程中更新
                        scrollRight.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                btnTop.setVisibility(View.GONE);
                break;
            case R.id.delect_tips:
                layTips.setVisibility(View.GONE);
                delectTips.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 动态生成显示items中的textview
     */
    private void showToolsView(List<AdvertList> types) {
        inflater = LayoutInflater.from(getActivity());
        int size = tools.getChildCount();
        for (int i = 0; i < size; i++) {
            tools.removeViewAt(0);
        }
        views = new View[types.size()];
        for (int i = 0; i < types.size(); i++) {
            View view = inflater.inflate(R.layout.fragment_order_left_item, null);
            view.setTag(i);
            view.setOnClickListener(toolsItemListener);
            tv_type_name = (TextView) view.findViewById(R.id.tv_type_name);
            tv_type_number = (TextView) view.findViewById(R.id.tv_type_number);
            iv_left = (ImageView) view.findViewById(R.id.iv_left);
            tv_type_name.setText(types.get(i).getPromotionName());
            tv_type_number.setText(types.get(i).getPromotionNo() + "");
            tools.addView(view);
            views[i] = view;
        }
        changeTextColor(currentList);
    }

    /**
     * 改变textView的颜色
     */
    private void changeTextColor(int position) {
        if (views.length < 0)
            return;
        for (int i = 0; i < views.length; i++) {
            if (i == position) {
                views[i].setBackgroundResource(R.color.fragment_order_left_item_select_bg);
                ((TextView) views[i].findViewById(R.id.tv_type_name)).setTextColor(getResources().getColor(R.color.red));
                ((TextView) views[i].findViewById(R.id.tv_type_number)).setTextColor(getResources().getColor(R.color.red));
                ((ImageView) views[i].findViewById(R.id.iv_left)).setVisibility(View.VISIBLE);
            } else {
                views[i].setBackgroundResource(R.color.fragment_order_left_item_nor_bg);
                ((TextView) views[i].findViewById(R.id.tv_type_name)).setTextColor(getResources().getColor(R.color.black3));
                ((TextView) views[i].findViewById(R.id.tv_type_number)).setTextColor(getResources().getColor(R.color.black3));
                ((ImageView) views[i].findViewById(R.id.iv_left)).setVisibility(View.GONE);
            }
        }
    }

    private View.OnClickListener toolsItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (Integer) v.getTag();
            currentList = tag;
            changeTextColor(tag);
            Map<String, String> params = new HashMap<String, String>();
            params.put("intPromotionID", types.get(currentList).getID() + "");
            initData(params);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.unregistFunctionClass(FragmentOrder.class);
    }
}
