/*
 * 文件名: PictureManager.java 版 权： Copyright Huawei Tech. Co. Ltd. All Rights
 * Reserved. 描 述: [该类的简要描述] 创建人: zhaozeyang 创建时间:2012-5-20 修改人： 修改时间: 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.basic.emotion.bean.EmojiBean;

/**
 * 
 * 此类的功能是贴图管理器，提供贴图的加载，解析<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-4-26]
 */
public class PictureManager
{
    /**
     * TAG
     */
    private static final String TAG = "PictureManager";
    
    /**
     * PictureManager的实例
     */
    private static PictureManager sInstance;
    
    private Map<String, EmojiBean> emojiMap = new HashMap<String, EmojiBean>();
    
    /**
     * 贴图表情集合
     */
    private List<List<List<Map<String, EmojiBean>>>> listTotal = new ArrayList<List<List<Map<String, EmojiBean>>>>();
    
    /**
     * 图片资源的前缀
     */
    private WeakReference<List<List<List<Map<String, EmojiBean>>>>> picturesWeakRef = new WeakReference<List<List<List<Map<String, EmojiBean>>>>>(
            listTotal);
    
    /**
     * 上下文
     */
    private Context mContext;
    
    private BitmapFactory.Options mBitmapOptions;;
    
    /**
     * 贴图资源文件夹
     */
    private String[] folder = { "001", "002" };
    
    /**
     * 贴图文件的软引用集合
     */
    private SoftReference<Map<String, EmojiBean>> refEmojiMap = new SoftReference<Map<String, EmojiBean>>(
            emojiMap);
    
    /**
     * 贴图资源文件的集合： 键为图片资源 ，值为描述
     */
    private Map<String, String> en2Cn = new HashMap<String, String>();
    
    /**
     * 构造方法 初始化资源文件
     * @param context 上下文
     */
    private PictureManager(Context context)
    {
        mContext = context;
        initPinupEmotion();
    }
    
