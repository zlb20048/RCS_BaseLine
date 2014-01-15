package com.huawei.basic.android.im.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.CustomGridView;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 应用界面<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 11, 2012]
 */
public class AppListActivity extends BasicActivity
{
    /**
     * 添加应用列表标识
     */
    public static final String ADD_APPLY_LIST = "add apply list exist";
    
    /**
     * Debug tag
     */
    private static final String TAG = "AppListActivity";
    
    /**
     * 上下文菜单中删除操作的ID
     */
    private static final int MENU_DELETE_APP = 0;
    
    /**
     * 添加应用的标志
     */
    private static final int ADD_APPLY = 1;
    
    /**
     * 我的应用最大数量
     */
    private static final int MAX_APP_NUM = 12;
    
    /**
     * 热门应用的整个布局
     */
    private View mHotAppTotalView;
    
    /**
     * 热门应用
     */
    private CustomGridView mHotAppGridView;
    
    /**
     * 我的应用
     */
    private CustomGridView mMyAppGridView;
    
    /**
     * 图片加载器
     */
    private PhotoLoader mIconLoader;
    
    /**
     * 热门应用适配器
     */
    private AppAdapter mHotAdapter;
    
    /**
     * 我的应用适配器
     */
    private AppAdapter mMyAdapter;
    
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_apply);
        initView();
        mIconLoader = new PhotoLoader(this, R.drawable.apply_icon, 80, 80,
                PhotoLoader.SOURCE_TYPE_FRIEND, null);
        
        mSettingsLogic.getAppFromeServer();
        showProgressDialog(R.string.connecting);
        
        getSysAppData();
        getMyAppData();
        mHotAppGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                SysAppInfoModel sysAppInfoModel = (SysAppInfoModel) mHotAdapter.getItem(position);
                String url = sysAppInfoModel.getAppUrl();
                if ("1".equals(sysAppInfoModel.getSso()))
                {
                    url += FusionConfig.getInstance().getAasResult().getToken();
                }
                //                Intent intent = new Intent(SettingsAction.ACTION_WEBVIEW_APP);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //                intent.putExtra(SettingsAction.APP_ICON_URL, url);
                //                intent.putExtra(SettingsAction.APP_ICON_NAME,
                //                        sysAppInfoModel.getName());
                if (isBrowserExisits())
                {
                    intent.setClassName("com.android.browser",
                            "com.android.browser.BrowserActivity");
                    startActivity(intent);
                }
                else
                {
                    showToast(R.string.browser_not_exist);
                }
            }
        });
        
        mMyAppGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                Logger.d(TAG, "-------->onItemClick");
                SysAppInfoModel myAppInfoModel = (SysAppInfoModel) mMyAdapter.getItem(position);
                if (StringUtil.isNullOrEmpty(myAppInfoModel.getAppId()))
                {
                    Intent intent = new Intent(
                            SettingsAction.ACTION_ACTIVITY_ADD_APP);
                    startActivityForResult(intent, ADD_APPLY);
                }
                else
                {
                    String url = myAppInfoModel.getAppUrl();
                    if ("1".equals(myAppInfoModel.getSso()))
                    {
                        url += FusionConfig.getInstance()
                                .getAasResult()
                                .getToken();
                    }
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    //                    Intent intent = new Intent(
                    //                            SettingsAction.ACTION_WEBVIEW_APP);
                    //                    intent.putExtra(SettingsAction.APP_ICON_URL, url);
                    //                    intent.putExtra(SettingsAction.APP_ICON_NAME,
                    //                            myAppInfoModel.getName());
                    if (isBrowserExisits())
                    {
                        intent.setClassName("com.android.browser",
                                "com.android.browser.BrowserActivity");
                        startActivity(intent);
                    }
                    else
                    {
                        showToast(R.string.browser_not_exist);
                    }
                    
                }
            }
        });
        
    }
    
    private void getSysAppData()
    {
        ArrayList<SysAppInfoModel> hotAppInfoModelList = mSettingsLogic.getSysAppInfoByType(SysAppInfoModel.TYPE_HOT);
        if (null == hotAppInfoModelList || hotAppInfoModelList.size() == 0)
        {
            mHotAppTotalView.setVisibility(View.GONE);
        }
        else
        {
            mHotAppTotalView.setVisibility(View.VISIBLE);
            mHotAdapter.setData(hotAppInfoModelList);
            mHotAdapter.notifyDataSetChanged();
        }
    }
    
    private void getMyAppData()
    {
        ArrayList<SysAppInfoModel> myAppInfoModelList = mSettingsLogic.getMyAppInfoFromDB();
        int size = 0;
        if (myAppInfoModelList != null)
        {
            size = myAppInfoModelList.size();
        }
        else
        {
            myAppInfoModelList = new ArrayList<SysAppInfoModel>();
        }
        for (int i = MAX_APP_NUM - size; i > 0; i--)
        {
            SysAppInfoModel defaultModel = new SysAppInfoModel();
            //            defaultModel.setName(getString(R.string.default_add));
            myAppInfoModelList.add(defaultModel);
        }
        mMyAdapter.setData(myAppInfoModelList);
        mMyAdapter.notifyDataSetChanged();
        
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
        mIconLoader.pause();
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
        mIconLoader.resume();
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
        mIconLoader.stop();
        super.onDestroy();
    }
    
    /**
     * 
     * 初始化页面<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        Button leftButton = (Button) findViewById(R.id.left_button);
        leftButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.applications_list);
        
        mHotAppTotalView = findViewById(R.id.app_hotapp_layout);
        mHotAppGridView = (CustomGridView) findViewById(R.id.hot_apply);
        mHotAppGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        
        mMyAppGridView = (CustomGridView) findViewById(R.id.my_apply);
        mMyAppGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        registerForContextMenu(mMyAppGridView);
        
        mHotAdapter = new AppAdapter(true);
        mMyAdapter = new AppAdapter(false);
        
        mHotAppGridView.setAdapter(mHotAdapter);
        mMyAppGridView.setAdapter(mMyAdapter);
        
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
     * 自定义适配器（我的应用）<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 5, 2012]
     */
    private class AppAdapter extends BaseAdapter
    {
        private boolean isHotApp;
        
        /**
         * app列表
         */
        private ArrayList<SysAppInfoModel> mAppList;
        
        public AppAdapter(boolean isHotApp)
        {
            this.isHotApp = isHotApp;
        }
        
        /**
         * 
         * 载入数据[一句话功能简述]<BR>
         * [功能详细描述]
         * 
         * @param models
         */
        public void setData(ArrayList<SysAppInfoModel> models)
        {
            mAppList = models;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder = null;
            final SysAppInfoModel myAppInfoModel = mAppList.get(position);
            
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(AppListActivity.this,
                        R.layout.gridview_cell,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.appLogo = (ImageView) convertView.findViewById(R.id.imageview);
                viewHolder.appName = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.appName.setText(myAppInfoModel.getName());
            mIconLoader.loadPhoto(viewHolder.appLogo,
                    myAppInfoModel.getIconUrl());
            if (!isHotApp)
            {
                ArrayList<SysAppInfoModel> myAppInfoModelList = mSettingsLogic.getMyAppInfoFromDB();
                int size = 0;
                if (myAppInfoModelList != null)
                {
                    size = myAppInfoModelList.size();
                }
                else
                {
                    myAppInfoModelList = new ArrayList<SysAppInfoModel>();
                }
                if (position < size)
                {
                    viewHolder.appLogo.setImageResource(R.drawable.apply_icon);
                    mIconLoader.loadPhoto(viewHolder.appLogo,
                            myAppInfoModel.getIconUrl());
                }
                else
                {
                    viewHolder.appName.setText((R.string.default_add));
                    viewHolder.appLogo.setImageResource(R.drawable.app_to_add);
                }
            }
            return convertView;
        }
        
        @Override
        public int getCount()
        {
            return mAppList != null ? mAppList.size() : 0;
        }
        
        @Override
        public Object getItem(int position)
        {
            return mAppList.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private static class ViewHolder
    {
        private ImageView appLogo;
        
        private TextView appName;
    }
    
    /**
     * 
     * 处理返回结果<BR>
     * [功能详细描述]
     * @param msg 返回消息
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        closeProgressDialog();
        int type = msg.what;
        switch (type)
        {
            case SettingsMessageType.GET_APP_SUCCESS:
                getSysAppData();
                break;
            
            case SettingsMessageType.DELETE_APP_SUCCESS:
                Logger.d(TAG, "onContextItemSelected-------------->点击删除");
                getMyAppData();
                showToast(R.string.delete_success);
                break;
            
            case SettingsMessageType.DELETE_APP_FAILED:
                showToast(R.string.delete_fail);
                break;
            
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * 点击添加应用<BR>
     * [功能详细描述]
     * @param item 应用条目
     * @return boolean类型
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Logger.d(TAG, "---------->onContextItemSelected");
        AdapterView.AdapterContextMenuInfo info;
        try
        {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        }
        catch (ClassCastException e)
        {
            return false;
        }
        
        if (item.getItemId() == MENU_DELETE_APP)
        {
            final SysAppInfoModel sysAppInfoModel = (SysAppInfoModel) mMyAdapter.getItem(info.position);
            
            if (StringUtil.isNullOrEmpty(sysAppInfoModel.getAppId()))
            {
                Toast.makeText(AppListActivity.this,
                        getResources().getString(R.string.add_app_tip),
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                showConfirmDialog(R.string.submit_delete_title,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                String appId = sysAppInfoModel.getAppId();
                                mSettingsLogic.deleteByAppId(appId);
                            }
                        });
            }
            
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param menu 弹出菜单
     * @param v 视图
     * @param menuInfo 菜单信息
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        menu.setHeaderTitle(R.string.operation);
        menu.add(0, MENU_DELETE_APP, 0, R.string.delete_app);
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param requestCode 请求码
     * @param resultCode  返回结果码
     * @param data 传递数据
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == ADD_APPLY)
            {
                getMyAppData();
            }
        }
    }
    
    /**
     * 
     * 判断Browser.apk是否安装
     * 
     * @return boolean
     */
    public boolean isBrowserExisits()
    {
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pkg : pkgs)
        {
            if ("com.android.browser".equals(pkg.packageName))
            {
                return true;
            }
        }
        return false;
    }
}
