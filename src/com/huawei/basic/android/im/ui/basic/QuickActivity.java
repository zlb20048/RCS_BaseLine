/*
 * 文件名: QuickActivity.java
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

import java.util.List;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.ui.basic.QuickBar.OnLetterPressListener;

/**
 * 
 * 含快捷条的Activity<BR>
 * [功能详细描述]
 * @Lidan 杨凡
 * @version [RCS Client V100R001C03, 2012-3-2]
 */
public abstract class QuickActivity extends BasicActivity implements
        OnLetterPressListener, OnScrollListener
{
    /**
     * FrameLayout第二层，快捷条
     */
    private QuickBar mQuickBar;
    
    /**
     * FrameLayout第三层，显示快捷条中当前定位到的索引
     */
    private TextView mShortCutTextView;
    
    /**
     * mListView对应的Adapter
     */
    private QuickAdapter mQuickAdapter;
    
    /**
     * 页面是否已经初始化
     */
    private boolean mViewInitialized;
    
    /**
     * FrameLayout第一层，列表
     */
    private ListView mListView;
    
    /**
     * 快捷条松开的处理<BR>
     * @see com.huawei.basic.android.im.ui.basic.QuickBar.
     * OnLetterPressListener#onPressUp()
     */
    @Override
    public void onPressUp()
    {
        if (mViewInitialized)
        {
            mShortCutTextView.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 快捷条松开的处理<BR>
     * [功能详细描述]
     * 
     * @param letter 当前按中的字母
     * @see com.huawei.basic.android.im.ui.basic.QuickBar.
     * OnLetterPressListener#onPressDown(java.lang.String)
     */
    @Override
    public void onPressDown(String letter)
    {
        if (mViewInitialized)
        {
            if (letter != null && letter.length() > 0)
            {
                mShortCutTextView.setText(letter.subSequence(0, 1));
            }
            else
            {
                mShortCutTextView.setText(letter);
            }
            mShortCutTextView.setVisibility(View.VISIBLE);
            
            // 判断列表中是否包含该字母开头的好友，如果有，
            //将列表移动，使含该字母开头的好友从屏幕顶端开始显示
            int index = mQuickAdapter.getIndexOfSpecifiedLetter(letter);
            if (index != -1)
            {
                mListView.setSelectionFromTop(index, 0);
            }
        }
    }
    
    /**
     * 
     * 右侧快捷栏对应的字母数组<BR>
     * [功能详细描述]
     * 
     * @return 数组
     */
    protected String[] getLetterArray()
    {
        return new String[] { getResources().getString(R.string.search_quick),
                getResources().getString(R.string.system_plugin_shot_cut), "A",
                "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
                "Z", "#" };
    }
    
    /**
     * 
     * 返回快捷条图片资源id<BR>
     * [功能详细描述]
     * 
     * @return 图片资源id
     */
    protected int getQuickBarResId()
    {
        return R.drawable.quick_bar_has_search;
    }
    
    /**
     * 
     * 是否有搜索框<BR>
     * [功能详细描述]
     * 
     * @return boolean
     */
    protected boolean hasSearch()
    {
        return true;
    }
    
    /**
     * get mListView
     * @return the mListView
     */
    protected ListView getListView()
    {
        return mListView;
    }

    /**
     * get QuickAlphabeticAdapter<BR>
     * [功能详细描述] new XXXQuickAlphabeticAdapter()
     * 
     * @return QuickAlphabeticAdapter
     */
    protected abstract QuickAdapter getQuickAdapter();
    
    /**
     * 
     * 对列表进行排序以及插入A-Z等快捷索引的String，生成用于Adapter进行处理的list<BR>
     * [功能详细描述]
     * 
     * @param contactList BaseContactModel列表
     * @return 用于Adapter进行处理的list
     */
    protected abstract List<Object> generateDisplayList(List<?> contactList);
    
    /**
     * 
     * 更新UI，并且是唯一的入口，不可继承<BR>
     * 最原始的BaseContactModel列表
     * 
     * @param contactList 列表
     */
    protected final void updateView(List<?> contactList)
    {
        // 初始化页面组件对象
        if (!mViewInitialized)
        {
            mListView = (ListView) findViewById(R.id.listview);
            mListView.setFocusableInTouchMode(true);
            TextView view = new TextView(this);
            view.setLines(0);
            mListView.addFooterView(view, null, true);
            mListView.setFooterDividersEnabled(true);
            mQuickBar = (QuickBar) findViewById(R.id.fast_scroller);
            //            mQuickBar.setImageResource(getQuickBarResId());
            mQuickBar.setBackgroundResource(getQuickBarResId());
            mQuickBar.setLetterArray(getLetterArray());
            mShortCutTextView = (TextView) findViewById(R.id.fast_position);
            mQuickBar.setOnLetterPressListener(this);
            
            mQuickAdapter = getQuickAdapter();
            mListView.setAdapter(mQuickAdapter);
            mListView.setOnScrollListener(this);
            mViewInitialized = true;
        }
        // 对contactList进行封装处理
        List<Object> displayList = generateDisplayList(contactList);
        
        // 设置QuickAdapter
        mQuickAdapter.setDataSource(this, displayList, hasSearch());
        mQuickAdapter.notifyDataSetChanged();
    }
    
    /**
     * 
     * onScroll
     * [功能详细描述]
     * @param view view
     * @param firstVisibleItem firstVisibleItem
     * @param visibleItemCount visibleItemCount
     * @param totalItemCount totalItemCount
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        
    }
    
    /**
     * 
     * onScrollStateChanged
     * [功能详细描述]
     * @param view view
     * @param scrollState scrollState
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        
    }
    
}
