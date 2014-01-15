/*
 * 文件名: MapsManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2012-4-16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic.emotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 表情符号的管理类<BR>
 * 定义了表情符号的类型，以及表情符号的缓存
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-5-17]
 */
public class MapsManager
{
    
    /**
     * 表情存在标志
     */
    private static final int EXSITS = 1;
    
    /**
     * 用于匹配发送的是表情符号，解决光标错位的问题
     */
    private static Map<String, Integer> map;
    
    
    private static List<List<Map<String, Object>>> list;
    
    /**
     * 表情符号的数据
     */
    private static String[] data = new String[] { "…(>o<)…", "﹙o⌒o﹚", "﹙≧≦﹚",
        "（≯≮）", "﹙⊙o-﹚", "(﹢-﹢)~?~?", "(○_○)", "(δДδ)", "(-ζ-)…♂", "【⌒?⌒】",
        "﹙⊙＝⊙﹚？", "**╲﹙①ο①﹚╱**", "(ˇ_ˇ)", "（σ_σ）", "【Ο-Ο】", "﹝ˇυˇ〕",
        "　－－！！！", "(Очо)", "（?￢?）", "3（⌒_⌒）Q", "[∪∧∪]", "（≥τ≤）", " (⊙μ⊙)",
        "o（︶∩︶）o", "\\≤⌒∩⌒≥/", "(⊙<⊙)", "≤θυθ≥ ", "[×∧×]", "?_?", "▽▁▽#",
        "（⌒v⌒）", " \\（①-①）/", "╯╭╮╰ ", "︴︴（⊙﹏⊙）︴︴", " \\（⊙з⊙）/", "[*о▁о*]",
        "（⊙⌒⊙）°°°", "﹙≧μ≦﹚°°°", "《ˉロˉ》", "﹝ΟΩΟ﹞", "[﹫∠﹫]", "╰(⊙_⊙)╮",
        "¤д¤", "〈≠﹏＝〉", "↖∪⌒∪↗", "╰(﹩⊥﹩)╯", "﹝ōτó﹞？", "〖θ﹏θ〗", "ヘ(⊙_⊙)ノ",
        "（﹥⌒﹤）", "﹙﹫ㄥ﹫﹖﹚" };
    
    /**
     * 获得表情符号数据
     * 
     * @return List
     */
    public static List<List<Map<String, Object>>> getMapsForAdapter()
    {
        
        if (list == null || map == null)
        {
            map = new HashMap<String, Integer>();
            list = new ArrayList<List<Map<String, Object>>>();
            ArrayList<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
            
            for (int i = 0; i < data.length; i++)
            {
                Map<String, Object> newEntry = new HashMap<String, Object>();
                newEntry.put(EmotionManager.ADAPTER_KEY_WORD, data[i]);
                itemList.add(newEntry);
                
                if (itemList.size() != 0 && itemList.size() % 12 == 0)
                {
                    list.add(itemList);
                    itemList = new ArrayList<Map<String, Object>>();
                }
                
                map.put(data[i], EXSITS);
            }
            
            if (itemList.size() != 0)
            {
                list.add(itemList);
            }
            
        }
        
        return list;
    }
}
