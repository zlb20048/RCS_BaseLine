/*
 * 文件名: CellInfoManager.java
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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * 
 * 基站管理类<BR>
 * @author raulxiao
 * @version [RCS Client V100R001C03, Mar 26, 2012]
 */
public class CellInfoManager
{
    /**
     * alone signal unit 独立信号单元
     */
    private int asu;
    
    /**
     * 网桥协议数据单元
     */
    private int bid;
    
    /**
     * CID标识码
     */
    private int cid;
    
    /**
     * 是否是CDMA
     */
    private boolean isCdma;
    
    /**
     * 是否是GSM
     */
    private boolean isGsm;
    
    /**
     * location area code 位置区码 
     */
    private int lac;
    
    /**
     * 纬度 
     */
    private int lat;
    
    /**
     * 经度
     */
    private int lng;
    
    /**
     * Mobile Country Code，移动国家号码
     */
    private int mcc;
    
    /**
     * Mobile Network Code，移动网络号码
     */
    private int mnc;
    
    /**
     * 网络识别码
     */
    private int nid;
    
    /**
     * System Identification
     */
    private int sid;
    
    /**
     * 电话管理类
     */
    private TelephonyManager tel;
    
    private boolean valid;
    
    /**
     * 电话监听
     */
    private PhoneStateListener listener;
    
