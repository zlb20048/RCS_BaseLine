/*
 * 文件名: FindFriendResultListActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.text.method.DialerKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 查询好友结果界面<BR>
 * 通过传入MODE参数分查询形态：根据ID查找、根据详细资料查找、根据认识的人查找
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 16, 2012] 
 */
public class FindFriendResultListActivity extends BasicActivity implements
        OnClickListener
{
    private static final String TAG = "FindFriendResultListActivity";
    
    /**
     * request to input reason
     */
    private static final int REQ_FOR_INPUT_REASON = 1;
    
    /**
     * 总记录条数
     */
    private static final int PAGE_RECORD_COUNT = 20;
    
    /**
     * ListView的Adapter对象
     */
    private ResultListAdapter mListAdaper;
    
    /**
     * ListView控件
     */
    private ListView mListView;
    
    /**
     * 标题的View
     */
    private TextView mTitle;
    
    /**
     * 编辑的文本框控件
     */
    private LimitedEditText mEditText;
    
    /**
     * 暂无搜索结果布局
     */
    private View noResultRow;
    
    /**
     * 搜索不出结果的时候应该展示的内容
     */
    private TextView mNoResultContent;
    
    /**
     * 查询关键字
     */
    private String mSearchValue;
    
    /**
     * 搜索按钮
     */
    private Button mSearchButton;
    
    /**
     * 查询组控件
     */
    private View mSearchGroup;
    
    /**
     * 显示记录最后的下标
     */
    private int mLastItemIndex;
    
    /**
     * 开始查询下标
     */
    private int mStartIndex = 1;
    
    /**
     * 当前查询模式
     */
    private int mMode;
    
    /**
     * 所有记录总条数
     */
    private int mTotalCount = 0;
    
    /**
     * 是否正在加载
     */
    private boolean mIsLoading = false;
    
    /**
     * 好友的logic对象
     */
    private IFriendLogic mFriendLogic;
    
    /**
     * 找朋友logic
     */
    private IFriendHelperLogic mFriendHelperLogic;
    
    /**
     * 图片加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 设置弹出框
     */
    private PopupWindow popupWindow;
    
    /**
     * 设置弹出框内部View
     */
    private View popView;
    
    private Context mContext;
    
    private boolean isSearching;
    
    private ContactInfoModel mCurrentContactInfoModel;
    
    private Button mLeftBtn;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState 
     *      传入的bundle对象
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend_result);
        mContext = this;
        mMode = getIntent().getIntExtra(FusionAction.FindFriendResultListAction.EXTRA_MODE,
                FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN);
        initView();
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        mListAdaper = new ResultListAdapter();
        mListView.setAdapter(mListAdaper);
        if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID
                || mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_DETAIL)
        {
            mListView.setOnScrollListener(new OnScrollListener()
            {
                /**
                 * 
                 * 在滚动切换的时候响应<BR>
                 * @param view
                 *      响应的ListView对象
                 * @param scrollState
                 *      滚动的状态
                 * @see android.widget.AbsListView.OnScrollListener
                 * #onScrollStateChanged(android.widget.AbsListView, int)
                 */
                public void onScrollStateChanged(AbsListView view,
                        int scrollState)
                {
                    if (scrollState == OnScrollListener.SCROLL_STATE_FLING)
                    {
                        mPhotoLoader.pause();
                    }
                    else
                    {
                        mPhotoLoader.resume();
                    }
                    if (mLastItemIndex == mListAdaper.getCount()
                            && scrollState == OnScrollListener.SCROLL_STATE_IDLE)
                    {
                        if (mStartIndex <= mTotalCount && !mIsLoading)
                        {
                            FindFriendResultListActivity.this.loadMoreFriends();
                        }
                    }
                }
                
                /**
                 * <BR>
                 * [功能详细描述]
                 * @param view
                 * @param firstVisibleItem
                 *      当前界面的顶部Item下标
                 * @param visibleItemCount
                 *      当前界面的item总数
                 * @param totalItemCount
                 *      所有item总数
                 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
                 */
                public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount)
                {
                    mLastItemIndex = firstVisibleItem + visibleItemCount;
                }
            });
            
            if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID)
            {
                findViewById(R.id.right_layout_total).setVisibility(View.GONE);
                mSearchButton.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.find_by_id_title);
                // 需要把editText设置为只能输数字
                mEditText.setKeyListener(DialerKeyListener.getInstance());
                mEditText.setHint(R.string.find_by_id_hint);
            }
            else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_DETAIL)
            {
                findViewById(R.id.right_layout_total).setVisibility(View.GONE);
                mSearchButton.setVisibility(View.VISIBLE);
                mTitle.setText(R.string.find_by_details_title);
                mEditText.setHint(R.string.find_by_details_hint);
            }
            //请求焦点
            mEditText.requestFocus();
            //弹出键盘
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN)
        {
            findViewById(R.id.right_layout_total).setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.GONE);
            mTitle.setText(R.string.find_maybe_konwn_title);
            mSearchGroup.setVisibility(View.GONE);
            
            mFriendLogic.loadMoreFriendByKnownPerson();
            super.showProgressDialog(R.string.searching);
        }
        else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_NEAR)
        {
            mTitle.setText(R.string.find_by_location_title);
            mSearchGroup.setVisibility(View.GONE);
            ImageView removeButton = (ImageView) findViewById(R.id.right_button);
            findViewById(R.id.right_layout).setOnClickListener(this);
            removeButton.setBackgroundResource(R.drawable.remove_location);
            removeButton.setVisibility(View.VISIBLE);
            mFriendLogic.loadMoreFriendByLocation();
        }
        
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            /**
             * 点击ListViewItem的响应事件回调<BR>
             * @param vParent
             *      ListView对象
             * @param vItem
             *      其中的item
             * @param iItemIndex
             *      item的下标
             * @param lItemId
             *      item的ID
             * @see android.widget.AdapterView.OnItemClickListener
             * #onItemClick(android.widget.AdapterView, android.view.View, int, long)
             */
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                ContactInfoModel contactBean = (ContactInfoModel) mListAdaper.getDataSrc()
                        .get(iItemIndex);
                
                Intent intent = new Intent();
                intent.setAction(ContactDetailAction.ACTION);
                intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                        ContactDetailAction.HITALK_CONTACT);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        contactBean.getFriendUserId());
                startActivity(intent);
                
            }
        });
    }
    
    private void resetView()
    {
        if (null != mSearchButton)
        {
            mSearchButton.setText(R.string.search);
        }
        if (null != mLeftBtn)
        {
            mLeftBtn.setText(R.string.back);
        }
        if (null == mTitle)
        {
            return;
        }
        if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID)
        {
            mTitle.setText(R.string.find_by_id_title);
            if (null != mEditText)
            {
                mEditText.setHint(R.string.find_by_id_hint);
            }
        }
        else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN)
        {
            mTitle.setText(R.string.find_maybe_konwn_title);
        }
        else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_DETAIL)
        {
            mTitle.setText(R.string.find_by_details_title);
            if (null != mEditText)
            {
                mEditText.setHint(R.string.find_by_details_hint);
            }
        }
        else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_NEAR)
        {
            mTitle.setText(R.string.find_by_location_title);
        }
        if (null != mListAdaper)
        {
            mListAdaper.notifyDataSetChanged();
            //            mListAdaper.notifyDataSetInvalidated();
            //            BaseListAdapter adapter = new BaseListAdapter();
            //            adapter.setData(mListAdaper.getDataSrc());
            //            mListView.setAdapter(adapter);
        }
    }
    
    /**
     * 初始化View<BR>
     */
    private void initView()
    {
        mSearchButton = (Button) findViewById(R.id.title_right_button);
        mSearchButton.setText(R.string.search);
        mLeftBtn = (Button) findViewById(R.id.left_button);
        mLeftBtn.setText(R.string.back);
        mLeftBtn.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mSearchGroup = findViewById(R.id.search_group);
        mEditText = (LimitedEditText) mSearchGroup.findViewById(R.id.search_edit);
        mEditText.setMaxCharLength(20);
        //        mSearchButton = (Button) mSearchGroup.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listView);
        noResultRow = findViewById(R.id.search_no_result_row);
        mNoResultContent = (TextView) findViewById(R.id.no_result_content);
        
        LayoutInflater inflater = getLayoutInflater();
        popView = inflater.inflate(R.layout.popview_remove_location, null);
        popView.findViewById(R.id.frist_btn).setOnClickListener(this);
        popView.findViewById(R.id.second_btn).setOnClickListener(this);
        popView.setOnClickListener(this);
        popupWindow = new PopupWindow(popView, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        if (popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
        switch (v.getId())
        {
            case R.id.left_button:
                FindFriendResultListActivity.this.finish();
                break;
            
            case R.id.right_layout:
                popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.frist_btn:
                mFriendLogic.removeLocationInfo();
                break;
            case R.id.second_btn:
                popupWindow.dismiss();
                break;
            case R.id.title_right_button:

                mSearchValue = mEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(mSearchValue) && !mIsLoading)
                {
                    
                    if (isSearching)
                    {
                        return;
                    }
                    isSearching = true;
                    FindFriendResultListActivity.this.showProgressDialog(R.string.searching);
                    // 进行搜索之前，先关闭输入法
                    hideInputWindow(mEditText);
                    mStartIndex = 1;
                    mTotalCount = 0;
                    loadMoreFriends();
                    Logger.d(TAG, "=========开始搜索=====");
                }
                
                break;
        }
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
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed()
    {
        //如果弹出框有显示，需要先关闭弹出框
        if (popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
        else
        {
            super.onBackPressed();
        }
    }
    
    /**
     * 加载更多的好友<BR>
     */
    private void loadMoreFriends()
    {
        mIsLoading = true;
        switch (mMode)
        {
            case FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID:
                mFriendLogic.loadMoreFriendById(mStartIndex,
                        PAGE_RECORD_COUNT,
                        mSearchValue);
                break;
            case FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_DETAIL:
                mFriendLogic.loadMoreFriendByDetail(mStartIndex,
                        PAGE_RECORD_COUNT,
                        mSearchValue);
                break;
            default:
                break;
        }
    }
    
    /**
     * 初始化logic对象的方法<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) super.getLogicByInterfaceClass(IFriendLogic.class);
        mFriendHelperLogic = (IFriendHelperLogic) super.getLogicByInterfaceClass(IFriendHelperLogic.class);
    }
    
    /**
     * logic的handler监听的回调方法<BR>
     * @param msg
     *      logic对象send的message
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        int type = msg.what;
        Object obj = msg.obj;
        switch (type)
        {
            case FriendMessageType.REQUEST_FRIEND_LOAD_MORE_FRIEND:
                isSearching = false;
                HashMap<String, Object> maps = (HashMap<String, Object>) obj;
                showMoreFriends(maps);
                mListAdaper.notifyDataSetChanged();
                closeProcessDialog();
                Logger.d(TAG, "SEARCH_FRIEND_FROM_SERVER RESPONSE ");
                break;
            case FriendMessageType.REQUEST_FRIEND_MAYBE_KNOWN_PERSON:
                isSearching = false;
                Logger.d(TAG, "GET_MAYBE_KNOWN_PERSON RESPONSE ");
                mListAdaper.setData((List<ContactInfoModel>) obj);
                mListAdaper.notifyDataSetChanged();
                closeProcessDialog();
                break;
            case FriendMessageType.FIND_MAYBE_KNOWN_FRIEND_FAIL:
                isSearching = false;
                showToast(R.string.get_maybe_known_friend_error);
                closeProcessDialog();
                break;
            case FriendMessageType.REQUEST_FIND_FRIEND_ERROR:
            case FriendMessageType.REQUEST_ERROR:
                closeProgressDialog();
                isSearching = false;
                showToast(R.string.request_error);
                break;
            case FriendMessageType.REQUEST_LOCATION:
                showProgressDialog(R.string.determining_location_information);
                break;
            case FriendMessageType.LOCATION_SUCCESS:
                closeProgressDialog();
                showProgressDialog(R.string.connecting);
                break;
            case FriendMessageType.FRIEND_BY_LOCATION_SUCCESS:
                closeProgressDialog();
                isSearching = false;
                Logger.d(TAG, "FRIEND_BY_LOCATION_SUCCESS ");
                mListAdaper.setData((List<ContactInfoModel>) obj);
                mListAdaper.notifyDataSetChanged();
                break;
            
            case FriendMessageType.FRIEND_BY_LOCATION_FAILED:
                closeProgressDialog();
                noResultRow.setVisibility(View.VISIBLE);
                mNoResultContent.setText(R.string.find_by_location_failer);
                break;
            case FriendMessageType.REQUEST_REMOVE_LOCATION:
                isSearching = false;
                showOnlyConfirmDialog(R.string.find_by_location_remove_success,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                finish();
                            }
                        });
                break;
            case FriendMessageType.REMOVE_LOCATION_ERROR:
                isSearching = false;
                showToast(FusionErrorInfo.getErrorInfo(mContext, (String) msg.obj));
                break;
            case FriendMessageType.CONTACT_INFO_INSERT:
                mListAdaper.notifyDataSetChanged();
                break;
            case FriendMessageType.CONTACT_INFO_DELETE:
                mListAdaper.notifyDataSetChanged();
                break;
            default:
                break;
            
        }
        mIsLoading = false;
        super.handleStateMessage(msg);
    }
    
    /**
     * 显示查询到更多的好友<BR>
     * @param maps
     *      好友数据的map
     */
    @SuppressWarnings("unchecked")
    private void showMoreFriends(HashMap<String, Object> maps)
    {
        if (mTotalCount == 0)
        {
            mListAdaper.setData(null);
        }
        if (null != maps)
        {
            List<ContactInfoModel> list = (List<ContactInfoModel>) maps.get("lists");
            List<ContactInfoModel> beforeList = (List<ContactInfoModel>) mListAdaper.getDataSrc();
            if (beforeList != null)
            {
                beforeList.addAll(list);
            }
            else
            {
                beforeList = list;
            }
            mListAdaper.setData(beforeList);
            if (maps.get("total") != null)
            {
                mTotalCount = (Integer) maps.get("total");
            }
            if (mStartIndex <= mTotalCount)
            {
                mStartIndex += PAGE_RECORD_COUNT;
            }
            Logger.d(TAG, "TOTALCOUNT == " + mTotalCount);
        }
    }
    
    /**
     * 列表展示的adapter<BR>
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    private class ResultListAdapter extends BaseListAdapter
    {
        
        private HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        
        /**
         * 数据变化响应的通知方法<BR>
         * @see android.widget.BaseAdapter#notifyDataSetChanged()
         */
        @Override
        public void notifyDataSetChanged()
        {
            map.clear();
            if (getCount() > 0)
            {
                noResultRow.setVisibility(View.GONE);
            }
            else
            {
                noResultRow.setVisibility(View.VISIBLE);
                //推荐好友，没有数据时，展示 暂时没有可能认识的人
                if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN)
                {
                    mNoResultContent.setText(R.string.no_recommend_friend);
                }
                //其它展示 暂无搜索结果
                else
                {
                    mNoResultContent.setText(R.string.search_no_result);
                }
            }
            super.notifyDataSetChanged();
        }
        
        /**
         * 获取当前的view<BR>
         * @param position
         *      view的下标
         * @param convertView
         *      当前的view控件对象
         * @param parent
         *      所属的ListView对象
         * @return
         *      生成的新的view对象
         * @see com.huawei.basic.android.im.ui.basic.BaseListAdapter#getView
         * (int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            final ViewHolder holder;
            final ContactInfoModel contactBean = (ContactInfoModel) getDataSrc().get(position);
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) FindFriendResultListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_list_row, null);
                holder = new ViewHolder();
                holder.mImage = (ImageView) convertView.findViewById(R.id.head);
                holder.mFriendNote = (TextView) convertView.findViewById(R.id.friend_note);
                holder.mNameView = (TextView) convertView.findViewById(R.id.name);
                holder.mIsFriendView = (TextView) convertView.findViewById(R.id.is_friend);
                holder.mInfoOneView = (TextView) convertView.findViewById(R.id.info_one);
                holder.mInfoTwoView = (TextView) convertView.findViewById(R.id.info_two);
                holder.mFriendStatus = (TextView) convertView.findViewById(R.id.friend_status);
                holder.mSexFlag = (ImageView) convertView.findViewById(R.id.sex_image);
                holder.mAddView = (ImageView) convertView.findViewById(R.id.add_image);
                holder.mInviteButton = (Button) convertView.findViewById(R.id.invite_button);
                holder.mInviteButton.setVisibility(View.GONE);
                holder.mHitalkIdView = (TextView) convertView.findViewById(R.id.hitalk_id);
                holder.mSignatrueView = (TextView) convertView.findViewById(R.id.signature);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            mPhotoLoader.loadPhoto(holder.mImage, contactBean.getFaceUrl());
            holder.mNameView.setText(contactBean.getNickName());
            holder.mAddView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!(contactBean.getFriendPrivacy() == 3 || contactBean.getAutoConfirmFriend() != 2))
                    {
                        if (null == map.get(position) || !map.get(position))
                        {
                            addFriendAction(contactBean);
                            map.put(position, true);
                        }
                    }
                    else
                    {
                        addFriendAction(contactBean);
                    }
                }
            });
            if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN)
            {
                String srsContent = "";
                switch (contactBean.getSrsType())
                {
                    case ContactInfoModel.SRS_TYPE_SAME_COMPANY:
                        srsContent = mContext.getString(R.string.find_friend_same_company);
                        break;
                    case ContactInfoModel.SRS_TYPE_SAME_SCHOOL:
                        srsContent = mContext.getString(R.string.find_friend_same_school);
                        break;
                    case ContactInfoModel.SRS_TYPE_SAME_CITY:
                        srsContent = mContext.getString(R.string.find_friend_same_city);
                        break;
                    case ContactInfoModel.SRS_TYPE_CONTACT_BOOK:
                        srsContent = mContext.getString(R.string.find_friend_have_you_phone);
                        break;
                    case ContactInfoModel.SRS_TYPE_SHARE_FRIENDS:
                        srsContent = String.format(mContext.getString(R.string.find_friend_share_friends),
                                (int) contactBean.getSrsCommonNum());
                        break;
                }
                holder.mAddView.setVisibility(View.VISIBLE);
                holder.mSignatrueView.setText(srsContent);
                if (mFriendLogic.isFriendExist(contactBean.getFriendSysId()))
                {
                    holder.mAddView.setVisibility(View.GONE);
                    holder.mIsFriendView.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.mAddView.setVisibility(View.VISIBLE);
                    holder.mIsFriendView.setVisibility(View.GONE);
                }
            }
            else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_NEAR)
            {
                holder.mAddView.setVisibility(View.GONE);
                // 设置性别图片
                int gender = contactBean.getGender();
                if (gender == 0)
                {
                    holder.mSexFlag.setVisibility(View.GONE);
                }
                else
                {
                    holder.mSexFlag.setVisibility(View.VISIBLE);
                    if (gender == 1)
                    {
                        holder.mSexFlag.setImageResource(R.drawable.setting_female);
                    }
                    else
                    {
                        holder.mSexFlag.setImageResource(R.drawable.setting_male);
                    }
                }
                // 设置好友距离
                holder.mSignatrueView.setText(BaseContactUtil.getDistance(mContext,
                        contactBean.getSrsCommonNum()));
                // 设置好友签名
                if (!StringUtil.isNullOrEmpty(contactBean.getSignature()))
                {
                    holder.mFriendNote.setVisibility(View.VISIBLE);
                    holder.mFriendNote.setText(contactBean.getSignature());
                }
                else
                {
                    holder.mFriendNote.setVisibility(View.GONE);
                }
                //标识是否为好友
                if (1 == contactBean.getFriendStatus())
                {
                    holder.mFriendStatus.setVisibility(View.VISIBLE);
                    holder.mFriendStatus.setText(R.string.find_by_location_friend_status);
                }
                else if (0 == contactBean.getFriendStatus())
                {
                    holder.mFriendStatus.setVisibility(View.GONE);
                }
            }
            else if (mMode == FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID)
            {
                if (mFriendLogic.isFriendExist(contactBean.getFriendSysId()))
                {
                    holder.mAddView.setVisibility(View.GONE);
                    holder.mIsFriendView.setVisibility(View.VISIBLE);
                    holder.mIsFriendView.setText(R.string.friends);
                    holder.mSignatrueView.setText(String.format(mContext.getString(R.string.find_friend_result_id),
                            contactBean.getFriendUserId()));
                }
                else
                {
                    holder.mAddView.setVisibility(View.VISIBLE);
                    holder.mIsFriendView.setVisibility(View.GONE);
                    if (!StringUtil.isNullOrEmpty(contactBean.getPrimaryMobile()))
                    {
                        holder.mSignatrueView.setText(String.format(mContext.getString(R.string.find_friend_result_mobile),
                                contactBean.getPrimaryMobile()));
                    }
                    else
                    {
                        holder.mSignatrueView.setText("");
                    }
                }
            }
            else
            {
                //通过个人资料查找好友
                holder.mSignatrueView.setVisibility(View.GONE);
                holder.mInfoOneView.setVisibility(View.INVISIBLE);
                holder.mHitalkIdView.setText(" ("
                        + contactBean.getFriendUserId() + ")");
                holder.mHitalkIdView.setVisibility(View.VISIBLE);
                
                //优先显示 Address >Company>School
                String infoOne = StringUtil.isNullOrEmpty(contactBean.getAddress()) ? (StringUtil.isNullOrEmpty(contactBean.getCompany()) ? contactBean.getSchool()
                        : contactBean.getCompany())
                        : contactBean.getAddress();
                String infoTwo = null;
                
                if (StringUtil.isNullOrEmpty(infoOne))
                {
                    infoTwo = null;
                }
                else
                {
                    infoTwo = infoOne.equals(contactBean.getAddress()) ? (StringUtil.isNullOrEmpty(contactBean.getCompany()) ? contactBean.getSchool()
                            : contactBean.getCompany())
                            : StringUtil.isNullOrEmpty(contactBean.getCompany()) ? null
                                    : contactBean.getSchool();
                }
                
                if (StringUtil.isNullOrEmpty(infoOne))
                {
                    holder.mInfoTwoView.setVisibility(View.GONE);
                }
                else
                {
                    holder.mInfoOneView.setVisibility(View.VISIBLE);
                    holder.mInfoOneView.setText(infoOne);
                    if (StringUtil.isNullOrEmpty(infoTwo))
                    {
                        holder.mInfoTwoView.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.mInfoTwoView.setVisibility(View.VISIBLE);
                        holder.mInfoTwoView.setText(infoTwo);
                    }
                }
                if (mFriendLogic.isFriendExist(contactBean.getFriendSysId()))
                {
                    holder.mAddView.setVisibility(View.GONE);
                    holder.mIsFriendView.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.mAddView.setVisibility(View.VISIBLE);
                    holder.mIsFriendView.setVisibility(View.GONE);
                }
            }
            return convertView;
        }
    }
    
    /**
     * 存放view对象的Holder<BR>
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    private static class ViewHolder
    {
        private ImageView mImage;
        
        private TextView mNameView;
        
        private TextView mHitalkIdView;
        
        private TextView mFriendNote;
        
        private TextView mSignatrueView;
        
        private TextView mInfoOneView;
        
        private TextView mInfoTwoView;
        
        private ImageView mSexFlag;
        
        private ImageView mAddView;
        
        private TextView mIsFriendView;
        
        private Button mInviteButton;
        
        private TextView mFriendStatus;
    }
    
    private void closeProcessDialog()
    {
        closeProgressDialog();
    }
    
    /**
     * 添加好友<BR>
     * 需要判断隐私设置
     */
    private void addFriendAction(ContactInfoModel contactModel)
    {
        // 对方设置不允许加好友
        if (contactModel.getFriendPrivacy() == 3)
        {
            showToast(R.string.add_friend_not_allowed);
            return;
        }
        // 对方被加好友需要验证
        else if (contactModel.getAutoConfirmFriend() != 2)
        {
            mCurrentContactInfoModel = contactModel;
            // 跳转到输入验证理由界面
            Intent intent = new Intent();
            intent.setAction(InputReasonAction.ACTION);
            intent.putExtra(InputReasonAction.EXTRA_MODE,
                    InputReasonAction.MODE_REASON);
            startActivityForResult(intent, REQ_FOR_INPUT_REASON);
        }
        else
        {
            mFriendHelperLogic.addFriend(contactModel.getFriendUserId(),
                    contactModel.getNickName(),
                    "",
                    contactModel.getFaceUrl());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_FOR_INPUT_REASON)
        {
            // 输验证信息界面反馈数据
            if (resultCode == RESULT_OK && null != data)
            {
                // 加好友附带请求信息
                String operateResult = data.getStringExtra(InputReasonAction.OPERATE_RESULT);
                // 发送加好友请求
                mFriendHelperLogic.addFriend(mCurrentContactInfoModel.getFriendUserId(),
                        mCurrentContactInfoModel.getNickName(),
                        operateResult,
                        mCurrentContactInfoModel.getFaceUrl());
            }
        }
    }
    
    /**
     * 配置信息发生变化<BR>
     * @param newConfig 新的配置信息
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Logger.e(TAG, "onConfigurationChanged ->>> " + newConfig.locale);
        resetView();
        super.onConfigurationChanged(newConfig);
    }
}
