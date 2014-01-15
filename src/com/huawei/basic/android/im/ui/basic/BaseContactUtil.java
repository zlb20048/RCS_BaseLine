/*
 * 文件名: BaseContactUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.logic.model.BaseContactModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 联系人UI显示工具类<BR>
 * 提供排序、获取首字母等方法
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2011-10-17]
 */
public class BaseContactUtil
{
    
    /**
     * debug tag
     */
    private static final String TAG = "BaseContactUtil";
    
    /**
     * 生肖
     */
    private static String[] sZodiac = null;
    
    /**
     * 星座
     */
    private static String[] sAstro = null;
    
    /**
     * 血型
     */
    private static String[] sBlood = null;
    
    /**
     * 
     * 获取生肖对应的字符串<BR>
     * [功能详细描述]
     * 
     * @param context context
     * @param zodiac 生肖
     * @return 生肖对应的字符串
     */
    public static synchronized String getZodiac(Context context, int zodiac)
    {
        if (sZodiac == null)
        {
            sZodiac = context.getResources()
                    .getStringArray(R.array.zodiac_item);
        }
        if (zodiac <= 0)
        {
            return "";
        }
        else
        {
            return sZodiac[zodiac];
        }
        
    }
    
    /**
     * 
     * 获取星座对应的字符串<BR>
     * [功能详细描述]
     * 
     * @param context context
     * @param astro 星座
     * @return  星座对应的字符串
     */
    public static synchronized String getAstro(Context context, int astro)
    {
        if (sAstro == null)
        {
            sAstro = context.getResources().getStringArray(R.array.astro_item);
        }
        if (astro <= 0)
        {
            return "";
        }
        else
        {
            return sAstro[astro];
        }
        
    }
    
    /**
     * 
     * 获取血型对应的字符串<BR>
     * [功能详细描述]
     * 
     * @param context context
     * @param blood 血型
     * @return 血型对应的字符串
     */
    public static synchronized String getBlood(Context context, int blood)
    {
        if (sBlood == null)
        {
            sBlood = context.getResources().getStringArray(R.array.blood_array);
        }
        if (blood <= 0)
        {
            return "";
        }
        else
        {
            return sBlood[blood];
        }
        
    }
    
