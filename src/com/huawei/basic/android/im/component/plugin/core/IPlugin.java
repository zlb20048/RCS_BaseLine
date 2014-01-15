/*
 * 文件名: IPlugin.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

/**
 * 插件接口<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public interface IPlugin
{
    /**
     * 安装插件<BR>
     * @param listener 回调监听器
     */
    void install(ICallBack listener);
    
    /**
     * 卸载插件<BR>
     * @param listener 回调监听器
     */
    void uninstall(ICallBack listener);
    
    /**
     * 更新插件<BR>
     * @param listener 回调监听器
     */
    void update(ICallBack listener);
    
    /**
     * 开始插件<BR>
     */
    void start();
    
}
