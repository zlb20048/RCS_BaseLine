/*
 * 文件名: ContactInfoManager.java 版 权： Copyright Huawei Tech. Co. Ltd. All Rights
 * Reserved. 描 述: 好友列表管理器 创建人: deanye 创建时间:2011-10-18 修改人： 修改时间: 修改内容：[修改内容]
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
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;

/**
 * 
 * 好友列表管理器 <BR>
 * 负责数据层的处理、封装数据、请求服务器等业务逻辑处理
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-2-16]
 */
public class ContactInfoManager extends HttpManager
{
    /**
     * 获取详情时间戳
     */
    public static final String GET_CONTACT_INFO_TIMESTAMP = "contact_info_timestamp";
    
    /**
     * 定义页面TAG
     */
    private static final String TAG = "ContactInfoManager";
    
    /**
     * 获取好友备注url
     */
    private static final String URL_GET_FRIEND_MEMOS = "v1/friendmemos/get/multi";
    
    /**
     * 修改好友备注的url
     */
    private static final String URL_UPDATE_FRIEND_MEMO = "v1/friendmemos";
    
    /**
     * 批量获取好友详情的url
     */
    private static final String URL_MANY_FRIENDS = "/v1/profiles/get/multi";
    
    /**
     * 定义Action：批量获取好友详情信息
     */
    private static final int GET_MULTI_FRIENDS_DETAILS = 0x0001;
    
    /**
     * 定义Action：更新服务器备注
     */
    private static final int UPDATE_FRIEND_MEMO = 0x0002;
    
    /**
     * 定义Action：获取备注信息
     */
    private static final int GET_FRIEND_MEMOS = 0x0003;
    
