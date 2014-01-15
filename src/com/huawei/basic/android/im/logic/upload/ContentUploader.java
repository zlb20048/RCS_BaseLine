/*
 * 文件名: ContentUploader.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-5-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.load.TaskManagerFactory;
import com.huawei.basic.android.im.component.load.task.ITask;
import com.huawei.basic.android.im.component.load.task.ITaskManager;
import com.huawei.basic.android.im.component.load.task.ITaskStatusListener;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.upload.http.RcsUploadHttpTask;
import com.huawei.basic.android.im.component.upload.http.UploadContentInfo;
import com.huawei.basic.android.im.logic.upload.UploadFileForURLResponse.UploadFileForURLResult;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 上传业务封装类<BR>
 * 实现直接上传、二次上传功能
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-5-14] 
 */
public class ContentUploader
{
    /**
     * DEBUG TAG.
     */
    private static final String TAG = "ContentUploader";
    
    /**
     * 直接在请求中携带上传文件内容，此时，要求文件大小小于100K
     */
    private static final int MAX_SIZE = 100 * 1024;
    
    /**
     * 上传监听器
     */
    private IUploadListener uploadListener;
    
    /**
     * 上传参数
     */
    private UploadParam uploadParam;
    
    /**
     * 下载、上传任务管理器对象，用于下载和上传。在增加状态监听器时，都使用各任务私有的添加监听器方法。
     */
    private ITaskManager mTaskManager;
    
    /**
     * 构造
     */
    public ContentUploader()
    {
        mTaskManager = TaskManagerFactory.getTaskManager();
    }
    
    /**
     * 
     * 上传方法<BR>
     * 
     * @param aUploadParam 上传参数
     * @param aUploadListener 上传监听器
     */
    public void upload(UploadParam aUploadParam, IUploadListener aUploadListener)
    {
        this.uploadParam = aUploadParam;
        this.uploadListener = aUploadListener;
        
        // 1.先构造UploadTask对象，用于一次上传
        RcsUploadHttpTask uploadTask = new RcsUploadHttpTask();
        
        // 上传url
        uploadTask.setUploadUrl(FusionConfig.getInstance()
                .getAasResult()
                .getRifurl()
                + "richlifeApp/extIntf/IUploadAndDownload");
        // header设置
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", FusionConfig.getInstance()
                .getOseReqAuthorization());
        uploadTask.setHeaders(headers);
        // 请求体
        uploadTask.setBytesBuf(generateRequestBody(aUploadParam).getBytes());
        
        // 2.根据上传大小确定使用一次上传还是二次上传
        // 直接上传
        if (aUploadParam.getTotalSize() < MAX_SIZE)
        {
            Logger.d(TAG,
                    "the size of upload file is less than 100KB, upload directly.");
            uploadDirect(uploadTask);
        }
        