    /**
     * 获得PictureManager实例<BR>
     * @param context 上下文
     * @return PictureManager实例
     */
    public static PictureManager getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new PictureManager(context);
        }
        return sInstance;
    }
    
    /**
     * 初始化贴图资源<BR>
     */
    private void initPinupEmotion()
    {
        en2Cn.put("001001.png", mContext.getString(R.string.bangj_1));
        en2Cn.put("001002.png", mContext.getString(R.string.fanu_1));
        en2Cn.put("001003.png", mContext.getString(R.string.kaic_1));
        en2Cn.put("001004.png", mContext.getString(R.string.kanb_1));
        en2Cn.put("001005.png", mContext.getString(R.string.mei_1));
        en2Cn.put("001006.png", mContext.getString(R.string.mmx_1));
        en2Cn.put("001007.png", mContext.getString(R.string.mofa_1));
        en2Cn.put("001008.png", mContext.getString(R.string.outu_1));
        en2Cn.put("001009.png", mContext.getString(R.string.qiaod_1));
        en2Cn.put("001010.png", mContext.getString(R.string.qinl_1));
        en2Cn.put("001011.png", mContext.getString(R.string.quanj_1));
        en2Cn.put("001012.png", mContext.getString(R.string.shangx_1));
        en2Cn.put("001013.png", mContext.getString(R.string.taop_1));
        en2Cn.put("001014.png", mContext.getString(R.string.tians_1));
        en2Cn.put("001015.png", mContext.getString(R.string.tuzhu_1));
        en2Cn.put("001016.png", mContext.getString(R.string.xiaot_1));
        en2Cn.put("001017.png", mContext.getString(R.string.yun_1));
        
        // 龙系列
        en2Cn.put("002001.png", mContext.getString(R.string.chaor_2));
        en2Cn.put("002002.png", mContext.getString(R.string.gongf_2));
        en2Cn.put("002003.png", mContext.getString(R.string.guangb_2));
        en2Cn.put("002004.png", mContext.getString(R.string.huax_2));
        en2Cn.put("002005.png", mContext.getString(R.string.jingx_2));
        en2Cn.put("002006.png", mContext.getString(R.string.kds_2));
        en2Cn.put("002007.png", mContext.getString(R.string.xiangn_2));
        en2Cn.put("002008.png", mContext.getString(R.string.yihuo_2));
        en2Cn.put("002009.png", mContext.getString(R.string.yun_2));
        en2Cn.put("002010.png", mContext.getString(R.string.zhain_2));
        en2Cn.put("002011.png", mContext.getString(R.string.zsfz_2));
        en2Cn.put("002012.png", mContext.getString(R.string.zui_2));
    }
    
    /**
     * 获得所有的贴图对象，用来作缓存
     * 
     * @return 所有贴图对象的缓存集合
     */
    public SoftReference<Map<String, EmojiBean>> getAllEmojis()
    {
        if (refEmojiMap.get() == null || refEmojiMap.get().size() == 0)
        {
            for (int i = 0; i < folder.length; i++)
            {
                String[] name = null;
                try
                {
                    name = mContext.getResources().getAssets().list(folder[i]);
                    for (int j = 0; j < name.length; j++)
                    {
                        String fileName = folder[i] + "/" + name[j];
                        InputStream is = mContext.getResources()
                                .getAssets()
                                .open(fileName);
                        Bitmap bitmap = BitmapFactory.decodeStream(is,
                                null,
                                getBitmapOptions());
                        EmojiBean emojiBean = new EmojiBean();
                        emojiBean.setAlt(en2Cn.get(name[j]));
                        emojiBean.setTtid(fileName);
                        emojiBean.setEmoji(bitmap);
                        emojiMap.put(fileName, emojiBean);
                    }
                }
                catch (IOException e)
                {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }
        refEmojiMap = new SoftReference<Map<String, EmojiBean>>(emojiMap);
        return refEmojiMap;
        
    }
    
    /**
     * 获得所有的贴图资源, 目前是加载全部的图片资源，后续升级时图片资源过多时，可以修改逻辑，按需索取，不需要全部加载
     * 
     * @return 所有的贴图资源的弱引用
     */
    public WeakReference<List<List<List<Map<String, EmojiBean>>>>> getAllPictures()
    {
        if (picturesWeakRef.get() == null || picturesWeakRef.get().size() == 0)
        {
            for (int i = 0; i < folder.length; i++)
            {
                String[] name = null;
                try
                {
                    name = mContext.getResources().getAssets().list(folder[i]);
                    List<Map<String, EmojiBean>> itemList = new ArrayList<Map<String, EmojiBean>>();
                    List<List<Map<String, EmojiBean>>> tabItemList = new ArrayList<List<Map<String, EmojiBean>>>();
                    
                    for (int j = 0; j < name.length; j++)
                    {
                        Map<String, EmojiBean> map = new HashMap<String, EmojiBean>();
                        String fileName = folder[i] + "/" + name[j];
                        InputStream is = mContext.getResources()
                                .getAssets()
                                .open(fileName);
                        Bitmap bitmap = BitmapFactory.decodeStream(is,
                                null,
                                getBitmapOptions());
                        EmojiBean emojiBean = new EmojiBean();
                        emojiBean.setAlt(en2Cn.get(name[j]));
                        emojiBean.setTtid(fileName);
                        emojiBean.setEmoji(bitmap);
                        map.put(fileName, emojiBean);
                        itemList.add(map);
                        
                        if (itemList.size() != 0 && itemList.size() % 8 == 0)
                        {
                            tabItemList.add(itemList);
                            itemList = new ArrayList<Map<String, EmojiBean>>();
                        }
                    }
                    if (itemList.size() != 0)
                    {
                        tabItemList.add(itemList);
                    }
                    listTotal.add(tabItemList);
                }
                catch (IOException e)
                {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }
        picturesWeakRef = new WeakReference<List<List<List<Map<String, EmojiBean>>>>>(
                listTotal);
        return picturesWeakRef;
        
    }
    
    /**
     * 设置加载图片时的options<BR>
     * @return BitmapFactory.Options
     */
    public BitmapFactory.Options getBitmapOptions()
    {
        if (mBitmapOptions == null)
        {
            mBitmapOptions = new BitmapFactory.Options();
            mBitmapOptions.inPurgeable = true;
            mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        return mBitmapOptions;
    }
}
