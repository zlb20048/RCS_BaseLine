/*
 * 文件名: EmotionWindow.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2012-4-16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic.emotion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.ui.basic.RichEditText;
import com.huawei.basic.android.im.ui.basic.emotion.bean.EmojiBean;
import com.huawei.basic.android.im.ui.im.PictureManager;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 表情展示界面的具体实现类<BR>
 * 该类目前只实现功能，相关优化和修改正在开发中
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-5-17]
 */
public class EmotionWindow implements OnGestureListener, OnTouchListener,
        OnItemClickListener, OnItemLongClickListener
{
    /**
     * 调用表情管理器的类型，有IM聊天，群发，广播其他等等，如果是IM聊天显示3个标签页
     */
    public static final int IM_CHAT = 0;
    
    /**
     * 控制只显示表情标签页
     */
    public static final int OTHER = 1;
    
    /**
     * 选中状态
     */
    public static final int SELECTED = 1;
    
    /**
     * 正常状态
     */
    public static final int NORMAL = 2;
    
    private static final String TAG = "EmotionWindow";
    
    /**
     * 设置允许左右滑动的最短距离
     */
    private static final int FLING_MIN_DISTANCE = 150;
    
    private static final int FLING_MIN_VELOCITY = 150;
    
    /**
     * 对象实例
     */
    private static EmotionWindow instance;
    
    private int blackColor = 0;
    
    private int whiteColor = 0;
    
    private String[] arrayTitles;
    
    private int[] arrayTabs;
    
    /**
     * 当前选中的title
     */
    private int currTitlePos = 0;
    
    /**
     * 当前选中的tab
     */
    private int currTabPos = 0;
    
    /**
     * 贴图包的Map
     */
    private List<List<List<Map<String, EmojiBean>>>> list;
    
    /**
     * 资源对象
     */
    private Resources resource;
    
    /**
     * 表情
     */
    private int emotion = 0;
    
    /**
     * 贴图
     */
    private int picture = 1;
    
    /**
     * 表情符号
     */
    private int maps = 2;
    
    /**
     * 最外层的layout, Tab的layout, 中间表情的layout
     */
    private LinearLayout mLayout, tabLayout, midLayout;
    
    /**
     * 分别为标题TAB视图，表情图片视图，分类TAB视图
     */
    private GridView mTitleGridView, mGridView1, mGridView2, mGridView3,
            mGridView5, mGridView6, mGridView7, mGridView8, mGridView9,
            mGridView10, mGridView11, mGridView12, mGridView13, mGridView14,
            mTabView;
    
    /**
     * Tab的适配器
     */
    private AdapterManager.TabAdapter adtTab;
    
    /**
     * 标题适配器
     */
    private AdapterManager.TitleAdapter adtTitle;
    
    /**
     * 表情管理器的适配器
     */
    private SimpleAdapter mAdapter1, mAdapter2, mAdapter3, mAdapter4,
            mAdapter5;
    
    /**
     * 上下文内容
     */
    private Activity context;
    
    /**
     * 表情管理器适配器源数据
     */
    private List<List<Map<String, Object>>> data;
    
    /**
     * 贴图的数据源
     */
    private List<List<Map<String, EmojiBean>>> emojiData;
    
    private ArrayList<TabItemBean> tabList;
    
    private RichEditText editText;
    
    private ViewFlipper mViewFlipper1, mViewFlipper2, mViewFlipper3,
            mViewFlipper4;
    
    private GestureDetector mGestureDetector;
    
    private ImageView[] imgs = new ImageView[5];
    
    /**
     * 贴图点击接口
     */
    private IPinupEmotionClickListener mPinupEmotionClickListener;
    
    /**
     *无参构造方法
     */
    public EmotionWindow()
    {
    }
    
    /**
     * 
     * 有参构造方法
     * @param context Activity
     * @param editText RichEditText
     */
    private EmotionWindow(Activity context, RichEditText editText)
    {
        this.context = context;
        this.editText = editText;
    }
    
    /**
     * 
     * 获得表情管理器对象实例<BR>
     * @param context context
     * @param editText RichEditText
     * @return EmotionWindow对象
     */
    public static EmotionWindow getInstance(Activity context,
            RichEditText editText)
    {
        instance = new EmotionWindow(context, editText);
        return instance;
    }
    
    /**
     * 
     * 获得emotionWindow对象<BR>
     * @param context Context
     * @return EmotionWindow EmotionWindow对象
     */
    public static EmotionWindow getCurrInstance(Activity context)
    {
        if (instance == null)
        {
            instance = new EmotionWindow();
        }
        return instance;
    }
    
    /**
     * 
     * 关闭表情窗口<BR>
     * @return 是否关闭
     */
    public boolean closePopWindow()
    {
        boolean isClosed = false;
        if (mLayout != null && mLayout.getVisibility() != View.GONE)
        {
            mLayout.setVisibility(View.GONE);
            mLayout = null;
            isClosed = true;
            instance = null;
            mViewFlipper1 = null;
            mViewFlipper2 = null;
            mViewFlipper3 = null;
            mViewFlipper4 = null;
        }
        return isClosed;
    }
    
    /**
     * 获得表情管理器状态，显示还是隐藏
     * @return 情管理器状态
     */
    public int getWindowStatus()
    {
        int status = View.GONE;
        if (mLayout != null)
        {
            status = mLayout.getVisibility();
        }
        return status;
    }
    
    /**
     * 设置表情管理器状态，显示还是隐藏
     * @param status 状态
     */
    public void setWindowStatus(int status)
    {
        if (mLayout != null)
        {
            mLayout.setVisibility(status);
        }
    }
    
    /**
     * 
     *  获得表情管理器的window<BR>
     * @param from 当前的聊天类型
     * @param pinupEmotionClickListener 贴图点击的监听
     */
    public void showEmotionWindow(int from,
            IPinupEmotionClickListener pinupEmotionClickListener)
    {
        mPinupEmotionClickListener = pinupEmotionClickListener;
        // 初始化基础数据
        initBaseData(from);
        
        // 后期可以考虑动态添加GridView
        mViewFlipper1 = (ViewFlipper) context.findViewById(R.id.flipper1);
        mViewFlipper2 = (ViewFlipper) context.findViewById(R.id.flipper2);
        mViewFlipper3 = (ViewFlipper) context.findViewById(R.id.flipper3);
        mViewFlipper4 = (ViewFlipper) context.findViewById(R.id.flipper4);
        mLayout = (LinearLayout) context.findViewById(R.id.ly_emotions);
        tabLayout = (LinearLayout) context.findViewById(R.id.ly_down_tab);
        midLayout = (LinearLayout) context.findViewById(R.id.ly_mid_emotions);
        mTitleGridView = (GridView) context.findViewById(R.id.gv_up_tab);
        
        //表情的三个gridView
        mGridView1 = (GridView) context.findViewById(R.id.gv_mid_emotions1);
        mGridView2 = (GridView) context.findViewById(R.id.gv_mid_emotions2);
        mGridView3 = (GridView) context.findViewById(R.id.gv_mid_emotions3);
        
        //贴图的gridView
        mGridView5 = (GridView) context.findViewById(R.id.gv_mid_emotions5);
        mGridView6 = (GridView) context.findViewById(R.id.gv_mid_emotions6);
        mGridView7 = (GridView) context.findViewById(R.id.gv_mid_emotions7);
        mGridView8 = (GridView) context.findViewById(R.id.gv_mid_emotions8);
        mGridView9 = (GridView) context.findViewById(R.id.gv_mid_emotions9);
        
        //表情符号的gridView
        mGridView10 = (GridView) context.findViewById(R.id.gv_mid_emotions10);
        mGridView11 = (GridView) context.findViewById(R.id.gv_mid_emotions11);
        mGridView12 = (GridView) context.findViewById(R.id.gv_mid_emotions12);
        mGridView13 = (GridView) context.findViewById(R.id.gv_mid_emotions13);
        mGridView14 = (GridView) context.findViewById(R.id.gv_mid_emotions14);
        
        imgs[0] = (ImageView) context.findViewById(R.id.switch_1);
        imgs[1] = (ImageView) context.findViewById(R.id.switch_2);
        imgs[2] = (ImageView) context.findViewById(R.id.switch_3);
        imgs[3] = (ImageView) context.findViewById(R.id.switch_4);
        imgs[4] = (ImageView) context.findViewById(R.id.switch_5);
        
        mTabView = (GridView) context.findViewById(R.id.gv_down_tab);
        
        // 添加事件,一定要给gridview加onTouch事件，要不然响应不了触摸方法
        mGridView1.setOnTouchListener(this);
        mGridView2.setOnTouchListener(this);
        mGridView3.setOnTouchListener(this);
        
        mGridView5.setOnTouchListener(this);
        mGridView6.setOnTouchListener(this);
        mGridView7.setOnTouchListener(this);
        mGridView8.setOnTouchListener(this);
        mGridView9.setOnTouchListener(this);
        mGridView10.setOnTouchListener(this);
        mGridView11.setOnTouchListener(this);
        mGridView12.setOnTouchListener(this);
        mGridView13.setOnTouchListener(this);
        mGridView14.setOnTouchListener(this);
        
        mTitleGridView.setOnItemClickListener(this);
        mGridView1.setOnItemClickListener(this);
        mGridView2.setOnItemClickListener(this);
        mGridView3.setOnItemClickListener(this);
        
        mGridView5.setOnItemClickListener(this);
        mGridView6.setOnItemClickListener(this);
        mGridView7.setOnItemClickListener(this);
        mGridView8.setOnItemClickListener(this);
        mGridView9.setOnItemClickListener(this);
        mGridView10.setOnItemClickListener(this);
        mGridView11.setOnItemClickListener(this);
        mGridView12.setOnItemClickListener(this);
        mGridView13.setOnItemClickListener(this);
        mGridView14.setOnItemClickListener(this);
        mTabView.setOnItemClickListener(this);
        
        mGridView1.setOnItemLongClickListener(this);
        mGridView2.setOnItemLongClickListener(this);
        mGridView3.setOnItemLongClickListener(this);
        
        mGridView5.setOnItemLongClickListener(this);
        mGridView6.setOnItemLongClickListener(this);
        mGridView7.setOnItemLongClickListener(this);
        mGridView8.setOnItemLongClickListener(this);
        mGridView9.setOnItemLongClickListener(this);
        mGridView10.setOnItemLongClickListener(this);
        mGridView11.setOnItemLongClickListener(this);
        mGridView12.setOnItemLongClickListener(this);
        mGridView13.setOnItemLongClickListener(this);
        mGridView14.setOnItemLongClickListener(this);
        
        // 初始化适配器数据
        initEmotionAdapter();
        
        // 初始化viewFlipper值
        // initFLipper();
        
        // 设置Adapter
        mTitleGridView.setAdapter(adtTitle);
        mTabView.setAdapter(adtTab);
        
        // 设置title标签页的列数
        mTitleGridView.setNumColumns(arrayTitles.length);
        
        mLayout.setVisibility(View.VISIBLE);
        mLayout.setFocusableInTouchMode(true);
        mLayout.setFocusable(true);
        mLayout.setLongClickable(true);
        switchTitleAndTab(emotion, 0);
    }
    
    /**
     * 初始化基础数据
     * @param from 广播，微博处只要表情和表情符号即可
     */
    private void initBaseData(int from)
    {
        resource = context.getResources();
        blackColor = resource.getColor(R.color.black);
        whiteColor = resource.getColor(R.color.white);
        if (from == IM_CHAT)
        {
            arrayTitles = resource.getStringArray(R.array.emotion_title1);
        }
        else
        {
            arrayTitles = resource.getStringArray(R.array.emotion_title2);
        }
        arrayTabs = new int[] { R.drawable.tab001, R.drawable.tab002 };
        if (tabList == null)
        {
            tabList = new ArrayList<TabItemBean>();
            
            for (int i = 0; i < arrayTabs.length; i++)
            {
                TabItemBean item = new TabItemBean();
                item.setResId(arrayTabs[i]);
                if (i == 0)
                {
                    item.setStatus(SELECTED);
                }
                else
                {
                    item.setStatus(NORMAL);
                }
                tabList.add(item);
            }
        }
        // 如果是两个标签的话，第二项为表情符号
        if (arrayTitles.length == 2)
        {
            maps = 1;
            picture = 2;
        }
        mGestureDetector = new GestureDetector(this);
        adtTitle = new AdapterManager.TitleAdapter(context, arrayTitles, 16,
                blackColor);
        adtTab = new AdapterManager.TabAdapter(context, tabList);
        currTabPos = 0;
        currTitlePos = 0;
    }
    
    /**
     * 
     * gridViewItem点击事件监听<BR>
     * @param paramAdapterView AdapterView
     * @param paramView View
     * @param paramInt paramInt
     * @param paramLong paramLong
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
            int paramInt, long paramLong)
    {
        switch (paramAdapterView.getId())
        {
            case R.id.gv_up_tab:
                if (paramInt == currTitlePos)
                {
                    return;
                }
                switchTitleAndTab(paramInt, 0);
                // 点击标题后更换adapter
                changeAdapter(paramInt, 0);
                break;
            case R.id.gv_down_tab:
                if (paramInt == currTabPos)
                {
                    return;
                }
                switchTitleAndTab(1, paramInt);
                changeAdapter(1, paramInt);
                break;
            default:
                getDataOfPos(paramInt);
        }
    }
    
    /**
     * 
     * 初始化表情适配器数据
     * @param context
     */
    private void initEmotionAdapter()
    {
        data = EmotionManager.getInstance(context)
                .getExpressionsForAdapter(EmotionManager.TYPE_DEFAULT);
        
        mAdapter1 = new SimpleAdapter(this.context, data.get(0), R.layout.item,
                new String[] { EmotionManager.ADAPTER_KEY_RES_ID },
                new int[] { R.id.item_image });
        mAdapter2 = new SimpleAdapter(this.context, data.get(1), R.layout.item,
                new String[] { EmotionManager.ADAPTER_KEY_RES_ID },
                new int[] { R.id.item_image });
        mAdapter3 = new SimpleAdapter(this.context, data.get(2), R.layout.item,
                new String[] { EmotionManager.ADAPTER_KEY_RES_ID },
                new int[] { R.id.item_image });
        //        mAdapter4 = new SimpleAdapter(this.context,
        //            data.get(3),
        //            R.layout.item,
        //            new String[] {EmotionManager.ADAPTER_KEY_RES_ID },
        //            new int[] {R.id.item_image });
        
        mGridView1.setAdapter(mAdapter1);
        mGridView2.setAdapter(mAdapter2);
        mGridView3.setAdapter(mAdapter3);
        
        mViewFlipper1.setVisibility(View.VISIBLE);
        mViewFlipper2.setVisibility(View.GONE);
        mViewFlipper3.setVisibility(View.GONE);
        mViewFlipper4.setVisibility(View.GONE);
        
        initFlipperIndex(mViewFlipper1);
    }
    
    private void initFlipperIndex(ViewFlipper mViewFlipper)
    {
        
        int childCount = mViewFlipper.getChildCount();
        
        for (int i = 0; i < imgs.length; i++)
        {
            // 默认都是可见
            imgs[i].setVisibility(View.VISIBLE);
            
            if (mViewFlipper.indexOfChild(mViewFlipper.getCurrentView()) == i)
            {
                imgs[i].setImageResource(R.drawable.features_welcome_p2);
            }
            else if (childCount > i)
            {
                imgs[i].setImageResource(R.drawable.features_welcome_p1);
            }
            else
            {
                // 将多余的图片隐藏
                imgs[i].setVisibility(View.GONE);
            }
        }
        
    }
    
    /**
     * 
     * 初始化贴图适配器数据
     * 
     * @param context
     */
    private void initEmojiAdapter(int posTab)
    {
        if (list == null)
        {
            list = PictureManager.getInstance(context.getApplicationContext())
                    .getAllPictures()
                    .get();
        }
        if (posTab == 0)
        {
            emojiData = list.get(posTab);
            mGridView5.setAdapter(new AdapterManager.ImageAdapter(context,
                    emojiData.get(0)));
            mGridView6.setAdapter(new AdapterManager.ImageAdapter(context,
                    emojiData.get(1)));
            mGridView7.setAdapter(new AdapterManager.ImageAdapter(context,
                    emojiData.get(2)));
            
            mViewFlipper1.setVisibility(View.GONE);
            mViewFlipper2.setVisibility(View.VISIBLE);
            mViewFlipper3.setVisibility(View.GONE);
            mViewFlipper4.setVisibility(View.GONE);
            initFlipperIndex(mViewFlipper2);
        }
        else
        {
            emojiData = list.get(posTab);
            mGridView8.setAdapter(new AdapterManager.ImageAdapter(context,
                    emojiData.get(0)));
            mGridView9.setAdapter(new AdapterManager.ImageAdapter(context,
                    emojiData.get(1)));
            mViewFlipper1.setVisibility(View.GONE);
            mViewFlipper2.setVisibility(View.GONE);
            mViewFlipper3.setVisibility(View.VISIBLE);
            mViewFlipper4.setVisibility(View.GONE);
            initFlipperIndex(mViewFlipper3);
        }
        
    }
    
    /**
     * 
     * 初始化表情符号适配器数据
     * 
     * @param context
     */
    private void initMapsAdapter()
    {
        
        data = MapsManager.getMapsForAdapter();
        if (data != null)
        {
            mAdapter1 = new SimpleAdapter(this.context, data.get(0),
                    R.layout.emotion_item_maps,
                    new String[] { EmotionManager.ADAPTER_KEY_WORD },
                    new int[] { R.id.item_text });
            mAdapter2 = new SimpleAdapter(this.context, data.get(1),
                    R.layout.emotion_item_maps,
                    new String[] { EmotionManager.ADAPTER_KEY_WORD },
                    new int[] { R.id.item_text });
            mAdapter3 = new SimpleAdapter(this.context, data.get(2),
                    R.layout.emotion_item_maps,
                    new String[] { EmotionManager.ADAPTER_KEY_WORD },
                    new int[] { R.id.item_text });
            mAdapter4 = new SimpleAdapter(this.context, data.get(3),
                    R.layout.emotion_item_maps,
                    new String[] { EmotionManager.ADAPTER_KEY_WORD },
                    new int[] { R.id.item_text });
            mAdapter5 = new SimpleAdapter(this.context, data.get(4),
                    R.layout.emotion_item_maps,
                    new String[] { EmotionManager.ADAPTER_KEY_WORD },
                    new int[] { R.id.item_text });
            
            mGridView10.setAdapter(mAdapter1);
            mGridView11.setAdapter(mAdapter2);
            mGridView12.setAdapter(mAdapter3);
            mGridView13.setAdapter(mAdapter4);
            mGridView14.setAdapter(mAdapter5);
            
            mViewFlipper1.setVisibility(View.GONE);
            mViewFlipper2.setVisibility(View.GONE);
            mViewFlipper3.setVisibility(View.GONE);
            mViewFlipper4.setVisibility(View.VISIBLE);
            
            initFlipperIndex(mViewFlipper4);
        }
    }
    
    /**
     * 点击标题Tab和下面Tab的选中效果以及布局的大小调整
     * 
     * @param posTitle 上面Title的位置
     * @param posTab 下面Tab的位置
     */
    private void switchTitleAndTab(int posTitle, int posTab)
    {
        // 此处控制下面的Tab视图是否显示，1代表是有tab的
        LinearLayout.LayoutParams params = (LayoutParams) midLayout.getLayoutParams();
        
        // 根据下面的tab是否要显示，改变视图的大小
        if (arrayTitles.length == 3)
        {
            if (posTitle == picture)
            {
                float fPx1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        160,
                        context.getResources().getDisplayMetrics());
                
                // 同理 px转dip：
                // float fDip =
                // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 307,
                // resources.getDisplayMetrics());
                // int iDip = Math.round(fDip);
                
                int iPx1 = Math.round(fPx1);
                tabLayout.setVisibility(View.VISIBLE);
                params.height = iPx1;
                midLayout.setLayoutParams(params);
            }
            else
            {
                float fPx2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        206,
                        context.getResources().getDisplayMetrics());
                
                // 同理 px转dip：
                // float fDip =
                // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 307,
                // resources.getDisplayMetrics());
                // int iDip = Math.round(fDip);
                
                int iPx2 = Math.round(fPx2);
                tabLayout.setVisibility(View.GONE);
                params.height = iPx2;
                midLayout.setLayoutParams(params);
            }
        }
        setSelected(posTitle, posTab);
    }
    
    /**
     * 
     * 设置选中后TAB的效果
     * 
     * @param posTitle 上面Title的位置
     * @param posTab 下面Tab的位置
     */
    private void setSelected(int posTitle, int posTab)
    {
        for (int i = 0; i < arrayTitles.length; i++)
        {
            TextView tvSelected = (TextView) mTitleGridView.getItemAtPosition(i);
            if (posTitle != i)
            {
                tvSelected.setBackgroundDrawable(resource.getDrawable(R.drawable.unselected_up_tab));
                tvSelected.setTextColor(whiteColor);
            }
            // 当前选中的标题项
            else
            {
                tvSelected.setBackgroundDrawable(resource.getDrawable(R.drawable.selected_up_tab));
                tvSelected.setTextColor(blackColor);
            }
        }
        
        if (posTitle == picture)
        {
            for (int i = 0; i < arrayTabs.length; i++)
            {
                if (posTab != i)
                
                {
                    tabList.get(i).setStatus(NORMAL);
                }
                // 当前选中的TAB项
                else
                {
                    tabList.get(i).setStatus(SELECTED);
                    
                }
            }
            adtTab.notifyDataSetChanged();
        }
    }
    
    private void getDataOfPos(int paramInt)
    {
        try
        {
            // 记录当前光标选取位置
            int mChatTextSelectionStart = editText.getSelectionStart();
            int mChatTextSelectionEnd = editText.getSelectionEnd();
            
            // 贴图
            if (currTitlePos == picture)
            {
                // 直接发送
                EmojiBean emojiBean = new EmojiBean();
                
                Set<Map.Entry<String, EmojiBean>> keyset = null;
                
                if (currTabPos == 0)
                {
                    @SuppressWarnings("unchecked")
                    Map<String, EmojiBean> emojiMap = (Map<String, EmojiBean>) (((GridView) mViewFlipper2.getCurrentView()).getAdapter().getItem(paramInt));
                    // 获得键的集合
                    keyset = emojiMap.entrySet();
                    
                }
                else
                {
                    @SuppressWarnings("unchecked")
                    Map<String, EmojiBean> emojiMap = (Map<String, EmojiBean>) ((GridView) mViewFlipper3.getCurrentView()).getAdapter()
                            .getItem(paramInt);
                    // 获得键的集合 
                    keyset = emojiMap.entrySet();
                }
                
                if (keyset != null)
                {
                    Iterator<Map.Entry<String, EmojiBean>> it = keyset.iterator();
                    if (it.hasNext())
                    {
                        Entry<String, EmojiBean> entry = it.next();
                        emojiBean = entry.getValue();
                    }
                }
                
                // 贴图的大小为10KB
                if (null != mPinupEmotionClickListener)
                {
                    mPinupEmotionClickListener.onPinupEmotionClick(MediaIndexModel.MEDIATYPE_EMOJI,
                            emojiBean.getTtid(),
                            "10",
                            -1,
                            emojiBean.getAlt());
                }
                
            }
            // 表情需要转义
            else if (currTitlePos == emotion)
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> express = (Map<String, Object>) ((GridView) mViewFlipper1.getCurrentView()).getAdapter()
                        .getItem(paramInt);
                final String word = (String) express.get(EmotionManager.ADAPTER_KEY_WORD);
                Editable editable = editText.getEditableText();
                //TODO：如果word为空则不是表情，可能是删除按钮或者是空
                if (StringUtil.isNullOrEmpty(word))
                {
                    //如果是最后一个元素，删除按钮，则进行删除
                    if (paramInt == (EmotionManager.PER_PAGE_COUNT - 1))
                    {
                        editable.delete(mChatTextSelectionStart - 1,
                                mChatTextSelectionStart);
                    }
                }
                else
                {
                    editable.replace(mChatTextSelectionStart,
                            mChatTextSelectionEnd,
                            EmotionManager.getInstance(context).format(word));
                }
            }
            
            else if (currTitlePos == maps)
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> express = (Map<String, Object>) ((GridView) mViewFlipper4.getCurrentView()).getAdapter()
                        .getItem(paramInt);
                final String word = (String) express.get(EmotionManager.ADAPTER_KEY_WORD);
                Editable editable = editText.getEditableText();
                
                editable.replace(mChatTextSelectionStart,
                        mChatTextSelectionEnd,
                        word);
                
            }
            
            editText.requestFocus();
        }
        catch (Exception e)
        {
            Logger.e(TAG, "error message : ", e);
        }
    }
    
    /**
     * 点击事件后，更换Adapter
     * 
     * @param posTitle
     * @param posTab
     */
    private void changeAdapter(int posTitle, int posTab)
    {
        
        if (posTitle == emotion)
        {
            initEmotionAdapter();
            currTitlePos = emotion;
        }
        else if (posTitle == picture)
        {
            initEmojiAdapter(posTab);
            currTitlePos = picture;
            currTabPos = posTab;
        }
        else if (posTitle == maps)
        {
            initMapsAdapter();
            currTitlePos = maps;
        }
    }
    
    /**
     * 
     * tab标签实体类<BR>
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-5-17]
     */
    static class TabItemBean
    {
        /**
         * 资源ID
         */
        private int resId;
        
        /**
         * 状态
         */
        private int status;
        
        public int getResId()
        {
            return resId;
        }
        
        public void setResId(int resId)
        {
            this.resId = resId;
        }
        
        public int getStatus()
        {
            return status;
        }
        
        public void setStatus(int status)
        {
            this.status = status;
        }
        
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param e MotionEvent
     * @return boolean
     * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
     */
    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param e MotionEvent
     * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
     */
    
    @Override
    public void onShowPress(MotionEvent e)
    {
        
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param e MotionEvent
     * @return boolean
     * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
     */
    
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return false;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param e1 MotionEvent
     * @param e2 MotionEvent
     * @param distanceX x位移
     * @param distanceY y位移
     * @return boolean
     * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent,
     *      android.view.MotionEvent, float, float)
     */
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY)
    {
        return false;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param e MotionEvent
     * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
     */
    
    @Override
    public void onLongPress(MotionEvent e)
    {
        Logger.d(TAG, "onLongPress");
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param e1 MotionEvent
     * @param e2 MotionEvent
     * @param velocityX velocityX
     * @param velocityY velocityY
     * @return boolean
     * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent,
     *      android.view.MotionEvent, float, float)
     */
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY)
        { // 向左滑动
            if (currTitlePos == emotion)
            {
                mViewFlipper1.setInAnimation(context, R.anim.right_in);
                mViewFlipper1.setOutAnimation(context, R.anim.left_out);
                mViewFlipper1.showNext();
                initFlipperIndex(mViewFlipper1);
            }
            else if (currTitlePos == picture)
            {
                if (currTabPos == 0)
                {
                    mViewFlipper2.setInAnimation(context, R.anim.right_in);
                    mViewFlipper2.setOutAnimation(context, R.anim.left_out);
                    mViewFlipper2.showNext();
                    initFlipperIndex(mViewFlipper2);
                }
                else
                {
                    mViewFlipper3.setInAnimation(context, R.anim.right_in);
                    mViewFlipper3.setOutAnimation(context, R.anim.left_out);
                    mViewFlipper3.showNext();
                    initFlipperIndex(mViewFlipper3);
                }
            }
            else
            {
                mViewFlipper4.setInAnimation(context, R.anim.right_in);
                mViewFlipper4.setOutAnimation(context, R.anim.left_out);
                mViewFlipper4.showNext();
                initFlipperIndex(mViewFlipper4);
            }
        }
        else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY)
        { // 向右滑动
            if (currTitlePos == emotion)
            {
                mViewFlipper1.setInAnimation(context, R.anim.left_in);
                mViewFlipper1.setOutAnimation(context, R.anim.right_out);
                mViewFlipper1.showPrevious();
                initFlipperIndex(mViewFlipper1);
            }
            else if (currTitlePos == picture)
            {
                if (currTabPos == 0)
                {
                    mViewFlipper2.setInAnimation(context, R.anim.left_in);
                    mViewFlipper2.setOutAnimation(context, R.anim.right_out);
                    mViewFlipper2.showPrevious();
                    initFlipperIndex(mViewFlipper2);
                }
                else
                {
                    mViewFlipper3.setInAnimation(context, R.anim.left_in);
                    mViewFlipper3.setOutAnimation(context, R.anim.right_out);
                    mViewFlipper3.showPrevious();
                    initFlipperIndex(mViewFlipper3);
                }
            }
            else
            {
                mViewFlipper4.setInAnimation(context, R.anim.left_in);
                mViewFlipper4.setOutAnimation(context, R.anim.right_out);
                mViewFlipper4.showPrevious();
                initFlipperIndex(mViewFlipper4);
            }
        }
        else
        {
            return false;
        }
        return true;
    }
    
    /**
     * 重写onTouch<BR>
     * @param v View
     * @param event MotionEvent
     * @return boolean
     * @see android.view.View.OnTouchListener#onTouch(android.view.View,
     *      android.view.MotionEvent)
     */
    
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return this.mGestureDetector.onTouchEvent(event);
    }

    /**
     * 长按事件监听<BR>
     * @param parent parent
     * @param view view
     * @param position position
     * @param id id
     * @return boolean
     * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
     */
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id)
    {
        return true;
    }
    
}
