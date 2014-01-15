/*
 * 文件名: BaseListAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 16, 2012] 
 */
public class BaseListAdapter extends BaseAdapter
{
    /**
     * 头像的默认宽度，子类可根据需要重新定义
     */
    protected static final int HEAD_WIDTH = 52;

    /**
     * 头像的默认高度，子类可根据需要重新定义
     */
    protected static final int HEAD_HEIGHT = 52;
    
    /**
     * 头像的圆角
     */
    protected static final int HEAD_ROTE = 8;

    private List<? extends Object> listItems;

    /**
     * 
     * 设置list展示的数据源<BR>
     * [功能详细描述]
     * 
     * @param list 数据List
     */
    public void setData(List<? extends Object> list)
    {
        listItems = list;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @return int
     * @see android.widget.Adapter#getCount()
     */

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return listItems != null ? listItems.size() : 0;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param position position
     * @return Object
     * @see android.widget.Adapter#getItem(int)
     */

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return listItems.get(position);
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param position position
     * @return long
     * @see android.widget.Adapter#getItemId(int)
     */

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 获取item展示的view,子类需要重写该方法获取自定义的view<BR>
     * [功能详细描述]
     * 
     * @param position item的位置
     * @param convertView 缓存的view
     * @param parent parent
     * @return view view
     * @see android.widget.Adapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取数据源<BR>
     * @return ArrayList<? extends Object> list
     */
    public List<? extends Object> getDataSrc()
    {
        return listItems;
    }

}