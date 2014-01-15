/*
 * 文件名: MsgCursorWrapper.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import android.database.Cursor;

import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-9] 
 */
public class MsgCursorWrapper extends BaseMsgCursorWrapper
{
    
    /**
     * [构造简要说明]
     * @param cursor cursor
     */
    public MsgCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseMsgCursorWrapper#parseMsgModel()
     */
    
    @Override
    public BaseMessageModel parseMsgModel()
    {
        MessageModel info = new MessageModel();
        String msgId = getString(getColumnIndex(MessageColumns.MSG_ID));
        info.setMsgId(msgId);
        info.setMsgSequence(getString(getColumnIndex(MessageColumns.MSG_SEQUENCE)));
        info.setFriendUserId(getString(getColumnIndex(MessageColumns.FRIEND_USERID)));
        info.setMsgSendOrRecv(getInt(getColumnIndex(MessageColumns.MSG_SENDORRECV)));
        info.setMsgTime(getString(getColumnIndex(MessageColumns.MSG_TIME)));
        info.setMsgStatus(getInt(getColumnIndex(MessageColumns.MSG_STATUS)));
        int msgType = getInt(getColumnIndex(MessageColumns.MSG_TYPE));
        info.setMsgType(msgType);
        info.setMsgContent(getString(getColumnIndex(MessageColumns.MSG_CONTENT)));
        //         如果消息类型是多媒体，则对多媒体信息表进行查询，查找相应记录
        if (MessageModel.MSGTYPE_MEDIA == msgType)
        {
            MediaIndexModel media = new MediaIndexModel();
            // 判断多媒体信息在多媒体信息表中是否存在
            media.setMsgId(msgId);
            media.setMediaType(getInt(getColumnIndex(MediaIndexColumns.MEDIA_TYPE)));
            media.setMediaSize(getString(getColumnIndex(MediaIndexColumns.MEDIA_SIZE)));
            media.setMediaPath(getString(getColumnIndex(MediaIndexColumns.MEDIA_PATH)));
            media.setMediaSmallPath(getString(getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH)));
            media.setMediaURL(getString(getColumnIndex(MediaIndexColumns.MEDIA_URL)));
            media.setMediaSmallURL(getString(getColumnIndex(MediaIndexColumns.MEDIA_SMALL_URL)));
            media.setPlayTime(getInt(getColumnIndex(MediaIndexColumns.PLAY_TIME)));
            media.setMediaAlt(getString(getColumnIndex(MediaIndexColumns.MEDIA_ALT)));
            media.setLocationLat(getString(getColumnIndex(MediaIndexColumns.LOCATION_LAT)));
            media.setLocationLon(getString(getColumnIndex(MediaIndexColumns.LOCATION_LON)));
            info.setMediaIndex(media);
        }
        return info;
    }
    
}
