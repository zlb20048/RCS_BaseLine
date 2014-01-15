/*
 * 文件名: ICall.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 28, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

/**
 * 通话操作接口
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 28, 2012] 
 */
public interface ICall
{
    /**
     * 呼叫通话
     * @param phoneNum
     *      对方电话号码
     * @return
     *      通话id
     */
    public int call(String phoneNum);
    
    /**
     * 接听通话
     * @return
     *      是否应答成功
     */
    public boolean answer();
    
    /**
     * 挂断通话
     * @return
     *      是否挂断
     */
    public boolean close();
    
    /**
     * 保持通话
     * @return
     *      是否保持成功
     */
    public boolean hold();
    
    /**
     * 恢复通话
     * @return
     *      是否恢复通话
     */
    public boolean retrieve();
}
