/*
 * 文件名: EmotionManager.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描 述: 表情管理器
 * 创建人: 杨凡
 * 创建时间: 2012-02-27
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic.emotion;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 
 * 表情管理器<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-27]
 */
public class EmotionManager
{
    
    /**
     * 每页显示多少个表情
     */
    public static final int PER_PAGE_COUNT = 30;
    
    /**
     * Adapter key resid
     */
    public static final String ADAPTER_KEY_RES_ID = "_ResId";
    
    /**
     * Adapter key word
     */
    public static final String ADAPTER_KEY_WORD = "_Word";
    
    /**
     * 默认类型
     */
    public static final int TYPE_DEFAULT = 0;
    
    /**
     * EmotionManager  tag
     */
    private static final String TAG = "EmotionManager";
    
    /**
     * 表情占位符与资源的对应关系
     */
    private static final Map<String, Integer> EXPRESSIONS_AND_RESOURCES = new LinkedHashMap<String, Integer>();
    
    /**
     * 表情图片资源的前缀
     */
    
    private static final String PREFIX = "exp_";
    
    private static EmotionManager sInstance;
    
    /**
     * 表情集合的弱引用
     */
    private WeakReference<List<List<Map<String, Object>>>> sExpressionsWeakRef = null;
    
    /**
     * 存储没个表情对象的map
     */
    private Map<String, String> en2Cn = new HashMap<String, String>();
    
    private Context mContext;
    
    /**
     * 私有构造方法
     * @param context
     */
    private EmotionManager(Context context)
    {
        mContext = context;
        initEmotionSrc();
    }
    
