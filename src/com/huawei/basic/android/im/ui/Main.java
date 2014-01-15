/*
 * 文件名: Main.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.LoginMainAction;
import com.huawei.basic.android.im.common.FusionAction.MainTabction;
import com.huawei.basic.android.im.common.FusionMessageType.Base;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.adapter.http.UpdateManager;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.logic.model.CheckUpdateInfoModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.DecodeUtil;

/**
 * 系统的初始化类<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 11, 2012]
 */
public class Main extends BasicActivity
{
    
    /**
     * TAG
     */
    public static final String TAG = "MainTabActivity";
    
    private String mStatus = "1";
    
    private String mForce;
    
    private String mUpdateUrl;
    
    private static final int Finish_GETTING_PACKAGENAME = 100;
    
    private static final int UPDATE_FILED = 101;
    
    private String packageUrl;
    
    /**
     * 登录模块的业务逻辑处理对象
     */
    private ILoginLogic mLoginLogic;
    
    private boolean autoLogin;
    
    /**
     * 是否点击back键
     */
    private boolean isBack;
    
    /**
     * 
     * activity生命周期入口
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.framework.ui.LauncheActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        autoLogin = hasLogined();
        super.onCreate(savedInstanceState);
        if (hasLogined())
        {
            autoLogin = true;
            startActivity(new Intent(MainTabction.ACTION));
            finish();
            return;
        }
        setContentView(R.layout.main);
        
        //检测软件更新
        try
        {
            //获取手机widthPixels 和 heightPixels
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            
            Logger.i(TAG,
                    "********************************** versionName"
                            + getPackageManager().getPackageInfo(this.getPackageName(),
                                    0).versionName);
            //发送验证信息
            new UpdateManager().requestCheckUpdate(getPackageManager().getPackageInfo(this.getPackageName(),
                    0).versionName,
                    String.valueOf(dm.widthPixels),
                    String.valueOf(dm.heightPixels),
                    new IHttpListener()
                    {
                        
                        @Override
                        public void onResult(int action, Response response)
                        {
                            
                            if (null != response
                                    && response.getResponseCode() == Response.ResponseCode.Succeed)
                            {
                                if (null != response.getObj()
                                        && response.getObj() instanceof CheckUpdateInfoModel)
                                {
                                    CheckUpdateInfoModel model = (CheckUpdateInfoModel) response.getObj();
                                    if (CheckUpdateInfoModel.UpdateStatus.HAVE_NEW_VERSION.equals(model.getStatus())
                                            || CheckUpdateInfoModel.UpdateStatus.FORCE_UPDATE.equals(model.getStatus()))
                                    {
                                        mStatus = model.getStatus();
                                        mForce = model.getForceupdate();
                                        mUpdateUrl = model.getUrl();
                                    }
                                }
                            }
                            else
                            {
                                
                                Logger.w(TAG, "Checkupdate fail");
                            }
                            
                            //发送升级消息给页面
                            getHandler().sendEmptyMessage(Base.MSGTYPE_TIMER_UPDATE);
                        }
                        
                        @Override
                        public void onProgress(boolean isInProgress)
                        {
                        }
                    });
        }
        catch (NameNotFoundException e)
        {
            Logger.e(TAG, " update failed because can not read versionName ", e);
        }
    }
    
    /**
     * 初始化logic方法<BR>
     * 重载该方法获取logic对象
     * 
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics(android.content.Context)
     */
    @Override
    protected void initLogics()
    {
        mLoginLogic = (ILoginLogic) getLogicByInterfaceClass(ILoginLogic.class);
    }
    
    /**
     * 返回一个boolean表示展示该页面是否需要登录成功
     * 
     * @return boolean 是否是登录后的页面
     */
    @Override
    protected boolean needLogin()
    {
        return autoLogin;
    }
    
