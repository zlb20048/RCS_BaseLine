/*
 * 文件名: PluginStringUtiljava
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zql
 * 创建时间:May 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.util.HashMap;

import com.huawei.basic.android.R;

/**
 * 插件中英文支持工具<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, May 21, 2012]
 */
public class PluginStringUtil
{
    private static HashMap<String, Integer> nameMap = null;
    
    private static HashMap<String, Integer> descMap = null;
    
    private static HashMap<String, Integer> methodMap = null;
    
    private static HashMap<String, Integer> otherMap = null;
    
    /**
     * 获取其它特性的中英文字符串<BR>
     * @param key 键
     * @return 对应的value
     */
    public static int getOtherId(String key)
    {
        if (null == otherMap)
        {
            initOtherMap();
        }
        return otherMap.get(key);
    }
    
    private static void initOtherMap()
    {
        otherMap = new HashMap<String, Integer>();
        otherMap.put("string_id_hitalk_sectary", R.string.hi_talk_sec);
    }
    
    
    /**
     * 根据插件ID获取插件名<BR>
     * @param pluginId 插件ID
     * @return 插件名
     */
    public static int getNameId(String pluginId)
    {
        if (null == nameMap)
        {
            initNameMap();
        }
        return nameMap.get(pluginId);
    }
    
    /**
     * 根据插件ID获取描述<BR>
     * @param pluginId 插件ID
     * @return 插件描述
     */
    public static int getDescId(String pluginId)
    {
        if (null == descMap)
        {
            initDescMap();
        }
        return descMap.get(pluginId);
    }
    
    /**
     * 根据插件ID获取插件方法名<BR>
     * @param methodName 插件ID
     * @return 方法名
     */
    public static int getMethodNameId(String methodName)
    {
        if (null == methodMap)
        {
            initMethodMap();
        }
        return methodMap.get(methodName);
    }
    
    private static void initMethodMap()
    {
        methodMap = new HashMap<String, Integer>();
        methodMap.put("isrecommend", R.string.plugin_method_name_isrecommend);
    }
    
    private static void initDescMap()
    {
        descMap = new HashMap<String, Integer>();
        descMap.put("1001", R.string.plugin_desc_1001);
        descMap.put("1002", R.string.plugin_desc_1002);
    }
    
    private static void initNameMap()
    {
        nameMap = new HashMap<String, Integer>();
        nameMap.put("1001", R.string.plugin_name_1001);
        nameMap.put("1002", R.string.plugin_name_1002);
    }
}
