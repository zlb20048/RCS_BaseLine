/*
 * 文件名: FriendManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.NearUserModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 好友联网处理<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 14, 2012] 
 */
public class FriendManager extends HttpManager
{
    /**
     * debug tag
     */
    private static final String TAG = "FriendManager";
    
    /**
     * 获取好友列表
     */
    private static final int ACTION_GET_FRIEND_LIST = 1;
    
    /**
     * 查询更多好友<好友列表>
     */
    private static final int ACTION_SEARCH_FRIEND_FROM_SERVER = 2;
    
    /**
     * <好友列表的action定义> 查询认识的好友
     */
    private static final int ACTION_SERARCH_MAYBE_KNOWN_PERSON = 3;
    
    /**
     * 添加分组
     */
    private static final int ACTION_ADD_SECTION = 4;
    
    /**
     * 更新分组名称
     */
    private static final int ACTION_UPDATE_SECTION_NAME_ACTION = 5;
    
    /**
     * 删除分组
     */
    private static final int ACTION_DELETE_SECTION = 6;
    
    /**
     * 添加联系人到分组
     */
    private static final int ACTION_ADD_CONTACTS_TO_SECTION = 7;
    
    /**
     * 移动分组
     */
    private static final int ACTION_MOVE_TO_SECTION = 8;
    
    /**
     * 从分组移除联系人
     */
    private static final int ACTION_REMOVE_MEMBER_FROM_SECTION = 9;
    
    /**
     * 附近查找
     */
    private static final int ACTION_SEARCH_LOCATION = 10;
    
    /**
     * 清除位置信息
     */
    private static final int ACTION_REMOVE_LOCATION = 11;
    
    /**
     * 封装JSON对象时需要用到的key定义
     */
    private static final String JSON_KEY_CONTACT_IDS = "FriendIDs";
    
    /**
     * 添加分组的 组名
     */
    private static final String ADD_SECTION_PARAM_NAME = "section_name";
    
    /**
     * 添加分组的 sys id list
     */
    private static final String ADD_SECTION_PARAM_SYS_ID_LIST = "sys_id_list";
    
    /**
     * 添加分组的 notes
     */
    private static final String ADD_SECTION_PARAM_NOTES = "section_notes";
    
    /**
     * 删除分组的分组ID
     */
    private static final String DELETE_SECTION_PARAM_SECTION_ID = "delete_section_param_section_id";
    
    /**
     * 刪除分組的组员的sys id列表
     */
    private static final String DELETE_SECTION_PARAM_SYS_ID_LIST = "delete_section_param_sys_id_list";
    
    /**
     * 更新分组名称 分组ID
     */
    private static final String UPDATE_SECTION_NAME_SECTION_ID = "update_section_name_section_id";
    
    /**
     * 更新分组名称 分组名称
     */
    private static final String UPDATE_SECTION_NAME_SECTION_NAME = "update_section_name_section_name";
    
    /**
     * 获取好友列表的时间戳
     */
    private static final String GET_FRIEND_LIST_TIMESTAMP = "get_friend_list_timestamp";
    
    /**
     * 移除成员--ID列表
     */
    private static final String REMOVE_MEMBER_SYSID_LIST = "remove_member_sysid_list";
    
    /**
     * 移除成员 -- 分组ID
     */
    private static final String REMOVE_MEMBER_SECTION_ID = "remove_member_section_id";
    
    /**
     * 分组管理基础url
     */
    private static final String URL_BASIC_CONTACT_SECTION = "v1/contactlists/me";
    
    /**
     * 附近查找好友的ＵＲＬ
     */
    private static final String URL_SEARCH_LOCATION = "/richlifeApp/devapp/ILocation";
    
    /**
     * 清除位置信息的URL
     */
    private static final String URL_REMOVE_LOCATION = "/richlifeApp/openIntf/ILM/removeUserLocation";
    
    /**
     * 封装JSON对象时需要用到的key定义
     */
    private static final String JSON_KEY_CONTACT_LIST_ID = "ContactListID";
    
    /**
     *移动到目的分组的ID
     */
    private static final String KEY_SECTION_NEW_ID = "desc";
    
    /**
     * 移动前好友的分组ID
     */
    private static final String KEY_SECTION_OLD_ID = "src";
    
    /**
     * 要移动分组的账号
     */
    private static final String KEY_SECTION_ACCOUNT = "accountId";
    
    /**
     * 封装JSON对象时需要用到的key定义-用于包含Name & Note
     */
    private static final String JSON_KEY_CONTACT_LIST = "ContactList";
    
    /**
     * 封装JSON对象时需要用到的key定义
     */
    private static final String JSON_KEY_NAME = "Name";
    
    /**
     * 封装JSON对象时需要用到的key定义
     */
    private static final String JSON_KEY_NOTE = "Note";
    
    /**
     * 封装JSON对象时需要用到的key定义-创建分组时使用
     */
    private static final String JSON_KEY_CONTACT_LIST_WITH_CONTACT_IDS = "ContactListWithContactIDs";
    
    /**
     * 获取好友列表信息的URL
     */
    private static final String URL_GET_FRIEND_LIST = "v1/profiles/all/friends/v2";
    
    /**
     * 查询更多好友的URL
     */
    private static final String URL_SEARCH_FRIEND_FROM_SERVER = "v1/profiles/all?";
    
    /**
     * 查询认识的好友的URL
     */
    private static final String URL_ERARCH_MAYBE_KNOWN_PERSON = "/v1/profiles/all/suggest/v2";
    
    /**
     * 删除分组url
     */
    private static final String URL_DEL_SECTION = "/del/multi?DelMode=0";
    
    /**
     * 把成员移除分组
     */
    private static final String URL_REMOVE_MEMBER_FROM_SECTION = "/del";
    
    /**
     * 封装JSON对象时需要用到的key定义
     */
    private static final String JSON_KEY_CONTACT_LIST_IDS = "ContactListIDs";
    
    /**
     * 封装JSON对象时需要用到的key定义-更新分组时使用
     */
    private static final String JSON_KEY_CONTACT_LIST_WITH_BOTH_IDS = "ContactListWithBothIDs";
    
    /**
     * account key
     */
    private static final String ACCOUNT_KEY = "account";
    
