/*
 * 文件名: SettingsActivity.java
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

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.common.FusionAction.SetHeadUtilAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.model.CheckUpdateInfoModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 更多界面
 * @author gaihe
 * @version [RCS Client V100R001C03, 2012-4-19]
 */
public class SettingsActivity extends BasicActivity implements OnClickListener
{
    /**
     * 版本更新成功
     */
    private static final int UPDATE_SUCCESSFUL = 0;
    
    /**
     * 系统错误
     */
    private static final int SYSTEM_ERROR = -1;
    
    /**
     * 无更新
     */
    private static final int NONE_UPDATE = 1;
    
    /**
     * 服务器忙
     */
    private static final int SERVER_BUSY = 2;
    
    /**
     * 强制更新
     */
    private static final int FORCE_UPDATE = 3;
    
    /**
     * 设置个性签名requestCode
     */
    private static final int SET_SIGNATURE = 1;
    
    /**
     * 系统头像
     */
    //    private static final int HEAD_IMAGE = 2;
    
    /**
     * 相册头像
     */
    private static final int HEAD_PHOTO = 3;
    
    /**
     * 拍照标识
     */
    private static final int TAKE_PHOTO = 4;
    
    /**
     * 个人头像显示
     */
    private ImageView mPersonImageView;
    
    /**
     * 个人ID显示
     */
    private TextView mIdTextView;
    
    /**
     * 个人昵称显示
     */
    private TextView mNameTextView;
    
    /**
     * 个人性别显示
     */
    private ImageView mSexImage;
    
    /**
     * 个性签名显示
     */
    private TextView mSignatureTextView;
    
    /**
     * 个人信息对象
     */
    private ContactInfoModel mUser;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 登录逻辑接口
     */
    private ILoginLogic mLoginLogic;
    
    /**
     * Voip逻辑接口
     */
    private IVoipLogic mVoipLogic;
    
    /**
     * 头像设置弹出框
     */
    private PopupWindow mPopupWindow;
    
    /**
     * 头像设置弹出框内部View
     */
    private View mPopView;
    
    /**
     * 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initView();
        mSettingsLogic.sendRequestPrivateProfile(null, false);
    }
    
    /**
     * 
     * 初始化组件<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        findViewById(R.id.left_button).setVisibility(View.INVISIBLE);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.more);
        mPersonImageView = (ImageView) findViewById(R.id.smain_iv_person);
        mPersonImageView.setOnClickListener(this);
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        mNameTextView = (TextView) findViewById(R.id.smain_tv_name);
        mSexImage = (ImageView) findViewById(R.id.sex_image);
        mIdTextView = (TextView) findViewById(R.id.smain_tv_id);
        mSignatureTextView = (TextView) findViewById(R.id.smain_signature_content);
        findViewById(R.id.smain_person).setOnClickListener(this);
        findViewById(R.id.smain_signature).setOnClickListener(this);
        findViewById(R.id.smain_my_account).setOnClickListener(this);
        findViewById(R.id.smain_system_plugins).setOnClickListener(this);
        findViewById(R.id.smain_applications_list).setOnClickListener(this);
        findViewById(R.id.smain_general).setOnClickListener(this);
        findViewById(R.id.smain_privacy).setOnClickListener(this);
        findViewById(R.id.smain_block).setOnClickListener(this);
        findViewById(R.id.smain_flow_rate).setOnClickListener(this);
        findViewById(R.id.set_password).setOnClickListener(this);
        findViewById(R.id.check_update).setOnClickListener(this);
        findViewById(R.id.about_hitalk).setOnClickListener(this);
        findViewById(R.id.clear_data).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        LayoutInflater inflater = getLayoutInflater();
        mPopView = inflater.inflate(R.layout.popwindow, null);
        //        popView.findViewById(R.id.frist_btn).setOnClickListener(this);
        mPopView.findViewById(R.id.second_btn).setOnClickListener(this);
        mPopView.findViewById(R.id.third_btn).setOnClickListener(this);
        mPopView.findViewById(R.id.forth_btn).setOnClickListener(this);
        mPopView.setOnClickListener(this);
        mPopupWindow = new PopupWindow(mPopView, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
    }
    
    /**
     *获取个人头像
     */
    private void getHead()
    {
        if (null != mUser.getFaceBytes())
        {
            ImageUtil.showFace(mPersonImageView,
                    mUser.getFaceUrl(),
                    mUser.getFaceBytes(),
                    R.drawable.default_contact_icon,
                    mPersonImageView.getWidth(),
                    mPersonImageView.getHeight());
        }
        else
        {
            mPhotoLoader.loadPhoto(mPersonImageView, mUser.getFaceUrl());
        }
    }
    
