/*
 * @(#)LocaleUtil.java 11-8-28 下午11:46 CopyRight 2011. All rights reserved
 */
package com.huawei.basic.android.im.utils;

import java.text.Collator;
import java.util.Locale;

/**
 *
 * 国际化操作封装
 *
 * @author Kelvin Van
 * @version [ME WOYOUClient_Handset V100R001C04SPC002, 2011-10-17]
 */
public abstract class LocaleUtil
{

    /**
     * 是否包含中国区域
     *
     * @return true代表包含中国区域, false则否
     */
    public static boolean hasChinaLocale()
    {
        final Locale[] locale = Collator.getAvailableLocales();
        for (int i = 0; i < locale.length; i++)
        {
            if (locale[i].equals(Locale.CHINA))
            {
                return true;
            }
        }
        return false;
    }
}
