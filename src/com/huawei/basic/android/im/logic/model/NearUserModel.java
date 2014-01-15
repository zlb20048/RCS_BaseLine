/*
 * 文件名: NearUserModel.java 版 权： Copyright Huawei Tech. Co. Ltd. All Rights
 * Reserved. 描 述: [该类的简要描述] 创建人: raulxiao 创建时间:2012-03-23 修改人： 修改时间: 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 
 * 查找附近的人结果xml字段<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Mar 23, 2012]
 */
@Root(name = "result", strict = false)
public class NearUserModel
{
    @Attribute(name = "resultCode", required = false)
    private int resultCode;
    
    @Element(name = "array", required = false)
    private UserLocationList userLocationList;
    
    /**
     * 
     * 返回结果码<BR>
     * @return int
     */
    public int getResultCode()
    {
        
        // 如果为207000007, 说明删除位置成功,返回0
        if (resultCode == 207000007)
        {
            resultCode = 207000007;
        }
        else if (resultCode == 207000001)
        {
            resultCode = 207000001;
        }
        else if (resultCode == 207000005)
        {
            resultCode = 207000005;
        }
        else if (resultCode == 0)
        {
            resultCode = 0;
        }
        
        return resultCode;
    }
    
    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return List
     */
    public List<UserLocationInfo> getUserLocationList()
    {
        if (userLocationList == null)
        {
            return null;
        }
        
        return userLocationList.getUserLocationInfoList();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return int
     */
    public int getListLength()
    {
        if (userLocationList == null)
        {
            return 0;
        }
        
        return userLocationList.getLength();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    @Root(name = "array", strict = false)
    public static class UserLocationList
    {
        @Attribute(name = "length")
        private int length;
        
        @ElementList(entry = "userLocationInfo", required = false, inline = true)
        private List<UserLocationInfo> userLocationInfoList;
        
        public int getLength()
        {
            return length;
        }
        
        /**
         * 
         * [一句话功能简述]<BR>
         * [功能详细描述]
         * @return List<UserLocationInfo>
         */
        public List<UserLocationInfo> getUserLocationInfoList()
        {
            if (userLocationInfoList == null || length == 0)
            {
                return null;
            }
            
            // 将List转换成数组
            UserLocationInfo[] locationList = new UserLocationInfo[length];
            userLocationInfoList.toArray(locationList);
            
            // 对数据进行排序
            InsertSorter<UserLocationInfo> sorter = new InsertSorter<UserLocationInfo>();
            sorter.sort(locationList, 0, length);
            
            // 返回排序后的数据, 将数组转换成List
            return Arrays.asList(locationList);
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    @Root(name = "userLocationInfo", strict = false)
    public static class UserLocationInfo implements
            Comparable<UserLocationInfo>
    {
        @Element(name = "account", required = false)
        private String account;
        
        @Element(name = "displayName", required = false)
        private String displayName;
        
        @Element(name = "note", required = false)
        private String note;
        
        @Element(name = "userLogoURL", required = false)
        private String userLogoURL;
        
        @Element(name = "gender", required = false)
        private int gender;
        
        @Element(name = "userDistance", required = false)
        private double userDistance;
        
        @Element(name = "lastUpdate", required = false)
        private String lastUpdate;
        
        @Element(name = "isFriend", required = false)
        private int isFriend;
        
        public int getFriendStatus()
        {
            return isFriend;
        }
        
        public void setFriendStatus(int friendStatus)
        {
            this.isFriend = friendStatus;
        }
        
        public String getAccount()
        {
            return account;
        }
        
        public String getDisplayName()
        {
            return displayName;
        }
        
        public String getNote()
        {
            return note;
        }
        
        public String getUserLogoURL()
        {
            return userLogoURL;
        }
        
        public int getGender()
        {
            return gender;
        }
        
        public double getUserDistance()
        {
            return userDistance;
        }
        
        public String getLastUpdate()
        {
            return lastUpdate;
        }
        
        /**
         * [一句话功能简述]<BR>
         * [功能详细描述]
         * @param another UserLocationInfo
         * @return int
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        
        @Override
        public int compareTo(UserLocationInfo another)
        {
            double dis = another.getUserDistance();
            if (this.userDistance > dis)
            {
                return 1;
            }
            
            if (this.userDistance < dis)
            {
                return -1;
            }
            
            return 0;
        }
        
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    public static class InsertSorter<E extends Comparable<E>>
    {
        
        /**
         * 
         * [一句话功能简述]<BR>
         * [功能详细描述]
         * @param array 数组
         * @param from from
         * @param len 长度
         */
        public void sort(E[] array, int from, int len)
        {
            E tmp = null;
            for (int i = from + 1; i < from + len; ++i)
            {
                tmp = array[i];
                int j = i;
                
                for (; j > from; --j)
                {
                    if (tmp.compareTo(array[j - 1]) < 0)
                    {
                        array[j] = array[j - 1];
                    }
                    else
                    {
                        break;
                    }
                }
                
                array[j] = tmp;
            }
        }
        
    }
}
