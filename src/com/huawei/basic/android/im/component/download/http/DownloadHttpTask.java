/*
 * 文件名: DownloadHttpTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-23
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.download.http;

import java.util.HashMap;
import java.util.Map;

import com.huawei.basic.android.im.component.download.DownloadTask;
import com.huawei.basic.android.im.component.load.driver.ITaskDriver;

/**
 * http下载任务封装对象的实现
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-23] 
 */
public class DownloadHttpTask extends DownloadTask
{
    
    /**
     * 是否是post请求
     */
    private boolean isPost = false;
    
    /**
     * 是否有代理
     */
    private boolean isProxy = false;
    
    /**
     * 代理地址
     */
    private String proxyHost;
    
    /**
     * 代理端口
     */
    private int proxyPort;
    
    /**
     * 缓冲字节区
     */
    private byte[] postBuf = null;
    
    /**
     * 网络请求头信息
     */
    private Map<String, String> headers = new HashMap<String, String>();
    
    /**
     * 获取是否是Post请求
     * @return boolean isPost
     */
    public boolean isPost()
    {
        return isPost;
    }
    
    /**
     * 设置是否是Post请求
     * @param ispost boolean
     */
    public void setIsPost(boolean ispost)
    {
        this.isPost = ispost;
    }
    
    /**
     * 获取是否设置代理
     * @return the isProxy
     */
    public boolean isProxy()
    {
        return isProxy;
    }
    
    /**
     * 设置是否设置代理
     * @param isproxy boolean
     */
    public void setIsProxy(boolean isproxy)
    {
        this.isProxy = isproxy;
    }
    
    /**
     * 获取代理地址
     * @return String proxyHost
     */
    public String getProxyHost()
    {
        return proxyHost;
    }
    
    /**
     * 设置代理地址
     * @param proxyHost String
     */
    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }
    
    /**
     * 获取代理端口
     * @return the proxyPort
     */
    public int getProxyPort()
    {
        return proxyPort;
    }
    
    /**
     * 设置代理端口
     * @param proxyPort int
     */
    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }
    
    /**
     * 获取缓冲区
     * @return the postContent
     */
    public byte[] getPostBuf()
    {
        return postBuf;
    }
    
    /**
     * 设置缓冲区
     * @param postBuf byte[] 
     */
    public void setPostBuf(byte[] postBuf)
    {
        this.postBuf = postBuf;
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
     * {@inheritDoc}  
     */
    @Override
    protected ITaskDriver getTaskDriver()
    {
        return new DownloadHttpDriver(this);
    }
    
    /**
     * 设置外文件缓冲区大小
     * @param bytesBuf byte[]
     */
    protected final void setFileByteBuf(byte[] bytesBuf)
    {
        this.setBytesBuf(bytesBuf);
    }
}