    /**
     * 获得表情管理器对象<BR>
     * @param context Context
     * @return EmotionManager
     */
    public static synchronized EmotionManager getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new EmotionManager(context);
        }
        return sInstance;
    }
    
    /**
     * 初始化加载表情资源<BR>
     */
    private void initEmotionSrc()
    {
        en2Cn = new HashMap<String, String>();
        String[] sysEmotionKey = mContext.getResources()
                .getStringArray(R.array.system_emotion_key);
        String[] sysEmotionValue = mContext.getResources()
                .getStringArray(R.array.system_emotion_values);
        for (int i = 0; i < sysEmotionKey.length; i++)
        {
            en2Cn.put(sysEmotionKey[i], sysEmotionValue[i]);
        }
        try
        {
            Field[] fields = R.drawable.class.getDeclaredFields();
            if (fields != null)
            {
                for (Field field : fields)
                {
                    String fieldName = field.getName();
                    if (fieldName.startsWith(PREFIX))
                    {
                        String cKey = fieldName.substring(PREFIX.length());
                        Integer cValue = (Integer) field.get(null);
                        //key : 文字描述  value：资源ID
                        EXPRESSIONS_AND_RESOURCES.put(en2Cn.get(cKey), cValue);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Logger.e(TAG, "initEmotionSrc ", e);
        }
    }
    
    /**
     * 获得表情及其资源<BR>
     * @return 存放表情与资源对应关系的map
     */
    public Map<String, Integer> getExpressionsAndResources()
    {
        return EXPRESSIONS_AND_RESOURCES;
    }
    
    /**
     * 根据占位符获取资源id, 后期是否可以改成直接返回Drawable?
     *
     * @param context 上下文
     * @param key 占位符
     * @return 表情资源id
     */
    public int getResourceId(Context context, String key)
    {
        key = String.valueOf(key);
        return EXPRESSIONS_AND_RESOURCES.containsKey(key) ? EXPRESSIONS_AND_RESOURCES.get(key)
                .intValue()
                : -1;
    }
    
    /**
     * 获取表情集合
     *
     * @param type 表情类型
     * @return 表情集合
     */
    public List<List<Map<String, Object>>> getExpressionsForAdapter(int type)
    {
        //TODO:Map<String, Object> 表情资源描述：表情资源ID
        //* List<Map<String, Object>> 表情资源的集合 （这里每个集合最大值为30）
        //* List<List<Map<String, Object>>> 封装多少个表情资源集合 （这个集合应该是给viewFlliper显示用的）
        List<List<Map<String, Object>>> list = null;
        if (sExpressionsWeakRef == null
                || (list = sExpressionsWeakRef.get()) == null)
        {
            list = new ArrayList<List<Map<String, Object>>>();
            
            List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
            
            // 优先保证按照插入的顺序展示
            if (en2Cn != null && en2Cn.size() > 0)
            {
                Iterator<Map.Entry<String, String>> itSort = en2Cn.entrySet()
                        .iterator();
                while (itSort.hasNext())
                {
                    Map<String, Object> newEntry = new HashMap<String, Object>();
                    //如果是最后一个位置，设置为功能按键 ---删除
                    //map不进行迭代
                    if (itemList.size() == (PER_PAGE_COUNT - 1))
                    {
                        newEntry.put(ADAPTER_KEY_WORD, "");
                        //资源ID
                        newEntry.put(ADAPTER_KEY_RES_ID, R.drawable.del_btn_nor);
                    }
                    else
                    {
                        //如果不是最后一个位置，map进行迭代，取下一个表情
                        Map.Entry<String, String> entry = itSort.next();
                        //资源描述
                        newEntry.put(ADAPTER_KEY_WORD, entry.getValue());
                        //资源ID
                        newEntry.put(ADAPTER_KEY_RES_ID,
                                EXPRESSIONS_AND_RESOURCES.get(entry.getValue())
                                        .intValue());
                    }
                    
                    //每页显示的表情个数 30
                    if (itemList.size() != 0
                            && itemList.size() % PER_PAGE_COUNT == 0)
                    {
                        list.add(itemList);
                        itemList = new ArrayList<Map<String, Object>>();
                    }
                    itemList.add(newEntry);
                    
                }
                if (itemList.size() != 0)
                {
                    if (itemList.size() < PER_PAGE_COUNT)
                    {
                        //键：表情文字     值：表情ID 
                        //TODO：这里实现添加删除按钮，逻辑需要重新处理，暂时实现功能
                        //这里主要是填充gridView空白item 使得删除按键能在最后位置
                        for (int i = itemList.size(); i < PER_PAGE_COUNT; i++)
                        {
                            Map<String, Object> newEntry = new HashMap<String, Object>();
                            if (i == (PER_PAGE_COUNT - 1))
                            {
                                newEntry.put(ADAPTER_KEY_WORD, "");
                                //资源ID
                                newEntry.put(ADAPTER_KEY_RES_ID,
                                        R.drawable.del_btn_nor);
                            }
                            else
                            {
                                newEntry.put(ADAPTER_KEY_WORD, "");
                                //资源ID
                                newEntry.put(ADAPTER_KEY_RES_ID, 0);
                            }
                            itemList.add(newEntry);
                        }
                    }
                    list.add(itemList);
                }
            }
            else
            {
                Iterator<Map.Entry<String, Integer>> iterator = EXPRESSIONS_AND_RESOURCES.entrySet()
                        .iterator();
                while (iterator.hasNext())
                {
                    Map.Entry<String, Integer> entry = iterator.next();
                    Map<String, Object> newEntry = new HashMap<String, Object>();
                    newEntry.put(ADAPTER_KEY_WORD, entry.getKey());
                    newEntry.put(ADAPTER_KEY_RES_ID, entry.getValue()
                            .intValue());
                    if (itemList.size() % PER_PAGE_COUNT == 0)
                    {
                        list.add(itemList);
                        itemList = new ArrayList<Map<String, Object>>();
                    }
                    itemList.add(newEntry);
                    
                }
                
                if (itemList.size() != 0)
                {
                    list.add(itemList);
                }
            }
            sExpressionsWeakRef = new WeakReference<List<List<Map<String, Object>>>>(
                    list);
        }
        return list;
    }
    
    /**
     * 格式化表情字符串
     *
     * @param word 表情文字
     * @return 表情字符串
     */
    public String format(String word)
    {
        return "[" + word + "]";
    }
}