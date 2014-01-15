package com.huawei.basic.android.im.component.service.core;

import com.huawei.basic.android.im.component.service.app.IAppEntry;
interface IServiceEntry
{
	// 注册回调对象
	void registerCallback(IAppEntry appEntry);

	// 登录接口
	void login(String account, String password, String verifyCode, String clientVersion);
	
	//登出接口
	void logout(String userID, String userSysID, String token, String loginID);
	
	// 刷新TOKEN
	void refreshToken(String userSysID, String token);
	
	// 停止刷新TOKEN
	void stopRefreshToken();

 	// 订阅
    int subNotify(String comId, int cmdId);
    
    //取消订阅
    int unSubNotify(String comId, int cmdId);
    
    //执行命令
    String executeCommand(String comId, int cmdId, String data);
    
    //请求登录信息
    void requestLoginMessage();
        
    //直接登录接口
    void reLogin();
    
    //重新登录接口
    void connectionChangerLogin(long delay);
    
    //无网络接口
    void sendNetMessage(int stauts);
}