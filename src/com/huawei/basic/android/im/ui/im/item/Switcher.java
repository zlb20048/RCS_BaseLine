/*
 * 文件名: Switcher.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;

/**
 * 转换类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-13] 
 */
public final class Switcher
{
    private static final String TAG = "Switcher";
    
    /**
     * 
     * 将messageModel中的消息类型转换成ItemCreator中定义的类型值<BR>
     * 
     * @param msgType 消息类型
     * @param mediaType 媒体类型
     * @return 消息类型
     */
    public static final int switchMsgType(int msgType, int mediaType)
    {
        
        int type = -1;
        
        switch (msgType)
        {
            case BaseMessageModel.MSGTYPE_TEXT:
                type = BaseMsgItem.MsgType.TEXT;
                break;
            case BaseMessageModel.MSGTYPE_SYSTEM:
                type = BaseMsgItem.MsgType.SYSTEM;
                break;
            case BaseMessageModel.MSGTYPE_MEDIA:
                switch (mediaType)
                {
                    case MediaIndexModel.MEDIATYPE_IMG:
                        type = BaseMsgItem.MsgType.IMG;
                        break;
                    case MediaIndexModel.MEDIATYPE_AUDIO:
                        type = BaseMsgItem.MsgType.AUDIO;
                        break;
                    case MediaIndexModel.MEDIATYPE_VIDEO:
                        type = BaseMsgItem.MsgType.VIDEO;
                        break;
                    case MediaIndexModel.MEDIATYPE_EMOJI:
                        type = BaseMsgItem.MsgType.EMOJI;
                        break;
                    case MediaIndexModel.MEDIATYPE_LOCATION:
                        type = BaseMsgItem.MsgType.LOCATION;
                        break;
                    case MediaIndexModel.MEDIATYPE_UNKNOWN:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        if (type == -1)
        {
            Logger.e(TAG, "Unknown msg type! msgType:" + msgType
                    + ";subMsgType:" + mediaType);
        }
        return type;
    }
    
    /**
     * 
     * 将messageModel中的消息类型转换成ItemCreator中定义的类型值<BR>
     * 
     * @param typeSendOrReceiveFromDB 消息类型
     * @return 消息类型
     */
    public static final int switchTypeSendOrReceive(int typeSendOrReceiveFromDB)
    {
        switch (typeSendOrReceiveFromDB)
        {
            case MessageModel.MSGSENDORRECV_SEND:
                return BaseMsgItem.SendOrReceive.SEND;
            case MessageModel.MSGSENDORRECV_RECV:
                return BaseMsgItem.SendOrReceive.RECV;
            default:
                Logger.e(TAG, "Unknown type!");
                return -1;
        }
    }
}