        // 使用二次上传方式
        else
        {
            Logger.d(TAG,
                    "the size of upload file is more than 100KB, upload redirectly.");
            uploadRedirect(uploadTask);
        }
    }
    
    /**
     * 
     * 直接上传<BR>
     * 一次性完成上传
     * 
     * @param uploadTask RcsUploadHttpTask
     */
    private void uploadDirect(final RcsUploadHttpTask uploadTask)
    {
        uploadTask.setUploadContentInfoList(uploadParam.getUploadContentInfoList());
        uploadTask.setUploadType(RcsUploadHttpTask.UploadType.DIRECT_UPLOAD);
        // 给该任务单独添加任务状态监听器
        uploadTask.addOwnerStatusListener(new ITaskStatusListener()
        {
            @Override
            public void onChangeStatus(ITask loadTask)
            {
                // 如果不是该任务，则返回
                if (!(loadTask instanceof RcsUploadHttpTask)
                        && loadTask.getId() != uploadTask.getId())
                {
                    return;
                }
                switch (loadTask.getStatus())
                {
                    case ITask.TASK_STATUS_NEW:
                        Logger.d(TAG, "[DOWNLOAD]NEW TASK:" + loadTask.getId());
                        break;
                    
                    case ITask.TASK_STATUS_RUNNING:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS RUNNING:" + loadTask.getId());
                        uploadListener.onUploadStart();
                        break;
                    
                    case ITask.TASK_STATUS_PROCESS:
                        //通知页面 下载进度
                        uploadListener.onUploadProgress((int) loadTask.getCurrentSize(),
                                (int) loadTask.getTotalSize(),
                                loadTask.getPercent());
                        break;
                    
                    case ITask.TASK_STATUS_WARTING:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS WAITING:" + loadTask.getId());
                        uploadListener.onUploadPause();
                        break;
                    
                    case ITask.TASK_STATUS_FINISHED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS FINISHED:"
                                        + loadTask.getId());
                        RcsUploadHttpTask dlHttpTask = (RcsUploadHttpTask) loadTask;
                        
                        // 解析服务器返回数据
                        // 根据返回码确定本次上传是否成功
                        UploadFileForURLResponse response = parseResponseData(dlHttpTask.getResponseData());
                        if (response != null)
                        {
                            if (response.getResultCode() == UploadFileForURLResponse.RESULT_CODE_SUCCESS)
                            {
                                uploadListener.onUploadFinish(response);
                            }
                            else
                            {
                                uploadListener.onUploadFail(String.valueOf(response.getResultCode()));
                            }
                        }
                        else
                        {
                            uploadListener.onUploadFail("Upload Failed!");
                        }
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_DELETED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS DELETED:" + loadTask.getId());
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_ERROR:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK MET ERROR:" + loadTask.getId());
                        uploadListener.onUploadFail("UPLOAD FAILED!");
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_STOPPED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS STOPED:" + loadTask.getId());
                        //发消息给页面，告诉它下载已经暂停
                        uploadListener.onUploadPause();
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    default:
                        break;
                }
            }
        });
        try
        {
            mTaskManager.createTask(uploadTask);
            mTaskManager.startTask(uploadTask.getId());
        }
        catch (TaskException e)
        {
            Logger.e(TAG, "create or start task met error!", e);
        }
    }
    
    /**
     * 
     * 采用二次上传<BR>
     * 
     * @param uploadTask RcsUploadHttpTask
     */
    private void uploadRedirect(final RcsUploadHttpTask uploadTask)
    {
        // 先上传request body，无实体文件上传，  服务器返回redirectUrl等信息，再根据该信息上传实体文件
        uploadTask.setUploadType(RcsUploadHttpTask.UploadType.REDIRECT_UPLOAD_FIRST_PHASE);
        uploadTask.addOwnerStatusListener(new ITaskStatusListener()
        {
            @Override
            public void onChangeStatus(ITask loadTask)
            {
                // 不是该任务的回调则返回
                if (!(loadTask instanceof RcsUploadHttpTask)
                        && loadTask.getId() != uploadTask.getId())
                {
                    return;
                }
                switch (loadTask.getStatus())
                {
                    case ITask.TASK_STATUS_NEW:
                        Logger.d(TAG, "[DOWNLOAD]NEW TASK:" + loadTask.getId());
                        break;
                    
                    case ITask.TASK_STATUS_RUNNING:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS RUNNING:" + loadTask.getId());
                        uploadListener.onUploadStart();
                        break;
                    
                    case ITask.TASK_STATUS_PROCESS:
                        // 二次上传第一阶段不回调进度
                        break;
                    
                    case ITask.TASK_STATUS_WARTING:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS WAITING:" + loadTask.getId());
                        uploadListener.onUploadPause();
                        break;
                    
                    case ITask.TASK_STATUS_FINISHED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS FINISHED:"
                                        + loadTask.getId());
                        RcsUploadHttpTask dlHttpTask = (RcsUploadHttpTask) loadTask;
                        
                        // 解析服务器返回数据
                        // 根据返回码确定本次上传是否成功
                        UploadFileForURLResponse response = parseResponseData(dlHttpTask.getResponseData());
                        if (response != null)
                        {
                            if (response.getResultCode() == UploadFileForURLResponse.RESULT_CODE_SUCCESS)
                            {
                                
                                // 开始上传文件（二次上传）
                                secondUpload(response,
                                        response.getUploadFileForURLResultList());
                            }
                            else
                            {
                                uploadListener.onUploadFail(String.valueOf(response.getResultCode()));
                            }
                        }
                        else
                        {
                            uploadListener.onUploadFail("Upload Failed!");
                        }
                        
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_DELETED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS DELETED:" + loadTask.getId());
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_ERROR:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK MET ERROR:" + loadTask.getId());
                        uploadListener.onUploadFail("UPLOAD FAILED!");
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    case ITask.TASK_STATUS_STOPPED:
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS STOPED:" + loadTask.getId());
                        //发消息给页面，告诉它下载已经暂停
                        uploadListener.onUploadPause();
                        uploadTask.removeOwnerStatusListener(this);
                        break;
                    
                    default:
                        break;
                }
            }
        });
        try
        {
            mTaskManager.createTask(uploadTask);
            mTaskManager.startTask(uploadTask.getId());
        }
        catch (TaskException e)
        {
            Logger.e(TAG, "[ERROR INFO]" + e.getMessage());
        }
    }
    
    /**
     * 
     * [文件上传接口]的请求体封装，不区分一次上传还是需要进行二次上传<BR>
     * 
     * @param aUploadParam UploadParam
     * @return XML of request body
     */
    private String generateRequestBody(UploadParam aUploadParam)
    {
        StringBuffer bodyBuffer = new StringBuffer();
        bodyBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bodyBuffer.append("<uploadFileForURL>");
        
        // account
        bodyBuffer.append("<account>");
        bodyBuffer.append(aUploadParam.getUserAccount());
        bodyBuffer.append("</account>");
        
        // operation
        bodyBuffer.append("<operation>");
        bodyBuffer.append(aUploadParam.getUploadType());
        bodyBuffer.append("</operation>");
        /* 
         // receivertype
         bodyBuffer.append("<receivertype>");
         bodyBuffer.append(uploadParam.getReceiverType());
         bodyBuffer.append("</receivertype>");
         
         // receiver
         bodyBuffer.append("<receiver>");
         bodyBuffer.append(uploadParam.getReceiver());
         bodyBuffer.append("</receiver>");
         */
        // fileCount
        bodyBuffer.append("<fileCount>");
        bodyBuffer.append(aUploadParam.getFileCount());
        bodyBuffer.append("</fileCount>");
        
        // totalSize
        bodyBuffer.append("<totalSize>");
        bodyBuffer.append(aUploadParam.getTotalSize());
        bodyBuffer.append("</totalSize>");
        
        // uploadContentList
        bodyBuffer.append("<uploadContentList length=\"");
        bodyBuffer.append(aUploadParam.getFileCount());
        bodyBuffer.append("\">");
        if (aUploadParam.getUploadContentInfoList() != null)
        {
            for (UploadContentInfo uploadFileInfo : aUploadParam.getUploadContentInfoList())
            {
                
                // uploadContentInfo
                bodyBuffer.append("<uploadContentInfo>");
                
                // contentName
                bodyBuffer.append("<contentName>");
                bodyBuffer.append(uploadFileInfo.getContentName());
                bodyBuffer.append("</contentName>");
                
                // contentSize
                bodyBuffer.append("<contentSize>");
                bodyBuffer.append(uploadFileInfo.getContentSize());
                bodyBuffer.append("</contentSize>");
                
                // contentDesc
                bodyBuffer.append("<contentDesc>");
                bodyBuffer.append(uploadFileInfo.getContentDesc());
                bodyBuffer.append("</contentDesc>");
                
                bodyBuffer.append("</uploadContentInfo>");
            }
        }
        bodyBuffer.append("</uploadContentList>");
        bodyBuffer.append("</uploadFileForURL>");
        return bodyBuffer.toString();
    }
    
    /**
     * 
     * 上传流程第二步：上传文件阶段<BR>
     * 
     * @param response 二次上传第一步服务器返回的数据解析对象
     * @param results 返回的针对每个文件的描述信息及该文件上传URL
     */
    private void secondUpload(final UploadFileForURLResponse response,
            List<UploadFileForURLResult> results)
    {
        if (results != null)
        {
            // 某个上传任务的上传文件阶段，使用同一个任务监听器对象
            SecondUploadListener listener = new SecondUploadListener(
                    results.size(), response);
            for (UploadFileForURLResult result : results)
            {
                
                // 每个文件的上传，对应创建一个上传任务
                RcsUploadHttpTask uploadTask = new RcsUploadHttpTask();
                uploadTask.setUploadType(RcsUploadHttpTask.UploadType.REDIRECT_UPLOAD_SECOND_PHASE);
                UploadContentInfo fileInfo = matchContentInfo(result.getContentName());
                uploadTask.setUploadUrl(result.getRedirectionUrl());
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", FusionConfig.getInstance()
                        .getOseReqAuthorization());
                headers.put("uploadTaskID", result.getUploadTaskID());
                headers.put("contentSize",
                        String.valueOf(fileInfo.getContentSize()));
                headers.put("Range", "bytes=0-" + fileInfo.getContentSize());
                uploadTask.setHeaders(headers);
                uploadTask.addUploadContentInfo(fileInfo);
                
                // 上面创建的监听器持有该上传任务
                listener.addTask(uploadTask);
                
                // 给该任务添加自己的监听器
                uploadTask.addOwnerStatusListener(listener);
                
                // 开始任务
                try
                {
                    mTaskManager.createTask(uploadTask);
                    mTaskManager.startTask(uploadTask.getId());
                }
                catch (TaskException e)
                {
                    Logger.e(TAG, "create or start task met error!", e);
                }
            }
        }
    }
    
    /**
     * 
     * 根据文件名找到对应的UploadContentInfo对象<BR>
     * 
     * @param contentName 文件名
     * @return UploadContentInfo
     */
    private UploadContentInfo matchContentInfo(String contentName)
    {
        List<UploadContentInfo> list = uploadParam.getUploadContentInfoList();
        if (list != null)
        {
            for (UploadContentInfo fileInfo : list)
            {
                if (fileInfo.getContentName() != null
                        && fileInfo.getContentName().equals(contentName))
                {
                    return fileInfo;
                }
            }
        }
        return null;
    }
    
    /**
     * 
     * 解析服务器返回的数据<BR>
     *  
     * @param responseData 服务器返回的数据
     * @return UploadFileForURLResponse
     */
    private UploadFileForURLResponse parseResponseData(byte[] responseData)
    {
        UploadFileForURLResponse response;
        try
        {
            response = new XmlParser().parseXmlString(UploadFileForURLResponse.class,
                    new String(responseData));
        }
        catch (Exception e)
        {
            Logger.e(TAG, "parse xml error!", e);
            response = null;
        }
        return response;
    }
    
    /**
     * 二次上传的上传监听器
     * <BR>
     * 某个上传流程的第二步，多个文件的上传采用统一一个状态监听器对象
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    private class SecondUploadListener implements ITaskStatusListener
    {
        
        /**
         * 本次上传的文件个数
         */
        private int uploadCount;
        
        /**
         * 上传后服务器下发数据解析对象
         */
        private UploadFileForURLResponse response;
        
        /**
         * 二次上传使用同一个监听器，保存下二次上传的任务对象集合
         */
        private List<RcsUploadHttpTask> uploadTaskList;
        
        /**
         * 
         * 构造函数
         * @param uploadCount 本次上传的文件个数
         * @param response UploadFileForURLResponse
         */
        public SecondUploadListener(int uploadCount,
                UploadFileForURLResponse response)
        {
            super();
            this.uploadCount = uploadCount;
            this.response = response;
            uploadTaskList = new ArrayList<RcsUploadHttpTask>();
        }
        
        /**
         * 
         * 添加上传任务到集合中<BR>
         *  
         * @param task 上传任务
         */
        public void addTask(RcsUploadHttpTask task)
        {
            uploadTaskList.add(task);
        }
        
        @Override
        public void onChangeStatus(ITask loadTask)
        {
            // 如果是上传任务并且在本监听器该处理的上传任务集合中才处理
            if (!(loadTask instanceof RcsUploadHttpTask)
                    && !uploadTaskList.contains(loadTask))
            {
                return;
            }
            switch (loadTask.getStatus())
            {
                case ITask.TASK_STATUS_NEW:
                    Logger.d(TAG, "[DOWNLOAD]NEW TASK:" + loadTask.getId());
                    break;
                
                case ITask.TASK_STATUS_RUNNING:
                    Logger.d(TAG,
                            "[DOWNLOAD]TASK IS RUNNING:" + loadTask.getId());
                    uploadListener.onUploadStart();
                    break;
                
                case ITask.TASK_STATUS_PROCESS:
                    //通知页面 下载进度
                    RcsUploadHttpTask task = (RcsUploadHttpTask) loadTask;
                    // 上传主文件才刷新进度
                    if (task.getUploadContentInfoList().get(0).isMainContent())
                    {
                        uploadListener.onUploadProgress((int) loadTask.getCurrentSize(),
                                (int) loadTask.getTotalSize(),
                                loadTask.getPercent());
                    }
                    break;
                
                case ITask.TASK_STATUS_WARTING:
                    Logger.d(TAG,
                            "[DOWNLOAD]TASK IS WAITING:" + loadTask.getId());
                    uploadListener.onUploadPause();
                    break;
                
                case ITask.TASK_STATUS_FINISHED:
                    // 二次上传时所有文件的上传都是此监听器处理，因此在对uploadCount进行操作时需要加上同步锁
                    synchronized (this)
                    {
                        uploadCount--;
                        Logger.d(TAG,
                                "[DOWNLOAD]TASK IS FINISHED:"
                                        + loadTask.getId());
                        RcsUploadHttpTask dlHttpTask = (RcsUploadHttpTask) loadTask;
                        
                        String resultCode = StringUtil.getXmlValue(new String(
                                dlHttpTask.getResponseData()), "resultCode");
                        
                        // 成功上传
                        if ("0".equals(resultCode))
                        {
                            if (uploadCount <= 0)
                            {
                                uploadListener.onUploadFinish(response);
                                removeStatusListener();
                            }
                        }
                        // 上传失败
                        else
                        {
                            uploadListener.onUploadFail(resultCode);
                            removeStatusListener();
                        }
                    }
                    break;
                
                case ITask.TASK_STATUS_DELETED:
                    Logger.d(TAG,
                            "[DOWNLOAD]TASK IS DELETED:" + loadTask.getId());
                    removeStatusListener();
                    break;
                
                case ITask.TASK_STATUS_ERROR:
                    Logger.d(TAG,
                            "[DOWNLOAD]TASK MET ERROR:" + loadTask.getId());
                    uploadListener.onUploadFail("UPLOAD FAILED!");
                    removeStatusListener();
                    break;
                
                case ITask.TASK_STATUS_STOPPED:
                    Logger.d(TAG,
                            "[DOWNLOAD]TASK IS STOPED:" + loadTask.getId());
                    //发消息给页面，告诉它下载已经暂停
                    uploadListener.onUploadPause();
                    removeStatusListener();
                    break;
                
                default:
                    break;
            }
        }
        
        /**
         * 
         * 上传结束，移除所有监听器<BR>
         *
         */
        private void removeStatusListener()
        {
            for (RcsUploadHttpTask task : uploadTaskList)
            {
                task.removeOwnerStatusListener(this);
            }
        }
    }
}
