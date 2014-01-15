/*
 * 文件名: UploadTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: admin
 * 创建时间:2012-4-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload;

import java.util.HashMap;
import java.util.Map;

import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * 上传任务
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-19] 
 */
public class UploadTask extends TaskOperation
{
    /**
    * 缓冲字节
    */
    private byte[] bytesBuf = null;
    
    /**
     * 上传地址
     */
    private String uploadUrl;
    
    /**
     * 网络请求头信息
     */
    private Map<String, String> headers = new HashMap<String, String>();
    
    /**
     * 服务器返回byte数组
     */
    private byte[] responseData;
    
    /**
     * 获取上传地址
     * @return String 上传地址
     */
    public String getUploadUrl()
    {
        return uploadUrl;
    }
    
    /**
     * 设置上传地址
     * @param  uploadUrl 上传地址
     */
    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
    /**
     * 获取缓冲字节
     * @return byte[] bytesBuf
     */
    public byte[] getBytesBuf()
    {
        return bytesBuf;
    }
    
    /**
     * 设置缓冲字节
     * @param bytesBuf  byte[] 
     */
    public void setBytesBuf(byte[] bytesBuf)
    {
        this.bytesBuf = bytesBuf;
    }
    
    /**
     * 获取网络请求头信息
     * @return the headers
     */
    public Map<String, String> getHeaders()
    {
        return headers;
    }
    
    /**
     * 设置网络请求头信息
     * @param  headers Map<String, String>
     */
    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }
    
    /**
     * get responseData
     * @return the responseData
     */
    public byte[] getResponseData()
    {
        return responseData;
    }
    
    /**
     * set responseData
     * @param responseData the responseData to set
     */
    public void setResponseData(byte[] responseData)
    {
        this.responseData = responseData;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public boolean isResumeTask()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.huawei.basic.android.im.component.load.task.TaskOperation#resetOnCreated()
     */
    
    @Override
    protected void resetOnCreated()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnStart()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnRestart()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected boolean equals(TaskOperation downloadTask)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected boolean isSave()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected ITaskDriver getTaskDriver()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnReload()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnDeleted()
    {
        // TODO Auto-generated method stub
        
    }
    
}
