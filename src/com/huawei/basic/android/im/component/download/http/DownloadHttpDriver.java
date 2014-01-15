/*
 * 文件名: DownloadHttpDriver.java
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.load.task.TaskOperation;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.Util;

/**
 * http 协议的下载实现
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-23] 
 */
public class DownloadHttpDriver implements ITaskDriver
{
    /**
     * TAG
     */
    private static final String TAG = "DownloadHttpDriver";
    
    /**
     * 默认的缓冲区大小
     */
    private static final int BYTE_LENGTH = 102400;
    
    /**
     * 进度更新时间，每500ms更新一次进度
     */
    private static final int REPORT_TIME = 500;
    
    /**
     * http协议的下载任务对象
     */
    private DownloadHttpTask mDownloadTask = null;
    
    /**
     * 读取流
     */
    private InputStream is = null;
    
    /**
     * http网络连接对象
     */
    private HttpURLConnection conn = null;
    
    /**
     * 构造器
     * @param downloadTask DownloadHttpTask
     */
    public DownloadHttpDriver(DownloadHttpTask downloadTask)
    {
        this.mDownloadTask = downloadTask;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws TaskException
    {
        // 1.打开HTTP连接
        openConnection();
        try
        {
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpStatus.SC_OK
                    && responseCode != HttpStatus.SC_PARTIAL_CONTENT)
            {
                Logger.e(TAG, "HTTP response error code : " + responseCode);
                throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
            }
            
            // 处理保存路径
            handleStorePath();
            
            long hSize = 0L;
            // 从返回头中获取返回长度
            String value = conn.getHeaderField("Content-Length");
            if (value != null && value.length() > 0)
            {
                try
                {
                    hSize = Long.parseLong(value);
                    Logger.i(TAG, "Content-Length : " + value);
                }
                catch (Exception e)
                {
                    Logger.w(TAG, e.getMessage(), e);
                }
            }
            else
            {
                String contentRange = conn.getHeaderField("content-range");
                Logger.i(TAG, "content-range : " + contentRange);
                if (null != contentRange)
                {
                    hSize = Long.parseLong(Util.split2(contentRange, "/")[1])
                            - mDownloadTask.getCurrentSize();
                }
            }
            long cSize = conn.getContentLength();
            Logger.i(TAG, "connect size : " + cSize + ", content length : "
                    + hSize);
            long curSize = mDownloadTask.getCurrentSize();
            cSize = cSize > hSize ? cSize : hSize;
            cSize += curSize;
            mDownloadTask.setTotalSize(cSize);
            Logger.i(TAG, "totalSize=" + cSize);
            
            // 从流中获取返回长度
            is = conn.getInputStream();
        }
        catch (IOException ex)
        {
            Logger.e(TAG, ex.getMessage(), ex);
            throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void read() throws TaskException
    {
        if (mDownloadTask.getStorePath() != null)
        {
            readDownloadFile();
        }
        else
        {
            readBytes();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        try
        {
            if (is != null)
            {
                is.close();
            }
        }
        catch (Exception e)
        {
            Logger.i("clear net Exception:", e.toString());
        }
        finally
        {
            is = null;
        }
        try
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
        catch (Exception e)
        {
            Logger.i("clear net Exception:", e.toString());
        }
        finally
        {
            conn = null;
        }
    }
    
    /**
     * 处理保存路径的方法，如果没有设置路径，则任务失败
     * <BR>
     * 
     * @throws TaskException TaskException
     */
    protected void handleStorePath() throws TaskException
    {
        if (mDownloadTask.getStorePath() == null)
        {
            Logger.e(TAG, "store path is null!");
            throw new TaskException(TaskException.WRITE_FILE_FAILED);
        }
        
        // 先判断该路径对应的是否是文件并且是否存在
        File file = new File(mDownloadTask.getStorePath());
        // 如果是文件夹，从联网信息中获取文件名
        if (file.isDirectory())
        {
            
            String fileName = getFileName();
            Logger.d(TAG, "fileName: " + fileName);
            String filePath = mDownloadTask.getStorePath();
            if (!filePath.endsWith("/"))
            {
                filePath += "/";
            }
            filePath += fileName;
            mDownloadTask.setStorePath(filePath);
        }
        
        if (null != mDownloadTask.getStorePath())
        {
            file = FileUtil.getFileByPath(mDownloadTask.getStorePath());
            if (file != null && file.exists() && mDownloadTask.getCurrentSize() == 0)
            {
                FileUtil.deleteFile(file);
            }
        }
        try
        {
            // 创建文件时如果该路径不能创建，或者已经存在文件则更改路径并创建
            file = FileUtil.createFile(mDownloadTask.getStorePath());
            mDownloadTask.setStorePath(file.getPath());
        }
        catch (IOException e)
        {
            Logger.e(TAG, e.getMessage(), e);
            throw new TaskException(TaskException.WRITE_FILE_FAILED);
        }
    }
    
    /**
     * 
     * 获取文件名<BR>
     * 如果服务器返回了文件名，则解析并返回；否则获取当前系统时间作为文件名并返回
     * @return 文件名
     */
    protected final String getFileName()
    {
        // 获取文件名
        // Content-Disposition: attachment;filename=1a079b824eb02229840bb8b30515295a_min.jpg
        String contentDis = conn.getHeaderField("Content-Disposition");
        String fileName = null;
        Logger.d(TAG, "Content-Disposition : " + contentDis);
        if (contentDis != null)
        {
            int index = contentDis.indexOf("filename=");
            if (index != -1)
            {
                fileName = contentDis.substring(index + 9);
            }
        }
        if (fileName == null)
        {
            fileName = String.valueOf(System.currentTimeMillis());
        }
        return fileName;
    }
    
    /**
     * 打开Http连接
     * <BR>
     * 
     * @throws TaskException
     */
    private void openConnection() throws TaskException
    {
        DataOutputStream os = null;
        try
        {
            URL url = new URL(mDownloadTask.getDownloadUrl());
            
            if (mDownloadTask.isProxy())
            {
                conn = (HttpURLConnection) url.openConnection(new Proxy(
                        Proxy.Type.HTTP, new InetSocketAddress(
                                mDownloadTask.getProxyHost(),
                                mDownloadTask.getProxyPort())));
            }
            else
            {
                conn = (HttpURLConnection) url.openConnection();
            }
            
            // 不使用Cache
            conn.setUseCaches(false);
            conn.setConnectTimeout(mDownloadTask.getTimeout());
            conn.setReadTimeout(mDownloadTask.getTimeout());
            // 设置请求类型
            if (mDownloadTask.isPost())
            {
                conn.setRequestMethod("POST");
            }
            else
            {
                conn.setRequestMethod("GET");
            }
            
            // 设置RANGE
            long curSize = mDownloadTask.getCurrentSize();
            if (curSize > 0)
            {
                conn.addRequestProperty("RANGE", "bytes=" + curSize + "-");
            }
            
            // 设置头信息
            Map<String, String> header = mDownloadTask.getHeaders();
            for (String key : header.keySet())
            {
                String value = header.get(key);
                conn.addRequestProperty(key, value);
            }
            mDownloadTask.getHeaders().clear();
            
            // 以内容实体方式发送请求参数
            byte[] postBuf = mDownloadTask.getPostBuf();
            long length = postBuf != null ? postBuf.length : curSize;
            if (mDownloadTask.isPost() && length > 0)
            {
                // 发送POST请求必须设置允许输出
                conn.setDoOutput(true);
                // 维持长连接
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Length",
                        String.valueOf(length));
                conn.setRequestProperty("Content-Type",
                        "application/octet-stream");
                
                // 开始写入数据
                os = new DataOutputStream(conn.getOutputStream());
                os.write(postBuf);
                os.flush();
            }
        }
        catch (IOException ex)
        {
            Logger.e(TAG, ex.getMessage(), ex);
            throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
        }
        finally
        {
            //关流
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    Logger.w(TAG, e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * 网络读取下载文件
     * @throws TaskException
     */
    private void readDownloadFile() throws TaskException
    {
        
        long currentSize = mDownloadTask.getCurrentSize();
        String storePath = mDownloadTask.getStorePath();
        // 存储每次从网络层读取到的数据
        // 临时数据缓冲区
        int len = 0;
        byte[] buff = new byte[BYTE_LENGTH];
        byte[] bytes = null;
        long time = 0;
        RandomAccessFile file = null;
        try
        {
            file = new RandomAccessFile(FileUtil.getFileByPath(storePath), "rw");
            while (mDownloadTask.getAction() != TaskOperation.ACTION_STOP
                    && mDownloadTask.getAction() != TaskOperation.ACTION_DELETE)
            {
                try
                {
                    if ((len = is.read(buff)) == -1)
                    {
                        break;
                    }
                }
                catch (IOException ex)
                {
                    Logger.e(ex.getMessage(), "readDownloadFile fail", ex);
                    throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
                }
                bytes = new byte[len];
                System.arraycopy(buff, 0, bytes, 0, len);
                
                file.seek(file.length());
                file.write(bytes);
                currentSize += len;
                Logger.i(TAG, "readFileData : currentSize=" + currentSize);
                long currentTime = System.currentTimeMillis();
                if (time == 0 || (currentTime - time > REPORT_TIME))
                {
                    mDownloadTask.onProgress();
                    time = currentTime;
                }
                mDownloadTask.setCurrentSize(currentSize);
            }
        }
        catch (TaskException ex1)
        {
            throw ex1;
        }
        catch (IOException ex2)
        {
            Logger.e(ex2.getMessage(), "write file fail", ex2);
            throw new TaskException(TaskException.WRITE_FILE_FAILED);
        }
        finally
        {
            try
            {
                if (null != file)
                {
                    file.close();
                }
            }
            catch (IOException e)
            {
                Logger.i(e.getMessage(), " fail");
            }
        }
        
    }
    
    /**
     * 网络读取下载字节
     * @throws TaskException
     */
    private void readBytes() throws TaskException
    {
        ByteArrayOutputStream bos = null;
        try
        {
            bos = new ByteArrayOutputStream();
            int ch = 0;
            byte[] d = new byte[BYTE_LENGTH];
            while ((ch = is.read(d)) != -1)
            {
                bos.write(d, 0, ch);
            }
            mDownloadTask.setFileByteBuf(bos.toByteArray());
        }
        catch (IOException e)
        {
            Logger.i(e.getMessage(), " fail");
            throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
        }
        finally
        {
            try
            {
                bos.close();
            }
            catch (IOException e)
            {
                Logger.i(e.getMessage(), " fail");
            }
        }
    }
    
}
