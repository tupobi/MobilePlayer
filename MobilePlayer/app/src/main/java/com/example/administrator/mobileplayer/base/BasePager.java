package com.example.administrator.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/5/11.
 */

public abstract class BasePager {

    public final Context mContext;
    public View rootView;
    public boolean isInit;

    public BasePager(Context mContext) {
        this.mContext = mContext;
        this.rootView = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
