/*
 * 文件名: SelectCountryActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Mar 20, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.SelectCountryAction;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.model.CountryItemModel;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.QuickActivity;
import com.huawei.basic.android.im.ui.basic.QuickAdapter;
import com.huawei.basic.android.im.utils.Match;

/**
 * 选择国家码页面
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 20, 2012]
 */
public class SelectCountryActivity extends QuickActivity implements
        OnClickListener
{
    
    /**
     * 国家码Adapter
     */
    private CountryListAdapter mAdapter;
    
    /**
     * 国家码列表
     */
    private List<CountryItemModel> mCountry;
    
    /**
     * 登录模块的业务逻辑处理对象
     */
    private ILoginLogic mLoginLogic;
    
    /**
     * 
     * 初始化页面
     * 
     * @param savedInstanceState
     *            savedInstanceState
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_country);
        
        //国家列表adapter
        mAdapter = new CountryListAdapter();
        
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @see android.app.ActivityGroup#onResume()
     */
    @Override
    protected void onResume()
    {
        super.hideInputWindow(mAdapter.getSearchEditText());
        super.onResume();
        // 初始化页面组件
        initView();
        //获取国家列表
        mCountry = mLoginLogic.getCountryCode(this);
        updateView(mCountry);
    }
    
    /**
     * 初始化页面组件、设置组件监听器 <BR>
     * 
     */
    private void initView()
    {
        // 组件初始化
        TextView clientText = (TextView) findViewById(R.id.title);
        
        Button leftButton = (Button) findViewById(R.id.left_button);
        // 设置可见性及显示文字
        clientText.setText(R.string.select_country);
        clientText.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        // 设置监听器
        leftButton.setOnClickListener(this);
    }
    
    /**
     * 点击事件
     * 
     * @param v
     *            View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v)
    {
        //点击返回
        if (v.getId() == R.id.left_button)
        {
            finish();
        }
        
    }
    
    /**
     * 
     * 登录模块的业务逻辑处理对象
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mLoginLogic = (ILoginLogic) getLogicByInterfaceClass(ILoginLogic.class);
    }
    
    /**
     * get QuickAdapter
     * 
     * @return mAdapter
     * @see com.huawei.basic.android.im.ui.basic.QuickActivity#getQuickAdapter()
     */
    
    @Override
    protected QuickAdapter getQuickAdapter()
    {
        return mAdapter;
    }
    
    /**
     * 
     * 对列表进行排序以及插入A-Z等快捷索引的String，生成用于Adapter进行处理的list<BR>
     * [功能详细描述]
     * 
     * @param countryList
     *            BaseContactModel列表
     * @return 用于Adapter进行处理的list
     */
    
    @Override
    protected List<Object> generateDisplayList(List<?> countryList)
    {
        return BaseContactUtil.contactListForDisplay(mCountry);
    }
    
    /**
     * 
     * 国家码按字母顺序排序的adapter
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 20, 2012]
     */
    private class CountryListAdapter extends QuickAdapter
    {
        /**
         * get view<BR>
         * [功能详细描述]
         * 
         * @param position
         *            当前位置
         * @param convertView
         *            View
         * @param parent
         *            ViewGroup
         * @return 返回经处理的view
         * @see android.widget.Adapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return super.getView(position, convertView, parent);
        }
        
        /**
         * 
         * 获取当前item的view<BR>
         * [功能详细描述]
         * 
         * @param position
         *            item位置
         * @param convertView
         *            item view
         * @param parent
         *            item parent
         * @return view 返回需要的view
         * @see android.widget.Adapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        
        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {
            final CountryItemModel mCountryItemModel = (CountryItemModel) getDisplayList().get(position);
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(getBelongedActivity(),
                        R.layout.country_item,
                        null);
                holder = new ViewHolder();
                holder.child = convertView.findViewById(R.id.child);
                holder.mCountryName = (TextView) convertView.findViewById(R.id.countryname);
                holder.mCountryCode = (TextView) convertView.findViewById(R.id.countrycode);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mCountryName.setText(mCountryItemModel.getChName());
            holder.mCountryCode.setText("+"
                    + mCountryItemModel.getCountryCode());
            holder.child.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(FusionAction.LoginAction.ACTION);
                    
                    intent.putExtra(SelectCountryAction.COUNTRY_NAME,
                            mCountryItemModel.getChName());
                    intent.putExtra(SelectCountryAction.COUNTRY_CODE, "+"
                            + mCountryItemModel.getCountryCode());
                    setResult(RESULT_OK, intent);
                    finish();
                    
                }
                
            });
            return convertView;
        }
        
        /**
         * 是否匹配
         * 
         * @param obj
         * @param key
         * @return
         * @see com.huawei.basic.android.im.ui.basic.QuickAdapter#isMatched(java.lang.Object,
         *      java.lang.String)
         */
        
        @Override
        public boolean isMatched(Object obj, String key)
        {
            if (obj instanceof CountryItemModel)
            {
                //可匹配国家中的姓名和简拼
                CountryItemModel mCountryItemModel = (CountryItemModel) obj;
                return Match.match(key,
                        mCountryItemModel.getChName(),
                        mCountryItemModel.getSimplePinyin(),
                        mCountryItemModel.getInitialName());
            }
            return false;
        }
        
    }
    
    /**
     * 
     * 存放view对象<BR>
     * [功能详细描述]
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 20, 2012]
     */
    private static class ViewHolder
    {
        /**
         * 布局
         */
        private View child;
        
        /**
         * 国家名字
         */
        private TextView mCountryName;
        
        /**
         * 国家码
         */
        private TextView mCountryCode;
    }
    
    /**
     * 返回一个boolean表示展示该页面是否需要登录成功
     * 
     * @return boolean 是否是登录后的页面
     */
    @Override
    protected boolean needLogin()
    {
        return false;
    }
    
}
