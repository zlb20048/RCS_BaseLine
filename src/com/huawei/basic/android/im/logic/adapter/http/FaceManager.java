/*
 * 文件名: FaceManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 16, 2012] 
 */
public class FaceManager extends HttpManager
{
    /**
     * TAG
     */
    private static final String TAG = "FaceManager";
    
    /**
     * 读取头像的Action标识
     */
    private static final int ACTION_LOAD_FACE_ICON = 1;
    
    /**
     * 头像的圆角
     */
    private static final int HEAD_ROTE = 8;
    
    /**
     * 头像类型的枚举<BR>
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Feb 18, 2012]
     */
    public enum FaceType
    {
        /**
         * 缩略图：0
         */
        THUMBNAIL,

        /**
         * 原图：1
         */
        ORIGINAL,

        /**
         * 群头像
         */
        GROUP
    }
    
    /**
     * 构建URL字符串 <BR>
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return URL字符串
     * @see com.huawei.basic.android.im.component.net.http
     * .HttpManager#getUrl(int, java.util.Map)
     */
    @Override
    protected String getUrl(int action, Map<String, Object> sendData)
    {
        switch (action)
        {
            case ACTION_LOAD_FACE_ICON:
                String faceUrl = (String) sendData.get("FaceUrl");
                FaceType faceType = (FaceType) sendData.get("FaceType");
                String userSysId = (String) sendData.get("UserSysId");
                if (faceUrl != null)
                {
                    if (faceUrl.contains("http://"))
                    {
                        switch (faceType)
                        {
                            case THUMBNAIL:
                                faceUrl += "&type=0";
                                break;
                            case ORIGINAL:
                                faceUrl += "&type=2";
                                break;
                            case GROUP:
                                faceUrl += "&type=2";
                                break;
                            default:
                                break;
                        }
                    }
                    // 一期的用户的头像URL
                    else if (faceUrl.startsWith("/v1/photos/profiles/"))
                    {
                        faceUrl = getSipFaceURL(faceUrl, userSysId);
                        Log.d(TAG, "重新拼装sip版头像地址：" + faceUrl);
                    }
                    else
                    {
                        faceUrl = getBasicUrl() + faceUrl + "&BMSuiteUserID="
                                + userSysId;
                    }
                }
                return faceUrl;
                
            default:
                return null;
        }
        
    }
    
    /**
     * get the mBasicUrl
     * @return the mBasicUrl
     */
    private String getBasicUrl()
    {
        return FusionConfig.getInstance().getAasResult().getCabgroupurl();
    }
    
    /**
     * 
     * 拼接一期好友头像的URL<BR>
     * @param url
     *      头像的url
     * @param userSysId
     *      好友的系统id
     * @return
     *      sip头像的服务器地址
     */
    private static String getSipFaceURL(String url, String userSysId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("http://123.125.97.219:19080")
                .append(url)
                .append("&BMSuiteUserID=")
                .append(userSysId);
        return sb.toString();
    }
    
