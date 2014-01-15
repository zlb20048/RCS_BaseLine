/*
 * 文件名: PhoneContactIndexModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qinyangwang
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 手机联系人信息<BR>
 * [功能详细描述]
 * 
 * @author 邵培培
 * @version [RCS Client V100R001C03, 2012-2-16]
 */
public class PhoneContactIndexModel extends BaseContactModel
{
    
    /**
     * 手机联系人
     */
    public static final int CONTACT_TYPE_PHONE = 0;
    
    /**
     * SIM卡1联系人
     */
    public static final int CONTACT_TYPE_SIM_ONE = 1;
    
    /**
     * SIM卡2联系人(如果有SIM卡2的话)
     */
    public static final int CONTACT_TYPE_SIM_TWO = 2;
    
    /**
     * 修改标记：新增
     */
    public static final int CONTACT_MODIFY_FLAG_ADD = 0;
    
    /**
     * 修改标记：修改
     */
    public static final int CONTACT_MODIFY_FLAG_UPDATE = 1;
    
    /**
     * 修改标记：删除
     */
    public static final int CONTACT_MODIFY_FLAG_DELETE = 2;
    
    /**
     * 修改标记：正常，没变更
     */
    public static final int CONTACT_MODIFY_FLAG_NORMAL = 3;
    
    /**
     * 用户被加好友的验证方式说明: 1：允许任何人
     */
    public static final int ADDFRIENDPRIVACY_ALLOW_ALL = 1;
    
    /**
     * 用户被加好友的验证方式说明: 2：需要验证信息
     */
    public static final int ADDFRIENDPRIVACY_NEED_CONFIRM = 2;
    
    /**
     * 用户被加好友的验证方式说明: 3：允许通讯录的HiTalk用户（客户端无特殊操作，同赋值1）
     */
    public static final int ADDFRIENDPRIVACY_ALLOW_CONTACT = 3;
    
    /**
     * 用户被加好友的验证方式说明: 4：不允许任何人
     */
    public static final int ADDFRIENDPRIVACY_NO_ALLOW = 4;
    
    /**
     * 用户被加好友的验证方式说明: 5：允许绑定了手机号码的用户经我确认后加我为好友（暂未使用）
     */
    public static final int ADDFRIENDPRIVACY_ALLOW_BIND = 5;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 在数据库表中的ID
     */
    private long id;
    
    /**
     * 联系人的本地通讯录ID
     */
    private String contactLUID;
    
    /**
     * 联系人在服务器上的ID
     */
    private String contactGUID;
    
    /**
     * 联系人在HiTalk系统上的唯一标识
     */
    private String contactSysId;
    
    /**
     * 联系人HiTalkID
     */
    private String contactUserId;
    
    /**
     * 用户被加好友的验证方式说明： <br>
     * 1：允许任何人 <br>
     * 2：需要验证信息 <br>
     * 3：允许通讯录的HiTalk用户（客户端无特殊操作，同赋值1） <br>
     * 4：不允许任何人 <br>
     * 5：允许绑定了手机号码的用户经我确认后加我为好友（暂未使用）
     */
    private int addFriendPrivacy;
    
    /**
     * 手机号码集合
     */
    private List<List<String>> phoneNumbers;
    
    /**
     * 邮箱集合
     */
    private List<List<String>> emailAddrs;
    
    /**
     * 签名
     */
    private String signature;
    
    /**
     * 是否是HiTalk好友
     */
    private boolean isHiTalk;
    
    /**
     * 是否是我的好友
     */
    private boolean isMyFriend;
    
    /**
     * 头像ID
     */
    private String photoId;
    
    /**
     * 头像字节数组
     */
    private byte[] headBytes;
    
    /**
     * 联系人类型
     */
    private int contactType;
    
    /**
     * 联系人Crc值, 用于比对是否修改
     */
    private String contactCrcValue;
    
    /**
     * 标记是否有手机号码
     */
    private int hasPhoneNumber;
    
    /**
     * 联系人修改标记
     */
    private int contactModifyFlag = CONTACT_MODIFY_FLAG_ADD;
    
    /**
     * HiTalk友的设置 是否允许加他为好友
     */
    private String fp;
    
    /**
     * 加HiTalk友时需要确认
     */
    private String acf;
    
    /**
     * 拼音
     */
    private String spellName;
    
    /**
     * 拼音简称
     */
    private String initialName;
    
    /**
     * [构造简要说明]
     */
    public PhoneContactIndexModel()
    {
        super();
    }
    
    public String getContactLUID()
    {
        return contactLUID;
    }
    