    /**
     * 比较器<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2011-10-17]
     */
    public static class BaseContactComparator implements
            Comparator<BaseContactModel>, Serializable
    {
        
        /**
         * 序列化ID
         */
        private static final long serialVersionUID = 1L;
        
        /**
         * 
         * 比较函数<BR>
         * [功能详细描述]
         * 
         * @param contactOne BaseContactModel
         * @param contactTwo BaseContactModel
         * @return a negative value if contactOne is less than contactTwo, 0 if
         *         they are equal and a positive value if contactOne is greater
         *         than contactTwo.
         * 
         * @see java.util.Comparator#
         * compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(BaseContactModel contactOne,
                BaseContactModel contactTwo)
        {
            Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
            String pyOne = contactOne.getSimplePinyin() == null ? ""
                    : contactOne.getSimplePinyin();
            String pyTwo = contactTwo.getSimplePinyin() == null ? ""
                    : contactTwo.getSimplePinyin();
            CollationKey first = (CollationKey) (cmp.getCollationKey(pyOne) == null ? ""
                    : cmp.getCollationKey(pyOne));
            CollationKey two = (CollationKey) (cmp.getCollationKey(pyTwo) == null ? ""
                    : cmp.getCollationKey(pyTwo));
            return cmp.compare(first.getSourceString(), two.getSourceString());
        }
        
    }
    
    /**
     * 
     * 将联系人进行排序<BR>
     * 1.移除没有display name的联系人
     *  2.按BaseContactComparator中规则对list进行排序
     * 
     * @param contactList List<BaseContactModel>
     */
    public static void sort(List<? extends BaseContactModel> contactList)
    {
        
        if (contactList != null)
        {
            BaseContactComparator comparator = new BaseContactComparator();
            try
            {
                Collections.sort(contactList, comparator);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e(TAG,
                        "Collections.sort failed, the msg is: "
                                + ex.getMessage());
            }
        }
    }
    
    /**
     * 
     * 将联系人列表转换为方便显示的列表<BR>
     * 1、将联系人按字母排序； 
     * 2、在以各字母开头的联系人中插入英文字母的String，
     * 形成BaseContactModel和String组成的list
     * 
     * @param contactList List<BaseContactModel>
     * @return 供显示使用的list
     */
    public static ArrayList<Object> contactListForDisplay(
            List<? extends BaseContactModel> contactList)
    {
        ArrayList<Object> contactListForDisplay = null;
        if (contactList != null)
        {
            
            // 排序
            sort(contactList);
            
            if (contactList.size() > 0)
            {
                contactListForDisplay = new ArrayList<Object>();
                // 将含“#”的放到最下面
                List<Object> bottomList = new ArrayList<Object>();
                for (BaseContactModel contact : contactList)
                {
                    char initialLetter;
                    
                    // 如果display name为空，initial letter设置为“#”
                    if (StringUtil.isNullOrEmpty(contact.getSimplePinyin()))
                    {
                        initialLetter = '#';
                    }
                    
                    // 取display name对应拼音的首字母（需大写），并设置到name head letter.
                    else
                    {
                        initialLetter = contact.getSimplePinyin()
                                .toUpperCase()
                                .charAt(0);
                        if (initialLetter > 'Z' || initialLetter < 'A')
                        {
                            initialLetter = '#';
                        }
                    }
                    contact.setNameHeadLetter(String.valueOf(initialLetter));
                    if ("#".equals(contact.getNameHeadLetter()))
                    {
                        if (!bottomList.contains("#"))
                        {
                            bottomList.add("#");
                        }
                        bottomList.add(contact);
                    }
                    else
                    {
                        
                        // 如果contactListForDisplay中没有此拼音对应的首字母字符串，
                        //则添加该字符串到该list中
                        if (!contactListForDisplay.contains(contact.getNameHeadLetter()))
                        {
                            contactListForDisplay.add(contact.getNameHeadLetter());
                        }
                        contactListForDisplay.add(contact);
                    }
                }
                contactListForDisplay.addAll(bottomList);
            }
        }
        return contactListForDisplay;
    }
    
    /**
     * 1: 同事<Br>
     * 2: 同学<Br>
     * 3: 同城<Br>
     * 4: 其通讯录中拥有我的联系方式（手机号码或Email地址），<Br>
     * 但自己的通讯录中没有其联系人方式<Br>
     * 5: 与其有共同的好友<Br>
     * 
     * 根据可能原因的类型获得相应的字符串<BR>
     * [功能详细描述]
     * 
     * @param context context
     * @param type 原因类型
     * @param num 共同好友数目
     * @return 对应的字符串
     */
    public static String getRecommandReason(Context context, int type, double num)
    {
        switch (type)
        {
            case 1:
                return context.getResources().getString(R.string.reason_1);
            case 2:
                return context.getResources().getString(R.string.reason_2);
            case 3:
                return context.getResources().getString(R.string.reason_3);
            case 4:
                return context.getResources().getString(R.string.reason_4);
            case 5:
                if (num > 1)
                {
                    return String.format(context.getResources()
                            .getString(R.string.reason_6),
                            num);
                }
                else
                {
                    return context.getResources().getString(R.string.reason_5);
                }
            default:
                break;
        }
        return null;
    }
    
    /**
     * 
     * 根据距离显示距离字符串<BR>
     * [功能详细描述]
     * @param context context
     * @param distance 距离
     * @return 距离字符串
     */
    public static String getDistance(Context context, double distance)
    {
        if (distance < 900)
        {
            // 将位置转换成100的倍数情况，处理成100米以内的形式
            int showDistance = (int) (distance / 100 + 1) * 100;
            
            return String.format(context.getString(R.string.find_by_location_m_in),
                    showDistance);
        }
        else if (distance < 10000)
        {
            // 转换成1000的倍数，显示成1公里以内的形式
            int showDistance = (int) (distance / 1000 + 1);
            
            return String.format(context.getString(R.string.find_by_location_km_in),
                    showDistance);
        }
        
        // 大于10公里的，都显示10公里以外
        return String.format(context.getString(R.string.find_by_location_km_out),
                10);
        
    }
}
