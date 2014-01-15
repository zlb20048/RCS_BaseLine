/*
 * 文件名: XmppResultCode.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

/**
 * XMPP返回码定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public interface XmppResultCode
{
    
    /**
     * 
     * 常规XMPP返回码定义<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-2-14]
     */
    interface Base
    {
        
        /**
         * 返回码基数
         */
        int FAST_ERR_BASEID = 1000;
        
        /**
         * 成功
         */
        int FAST_ERR_SUCCESS = 0;
        
        /**
         * 失败
         */
        int FAST_ERR_FAILURE = -1;
        
        /**
         * 未知错误
         */
        int FAST_ERR_UNKOWN = FAST_ERR_BASEID + 0;
        
        /**
         * 参数错误，如指针为空
         */
        int FAST_ERR_PARAMS = FAST_ERR_BASEID + 1;
        
        /**
         * 解析错误
         */
        int FAST_ERR_PARSER = FAST_ERR_BASEID + 2;
        
        /**
         * 超时错误
         */
        int FAST_ERR_TIMEOUT = FAST_ERR_BASEID + 3;
        
        /**
         * 发送错误
         */
        int FAST_ERR_SEND = FAST_ERR_BASEID + 4;
        
        /**
         * /网络异常(无法获得、中断等),注册过程中返回ERROR_STREAM 时调用
         */
        int FAST_ERR_NET_ERROR = FAST_ERR_BASEID + 10; 
        
        /**
         *  主机不可达，需要重连
         */
        int FAST_ERR_HOST_UNREACHABLE = FAST_ERR_BASEID + 11;
        
        /**
         * 服务器主动断开客户端连接
         */
        int FAST_ERR_SERVER_CONNECTION_CLOSED = FAST_ERR_BASEID + 12;
    }
    
    /**
     * XMPP注册模块特定的返回码定义
     * <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-2-14]
     */
    interface Register extends Base
    {
        
        /**
         *  鉴权失败
         */
        int FAST_ERR_NOT_AUTHORIZED = FAST_ERR_BASEID + 101;
        
        /**
         * 账号冲突,通常是相同账号登陆导致注销
         */
        int FAST_ERR_CONFLICT = FAST_ERR_BASEID + 102;
        
        /**
         * XMPP Ping 心跳超时导致注销
         */
        int FAST_ERR_HEARTBEAT_TIMEOUT = FAST_ERR_BASEID + 103;
        
        /**
         * 未登陆
         */
        int FAST_ERR_NOT_REGISTERED = FAST_ERR_BASEID + 104;
        
        /**
         * session不存在，调用CMD命令时在出参中返回
         */
        int FAST_ERR_SESSION_ID_NOT_EXIST = FAST_ERR_BASEID + 201;
        
    }

}
