/*
 * 文件名: ContactManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Administrator
 * 创建时间:2012-2-22
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.json.JsonUtils;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 通讯录列表管理<BR>
 * [功能详细描述]
 * @author 邵培培
 * @version [RCS Client V100R001C03, 2012-2-22] 
 */
public class ContactManager extends HttpManager
{
    
    /**
     * 上传增加的联系人
     */
    private static final int ACTION_UPLOAD_ADD_CONTACTS = 0x00000000;
    
    /**
     * 上传更改的联系人
     */
    private static final int ACTION_UPLOAD_UPDATE_CONTACTS = 0x00000001;
    
    /**
     * 上传删除的联系人
     */
    private static final int ACTION_UPLOAD_DELETE_CONTACTS = 0x00000002;
    
    /**
     * 得到联系人的userId
     */
    private static final int ACTION_GET_USER_USERID = 0x00000003;
    
    /**
     * 邀请好友
     */
    private static final int ACTION_INVITE_FRIEND = 0x00000004;
    
    /**
     * 删除服务器上的电话簿好友
     */
    private static final int ACTION_DELETE_ALL_UPLOADED_CONTACTS = 0x00000005;
    
    /**
     * 上传新增联系人的URL
     */
    private static final String URL_UPLOAD_ADD_CONTACTS = "v1/contacts/me?ER=1";
    
    /**
     * 上传更改的联系人的URL
     */
    private static final String URL_UPLOAD_UPDATE_CONTACTS = "v1/contacts/me";
    
    /**
     * 上传删除的联系人的URL
     */
    private static final String URL_UPLOAD_DELETE_CONTACTS = "v1/contacts/me/del/multi?DelMode=0";
    
    /**
     * 得到联系人的userId的URL
     */
    private static final String URL_GET_USER_USERID = "v1/contacts/user/me/get/all";
    
    /**
     * 邀请好友
     */
    private static final String URL_INVITE_FRIEND = "inviteUserServlet";
    
    /**
     * 全部删除服务器上通讯录好友（把状态置成delete）
     */
    private static final String URL_DELETE_ALL_UPLOADED_CONTACTS = "/v1/contacts/me/all?{DelMode}=0";
    
    private static final int ALTERNATE_MAX_SIZE = 5;
    
    /**
     * TAG
     */
    private static final String TAG = "ContactManager";
    
    /**
     * 
     * 获取所有联系人在沃友系统中的SysId<BR>
     * [功能详细描述]
     * @param sendData sendData
     * @param iListener iListener
     */
    public void sendForContact(HashMap<String, Object> sendData,
            IHttpListener iListener)
    {
        super.send(ACTION_GET_USER_USERID, sendData, iListener);
    }
    
    /**
     * 
     * 好友邀请<BR>
     * [功能详细描述]
     * @param sendData 发送数据
     * @param iListener 回调监听
     */
    public void inviteFriend(Map<String, Object> sendData,
            IHttpListener iListener)
    {
        super.send(ACTION_INVITE_FRIEND,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 
     * 上传通讯录<BR>
     * [功能详细描述]
     * @param action action
     * @param contacts 发送数据
     * @param iListener 监听
     */
    public void uploadContactsToCAB(int action,
            List<PhoneContactIndexModel> contacts, IHttpListener iListener)
    {
        //在此处理向服务器请求的东西
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put("List", contacts);
        // 发送服务器
        super.send(action, sendData, iListener);
    }
    
    /**
     * 删除服务器上所有通讯录好友<BR>
     * @param iListener 回调接口
     */
    public void deleteUploadedContacts(IHttpListener iListener)
    {
        super.send(ACTION_DELETE_ALL_UPLOADED_CONTACTS, null, iListener);
    }
    
    /**
     * 根据Action获取请求的Body<BR>
     * [功能详细描述]
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return 请求消息体字符串，一般为XML或者JSON
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getBody(int, java.util.Map)
     */
    
    @SuppressWarnings("unchecked")
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        List<PhoneContactIndexModel> contacts = null;
        if (null != sendData)
        {
            contacts = (List<PhoneContactIndexModel>) sendData.get("List");
        }
        switch (action)
        {
            case ACTION_UPLOAD_ADD_CONTACTS:
                //全量上传时，默认为新增的
                return generateUploadReqStr(contacts,
                        PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD);
            case ACTION_UPLOAD_UPDATE_CONTACTS:
                return generateUploadReqStr(contacts,
                        PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE);
            case ACTION_UPLOAD_DELETE_CONTACTS:
                return generateUploadReqStr(contacts,
                        PhoneContactIndexModel.CONTACT_MODIFY_FLAG_DELETE);
            case ACTION_GET_USER_USERID:
                return null;
            case ACTION_INVITE_FRIEND:
                return getInviteFriendsBody(sendData);
            case ACTION_DELETE_ALL_UPLOADED_CONTACTS:
                return null;
        }
        return null;
    }
    
    private String getInviteFriendsBody(Map<String, Object> sendData)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version = \"1.0\" encoding=\"utf-8\" ?>");
        xml.append("<root>")
                .append("<account>")
                .append(sendData.get("account"))
                .append("</account>")
                .append("<displayName>")
                .append(sendData.get("displayName"))
                .append("</displayName>")
                .append("<destUser>")
                .append(sendData.get("destUser"))
                .append("</destUser>")
                .append("<inviteType>")
                .append(sendData.get("inviteType"))
                .append("</inviteType>")
                .append("</root>");
        Logger.i(TAG, " ACTION_GET_USER_USERID:Body = " + xml.toString());
        
