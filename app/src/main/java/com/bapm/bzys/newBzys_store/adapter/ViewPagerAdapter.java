package com.bapm.bzys.newBzys_store.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by fs-ljh on 2017/4/25.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<View> viewdata;

    public ViewPagerAdapter(Context mainActivity, List<View> mview) {
        context = mainActivity;
        viewdata = mview;
    }

    //这个方法是获取一共有多少个item
    @Override
    public int getCount() {
        return viewdata.size();
    }

    //这个就这样写就OK ，无需管
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    //这个方法用来实例化页卡
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewdata.get(position),0);
        return viewdata.get(position);
    }

    //删除实例化页卡
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        // TODO Auto-generated method stub
        container.removeView(viewdata.get(position));
    }


}
