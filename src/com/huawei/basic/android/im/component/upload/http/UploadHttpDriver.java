/*
 * 文件名: UploadHttpDriver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload.http;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;

import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.load.task.TaskException;

/**
 * http 协议的上传的实现
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-24] 
 */
public class UploadHttpDriver implements ITaskDriver
{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws TaskException
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void read() throws TaskException
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        
    }
    
    /**
     * 
     * 字节数组Entity封装类<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    protected class BasicByteArrayEntity extends ByteArrayEntity
    {
        
        /**
         * 上传完成度监听器
         */
        private FinishedListener finishedListener;
        
        /**
         * 
         * 构造
         * @param b byte[]
         * @param finishedListener 上传完成度监听器对象
         */
        public BasicByteArrayEntity(byte[] b, FinishedListener finishedListener)
        {
            super(b);
            this.finishedListener = finishedListener;
        }
        
        /**
         * 
         * <BR>
         * {@inheritDoc}
         * @see org.apache.http.entity.ByteArrayEntity#writeTo(java.io.OutputStream)
         */
        @Override
        public void writeTo(OutputStream outstream) throws IOException
        {
            FilterOutputStream fos = new BasicFilterOutputStream(outstream,
                    finishedListener);
            super.writeTo(fos);
        }
        
    }
    
    /**
     * 
     * 文件Entity封装类<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    protected class BasicFileEntity extends FileEntity
    {
        /**
         * 上传完成度监听器
         */
        private FinishedListener finishedListener;
        
        /**
         * 
         * 构造函数
         * @param file 文件对象
         * @param contentType 文件的类型
         * @param finishedListener 上传完成度监听器对象
         */
        public BasicFileEntity(File file, String contentType,
                FinishedListener finishedListener)
        {
            super(file, contentType);
            this.finishedListener = finishedListener;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see org.apache.http.entity.FileEntity#writeTo(java.io.OutputStream)
         */
        
        @Override
        public void writeTo(OutputStream outstream) throws IOException
        {
            FilterOutputStream fos = new BasicFilterOutputStream(outstream,
                    finishedListener);
            super.writeTo(fos);
        }
        
    }
    
    /**
     * 
     * MultipartEntity封装类<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    protected class BasicMultipartEntity extends MultipartEntity
    {
        /**
         * 上传完成度监听器
         */
        private FinishedListener finishedListener;
        
        /**
         * 构造函数
         * 
         * @param finishedListener 上传完成度监听器
         */
        public BasicMultipartEntity(FinishedListener finishedListener)
        {
            this.finishedListener = finishedListener;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see org.apache.http.entity.mime.MultipartEntity#writeTo(java.io.OutputStream)
         */
        
        @Override
        public void writeTo(OutputStream outstream) throws IOException
        {
            FilterOutputStream fos = new BasicFilterOutputStream(outstream,
                    finishedListener);
            super.writeTo(fos);
        }
        
        /**
         * 
         * <BR>
         * {@inheritDoc}
         * @see org.apache.http.entity.mime.MultipartEntity#generateContentType(java.lang.String, java.nio.charset.Charset)
         */
        @Override
        protected String generateContentType(String boundary, Charset charset)
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append("multipart/related;boundary=\"");
            buffer.append(boundary);
            buffer.append("\"");
            return buffer.toString();
        }
    }
    
    /**
     * 
     * FilterOutputStream封装类<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    protected class BasicFilterOutputStream extends FilterOutputStream
    {
        /**
         * 上传完成度监听器
         */
        private FinishedListener mFinishedListener;
        
        /**
         * 已完成写入数
         */
        private int mFinished;
        
        /**
         * 
         * 构造函数 
         * @param out OutputStream
         * @param finishedListener 上传完成度监听器对象
         */
        public BasicFilterOutputStream(OutputStream out,
                FinishedListener finishedListener)
        {
            super(out);
            mFinished = 0;
            mFinishedListener = finishedListener;
        }
        
        /**
         * 
         * <BR>
         * {@inheritDoc}
         * @see java.io.FilterOutputStream#write(byte[], int, int)
         */
        @Override
        public void write(byte[] buffer, int offset, int count)
                throws IOException
        {
            super.write(buffer, offset, count);
            mFinished += count;
            mFinishedListener.onFinish(mFinished);
        }
    }
    
    /**
     * 
     * 写入流的进度监听器<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-14]
     */
    protected interface FinishedListener
    {
        /**
         * 已完成部分
         * <BR>
         * 
         * @param finished 已完成
         */
        void onFinish(int finished);
    }
    
}
