package com.example.administrator.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.util.LogUtil;

/**
 * Created by Administrator on 2017/5/11.
 */

public class NetAudioPager extends BasePager {
    private TextView tv;

    public NetAudioPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音乐页面初始化！");
        tv = new TextView(mContext);
        tv.setTextSize(20);
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("网络音乐");
        LogUtil.e("网络音乐数据初始化！");
    }
}
