/*
 * 文件名: BasicActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.util.Stack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.load.TaskManagerFactory;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.plugin.core.IPluginInit;
import com.huawei.basic.android.im.component.plugin.core.PluginManager;
import com.huawei.basic.android.im.component.plugin.plugins.apk.ApkInit;
import com.huawei.basic.android.im.component.plugin.plugins.internal.InternalInit;
import com.huawei.basic.android.im.framework.logic.BaseLogicBuilder;
import com.huawei.basic.android.im.framework.ui.LauncheActivity;
import com.huawei.basic.android.im.logic.LogicBuilder;
import com.huawei.basic.android.im.logic.login.ConfigManager;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.login.LoginLogic;
import com.huawei.basic.android.im.logic.login.receiver.ConnectionChangedReceiver;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.logic.voip.VoipLogic;
import com.huawei.basic.android.im.utils.GlobalExceptionHandler;

/**
 * UI 层基类Activity<BR>
 * 包含UI 层的公用弹出框之类
 * 
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, 2011-10-19]
 */
public abstract class BasicActivity extends LauncheActivity
{
    
    private static final String TAG = "BasicActivity";
    
    /**
     * 退出菜单
     */
    private static final int MENU_QUIT = 1;
    
    /**
     * 当前的activity的对象实例
     */
    public static BasicActivity currentActivtiy;
    
    /**
     * activity堆栈
     */
    private static Stack<Activity> mActivityStack = new Stack<Activity>();
    
    /**
     * 页面是否进入pause状态
     */
    private boolean isPaused;
    
    /**
     * 页面是否需要刷新
     */
    private boolean needUpdate;
    
    /**
     * 进度显示框
     */
    private ProgressDialog mProDialog;
    
    /**
     * 弹出对话框一个确定按钮
     */
    private BasicDialog mShowPromptDialog;
    
    /**
     * Toast对象
     */
    private Toast mToast;
    
    private IVoipLogic mVoipLogic;
    
    private LoginLogic mLoginLogic;
    
    /**
     * 
     * 启动
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (needLogin() && !hasLogined())
        {
            finish();
            return;
        }
        mActivityStack.add(this);
        mLoginLogic = (LoginLogic) super.getLogicByInterfaceClass(ILoginLogic.class);
        mVoipLogic = (VoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        isPaused = true;
        super.onPause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        if (!isPrivateHandler())
        {
            currentActivtiy = this;
        }
        super.onResume();
        isPaused = false;
        showLoginBar();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        //实现父类的抽象方法.子类就可以选择性实现此方法了
    }
    
    /**
     * 创建Logic建造管理类<BR>
     * 指定Logic建造管理类具体实例
     * 
     * @param context
     *            系统的context对象
     * @return Logic建造管理类具体实例
     */
    @Override
    protected BaseLogicBuilder createLogicBuilder(Context context)
    {
        BaseLogicBuilder builder = LogicBuilder.getInstance(context);
        LoginLogic loginLogic = (LoginLogic) builder.getLogicByInterfaceClass(ILoginLogic.class);
        loginLogic.addHandler(new Handler()
        {
            public void handleMessage(Message msg)
            {
                if (null != currentActivtiy)
                {
                    currentActivtiy.handleStatusMessage(msg);
                }
            };
        });
        if (hasLogined())
        {
            loginLogic.requestLoginMessage();
        }
        return builder;
    }
    
    /**
     * 返回一个boolean表示展示该页面是否需要登录成功
     * 
     * @return boolean 是否是登录后的页面
     */
    protected boolean needLogin()
    {
        return true;
    }
    
    private void handleStatusMessage(Message msg)
    {
        int messageType = msg.what;
        if (LoginMessageType.LOGIN_SUCCESS == messageType
                || LoginMessageType.STATUS_ONLINE == messageType)
        {
            mVoipLogic.autoLoginVoip();
        }
        if (needLogin())
        {
            FusionConfig.getInstance().setUserStatus(messageType);
            switch (messageType)
            {
                case LoginMessageType.LOGIN_ERROR:
                    quit();
                    showToast(FusionErrorInfo.getLoginErrorInfo(this,
                            (String) msg.obj));
                    break;
                case LoginMessageType.LOGIN:
                    mLoginLogic.reLogin(0);
                    break;
                case LoginMessageType.NET_STATUS_WAP:
                case LoginMessageType.NET_STATUS_DISABLE:
                    mVoipLogic.closeVoip(false);
                    mVoipLogic.logout();
                    break;
                case LoginMessageType.LOGIN_SUCCESS:
                    mLoginLogic.afterLoginSuccessed(false);
                    break;
            }
            showLoginBar();
        }
    }
    
