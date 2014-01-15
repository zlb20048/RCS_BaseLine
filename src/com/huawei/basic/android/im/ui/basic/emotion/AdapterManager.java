/*
 * 文件名: AdapterManager.java
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.basic.emotion.EmotionWindow.TabItemBean;
import com.huawei.basic.android.im.ui.basic.emotion.bean.EmojiBean;

/**
 * 
 * 表情界面的adapter管理类<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-4-26]
 */
public class AdapterManager
{
    private static final String TAG = "AdapterManager";
    
    /**
     * 
     * 标题和Tab的自定义Adapter
     */
    public static class TitleAdapter extends BaseAdapter
    {
        /**
         * 上下文
         */
        private Context mContext;
        
        /**
         * 字体颜色
         */
        private int fontColor;
        
        /**
         * 贴图tab标题的文本框数组
         */
        private TextView[] title;
        
        /**
         * 构建菜单项
         * 
         * @param context 上下文
         * @param titles 标题
         * @param fontSize 字体大小
         * @param color 字体颜色
         */
        public TitleAdapter(Context context, String[] titles, int fontSize,
                int color)
        {
            this.mContext = context;
            this.fontColor = color;
            this.title = new TextView[titles.length];
            float fPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    40,
                    mContext.getResources().getDisplayMetrics());
            
            // 同理 px转dip：
            // float fDip =
            // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 307,
            // resources.getDisplayMetrics());
            // int iDip = Math.round(fDip);
            
