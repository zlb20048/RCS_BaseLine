/*
 * 文件名: ILoginLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.login;

import java.util.List;
import android.content.Context;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.logic.model.CountryItemModel;

/**
 * 登录模块逻辑处理接口定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-6]
 */
public interface ILoginLogic
{
    
    /**
     * 
     * 获取上次登录的用户model<BR>
     * 
     * @param context
     *            Context
     * @return 返回用户model
     */
    AccountModel getLastLoginAccountModel(Context context);
    
    /**
     * 
     * 登录<BR>
     * （不包含验证码）
     * 
     * @param userAccount
     *            用户账号
     * @param passwd
     *            密码
     */
    void login(String userAccount, String passwd);
    
    /**
     * 登录 <BR>
     * （包含验证码）
     * 
     * @param userAccount
     *            用户账号
     * @param passwd
     *            密码
     * @param verifyCode
     *            验证码
     */
    void login(String userAccount, String passwd, String verifyCode);
    
    /**
     * 
     * 重新登录
     * 
     * @param delay
     *            重练延时
     */
    void reLogin(long delay);
    
    /**
     * 登出、注销 <BR>
     * 
     */
    void logout();
    
    /**
     * 
     * 获取验证码
     * 
     * @param account
     *            账号
     */
    void getVerifyCode(String account);
    
    /**
     * 
     * 设置登录模式，是否自动登录<BR>
     * 
     * @param autoLogin
     *            自动登录
     */
    void setLoginMode(boolean autoLogin);
    
    /**
     * 
     * 载入国家文件，解析XML
     * 
     * @param context
     *            上下文
     * @return 国家码列表
     */
    List<CountryItemModel> getCountryCode(Context context);
    
    /**
     * 请求登录信息 <BR>
     * 
     * 
     */
    void requestLoginMessage();
    
    /**
     * 校验验证码<BR>
     * 
     * @param userAccount
     *            账号
     * @param verifyCode
     *            验证码
     */
    
    void sendVerifyCodeImage(String userAccount, String verifyCode);
    
    /**
     * 
     * 保存用户信息
     * 
     * @param context
     *            Context
     * 
     * @param userSysID
     *            系统id
     * @param userID
     *            用户id
     * @param account
     *            账号
     * @param passwd
     *            密码
     * 
     * @return boolean 是否首次登录
     */
    boolean saveAccount(Context context, String userSysID, String userID,
            String account, String passwd);
    
    /**
     * 
     * 登录成功
     * 
     * @param isFirst
     *            是否首次登录 登录系统成功以后执行的代码
     */
    void afterLoginSuccessed(boolean isFirst);
}
