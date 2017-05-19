package com.example.administrator.mobileplayer.util;

import android.content.Context;
import android.net.TrafficStats;

/**
 * Created by Administrator on 2017/5/19.
 */

public class GetNetSpeed {
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String getNetSpeed(Context context){
        String netSpeed = "0 kb/s";

        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes()/1024);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";

        float netSpeedMb = Long.valueOf(speed);
        if (netSpeedMb > 1024){//设置为MB
            netSpeedMb = netSpeedMb/1024;
            float  temp   =  (float)(Math.round(netSpeedMb*100))/100;//保留两位小数
            netSpeed = String.valueOf(temp) + "Mb/s";
        }
        return netSpeed;
    }
}