    /**
     * 请求method类型<BR>
     * @param action 请求标识，不同请求定义不同的标识位
     * @return GET请求
     */
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        return RequestMethod.GET;
    }
    
    /**
     * need to set the response data's type to 'byte' instead of 'String'<BR>
     * @param action 请求标识，不同请求定义不同的标识位
     * @return  默认不需要byte数组
     */
    @Override
    protected boolean isNeedByte(int action)
    {
        return true;
    }
    
    /**
     * 封装请求消息体 <BR>
     * @param action 请求标识，不同请求定义不同的标识位
     * @param sendData 请求参数
     * @return 请求消息体字符串，一般为XML或者JSON
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getBody(int, java.util.Map)
     */
    
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        return null;
    }
    
    /**
     * 
     * 对服务器返回的数据进行解析处理，封装对象<BR>
     * 
     * 
     * @param action 请求Action，用来标识不同的请求
     * @param sendData 调用者发送请求时封装的数据
     * @param response 服务器返回数据对象
     * @return 封装后的对象，在onResult()中通过response.getObj()获得。
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#handleData(int, java.util.Map, java.lang.String)
     */
    
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        Logger.d("PrivacyApplyActivity", "response.getByteData() == "
                + (response.getByteData() == null));
        return response.getByteData();
    }
    
    /**
     * 获取服务器头像<BR>
     * @param friendUserId 好友的系统ID
     * @param faceUrl 头像的Url
     * @param httpListener 回调的监听对象
     *      
     */
    public void loadFaceIcon(String friendUserId, String faceUrl,
            IHttpListener httpListener)
    {
        loadFaceIcon(friendUserId, faceUrl, FaceType.ORIGINAL, httpListener);
        
    }
    
    /**
     * 
      * 获取服务器头像<BR>
     * @param friendUserId 好友的系统ID
     * @param faceType 头像类型
     * @param faceUrl 头像的Url
     * @param httpListener 回调的监听对象
     */
    public void loadFaceIcon(String friendUserId, String faceUrl,
            FaceType faceType, IHttpListener httpListener)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put("FaceUrl", faceUrl);
        sendData.put("FaceType", faceType);
        sendData.put("UserSysId", friendUserId);
        super.send(ACTION_LOAD_FACE_ICON, sendData, httpListener);
        
    }
    
    /**
     * 更新图片表<br>
     * 如果数据库没有，直接插入数据库；如果数据库有，比较URL是否相同，如果不同更新数据库 [功能详细描述]
     * 
     * @param context Context
     * @param faceId face id
     * @param faceUrl face URL
     */
    public static void updateFace(Context context, String faceId, String faceUrl)
    {
        if (faceId != null && faceUrl != null)
        {
            FaceThumbnailDbAdapter faceDbAdapter = FaceThumbnailDbAdapter.getInstance(context);
            // 先从数据库获取已有的头像
            FaceThumbnailModel face = faceDbAdapter.queryByFaceId(faceId);
            
            // 如果数据库没有数据，则直接插入
            if (face == null)
            {
                faceDbAdapter.insertFaceThumbnail(new FaceThumbnailModel(
                        faceId, faceUrl));
            }
            
            // 如果数据库中有数据，则比较URL，如果URL不同，则更新，并清除原有头像byte[]
            else
            {
                if (!faceUrl.equals(face.getFaceUrl()))
                {
                    // 全量更新时会覆盖原有的值
                    faceDbAdapter.updateByFaceId(faceId,
                            new FaceThumbnailModel(faceId, faceUrl));
                }
            }
        }
    }
    
    /**
     * 
     * 显示头像<BR>
     * [功能详细描述]
     * 
     * @param faceImageView 用于显示头像的ImageView
     * @param faceUrl 头像URL
     * @param faceByteData 头像byte数组
     * @param defaultFaceId 默认头像资源id
     * @param width 头像宽
     * @param height 头像高
     */
    public static void showFace(ImageView faceImageView, String faceUrl,
            byte[] faceByteData, int defaultFaceId, int width, int height)
    {
        // 系统头像就直接显示
        if (SystemFacesUtil.isSystemFaceUrl(faceUrl))
        {
            faceImageView.setImageResource(SystemFacesUtil.getFaceImageResourceIdByFaceUrl(faceUrl));
        }
        else
        {
            
            Bitmap bitmap = null;
            // 自定义头像
            if (faceByteData != null)
            {
                bitmap = BitmapFactory.decodeByteArray(faceByteData,
                        0,
                        faceByteData.length);
            }
            if (bitmap != null)
            {
                if (width > 0 && height > 0)
                {
                    ImageUtil.drawRoundCorner(bitmap,
                            width,
                            height,
                            HEAD_ROTE,
                            faceImageView);
                }
                else
                {
                    faceImageView.setImageBitmap(bitmap);
                }
            }
            else
            {
                // 默认头像
                faceImageView.setImageResource(defaultFaceId);
            }
        }
    }
    
    /**
     * 
     * 生成聊吧头像并显示<BR>
     * [功能详细描述]
     * 
     * @param faceImageView 用于显示头像的ImageView
     * @param groupId 要显示的聊吧id
     * @param sortedGroupMemberModelList 已经排序过的成员
     * @param context context
     * @param width 头像宽
     * @param height 头像高
     */
    public static void showCharBarFace(ImageView faceImageView, String groupId,
            List<GroupMemberModel> sortedGroupMemberModelList, Context context,
            int width, int height)
    {
        
        faceImageView.setImageResource(R.drawable.default_chatbar_icon);
        
        if (sortedGroupMemberModelList != null)
        {
            List<Drawable> drawables = new ArrayList<Drawable>();
            //获取有多少个成员
            int size = sortedGroupMemberModelList.size();
            Drawable drawable = null;
            Drawable defaultDrawable = context.getResources()
                    .getDrawable(R.drawable.default_contact_icon);
            String url = null;
            byte[] faceBytes = null;
            final int drawFaceTimes = 4;
            for (int i = 0; i < (size > drawFaceTimes ? drawFaceTimes : size); i++)
            {
                url = sortedGroupMemberModelList.get(i).getMemberFaceUrl();
                faceBytes = sortedGroupMemberModelList.get(i)
                        .getMemberFaceBytes();
                if (SystemFacesUtil.isSystemFaceUrl(url))
                {
                    drawable = context.getResources()
                            .getDrawable(SystemFacesUtil.getFaceImageResourceIdByFaceUrl(url));
                }
                else
                {
                    Bitmap face = null;
                    if (faceBytes != null)
                    {
                        Log.e(TAG, "faceBytes != NULL"
                                + sortedGroupMemberModelList.get(i)
                                        .getMemberId());
                        face = BitmapFactory.decodeByteArray(faceBytes,
                                0,
                                faceBytes.length);
                    }
                    if (face != null)
                    {
                        drawable = ImageUtil.drawRoundCornerForDrawable(face,
                                (width - 6) / 2,
                                (height - 6) / 2,
                                4);
                    }
                    else
                    {
                        drawable = defaultDrawable;
                    }
                }
                drawables.add(drawable);
            }
            //如果成员头像数量<4,绘制默认的头像，保证绘制的头像有4个
            int drawTimes = drawFaceTimes - drawables.size();
            //如果成员数量<4,另外的头像显示默认头像
            if (drawTimes > 0)
            {
                for (int i = 0; i < drawTimes; i++)
                {
                    drawables.add(defaultDrawable);
                }
            }
            faceImageView.setImageBitmap(ImageUtil.createChatBarBitmap(R.drawable.chatbar_bg,
                    drawables,
                    width,
                    height,
                    context));
        }
        
    }
    
}
