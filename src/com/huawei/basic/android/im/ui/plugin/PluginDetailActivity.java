/*
 * 文件名: PluginDetailActivity.java
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

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.plugin.core.BaseMethod;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.CommonMethod;
import com.huawei.basic.android.im.component.plugin.core.IPluginManager;
import com.huawei.basic.android.im.component.plugin.core.PluginManager;
import com.huawei.basic.android.im.component.plugin.core.SwitchMethod;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.PluginStringUtil;

/**
 * 插件详情<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 18, 2012] 
 */
public class PluginDetailActivity extends BasicActivity
{
    /**
     * TAG
     */
    private static final String TAG = "PluginDetailActivity";
    
    /**
     * 插件图标
     */
    private ImageView mPluginIcon;
    
    /**
     * 插件名称
     */
    private TextView mPluginNameTx;
    
    /**
     * 插件描述
     */
    private TextView mPluginDescTx;
    
    /**
     * 插件管理器
     */
    private IPluginManager mPluginManager;
    
    /**
     * 插件对象
     */
    private BasePlugin mBasePlugin;
    
    private TextView mTitleTx;
    
    private Context mContext;
    
    private Button mBackButton;
    
    private LinearLayout mLinearLayout;
    
    private ListView mMethodListView;
    
    private MethodAdapter mMethodAdapter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_detail);
        
        mContext = getApplicationContext();
        
        initView();
        
        mPluginManager = PluginManager.getInstance();
        
        Intent intent = getIntent();
        String pluginId = intent.getStringExtra(FusionAction.PluginDetailAction.EXTRA_PLUGIN_ID);
        if (null != pluginId)
        {
            Logger.e(TAG, "pluginId >> " + pluginId);
            mBasePlugin = mPluginManager.getPluginByPluginId(pluginId);
            setValues(mBasePlugin);
        }
    }
    
    private void setValues(BasePlugin basePlugin)
    {
        if (null != basePlugin)
        {
            FaceManager.showFace(mPluginIcon,
                    basePlugin.getIconUrl(),
                    null,
                    R.drawable.icon,
                    50,
                    50);
            mPluginNameTx.setText(PluginStringUtil.getNameId(basePlugin.getPluginId()));
            mPluginDescTx.setText(PluginStringUtil.getDescId(basePlugin.getPluginId()));
            mMethodAdapter.setData(mBasePlugin.getMethods());
            mMethodAdapter.notifyDataSetChanged();
        }
    }
    
    private void initView()
    {
        mPluginIcon = (ImageView) findViewById(R.id.plugin_big_icon);
        mPluginNameTx = (TextView) findViewById(R.id.plugin_name);
        mPluginDescTx = (TextView) findViewById(R.id.plugin_desc);
        mTitleTx = (TextView) findViewById(R.id.title);
        mTitleTx.setText(mContext.getResources().getString(R.string.plugins));
        
        mBackButton = (Button) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PluginDetailActivity.this.finish();
            }
        });
        
        mLinearLayout = (LinearLayout) findViewById(R.id.plugin_entry);
        mLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPluginManager.start(mBasePlugin);
            }
        });
        
        mMethodListView = (ListView) findViewById(R.id.method_list);
        mMethodAdapter = new MethodAdapter();
        mMethodListView.setAdapter(mMethodAdapter);
    }
    
    /**
     * 操作的listview<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 23, 2012]
     */
    class MethodAdapter extends BaseAdapter
    {
        private List<BaseMethod> methodList;
        
        public void setData(List<BaseMethod> list)
        {
            methodList = list;
        }
        
        @Override
        public int getCount()
        {
            return methodList == null ? 0 : methodList.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return methodList.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder = null;
            BaseMethod method = methodList.get(position);
            if (null == convertView)
            {
                convertView = LinearLayout.inflate(mContext,
                        R.layout.plugin_method_item,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.descTx = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.operationButton = (Button) convertView.findViewById(R.id.operation);
                viewHolder.switchButton = (ImageButton) convertView.findViewById(R.id.switch_on);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            setValues(method, viewHolder);
            return convertView;
        }
        
        private void setValues(final BaseMethod method,
                final ViewHolder viewHolder)
        {
            viewHolder.descTx.setText(PluginStringUtil.getMethodNameId(method.getName()));
            if (method instanceof SwitchMethod)
            {
                viewHolder.operationButton.setVisibility(View.GONE);
                final SwitchMethod switchMethod = (SwitchMethod) method;
                if (((SwitchMethod) method).getStatus())
                {
                    viewHolder.switchButton.setImageResource(R.drawable.button_switch_on);
                }
                else
                {
                    viewHolder.switchButton.setImageResource(R.drawable.button_switch_off);
                }
                viewHolder.switchButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean currentStatus = switchMethod.getStatus();
                        switchMethod.invoke(!currentStatus);
                        if (!currentStatus)
                        {
                            viewHolder.switchButton.setImageResource(R.drawable.button_switch_on);
                        }
                        else
                        {
                            viewHolder.switchButton.setImageResource(R.drawable.button_switch_off);
                        }
                    }
                });
            }
            else if (method instanceof CommonMethod)
            {
                viewHolder.switchButton.setVisibility(View.GONE);
                viewHolder.operationButton.setText(method.getName());
                viewHolder.operationButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ((CommonMethod) method).invoke();
                    }
                });
            }
        }
    }
    
    /**
     * ViewHolder<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 24, 2012]
     */
    private static class ViewHolder
    {
        /**
         * 操作描述
         */
        private TextView descTx;
        
        /**
         * 开关按钮
         */
        private ImageButton switchButton;
        
        /**
         * 操作按钮
         */
        private Button operationButton;
    }
}