    /**
     * 处理点击事件
     * @param v 点击视图
     */
    @Override
    public void onClick(View v)
    {
        if (null != mPopupWindow && mPopupWindow.isShowing())
        {
            mPopupWindow.dismiss();
        }
        Intent intent = new Intent();
        switch (v.getId())
        {
        // 头像按键
            case R.id.smain_iv_person:
                mPopupWindow.showAtLocation(mPopView, Gravity.BOTTOM, 0, 0);
                break;
            // 系统头像设置,跳转到系统头像选择界面
            //            case R.id.frist_btn:
            //                intent.setAction(SetHeadUtilAction.ACTION);
            //                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
            //                        SetHeadUtilAction.MODE_SYSTEM);
            //                startActivityForResult(intent, HEAD_IMAGE);
            //                break;
            case R.id.second_btn:
                // 相册选择头像
                intent.setAction(SetHeadUtilAction.ACTION);
                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
                        SetHeadUtilAction.MODE_FILE);
                startActivityForResult(intent, HEAD_PHOTO);
                break;
            // 拍照设置头像
            case R.id.third_btn:
                intent.setAction(SetHeadUtilAction.ACTION);
                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
                        SetHeadUtilAction.MODE_CAMERA);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.forth_btn:
                mPopupWindow.dismiss();
                break;
            
