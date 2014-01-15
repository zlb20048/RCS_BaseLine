/*
 * 文件名: FastAudioCall.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 12, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import com.huawei.fast.voip.FastVoIP;
import com.huawei.fast.voip.FastVoIPConstant;

/**
 * 音频通话操作类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 12, 2012] 
 */
public class FastAudioCall extends FastCall
{
    
    /**
     * 构造方法
     * @param isCaller
     *      是否是主叫方
     * @param fastVoIP
     *      Fast组件对象
     * @param callSession
     *      通话会话对象
     */
    protected FastAudioCall(boolean isCaller, FastVoIP fastVoIP,
            CallSession callSession)
    {
        super(isCaller, fastVoIP, callSession);
    }
    
    /**
     * 呼叫电话
     * @param phoneNum
     *      对方的电话号码
     * @return
     *      通话id
     * @see com.huawei.basic.android.im.component.voip.ICall#call()
     */
    public int call(String phoneNum)
    {
        int callId = getFastVoIP().call(phoneNum, FastVoIPConstant.AUDIOTYPE);
        super.getCallSession().setCallId(callId);
        return callId;
        
    }
    
    /**
     * 接听电话
     * @return 
     *      是否接听电话成功
     * @see com.huawei.basic.android.im.component.voip.ICall#answer()
     */
    public boolean answer()
    {
        int callId = getCallSession().getCallId();
        return getFastVoIP().answer(callId, FastVoIPConstant.AUDIOTYPE);
    }
    
    /**
     * 保持通话
     * @return
     *      是否保持操作成功
     * @see com.huawei.basic.android.im.component.voip.ICall#hold()
     */
    public boolean hold()
    {
        int callId = getCallSession().getCallId();
        return getFastVoIP().hold(callId);
        
    }
    
    /**
     * 恢复通话
     * @return
     *      是否恢复操作成功
     * @see com.huawei.basic.android.im.component.voip.ICall#retrieve()
     */
    public boolean retrieve()
    {
        int callId = getCallSession().getCallId();
        return getFastVoIP().retrieve(callId);
    }
    
    /**
     * 挂断电话
     * @return
     *      是否挂断操作成功
     * @see com.huawei.basic.android.im.component.voip.ICall#close()
     */
    @Override
    public boolean close()
    {
        int callId = getCallSession().getCallId();
        return getFastVoIP().close(callId);
        
    }
}
