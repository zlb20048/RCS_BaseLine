/*
 * 文件名: CommunicationLog.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model.voip;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 14, 2012] 
 */
public class CommunicationLog
{
    
    /**
     * 类型：原生电话呼出
     */
    public static final int TYPE_ORDINARY_CALL_OUT = 1;
    
    /**
     * 类型：VOIP电话呼出
     */
    public static final int TYPE_VOIP_CALL_OUT = 2;
    
    /**
     * 类型：VOIP来电已接
     */
    public static final int TYPE_VOIP_CALL_IN_ALREADY = 3;
    
    /**
     * 类型：VOIP来电未接
     */
    public static final int TYPE_VOIP_CALL_IN_MISSED = 4;
    /**
     * 类型：VOIP来电拒绝
     */
    public static final int TYPE_VOIP_CALL_IN_REFUSED = 5;
    
    /**
     * 类别：音频电话
     */
    public static final int SORT_AUDIO_CALL = 1;
    
    /**
     * 类别：视频电话
     */
    public static final int SORT_VIDEO_CALL = 2;
    
    /**
     * 日期格式
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd");
    
    /**
     * 主键
     */
    private long id;
    
    /**
     * 所有者的userid
     */
    private String ownerUserId;
    
    /**
     * 来电或去电用户id
     */
    private String remoteUserId;
    
    /**
     * 通话id
     */
    private int callId;
    
    /**
     * 来电或去电用户名称
     */
    private String remoteDisplayName;
    
    /**
     * 对方的uri
     */
    private String remoteUri;
    
    /**
     * 电话号码
     */
    private String remotePhoneNum;
    
    /**
     * 类型 ：1、来电已接 2、来电未接 3、去电已接 4、去电未接
     */
    private int type;
    
    /**
     * 类别 1、音频电话 2、视频电话
     */
    private int sort;
    
    /**
     * 头像地址
     */
    private String faceUrl;
    
    /**
     * 头像数据
     */
    private byte[] faceData;
    
    /**
     * 呼叫日期
     */
    private String callDate;
    
    /**
     * 是否未读
     */
    private boolean isUnread = true;
    /**
     * 呼叫接通时间
     */
    private Date callTime;
    /**
     * 通话结束时间
     */
    private Date startTime;
    
    /**
     * 通话开始时间
     */
    private Date endTime;
    
    /**
     * 未读总数
     */
    private int unreadAmout;
    
    /**
     * 获取主键
     * 
     * @return 用户id
     */
    public long getId()
    {
        return id;
    }
    
    /**
     * 设置主键 
     *  
     * @param id   主键
     */
    public void setId(long id)
    {
        this.id = id;
    }
    
    /**
     * 获取用户id
     * 
     * @return 用户id
     */
    
    public String getOwnerUserId()
    {
        return ownerUserId;
    }
    
    /**
     * 设置当前用户id 
     *  
     * @param ownerUserId  当前用户id
     */
    public void setOwnerUserId(String ownerUserId)
    {
        this.ownerUserId = ownerUserId;
    }
    
    /**
     * 获取对方的id
     * 
     * @return 对方的id
     */
    public String getRemoteUserId()
    {
        return remoteUserId;
    }
    
    /**
     * 
     *  设置对方的id
     * @param remoteUserId  对方的id
     */
    public void setRemoteUserId(String remoteUserId)
    {
        this.remoteUserId = remoteUserId;
    }
    
    /**
     * 获取通话id
     *  
     * @return  通话id
     */
    public int getCallId()
    {
        return callId;
    }
    
    /**
     * 设置通话id
     * 
     * @param callId 通话id
     */
    public void setCallId(int callId)
    {
        this.callId = callId;
    }
    
    public String getRemoteDisplayName()
    {
        return remoteDisplayName;
    }
    
    public void setRemoteDisplayName(String remoteDisplayName)
    {
        this.remoteDisplayName = remoteDisplayName;
    }
    
    public String getRemoteUri()
    {
        return remoteUri;
    }
    
    public void setRemoteUri(String remoteUri)
    {
        this.remoteUri = remoteUri;
    }
    
    public String getRemotePhoneNum()
    {
        return remotePhoneNum;
    }
    
    public void setRemotePhoneNum(String remotePhoneNum)
    {
        this.remotePhoneNum = remotePhoneNum;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public int getSort()
    {
        return sort;
    }
    
    public void setSort(int sort)
    {
        this.sort = sort;
    }
    
    public String getFaceUrl()
    {
        return faceUrl;
    }
    
    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }
    
    public byte[] getFaceData()
    {
        return faceData;
    }
    
    public void setFaceData(byte[] faceData)
    {
        this.faceData = faceData;
    }
    
    public boolean getIsUnread()
    {
        return isUnread;
    }
    
    public void setIsUnread(boolean isUnread)
    {
        this.isUnread = isUnread;
    }
    
    public Date getCallTime()
    {
        return callTime;
    }

    /**
     * 
     * 设置呼叫接通时间
     * @param callTime 呼叫接通时间
     */
    public void setCallTime(Date callTime)
    {
        this.callTime = callTime;
        if (callTime != null)
        {
            this.callDate = DATE_FORMAT.format(callTime);
        }
    }

    public Date getStartTime()
    {
        return startTime;
    }
    
    /**
     * 设置开始时间
     *  
     * @param startTime 开始时间
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }
    
    public Date getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }
    
    public String getCallDate()
    {
        return callDate;
    }
    
    public void setCallDate(String callDate)
    {
        this.callDate = callDate;
    }
    
    public int getUnreadAmout()
    {
        return unreadAmout;
    }
    
    public void setUnreadAmout(int unreadAmout)
    {
        this.unreadAmout = unreadAmout;
    }
    
    /**
     * 
     *获取通话时长
     * @return 通话时长
     */
    public long getTalkTime()
    {
        if (startTime == null || endTime == null || type == TYPE_VOIP_CALL_IN_REFUSED
                || type == TYPE_VOIP_CALL_IN_MISSED || type == TYPE_ORDINARY_CALL_OUT)
        {
            return 0;
        }
        else
        {
            return Math.abs(endTime.getTime() - startTime.getTime()) / 1000;
        }
    }
    
    /**
     * 
     *复写equals方法
     * @param o 比较的对象
     * @return 是否相同
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        CommunicationLog cl = (CommunicationLog) o;
        return cl.getId() == this.id;
    }
}
