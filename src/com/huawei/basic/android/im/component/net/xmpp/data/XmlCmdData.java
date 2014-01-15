/*
 * 文件名: XmlData.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

/**
 * xmlData 应该实现的接口<BR>
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-11-6] 
 */
public interface XmlCmdData
{
    /**
     * 返回数据xml串<BR>
     * @return 数据的 xml 串
     */
    String makeCmdData();
}
