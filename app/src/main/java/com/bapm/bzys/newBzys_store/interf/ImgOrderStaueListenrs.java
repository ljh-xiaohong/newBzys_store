package com.bapm.bzys.newBzys_store.interf;

import com.bapm.bzys.newBzys_store.adapter.OrdersListAdapter;

/**
 * Created by fs-ljh on 2017/4/20.
 */

public interface ImgOrderStaueListenrs {
    void callback(String id, String status,OrdersListAdapter adapters);
}
