/*
 * 文件名: HolderCreater.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 创建Item的工厂类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class ItemCreator
{
    
    /**
     * debug tag
     */
    private static final String TAG = "ItemCreator";
    
    /**
     * 
     * 根据类型创建BaseMsgItem对象<BR>
     * 
     * @param type item类型，类型的组成为：发送/接收类型 | 消息内容类型
     * @param holderEventListener HolderEventListener
     * @return BaseMsgItem
     */
    public static BaseMsgItem creator(int type,
            HolderEventListener holderEventListener)
    {
        int sendOrRecv = type / BaseMsgItem.MSG_TYPE_COUNT;
        int msgType = type % BaseMsgItem.MSG_TYPE_COUNT;
        Logger.d(TAG, "creator()");
        BaseMsgItem msgItem = null;
        switch (msgType)
        {
            case BaseMsgItem.MsgType.TEXT:
                msgItem = new TextItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.SYSTEM:
                msgItem = new SystemEventItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.IMG:
                msgItem = new ImageItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.AUDIO:
                msgItem = new AudioItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.VIDEO:
                msgItem = new VideoMsgItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.EMOJI:
                msgItem = new EmotionItem(holderEventListener);
                break;
            case BaseMsgItem.MsgType.LOCATION:
                msgItem = new LocationItem(holderEventListener);
                break;
            default:
                break;
        }
        
        if (msgItem != null)
        {
            switch (sendOrRecv)
            {
                case BaseMsgItem.SendOrReceive.SEND:
                    msgItem.setTypeSendOrReceive(BaseMsgItem.SendOrReceive.SEND);
                    break;
                case BaseMsgItem.SendOrReceive.RECV:
                    msgItem.setTypeSendOrReceive(BaseMsgItem.SendOrReceive.RECV);
                    break;
                default:
                    break;
            }
            Logger.d(TAG,
                    "msgItem's send or receive :"
                            + msgItem.getTypeSendOrReceive());
        }
        return msgItem;
    }
    
    /**
     * 
     * 获取所有的item展示类型个数<BR>
     * 每种类型包含发送和接收两种展现形式
     * @return 类型总数
     */
    public static int getItemTypeCount()
    {
        return BaseMsgItem.MSG_TYPE_COUNT * BaseMsgItem.SEND_OR_RECV_TYPE_COUNT;
    }
    
    /**
     * 生成ITEM的类型int值
     * <BR>
     * 
     * @param sendOrRecv 发送/接收类型
     * @param msgType 消息内容的类型
     * @return ITEM的类型int值
     */
    public static int buildType(int sendOrRecv, int msgType)
    {
        return sendOrRecv * BaseMsgItem.MSG_TYPE_COUNT + msgType;
    }
}
