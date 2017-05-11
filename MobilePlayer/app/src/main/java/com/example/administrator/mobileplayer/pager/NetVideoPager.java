package com.example.administrator.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.util.LogUtil;

/**
 * Created by Administrator on 2017/5/11.
 */

public class NetVideoPager extends BasePager {
    private TextView tv;

    public NetVideoPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视频页面初始化！");
        tv = new TextView(mcontext);
        tv.setTextSize(20);
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("网络视频");
        LogUtil.e("网络视频数据初始化！");
    }
}
