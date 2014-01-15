/*
 * 文件名: QuickAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;

/**
 * 快捷条<BR>
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-13]
 */
public abstract class QuickAdapter extends BaseAdapter implements TextWatcher
{
    
    /**
     * 搜索框部分<BR>
     * @author Lidan
     * @version [RCS Client V100R001C03, 2012-2-13]
     */
    private static class SearchViewHolder
    {
        /**
         * 输入框
         */
        private EditText mSearch;
        
        /**
         * 删除按钮
         */
        private ImageView mCancel;
    }
    
    /**
     * debug tag
     */
    private static final String TAG = "QuickAdapter";
    
    /**
     * 列表中item布局类型的个数为
     * 3：搜索框、快捷item（@、A-Z、#）（可编辑）
     * 、各页面的item
     */
    private static final int VIEW_TYPE_COUNT = 3;
    
    /**
     * 0：搜索框；1：快捷item；2：主item
     */
    private static final int VIEW_TYPE_SEARCH = 0, VIEW_TYPE_SHORTCUT = 1,
            VIEW_TYPE_MAIN_ITEM = 2;
    
    /**
     * 该Adapter归属的Activity对象
     */
    private Activity mBelongedActivity;
    
    /**
     * 显示列表list,第一项为搜索框
     */
    private List<Object> mDisplayList;
    
    /**
     * 调用者传入的数据
     */
    private List<Object> mOriginalList;
    
    /**
     * 是否包含搜索框
     */
    private boolean mHasSearch;
    
    /**
     * 是否在搜索状态
     */
    private boolean mInSearch;
    
    /**
     * view holder
     */
    private SearchViewHolder mHolder;
    
    /**
     * get the mDisplayList
     * 
     * @return the mDisplayList
     */
    public List<Object> getDisplayList()
    {
        return mDisplayList;
    }
    
    /**
     * get the mBelongedActivity
     * 
     * @return the mBelongedActivity
     */
    public Activity getBelongedActivity()
    {
        return mBelongedActivity;
    }
    
    /**
     *  设置数据源<BR>
     * 
     * @param belongedActivity 该Adapter所属QuickActivty对象
     * @param originalList 用于展示的list
     * @param hasSearch hasSearch
     */
    public void setDataSource(Activity belongedActivity,
            List<Object> originalList, boolean hasSearch)
    {
        this.mBelongedActivity = belongedActivity;
        mOriginalList = originalList;
        
        if (mOriginalList == null)
        {
            mOriginalList = new ArrayList<Object>();
        }
        mHasSearch = hasSearch;
        if (hasSearch)
        {
            // 添加搜索框到第一项
            mOriginalList.add(0,
                    belongedActivity.getResources()
                            .getString(R.string.search_quick));
        }
        
        mDisplayList = new ArrayList<Object>();
        mDisplayList.addAll(mOriginalList);
    }
    
    /**
     * 
     * 从mDisplayList获取指定letter的索引，如果没有返回-1 <BR>
     * [功能详细描述]
     * 
     * @param letter 指定的姓名首字母
     * @return 指定letter的索引
     */
    public int getIndexOfSpecifiedLetter(String letter)
    {
        if (mDisplayList != null)
        {
            return mDisplayList.indexOf(letter);
        }
        else
        {
            return -1;
        }
    }
    
    /**
     * <BR>
     * [功能详细描述]
     * 
     * @param s Editable
     * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
     */
    
    @Override
    public void afterTextChanged(Editable s)
    {
        
        mInSearch = s.toString().length() > 0;
        // 获取用户输入的搜索关键字
        String searchKey = s.toString().trim();
        Log.d(TAG, "afterTextChanged() searchKey : " + searchKey);
        if (mDisplayList != null)
        {
            mDisplayList.clear();
        }
        
        if (mOriginalList != null)
        {
            for (Object obj : mOriginalList)
            {
                if (obj instanceof String || isMatched(obj, searchKey))
                {
                    mDisplayList.add(obj);
                }
            }
            
            // 对搜索后的结果进行过滤。例如，
            //若“A"下面没有相关的好友，则将“A"也从集合中删除。
            for (int i = mDisplayList.size() - 1; i > 0
                    && i < mDisplayList.size(); i--)
            {
                if (mDisplayList.get(i) instanceof String)
                {
                    if (i == mDisplayList.size() - 1)
                    {
                        mDisplayList.remove(i);
                        continue;
                    }
                    else if (mDisplayList.get(i - 1) instanceof String)
                    {
                        if (i - 1 != 0)
                        {
                            mDisplayList.remove(i - 1);
                        }
                    }
                }
            }
            Log.d(TAG, "mDisplayList : " + mDisplayList.size());
            notifyDataSetChanged();
        }
    }
    
