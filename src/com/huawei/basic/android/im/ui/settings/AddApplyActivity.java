package com.huawei.basic.android.im.ui.settings;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;

/**
 * 
 * 添加应用类<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 16, 2012]
 */
public class AddApplyActivity extends BasicActivity
{
    /**
     * TAG
     */
    private static final String TAG = "AddApplyActivity";
    
    /**
     * 添加按钮
     */
    private Button mAddButton;
    
    /**
     * 待添加应用列表
     */
    private ListView mListView;
    
    /**
     * 待添加列表适配器
     */
    private AppAddAdapter mAppAddAdapter;
    
    /**
     * 用于记录待添加的appID
     */
    private ArrayList<Integer> mIds;
    
    /**
     * 图片加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 业务逻辑处理接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 初始化界面 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param savedInstanceState 保存的状态
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "----------->onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_apply_add);
        initView();
        setData();
        mPhotoLoader = new PhotoLoader(this, R.drawable.apply_icon, 80, 80,
                PhotoLoader.SOURCE_TYPE_FRIEND, null);
    }
    
    /**
     * 初始化
     */
    private void initView()
    {
        mIds = new ArrayList<Integer>();
        
        Button leftButton = (Button) findViewById(R.id.left_button);
        leftButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.add_app_title);
        
        mAddButton = (Button) findViewById(R.id.add_apply_add_button);
        mAddButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSettingsLogic.addAppToDB(mIds);
            }
        });
        mListView = (ListView) findViewById(R.id.add_apply_lv);
        //好友列表滑动式不暂停加载头像
        mListView.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (scrollState == OnScrollListener.SCROLL_STATE_FLING)
                {
                    mPhotoLoader.pause();
                }
                else
                {
                    mPhotoLoader.resume();
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
            }
        });
    }
    
    /**
     * 
     * 初始化Logic<BR>
     * [功能详细描述]
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        mPhotoLoader.pause();
        super.onPause();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        mPhotoLoader.resume();
        super.onResume();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        mPhotoLoader.stop();
        super.onDestroy();
    }
    
    /**
     * 加载数据
     */
    private void setData()
    {
        mAppAddAdapter = new AppAddAdapter();
        //应用列表界面传入的已添加的我的应用（需从列表中删除）
        //待添加应用列表
        ArrayList<SysAppInfoModel> addAppList = mSettingsLogic.getAddedData();
        mAppAddAdapter.setData(addAppList);
        mListView.setAdapter(mAppAddAdapter);
    }
    
    /**
     * 
     * 根据返回消息处理UI显示<BR>
     * [功能详细描述]
     * @param msg 返回消息
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int type = msg.what;
        switch (type)
        {
            case SettingsMessageType.EXCESS_MAXNUM:
                showToast(R.string.app_reach_max);
                break;
            
            case SettingsMessageType.NO_APP:
                finish();
                break;
            
            case SettingsMessageType.CAN_INSERT:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * list view Adapter自定义适配器
     * 
     */
    private class AppAddAdapter extends BaseListAdapter
    {
        /**
         * 
         * contact info对应的好友是否被选中<BR>
         * [功能详细描述]
         * 
         * @param contactInfo ContactInfoModel
         * @return 选中返回true，反之返回false
         */
        public boolean isChoosed(SysAppInfoModel contactInfo)
        {
            return mIds.contains(Integer.parseInt(contactInfo.getAppId()));
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder viewHolder;
            final SysAppInfoModel sysAppInfoBean = (SysAppInfoModel) getDataSrc().get(position);
            if (convertView == null)
            {
                Logger.d(TAG, "------->convertView == null");
                // Item 的Layout
                convertView = LinearLayout.inflate(AddApplyActivity.this,
                        R.layout.add_app_item,
                        null);
                
                viewHolder = new ViewHolder();
                viewHolder.itemView = convertView.findViewById(R.id.item);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.app_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.app_name);
                viewHolder.checkbox = (ImageView) convertView.findViewById(R.id.app_cb);
                
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            mPhotoLoader.loadPhoto(viewHolder.image,
                    sysAppInfoBean.getIconUrl());
            viewHolder.name.setText(sysAppInfoBean.getName());
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.checkbox.setImageResource(isChoosed(sysAppInfoBean) ? R.drawable.checkbox_selected
                    : R.drawable.checkbox_normal);
            viewHolder.itemView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Integer id = (Integer) Integer.parseInt(sysAppInfoBean.getAppId());
                    if (!isChoosed(sysAppInfoBean))
                    {
                        viewHolder.checkbox.setImageResource(!isChoosed(sysAppInfoBean) ? R.drawable.checkbox_selected
                                : R.drawable.checkbox_normal);
                        mIds.add(id);
                    }
                    else
                    {
                        viewHolder.checkbox.setImageResource(!isChoosed(sysAppInfoBean) ? R.drawable.checkbox_selected
                                : R.drawable.checkbox_normal);
                        mIds.remove(id);
                    }
                    mAddButton.setText(getResources().getString(R.string.default_add)
                            + "(" + mIds.size() + ")");
                }
            });
            return convertView;
        }
    }
    
    /**
     * ViewHolder
     * 
     * @author s00197381
     * 
     */
    private static class ViewHolder
    {
        /**
         * 整行(item)
         */
        private View itemView;
        
        /**
         * 应用图标
         */
        private ImageView image;
        
        /**
         * 应用图标名称
         */
        private TextView name;
        
        /**
         * 复选框
         */
        private ImageView checkbox;
    }
}
