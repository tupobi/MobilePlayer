package com.example.administrator.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.util.LogUtil;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2017/5/11.
 */

public class LocalVideoPager extends BasePager {
    private TextView tv;

    public LocalVideoPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        LogUtil.e("本地视频页面初始化！");
        tv = new TextView(mcontext);
        tv.setTextSize(20);
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("本地视频");
        LogUtil.e("本地视频数据初始化！");
    }
}