    /**
     * Handle 消息信息处理
     * 
     * @param msg
     *            msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        if (isBack)
        {
            finish();
            return;
        }
        int what = msg.what;
        Intent intent;
        switch (what)
        {
            case Base.MSGTYPE_TIMER_UPDATE:
                //如果有新版本或是强制升级版本，弹出对话框
                if (CheckUpdateInfoModel.UpdateStatus.HAVE_NEW_VERSION.equals(mStatus)
                        || CheckUpdateInfoModel.UpdateStatus.FORCE_UPDATE.equals(mStatus))
                {
                    promptUpdata(CheckUpdateInfoModel.UpdateStatus.FORCE_UPDATE.equals(mStatus)
                            || CheckUpdateInfoModel.ForceUpdate.DO_UPDATE.equals(mForce),
                            mUpdateUrl);
                }
                else
                {
                    Logger.i(TAG, "No need to update. Update status=" + mStatus);
                    login();
                }
                break;
            
            case Finish_GETTING_PACKAGENAME:
                //已经获取包名称
                openDownloadPackWindow(packageUrl);
                break;
            case UPDATE_FILED:
                //获取更新消息失败
                showToast(R.string.resultcode_not_null);
                login();
                break;
            case Base.MSGTYPE_TIMER_LOGIN:
                // 跳转登陆界面
                intent = new Intent(LoginAction.ACTION);
                startActivity(intent);
                finish();
                break;
            case Base.MSGTYPE_TIMER_LOGIN_MAIN:
                
                // 跳转首次登陆界面
                intent = new Intent(LoginMainAction.ACTION);
                startActivity(intent);
                finish();
                break;
            
            case Base.MSGTYPE_TIMER_MAINTAB:
                //                AccountModel mAccountModel = mLoginLogic.getLastLoginAccountModel();
                //                mLoginLogic.login(mAccountModel.getLoginAccount(),
                //                        DecodeUtil.decrypt(mAccountModel.getLoginAccount(),
                //                                mAccountModel.getPassword()));
                //                break;
                //            case LoginMessageType.LOGIN_SUCCESS:
                // 跳转主界面
                intent = new Intent(MainTabction.ACTION);
                startActivity(intent);
                finish();
                break;
            //            //登录失败进入登录页面
            //            case LoginMessageType.LOGIN_ERROR:
            //                intent = new Intent(LoginMainAction.ACTION);
            //                startActivity(intent);
            //                finish();
            //                break;
            default:
                break;
        }
        
    }
    
    /**
     * 升级检测弹出的对话框
     * @param forceUpdate 是否强制升级
     * @param updateUrl 升级的url
     */
    private void promptUpdata(final boolean forceUpdate, final String updateUrl)
    {
        int info = R.string.update_info;
        int rightButton = R.string.update_skip;
        
        //如果为强制升级，则替换消息信息和右键信息
        if (forceUpdate)
        {
            info = R.string.update_info_force;
            rightButton = R.string.update_exit;
        }
        try
        {
            showMessageDialog(R.string.update_title,
                    R.drawable.icon_warning,
                    info,
                    R.string.update_confirm,
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            new UpdateManager().downloadAndParseFilelist(updateUrl,
                                    new IHttpListener()
                                    {
                                        
                                        @Override
                                        public void onResult(int action,
                                                Response response)
                                        {
                                            if (null != response
                                                    && response.getResponseCode() == Response.ResponseCode.Succeed
                                                    && null != response.getData())
                                            {
                                                //获取数据类型                                 
                                                CheckUpdateInfoModel model = (CheckUpdateInfoModel) response.getObj();
                                                
                                                packageUrl = mUpdateUrl
                                                        + CheckUpdateInfoModel.MID_URL
                                                        + model.getFilelist()
                                                                .getDpath();
                                                Logger.d(TAG,
                                                        "Update package's url is "
                                                                + packageUrl);
                                                //发送已获得安装包名称消息给页面
                                                getHandler().sendEmptyMessage(Finish_GETTING_PACKAGENAME);
                                            }
                                            else
                                            {
                                                //发送下载失败消息给页面
                                                getHandler().sendEmptyMessage(UPDATE_FILED);
                                            }
                                        }
                                        
                                        @Override
                                        public void onProgress(
                                                boolean isInProgress)
                                        {
                                        }
                                    });
                        }
                    },
                    rightButton,
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (forceUpdate)
                            {
                                //如果为强制更新，点击右键后直接退出程序
                                finish();
                            }
                            else
                            {
                                //点击右键，继续登录
                                login();
                                finish();
                            }
                        }
                    });
            
        }
        catch (Exception e)
        {
            Logger.e(TAG, "There is exception in method promptUpdata.", e);
        }
    }
    
    /**
     * 
     * 点击back键，不执行handle
     * @param keyCode keyCode
     * @param event event
     * @return 点击事件
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            isBack = true;
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void login()
    {
        AccountModel accountModel = mLoginLogic.getLastLoginAccountModel(this);
        if (null != accountModel)
        {
            if (null != accountModel.getPassword())
            {
                setLogined(true);
                autoLogin = true;
                // 主界面延时2秒跳转回话页面
                getHandler().sendEmptyMessageDelayed(Base.MSGTYPE_TIMER_MAINTAB,
                        2000);
                mLoginLogic.login(accountModel.getLoginAccount(),
                        DecodeUtil.decrypt(accountModel.getLoginAccount(),
                                accountModel.getPassword()));
            }
            else
            {
                autoLogin = false;
                // 主界面延时2秒跳转登陆界面
                getHandler().sendEmptyMessageDelayed(Base.MSGTYPE_TIMER_LOGIN,
                        100);
            }
        }
        else
        {
            autoLogin = false;
            getHandler().sendEmptyMessageDelayed(Base.MSGTYPE_TIMER_LOGIN_MAIN,
                    100);
        }
    }
    
    private void openDownloadPackWindow(String updateUrl)
    {
        //直接跳转到url界面
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        startActivity(intent);
        finish();
    }
}
