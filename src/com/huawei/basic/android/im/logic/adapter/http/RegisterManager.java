/*
 * 文件名: RegisterManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-8] 
 */
public class RegisterManager extends HttpManager
{
    /**
     * 昵称
     */
    private static final String KEY_NICKNAME = "nickname";
    /**
     * 密码
     */
    private static final String KEY_PASSWORD = "password";
    /**
     * 绑定信息
     */
    private static final String KEY_BINDINFO = "bindInfo";
    /**
     * 验证码凭证
     */
    private static final String KEY_CRED = "cred";
    /**
     * 验证码
     */
    private static final String KEY_VERIFY_CODE = "verifyCode";
    /**
     * 操作类型   0：绑定   
     */
    private static final String KEY_OPER_TYPE = "operType";
    
    /**
     * 注册新用户请求
     */
    private static final int ACTION_REGISTER_ACCOUT = 0;
    
    /**
     * 检测手机号是否绑定请求
     */
    private static final int ACTION_CHECK_BIND_MSISDN = 1;
    
    /**
     * 检测邮箱是否绑定请求
     */
    private static final int ACTION_CHECK_BIND_EMAIL = 2;
    
    /**
     * 获取手机验证码请求
     */
    private static final int ACTION_GET_MSISDN_VERIFY_CODE = 5;
    
    /**
     * 获取邮箱验证码请求
     */
    private static final int ACTION_GET_EMAIL_VERIFY_CODE = 6;
    
    /**
     * 检测手机验证码请求
     */
    private static final int ACTION_CHECK_MSISDN_VERIFY_CODE = 7;
    
    /**
     * 检测邮箱验证码请求
     */
    private static final int ACTION_CHECK_EMAIL_VERIFY_CODE = 8;
    
    /**
     * 注册新用户请求的URL
     */
    private static final String USER_REGISTER_SERVLET = "userRegisterServlet";
    
    /**
     * 检测手机号是否绑定请求的URL
     */
    private static final String CHECK_BIND_MSISDN_SERVLET = "checkBindMSISDNServlet";
    
    /**
     * 检测邮箱是否绑定请求的URL
     */
    private static final String CHECK_BIND_EMAIL_SERVLET = "checkBindEmailServlet";
    
    /**
     * 获取手机验证码请求的URL
     */
    private static final String GET_MSISDN_VERIFY_CODE_SERVLET = "getMSISDNVerifyCodeServlet";
    
    /**
     * 获取邮箱验证码请求的URL
     */
    private static final String GET_EMAIL_VERIFY_CODE_SERVLET = "getEmailVerifyCodeServlet";
    
    /**
     * 检测手机验证码请求的URL
     */
    private static final String CHECK_MSISDN_VERIFY_CODE_SERVLET = "checkMSISDNVerifyCodeServlet";
    
    /**
     * 检测邮箱验证码请求的URL
     */
    private static final String CHECK_EMAIL_VERIFY_CODE_SERVLET = "checkEmailVerifyCodeServlet";
    
    /**
     * 注册完成后 logic 层调用函数 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param nickName  昵称
     * @param password 密码
     * @param bindInfo 手机号或是邮箱号
     * @param cred 验证码返回凭证
     * @param httpListener IHttpListener
     */
    public void registeAccount(String nickName, String password,
            String bindInfo, String cred, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(KEY_NICKNAME, nickName);
        sendData.put(KEY_PASSWORD, password);
        sendData.put(KEY_BINDINFO, bindInfo);
        sendData.put(KEY_CRED, cred);
        send(ACTION_REGISTER_ACCOUT, sendData, httpListener);
    }
    
    /**
     * logic 层检测手机是否绑定的请求 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param phoneNumber 手机号
     * @param httpListener  httpListener
     */
    public void checkMobileBind(String phoneNumber, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(KEY_BINDINFO, phoneNumber);
        send(ACTION_CHECK_BIND_MSISDN, sendData, httpListener);
    }
    
    /**
     * logic 层检测邮箱是否绑定的方法 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param email
     *            邮箱
     * @param httpListener
     *            httpListener
     */
    public void checkEmailBind(String email, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(KEY_BINDINFO, email);
        send(ACTION_CHECK_BIND_EMAIL, sendData, httpListener);
    }
    
