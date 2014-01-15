/*
 * 文件名: Response.java
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

/**
 * 连接服务器后并获取数据后封装的响应对象
 */
public class Response
{
    /**
     * 定义http连接返回的响应码 
     */
    public enum ResponseCode
    {
        /**
         * 操作成功
         */
        Succeed,
        
        /**
         * 超时
         */
        Timeout,
        
        /**
         * 网络错误
         */
        NetworkError,
        
        /**
         * 鉴权失败
         */
        AuthError,
        
        /**
         * 请求参数错误
         */
        ParamError,
        
        /**
         * 未知错误
         */
        Failed,
        /**
         * 错误请求
         */
        BadRequest,
        /**
         * 401需要鉴权
         */
        UnAuthorized,
        /**
         * 403鉴权未通过
         */
        Forbidden,
        /**
         * 404 请求路径未找到
         */
        NotFound,
        /**
         * 409 服务器在完成请求时发生冲突
         */
        Conflict,
        /**
         * 500 服务器错误
         */
        InternalError
    }
    
    /**
     * 与此Response对应的请求对象
     */
    private Request correspondingRequest;
    
    /**
     * 响应返回码
     */
    private ResponseCode responseCode;
    
    /**
     * 服务器连接正常时返回的数据
     */
    private String data;
    
    /**
     * the data type that the server responded.
     */
    private byte[] byteData;
    
    /**
     * 对服务器返回的data进行封装处理。返回给最终的调用者使用。
     */
    private Object obj;
    
    /**
     * 服务器正常返回时的结果码
     */
    private int resultCode;
    
    /**
     * 服务器正常返回时的结果描述
     */
    private String resultDesc;
    
    /**
     * 获取响应码
     * 
     * @return the responseCode
     */
    public ResponseCode getResponseCode()
    {
        return responseCode;
    }
    
    /**
     * 设置响应码
     * 
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(ResponseCode responseCode)
    {
        this.responseCode = responseCode;
    }
    
    /**
     * 获取服务器返回的数据
     * 
     * @return the data
     */
    public String getData()
    {
        return data;
    }
    
    /**
     * 设置服务器返回的数据
     * 
     * @param data the data to set
     */
    public void setData(String data)
    {
        this.data = data;
    }
    
    /**
     * 获取服务器返回的字节数组
     * @return the byteData
     */
    public byte[] getByteData()
    {
        return byteData;
    }
    
    /**
     * 设置服务器返回数据，类型为字节数组
     * @param byteData the byteData to set
     */
    public void setByteData(byte[] byteData)
    {
        this.byteData = byteData;
    }
    
    /**
     * 获取与此响应相对应的请求对象
     * 
     * @return the correspondingRequest
     */
    public Request getCorrespondingRequest()
    {
        return correspondingRequest;
    }
    
    /**
     * 设置与此响应相对应的请求对象
     * 
     * @param correspondingRequest the correspondingRequest to set
     */
    public void setCorrespondingRequest(Request correspondingRequest)
    {
        this.correspondingRequest = correspondingRequest;
    }
    
    /**
     * 获取最终返回给调用者的对象，一般由逻辑层对obj进行赋值
     * 
     * @return the obj
     */
    public Object getObj()
    {
        return obj;
    }
    
    /**
     * 设置最终返回给调用者的对象
     * 
     * @param obj the obj to set
     */
    public void setObj(Object obj)
    {
        this.obj = obj;
    }
    
    /**
     * get the resultCode
     * 
     * @return the resultCode
     */
    public int getResultCode()
    {
        return resultCode;
    }
    
    /**
     * 
     * 解析服务器返回的数据中result-code字段
     * @param resultCode the resultCode to set
     */
    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }
    
    /**
     * get the resultDesc
     * 
     * @return the resultDesc
     */
    public String getResultDesc()
    {
        return resultDesc;
    }
    
    /**
     * 解析服务器返回的数据中result-desc字段
     * @param resultDesc the resultDesc to set
     */
    public void setResultDesc(String resultDesc)
    {
        this.resultDesc = resultDesc;
    }
    
    /**
     * 
     * @return toString()
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString()
    {
        return "[" + resultCode + "," + resultDesc + "," + data + "]";
    }
    
}
