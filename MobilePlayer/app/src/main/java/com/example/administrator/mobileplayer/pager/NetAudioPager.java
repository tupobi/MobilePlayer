package com.example.administrator.mobileplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.mobileplayer.Adapter.NetAudioAdapter;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.entity.NetAudioBean;
import com.example.administrator.mobileplayer.util.CacheUtils;
import com.example.administrator.mobileplayer.util.Constants;
import com.example.administrator.mobileplayer.util.LogUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.thirdparty.V;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/5/11.
 */

public class NetAudioPager extends BasePager {
    private ListView lvNetAudio;
    private TextView tvNotFoundNetAudio;
    private TextView tvLoadingNetAudio;
    private ProgressBar pbLoadingNetAudio;
    private List<NetAudioBean.ListBean> netAudioBeans;
    private NetAudioAdapter netAudioAdapter;

    public NetAudioPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.aty_netaudio_pager, null);
        lvNetAudio = (ListView) view.findViewById(R.id.lv_netAudio);
        tvNotFoundNetAudio = (TextView) view.findViewById(R.id.tv_notFoundNetAudio);
        tvLoadingNetAudio = (TextView) view.findViewById(R.id.tv_loadingNetAudio);
        pbLoadingNetAudio = (ProgressBar) view.findViewById(R.id.pb_loadingNetAudio);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐数据初始化！");

        getDataFromNet();
    }

    private void getDataFromNet() {

        String url = Constants.ALL_RES_URL;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new MyStringCallback());

        String netAudioJsonData = CacheUtils.getNetAudioJsonData(mContext, CacheUtils.NET_AUDIO_JSON_DATA);
        LogUtil.e("netAudioJsonData:" + netAudioJsonData);
        processNetAudioJsonData(netAudioJsonData);

    }

    private void processNetAudioJsonData(String netAudioJsonData) {
        NetAudioBean netAudioBean = parseNetAudioJsonData(netAudioJsonData);
        netAudioBeans = netAudioBean.getList();
        if (netAudioBeans != null && netAudioBeans.size() > 0) {
            lvNetAudio.setVisibility(View.VISIBLE);
            netAudioAdapter = new NetAudioAdapter(mContext, netAudioBeans);
            lvNetAudio.setAdapter(netAudioAdapter);
        }else {
            tvNotFoundNetAudio.setVisibility(View.VISIBLE);
            tvNotFoundNetAudio.setText("没有数据...");
        }
    }

    private NetAudioBean parseNetAudioJsonData(String netAudioJsonData) {
        return new Gson().fromJson(netAudioJsonData, NetAudioBean.class);
    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
            tvLoadingNetAudio.setVisibility(View.GONE);
            pbLoadingNetAudio.setVisibility(View.GONE);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            tvNotFoundNetAudio.setVisibility(View.VISIBLE);
            tvNotFoundNetAudio.setText("请求数据错误！");
        }

        @Override
        public void onResponse(String response, int id) {
            CacheUtils.putNetAudioJsonData(mContext, CacheUtils.NET_AUDIO_JSON_DATA, response);
        }
    }
}