    /**
     * 
     * 获取请求方法类型<BR>
     * [功能详细描述]
     * @param action action
     * @return RequestMethod
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    @Override
    public RequestMethod getRequestMethod(int action)
    {
        switch (action)
        {
            case UPDATE_FRIEND_MEMO:
                return RequestMethod.PUT;
        }
        return super.getRequestMethod(action);
    }
    
    /**
     * 
     * 封装Send()方法<BR>
     * 获取好友详细信息
     * @param sendData 发送服务器信息
     * @param iListener 监听
     */
    public void sendDetail(HashMap<String, Object> sendData,
            IHttpListener iListener)
    {
        super.send(GET_MULTI_FRIENDS_DETAILS,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 
     * 封装Send()方法<BR>
     * 更新服务器备注
     * @param sendData 发送服务器信息
     * @param iListener 监听
     */
    public void updateFriendMemo(HashMap<String, Object> sendData,
            IHttpListener iListener)
    {
        super.send(UPDATE_FRIEND_MEMO,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 
     * 封装Send()方法<BR>
     * 获取服务器备注信息
     * @param sendData 发送服务器信息
     * @param iListener 监听
     */
    public void getFriendMemo(HashMap<String, Object> sendData,
            IHttpListener iListener)
    {
        super.send(GET_FRIEND_MEMOS,
                (HashMap<String, Object>) sendData,
                iListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return body
     */
    private String jsonOfGetFriendMemo(Map<String, Object> sendData)
    {
        String body = null;
        JSONObject getFriendMemoObj = new JSONObject();
        JSONArray getFriendMemoArray = new JSONArray();
        try
        {
            body = getFriendMemoObj.put("UserIDs",
                    getFriendMemoArray.put(sendData.get("sysId"))).toString();
            
            Logger.i(TAG, "jsonOfGetFriendMemo" + body);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return body;
    }
    
    /**
    *
    * 解析获得好友备注的JOSN
    *
    * @param data 服务器返回字符串
    * @return List<ContactInfoModel>
    */
    private List<ContactInfoModel> parsememosContactInfoModel(String data,
            Map<String, Object> sendData)
    {
        List<ContactInfoModel> result = null;
        ContactInfoModel contactInfoModel = new ContactInfoModel();
        
        if (null != data)
        {
            try
            {
                // 将contactInfoModel引用添加到List中
                result = new ArrayList<ContactInfoModel>();
                result.add(contactInfoModel);
                // 解析DATA
                JSONObject rootJsonObj = new JSONObject(data);
                if (rootJsonObj.has("FriendMemos"))
                {
                    JSONArray friendmemos = rootJsonObj.getJSONArray("FriendMemos");
                    for (int i = 0; i < friendmemos.length(); i++)
                    {
                        JSONObject friendmemo = friendmemos.getJSONObject(i);
                        if (friendmemo.has("UserID"))
                        {
                            contactInfoModel.setFriendSysId(friendmemo.getString("UserID"));
                        }
                        else
                        {
                            contactInfoModel.setFriendSysId((String) sendData.get("sysId"));
                        }
                        if (friendmemo.has("DisplayName"))
                        {
                            contactInfoModel.setMemoName(friendmemo.getString("DisplayName"));
                        }
                        if (friendmemo.has("PhoneNums"))
                        {
                            JSONArray phonenums = friendmemo.getJSONArray("PhoneNums");
                            List<String> phoneslist = new ArrayList<String>();
                            for (int j = 0; j < phonenums.length(); j++)
                            {
                                phoneslist.add(phonenums.getString(j));
                            }
                            contactInfoModel.setMemoPhones(phoneslist);
                        }
                        if (friendmemo.has("Emails"))
                        {
                            JSONArray emails = friendmemo.getJSONArray("Emails");
                            List<String> emailslist = new ArrayList<String>();
                            for (int j = 0; j < emails.length(); j++)
                            {
                                emailslist.add(emails.getString(j));
                            }
                            contactInfoModel.setMemoEmails(emailslist);
                        }
                        
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return body
     */
    private String getFriendsJsonBody(Map<String, Object> sendData)
    {
        String body = null;
        @SuppressWarnings("unchecked")
        ArrayList<String> ids = (ArrayList<String>) sendData.get("List");
        try
        {
            // 如果已经有该好友的数据，需要加上好友的时间戳
            JSONObject obj = new JSONObject();
            JSONArray objArray = new JSONArray();
            JSONObject subObj = null;
            for (String hiTalk : ids)
            {
                subObj = new JSONObject();
                subObj.put("AID", hiTalk);
                
                if (null != sendData.get(ContactInfoManager.GET_CONTACT_INFO_TIMESTAMP))
                {
                    subObj.put("UT",
                            sendData.get(ContactInfoManager.GET_CONTACT_INFO_TIMESTAMP));
                }
                objArray.put(subObj);
            }
            obj.put("AIDTs", objArray);
            body = obj.toString();
            
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return body;
    }
    
    /**
    *
    * 修改好友备注的json包
    *
    * @return body
    */
    private String jsonOfUpdateFriendMemo(Map<String, Object> sendData)
    {
        String body = null;
        try
        {
            JSONObject updateFriendMemoObj = new JSONObject();
            JSONArray updateFriendMemoArray = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("UserID", sendData.get(ContactInfoColumns.FRIEND_SYSID));
            obj.put("DisplayName", sendData.get(ContactInfoColumns.MEMO_NAME));
            obj.put("PhoneNums",
                    new JSONArray().put(sendData.get(ContactInfoColumns.MEMO_PHONE)));
            obj.put("Emails",
                    new JSONArray().put(sendData.get(ContactInfoColumns.MEMO_EMAIL)));
            updateFriendMemoArray.put(obj);
            updateFriendMemoObj.put("Friendmemos", updateFriendMemoArray);
            
            body = updateFriendMemoObj.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return body;
    }
    
    /**
    *
    * 解析获得好友详细
    *
    * @param data
    * @return
    */
    private ArrayList<Object> parseContactInfoModel(String data,
            Map<String, Object> sendData)
    {
        // 封装返回对象：resultObj.get(0) 详细信息，resultObj.get(1) 服务器返回时间戳（需要更新数据库）
        ArrayList<Object> resultObj = new ArrayList<Object>();
        // 服务器返回详细信息封装对象ContactInfoModel
        ContactInfoModel contactInfoModel = null;
        // 存放多条详情记录ArrayList<ContactInfoModel>
        ArrayList<ContactInfoModel> resultContactInfo = null;
        
        if (null != data)
        {
            try
            {
                JSONObject rootJsonObj = new JSONObject(data);
                if (rootJsonObj.has("ProfileList"))
                {
                    JSONArray publicProfileArray = rootJsonObj.getJSONArray("ProfileList");
                    int size = publicProfileArray.length();
                    resultContactInfo = new ArrayList<ContactInfoModel>();
                    for (int i = 0; i < size; i++)
                    {
                        contactInfoModel = new ContactInfoModel();
                        resultContactInfo.add(contactInfoModel);
                        JSONObject publicProfileObj = publicProfileArray.getJSONObject(i);
                        if (publicProfileObj != null)
                        {
                            if (publicProfileObj.has("UserID"))
                            {
                                contactInfoModel.setFriendSysId(publicProfileObj.getString("UserID"));
                            }
                            if (publicProfileObj.has("Birthday"))
                            {
                                contactInfoModel.setBirthday(publicProfileObj.getString("Birthday"));
                            }
                            // 用户的性别
                            if (publicProfileObj.has("Gender"))
                            {
                                contactInfoModel.setGender(publicProfileObj.getInt("Gender"));
                            }
                            // 用户的婚姻状况
                            if (publicProfileObj.has("MarriageStatus"))
                            {
                                contactInfoModel.setMarriageStatus(publicProfileObj.getInt("MarriageStatus"));
                            }
                            // 用户详细地址
                            if (publicProfileObj.has("Address"))
                            {
                                JSONObject addressObj = publicProfileObj.getJSONObject("Address");
                                // 联系人的邮政编码
                                if (addressObj.has("PostalCode"))
                                {
                                    contactInfoModel.setPostalCode(addressObj.getString("PostalCode"));
                                }
                                // 联系人的邮政信箱
                                if (addressObj.has("Building"))
                                {
                                    contactInfoModel.setBuilding(addressObj.getString("Building"));
                                }
                                // 联系人所在街道
                                if (addressObj.has("Street"))
                                {
                                    contactInfoModel.setStreet(addressObj.getString("Street"));
                                }
                                // 联系人的地址详情
                                if (addressObj.has("AddressLine1"))
                                {
                                    contactInfoModel.setAddress(addressObj.getString("AddressLine1"));
                                }
                                // 联系人所在的城/县
                                if (addressObj.has("City"))
                                {
                                    contactInfoModel.setCity(addressObj.getString("City"));
                                }
                                // 联系人所在的省/市/自治区
                                if (addressObj.has("Province"))
                                {
                                    contactInfoModel.setProvince(addressObj.getString("Province"));
                                }
                                // 联系人所在的国家
                                if (addressObj.has("Country"))
                                {
                                    contactInfoModel.setCountry(addressObj.getString("Country"));
                                }
                            }
                            // 用户的移动手机号码
                            if (publicProfileObj.has("PrimaryMobile"))
                            {
                                contactInfoModel.setPrimaryMobile(publicProfileObj.getString("PrimaryMobile"));
                            }
                            if (publicProfileObj.has("WorkInfo"))
                            {
                                JSONArray workInfosArray = publicProfileObj.getJSONArray("WorkInfo");
                                JSONObject srsObj = null;
                                for (int j = 0; j < workInfosArray.length(); j++)
                                {
                                    srsObj = workInfosArray.getJSONObject(j);
                                    // 返回数组存几个阿
                                    if (srsObj.has("Company"))
                                    {
                                        contactInfoModel.setCompany(srsObj.getString("Company"));
                                    }
                                    if (srsObj.has("Department"))
                                    {
                                        contactInfoModel.setDeparment(srsObj.getString("Department"));
                                    }
                                    if (srsObj.has("Title"))
                                    {
                                        contactInfoModel.setTitle(srsObj.getString("Title"));
                                    }
                                }
                            }
                            // 用户的学校信息
                            if (publicProfileObj.has("SchoolInfo"))
                            {
                                JSONArray workInfosArray = publicProfileObj.getJSONArray("SchoolInfo");
                                JSONObject srsObj = null;
                                for (int j = 0; j < workInfosArray.length(); j++)
                                {
                                    srsObj = workInfosArray.getJSONObject(j);
                                    // 返回数组存几个阿
                                    if (srsObj.has("School"))
                                    {
                                        contactInfoModel.setSchool(srsObj.getString("School"));
                                    }
                                    if (srsObj.has("Course"))
                                    {
                                        contactInfoModel.setCourse(srsObj.getString("Course"));
                                    }
                                    if (srsObj.has("Batch"))
                                    {
                                        contactInfoModel.setBatch(srsObj.getString("Batch"));
                                    }
                                }
                            }
                            // 头像照片
                            if (publicProfileObj.has("PhotoURL"))
                            {
                                contactInfoModel.setFaceUrl(publicProfileObj.getString("PhotoURL"));
                            }
                            // 说明（个人签名）
                            if (publicProfileObj.has("Notes"))
                            {
                                contactInfoModel.setSignature(publicProfileObj.getString("Notes"));
                            }
                            // 用户的年龄，取值范围为[0,200]
                            if (publicProfileObj.has("Age"))
                            {
                                contactInfoModel.setAge(publicProfileObj.getString("Age"));
                            }
                            // 生肖
                            if (publicProfileObj.has("Zodiac"))
                            {
                                contactInfoModel.setZodiac(publicProfileObj.getInt("Zodiac"));
                            }
                            // 星座
                            if (publicProfileObj.has("Astro"))
                            {
                                contactInfoModel.setAstro(publicProfileObj.getInt("Astro"));
                            }
                            // 用户的血型
                            if (publicProfileObj.has("Blood"))
                            {
                                contactInfoModel.setBlood(publicProfileObj.getInt("Blood"));
                            }
                            // 用户是否在线
                            if (publicProfileObj.has("Online"))
                            {
                                contactInfoModel.setOnline(publicProfileObj.getInt("Online"));
                            }
                            // 即时通信用户业务ID
                            if (publicProfileObj.has("Account"))
                            {
                                contactInfoModel.setFriendUserId(publicProfileObj.getString("Account"));
                            }
                            // 用户最后更新个人信息信息的时间
                            if (publicProfileObj.has("LastUpdate"))
                            {
                                contactInfoModel.setLastUpdate(publicProfileObj.getString("LastUpdate"));
                            }
                            // 用户绑定的Email地址。
                            if (publicProfileObj.has("Email"))
                            {
                                contactInfoModel.setPrimaryEmail(publicProfileObj.getString("Email"));
                            }
                            // 用户显示名称或昵称
                            if (publicProfileObj.has("DisplayName"))
                            {
                                contactInfoModel.setNickName(publicProfileObj.getString("DisplayName"));
                            }
                            // 用户爱好
                            if (publicProfileObj.has("Hobby"))
                            {
                                contactInfoModel.setHobby(publicProfileObj.getString("Hobby"));
                            }
                            if (publicProfileObj.has("Introduction"))
                            {
                                contactInfoModel.setDescription(publicProfileObj.getString("Introduction"));
                            }
                            if (publicProfileObj.has("Level"))
                            {
                                contactInfoModel.setLevel(publicProfileObj.getInt("Level"));
                            }
                            // 归属地 0：未知 1：本网 2：异网
                            if (publicProfileObj.has("HomeLocation"))
                            {
                                contactInfoModel.setHomeLocation(publicProfileObj.getInt("HomeLocation"));
                            }
                            if (publicProfileObj.has("FriendPrivacy"))
                            {
                                contactInfoModel.setFriendPrivacy(publicProfileObj.getInt("FriendPrivacy"));
                            }
                            if (publicProfileObj.has("AutoConfirmFriend"))
                            {
                                contactInfoModel.setAutoConfirmFriend(publicProfileObj.getInt("AutoConfirmFriend"));
                            }
                        }
                    }
                }
                // 始终将详情信息加入resultObj
                resultObj.add(resultContactInfo);
                
                //时间戳处理
                String timestamp = null;
                if (rootJsonObj.has("T"))
                {
                    timestamp = "T=" + rootJsonObj.getString("T");
                    
                }
                if (rootJsonObj.has("LV"))
                {
                    timestamp = timestamp + "&LV="
                            + rootJsonObj.getString("LV");
                }
                if (timestamp != null)
                {
                    resultObj.add(timestamp);
                }
                else
                {
                    resultObj.add(null);
                }
            }
            catch (JSONException e)
            {
                Logger.e(TAG, "Parse JSON failed, msg is: " + e.getMessage());
            }
        }
        return resultObj;
    }
    
    /**
     * 封装请求消息体<BR>
     * 
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return 请求消息体字符串，一般为XML或者JSON
     */
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        String body = null;
        switch (action)
        {
            // 批量获取好友信息
            case GET_MULTI_FRIENDS_DETAILS:
                body = getFriendsJsonBody(sendData);
                break;
            // 更新备注信息
            case UPDATE_FRIEND_MEMO:
                body = jsonOfUpdateFriendMemo(sendData);
                break;
            // 获取备注信息
            case GET_FRIEND_MEMOS:
                body = jsonOfGetFriendMemo(sendData);
        }
        return body;
    }
    
    /**
     * 
     * 对服务器返回的数据进行解析处理，封装对象<BR>
     * 
     * @param action 请求Action，用来标识不同的请求
     * @param sendData 调用者发送请求时封装的数据
     * @param response 服务器返回数据对象
     * @return 封装后的对象，在onResult()中通过response.getObj()获得。
     */
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        switch (action)
        {
            // 获取好友详细信息
            case GET_MULTI_FRIENDS_DETAILS:
                return (ArrayList<Object>) parseContactInfoModel(response.getData(),
                        sendData);
                // 获取备注信息
            case GET_FRIEND_MEMOS:
                return (List<ContactInfoModel>) parsememosContactInfoModel(response.getData(),
                        sendData);
        }
        
        return null;
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
     * 
     * 获取好友分组特定接口对应的URL<BR><BR>
     * 根据不同的action赋值不同的URL
     * @param action action
     * @param sendData sendData
     * @return 服务器url地址
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getUrl(int, java.util.Map)
     */
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        String url = null;
        switch (action)
        {
            // 批量获取好友信息
            case GET_MULTI_FRIENDS_DETAILS:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_MANY_FRIENDS;
                break;
            // 更新备注信息    
            case UPDATE_FRIEND_MEMO:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_UPDATE_FRIEND_MEMO;
                break;
            // 获取备注信息
            case GET_FRIEND_MEMOS:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getCabgroupurl()
                        + URL_GET_FRIEND_MEMOS;
                break;
            default:
                break;
        }
        return url;
    }
    
    /**
     * 更新服务器备注信息
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param contactInfoModel contactInfoModel
     * @param friendMemoName friendMemoName
     * @param friendMemoPhone friendMemoPhone
     * @param friendMemoEmail friendMemoEmail
     * @param httpListener httpListener
     */
    public void sendFriendMemo(final ContactInfoModel contactInfoModel,
            final String friendMemoName, final String friendMemoPhone,
            final String friendMemoEmail, IHttpListener httpListener)
    {
        final HashMap<String, Object> friendMap = new HashMap<String, Object>();
        friendMap.put(ContactInfoColumns.FRIEND_SYSID,
                contactInfoModel.getFriendSysId());
        friendMap.put(ContactInfoColumns.MEMO_NAME, friendMemoName);
        friendMap.put(ContactInfoColumns.MEMO_PHONE, friendMemoPhone);
        friendMap.put(ContactInfoColumns.MEMO_EMAIL, friendMemoEmail);
        super.send(UPDATE_FRIEND_MEMO,
                friendMap,
                httpListener);
    }
    
    /**
     * 获取服务器备注信息
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param friendSysId friendSysId
     * @param httpListener httpListener
     */
    public void getFriendMemo(final String friendSysId,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put("sysId", friendSysId);
        super.send(GET_FRIEND_MEMOS, sendData, httpListener);
    }
    
}