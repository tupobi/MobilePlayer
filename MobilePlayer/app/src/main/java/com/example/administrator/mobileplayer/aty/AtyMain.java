package com.example.administrator.mobileplayer.aty;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.base.BasePager;
import com.example.administrator.mobileplayer.pager.LocalAudioPager;
import com.example.administrator.mobileplayer.pager.LocalVideoPager;
import com.example.administrator.mobileplayer.pager.NetAudioPager;
import com.example.administrator.mobileplayer.pager.NetVideoPager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/11.
 */

public class AtyMain extends FragmentActivity {
    private RadioGroup rgBottomTag;
    private FrameLayout flMainContent;
    private static int posOfPagers;
    private static ArrayList<BasePager> pagers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initPagers();
    }

    private void initPagers() {
        pagers = new ArrayList<>();
        pagers.add(new LocalVideoPager(this));
        pagers.add(new LocalAudioPager(this));
        pagers.add(new NetVideoPager(this));
        pagers.add(new NetAudioPager(this));

        rgBottomTag.setOnCheckedChangeListener(new MyOnCheckedChangedListener());
        rgBottomTag.check(R.id.rb_localVideo);

    }

    class MyOnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            switch (i){
                case R.id.rb_localVideo:
                    posOfPagers = 0;
                    break;
                case R.id.rb_localAudio:
                    posOfPagers = 1;
                    break;
                case R.id.rb_netVideo:
                    posOfPagers = 2;
                    break;
                case R.id.rb_netAudio:
                    posOfPagers = 3;
                    break;
                default:
                    posOfPagers = 0;
                    break;
            }
            setFragment();
            //切换pager在监听法法中执行，RadioButton被改选一次便执行一次。
        }
    }

    public static class PagerFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            BasePager pager = getPager();
            if (pager != null){
                return pager.rootView;
            }
            return null;
        }
    };

    public void setFragment() {
        //1.得到FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();

//        PagerFragment pagerFragment = new PagerFragment();
        //3.替换
        ft.replace(R.id.fl_mainContent, new PagerFragment());//注意，编译器目前不支持匿名内部类的形式了。切必须为public static 修饰的外部类

        //4.提交事务
        ft.commit();
    }

    private void initView() {
        setContentView(R.layout.aty_main);
        rgBottomTag = (RadioGroup) findViewById(R.id.rg_bottomTag);
        flMainContent = (FrameLayout) findViewById(R.id.fl_mainContent);
    }

    /**
     * 初始化方法只调用一次就好，否则消耗流量。
     * 所以设置flag看看是否已经初始化了。
     * @return
     */
    private static BasePager getPager() {
        BasePager pager = pagers.get(posOfPagers);
        if (pager != null && !pager.isInit){
            pager.initData();
            pager.isInit = true;
        }
        return pager;
    }

    private boolean isExit;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (posOfPagers != 0) {
                rgBottomTag.check(R.id.rb_localVideo);
                return true;
            }else if (!isExit){
                isExit = true;
                Toast.makeText(this, "连按退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                        //1.5秒后，重置isExit = false;
                    }
                }, 1500);
                return true;
            }
        }
        //如果在1.5秒内又按了一次退出，此时isExit = true 两个if else if都不会执行，直接退出软件。
        return super.onKeyUp(keyCode, event);
    }
}
