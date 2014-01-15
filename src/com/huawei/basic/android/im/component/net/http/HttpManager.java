/*
 * 文件名: HttpManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: admin
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 业务逻辑层与HTTP层的结合类，负责调用HTTP模块发起请求
 * HttpManager必须子类化才能使用。子类至少需要实现getUrl()和getBody()两个方法。
 * 
 */
public abstract class HttpManager
{
    
    /**
     * debug tag
     */
    private static final String TAG = "HttpManager";
    
    /**
     * 线程池最大线程数:5
     */
    private static final int THREAD_POOL_MAX_SIZE = 5;
    
    private static Executor sFixedThreadPoolExecutor = Executors.newFixedThreadPool(THREAD_POOL_MAX_SIZE);
    
    /**
     * 
     * HTTP请求方法
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数，由调用者自己进行封装，其中的key-value自己去定义
     * @param httpListener HTTP监听器，服务器返回结果会组装成Response对象并通过onResult()方法回调给调用者
     */
    protected void send(final int action,
            final HashMap<String, Object> sendData,
            final IHttpListener httpListener)
    {
        
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                
                // 1.联网进行中
                httpListener.onProgress(true);
                
                // 2.构建Request请求对象
                Request request = buildRequest(action, sendData);
                
                // 3.联网过程，获取Response响应对象
                Response response = HttpConnector.connect(request);
                
                // 连接获取Response对象后如果getResponseCode()为空，说明联网失败，将值设置为Failed
                if (response.getResponseCode() == null)
                {
                    response.setResponseCode(ResponseCode.Failed);
                }
                // 4.在联网正常的情况下，解析数据
                switch (response.getResponseCode())
                {
                    case Succeed:
                    case BadRequest:
                    case UnAuthorized:
                    case Forbidden:
                    case NotFound:
                    case Conflict:
                    case InternalError:
                        parserResult(action, response);
                        Logger.d(TAG,
                                "response:result code["
                                        + response.getResultCode()
                                        + "] result desc["
                                        + response.getResultDesc() + "]");
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            Logger.d(TAG, "handledata()");
                            response.setObj(handleData(action,
                                    sendData,
                                    response));
                        }
                        break;
                    default:
                        break;
                }
                
                // 5.回调
                httpListener.onResult(action, response);
                
                //6.关闭联网提示
                httpListener.onProgress(false);
            }
            
        };
        sFixedThreadPoolExecutor.execute(runnable);
    }
    
    /**
     * 
     * 构建URL字符串
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return URL字符串
     */
    protected abstract String getUrl(int action, Map<String, Object> sendData);
    
    /**
     * 封装请求消息体
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return 请求消息体字符串，一般为XML或者JSON
     */
    protected abstract String getBody(int action, Map<String, Object> sendData);
    
    /**
     * 
     * 请求method类型
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @return 默认为POST请求
     */
    protected RequestMethod getRequestMethod(int action)
    {
        return RequestMethod.POST;
    }
    
    /**
     * 
     * 请求消息体数据类型
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @return 默认为XML格式
     */
    protected ContentType getContentType(int action)
    {
        return ContentType.JSON;
    }
    
    /**
     * 
     * 请求property
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @return request property list
     */
    protected List<NameValuePair> getRequestProperties(int action)
    {
        List<NameValuePair> requestProperties = new ArrayList<NameValuePair>();
        return requestProperties;
    }
    
    /**
     * 
     * need to set the response data's type to 'byte' instead of 'String'<BR>
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @return  默认不需要byte数组
     */
    protected boolean isNeedByte(int action)
    {
        return false;
    }
    
    /**
     * 
     * 对服务器返回的数据进行解析处理，封装对象
     * 
     * 
     * @param action 请求Action，用来标识不同的请求
     * @param sendData 调用者发送请求时封装的数据
     * @param response 服务器返回数据对象
     * @return 封装后的对象，在onResult()中通过response.getObj()获得。
     */
    protected abstract Object handleData(int action,
            Map<String, Object> sendData, Response response);
    
    /**
     * 
     * 解析服务器返回的错误码及描述
     * @param action 区分不同请求的标志位
     * @param response Response
     */
    protected void parserResult(int action, Response response)
    {
        String data = response.getData();
        if (data != null)
        {
            // JSON
            if (getContentType(action) == ContentType.JSON
                    && !isNeedByte(action))
            {
                try
                {
                    JSONObject rootJsonObj = new JSONObject(data);
                    
                    // 仅解析Result
                    if (rootJsonObj.has("Result"))
                    {
                        JSONObject resultObj = rootJsonObj.getJSONObject("Result");
                        int resultCode = resultObj.getInt("resultCode");
                        String resultDesc = resultObj.getString("resultDesc");
                        
                        response.setResultCode(resultCode);
                        response.setResultDesc(resultDesc);
                    }
                }
                catch (JSONException e)
                {
                    Logger.e(TAG, e.toString());
                }
            }
            
            // XML
            else if (getContentType(action) == ContentType.XML
                    && !isNeedByte(action))
            {
                String strRetCode = StringUtil.getXmlValue(data, "retCode");
                String strRetDesc = StringUtil.getXmlValue(data, "retDesc");
                if (strRetCode != null)
                {
                    response.setResultCode(Integer.parseInt(strRetCode));
                }
                response.setResultDesc(strRetDesc);
            }
        }
    }
    
    /**
     * 构建Request对象 
     * 
     * @return Request
     */
    private Request buildRequest(int action, Map<String, Object> sendData)
    {
        // 封装Request
        Request request = new Request();
        request.setUrl(getUrl(action, sendData));
        request.setBody(getBody(action, sendData));
        request.setRequestMethod(getRequestMethod(action));
        request.setContentType(getContentType(action));
        request.setRequestProperties(getRequestProperties(action));
        request.setNeedByte(isNeedByte(action));
        return request;
    }
}
