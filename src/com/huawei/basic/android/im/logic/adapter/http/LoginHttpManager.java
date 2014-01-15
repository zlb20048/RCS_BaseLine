/*
 * 文件名: LoginHttpManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 登录HTTP请求管理类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public class LoginHttpManager extends HttpManager
{
    
    /**
     * 登陆action
     */
    private static final int LOGIN = 0x00000001;
    
    /**
     * 登出action
     */
    private static final int LOGOUT = 0x00000002;
    
    /**
     * 获取验证码图形
     */
    private static final int VERIFY_CODE_IMAGE = 0x00000003;
    
    /**
     * 获取验证码图形
     */
    private static final int TOKEN_REFRESH = 0x00000004;
    
    /**
     * 校验验证码图形
     */
    private static final int SEND_VERIFY_CODE_IMAGE = 0x00000005;
    
    /**
     * 发送请求时需传递的参数key-用户名
     */
    private static final String KEY_USERNAME = "username";
    
    /**
     * 发送请求时需传递的参数key-密码
     */
    private static final String KEY_PASSWORD = "password";
    
    /**
     * 发送请求时需传递的参数key-验证码
     */
    private static final String KEY_VERIFY_CODE = "verify_code";
    
    /**
     * 发送请求时需传递的参数key-客户端版本号
     */
    private static final String KEY_CLIENT_VERSION = "client_version";
    
    /**
     * 发送请求时需传递的参数key-用户id
     */
    private static final String KEY_USERID = "userid";
    
    /**
     * 发送请求时需传递的参数key-token
     */
    private static final String KEY_TOKEN = "token";
    
    /**
     * 发送请求时需传递的参数key-登录id
     */
    private static final String KEY_LOGIN_ID = "login_id";
    
    /**
     * debug tag
     */
    private static final String TAG = "LoginHttpManager";
    
    //AAS地址: 南京N5
//    private static final String AAS_URL = "http://221.226.48.130:2136/tellin/";
    //    private static final String AAS_URL = "http://221.226.48.130:2136/tellin/";