            // 编辑个人资料
            case R.id.smain_person:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING);
                intent.putExtra(SettingsAction.FLAG_FROM_SET, true);
                intent.putExtra(SettingsAction.FLAG_USER_PROFILE, mUser);
                //startActivityForResult(intent, SET_PROFILE);
                startActivity(intent);
                break;
            
            // 个性签名
            case R.id.smain_signature:
                intent.setAction(InputReasonAction.ACTION);
                intent.putExtra(InputReasonAction.EXTRA_MODE,
                        InputReasonAction.MODE_SIGNATURE);
                intent.putExtra(InputReasonAction.EXTRA_CONTENT,
                        mUser.getSignature());
                startActivityForResult(intent, SET_SIGNATURE);
                break;
            
            //我的账号
            case R.id.smain_my_account:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_MY_ACCOUNT);
                startActivity(intent);
                break;
            
            //系统插件
            case R.id.smain_system_plugins:
                intent.setAction(FusionAction.PluginListAction.ACTION);
                startActivity(intent);
                break;
            
            //应用列表
            case R.id.smain_applications_list:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_APPINFO);
                startActivity(intent);
                break;
            
            // 通用设置
            case R.id.smain_general:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_MESSAGE_TIP_SSETTINGS);
                startActivity(intent);
                break;
            
            // 隐私设置
            case R.id.smain_privacy:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_PRIVACY_SETTINGS);
                startActivity(intent);
                break;
            
            //黑名单
            case R.id.smain_block:
                // TODO 跳转到黑名单界面
                break;
            
            //流量统计
            case R.id.smain_flow_rate:
                // TODO 跳转到流量统计界面
                break;
            
            // 修改密码
            case R.id.set_password:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_MODIFY_PASSWORD);
                startActivity(intent);
                break;
            
            // 检查更新
            case R.id.check_update:
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                String version = FusionConfig.getInstance().getClientVersion();
                String width = String.valueOf(dm.widthPixels);
                String height = String.valueOf(dm.heightPixels);
                mSettingsLogic.sendRequestCheckUpdate(version, width, height);
                break;
            
            // 关于HiTalk
            case R.id.about_hitalk:
                intent.putExtra(FusionAction.SettingsAction.EXTRA_ABOUT_TYPE,
                        FusionAction.SettingsAction.EXTRA_VALUE_ABOUT);
                intent.setAction(SettingsAction.ACTION_ACTIVITY_ABOUT_FEEDBACK);
                startActivity(intent);
                break;
            
            // 清空聊天记录
            case R.id.clear_data:
                showConfirmDialog(R.string.clear_data_alert,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                mSettingsLogic.clearAllData();
                            }
                        });
                break;
            
            //注销登录
            case R.id.logout:
                showDialog(R.string.confirm_logout,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                quit();
                            }
                        });
                break;
            default:
                break;
        }
    }
    
    /**
     * 设置名字、性别、ID、个性签名
     */
    private void setData()
    {
        mNameTextView.setText(mUser.getNickName());
        setGender();
        mIdTextView.setText(getString(R.string.user_id)
                + mUser.getFriendUserId());
        setSignature();
    }
    
    /**
     * 设置个性签名
     */
    private void setSignature()
    {
        if (StringUtil.isNullOrEmpty(mUser.getSignature()))
        {
            setText(mSignatureTextView, getString(R.string.signature));
        }
        else
        {
            setText(mSignatureTextView, mUser.getSignature());
        }
    }
    
    /**
     *设置性别
     */
    private void setGender()
    {
        int gender = mUser.getGender();
        if (gender == 1)
        {
            mSexImage.setImageResource(R.drawable.setting_female);
        }
        else if (gender == 2)
        {
            mSexImage.setImageResource(R.drawable.setting_male);
        }
        else
        {
            mSexImage.setImageResource(R.drawable.setting_sex_none);
        }
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
     *PopupWindow弹出时屏蔽返回键
     */
    @Override
    public void onBackPressed()
    {
        //如果弹出框有显示，需要先关闭弹出框
        if (null != mPopupWindow && mPopupWindow.isShowing())
        {
            mPopupWindow.dismiss();
        }
        else
        {
            //切换到后台
            PackageManager pm = getPackageManager();
            ResolveInfo homeInfo = pm.resolveActivity(new Intent(
                    Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }
    
    /**
     *处理Activity跳转回来的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (RESULT_OK == resultCode && null != data)
        {
            if (SET_SIGNATURE == requestCode)
            {
                String text = (String) data.getSerializableExtra(InputReasonAction.OPERATE_RESULT);
                if (!text.equals(mUser.getSignature()))
                {
                    mUser.setSignature(text);
                    setText(mSignatureTextView, mUser.getSignature());
                    if (StringUtil.isNullOrEmpty(text))
                    {
                        setText(mSignatureTextView,
                                getString(R.string.signature));
                    }
                    mSettingsLogic.sendUpdateSignature(mUser);
                }
            }
            else
            {
                String url = data.getStringExtra(SetHeadUtilAction.EXTRA_URL);
                byte[] photoByte = data.getByteArrayExtra(SetHeadUtilAction.EXTRA_BYTES);
                mUser.setFaceUrl(url);
                mUser.setFaceBytes(photoByte);
                mSettingsLogic.sendUpdatePrivateProfile(mUser);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
        mLoginLogic = (ILoginLogic) getLogicByInterfaceClass(ILoginLogic.class);
        mVoipLogic = (IVoipLogic) getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case SettingsMessageType.GET_MYPROFILE_SUCCEED:
                mUser = (ContactInfoModel) msg.obj;
                if (null != mUser)
                {
                    getHead();
                    setData();
                }
                break;
            //相同的处理,case合并
            case SettingsMessageType.GET_MYPROFILE_FAILED:
            case SettingsMessageType.CONNECT_FAILED:
                String errorCode = msg.obj.toString();
                showToast(FusionErrorInfo.getErrorInfo(this, errorCode));
                break;
            case SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_SUCCEED:
                mUser = mSettingsLogic.queryMyProfile(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId());
                if (null != mUser)
                {
                    getHead();
                    setData();
                }
                showToast(R.string.setting_person_information_success);
                break;
            case SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_FAILED:
                showToast(R.string.setting_person_information_failure);
                break;
            //保存个人签名成功
            case SettingsMessageType.MSG_TYPE_UPDATE_SIGNATURE_SUCCEED:
                showToast(R.string.setting_person_information_success);
                break;
            //保存个人签名失败
            case SettingsMessageType.MSG_TYPE_UPDATE_SIGNATURE_FAILED:
                showToast(R.string.setting_person_information_failure);
                break;
            //版本更新成功
            case SettingsMessageType.MSG_TYPE_CHECK_UPDATE_VERSION_SUCCEED:
                handleCheckUpdate(msg);
                break;
            //版本更新失败
            case SettingsMessageType.MSG_TYPE_CHECK_UPDATE_VERSION_FAILED:
                showToast(R.string.resultcode_not_null);
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 点击MENU键退出
     */
    @Override
    protected boolean isNeedMenu()
    {
        return true;
    }
    
    /**
     * 处理更新版本服务器返回
     * @param msg 返回消息
     */
    private void handleCheckUpdate(Message msg)
    {
        CheckUpdateInfoModel checkUpdateModel = (CheckUpdateInfoModel) msg.obj;
        String status = checkUpdateModel.getStatus();
        String forceUpdate = checkUpdateModel.getForceupdate();
        String url = checkUpdateModel.getUrl();
        
        if (StringUtil.isNullOrEmpty(status) || !StringUtil.isNumeric(status))
        {
            showToast(R.string.state_exception);
            return;
        }
        
        // 强制下载apk的路径
        final String downloadUrl = url + "full/UIM_MOBILE.apk";
        // 0:成功,有新版本 1:无新版本 2:服务器忙 3:客户端强制升级 -1:系统错误
        // 不强制升级0--不强制升级
        
        switch (Integer.parseInt(status))
        {
            case UPDATE_SUCCESSFUL:
                if (StringUtil.isNullOrEmpty(forceUpdate)
                        || !StringUtil.isNumeric(forceUpdate))
                {
                    showToast(R.string.force_state_exception);
                    return;
                }
                
                if (Integer.parseInt(forceUpdate) == 0)
                {
                    
                    showDialog(R.string.version_new,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    Uri uri = Uri.parse(downloadUrl);
                                    Intent intent = new Intent(
                                            Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                }
                else
                {
                    // 强制升级 调用系统下载
                    showPromptDialog(getResources().getString(R.string.update_info_force),
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    // 退出客户端
                                    hideInputWindow(getCurrentFocus());
                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            mLoginLogic.logout();
                                        }
                                    }.start();
                                    new Thread()
                                    {
                                        public void run()
                                        {
                                            mVoipLogic.closeVoip(false);
                                            mVoipLogic.logout();
                                        }
                                    }.start();
                                    // 下载
                                    Uri uri = Uri.parse(downloadUrl);
                                    Intent intent1 = new Intent(
                                            Intent.ACTION_VIEW, uri);
                                    startActivity(intent1);
                                }
                            },
                            false);
                }
                break;
        //            case NONE_UPDATE:
        //                showDialog(R.string.version_latest, null);
        //                break;
        //            case SERVER_BUSY:
        //                showDialog(R.string.version_serverbusy, null);
        //                break;
        //            case FORCE_UPDATE:
        //                showDialog(R.string.version_clientforceupdate, null);
        //                break;
        //            case SYSTEM_ERROR:
        //                showDialog(R.string.system_error, null);
        //                break;
        //            default:
        //                showToast(R.string.type_exception);
        //                break;
        }
    }
    
    /**
     * 屏蔽MENU键
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (null != mPopupWindow && mPopupWindow.isShowing())
        {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU)
            {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
