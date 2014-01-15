/*
 * 文件名: PluginListActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 18, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.PluginManager;
import com.huawei.basic.android.im.component.plugin.plugins.apk.ApkPlugin;
import com.huawei.basic.android.im.component.plugin.plugins.internal.InternalPlugin;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.plugin.IPluginLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.PluginStringUtil;

/**
 * 插件列表界面<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 18, 2012] 
 */
public class PluginListActivity extends BasicActivity
{
    private PluginManager mPluginManager;
    
    private Context mContext;
    
    private TextView mInternalTitle;
    
    private TextView mExternalTitle;
    
    private ListView mInternalPluginListView;
    
    private ListView mExternalPluginListView;
    
    private PluginListAdapter mInternalListAdapter;
    
    private PluginListAdapter mExternalListAdapter;
    
    private TextView mTitleTx;
    
    private Button mBackButton;
    
    private ImageButton mShowOnContactsList;
    
    private boolean mStatus = true;
    
    private IPluginLogic mPluginLogic;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_list);
        
        mContext = getApplicationContext();
        mPluginManager = PluginManager.getInstance();
        
        //初始化view
        initView();
        
        //初始化数据
        initValues();
    }
    
    /**
     * 初始化数据<BR>
     */
    private void initValues()
    {
        ArrayList<InternalPlugin> internalList = mPluginManager.getInternalPluginList();
        ArrayList<ApkPlugin> apkPluginList = mPluginManager.getApkPluginList();
        if (null == internalList || internalList.size() < 1)
        {
            mInternalTitle.setVisibility(View.GONE);
            mInternalPluginListView.setVisibility(View.GONE);
        }
        if (null == apkPluginList || apkPluginList.size() < 1)
        {
            mExternalTitle.setVisibility(View.GONE);
            mExternalPluginListView.setVisibility(View.GONE);
        }
        if (null != internalList)
        {
            for (InternalPlugin plugin : internalList)
            {
                if (!plugin.isShowInContactList())
                {
                    mStatus = false;
                    break;
                }
            }
        }
        if (mStatus)
        {
            if (null != apkPluginList)
            {
                for (ApkPlugin plugin : apkPluginList)
                {
                    if (!plugin.isShowInContactList())
                    {
                        mStatus = false;
                        break;
                    }
                }
            }
        }
        mInternalListAdapter.setData(internalList);
        mInternalListAdapter.notifyDataSetChanged();
        mExternalListAdapter.setData(apkPluginList);
        mExternalListAdapter.notifyDataSetChanged();
    }
    
    private void initView()
    {
        mInternalPluginListView = (ListView) findViewById(R.id.internal_plugin_list);
        mExternalPluginListView = (ListView) findViewById(R.id.external_plugin_list);
        mInternalListAdapter = new PluginListAdapter();
        mExternalListAdapter = new PluginListAdapter();
        mInternalPluginListView.setAdapter(mInternalListAdapter);
        mExternalPluginListView.setAdapter(mExternalListAdapter);
        mInternalTitle = (TextView) findViewById(R.id.internal_plugin_titile);
        mExternalTitle = (TextView) findViewById(R.id.external_plugin_titile);
        mInternalPluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id)
            {
                BasePlugin plugin = (BasePlugin) mInternalListAdapter.getItem(position);
                if (null != plugin)
                {
                    Intent intent = new Intent(
                            FusionAction.PluginDetailAction.ACTION);
                    intent.putExtra(FusionAction.PluginDetailAction.EXTRA_PLUGIN_ID,
                            plugin.getPluginId());
                    startActivity(intent);
                }
            }
        });
        
        mTitleTx = (TextView) findViewById(R.id.title);
        mTitleTx.setText(mContext.getResources().getString(R.string.plugins));
        
        mBackButton = (Button) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PluginListActivity.this.finish();
            }
        });
        
        mShowOnContactsList = (ImageButton) findViewById(R.id.show_on_contact_list);
        mStatus = mPluginLogic.getShowPluginsOnContacts();
        if (mStatus)
        {
            mShowOnContactsList.setImageResource(R.drawable.button_switch_on);
        }
        else
        {
            mShowOnContactsList.setImageResource(R.drawable.button_switch_off);
        }
        mShowOnContactsList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mStatus = !mStatus;
                if (mStatus)
                {
                    mShowOnContactsList.setImageResource(R.drawable.button_switch_on);
                    mPluginLogic.setShowPluginsOnContacts(true);
                }
                else
                {
                    mShowOnContactsList.setImageResource(R.drawable.button_switch_off);
                    mPluginLogic.setShowPluginsOnContacts(false);
                }
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mPluginLogic = (IPluginLogic) getLogicByInterfaceClass(IPluginLogic.class);
    }
    
    /**
     * 插件列表的Adapter<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, May 22, 2012]
     */
    class PluginListAdapter extends BaseAdapter
    {
        private ArrayList<BasePlugin> mPluginList = new ArrayList<BasePlugin>();
        
        /**
         * adapter设置数据<BR>
         * @param pluginList 插件列表
         */
        public void setData(ArrayList<? extends BasePlugin> pluginList)
        {
            mPluginList.clear();
            if (null != pluginList)
            {
                mPluginList.addAll(pluginList);
            }
        }
        
        @Override
        public int getCount()
        {
            return mPluginList.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return mPluginList.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            BasePlugin plugin = mPluginList.get(position);
            if (null == convertView)
            {
                convertView = LinearLayout.inflate(mContext,
                        R.layout.plugin_item,
                        null);
                PluginViewHolder viewHolder = new PluginViewHolder();
                viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.plugin_icon);
                viewHolder.pluginNameTx = (TextView) convertView.findViewById(R.id.plugin_name);
                setHolderValue(plugin, viewHolder);
                convertView.setTag(viewHolder);
            }
            else
            {
                setHolderValue(plugin, (PluginViewHolder) convertView.getTag());
            }
            return convertView;
        }
        
        private void setHolderValue(BasePlugin plugin, PluginViewHolder holder)
        {
            if (plugin != null && holder != null)
            {
                holder.pluginNameTx.setText(PluginStringUtil.getNameId(plugin.getPluginId()));
                FaceManager.showFace(holder.iconIv,
                        plugin.getIconUrl(),
                        null,
                        R.drawable.icon,
                        80,
                        80);
            }
        }
    }
    
    /**
     * ViewHolder<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 18, 2012]
     */
    private static class PluginViewHolder
    {
        /**
         * 插件的头像
         */
        private ImageView iconIv;;
        
        /**
         * 插件的名称
         */
        private TextView pluginNameTx;
    }
}
