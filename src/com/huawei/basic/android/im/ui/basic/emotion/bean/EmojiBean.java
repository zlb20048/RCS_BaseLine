package com.huawei.basic.android.im.ui.basic.emotion.bean;

import android.graphics.Bitmap;
/**
 * 贴图表情实体类<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-5-24]
 */
public class EmojiBean
{
    
    /**
     * 文件名
     */
    private String ttid;
    
    /**
     * 图片的中文解释
     */
    private String alt;
    
    /**
     * 具体的资源图片
     */
    private Bitmap emoji;
    
    public String getTtid()
    {
        return ttid;
    }
    
    public void setTtid(String ttid)
    {
        this.ttid = ttid;
    }
    
    public String getAlt()
    {
        return alt;
    }
    
    public void setAlt(String alt)
    {
        this.alt = alt;
    }
    
    public Bitmap getEmoji()
    {
        return emoji;
    }
    
    public void setEmoji(Bitmap emoji)
    {
        this.emoji = emoji;
    }
    
}