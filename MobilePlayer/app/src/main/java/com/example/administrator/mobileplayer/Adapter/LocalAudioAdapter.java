package com.example.administrator.mobileplayer.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.LocalAudio;
import com.example.administrator.mobileplayer.entity.LocalVideo;
import com.example.administrator.mobileplayer.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class LocalAudioAdapter extends BaseAdapter {
    private Context mContext;
    private List<LocalAudio> mLocalAudios;

    public LocalAudioAdapter(Context context, List<LocalAudio> localAudios) {
        mLocalAudios = localAudios;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mLocalAudios.size();
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
            view = View.inflate(mContext, R.layout.listitem_local_video, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_videoDefaultIcon = (ImageView) view.findViewById(R.id.iv_videoDefaultIcon);
            viewHolder.tv_localVideoName = (TextView) view.findViewById(R.id.tv_localVideoName);
            viewHolder.tv_localVideoSize = (TextView) view.findViewById(R.id.tv_localVideoSize);
            viewHolder.tv_localVideoDuration = (TextView) view.findViewById(R.id.tv_localVideoDuration);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        LocalAudio localAudio = mLocalAudios.get(i);
        viewHolder.tv_localVideoName.setText(localAudio.getName());
        DateUtil dateUtil = new DateUtil();
        viewHolder.tv_localVideoDuration.setText(dateUtil.stringForTime((int) localAudio.getDuration()));
        viewHolder.tv_localVideoSize.setText(Formatter.formatFileSize(mContext, localAudio.getSize()));
        viewHolder.iv_videoDefaultIcon.setImageResource(R.drawable.music_default_bg);
        return view;
    }

    static class ViewHolder {
        ImageView iv_videoDefaultIcon;
        TextView tv_localVideoName, tv_localVideoDuration, tv_localVideoSize;
    }
}
