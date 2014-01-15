/*
 * 文件名: AboutFeedbackActivtiy.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: meiyue
 * 创建时间:2012-2-27
 * 
 * 修改人：hegai
 * 修改时间:2012-5-24
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 基线关于界面
 * @author meiyue
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class AboutFeedbackActivtiy extends BasicActivity
{
    /**
     * 客服电话
     */
    private static final String TEL = "tel:01082276930";
    
    /**
     * 客服邮箱
     */
    private static final String EMAIL = "mailto:hitalk@rcs.com.cn";
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_about_feedback);
        initView();
    }
    
    /**
     * 初始化组件
     */
    private void initView()
    {
        TextView mTitle = (TextView) this.findViewById(R.id.title);
        mTitle.setText(R.string.about_hitalk);
        TextView mVersionTv = (TextView) this.findViewById(R.id.version_tv);
        mVersionTv.setText(FusionConfig.getInstance().getClientVersion());
        Button mReturnBtn = (Button) this.findViewById(R.id.left_button);
        //点击返回上一界面
        mReturnBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                AboutFeedbackActivtiy.this.finish();
            }
        });
        
        // 客服电话
        Button mIndexPhone = (Button) findViewById(R.id.index_phone);
        mIndexPhone.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Uri uri = Uri.parse(TEL);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                try
                {
                    AboutFeedbackActivtiy.this.startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        
        // 客服邮箱
        Button mBlogEmail = (Button) findViewById(R.id.blog_email);
        mBlogEmail.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Uri emailToUri = Uri.parse(EMAIL);
                Intent intent = new Intent(Intent.ACTION_SENDTO, emailToUri);
                try
                {
                    AboutFeedbackActivtiy.this.startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    //                    Toast.makeText(AboutFeedbackActivtiy.this,
                    //                            R.string.settings_feedback_no_email_client,
                    //                            Toast.LENGTH_LONG).show();
                    showToast(R.string.settings_feedback_no_email_client);
                }
            }
        });
    }
}
