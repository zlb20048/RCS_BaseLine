/*
 * 文件名: Match.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

/**
 * 字符匹配类<BR>
 * [功能详细描述]
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2011-11-4]
 */
public class Match
{
    
    /**
     * 
     * 多项匹配<BR>
     * 逐一循环matchings，直到找到匹配项<br>
     * key如果是中文或纯数字，则匹配规则是该匹配项是否包含该中文或纯数字；key是字母数字等，则按照混拼的规则进行匹配
     * 
     * @param key 匹配关键字
     * @param matchings 待匹配项
     * @return 有匹配项即返回true
     */
    public static boolean match(String key, String... matchings)
    {
        
        boolean isMatched = false;
        
        if (matchings != null && key != null)
        {
            // 匹配关键字是否包含中文或者是纯数字
            boolean matchByChineseOrNumeric = StringUtil.hasChinese(key)
                    || StringUtil.isNumeric(key);
            for (String matching : matchings)
            {
                
                // 按中文规则匹配
                if (matchByChineseOrNumeric)
                {
                    if(!StringUtil.isNullOrEmpty(matching)
                            && matching.contains(key))
                    {
                        isMatched = true;
                    }
                }
                
                // 按字母数字的混拼规则匹配
                else
                {
                    isMatched = nameMatch(matching, key);
                }
                // 如果已经找到匹配项，则不再继续匹配
                if (isMatched)
                {
                    break;
                }
                else
                {
                    if (!StringUtil.isNullOrEmpty(matching)
                            && matching.contains(key))
                    {
                        isMatched = true;
                        break;
                    }
                }
            }
        }
        return isMatched;
    }
    
    /**
     * name matched or not.
     * 
     * @param srcData spell name array
     * @param contraint number inputed by user.
     * @return name matched or not
     */
    public static boolean nameMatch(String srcData, String contraint)
    {
        if (contraint == null || contraint.length() == 0)
        {
            return true;
        }
        return srcData != null
                && srcData.toUpperCase().contains(contraint.toUpperCase());
    }
    
}
