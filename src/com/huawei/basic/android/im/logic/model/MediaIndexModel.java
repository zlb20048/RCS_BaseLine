/*
 * 文件名: MediaIndexModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 27, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

/**
 * 多媒体文件索引信息<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 27, 2012] 
 */
public class MediaIndexModel implements Serializable
{
    /**
     * 多媒体消息类型：未知
     */
    public static final int MEDIATYPE_UNKNOWN = 0;
    
    /**
     * 多媒体消息类型: 图片
     */
    public static final int MEDIATYPE_IMG = 1;
    
    /**
     * 多媒体消息类型: 语音
     */
    public static final int MEDIATYPE_AUDIO = 2;
    
    /**
     * 多媒体消息类型: 视频
     */
    public static final int MEDIATYPE_VIDEO = 3;
    
    /**
     * 文本消息类型：贴图，但是存放相关信息在MediaIndex表中
     */
    public static final int MEDIATYPE_EMOJI = 4;
    
    /**
     * 文本消息类型：地理位置(经纬度)
     */
    public static final int MEDIATYPE_LOCATION = 5;
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID，与聊天纪录表内的msgID一致，通过此ID找到多媒体消息内容体
     */
    private String msgId;
    
    /**
     * 多媒体消息类型： <br>
     * 1：图片 <br>
     * 2：语音 <br>
     * 3：视频<br>
     * 4: 贴图<br>
     * 5: 地理位置<br>
     */
    private int mediaType;
    
    /**
     * 图片/音频/视频大小，单位：KB <br>
     * 注：图文混排只统计图片大小，文字大小忽略不计，小于1KB，统一显示1KB
     */
    private String mediaSize;
    
    /**
     * 多媒体文件存放路径(原始图)
     */
    private String mediaPath;
    
    /**
     * 多媒体(缩略图)存放路径
     */
    private String mediaSmallPath;
    
    /**
     * 多媒体在文件服务器URL
     */
    private String mediaURL;
    
    /**
     * 多媒体的缩略图在文件服务器的URL
     */
    private String mediaSmallURL;
    
    /**
     * 音频/视频媒体文件播放时长，单位：秒， 取整数，四舍五入
     */
    private int playTime;
    
    /**
     * 多媒体文件描述
     * 多媒体注解，对多媒体内容的简单描述，例如，贴图图片有个对应的名字，表达此张贴图的含义
     * 地图填写desc描述
     */
    private String mediaAlt;
    
    /**
     * 定位 经度
     */
    private String locationLat;
    
    /**
     * 定位  纬度
     */
    private String locationLon;
    
    /**
     * 尝试下载次数
     */
    private int downloadTryTimes;
    
    /**
     * 构造方法
     */
    public MediaIndexModel()
    {
        super();
    }
    
    public String getMsgId()
    {
        return msgId;
    }
    
    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }
    
    public int getMediaType()
    {
        return mediaType;
    }
    
    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }
    
    public String getMediaSize()
    {
        return mediaSize;
    }
    
    public void setMediaSize(String mediaSize)
    {
        this.mediaSize = mediaSize;
    }
    
    public String getMediaPath()
    {
        return mediaPath;
    }
    
    public void setMediaPath(String mediaPath)
    {
        this.mediaPath = mediaPath;
    }
    
    public String getMediaSmallPath()
    {
        return mediaSmallPath;
    }
    
    public void setMediaSmallPath(String mediaSmallPath)
    {
        this.mediaSmallPath = mediaSmallPath;
    }
    
    public String getMediaURL()
    {
        return mediaURL;
    }
    
    public void setMediaURL(String mediaURL)
    {
        this.mediaURL = mediaURL;
    }
    
    public String getMediaSmallURL()
    {
        return mediaSmallURL;
    }
    
    public void setMediaSmallURL(String mediaSmallURL)
    {
        this.mediaSmallURL = mediaSmallURL;
    }
    
    public int getPlayTime()
    {
        return playTime;
    }
    
    public void setPlayTime(int playTime)
    {
        this.playTime = playTime;
    }
    
    public String getMediaAlt()
    {
        return mediaAlt;
    }
    
    public void setMediaAlt(String mediaAlt)
    {
        this.mediaAlt = mediaAlt;
    }
    
    public String getLocationLat()
    {
        return locationLat;
    }
    
    public void setLocationLat(String locationLat)
    {
        this.locationLat = locationLat;
    }
    
    public String getLocationLon()
    {
        return locationLon;
    }
    
    public void setLocationLon(String locationLon)
    {
        this.locationLon = locationLon;
    }
    
    public int getDownloadTryTimes()
    {
        return downloadTryTimes;
    }
    
    public void setDownloadTryTimes(int downloadTryTimes)
    {
        this.downloadTryTimes = downloadTryTimes;
    }
    
    /**
     * Override<BR>
     *
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" msgId:").append(msgId);
        sb.append(" mediaType:").append(mediaType);
        sb.append(" mediaSize:").append(mediaSize);
        sb.append(" mediaPath:").append(mediaPath);
        sb.append(" medialocationLon:").append(locationLon);
        sb.append(" medialocationLat:").append(locationLat);
        sb.append(" mediaURL:").append(mediaURL);
        sb.append(" mediaSmallURL:").append(mediaSmallURL);
        sb.append(" mediaAlt:").append(mediaAlt);
        sb.append(" playTime:").append(playTime);
        return sb.toString();
    }
    
}
