package com.huawei.basic.android.im.component.service.app;

import com.huawei.basic.android.im.logic.model.AASResult;
interface IAppEntry
{
	
	void sendXmppMessage(int messageType, String result);
	void sendLoginMessage(int messageType, String result);
	
	void loginSuccessCallback(in AASResult aasResult);
	
   // XMPP回调.identifier由componentID+":"+notifyID组成
    void xmppCallback(String componentID, int notifyID, String data);

}