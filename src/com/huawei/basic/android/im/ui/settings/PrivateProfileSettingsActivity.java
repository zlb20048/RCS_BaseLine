/*
 * 文件名: PrivateProfileSettingsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhoumi
 * 创建时间:2012-4-6
 * 
 * 修改人：hegai
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 个人设置界面
 * @author zhoumi
 * @version [[RCS Client_Handset V100R001C04SPC002, Feb 14, 2012]
 */
public class PrivateProfileSettingsActivity extends BasicActivity implements
        View.OnClickListener
{
    /**
     * TAG
     */
    private static final String TAG = "PrivateProfileSettingsActivity";
    
    /**
     * 个人资料 昵称
     */
    private static final int NICKNAME = 0x00000001;
    
    /**
     * 个人资料 公司
     */
    private static final int COMPANY = 0x00000008;
    
    /**
     * 个人资料 学校
     */
    private static final int SCHOOL = 0x00000012;
    
    /**
     * 选取地区
     */
    private static final int REGION = 6;
    
    /**
     * 防止频繁点击屏幕
     */
    private static final int TOUCH_SCREEN_DELAY = 1000;
    
    /**
     * 个人信息对象
     */
    private ContactInfoModel mUser;
    
    /**
     * 联系人对象封装后的crcValue
     */
    private String mBeginCrcValue;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private_profile_new);
        findView();
        initMyProfileInfo();
    }
    
    /**
     * 初始化个人资料界面
     */
    private void initMyProfileInfo()
    {
        // 若从设置界面进入
        if (getIntent().getBooleanExtra(SettingsAction.FLAG_FROM_SET, false))
        {
            mUser = (ContactInfoModel) getIntent().getSerializableExtra(SettingsAction.FLAG_USER_PROFILE);
        }
        else
        {
            //查询数据库
            mUser = mSettingsLogic.queryMyProfile(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId());
        }
        if (null != mUser)
        {
            mBeginCrcValue = mSettingsLogic.getCrcValue(mUser);
            setData();
        }
    }
    
    /**
     * 初始化组件
     */
    private void findView()
    {
        findViewById(R.id.left_button).setOnClickListener(this);
        Button rightButton = (Button) findViewById(R.id.right_button);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setText(R.string.default_save);
        rightButton.setOnClickListener(this);
        findViewById(R.id.set_sex).setOnClickListener(this);
        findViewById(R.id.set_school).setOnClickListener(this);
        findViewById(R.id.set_company).setOnClickListener(this);
        findViewById(R.id.set_country).setOnClickListener(this);
        findViewById(R.id.set_nick_layout).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.private_profile);
    }
    
    /**
     * 设置个人信息
     */
    private void setData()
    {
        setContent(NICKNAME);
        setGenderStr();
        setCountry();
        setContent(COMPANY);
        setContent(SCHOOL);
    }
    
    /**
     * 设置性别
     */
    private void setGenderStr()
    {
        String genderStr = null;
        switch (mUser.getGender())
        {
            case 1:
                genderStr = getString(R.string.female);
                break;
            case 2:
                genderStr = getString(R.string.male);
                break;
            case 0:
            default:
                genderStr = getString(R.string.not_fill_in);
                break;
        }
        setText((TextView) findViewById(R.id.set_sex_content), genderStr);
    }
    
    /**
     * 设置组件文字显示
     * @param text text
     * @param content content
     */
    private void setText(TextView text, String content)
    {
        if (!StringUtil.isNullOrEmpty(content))
        {
            content = content.trim();
            text.setText(content);
        }
        else
        {
            text.setText(R.string.not_fill_in);
        }
    }
    
    /**
     * 
     *设置昵称等
     * @param id id
     */
    private void setContent(int id)
    {
        switch (id)
        {
            case NICKNAME:
                setText((TextView) findViewById(R.id.set_nickname_content),
                        mUser.getNickName());
                break;
            case COMPANY:
                setText((TextView) findViewById(R.id.set_company_content),
                        mUser.getCompany());
                break;
            case SCHOOL:
                setText((TextView) findViewById(R.id.set_school_content),
                        mUser.getSchool());
                break;
            default:
                break;
        }
    }
    
    /**
     * 设置地区
     */
    private void setCountry()
    {
        String countryStr = mUser.getCountry();
        String provinceStr = mUser.getProvince();
        String cityStr = mUser.getCity();
        if (StringUtil.isNullOrEmpty(provinceStr)
                && StringUtil.isNullOrEmpty(cityStr))
        {
            if (!StringUtil.isNullOrEmpty(countryStr)
                    && countryStr.equalsIgnoreCase("china"))
            {
                countryStr = getResources().getString(R.string.setting_CHINA);
                setText((TextView) findViewById(R.id.set_country_content),
                        countryStr);
            }
            else if (!StringUtil.isNullOrEmpty(countryStr))
            {
                setText((TextView) findViewById(R.id.set_country_content),
                        countryStr);
            }
            else if (StringUtil.isNullOrEmpty(countryStr))
            {
                setText((TextView) findViewById(R.id.set_country_content), "");
            }
        }
        else if (StringUtil.isNullOrEmpty(provinceStr)
                && !StringUtil.isNullOrEmpty(cityStr))
        {
            setText((TextView) findViewById(R.id.set_country_content), cityStr);
        }
        else if (!StringUtil.isNullOrEmpty(provinceStr)
                && StringUtil.isNullOrEmpty(cityStr))
        {
            setText((TextView) findViewById(R.id.set_country_content),
                    provinceStr);
        }
        else
        {
            setText((TextView) findViewById(R.id.set_country_content),
                    provinceStr + " " + cityStr);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void finish()
    {
        if (StringUtil.isNullOrEmpty(mUser.getNickName()))
        {
            showToast(R.string.setting_name_not_null);
        }
        else
        {
            updataMyprofile();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        long lastTouchTime = 0L;
        long now = System.currentTimeMillis();
        //控制不能频繁点击屏幕
        if ((now - lastTouchTime) < TOUCH_SCREEN_DELAY)
        {
            return;
        }
        lastTouchTime = now;
        switch (v.getId())
        {
        // 返回按钮
            case R.id.left_button:
                super.finish();
                break;
            
            //保存按钮
            case R.id.right_button:
                finish();
                break;
            
            // 选择性别
            case R.id.set_sex:
                String[] sexStr = new String[] {
                        getResources().getString(R.string.sex_two),
                        getResources().getString(R.string.sex_one),
                        getResources().getString(R.string.secret) };
                showSingleChoiceDialog(R.string.set_gender,
                        sexStr,
                        2 - mUser.getGender(),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                mUser.setGender(2 - which);
                                setGenderStr();
                                dialog.dismiss();
                            }
                        });
                break;
            
            // 设置学校
            case R.id.set_school:
                inputSchool(mUser.getSchool());
                break;
            
            // 设置国家
            case R.id.set_country:
                showSetCountry();
                break;
                
            // 设置公司
            case R.id.set_company:
                showSetCompany();
                break;
            
            // 设置姓名
            case R.id.set_nick_layout:
                showSetNickname();
                break;
            default:
                break;
        }
    }
    
    /**
     * 更改昵称
     */
    private void showSetNickname()
    {
        final LimitedEditText editSign = new LimitedEditText(this);
        openInputWindow();
        editSign.setText(mUser.getNickName());
        editSign.setMaxCharLength(20);
        showTextEditDialog(R.string.set_nick_name,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mUser.setNickName(editSign.getText().toString());
                        setContent(NICKNAME);
                    }
                },
                editSign);
    }
    
    /**
     *更改地区
     */
    private void showSetCountry()
    {
        Intent intent = new Intent();
        intent.setAction(SettingsAction.ACTION_ACTIVITY_REGION_LIST);
        startActivityForResult(intent, REGION);
    }
    
    /**
     * 更改公司
     */
    private void showSetCompany()
    {
        final LimitedEditText editSign = new LimitedEditText(this);
        openInputWindow();
        editSign.setMaxCharLength(40);
        editSign.setText(mUser.getCompany());
        showTextEditDialog(R.string.write_company,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mUser.setCompany(editSign.getText().toString());
                        setContent(COMPANY);
                    }
                },
                editSign);
    }
    
    /**
     * 更改学校
     * @param string  学校
     */
    private void inputSchool(String string)
    {
        final LimitedEditText editText = new LimitedEditText(this);
        openInputWindow();
        editText.setHint(R.string.set_school_name);
        editText.setMaxCharLength(40);
        editText.setText(string);
        editText.selectAll();
        showTextEditDialog(R.string.set_school,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        final String text = editText.getText().toString();
                        mUser.setSchool(text);
                        setContent(SCHOOL);
                    }
                },
                editText);
    }
    
    /**
     * 处理Activity跳转回来的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REGION:
                if (resultCode != RESULT_CANCELED)
                {
                    String countryStr = data.getStringExtra(AllRegionsExpandableListActivity.COUNTRY);
                    String provinceStr = data.getStringExtra(AllRegionsExpandableListActivity.PROVINCE);
                    String cityStr = data.getStringExtra(AllRegionsExpandableListActivity.CITY);
                    if (null == countryStr)
                    {
                        countryStr = "";
                    }
                    if (null == provinceStr)
                    {
                        provinceStr = "";
                    }
                    if (null == cityStr)
                    {
                        cityStr = "";
                    }
                    Logger.d(TAG, "=====>>>" + countryStr + provinceStr
                            + cityStr);
                    mUser.setCountry(countryStr);
                    mUser.setProvince(provinceStr);
                    mUser.setCity(cityStr);
                    setCountry();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 更新个人资料
     */
    private void updataMyprofile()
    {
        String endCrcValue = mSettingsLogic.getCrcValue(mUser);
        if (!endCrcValue.equals(mBeginCrcValue))
        {
            if (checkNet())
            {
                //保存个人资料进度
                showProgressDialog(getString(R.string.setting_person_info_save_dialog));
                //向服务器更新个人资料
                mSettingsLogic.sendUpdatePrivateProfile(mUser);
            }
            else
            {
                super.finish();
            }
        }
        else
        {
            super.finish();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_SUCCEED:
            case SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_FAILED:
            case SettingsMessageType.CONNECT_FAILED:
                closeProgressDialog();
                super.finish();
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 打开软键盘
     */
    private void openInputWindow()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                InputMethodManager imm = (InputMethodManager) PrivateProfileSettingsActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        },
                500);
    }
}
