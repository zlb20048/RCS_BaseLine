/*
 * 文件名: ds.java 
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved. 
 * 描 述:[该类的简要描述] 
 * 创建人: 王媛媛 
 * 创建时间:2012-2-15 
 * 修改人：
 * 修改时间: 
 * 修改内容: [修改内容]
 */
package com.huawei.basic.android.im.logic.login;

/**
 * 注册 模块接口
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public interface IRegisterLogic
{
    
    /**
     * 
     * 
     * 检测手机号是否绑定
     * 
     * @param phoneNumber 手机号
     */
    void checkMobileBind(String phoneNumber);
    
    /**
     * 
     * 检测邮箱是否绑定
     * 
     * @param email 邮箱
     */
    void checkEmailBind(String email);
    
    /**
     * 检测验证码是否正确
     * 
     * @param verifyCode 验证码
     * @param bindInfo  手机号或邮箱地址
     */
    void checkVerifyCode(String verifyCode, String bindInfo);
    
    /**
     * 
     *  注册完成
     * 
     * @param nickName 昵称
     * @param password 密码
     * @param bindInfo 手机号或邮箱地址
     * @param cred 检测验证码后返回的凭证
     */
    void registeAccount(String nickName, String password, String bindInfo,
            String cred);
    
    /**
     * 
     * 获取验证码
     * 
     * @param bindInfo 手机号或邮箱地址
     */
    void getVerifyCode(String bindInfo);
}
