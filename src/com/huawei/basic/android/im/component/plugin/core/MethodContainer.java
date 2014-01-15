/*
 * 文件名: MethodContainer.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 23, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * 缓存Plugin对应的操作<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
public class MethodContainer
{
    private static Map<String, List<BaseMethod>> methodCache = new HashMap<String, List<BaseMethod>>();
    
    private static Context sContext;
    
    /**
     * 初始化<BR>
     * @param context context
     */
    public static void init(Context context)
    {
        sContext = context;
    }
    
    /**
     * 增加插件对应的操作入口<BR>
     * @param pluginId 插件ID
     * @param methodList 方法列表
     */
    public static void put(String pluginId, List<BaseMethod> methodList)
    {
        methodCache.put(pluginId, methodList);
        for (BaseMethod method : methodList)
        {
            method.setContext(sContext);
        }
    }
    
    /**
     * 获取插件对应的操作入口<BR>
     * @param pluginId 插件ID
     * @return List<BaseMethod>
     */
    public static List<BaseMethod> get(String pluginId)
    {
        return methodCache.get(pluginId);
    }
}
