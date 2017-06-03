package com.example.administrator.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.mobileplayer.Adapter.NetVideoAdapter;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.aty.SystemVideoPlayer;
import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.entity.NetVideo;
import com.example.administrator.mobileplayer.util.Constants;
import com.example.administrator.mobileplayer.util.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/5/11.
 */

public class NetVideoPager extends BasePager {
    private ListView lvNetVideo;
    private ProgressBar pbLoadingNetVideo;
    private TextView tvLoadingNetVideo, tvNotFoundNetVideo;
    private ArrayList<NetVideo> netVideos;

    public NetVideoPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.aty_netvideo_pager, null);

        lvNetVideo = (ListView) view.findViewById(R.id.lv_netVideo);
        pbLoadingNetVideo = (ProgressBar) view.findViewById(R.id.pb_loadingNetVideo);
        tvLoadingNetVideo = (TextView) view.findViewById(R.id.tv_loadingNetVideo);
        tvNotFoundNetVideo = (TextView) view.findViewById(R.id.tv_notFoundNetVideo);

        lvNetVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                传递一个具体的视频地址
                NetVideo netVideo = netVideos.get(i);
                Intent intent = new Intent(mContext, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse(netVideo.getData()), "video/*");
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频数据初始化！");

        String url = Constants.NET_URL;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new MyStringCallback());
    }

    private class MyStringCallback extends StringCallback {

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);

        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
            pbLoadingNetVideo.setVisibility(View.GONE);
            tvLoadingNetVideo.setVisibility(View.GONE);
            if (netVideos != null && netVideos.size() > 0) {
                lvNetVideo.setVisibility(View.VISIBLE);
                NetVideoAdapter netVideoAdapter = new NetVideoAdapter(mContext, netVideos);
                lvNetVideo.setAdapter(netVideoAdapter);
            }else {
                tvNotFoundNetVideo.setVisibility(View.VISIBLE);
                tvNotFoundNetVideo.setText("抱歉，没有数据！");
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            tvNotFoundNetVideo.setVisibility(View.VISIBLE);
            tvNotFoundNetVideo.setText("Error");
        }

        @Override
        public void onResponse(String response, int id) {
            parseJson(response);
        }
    }

    private void parseJson(String json) {
        ArrayList<NetVideo> tempNetVideos = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            //即使服务器没有这个字段也不会崩溃，所以不用jsonObject.getJSONArray("trailers");
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if (jsonObjectItem != null) {
                        NetVideo netVideo = new NetVideo();
                        String movieName = jsonObjectItem.optString("movieName");
                        netVideo.setName(movieName);
//                        String videoTitle = jsonObjectItem.optString("videoTitle");
                        String coverImgUrl = jsonObjectItem.optString("coverImg");
                        netVideo.setCoverImgUrl(coverImgUrl);
                        String summary = jsonObjectItem.optString("summary");
                        netVideo.setSummary(summary);
                        String hightUrl = jsonObjectItem.optString("hightUrl");
                        netVideo.setData(hightUrl);

                        tempNetVideos.add(netVideo);
                    }
                }
                netVideos = tempNetVideos;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
