package com.bapm.bzys.newBzys_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bapm.bzys.newBzys.R;
import com.bapm.bzys.newBzys_store.adapter.ViewPagerAdapter;
import com.bapm.bzys.newBzys_store.base.BaseActivity;
import com.bapm.bzys.newBzys_store.util.DadanPreference;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by fs-ljh on 2017/4/25.
 */

public class FristActivity extends BaseActivity {
    //播放gif
    private GifImageView test_gif;

/*
*
* 引导页
*
* */
    private ViewPager vp;

    //把定义好的三个布局进行初始化对象
    private View  item_view01,item_view02,item_view03;
    //创建一个list集合 参数为view

    private List<View> Mview = new ArrayList<>();

    //用于引用布局好的三个itemView布局
    private LayoutInflater inflater;


    private ViewPagerAdapter adapter;

    //定义一个点集合
    private List<View> dots;

    private int oldPosition = 0;// 记录上一次点的位置

    private int currentItem; // 当前页面

    TextView btnStart;

    ImageView imgStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frist);
        imgStart= (ImageView) findViewById(R.id.img_start);
        if (DadanPreference.getInstance(this).getString("isFrist").equals("")){
            handler.sendEmptyMessageDelayed(0,3000);//设置3秒后加载ViewPager
            initViewPager();//加载ViewPager
        }else{
            handler.sendEmptyMessageDelayed(0,3000);
            //判断是是否登录
            if(DadanPreference.getInstance(this).getTicket().equals("-1")){
                Intent intent=new Intent(FristActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }else {
                test_gif = (GifImageView) findViewById(R.id.test_gif);
                test_gif.setVisibility(View.VISIBLE);
                //设置图片数据
                test_gif.setImageResource(R.drawable.logined);
                final android.widget.MediaController mediaController = new android.widget.MediaController(this);
                mediaController.setMediaPlayer((GifDrawable) test_gif.getDrawable());
                mediaController.show();
                handler.sendEmptyMessageDelayed(1, 5000);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                imgStart.setVisibility(View.GONE);
            }else{
                Intent intent=new Intent(FristActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            super.handleMessage(msg);
        }
    };
    private void initViewPager() {
        inflater = getLayoutInflater();
        //初始化viewPager
        vp = (ViewPager)findViewById(R.id.viewp_01);

        //把这三个点的ID找到并添加到list集合中，统一管理；下面是简写。你也可以创建三个对象，添加到集合中
//        dots = new ArrayList<View>();
//        dots.add(findViewById(R.id.dot_1));
//        dots.add(findViewById(R.id.dot_2));
//        dots.add(findViewById(R.id.dot_3));
//
//        //并且，默认第一个是选中状态
//        dots.get(0).setBackgroundResource(R.drawable.dot_focused);
//
        item_view01 = inflater.inflate(R.layout.item01,null);
        item_view02 = inflater.inflate(R.layout.item02,null);
        item_view03 = inflater.inflate(R.layout.item03,null);
        //把三个View布局对象加载到list中，这些就是item的数据
        Mview.add(item_view01);
        Mview.add(item_view02);
        Mview.add(item_view03);
        btnStart= (TextView) item_view03.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DadanPreference.getInstance(FristActivity.this).setString("isFrist","no");
                Intent intent=new Intent(FristActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//            @Override
//            public void onPageSelected(int position) {
//
//                //下面就是获取上一个位置，并且把点的状体设置成默认状体
//                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
//                //获取到选中页面对应的点，设置为选中状态
//                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//                //下面是记录本次的位置，因为在滑动，他就会变成过时的点了
//                oldPosition = position;
//                //关联页卡
//                currentItem = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        //把数据传递给适配器中，进行数据处理。
        adapter = new ViewPagerAdapter(this,Mview);
        vp.setAdapter(adapter);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           this.finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
