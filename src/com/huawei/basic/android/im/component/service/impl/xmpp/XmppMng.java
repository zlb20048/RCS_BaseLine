/*
 * 文件名: XmppMng.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl.xmpp;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.fast.IEngineBridge;

/**
 * XMPP某一单独模块的共有基类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public abstract class XmppMng
{
    /**
     * log tag.
     */
    protected static final String TAG = XmppMng.class.getSimpleName();
    
    /**
     * 某一模块的组件id
     */
    private final String mComponentId;
    
    /**
     * IObserver
     */
    private IObserver mObserver;
    
    /**
     * IEngineBridge
     */
    private IEngineBridge mEngineBridge;
    
    /**
     * 构造函数<BR>
     * 在构造方法中获取该组件管理类对应的组件id，并且订阅该组件的相关服务
     * 
     * @param engineBridge IEngineBridge
     * @param observer IObserver
     */
    public XmppMng(IEngineBridge engineBridge, IObserver observer)
    {
        mEngineBridge = engineBridge;
        mObserver = observer;
        mComponentId = getComponentId();
        Logger.i(TAG, "subscribe XMPP service, component : "
                + this.getClass().getSimpleName());
        subNotify();
    }
    
    /**
     * 
     * 匹配传入的组件id是否与当前mng的组件id相同<BR>
     * 
     * @param componentId 组件id
     * @param notifyID 订阅id
     * @return  匹配返回true
     */
    public boolean matched(String componentId, int notifyID)
    {
        return mComponentId.equals(componentId);
    }
    
    /**
     * 处理指定组件推送的信息
     * <BR>
     * 
     * @param componentID 组件ID
     * @param notifyId 订阅消息id
     * @param data 推送消息字符串
     */
    public abstract void handleNotification(String componentID, int notifyId,
            String data);
    
    /**
     * 获取组件id <BR>
     * 
     * @return 组件id
     */
    protected abstract String getComponentId();
    
    /**
     * 订阅该组件的服务 <BR>
     */
    protected abstract void subNotify();
    /**
     * 
     * 解析XMPP服务器推送过来的字符串消息<BR>
     * 
     * @param type 解析类class
     * @param data 推送消息字符串
     * @param <T> 解析类
     * @return 返回对应的解析类对象
     */
    protected <T> T parseData(Class<? extends T> type, String data)
    {
        try
        {
            return new XmlParser().parseXmlString(type, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.e(TAG, "xmpp xml parse failed!");
            return null;
        }
    }
    
    /**
     * 对常规XMPP返回信息进行处理，针对基本的返回码
     * <BR>
     * 特定返回码由XmppResultCode.Base定义
     * @param baseNtf BaseNotification
     * @return 特定返回码已经由本方法处理，则返回true;反之返回false
     */
    protected boolean handleCommonNotification(BaseNotification baseNtf)
    {
        // XMPP服务器返回码
        int iErrorCode = baseNtf.getErrorCode();
        Logger.i(TAG, "XMPP result code : " + iErrorCode);
        
        boolean bHandled = true;
        switch (iErrorCode)
        {
            
            case XmppResultCode.Base.FAST_ERR_SUCCESS:
                Logger.i(TAG, "XMPP return successfully.");
                break;
            
            case XmppResultCode.Base.FAST_ERR_HOST_UNREACHABLE:
                break;
            
            case XmppResultCode.Base.FAST_ERR_TIMEOUT:
                break;
            
            case XmppResultCode.Base.FAST_ERR_SEND:
                break;
            
            case XmppResultCode.Base.FAST_ERR_NET_ERROR:
                break;
            
            case XmppResultCode.Base.FAST_ERR_SERVER_CONNECTION_CLOSED:
                break;
            
            // 鉴权失败，将AASResult中TOKEN置为空 
            case XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED:
                break;
            case XmppResultCode.Register.FAST_ERR_HEARTBEAT_TIMEOUT:
                break;
            case XmppResultCode.Register.FAST_ERR_NOT_REGISTERED:
                break;
            default:

                bHandled = false;
                break;
        }
        return bHandled;
    }

    /**
     * get mObserver
     * @return the mObserver
     */
    protected IObserver getObserver()
    {
        return mObserver;
    }

    /**
     * get mEngineBridge
     * @return the mEngineBridge
     */
    protected IEngineBridge getEngineBridge()
    {
        return mEngineBridge;
    }
    
}