//    private static final String AAS_URL = "http://221.226.48.130:2136/tellin/";
    
    //深圳
    //     private static final String AAS_URL = "http://119.145.9.215:5220/tellin/";
    
    //北京
    //    private static final String AAS_URL = "http://123.125.97.217:5020/tellin/";
    
    // 香港Hosting环境
        private static final String AAS_URL = "http://202.55.9.41:5220/tellin/";
    
    /**
     * 
     * 登录<BR>
     * 
     * @param userAccount
     *            用户账号，可以是用户系统ID，手机号，邮箱
     * @param passwd
     *            密码
     * @param verifyCode
     *            验证码 （可选项）
     * @param clientVersion
     *            版本号
     * @param httpListener
     *            IHttpListener
     */
    public void login(String userAccount, String passwd, String verifyCode,
            String clientVersion, final IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(LoginHttpManager.KEY_USERNAME, userAccount);
        sendData.put(LoginHttpManager.KEY_PASSWORD, passwd);
        sendData.put(LoginHttpManager.KEY_VERIFY_CODE, verifyCode);
        sendData.put(LoginHttpManager.KEY_CLIENT_VERSION, clientVersion);
        send(LoginHttpManager.LOGIN, sendData, httpListener);
    }
    
    /**
     * 
     * 手动注销AAS<BR>
     * 
     * @param userID
     *            用户ID
     * @param userSysID
     *            用户系统ID
     * @param token
     *            TOKEN
     * @param loginID
     *            上次登录时AAS返回的"loginid"字段
     * @param httpListener
     *            IHttpListener
     */
    public void logout(String userID, String userSysID, String token,
            String loginID, final IHttpListener httpListener)
    {
        final HashMap<String, Object> mSendData = new HashMap<String, Object>();
        mSendData.put(LoginHttpManager.KEY_USERNAME, userID);
        mSendData.put(LoginHttpManager.KEY_USERID, userSysID);
        mSendData.put(LoginHttpManager.KEY_TOKEN, token);
        mSendData.put(LoginHttpManager.KEY_LOGIN_ID, loginID);
        send(LoginHttpManager.LOGOUT, mSendData, httpListener);
    }
    
    /**
     * 
     * 刷新token<BR>
     * 
     * @param userSysID
     *            用户系统ID
     * @param token
     *            之前的token值
     * @param httpListener
     *            IHttpListener
     */
    public void refreshToken(String userSysID, String token,
            final IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(LoginHttpManager.KEY_USERID, userSysID);
        sendData.put(LoginHttpManager.KEY_TOKEN, token);
        send(LoginHttpManager.TOKEN_REFRESH, sendData, httpListener);
    }
    
    /**
     * 获取验证码 <BR>
     * 
     * @param userAccount
     *            用户账号
     * @param httpListener
     *            IHttpListener
     */
    public void getVerifyCode(String userAccount,
            final IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(LoginHttpManager.KEY_USERNAME, userAccount);
        send(LoginHttpManager.VERIFY_CODE_IMAGE, sendData, httpListener);
    }
    
    /**
     * 校验验证码 <BR>
     * 
     * @param userAccount
     *            用户账号
     * @param verifyCode
     *            验证码
     * @param httpListener
     *            IHttpListener
     */
    public void sendVerifyCode(String userAccount, String verifyCode,
            final IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(LoginHttpManager.KEY_USERNAME, userAccount);
        sendData.put(LoginHttpManager.KEY_VERIFY_CODE, verifyCode);
        send(LoginHttpManager.SEND_VERIFY_CODE_IMAGE, sendData, httpListener);
    }
    
    /**
     * <BR>
     * 
     * @param action
     *            action
     * @param sendData
     *            sendData
     * @return request url
     * @see com.huawei.basic.android.im.component.net.http.RequestAdapter#getUrl(int,
     *      java.util.Map)
     */
    
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        String basicUrl = AAS_URL;
        switch (action)
        {
            case LOGIN:
                basicUrl += "login.do";
                break;
            case LOGOUT:
                basicUrl += "logout.do";
                break;
            case VERIFY_CODE_IMAGE:
                basicUrl += "verfycode.do";
                break;
            case SEND_VERIFY_CODE_IMAGE:
                basicUrl += "verfycodeauth.do";
                break;
            case TOKEN_REFRESH:
                basicUrl += "authTokenRefresh.do";
                break;
            default:
                Logger.e(TAG, "[getUrl()] request's action is invalid!");
                break;
        }
        return basicUrl;
    }
    
    /**
     * <BR>
     * 
     * @param action
     *            int
     * @param sendData
     *            sendData
     * @return 请求体
     * @see com.huawei.basic.android.im.component.net.http.RequestAdapter#getBody(int,
     *      java.util.Map)
     */
    
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        String requestBody;
        if (sendData == null)
        {
            Logger.i(TAG, "[getBody()] input parameter(sendData) is null");
            return null;
        }
        switch (action)
        {
            case LOGIN:
                requestBody = getAasLoginRequestParam(sendData);
                break;
            case LOGOUT:
                requestBody = getAasLogoutRequestParam(sendData);
                break;
            case VERIFY_CODE_IMAGE:
                requestBody = getAasverifyCodeImageRequestParam(sendData);
                break;
            case SEND_VERIFY_CODE_IMAGE:
                requestBody = sendVerifyCodeImage(sendData);
                break;
            case TOKEN_REFRESH:
                requestBody = getAuthTokenRefreshRequestParam(sendData);
                break;
            default:
                requestBody = null;
                Logger.e(TAG, "[getBody()] request's action is invalid!");
                break;
        }
        return requestBody;
    }
    
    /**
     * 获取请求属性
     * 
     * @param action
     *            事件
     * @return 解析列表
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestProperties(int)
     */
    
    @Override
    protected List<NameValuePair> getRequestProperties(int action)
    {
        return null;
    }
    
    /**
     * <BR>
     * 
     * @param action
     *            int
     * @param sendData
     *            sendData
     * @param response
     *            Response
     * @return Object
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#handleData(int,
     *      java.util.Map, java.lang.String)
     */
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        switch (action)
        {
            case LoginHttpManager.LOGIN:

                return parseLoginResult(sendData, response.getData());
            case LoginHttpManager.TOKEN_REFRESH:
                if (response.getData().contains("<token>"))
                {
                    return response.getData().substring(response.getData()
                            .indexOf("<token>") + 7,
                            response.getData().indexOf("</token>"));
                }
                else if (response.getData().contains("return"))
                {
                    return response.getData().substring(response.getData()
                            .indexOf("<return>") + 8,
                            response.getData().indexOf("</return>"));
                }
                else
                {
                    return null;
                }
            default:
                return null;
        }
        
    }
    
    /**
     * <BR>
     * 
     * @param action
     *            action
     * @return ContentType
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getContentType(int)
     */
    @Override
    protected ContentType getContentType(int action)
    {
        return ContentType.XML;
    }
    
    /**
     * HTTP请求方式
     * 
     * @param action
     *            action
     * @return HTTP请求方式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        return RequestMethod.POST;
    }
    
    /**
     * 
     * HTTP请求数据
     * 
     * @param action
     *            请求事件
     * @return 是否需要返回数组
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#isNeedByte(int)
     */
    @Override
    protected boolean isNeedByte(int action)
    {
        if (action == VERIFY_CODE_IMAGE)
        {
            return true;
        }
        return false;
    }
    
    /**
     * [AAS登陆参数]<BR>
     * 
     * @return 登陆参数
     */
    private String getAasLoginRequestParam(Map<String, Object> sendData)
    {
        // username 登陆账户 password 登陆密码 verifyCode 验证码
        String username = (String) sendData.get(KEY_USERNAME);
        
        // 用户账号类型.
        String userAccountType;
        if (StringUtil.isEmail(username))
        {
            userAccountType = "11";
        }
        else if (StringUtil.isMobile(username))
        {
            userAccountType = "10";
            // 如果是手机号，则加上+86进行登录
            username = "+86" + StringUtil.fixPortalPhoneNumber(username);
        }
        else
        {
            userAccountType = "0";
        }
        
        String password = (String) sendData.get(KEY_PASSWORD);
        String verifyCode = (String) sendData.get(KEY_VERIFY_CODE);
        String clientVersion = (String) sendData.get(KEY_CLIENT_VERSION);
        String random = StringUtil.createRandomString(3, 7);
        // random = "ilt";
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<root>")
                .append("<user>")
                .append(username)
                .append("</user>")
                .append("<random>")
                .append(random)
                .append("</random>")
                .append("<secinfo>")
                .append(DecodeUtil.sha256Encode(username
                        + DecodeUtil.sha256Encode(password) + random))
                .append("</secinfo>")
                .append("<version>")
                .append(clientVersion)
                .append("</version>")
                .append("<clienttype>2</clienttype>")
                .append("<pintype>0</pintype>")
                .append("<usertype>")
                .append(userAccountType)
                .append("</usertype>")
                .append("<requestip>")
                .append(getLocalIpAddress())
                .append("</requestip>")
                .append("<devtype>22xxx</devtype>");
        if (verifyCode != null)
        {
            xml.append("<vcStr><![CDATA[")
                    .append(verifyCode)
                    .append("]]></vcStr>");
        }
        xml.append("</root>");
        return xml.toString();
    }
    
    /**
     * [AAS登出参数]<BR>
     * 
     * @return 登陆参数
     */
    private String getAasLogoutRequestParam(Map<String, Object> sendData)
    {
        StringBuffer stringBuffer = new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        stringBuffer.append("<root>");
        stringBuffer.append("<msisdn>")
                .append((String) sendData.get(KEY_USERNAME))
                .append("</msisdn>");
        stringBuffer.append("<userid>")
                .append((String) sendData.get(KEY_USERID))
                .append("</userid>");
        stringBuffer.append("<token>")
                .append((String) sendData.get(KEY_TOKEN))
                .append("</token>");
        stringBuffer.append("<loginid>")
                .append((String) sendData.get(KEY_LOGIN_ID))
                .append("</loginid>");
        stringBuffer.append("</root>");
        return stringBuffer.toString();
    }
    
    /**
     * [AAS登陆参数]<BR>
     * 
     * @return 登出参数
     */
    private String getAasverifyCodeImageRequestParam(
            Map<String, Object> sendData)
    {
        return new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><account>").append((String) sendData.get(KEY_USERNAME))
                .append("</account></root>")
                .toString();
    }
    
    /**
     * 校验验证码<BR>
     * 
     * @return 登出参数
     */
    private String sendVerifyCodeImage(Map<String, Object> sendData)
    {
        return new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><account>").append((String) sendData.get(KEY_USERNAME))
                .append("<vcStr><![CDATA[")
                .append((String) sendData.get(KEY_VERIFY_CODE))
                .append("]]></vcStr>")
                .append("</account></root>")
                .toString();
    }
    
    /**
     * [AAS登陆参数]<BR>
     * 
     * @return 登出参数
     */
    private String getAuthTokenRefreshRequestParam(Map<String, Object> sendData)
    {
        StringBuffer stringBuffer = new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<root>")
                .append("<token>")
                .append((String) sendData.get(KEY_TOKEN))
                .append("</token>")
                .append("<userid>")
                .append((String) sendData.get(KEY_USERID))
                .append("</userid>")
                .append("</root>");
        return stringBuffer.toString();
    }
    
    /**
     * 解析登录请求的服务器返回数据 <BR>
     * 
     * @param data
     *            服务器返回的字符串
     * @return AASResult
     */
    private AASResult parseLoginResult(Map<String, Object> sendData, String data)
    {
        AASResult aasResult = new AASResult();
        // int messageType = MessageType.DEF_MSGTYPE_FAIL;
        // 结果预判断
        AASHandler aasParser = new AASHandler(); // TODO:将AasParser类中的AASResult属性去掉
        if (!data.contains("return"))
        {
            // 解密返回的字符串
            data = aasParser.doDecode(data, (String) sendData.get(KEY_PASSWORD));
            
        }
        aasResult.setResult(data);
        aasParser.doParse(aasResult, data);
        return aasResult;
    }
    
    /**
     * 获取本地IP地址
     * 
     * @return 本地IP地址
     */
    private static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            Logger.e(TAG, "SocketException");
        }
        return null;
    }
}
