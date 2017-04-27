package com.bapm.bzys.newBzys.interf;

import com.bapm.bzys.newBzys.adapter.OrdersListAdapter;

/**
 * Created by fs-ljh on 2017/4/20.
 */

public interface ImgOrderStaueListenrs {
    void callback(String id, String status,OrdersListAdapter adapters);
}
