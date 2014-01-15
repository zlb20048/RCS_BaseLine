package com.huawei.basic.android.im.ui.friend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 
 * 查看附近的人跳转Activity<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 23, 2012]
 */
public class CheckAroundActivity extends BasicActivity implements
        OnClickListener
{
    private Context mContext;
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState Bundle
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_check_around);
        mContext = CheckAroundActivity.this;
        initView();
    }
    
    private void initView()
    {
        TextView titleTxt = (TextView) findViewById(R.id.title);
        titleTxt.setText(R.string.find_by_location_title);
        
        findViewById(R.id.left_button).setOnClickListener(this);
        findViewById(R.id.check_button).setOnClickListener(this);
    }
    
    /**
     * 
     * 点击事件<BR>
     * [功能详细描述]
     * @param v View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.check_button:
                showTipsDialog();
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 
     * 获取位置信息提示对话框<BR>
     * [功能详细描述]
     */
    private void showTipsDialog()
    {
        if (mContext.getSharedPreferences(FusionCode.Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE)
                .getBoolean(FusionCode.Common.SHARE_UPLOAD_LOCATION, true))
        {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.friend_location_tips_dialog,
                    null);
            final CheckBox showTips = (CheckBox) dialogView.findViewById(R.id.showTips);
            showTips.setChecked(false);
            
            showViewDialog(R.string.prompt,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (showTips.isChecked())
                            {
                                SharedPreferences.Editor editor = mContext.getSharedPreferences(FusionCode.Common.SHARED_PREFERENCE_NAME,
                                        Context.MODE_PRIVATE)
                                        .edit();
                                editor.putBoolean(FusionCode.Common.SHARE_UPLOAD_LOCATION,
                                        false);
                                editor.commit();
                            }
                            redirectToResultActivty();
                        }
                    },
                    dialogView);
        }
        else
        {
            redirectToResultActivty();
        }
    }
    
    /**
     * 
     * 跳转到搜索结果页面<BR>
     */
    private void redirectToResultActivty()
    {
        Intent intent = new Intent();
        intent.setAction(FusionAction.FindFriendResultListAction.ACTION);
        intent.putExtra(FusionAction.FindFriendResultListAction.EXTRA_MODE,
                FusionAction.FindFriendResultListAction.MODE.MODE_FIND_NEAR);
        startActivity(intent);
        finish();
    }
}
