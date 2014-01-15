/*
 * 文件名: ds.java 
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved. 
 * 描 述: [该类的简要描述] 
 * 创建人: 王媛媛
 * 创建时间:2012-2-15 
 * 修改人： 
 * 修改时间: 
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Html;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.json.JsonUtils;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.Base64;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 用户管理类，用于注册、绑定手机号和邮箱、解绑手机号和邮箱、修改密码、重置密码等操作
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class UserManager extends HttpManager
{
    /**
     * 打印日志标识。
     */
    public static final String TAG = "UserManager";
    
    /**
     * 响应码
     */
    public static final String RET_CODE = "ret_code";
    
    /**
     * 解绑类型标识
     */
    public static final String OPER_TYPE = "oper_type";
    
    /**
     * 验证码校验成功后服务器返回的凭据
     */
    public static final String CRED = "cred";
    
    /**
     * 用户账号标识
     */
    public static final String ACCOUNT = "account";
    
    /**
     * 电话号码标识
     */
    public static final String PHONE_NUMBER = "phone_number";
    
    /**
     * 邮箱标识
     */
    public static final String EMAIL = "email";
    
    /**
     * 获取应用。
     */
    public static final int GET_ACHIEVE_APP = 26;
    
    /**
     * 应用URL(WebView)。
     */
    public static final String APPURL = "app_url";
    
    /**
     * 应用名称(WebView)。
     */
    public static final String APPNAME = "app_name";
    
    /**
     * 应用信息Model
     */
    public static final String SYSAPP = "sys_app_model";
    
    /**
     * 验证码标识
     */
    //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
    //    private static final String VERIFY_CODE = "verifyCode";
    public static final String VERIFY_CODE = "verifyCode";
    
    /**
     * 邀请码标识。
     */
    private static final String INVITE_CODE = "inviteCode";
    
    /**
     * 用户昵称标识
     */
    private static final String NICKNAME = "nickName";
    
    /**
     * 用户密码标识
     */
    private static final String PASSWORD = "passWord";
    
    /**
     * 用户密码标识
     */
    private static final String NEW_PASSWORD = "newpassWord";
    
    /**
     * 用户信息标识。
     */
    private static final String USER_INFO = "user_info";
    
    /**
     * 用户类型标识。
     */
    private static final String USER_TYPE = "user_type";
    
    /**
     * 时间戳标识。
     */
    private static final String TIME_STAMP = "time_stamp";
    
    /**
     * 注册账号
     */
    private static final int HTTP_ACTION_REGISTE_ACCOUNT = 2;
    
    /**
     * 绑定手机号
     */
    private static final int HTTP_ACTION_BIND_PHONE = 3;
    
    /**
     * 绑定邮箱
     */
    private static final int HTTP_ACTION_BIND_EMAIL = 4;
    
    /**
     * 解绑手机号或邮箱
     */
    private static final int HTTP_ACTION_UNBIND = 5;
    
    /**
     * 检测手机验证码是否正确
     */
    private static final int HTTP_ACTION_CHECK_MSISDN_VERIFY_CODE = 6;
    
    /**
     * 检测邮箱验证码是否正确
     */
    private static final int HTTP_ACTION_CHECK_EMAIL_VERIFY_CODE = 7;
    
    /**
     * 检测手机号是否已绑定
     */
    private static final int HTTP_ACTION_CHECK_MOBILE_BIND = 8;
    
    /**
     * 检测email是否绑定
     */
    private static final int HTTP_ACTION_CHECK_EMAIL_BIND = 9;
    
    /**
     * 获取手机验证码
     */
    private static final int HTTP_ACTION_GET_MSISDN_VERIFY_CODE = 10;
    
    /**
     * 获取邮箱验证码
     */
    private static final int HTTP_ACTION_GET_EMAIL_VERIFY_CODE = 11;
    
    /**
     * 获取我的个人信息。
     */
    private static final int HTTP_ACTION_GET_MYPROFILE = 12;
    
    /**
     * 更新我的个人信息。
     */
    private static final int HTTP_ACTION_UPDATE_MYPROFILE = 13;
    
    /**
     * 更新我的个人信息。
     */
    private static final int HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY = 14;
    
    /**
     * 修改密码。
     */
    private static final int HTTP_ACTION_MODIFY_PASSWORD = 15;
    
    /**
     * 修改短消息免打扰设置策略。
     */
    private static final int HTTP_ACTION_MODIFY_UNDISTURB = 16;
    
    /**
     * 增加短消息免打扰设置策略。
     */
    private static final int HTTP_ACTION_ADD_UNDISTURB = 17;
    
    /**
     * 接收短消息免打扰设置策略。
     */
    private static final int HTTP_ACTION_GET_UNDISTURB = 18;
    
    /**
     * 重置密码。
     */
    private static final int HTTP_ACTION_RESET_PASSWORD = 19;
    
    /**
     * 更新我的个性签名。
     */
    private static final int HTTP_ACTION_UPDATE_SIGNATURE = 20;
    
    /**
     * 获取验证码失败
     */
    private static final int HTTP_ACTION_UPDATE_PRIVACY_MATERIAL = 25;
    
    /**
     * 用手机号码重置密码类型标识
     */
    private static final String PHONE_TYPE = "10";
    
    /**
     * ; 用邮箱重置密码类型标识
     */
    private static final String EMAIL_TYPE = "2";
    
    /**
     * 
     * [构造简要说明]
     */
    public UserManager()
    {
        super();
    }
    
    /**
     * 封装Send()方法<BR>
     * 发送修改个人资料请求 发送服务器信息
     * 
     * @param user
     *            ContactInfoModel
     * @param iListener
     *            IHttpListener
     */
    public void updatePrivateProfile(ContactInfoModel user,
            IHttpListener iListener)
    {
        
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(ContactInfoModel.CONTACT_INFO_MODEL, user);
        super.send(HTTP_ACTION_UPDATE_MYPROFILE,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 封装Send()方法<BR>
     * 发送修改个人资料请求 发送服务器信息
     * 
     * @param user
     *            ContactInfoModel
     * @param iListener
     *            IHttpListener
     */
    public void updatePrivateSignature(ContactInfoModel user,
            IHttpListener iListener)
    {
        
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(ContactInfoModel.CONTACT_INFO_MODEL, user);
        super.send(HTTP_ACTION_UPDATE_SIGNATURE,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 查询个人资料 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param timeStamp
     *            String
     * @param iListener
     *            IHttpListener
     */
    public void requestPrivateProfile(String timeStamp, IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.TIME_STAMP, timeStamp);
        super.send(HTTP_ACTION_GET_MYPROFILE, sendData, iListener);
    }
    
    /**
     * 更新个人隐私设置 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param autoConfirmFriendStr
     *            String
     * @param friendPrivacyStr
     *            String
     * @param iListener
     *            IHttpListener
     */
    public void updatePrivateProfilePrivacy(String autoConfirmFriendStr,
            String friendPrivacyStr, IHttpListener iListener)
    {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put(UserConfigModel.AUTO_CONFIRM_FRIEND, autoConfirmFriendStr);
        data.put(UserConfigModel.FRIEND_PRIVACY, friendPrivacyStr);
        super.send(HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY, data, iListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param oldPassword
     *            String
     * @param newPassword
     *            String
     * @param iListener
     *            IHttpListener
     */
    
    public void requestModifyPassword(String oldPassword, String newPassword,
            IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.PASSWORD, oldPassword);
        sendData.put(UserManager.NEW_PASSWORD, newPassword);
        super.send(HTTP_ACTION_MODIFY_PASSWORD,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 更新个人资料 privacy [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param privacy
     *            String
     * @param iListener
     *            IHttpListener
     */
    public void updateProfilePrivacyMaterial(String privacy,
            IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserConfigModel.PRIVACY, privacy);
        super.send(HTTP_ACTION_UPDATE_PRIVACY_MATERIAL, sendData, iListener);
    }
    
    /**
     * 获得手机验证码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param phoneNumber
     *            phoneNumber
     * @param operType
     *            operType
     * @param iListener
     *            IHttpListener
     */
    public void sendRequestGetMsisdnVerifyCode(String phoneNumber,
            String operType, IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.PHONE_NUMBER, phoneNumber);
        sendData.put(UserManager.OPER_TYPE, operType);
        send(HTTP_ACTION_GET_MSISDN_VERIFY_CODE,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 绑定手机 [功能详细描述]
     * 
     * @param phoneNum
     *            phoneNum
     * @param verifyCode
     *            verifyCode
     * @param iListener
     *            IHttpListener
     */
    public void sendRequestBindPhone(String phoneNum, String verifyCode,
            IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.PHONE_NUMBER, phoneNum);
        sendData.put(UserManager.VERIFY_CODE, verifyCode);
        send(HTTP_ACTION_BIND_PHONE,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 解绑手机 [功能详细描述]
     * 
     * @param opreType
     *            opreType
     * @param verifyCode
     *            verifyCode
     * @param iListener
     *            IHttpListener
     */
    public void sendRequestUnBindPhone(String opreType, String verifyCode,
            IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.OPER_TYPE, opreType);
        sendData.put(UserManager.VERIFY_CODE, verifyCode);
        send(HTTP_ACTION_UNBIND, (HashMap<String, Object>) sendData, iListener);
    }
    
    /**
     * 绑定邮箱 [功能详细描述]
     * 
     * @param emailAdrr
     *            emailAdrr
     * @param nickName
     *            nickName
     * @param iListener
     *            IHttpListener
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#bindMail(java.lang.String,
     *      java.lang.String)
     */
    public void sendRequestBindMail(String emailAdrr, String nickName,
            IHttpListener iListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.EMAIL, emailAdrr);
        sendData.put(UserManager.NICKNAME, nickName);
        send(HTTP_ACTION_BIND_EMAIL,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 重置密码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param number
     *            手机号
     * @param httpListener
     *            IHttpListener
     */
    public void resetPasswordFromNumber(String number,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.PHONE_NUMBER, number);
        send(HTTP_ACTION_CHECK_MOBILE_BIND,
                (HashMap<String, Object>) sendData,
                httpListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param email
     *            邮箱
     * @param httpListener
     *            IHttpListener
     */
    public void resetPasswordFromEmail(String email, IHttpListener httpListener)
    {
        Map<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.USER_INFO, email);
        sendData.put(UserManager.USER_TYPE, EMAIL_TYPE);
        
        send(HTTP_ACTION_RESET_PASSWORD,
                (HashMap<String, Object>) sendData,
                httpListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param number
     *            用户info 手机
     * @param verifyCode
     *            验证码
     * @param password
     *            密码
     * @param httpListener
     *            IHttpListenter
     */
    public void resetPasswordFromNumberAndVerify(String number,
            String verifyCode, String password, IHttpListener httpListener)
    {
        Map<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.USER_INFO, number);
        sendData.put(UserManager.USER_TYPE, PHONE_TYPE);
        sendData.put(UserManager.VERIFY_CODE, verifyCode);
        sendData.put(UserManager.NEW_PASSWORD, password);
        send(HTTP_ACTION_RESET_PASSWORD,
                (HashMap<String, Object>) sendData,
                httpListener);
    }
    
    /**
     * 重置密码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param number
     *            手机号
     * @param openType
     *            类型
     * @param httpListener
     *            IHttpListener
     */
    public void resetPasswordFromNumberWithType(String number, String openType,
            IHttpListener httpListener)
    {
        Map<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.PHONE_NUMBER, number);
        sendData.put(UserManager.OPER_TYPE, openType);
        
        send(HTTP_ACTION_GET_MSISDN_VERIFY_CODE,
                (HashMap<String, Object>) sendData,
                httpListener);
    }
    
    /**
     * 解绑邮箱 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param operType
     *            String
     * @param httpListener
     *            IHttpListener
     */
    public void sendUnBindMail(String operType, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserManager.OPER_TYPE, operType);
        send(HTTP_ACTION_UNBIND, sendData, httpListener);
    }
    
    /**
     * 
     * 获取应用<BR>
     * [功能详细描述]
     * 
     * @param appVerion
     *            应用版本号
     * @param httpListener
     *            Http监听
     */
    public void getApp(String appVerion, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserConfigModel.SYS_APP_INFO_VERSION, appVerion);
        send(GET_ACHIEVE_APP, sendData, httpListener);
    }
    
    /**
     * 
     * 获取 URL地址
     * 
     * @param action
     *            响应动作
     * @param sendData
     *            Map
     * @return URL地址
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getUrl(int,
     *      java.util.Map)
     */
    
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        String url = "";
        switch (action)
        {
            case HTTP_ACTION_REGISTE_ACCOUNT:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "userRegisterServlet";
                break;
            case HTTP_ACTION_CHECK_MOBILE_BIND:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "checkBindMSISDNServlet";
                break;
            case HTTP_ACTION_CHECK_EMAIL_BIND:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "checkBindEmailServlet";
                break;
            case HTTP_ACTION_BIND_PHONE:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "bindMobileServlet";
                break;
            case HTTP_ACTION_CHECK_MSISDN_VERIFY_CODE:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "checkMSISDNVerifyCodeServlet";
                break;
            case HTTP_ACTION_CHECK_EMAIL_VERIFY_CODE:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "checkEmailVerifyCodeServlet";
                break;
            case HTTP_ACTION_BIND_EMAIL:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "getBindEmailLinkServlet";
                break;
            case HTTP_ACTION_UNBIND:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "unBindServlet";
                break;
            case HTTP_ACTION_GET_MSISDN_VERIFY_CODE:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "getMSISDNVerifyCodeServlet";
                break;
            case HTTP_ACTION_GET_EMAIL_VERIFY_CODE:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "getEmailVerifyCodeServlet";
                break;
            
            case HTTP_ACTION_GET_MYPROFILE:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + "v1/profiles/me";
                if (null != sendData)
                {
                    String timeStamp = (String) sendData.get(TIME_STAMP);
                    if (!StringUtil.isNullOrEmpty(timeStamp))
                    {
                        url += "?Timestamp=" + timeStamp;
                    }
                }
                break;
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
            case HTTP_ACTION_UPDATE_SIGNATURE:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + "/v1/profiles/me";
                break;
            case HTTP_ACTION_MODIFY_PASSWORD:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "modifyPasswordServlet";
                break;
            case HTTP_ACTION_ADD_UNDISTURB:
            case HTTP_ACTION_MODIFY_UNDISTURB:
                url = FusionConfig.getInstance().getAasResult().getRifurl()
                        + "/richlifeApp/openIntf/IRichMsgMgt/notifyModUserServiceInfo";
                break;
            case HTTP_ACTION_GET_UNDISTURB:
                url = FusionConfig.getInstance().getAasResult().getRifurl()
                        + "/richlifeApp/openIntf/IRichMsgMgt/queryUserServiceInfo";
                break;
            case HTTP_ACTION_RESET_PASSWORD:
                url = FusionConfig.getInstance().getAasResult().getPortalurl()
                        + "resetPasswdServlet";
                break;
            case GET_ACHIEVE_APP:
                Logger.d(TAG, "GET_ACHIEVE_APP---------------->>>getUrl() ");
                if (sendData != null
                        && sendData.get(UserConfigModel.SYS_APP_INFO_VERSION) == null)
                {
                    url = FusionConfig.getInstance()
                            .getAasResult()
                            .getCabgroupurl()
                            + "/v1/apps/sys/get/all?V=20010825121149&devtype=22001";
                    Logger.d(TAG, "获取系统应用的url = " + url);
                }
                if (sendData != null
                        && sendData.get(UserConfigModel.SYS_APP_INFO_VERSION) != null)
                {
                    url = FusionConfig.getInstance()
                            .getAasResult()
                            .getCabgroupurl()
                            + "/v1/apps/sys/get/all?V="
                            + sendData.get(UserConfigModel.SYS_APP_INFO_VERSION)
                            + "&devtype=22001";
                    Logger.d(TAG, "获取系统应用的url = " + url);
                }
                
                break;
            default:
                break;
        }
        return url;
        
    }
    
    /**
     * 获取HTTP请求信息体
     * 
     * @param action
     *            请求动作
     * @param sendData
     *            请求数据
     * @return android解析后的请求数据
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getBody(int,
     *      java.util.Map)
     */
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        String phoneNumber = "";
        String verifyCode = "";
        String operType = "";
        String password = "";
        String newPassword = "";
        String nickName = "";
        String email = "";
        String strSCCUserName = "";
        String xmlServiceInfo = "";
        String statusStr = "";
        String timesLotsStr = "";
        
        String xmlData = null;
        switch (action)
        {
            case HTTP_ACTION_REGISTE_ACCOUNT:

                String inviteCode = (String) sendData.get(UserManager.INVITE_CODE);
                phoneNumber = (String) sendData.get(UserManager.PHONE_NUMBER);
                String cred = (String) sendData.get(UserManager.CRED);
                password = (String) sendData.get(UserManager.PASSWORD);
                nickName = (String) sendData.get(UserManager.NICKNAME);
                email = (String) sendData.get(UserManager.EMAIL);
                
                xmlData = getRegisterParameterStr(nickName,
                        password,
                        phoneNumber,
                        cred,
                        inviteCode,
                        email);
                break;
            case HTTP_ACTION_CHECK_MOBILE_BIND:
                phoneNumber = (String) sendData.get(UserManager.PHONE_NUMBER);
                xmlData = getCheckMobileBind(phoneNumber);
                break;
            case HTTP_ACTION_CHECK_EMAIL_BIND:
                email = (String) sendData.get(UserManager.EMAIL);
                xmlData = getCheckEmailBind(email);
                break;
            case HTTP_ACTION_BIND_PHONE:
                phoneNumber = (String) sendData.get(UserManager.PHONE_NUMBER);
                verifyCode = (String) sendData.get(UserManager.VERIFY_CODE);
                
                if (phoneNumber != null && phoneNumber.startsWith("00"))
                {
                    phoneNumber = "+" + phoneNumber.substring(2);
                }
                xmlData = getBindPhoneParameterStr(phoneNumber, verifyCode);
                break;
            case HTTP_ACTION_BIND_EMAIL:
                String emailAdd = (String) sendData.get(UserManager.EMAIL);
                nickName = (String) sendData.get(UserManager.NICKNAME);
                xmlData = getBindEmailParameterStr(nickName, emailAdd);
                break;
            case HTTP_ACTION_UNBIND:
                operType = (String) sendData.get(UserManager.OPER_TYPE);
                verifyCode = (String) sendData.get(UserManager.VERIFY_CODE);
                xmlData = getUnBindParameterStr(operType, verifyCode);
                break;
            case HTTP_ACTION_GET_MSISDN_VERIFY_CODE:
                phoneNumber = (String) sendData.get(UserManager.PHONE_NUMBER);
                operType = (String) sendData.get(UserManager.OPER_TYPE);
                xmlData = getMSISDNVerifyCodeParameterStr(phoneNumber, operType);
                break;
            case HTTP_ACTION_GET_EMAIL_VERIFY_CODE:
                email = (String) sendData.get(UserManager.EMAIL);
                operType = (String) sendData.get(UserManager.OPER_TYPE);
                xmlData = getEmailVerifyCodeParameterStr(email, operType);
                break;
            case HTTP_ACTION_CHECK_MSISDN_VERIFY_CODE:
                phoneNumber = (String) sendData.get(UserManager.PHONE_NUMBER);
                verifyCode = (String) sendData.get(UserManager.VERIFY_CODE);
                xmlData = getCheckMSISDNVerifyCode(phoneNumber, verifyCode);
                break;
            case HTTP_ACTION_CHECK_EMAIL_VERIFY_CODE:
                email = (String) sendData.get(UserManager.EMAIL);
                verifyCode = (String) sendData.get(UserManager.VERIFY_CODE);
                xmlData = getCheckEmailVerifyCode(email, verifyCode);
                break;
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_SIGNATURE:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
                ContactInfoModel contactInfo = (ContactInfoModel) sendData.get(ContactInfoModel.CONTACT_INFO_MODEL);
                String friendPrivacy = (String) sendData.get(UserConfigModel.FRIEND_PRIVACY);
                String privacy = (String) sendData.get(UserConfigModel.PRIVACY);
                String autoConfirm = (String) sendData.get(UserConfigModel.AUTO_CONFIRM_FRIEND);
                xmlData = getUpdateMyProfileParamaterStr(contactInfo,
                        friendPrivacy,
                        privacy,
                        autoConfirm);
                break;
            case HTTP_ACTION_MODIFY_PASSWORD:
                password = (String) sendData.get(UserManager.PASSWORD);
                newPassword = (String) sendData.get(UserManager.NEW_PASSWORD);
                xmlData = getModifyparameter(password, newPassword);
                break;
            case HTTP_ACTION_MODIFY_UNDISTURB:
                strSCCUserName = (String) sendData.get(UserConfigModel.USERSYSID);
                statusStr = (String) sendData.get(UserConfigModel.UNDISTURB_POLICY_STATUS);
                timesLotsStr = (String) sendData.get(UserConfigModel.UNDISTURB_POLICY_TIME);
                xmlServiceInfo = getUndisturbModifyRulesParameterStr(timesLotsStr,
                        statusStr);
                xmlData = getUndisturbAndIMParamsStr(strSCCUserName,
                        xmlServiceInfo);
                break;
            case HTTP_ACTION_ADD_UNDISTURB:
                strSCCUserName = (String) sendData.get(UserConfigModel.USERSYSID);
                statusStr = (String) sendData.get(UserConfigModel.UNDISTURB_POLICY_STATUS);
                timesLotsStr = (String) sendData.get(UserConfigModel.UNDISTURB_POLICY_TIME);
                xmlServiceInfo = getUndisturbAddRulesParameterStr(timesLotsStr,
                        statusStr);
                xmlData = getUndisturbAndIMParamsStr(strSCCUserName,
                        xmlServiceInfo);
                break;
            
            case HTTP_ACTION_GET_UNDISTURB:
                strSCCUserName = (String) sendData.get(UserConfigModel.USERSYSID);
                xmlServiceInfo = getQueryUndisturbRulesParamStr();
                xmlData = getUndisturbAndIMParamsStr(strSCCUserName,
                        xmlServiceInfo);
                break;
            case HTTP_ACTION_RESET_PASSWORD:
                String userInfo = (String) sendData.get(UserManager.USER_INFO);
                String userType = (String) sendData.get(UserManager.USER_TYPE);
                verifyCode = (String) sendData.get(UserManager.VERIFY_CODE);
                newPassword = (String) sendData.get(UserManager.NEW_PASSWORD);
                xmlData = getResetParamaterStr(userInfo,
                        userType,
                        verifyCode,
                        newPassword);
                break;
            default:
                break;
        }
        return xmlData;
    }
    
    /**
     * android解析数据
     * 
     * @param action
     *            动作
     * @param sendData
     *            请求消息，客户端向服务器发送的请求数据
     * @param response
     *            响应消息，
     * @return android解析后 封装的最终数据
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#handleData(int,
     *      java.util.Map, java.lang.String)
     */
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        String id = "";
        String timeslots = "";
        String status = "";
        String retInfo = "";
        String retCode = "";
        String updatetime = "";
        String responseData = response.getData();
        switch (action)
        {
            case HTTP_ACTION_REGISTE_ACCOUNT:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                String account = StringUtil.getXmlValue(responseData,
                        "<account>");
                sendData.put(RET_CODE, retCode);
                sendData.put(ACCOUNT, account);
                break;
            case HTTP_ACTION_CHECK_MOBILE_BIND:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                sendData.put(RET_CODE, retCode);
                break;
            
            case HTTP_ACTION_CHECK_EMAIL_BIND:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                sendData.put(RET_CODE, retCode);
                break;
            case HTTP_ACTION_GET_MSISDN_VERIFY_CODE:
            case HTTP_ACTION_GET_EMAIL_VERIFY_CODE:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                //演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
                //                String stCode = StringUtil.getXmlValue(responseData, "<stcode>");
                String stCode = StringUtil.getXmlValue(responseData,
                        "<checkcode>");
                sendData.put(RET_CODE, retCode);
                sendData.put(VERIFY_CODE, stCode);
                break;
            case HTTP_ACTION_CHECK_MSISDN_VERIFY_CODE:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                String cred = StringUtil.getXmlValue(responseData, "<cred>");
                sendData.put(RET_CODE, retCode);
                sendData.put(CRED, cred);
                break;
            case HTTP_ACTION_CHECK_EMAIL_VERIFY_CODE:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                String eCred = StringUtil.getXmlValue(responseData, "<eCred>");
                sendData.put(RET_CODE, retCode);
                sendData.put(CRED, eCred);
                break;
            case HTTP_ACTION_RESET_PASSWORD:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                sendData.put(RET_CODE, retCode);
                break;
            case HTTP_ACTION_BIND_EMAIL:
            case HTTP_ACTION_BIND_PHONE:
            case HTTP_ACTION_UNBIND:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                sendData.put(RET_CODE, retCode);
                break;
            
            case HTTP_ACTION_MODIFY_PASSWORD:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                sendData.put(RET_CODE, retCode);
                break;
            case HTTP_ACTION_GET_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
            case HTTP_ACTION_UPDATE_SIGNATURE:
                JSONObject resultObj = JsonUtils.newJSONObject(responseData);
                if (resultObj == null)
                {
                    return null;
                }
                else
                {
                    return parseJSONResult(action,
                            (HashMap<String, Object>) sendData,
                            resultObj);
                }
            case HTTP_ACTION_ADD_UNDISTURB:
            case HTTP_ACTION_MODIFY_UNDISTURB:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                retInfo = StringUtil.getXmlValue(responseData, "<retInfo>");
                return new String[] { retCode, retInfo };
            case HTTP_ACTION_GET_UNDISTURB:
                retCode = StringUtil.getXmlValue(responseData, "<retCode>");
                retInfo = StringUtil.getXmlValue(responseData, "<retInfo>");
                CharSequence styledText = Html.fromHtml(retInfo);
                retInfo = styledText.toString();
                
                if (retInfo.contains("id=\""))
                {
                    id = retInfo.substring(responseData.indexOf("id=\"") + 9,
                            retInfo.indexOf("\">"));
                }
                timeslots = StringUtil.getXmlValue(responseData, "<timeslots>");
                status = StringUtil.getXmlValue(responseData, "<status>");
                updatetime = StringUtil.getXmlValue(responseData,
                        "<updatetime>");
                
                return new String[] { retCode, id, timeslots, status,
                        updatetime };
                
            case GET_ACHIEVE_APP:
                /**
                 * 把JSON数据封装一个JSON对象，需要对该对象操作
                 */
                JSONObject resultObjApp = JsonUtils.newJSONObject(responseData);
                
                if (null != resultObjApp)
                {
                    HashMap<String, Object> resulrMap = parseAppJSONResult(resultObjApp);
                    Logger.d(TAG, "(null == appList) = " + (null == resulrMap));
                    return resulrMap;
                }
                else
                {
                    Logger.d(TAG, "resultObjApp == null !!!!");
                }
                
                return null;
            default:
                break;
        }
        return sendData;
    }
    
    private HashMap<String, Object> parseAppJSONResult(JSONObject resultObjApp)
    {
        Logger.d(TAG, "parseAppJSONResult----->enter");
        
        /**
         * 应用列表数据
         */
        JSONArray appListObjt = JsonUtils.getJSONArray(resultObjApp, "AppInfos");
        String version = JsonUtils.getString(resultObjApp, "V");
        HashMap<String, Object> results = new HashMap<String, Object>();
        ArrayList<SysAppInfoModel> appList = new ArrayList<SysAppInfoModel>();
        if (appListObjt != null)
        {
            int listSize = appListObjt.length();
            Logger.d(TAG, "AppListObj length is : " + listSize);
            for (int i = 0; i < listSize; ++i)
            {
                JSONObject appObj = JsonUtils.getJSONObject(appListObjt, i);
                if (appObj != null)
                {
                    SysAppInfoModel app = new SysAppInfoModel();
                    
                    app.setAppId(JsonUtils.getString(appObj, "ID"));
                    app.setName(JsonUtils.getString(appObj, "Name"));
                    app.setType(Integer.parseInt(JsonUtils.getString(appObj,
                            "Type")));
                    app.setDesc(JsonUtils.getString(appObj, "Desc"));
                    app.setIconUrl(JsonUtils.getString(appObj, "Icon"));
                    //                    app.setIconUrl("http://123.125.97.193:8081/storageWeb/servlet/GetFileByURLServlet?root=/mnt/dir1&fileid=0A2c55c524fe5c83bbe965d878768f921a.png&ci=0b11S8ZKj10201020120316184429091&cn=%E6%B2%83%E9%98%85%E8%AF%BB%2803-15-14-13-38%29&ct=1&code=3D7A450ED79029C140AEF7AF1C6499B9");
                    app.setAppUrl(JsonUtils.getString(appObj, "App"));
                    app.setUpdateTime(JsonUtils.getString(appObj, "Update"));
                    app.setSso(JsonUtils.getString(appObj, "SSO"));
                    appList.add(app);
                    Logger.d(TAG, app.getIconUrl());
                }
            }
            results.put(UserConfigModel.SYS_APP_INFO_VERSION, version);
            results.put(SYSAPP, appList);
            return results;
        }
        else
        {
            Logger.d("GET_ACHIEVE_APP", "AppListObj is null");
        }
        return results;
    }
    
    /**
     * 获取request请求方式<BR>
     * 
     * @param action
     *            请求Action
     * @return RequestMethod request请求方式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        switch (action)
        {
            case HTTP_ACTION_GET_MYPROFILE:
            case GET_ACHIEVE_APP:
                return RequestMethod.GET;
                
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_SIGNATURE:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
                return RequestMethod.PUT;
            case HTTP_ACTION_BIND_EMAIL:
            case HTTP_ACTION_BIND_PHONE:
                return RequestMethod.POST;
            default:
                break;
        }
        return super.getRequestMethod(action);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param action
     *            int
     * @return list 动作的属性列表
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestProperties(int)
     */
    @Override
    protected List<NameValuePair> getRequestProperties(int action)
    {
        List<NameValuePair> temp = super.getRequestProperties(action);
        switch (action)
        {
            case HTTP_ACTION_GET_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
            case HTTP_ACTION_UPDATE_SIGNATURE:
            case GET_ACHIEVE_APP:
                temp.add(new BasicNameValuePair("Authorization",
                        FusionConfig.getInstance().getCabReqAuthorization()));
                break;
            case HTTP_ACTION_MODIFY_UNDISTURB:
            case HTTP_ACTION_ADD_UNDISTURB:

                temp.add(new BasicNameValuePair("Authorization",
                        FusionConfig.getInstance().getOseReqAuthorization()));
                temp.add(new BasicNameValuePair("SOAPAction",
                        "\"UMA.SCC_UMA_USER_MNG#notifyModUserServiceInfo\""));
                break;
            case HTTP_ACTION_MODIFY_PASSWORD:
                temp.add(new BasicNameValuePair("Authorization",
                        FusionConfig.getInstance().getOseReqAuthorization()));
                break;
            
            case HTTP_ACTION_GET_UNDISTURB:
                temp.add(new BasicNameValuePair("Authorization",
                        FusionConfig.getInstance().getOseReqAuthorization()));
                temp.add(new BasicNameValuePair("SOAPAction",
                        "\"UMA.SCC_UMA_USER_MNG#queryUserServiceInfo\""));
                break;
            case HTTP_ACTION_BIND_EMAIL:
            case HTTP_ACTION_BIND_PHONE:
            case HTTP_ACTION_UNBIND:
                temp.add(new BasicNameValuePair("Authorization",
                        FusionConfig.getInstance().getOseReqAuthorization()));
                break;
            case HTTP_ACTION_REGISTE_ACCOUNT:
            case HTTP_ACTION_CHECK_MOBILE_BIND:
            case HTTP_ACTION_GET_MSISDN_VERIFY_CODE:
            case HTTP_ACTION_GET_EMAIL_VERIFY_CODE:
            case HTTP_ACTION_CHECK_MSISDN_VERIFY_CODE:
            case HTTP_ACTION_CHECK_EMAIL_VERIFY_CODE:
            case HTTP_ACTION_CHECK_EMAIL_BIND:
            case HTTP_ACTION_RESET_PASSWORD:
                return null;
        }
        return temp;
    }
    
    /**
     * 获取http内容类型。 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param action
     *            http内容类型
     * @return http内容
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getContentType(int)
     */
    @Override
    protected ContentType getContentType(int action)
    {
        ContentType type = ContentType.XML;
        switch (action)
        {
            case HTTP_ACTION_GET_MYPROFILE:
            case HTTP_ACTION_UPDATE_MYPROFILE:
            case HTTP_ACTION_UPDATE_PRIVACY_MATERIAL:
            case HTTP_ACTION_UPDATE_MYPROFILE_PRIVACY:
            case HTTP_ACTION_UPDATE_SIGNATURE:
            case GET_ACHIEVE_APP:
                type = ContentType.JSON;
                break;
            default:
                break;
        }
        return type;
    }
    
    /**
     * 
     * 生成绑定手机所需参数
     * 
     * @param phoneNum
     *            String
     * @param verifyCode
     *            String
     * @return String
     */
    private String getBindPhoneParameterStr(String phoneNum, String verifyCode)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version = \"1.0\" encoding=\"utf-8\" ?>");
        xml.append("<root>");
        xml.append("<account>");
        xml.append(FusionConfig.getInstance().getAasResult().getUserID());
        xml.append("</account><MSISDN>");
        xml.append(phoneNum);
        xml.append("</MSISDN><verificationCode>");
        xml.append(verifyCode);
        xml.append("</verificationCode></root>");
        return xml.toString();
    }
    
    /**
     * 
     * 获取检测手机号码是否已绑定请求数据
     * 
     * @param phoneNum
     *            电话号
     * @return String
     */
    
    private String getCheckMobileBind(String phoneNum)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<MSISDN>");
        // 去除"0086"、"+86"
        phoneNum = StringUtil.fixPortalPhoneNumber(phoneNum);
        xml.append(phoneNum);
        xml.append("</MSISDN></root>");
        
        return xml.toString();
    }
    
    /**
     * 
     * 获取检测邮箱是否已绑定请求数据
     * 
     * @param email
     *            邮箱地址
     * @return 检测邮箱是否已绑定请求数据
     */
    private String getCheckEmailBind(String email)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<email>");
        xml.append(email);
        xml.append("</email></root>");
        return xml.toString();
    }
    
    /**
     * 
     * 生成绑定邮箱所需参数
     * 
     * @param displayName
     *            用户昵称
     * @param emailAdd
     *            邮箱地址
     * @return 绑定邮箱参数
     */
    private String getBindEmailParameterStr(String displayName, String emailAdd)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<account>");
        xml.append(FusionConfig.getInstance().getAasResult().getUserID());
        xml.append("</account>");
        
        if (displayName != null)
        {
            xml.append("<displayName>");
            xml.append(displayName);
            xml.append("</displayName>");
        }
        else
        {
            xml.append("<displayName>");
            xml.append(FusionConfig.getInstance().getAasResult().getUserID());
            xml.append("</displayName>");
        }
        xml.append("<emailAddr>");
        xml.append(emailAdd);
        xml.append("</emailAddr></root>");
        
        return xml.toString();
    }
    
    /**
     * 生成解绑手机号或邮箱所需参数
     * 
     * @param operType
     *            解绑方式
     * @param verifyCode
     *            手机短信验证码
     * @return String 解绑参数
     */
    private String getUnBindParameterStr(String operType, String verifyCode)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<account>");
        xml.append(FusionConfig.getInstance().getAasResult().getUserID());
        xml.append("</account>");
        xml.append("<operType>");
        xml.append(operType);
        xml.append("</operType>");
        if (verifyCode != null)
        {
            xml.append("<verificationCode>");
            xml.append(verifyCode);
            xml.append("</verificationCode>");
        }
        xml.append("</root>");
        
        return xml.toString();
    }
    
    /**
     * 
     * 生成获取手机验证码所需参数。
     * 
     * @param phoneNumber
     *            手机号码。
     * @param operType
     *            获取手机验证码用途 0：绑定号码验证 1：解绑号码验证 2：重置密码验证。
     * @return 获取手机验证码所需参数。
     */
    private String getMSISDNVerifyCodeParameterStr(String phoneNumber,
            String operType)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<MSISDN>");
        // 注册时如果加了"+86"时，除去"+86"
        phoneNumber = StringUtil.fixPortalPhoneNumber(phoneNumber);
        xml.append(phoneNumber);
        xml.append("</MSISDN><operType>");
        xml.append(operType);
        xml.append("</operType></root>");
        
        return xml.toString();
    }
    
    /**
     * 生成检测手机验证码是否正确所需参数。
     * 
     * @param phoneNumber
     *            手机号码。
     * @param verifyCode
     *            短信验证码。
     * @return 检测手机验证码是否正确所需参数。
     */
    private String getCheckMSISDNVerifyCode(String phoneNumber,
            String verifyCode)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<MSISDN>");
        // 短信验证码对应的手机号码。
        // 格式固定为：“国际码前缀+”+“国家码”+“手机号码”，比如：+8618655555000。
        phoneNumber = "+86" + StringUtil.fixPortalPhoneNumber(phoneNumber);
        xml.append(phoneNumber);
        xml.append("</MSISDN><verificationCode>");
        xml.append(verifyCode);
        xml.append("</verificationCode></root>");
        
        return xml.toString();
    }
    
    /**
     * 检测邮箱验证码是否正确 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param email
     *            邮箱
     * @param verifyCode
     *            验证码
     * @return 检测手机验证码是否正确所需参数
     */
    private String getCheckEmailVerifyCode(String email, String verifyCode)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<email>");
        xml.append(email);
        xml.append("</email><eVerificationCode>");
        xml.append(verifyCode);
        xml.append("</eVerificationCode></root>");
        
        return xml.toString();
    }
    
    /**
     * 生成注册所需参数。
     * 
     * @param nickName
     *            用户昵称。
     * @param passWord
     *            密码。
     * @param phoneNumber
     *            电话号码。
     * @param cred
     *            验证码检测后返回的凭证。
     * @param invateCode
     *            邀请码。
     * @param email
     *            邮箱地址。
     * @return String 返回注册所需参数。
     */
    private String getRegisterParameterStr(String nickName, String passWord,
            String phoneNumber, String cred, String invateCode, String email)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<displayName><![CDATA[");
        xml.append(nickName);
        xml.append("]]></displayName><passwd>");
        xml.append(DecodeUtil.sha256Encode(passWord));
        xml.append("</passwd>");
        
        if (!StringUtil.isNullOrEmpty(invateCode))
        {
            xml.append("<inviteCode>");
            xml.append(invateCode);
            xml.append("</inviteCode>");
        }
        if (!StringUtil.isNullOrEmpty(phoneNumber))
        {
            xml.append("<MSISDN>");
            xml.append(phoneNumber);
            xml.append("</MSISDN>");
            if (!StringUtil.isNullOrEmpty(cred))
            {
                xml.append("<cred>");
                xml.append(cred);
                xml.append("</cred>");
            }
        }
        if (!StringUtil.isNullOrEmpty(email))
        {
            xml.append("<email>");
            xml.append(email);
            xml.append("</email>");
            
            if (!StringUtil.isNullOrEmpty(cred))
            {
                xml.append("<eCred>");
                xml.append(cred);
                xml.append("</eCred>");
            }
        }
        xml.append("</root>");
        
        return xml.toString();
    }
    
    /**
     * 解析个人信息JSON信息。
     * 
     * @param params
     *            封装个人信息Map。
     * @param resultObj
     *            JSON对象。
     */
    private Object parseJSONResult(int action, Map<String, Object> params,
            JSONObject resultObj)
    {
        switch (action)
        {
            case HTTP_ACTION_GET_MYPROFILE:
                String userId = FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId();
                String account = FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID();
                Map<String, Object> modelMap = new HashMap<String, Object>();
                ContactInfoModel myProfile = null;
                UserConfigModel config1 = null;
                UserConfigModel config2 = null;
                UserConfigModel config3 = null;
                JSONObject privateProfileObj = null;
                
                // 用户私有信息
                if (resultObj.has("PrivateProfile"))
                {
                    myProfile = new ContactInfoModel();
                    myProfile.setFriendSysId(userId);
                    myProfile.setFriendUserId(account);
                    privateProfileObj = JsonUtils.getJSONObject(resultObj,
                            "PrivateProfile");
                    parsePrivateProfileObj(params, myProfile, privateProfileObj);
                    modelMap.put("PrivateProfile", myProfile);
                }
                // 用户被添加好友控制策略
                if (resultObj.has("FriendPrivacy"))
                {
                    config1 = new UserConfigModel();
                    config1.setKey("FriendPrivacy");
                    String friendPrivacy = JsonUtils.getString(resultObj,
                            "FriendPrivacy");
                    config1.setValue(friendPrivacy);
                    modelMap.put("FriendPrivacy", config1);
                }
                // 用户被加好友确认策略
                if (resultObj.has("AutoConfirmFriend"))
                {
                    config2 = new UserConfigModel();
                    String autoConfirm = JsonUtils.getString(resultObj,
                            "AutoConfirmFriend");
                    config2.setKey("AutoConfirmFriend");
                    config2.setValue(autoConfirm);
                    modelMap.put("AutoConfirmFriend", config2);
                }
                
                // 用户当前设置的对外授权规则
                if (resultObj.has("Privacy"))
                {
                    config3 = new UserConfigModel();
                    String privacy = JsonUtils.getString(resultObj, "Privacy");
                    config3.setKey("Privacy");
                    config3.setValue(privacy);
                    modelMap.put("Privacy", config3);
                }
                return modelMap;
            case HTTP_ACTION_UPDATE_MYPROFILE:
                break;
            default:
                break;
        }
        return null;
    }
    
    /**
     * 
     * 解析我的个人信息。
     * 
     * @param params
     *            参数map。
     * @param myProfile
     *            个人信息封装类。
     * @param faceModel
     *            头像信息封装类。
     * @param privateProfileObj
     *            JSON数据。
     * @return 个人信息封装类。
     */
    private Map<String, Object> parsePrivateProfileObj(
            Map<String, Object> params, ContactInfoModel myProfile,
            JSONObject privateProfileObj)
    {
        if ((myProfile != null) && (privateProfileObj != null))
        {
            // 生日
            myProfile.setBirthday(JsonUtils.getString(privateProfileObj,
                    "Birthday"));
            
            // 性别
            myProfile.setGender(JsonUtils.getInt(privateProfileObj, "Gender"));
            
            // 婚姻状况
            myProfile.setMarriageStatus(JsonUtils.getInt(privateProfileObj,
                    "MarriageStatus"));
            
            // 用户详细地址
            JSONObject addrObj = JsonUtils.getJSONObject(privateProfileObj,
                    "Address");
            if (addrObj != null)
            {
                // 国家
                myProfile.setCountry(JsonUtils.getString(addrObj, "Country"));
                
                // 省份
                myProfile.setProvince(JsonUtils.getString(addrObj, "Province"));
                
                // 城市
                myProfile.setCity(JsonUtils.getString(addrObj, "City"));
                
                // 街道
                myProfile.setStreet(JsonUtils.getString(addrObj, "Street"));
                
                // 邮政编码
                myProfile.setPostalCode(JsonUtils.getString(addrObj,
                        "Postalcode"));
                
                // 邮政信箱
                myProfile.setBuilding(JsonUtils.getString(addrObj, "Building"));
                
                // 地址详情
                myProfile.setAddress(JsonUtils.getString(addrObj,
                        "AddressLine1"));
            }
            
            // 用户绑定的手机号
            myProfile.setPrimaryMobile(JsonUtils.getString(privateProfileObj,
                    "PrimaryMobile"));
            
            // 用户绑定的邮箱
            myProfile.setPrimaryEmail(JsonUtils.getString(privateProfileObj,
                    "Email"));
            
            // 用户待绑定的手机和邮箱信息(扩展信息)
            JSONArray extPropertyArray = JsonUtils.getJSONArray(privateProfileObj,
                    "ExtProperties");
            if (null != extPropertyArray)
            {
                for (int i = extPropertyArray.length() - 1; i >= 0; i--)
                {
                    JSONObject extPropertyObj = JsonUtils.getJSONObject(extPropertyArray,
                            i);
                    
                    if (null != extPropertyObj)
                    {
                        // 待绑定的手机
                        String name = JsonUtils.getString(extPropertyObj,
                                "Name");
                        if ("X-ToBeBindPrimaryMobile".equals(name))
                        {
                            myProfile.setToBeBindPrimaryMobile(JsonUtils.getString(extPropertyObj,
                                    "Value"));
                        }
                        else if ("X-ToBeBindEmail".equals(name))
                        {
                            // 待绑定的邮箱
                            myProfile.setToBeBindPrimaryEmail(JsonUtils.getString(extPropertyObj,
                                    "Value"));
                        }
                        
                    }
                }
            }
            
            // 用户工作信息
            JSONArray workInfoArray = JsonUtils.getJSONArray(privateProfileObj,
                    "WorkInfo");
            if (workInfoArray != null)
            {
                JSONObject workInfoObj = JsonUtils.getJSONObject(workInfoArray,
                        0);
                
                if (workInfoObj != null)
                {
                    // 公司名称
                    myProfile.setCompany(JsonUtils.getString(workInfoObj,
                            "Company"));
                    
                    // 部门名称
                    myProfile.setDeparment(JsonUtils.getString(workInfoObj,
                            "Department"));
                    
                    // 职位角色
                    myProfile.setTitle(JsonUtils.getString(workInfoObj, "Title"));
                }
            }
            
            // 用户的学校信息
            JSONArray schoolArray = JsonUtils.getJSONArray(privateProfileObj,
                    "SchoolInfo");
            if (schoolArray != null)
            {
                JSONObject schoolObj = JsonUtils.getJSONObject(schoolArray, 0);
                
                if (schoolObj != null)
                {
                    // 学校名称
                    myProfile.setSchool(JsonUtils.getString(schoolObj, "School"));
                    
                    // 专业名称
                    myProfile.setCourse(JsonUtils.getString(schoolObj, "Course"));
                    
                    // 入学时间、级、届
                    myProfile.setBatch(JsonUtils.getString(schoolObj, "Batch"));
                }
            }
            
            // 签名
            myProfile.setSignature(JsonUtils.getString(privateProfileObj,
                    "Notes"));
            
            // 年龄
            myProfile.setAge(JsonUtils.getString(privateProfileObj, "Age"));
            
            // 生肖
            myProfile.setZodiac(JsonUtils.getInt(privateProfileObj, "Zodiac"));
            
            // 星座
            myProfile.setAstro(JsonUtils.getInt(privateProfileObj, "Astro"));
            
            // 血型
            myProfile.setBlood(JsonUtils.getInt(privateProfileObj, "Blood"));
            
            // 最后更新时间
            myProfile.setLastUpdate(JsonUtils.getString(privateProfileObj,
                    "LastUpdate"));
            
            // 昵称
            String nickName = JsonUtils.getString(privateProfileObj,
                    "DisplayName");
            if (nickName != null)
            {
                myProfile.setDisplayName(nickName);
                myProfile.setNickName(nickName);
            }
            
            // 用户爱好
            myProfile.setHobby(JsonUtils.getString(privateProfileObj, "Hobby"));
            
            // 用户等级
            myProfile.setLevel(JsonUtils.getInt(privateProfileObj, "Level"));
            
            // 好友自我描述
            myProfile.setDescription(JsonUtils.getString(privateProfileObj,
                    "Introduction"));
            
            String photoUrl = JsonUtils.getString(privateProfileObj, "PhotoURL");
            if (StringUtil.isNullOrEmpty(photoUrl))
            {
                // 如果url为空，则直接取photoData (为了兼容一期的账号)
                String photoData = JsonUtils.getString(privateProfileObj,
                        "PhotoData");
                if (!StringUtil.isNullOrEmpty(photoData))
                {
                    // Base64.decode(photoData)
                    myProfile.setFaceBytes(Base64.decode(photoData));
                }
            }
            else
            {
                myProfile.setFaceUrl(photoUrl);
            }
            
            return params;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 获取更新个人信息消息体。
     * 
     * @param contactInfo
     *            个人信息封装对象。
     * @param friendPrivacy
     *            用户当前设置的对外授权规则。
     * @param privacy
     *            用户被添加好友控制策略。
     * @param autoConfirm
     *            用户被加好友确认策略。
     * @return 更新个人信息消息体。
     */
    private String getUpdateMyProfileParamaterStr(ContactInfoModel contactInfo,
            String friendPrivacy, String privacy, String autoConfirm)
    {
        JSONObject requesObj = new JSONObject();
        if (contactInfo != null)
        {
            JSONObject privateObj = switchToPrivateProfileObj(contactInfo);
            
            if (privateObj != null)
            {
                JsonUtils.putPairIntoJSONObject(requesObj,
                        "PrivateProfile",
                        privateObj);
            }
        }
        
        if (!StringUtil.isNullOrEmpty(friendPrivacy))
        {
            JsonUtils.putPairIntoJSONObject(requesObj,
                    "FriendPrivacy",
                    friendPrivacy);
        }
        
        if (!StringUtil.isNullOrEmpty(privacy))
        {
            JsonUtils.putPairIntoJSONObject(requesObj, "Privacy", privacy);
        }
        
        if (!StringUtil.isNullOrEmpty(autoConfirm))
        {
            JsonUtils.putPairIntoJSONObject(requesObj,
                    "AutoConfirmFriend",
                    autoConfirm);
        }
        
        return requesObj.toString();
    }
    
    /**
     * 封装我的个人信息JSON数据。
     * 
     * @param info
     *            个人信息封装类。
     * @return 个人信息JSON数据。
     */
    private JSONObject switchToPrivateProfileObj(ContactInfoModel info)
    {
        if (info == null)
        {
            return null;
        }
        
        JSONObject privateProfileObj = new JSONObject();
        
        putStringIntoJson(privateProfileObj, "Birthday", info.getBirthday());
        
        // Gender
        putIntIntoJson(privateProfileObj, "Gender", info.getGender());
        
        // MarriageStatus
        putIntIntoJson(privateProfileObj,
                "MarriageStatus",
                info.getMarriageStatus());
        
        // AddressObj
        JSONObject addressObj = new JSONObject();
        
        putStringIntoJson(addressObj, "Postalcode", info.getPostalCode());
        putStringIntoJson(addressObj, "Building", info.getBuilding());
        putStringIntoJson(addressObj, "Street", info.getStreet());
        putStringIntoJson(addressObj, "AddressLine1", info.getAddress());
        
        if (info.getCity() != null)
        {
            JsonUtils.putPairIntoJSONObject(addressObj, "City", info.getCity());
        }
        else
        {
            JsonUtils.putPairIntoJSONObject(addressObj, "City", "");
        }
        if (info.getProvince() != null)
        {
            JsonUtils.putPairIntoJSONObject(addressObj,
                    "Province",
                    info.getProvince());
        }
        else
        {
            JsonUtils.putPairIntoJSONObject(addressObj, "Province", "");
        }
        if (info.getCountry() != null)
        {
            info.getCountry();
        }
        else
        {
            JsonUtils.putPairIntoJSONObject(addressObj, "Country", "");
        }
        
        if (addressObj.length() > 0)
        {
            JsonUtils.putPairIntoJSONObject(privateProfileObj,
                    "Address",
                    addressObj);
        }
        
        // PrimaryMobile
        putStringIntoJson(privateProfileObj,
                "PrimaryMobile",
                info.getPrimaryMobile());
        
        // Email
        putStringIntoJson(privateProfileObj, "Email", info.getPrimaryEmail());
        
        JSONArray workinfoArray = new JSONArray();
        // WorkInfo
        JSONObject workInfoObject = new JSONObject();
        
        putStringIntoJson(workInfoObject, "Company", info.getCompany());
        putStringIntoJson(workInfoObject, "Department", info.getDeparment());
        putStringIntoJson(workInfoObject, "Title", info.getTitle());
        
        if (workInfoObject.length() > 0)
        {
            workinfoArray.put(workInfoObject);
        }
        JsonUtils.putPairIntoJSONObject(privateProfileObj,
                "WorkInfo",
                workinfoArray);
        
        // SchoolInfo
        JSONArray schoolInfoArray = new JSONArray();
        JSONObject schoolInfoObject = new JSONObject();
        
        putStringIntoJson(schoolInfoObject, "School", info.getSchool());
        putStringIntoJson(schoolInfoObject, "Course", info.getCourse());
        putStringIntoJson(schoolInfoObject, "Batch", info.getBatch());
        
        if (schoolInfoObject.length() > 0)
        {
            schoolInfoArray.put(schoolInfoObject);
        }
        JsonUtils.putPairIntoJSONObject(privateProfileObj,
                "SchoolInfo",
                schoolInfoArray);
        
        // photo url
        putStringIntoJson(privateProfileObj, "PhotoURL", info.getFaceUrl());
        
        // Notes
        if (info.getSignature() != null)
        {
            JsonUtils.putPairIntoJSONObject(privateProfileObj,
                    "Notes",
                    info.getSignature());
        }
        // Introduction
        if (info.getDescription() != null)
        {
            JsonUtils.putPairIntoJSONObject(privateProfileObj,
                    "Introduction",
                    info.getDescription());
        }
        // Age
        putStringIntoJson(privateProfileObj, "Age", info.getAge());
        
        // Zodiac
        putIntIntoJson(privateProfileObj, "Zodiac", info.getZodiac());
        
        // Astro
        putIntIntoJson(privateProfileObj, "Astro", info.getAstro());
        
        // Blood
        putIntIntoJson(privateProfileObj, "Blood", info.getBlood());
        
        // Account
        putStringIntoJson(privateProfileObj, "Account", info.getFriendUserId());
        
        // LastUpdate
        SimpleDateFormat df2 = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        putStringIntoJsonWithDefault(privateProfileObj,
                "LastUpdate",
                info.getLastUpdate(),
                df2.format(new Date()));
        
        // DisplayName
        if (info.getNickName() != null)
        {
            JsonUtils.putPairIntoJSONObject(privateProfileObj,
                    "DisplayName",
                    info.getNickName());
        }
        
        // Hobby
        if (info.getHobby() != null)
        {
            JsonUtils.putPairIntoJSONObject(privateProfileObj,
                    "Hobby",
                    info.getHobby());
        }
        
        return privateProfileObj;
        
    }
    
    /**
     * 将int型数值加入到JSON对象中。
     * 
     * @param json
     * @param key
     * @param value
     */
    private void putIntIntoJson(JSONObject json, String key, int value)
    {
        if (value >= 0)
        {
            JsonUtils.putPairIntoJSONObject(json, key, value);
        }
    }
    
    /**
     * 将字符串加入到JSON对象中。
     * 
     * @param json
     *            需要加入的JSON对象。
     * @param key
     *            需要加入字符串对应的Key。
     * @param value
     *            需要加入的字符串。
     */
    private void putStringIntoJson(JSONObject json, String key, String value)
    {
        if (!StringUtil.isNullOrEmpty(value))
        {
            JsonUtils.putPairIntoJSONObject(json, key, value);
        }
    }
    
    /**
     * 将字符串加入到JSON对象中，如果引字符串有默认值，则使用此默认值。
     * 
     * @param json
     * @param key
     * @param value
     * @param defaultValue
     */
    private void putStringIntoJsonWithDefault(JSONObject json, String key,
            String value, String defaultValue)
    {
        String var = value;
        
        if (null == value)
        {
            var = defaultValue;
        }
        
        JsonUtils.putPairIntoJSONObject(json, key, var);
    }
    
    /**
     * 生成获取邮箱验证码所需参数。
     * 
     * @param email
     *            email
     * @param operType
     *            获取邮箱验证码用途 0：绑定号码验证 1：解绑号码验证 2：重置密码验证。
     * @return 获取邮箱验证码所需参数。
     */
    private String getEmailVerifyCodeParameterStr(String email, String operType)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<email>").append(email).append("</email>");
        xml.append("<operType>").append(operType).append("</operType>");
        xml.append("</root>");
        return xml.toString();
    }
    
    /**
     * 
     * 生成修改密码所需参数。
     * 
     * @param oldPass
     *            旧密码。
     * @param newPass
     *            新密码。
     * @return 修改密码所需参数。
     */
    private String getModifyparameter(String oldPass, String newPass)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<account>");
        xml.append(FusionConfig.getInstance().getAasResult().getUserID());
        xml.append("</account>");
        xml.append("<oldPasswd>");
        xml.append(DecodeUtil.sha256Encode(oldPass));
        xml.append("</oldPasswd>");
        xml.append("<newPasswd>");
        xml.append(DecodeUtil.sha256Encode(newPass));
        xml.append("</newPasswd>");
        xml.append("</root>");
        
        return xml.toString();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * 用户业务管理接口。(用于对用户短信免打扰策略进行增、删、改等功能及设置IM离线处理策略。) [功能详细描述]
     * 
     * @param strSCCUserName
     *            用户的系统唯一标示。
     * @param xmlServiceInfo
     *            XML子消息体。
     * @return String 请求消息体。
     */
    private String getUndisturbAndIMParamsStr(String strSCCUserName,
            String xmlServiceInfo)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<root>");
        xml.append("<strSCCUserName>");
        xml.append(strSCCUserName);
        xml.append("</strSCCUserName>");
        xml.append("<xmlServiceInfo><![CDATA[");
        xml.append(xmlServiceInfo);
        xml.append("]]></xmlServiceInfo>");
        xml.append("</root>");
        
        return xml.toString();
        
    }
    
    /**
     * 免打扰策略修改过滤规则所需参数。
     * 
     * id 增加成功后返回这个id，后续的修改、删除、激活、去激活操作都需要携带这个ID（写死）。
     * 
     * effectiveMode 过滤规则的生效周期（写死）。
     * 
     * @param timesLots
     *            表示在指定的生效周期下生效的时间段。
     * @param status
     *            规则状态。
     * @return String 返回免打扰策略修改过滤规则所需参数。
     */
    private String getUndisturbModifyRulesParameterStr(String timesLots,
            String status)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<services>");
        xml.append("<serviceItem>");
        xml.append("<svcId>100006</svcId>");
        xml.append("<action>modify</action>");
        xml.append("<property name=\"filter_rule\" id=\"12345\">");
        xml.append("<effectivemode>1</effectivemode>");
        xml.append("<timeslots>");
        xml.append(timesLots);
        xml.append("</timeslots>");
        xml.append("<status>");
        xml.append(status);
        xml.append("</status>");
        xml.append("<filteraction>2</filteraction>");
        xml.append("</property>");
        xml.append("</serviceItem>");
        xml.append("</services>");
        
        return xml.toString();
    }
    
    /**
     * 免打扰策略增加过滤规则所需参数。
     * 
     * @param timesLots
     *            表示在指定的生效周期下生效的时间段。
     * @param status
     *            规则的状态。
     * @return String 返回免打扰策略增加过滤规则所需参数。
     */
    private String getUndisturbAddRulesParameterStr(String timesLots,
            String status)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<services>");
        xml.append("<serviceItem>");
        xml.append("<svcId>100006</svcId>");
        xml.append("<action>add</action>");
        xml.append("<property name=\"filter_rule\" id=\"12345\">");
        xml.append("<rule_type>rejectall</rule_type>");
        xml.append("<effectivemode>1</effectivemode>");
        xml.append("<timeslots>");
        xml.append(timesLots);
        xml.append("</timeslots>");
        xml.append("<status>");
        xml.append(status);
        xml.append("</status>");
        xml.append("<filteraction>2</filteraction>");
        xml.append("</property>");
        xml.append("</serviceItem>");
        xml.append("</services>");
        
        return xml.toString();
    }
    
    /**
     * 查询免打扰策略过滤规则所需参数。
     * 
     * @return String 返回查询免打扰策略过滤规则所需参数。
     */
    private String getQueryUndisturbRulesParamStr()
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<services>");
        xml.append("<serviceItem>");
        xml.append("<svcId>100006</svcId>");
        xml.append("<action>query</action>");
        xml.append("<property name=\"filter_rule\">");
        xml.append("</property>");
        xml.append("</serviceItem>");
        xml.append("</services>");
        
        return xml.toString();
    }
    
    /**
     * 生成重置密码所需参数。
     * 
     * @param userInfo
     *            重置密码用户信息（重置方式：手机或者邮箱）。
     * @param userType
     *            用户信息类型 0为即时通信用户业务ID；2为绑定邮箱；10为绑定的手机号码。
     * @param verificationCode
     *            验证码（手机重置密码所需参数）。
     * @param newPassword
     *            新密码（手机重置密码所需参数）。
     * @return String 字符串格式的请求参数。
     */
    private String getResetParamaterStr(String userInfo, String userType,
            String verificationCode, String newPassword)
    {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<user>");
        xml.append(userInfo);
        xml.append("</user>");
        xml.append("<userType>");
        xml.append(userType);
        xml.append("</userType>");
        if (!StringUtil.isNullOrEmpty(verificationCode))
        {
            xml.append("<verificationCode >");
            xml.append(verificationCode);
            xml.append("</verificationCode>");
        }
        if (!StringUtil.isNullOrEmpty(newPassword))
        {
            xml.append("<newPasswd>");
            xml.append(DecodeUtil.sha256Encode(newPassword));
            xml.append("</newPasswd>");
        }
        xml.append("</root>");
        
        return xml.toString();
    }
    
}
