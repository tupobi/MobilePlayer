package com.example.administrator.mobileplayer.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.NetVideo;
import com.example.administrator.mobileplayer.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class NetVideoAdapter extends BaseAdapter {
    private Context mContext;
    private List<NetVideo> mNetVideos;

    public NetVideoAdapter(Context context, List<NetVideo> NetVideos) {
        mNetVideos = NetVideos;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mNetVideos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.listitem_net_video, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_videoDefaultIcon = (ImageView) view.findViewById(R.id.iv_videoDefaultIcon);
            viewHolder.tv_NetVideoName = (TextView) view.findViewById(R.id.tv_NetVideoName);
            viewHolder.tv_NetVideoSummary = (TextView) view.findViewById(R.id.tv_netVideSummary);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        NetVideo netVideo = mNetVideos.get(i);
        viewHolder.tv_NetVideoName.setText(netVideo.getName());
        viewHolder.tv_NetVideoSummary.setText(netVideo.getSummary());
        Glide.with(mContext).load(netVideo.getCoverImgUrl()).placeholder(R.drawable.video_default_icon).centerCrop().into(viewHolder.iv_videoDefaultIcon);

        return view;
    }

    static class ViewHolder {
        ImageView iv_videoDefaultIcon;
        TextView tv_NetVideoName, tv_NetVideoSummary;
    }
}
