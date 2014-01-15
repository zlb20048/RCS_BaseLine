/*
 * 文件名: SetSystemHeadActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 
 * 创建人: tjzhang
 * 创建时间:2012-3-28
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SetSystemHeadAction;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.SystemFacesUtil;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-28] 
 */
public class SetSystemHeadActivity extends BasicActivity
{
    /**
     * gridview放置头像
     */
    private GridView mGridView;
    
    private HeadAdapter mHeadAdapter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_head);
        initView();
        
        int headType = getIntent().getIntExtra(SetSystemHeadAction.EXTRA_MODE,
                SetSystemHeadAction.MODE_PERSON);
        mHeadAdapter = new HeadAdapter(this, headType);
        mGridView.setAdapter(mHeadAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                Entry<String, Integer> entry = (Entry<String, Integer>) mHeadAdapter.getItem(position);
                
                Intent intent = new Intent();
                intent.putExtra(SetSystemHeadAction.EXTRA_HEAD_URL,
                        entry.getKey());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    
    /**
     * 
     * 初始化界面<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        findViewById(R.id.left_button).setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.system_head);
        mGridView = (GridView) findViewById(R.id.gradview);
    }
    
    /**
     * 
     * 放置头像的Adapter [功能详细描述]
     * 
     * @author
     * @version [ME MTVClient_Handset V100R001C04SPC002, 2011-11-22]
     */
    public static class HeadAdapter extends BaseAdapter
    {
        /**
         * Context
         */
        private Context mContext;
        
        private int headType;
        
        private List<Map.Entry<String, Integer>> mList;
        
        /**
         * 实例化当前对象 [构造简要说明]
         * 
         * @param context Context对象
         * @param type 系统头像类型
         */
        public HeadAdapter(Context context, int type)
        {
            mContext = context;
            headType = type;
            mList = SystemFacesUtil.getList(headType);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int getCount()
        {
            return null != mList ? mList.size() : 0;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return mList.get(position);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View currentView, ViewGroup viewGroup)
        {
            ImageView imageView = null;
            if (currentView == null)
            {
                // 给ImageView设置资源
                imageView = new ImageView(mContext);
                // 设置布局 图片显示
                imageView.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.FILL_PARENT,
                        GridView.LayoutParams.FILL_PARENT));
                // 设置显示比例类型
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                currentView = imageView;
            }
            else
            {
                imageView = (ImageView) currentView;
            }
            imageView.setImageResource(mList.get(position).getValue());
            return imageView;
        }
    }
}
