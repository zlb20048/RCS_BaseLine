package com.huawei.basic.android.im.logic.adapter.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ConversationColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FriendManagerColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.MessageUtils;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 找朋友小助手(推荐)信息 数据操作适配器 (特别注意：所以的更新记录的操作第一个参数必须为FriendUserId,因为在
 * ContentProvider监听一条记录update操作是，返回的uri需要带friendUserID)<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-20]
 */
public class FriendManagerDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "FriendManagerDbAdapter";
    
    /**
     * FriendManagerDbAdapter对象
     */
    private static FriendManagerDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mContentResolver;
    
    /**
     * 头像插入
     */
    private FaceThumbnailDbAdapter mFaceDbAdapter;
    
    /**
     * 会话操作adapter
     */
    private ConversationDbAdapter mConversationDbAdapter;
    
    /**
     * 上下文
     */
    private Context mContext;
    
    /**
     * 构造方法
     * 
     * @param context
     *            上下文
     */
    private FriendManagerDbAdapter(Context context)
    {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mFaceDbAdapter = FaceThumbnailDbAdapter.getInstance(context);
        mConversationDbAdapter = ConversationDbAdapter.getInstance(context);
    }
    
    /**
     * 获取FriendManagerDbAdapter对象<BR>
     * 单例
     * 
     * @param context
     *            上下文
     * @return FriendManagerDbAdapter
     */
    public static synchronized FriendManagerDbAdapter getInstance(
            Context context)
    {
        if (null == sInstance)
        {
            sInstance = new FriendManagerDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param info
     *            插入对象
     * @param content
     *            需要插入到会话表的内容说明，如果为null，则不插入会话。
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insert(String userSysId, FriendManagerModel info,
            String content, boolean isNeedUnread)
    {
        long result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId) && null != info)
            {
                Uri uri = null;
                if (null != info.getFriendUserId())
                {
                    uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                            info.getFriendUserId());
                }
                else
                {
                    uri = URIField.FRIENDMANAGER_URI;
                }
                ContentValues cv = setValues(userSysId, info);
                String operateTime = info.getOperateTime();
                if (StringUtil.isNullOrEmpty(operateTime))
                {
                    operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
                    cv.put(FriendManagerColumns.OPERATE_TIME, operateTime);
                }
                
                Uri resutltUri = mContentResolver.insert(uri, cv);
                if (null == resutltUri)
                {
                    return -1;
                }
                else
                {
                    // 插入头像表
                    FaceThumbnailModel model = new FaceThumbnailModel();
                    model.setFaceId(info.getFriendUserId());
                    model.setFaceUrl(info.getFaceUrl());
                    mFaceDbAdapter.updateOrInsert(model);
                }
                result = ContentUris.parseId(resutltUri);
                // 如果content不为null则执行会话表插入操作
                if (!StringUtil.isNullOrEmpty(content) && result != -1)
                {
                    this.insertOrUpdateConversationFriendManager(userSysId,
                            info,
                            content,
                            operateTime,
                            isNeedUnread);
                }
                Logger.i(TAG, "insertFriendHelper, result = " + result);
            }
            else
            {
                Logger.w(TAG, "insertFriendHelper fail, account is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据好友ID删除找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subServices
     *            业务类型
     * @param friendUserId
     *            好友JID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByFriendUserIdAndSubservices(String userSysId,
            int[] subServices, String friendUserId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != subServices
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.FRIENDMANAGER_URI;
            FriendManagerModel lastFM = this.queryLastFriendManager(userSysId);
            FriendManagerModel currentFM = this.queryByFriendUserIdNoUnion(userSysId,
                    friendUserId);
            StringBuilder sb = new StringBuilder();
            sb.append(FriendManagerColumns.SUBSERVICE).append(" IN('");
            for (int i = 0, j = subServices.length; i < j; i++)
            {
                sb.append(subServices[i]).append("','");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
            
            result = mContentResolver.delete(uri,
                    FriendManagerColumns.FRIEND_USERID + "=? AND "
                            + FriendManagerColumns.USER_SYSID + "=? AND "
                            + sb.toString(),
                    new String[] { friendUserId, userSysId });
            if (result > 0)
            {
                deleteFriendManagerRecordInConversation(userSysId,
                        currentFM,
                        lastFM);
            }
            Logger.i(TAG, "deleteByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 根据好友ID删除找朋友小助手信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByFriendSysId(String userSysId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.FRIENDMANAGER_URI;
            result = mContentResolver.delete(uri,
                    FriendManagerColumns.USER_SYSID + "=?",
                    new String[] { userSysId });
            if (result > 0)
            {
                mConversationDbAdapter.deleteByConversationId(userSysId,
                        ConversationModel.ID_VALUE_FRIEND_MANAGER,
                        ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER);
            }
            Logger.i(TAG, "deleteByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 根据群的JID和好友的userId删除小助手记录，提供给群成员退出群时，群主删除小助手相关的数据<BR>
     * 
     * @param userSysId
     *            当前登录用户的sysId
     * @param friendUserId
     *            好友的
     * @param groupJid
     *            群的JID
     * @return 删除的条数
     */
    public int deleteByFriendUserIdAndGroupId(String userSysId,
            String friendUserId, String groupJid)
    {
        int result = -1;
        FriendManagerModel lastFM = this.queryLastFriendManager(userSysId);
        FriendManagerModel currentFM = this.queryByFriendUserIdNoUnion(userSysId,
                friendUserId);
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.FRIENDMANAGER_URI;
            result = mContentResolver.delete(uri, FriendManagerColumns.GROUP_ID
                    + "=? AND " + FriendManagerColumns.USER_SYSID + "=? AND "
                    + FriendManagerColumns.FRIEND_USERID + " =?", new String[] {
                    groupJid, userSysId, friendUserId });
            
            if (result > 0)
            {
                deleteFriendManagerRecordInConversation(userSysId,
                        currentFM,
                        lastFM);
            }
            
            Logger.i(TAG, "deleteByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 会话表中的小助手信息的删除
     * 
     * @param userSysId
     *            用户系统ID
     * @param currentModel
     *            当前删除的对象
     * @param lastModel
     *            数据库中最后一条小助手对象
     * @return
     */
    private void deleteFriendManagerRecordInConversation(String userSysId,
            FriendManagerModel currentModel, FriendManagerModel lastModel)
    {
        if (currentModel != null && lastModel != null)
        {
            boolean isLastObj = false;
            // 比较 是否为最后一条
            if (StringUtil.equals(currentModel.getMsgId(), lastModel.getMsgId()))
            {
                isLastObj = true;
            }
            if (isLastObj)
            {
                // 重新查询删除最后一条
                FriendManagerModel newLastFM = this.queryLastFriendManager(userSysId);
                if (newLastFM != null)
                {
                    String operateTime = newLastFM.getOperateTime();
                    if (StringUtil.isNullOrEmpty(operateTime))
                    {
                        operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
                    }
                    
                    // 更新会话
                    this.insertOrUpdateConversationFriendManager(userSysId,
                            newLastFM,
                            generateConversationString(this.mContext, newLastFM),
                            operateTime,
                            false);
                }
                else
                {
                    this.mConversationDbAdapter.deleteByConversationId(userSysId,
                            ConversationModel.ID_VALUE_FRIEND_MANAGER,
                            ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER);
                }
            }
        }
    }
    
    /**
     * 根据 groupId 删除找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param groupId
     *            群组ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByGroupId(String userSysId, String groupId)
    {
        
        int result = -1;
        ConversationModel conversationModel = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                result = mContentResolver.delete(uri,
                        FriendManagerColumns.GROUP_ID + "=? AND "
                                + FriendManagerColumns.USER_SYSID + "=? ",
                        new String[] { groupId, userSysId });
                FriendManagerModel lastModel = this.queryLastFriendManager(userSysId);
                // 如果没有查询到这个登录ID的小助手信息记录，说明记录全部删除，同步删除会话表记录
                if (null == lastModel)
                {
                    mConversationDbAdapter.deleteFriendManagerInfoByUserSysId(userSysId);
                }
                else
                {
                    conversationModel = mConversationDbAdapter.queryFriendManagerInConverByUserSysId(userSysId);
                    if (conversationModel != null)
                    {
                        String lastMsgId = conversationModel.getLastMsgId();
                        String lastModelMsgId = lastModel.getMsgId();
                        // 如果会话表中的msgId与最后一条信息的msgId不同则更新会话表中的小助手信息
                        if (!StringUtil.equals(lastMsgId, lastModelMsgId))
                        {
                            String timeStringForConversation = DateUtil.getCurrentDateString();
                            
                            try
                            {
                                timeStringForConversation = DateUtil.getDateString(DateUtil.getDateFromFriendManageTimeString(lastModel.getOperateTime()));
                            }
                            catch (ParseException e)
                            {
                                Logger.e(TAG, e.getMessage(), e);
                            }
                            
                            this.updateFriendInfoInConver(userSysId,
                                    lastModel,
                                    null,
                                    timeStringForConversation,
                                    -1);
                            
                        }
                    }
                }
                Logger.i(TAG, "deleteByFriendUserId, result = " + result);
            }
            else
            {
                Logger.w(TAG,
                        "deleteByFriendUserId fail, friendUserId is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据hitalk id 和 subservice 更新数据库<BR>
     * 
     * @param userSysId
     *            当前登录用户的系统唯一标识符
     * @param friendUserId
     *            hitalk id
     * @param subservices
     *            subservice
     * @param model
     *            model
     * @param content
     *            会话内容
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 更新的条数或者插入的条数
     */
    public int updateOrInsert(String userSysId, String friendUserId,
            int[] subservices, FriendManagerModel model, String content,
            boolean isNeedUnread)
    {
        FriendManagerModel savedModel = queryBySubServiceAndFriendUserId(userSysId,
                friendUserId,
                subservices);
        
        if (null != savedModel)
        {
            ContentValues cv = setValues(userSysId, model);
            return updateByFriendUserIdAndSubservices(userSysId,
                    friendUserId,
                    subservices,
                    cv,
                    content,
                    isNeedUnread);
        }
        else
        {
            if (insert(userSysId, model, content, isNeedUnread) > 0)
            {
                return 1;
            }
        }
        return -1;
    }
    
    /**
     * 根据好友 JID 和 subService 修改找朋友小助手信息。（局部更新）<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @param subServices
     *            业务类型
     * @param cv
     *            需要修改的字段
     * @param content
     *            需要插入到会话表的内容说明，如果为null，则不修改会话。
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendUserIdAndSubservices(String userSysId,
            String friendUserId, int[] subServices, ContentValues cv,
            String content, boolean isNeedUnread)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != subServices
                && 0 < subServices.length
                && !StringUtil.isNullOrEmpty(friendUserId) && null != cv
                && 0 < cv.size())
        {
            // 更新时候生成新的ID
            String msgId = MessageUtils.generateMsgId();
            cv.put(FriendManagerColumns.MSG_ID, msgId);
            
            Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                    friendUserId);
            
            StringBuilder sb = new StringBuilder();
            sb.append(" AND (");
            for (int i = 0, j = subServices.length; i < j; i++)
            {
                sb.append(FriendManagerColumns.SUBSERVICE)
                        .append("=")
                        .append(subServices[i])
                        .append(" OR ");
            }
            sb.delete(sb.length() - 3, sb.length() - 1);
            sb.append(")");
            
            result = mContentResolver.update(uri,
                    cv,
                    FriendManagerColumns.FRIEND_USERID + "=? AND "
                            + FriendManagerColumns.USER_SYSID + "=? "
                            + sb.toString(),
                    new String[] { friendUserId, userSysId });
            // 如果content不等于空，要对会话表进行同步更新
            if (result > 0)
            {
                updateConversationRel(userSysId,
                        friendUserId,
                        cv,
                        content,
                        isNeedUnread);
            }
            Logger.i(TAG, "updateByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 根据好友 JID 和 subService 修改找朋友小助手信息(全量)。<BR>
     * 会查询最后一条信息与会话中记录进行比较
     * 
     * @param userSysId
     *            用户系统ID
     * @param friendUserId
     *            会话ID
     * @param cv
     *            要更改的值
     * @param content
     *            会话内容
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     */
    private void updateConversationRel(String userSysId, String friendUserId,
            ContentValues cv, String content, boolean isNeedUnread)
    {
        if (null != content)
        {
            String operateTime = cv.getAsString(FriendManagerColumns.OPERATE_TIME);
            String msgId = cv.getAsString(FriendManagerColumns.MSG_ID);
            FriendManagerModel model = new FriendManagerModel();
            model.setFriendUserId(friendUserId);
            if (StringUtil.isNullOrEmpty(operateTime))
            {
                operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
                model.setOperateTime(operateTime);
            }
            if (!StringUtil.isNullOrEmpty(msgId))
            {
                model.setMsgId(msgId);
            }
            insertOrUpdateConversationFriendManager(userSysId,
                    model,
                    content,
                    operateTime,
                    isNeedUnread);
        }
    }
    
    /**
     * 根据好友 JID 和 subService 修改找朋友小助手信息。<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @param subService
     *            业务类型
     * @param model
     *            model
     * @param content
     *            需要插入到会话表的内容说明，如果为null，则不修改会话。
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendUserIdAndSubservices(String userSysId,
            String friendUserId, int[] subService, FriendManagerModel model,
            String content, boolean isNeedUnread)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != subService
                && 0 < subService.length
                && !StringUtil.isNullOrEmpty(friendUserId) && model != null)
        {
            ContentValues cv = setValues(userSysId, model);
            // 更新时候生成新的ID
            String msgId = MessageUtils.generateMsgId();
            cv.put(FriendManagerColumns.MSG_ID, msgId);
            Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                    friendUserId);
            StringBuilder sb = new StringBuilder();
            sb.append(" AND (");
            for (int i = 0, j = subService.length; i < j; i++)
            {
                sb.append(FriendManagerColumns.SUBSERVICE)
                        .append("=")
                        .append(subService[i])
                        .append(" OR ");
            }
            sb.delete(sb.length() - 3, sb.length() - 1);
            sb.append(")");
            
            result = mContentResolver.update(uri,
                    cv,
                    FriendManagerColumns.FRIEND_USERID + "=? AND "
                            + FriendManagerColumns.USER_SYSID + "=? "
                            + sb.toString(),
                    new String[] { friendUserId, userSysId });
            // 如果数据有更新,则更新会话表中的记录
            if (result > 0)
            {
                this.updateConversationRel(userSysId,
                        friendUserId,
                        cv,
                        content,
                        isNeedUnread);
            }
            Logger.i(TAG, "updateByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 增量更新friendManager<BR>
     * 
     * @param friendUserId
     *            friendUserId
     * @param userSysId
     *            当前登录用户的sysId
     * @param subServices
     *            subServices
     * @param groupJid
     *            groupJid
     * @param cv
     *            需要更新的字段的新值
     * @param content
     *            content
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 更新的记录条数
     */
    public int updateByFriendUserIdAndGroupId(String friendUserId,
            String userSysId, int[] subServices, String groupJid,
            ContentValues cv, String content, boolean isNeedUnread)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(friendUserId)
                && !StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid) && subServices != null)
        {
            Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                    friendUserId);
            StringBuffer subservices = new StringBuffer();
            // 更新时候生成新的ID
            String msgId = MessageUtils.generateMsgId();
            cv.put(FriendManagerColumns.MSG_ID, msgId);
            int len = subServices == null ? 0 : subServices.length;
            if (len != 0)
            {
                subservices.append(" in(");
            }
            for (int i = 0; i < len; i++)
            {
                if (i == len - 1)
                {
                    subservices.append(subServices[i]).append(")");
                }
                else
                {
                    subservices.append(subServices[i]).append(",");
                }
            }
            String where = new StringBuffer().append(FriendManagerColumns.FRIEND_USERID)
                    .append("=? AND ")
                    .append(FriendManagerColumns.USER_SYSID)
                    .append("=? AND ")
                    .append(FriendManagerColumns.SUBSERVICE)
                    .append(subservices.toString())
                    .append(" AND ")
                    .append(FriendManagerColumns.GROUP_ID)
                    .append("=?")
                    .toString();
            result = mContentResolver.update(uri, cv, where, new String[] {
                    friendUserId, userSysId, groupJid });
            if (result > 0 && content != null)
            {
                this.updateConversationRel(userSysId,
                        friendUserId,
                        cv,
                        content,
                        isNeedUnread);
            }
            Logger.i(TAG, "updateStatusByFriendUserIdAndGroupId, result = "
                    + result);
        }
        else
        {
            Logger.w(TAG, "updateByFriendUserId fail,param error!");
        }
        return result;
    }
    
    /**
     * 更新数据（全量）<BR>
     * 
     * @param friendUserId
     *            好友ID
     * @param userSysId
     *            用户系统ID
     * @param subServices
     *            subServices
     * @param groupJid
     *            groupJid
     * @param model
     *            model
     * @param content
     *            content
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     * @return 改变的行数
     */
    public int updateByFriendUserIdAndGroupId(String friendUserId,
            String userSysId, int[] subServices, String groupJid,
            FriendManagerModel model, String content, boolean isNeedUnread)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(friendUserId)
                && !StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid) && subServices != null
                && model != null)
        {
            Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                    friendUserId);
            StringBuffer subservices = new StringBuffer();
            int len = subServices == null ? 0 : subServices.length;
            if (len != 0)
            {
                subservices.append(" in(");
            }
            for (int i = 0; i < len; i++)
            {
                if (i == len - 1)
                {
                    subservices.append(subServices[i]).append(")");
                }
                else
                {
                    subservices.append(subServices[i]).append(",");
                }
            }
            ContentValues cv = setValues(userSysId, model);
            // 更新时候生成新的ID
            String msgId = MessageUtils.generateMsgId();
            cv.put(FriendManagerColumns.MSG_ID, msgId);
            String where = new StringBuffer().append(FriendManagerColumns.FRIEND_USERID)
                    .append("=? AND ")
                    .append(FriendManagerColumns.USER_SYSID)
                    .append("=? AND ")
                    .append(FriendManagerColumns.SUBSERVICE)
                    .append(subservices)
                    .append(" AND ")
                    .append(FriendManagerColumns.GROUP_ID)
                    .append("=?")
                    .toString();
            result = mContentResolver.update(uri, cv, where, new String[] {
                    friendUserId, userSysId, groupJid });
            // 如果数据有更新,更新会话表中的小助手信息
            if (result > 0)
            {
                this.updateConversationRel(userSysId,
                        friendUserId,
                        cv,
                        content,
                        isNeedUnread);
            }
            Logger.i(TAG, "updateStatusByFriendUserIdAndGroupId, result = "
                    + result);
        }
        else
        {
            Logger.w(TAG, "updateByFriendUserId fail,param error!");
        }
        return result;
        
    }
    
    /**
     * 根据好友JID查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryByFriendUserIdWithCursor(String userSysId,
            String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri, null, Tables.FRIEND_MANAGER
                    + "." + FriendManagerColumns.FRIEND_USERID + "=? AND "
                    + Tables.FRIEND_MANAGER + "."
                    + FriendManagerColumns.USER_SYSID + "=?", new String[] {
                    friendUserId, userSysId }, null);
        }
        return cursor;
    }
    
    /**
     * 根据好友JID查询找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @return 成功：UnAuthorizedContactInfoModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryByFriendUserId(String userSysId,
            String friendUserId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                cursor = queryByFriendUserIdWithCursor(userSysId, friendUserId);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据subService 和 friendUserId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subService
     *            子业务类型
     * @param friendUserId
     *            好友JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryBySubserviceAndFriendUserIdWithCursor(String userSysId,
            int subService, String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            StringBuilder queryCondition = new StringBuilder();
            
            queryCondition.append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.SUBSERVICE)
                    .append("=? AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.FRIEND_USERID)
                    .append("=? AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.USER_SYSID)
                    .append("=?");
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri,
                    null,
                    queryCondition.toString(),
                    new String[] { Integer.toString(subService), friendUserId,
                            userSysId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 根据subService 和 friendUserId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subService
     *            子业务类型
     * @param friendUserId
     *            好友JID
     * @return 成功：FriendManagerModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryBySubServiceAndFriendUserId(
            String userSysId, int subService, String friendUserId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                cursor = queryBySubserviceAndFriendUserIdWithCursor(userSysId,
                        subService,
                        friendUserId);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据群的ID，subservice(单个subservice)，好友的id，userSysID查询记录<BR>
     * 
     * @param userSysId
     *            userSysId
     * @param subService
     *            subService
     * @param friendUserId
     *            friendUserId
     * @param groupId
     *            groupId
     * @return 找朋友小助手model
     */
    public FriendManagerModel queryByFriendUserIdAndGroupId(String userSysId,
            int subService, String friendUserId, String groupId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                String selection = new StringBuffer().append(FriendManagerColumns.USER_SYSID)
                        .append("=? AND ")
                        .append(FriendManagerColumns.SUBSERVICE)
                        .append("=? AND ")
                        .append(FriendManagerColumns.FRIEND_USERID)
                        .append("=? AND ")
                        .append(FriendManagerColumns.GROUP_ID)
                        .append("=?")
                        .toString();
                cursor = mContentResolver.query(uri,
                        null,
                        selection,
                        new String[] { userSysId, String.valueOf(subService),
                                friendUserId, groupId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据群的ID，subservice(多个subservice)，好友的id，userSysID查询记录<BR>
     * 
     * @param userSysId
     *            userSysId
     * @param subServices
     *            subServices
     * @param friendUserId
     *            friendUserId
     * @param groupId
     *            groupId
     * @return model
     */
    public FriendManagerModel queryByFriendUserIdAndGroupId(String userSysId,
            int[] subServices, String friendUserId, String groupId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                StringBuffer subservices = new StringBuffer();
                int len = subServices == null ? 0 : subServices.length;
                if (len != 0)
                {
                    subservices.append(" in(");
                }
                for (int i = 0; i < len; i++)
                {
                    if (i == len - 1)
                    {
                        subservices.append(subServices[i]).append(")");
                    }
                    else
                    {
                        subservices.append(subServices[i]).append(",");
                    }
                }
                
                String selection = new StringBuffer().append(FriendManagerColumns.USER_SYSID)
                        .append("=? AND ")
                        .append(FriendManagerColumns.SUBSERVICE)
                        .append(subservices.toString())
                        .append(" AND ")
                        .append(FriendManagerColumns.FRIEND_USERID)
                        .append("=? AND ")
                        .append(FriendManagerColumns.GROUP_ID)
                        .append("=?")
                        .toString();
                cursor = mContentResolver.query(uri,
                        null,
                        selection,
                        new String[] { userSysId, friendUserId, groupId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据subService 和 friendUserId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subService
     *            子业务类型
     * @param friendUserId
     *            好友JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryBySubserviceAndFriendUserIdWithCursor(String userSysId,
            int[] subService, String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != subService
                && subService.length > 0
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            StringBuilder queryCondition = new StringBuilder();
            
            queryCondition.append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.SUBSERVICE)
                    .append(" IN('");
            for (int i = subService.length - 1; i >= 0; i--)
            {
                queryCondition.append(subService[i]).append("','");
            }
            queryCondition.delete(queryCondition.length() - 2,
                    queryCondition.length());
            queryCondition.append(") AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.FRIEND_USERID)
                    .append("=? AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.USER_SYSID)
                    .append("=?");
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri,
                    null,
                    queryCondition.toString(),
                    new String[] { friendUserId, userSysId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 根据subService 和 friendUserId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @param subServices
     *            子业务类型
     * @return 成功：FriendManagerModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryBySubServiceAndFriendUserId(
            String userSysId, String friendUserId, int[] subServices)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                cursor = queryBySubserviceAndFriendUserIdWithCursor(userSysId,
                        subServices,
                        friendUserId);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据subService 和 friendSysId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subService
     *            子业务类型
     * @param friendSysId
     *            好友系统ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryBySubserviceAndFriendSysIdWithCursor(String userSysId,
            int subService, String friendSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId))
        {
            StringBuilder queryCondition = new StringBuilder();
            
            queryCondition.append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.SUBSERVICE)
                    .append("=? AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.FRIEND_SYSID)
                    .append("=? AND ")
                    .append(Tables.FRIEND_MANAGER)
                    .append(".")
                    .append(FriendManagerColumns.USER_SYSID)
                    .append("=?");
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri,
                    null,
                    queryCondition.toString(),
                    new String[] { Integer.toString(subService), friendSysId,
                            userSysId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 根据subService 和 friendSysId 查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param subService
     *            子业务类型
     * @param friendSysId
     *            好友系统ID
     * @return 成功：FriendManagerModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryBySubService(String userSysId,
            int subService, String friendSysId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendSysId))
            {
                cursor = this.queryBySubserviceAndFriendSysIdWithCursor(userSysId,
                        subService,
                        friendSysId);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据好友friendSysId查询找朋友小助手(推荐)信息， 关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendSysId
     *            好友JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryByFriendSysIdWithCursor(String userSysId,
            String friendSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId))
        {
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri, null, Tables.FRIEND_MANAGER
                    + "." + FriendManagerColumns.FRIEND_SYSID + "=? AND "
                    + Tables.FRIEND_MANAGER + "."
                    + FriendManagerColumns.USER_SYSID + "=?", new String[] {
                    friendSysId, userSysId }, null);
        }
        return cursor;
    }
    
    /**
     * 根据好友JID查询找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendSysId
     *            好友friendSysId
     * @return 成功：UnAuthorizedContactInfoModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryByFriendSysId(String userSysId,
            String friendSysId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendSysId))
            {
                cursor = queryByFriendSysIdWithCursor(userSysId, friendSysId);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 查询所有找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    private Cursor queryAllWithCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mContentResolver.query(uri,
                    null,
                    Tables.FRIEND_MANAGER + "."
                            + FriendManagerColumns.USER_SYSID + "=? ORDER BY "
                            + FriendManagerColumns.OPERATE_TIME + ","
                            + FriendManagerColumns.ID + " DESC",
                    new String[] { userSysId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 查询所有找朋友小助手(推荐)信息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @return 成功：UnAuthorizedContactInfoModel列表<br>
     *         失败：null
     */
    public ArrayList<FriendManagerModel> queryAll(String userSysId)
    {
        ArrayList<FriendManagerModel> list = null;
        Cursor cursor = null;
        try
        {
            FriendManagerModel info = null;
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                cursor = queryAllWithCursor(userSysId);
                if (null != cursor && cursor.moveToFirst())
                {
                    list = new ArrayList<FriendManagerModel>();
                    while (!cursor.isAfterLast())
                    {
                        info = parseCursorToFriendHelperModel(cursor);
                        list.add(info);
                        cursor.moveToNext();
                    }
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return list;
    }
    
    /**
     * 按条件查询， 不关联头像<BR>
     * [通用方法]
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param params
     *            自定义查询条件
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByMapWithCursor(String userSysId,
            Map<String, Object> params)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != params
                && 0 < params.size())
        {
            Uri uri = URIField.FRIENDMANAGER_URI;
            Iterator<Map.Entry<String, Object>> it = params.entrySet()
                    .iterator();
            String selection = "";
            String[] selectionArgs = new String[params.size()];
            int i = 0;
            while (it.hasNext())
            {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (0 < i)
                {
                    selection += " AND ";
                }
                
                selection += key;
                selection += "=?";
                
                selectionArgs[i] = value + "";
                i++;
            }
            
            cursor = mContentResolver.query(uri,
                    null,
                    selection,
                    selectionArgs,
                    FriendManagerColumns.OPERATE_TIME);
            
        }
        return cursor;
    }
    
    /**
     * 按条件查询，不关联头像<BR>
     * [通用方法]
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param params
     *            自定义查询条件
     * @return 成功：UnAuthorizedContactInfoModel列表 <br>
     *         失败：null
     */
    public List<FriendManagerModel> queryByMap(String userSysId,
            Map<String, Object> params)
    {
        List<FriendManagerModel> list = null;
        Cursor cursor = null;
        try
        {
            FriendManagerModel info = null;
            cursor = queryByMapWithCursor(userSysId, params);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<FriendManagerModel>();
                
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                    list.add(info);
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return list;
    }
    
    /**
     * 根据好友JID查询找朋友小助手信息， 未关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param friendUserId
     *            好友JID
     * @return 成功：UnAuthorizedContactInfoModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryByFriendUserIdNoUnion(String userSysId,
            String friendUserId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                cursor = mContentResolver.query(uri,
                        null,
                        FriendManagerColumns.FRIEND_USERID + "=? AND "
                                + FriendManagerColumns.USER_SYSID + "=?",
                        new String[] { friendUserId, userSysId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据好友JID查询找朋友小助手信息， 未关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param groupId
     *            群组ID
     * @param friendUserId
     *            成员userId
     * @return 成功：FriendManagerModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryByGroupIdNoUnion(String userSysId,
            String groupId, String friendUserId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                cursor = mContentResolver.query(uri,
                        null,
                        FriendManagerColumns.GROUP_ID + "=? AND "
                                + FriendManagerColumns.USER_SYSID + "=? AND "
                                + FriendManagerColumns.FRIEND_USERID + "=?",
                        new String[] { groupId, userSysId, friendUserId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据好友JID查询找朋友小助手信息， 未关联头像<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param groupId
     *            群组ID
     * @return 成功：FriendManagerModel对象<br>
     *         失败：null
     */
    public FriendManagerModel queryByGroupIdNoUnion(String userSysId,
            String groupId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                cursor = mContentResolver.query(uri,
                        null,
                        FriendManagerColumns.GROUP_ID + "=? AND "
                                + FriendManagerColumns.USER_SYSID + "=?",
                        new String[] { groupId, userSysId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = this.parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 根据业务类型和群组ID查询找朋友小助手信息<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param subService
     *            业务类型
     * @param groupId
     *            群组ID
     * @return 成功：FriendManagerModel <br>
     *         失败：null
     */
    public FriendManagerModel queryBySubServiceAndGroupIdNoUnion(
            String userSysId, int subService, String groupId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.FRIENDMANAGER_URI;
                cursor = mContentResolver.query(uri,
                        null,
                        FriendManagerColumns.USER_SYSID + "=? AND "
                                + FriendManagerColumns.SUBSERVICE + "=? AND "
                                + FriendManagerColumns.GROUP_ID + "=?",
                        new String[] { userSysId, String.valueOf(subService),
                                groupId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    info = parseCursorToFriendHelperModel(cursor);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 分页查询<BR>
     * 
     * @param userSysId
     *            当前用户的sysId
     * @param startIndex
     *            开始位置，1 表示第一行
     * @param pageSize
     *            页的记录数量
     * @return 找朋友小助手数据列表
     */
    public ArrayList<FriendManagerModel> queryListByPage(String userSysId,
            int startIndex, int pageSize)
    {
        ArrayList<FriendManagerModel> list = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.FRIENDMANAGER_QUERY_WITH_PAGE_URI;
                cursor = mContentResolver.query(uri,
                        null,
                        FriendManagerColumns.USER_SYSID + "=?",
                        new String[] { userSysId, String.valueOf(pageSize),
                                String.valueOf(startIndex) },
                        null);
                if (null == cursor || !cursor.moveToFirst())
                {
                    return null;
                }
                list = new ArrayList<FriendManagerModel>();
                do
                {
                    list.add(parseCursorToFriendHelperModel(cursor));
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return list;
    }
    
    /**
     * 查询最后一条"找朋友小助手"记录<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @return 成功：FriendManagerModel <br>
     *         失败：null
     */
    public FriendManagerModel queryLastFriendManager(String userSysId)
    {
        FriendManagerModel info = null;
        Cursor cursor = null;
        try
        {
            Uri uri = URIField.FRIENDMANAGER_URI;
            cursor = this.mContentResolver.query(uri,
                    null,
                    FriendManagerColumns.USER_SYSID + "=?",
                    new String[] { userSysId },
                    FriendManagerColumns.OPERATE_TIME + " ASC ");
            
            if (cursor != null && cursor.moveToLast())
            {
                info = this.parseCursorToFriendHelperModel(cursor);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return info;
    }
    
    /**
     * 生成会话展示
     * 
     * @param friendManagerModel
     *            friendManagerModel
     * @param context
     *            上下文对象
     * @return 生成的字符串
     */
    private String generateConversationString(Context context,
            FriendManagerModel friendManagerModel)
    {
        int subservice = friendManagerModel.getSubService();
        int status = friendManagerModel.getStatus();
        
        Logger.d(TAG, "generateConversationString -------> subservice:"
                + subservice);
        Logger.d(TAG, "generateConversationString -------> status:" + status);
        
        Resources rcs = context.getResources();
        String lastConversationMsg = null;
        
        // 会话中展示的好友名称，如果为空 则显示 好友的WoYou ID
        String displayFriendName = null;
        // 会话中展示的群组名称 ，如果为空 则显示 群组的 ID
        String displayGroupName = null;
        
        displayGroupName = StringUtil.isNullOrEmpty(friendManagerModel.getGroupName()) ? friendManagerModel.getGroupId()
                : friendManagerModel.getGroupName();
        displayFriendName = StringUtil.isNullOrEmpty(friendManagerModel.getNickName()) ? friendManagerModel.getFriendUserId()
                : friendManagerModel.getNickName();
        
        switch (subservice)
        {
            case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
            {
                // 申请加别人好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_pass));
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_decline));
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_BE_ADD:
            {
                // 被加好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_pass),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
            {
                
                // 接收到群邀请
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.firendmanager_message_inviting_refuse),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
            {
                // 群主受理待加入成员
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_wait),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName,
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
            {
                // 申请加入群
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_wait),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_INVITE_REGISTER:
            case FriendManagerModel.SUBSERVICE_SYSTEM_MATCH:
                break;
            case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_friend_success),
                        displayFriendName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_self),
                        displayGroupName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_owner),
                        displayFriendName,
                        displayGroupName);
                break;
        }
        Logger.d(TAG,
                "generateConversationString -------> 生成的字符串  lastConversationMsg: "
                        + lastConversationMsg);
        return lastConversationMsg;
    }
    
    /**
     * 在会话表中插入或者更新小助手相关消息记录
     * 
     * @param userSysId
     *            用户系统ID
     * @param info
     *            小助手实体
     * @param content
     *            会话消息内容
     * @param isNeedUnread
     *            是否是服务器主动推送过来的请求，如果是的话，需要增加找朋友小助手未读消息
     */
    private void insertOrUpdateConversationFriendManager(String userSysId,
            FriendManagerModel info, String content, String operateTime,
            boolean isNeedUnread)
    {
        
        ConversationModel conversationModel = mConversationDbAdapter.queryFriendManagerInConverByUserSysId(userSysId);
        
        String timeStringForConversation = DateUtil.getCurrentDateString();
        
        try
        {
            timeStringForConversation = DateUtil.getDateString(DateUtil.getDateFromFriendManageTimeString(operateTime));
        }
        catch (ParseException e)
        {
            Logger.e(TAG, e.getMessage(), e);
        }
        
        if (null == conversationModel)
        {
            ConversationModel model = new ConversationModel();
            model.setLastMsgId(MessageUtils.generateMsgId());
            model.setUserSysId(userSysId);
            model.setLastMsgContent(content);
            model.setConversationId(ConversationModel.ID_VALUE_FRIEND_MANAGER);
            model.setLastMsgType(MessageModel.MSGTYPE_TEXT);
            model.setUnReadmsg(isNeedUnread ? 1 : 0);
            model.setLastTime(timeStringForConversation);
            model.setConversationType(ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER);
            mConversationDbAdapter.insertConversation(userSysId, model);
        }
        else
        {
            updateFriendInfoInConver(userSysId,
                    info,
                    content,
                    timeStringForConversation,
                    isNeedUnread ? conversationModel.getUnReadmsg() + 1 : -1);
        }
    }
    
    /**
     * 更新会话表中小助手会话信息<BR>
     * 该方法直接调用conversation中的更新方法，不必进行对比和查询
     * 
     * @param userSysId
     *            用户系统ID
     * @param info
     *            friendUser对象
     * @param content
     *            会话内容
     * @param operateTime
     *            时间
     * @param unreadNumber
     *            未读数量 如果为-1则不更新未读数量
     */
    private void updateFriendInfoInConver(String userSysId,
            FriendManagerModel info, String content, String operateTime,
            int unreadNumber)
    {
        ContentValues cv = new ContentValues();
        if (null != content)
        {
            cv.put(ConversationColumns.LAST_MSG_CONTENT, content);
        }
        else
        {
            cv.put(ConversationColumns.LAST_MSG_CONTENT,
                    generateConversationString(mContext, info));
        }
        cv.put(ConversationColumns.LASTTIME, operateTime);
        cv.put(ConversationColumns.LAST_MSG_ID, info.getMsgId());
        if (unreadNumber != -1)
        {
            cv.put(ConversationColumns.UNREAD_MSG, unreadNumber);
        }
        mConversationDbAdapter.updateFriendManagerRecordInConversation(userSysId,
                cv);
    }
    
    /**
     * 根据游标解析找朋友小助手(推荐)信息<BR>
     * 
     * @param cursor
     *            游标对象
     * @return 找朋友小助手信息
     */
    private FriendManagerModel parseCursorToFriendHelperModel(Cursor cursor)
    {
        FriendManagerModel info = new FriendManagerModel();
        info.setSubService(cursor.getInt(cursor.getColumnIndex(FriendManagerColumns.SUBSERVICE)));
        info.setFriendSysId(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.FRIEND_SYSID)));
        info.setMsgId(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.MSG_ID)));
        info.setFriendUserId(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.FRIEND_USERID)));
        info.setFirstName(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.FIRSTNAME)));
        info.setMiddleName(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.MIDDLENAME)));
        info.setLastName(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.LASTNAME)));
        info.setSignature(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.SIGNATURE)));
        info.setNickName(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.NICKNAME)));
        info.setStatus(cursor.getInt(cursor.getColumnIndex(FriendManagerColumns.STATUS)));
        info.setReason(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.REASON)));
        info.setOperateTime(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.OPERATE_TIME)));
        info.setGroupId(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.GROUP_ID)));
        info.setGroupName(cursor.getString(cursor.getColumnIndex(FriendManagerColumns.GROUP_NAME)));
        return info;
    }
    
    /**
     * 将对象中的值放入contentValues对象中<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param info
     *            FriendManagerModel
     * @return ContentValues对象
     */
    private ContentValues setValues(String userSysId, FriendManagerModel info)
    {
        ContentValues cv = new ContentValues();
        cv.put(FriendManagerColumns.USER_SYSID, userSysId);
        cv.put(FriendManagerColumns.SUBSERVICE, info.getSubService());
        cv.put(FriendManagerColumns.FRIEND_SYSID, info.getFriendSysId());
        cv.put(FriendManagerColumns.MSG_ID, MessageUtils.generateMsgId());
        cv.put(FriendManagerColumns.FRIEND_USERID, info.getFriendUserId());
        cv.put(FriendManagerColumns.NICKNAME, info.getNickName());
        cv.put(FriendManagerColumns.FIRSTNAME, info.getFirstName());
        cv.put(FriendManagerColumns.MIDDLENAME, info.getMiddleName());
        cv.put(FriendManagerColumns.LASTNAME, info.getLastName());
        cv.put(FriendManagerColumns.SIGNATURE, info.getSignature());
        cv.put(FriendManagerColumns.STATUS, info.getStatus());
        cv.put(FriendManagerColumns.REASON, info.getReason());
        cv.put(FriendManagerColumns.OPERATE_TIME, info.getOperateTime());
        cv.put(FriendManagerColumns.GROUP_ID, info.getGroupId());
        cv.put(FriendManagerColumns.GROUP_NAME, info.getGroupName());
        return cv;
    }
    
}