    /**
     * logic层 获取验证码请求
     * 
     * @param bindInfo
     *            手机号或邮箱
     * @param httpListener
     *            httpListener
     */
    public void getVerifyCode(String bindInfo, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(KEY_BINDINFO, bindInfo);
        sendData.put(KEY_OPER_TYPE, "0");
        if (StringUtil.isMobile(bindInfo))
        {
            send(ACTION_GET_MSISDN_VERIFY_CODE, sendData, httpListener);
        }
        else
        {
            send(ACTION_GET_EMAIL_VERIFY_CODE, sendData, httpListener);
        }
    }
    
    /**
     * Login层检测验证码是否 正确调用的方法
     * 
     * @param verifyCode 验证码
     * @param bindInfo  手机号或是邮箱
     * @param httpListener IHttpListener
     */
    public void checkVerifyCode(String verifyCode, String bindInfo,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(KEY_BINDINFO, bindInfo);
        sendData.put(KEY_VERIFY_CODE, verifyCode);
        if (StringUtil.isMobile(bindInfo))
        {
            send(ACTION_CHECK_MSISDN_VERIFY_CODE, sendData, httpListener);
        }
        else
        {
            send(ACTION_CHECK_EMAIL_VERIFY_CODE, sendData, httpListener);
        }
    }
    
