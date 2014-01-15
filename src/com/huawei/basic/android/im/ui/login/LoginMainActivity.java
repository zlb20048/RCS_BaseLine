/*
 * 文件名: LoginMainActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Mar 20, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.RegisterAction;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 登录页面入口
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 20, 2012] 
 */
public class LoginMainActivity extends BasicActivity implements OnClickListener
{
    private static BasicActivity finishInstance;
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        finishInstance = this;
        setContentView(R.layout.login_main);
        //初始化组件
        initView();
    }
    
    /**
     * 初始化页面组件、设置组件监听器
     * <BR>
     *
     */
    private void initView()
    {
        // 组件初始化
        Button loginButton = (Button) findViewById(R.id.loginbutton);
        Button registerButton = (Button) findViewById(R.id.registerbutton);
        //设置监听
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }
    
    /**
     * 按钮点击
     * @param v View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginbutton:
                //登录
                Intent intent = new Intent(LoginAction.ACTION);
                startActivity(intent);
                break;
            case R.id.registerbutton:
                //注册
                startActivity(new Intent(RegisterAction.ACTION));
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 返回一个boolean表示展示该页面是否需要登录成功
     * 
     * @return boolean 是否是登录后的页面
     */
    @Override
    protected boolean needLogin()
    {
        return false;
    }
    
    static void finishActivity()
    {
        if (null != finishInstance)
        {
            finishInstance.finish();
        }
    }
    
    /**
     * 
     * 屏蔽menu
     * @param keyCode keyCode
     * @param event event
     * @return true
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            super.openOptionsMenu();
        }
        else
        {
            super.onKeyDown(keyCode, event);
        }
        return true;
    }
}
