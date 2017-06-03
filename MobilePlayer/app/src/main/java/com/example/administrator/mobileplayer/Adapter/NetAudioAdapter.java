package com.example.administrator.mobileplayer.Adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.NetAudioBean;
import com.example.administrator.mobileplayer.util.DateUtil;
import com.example.administrator.mobileplayer.util.DensityUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/5/13.
 */

public class NetAudioAdapter extends BaseAdapter {

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    private DateUtil dateUtil;
    private Context mContext;
    private List<NetAudioBean.ListBean> mNetAudioBeans;


    public NetAudioAdapter(Context context, List<NetAudioBean.ListBean> netAudioBeans) {
        this.mNetAudioBeans = netAudioBeans;
        mContext = context;
        dateUtil = new DateUtil();
        initUniversalImageLoader();
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        NetAudioBean.ListBean listBean = mNetAudioBeans.get(position);
        String type = listBean.getType();
        int itemViewType = -1;
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广告
        }
        return itemViewType;

    }

    @Override
    public int getCount() {
        return mNetAudioBeans.size();
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
        int itemViewType = getItemViewType(i);

        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();

            switch (itemViewType) {
                case TYPE_VIDEO://视频
                    view = View.inflate(mContext, R.layout.all_video_item, null);
                    viewHolder.tv_play_nums = (TextView) view.findViewById(R.id.tv_play_nums);
                    viewHolder.tv_video_duration = (TextView) view.findViewById(R.id.tv_video_duration);
                    viewHolder.iv_commant = (ImageView) view.findViewById(R.id.iv_commant);
                    viewHolder.tv_commant_context = (TextView) view.findViewById(R.id.tv_commant_context);
                    viewHolder.jcv_videoplayer = (JCVideoPlayer) view.findViewById(R.id.jcv_videoplayer);

                    break;
                case TYPE_IMAGE://图片
                    view = View.inflate(mContext, R.layout.all_image_item, null);
                    viewHolder.iv_image_icon = (ImageView) view.findViewById(R.id.iv_image_icon);
                    break;
                case TYPE_TEXT://文字
                    view = View.inflate(mContext, R.layout.all_text_item, null);
                    break;
                case TYPE_GIF://gif
                    view = View.inflate(mContext, R.layout.all_gif_item, null);
                    viewHolder.iv_image_gif = (GifImageView) view.findViewById(R.id.iv_image_gif);
                    break;
                case TYPE_AD://软件广告
                    view = View.inflate(mContext, R.layout.all_ad_item, null);
                    viewHolder.btn_install = (Button) view.findViewById(R.id.btn_install);
                    viewHolder.iv_image_icon = (ImageView) view.findViewById(R.id.iv_image_icon);
                    break;
            }

            switch (itemViewType) {
                case TYPE_VIDEO://视频
                case TYPE_IMAGE://图片
                case TYPE_TEXT://文字
                case TYPE_GIF://gif
                    //加载除开广告部分的公共部分视图
                    //user info
                    viewHolder.iv_headpic = (ImageView) view.findViewById(R.id.iv_headpic);
                    viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                    viewHolder.tv_time_refresh = (TextView) view.findViewById(R.id.tv_time_refresh);
                    viewHolder.iv_right_more = (ImageView) view.findViewById(R.id.iv_right_more);
                    //bottom
                    viewHolder.iv_video_kind = (ImageView) view.findViewById(R.id.iv_video_kind);
                    viewHolder.tv_video_kind_text = (TextView) view.findViewById(R.id.tv_video_kind_text);
                    viewHolder.tv_shenhe_ding_number = (TextView) view.findViewById(R.id.tv_shenhe_ding_number);
                    viewHolder.tv_shenhe_cai_number = (TextView) view.findViewById(R.id.tv_shenhe_cai_number);
                    viewHolder.tv_posts_number = (TextView) view.findViewById(R.id.tv_posts_number);
                    viewHolder.ll_download = (LinearLayout) view.findViewById(R.id.ll_download);

                    break;
            }
            //中间公共部分 -所有的都有
            viewHolder.tv_context = (TextView) view.findViewById(R.id.tv_context);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        NetAudioBean.ListBean mediaItem = mNetAudioBeans.get(i);

        switch (itemViewType) {
            case TYPE_VIDEO://视频
                bindData(viewHolder, mediaItem);
                //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
                viewHolder.jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0), mediaItem.getVideo().getThumbnail().get(0), null);
                viewHolder.tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(dateUtil.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");

                break;
            case TYPE_IMAGE://图片
                bindData(viewHolder, mediaItem);
                viewHolder.iv_image_icon.setImageResource(R.drawable.bg_item);
                int height = mediaItem.getImage().getHeight() <= DensityUtil.getScreenHeight() * 0.75 ? mediaItem.getImage().getHeight() : (int) (DensityUtil.getScreenHeight() * 0.75);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.getScreenWidth(), height);
                viewHolder.iv_image_icon.setLayoutParams(params);
                if (mediaItem.getImage() != null && mediaItem.getImage().getBig() != null && mediaItem.getImage().getBig().size() > 0) {
//                    x.image().bind(viewHolder.iv_image_icon, mediaItem.getImage().getBig().get(0));
                    Glide.with(mContext).load(mediaItem.getImage().getBig().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.bg_item).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.iv_image_icon);
                }
                break;
            case TYPE_TEXT://文字
                bindData(viewHolder, mediaItem);
                break;
            case TYPE_GIF://gif
                bindData(viewHolder, mediaItem);
                System.out.println("mediaItem.getGif().getImages().get(0)" + mediaItem.getGif().getImages().get(0));
                Glide.with(mContext).load(mediaItem.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_image_gif);

                break;
            case TYPE_AD://软件广告
                break;
        }
        //设置文本
        viewHolder.tv_context.setText(mediaItem.getText());
        return view;
    }

    private void initUniversalImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.parseColor("#f0f0f0")))
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new FadeInBitmapDisplayer(1000)) // 设置图片渐显的时间
//                .delayBeforeLoading(300)  // 下载前的延迟时间
                .build();

        int memClass = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int memCacheSize = 1024 * 1024 * memClass / 8;

        File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/jiecao/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(3) // default  线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new UsingFreqLimitedMemoryCache(memCacheSize)) // You can pass your own memory cache implementation/
                .memoryCacheSize(memCacheSize) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(mContext, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .defaultDisplayImageOptions(options)
//                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    private void bindData(ViewHolder viewHolder, NetAudioBean.ListBean mediaItem) {
        if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
//            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
            Glide.with(mContext).load(mediaItem.getU().getHeader().get(0).toString()).placeholder(R.drawable.user).into(viewHolder.iv_headpic);
        }
        if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
            viewHolder.tv_name.setText(mediaItem.getU().getName() + "");
        }

        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());

        //设置标签
        List<NetAudioBean.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp());
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        viewHolder.tv_posts_number.setText(mediaItem.getForward() + "");

    }

    class ViewHolder {
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
//        TextView tv_context;
        TextView tv_play_nums;
        TextView tv_video_duration;
        ImageView iv_commant;
        TextView tv_commant_context;
        JCVideoPlayer jcv_videoplayer;

        //Image
        ImageView iv_image_icon;
//        TextView tv_context;

        //Text
//        TextView tv_context;

        //Gif
        GifImageView iv_image_gif;
//        TextView tv_context;

        //软件推广
        Button btn_install;
//        TextView iv_image_icon;
        //TextView tv_context;
    }


}
