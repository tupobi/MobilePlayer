package com.example.administrator.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/5/11.
 */

public abstract class BasePager {

    public final Context mcontext;
    private View rootView;

    public BasePager(Context mcontext) {
        this.mcontext = mcontext;
        this.rootView = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
