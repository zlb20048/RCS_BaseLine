/*
 * 文件名: IBindVoip.java
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

import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;

import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.voip.VoipAccount;

/**
 * voip逻辑处理
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15]
 */
public interface IVoipLogic
{
    /**
     * 
     * 初始化VOIP
     * @return boolean
     */
    boolean init(); 
    /**
     * 自动登录
     * 
     */
    void autoLoginVoip();
    
    /**
     * 
     *登录
     * @param aor  账号
     * @param password 密码
     */
    void bindVoip(String aor, String password);
    
    /**
     * 登出
     * 
     */
    void unbindVOIP();
    
    /**
     * 
     *查询数据库中的VoipAccount
     * @return VoipAccount
     */
    VoipAccount queryVoipAccount();
    
    /**
     * 拨打电话流程
     * @param displayName displayName
     * @param phoneNum phoneNum
     * @param type type
    * @param faceThumbnailModel FaceThumbnailModel
    */
    void callVoip(String displayName, String phoneNum, String type,
            FaceThumbnailModel faceThumbnailModel);
    
    /**
     * 判断Voip是否登录
     * @return 登陆
     */
    boolean isLogin();
    
    /**
     * 接听来电
     * 
     */
    void answerVoip();
    
    /**
     * 校验填写的密码和数据库中查询的密码是否相同
     * @param ps 填写的密码
     */
    void checkPS(String ps);
    
    /**
     * 退出HITalk时登出voip账号,此时并没有解除绑定，数据库中仍然保存有VOIP账号信息
     */
    void logout();
    
    /**
     *  检测是否登录VOIP
     */
    void checkVoipLogin();
    
     
    /**
     *  挂断电话
     * @param isRefuse boolean
     */
    void closeVoip(boolean isRefuse);
    
    /**
     * 免提是否打开
     * @return 免提是否打开
     */
    boolean isOpenSpeaker();
    
    /**
     * 打开免提
     */
    void openSpeaker();
    
    /**
     * 关闭免提
     */
    void closeSpeaker();
    
    /**
     * 获得好友头像
     * @param friendUserId 好友UserID
     * @return 头像
     */
    FaceThumbnailModel getFaceThumbnailModel(String friendUserId);
    
    /**
     * 是否静音
     * @return
     *      是否静音
     */
    boolean isMute();
    
    /**
     * 打开静音
     */
    void openMute();
    
    /**
     * 关闭静音
     */
    void closeMute();
    
    /**
     * 根据电话号码得到好友信息
     * @param phoneNum 手机号或是VOIP账号
     * @return ContactInfoModel
     */
    ContactInfoModel getContactInfoModelByPhone(String phoneNum);
    
    /**
     * 二次拨号
     * @param code 二次拨号输入的号码
     */
    void redial(String code);
    
    /**
     * 添加原生呼出记录
     * @param phoneNum 号码
     */ 
    void addLocalContactCommunicationLog(String phoneNum);
    
    /**
     * 添加通知
     * @param tickerText 状态栏标题 显示为"与某某通话中......" + 通话时间
     * @param faceBitmap 联系人头像
     * @param contactName 联系人姓名
     * @param callStartTime 电话开始时间
     * @param intent 点击通知栏跳转页面
     * @return String 新的通知消息
     */
    String showNewNotification(CharSequence tickerText, Bitmap faceBitmap,
            String contactName, Date callStartTime, Intent intent);
    
    /**
     * 取消通知
     */
    void destroyNotification();
    
    /**
     * 更新通知栏头像 
     * @param faceBitmap 通知栏头像bitmap对象
     * @param name 联系人姓名
     */
    void updateNotificationData(Bitmap faceBitmap, String name);
}
