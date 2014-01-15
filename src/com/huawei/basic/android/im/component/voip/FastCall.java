/*
 * 文件名: FastCall.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 通话操作基类
 * 创建人: 刘鲁宁
 * 创建时间:Mar 12, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import com.huawei.fast.voip.FastVoIP;

/**
 * 通话操作基类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 12, 2012] 
 */
public abstract class FastCall implements ICall
{
    /**
     * Fast组件对象
     */
    private FastVoIP mFastVoIP;
    
    /**
     * 是否是主叫方
     */
    private boolean mIsCaller;
    
    /**
     * 通话会话
     */
    private CallSession mCallSession;
    
    /**
     * 构造方法
     * @param isCaller
     *      是否是主叫方
     * @param fastVoIP
     *      Fast组件对象
     * @param callSession
     *      通话会话
     */
    protected FastCall(boolean isCaller, FastVoIP fastVoIP,
            CallSession callSession)
    {
        this.mIsCaller = isCaller;
        this.mFastVoIP = fastVoIP;
        this.mCallSession = callSession;
    }
    
    /**
     * get Fast组件对象
     * @return
     *     Fast组件对象
     */
    protected final FastVoIP getFastVoIP()
    {
        return mFastVoIP;
    }
    
    /**
     * get 通话会话
     * @return
     *     通话会话
     */
    protected final CallSession getCallSession()
    {
        return mCallSession;
    }
    
    /**
     * 是否主叫方
     * @return
     *      是否主叫方
     * @see com.huawei.basic.android.im.component.voip.ICall#isCaller()
     */
    protected boolean isCaller()
    {
        return mIsCaller;
    }
    
}
