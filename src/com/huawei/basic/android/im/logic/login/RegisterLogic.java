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
package com.huawei.basic.android.im.logic.login;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.huawei.basic.android.im.common.FusionMessageType.RegisterMessageType;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.SMSDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.RegisterManager;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 注册模块接口实现类
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class RegisterLogic extends BaseLogic implements IRegisterLogic
{
    /**
     * TAG
     */
    private static final String TAG = "RegisterLogic";
    
    /**
     *短信数据操作适配器
     *  
     */
    private SMSDbAdapter mSMSDbAdapter;
    
    /**
     * 公有构造函数 [构造简要说明]
     * 
     */
    public RegisterLogic()
    {
        
    }
    /**
     * 
     * 构造方法
     * @param context 上下文
     */
    public RegisterLogic(Context context)
    {
        super();
        mSMSDbAdapter = SMSDbAdapter.getInstance(context);
    }
    
    /**
     * 注册完成
     * 
     * @param nickName 昵称
     * @param password 密码
     * @param bindInfo 手机号或邮箱号
     * @param cred 短信验证码校验成功后服务器返回的凭据
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#registeAccount(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public void registeAccount(String nickName, String password,
            String bindInfo, String cred)
    {
        //对URI解除注册鉴定数据库表
        super.unRegisterObserver(Uri.parse("content://sms/"));
        if (StringUtil.isMobile(bindInfo))
        {
            bindInfo = "+86" + StringUtil.fixPortalPhoneNumber(bindInfo);
            
        }
        new RegisterManager().registeAccount(nickName,
                password,
                bindInfo,
                cred,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        
                        // 联网成功
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            
                            // A.如果retCode为0，表明注册成功
                            if (response.getResultCode() == 0)
                            {
                                String userID = (String) response.getObj();
                                sendMessage(RegisterMessageType.REGISTE_ACCOUNT_SUCCESS,
                                        userID);
                            }
                            
                            // B.如果retCode不为0，表明业务上有错误
                            else
                            {
                                sendMessage(RegisterMessageType.REGISTER_ACCOUNT_FAILED,
                                        response.getResultCode());
                            }
                        }
                        
                        // 联网失败时
                        else
                        {
                            sendMessage(RegisterMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        // TODO Auto-generated method stub
                    }
                });
        
    }
    
    /**
     * 检测手机号是否绑定
     * 
     * @param phoneNumber 电话号
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#checkMobileBind(java.lang.String)
     */
    @Override
    public void checkMobileBind(String phoneNumber)
    {
        final String phoneNum = "+86"
                + StringUtil.fixPortalPhoneNumber(phoneNumber);
        
        new RegisterManager().checkMobileBind(phoneNum, new IHttpListener()
        {
            @Override
            public void onResult(int action, Response response)
            {
                
                // 联网成功
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    
                    // A.如果retCode为0，表明手机号没有被绑定,去获取验证码
                    if (response.getResultCode() == 0)
                    {
                        
                        sendMessage(RegisterMessageType.CHECK_MOBILE_BIND_SUCCESS,
                                null);
                        getVerifyCode(phoneNum);
                    }
                    
                    // B.如果retCode不为0，表明业务上有错误
                    else
                    {
                        sendMessage(RegisterMessageType.CHECK_BIND_FAILED,
                                response.getResultCode());
                    }
                }
                
                // 联网失败时
                else
                {
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                // TODO Auto-generated method stub
            }
        });
    }
    
    /**
     * 检测邮箱是否绑定
     * 
     * @param email 邮箱
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#checkEmailBind(java.lang.String)
     */
    @Override
    public void checkEmailBind(final String email)
    {
        new RegisterManager().checkEmailBind(email, new IHttpListener()
        {
            @Override
            public void onResult(int action, Response response)
            {
                
                //联网成功
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    
                    //如果retCode為0，表明邮箱未绑定,去获取验证码
                    if (response.getResultCode() == 0)
                    {
                        
                        sendEmptyMessage(RegisterMessageType.CHECK_EMAIL_BIND_SUCCESS);
                        getVerifyCode(email);
                    }
                    
                    //如果retCode不是0，表明邮箱已经被绑定
                    else
                    {
                        sendMessage(RegisterMessageType.CHECK_BIND_FAILED,
                                response.getResultCode());
                    }
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    /**
     * 获取验证码
     * 
     * @param bindInfo 手机号
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#getVerifyCode(java.lang.String)
     */
    
    @Override
    public void getVerifyCode(String bindInfo)
    {
        if (StringUtil.isMobile(bindInfo))
        {
            bindInfo = "+86" + StringUtil.fixPortalPhoneNumber(bindInfo);
            
        }
        
        final String bindInfo2 = bindInfo;
        new RegisterManager().getVerifyCode(bindInfo, new IHttpListener()
        {
            
            @Override
            public void onResult(int action, Response response)
            {
                
                //联网成功
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    
                    //如果retCode為0，表明验证码获取成功
                    if (response.getResultCode() == 0)
                    {
                        //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
                        String verifyCode = (String) response.getObj();
                        if (StringUtil.isMobile(bindInfo2))
                        {
                            //演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
                            //sendEmptyMessage(RegisterMessageType.GET_MSISDN_VERIFY_CODE_SUCCESS);
                            sendMessage(RegisterMessageType.GET_MSISDN_VERIFY_CODE_SUCCESS,
                                    verifyCode);
                        }
                        else
                        {
                            //演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509                            
                            //sendEmptyMessage(RegisterMessageType.GET_EMAIL_VERIFY_CODE_SUCCESS);
                            sendMessage(RegisterMessageType.GET_EMAIL_VERIFY_CODE_SUCCESS,
                                    verifyCode);
                        }
                    }
                    
                    //如果retCode不是0，表明没有获取到验证码
                    else
                    {
                        sendMessage(RegisterMessageType.GET_VERIFY_CODE_FAILED,
                                response.getResultCode());
                    }
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    /**
     * 检测验证码是否正确
     * 
     * @param verifyCode 验证码
     * @param bindInfo 手机号码
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#checkVerifyCode(java.lang.String)
     */
    @Override
    public void checkVerifyCode(String verifyCode, String bindInfo)
    {
        new RegisterManager().checkVerifyCode(verifyCode,
                bindInfo,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        
                        //联网成功
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            
                            //如果retCode為0,表明验证码正确
                            if (response.getResultCode() == 0)
                            {
                                sendMessage(RegisterMessageType.CHECK_VERIFY_CODE_SUCCESS,
                                        response.getObj());
                            }
                            
                            //如果retCode不是0，表明验证码不正确
                            else
                            {
                                sendMessage(RegisterMessageType.CHECK_VERIFY_CODE_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            
                            //连接服务器失败
                            sendMessage(RegisterMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        // TODO Auto-generated method stub
                    }
                });
    }
    
    /**
     * 对 该logic对象定义被监听的Uri
     * @return  短信数据库的uri
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#getObserverUris()
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { Uri.parse("content://sms/") };
    }
    
    /**
     * 传入Uri数组来监听该logic所要监听的数据库对象
     * @param selfChange 是否改变
     * @param uri   logic所要监听的数据库对象的uri
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#onChangeByUri(boolean, android.net.Uri)
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        Log.d(TAG, "registerLogic $inbox onChangeByUri uri: " + uri);
        String phoneNumber = "1252015195878834";
        //获取短信内容
        String smsBody = mSMSDbAdapter.getSMSBody(phoneNumber);
        //获取短信内容中验证码
        String smsVerifyCode = getSMSVerifyCode(smsBody);
       
        //向UI发送消息和获取到的验证码
        if (null != smsVerifyCode)
        {
            sendMessage(RegisterMessageType.GET_MSISDN_VERIFY_CODE_MESSAGE,
                    smsBody);
        }
        
    }

    /**
     * 通过正则表达式获取smsBody中验证码的方法
     * @param smsBody  获取短信内容
     * @return smsBody中验证码
     */
    private String getSMSVerifyCode(String smsBody)
    {
        return null;
    }
}
