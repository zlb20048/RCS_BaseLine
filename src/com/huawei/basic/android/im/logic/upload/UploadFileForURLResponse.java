/*
 * 文件名: UploadFileForURLReponse.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 用于解析文件上传以后的响应的xml字符串对象
 *       xml字符串格式如下
 *        <result resultCode="0">
 *          <array length=’2’>
 *             <uploadFileForURLResult>
 *               <contentName>快乐风景.jpg</contentName>
 *               <downloadUrl>
 *                 http://server:port/storageWeb/servlet/GetFileByURLServlet?root=xxx&fileid=111&ci=111111&cn=xxxx&ct=xxxx&code=xxx
 *               </downloadUrl>
 *               <uploadTaskID>1100</uploadTaskID>
 *               <redirectionUrl>http://server:port/storageWeb/servlet/pcUploadFile?code=xxxx</redirectionUrl>
 *             </uploadFileForURLResult>
 *             <uploadFileForURLResult>
 *                 <contentName>音乐.mp3</contentName>
 *                 <downloadUrl>
 *                       http://server:port/storageWeb/servlet/GetFileByURLServlet?root=xxx&fileid=222&ci=111111&cn=xxxx&ct=xxxx&code=xxx
 *                 </downloadUrl>
 *                 <uploadTaskID>1100</uploadTaskID>
 *                 <redirectionUrl>http://server:port/storageWeb/servlet/pcUploadFile?code=xxxx</redirectionUrl>
 *              </uploadFileForURLResult>
 *         </array>
 *       </result>
 * 创建人: 周雪松
 * 创建时间:2012-4-11
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.upload;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 
 * 用户执行 uploadFileForURL文件上传以后返回的xml字符串解析类<BR>
 * @author xuesongzhou
 * @version [RCS Client V100R001C03, Apr 17, 2012]
 */
@Root(name = "result", strict = false)
public class UploadFileForURLResponse
{
    /**
     * 服务器告知用户上传成功
     */
    public static final int RESULT_CODE_SUCCESS = 0;
    @Attribute(name = "resultCode", required = false)
    //@Element(name = "resultCode", required = false)
    private int resultCode;
    
    @Element(name = "array", required = false)
    private UploadFileForURLResultList uploadFileForURLResultList;
    
    public int getResultCode()
    {
        return this.resultCode;
    }
    
    /**
     * 
     * 返回xml中所有的uploadFileForURLResult元素，把这个元素当成对象返回<BR>
     * @return uploadFileForURLResult元素
     */
    public List<UploadFileForURLResult> getUploadFileForURLResultList()
    {
        if (null == uploadFileForURLResultList)
        {
            return null;
        }
        return uploadFileForURLResultList.getList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "UploadFileForURLResponse [resultCode=" + resultCode
                + ", uploadFileForURLResultList=" + uploadFileForURLResultList
                + "]";
    }
    
    /**
     * 
     * 封装返回的xml中array元素，把这个元素解析成对象<BR>
     * @author xuesongzhou
     * @version [RCS Client V100R001C03, Apr 17, 2012]
     */
    @Root(name = "array", strict = false)
    public static class UploadFileForURLResultList
    {
        @Attribute(name = "length")
        private int length;
        
        @ElementList(entry = "uploadFileForURLResult", required = false, inline = true)
        private List<UploadFileForURLResult> list;
        
        public int getLength()
        {
            return this.length;
        }
        
        /**
         * 
         * 封装返回的xml中UploadFileForURLResult元素，把这个元素解析成对象<BR>
         * @return xml中解析以后的UploadFileForURLResult元素
         */
        public List<UploadFileForURLResult> getList()
        {
            if (0 == this.length || null == list)
            {
                return null;
            }
            return list;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return "UploadFileForURLResultList [length=" + this.length + "]";
        }
    }
    
    /**
     * 
     * 封装 xml中uploadFileForURLResult元素,把这个元素解析成对象<BR>
     * @author xuesongzhou
     * @version [RCS Client V100R001C03, Apr 17, 2012]
     */
    @Root(name = "uploadFileForURLResult", strict = false)
    public static class UploadFileForURLResult
    {
        @Element(name = "contentName", required = false)
        private String contentName;
        
        @Element(name = "downloadUrl", required = false)
        private String downloadUrl;
        
        @Element(name = "uploadTaskID", required = false)
        private String uploadTaskID;
        
        @Element(name = "redirectionUrl", required = false)
        private String redirectionUrl;
        
        public String getContentName()
        {
            return this.contentName;
        }
        
        public String getDownloadUrl()
        {
            return this.downloadUrl;
        }
        
        public String getUploadTaskID()
        {
            return this.uploadTaskID;
        }
        
        public String getRedirectionUrl()
        {
            return this.redirectionUrl;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return "UploadFileForURLResult [contentName=" + contentName
                    + ", downloadUrl=" + downloadUrl + ", uploadTaskID="
                    + uploadTaskID + ", redirectionUrl=" + redirectionUrl + "]";
        }
    }
}
