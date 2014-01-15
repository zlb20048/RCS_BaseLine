/*
 * 文件名: HolderEventListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import android.graphics.Bitmap;

import android.view.View;

import com.huawei.basic.android.im.logic.model.BaseMessageModel;

/**
 * holder内组件事件监听器定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-10] 
 */
public interface HolderEventListener
{
    /**
     * 为某个view对象注册上下文菜单
     * <BR>
     * 
     * @param view View
     */
    void registerContextMenu(View view);
    
    /**
     * 某条消息的用户头像点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onUserPhotoClick(BaseMessageModel msg);
    
    
    /**
     * 图片按钮点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onImageClick(BaseMessageModel msg);
    
    /**
     * 视频播放按钮点击事件<BR>
     * @param msg BaseMessageModel
     */
    void onVideoClick(BaseMessageModel msg);
    
    /**
     * 获取指定路径的图片对象<BR>
     * @param path 图片本地路径
     * @return bitmap
     */
    Bitmap getBitmap(String path);
    
    /**
     * 
     * 获取贴图资源<BR>
     * @param path 贴图资源路径
     * @return 贴图
     */
    Bitmap getEmojBitmap(String path);
    
    /**
     * 
     * 开始播放音频<BR>
     * 
     * @param msg BaseMessageModel
     */
    void startPlayAudio(BaseMessageModel msg);
    
    /**
     * 停止播放音频
     * <BR>
     */
    void stopPlayAudio();
    
    /**
     * 
     * 更新消息状态为已读<BR>
     * 在显示消息的时候更新消息状态，如果消息未读，更新为已读；如果需要发送阅读报告，则发送阅读报告<BR>
     * @param msg BaseMessageModel
     */
    void setMsgAsReaded(BaseMessageModel msg);
    
    /**
     * 
     * 文本消息点击事件<BR>
     * @param msgModel BaseMessageModel
     */
    void onTextClick(BaseMessageModel msgModel);
    /**
     * 
     * 我的位置图片点击事件<BR>
     * @param msgModel BaseMessageModel
     */
    void onLocationImageClick(BaseMessageModel msgModel);
    /**
     * 
     * 下载地图缩略图<BR>
     * @param msgModel BaseMessageModel
     */
    void downLocationImage(BaseMessageModel msgModel);
}
