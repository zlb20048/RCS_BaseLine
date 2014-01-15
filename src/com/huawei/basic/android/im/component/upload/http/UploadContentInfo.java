/*
 * 文件名: UploadContentInfo.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-11
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload.http;

import java.io.File;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 上传文件的文件信息<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-11] 
 */
public class UploadContentInfo
{
    /**
     * 
     * 文件类型字符串定义<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-4-11]
     */
    public interface MimeType
    {
        /**
         * 图片内容
         */
        String IMG = "image/jpeg";
        
        /**
         * 视频内容
         */
        String VIDEO = "video/mpeg4";
        
        /**
         * 音频内容
         */
        String AUDIO = "audio/mpeg";
    }
    
    /**
     * 文件名称
     */
    private String contentName;
    
    /**
     * 文件大小
     */
    private long contentSize;
    
    /**
     * 文件描述
     */
    private String contentDesc;
    
    /**
     * 文件类型
     */
    private String mimeType;
    
    /**
     * 待上传文件的本地绝对路径
     */
    private String filePath;
    
    /**
     * 待上传数据
     */
    private byte[] data;
    
    /**
     * 主上传文件
     */
    private boolean isMainContent;
    
    /**
     * get fileName
     * @return the fileName
     */
    public String getContentName()
    {
        return contentName;
    }
    
    /**
     * set fileName
     * @param fileName the fileName to set
     */
    public void setContentName(String fileName)
    {
        this.contentName = fileName;
    }
    
    /**
     * get fileSize
     * @return the fileSize
     */
    public long getContentSize()
    {
        return contentSize;
    }
    
    /**
     * get fileDesc
     * @return the fileDesc
     */
    public String getContentDesc()
    {
        return contentDesc;
    }
    
    /**
     * set fileDesc
     * @param fileDesc the fileDesc to set
     */
    public void setContentDesc(String fileDesc)
    {
        this.contentDesc = fileDesc;
    }
    
    /**
     * get mimeType
     * @return the mimeType
     */
    public String getMimeType()
    {
        return mimeType;
    }
    
    /**
     * set mimeType
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
    
    /**
     * get filePath
     * @return the filePath
     */
    public String getFilePath()
    {
        return filePath;
    }
    
    /**
     * set filePath
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
        
        // get the file length
        if (filePath != null)
        {
            File file = new File(filePath);
            if (file.exists())
            {
                this.contentSize = file.length();
            }
        }
        Logger.d("UPLOAD FILE INFO", "filePath : " + filePath);
        Logger.d("UPLOAD FILE INFO", "fileSize : " + contentSize);
    }
    
    /**
     * get data
     * @return the data
     */
    public byte[] getData()
    {
        return data;
    }
    
    /**
     * set data
     * @param data the data to set
     */
    public void setData(byte[] data)
    {
        this.data = data;
        if (data != null)
        {
            this.contentSize = data.length;
        }
    }
    
    /**
     * get isMainContent
     * @return the isMainContent
     */
    public boolean isMainContent()
    {
        return isMainContent;
    }
    
    /**
     * set isMainContent
     * @param bIsMainContent the isMainContent to set
     */
    public void setMainContent(boolean bIsMainContent)
    {
        this.isMainContent = bIsMainContent;
    }
    
}
