package com.example.administrator.mobileplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.aty.AtySearch;

/**
 * Created by Administrator on 2017/5/12.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tvTopSearch, ivTopGameBanner, getIvTopHistoryRecordBanner;
    private Context mContext;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        tvTopSearch = findViewById(R.id.tv_topSearch);
        ivTopGameBanner = findViewById(R.id.iv_topbannerGame);
        getIvTopHistoryRecordBanner = findViewById(R.id.iv_topbannerHistoryRecord);

        tvTopSearch.setOnClickListener(this);
        ivTopGameBanner.setOnClickListener(this);
        getIvTopHistoryRecordBanner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_topSearch:
//                Toast.makeText(mContext, "搜索", Toast.LENGTH_SHORT).show();
                AtySearch.actionStart(mContext);
                break;
            case R.id.iv_topbannerGame:
                Toast.makeText(mContext, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_topbannerHistoryRecord:
                Toast.makeText(mContext, "记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
