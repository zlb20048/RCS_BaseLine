/*
 * 文件名: ContactInfoModel.java
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

import java.util.List;

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 个人/好友信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class ContactInfoModel extends BaseContactModel
{
    /**
     * 归属地 : 本网
     */
    public static final int HOMELOCATION_LOCAL = 1;
    
    /**
     * 归属地 ： 异网
     */
    public static final int HOMELOCATION_REMOTE = 2;
    
    /**
     * 好友信息key值
     */
    public static final String CONTACT_INFO_MODEL = "contactInfoModel";
    
    /**
     * 用户绑定手机号key值
     */
    public static final String PRIMARY_MOBILE = "primaryMobile";
    
    /********  可能认识的人的原因   *************/
    
    /**
     * 
     * 同一个单位
     */
    public static final int SRS_TYPE_SAME_COMPANY = 1;
    
    /**
     * 相同的学校
     */
    public static final int SRS_TYPE_SAME_SCHOOL = 2;
    
    /**
     * 相同的城市
     */
    public static final int SRS_TYPE_SAME_CITY = 3;
    
    /**
     * 有我的联系方式
     */
    public static final int SRS_TYPE_CONTACT_BOOK = 4;
    
    /**
     * 有共同好友
     */
    public static final int SRS_TYPE_SHARE_FRIENDS = 5;
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 好友在HiTalk系统的唯一标识，如果为个人信息时，必须 friendSysId = userSysId
     */
    private String friendSysId;
    
    /**
     * 好友的IM通信JID（服务器对应的名称是JID：Jabber Identity） 如果为个人信息时, friendUserId = userId
     */
    private String friendUserId;
    
    /**
     * 好友的名，联通项目只用该字段纪录好友的真实姓名
     */
    // private String userName;
    
    /**
     * 好友的中间名，暂不使用
     */
    private String middleName;
    
    /**
     * 好友的姓，暂不使用
     */
    private String lastName;
    
    /**
     * 本地缓存的好友头像: 自定义头像，赋值（0） 系统头像，赋值（001-999）
     */
    // private String face;
    
    /**
     * 显示名称拼音，用来做排序
     */
    private String displaySpellName;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 好友绑定的手机号
     */
    private String primaryMobile;
    
    /**
     * 好友绑定的邮箱
     */
    private String primaryEmail;
    
    /**
     * 好友的签名
     */
    private String signature;
    
    /**
     * 好友的自我描述（服务器还没此字段名，后续可进行修正）
     */
    private String description;
    
    /**
     * 要绑定，但是还未验证的手机号码
     */
    private String toBeBindPrimaryMobile;
    
    /**
     * 要绑定，但是还未验证的eMail
     */
    private String toBeBindPrimaryEmail;
    
    /**
     * 好友各种名字的拼音串，便于本地好友的快速搜索； 例如：xiaoli,lisi,ligege 注：小李，李四，李哥哥
     */
    private String spellName;
    
    /**
     * 好友的各种名字的拼音首字母串，便于本地好友的快速搜索；例如：xl,ls,lgg
     */
    private String initialName;
    
    /**
     * 性别 0：未设置 1：女 2：男
     */
    private int gender = -1;
    
    /**
     * 生日，格式为yyyyMMdd。例如19800127
     */
    private String birthday;
    
    /**
     * 婚姻状况, 0:未知；1：单身；2：恋爱中；3：订婚；4：已婚
     */
    private int marriageStatus;
    
    /**
     * 好友年龄
     */
    private String age;
    
    /**
     * 好友生肖 0: 没有设置 1: 鼠 2: 牛 3: 虎 4: 兔 5: 龙 6: 蛇 7: 马 8: 羊 9: 猴 10: 鸡 11: 狗 12:
     * 猪
     */
    private int zodiac;
    
    /**
     *星座 0: 没有设置 1: 白羊座 2: 金牛座 3: 双子座 4: 巨蟹座 5: 狮子座 6: 处女座 7: 天秤座 8: 天蝎座 9:
     * 射手座10: 摩羯座 11: 水瓶座 12: 双鱼座
     */
    private int astro;
    
    /**
     * 血型 0: 没有设置 1: A型血 2: B型血 3: AB型血 4: O型血 5: Rh型血 6: MN型血 7: HLA型血
     */
    private int blood;
    
    /**
     * 爱好
     */
    private String hobby;
    
    /**
     * 公司
     */
    private String company;
    
    /**
     * 部门
     */
    private String deparment;
    
    /**
     * 职位
     */
    private String title;
    
    /**
     * 学校
     */
    private String school;
    
    /**
     * 用户的专业名称
     */
    private String course;
    
    /**
     * 入学时间、级、届
     */
    private String batch;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 街道
     */
    private String street;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 邮政信箱
     */
    private String building;
    
    /**
     * 用户等级
     */
    private int level;
    
    /**
     * 归属地
     */
    private int homeLocation;
    
    /**
     * 用户设置的好友昵称（用户设置的，比好友自己设置的昵称优先展现）
     */
    private String memoName;
    
    /**
     * 用户设置的多个好友手机（用户设置的，优先展现），格式：Phone1|phone2|phone3
     */
    private List<String> memoPhones;
    
    /**
     * 用户设置的多个好友邮箱（用户设置的，优先展现），格式：email1|email2|email3
     */
    private List<String> memoEmails;
    
    /**
     * 最后一次更新的时间戳，ZZZZ表示为时区，格式：YYYYMMDDHHMMSSZZZZ
     */
    private String lastUpdate;
    
    // 以下属性是 "关联属性"，需要关联操作。
    //插入个人/好友信息时不需要维护以下字段，查询时需维护。
    
    /**
     * 头像URL
     */
    private String faceUrl;
    
    /**
     * 头像缩略图数据
     */
    private byte[] faceBytes;
    
    /**
     * 好友分组ID
     */
    private String contactSectionId;
    
    /**
     * 好友分组名称
     */
    private String contactSectionName;
    
    /**
     * 好友分组说明
     */
    private String contactSectionNotes;
    
    /**
     * 可能认识的人的原因
     */
    private int srsType;
    
    /**
     * 用户被加好友控制策略字段取值说明： 取值为1：允许所有用户经我确认后加我为好友 取值为2：仅允许通讯录中的用户经我确认后加我为好友
     * 取值为3：不允许任何人加我为好友 取值为4：允许绑定了手机号码的用户经我确认后加我为好友
     */
    private int friendPrivacy;
    
    /**
     * 用户被加好友控制策略字段取值说明： 取值为1：允许所有用户经我确认后加我为好友 取值为2：仅允许通讯录中的用户经我确认后加我为好友
     * 取值为3：不允许任何人加我为好友 取值为4：允许绑定了手机号码的用户经我确认后加我为好友
     */
    private int autoConfirmFriend;
    
    /**
     * 该用户当前在线状态。 1：在线 2：离线 Online
     */
    private int online;
    
    /**
     * 与可能认识的人共同用户的好友的个数
     */
    private double srsCommonNum;
    
    /**
     * 好友关系标识   1：好友；0：非好友
     */
    private int friendStatus;
    
    /**
     * 默认构造方法
     */
    public ContactInfoModel()
    {
        super();
    }
    
    public int getFriendStatus()
    {
        return friendStatus;
    }
    
    public void setFriendStatus(int friendStatus)
    {
        this.friendStatus = friendStatus;
    }
    
    public double getSrsCommonNum()
    {
        return srsCommonNum;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param srsCommonNum
     *            共同好友个数
     */
    public void setSrsCommonNum(double srsCommonNum)
    {
        this.srsCommonNum = srsCommonNum;
    }
    
    public String getFriendSysId()
    {
        return friendSysId;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param friendSysId
     *            好友系统标识
     */
    public void setFriendSysId(String friendSysId)
    {
        this.friendSysId = friendSysId;
    }
    
    public String getFriendUserId()
    {
        return friendUserId;
    }
    
    /**
     * 设置userId
     * 
     * @param friendUserId
     *            friendUserId
     */
    public void setFriendUserId(String friendUserId)
    {
        this.friendUserId = friendUserId;
        
        // 如果备注名、真实姓名、昵称都为空
        if (StringUtil.isNullOrEmpty(memoName)
                && StringUtil.isNullOrEmpty(nickName))
        {
            // 如果昵称不为空
            if (!StringUtil.isNullOrEmpty(friendUserId))
            {
                super.setDisplayName(friendUserId);
            }
        }
    }
    
    // public String getUserName()
    // {
    // return userName;
    // }
    
    // public void setUserName(String userName)
    // {
    //
    // // 用户备注名>好友真实姓名>好友昵称>好友HiTalkID
    // this.userName = userName;
    //
    // // 如果备注名为空
    // if (StringUtil.isNullOrEmpty(memoName))
    // {
    //
    // // 如果真是姓名不为空
    // if (!StringUtil.isNullOrEmpty(userName))
    // {
    // super.setDisplayName(userName);
    // }
    // }
    // }
    
    /**
     * 获取用户姓名
     * 
     * @return 用户真实姓名
     */
    public String getMiddleName()
    {
        return middleName;
    }
    
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public String getDisplaySpellName()
    {
        return displaySpellName;
    }
    
    public void setDisplaySpellName(String displaySpellName)
    {
        this.displaySpellName = displaySpellName;
    }
    
    public String getNickName()
    {
        return nickName == null ? friendUserId : nickName;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param nickName
     *            昵称
     */
    public void setNickName(String nickName)
    {
        // 用户备注名>好友真实姓名>好友昵称>好友HiTalkID
        this.nickName = nickName;
        
        // 如果备注名为空
        if (StringUtil.isNullOrEmpty(memoName))
        {
            // 如果昵称不为空
            if (!StringUtil.isNullOrEmpty(nickName))
            {
                super.setDisplayName(nickName);
            }
        }
    }
    
    public String getPrimaryMobile()
    {
        return primaryMobile;
    }
    
    public void setPrimaryMobile(String primaryMobile)
    {
        this.primaryMobile = primaryMobile;
    }
    
    public String getPrimaryEmail()
    {
        return primaryEmail;
    }
    
    public void setPrimaryEmail(String primaryEmail)
    {
        this.primaryEmail = primaryEmail;
    }
    
    public String getSignature()
    {
        return signature;
    }
    
    public void setSignature(String signature)
    {
        this.signature = signature;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getToBeBindPrimaryMobile()
    {
        return toBeBindPrimaryMobile;
    }
    
    public void setToBeBindPrimaryMobile(String toBeBindPrimaryMobile)
    {
        this.toBeBindPrimaryMobile = toBeBindPrimaryMobile;
    }
    
    public String getToBeBindPrimaryEmail()
    {
        return toBeBindPrimaryEmail;
    }
    
    public void setToBeBindPrimaryEmail(String toBeBindPrimaryEmail)
    {
        this.toBeBindPrimaryEmail = toBeBindPrimaryEmail;
    }
    
    public String getSpellName()
    {
        return spellName;
    }
    
    public void setSpellName(String spellName)
    {
        this.spellName = spellName;
    }
    
    public String getInitialName()
    {
        return initialName;
    }
    
    public void setInitialName(String initialName)
    {
        this.initialName = initialName;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @return int
     */
    public int getGender()
    {
        if (gender >= 0 && gender <= 2)
        {
            return gender;
        }
        return 0;
    }
    
    public void setGender(int gender)
    {
        this.gender = gender;
    }
    
    public String getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }
    
    public int getMarriageStatus()
    {
        return marriageStatus;
    }
    
    public void setMarriageStatus(int marriageStatus)
    {
        this.marriageStatus = marriageStatus;
    }
    
    public String getAge()
    {
        return age == null ? "" : age;
    }
    
    public void setAge(String age)
    {
        this.age = age;
    }
    
    public int getZodiac()
    {
        return zodiac;
    }
    
    public void setZodiac(int zodiac)
    {
        this.zodiac = zodiac;
    }
    
    public int getAstro()
    {
        return astro;
    }
    
    public void setAstro(int astro)
    {
        this.astro = astro;
    }
    
    public int getBlood()
    {
        return blood;
    }
    
    public void setBlood(int blood)
    {
        this.blood = blood;
    }
    
    public String getHobby()
    {
        return hobby;
    }
    
    public void setHobby(String hobby)
    {
        this.hobby = hobby;
    }
    
    public String getCompany()
    {
        return company;
    }
    
    public void setCompany(String company)
    {
        this.company = company;
    }
    
    public String getDeparment()
    {
        return deparment;
    }
    
    public void setDeparment(String deparment)
    {
        this.deparment = deparment;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getSchool()
    {
        return school;
    }
    
    public void setSchool(String school)
    {
        this.school = school;
    }
    
    public String getCourse()
    {
        return course;
    }
    
    public void setCourse(String course)
    {
        this.course = course;
    }
    
    public String getBatch()
    {
        return batch;
    }
    
    public void setBatch(String batch)
    {
        this.batch = batch;
    }
    
    public String getCountry()
    {
        return country;
    }
    
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    public String getProvince()
    {
        return province;
    }
    
    public void setProvince(String province)
    {
        this.province = province;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    
    public String getStreet()
    {
        return street;
    }
    
    public void setStreet(String street)
    {
        this.street = street;
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public String getPostalCode()
    {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }
    
    public String getBuilding()
    {
        return building;
    }
    
    public void setBuilding(String building)
    {
        this.building = building;
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
    }
    
    public int getHomeLocation()
    {
        return homeLocation;
    }
    
    public void setHomeLocation(int homeLocation)
    {
        this.homeLocation = homeLocation;
    }
    
    public String getMemoName()
    {
        return memoName;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param memoName
     *            备注
     */
    public void setMemoName(String memoName)
    {
        // 用户备注名>好友真实姓名>好友昵称>好友HiTalkID
        this.memoName = memoName;
        
        // 如果备注名不为空
        if (!StringUtil.isNullOrEmpty(memoName))
        {
            super.setDisplayName(memoName);
        }
    }
    
    public List<String> getMemoPhones()
    {
        return memoPhones;
    }
    
    public void setMemoPhones(List<String> memoPhones)
    {
        this.memoPhones = memoPhones;
    }
    
    public List<String> getMemoEmails()
    {
        return memoEmails;
    }
    
    public void setMemoEmails(List<String> memoEmails)
    {
        this.memoEmails = memoEmails;
    }
    
    public String getLastUpdate()
    {
        return lastUpdate;
    }
    
    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }
    
    public String getFaceUrl()
    {
        return faceUrl;
    }
    
    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }
    
    public byte[] getFaceBytes()
    {
        return faceBytes;
    }
    
    public void setFaceBytes(byte[] faceBytes)
    {
        this.faceBytes = faceBytes;
    }
    
    public String getContactSectionId()
    {
        return contactSectionId;
    }
    
    public void setContactSectionId(String contactSectionId)
    {
        this.contactSectionId = contactSectionId;
    }
    
    public String getContactSectionName()
    {
        return contactSectionName;
    }
    
    public void setContactSectionName(String contactSectionName)
    {
        this.contactSectionName = contactSectionName;
    }
    
    public String getContactSectionNotes()
    {
        return contactSectionNotes;
    }
    
    public void setContactSectionNotes(String contactSectionNotes)
    {
        this.contactSectionNotes = contactSectionNotes;
    }
    
    public int getSrsType()
    {
        return srsType;
    }
    
    /**
     * 设置可能认识人的原因
     * 
     * @param srsType
     *            原因的类型
     */
    public void setSrsType(int srsType)
    {
        // 4的优先级最高
        if (SRS_TYPE_CONTACT_BOOK != this.srsType)
        {
            this.srsType = srsType;
        }
    }
    
    public int getFriendPrivacy()
    {
        return this.friendPrivacy;
    }
    
    public void setFriendPrivacy(int friendPrivacy)
    {
        this.friendPrivacy = friendPrivacy;
    }
    
    public int getAutoConfirmFriend()
    {
        return this.autoConfirmFriend;
    }
    
    public void setAutoConfirmFriend(int autoConfirmFriend)
    {
        this.autoConfirmFriend = autoConfirmFriend;
    }
    
    public int getOnline()
    {
        return this.online;
    }
    
    public void setOnline(int online)
    {
        this.online = online;
    }
    
    /**
     * 比较方法 为了不让子类复用equals选择了别的方法名
     * 
     * @param obj
     *            Object
     * @return boolean
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean profileEquals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ContactInfoModel other = (ContactInfoModel) obj;
        if (faceUrl == null)
        {
            if (other.faceUrl != null)
            {
                return false;
            }
        }
        else if (!faceUrl.equals(other.faceUrl))
        {
            return false;
        }
        if (faceBytes == null)
        {
            if (other.faceBytes != null)
            {
                return false;
            }
        }
        else if (!faceBytes.equals(other.faceBytes))
        {
            return false;
        }
        if (address == null)
        {
            if (other.address != null)
            {
                return false;
            }
        }
        else if (!address.equals(other.address))
        {
            return false;
        }
        if (!age.equals(other.age))
        {
            return false;
        }
        if (astro != other.astro)
        {
            return false;
        }
        if (batch == null)
        {
            if (other.batch != null)
            {
                return false;
            }
        }
        else if (!batch.equals(other.batch))
        {
            return false;
        }
        if (birthday == null)
        {
            if (other.birthday != null)
            {
                return false;
            }
        }
        else if (!birthday.equals(other.birthday))
        {
            return false;
        }
        // Log.e("proequals", "836");
        if (blood != other.blood)
        {
            return false;
        }
        if (building == null)
        {
            if (other.building != null)
            {
                return false;
            }
        }
        else if (!building.equals(other.building))
        {
            return false;
        }
        if (city == null)
        {
            if (other.city != null)
            {
                return false;
            }
        }
        else if (!city.equals(other.city))
        {
            return false;
        }
        if (company == null)
        {
            if (other.company != null)
            {
                return false;
            }
        }
        else if (!company.equals(other.company))
        {
            return false;
        }
        if (contactSectionId == null)
        {
            if (other.contactSectionId != null)
            {
                return false;
            }
        }
        else if (!contactSectionId.equals(other.contactSectionId))
        {
            return false;
        }
        if (contactSectionName == null)
        {
            if (other.contactSectionName != null)
            {
                return false;
            }
        }
        else if (!contactSectionName.equals(other.contactSectionName))
        {
            return false;
        }
        // Log.e("proequals", "872");
        if (contactSectionNotes == null)
        {
            if (other.contactSectionNotes != null)
            {
                return false;
            }
        }
        else if (!contactSectionNotes.equals(other.contactSectionNotes))
        {
            return false;
        }
        // Log.e("proequals", "873");
        // Log.e("proequals", "873"+country+other.country);
        if (country == null)
        {
            if (other.country != null && !"中国".equals(other.country))
            {
                return false;
            }
        }
        else if (!country.equals(other.country))
        {
            return false;
        }
        // Log.e("proequals", "8731");
        // Log.e("proequals", "8731"+course+other.course);
        if (course == null)
        {
            if (other.course != null)
            {
                return false;
            }
        }
        else if (!course.equals(other.course))
        {
            return false;
        }
        // Log.e("proequals", "874");
        if (deparment == null)
        {
            if (other.deparment != null)
            {
                return false;
            }
        }
        else if (!deparment.equals(other.deparment))
        {
            return false;
        }
        if (description == null)
        {
            if (other.description != null)
            {
                return false;
            }
        }
        else if (!description.equals(other.description))
        {
            return false;
        }
        // Log.e("proequals", "875");
        //        if (displaySpellName == null)
        //        {
        //            if (other.displaySpellName != null)
        //            {
        //                return false;
        //            }
        //        }
        //        else if (!displaySpellName.equals(other.displaySpellName))
        //        {
        //            return false;
        //        }
        // Log.e("proequals", "916");
        // Log.e("proequals", ""+face);
        // Log.e("proequals", ""+other.face);
        
        // if (face == null)
        // {
        // if (other.face != null)
        // {
        // return false;
        // }
        // }
        // else if (!face.equals(other.face))
        // {
        // return false;
        // }
        
        // Log.e("proequals", "a");
        // if (!Arrays.equals(faceBytes,
        // other.faceBytes))
        // return false;
        // Log.e("proequals", "b");
        // Log.e("proequals", "930");
        // Log.e("proequals", ""+faceUrl);
        // Log.e("proequals", ""+other.faceUrl);
        // if (faceUrl == null)
        // {
        // if (other.faceUrl != null)
        // return false;
        // }
        // else if (!faceUrl.equals(other.faceUrl))
        // return false;
        // Log.e("proequals", "935");
        if (friendSysId == null)
        {
            if (other.friendSysId != null)
            {
                return false;
            }
        }
        else if (!friendSysId.equals(other.friendSysId))
        {
            return false;
        }
        if (friendUserId == null)
        {
            if (other.friendUserId != null)
            {
                return false;
            }
        }
        else if (!friendUserId.equals(other.friendUserId))
        {
            return false;
        }
        if (gender != other.gender)
        {
            return false;
        }
        if (hobby == null)
        {
            if (other.hobby != null)
            {
                return false;
            }
        }
        else if (!hobby.equals(other.hobby))
        {
            return false;
        }
        // Log.e("proequals", "955");
        if (initialName == null)
        {
            if (other.initialName != null)
            {
                return false;
            }
        }
        else if (!initialName.equals(other.initialName))
        {
            return false;
        }
        // Log.e("proequals", "956");
        if (lastName == null)
        {
            if (other.lastName != null)
            {
                return false;
            }
        }
        else if (!lastName.equals(other.lastName))
        {
            return false;
        }
        // Log.e("proequals", "957"+lastUpdate+other.lastUpdate);
        // if (lastUpdate == null)
        // {
        // if (other.lastUpdate != null)
        // {
        // return false;
        // }
        // }
        // else if (!lastUpdate.equals(other.lastUpdate))
        // {
        // return false;
        // }
        // Log.e("proequals", "958");
        if (level != other.level)
        {
            return false;
        }
        // Log.e("proequals", "959");
        if (marriageStatus != other.marriageStatus)
        {
            return false;
        }
        // Log.e("proequals", "1000");
        if (memoEmails == null)
        {
            if (other.memoEmails != null)
            {
                return false;
            }
        }
        else if (!memoEmails.equals(other.memoEmails))
        {
            return false;
        }
        if (memoName == null)
        {
            if (other.memoName != null)
            {
                return false;
            }
        }
        else if (!memoName.equals(other.memoName))
        {
            return false;
        }
        if (memoPhones == null)
        {
            if (other.memoPhones != null)
            {
                return false;
            }
        }
        else if (!memoPhones.equals(other.memoPhones))
        {
            return false;
        }
        if (middleName == null)
        {
            if (other.middleName != null)
            {
                return false;
            }
        }
        else if (!middleName.equals(other.middleName))
        {
            return false;
        }
        if (nickName == null)
        {
            if (other.nickName != null)
            {
                return false;
            }
        }
        else if (!nickName.equals(other.nickName))
        {
            return false;
        }
        if (postalCode == null)
        {
            if (other.postalCode != null)
            {
                return false;
            }
        }
        else if (!postalCode.equals(other.postalCode))
        {
            return false;
        }
        if (primaryEmail == null)
        {
            if (other.primaryEmail != null)
            {
                return false;
            }
        }
        else if (!primaryEmail.equals(other.primaryEmail))
        {
            return false;
        }
        // Log.e("proequals", "1005");
        if (primaryMobile == null)
        {
            if (other.primaryMobile != null)
            {
                return false;
            }
        }
        else if (!StringUtil.fixPortalPhoneNumber(primaryMobile)
                .equals(StringUtil.fixPortalPhoneNumber(other.primaryMobile)))
        {
            return false;
        }
        if (province == null)
        {
            if (other.province != null)
            {
                return false;
            }
        }
        else if (!province.equals(other.province))
        {
            return false;
        }
        if (school == null)
        {
            if (other.school != null)
            {
                return false;
            }
        }
        else if (!school.equals(other.school))
        {
            return false;
        }
        if (signature == null)
        {
            if (other.signature != null)
            {
                return false;
            }
        }
        else if (!signature.equals(other.signature))
        {
            return false;
        }
        if (spellName == null)
        {
            if (other.spellName != null)
            {
                return false;
            }
        }
        else if (!spellName.equals(other.spellName))
        {
            return false;
        }
        // Log.e("proequals", "1010");
        if (srsType != other.srsType)
        {
            return false;
        }
        if (street == null)
        {
            if (other.street != null)
            {
                return false;
            }
        }
        else if (!street.equals(other.street))
        {
            return false;
        }
        if (title == null)
        {
            if (other.title != null)
            {
                return false;
            }
        }
        else if (!title.equals(other.title))
        {
            return false;
        }
        //        if (toBeBindPrimaryEmail == null)
        //        {
        //            if (other.toBeBindPrimaryEmail != null)
        //            {
        //                return false;
        //            }
        //        }
        //        else if (!toBeBindPrimaryEmail.equals(other.toBeBindPrimaryEmail))
        //        {
        //            return false;
        //        }
        if (toBeBindPrimaryMobile == null)
        {
            if (other.toBeBindPrimaryMobile != null)
            {
                return false;
            }
        }
        else if (!toBeBindPrimaryMobile.equals(other.toBeBindPrimaryMobile))
        {
            return false;
        }
        // if (userName == null)
        // {
        // if (other.userName != null)
        // {
        // return false;
        // }
        // }
        // else if (!userName.equals(other.userName))
        // {
        // return false;
        // }
        if (zodiac != other.zodiac)
        {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * 个人消息字符串格式<BR>
     * 
     * @return 个人消息字符串
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(" friendSysId:").append(friendSysId);
        sb.append(" friendUserId:").append(friendUserId);
        sb.append(" displayName:").append(displaySpellName);
        sb.append(" middleName:").append(middleName);
        sb.append(" lastName:").append(lastName);
        sb.append(" nickName:").append(nickName);
        sb.append(" primaryMobile:").append(primaryMobile);
        sb.append(" primaryEmail:").append(primaryEmail);
        sb.append(" signature:").append(signature);
        sb.append(" toBeBindPrimaryMobile:").append(toBeBindPrimaryMobile);
        sb.append(" toBeBindPrimaryEmail:").append(toBeBindPrimaryEmail);
        sb.append(" spellName:").append(spellName);
        sb.append(" initialName:").append(initialName);
        sb.append(" gender:").append(gender);
        sb.append(" birthday:").append(birthday);
        sb.append(" marriageStatus:").append(marriageStatus);
        sb.append(" age:").append(age);
        sb.append(" zodiac:").append(zodiac);
        sb.append(" astro:").append(astro);
        sb.append(" blood:").append(blood);
        sb.append(" hobby:").append(hobby);
        sb.append(" company:").append(company);
        sb.append(" deparment:").append(deparment);
        sb.append(" title:").append(title);
        sb.append(" school:").append(school);
        sb.append(" course:").append(course);
        sb.append(" batch:").append(batch);
        sb.append(" country:").append(country);
        sb.append(" province:").append(province);
        sb.append(" city:").append(city);
        sb.append(" street:").append(street);
        sb.append(" address:").append(address);
        sb.append(" postalCode:").append(postalCode);
        sb.append(" building:").append(building);
        sb.append(" faceUrl:").append(faceUrl);
        sb.append(" faceBytes:").append(faceBytes);
        sb.append(" contactSectionId:").append(contactSectionId);
        sb.append(" contactSectionName:").append(contactSectionName);
        sb.append(" contactSectionNotes:").append(contactSectionNotes);
        return sb.toString();
    }
}
