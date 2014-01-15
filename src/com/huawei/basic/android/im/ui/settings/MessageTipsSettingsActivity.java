/*
 * 文件名: MessageTipsSettingsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: meiyue
 * 创建时间:2012-2-27
 * 
 * 修改人：hegai
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 消息提示设置界面
 * @author meiyue
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class MessageTipsSettingsActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 声音提示设置
     */
    private ImageView mSetVoiceCtv;
    
    /**
     * 声音提示设置的策略结果(0:开启；1:关闭)
     */
    private String mVoiceResult;
    
    /**
     * 逻辑对象
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 是否开启
     */
    private boolean mIsOn;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_message_tips);
        initView();
        setViewValues();
    }
    
    /**
     * 初始化组件
     */
    private void initView()
    {
        findViewById(R.id.left_button).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.general);
        
        RelativeLayout mGroupMessageLayout = (RelativeLayout) findViewById(R.id.setting_group_message);
        mGroupMessageLayout.setOnClickListener(this);
        
        mSetVoiceCtv = (ImageView) findViewById(R.id.setting_voice_ctv);
        mSetVoiceCtv.setVisibility(View.VISIBLE);
        mSetVoiceCtv.setOnClickListener(this);
    }
    
    /**
     * 查询数据库，设置mSetVoiceCtv声音提示状态
     */
    private void setViewValues()
    {
        UserConfigModel config = mSettingsLogic.configQueryByKey(UserConfigModel.VOICE_TIPS);
        //如果config不为空，mVoiceResult赋值为config.getValue()，否则为UserConfigModel.OPEN_VOICE
        mVoiceResult = null != config ? config.getValue()
                : UserConfigModel.OPEN_VOICE;
        if ((UserConfigModel.CLOSE_VOICE).equals(mVoiceResult))
        {
            mIsOn = false;
            mSetVoiceCtv.setBackgroundResource(R.drawable.button_switch_off);
        }
        else
        {
            mIsOn = true;
            mSetVoiceCtv.setBackgroundResource(R.drawable.button_switch_on);
        }
        
    }
    
    /**
     * 处理点击事件
     * @param v 点击视图
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            //声音提示设置
            case R.id.setting_voice_ctv:
                mIsOn = !mIsOn;
                if (mIsOn)
                {
                    mSetVoiceCtv.setBackgroundResource(R.drawable.button_switch_on);
                }
                else
                {
                    mSetVoiceCtv.setBackgroundResource(R.drawable.button_switch_off);
                }
                break;
            //群消息策略设置
            case R.id.setting_group_message:
                startActivity(new Intent(
                        SettingsAction.ACTION_ACTIVITY_GROUPMESSAGEPOLICY));
                break;
            default:
                break;
        }
    }
    
    /**
     * 是否屏蔽返回键
     * @param keyCode  键盘码
     * @param event 键盘事件
     * @return 是否屏蔽返回键
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event)
    //    {
    //        if (keyCode == KeyEvent.KEYCODE_BACK)
    //        {
    //            finish();
    //            return true;
    //        }
    //        return super.onKeyDown(keyCode, event);
    //    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * 保存声音提示状态到数据库中
     */
    @Override
    public void finish()
    {
        String userId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        mVoiceResult = mIsOn ? UserConfigModel.OPEN_VOICE
                : UserConfigModel.CLOSE_VOICE;
        mSettingsLogic.addConfig(userId,
                UserConfigModel.VOICE_TIPS,
                mVoiceResult);
        super.finish();
    }
}
