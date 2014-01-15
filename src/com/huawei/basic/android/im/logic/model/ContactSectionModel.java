/*
 * 文件名: ContactSectionModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周雪松
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * 好友分组信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class ContactSectionModel implements Serializable
{
    /**
     * 默认分组ID
     */
    public static final String DEFAULT_SECTION_ID = "0";
    
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 好友分组ID
     */
    private String contactSectionId;

    /**
     * 好友分组名称
     */
    private String name;

    /**
     * 好友分组备注
     */
    private String notes;

    /**
     * 好友ID集合
     */
    private ArrayList<String> friendSysIds;

    /**
     * 好友账号集合
     */
    private ArrayList<String> friendUserIds;

    /**
     * 好友信息集合
     */
    private List<ContactInfoModel> friendList;
    
    /**
     * 默认构造方法
     */
    public ContactSectionModel()
    {
        super();
    }

    public String getContactSectionId()
    {
        return contactSectionId;
    }

    public void setContactSectionId(String contactSectionId)
    {
        this.contactSectionId = contactSectionId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public ArrayList<String> getFriendSysIds()
    {
        return friendSysIds;
    }

    public void setFriendSysIds(ArrayList<String> friendSysIds)
    {
        this.friendSysIds = friendSysIds;
    }

    public ArrayList<String> getFriendUserIds()
    {
        return friendUserIds;
    }

    public void setFriendUserIds(ArrayList<String> friendUserIds)
    {
        this.friendUserIds = friendUserIds;
    }

    public List<ContactInfoModel> getFriendList()
    {
        return friendList;
    }

    public void setFriendList(List<ContactInfoModel> friendList)
    {
        this.friendList = friendList;
    }

    /**
     * Override<BR>
     * 
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" contactSectionId:").append(contactSectionId);
        sb.append(" name:").append(name);
        sb.append(" notes:").append(notes);
        return sb.toString();
    }

}