    /**
     * <BR>
     * [功能详细描述]
     * 
     * @param s CharSequence
     * @param start int
     * @param count int
     * @param after int
     * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
     *      int, int, int)
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after)
    {
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount()
    {
        if (mDisplayList != null)
        {
            return mDisplayList.size();
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getItem(int position)
    {
        if (mDisplayList != null)
        {
            return mDisplayList.get(position);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        
        // 处理搜索框的显示
        if (position == 0 && mHasSearch)
        {
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(mBelongedActivity,
                        R.layout.component_quick_search,
                        null);
                if (null == mHolder)
                {
                    mHolder = new SearchViewHolder();
                    mHolder.mSearch = (EditText) convertView.findViewById(R.id.search_friend);
                    mHolder.mSearch.setOnFocusChangeListener(new OnFocusChangeListener()
                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            mHolder.mSearch.setSelected(hasFocus);
                        }
                    });
                    mHolder.mCancel = (ImageView) convertView.findViewById(R.id.cancel);
                    mHolder.mSearch.addTextChangedListener(this);
                }
                convertView.setTag(mHolder);
            }
            else
            {
                mHolder = (SearchViewHolder) convertView.getTag();
            }
            mHolder.mSearch.requestFocus();
            mHolder.mCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mHolder.mSearch.setText("");
                }
            });
            mHolder.mCancel.setVisibility(mInSearch ? View.VISIBLE
                    : View.INVISIBLE);
        }
        
        // 处理字母的显示逻辑
        else if (mDisplayList.get(position) instanceof String)
        {
            TextView initialTV;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(mBelongedActivity,
                        R.layout.component_contact_initial_letter_item,
                        null);
                initialTV = (TextView) convertView.findViewById(R.id.group);
                convertView.setTag(initialTV);
            }
            else
            {
                initialTV = (TextView) convertView.getTag();
            }
            initialTV.setText(mDisplayList.get(position).toString());
        }
        
        // 各Adapter自己处理
        else
        {
            return getItemView(position, convertView, parent);
        }
        return convertView;
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract View getItemView(int position, View convertView,
            ViewGroup parent);
    
    /**
     * 
     * 判断该ContactInfo是否与关键字匹配<BR>
     * 
     * @param obj Object
     * @param key 匹配的关键字
     * @return 如果与关键字匹配，返回true；反之返回false
     */
    public abstract boolean isMatched(Object obj, String key);
    
    /**
     * 
     * 是否所有item可选中<BR>
     * [功能详细描述]
     * 
     * @return boolean 是否全可选中
     * @see android.widget.BaseAdapter#areAllItemsEnabled()
     */
    public boolean areAllItemsEnabled()
    {
        // 设置ListView中不是所有的选项都可以选
        return false;
    }
    
    /**
     * 
     * 指定位置是否可选中<BR>
     * [功能详细描述]
     * 
     * @param position 需要判断的位置
     * @return boolean 是否可选
     * @see android.widget.BaseAdapter#isEnabled(int)
     */
    public boolean isEnabled(int position)
    {
        return !(mDisplayList.get(position) instanceof String);
    }
    
    /**
     * 
     * 获取当前位置item的视图类型<BR>
     * [功能详细描述]
     * 
     * @param position item id
     * @return 类型设置为{@link #VIEW_TYPE_SEARCH}, {@link #VIEW_TYPE_SHORTCUT},
     *         {@link #VIEW_TYPE_MAIN_ITEM}
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position)
    {
        
        // 第一个为搜索框
        if (position == 0 && mHasSearch)
        {
            return VIEW_TYPE_SEARCH;
        }
        else
        {
            return mDisplayList.get(position) instanceof String ? VIEW_TYPE_SHORTCUT
                    : VIEW_TYPE_MAIN_ITEM;
        }
    }
    
    /**
     * 
     * 返回视图类型总数<BR>
     * 列表中item布局类型的个数为
     * 3：搜索框、快捷item（搜索、@、A-Z、#）（可编辑）、
     * 显示快捷item的TextView
     * 
     * @return 总数3个
     * @see android.widget.BaseAdapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount()
    {
        return VIEW_TYPE_COUNT;
    }
    
    /**
     * 获取搜索框<BR>
     * @return 搜索框
     */
    public EditText getSearchEditText()
    {
        return mHolder == null ? null : mHolder.mSearch;
    }
    
}
