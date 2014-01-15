/*
 * 文件名: JsonUtils.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: JSON解释工具类<BR>
 * 创建人: 盛兴亚
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.basic.android.im.utils.StringUtil;

import android.text.TextUtils;

/**
 * 
 * JSON解释工具类<BR>
 * 使用反射方式完成JSON到对象，对象到JSON的解析
 * 
 * @author 盛兴亚
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class JsonUtils
{
    /**
     * Simple JsonString to obj.
     * 
     * @param jsonString
     *            jsonString
     * @param c
     *            class
     * @return instance of c
     */
    public static Object simpleJsonToObject(String jsonString, Class<?> c)
    {
        try
        {
            JSONObject obj = new JSONObject(jsonString);
            return toObject(obj, c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * JsonString to obj.
     * 
     * @param jsonString
     *            jsonString
     * @param c
     *            class
     * @return instance of c and values in string.
     */
    public static Object toObject(String jsonString, Class<?> c)
    {
        try
        {
            JSONObject obj = new JSONObject(jsonString);
            return toObject(obj.getJSONObject(c.getSimpleName()), c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
            
        }
        
    }
    
    /**
     * 
     * the topName maybe not same with the class c's simple name.
     * 
     * @param jsonString
     *            jsonString
     * @param c
     *            假如是数组就传Example[].class 否则传Example.class 返回的Object是 c的实例
     * @param topName
     *            最外层的key.
     * @return c's new instance.
     */
    public static Object toObject(String jsonString, Class<?> c, String topName)
    {
        try
        {
            if (null == jsonString || jsonString.length() == 0)
            {
                return null;
            }
            JSONObject obj = new JSONObject(jsonString);
            if (c.isArray())
            {
                // array's class name -> [L*******;
                return toObjectArray(obj.optJSONArray(topName),
                        Class.forName(c.getName().substring(2,
                                c.getName().length() - 1)));
            }
            else
            {
                return toObject(obj.optJSONObject(topName), c);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
            
        }
    }
    
    /**
     * 
     * obj to JsonString.
     * 
     * @param obj
     *            object
     * @return the jsonString of this obj.
     */
    public static String toJsonString(Object obj)
    {
        return toJsonString(obj, obj.getClass().getSimpleName());
    }
    
    /**
     * 
     * obj to JsonString.
     * 
     * @param obj
     *            object
     * @param topString
     *            topString
     * @return the jsonString of this obj.
     */
    public static String toJsonString(Object obj, String topString)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.putOpt(topString, toJson(obj));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return jobj.toString();
    }
    
    private static Object[] toObjectArray(JSONArray array, Class<?> c)
    {
        try
        {
            if (array != null && array.length() > 0)
            {
                Object[] objArray = (Object[]) Array.newInstance(c,
                        array.length());
                for (int i = 0; i < array.length(); i++)
                {
                    if ((array.get(i) instanceof String)
                            || (array.get(i) instanceof Long)
                            || (array.get(i) instanceof Double)
                            || (array.get(i) instanceof Boolean))
                    {
                        
                        objArray[i] = array.get(i);
                    }
                    else
                    {
                        if (objArray == null)
                        {
                            objArray = new Object[array.length()];
                        }
                        Object innerObj = toObject(array.getJSONObject(i), c);
                        
                        if (innerObj != null)
                        {
                            objArray[i] = innerObj;
                        }
                    }
                }
                return objArray;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * JsonObject to Object .<BR>
     * [功能详细描述]
     * 
     * @param jobj
     *            JSONObject
     * @param c
     *            class
     * @return Object
     */
    public static Object toObject(JSONObject jobj, Class<?> c)
    {
        if (c == null || jobj == null)
        {
            return null;
        }
        try
        {
            Object obj = c.newInstance();
            Field[] fields = c.getFields();
            for (Field f : fields)
            {
                String className = f.getType().getName();
                if (className.equals("int")
                        || className.equals(Integer.class.getName()))
                {
                    f.setInt(obj, jobj.optInt(f.getName()));
                }
                else if (className.equals("long")
                        || className.equals(Long.class.getName()))
                {
                    f.setLong(obj, jobj.optLong(f.getName()));
                }
                else if (className.equals("double")
                        || className.equals(Double.class.getName()))
                {
                    f.setDouble(obj, jobj.optDouble(f.getName()));
                }
                else if (className.equals("boolean")
                        || className.equals(Boolean.class.getName()))
                {
                    f.setBoolean(obj, jobj.optBoolean(f.getName()));
                }
                else if (className.equals(String.class.getName()))
                {
                    String s = jobj.optString(f.getName());
                    if (!TextUtils.isEmpty(s))
                    {
                        f.set(obj, s);
                    }
                }
                else if (className.startsWith("[L"))// boolean isArray.
                {
                    JSONArray array = jobj.optJSONArray(f.getName());
                    if (array != null && array.length() > 0)
                    {
                        Class<?> innerClass = Class.forName(className.substring(2,
                                className.length() - 1));
                        Object[] objArray = (Object[]) Array.newInstance(innerClass,
                                array.length());
                        for (int i = 0; i < array.length(); i++)
                        {
                            if ((array.get(i) instanceof String)
                                    || (array.get(i) instanceof Long)
                                    || (array.get(i) instanceof Double)
                                    || (array.get(i) instanceof Boolean))
                            {
                                
                                objArray[i] = array.get(i);
                            }
                            else
                            {
                                if (objArray == null)
                                {
                                    objArray = new Object[array.length()];
                                }
                                Object innerObj = toObject(array.getJSONObject(i),
                                        innerClass);
                                
                                if (innerObj != null)
                                {
                                    objArray[i] = innerObj;
                                }
                            }
                        }
                        f.set(obj, objArray);
                    }
                }
                else
                {
                    JSONObject json = jobj.optJSONObject(f.getName());
                    if (json != null)
                    {
                        Object innerObj = toObject(json,
                                Class.forName(f.getType().getName()));
                        if (innerObj != null)
                        {
                            f.set(obj, innerObj);
                        }
                    }
                    
                }
            }
            return obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * obj to JsonObj.
     * 
     * @param obj
     *            input obj.
     * @return if obj is array return JsonArray else JsonObject.
     */
    public static Object toJson(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        try
        {
            if (obj.getClass().isArray())
            {
                JSONArray jArray = null;
                if (((Object[]) obj).length > 0)
                {
                    jArray = new JSONArray();
                    for (int i = 0; i < ((Object[]) obj).length; i++)
                    {
                        if (((Object[]) obj)[i] != null)
                        {
                            Class<?> c = obj.getClass();
                            if (c == int[].class || c == Integer[].class)
                            {
                                jArray.put(((Integer[]) obj)[i]);
                            }
                            else if (c == long[].class || c == Long[].class)
                            {
                                jArray.put(((Long[]) obj)[i]);
                            }
                            else if (c == boolean[].class
                                    || c == Boolean[].class)
                            {
                                jArray.put(((Boolean[]) obj)[i]);
                            }
                            else if (c == double[].class || c == Double[].class)
                            {
                                jArray.put(((Double[]) obj)[i]);
                            }
                            else if (c == String[].class)
                            {
                                jArray.put(((String[]) obj)[i]);
                            }
                            else
                            {
                                jArray.put(toJson(((Object[]) obj)[i]));
                            }
                        }
                    }
                }
                return jArray;
            }
            JSONObject jobj = new JSONObject();
            Field[] f = obj.getClass().getFields();
            for (Field field : f)
            {
                Object inner = field.get(obj);
                if (inner == null)
                {
                    continue;
                }
                Class<?> c = inner.getClass();
                if (c == int.class || c == Integer.class)
                {
                    jobj.putOpt(field.getName(), inner);
                }
                else if (c == long.class || c == Long.class)
                {
                    jobj.putOpt(c.getSimpleName(), inner);
                }
                else if (c == String.class)
                {
                    if (!"".equals(inner))
                    {
                        jobj.putOpt(field.getName(), inner);
                    }
                }
                else
                {
                    jobj.putOpt(field.getName(), toJson(inner));
                }
            }
            return jobj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
    *
    * 封装JSON数据
    *
    * @param object JSON对象
    * @param key key值
    * @param value value值
    */
    public static void putPairIntoJSONObject(JSONObject object, String key,
            Object value)
    {
        try
        {
            object.put(key, value);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * 获取JSON数组对象
     * 
     * @param parentObj
     *            JSON对象
     * @param key
     *            key值
     * @return JSON数组对象
     */
    public static JSONArray getJSONArray(JSONObject parentObj, String key)
    {
        JSONArray jArray = null;
        try
        {
            if (parentObj.has(key))
            {
                jArray = parentObj.getJSONArray(key);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jArray;
    }
    
    /**
     * 
     * 获取JSON对象
     * 
     * @param parentArray
     *            JSON数组
     * @param index
     *            数组索引
     * @return JSON对象
     */
    public static JSONObject getJSONObject(JSONArray parentArray, int index)
    {
        JSONObject retObj = null;
        try
        {
            retObj = parentArray.getJSONObject(index);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return retObj;
    }
    
    /**
     * 
     * 根据JSON数据封装一个新的JSON对象
     * 
     * @param content
     *            JSON数据
     * @return 新的JSON数据
     */
    public static JSONObject newJSONObject(String content)
    {
        if (StringUtil.isNullOrEmpty(content))
        {
            return null;
        }
        JSONObject retObj = null;
        try
        {
            String filterStr = StringUtil.filterHtmlTag(content);
            retObj = new JSONObject(filterStr);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return retObj;
    }
    
    /**
     * 
     * 获取JSON对象
     * 
     * @param parentObj
     *            JSON对象
     * @param key
     *            key值
     * @return JSON对象
     */
    public static JSONObject getJSONObject(JSONObject parentObj, String key)
    {
        JSONObject retObj = null;
        try
        {
            if (parentObj.has(key))
            {
                retObj = parentObj.getJSONObject(key);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return retObj;
    }
    
    /**
     * 
     * 获取JSON数据
     * 
     * @param parentObj
     *            JSON对象
     * @param key
     *            key值
     * @return JSON数据
     */
    public static String getString(JSONObject parentObj, String key)
    {
        try
        {
            if (parentObj.has(key))
            {
                return parentObj.getString(key);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return "";
    }
    
    /**
     * 
     * <BR>
     * 
     * @param parentObj JSONObject
     * @param key key
     * @return int
     */
    public static int getInt(JSONObject parentObj, String key)
    {
        try
        {
            if (parentObj.has(key))
            {
                return parentObj.getInt(key);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param parentObj JSONObject
     * @param key key
     * @return double
     */
    public static double getDouble(JSONObject parentObj, String key)
    {
        try
        {
            if (parentObj.has(key))
            {
                return parentObj.getDouble(key);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return 0;
    }
}
