/*
 * 文件名: PullToRefreshListView.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-23
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 下拉刷新的list view<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-23] 
 */
public class PullToRefreshListView extends ListView implements OnScrollListener
{
    /**
     * debug tag
     */
    private static final String TAG = "PullToRefreshListView";
    
    /**
     * 刷新的ProgressBar
     */
    private ProgressBar mRefreshViewProgress;
    
    /**
     * 刷新布局对象
     */
    private LinearLayout refreshLayout;
    
    /**
     * 刷新对应的ListView header高度
     */
    private int headerHeight;
    
    /**
     * 按下时y坐标
     */
    private float mDownY = 0;
    
    /**
     * 是否在refresh状态
     */
    private boolean refresh = false;
    
    /**
     * refresh是否可用
     */
    private boolean canRefresh = false;
    
    /**
     * 刷新监听器对象
     */
    private OnRefreshListener onRefreshListener;
    
    /**
     * 设置最小的可刷新条目数，如果显示的条数小于该值，则不可刷新
     */
    private int minCount = 0;
    
    /**
     * 刷新是否可用
     */
    private boolean refreshEnable = true;
    
    /**
     * PullToRefreshListView构造函数
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle int
     */
    public PullToRefreshListView(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    /**
     * PullToRefreshListView构造函数
     * @param context Context
     * @param attrs AttributeSet
     */
    public PullToRefreshListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    /**
     * PullToRefreshListView构造函数
     * @param context Context
     */
    public PullToRefreshListView(Context context)
    {
        super(context);
        init(context);
    }
    
    /**
     * 
     * 设置最小可允许刷新的列表条目个数<BR>
     * 
     * @param aMinCount 最小可刷新条目个数
     */
    public void setRefreshEnableByCount(int aMinCount)
    {
        this.minCount = aMinCount;
    }
    
    /**
     * 设置刷新是否可用
     * <BR>
     * 
     * @param enable 刷新是否可用
     */
    public void setRefreshEnable(boolean enable)
    {
        refreshEnable = enable;
    }
    
    /**
     * 
     * 下拉刷新是否有效<BR>
     * 
     * @return 下拉刷新可用时返回true
     */
    private boolean isRefreshEnable()
    {
        return refreshEnable && getAdapter() != null && !getAdapter().isEmpty()
                && getAdapter().getCount() > minCount;
    }
    
    private void init(Context context)
    {
        // 1.初始化页眉组件对象
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refreshLayout = (LinearLayout) inflater.inflate(R.layout.im_refresh_progress_bar,
                this,
                false);
        mRefreshViewProgress = (ProgressBar) refreshLayout.findViewById(R.id.pull_to_refresh_progress);
        
        // 2.添加到页眉显示
        addHeaderView(refreshLayout);
        
        // 3.计算出页眉显示高度，在刷新时需要根据下拉的高度与该高度进行比较，如果下拉的高度大于页眉显示高度，则显示正在刷新的页眉
        measureView(refreshLayout);
        headerHeight = refreshLayout.getMeasuredHeight();
        
        // 4.一开始不显示页眉
        refreshLayout.removeView(mRefreshViewProgress);
        
        setOnScrollListener(this);
        
    }
    
    // 估算headview的width和height  
    private void measureView(View child)
    {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null)
        {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0)
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        }
        else
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see android.widget.ListView#onTouchEvent(android.view.MotionEvent)
     */
    
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (isRefreshEnable())
        {
            
            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mDownY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    
                    float curY = ev.getY();
                    if (canRefresh && curY - mDownY > headerHeight)
                    {
                        refresh = true;
                        refreshLayout.removeAllViews();
                        refreshLayout.addView(mRefreshViewProgress);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startRefresh();
                    break;
                
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        if (isRefreshEnable())
        {
            canRefresh = firstVisibleItem == 0;
        }
    }
    
    /**
     * 
     * 刷新完毕时需要执行的动作<BR>
     *
     */
    public void onRefreshComplete()
    {
        refresh = false;
        refreshLayout.removeView(mRefreshViewProgress);
    }
    
    /**
     * set onRefreshListener
     * @param onRefreshListener the onRefreshListener to set
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener)
    {
        this.onRefreshListener = onRefreshListener;
    }
    
    private void startRefresh()
    {
        
        if (refresh)
        {
            // start refresh.
            if (onRefreshListener != null)
            {
                Logger.d(TAG, "start Refresh list view");
                onRefreshListener.onRefresh();
            }
        }
    }
    
    /**
     * 
     * 刷新监听器接口定义<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-22]
     */
    public interface OnRefreshListener
    {
        /**
         * 
         * 正在刷新方法<BR>
         *
         */
        void onRefresh();
    }
}
