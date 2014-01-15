/*
 * 文件名: Config.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.common;

/**
 * 配置信息<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public class Config
{
    /**
     * 插件配置文件的路径,暂未未用到，直接放在raw资源文件下
     */
    public static final String PLUGIN_CONFIG_PATH = "/sdcard/rcsbaseline/plugin_config.xml";
    
    /**
     * 插件的错误码<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    public interface ErrorCode
    {
        /**
         * BASE
         */
        int BASE = 0;
        
        /**
         * 成功
         */
        int SUCCESS = BASE + 1;
        
        /**
         * 失败
         */
        int FAILED = BASE + 2;
    }
}
