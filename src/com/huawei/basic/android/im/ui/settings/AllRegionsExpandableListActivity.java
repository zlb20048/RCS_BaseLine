/*
 * 文件名: MessageTipsSettingsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhoumi
 * 创建时间:2012-4-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 地区选择
 * @author zhoumi
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 14, 2012]
 */
public class AllRegionsExpandableListActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 国家的静态标示
     */
    public static final String COUNTRY = "country";
    
    /**
     * 省份的静态标示
     */
    public static final String PROVINCE = "province";
    
    /**
     * 城市的静态标示
     */
    public static final String CITY = "city";
    
    /**
     * Log 标签
     */
    private static final String TAG = "AllRegionsExpandableListActivity";
    
    /**
     * 地区的静态标示
     */
    //    private static final String REG = "Region";
    
    /**
     * 所有地区的适配器
     */
    private MyExpandableListAdapter mAdapter;
    
    /**
     * 展开列表控件
     */
    private ExpandableListView mAllRegionsExpandableList;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allregions_list);
        mAllRegionsExpandableList = (ExpandableListView) this.findViewById(R.id.list);
        Button returnBtn = (Button) this.findViewById(R.id.left_button);
        returnBtn.setOnClickListener(this);
        TextView titleTv = (TextView) this.findViewById(R.id.title);
        titleTv.setText(getResources().getString(R.string.setting_select_district));
        
        mAdapter = new MyExpandableListAdapter();
        mAllRegionsExpandableList.setAdapter(mAdapter);
        Logger.v(TAG, "mAllregionsexpandableList onCreate.......");
    }
    
    private class MyExpandableListAdapter extends BaseExpandableListAdapter
    {
        private String[] groups = getResources().getStringArray(R.array.message_province);
        
        private String[][] children = { getListFrom(R.array.message_beijin),
                getListFrom(R.array.message_tianjin),
                getListFrom(R.array.message_shanghai),
                getListFrom(R.array.message_chongqing),
                getListFrom(R.array.message_xinjiang),
                getListFrom(R.array.message_xizang),
                getListFrom(R.array.message_ninxia),
                getListFrom(R.array.message_neimenggu),
                getListFrom(R.array.message_guangxi),
                getListFrom(R.array.message_heilongjiang),
                getListFrom(R.array.message_jilin),
                getListFrom(R.array.message_liaonin),
                getListFrom(R.array.message_hebei),
                getListFrom(R.array.message_shandong),
                getListFrom(R.array.message_jiangsu),
                getListFrom(R.array.message_anhui),
                getListFrom(R.array.message_fujian),
                getListFrom(R.array.message_guangdong),
                getListFrom(R.array.message_hainan),
                getListFrom(R.array.message_yunnan),
                getListFrom(R.array.message_guizhou),
                getListFrom(R.array.message_sichuan),
                getListFrom(R.array.message_hunan),
                getListFrom(R.array.message_hubei),
                getListFrom(R.array.message_henan),
                getListFrom(R.array.message_shanxi_jin),
                getListFrom(R.array.message_shanxi_shan),
                getListFrom(R.array.message_gansu),
                getListFrom(R.array.message_qinghai),
                getListFrom(R.array.message_jiangxi),
                getListFrom(R.array.message_taiwan),
                getListFrom(R.array.message_xianggang),
                getListFrom(R.array.message_aomen),
                getListFrom(R.array.message_others) };
        
        /**
         * 根据Id从资源数组中获取字符串数组
         * @param arrId 字符数组ID
         * @return 字符串数组
         */
        private String[] getListFrom(int arrId)
        {
            return getResources().getStringArray(arrId);
        }
        
        /**
         * 获取省份下相应的城市
         * @param groupPosition 省份ID
         * @param childPosition 城市ID
         * @return 城市
         */
        public Object getChild(int groupPosition, int childPosition)
        {
            return children[groupPosition][childPosition];
        }
        
        /**
         * 获取城市相应的id
         * @param groupPosition 省份ID
         * @param childPosition  城市ID
         * @return 城市ID
         */
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }
        
        /**
         * 获取一个省份下的城市的数量
         * @param groupPosition 省份ID
         * @return 该省份下的城市数
         */
        public int getChildrenCount(int groupPosition)
        {
            return children[groupPosition].length;
        }
        
        /**
         * 创建一个TextView的方法
         * @return TextView
         */
        //        public TextView getGenericView()
        //        {
        //            // Layout parameters for the ExpandableListView
        //            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
        //                    ViewGroup.LayoutParams.FILL_PARENT, 70);
        //            
        //            TextView textView = new TextView(mAllregionsexpandableList.this);
        //            textView.setLayoutParams(lp);
        //            // Center the text vertically
        //            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        //            // Set the text starting position
        //            textView.setPadding(44, 0, 0, 0);
        //            return textView;
        //        }
        
        /**
         * 点击省份后生成相应城市的二级界面
         * @param groupPosition 省份ID
         * @param childPosition 城市ID
         * @param isLastChild 最后一个城市
         * @param convertView 当前界面
         * @param parent 父界面
         * @return 城市界面
         */
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent)
        {
            final int groupId = groupPosition;
            final int childId = childPosition;
            ChildViewHolder viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new ChildViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.allregions_child_row,
                        null);
                viewHolder.cityTv = (TextView) convertView.findViewById(R.id.child_tv);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ChildViewHolder) convertView.getTag();
            }
            
            String cityname = (String) getChild(groupPosition, childPosition);
            if (cityname != null)
            {
                viewHolder.cityTv.setText(cityname);
                final View view = convertView;
                view.findFocus();
                viewHolder.cityTv.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View arg0)
                    {
                        Logger.v(TAG, "getCombinedChildId groupPosition ="
                                + groupId + " childPosition = " + childId);
                        Logger.v(TAG, "getCombinedChildId group name: ="
                                + groups[groupId] + " child name:"
                                + children[groupId][childId]);
                        view.setBackgroundResource(R.drawable.ic_btn_title_background);
                        Intent intent = new Intent();
                        
                        /*
                         * 把省份、城市、国家融合的一步
                         */
                        
                        // 当且仅当选中最后一个条目
                        if (groupId == groups.length - 1)
                        {
                            Logger.d(TAG, "国家地区");
                            intent.putExtra(COUNTRY, children[groupId][childId]);
                            intent.putExtra(PROVINCE, "");
                            intent.putExtra(CITY, "");
                        }
                        else
                        {
                            Logger.d(TAG, "省份城市");
                            intent.putExtra(COUNTRY, getString(R.string.china));
                            intent.putExtra(PROVINCE, groups[groupId]);
                            intent.putExtra(CITY, children[groupId][childId]);
                        }
                        // intent
                        // .putExtra(REG,
                        // groups[groupId] + "  "
                        // + children[groupId][childId]);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
            return convertView;
        }
        
        /**
         * 获取省份
         * @param groupPosition  省份ID
         * @return 省份
         */
        public Object getGroup(int groupPosition)
        {
            return groups[groupPosition];
        }
        
        /**
         * 获取省份数量
         * @return 省份数
         */
        public int getGroupCount()
        {
            return groups.length;
        }
        
        /**
         * 获取省份ID
         * @param groupPosition 省份ID
         * @return 省份ID
         */
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }
        
        /**
         * 生成省份界面
         * @param groupPosition 省份ID
         * @param isExpanded 是否可展开
         * @param convertView  当前视图
         * @param parent 父视图
         * @return 省份视图
         */
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent)
        {
            GroupViewHolder viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new GroupViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_row, null);
                viewHolder.tv = (TextView) convertView.findViewById(R.id.group_tv);
                viewHolder.pointImg = (ImageView) convertView.findViewById(R.id.point_right);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            Object object = getGroup(groupPosition);
            if (object != null)
            {
                if (!isExpanded)
                {
                    viewHolder.pointImg.setImageResource(R.drawable.pointer);
                }
                else
                {
                    viewHolder.pointImg.setImageResource(R.drawable.pointer_down);
                }
                viewHolder.tv.setText(getGroup(groupPosition).toString());
            }
            return convertView;
        }
        
        /**
         * 判断拥有稳定的ID
         * @return 是否稳定
         */
        public boolean hasStableIds()
        {
            return false;
        }
        
        /**
         * 点击一个省份时候触发的处理(收起其他展开的省份)
         * @param groupPosition 省份ID
         */
        @Override
        public void onGroupExpanded(int groupPosition)
        {
            //上次展开位置
            int lastExpandedGroupPosition = 0;
            if (groupPosition != lastExpandedGroupPosition)
            {
                mAllRegionsExpandableList.collapseGroup(lastExpandedGroupPosition);
            }
            super.onGroupExpanded(groupPosition);
            lastExpandedGroupPosition = groupPosition;
            
            Logger.v(TAG, "MyExpandableListAdapter onGroupExpanded!");
        }
        
        /**
         * 选择城市
         * @param groupPosition 省份ID
         * @param childPosition 城市ID
         * @return 是否选中
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return false;
        }
    }
    
    private static class GroupViewHolder
    {
        /**
         * 箭头图标
         */
        private ImageView pointImg;
        
        /**
         * 省份
         */
        private TextView tv;
    }
    
    private static class ChildViewHolder
    {
        /**
         * 城市
         */
        private TextView cityTv;
    }
    
    /**
     * 处理界面点击事件
     * @param v 点击视图
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            default:
                break;
        }
    }
}
