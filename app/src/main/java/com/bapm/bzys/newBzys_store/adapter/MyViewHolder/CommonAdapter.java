package com.bapm.bzys.newBzys_store.adapter.MyViewHolder;

import android.widget.BaseAdapter;

/**
 * Created by fs-ljh on 2017/4/24.
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public abstract class CommonAdapter<T> extends BaseAdapter
{
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId)
    {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public T getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MyViewHolder MyViewHolder = getMyViewHolder(position, convertView,
                parent);
        convert(MyViewHolder, getItem(position));
        return MyViewHolder.getConvertView();

    }

    public abstract void convert(MyViewHolder helper, T item);

    private MyViewHolder getMyViewHolder(int position, View convertView,
                                     ViewGroup parent)
    {
        return MyViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }


}
