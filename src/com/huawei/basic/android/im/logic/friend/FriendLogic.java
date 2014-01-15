/*
 * 文件名: FriendLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.ContactDetailsMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactSectionColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.location.LocationDataListener;
import com.huawei.basic.android.im.component.location.LocationInfo;
import com.huawei.basic.android.im.component.location.RCSLocationManager;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.component.service.app.IXmppServiceListener;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ContactSectionDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.PhoneContactIndexDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.PhoneLocalAdapter;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.ContactInfoManager;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.adapter.http.FriendManager;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.NearUserModel;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 好友模块的logic实现类<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012] 
 */
public class FriendLogic extends BaseLogic implements IFriendLogic,
        IXmppServiceListener
{
    /**
     * TAG
     */
    public static final String TAG = "FriendLogic";
    
    /**
     * 好友详细数据库操作类
     */
    private ContactInfoDbAdapter mContactDbAdapter;
    
    /**
     * 配置信息表
     */
    private UserConfigDbAdapter mConfigDbAdapter;
    
    /**
     * 分组查询适配器
     */
    private ContactSectionDbAdapter mSectionDbAdapter;
    
    /**
     * 系统 Context 对象
     */
    private Context mContext;
    
    /**
     * 构造方法
     * @param context 
     *      系统 Context 对象
     * @param serviceSender 
     *      与service交互的发送接口定义
     */
    public FriendLogic(Context context, IServiceSender serviceSender)
    {
        this.mContext = context;
        serviceSender.addXmppServiceListener(this);
        mContactDbAdapter = ContactInfoDbAdapter.getInstance(mContext);
        mConfigDbAdapter = UserConfigDbAdapter.getInstance(mContext);
        mSectionDbAdapter = ContactSectionDbAdapter.getInstance(mContext);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addSection(final String sectionName, final String sectionNotes,
            final ArrayList<String> friendSysIdList)
    {
        new FriendManager().addSection(sectionName,
                sectionNotes,
                friendSysIdList,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        //网络异常
                        if (handleResponse(response, mContext.getResources()
                                .getString(R.string.section_add_opera)))
                        {
                            if (response.getObj() != null)
                            {
                                //更新数据库
                                String contactSectionId = (String) response.getObj();
                                String sysId = FusionConfig.getInstance()
                                        .getAasResult()
                                        .getUserSysId();
                                
                                ContactSectionModel newSection = new ContactSectionModel();
                                newSection.setName(sectionName);
                                newSection.setNotes(sectionNotes);
                                newSection.setContactSectionId(contactSectionId);
                                if (mSectionDbAdapter.insertContactSection(sysId,
                                        newSection) == -1)
                                {
                                    Logger.d(TAG,
                                            "addSection ---------> 插入新的分组记录失败");
                                    return;
                                }
                                
                                ContentValues params = new ContentValues();
                                params.put(ContactInfoColumns.CONTACT_SECTIONID,
                                        contactSectionId);
                                
                                for (String friendSysId : friendSysIdList)
                                {
                                    mContactDbAdapter.updateByFriendSysId(sysId,
                                            friendSysId,
                                            params);
                                }
                                
                                //发送数据
                                sendMessage(FriendMessageType.REQUEST_TO_ADD_SECTION,
                                        getAllContactListFromDb());
                            }
                        }
                        
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addContactsToSection(final String sectionId,
            final ArrayList<String> friendIdList)
    {
        new FriendManager().addContactsToSection(sectionId,
                friendIdList,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (handleResponse(response,
                                mContext.getResources()
                                        .getString(R.string.add_contact_to_section_opera)))
                        {
                            //时间戳处理
                            dealWithTimeStamp((String) response.getObj(), false);
                            
                            //处理数据 "contactSectionId"
                            final String userSysId = FusionConfig.getInstance()
                                    .getAasResult()
                                    .getUserSysId();
                            ContentValues params = new ContentValues();
                            params.put(ContactInfoColumns.CONTACT_SECTIONID,
                                    sectionId);
                            
                            for (String friendSysId : friendIdList)
                            {
                                mContactDbAdapter.updateByFriendSysId(userSysId,
                                        friendSysId,
                                        params);
                            }
                            sendMessage(FriendMessageType.REQUEST_ADD_CONTACT_TO_SECTION,
                                    getAllContactListFromDb());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSection(final String sectionId)
    {
        ContactSectionModel sectionModel = new ContactSectionModel();
        sectionModel.setContactSectionId(sectionId);
        final String sysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        final List<ContactInfoModel> contactList = mContactDbAdapter.queryByContactSectionId(sysId,
                sectionId);
        //生成sys id列表
        ArrayList<String> idList = new ArrayList<String>();
        if (contactList != null)
        {
            for (ContactInfoModel contactInfoModel : contactList)
            {
                idList.add(contactInfoModel.getFriendSysId());
            }
        }
        
        new FriendManager().deleteSection(sectionId,
                idList,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (handleResponse(response, mContext.getResources()
                                .getString(R.string.section_delete_opera)))
                        {
                            if (response.getObj() != null)
                            {
                                //时间戳处理
                                dealWithTimeStamp((String) response.getObj(),
                                        false);
                                
                                //把所有相关组员的分组ID都更新成默认分组的信息
                                if (contactList != null)
                                {
                                    for (ContactInfoModel contactInfoModel : contactList)
                                    {
                                        contactInfoModel.setContactSectionId(ContactSectionModel.DEFAULT_SECTION_ID);
                                        mContactDbAdapter.updateByFriendSysId(sysId,
                                                contactInfoModel.getFriendSysId(),
                                                contactInfoModel);
                                    }
                                }
                                
                                if (mSectionDbAdapter.deleteByContactSectionId(sysId,
                                        sectionId) != -1)
                                {
                                    Logger.d(TAG,
                                            "deleteSection -----------> 删除分组成功");
                                    //发送数据到UI
                                    sendMessage(FriendMessageType.REQUEST_DELETE_SECTION,
                                            getAllContactListFromDb());
                                }
                            }
                        }
                        
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getAllContactList(final boolean isNeedHandleFriendManagerData,
            final String friendUserId)
    {
        final String sysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        UserConfigModel timestampModel = mConfigDbAdapter.queryByKey(sysId,
                UserConfigModel.GET_FRIEND_LIST_TIMESTAMP);
        String timetamp = null == timestampModel ? null
                : timestampModel.getValue();
        new FriendManager().getAllContactList(timetamp, new IHttpListener()
        {
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                if (handleResponse(response,
                        mContext.getResources()
                                .getString(R.string.get_contactlist_opera)))
                {
                    //服务返回的数据（包含完整时间戳）
                    ArrayList<Object> responseData = (ArrayList<Object>) response.getObj();
                    
                    //返回的数据至少应该包含时间戳和好友列表
                    if (responseData == null || responseData.size() < 4)
                    {
                        return;
                    }
                    
                    //数据列表
                    ArrayList<ContactSectionModel> responseSectionModelList = (ArrayList<ContactSectionModel>) responseData.get(0);
                    
                    //sys id的列表
                    ArrayList<String> friendSysIdList = (ArrayList<String>) responseData.get(1);
                    
                    //查询出好友列表
                    //                    ArrayList<ContactInfoModel> contactInfoList = mContactDbAdapter.queryAllWithAZ(sysId);
                    
                    //时间戳
                    String timestamp = (String) responseData.get(2);
                    
                    //时间戳是否相同
                    boolean flag = true;
                    if (timestamp != null)
                    {
                        Logger.d(TAG, "getAllContactList ----> 获取好友列表 "
                                + "服务器返回的时间戳数据：" + timestamp);
                        flag = dealWithTimeStamp(timestamp, true);
                    }
                    
                    if (!flag)
                    {
                        //删除好友列表中不存在的数据
                        deleteContactInfoNotExist(friendSysIdList);
                    }
                    
                    //数据库中，原来不存在的数据列表
                    ArrayList<ContactInfoModel> newList = new ArrayList<ContactInfoModel>();
                    
                    for (ContactSectionModel contactSectionModel : responseSectionModelList)
                    {
                        for (ContactInfoModel contactInfoModel : contactSectionModel.getFriendList())
                        {
                            //更新联系人信息数据库，更新失败则插入
                            
                            ContactInfoModel model = mContactDbAdapter.queryByFriendUserIdNoUnion(sysId,
                                    contactInfoModel.getFriendUserId());
                            if (null == model)
                            {
                                newList.add(contactInfoModel);
                            }
                            else
                            {
                                mContactDbAdapter.updateByFriendUserId(sysId,
                                        model.getFriendUserId(),
                                        contactInfoModel);
                            }
                            FaceManager.updateFace(mContext,
                                    contactInfoModel.getFriendUserId(),
                                    contactInfoModel.getFaceUrl());
                        }
                        
                    }
                    
                    //批量插入数据
                    mContactDbAdapter.insertContactInfo(sysId, newList);
                    
                }
                
                // 根据新加好友ID赋值通讯录名称为备注名
                if (null != friendUserId)
                {
                    dealFriendMemoName(friendUserId);
                }
                
                //界面不用监听数据库，直接根据handleState.Message处理，
                //（主要是监听数据库，会导致多次触发事件，降低效率）
                sendMessage(FriendMessageType.REQUEST_FOR_CONTACT_LIST,
                        getAllContactListFromDb());
            }
            
            /**
             * onProgress<BR>
             * @param isInProgress isInProgress
             * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
             */
            public void onProgress(boolean isInProgress)
            {
                
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ContactSectionModel> getContactSectionList()
    {
        ArrayList<ContactSectionModel> list = mSectionDbAdapter.queryAllContactSection(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getContactSectionListWithFriendListAsyn()
    {
        //直接从数据库查询出好友列表
        sendMessage(FriendMessageType.GET_CONTACT_LIST_FROM_DB,
                getAllContactListFromDb());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ContactInfoModel> getContactInfoListFromDb()
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        return mContactDbAdapter.queryAllWithFaceUrl(sysId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ContactInfoModel> getContactListBySectionId(
            String sectionId)
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        
        //默认分组的好友列表，默认分组ID 建议在model中定义
        return mContactDbAdapter.queryByContactSectionId(sysId, sectionId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContactInfoModel getContactInfoByFriendUserId(String friendUserID)
    {
        ContactInfoModel contactInfoModel = mContactDbAdapter.queryByFriendUserIdWithPrivate(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                friendUserID);
        return contactInfoModel;
    }
    
    /**
     * 获取ContactSection列表，包括ContactSection包含contact list<BR>
     * @return 分组列表
     */
    public ArrayList<ContactInfoModel> getAllContactListFromDb()
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        //数据库查询数据，直接抛到界面
        ArrayList<ContactInfoModel> contactList = mContactDbAdapter.queryAllWithFaceUrl(sysId);
        return contactList;
    }
    
    /**
     * {@inheritDoc}
     */
    public void loadMoreFriendById(int startIndex, int recordCount,
            String searchValue)
    {
        new FriendManager().loadMoreFriendById(startIndex,
                recordCount,
                searchValue,
                new IHttpListener()
                {
                    /**
                     * 网络交互监听的回调方法<BR
                     * @param action
                     *      监听动作
                     * @param response
                     *      响应的Response
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
                     * (int, com.huawei.basic.android.im.component.net.http.Response)
                     */
                    @Override
                    public void onResult(int action, Response response)
                    {
                        Response.ResponseCode reponseCode = response.getResponseCode();
                        if (reponseCode != ResponseCode.Succeed)
                        {
                            FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_FIND_FRIEND_ERROR);
                        }
                        else
                        {
                            Object obj = response.getObj();
                            FriendLogic.this.sendMessage(FriendMessageType.REQUEST_FRIEND_LOAD_MORE_FRIEND,
                                    obj);
                        }
                    }
                    
                    /**
                     * 进度变化回调<BR>
                     * @param isInProgress
                     *      true为进度开始，false为进度结束
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
                     */
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMoreFriendByDetail(int startIndex, int recordCount,
            String searchValue)
    {
        new FriendManager().loadMoreFriendByDetail(startIndex,
                recordCount,
                searchValue,
                new IHttpListener()
                {
                    /**
                     * 网络交互监听的回调方法<BR
                     * @param action
                     *      监听动作
                     * @param response
                     *      响应的Response
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
                     * (int, com.huawei.basic.android.im.component.net.http.Response)
                     */
                    @Override
                    public void onResult(int action, Response response)
                    {
                        Response.ResponseCode reponseCode = response.getResponseCode();
                        if (reponseCode != ResponseCode.Succeed)
                        {
                            FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_FIND_FRIEND_ERROR);
                        }
                        Object obj = response.getObj();
                        FriendLogic.this.sendMessage(FriendMessageType.REQUEST_FRIEND_LOAD_MORE_FRIEND,
                                obj);
                        
                    }
                    
                    /**
                     * 进度变化回调<BR>
                     * @param isInProgress
                     *      true为进度开始，false为进度结束
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
                     */
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMoreFriendByKnownPerson()
    {
        new FriendManager().loadMoreFriendByKnownPerson(new IHttpListener()
        {
            /**
             * 网络交互监听的回调方法<BR
             * @param action
             *      监听动作
             * @param response
             *      响应的Response
             * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
             * (int, com.huawei.basic.android.im.component.net.http.Response)
             */
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                Response.ResponseCode reponseCode = response.getResponseCode();
                if (reponseCode != ResponseCode.Succeed)
                {
                    FriendLogic.this.sendEmptyMessage(FriendMessageType.FIND_MAYBE_KNOWN_FRIEND_FAIL);
                }
                Object obj = response.getObj();
                //批量获取头像，这里严重影响效率，改用photoLoader获取头像
                if (null != obj)
                {
                    updateFaceUrl((ArrayList<ContactInfoModel>) obj);
                }
                FriendLogic.this.sendMessage(FriendMessageType.REQUEST_FRIEND_MAYBE_KNOWN_PERSON,
                        obj);
            }
            
            /**
             * 进度变化回调<BR>
             * @param isInProgress
             *      true为进度开始，false为进度结束
             * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
             */
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadMoreFriendByLocation()
    {
        new RCSLocationManager(mContext).getLocationInfo(new LocationDataListener()
        {
            @Override
            public void onLocationResult(LocationInfo result)
            {
                Location location = result.getLocation();
                if (null != location)
                {
                    FriendLogic.this.sendEmptyMessage(FriendMessageType.LOCATION_SUCCESS);
                    new FriendManager().loadMoreFriendByLocation(location.getLatitude(),
                            location.getLongitude(),
                            new IHttpListener()
                            {
                                /**
                                 * 网络交互监听的回调方法<BR
                                 * @param action
                                 *      监听动作
                                 * @param response
                                 *      响应的Response
                                 * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
                                 * (int, com.huawei.basic.android.im.component.net.http.Response)
                                 */
                                @SuppressWarnings("unchecked")
                                @Override
                                public void onResult(int action,
                                        Response response)
                                {
                                    Response.ResponseCode reponseCode = response.getResponseCode();
                                    Logger.d(TAG,
                                            "Response  = "
                                                    + response.toString());
                                    Logger.d(TAG,
                                            "Response Code  ="
                                                    + response.getResponseCode());
                                    if (reponseCode != ResponseCode.Succeed)
                                    {
                                        FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_FIND_FRIEND_ERROR);
                                    }
                                    else
                                    {
                                        Object obj = response.getObj();
                                        updateFaceUrl((ArrayList<ContactInfoModel>) obj);
                                        FriendLogic.this.sendMessage(FriendMessageType.FRIEND_BY_LOCATION_SUCCESS,
                                                obj);
                                    }
                                }
                                
                                @Override
                                public void onProgress(boolean isInProgress)
                                {
                                    
                                }
                            });
                }
                else
                {
                    FriendLogic.this.sendEmptyMessage(FriendMessageType.FRIEND_BY_LOCATION_FAILED);
                }
            }
            
            @Override
            public void onLocationProgress(boolean show)
            {
                if (show)
                {
                    FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_LOCATION);
                }
                
            }
        },
                false);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLocationInfo()
    {
        new FriendManager().removeLocationInfo(new IHttpListener()
        {
            /**
             * 网络交互监听的回调方法<BR
             * @param action
             *      监听动作
             * @param response
             *      响应的Response
             * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
             * (int, com.huawei.basic.android.im.component.net.http.Response)
             */
            @Override
            public void onResult(int action, Response response)
            {
                Response.ResponseCode reponseCode = response.getResponseCode();
                if (reponseCode != ResponseCode.Succeed)
                {
                    FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_ERROR);
                }
                else
                {
                    Object obj = response.getObj();
                    NearUserModel model = (NearUserModel) obj;
                    if (model.getResultCode() == 0)
                    {
                        FriendLogic.this.sendEmptyMessage(FriendMessageType.REQUEST_REMOVE_LOCATION);
                    }
                    else
                    {
                        FriendLogic.this.sendMessage(FriendMessageType.REMOVE_LOCATION_ERROR,
                                model.getResultCode());
                    }
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void syncFaceIcon(final ContactInfoModel contactInfo)
    {
        String faceUrl = contactInfo.getFaceUrl();
        String friendUserId = contactInfo.getFriendUserId();
        new FaceManager().loadFaceIcon(friendUserId,
                faceUrl,
                new IHttpListener()
                {
                    /**
                     * 网络交互监听的回调方法<BR
                     * @param action
                     *      监听动作
                     * @param response
                     *      响应的Response
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
                     * (int, com.huawei.basic.android.im.component.net.http.Response)
                     */
                    @Override
                    public void onResult(int action, Response response)
                    {
                        Object obj = response.getObj();
                        contactInfo.setFaceBytes((byte[]) obj);
                        FriendLogic.this.sendEmptyMessage(FriendMessageType.DEF_FRIEND_SYNC_FACE_ICON);
                    }
                    
                    /**
                     * 进度变化回调<BR>
                     * @param isInProgress
                     *      true为进度开始，false为进度结束
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
                     */
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSectionName(final String sectionName,
            final String sectionId)
    {
        final String sysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        new FriendManager().updateSectionName(sectionName,
                sectionId,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (handleResponse(response, mContext.getResources()
                                .getString(R.string.update_section_name_opera)))
                        {
                            if (response.getObj() != null)
                            {
                                //更新时间戳
                                dealWithTimeStamp((String) response.getObj(),
                                        false);
                                
                                ContentValues cv = new ContentValues();
                                cv.put(ContactSectionColumns.NAME, sectionName);
                                if (mSectionDbAdapter.updateContactSection(sysId,
                                        sectionId,
                                        cv) != -1)
                                {
                                    sendMessage(FriendMessageType.REQUEST_UPDATE_SECTION_NAME,
                                            getAllContactListFromDb());
                                }
                            }
                        }
                        
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMemberFromSection(final ArrayList<String> sysIdList,
            String sectionId)
    {
        
        new FriendManager().removeMemberFromSection(sysIdList,
                sectionId,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (handleResponse(response,
                                mContext.getResources()
                                        .getString(R.string.remove_member_from_section_opera)))
                        {
                            if (response.getObj() != null)
                            {
                                //时间戳处理
                                dealWithTimeStamp((String) response.getObj(),
                                        false);
                                
                                //更新数据库，刷新列表
                                String sysId = FusionConfig.getInstance()
                                        .getAasResult()
                                        .getUserSysId();
                                ContentValues params = new ContentValues();
                                params.put(ContactInfoColumns.CONTACT_SECTIONID,
                                        ContactSectionModel.DEFAULT_SECTION_ID);
                                for (String id : sysIdList)
                                {
                                    mContactDbAdapter.updateByFriendSysId(sysId,
                                            id,
                                            params);
                                }
                                sendMessage(FriendMessageType.REQUEST_REMOVE_CONTACT_FROM_SECTION,
                                        getAllContactListFromDb());
                            }
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeToSection(final String accountId,
            final String newSectionId, final String oldSectionId)
    {
        
        new FriendManager().removeToSection(accountId,
                newSectionId,
                oldSectionId,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        Logger.d(TAG, "removeToSection ---> 服务器返回的时间戳："
                                + response.getObj());
                        if (handleResponse(response,
                                mContext.getResources()
                                        .getString(R.string.remove_member_from_section_opera)))
                        {
                            if (response.getObj() != null)
                            {
                                String userSysId = FusionConfig.getInstance()
                                        .getAasResult()
                                        .getUserSysId();
                                
                                //联系人信息表，查询Adapter
                                ContentValues params = new ContentValues();
                                params.put(ContactInfoColumns.CONTACT_SECTIONID,
                                        newSectionId);
                                
                                if (mContactDbAdapter.updateByFriendUserId(userSysId,
                                        accountId,
                                        params) >= 1)
                                {
                                    //界面不用监听数据库，直接根据handleStateMessage处理，
                                    //（主要是监听数据库，会导致多次触发事件，降低效率）
                                    sendMessage(FriendMessageType.REQUEST_REMOVE_CONTACT_TO_SECTION,
                                            getAllContactListFromDb());
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContactDetails(final String hiTalkID,
            final int contactType)
    {
        
        // 本地数据库取出详细信息
        final ContactInfoModel mContactInfoModel = getContactInfoByFriendUserId(hiTalkID);
        
        new ContactInfoManager().sendDetail(new FriendManager().getContactInfoList(hiTalkID,
                contactType,
                mContactInfoModel),
                new IHttpListener()
                {
                    /**
                     * 
                     * 通过该方法获取response数据并反馈UI层<BR>
                     * [功能详细描述]
                     * @param action action
                     * @param response response
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener
                     *      #onResult(int, com.huawei.basic.android.im.component.net.http.Response)
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        // 接口调用成功返回
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            Logger.d(TAG, "Response:" + response.getObj());
                            
                            // 返回UI对象
                            ContactInfoModel contactInfoModel = null;
                            
                            // 通过时间戳判断:有更新返回数据，否则不返回 
                            if (null != response.getObj())
                            {
                                // 封装返回对象：resultObj.get(0) 详细信息，
                                //resultObj.get(1) 服务器返回时间戳（需要更新数据库）
                                ArrayList<Object> resultObj = new ArrayList<Object>();
                                resultObj = (ArrayList<Object>) response.getObj();
                                
                                // 若无数据，直接返回
                                if (resultObj.size() < 2)
                                {
                                    return;
                                }
                                
                                // 获取的详细信息
                                ArrayList<ContactInfoModel> resContactInfo = (ArrayList<ContactInfoModel>) resultObj.get(0);
                                
                                // 判断服务器数据是否更改
                                if (null != resContactInfo)
                                {
                                    // 获取的时间戳
                                    String timestamp = (String) resultObj.get(1);
                                    
                                    // 服务器返回的contactInfoModel 其中不包含备注信息
                                    contactInfoModel = (ContactInfoModel) resContactInfo.get(0);
                                    
                                    // 本地数据库有数据
                                    if (mContactInfoModel != null)
                                    {
                                        // 需要把本地数据库中已存的备注信息加入
                                        //到到contactInfoModel去，
                                        //防止本地备注信息被覆盖
                                        contactInfoModel.setMemoName(mContactInfoModel.getMemoName());
                                        contactInfoModel.setMemoPhones(mContactInfoModel.getMemoPhones());
                                        contactInfoModel.setMemoEmails(mContactInfoModel.getMemoEmails());
                                        // 需要把本地分组信息加到contactInfoModel
                                        contactInfoModel.setContactSectionId(mContactInfoModel.getContactSectionId());
                                        
                                        // 若返回时间戳不为空，放入contactInfoModel
                                        if (timestamp != null)
                                        {
                                            contactInfoModel.setLastUpdate(timestamp);
                                        }
                                        
                                        // 更新数据库
                                        updateContactInfoDB(hiTalkID,
                                                contactInfoModel);
                                    }
                                    else if (contactType == ContactDetailAction.FRIEND_CONTACT)
                                    {
                                        // 更新数据库
                                        updateContactInfoDB(hiTalkID,
                                                contactInfoModel);
                                    }
                                }
                                else
                                {
                                    contactInfoModel = mContactInfoModel;
                                }
                            }
                            else
                            {
                                contactInfoModel = mContactInfoModel;
                            }
                            
                            // 更新头像
                            updateFace(contactInfoModel.getFriendUserId(),
                                    contactInfoModel.getFaceUrl());
                            // 返回UI
                            sendMessage(ContactDetailsMessageType.GET_MULTI_FRIENDS_DETAILS,
                                    contactInfoModel);
                            
                        }
                        else
                        {
                            // 获取好友信息失败
                            sendMessage(ContactDetailsMessageType.GET_FRIENDS_DETAILS_FALSE,
                                    null);
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendFriendMemo(final ContactInfoModel contactInfoModel,
            final String friendMemoName, final String friendMemoPhone,
            final String friendMemoEmail)
    {
        
        new ContactInfoManager().sendFriendMemo(contactInfoModel,
                friendMemoName,
                friendMemoPhone,
                friendMemoEmail,
                new IHttpListener()
                {
                    /**
                     * 
                     * 通过该方法获取response数据并反馈UI层<BR>
                     * [功能详细描述]
                     * @param action action
                     * @param response response
                     * @see com.huawei.basic.android.im.component.net.http.IHttpListener
                     *      #onResult(int, com.huawei.basic.android.im.component.net.http.Response)
                     */
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            Logger.i(TAG, "服务更新器备注信息成功！");
                            
                            // 封装friendMemoPhone
                            List<String> listPhone = new ArrayList<String>();
                            listPhone.add(friendMemoPhone);
                            // 封装friendMemoEmail
                            List<String> listEmail = new ArrayList<String>();
                            listEmail.add(friendMemoEmail);
                            
                            contactInfoModel.setMemoName(friendMemoName);
                            contactInfoModel.setMemoPhones(listPhone);
                            contactInfoModel.setMemoEmails(listEmail);
                            
                            // 更新数据库
                            if (mContactDbAdapter.updateByFriendSysId(FusionConfig.getInstance()
                                    .getAasResult()
                                    .getUserSysId(),
                                    contactInfoModel.getFriendSysId(),
                                    contactInfoModel) >= 1)
                            {
                                // 返回消息给UI
                                sendMessage(ContactDetailsMessageType.UPDATE_FRIEND_MEMO,
                                        contactInfoModel);
                            }
                        }
                        else
                        {
                            Logger.i(TAG, "服务更新器备注信息失败！");
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sectionNameExist(String sectionName)
    {
        boolean flag = false;
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<ContactSectionModel> contactSectionList = mSectionDbAdapter.queryAllContactSection(userSysId);
        if (contactSectionList != null)
        {
            for (ContactSectionModel contactSection : contactSectionList)
            {
                String contactSectionName = contactSection.getName().trim();
                Logger.d(TAG, "contactSectionName====" + contactSectionName);
                if (sectionName.equalsIgnoreCase(contactSectionName))
                {
                    flag = true;
                    return flag;
                }
            }
        }
        return flag;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean otherSectionNameExist(String beforeSectionName,
            String newSectionName)
    {
        //默认相同分组名不存在
        boolean flag = false;
        List<ContactSectionModel> sameModel = new ArrayList<ContactSectionModel>();
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<ContactSectionModel> contactSectionList = mSectionDbAdapter.queryAllContactSection(userSysId);
        if (contactSectionList != null)
        {
            for (ContactSectionModel contactSection : contactSectionList)
            {
                Logger.d(TAG, "beforeSectionName====" + beforeSectionName
                        + "NEW sECTIONNAME==" + newSectionName
                        + "beforeSectionName.equalsIgnoreCase newSectionName"
                        + "" + (beforeSectionName.equals(newSectionName)));
                if (beforeSectionName.equalsIgnoreCase(newSectionName))
                {
                    //移除现在正在编辑的分组
                    sameModel.add(contactSection);
                }
            }
            contactSectionList.removeAll(sameModel);
            Logger.d(TAG, "sameModel ==" + sameModel.toString());
            for (ContactSectionModel otherContactSection : contactSectionList)
            {
                String otherContactSectionName = otherContactSection.getName()
                        .trim();
                
                if (newSectionName.equalsIgnoreCase(otherContactSectionName))
                {
                    flag = true;
                }
            }
        }
        
        return flag;
        
    }
    
    /**
     * 
     * 通过hiTalkID监听数据库单条好友信息<BR>
     * 
     * @param hiTalkID hiTalkID
     */
    @Override
    public void registerObserverByID(final String hiTalkID)
    {
        // 空指针保护
        if (null == hiTalkID)
        {
            return;
        }
        
        Uri uri = Uri.withAppendedPath(URIField.CONTACTINFO_WITH_FRIEND_USER_ID_URI,
                hiTalkID);
        // 调用父类方法，将当前uri注册到监听中
        registerObserver(uri, new ContentObserver(new Handler())
        {
            public void onChange(boolean selfChange)
            {
                sendMessage(FriendMessageType.CONTACTINFO_CHANGED, hiTalkID);
            }
        });
    }
    
    /**
     * 
     * 通过hiTalkID移除数据库单条好友信息监听<BR>
     * 
     * @param hiTalkID hiTalkID
     */
    @Override
    public void unRegisterObserverByID(String hiTalkID)
    {
        // 空指针保护
        if (null == hiTalkID)
        {
            return;
        }
        
        Uri uri = Uri.withAppendedPath(URIField.CONTACTINFO_WITH_FRIEND_USER_ID_URI,
                hiTalkID);
        // 调用父类方法，移除uri
        unRegisterObserver(uri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        if (uri.equals(URIField.CONTACTINFO_INSERT_URI))
        {
            Logger.d(TAG, "insert uri >>> " + uri);
            sendEmptyMessage(FriendMessageType.CONTACT_INFO_INSERT);
        }
        else
        {
            Logger.d(TAG, "delete uri >>> " + uri);
            sendEmptyMessage(FriendMessageType.CONTACT_INFO_DELETE);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { URIField.CONTACTINFO_DELETE_URI,
                URIField.CONTACTINFO_INSERT_URI };
    }
    
    /**
     * 
     * 处理服务器返回值
     * @param response 服务器的响应
     * @param operation 联网操作
     * @return 是否继续处理
     */
    private boolean handleResponse(Response response, String operation)
    {
        boolean flag = false;
        
        Logger.d(TAG,
                "response.getResponseCode()====" + response.getResponseCode());
        switch (response.getResponseCode())
        {
            
            case Failed:
            case AuthError:
            case ParamError:
                sendMessage(FriendMessageType.RESPONSE_ERROR,
                        mContext.getResources()
                                .getString(R.string.service_error)
                                + operation
                                + mContext.getResources()
                                        .getString(R.string.error_failure));
                break;
            case Timeout:
            case NetworkError:
                sendMessage(FriendMessageType.RESPONSE_ERROR,
                        mContext.getResources().getString(R.string.net_error)
                                + operation
                                + mContext.getResources()
                                        .getString(R.string.error_failure));
                break;
            case Succeed:
                flag = true;
                return flag;
            default:
                break;
        }
        return flag;
    }
    
    /**
     * 更新图片表<br>
     * 如果数据库没有，直接插入数据库；如果数据库有，
     * 比较URL是否相同，如果不同更新数据库
     * 
     * @param faceId face id
     * @param faceUrl face URL
     */
    private void updateFace(String faceId, String faceUrl)
    {
        if (faceId != null && faceUrl != null)
        {
            FaceThumbnailDbAdapter faceDbAdapter = FaceThumbnailDbAdapter.getInstance(mContext);
            // 先从数据库获取已有的头像
            FaceThumbnailModel face = faceDbAdapter.queryByFaceId(faceId);
            
            // 如果数据库没有数据，则直接插入
            if (face == null)
            {
                faceDbAdapter.insertFaceThumbnail(new FaceThumbnailModel(
                        faceId, faceUrl));
            }
            
            // 如果数据库中有数据，则比较URL，如果URL不同，则更新，并清除原有头像byte[]
            else
            {
                if (!faceUrl.equals(face.getFaceUrl()))
                {
                    // 全量更新时会覆盖原有的值
                    faceDbAdapter.updateByFaceId(faceId,
                            new FaceThumbnailModel(faceId, faceUrl));
                }
            }
        }
    }
    
    /**
     * 数据库操作<BR>
     * 更新本地数据库好友信息
     * @param hiTalkID hitalkID
     * @param contactInfoModel contactInfoModel
     */
    private void updateContactInfoDB(String hiTalkID,
            ContactInfoModel contactInfoModel)
    {
        // 判断本地数据库存在好友信息
        if (getContactInfoByFriendUserId(hiTalkID) == null)
        {
            mContactDbAdapter.insertContactInfo(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(), contactInfoModel);
        }
        else
        {
            mContactDbAdapter.updateByFriendUserId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(), hiTalkID, contactInfoModel);
        }
    }
    
    /**
     * 联系人列表转联系人分组列表（方便界面展示）<BR>
     * @param contactInfoList contactInfoList
     * @param contactSectionList contactSectionList
     */
    @SuppressWarnings("unused")
    private void getContactSectionList(List<ContactInfoModel> contactInfoList,
            List<ContactSectionModel> contactSectionList)
    {
        ContactSectionModel defaultSection = null;
        for (ContactSectionModel contactSectionModel : contactSectionList)
        {
            //默认组名，考虑在model定义常量。。移动我的好友分组在最前面
            if (ContactSectionModel.DEFAULT_SECTION_ID.equals(contactSectionModel.getContactSectionId()))
            {
                defaultSection = contactSectionModel;
                break;
            }
        }
        contactSectionList.remove(defaultSection);
        contactSectionList.add(0, defaultSection);
        if (contactInfoList == null)
        {
            return;
        }
        HashMap<String, ContactSectionModel> sectionIdMap = new HashMap<String, ContactSectionModel>();
        for (ContactSectionModel contactSectionModel : contactSectionList)
        {
            ArrayList<ContactInfoModel> list = new ArrayList<ContactInfoModel>();
            contactSectionModel.setFriendList(list);
            sectionIdMap.put(contactSectionModel.getContactSectionId(),
                    contactSectionModel);
        }
        
        //数据以ContactSectionModel的方式提供数据到UI
        for (ContactInfoModel contactInfoModel : contactInfoList)
        {
            // 增加SectionID空值处理
            String sectionID = contactInfoModel.getContactSectionId();
            sectionIdMap.get(sectionID == null ? ContactSectionModel.DEFAULT_SECTION_ID
                    : sectionID)
                    .getFriendList()
                    .add(contactInfoModel);
        }
    }
    
    /**
     * 处理时间戳<BR>
     * @param timestamp 时间戳数据
     * @param isFull 是否是完整时间戳
     * （获取好友列表是完整的时间戳，分组操作是时间戳的后半截（版本号））
     * @return 版本号是否改变 [是 true 否 false]
     */
    private boolean dealWithTimeStamp(String timestamp, boolean isFull)
    {
        //网络连接异常时间戳判空操作
        if (null != timestamp)
        {
            Logger.d(TAG, "dealWithTimeStamp ------>timestamp:" + timestamp);
            String userSysId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId();
            UserConfigDbAdapter configAdapter = UserConfigDbAdapter.getInstance(mContext);
            if (isFull)
            {
                UserConfigModel config = configAdapter.queryByKey(userSysId,
                        UserConfigModel.GET_FRIEND_LIST_TIMESTAMP);
                
                if (null == config)
                {
                    //插入数据
                    configAdapter.insertUserConfig(userSysId,
                            UserConfigModel.GET_FRIEND_LIST_TIMESTAMP,
                            timestamp);
                }
                else
                {
                    //更新数据库
                    configAdapter.updateByKey(userSysId,
                            UserConfigModel.GET_FRIEND_LIST_TIMESTAMP,
                            timestamp);
                    
                    String value = config.getValue();
                    //版本号
                    String lv = value.substring(value.indexOf("LV=") + 2);
                    String retTimestamp = timestamp.substring(timestamp.indexOf("LV=") + 2);
                    Logger.d(TAG, "dealWithTimeStamp >> " + lv);
                    //如果相同返回true,否则false
                    if (lv.equals(retTimestamp))
                    {
                        return true;
                    }
                }
            }
            else
            {
                timestamp = timestamp.substring(0, timestamp.indexOf("LV=") + 3)
                        + timestamp;
                configAdapter.updateByKey(userSysId,
                        UserConfigModel.GET_FRIEND_LIST_TIMESTAMP,
                        timestamp);
            }
            
        }
        else
        {
            Logger.d(TAG, "网络连接异常时间戳判空操作");
        }
        return false;
    }
    
    /**
     * 删除好友列表中不存在的数据（根据sys id 列表）<BR>
     * @param userFriendSysIdList 
     */
    private void deleteContactInfoNotExist(ArrayList<String> userFriendSysIdList)
    {
        //当前登录用户的sysId
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        // 根据useridlist删除本地多余的好友
        if (null != userFriendSysIdList)
        {
            ArrayList<ContactInfoModel> contactinfolist = mContactDbAdapter.queryAllWithAZ(userSysId);
            if (null != contactinfolist)
            {
                boolean finded = false;
                for (ContactInfoModel contactInfo : contactinfolist)
                {
                    finded = false;
                    String dbsysid = contactInfo.getFriendSysId();
                    for (String httpusersysid : userFriendSysIdList)
                    {
                        if (httpusersysid.equals(dbsysid))
                        {
                            finded = true;
                            break;
                        }
                    }
                    if (!finded)
                    {
                        // 网络回执的全量中没有就删除
                        Logger.d(TAG,
                                "删除记录--->friendUserId:"
                                        + contactInfo.getFriendUserId());
                        mContactDbAdapter.deleteByFriendUserId(userSysId,
                                contactInfo.getFriendUserId());
                    }
                }
            }
            userFriendSysIdList.clear();
        }
    }
    
    /**
     * 生成会话展示
     * (由于现在不展示离线的小助手信息，所以这个方法暂时不用，将来应该会使用到)
     * @param friendManagerModel friendManagerModel
     * @param context 上下文对象
     * @return 生成的字符串
     */
    @SuppressWarnings("unused")
    private String generateConversationString(Context context,
            FriendManagerModel friendManagerModel)
    {
        int subservice = friendManagerModel.getSubService();
        int status = friendManagerModel.getStatus();
        
        Logger.d(TAG, "generateConversationString -------> subservice:"
                + subservice);
        Logger.d(TAG, "generateConversationString -------> status:" + status);
        
        Resources rcs = context.getResources();
        String lastConversationMsg = null;
        
        // 会话中展示的好友名称，如果为空 则显示 好友的WoYou ID
        String displayFriendName = null;
        // 会话中展示的群组名称 ，如果为空 则显示 群组的 ID
        String displayGroupName = null;
        
        displayGroupName = StringUtil.isNullOrEmpty(friendManagerModel.getGroupName()) ? friendManagerModel.getGroupId()
                : friendManagerModel.getGroupName();
        displayFriendName = StringUtil.isNullOrEmpty(friendManagerModel.getNickName()) ? friendManagerModel.getFriendUserId()
                : friendManagerModel.getNickName();
        
        switch (subservice)
        {
            case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
            {
                // 申请加别人好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_pass));
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_decline));
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_BE_ADD:
            {
                // 被加好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_pass),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
            {
                
                // 接收到群邀请
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.firendmanager_message_inviting_refuse),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
            {
                // 群主受理待加入成员
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_wait),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName,
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
            {
                // 申请加入群
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_wait),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_INVITE_REGISTER:
            case FriendManagerModel.SUBSERVICE_SYSTEM_MATCH:
                break;
            case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_friend_success),
                        displayFriendName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_self),
                        displayGroupName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_owner),
                        displayFriendName,
                        displayGroupName);
                break;
        }
        Logger.d(TAG,
                "generateConversationString -------> 生成的字符串  lastConversationMsg: "
                        + lastConversationMsg);
        return lastConversationMsg;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendXmppMessage(int messageType, String result)
    {
        switch (messageType)
        {
            case FriendHelperMessageType.NEW_FRIEND_ADDED:
                getAllContactList(false, result);
                sendMessage(FriendHelperMessageType.NEW_FRIEND_ADDED, result);
                break;
            case FriendHelperMessageType.REQUEST_DO_AUTH_SUCCESS:
                getAllContactList(false, null);
                sendEmptyMessage(FriendHelperMessageType.REQUEST_DO_AUTH_SUCCESS);
                break;
            case FriendHelperMessageType.DELETE_FRIEND_SUCCESS:
                sendEmptyMessage(FriendHelperMessageType.DELETE_FRIEND_SUCCESS);
                break;
            case FriendHelperMessageType.BE_DELETED:
                sendMessage(FriendHelperMessageType.BE_DELETED, result);
                break;
            case FriendHelperMessageType.FRIENDHELPER_PRESENCE:
                sendEmptyMessage(FriendHelperMessageType.FRIENDHELPER_PRESENCE);
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * 赋值通讯录名称为好友备注<BR>
     * [功能详细描述]
     * @param friendUserId
     */
    private void dealFriendMemoName(String friendUserId)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        ContactInfoModel contactModel = mContactDbAdapter.queryByFriendUserIdWithPrivate(userSysId,
                friendUserId);
        
        if (null != contactModel && null == contactModel.getMemoName())
        {
            PhoneContactIndexModel contactIndexModel = PhoneContactIndexDbAdapter.getInstance(mContext)
                    .queryByFriendUserId(userSysId, friendUserId);
            if (null != contactIndexModel
                    && null != contactIndexModel.getContactLUID())
            {
                PhoneLocalAdapter phoneLocalAdapter = PhoneLocalAdapter.getInstance(mContext);
                contactIndexModel = phoneLocalAdapter.queryContactByID(contactIndexModel.getContactLUID());
                if (null != contactIndexModel
                        && !StringUtil.isNullOrEmpty(contactIndexModel.getDisplayName()))
                {
                    sendFriendMemo(contactModel,
                            contactIndexModel.getDisplayName(),
                            null,
                            null);
                }
            }
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void xmppCallback(String componentID, int notifyID, String data)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFriendExist(String friendSysId)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        if (null != mContactDbAdapter.queryByFriendSysIdNoUnion(userSysId,
                friendSysId))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * 插入好友头像ＵＲＬ<BR>
     * [功能详细描述]
     * @param infoList
     */
    private void updateFaceUrl(ArrayList<ContactInfoModel> infoList)
    {
        if (null != infoList)
        {
            for (ContactInfoModel contactInfo : infoList)
            {
                FaceManager.updateFace(mContext,
                        contactInfo.getFriendUserId(),
                        contactInfo.getFaceUrl());
            }
        }
    }
}
