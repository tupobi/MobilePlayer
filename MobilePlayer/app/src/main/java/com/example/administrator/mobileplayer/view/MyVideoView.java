package com.example.administrator.mobileplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/5/16.
 */

public class MyVideoView extends android.widget.VideoView {

    public MyVideoView(Context context) {
        this(context, null);
    }

    public MyVideoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setVideoSize(int videoWidth, int videoHeight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = videoHeight;
        params.width = videoWidth;
        setLayoutParams(params);
    }
}
