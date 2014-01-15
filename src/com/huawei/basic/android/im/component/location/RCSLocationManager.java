/*
 * 文件名: WoYouLocationManager.java<BR> 版 权： Copyright Huawei Tech. Co. Ltd. All
 * Rights Reserved. 描 述: [该类的简要描述] 创建人: raulxiao 创建时间:2012-03-26 修改人： 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.location;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 
 * 获取位置信息的管理类<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Mar 26, 2012]
 */
public class RCSLocationManager
{
    /**
     * Debug Tag
     */
    private static final String TAG = "RCSLocationManager";
    
    /**
     * ExecutorService
     */
    private static ExecutorService sFixedThreadPoolExecutor = Executors.newCachedThreadPool();
    
    /**
     * Context
     */
    private Context mContext;
    
    /**
     * 
     * [构造简要说明]
     * @param context Context
     */
    public RCSLocationManager(Context context)
    {
        mContext = context;
    }
    
    /**
     * 
     * 获取位置信息<BR>
     * [功能详细描述]
     * @param listener 位置信息监听
     * @param needAddressInfo 是否需要地址信息
     */
    public void getLocationInfo(final LocationDataListener listener,
            final boolean needAddressInfo)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                LocationInfo result = new LocationInfo();
                listener.onLocationProgress(true);
                try
                {
                    result = LocationService.getLocationImpl(mContext)
                            .getLocationInfo();
                    
                    Location location = result.getLocation();
                    if (location != null && needAddressInfo)
                    {
                        result.setAddressInfo(LocationService.getLocationImpl(mContext)
                                .getAddress(location.getLatitude(),
                                        location.getLongitude()));
                    }
                    
                }
                catch (Exception e)
                {
                    Logger.e(TAG, e.toString());
                }
                listener.onLocationResult(result);
                listener.onLocationProgress(false);
                Looper.loop();
                
            }
        };
        sFixedThreadPoolExecutor.execute(runnable);
    }
}
