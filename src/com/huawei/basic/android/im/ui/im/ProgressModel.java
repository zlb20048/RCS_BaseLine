/*
 * 文件名: ProgressModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

/**
 * 下载/上传进度model<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-20] 
 */
public class ProgressModel
{
    /**
     * id
     */
    private String id;
    
    /**
     * 完成部分
     */
    private long finished;
    
    /**
     * 总共大小
     */
    private long total;
    
    /**
     * 完成百分比
     */
    private int percent;
    
    /**
     * [构造简要说明]
     */
    public ProgressModel()
    {
        super();
    }
    
    /**
     * [构造简要说明]
     * @param id id
     * @param finished 完成部分
     * @param total 总共大小
     * @param percent 完成百分比
     */
    public ProgressModel(String id, long finished, long total, int percent)
    {
        super();
        this.id = id;
        this.finished = finished;
        this.total = total;
        this.percent = percent;
    }
    
    /**
     * get id
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * set id
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * get finished
     * @return the finished
     */
    public long getFinished()
    {
        return finished;
    }
    
    /**
     * set finished
     * @param finished the finished to set
     */
    public void setFinished(long finished)
    {
        this.finished = finished;
    }
    
    /**
     * get total
     * @return the total
     */
    public long getTotal()
    {
        return total;
    }
    
    /**
     * set total
     * @param total the total to set
     */
    public void setTotal(long total)
    {
        this.total = total;
    }
    
    /**
     * get percent
     * @return the percent
     */
    public int getPercent()
    {
        return percent;
    }
    
    /**
     * set percent
     * @param percent the percent to set
     */
    public void setPercent(int percent)
    {
        this.percent = percent;
    }
}
