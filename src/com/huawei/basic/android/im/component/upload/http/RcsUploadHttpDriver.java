/*
 * 文件名: RcsUploadHttpDriver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-5-7
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-5-7] 
 */
public class RcsUploadHttpDriver extends UploadHttpDriver
{
    private static final String TAG = "RcsUploadHttpDriver";
    
    private RcsUploadHttpTask mTask;
    
    RcsUploadHttpDriver(RcsUploadHttpTask task)
    {
        mTask = task;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.upload.http.UploadHttpDriver#connect()
     */
    
    @Override
    public void connect() throws TaskException
    {
        // TODO Auto-generated method stub
        super.connect();
        
        HttpClient client;
        client = new DefaultHttpClient();
        HttpPost post = new HttpPost(mTask.getUploadUrl());
        Logger.d(TAG, "url : " + mTask.getUploadUrl());
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        Map<String, String> header = mTask.getHeaders();
        for (String key : header.keySet())
        {
            String value = header.get(key);
            Logger.d(TAG, key + ":" + value);
            post.setHeader(key, value);
        }
        
        byte[] reqBody = mTask.getBytesBuf();
        List<UploadContentInfo> contentInfoList = mTask.getUploadContentInfoList();
        
        HttpEntity entity = null;
        FinishedListener listener = new FinishedListener()
        {
            
            @Override
            public void onFinish(int finished)
            {
                mTask.setCurrentSize(finished);
                try
                {
                    mTask.onProgress();
                }
                catch (TaskException e)
                {
                    e.printStackTrace();
                }
            }
        };
        
        switch (mTask.getUploadType())
        {
            case RcsUploadHttpTask.UploadType.DIRECT_UPLOAD:
                BasicMultipartEntity multiEntity = new BasicMultipartEntity(
                        listener);
                // 直接上传需要request body及文件列表
                if (reqBody == null || reqBody.length == 0
                        || contentInfoList == null
                        || contentInfoList.size() == 0)
                {
                    Logger.e(TAG, "[DIRECT_UPLOAD]request parameters error!");
                    throw new TaskException(TaskException.START_TASK_FAILED);
                }
                
                // 添加请求xml
                multiEntity.addPart("request body", new ByteArrayBody(reqBody,
                        "text/xml", "request body"));
                
                for (int i = 0; i < contentInfoList.size(); i++)
                {
                    UploadContentInfo info = contentInfoList.get(i);
                    // 文件
                    if (info.getFilePath() != null)
                    {
                        File file = new File(info.getFilePath());
                        if (file == null || !file.exists())
                        {
                            Logger.e(TAG, "[addPart]file is not existed!");
                            throw new TaskException(
                                    TaskException.START_TASK_FAILED);
                        }
                        multiEntity.addPart(String.valueOf(i), new FileBody(
                                file, info.getMimeType()));
                    }
                    // 数据
                    else if (info.getData() != null)
                    {
                        multiEntity.addPart(String.valueOf(i),
                                new ByteArrayBody(info.getData(),
                                        info.getMimeType(),
                                        info.getContentName()));
                    }
                    else
                    {
                        Logger.e(TAG, "[addPart]info data & path are NULL!");
                        throw new TaskException(TaskException.START_TASK_FAILED);
                    }
                }
                entity = multiEntity;
                break;
            case RcsUploadHttpTask.UploadType.REDIRECT_UPLOAD_FIRST_PHASE:
                // 间接上传第一阶段仅上传request body
                BasicMultipartEntity reqEntity = new BasicMultipartEntity(
                        listener);
                if (reqBody == null || reqBody.length == 0)
                {
                    Logger.e(TAG,
                            "[REDIRECT_UPLOAD_FIRST_PHASE]request parameters error!");
                    throw new TaskException(TaskException.START_TASK_FAILED);
                }
                // 添加请求xml
                reqEntity.addPart("request body", new ByteArrayBody(reqBody,
                        "text/xml", "request body"));
                entity = reqEntity;
                break;
            case RcsUploadHttpTask.UploadType.REDIRECT_UPLOAD_SECOND_PHASE:
                
                // 间接上传第二阶段仅上传实体内容，且仅上传一个实体内容
                if (contentInfoList == null || contentInfoList.size() != 1)
                {
                    Logger.e(TAG,
                            "[REDIRECT_UPLOAD_SECOND_PHASE]request parameters error!");
                    throw new TaskException(TaskException.START_TASK_FAILED);
                }
                UploadContentInfo info = contentInfoList.get(0);
                // 文件
                if (info.getFilePath() != null)
                {
                    File file = new File(info.getFilePath());
                    if (file == null || !file.exists())
                    {
                        Logger.e(TAG, "[addPart]file is not existed!");
                        throw new TaskException(TaskException.START_TASK_FAILED);
                    }
                    BasicFileEntity fileEntity = new BasicFileEntity(file,
                            info.getMimeType() + ",name="
                                    + info.getContentName(), listener);
                    entity = fileEntity;
                }
                // 数据
                else if (info.getData() != null)
                {
                    BasicByteArrayEntity byteArrayEntity = new BasicByteArrayEntity(
                            info.getData(), listener);
                    byteArrayEntity.setContentType(info.getMimeType()
                            + ",name=" + info.getContentName());
                    entity = byteArrayEntity;
                }
                else
                {
                    Logger.e(TAG, "[addPart]info data & path are NULL!");
                    throw new TaskException(TaskException.START_TASK_FAILED);
                }
                
                break;
            default:
                if (contentInfoList == null || contentInfoList.size() != 1)
                {
                    Logger.e(TAG,
                            "[UNKNOWN UPLOAD TYPE]request parameters error!");
                    throw new TaskException(TaskException.START_TASK_FAILED);
                }
                break;
        }
        long totalSize = entity.getContentLength();
        Logger.d(TAG, "[upload total size]" + totalSize);
        mTask.setTotalSize(totalSize);
        post.setEntity(entity);
        
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        try
        {
            Logger.d(TAG, "start connect()");
            HttpResponse response = client.execute(post);
            int resultCode = response.getStatusLine().getStatusCode();
            Logger.d(TAG, "response " + resultCode);
            byte[] resultData = null;
            
            inputStream = response.getEntity().getContent();
            if (inputStream != null)
            {
                bos = new ByteArrayOutputStream();
                
                byte[] data = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(data)) > -1)
                {
                    bos.write(data, 0, length);
                }
                resultData = bos.toByteArray();
                Logger.d(TAG, new String(resultData));
                mTask.setResponseData(resultData);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.e(TAG, "[NET]" + e.getMessage());
            throw new TaskException(TaskException.WRITE_FILE_FAILED);
        }
        finally
        {
            try
            {
                if (bos != null)
                {
                    bos.close();
                    bos = null;
                }
                if (inputStream != null)
                {
                    inputStream.close();
                    inputStream = null;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.upload.http.UploadHttpDriver#read()
     */
    
    @Override
    public void read() throws TaskException
    {
        // TODO Auto-generated method stub
        super.read();
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.upload.http.UploadHttpDriver#close()
     */
    
    @Override
    public void close()
    {
        // TODO Auto-generated method stub
        super.close();
    }
    
}
