/*
 * 文件名: CallSession.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.huawei.fast.voip.FastVoIP;
import com.huawei.fast.voip.FastVoIPConstant;

/**
 * 通话会话
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 12, 2012] 
 */
public class CallSession
{
    /**
     * 通话id
     */
    private int mCallId;
    
    /**
     * 通话操作对象
     */
    private ICall mCall;
    
    /**
     * 通话开始时间
     */
    private Date startTime;
    
    /**
     * 通话结束时间
     */
    private Date endTime;
    /**
     * 主叫者信息
     */
    private CallInfo mCaller = null;
    /**
     * 被叫者信息
     */
    private List<CallInfo> mCallees = new ArrayList<CallInfo>();
    
    /**
     * 构造方法
     * @param callType
     *      通话类型 音频：FastVoIPConstant.AUDIOTYPE,视频：FastVoIPConstant.VIDEOTYPE
     * @param isCaller
     *      是否是主叫方
     * @param fastVoIP
     *      Fast组件对象
     */
    protected CallSession(String callType, boolean isCaller, FastVoIP fastVoIP)
    {
        this.mCall = createCallByType(callType, isCaller, fastVoIP);
        this.startTime = new Date(System.currentTimeMillis());
    }
    
    /**
     * 获取主叫者信息
     * @return
     *      主叫者信息
     */
    public CallInfo getCaller()
    {
        return mCaller;
    }
    
    /**
     * 设置主叫者信息
     * @param caller
     *      主叫者信息
     */
    protected void setCaller(CallInfo caller)
    {
        this.mCaller = caller;
    }
    
    /**
     * 获取所有被叫者信息
     * @return
     *      被叫者信息集合
     */
    protected List<CallInfo> getCallees()
    {
        return mCallees;
    }
    
    /**
     * 加入被叫者信息
     * @param callee
     *      被叫者信息
     */
    public void addCallee(CallInfo callee)
    {
        this.mCallees.add(callee);
    }
    
    /**
     * 得到通话操作对象
     * @return
     *      通话操作对象
     */
    protected ICall getCall()
    {
        return mCall;
    }
    
    /**
     * 得到通话id
     * @return
     *      通话id
     */
    public int getCallId()
    {
        return mCallId;
    }
    
    /**
     * 根据类型创建通话操作对象
     * @param callType
     *      通话类型 音频：FastVoIPConstant.AUDIOTYPE,视频：FastVoIPConstant.VIDEOTYPE
     * @param isCaller
     *      是否是主叫方
     * @param fastVoIP
     *      Fast组件对象
     * @return
     *      通话操作对象
     */
    private ICall createCallByType(String callType, boolean isCaller,
            FastVoIP fastVoIP)
    {
        if (callType.equals(FastVoIPConstant.AUDIOTYPE))
        {
            return new FastAudioCall(isCaller, fastVoIP, this);
        }
        else if (callType.equals(FastVoIPConstant.VIDEOTYPE))
        {
            return null;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 关闭会话
     */
    public void close()
    {
        setEndTime();
    }
    
    /**
     * 设置开始时间
     */
    protected void setStartTime()
    {
        startTime = new Date(System.currentTimeMillis());
    }
    
    /**
     * 设置结束时间
     */
    protected void setEndTime()
    {
        endTime = new Date(System.currentTimeMillis());
    }
    
    /**
     * 得到开始通话时间
     * @return
     *      开始通话时间
     */
    public Date getStartTime()
    {
        return startTime;
    }
    
    /**
     * 得到结束通话时间
     * @return
     *      结束通话时间
     */
    public Date getEndTime()
    {
        return endTime;
    }
    
    /**
     * 设置通话id
     * @param callId
     *     通话id
     */
    protected void setCallId(int callId)
    {
        this.mCallId = callId;
        
    }
}