            int iPx = Math.round(fPx);
            for (int i = 0; i < titles.length; i++)
            {
                title[i] = new TextView(mContext);
                title[i].setText(titles[i]);
                title[i].setTextSize(fontSize);
                title[i].setTextColor(fontColor);
                title[i].setGravity(Gravity.CENTER);
                title[i].setHeight(iPx);
                title[i].setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.bottom_tab_bg));
                
            }
        }
        
        public int getCount()
        {
            return title.length;
        }
        
        /**
         * 
         * 获得具体tab标题对象<BR>
         * @param position 标题位置
         * @return tab标题的textView
         * @see android.widget.Adapter#getItem(int)
         */
        public Object getItem(int position)
        {
            
            return title[position];
        }
        
        /**
         * 获得具体tab标题对象ID<BR>
         * @param position 标题位置
         * @return tab标题对象ID
         * @see android.widget.Adapter#getItemId(int)
         */
        public long getItemId(int position)
        {
            
            return title[position].getId();
        }
        
        /**
         * 显示每个ITEM<BR>
         * @param position 位置
         * @param convertView 布局元素
         * @param parent ViewGroup
         * @return 每个item的view
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = null;
            if (convertView == null)
            {
                v = title[position];
            }
            else
            {
                v = convertView;
            }
            return v;
        }
        
    }
    
    /**
     * 
     * 标题和Tab的自定义Adapter
     */
    public static class TabAdapter extends BaseAdapter
    {
        /**
         * 上下文
         */
        private Context mContext;
        
        /**
         * 文字颜色
         */
        private int fontColor;
        
        private TextView[] title;
        
        private ArrayList<TabItemBean> list;
        
        /**
         * 构建菜单项
         * 
         * @param context 上下文
         * @param titles 标题
         * @param fontSize 字体大小
         * @param color 字体颜色
         */
        public TabAdapter(Context context, String[] titles, int fontSize,
                int color)
        {
            this.mContext = context;
            this.fontColor = color;
            this.title = new TextView[titles.length];
            for (int i = 0; i < titles.length; i++)
            {
                title[i] = new TextView(mContext);
                title[i].setText(titles[i]);
                title[i].setTextSize(fontSize);
                title[i].setTextColor(fontColor);
                title[i].setGravity(Gravity.CENTER);
                title[i].setHeight(68);
                title[i].setSingleLine();
                title[i].setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.unselected_up_tab));
            }
        }
        
        /**
         * 构造方法<BR>
         * @param c 上下文
         * @param li tab集合
         */
        public TabAdapter(Context c, ArrayList<TabItemBean> li)
        {
            mContext = c;
            list = li;
        }
        
        /**
         * 获得tab个数<BR>
         * @return tab个数
         * @see android.widget.Adapter#getCount()
         */
        public int getCount()
        {
            if (list == null)
            {
                return 0;
            }
            
            return list.size();
        }
        
        /**
         * 
         * 获得tab实体<BR>
         * @param position 位置
         * @return tab实体
         * @see android.widget.Adapter#getItem(int)
         */
        public Object getItem(int position)
        {
            return list.get(position);
        }
        
        /**
         * 获得tabId<BR>
         * @param position 位置
         * @return tabId
         * @see android.widget.Adapter#getItemId(int)
         */
        public long getItemId(int position)
        {
            return 0;
        }
        
        /**
         * 显示tab<BR>
         * @param position 位置
         * @param convertView 每个tab的view 
         * @param parent parent
         * @return tab
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.emotion_item_tab, null);
                holder.relLayout = (RelativeLayout) convertView;
                holder.imgView = (ImageView) convertView.findViewById(R.id.item_tab);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imgView.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(list.get(position).getResId()));
            if (list.get(position).getResId() == EmotionWindow.SELECTED)
            {
                holder.relLayout.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.bottom_tab_selected));
            }
            else
            {
                holder.relLayout.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.bottom_tab_bg));
            }
            return convertView;
        }
        
        /**
         * ViewHolder<BR>
         * @author zhaozeyang
         * @version [RCS Client V100R001C03, 2012-4-26]
         */
        static class ViewHolder
        {
            /**
             * 布局
             */
            private RelativeLayout relLayout;
            
            /**
             * 图片
             */
            private ImageView imgView;
        }
        
    }
    
    /**
     * 图片的adapter<BR>
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-4-26]
     */
    public static class ImageAdapter extends BaseAdapter
    {
        /**
         * 上下文
         */
        private Context mContext;
        
        private List<Map<String, EmojiBean>> list;
        
        /**
         * 构造方法
         * @param c 上下文
         * @param li 贴图资源集合
         */
        public ImageAdapter(Context c, List<Map<String, EmojiBean>> li)
        {
            mContext = c;
            list = li;
        }
        
        /**
         * 
         * 返回图片个数<BR>
         * @return 图片个数
         * @see android.widget.Adapter#getCount()
         */
        public int getCount()
        {
            if (list == null)
            {
                return 0;
            }
            
            return list.size();
        }
        
        /**
         * 
         * 获得图片<BR>
         * @param position 位置
         * @return Item view对象
         * @see android.widget.Adapter#getItem(int)
         */
        public Object getItem(int position)
        {
            return list.get(position);
        }
        
        /* 一定要重写的方法getItemId,传并position */
        /**
         * 
         * 获得指定位置ID<BR>
         * @param position 位置 
         * @return ItemId
         * @see android.widget.Adapter#getItemId(int)
         */
        public long getItemId(int position)
        {
            return position;
        }
        
        /* 几定要重写的方法getView,传并几View对象 */
        /**
         * 
         * 每个条目的布局定义<BR>
         * @param position position
         * @param convertView convertView
         * @param parent parent
         * @return View
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = null;
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item, null);
            }
            else
            {
                view = convertView;
            }
            
            float fPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    64,
                    mContext.getResources().getDisplayMetrics());
            
            // 同理 px转dip：
            // float fDip =
            // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 307,
            // resources.getDisplayMetrics());
            // int iDip = Math.round(fDip);
            
            int iPx = Math.round(fPx);
            ImageView iv = (ImageView) view.findViewById(R.id.item_image);
            iv.getLayoutParams().height = iPx;
            iv.getLayoutParams().width = iPx;
            iv.setImageBitmap(getPicture(position));
            return view;
        }
        
        /**
         * 获得具体图片<BR>
         * @param position 位置
         * @return 图片
         */
        private Bitmap getPicture(int position)
        {
            EmojiBean value = null;
            try
            {
                Map<String, EmojiBean> map = list.get(position);
                // 获得键的集合
                Set<String> keyset = map.keySet();
                if (keyset != null)
                {
                    Iterator<String> it = keyset.iterator();
                    if (it.hasNext())
                    {
                        Object key = it.next();
                        value = map.get(key);
                    }
                }
            }
            catch (Exception e)
            {
                Logger.e(TAG, "error " + e.getMessage());
            }
            return value.getEmoji();
        }
        
    }
    
}
