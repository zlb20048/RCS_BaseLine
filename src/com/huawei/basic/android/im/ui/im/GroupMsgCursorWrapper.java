/*
 * 文件名: GroupMsgCursorWrapper.java
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

import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMessageColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-9] 
 */
public class GroupMsgCursorWrapper extends BaseMsgCursorWrapper
{
    
    /**
     * [构造简要说明]
     * @param cursor cursor
     */
    public GroupMsgCursorWrapper(Cursor cursor)
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
        GroupMessageModel info = new GroupMessageModel();
        info.setGroupId(getString(getColumnIndex(GroupMessageColumns.GROUP_ID)));
        info.setMsgId(getString(getColumnIndex(GroupMessageColumns.MSG_ID)));
        info.setMsgSequence(getString(getColumnIndex(GroupMessageColumns.MSG_SEQUENCE)));
        info.setMemberUserId(getString(getColumnIndex(GroupMessageColumns.MEMBER_USERID)));
        info.setMemberNick(getString(getColumnIndex(GroupMessageColumns.MEMBER_NAME)));
        info.setMsgTime(getString(getColumnIndex(GroupMessageColumns.MSG_TIME)));
        info.setMsgType(getInt(getColumnIndex(GroupMessageColumns.MSG_TYPE)));
        info.setMsgContent(getString(getColumnIndex(GroupMessageColumns.MSG_CONTENT)));
        info.setMsgStatus(getInt(getColumnIndex(GroupMessageColumns.MSG_STATUS)));
        info.setMsgSendOrRecv(getInt(getColumnIndex(GroupMessageColumns.MSG_SENDORRECV)));
        int msgType = getInt(getColumnIndex(MessageColumns.MSG_TYPE));
        if (MessageModel.MSGTYPE_MEDIA == msgType)
        {
            MediaIndexModel media = new MediaIndexModel();
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
