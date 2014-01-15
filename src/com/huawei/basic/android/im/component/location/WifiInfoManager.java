/*
 * 文件名: WifiInfoManager.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 
 * wifi管理类<BR>
 * @author raulxiao
 * @version [RCS Client V100R001C03, Mar 26, 2012]
 */
public class WifiInfoManager
{
    private static final String TAG = "WifiInfoManager";
    
    private WifiManager wifiManager;
    
    /**
     * 
     * [构造简要说明]
     * @param paramContext Context
     */
    public WifiInfoManager(Context paramContext)
    {
        wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
    }
    
    /**
     * 
     *  获取wifi相关信息<BR>
     * [功能详细描述]
     * @return
     */
    private List<WifiInfo> dump()
    {
        if (!this.wifiManager.isWifiEnabled())
        {
            return new ArrayList<WifiInfo>();
        }
        android.net.wifi.WifiInfo wifiConnection = this.wifiManager.getConnectionInfo();
        WifiInfo currentWIFI = null;
        if (wifiConnection != null)
        {
            String s = wifiConnection.getBSSID();
            int i = wifiConnection.getRssi();
            String s1 = wifiConnection.getSSID();
            currentWIFI = new WifiInfo(s, i, s1);
            
        }
        ArrayList<WifiInfo> lsAllWIFI = new ArrayList<WifiInfo>();
        if (currentWIFI != null)
        {
            lsAllWIFI.add(currentWIFI);
        }
        List<ScanResult> lsScanResult = this.wifiManager.getScanResults();
        for (ScanResult result : lsScanResult)
        {
            WifiInfo scanWIFI = new WifiInfo(result);
            if (!scanWIFI.equals(currentWIFI))
            {
                lsAllWIFI.add(scanWIFI);
            }
        }
        return lsAllWIFI;
    }
    
    /**
     * 
     * 判断wifi是否开启<BR>
     * [功能详细描述]
     * @return wifi是否可用 
     */
    public boolean isWifiEnabled()
    {
        return this.wifiManager.isWifiEnabled();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return WifiManager
     */
    public WifiManager wifiManager()
    {
        return this.wifiManager;
    }
    
    /**
     * 将WIFI信息组合成JSON包，去服务器查询位置时做为数据上传至Google的位置服务器
     * 
     * @return JSONArray
     */
    public JSONArray wifiTowers()
    {
        JSONArray jsonArray = new JSONArray();
        if (isWifiEnabled())
        {
            try
            {
                Iterator<WifiInfo> localObject = dump().iterator();
                while (true)
                {
                    if (!localObject.hasNext())
                    {
                        return jsonArray;
                    }
                    
                    jsonArray.put(localObject.next().towerJson());
                }
            }
            catch (Exception localException)
            {
                Logger.d(TAG, "wifiTowers: " + localException.getMessage());
            }
        }
        
        return jsonArray;
    }
    
    /**
     * 
     * Wifi信息类<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 20, 2012]
     */
    private class WifiInfo implements Comparable<WifiInfo>
    {
        private final String bssid;
        
        private final int dBm;
        
        private final String ssid;
        
        /**
         * 
         * [构造简要说明]
         * @param s String
         * @param i int
         * @param s1 String
         */
        public WifiInfo(String s, int i, String s1)
        {
            bssid = s;
            dBm = i;
            ssid = s1;
        }
        
        /**
         * 
         * [构造简要说明]
         * @param scanresult
         */
        public WifiInfo(ScanResult scanresult)
        {
            String s = scanresult.BSSID;
            bssid = s;
            int i = scanresult.level;
            dBm = i;
            String s1 = scanresult.SSID;
            ssid = s1;
        }
        
        public int compareTo(WifiInfo wifiinfo)
        {
            int i = wifiinfo.dBm;
            int j = dBm;
            return i - j;
        }
        
        public boolean equals(Object obj)
        {
            boolean flag = false;
            if (obj == this)
            {
                flag = true;
                return flag;
            }
            else
            {
                if (obj instanceof WifiInfo)
                {
                    WifiInfo wifiinfo = (WifiInfo) obj;
                    String s = wifiinfo.bssid;
                    String s1 = bssid;
                    if (s.equals(s1))
                    {
                        flag = true;
                        return flag;
                    }
                }
                else
                {
                    flag = false;
                }
            }
            return flag;
        }
        
        public int hashCode()
        {
            int i = dBm;
            int j = bssid.hashCode();
            return i ^ j;
        }
        
        /**
         * 
         * wifi mac JSON<BR>
         * [功能详细描述]
         * @return
         */
        @SuppressWarnings("unused")
        private JSONObject info()
        {
            JSONObject jsonobject = new JSONObject();
            try
            {
                String s = bssid;
                jsonobject.put("mac", s);
                String s1 = ssid;
                jsonobject.put("ssid", s1);
                int i = dBm;
                jsonobject.put("dbm", i);
            }
            catch (Exception ex)
            {
                Logger.d(TAG, "info failed: " + ex.getMessage());
            }
            return jsonobject;
        }
        
        /**
         * 
         * wifi mac JSON<BR>
         * [功能详细描述]
         * @return
         */
        private JSONObject towerJson()
        {
            JSONObject jsonobject = new JSONObject();
            try
            {
                String s = bssid;
                jsonobject.put("mac_address", s);
                int i = dBm;
                jsonobject.put("signal_strength", i);
                String s1 = ssid;
                jsonobject.put("ssid", s1);
                jsonobject.put("age", 0);
            }
            catch (Exception ex)
            {
                Logger.d(TAG, ex.getMessage());
            }
            return jsonobject;
        }
    }
}
