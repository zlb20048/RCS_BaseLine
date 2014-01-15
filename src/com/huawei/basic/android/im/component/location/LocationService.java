/*
 * 文件名: LocationService.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: RaulXiao
 * 创建时间:Mar 26, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.huawei.basic.android.im.component.json.JsonUtils;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.utils.FileUtil;

/**
 * 
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Mar 26, 2012]
 */
public final class LocationService
{
    /**
     * 获取位置监听最短间隔时间
     */
    private static final long MIN_TIME = 500;
    
    /**
     * 获取位置监听最短间隔距离
     */
    private static final float MIN_INSTANCE = 20;
    
    /**
     * Debug Tag
     */
    private static final String TAG = "LocationService";
    
    private static Context sContext;
    
    private static LocationService mLocationService;
    
    /**
     * 定位管理类
     */
    private LocationManager mLocationMgr;
    
    /**
     * 当前位置
     */
    private Location mLocation;
    
    /**
     * 位置监听
     */
    private LocationListener mLocationListener;
    
    /**
     * 
     * [构造简要说明]
     * @param context 全局上下文
     */
    public LocationService(Context context)
    {
        sContext = context;
        mLocationMgr = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        mLocationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Logger.d(TAG, "====location changed====");
                
                Logger.d(TAG, "changed    =  " + location.getLatitude()
                        + "*****" + location.getLongitude());
                LocationService.this.mLocation = location;
            }
            
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }
            
            @Override
            public void onProviderEnabled(String s)
            {
            }
            
            @Override
            public void onProviderDisabled(String s)
            {
            }
        };
    }
    
    /**
     * 获取位置服务实例
     * 
     * @param context 上下文
     * @return 位置服务实例
     */
    public static LocationService getLocationImpl(Context context)
    {
        synchronized (LocationService.class)
        {
            if (mLocationService == null)
            {
                mLocationService = new LocationService(context);
            }
        }
        return mLocationService;
    }
    
    /**
     * 
     * 获取用户位置 根据GPS or WiFi Or 基站定位用户位置
     * 
     * @return Location 经纬度Location
     */
    public LocationInfo getLocationInfo()
    {
        Logger.d(TAG, "------getLocation---start---");
        mLocation = null;
        
        long init = System.currentTimeMillis();
        LocationInfo locationInfo = new LocationInfo();
        boolean hasStartListener = false;
        if (mLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Logger.d(TAG, "startListenLocation NETWORK_PROVIDER");
            startListenLocation(LocationManager.NETWORK_PROVIDER);
            hasStartListener = true;
        }
        
        if (mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Logger.d(TAG, "startListenLocation GPS_PROVIDER");
            startListenLocation(LocationManager.GPS_PROVIDER);
            hasStartListener = true;
        }
        if (locationInfo.getLocation() == null)
        {
            long start = System.currentTimeMillis();
            locationInfo = getLocationByCellOrWifi();
            Logger.d(TAG, "getByCellOrWifi = "
                    + (System.currentTimeMillis() - start));
        }
        if (hasStartListener)
        {
            if (locationInfo.getLocation() == null)
            {
                int counter = 0;
                while (counter++ < 20)
                {
                    SystemClock.sleep(MIN_TIME);
                    if (mLocation != null)
                    {
                        Logger.d(TAG, "got location by provider");
                        locationInfo.setLocation(mLocation);
                        break;
                    }
                }
            }
            // 移除监听
            stopListenLocation();
        }
        
        if (locationInfo.getLocation() != null)
        {
            Logger.d(TAG, "end    =  "
                    + locationInfo.getLocation().getLatitude() + "*****"
                    + locationInfo.getLocation().getLongitude());
        }
        Logger.d(TAG, "totalTime = " + (System.currentTimeMillis() - init));
        return locationInfo;
    }
    
    /**
     * 开始监听位置信息变化
     * 
     * @param locationProvider 位置服务提供者
     */
    private void startListenLocation(String locationProvider)
    {
        mLocationMgr.requestLocationUpdates(locationProvider,
                MIN_TIME,
                MIN_INSTANCE,
                mLocationListener);
    }
    
    /**
     * 停止监听位置信息变化
     */
    private void stopListenLocation()
    {
        mLocationMgr.removeUpdates(mLocationListener);
        mLocation = null;
    }
    
    /**
     * 
     * 根据基站或者Wifi获取位置详细信息
     * 
     * @param jsonArray
     * @param isByCell
     * @return
     */
    private LocationInfo getLocationByCellOrWifi()
    {
        LocationInfo info = new LocationInfo();
        
        CellInfoManager cellManager = new CellInfoManager(sContext);
        WifiInfoManager mWiFiManager = new WifiInfoManager(sContext);
        JSONArray cellArray = cellManager.cellTowers();
        JSONArray wifiArray = mWiFiManager.wifiTowers();
        Logger.d(TAG, "getLocationInfo----cellArray=" + cellArray
                + ", wifiArray=" + wifiArray);
        if (cellArray.length() < 1 && wifiArray.length() < 1)
        {
            // 如果本地的定位信息缺失，不去服务器查询
            return info;
        }
        JSONObject holder = new JSONObject();
        JSONObject retLocationJson, locationData, addressData;
        try
        {
            holder.put("version", "1.1.0");
            holder.put("host", "maps.google.com");
            holder.put("address_language", "zh_CN");
            if (cellArray.length() > 0)
            {
                if (cellManager.isGsm())
                {
                    holder.put("radio_type", "gsm");
                    
                }
                else if (cellManager.isCdma())
                {
                    
                    holder.put("radio_type", "cdma");
                }
            }
            holder.put("request_address", true);
            holder.put("cell_towers", cellArray);
            holder.put("wifi_towers", wifiArray);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost localHttpPost = new HttpPost(
                    "http://www.google.com/loc/json");
            String strJson = holder.toString();
            StringEntity objJsonEntity = new StringEntity(strJson);
            Logger.d(TAG, "getLocationInfo: Location Send*****" + strJson);
            localHttpPost.setEntity(objJsonEntity);
            HttpResponse objResponse = client.execute(localHttpPost);
            HttpEntity httpEntity = objResponse.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    httpEntity.getContent()));
            StringBuffer sb = new StringBuffer();
            String result = null;
            while ((result = br.readLine()) != null)
            {
                Logger.d(TAG, "getLocationInfo: Locaiton receive*****" + result);
                sb.append(result);
            }
            FileUtil.closeStream(br);
            
            retLocationJson = new JSONObject(sb.toString());
            locationData = JsonUtils.getJSONObject(retLocationJson, "location");
            if (locationData != null)
            {
                Location loc = new Location(LocationManager.NETWORK_PROVIDER);
                loc.setLatitude(JsonUtils.getDouble(locationData, "latitude"));
                loc.setLongitude(JsonUtils.getDouble(locationData, "longitude"));
                loc.setAccuracy((float) JsonUtils.getDouble(locationData,
                        "accuracy"));
                loc.setTime(System.currentTimeMillis());
                info.setLocation(loc);
                
                addressData = JsonUtils.getJSONObject(locationData, "address");
                if (addressData != null)
                {
                    StringBuffer strBuffer = new StringBuffer();
                    if (JsonUtils.getString(addressData, "country") != null)
                    {
                        strBuffer.append(JsonUtils.getString(addressData,
                                "country"));
                    }
                    if (JsonUtils.getString(addressData, "region") != null)
                    {
                        strBuffer.append(JsonUtils.getString(addressData,
                                "region"));
                    }
                    if (JsonUtils.getString(addressData, "city") != null)
                    {
                        strBuffer.append(JsonUtils.getString(addressData,
                                "city"));
                    }
                    if (JsonUtils.getString(addressData, "street") != null)
                    {
                        strBuffer.append(JsonUtils.getString(addressData,
                                "street"));
                    }
                    if (JsonUtils.getString(addressData, "street_number") != null)
                    {
                        strBuffer.append(JsonUtils.getString(addressData,
                                "street_number"));
                    }
                    info.setAddressInfo(strBuffer.toString());
                }
            }
            
            return info;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.d(TAG, "getLocationInfo Exception =" + e.toString());
        }
        return info;
    }
    
    /**
     * 
     * 根据经纬度获取到位置信息<BR>
     * @param lat 经度
     * @param lon 维度
     * @return 地址
     */
    public String getAddress(double lat, double lon)
    {
        // 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
        // 密钥可以随便写一个key=abc
        // http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s
        // output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
        String language = Locale.getDefault().getLanguage();
        String parseLanguage = "en";
        if ("zh".equals(language))
        {
            parseLanguage = "zh_cn";
        }
        else if ("en".equals(language))
        {
            parseLanguage = "en";
        }
        Logger.d(TAG, "Language =" + language);
        String url = String.format("http://maps.google.cn/maps/geo?output=csv&hl=%s&key=abcdef&q=%s,%s",
                parseLanguage,
                lat,
                lon);
        Logger.d(TAG, "getAddress: Address send*****" + url);
        String result = "";
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse hr = client.execute(new HttpGet(url));
            HttpEntity entity = hr.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    entity.getContent()));
            
            if ((result = br.readLine()) != null)
            {
                Logger.d(TAG, "getAddress: Address receive*****" + result);
                // 英文返回值：200,9,"帝景天城小区停车场 Jiangjun Ave, Jiangning, Nanjing, Jiangsu, China"
                // 中文返回值：200,9,"中国江苏省南京市江宁区将军大道帝景天城小区停车场"
                String[] retList = result.split(",");
                if (retList.length > 2 && ("200".equals(retList[0])))
                {
                    StringBuffer strBuffer = new StringBuffer();
                    for (int i = 2; i < retList.length; i++)
                    {
                        strBuffer.append(retList[i]);
                    }
                    result = strBuffer.toString();
                    result = result.replace("\"", "");
                }
                else
                {
                    result = "";
                }
            }
            FileUtil.closeStream(br);
            Logger.d(TAG, "getAddress: Address =" + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.d(TAG, "getAddress: Exception =" + e.toString());
            return null;
        }
        return result;
    }
    
    /**
     * 
     * [一句话功能简述]判断/system/framework/中是否有com.google.android.maps.jar [功能详细描述]
     * 
     * @return boolean
     */
    public boolean isJarExisits()
    {
        return new File("/system/framework/com.google.android.maps.jar").exists();
    }
}