    /**
     * 
     * [构造简要说明]
     * @param paramContext Context
     */
    public CellInfoManager(Context paramContext)
    {
        tel = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        this.listener = new CellInfoListener(this);
        this.tel.listen(this.listener, PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
    }
    
    private int dBm(int i)
    {
        int j;
        if (i >= 0 && i <= 31)
        {
            j = i * 2 + -113;
        }
        else
        {
            j = 0;
        }
        return j;
    }
    
    /**
     * 
     * 独立信号单元<BR>
     * [功能详细描述]
     * @return
     */
    private int asu()
    {
        return this.asu;
    }
    
    /**
     * 
     * 网桥协议数据<BR>
     * [功能详细描述]
     * @return
     */
    private int bid()
    {
        if (!this.valid)
        {
            update();
        }
        return this.bid;
    }
    
    /**
     * 
     * CDMA信息<BR>
     * [功能详细描述]
     * @return
     */
    @SuppressWarnings("unused")
    private JSONObject cdmaInfo()
    {
        if (!isCdma())
        {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("bid", bid());
            jsonObject.put("sid", sid());
            jsonObject.put("nid", nid());
            jsonObject.put("lat", lat());
            jsonObject.put("lng", lng());
        }
        catch (JSONException ex)
        {
            jsonObject = null;
            Log.e("CellInfoManager", ex.getMessage());
        }
        return jsonObject;
    }
    
    /**
     * 
     * 获取基站定位信息<BR>
     * [功能详细描述]
     * @return JSONArray
     */
    public JSONArray cellTowers()
    {
        int mLat;
        int mMcc;
        int mMnc;
        int[] aryCell = dumpCells();
        JSONArray jsonarray = new JSONArray();
        if (!hasIccCard())
        {
            return jsonarray;
        }
        mLat = lac();
        mMcc = mcc();
        mMnc = mnc();
        if (aryCell == null || aryCell.length < 2)
        {
            aryCell = new int[2];
            aryCell[0] = cid;
            aryCell[1] = -60;
        }
        for (int i = 0; i < aryCell.length; i += 2)
        {
            try
            {
                int j2 = dBm(i + 1);
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("cell_id", aryCell[i]);
                jsonobject.put("location_area_code", mLat);
                jsonobject.put("mobile_country_code", mMcc);
                jsonobject.put("mobile_network_code", mMnc);
                jsonobject.put("signal_strength", j2);
                jsonobject.put("age", 0);
                jsonarray.put(jsonobject);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e("CellInfoManager", ex.getMessage());
            }
        }
        if (isCdma())
        {
            jsonarray = new JSONArray();
            CdmaCellLocation location = (CdmaCellLocation) tel.getCellLocation();
            int cellIDs = location.getBaseStationId();
            int networkID = location.getNetworkId();
            StringBuilder nsb = new StringBuilder();
            nsb.append(location.getSystemId());
            try
            {
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("cell_id", cellIDs);
                jsonobject.put("location_area_code", networkID);
                jsonobject.put("mobile_country_code", tel.getNetworkOperator()
                        .substring(0, 3));
                jsonobject.put("mobile_network_code", nsb.toString());
                jsonobject.put("age", 0);
                jsonarray.put(jsonobject);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e("CellInfoManager", ex.getMessage());
            }
        }
        return jsonarray;
        
    }
    
    /**
     * 
     * CID标识码<BR>
     * [功能详细描述]
     * @return
     */
    private int cid()
    {
        if (!this.valid)
        {
            update();
        }
        return this.cid;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     */
    private int[] dumpCells()
    {
        int[] aryCells;
        if (cid() == 0)
        {
            aryCells = new int[0];
            return aryCells;
        }
        
        List<NeighboringCellInfo> lsCellInfo = this.tel.getNeighboringCellInfo();
        if (lsCellInfo == null || lsCellInfo.size() == 0)
        {
            aryCells = new int[1];
            int i = cid();
            aryCells[0] = i;
            return aryCells;
        }
        int[] arrayOfInt1 = new int[lsCellInfo.size() * 2 + 2];
        int j = 0 + 1;
        int k = cid();
        arrayOfInt1[0] = k;
        int m = j + 1;
        int n = asu();
        arrayOfInt1[j] = n;
        Iterator<NeighboringCellInfo> iter = lsCellInfo.iterator();
        while (true)
        {
            if (!iter.hasNext())
            {
                break;
            }
            NeighboringCellInfo localNeighboringCellInfo = (NeighboringCellInfo) iter.next();
            int i2 = localNeighboringCellInfo.getCid();
            if ((i2 <= 0) || (i2 == 65535))
            {
                continue;
            }
            int i3 = m + 1;
            arrayOfInt1[m] = i2;
            m = i3 + 1;
            int i4 = localNeighboringCellInfo.getRssi();
            arrayOfInt1[i3] = i4;
        }
        int[] arrayOfInt2 = new int[m];
        System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, m);
        aryCells = arrayOfInt2;
        return aryCells;
        
    }
    
    /**
     * 
     * 判断是否为CDMA<BR>
     * [功能详细描述]
     * @return boolean
     */
    public boolean isCdma()
    {
        if (!this.valid)
        {
            update();
        }
        return this.isCdma;
    }
    
    /**
     * 
     * 判断是否为GSM<BR>
     * [功能详细描述]
     * @return boolean
     */
    public boolean isGsm()
    {
        if (!this.valid)
        {
            update();
        }
        return this.isGsm;
    }
    
    /**
     * 
     * 位置区码 <BR>
     * [功能详细描述]
     * @return
     */
    private int lac()
    {
        if (!this.valid)
        {
            update();
        }
        return this.lac;
    }
    
    /**
     * 
     * 计算维度<BR>
     * [功能详细描述]
     * @return
     */
    private int lat()
    {
        if (!this.valid)
        {
            update();
        }
        return this.lat;
    }
    
    /**
     * 
     * 计算经度<BR>
     * [功能详细描述]
     * @return
     */
    private int lng()
    {
        if (!this.valid)
        {
            update();
        }
        return this.lng;
    }
    
    /**
     * 
     * 移动国家号码<BR>
     * [功能详细描述]
     * @return
     */
    private int mcc()
    {
        if (!this.valid)
        {
            update();
        }
        return this.mcc;
    }
    
    /**
     * 
     * 移动网络号码<BR>
     * [功能详细描述]
     * @return
     */
    private int mnc()
    {
        if (!this.valid)
        {
            update();
        }
        return this.mnc;
    }
    
    /**
     * 
     * 网络识别码<BR>
     * [功能详细描述]
     * @return
     */
    private int nid()
    {
        if (!this.valid)
        {
            update();
        }
        return this.nid;
    }
    
    /**
     * 
     * System Identification<BR>
     * [功能详细描述]
     * @return
     */
    private int sid()
    {
        if (!this.valid)
        {
            update();
        }
        return this.sid;
    }
    
    /**
     * 
     * 判断是否有电话卡<BR>
     * [功能详细描述]
     * @return boolean
     */
    public boolean hasIccCard()
    {
        return tel.hasIccCard() || (tel.getCellLocation() != null);
    }
    
    /**
     * 
     * 计算GSM和网络信息<BR>
     * [功能详细描述]
     */
    private void update()
    {
        this.isGsm = false;
        this.isCdma = false;
        this.cid = -1;
        this.lac = -1;
        this.mcc = 0;
        this.mnc = 0;
        CellLocation cellLocation = this.tel.getCellLocation();
        int nPhoneType = this.tel.getPhoneType();
        if (nPhoneType == 1 && cellLocation instanceof GsmCellLocation)
        {
            this.isGsm = true;
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            int nGSMCID = gsmCellLocation.getCid();
            if (nGSMCID > 0)
            {
                if (nGSMCID != 65535)
                {
                    this.cid = nGSMCID;
                    this.lac = gsmCellLocation.getLac();
                }
            }
        }
        try
        {
            String strNetworkOperator = this.tel.getNetworkOperator();
            int nNetworkOperatorLength = strNetworkOperator.length();
            if (nNetworkOperatorLength != 5)
            {
                if (nNetworkOperatorLength != 6)
                {
                    ;
                }
            }
            else
            {
                this.mcc = Integer.parseInt(strNetworkOperator.substring(0, 3));
                this.mnc = Integer.parseInt(strNetworkOperator.substring(3,
                        nNetworkOperatorLength));
            }
            if (this.tel.getPhoneType() == 2)
            {
                this.valid = true;
                Class<?> clsCellLocation = cellLocation.getClass();
                Class<?>[] aryClass = new Class[0];
                Method localMethod1 = clsCellLocation.getMethod("getBaseStationId",
                        aryClass);
                Method localMethod2 = clsCellLocation.getMethod("getSystemId",
                        aryClass);
                Method localMethod3 = clsCellLocation.getMethod("getNetworkId",
                        aryClass);
                Object[] aryDummy = new Object[0];
                this.bid = ((Integer) localMethod1.invoke(cellLocation,
                        aryDummy)).intValue();
                this.sid = ((Integer) localMethod2.invoke(cellLocation,
                        aryDummy)).intValue();
                this.nid = ((Integer) localMethod3.invoke(cellLocation,
                        aryDummy)).intValue();
                Method localMethod7 = clsCellLocation.getMethod("getBaseStationLatitude",
                        aryClass);
                Method localMethod8 = clsCellLocation.getMethod("getBaseStationLongitude",
                        aryClass);
                this.lat = ((Integer) localMethod7.invoke(cellLocation,
                        aryDummy)).intValue();
                this.lng = ((Integer) localMethod8.invoke(cellLocation,
                        aryDummy)).intValue();
                this.isCdma = true;
            }
        }
        catch (Exception ex)
        {
            Log.e("CellInfoManager", ex.getMessage());
        }
    }
    
    /**
     * 
     * 基站监听<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 25, 2012]
     */
    class CellInfoListener extends PhoneStateListener
    {
        CellInfoListener(CellInfoManager manager)
        {
            
        }
        
        public void onCellLocationChanged(CellLocation paramCellLocation)
        {
            CellInfoManager.this.valid = false;
        }
        
        public void onSignalStrengthChanged(int paramInt)
        {
            CellInfoManager.this.asu = paramInt;
        }
    }
}
