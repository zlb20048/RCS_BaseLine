/*
 * 文件名: BaseContactModel.java
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

/**
 * 联系人基类model<BR>
 * 主要用于PhoneContactIndexModel和ContactInfoModel的相同的排序处理
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class BaseContactModel implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 联系人显示的名称 显示名称，
     * 查询时逻辑层根据 '备注名称'、'真实名称'、'昵称'、'账号'的优先级计算
     */
    private String displayName;

    /**
     * 联系人姓名的简拼
     */
    private String simplePinyin;

    /**
     * 名字首个字符(拼音或首字母)
     */
    private String nameHeadLetter;

    /**
     * display name
     * 
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * set display name
     * 
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
        this.simplePinyin = com.huawei.basic.android.im.utils.HanziToPinyin.getInstance().getPinyin(displayName);
    }

    /**
     * get simple pinyin of display name
     * 
     * @return the simplePinyin
     */
    public String getSimplePinyin()
    {
        return simplePinyin;
    }

    /**
     * set simple pinyin of display name
     * 
     * @param simplePinyin the simplePinyin to set
     */
    public void setSimplePinyin(String simplePinyin)
    {
        this.simplePinyin = simplePinyin;
    }

    /**
     * 拼音首字母
     * 
     * @return the nameHeadLetter
     */
    public String getNameHeadLetter()
    {
        return nameHeadLetter;
    }

    /**
     * 设置拼音首字母
     * 
     * @param nameHeadLetter the nameHeadLetter to set
     */
    public void setNameHeadLetter(String nameHeadLetter)
    {
        this.nameHeadLetter = nameHeadLetter;
    }
}
