/*
 * 文件名: ChatTextParser.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fanniu
 * 创建时间:2012-3-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.basic.emotion.EmotionManager;

/**
 * 文本解析<BR>
 * @author fanniu
 * @version [RCS Client V100R001C03, 2012-3-19] 
 */
public final class ChatTextParser
{
    /**
     * DEBUG_TAG
     */
    private static final String TAG = "ChatTextParser";
    
    /**
     * 表情正则表达式
     */
    private static Pattern sRegexEmotion;
    
    /**
     * 单例
     */
    private static ChatTextParser sInstance;
    
    /**
     * 上下文
     */
    private Context mContext;
    
    /**
     * 构造方法<BR>
     * @param context Context
     */
    private ChatTextParser(Context context)
    {
        mContext = context;
        //表情正则表达式初始化
        initEmotionRegex();
    }
    
    /**
     * 获得ChatTextParser对象<BR>
     * @param context 上下文
     * @return ChatTextParser对象
     */
    public static synchronized ChatTextParser getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new ChatTextParser(context);
        }
        return sInstance;
    }
    
    /**
     * 初始化表情正则表达式<BR>
     */
    private void initEmotionRegex()
    {
        Map<String, Integer> expressions = EmotionManager.getInstance(mContext)
                .getExpressionsAndResources();
        
        StringBuilder strBuilder = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> iterator = expressions.entrySet()
                .iterator();
        
        while (iterator.hasNext())
        {
            Map.Entry<String, Integer> entry = iterator.next();
            strBuilder.append("|").append(entry.getKey());
        }
        if (strBuilder.length() > 0)
        {
            sRegexEmotion = Pattern.compile("\\[(" + strBuilder.substring(1)
                    + ")\\]");
            
        }
    }
    
    /**
     * 文本解析<BR>
     * @param origin origin
     * @param context context
     * @param strict strict
     * @param markHtmlLink markHtmlLink
     * @return sIntance.parse(origin, context, strict, markHtmlLink) 调用parse()进行文本解析
     */
    public CharSequence parseText(String origin, Context context,
            boolean strict, boolean markHtmlLink)
    {
        return sInstance.parse(origin, context, strict, markHtmlLink);
    }
    
    /**
     * 简单文本解析<BR>
     * @param origin origin
     * @param context context
     * @return  sIntance.parse(origin, context, strict, markHtmlLink) 调用parse()进行文本解析
     */
    public CharSequence parseText(String origin, Context context)
    {
        return parse(origin, context, false, true);
    }
    
    /**
     * 简单文本解析<BR>
     * @param origin 原始字符串
     * @param context Context
     * @param strict strict
     * @param markHtmlLink markHtmlLink
     * @return CharSequence 对象
     */
    private CharSequence parse(CharSequence origin, Context context,
            boolean strict, boolean markHtmlLink)
    {
        if (origin == null)
        {
            if (strict)
            {
                return null;
            }
            else
            {
                return "";
            }
        }
        SpannableStringBuilder sstrBuilder = new SpannableStringBuilder(origin);
        
        //表情
        Matcher matcher = sRegexEmotion.matcher(origin);
        while (matcher.find())
        {
            String expression = matcher.group();
            expression = expression.substring(1, expression.length() - 1);
            int resId = EmotionManager.getInstance(mContext)
                    .getResourceId(context, expression);
            if (resId < 1)
            {
                continue;
            }
            Drawable drawable = context.getResources().getDrawable(resId);
            drawable.setBounds(0,
                    0,
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable,
                    DynamicDrawableSpan.ALIGN_BOTTOM);
            sstrBuilder.setSpan(imageSpan,
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        if (markHtmlLink)
        {
            //HTML连接
            matcher = sRegexEmotion.matcher(origin);
            while (matcher.find())
            {
                String href = matcher.group();
                Logger.d(TAG, "" + href);
                //                URLSpan urlSpan = new MyU
            }
        }
        
        return sstrBuilder;
    }
    
    /**
     * 获取表情匹配正则表达式
     * 
     * @return 表情匹配正则表达式
     */
    public Pattern getEmotionPattern()
    {
        return sRegexEmotion;
    }
    
}
