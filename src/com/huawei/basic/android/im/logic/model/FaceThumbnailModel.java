/*
 * 文件名: CountryItemModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qinyangwang
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * 用户和好友头像信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-16]
 */
public class FaceThumbnailModel implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 头像ID，可以是JID和群组ID;
     */
    private String faceId;

    /**
     * 头像缩略图数据
     */
    private byte[] faceBytes;

    /**
     * 用户头像url地址，服务器更新后，需要及时更新到本地
     */
    private String faceUrl;
    
    /**
     * 头像缩略图
     */
    private Bitmap faceBitmap;
    
    /**
     * 头像文件的路径
     */
    private String faceFilePath;
    

    /**
     * 默认构造方法
     */
    public FaceThumbnailModel()
    {
    }

    /**
     * 构造方法
     * 
     * @param f FaceThumbnailModel
     */
    public FaceThumbnailModel(FaceThumbnailModel f)
    {
        faceId = f.getFaceId();
        faceUrl = f.getFaceUrl();
        faceBitmap = f.getFaceBitmap();
        faceBytes = f.getFaceBytes();
    }

    /**
     * 构造方法
     * 
     * @param faceId ID
     * @param faceurl URL
     * @param faceBytes Bytes
     */
    public FaceThumbnailModel(String faceId, String faceurl,
        byte[] faceBytes)
    {
        this.faceId = faceId;
        this.faceUrl = faceurl;
        this.faceBytes = faceBytes;
    }

    /**
     * 构造方法
     * 
     * @param faceId String
     * @param faceUrl String
     */
    public FaceThumbnailModel(String faceId, String faceUrl)
    {
        this.faceId = faceId;
        this.faceUrl = faceUrl;
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public byte[] getFaceBytes()
    {
        return faceBytes;
    }

    public void setFaceBytes(byte[] faceBytes)
    {
        this.faceBytes = faceBytes;
    }

    public String getFaceUrl()
    {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }
    
    public Bitmap getFaceBitmap()
    {
        return faceBitmap;
    }

    public void setFaceBitmap(Bitmap faceBitmap)
    {
        this.faceBitmap = faceBitmap;
    }
    
    public String getFaceFilePath()
    {
        return faceFilePath;
    }

    public void setFaceFilePath(String faceFilePath)
    {
        this.faceFilePath = faceFilePath;
    }

    /**
     * 
     * 头像相关信息的字符串格式
     * 
     * @return 头像信息字符串格式
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("faceId:").append(faceId);
        sb.append(" faceUrl:").append(faceUrl);
        sb.append(" faceBytes:").append(faceBytes);
        sb.append(" faceFilePath: ").append(faceFilePath);
        return sb.toString();
    }

}