    /**
     * 
     * 网络变更提示
     */
    private void showLoginBar()
    {
        View loginBar = findViewById(R.id.connection_changed);
        if (loginBar != null)
        {
            Button button = (Button) loginBar.findViewById(R.id.relogin);
            button.setEnabled(true);
            TextView netWorkTitle = (TextView) loginBar.findViewById(R.id.networking_title);
            ImageView noNetImage = (ImageView) loginBar.findViewById(R.id.networking_image);
            switch (FusionConfig.getInstance().getUserStatus())
            {
                case LoginMessageType.STATUS_LOGINING:
                case LoginMessageType.LOGIN:
                    netWorkTitle.setText(R.string.logining);
                    noNetImage.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    loginBar.setVisibility(View.VISIBLE);
                    break;
                case LoginMessageType.STATUS_BREAK:
                    noNetImage.setVisibility(View.VISIBLE);
                    netWorkTitle.setText(R.string.please_wait);
                    button.setText(R.string.retry);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener()
                    {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(View v)
                        {
                            mLoginLogic.reLogin(-1);
                            v.setEnabled(false);
                        }
                    });
                    loginBar.setVisibility(View.VISIBLE);
                    break;
                case LoginMessageType.NET_STATUS_DISABLE:
                    noNetImage.setVisibility(View.VISIBLE);
                    netWorkTitle.setText(R.string.check_network);
                    button.setText(R.string.rwap_change);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener()
                    {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(View v)
                        {
                            Intent wirelessSerrionsIntent = new Intent(
                                    Settings.ACTION_WIRELESS_SETTINGS);
                            wirelessSerrionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(wirelessSerrionsIntent);
                        }
                    });
                    loginBar.setVisibility(View.VISIBLE);
                    break;
                case LoginMessageType.NET_STATUS_WAP:
                    noNetImage.setVisibility(View.VISIBLE);
                    netWorkTitle.setText(R.string.wap_apn);
                    button.setText(R.string.rwap_change);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener()
                    {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void onClick(View v)
                        {
                            ConnectionChangedReceiver.switchOverAPN(BasicActivity.this);
                            v.setEnabled(false);
                        }
                    });
                    loginBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    loginBar.setVisibility(View.GONE);
                    break;
            }
        }
    }
    
    /**
     * 系统的初始化方法<BR>
     * 
     * @param context
     *            系统的context对象
     * @see com.huawei.basic.android.im.framework.ui.LauncheActivity#init(android.content.Context)
     */
    
    @Override
    protected void initSystem(Context context)
    {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        FusionConfig.getInstance().setClientVersion(getAppVersionName());
        FusionConfig.getInstance()
                .getAasResult()
                .setUserSysId(getSharedPreferences().getString(Common.KEY_USER_SYSID,
                        null));
        FusionConfig.getInstance()
                .getAasResult()
                .setUserID(getSharedPreferences().getString(Common.KEY_USER_ID,
                        null));
        NotificationEntityManager.getInstance()
                .init(context.getApplicationContext());
        
        // 网络配置文件的初始化
        ConfigManager.getInstance().initConfig(this);
        
        //        // 初始化通讯录上传管理
        //        HttpContactController.getInstance().init(context.getApplicationContext());
        
        //初始化Fast SDK组件,并设置系统参数
        //因为initSystem在父类中onCreate被执行,所以当前mVoipLogic不能使用,必须自行获取
        IVoipLogic voipLogic = (VoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
        voipLogic.init();
        
        TaskManagerFactory.init(context);
        //初始化插件
        //需要执行初始化操作的各种插件种类
        IPluginInit[] initArrays = new IPluginInit[] { new ApkInit(),
                new InternalInit() };
        PluginManager.getInstance().init(context.getApplicationContext(),
                initArrays);
        
        currentActivtiy = this;
    }
    
    /**
     * 
     * 确认提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，左边显示“确定”按钮，右边显示“取消”按钮
     * 
     * [功能详细描述]
     * 
     * @param msgResId
     *            字符串id
     * @param onClickListener
     *            确认按钮的点击事件处理
     */
    public void showConfirmDialog(int msgResId,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        BasicDialog mShowConfirmDialog;
        mShowConfirmDialog = getBuilder().setTitle(R.string.prompt)
                .setMessage(getResources().getString(msgResId))
                .setPositiveButton(R.string.confirm,
                        (android.content.DialogInterface.OnClickListener) onClickListener)
                .setNegativeButton(R.string.cancel, null)
                .create();
        mShowConfirmDialog.show();
    }
    
    /**
     * 
     * 确认提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，左边显示“确定”按钮，右边显示“取消”按钮
     * 
     * [功能详细描述]
     * 
     * @param msgRes
     *            字符串
     * @param onClickListener
     *            确认按钮的点击事件处理
     */
    public void showConfirmDialog(String msgRes,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        BasicDialog mShowConfirmDialog;
        
        mShowConfirmDialog = getBuilder().setTitle(R.string.prompt)
                .setMessage(msgRes)
                .setPositiveButton(R.string.confirm,
                        (android.content.DialogInterface.OnClickListener) onClickListener)
                .setNegativeButton(R.string.cancel, null)
                .create();
        mShowConfirmDialog.show();
    }
    
    /**
     * 确认提示框 标题栏显示“提示”，显示内容由msgResId提供， 只显示“确定”按钮
     * 
     * @param msgResId
     *            资源id
     * @param onClickListener
     *            onClickListener
     */
    public void showOnlyConfirmDialog(int msgResId,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        BasicDialog mShowConfirmDialog;
        mShowConfirmDialog = getBuilder().setTitle(R.string.prompt)
                .setMessage(getResources().getString(msgResId))
                .setPositiveButton(R.string.confirm,
                        (android.content.DialogInterface.OnClickListener) onClickListener)
                .create();
        mShowConfirmDialog.show();
    }
    
    /**
     * 确认提示框 标题栏显示“提示”，显示内容由msgResId提供， 只显示“确定”按钮
     * 
     * @param msgResId
     *            资源id
     */
    public void showOnlyConfirmDialog(int msgResId)
    {
        showOnlyConfirmDialog(msgResId, null);
    }
    
    /**
     * 
     * 确认提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，左边显示“确定”按钮，右边显示“取消”按钮
     * 
     * @param titleId
     *            标题
     * @param iconId
     *            图片
     * @param messageId
     *            提示内容
     * @param onClickListener
     *            确认按钮的点击事件处理
     */
    public void showIconDialog(int titleId, int iconId, int messageId,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        BasicDialog mShowConfirmDialog;
        mShowConfirmDialog = getBuilder().setTitle(titleId)
                .setIcon(iconId)
                .setMessage(getResources().getString(messageId))
                .setPositiveButton(R.string.confirm,
                        (android.content.DialogInterface.OnClickListener) onClickListener)
                .setNegativeButton(R.string.cancel, null)
                .create();
        mShowConfirmDialog.show();
    }
    
    /**
     * 
     * 确认提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，只显示“确定”按钮
     * 
     * @param titleId
     *            标题
     * @param iconId
     *            图片
     * @param messageId
     *            提示内容
     * @param onClickListener
     *            确认按钮的点击事件处理
     */
    public void showOnlyConfirmIconDialog(int titleId, int iconId,
            int messageId,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        BasicDialog mShowConfirmDialog;
        mShowConfirmDialog = getBuilder().setTitle(titleId)
                .setIcon(iconId)
                .setMessage(getResources().getString(messageId))
                .setPositiveButton(R.string.confirm,
                        (android.content.DialogInterface.OnClickListener) onClickListener)
                .create();
        mShowConfirmDialog.show();
    }
    
    /**
     * 菜单
     * 
     * @param menu
     *            Menu
     * @return true
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (isNeedMenu())
        {
            menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, R.string.exit);
            menu.getItem(0).setIcon(R.drawable.menu_exit_icon);
        }
        return true;
    }
    
    /**
     * 
     * 退出菜单
     * 
     * @param item
     *            MenuItem
     * @return true
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == MENU_QUIT)
        {
            showIconDialog(R.string.wake,
                    R.drawable.icon_warning,
                    R.string.cancel_hitalk,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //本地保存登录信息
                            //                            Editor edit = getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            //                                    Context.MODE_PRIVATE).edit();
                            //                            edit.putBoolean(Common.KEY_ISLOGIN, false);
                            //                            edit.commit();
                            setLogined(false);
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
                            dialog.dismiss();
                            finish();
                            clearActivityStack(mActivityStack);
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 
     * 显示进度框<BR>
     * [功能详细描述]
     * 
     * @param message
     *            对话框显示信息
     */
    protected void showProgressDialog(String message)
    {
        if (mProDialog == null)
        {
            if (getParent() != null)
            {
                mProDialog = new ProgressDialog(getParent());
            }
            else
            {
                mProDialog = new ProgressDialog(this);
            }
        }
        mProDialog.setMessage(message);
        showProgressDialog(mProDialog);
    }
    
    /**
     * 
     * 弹出进度框<BR>
     * [功能详细描述]
     * 
     * @param proDialog
     *            对话框显示信息
     */
    protected void showProgressDialog(ProgressDialog proDialog)
    {
        if (!isPaused)
        {
            proDialog.show();
        }
    }
    
    /**
     * 
     * 显示进度框<BR>
     * [功能详细描述]
     * 
     * @param msgResId
     *            对话框显示信息
     */
    protected void showProgressDialog(int msgResId)
    {
        showProgressDialog(getResources().getString(msgResId));
    }
    
    /**
     * 
     * 关闭进度框<BR>
     * [功能详细描述]
     */
    protected void closeProgressDialog()
    {
        if (mProDialog != null)
        {
            mProDialog.dismiss();
            mProDialog = null;
        }
    }
    
    /**
     * 
     * 显示提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，居中显示“确定”按钮，用户点击后关闭对话框。
     * 
     * @param msgResId
     *            提示信息
     */
    protected void showPromptDialog(int msgResId)
    {
        showPromptDialog(getResources().getString(msgResId));
    }
    
    /**
     * 
     * 显示提示框<BR>
     * 标题栏显示“提示”，显示内容由msgResId提供，居中显示“确定”按钮，用户点击后关闭对话框。 [功能详细描述]
     * 
     * @param message
     *            提示信息
     */
    protected void showPromptDialog(String message)
    {
        showPromptDialog(message, null, true);
    }
    
    /**
     * 
     * 显示提示框<BR>
     * [功能详细描述]
     * 
     * @param message
     *            message
     * @param onClickListener
     *            onClickListener
     * @param cancelable
     *            cancelable
     */
    protected void showPromptDialog(String message,
            android.content.DialogInterface.OnClickListener onClickListener,
            boolean cancelable)
    {
        // 显示对话框 居中的“确定”按钮，点击后关闭对话框
        
        mShowPromptDialog = getBuilder().setTitle(R.string.prompt)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, onClickListener)
                .setNegativeButton(R.string.cancel, null)
                .create();
        mShowPromptDialog.setCancelable(cancelable);
        mShowPromptDialog.show();
    }
    
    /**
     * 显示提示框
     * 
     * @param message
     *            提示内容
     * @param titleStr
     *            标题
     * @param leftStr
     *            左按钮
     * @param rightStr
     *            右按钮
     * @param onClickListener
     *            监听
     * @param cancelable
     *            cancelable
     */
    protected void showPromptDialog(String message, int titleStr, int leftStr,
            int rightStr,
            android.content.DialogInterface.OnClickListener onClickListener,
            boolean cancelable)
    {
        // 显示对话框 居中的“确定”按钮，点击后关闭对话框
        
        mShowPromptDialog = getBuilder().setTitle(titleStr)
                .setMessage(message)
                .setPositiveButton(leftStr, onClickListener)
                .setNegativeButton(rightStr, null)
                .create();
        mShowPromptDialog.setCancelable(cancelable);
        mShowPromptDialog.show();
    }
    
    /**
     * 
     * 是否需要MENU<BR>
     * 
     * @return 默认为否
     */
    
    protected boolean isNeedMenu()
    {
        return false;
    }
    
    /**
     * 
     * 销毁 当前Activity置空
     * 
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    
    /**
     * 
     * 更新提示Dialog [功能详细描述]
     * 
     * @param id
     *            字符串
     * @param listener
     *            点击监听
     */
    protected void showDialog(int id,
            final DialogInterface.OnClickListener listener)
    {
        BasicDialog.Builder mBuilder = getBuilder();
        mBuilder.setTitle(id)
                .setPositiveButton(R.string.confirm, listener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
    
    /**
     * 
     * 根据资源id show toast<BR>
     * [功能详细描述]
     * 
     * @param msgId
     *            字符串id
     */
    protected void showToast(int msgId)
    {
        showToast(getResources().getString(msgId));
    }
    
    /**
     * 
     * 根据字符串 show toast<BR>
     * [功能详细描述]
     * 
     * @param message
     *            字符串
     */
    protected void showToast(CharSequence message)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(message);
        }
        mToast.show();
    }
    
    /**
     * 
     * 显示编辑Dialog [功能详细描述]
     * 
     * @param textId
     *            textId
     * @param listener
     *            listener
     * @param editText
     *            editText
     */
    protected void showTextEditDialog(int textId,
            final OnClickListener listener, final EditText editText)
    {
        editText.setSelection(editText.getText().toString().length());
        getBuilder().setTitle(textId)
                .setContentView(editText)
                .setPositiveButton(R.string.confirm, listener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
    
    /**
     * 
     * 显示信息Dialog<BR>
     * 
     * @param titleId
     *            标题
     * @param iConId
     *            标题图片
     * @param messageID
     *            展示信息
     * @param leftButton
     *            左按钮名称
     * @param leftListener
     *            监听
     * @param rightButton
     *            右按钮名称
     * @param rightListener
     * 
     *            监听
     * @return BasicDialog
     */
    protected BasicDialog showMessageDialog(int titleId, int iConId,
            int messageID, int leftButton, final OnClickListener leftListener,
            int rightButton, final OnClickListener rightListener)
    {
        BasicDialog basicDialog = getBuilder().setTitle(titleId)
                .setIcon(iConId)
                .setMessage(messageID)
                .setPositiveButton(leftButton, leftListener)
                .setNegativeButton(rightButton, rightListener)
                .create();
        basicDialog.show();
        return basicDialog;
    }
    
    /**
     * 
     * 带自画View的Dialog<BR>
     * [功能详细描述]
     * 
     * @param textId
     *            textId
     * @param listener
     *            listener
     * @param view
     *            view
     */
    protected void showViewDialog(int textId, final OnClickListener listener,
            final View view)
    {
        getBuilder().setTitle(textId)
                .setContentView(view)
                .setPositiveButton(R.string.confirm, listener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
    
    /**
     * 列表格式的弹出框
     * 
     * @param titleId
     *            标题ID
     * @param strArray
     *            列表内容
     * @param defaltValue
     *            默认值
     * @param listener
     *            监听器
     */
    protected void showSingleChoiceDialog(int titleId, String[] strArray,
            int defaltValue, DialogInterface.OnClickListener listener)
    {
        getBuilder().setTitle(getString(titleId))
                .setSingleChoiceItems(strArray, defaltValue, listener)
                .create()
                .show();
    }
    
    /**
     * 列表格式的弹出框
     * 
     * @param titleId
     *            标题ID
     * @param strArray
     *            列表内容
     * @param listener
     *            监听器
     */
    protected void showListDialog(int titleId, String[] strArray,
            DialogInterface.OnClickListener listener)
    {
        getBuilder().setTitle(getString(titleId))
                .setItems(strArray, listener)
                .create()
                .show();
    }
    
    /**
     * 检查网络
     * 
     * @return 是否连接
     */
    protected boolean checkNet()
    {
        //检查网络
        ConnectivityManager manger = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manger.getActiveNetworkInfo();
        if (!(info != null && info.isConnected()))
        {
            showToast(R.string.check_network);
            return false;
        }
        return true;
    }
    
    /**
     * 被踢出 <BR>
     * 
     */
    protected void kickOut()
    {
        quit(true);
    }
    
    /**
     * 用户主动退出 将密码置空<BR>
     * 
     */
    protected void quit()
    {
        //从数据库查找账号信息
        //        AccountModel model = AccountDbAdapter.getInstance(this)
        //                .queryByUserSysId(FusionConfig.getInstance()
        //                        .getAasResult()
        //                        .getUserSysId());
        //密码置空
        //        model.setPassword(null);
        
        Editor edit = getSharedPreferences().edit();
        edit.putString(Common.KEY_USER_PASSWORD, "");
        edit.commit();
        
        //更新数据库
        //        AccountDbAdapter.getInstance(this).updateByLoginAccount(model);
        quit(false);
    }
    
    /**
     * 退回到登陆界面
     * 
     * @param isKickOut
     *            是否被踢出
     */
    private void quit(boolean isKickOut)
    {
        //登录状态改为false
        setLogined(false);
        /*
         * 在被踢出/主动退出时，进入登录页面，并且清空Activity栈。
         * 先重新构建新的栈用于保存新创建的登录页面Activity，
         * 将原Activity栈临时保存下，在方法的最后对其finish(),销毁所有Activity.
         * edit by 杨凡
         */
        Stack<Activity> tmpStack = mActivityStack;
        mActivityStack = new Stack<Activity>();
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
        Intent intent = new Intent(LoginAction.ACTION);
        if (isKickOut)
        {
            intent.putExtra(LoginAction.EXTRA_IS_KICK_OUT, isKickOut);
        }
        startActivity(intent);
        clearActivityStack(tmpStack);
    }
    
    /**
     * 
     * 清空栈内Activity<BR>
     * 
     * @param stack
     *            activity堆栈
     */
    private void clearActivityStack(Stack<Activity> stack)
    {
        for (int i = 0; i < stack.size(); i++)
        {
            stack.get(i).finish();
        }
    }
    
    /**
     * 
     * 构建弹出提示框
     * 
     * @return AlertDialog.Builder
     */
    private BasicDialog.Builder getBuilder()
    {
        BasicDialog.Builder builder;
        if (getParent() != null)
        {
            builder = new BasicDialog.Builder(getParent());
        }
        else
        {
            builder = new BasicDialog.Builder(this);
        }
        return builder;
    }
    
    /**
     * 
     * 是否是paused状态<BR>
     * [功能详细描述]
     * 
     * @return boolean
     */
    protected boolean isPaused()
    {
        return isPaused;
    }
    
    /**
     * 是否需要更新<BR>
     * [功能详细描述]
     * 
     * @return boolean
     */
    protected boolean isNeedUpdate()
    {
        return needUpdate;
    }
    
    /**
     * 
     * 设置是否要更新<BR>
     * [功能详细描述]
     * 
     * @param needUpdate
     *            needUpdate
     */
    protected void setNeedUpdate(boolean needUpdate)
    {
        this.needUpdate = needUpdate;
    }
    
    /**
     * 结束Activity
     * 
     * @see android.app.Activity#finish()
     */
    @Override
    public void finish()
    {
        mActivityStack.remove(this);
        super.finish();
    }
    
    /**
     * 
     * 获取shared preferences<BR>
     * [功能详细描述]
     * 
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreferences()
    {
        return getSharedPreferences(Common.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
    }
    
    /**
     * 当焦点停留在view上时，隐藏输入法栏
     * 
     * @param view
     *            view
     */
    protected void hideInputWindow(View view)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        
        if (imm != null && view != null)
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * 
     * 返回当前程序版本名
     * 
     * @return 版本号
     */
    protected String getAppVersionName()
    {
        String versionName = "";
        try
        {
            // ---get the package info---
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0)
            {
                return "";
            }
        }
        catch (Exception e)
        {
            Logger.i(TAG, "Exception", e);
        }
        return versionName;
    }
    
    /**
     * 
     * 是否为已经登录<BR>
     * 
     * @return 是否为已经登录
     */
    protected boolean hasLogined()
    {
        return getSharedPreferences().getBoolean(Common.KEY_ISLOGIN, false);
    };
    
    /**
     * 
     * 设置已经成功登录<BR>
     * 
     * @param logined
     *            boolean 是否为已经登录
     */
    protected void setLogined(boolean logined)
    {
        getSharedPreferences().edit()
                .putBoolean(Common.KEY_ISLOGIN, logined)
                .commit();
    }
}