    /**
     * 经度key
     */
    private static final String LONGITUDE_KEY = "longitude";
    
    /**
     * 纬度key
     */
    private static final String LATITUDE_KEY = "latitude";
    
    /**
     * 根据action获取对应操作的Url<BR>
     * 重写父类的方法，被父类调用
     *      * @param action
     *      action标识
     * @param sendData
     *      发送的数据对象
     * @return
     *      Url字符串
     * @see com.huawei.basic.android.im.component.net.http.RequestAdapter#getUrl(int, java.util.Map)
     */
    
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        if (sendData == null)
        {
            Logger.d(TAG, "getUrl -------> sendData is null");
        }
        switch (action)
        {
            case ACTION_GET_FRIEND_LIST:
            {
                String url = getBasicUrl() + URL_GET_FRIEND_LIST;
                Logger.e(TAG, "url >> " + url);
                if (sendData != null
                        && sendData.get(UserConfigModel.GET_FRIEND_LIST_TIMESTAMP) != null)
                {
                    url = url
                            + "?"
                            + (String) sendData.get(UserConfigModel.GET_FRIEND_LIST_TIMESTAMP);
                }
                return url;
            }
            case ACTION_SEARCH_FRIEND_FROM_SERVER:
            {
                int startIndex = (Integer) sendData.get("start");
                int count = (Integer) sendData.get("count");
                String url = getBasicUrl() + URL_SEARCH_FRIEND_FROM_SERVER
                        + "Count=" + count + "&StartIndex=" + startIndex
                        + "&RstMode=1";
                return url;
            }
            case ACTION_SERARCH_MAYBE_KNOWN_PERSON:
                return getBasicUrl() + URL_ERARCH_MAYBE_KNOWN_PERSON;
                
            case ACTION_SEARCH_LOCATION:
                Logger.d(TAG, "Location URL---->URL:"
                        + (FusionConfig.getInstance()
                                .getAasResult()
                                .getRifurl() + URL_SEARCH_LOCATION));
                
                Logger.d(TAG, "OSEUrl  ="
                        + FusionConfig.getInstance().getAasResult().getRifurl());
                
                Logger.d(TAG, "BasicUrl  ="
                        + FusionConfig.getInstance()
                                .getAasResult()
                                .getCabgroupurl());
                
                return FusionConfig.getInstance().getAasResult().getRifurl()
                        + URL_SEARCH_LOCATION;
            case ACTION_REMOVE_LOCATION:
                Logger.d(TAG, "Remove Location URL---->URL:"
                        + (FusionConfig.getInstance()
                                .getAasResult()
                                .getRifurl() + URL_SEARCH_LOCATION));
                return FusionConfig.getInstance().getAasResult().getRifurl()
                        + URL_REMOVE_LOCATION;
                
            case ACTION_ADD_SECTION:
                return getBasicUrl() + URL_BASIC_CONTACT_SECTION;
            case ACTION_UPDATE_SECTION_NAME_ACTION:
                return getBasicUrl() + URL_BASIC_CONTACT_SECTION;
            case ACTION_DELETE_SECTION:
                return getBasicUrl() + URL_BASIC_CONTACT_SECTION
                        + URL_DEL_SECTION;
            case ACTION_ADD_CONTACTS_TO_SECTION:
                return getBasicUrl()
                        + URL_BASIC_CONTACT_SECTION
                        + "/"
                        + sendData.get(FriendManager.DELETE_SECTION_PARAM_SECTION_ID);
            case ACTION_MOVE_TO_SECTION:
                //                dest移动分组后的ID，src之前的ID
                return getBasicUrl() + URL_BASIC_CONTACT_SECTION + "/"
                        + sendData.get(FriendManager.KEY_SECTION_NEW_ID)
                        + "?SrcListID="
                        + sendData.get(FriendManager.KEY_SECTION_OLD_ID);
            case ACTION_REMOVE_MEMBER_FROM_SECTION:
                return getBasicUrl() + URL_BASIC_CONTACT_SECTION
                        + URL_REMOVE_MEMBER_FROM_SECTION + "/"
                        + sendData.get(REMOVE_MEMBER_SECTION_ID);
        }
        return null;
    }
    
    /**
     * 根据Action获取请求的Body<BR>
     * @param action
     *      action标识
     * @param sendData
     *      发送的数据对象
     * @return
     *      Body的字符串
     * @see com.huawei.basic.android.im.component.net.http.RequestAdapter#getBody(int, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        if (sendData == null)
        {
            Logger.d(TAG, "getBody -----------> senData is null");
        }
        switch (action)
        {
            case ACTION_GET_FRIEND_LIST:
                return null;
            case ACTION_SEARCH_FRIEND_FROM_SERVER:
                return getSearchFilterJson(sendData);
            case ACTION_SERARCH_MAYBE_KNOWN_PERSON:
            {
                JSONObject jsonObj = new JSONObject();
                try
                {
                    jsonObj.put("NeedReason", 1);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return jsonObj.toString();
            }
            case ACTION_SEARCH_LOCATION:
                Logger.d(TAG, "Location SEND body=="
                        + getNearUserBody(sendData));
                return getNearUserBody(sendData);
                
            case ACTION_REMOVE_LOCATION:

                Logger.d(TAG, "Remove location SEND data=="
                        + getRemoveLocationBody(sendData));
                return getRemoveLocationBody(sendData);
            case ACTION_ADD_SECTION:
            {
                return jsonOfNewSection((String) sendData.get(ADD_SECTION_PARAM_NAME),
                        (String) sendData.get(ADD_SECTION_PARAM_NOTES),
                        (ArrayList<String>) sendData.get(ADD_SECTION_PARAM_SYS_ID_LIST));
            }
            case ACTION_UPDATE_SECTION_NAME_ACTION:
                Logger.d(TAG,
                        "jsonOfUpdateSection SEND data==" + sendData.toString());
                return jsonOfUpdateSection((String) sendData.get(UPDATE_SECTION_NAME_SECTION_ID),
                        (String) sendData.get(UPDATE_SECTION_NAME_SECTION_NAME));
            case ACTION_DELETE_SECTION:
            {
                Logger.d(TAG,
                        "jsonOfDeleteSection SEND data==" + sendData.toString());
                return jsonOfDeleteSection((String) sendData.get(DELETE_SECTION_PARAM_SECTION_ID));
            }
            case ACTION_REMOVE_MEMBER_FROM_SECTION:
            {
                Logger.d(TAG, "removeMember ------> " + sendData.toString());
                return jsonOfAddOrRemoveFriends((ArrayList<String>) sendData.get(FriendManager.REMOVE_MEMBER_SYSID_LIST));
            }
            case ACTION_ADD_CONTACTS_TO_SECTION:
                Logger.d(TAG, "jsonOfAddContactToSection SEND data=="
                        + sendData.toString());
                return jsonOfAddOrRemoveFriends((ArrayList<String>) sendData.get(FriendManager.ADD_SECTION_PARAM_SYS_ID_LIST));
            case ACTION_MOVE_TO_SECTION:
                Logger.d(TAG,
                        "jsonOf MoveToSection SEND data=="
                                + sendData.toString());
                return jsonOfMoveSection((String) sendData.get("accountId"));
            default:
                break;
        }
        return null;
    }
    
    /**
     * 和服务器交互响应后解析Response为返回的数据对象<BR>
     * @param action
     *      action标识
     * @param sendData
     *      发送的数据对象
     * @param response response
     *      返回的response对象
     * @return 
     *      解析的数据对象
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#handleData(int, java.util.Map, java.lang.String)
     */
    
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        Logger.d(TAG, "handleData---->responseData:" + response.getData());
        
        switch (action)
        {
            case ACTION_GET_FRIEND_LIST:
                return parseContactSectionList(response.getData());
            case ACTION_SEARCH_FRIEND_FROM_SERVER:
                return parseSearchResult(response.getData());
            case ACTION_SERARCH_MAYBE_KNOWN_PERSON:
                return parseMaybeKnownPerson(response.getData());
            case ACTION_ADD_SECTION:
                return parseCreateContactSection(response.getData());
            case ACTION_UPDATE_SECTION_NAME_ACTION:
            case ACTION_DELETE_SECTION:
            case ACTION_ADD_CONTACTS_TO_SECTION:
            case ACTION_REMOVE_MEMBER_FROM_SECTION:
                return parseContactListsVersion(response.getData());
            case ACTION_MOVE_TO_SECTION:
                // 只需要把时间戳存到数据库中
                Logger.e(TAG, "response.getData()===" + response.getData());
                return response.getData();
                
            case ACTION_SEARCH_LOCATION:
                return parseLocationResult(response.getData());
            case ACTION_REMOVE_LOCATION:
                return parseRemoveResult(response.getData());
                
        }
        return null;
    }
    
    //解析服务器返回的数据
    private String parseContactListsVersion(String data)
    {
        try
        {
            if (data != null)
            {
                JSONObject obj = new JSONObject(data);
                if (obj.has("ContactListsVersion"))
                {
                    //                    updateContactSectionDB(action,sendData);
                    //                    String contactListsVersion;
                    Log.e(TAG,
                            "ContactListsVersion===="
                                    + obj.getString("ContactListsVersion"));
                    return obj.getString("ContactListsVersion");
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * 移动分组拼装request<BR>
     * [功能详细描述]
     * @return
     */
    private String jsonOfMoveSection(String accountId)
    {
        
        //        String id = (String) getSendData().get("id");
        
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            ja.put(accountId);
            jo.put("AccountIDs", ja);
            return jo.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 删除好友分组<BR>
     * [功能详细描述]
     * 
     * @return JSON字符串
     */
    private String jsonOfAddOrRemoveFriends(ArrayList<String> idList)
    {
        try
        {
            JSONObject jo = new JSONObject();
            jo.put(JSON_KEY_CONTACT_IDS, jsonOfContactIDs(idList));
            return jo.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        
    }
    
    /**
     * 删除好友分组<BR>
     * [功能详细描述]
     * 
     * @return JSON字符串
     */
    private String jsonOfDeleteSection(String sectionId)
    {
        if (null == sectionId)
        {
            return null;
        }
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            ja.put(sectionId);
            jo.put(JSON_KEY_CONTACT_LIST_IDS, ja);
            return jo.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 更新好友分组<BR>
     * [功能详细描述]
     * 
     * @return JSON字符串FriendManager.UPDATE_SECTION_NAME_ACTION
     */
    private String jsonOfUpdateSection(String sectionId, String sectionName)
    {
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray jaSectionList = new JSONArray();
            JSONObject joSection = new JSONObject();
            joSection.put(JSON_KEY_CONTACT_LIST,
                    jsonOfContactSection(sectionName, null));
            joSection.put(JSON_KEY_CONTACT_IDS, jsonOfContactIDs(null));
            joSection.put(JSON_KEY_CONTACT_LIST_ID, sectionId);
            jaSectionList.put(joSection);
            jo.put(JSON_KEY_CONTACT_LIST_WITH_BOTH_IDS, jaSectionList);
            return jo.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * 生成好友id的JSON数组<BR>
     * [功能详细描述]
     * 
     * @param contactInfoList 好友列表
     * @return JSON数组
     */
    private JSONArray jsonOfContactIDs(ArrayList<String> idList)
    {
        JSONArray jaContactIDs = new JSONArray();
        if (idList != null)
        {
            for (String id : idList)
            {
                jaContactIDs.put(id);
            }
        }
        return jaContactIDs;
    }
    
    /**
     * 
     * 生成分组属性的JSON字符串<BR>
     * 组装结果为：
     * <p>
     * {“Name”:”New Friends”,”Note”:”All my new friends” }
     * 
     * @param contactSection 分组对象
     * @return JSON对象
     * @throws JSONException
     */
    private JSONObject jsonOfContactSection(String sectionName,
            String sectionNote) throws JSONException
    {
        JSONObject joContactSection = new JSONObject();
        if (sectionName != null)
        {
            joContactSection.put(JSON_KEY_NAME, sectionName);
        }
        if (sectionNote != null)
        {
            joContactSection.put(JSON_KEY_NOTE, sectionNote);
        }
        return joContactSection;
    }
    
    /**
     * 解析认识的人的查询返回数据<BR>
     * @param data
     *      查询响应返回的数据字符串
     * @return
     *      通讯录集合
     */
    private List<ContactInfoModel> parseMaybeKnownPerson(String data)
    {
        List<ContactInfoModel> result = null;
        if (data != null)
        {
            try
            {
                JSONObject rootJsonObj = new JSONObject(data);
                result = new ArrayList<ContactInfoModel>();
                
                if (rootJsonObj.has("ProfileList"))
                {
                    JSONArray profileListArray = rootJsonObj.getJSONArray("ProfileList");
                    JSONObject profileListObj = null;
                    int length = profileListArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        profileListObj = profileListArray.getJSONObject(i);
                        ContactInfoModel contactInfo = null;
                        if (profileListObj.has("U"))
                        {
                            JSONObject uObj = profileListObj.getJSONObject("U");
                            contactInfo = parseContactInfo(uObj);
                        }
                        else
                        {
                            contactInfo = new ContactInfoModel();
                        }
                        if (profileListObj.has("SRs"))
                        {
                            JSONArray srsArray = profileListObj.getJSONArray("SRs");
                            JSONObject srsObj = null;
                            for (int j = 0; j < srsArray.length(); j++)
                            {
                                srsObj = srsArray.getJSONObject(j);
                                if (srsObj.has("T"))
                                {
                                    contactInfo.setSrsType(srsObj.getInt("T"));
                                }
                                //                                if (srsObj.has("V"))
                                //                                {
                                //                                    //暂时不处理
                                //                                }
                                if (srsObj.has("CFAs"))
                                {
                                    JSONArray cfasArray = srsObj.getJSONArray("CFAs");
                                    if (cfasArray != null)
                                    {
                                        contactInfo.setSrsCommonNum(cfasArray.length());
                                        
                                    }
                                }
                            }
                        }
                        result.add(contactInfo);
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
     * 解析根据条件查询好友的结果<BR>
    * @param data
     *      查询响应返回的数据字符串
     * @return
     *      通讯录集合和总记录条数的map
     */
    private HashMap<String, Object> parseSearchResult(String data)
    {
        HashMap<String, Object> maps = null;
        List<ContactInfoModel> result = null;
        if (data != null)
        {
            try
            {
                maps = new HashMap<String, Object>();
                JSONObject rootJsonObj = new JSONObject(data);
                if (rootJsonObj.has("ProfileList"))
                {
                    JSONArray listArray = rootJsonObj.getJSONArray("ProfileList");
                    result = new ArrayList<ContactInfoModel>();
                    for (int i = 0; i < listArray.length(); i++)
                    {
                        ContactInfoModel contactInfo = parseContactInfo(listArray.getJSONObject(i));
                        result.add(contactInfo);
                    }
                    maps.put("lists", result);
                }
                if (rootJsonObj.has("TotalResults"))
                {
                    int totalCount = rootJsonObj.getInt("TotalResults");
                    maps.put("total", totalCount);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return maps;
    }
    
    /**
     * 
     * 解析附近查找的返回结果<BR>
     * [功能详细描述]
     * @param data
     * @return
     */
    private List<ContactInfoModel> parseLocationResult(String data)
    {
        XmlParser parser = null;
        NearUserModel model = null;
        try
        {
            parser = new XmlParser();
            model = parser.parseXmlString(NearUserModel.class, data);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "*******Raul******" + e.toString());
        }
        if (model != null && model.getResultCode() == 0)
        {
            if (model.getUserLocationList() == null
                    || model.getListLength() == 0)
            {
                // 在返回值为0时, 有可能没有对应的UserLocationList, 此时中断处理
                return null;
            }
            
            List<NearUserModel.UserLocationInfo> list = model.getUserLocationList();
            List<ContactInfoModel> cims = new ArrayList<ContactInfoModel>(
                    list.size());
            ContactInfoModel cim = null;
            for (NearUserModel.UserLocationInfo info : list)
            {
                cim = new ContactInfoModel();
                cim.setFriendUserId(info.getAccount());
                cim.setNickName(info.getDisplayName());
                cim.setSignature(info.getNote());
                cim.setFaceUrl(info.getUserLogoURL());
                cim.setSrsCommonNum(info.getUserDistance());
                cim.setGender(info.getGender());
                cim.setFriendStatus(info.getFriendStatus());
                cims.add(cim);
            }
            return cims;
        }
        return null;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param data
     * @return
     */
    private NearUserModel parseRemoveResult(String data)
    {
        XmlParser parser = null;
        NearUserModel model = null;
        try
        {
            parser = new XmlParser();
            model = parser.parseXmlString(NearUserModel.class, data);
        }
        catch (Exception e)
        {
            Logger.e(TAG, e.toString());
            return null;
        }
        return model;
    }
    
    /**
     * 获取前缀Url<BR>
     * @return
     *      服务器地址的前缀Url
     */
    private String getBasicUrl()
    {
        return FusionConfig.getInstance().getAasResult().getCabgroupurl();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param action ACTION
     * @return ContentType
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getContentType(int)
     */
    @Override
    protected ContentType getContentType(int action)
    {
        switch (action)
        {
            case ACTION_SEARCH_LOCATION:
            case ACTION_REMOVE_LOCATION:
                return ContentType.XML;
        }
        return super.getContentType(action);
    }
    
    /**
     * 获取查询过滤器的Json对象字符串<BR>
     * @param sendData
     *      发送的数据对象
     * @return
     *      过滤器Json对象的字符串
     */
    @SuppressWarnings("unchecked")
    private String getSearchFilterJson(Map<String, Object> sendData)
    {
        String body = null;
        try
        {
            JSONObject searchObj = new JSONObject();
            JSONArray filterArray = new JSONArray();
            ArrayList<SearchFilter> filters = (ArrayList<SearchFilter>) sendData.get("filters");
            JSONObject subObj = null;
            if (filters != null && filters.size() > 0)
            {
                for (int i = 0; i < filters.size(); i++)
                {
                    subObj = new JSONObject();
                    subObj.put("Field", filters.get(i).getField());
                    subObj.put("Value", filters.get(i).getValue());
                    subObj.put("Mode", filters.get(i).getMode());
                    filterArray.put(subObj);
                }
                searchObj.put("Filters", filterArray);
                
            }
            subObj = new JSONObject();
            subObj.put("Field", 2);
            subObj.put("Order", 1);
            searchObj.put("SortMode", subObj);
            
            searchObj.put("Logical", 2);
            body = searchObj.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return body;
    }
    
    /**
     * 获取request属性的集合<BR>
     * @param action
     *      action标识
     * @return
     *      request属性值的集合
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestProperties(int)
     */
    
    @Override
    protected List<NameValuePair> getRequestProperties(int action)
    {
        List<NameValuePair> list = super.getRequestProperties(action);
        if (ACTION_SEARCH_LOCATION == action
                || ACTION_REMOVE_LOCATION == action)
        {
            list.add(new BasicNameValuePair("Authorization",
                    FusionConfig.getInstance().getOseReqAuthorization()));
        }
        else
        {
            list.add(new BasicNameValuePair("Authorization",
                    FusionConfig.getInstance().getCabReqAuthorization()));
        }
        return list;
    }
    
    /**
     * 获取request请求方式<BR>
     * @param action
     *      action标识
     * @return RequestMethod
     *      request请求方式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        switch (action)
        {
            case ACTION_ADD_SECTION:
            case ACTION_GET_FRIEND_LIST:
                return RequestMethod.GET;
            case ACTION_UPDATE_SECTION_NAME_ACTION:
            case ACTION_MOVE_TO_SECTION:
                return RequestMethod.PUT;
            case ACTION_SEARCH_LOCATION:
            case ACTION_REMOVE_LOCATION:
                return RequestMethod.POST;
        }
        return super.getRequestMethod(action);
    }
    
    /**
     * 解析联系人分组<BR>
     * @param jsonData jsonData
     * @return ArrayList<Object> 
     * 联系人分组列表和时间戳(以列表的方式返回，第一条为分组数据，第二个为好友的ID列表，第三条为时间戳，第四个为找朋友小助手列表)
     */
    private ArrayList<Object> parseContactSectionList(String jsonData)
    {
        ArrayList<Object> responseData = new ArrayList<Object>();
        ArrayList<ContactSectionModel> contactSectionList = null;
        ArrayList<String> userSysIdList = null;
        ArrayList<String> contactSectionIdList = null;
        if (jsonData != null)
        {
            try
            {
                JSONObject rootJsonObj = new JSONObject(jsonData);
                contactSectionList = new ArrayList<ContactSectionModel>();
                if (rootJsonObj.has("LMs"))
                {
                    userSysIdList = new ArrayList<String>();
                    contactSectionIdList = new ArrayList<String>();
                    JSONArray lmsArray = rootJsonObj.getJSONArray("LMs");
                    ContactSectionModel contactSection;
                    JSONObject lmsObj;
                    for (int i = 0; i < lmsArray.length(); i++)
                    {
                        lmsObj = lmsArray.getJSONObject(i);
                        // 创建ContactSection时同时给其初始化数据
                        contactSection = new ContactSectionModel();
                        contactSection.setFriendList(new ArrayList<ContactInfoModel>());
                        contactSection.setFriendSysIds(new ArrayList<String>());
                        if (lmsObj.has("ContactList"))
                        {
                            JSONObject contactListObj = lmsObj.getJSONObject("ContactList");
                            if (contactListObj.has("Name"))
                            {
                                contactSection.setName(contactListObj.getString("Name"));
                            }
                            if (contactListObj.has("Note"))
                            {
                                contactSection.setNotes(contactListObj.getString("Note"));
                            }
                        }
                        if (lmsObj.has("ContactListID"))
                        {
                            if (!StringUtil.isNullOrEmpty(lmsObj.getString("ContactListID")))
                            {
                                contactSection.setContactSectionId(lmsObj.getString("ContactListID"));
                            }
                            else
                            {
                                contactSection.setContactSectionId(ContactSectionModel.DEFAULT_SECTION_ID);
                            }
                            
                            contactSectionIdList.add(contactSection.getContactSectionId());
                        }
                        if (lmsObj.has("FriendIDs"))
                        {
                            JSONArray friendIdArray = lmsObj.getJSONArray("FriendIDs");
                            for (int friendIdIndex = 0; friendIdIndex < friendIdArray.length(); friendIdIndex++)
                            {
                                String friendUserId = friendIdArray.getString(friendIdIndex);
                                if (friendUserId != null)
                                {
                                    // 好友的sysidlist
                                    userSysIdList.add(friendUserId);
                                    contactSection.getFriendSysIds()
                                            .add(friendUserId);
                                }
                            }
                        }
                        contactSectionList.add(contactSection);
                    }
                }
                
                //好友列表数据
                if (rootJsonObj.has("FPs"))
                {
                    JSONArray fpsArray = rootJsonObj.getJSONArray("FPs");
                    JSONObject fpsObj;
                    for (int i = 0; i < fpsArray.length(); i++)
                    {
                        fpsObj = fpsArray.getJSONObject(i);
                        ContactInfoModel contactInfo = parseContactInfo(fpsObj);
                        for (ContactSectionModel contactSection : contactSectionList)
                        {
                            if (contactSection.getFriendSysIds()
                                    .contains(contactInfo.getFriendSysId()))
                            {
                                contactSection.getFriendList().add(contactInfo);
                                contactInfo.setContactSectionId(contactSection.getContactSectionId());
                                break;
                            }
                        }
                    }
                }
                
                //把分组数据放入responseData 分组数据 第一个 
                responseData.add(contactSectionList);
                
                //新的好友列表（如果分组发生变化（删除好友，或者增加分组））
                responseData.add(userSysIdList);
                
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
                
                //时间戳
                responseData.add(timestamp);
                
                //解析找朋友小助手相关的信息
                responseData.add(parseFriendManagerData(rootJsonObj));
                
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return responseData;
    }
    
    /**
     * 解析好友详情数据<BR>
     * @param json fpsObj
     *      好友的Json对象
     * @param contactInfo contactInfo
     *      装载的contactInfo
     */
    private ContactInfoModel parseContactInfo(JSONObject json)
    {
        ContactInfoModel contactInfo = new ContactInfoModel();
        try
        {
            if (json.has("ID"))
            {
                contactInfo.setFriendSysId(json.getString("ID"));
            }
            if (json.has("A"))
            {
                contactInfo.setFriendUserId(json.getString("A"));
            }
            if (json.has("DN"))
            {
                contactInfo.setNickName(json.getString("DN"));
            }
            if (json.has("PU"))
            {
                contactInfo.setFaceUrl(json.getString("PU"));
            }
            if (json.has("N"))
            {
                contactInfo.setSignature(json.getString("N"));
            }
            if (json.has("M"))
            {
                JSONObject memoObj = json.getJSONObject("M");
                if (memoObj.has("DN"))
                {
                    contactInfo.setMemoName(memoObj.getString("DN"));
                }
                if (memoObj.has("PNs"))
                {
                    JSONArray phonenums = memoObj.getJSONArray("PNs");
                    List<String> phoneslist = new ArrayList<String>();
                    for (int j = 0; j < phonenums.length(); j++)
                    {
                        phoneslist.add(phonenums.getString(j));
                    }
                    contactInfo.setMemoPhones(phoneslist);
                }
                if (memoObj.has("Es"))
                {
                    JSONArray emails = memoObj.getJSONArray("Es");
                    List<String> emailslist = new ArrayList<String>();
                    for (int j = 0; j < emails.length(); j++)
                    {
                        emailslist.add(emails.getString(j));
                    }
                    contactInfo.setMemoEmails(emailslist);
                }
            }
            if (json.has("S"))
            {
                contactInfo.setOnline(json.getInt("S"));
            }
            if (json.has("HL"))
            {
                contactInfo.setHomeLocation(json.getInt("HL"));
            }
            if (json.has("FP"))
            {
                contactInfo.setFriendPrivacy(json.getInt("FP"));
            }
            if (json.has("ACF"))
            {
                contactInfo.setAutoConfirmFriend(json.getInt("ACF"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return contactInfo;
    }
    
    /**
     * 根据ID查询获取的更多的好友<BR>
     * @param startIndex
     *      查询开始下标
     * @param recordCount
     *      返回记录条数
     * @param searchValue
     *      查询关键字
     * @param httpListener
     *      远程回调监听 
     */
    public void loadMoreFriendById(int startIndex, int recordCount,
            String searchValue, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        List<SearchFilter> filters = new ArrayList<SearchFilter>();
        
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setField(11);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(1);
        filters.add(searchFilter);
        searchFilter = new SearchFilter();
        // 匹配手机号码
        searchFilter.setField(15);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        //modified by liying00124251 for adding filter of phonenum begin
        filters.add(searchFilter);
        //modified by liying00124251 for adding filter of phonenum begin
        
        sendData.put("filters", filters);
        sendData.put("start", startIndex);
        sendData.put("count", recordCount);
        super.send(ACTION_SEARCH_FRIEND_FROM_SERVER, sendData, httpListener);
        
    }
    
    /**
     * 根据详细信息查询获取的更多的好友<BR>
     * @param startIndex
     *      查询开始下标
     * @param recordCount
     *      返回记录条数
     * @param searchValue
     *      查询关键字
     * @param httpListener
     *      远程回调监听 
     */
    public void loadMoreFriendByDetail(int startIndex, int recordCount,
            String searchValue, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        List<SearchFilter> filters = new ArrayList<SearchFilter>();
        
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setField(0);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        filters.add(searchFilter);
        // 匹配学校名称
        searchFilter = new SearchFilter();
        searchFilter.setField(4);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        filters.add(searchFilter);
        // 匹配公司名称
        searchFilter = new SearchFilter();
        searchFilter.setField(5);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        filters.add(searchFilter);
        // 匹配displayname
        searchFilter = new SearchFilter();
        searchFilter.setField(10);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        filters.add(searchFilter);
        // 匹配爱好
        searchFilter = new SearchFilter();
        searchFilter.setField(14);
        searchFilter.setValue(searchValue);
        searchFilter.setMode(0);
        filters.add(searchFilter);
        
        sendData.put("filters", filters);
        sendData.put("start", startIndex);
        sendData.put("count", recordCount);
        super.send(ACTION_SEARCH_FRIEND_FROM_SERVER, sendData, httpListener);
    }
    
    /**
     * 根据认识的人加载好友<BR>
     * @param httpListener 
     *      远程回调监听
     */
    public void loadMoreFriendByKnownPerson(IHttpListener httpListener)
    {
        super.send(ACTION_SERARCH_MAYBE_KNOWN_PERSON, null, httpListener);
    }
    
    /**
     * 
     * 附近查找加载好友<BR>
     * [功能详细描述]
     * @param lat 经度
     * @param lon 维度
     * @param httpListener 联网监听
     */
    public void loadMoreFriendByLocation(double lat, double lon,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.ACCOUNT_KEY, FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        sendData.put(FriendManager.LATITUDE_KEY, String.valueOf(lat));
        sendData.put(FriendManager.LONGITUDE_KEY, String.valueOf(lon));
        super.send(ACTION_SEARCH_LOCATION, sendData, httpListener);
    }
    
    /**
     * 
     * 清除位置信息<BR>
     * [功能详细描述]
     * @param httpListener 联网监听
     */
    public void removeLocationInfo(IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.ACCOUNT_KEY, FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        Logger.d(TAG, "sendData =" + sendData.toString());
        super.send(ACTION_REMOVE_LOCATION, sendData, httpListener);
    }
    
    /**
     * 增加分组的json串<BR>
     * @param sectionName sectionName
     * @param sectionNotes sectionNotes
     * @param friendSysList friendSysList
     * @return 增加section的String
     */
    private String jsonOfNewSection(String sectionName, String sectionNotes,
            ArrayList<String> friendSysList)
    {
        try
        {
            JSONObject jo = new JSONObject();
            JSONArray jaSectionList = new JSONArray();
            JSONObject joContactSection = new JSONObject();
            
            JSONObject tempJo = new JSONObject();
            if (sectionName != null)
            {
                tempJo.put(JSON_KEY_NAME, sectionName);
            }
            if (sectionNotes != null)
            {
                tempJo.put(JSON_KEY_NOTE, sectionNotes);
            }
            joContactSection.put(JSON_KEY_CONTACT_LIST, tempJo);
            
            JSONArray jaContactIDs = new JSONArray();
            if (friendSysList != null)
            {
                for (String sysId : friendSysList)
                {
                    jaContactIDs.put(sysId);
                }
            }
            joContactSection.put(JSON_KEY_CONTACT_IDS, jaContactIDs);
            jaSectionList.put(joContactSection);
            jo.put(JSON_KEY_CONTACT_LIST_WITH_CONTACT_IDS, jaSectionList);
            return jo.toString();
        }
        catch (JSONException e)
        {
            Logger.e(TAG, "jsonOfNewSection-------->" + e.getStackTrace());
        }
        return null;
    }
    
    /**
     * 找朋友json对象数据解析<BR>
     * @param obj object
     * @throws JSONException 
     */
    private ArrayList<FriendManagerModel> parseFriendManagerData(
            JSONObject rootObj) throws JSONException
    {
        ArrayList<FriendManagerModel> list = new ArrayList<FriendManagerModel>();
        
        //时间递增
        int timer = 0;
        
        //subs节点下的数据为加别人好友等待验证的
        if (rootObj.has("Subs"))
        {
            JSONArray subsArray = rootObj.getJSONArray("Subs");
            for (int i = 0; i < subsArray.length(); i++)
            {
                JSONObject object = subsArray.getJSONObject(i);
                FriendManagerModel model = new FriendManagerModel();
                if (object.has("LP"))
                {
                    JSONObject lpObj = object.getJSONObject("LP");
                    if (lpObj.has("ID"))
                    {
                        model.setFriendSysId(lpObj.getString("ID"));
                    }
                    if (lpObj.has("A"))
                    {
                        model.setFriendUserId(lpObj.getString("A"));
                    }
                    if (lpObj.has("DN"))
                    {
                        model.setNickName(lpObj.getString("DN"));
                    }
                    if (lpObj.has("PU"))
                    {
                        model.setFaceUrl(lpObj.getString("PU"));
                    }
                    if (lpObj.has("N"))
                    {
                        model.setSignature(lpObj.getString("N"));
                    }
                }
                //                if (object.has("CT"))
                //                {
                //                    //时间戳数据
                //                }
                model.setSubService(FriendManagerModel.SUBSERVICE_ADD_FRIEND);
                model.setStatus(FriendManagerModel.STATUS_WAITTING);
                model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(new Date(
                        System.currentTimeMillis() + (timer++ * 1000))));
                list.add(model);
            }
        }
        //由别人发起加我为好友的数据列表
        if (rootObj.has("Wats"))
        {
            //            "Wats":[{"LP":{"ID":"234","A":"10148","DN":"qq","FP":3,"ACF":2},"CT":"2012-03-07T12:56:02.438Z"}]
            JSONArray watsArray = rootObj.getJSONArray("Wats");
            for (int i = 0; i < watsArray.length(); i++)
            {
                JSONObject object = watsArray.getJSONObject(i);
                JSONObject lpObj = object.getJSONObject("LP");
                FriendManagerModel model = new FriendManagerModel();
                if (lpObj.has("ID"))
                {
                    model.setFriendSysId(lpObj.getString("ID"));
                }
                if (lpObj.has("A"))
                {
                    model.setFriendUserId(lpObj.getString("A"));
                }
                if (lpObj.has("DN"))
                {
                    model.setNickName(lpObj.getString("DN"));
                }
                if (lpObj.has("PU"))
                {
                    model.setFaceUrl(lpObj.getString("PU"));
                }
                if (lpObj.has("N"))
                {
                    model.setSignature(lpObj.getString("N"));
                }
                model.setSubService(FriendManagerModel.SUBSERVICE_BE_ADD);
                model.setStatus(FriendManagerModel.STATUS_WAITTING);
                model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(new Date(
                        System.currentTimeMillis() + (timer++ * 1000))));
                list.add(model);
            }
        }
        return list;
    }
    
    /**
     * 创建分组成功直接更新数据库
     * 
     * @param data respone返回数据
     */
    private String parseCreateContactSection(String data)
    {
        /**
         * {"Result":{"resultCode" :"0","resultDesc":"Operation succeeds"
         * },"ContactListIDs":["1508"],"AddrBookVersion"
         * :"20111219-00004","ContactListsVersion"
         * :"20111219-00178","ContactsVersion":"20111216-00047"}
         */
        
        //TODO 缺少时间戳处理，发出添加分组请求后，服务器会返分组，本地应该根据这个分组展示出来
        if (data != null)
        {
            try
            {
                JSONObject rootJsonObj = new JSONObject(data);
                if (rootJsonObj.has("ContactListIDs"))
                {
                    JSONArray contactListIDs = rootJsonObj.getJSONArray("ContactListIDs");
                    // 只考虑单个创建的情况,服务器支持多个分组一起创建
                    return contactListIDs.getString(0);
                }
                //时间戳数据
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 搜索传递的关键字对象
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Feb 16, 2012]
     */
    public class SearchFilter
    {
        
        /**
         * Field表示过滤的字段名，定义如下<Br>
         * 0: 用户信息的FirstName字段<Br>
         * 1: 用户信息的MiddleName字段<Br>
         * 2: 用户信息的 LastName字段<Br>
         * 3: 用户信息的 alternate name字段<Br>
         * 4: 用户信息的 school name字段<Br>
         * 5: 用户信息的 company name字段<Br>
         * 6: 用户信息的 city字段<Br>
         * 9: 用户信息的full name 或 display
         * name字段（在同一搜索请求中，不要与FirstName、
         * MiddleName、LastName、display name字段同时出现）<Br>
         * 10: 用户信息的display name字段<Br>
         * 11: 用户账号，即联通即时通信ID<Br>
         * 12: 用户信息的Gender字段<Br>
         * 13: 用户信息的Age字段<Br>
         * 14: 用户信息的Hobby字段<Br>
         * 15: 用户信息的PrimaryMobile字段<Br>
         */
        private int field;
        
        /**
         * 字段名具体的值
         */
        private String value;
        
        /**
         * Mode表示过滤模式，定义如下：<Br>
         * 非年龄字段搜索时，有效取值为：<Br>
         * 0: 包含. <Br>
         * 1: 相等. <Br>
         * 2: 以××开头. <Br>
         * 3: 存在. <Br>
         * 对于年龄字段搜索时，有效取值为：<Br>
         * 4: 小于, 只适用于搜索年龄。<Br>
         * 5: 大于, 只适用于搜索年龄<Br>
         * 6: 之间, 只适用于搜索年龄<Br>
         */
        private int mode;
        
        public int getField()
        {
            return field;
        }
        
        public void setField(int field)
        {
            this.field = field;
        }
        
        public String getValue()
        {
            return value;
        }
        
        public void setValue(String value)
        {
            this.value = value;
        }
        
        public int getMode()
        {
            return mode;
        }
        
        public void setMode(int mode)
        {
            this.mode = mode;
        }
        
    }
    
    /**
     * 根据时间戳获取好友列表<BR>
     * @param timeStamp timeStamp
     * @param httpListener httpListener
     */
    public void getAllContactList(String timeStamp, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        if (null != timeStamp)
        {
            //加入时间戳
            sendData.put(GET_FRIEND_LIST_TIMESTAMP, timeStamp);
        }
        super.send(ACTION_GET_FRIEND_LIST, sendData, httpListener);
    }
    
    /**
     * 添加分组
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param sectionName sectionName
     * @param sectionNotes sectionNotes
     * @param friendSysIdList friendSysIdList
     * @param httpListener httpListener
     */
    public void addSection(final String sectionName, final String sectionNotes,
            final ArrayList<String> friendSysIdList, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.ADD_SECTION_PARAM_NAME, sectionName);
        sendData.put(FriendManager.ADD_SECTION_PARAM_NOTES, sectionNotes);
        sendData.put(FriendManager.ADD_SECTION_PARAM_SYS_ID_LIST,
                friendSysIdList);
        super.send(ACTION_ADD_SECTION, sendData, httpListener);
    }
    
    /**
     * 修改组名
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param sectionName sectionName
     * @param sectionId sectionId
     * @param httpListener httpListener
     */
    public void updateSectionName(final String sectionName,
            final String sectionId, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.UPDATE_SECTION_NAME_SECTION_ID, sectionId);
        sendData.put(FriendManager.UPDATE_SECTION_NAME_SECTION_NAME,
                sectionName);
        super.send(ACTION_UPDATE_SECTION_NAME_ACTION, sendData, httpListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param sectionId sectionId
     * @param idList idList
     * @param httpListener httpListener
     */
    public void deleteSection(final String sectionId, ArrayList<String> idList,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.DELETE_SECTION_PARAM_SECTION_ID, sectionId);
        sendData.put(FriendManager.DELETE_SECTION_PARAM_SYS_ID_LIST, idList);
        super.send(ACTION_DELETE_SECTION, sendData, httpListener);
    }
    
    /**
     * 添加联系人到分组
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param sectionId sectionId
     * @param friendIdList friendIdList
     * @param httpListener httpListener
     */
    public void addContactsToSection(final String sectionId,
            final List<String> friendIdList, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.DELETE_SECTION_PARAM_SECTION_ID, sectionId);
        sendData.put(FriendManager.ADD_SECTION_PARAM_SYS_ID_LIST, friendIdList);
        super.send(ACTION_ADD_CONTACTS_TO_SECTION, sendData, httpListener);
    }
    
    /**
     * 分组移除成员
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param sysIdList sysIdList
     * @param sectionId sectionId
     * @param httpListener httpListener
     */
    public void removeMemberFromSection(final ArrayList<String> sysIdList,
            String sectionId, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.REMOVE_MEMBER_SYSID_LIST, sysIdList);
        sendData.put(FriendManager.REMOVE_MEMBER_SECTION_ID, sectionId);
        super.send(ACTION_REMOVE_MEMBER_FROM_SECTION, sendData, httpListener);
    }
    
    /**
     * 移动分组
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param accountId accountId
     * @param newSectionId newSectionId
     * @param oldSectionId oldSectionId
     * @param httpListener httpListener
     */
    public void removeToSection(final String accountId,
            final String newSectionId, final String oldSectionId,
            IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(FriendManager.KEY_SECTION_NEW_ID, newSectionId);
        sendData.put(FriendManager.KEY_SECTION_OLD_ID, oldSectionId);
        sendData.put(FriendManager.KEY_SECTION_ACCOUNT, accountId);
        super.send(ACTION_MOVE_TO_SECTION, sendData, httpListener);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param hiTalkID hiTalkID
     * @param contactType contactType
     * @param mContactInfoModel mContactInfoModel
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getContactInfoList(final String hiTalkID,
            final int contactType, final ContactInfoModel mContactInfoModel)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(hiTalkID);
        sendData.put("List", ids);
        sendData.put("contactType", contactType);
        if (mContactInfoModel != null)
        {
            // 获取本地数据最后修改时间
            String sLastUpdate = mContactInfoModel.getLastUpdate();
            
            // 发送服务器消息添加时间戳到
            sendData.put(ContactInfoManager.GET_CONTACT_INFO_TIMESTAMP,
                    sLastUpdate == null ? "" : sLastUpdate);
        }
        else
        {
            // 发送服务器消息添加时间戳到
            sendData.put(ContactInfoManager.GET_CONTACT_INFO_TIMESTAMP, null);
        }
        return sendData;
    }
    
    /**
     * 
     * 附近查询请求Body<BR>
     * @param sendParams
     * @return
     */
    private String getNearUserBody(Map<String, Object> sendParams)
    {
        String account = (String) sendParams.get(ACCOUNT_KEY);
        String longitude = (String) sendParams.get(LONGITUDE_KEY);
        String latitude = (String) sendParams.get(LATITUDE_KEY);
        
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<queryNearbyUser>");
        xml.append("<account>").append(account).append("</account>");
        xml.append("<longitude>").append(longitude).append("</longitude>");
        xml.append("<latitude>").append(latitude).append("</latitude>");
        
        xml.append("</queryNearbyUser>");
        return xml.toString();
    }
    
    /**
     * 
     * 清除位置信息请求Body<BR>
     * @param sendParams
     * @return
     */
    private String getRemoveLocationBody(Map<String, Object> sendParams)
    {
        String account = (String) sendParams.get(ACCOUNT_KEY);
        
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<removeUserLocation>");
        xml.append("<account>").append(account).append("</account>");
        xml.append("</removeUserLocation>");
        
        return xml.toString();
    }
}
