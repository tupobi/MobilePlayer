package com.example.administrator.mobileplayer.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.administrator.mobileplayer.R;

/**
 * Created by Administrator on 2017/5/11.
 */

public class AtyMain extends FragmentActivity {
    private RadioGroup rgBottomTag;
    private FrameLayout flMainContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.aty_main);
        rgBottomTag = (RadioGroup) findViewById(R.id.rg_bottomTag);
        flMainContent = (FrameLayout) findViewById(R.id.fl_mainContent);
        rgBottomTag.check(R.id.rb_localVideo);
    }
}
