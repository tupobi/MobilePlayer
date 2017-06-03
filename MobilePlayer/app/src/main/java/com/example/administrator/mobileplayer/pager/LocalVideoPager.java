package com.example.administrator.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.mobileplayer.Adapter.LocalVideoAdapter;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.aty.SystemVideoPlayer;
import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.entity.LocalVideo;
import com.example.administrator.mobileplayer.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/11.
 */

public class LocalVideoPager extends BasePager {
    private ListView lvLocalVideo;
    private TextView tvLoadingLocalVideo, tvNotFoundLocalVideo;
    private ProgressBar pbLoadingLocalVideo;
    private ArrayList<LocalVideo> localVideos;
    private LocalVideoAdapter localVideoAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pbLoadingLocalVideo.setVisibility(View.GONE);
            tvLoadingLocalVideo.setVisibility(View.GONE);
            if (localVideos != null && localVideos.size() > 0) {
                //有数据
                //设置适配器
                lvLocalVideo.setVisibility(View.VISIBLE);
                localVideoAdapter = new LocalVideoAdapter(mContext, localVideos);
                lvLocalVideo.setAdapter(localVideoAdapter);
                //progressBar隐藏
            } else {
                //没有数据
                //progressBar隐藏
                tvNotFoundLocalVideo.setVisibility(View.VISIBLE);
                //无数据文本显示
            }
        }
    };

    public LocalVideoPager(Context mcontext) {
        super(mcontext);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.aty_localvideo_pager, null);
        lvLocalVideo = (ListView) view.findViewById(R.id.lv_localVideo);
        tvLoadingLocalVideo = (TextView) view.findViewById(R.id.tv_loadingLocalVideo);
        tvNotFoundLocalVideo = (TextView) view.findViewById(R.id.tv_notFoundLocalVideo);
        pbLoadingLocalVideo = (ProgressBar) view.findViewById(R.id.pb_loadingLocalVideo);

        lvLocalVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                LocalVideo localVideo = localVideos.get(i);
                //隐式，调用所有匹配的播放器。
//                Intent intent = new Intent();
//                intent.setDataAndType(Uri.parse(localVideo.getData()), "video/*");
//                mContext.startActivity(intent);

                //显式，调用自定义播放器。
                //传递一个具体的视频地址
//                Intent intent = new Intent(mContext, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(localVideo.getData()), "video/*");
//                mContext.startActivity(intent);

                //传递列表视频,需要序列化，不然崩溃。
                Intent intent = new Intent(mContext, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(localVideo.getData()), "video/*");
                //传递当前点击视频项
                Bundle bundle = new Bundle();
                bundle.putSerializable("localVideos", localVideos);
//                putSerializable参数可以为ArrayList<>类型，不能为List类型，需要强转
                intent.putExtras(bundle);
//                bundle也要加入到intent中去。
                intent.putExtra("position", i);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("-->本地视频数据初始化！");
        getVideoFromLocal();
    }

    /**
     * 从内容提供者里获取视频
     * 如果是6.0系统，动态获取读取sdcard权限。
     */
    private void getVideoFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isGrantExternalRW((Activity) mContext)) {
                    SystemClock.sleep(1 * 1000);
                    ContentResolver contentResolver = mContext.getContentResolver();
                    Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    String[] objs = {
                            MediaStore.Video.Media.DISPLAY_NAME,//视频名
                            MediaStore.Video.Media.DURATION,//时长
                            MediaStore.Video.Media.SIZE,//大小
                            MediaStore.Video.Media.DATA,//视频的播放地址
                            MediaStore.Video.Media.ARTIST//艺术家
                    };
                    Cursor cursor = contentResolver.query(uri, objs, null, null, null);
                    if (cursor != null) {
                        localVideos = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            localVideos.add(new LocalVideo(cursor.getString(0), cursor.getLong(1), cursor.getLong(2),
                                    cursor.getString(3), cursor.getString(4)));
                        }
                        cursor.close();
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();//不要忘记启动子线程
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }
}