    public void setContactLUID(String contactLUID)
    {
        this.contactLUID = contactLUID;
    }
    
    public String getContactGUID()
    {
        return contactGUID;
    }
    
    public String getContactSysId()
    {
        return contactSysId;
    }
    
    public void setContactSysId(String contactSysId)
    {
        this.contactSysId = contactSysId;
    }
    
    public void setContactGUID(String contactGUID)
    {
        this.contactGUID = contactGUID;
    }
    
    public List<List<String>> getPhoneNumbers()
    {
        return phoneNumbers;
    }
    
    /**
     * 增加一个电话<BR>
     * 
     * @param phoneNumber
     *            电话号码
     *            号码类型(住宅、手机等)
     */
    public void addPhoneNumber(List<String> phoneNumber)
    {
        if (phoneNumbers == null)
        {
            phoneNumbers = new ArrayList<List<String>>();
        }
        phoneNumbers.add(phoneNumber);
    }
    
    public List<List<String>> getEmailAddrs()
    {
        return emailAddrs;
    }
    
    /**
     * 增加一个邮箱<BR>
     * 
     * @param emailAddr
     *            邮箱地址
     *            邮箱类型(家用、单位等)
     */
    public void addEmailAddr(List<String> emailAddr)
    {
        if (emailAddrs == null)
        {
            emailAddrs = new ArrayList<List<String>>();
        }
        emailAddrs.add(emailAddr);
    }
    
    public String getSignature()
    {
        return signature;
    }
    
    public void setSignature(String signature)
    {
        this.signature = signature;
    }
    
    public boolean isHiTalk()
    {
        return isHiTalk;
    }
    
    public void setHiTalk(boolean hitalk)
    {
        this.isHiTalk = hitalk;
    }
    
    public boolean isMyFriend()
    {
        return isMyFriend;
    }
    
    public void setMyFriend(boolean myFriend)
    {
        this.isMyFriend = myFriend;
    }
    
    public String getPhotoId()
    {
        return photoId;
    }
    
    public void setPhotoId(String photoId)
    {
        this.photoId = photoId;
    }
    
    public byte[] getHeadBytes()
    {
        return headBytes;
    }
    
    public void setHeadBytes(byte[] bytes)
    {
        this.headBytes = bytes;
    }
    
    public int getContactType()
    {
        return contactType;
    }
    
    public void setContactType(int contactType)
    {
        this.contactType = contactType;
    }
    
    public String getContactCrcValue()
    {
        return contactCrcValue;
    }
    
    public void setContactCrcValue(String contactCrcValue)
    {
        this.contactCrcValue = contactCrcValue;
    }
    
    public int getContactModifyFlag()
    {
        return contactModifyFlag;
    }
    
    public void setContactModifyFlag(int contactModifyFlag)
    {
        this.contactModifyFlag = contactModifyFlag;
    }
    
    public String getContactUserId()
    {
        return contactUserId;
    }
    
    public void setContactUserId(String contactUserId)
    {
        this.contactUserId = contactUserId;
    }
    
    public int getAddFriendPrivacy()
    {
        return addFriendPrivacy;
    }
    
    public void setAddFriendPrivacy(int addFriendPrivacy)
    {
        this.addFriendPrivacy = addFriendPrivacy;
    }
    
    public int getHasPhoneNumber()
    {
        return hasPhoneNumber;
    }
    
    public void setHasPhoneNumber(int hasPhoneNumber)
    {
        this.hasPhoneNumber = hasPhoneNumber;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getFp()
    {
        return fp;
    }
    
    public void setFp(String fp)
    {
        this.fp = fp;
    }
    
    public String getAcf()
    {
        return acf;
    }
    
    public void setAcf(String acf)
    {
        this.acf = acf;
    }
    
    /**
     * 获取可打印的对象信息
     * 
     * @return 可打印的对象信息
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("contactLUID:").append(contactLUID);
        sb.append(" contactGUID:").append(contactGUID);
        sb.append(" contactSysId:").append(contactSysId);
        sb.append(" contactCrcValue:").append(contactCrcValue);
        sb.append(" contactSysId:").append(contactSysId);
        sb.append(" contactUserId:").append(contactUserId);
        sb.append(" addFriendPrivacy:").append(addFriendPrivacy);
        return sb.toString();
    }
    
    public void setSpellName(String spellName)
    {
        this.spellName = spellName;
    }
    
    public String getSpellName()
    {
        return spellName;
    }
    
    public void setInitialName(String initialName)
    {
        this.initialName = initialName;
    }
    
    public String getInitialName()
    {
        return initialName;
    }
}