        return xml.toString();
    }
    
    /**
     * 
     * 获取上传通讯录特定接口对应的URL<BR><BR>
     * 根据不同的action赋值不同的URL
     * @param action action
     * @param sendData sendData
     * @return 服务器url地址
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getUrl(int, java.util.Map)
     */
    
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        switch (action)
        {
            case ACTION_UPLOAD_ADD_CONTACTS:
                return FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_UPLOAD_ADD_CONTACTS;
            case ACTION_UPLOAD_UPDATE_CONTACTS:
                return FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_UPLOAD_UPDATE_CONTACTS;
            case ACTION_UPLOAD_DELETE_CONTACTS:
                return FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_UPLOAD_DELETE_CONTACTS;
            case ACTION_GET_USER_USERID:
                return FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_GET_USER_USERID;
            case ACTION_INVITE_FRIEND:
                return FusionConfig.getInstance().getAasResult().getPortalurl()
                        + URL_INVITE_FRIEND;
            case ACTION_DELETE_ALL_UPLOADED_CONTACTS:
                return FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_DELETE_ALL_UPLOADED_CONTACTS;
        }
        return null;
    }
    
    /**
     * 对服务器返回的数据进行解析处理，封装对象<BR>
     * [功能详细描述]
     * @param action 请求Action，用来标识不同的请求
     * @param sendData 调用者发送请求时封装的数据
     * @param response 服务器返回数据对象
     * @return 封装后的对象，在onResult()中通过response.getObj()获得。
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#handleData
     *    (int, java.util.Map, com.huawei.basic.android.im.component.net.http.Response)
     */
    
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        switch (action)
        {
            case ACTION_UPLOAD_ADD_CONTACTS:
                List<PhoneContactIndexModel> resultUploadAdd = parsePhoneContactIndexModel(response.getData(),
                        sendData);
                return resultUploadAdd;
            case ACTION_UPLOAD_UPDATE_CONTACTS:
                List<PhoneContactIndexModel> resultUploadUpdate = parsePhoneContactIndexModel(response.getData(),
                        sendData);
                return resultUploadUpdate;
            case ACTION_UPLOAD_DELETE_CONTACTS:
                List<PhoneContactIndexModel> resultUploadDelete = parsePhoneContactIndexModel(response.getData(),
                        sendData);
                return resultUploadDelete;
            case ACTION_GET_USER_USERID:
                return parseUserID(response.getData());
            case ACTION_INVITE_FRIEND:
                return parseInviteFriend(response.getData());
            case ACTION_DELETE_ALL_UPLOADED_CONTACTS:
                return null;
        }
        return null;
    }
    
    /**
     * 
     * 解析邀请好友请求服务器返回信息<BR>
     * 
     * @param sData
     * @return
     */
    private String parseInviteFriend(String responseData)
    {
        String sReturnCode = "-1";
        if (null != responseData)
        {
            try
            {
                HttpDataParse.InviteFiendParse pn = new XmlParser().parseXmlString(HttpDataParse.InviteFiendParse.class,
                        responseData);
                if (null != pn.getInviteRetInfos())
                {
                    sReturnCode = pn.getInviteRetInfos().get(0).getRetCode();
                    pn.getInviteRetInfos().get(0).getRetDesc();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return sReturnCode;
    }
    
    /**
     * 获取request请求方式<BR>
     * @param action action标识
     * @return RequestMethod request请求方式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        switch (action)
        {
            case ACTION_UPLOAD_DELETE_CONTACTS:
                return RequestMethod.POST;
            case ACTION_INVITE_FRIEND:
                return RequestMethod.POST;
            case ACTION_UPLOAD_ADD_CONTACTS:
                return RequestMethod.POST;
            case ACTION_UPLOAD_UPDATE_CONTACTS:
                return RequestMethod.PUT;
            case ACTION_GET_USER_USERID:
                return RequestMethod.GET;
            case ACTION_DELETE_ALL_UPLOADED_CONTACTS:
                return RequestMethod.DELETE;
        }
        return super.getRequestMethod(action);
    }
    
    /**
     * 
     * 请求property<BR> 
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @return request property list
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestProperties(int)
     */
    @Override
    protected List<NameValuePair> getRequestProperties(int action)
    {
        List<NameValuePair> temp = super.getRequestProperties(action);
        temp.add(new BasicNameValuePair("Authorization",
                FusionConfig.getInstance().getCabReqAuthorization()));
        return temp;
    }
    
    /**
     * 解析服务器返回的变更的联系人数据<BR>
     * [功能详细描述]
     * @param data 收到的data
     * @param sendData 发送时的数据
     * @return 解析后的数据
     */
    @SuppressWarnings("unchecked")
    private List<PhoneContactIndexModel> parsePhoneContactIndexModel(
            String data, Map<String, Object> sendData)
    {
        List<PhoneContactIndexModel> contacts = (List<PhoneContactIndexModel>) sendData.get("List");
        if (null != data)
        {
            try
            {
                JSONObject rootJsonObj = new JSONObject(data);
                // 解析返回结果
                JSONArray contactIDs;
                contactIDs = rootJsonObj.getJSONArray("ContactIDs");
                if (contactIDs != null && contactIDs.length() > 0)
                {
                    for (int k = 0; k < contacts.size(); k++)
                    {
                        String contactId = null;
                        try
                        {
                            contactId = contactIDs.getString(k);
                        }
                        catch (Exception e)
                        {
                            // 比如数组越界
                            Logger.w(TAG, "解析ContactIDs失败", e);
                            continue;
                        }
                        PhoneContactIndexModel contact = contacts.get(k);
                        contact.setContactGUID(contactId);
                    }
                }
                else
                {
                    Logger.w(TAG, "没有ContactIDs");
                }
            }
            catch (JSONException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return contacts;
    }
    
    /**
     * 
     * 在向服务器发送数据前，封装发送变更联系人的数据<BR>
     * [功能详细描述]
     * @param contacts 联系人
     * @param modifyFlag 修改标志
     * @return 封装的数据
     */
    private String generateUploadReqStr(List<PhoneContactIndexModel> contacts,
            int modifyFlag)
    {
        if (contacts == null || contacts.size() < 1)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD
                || modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE)
        {
            // 新增或修改
            for (PhoneContactIndexModel contact : contacts)
            {
                sb.append(",{\"ContactInfo\":{");
                List<List<String>> numbers = contact.getPhoneNumbers();
                if (numbers != null && !numbers.isEmpty())
                {
                    sb.append("\"PrimaryMobile\":\"");
                    sb.append(numbers.get(0).get(0));
                    sb.append("\"");
                    if (numbers.size() > 1)
                    {
                        sb.append(",AlternateMobiles:[");
                        int maxSize = numbers.size();
                        if (maxSize > ALTERNATE_MAX_SIZE + 1)
                        {
                            maxSize = ALTERNATE_MAX_SIZE + 1;
                        }
                        for (int k = 1; k < maxSize; k++)
                        {
                            if (k == 1)
                            {
                                sb.append("\"");
                            }
                            else
                            {
                                sb.append(",\"");
                            }
                            sb.append(numbers.get(k));
                            sb.append("\"");
                        }
                        sb.append("]");
                    }
                }
                else
                {
                    sb.append("\"PrimaryMobile\":\"");
                    sb.append("\"");
                }
                
                List<List<String>> emails = contact.getEmailAddrs();
                if (emails != null && !emails.isEmpty())
                {
                    sb.append(",");
                    sb.append("\"PrimaryEmail\":\"");
                    sb.append(emails.get(0).get(0));
                    sb.append("\"");
                    if (emails.size() > 1)
                    {
                        sb.append(",AlternateEmails:[");
                        int maxSize = emails.size();
                        if (maxSize > ALTERNATE_MAX_SIZE + 1)
                        {
                            maxSize = ALTERNATE_MAX_SIZE + 1;
                        }
                        for (int k = 1; k < maxSize; k++)
                        {
                            if (k == 1)
                            {
                                sb.append("\"");
                            }
                            else
                            {
                                sb.append(",\"");
                            }
                            sb.append(emails.get(k));
                            sb.append("\"");
                        }
                        sb.append("]");
                    }
                }
                else
                {
                    sb.append(",");
                    sb.append("\"PrimaryEmail\":\"");
                    sb.append("\"");
                }
                
                sb.append("}");
                if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE)
                {
                    sb.append(",\"ContactID\":\"");
                    sb.append(contact.getContactGUID());
                    sb.append("\"");
                }
                sb.append("}");
            }
            sb.deleteCharAt(0);
            if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD)
            {
                sb.insert(0, "{\"ContactWithListIDs\":[");
            }
            else
            {
                sb.insert(0, "{\"ContactWithBothIDs\":[");
            }
            sb.append("]}");
        }
        else
        {
            for (PhoneContactIndexModel contact : contacts)
            {
                sb.append(",\"");
                sb.append(contact.getContactGUID());
                sb.append("\"");
            }
            sb.deleteCharAt(0);
            sb.insert(0, "{\"ContactIDs\":[");
            sb.append("]}");
        }
        
        String str = sb.toString();
        sb.delete(0, sb.length());
        return str;
    }
    
    /**
     * 
     * 解析Userid<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @return 解析的数据
     */
    private ArrayList<PhoneContactIndexModel> parseUserID(String data)
    {
        ArrayList<PhoneContactIndexModel> contactUsers = new ArrayList<PhoneContactIndexModel>();
        JSONObject rootJsonObj;
        JSONArray contactUserArr = null;
        try
        {
            rootJsonObj = new JSONObject(data);
            if (rootJsonObj.has("ContactUsers"))
            {
                contactUserArr = rootJsonObj.getJSONArray("ContactUsers");
                String contactId = null;
                String userId = null;
                String account = null;
                String fp = null;
                String acf = null;
                PhoneContactIndexModel contact = null;
                for (int j = 0; j < contactUserArr.length(); j++)
                {
                    JSONObject obj = JsonUtils.getJSONObject(contactUserArr, j);
                    contactId = JsonUtils.getString(obj, "ContactID");
                    userId = JsonUtils.getString(obj, "UserID");
                    account = JsonUtils.getString(obj, "Account");
                    fp = JsonUtils.getString(obj, "FP");
                    acf = JsonUtils.getString(obj, "ACF");
                    
                    if (!StringUtil.isNullOrEmpty(contactId)
                            && !StringUtil.isNullOrEmpty(userId)
                            && !StringUtil.isNullOrEmpty(account))
                    {
                        contact = new PhoneContactIndexModel();
                        contact.setContactGUID(contactId);
                        contact.setContactSysId(userId);
                        contact.setContactUserId(account);
                        contact.setFp(fp);
                        contact.setAcf(acf);
                        
                        contactUsers.add(contact);
                    }
                }
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return contactUsers;
    }
    
}
