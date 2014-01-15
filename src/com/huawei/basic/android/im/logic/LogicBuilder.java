/*
 * 文件名: LogicBuilder.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic;

import android.content.Context;

import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.component.service.app.ServiceSender;
import com.huawei.basic.android.im.framework.logic.BaseLogicBuilder;
import com.huawei.basic.android.im.logic.contact.ContactLogic;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.friend.FriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.FriendLogic;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.group.GroupLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.im.ConversationLogic;
import com.huawei.basic.android.im.logic.im.IConversationLogic;
import com.huawei.basic.android.im.logic.im.IImLogic;
import com.huawei.basic.android.im.logic.im.ImLogic;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.login.IRegisterLogic;
import com.huawei.basic.android.im.logic.login.LoginLogic;
import com.huawei.basic.android.im.logic.login.RegisterLogic;
import com.huawei.basic.android.im.logic.plugin.IPluginLogic;
import com.huawei.basic.android.im.logic.plugin.PluginLogic;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.logic.settings.SettingsLogic;
import com.huawei.basic.android.im.logic.voip.CommunicationLogLogic;
import com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.logic.voip.VoipLogic;

/**[一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012] 
 */
public class LogicBuilder extends BaseLogicBuilder
{
    
    private static BaseLogicBuilder instance;
    
    /**
     * 构造方法，继承BaseLogicBuilder的构造方法，由父类BaseLogicBuilder对所有logic进行初始化。
     * @param context
     *      系统的context对象
     */
    private LogicBuilder(Context context)
    {
        super(context);
    }
    
    /**
     * 
     * 获取BaseLogicBuilder单例<BR>
     * 单例模式
     * @param context 系统的context对象
     * @return BaseLogicBuilder 单例对象
     */
    public static synchronized BaseLogicBuilder getInstance(Context context)
    {
        if (null == instance)
        {
            instance = new LogicBuilder(context);
        }
        return instance;
    }
    
    /**
     * LogicBuidler的初始化方法，系统初始化的时候执行<BR>
     * @param context
     *      系统的context对象
     * @see com.huawei.basic.android.im.framework.logic.BaseLogicBuilder#init(android.content.Context)
     */
    protected void init(Context context)
    {
        registerAllLogics(context);
    }
    
    /**
     * 所有logic对象初始化及注册的方法<BR>
     */
    private void registerAllLogics(Context context)
    {
        IServiceSender sender = ServiceSender.getIServiceSender(context);
        
        VoipLogic voipLogic = new VoipLogic(context);
        ImLogic imLogic = new ImLogic(context, sender);
        PluginLogic pluginLogic = new PluginLogic(context);
        GroupLogic groupLogic = new GroupLogic(context, sender);
        LoginLogic loginLogic = new LoginLogic(context, sender);
        RegisterLogic registerLogic = new RegisterLogic(context);
        FriendLogic friendLogic = new FriendLogic(context, sender);
        ContactLogic contactLogic = new ContactLogic(context, sender);
        SettingsLogic settingsLogic = new SettingsLogic(context, sender);
        FriendHelperLogic friendHelperLogic = new FriendHelperLogic(context,
                sender);
        ConversationLogic conversationLogic = new ConversationLogic(context,
                sender);
        CommunicationLogLogic communicationLogLogic = new CommunicationLogLogic(
                context);
        
        loginLogic.setImLogic(imLogic);
        loginLogic.setGroupLogic(groupLogic);
        loginLogic.setFriendLogic(friendLogic);
        loginLogic.setContactLogic(contactLogic);
        loginLogic.setSettingsLogic(settingsLogic);
        
        registerLogic(IImLogic.class, imLogic);
        registerLogic(IVoipLogic.class, voipLogic);
        registerLogic(IGroupLogic.class, groupLogic);
        registerLogic(ILoginLogic.class, loginLogic);
        registerLogic(IFriendLogic.class, friendLogic);
        registerLogic(IPluginLogic.class, pluginLogic);
        registerLogic(IContactLogic.class, contactLogic);
        registerLogic(ISettingsLogic.class, settingsLogic);
        registerLogic(IRegisterLogic.class, registerLogic);
        registerLogic(IFriendHelperLogic.class, friendHelperLogic);
        registerLogic(IConversationLogic.class, conversationLogic);
        registerLogic(ICommunicationLogLogic.class, communicationLogLogic);
    }
}
