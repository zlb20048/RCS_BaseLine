/*
 * 文件名: GroupSearchActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群组搜索页面
 * 创建人: tjzhang
 * 创建时间:2012-3-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.GroupDetailAction;
import com.huawei.basic.android.im.common.FusionAction.GroupSearchAction;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 群组搜索页面：包括分类搜索和关键字搜索<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-14] 
 */
public class GroupSearchActivity extends BasicActivity implements
        OnClickListener, OnScrollListener
{
    private static final String TAG = "GroupSearchActivity";
    
    private static final int PAGE_SIZE = 50;
    
    /**
     * 分页查询的初始id
     */
    private int pageId = 1;
    
    /**
     * 页面的Mode
     */
    private int mode;
    
    /**
     * 分类类型
     */
    private String mSortCategroy;
    
    /**
     * 搜索关键字
     */
    private String mSearchValue;
    
    private ListView mListView;
    
    /**
     * 标题栏的title
     */
    private TextView mTitleText;
    
    /**
     * 暂无搜索结果布局
     */
    private View noResultRow;
    
    /**
     * 搜索view 整行
     */
    private View searchGroup;
    
    /**
     * 输入框
     */
    private EditText mEditText;
    
    /**
     * 清除搜索框中的文字
     */
    private ImageView cancelButton;
    
    /**
     * 返回按键
     */
    private Button backButton;
    
    /**
     * 搜索按键
     */
    private Button searchButton;
    
    /**
     * 记录下listView上次滑动的item的position
     */
    private int mLastItem;
    
    /**
     * 标记是否还有更多搜索结果
     */
    private boolean hasMoreResult;
    
    /**
     * 标记是否是根据新关键词搜索
     */
    private boolean mResearch;
    
    private Context mContext;
    
    private BaseListAdapter mListAdaper;
    
    private IGroupLogic mGroupLogic;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_search);
        mContext = this;
        mode = getIntent().getIntExtra(GroupSearchAction.EXTRA_MODE,
                GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY);
        Logger.d(TAG, "MODE ==" + mode);
        initView();
        if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY)
        {
            // 选择分类的内容是固定的，用了一个简单的adapter
            mListAdaper = new CatagroyListAdapter();
            mListAdaper.setData(mGroupLogic.getCategroyTitles());
        }
        else
        {
            mListAdaper = new ResultListAdapter();
            mPhotoLoader = new PhotoLoader(this,
                    R.drawable.default_group_head_icon, 52, 52,
                    PhotoLoader.SOURCE_TYPE_GROUP, null);
            if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY_RESULT)
            {
                mTitleText.setText(getIntent().getStringExtra(GroupSearchAction.EXTRA_TITLE));
                mSortCategroy = getIntent().getStringExtra(GroupSearchAction.EXTRA_CATEGROY_MODE);
                // 分类搜索结果页面是没有搜索框的
                searchGroup.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
                showProgressDialog(R.string.connecting);
                mGroupLogic.searchGroupByCategory(pageId,
                        PAGE_SIZE,
                        mSortCategroy);
                // 根据分类的类别从xmpp服务器获取数据
                Logger.d(TAG, "=====根据分类的类别从xmpp服务器获取数据==========");
            }
            else if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_KEY)
            {
                // 根据关键字搜索群组
                mSearchValue = getIntent().getStringExtra(GroupSearchAction.EXTRA_SEARCH_KEY);
                mEditText.setText(mSearchValue);
                showProgressDialog(R.string.connecting);
                mGroupLogic.searchGroupByKey(pageId, PAGE_SIZE, mSearchValue);
                Logger.d(TAG, "=====根据关键字搜索群组=========");
            }
            mListView.setOnScrollListener(this);
        }
        mListView.setAdapter(mListAdaper);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                //跳转到群详情页面
                GroupInfoModel model = (GroupInfoModel) mListAdaper.getDataSrc()
                        .get(iItemIndex);
                Intent intent = new Intent(
                        GroupDetailAction.ACTION_GROUP_DETAIL);
                intent.putExtra(GroupDetailAction.EXTRA_MODEL, model);
                startActivity(intent);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (null != mPhotoLoader)
        {
            mPhotoLoader.stop();
        }
        closeProgressDialog();
    }
    
    private void initView()
    {
        backButton = (Button) findViewById(R.id.left_button);
        backButton.setOnClickListener(this);
        mTitleText = (TextView) findViewById(R.id.title);
        mTitleText.setVisibility(View.VISIBLE);
        mTitleText.setText(R.string.search_group_title);
        noResultRow = findViewById(R.id.search_no_result_row);
        searchGroup = findViewById(R.id.quick_search);
        mListView = (ListView) findViewById(R.id.listView);
        searchButton = (Button) findViewById(R.id.right_button);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setText(R.string.search);
        searchButton.setOnClickListener(this);
        mEditText = (EditText) searchGroup.findViewById(R.id.search_friend);
        mEditText.setHint(R.string.search_hint);
        mEditText.addTextChangedListener(new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                cancelButton.setVisibility(s.toString().length() > 0 ? View.VISIBLE
                        : View.GONE);
            }
        });
        cancelButton = (ImageView) searchGroup.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //点清除按钮，清除编辑框中的文字
                mEditText.setText("");
                // 如果是在搜索结果页面，则跳转到搜索的入口页面
                if (mode != GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY)
                {
                    finish();
                }
            }
        });
    }
    
    /**
     * 
     * 搜索结果展示的adapter<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-14]
     */
    private class ResultListAdapter extends BaseListAdapter
    {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyDataSetChanged()
        {
            super.notifyDataSetChanged();
            if (getCount() > 0)
            {
                noResultRow.setVisibility(View.GONE);
                setTitle();
            }
            else
            {
                noResultRow.setVisibility(View.VISIBLE);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(mContext,
                        R.layout.group_list_row,
                        null);
                holder = new ViewHolder();
                holder.group = convertView.findViewById(R.id.group_item);
                holder.headImage = (ImageView) convertView.findViewById(R.id.group_head);
                holder.name = (TextView) convertView.findViewById(R.id.group_name);
                //                holder.unreadNumber = (TextView) convertView.findViewById(R.id.group_unread_number);
                holder.description = (TextView) convertView.findViewById(R.id.group_description);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            final GroupInfoModel model = (GroupInfoModel) getDataSrc().get(position);
            holder.name.setText(model.getGroupName());
            holder.description.setText(model.getGroupDesc());
            mPhotoLoader.loadPhoto(holder.headImage, model.getFaceUrl());
            //            int unRead = model.getUnReadMsg();
            //            if (unRead > 0)
            //            {
            //                holder.unreadNumber.setVisibility(View.VISIBLE);
            //                holder.unreadNumber.setText(String.valueOf(unRead));
            //            }
            //            else
            //            {
            //                holder.unreadNumber.setVisibility(View.GONE);
            //            }
            return convertView;
        }
        
    }
    
    /**
     * 
     * 分类条目list 的adapter<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-14]
     */
    private class CatagroyListAdapter extends BaseListAdapter
    {
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(mContext,
                        R.layout.group_catagroy_list_row,
                        null);
                holder = new ViewHolder();
                holder.group = convertView.findViewById(R.id.group_catagroy);
                holder.name = (TextView) convertView.findViewById(R.id.group_catagroy_name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.group.setBackgroundResource(R.drawable.setting_item_bg_mid);
            final String groupTitle = (String) getDataSrc().get(position);
            final String categroyMode = String.valueOf(position + 1);
            holder.name.setText(groupTitle);
            holder.group.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    // 跳转到分类查找结果页面
                    Intent intent = new Intent(
                            GroupSearchAction.ACTION_GROUP_SEARCH);
                    intent.putExtra(GroupSearchAction.EXTRA_MODE,
                            GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY_RESULT);
                    intent.putExtra(GroupSearchAction.EXTRA_TITLE, groupTitle);
                    intent.putExtra(GroupSearchAction.EXTRA_CATEGROY_MODE,
                            categroyMode);
                    mContext.startActivity(intent);
                    
                }
            });
            return convertView;
        }
        
    }
    
    /**
     * 
     * 存放View holder<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-14]
     */
    private static class ViewHolder
    {
        private View group;
        
        private ImageView headImage;
        
        private TextView name;
        
        //        private TextView unreadNumber;
        
        private TextView description;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.right_button:
                mSearchValue = mEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(mSearchValue))
                {
                    // 进行搜索之前，先关闭输入法，并清掉前面list中的内容
                    hideInputWindow(mEditText);
                    // 如果是在搜索的入口页面，则跳转到搜索结果页面<Br>
                    // 否则直接进行搜索
                    if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY)
                    {
                        Intent intent = new Intent(
                                GroupSearchAction.ACTION_GROUP_SEARCH);
                        intent.putExtra(GroupSearchAction.EXTRA_MODE,
                                GroupSearchAction.MODE_SEARCH_GROUP_BY_KEY);
                        intent.putExtra(GroupSearchAction.EXTRA_SEARCH_KEY,
                                mSearchValue);
                        mContext.startActivity(intent);
                        mEditText.setText("");
                    }
                    else
                    {
                        showProgressDialog(R.string.searching);
                        mResearch = true;
                        pageId = 1;
                        mGroupLogic.searchGroupByKey(pageId,
                                PAGE_SIZE,
                                mSearchValue);
                    }
                }
                break;
            default:
                break;
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        //查看群分类页面是不需要处理消息的
        if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY)
        {
            return;
        }
        int what = msg.what;
        Object obj = msg.obj;
        switch (what)
        {
            case GroupMessageType.SEARCH_GROUP_SUCCESS:
                closeProgressDialog();
                // 先需要判断是否是重新搜索的，如果是，则先清除先前的list
                if (mResearch || pageId == 1)
                {
                    mListAdaper.setData(null);
                    mResearch = false;
                }
                List<GroupInfoModel> list = (List<GroupInfoModel>) obj;
                if (null == list)
                {
                    hasMoreResult = false;
                }
                else
                {
                    if (list.size() == PAGE_SIZE)
                    {
                        hasMoreResult = true;
                        pageId++;
                    }
                }
                // 由于是再次请求，需要把数据进行合并
                List<GroupInfoModel> beforeList = (List<GroupInfoModel>) mListAdaper.getDataSrc();
                if (null != beforeList)
                {
                    if (null != list)
                    {
                        beforeList.addAll(list);
                    }
                }
                else
                {
                    beforeList = list;
                }
                mListAdaper.setData(beforeList);
                mListAdaper.notifyDataSetChanged();
                break;
            case GroupMessageType.SEARCH_GROUP_FAILED:
                closeProgressDialog();
                mListAdaper.notifyDataSetChanged();
                if (null != obj)
                {
                    showToast((String) obj);
                }
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        Logger.d(TAG, "==========onScroll=========");
        mLastItem = firstVisibleItem + visibleItemCount;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (mLastItem == mListAdaper.getCount()
                && scrollState == OnScrollListener.SCROLL_STATE_IDLE)
        {
            if (hasMoreResult)
            {
                // 获取更多数据
                showProgressDialog(R.string.connecting);
                if (mode == GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY_RESULT)
                {
                    mGroupLogic.searchGroupByCategory(pageId,
                            PAGE_SIZE,
                            mSortCategroy);
                }
                else
                {
                    mGroupLogic.searchGroupByKey(pageId,
                            PAGE_SIZE,
                            mSearchValue);
                }
            }
        }
        if (null != mPhotoLoader)
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
        
    }
    
    /**
     * 
     * 设置title，显示搜索结果个数<BR>
     * [功能详细描述]
     */
    private void setTitle()
    {
        int count = mListAdaper.getCount();
        if (mode != GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY_RESULT)
        {
            mTitleText.setText(getResources().getString(R.string.search_group_title)
                    + "(" + count + ")");
        }
    }
    
}