    /**
     * 获取请求对象request的URL地址
     * @param action 区分不同的请求
     * @param sendData 用户发送请求时的数据
     * @return 请求的接口地址
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getUrl(int, java.util.Map)
     */
    
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        
        String url = FusionConfig.getInstance().getAasResult().getPortalurl();
        switch (action)
        {
            
            //注册新用户请求
            case ACTION_REGISTER_ACCOUT:
                return url + USER_REGISTER_SERVLET;
                
                //检测手机号是否绑定请求
            case ACTION_CHECK_BIND_MSISDN:
                return url + CHECK_BIND_MSISDN_SERVLET;
                
                //检测邮箱是否绑定请求
            case ACTION_CHECK_BIND_EMAIL:
                return url + CHECK_BIND_EMAIL_SERVLET;
                
                //获取手机验证码请求
            case ACTION_GET_MSISDN_VERIFY_CODE:
                return url + GET_MSISDN_VERIFY_CODE_SERVLET;
                
                //获取邮箱验证码请求
            case ACTION_GET_EMAIL_VERIFY_CODE:
                return url + GET_EMAIL_VERIFY_CODE_SERVLET;
                
                // 检测手机验证码请求
            case ACTION_CHECK_MSISDN_VERIFY_CODE:
                return url + CHECK_MSISDN_VERIFY_CODE_SERVLET;
                
                //检测邮箱验证码请求
            case ACTION_CHECK_EMAIL_VERIFY_CODE:
                return url + CHECK_EMAIL_VERIFY_CODE_SERVLET;
                
            default:
                break;
        }
        return null;
        
    }
    
    /**
     * 
     * 请求消息体数据类型
     * @param action 请求标识，不同请求定义不同的标识位
     * @return  XML格式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getContentType(int)
     */
    @Override
    protected ContentType getContentType(int action)
    {
        // TODO Auto-generated method stub
        return ContentType.XML;
    }
    
    /**
     * 获取请求对象request的请求体
     * @param action 区分不同的请求
     * @param sendData 用户发送请求时的数据
     * @return 请求体
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getBody(int, java.util.Map)
     */
    
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        String data = null;
        switch (action)
        {
            
            //注册新用户请求
            case ACTION_REGISTER_ACCOUT:
                String nickName = (String) sendData.get(KEY_NICKNAME);
                String password = (String) sendData.get(KEY_PASSWORD);
                String bindInfo = (String) sendData.get(KEY_BINDINFO);
                String cred = (String) sendData.get(KEY_CRED);
                data = getRegisterAccountBody(nickName,
                        password,
                        bindInfo,
                        cred);
                break;
            
            //检测手机号是否绑定请求
            case ACTION_CHECK_BIND_MSISDN:
                String phoneNumber = (String) sendData.get(KEY_BINDINFO);
                data = getCheckMobileBind(phoneNumber);
                break;
            
            //检测邮箱是否绑定请求
            case ACTION_CHECK_BIND_EMAIL:

                String email = (String) sendData.get(KEY_BINDINFO);
                data = getCheckEmailBind(email);
                Log.d("RegisterManager", data + email);
                break;
            
            //获取手机验证码请求
            case ACTION_GET_MSISDN_VERIFY_CODE:
                phoneNumber = (String) sendData.get(KEY_BINDINFO);
                String operType = (String) sendData.get(KEY_OPER_TYPE);
                data = getMSISDNVerifyCodeParameterStr(phoneNumber, operType);
                break;
            
            //获取邮箱验证码请求
            case ACTION_GET_EMAIL_VERIFY_CODE:
                email = (String) sendData.get(KEY_BINDINFO);
                operType = (String) sendData.get(KEY_OPER_TYPE);
                data = getEmailVerifyCodeParameterStr(email, operType);
                break;
            
            // 检测手机验证码请求
            case ACTION_CHECK_MSISDN_VERIFY_CODE:
                phoneNumber = (String) sendData.get(KEY_BINDINFO);
                String verifyCode = (String) sendData.get(KEY_VERIFY_CODE);
                data = getCheckMSISDNVerifyCode(phoneNumber, verifyCode);
                
                break;
            
            //检测邮箱验证码请求
            case ACTION_CHECK_EMAIL_VERIFY_CODE:
                email = (String) sendData.get(KEY_BINDINFO);
                verifyCode = (String) sendData.get(KEY_VERIFY_CODE);
                data = getCheckEmailVerifyCode(email, verifyCode);
                break;
            default:
                break;
        }
        return data.toString();
    }
    
    /**
     * 解析服务器返回的数据，封装响应对象response
     * @param action 区分不同的请求
     * @param sendData 用户发送请求时的数据
     * @param response 封装的响应对象
     * @return 封装的响应对象
     * @see com.huawei.basic.android.im.component.
     * net.http.HttpManager#handleData(int, java.util.Map, 
     * com.huawei.basic.android.im.component.net.http.Response)
     */
    
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        switch (action)
        {
            
            //注册新用户请求
            case ACTION_REGISTER_ACCOUT:
                return StringUtil.getXmlValue(response.getData(), "<account>");
                
                //检测手机号、邮箱是否绑定请求
            case ACTION_CHECK_BIND_MSISDN:
            case ACTION_CHECK_BIND_EMAIL:
                return null;
                
                //获取验证码请求
            case ACTION_GET_MSISDN_VERIFY_CODE:
            case ACTION_GET_EMAIL_VERIFY_CODE:
                //演示版本从服务器返回中直接获取验证码  added by zhanggj 20120509
                return StringUtil.getXmlValue(response.getData(), "<checkcode>");
                
                // 检测手机 号、邮箱验证码请求
            case ACTION_CHECK_MSISDN_VERIFY_CODE:
                return StringUtil.getXmlValue(response.getData(), "<cred>");
            case ACTION_CHECK_EMAIL_VERIFY_CODE:
                return StringUtil.getXmlValue(response.getData(), "<eCred>");
                
            default:
                return null;
        }
    }
    
    /**
     * 生成注册请求对象request的请求体
     * @param nickName
     * @param password
     * @param bindInfo
     * @param cred
     * @return
     */
    private String getRegisterAccountBody(String nickName, String password,
            String bindInfo, String cred)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root><passwd>");
        xml.append(DecodeUtil.sha256Encode(password));
        xml.append("</passwd>");
        xml.append("<displayName>");
        xml.append(nickName);
        xml.append("</displayName>");
        if (StringUtil.isMobile(bindInfo))
        {
            xml.append("<MSISDN>");
            xml.append(bindInfo);
            xml.append("</MSISDN>");
            if (!StringUtil.isNullOrEmpty(cred))
            {
                xml.append("<cred>");
                xml.append(cred);
                xml.append("</cred>");
            }
        }
        else
        {
            xml.append("<email>");
            xml.append(bindInfo);
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
     * 生成获取手机验证码所需参数。
     * 
     * @param phoneNumber
     *            手机号码。
     * @param operType
     *            获取验证码    0：绑定号码验证   1：解绑号码验证   2：重置密码验证。
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
     * 生成获取邮箱验证码所需参数。
     * 
     * @param email
     *            email
     * @param operType 获取邮箱验证码用途
     *         0：绑定号码验证 1：解绑号码验证 2：重置密码验证。
     * @return 获取邮箱验证码所需参数
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
     * 检测邮箱验证码是否正确 
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
    
}
