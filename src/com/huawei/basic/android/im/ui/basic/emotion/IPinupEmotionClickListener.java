/*
 * 文件名: IPinupEmotionClickListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 贴图发送接口
 * 创建人: zhaozeyang
 * 创建时间:2012-4-16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic.emotion;
/**
 * 
 * 贴图发送接口<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-5-2]
 */
public interface IPinupEmotionClickListener
{
    /**
     * 
     * 点击贴图进行发送<BR>
     * @param mediaType 媒体类型
     * @param mediaPath 媒体路径 
     * @param size 大小
     * @param duration 延迟
     * @param alt 贴图描述
     */
    void onPinupEmotionClick(int mediaType, String mediaPath, String size,
            int duration, String alt);
}