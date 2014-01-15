/*
 * 文件名: ICommunication.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.voip;

import java.util.List;

import android.content.Context;

import com.huawei.basic.android.im.logic.model.voip.CommunicationLog;
import com.huawei.basic.android.im.logic.voip.CommunicationLogLogic.PhoneContact;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15] 
 */
public interface ICommunicationLogLogic
{
    /**
     * 
     * 发送获取通话记录消息
     * 
     */
    void getAllCommunicationLogs();
    /**
     * 查询所有的通话记录
     * @return 查询的所有的CommunicationLog集合
     */
    List<CommunicationLog> getAllCommunicationLogsFromDB();
    
    /**
     * 根据来电或去电用户id查询该用户所有的通讯记录详情
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param remoteUri   对方的voip账号
     * @param remotePhoneNum  对方的手机号
     * @param startIndex 开始下标
     * @param recordNum  记录总数
     * @see com.huawei.basic.android.im.logic.voip.
     * ICommunicationLogLogic#findCommunicationLogByRemoteUriOrRemotePhoneNum
     * (java.lang.String, java.lang.String, int, int)
     */
    void loadCommunicationLogByRemoteUriOrRemotePhoneNum(String remoteUri,
            String remotePhoneNum, int startIndex, int recordNum);
    
    /**
     * 根据来电或去电用户id删除该用户所有的通讯记录详情
     *  
     * @param remoteUri 来电或去电用户voip账号
     * @param remotePhoneNum 来电或去电用户电话号码
     */
    void deleteByRemoteUriOrRemotePhoneNum(String remoteUri,
            String remotePhoneNum);
    
    /**
     *  删除所属用户ID需要显示通话记录以外的通话记录
     * 
     * @param startIndex 开始删除条数下标
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#deleteCommunicationLogGroupByOwnerSysId(int)
     */
    void deleteCommunicationLogGroupByOwnerSysId(int startIndex);
    
    /**
     *删除通话详情记录 10 条以外的记录
     * @param remoteUri
     * @param remotePhoneNum
     * @param startIndex
     */
    //    void deleteCommunicationLogDetail(String remoteUri, String remotePhoneNum,
    //            int startIndex);
    //    
    /**
     * 获取未读电话数量
     *  
     * @return  未读电话数量
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#getUnreadTotal()
     */
    int getUnreadTotal();
    
    /**
     * 根据所属用户ID更新通讯记录为已读
     * 
     * @param remoteUri voip号
     * @param remotePhoneNum 手机号
     */
    void updateToIsReadByOwnerUserId(String remoteUri, String remotePhoneNum);
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param context Context
     * @param phoneNumber  来电或去电号码 voip账号
     * @return  PhoneContact
     */
    PhoneContact getPhoneContacts(Context context, String phoneNumber);
    
    /**
     * 删除所有的通话记录
     */
    void deleteAllCommunicationLogs();
}
