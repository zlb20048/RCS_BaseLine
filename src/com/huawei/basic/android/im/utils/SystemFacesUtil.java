package com.huawei.basic.android.im.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SetSystemHeadAction;

/**
 * 处理系统头像和相关信息的类
 * 
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 14, 2012] 
 */
public class SystemFacesUtil
{
    
    private static final String PREFIX = "PortraitID";
    /**
     * 存放个人系统头像map对
     */
    private static final Map<String, Integer> MAP_PERSON = new HashMap<String, Integer>();
    /**
     * 存放群组系统头像map对
     */
    private static final Map<String, Integer> MAP_GROUP = new HashMap<String, Integer>();
    
    static
    {
        MAP_PERSON.put("PortraitID_0001", R.drawable.portraitid_0001);
        MAP_PERSON.put("PortraitID_0002", R.drawable.portraitid_0002);
        MAP_PERSON.put("PortraitID_0003", R.drawable.portraitid_0003);
        MAP_PERSON.put("PortraitID_0004", R.drawable.portraitid_0004);
        MAP_PERSON.put("PortraitID_0005", R.drawable.portraitid_0005);
        MAP_PERSON.put("PortraitID_0006", R.drawable.portraitid_0006);
        MAP_PERSON.put("PortraitID_0007", R.drawable.portraitid_0007);
        MAP_PERSON.put("PortraitID_0008", R.drawable.portraitid_0008);
        MAP_PERSON.put("PortraitID_0009", R.drawable.portraitid_0009);
        MAP_PERSON.put("PortraitID_0010", R.drawable.portraitid_0010);
        MAP_PERSON.put("PortraitID_0011", R.drawable.portraitid_0011);
        MAP_PERSON.put("PortraitID_0012", R.drawable.portraitid_0012);
        MAP_PERSON.put("PortraitID_0013", R.drawable.portraitid_0013);
        MAP_PERSON.put("PortraitID_0014", R.drawable.portraitid_0014);
        MAP_PERSON.put("PortraitID_0015", R.drawable.portraitid_0015);
        MAP_PERSON.put("PortraitID_0016", R.drawable.portraitid_0016);
        MAP_PERSON.put("PortraitID_0017", R.drawable.portraitid_0017);
        MAP_PERSON.put("PortraitID_0018", R.drawable.portraitid_0018);
        MAP_PERSON.put("PortraitID_0019", R.drawable.portraitid_0019);
        MAP_PERSON.put("PortraitID_0020", R.drawable.portraitid_0020);
        MAP_PERSON.put("PortraitID_0021", R.drawable.portraitid_0021);
        MAP_PERSON.put("PortraitID_0022", R.drawable.portraitid_0022);
        MAP_PERSON.put("PortraitID_0023", R.drawable.portraitid_0023);
        MAP_PERSON.put("PortraitID_0024", R.drawable.portraitid_0024);
        MAP_PERSON.put("PortraitID_0025", R.drawable.portraitid_0025);
        MAP_PERSON.put("PortraitID_0026", R.drawable.portraitid_0026);
        MAP_PERSON.put("PortraitID_0027", R.drawable.portraitid_0027);
        MAP_PERSON.put("PortraitID_0028", R.drawable.portraitid_0028);
        MAP_PERSON.put("PortraitID_0029", R.drawable.portraitid_0029);
        MAP_PERSON.put("PortraitID_0030", R.drawable.portraitid_0030);
        MAP_PERSON.put("PortraitID_0031", R.drawable.portraitid_0031);
        MAP_PERSON.put("PortraitID_0032", R.drawable.portraitid_0032);
        MAP_PERSON.put("PortraitID_0033", R.drawable.portraitid_0033);
    }
    
    static
    {
        MAP_GROUP.put("PortraitID_1001", R.drawable.portraitid_1001);
        MAP_GROUP.put("PortraitID_1002", R.drawable.portraitid_1002);
        MAP_GROUP.put("PortraitID_1003", R.drawable.portraitid_1003);
        MAP_GROUP.put("PortraitID_1004", R.drawable.portraitid_1004);
        MAP_GROUP.put("PortraitID_1005", R.drawable.portraitid_1005);
        MAP_GROUP.put("PortraitID_1006", R.drawable.portraitid_1006);
        MAP_GROUP.put("PortraitID_1007", R.drawable.portraitid_1007);
        MAP_GROUP.put("PortraitID_1008", R.drawable.portraitid_1008);
        MAP_GROUP.put("PortraitID_1009", R.drawable.portraitid_1009);
        MAP_GROUP.put("PortraitID_1010", R.drawable.portraitid_1010);
        //插件
        MAP_GROUP.put("PortraitID_1011", R.drawable.icon_secretary);
        MAP_GROUP.put("PortraitID_1012", R.drawable.icon_find_friend);
    }
    
    /**
     * 
     * 把头像地址转化为图片资源
     * 
     * @param faceUrl 头像地址
     * @return 图片资源
     */
    public static int getFaceImageResourceIdByFaceUrl(String faceUrl)
    {
        if (MAP_GROUP.containsKey(faceUrl))
        {
            return MAP_GROUP.get(faceUrl);
        }
        else if (MAP_PERSON.containsKey(faceUrl))
        {
            return MAP_PERSON.get(faceUrl);
        }
        return R.drawable.default_contact_icon;
    }
    /**
     * 
     * 根据类型返回系统头像总数<BR>
     * [功能详细描述]
     * @param type 头像类型
     * @return 头像总数
     */
    public static int getCount(int type)
    {
        if (type == SetSystemHeadAction.MODE_PERSON)
        {
            return MAP_PERSON.size();
        }
        else if (type == SetSystemHeadAction.MODE_GROUP)
        {
            return MAP_GROUP.size();
        }
        return 0;
    }
    /**
     * 根据不同的类型 取得相应的图片数组
     * 
     * @param type 类型
     * @return 返回对应的图片数组
     */
    @SuppressWarnings("unchecked")
    public static List<Map.Entry<String, Integer>> getList(int type)
    {
        Map<String, Integer> map = MAP_PERSON;
        if (type == SetSystemHeadAction.MODE_GROUP)
        {
            map = MAP_GROUP;
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                map.size());
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet()
                .iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, Integer> entry = iterator.next();
            list.add(entry);
        }
        CompareEntry compareEntry = new CompareEntry();
        Collections.sort(list, compareEntry);
        return list;
    }
    
    /**
     * list排序
     */
    @SuppressWarnings("rawtypes")
    private static class CompareEntry implements Comparator
    {
        
        @Override
        @SuppressWarnings("unchecked")
        public int compare(Object object, Object object01)
        {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) object;
            Map.Entry<String, Integer> entryTwo = (Map.Entry<String, Integer>) object01;
            return entry.getKey().compareTo(entryTwo.getKey());
        }
    }
    
    /**
     * 
     * 返回当前url是否有对应的系统头像
     * 
     * @param faceUrl 头像的url
     * @return 是否有对应的系统头像
     */
    public static boolean isSystemFaceUrl(String faceUrl)
    {
        return !StringUtil.isNullOrEmpty(faceUrl)
                && faceUrl.startsWith(PREFIX);
    }
}
