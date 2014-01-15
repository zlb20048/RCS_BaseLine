/*
 * 文件名: PhotoLoader.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 异步加载联系人或者好友头像的工具
 * 创建人: tjzhang
 * 创建时间:2012-3-7
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.widget.ImageView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMemberDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;

/**
 * 批量读取手机通讯录,或者头像表里面的头像<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, Mar 29, 2012]
 */
public class PhotoLoader implements Callback
{
    
    /**
     * 
     * 联网获取图片的回调<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-7]
     */
    public interface ContactPhotoLoaderListener
    {
        /**
         * 
         * 获取到图片，回调<BR>
         * [功能详细描述]
         */
        public void onPhotoLoaded();
    }
    
    /**
     * 需要读取头像的数据来源:联系人
     */
    public static final int SOURCE_TYPE_CONTACT = 1;
    
    /**
     * 需要读取头像的数据来源:好友
     */
    public static final int SOURCE_TYPE_FRIEND = 2;
    
    /**
     * 需要读取头像的数据来源:群组
     */
    public static final int SOURCE_TYPE_GROUP = 3;
    
    /**
     * 需要读取头像的数据来源:多人会话
     */
    public static final int SOURCE_TYPE_CHAT_BAR = 4;
    
    /**
     * 标记这个view不需要异步加载头像
     */
    public static final int NOT_AVAIABLE_VIEW = -1;
    
    private static final String TAG = "PhotoLoader";
    
    /**
     * 标识有头像需要加载
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;
    
    /**
     * 标识已加载好头像
     */
    private static final int MESSAGE_PHOTOS_LOADED = 2;
    
    /**
     * 聊吧头像最小的数目
     */
    private static final int MIN_FACE_COUNT = 4;
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    /**
     * 联系人查询的projection
     */
    private static final String[] CONTACT_PROJECTION = new String[] {
            Photo._ID, ContactsContract.Contacts.Photo.DATA15 };
    
    /**
     * 默认显示的头像资源
     */
    private final int mDefaultResourceId;
    
    /**
     * 头像的宽
     */
    private final int imageWidth;
    
    /**
     * 头像的高
     */
    private final int imageHeight;
    
    /**
     * 头像类型
     */
    private final int type;
    
    /**
     * 监听联网获取到头像的listener
     */
    private final ContactPhotoLoaderListener mListenter;
    
    private final Context mContext;
    
    /**
     * 弱缓存获取到的头像的map
     */
    private final ConcurrentHashMap<String, BitmapHolder> mBitmapCache = new ConcurrentHashMap<String, BitmapHolder>();
    
    /**
     * 缓存头像加载请求的map
     */
    private final ConcurrentHashMap<ImageView, String> mPendingRequests = new ConcurrentHashMap<ImageView, String>();
    
    /**
     * 发送到UI 线程的handler,可以直接给ImageView设置图片
     */
    private final Handler mMainThreadHandler = new Handler(this);
    
    /**
     * 获取头像数据的工作线程对象
     */
    private LoaderThread mLoaderThread;
    
    /**
     * 正在加载的标记位
     */
    private boolean mLoadingRequested;
    
    /**
     * 是否暂停加载
     */
    private boolean mPaused;
    
    /**
     * PhotoLoader 的构造
     * @param context content context
     * @param defaultResourceId 默认头像id
     * @param width 需要生成的头像宽
     * @param height 需要生成的头像高
     * @param contactType 获取头像的数据来源:好友or联系人
     * @param listener 监听联网获取数据的listener
     */
    public PhotoLoader(Context context, int defaultResourceId, int width,
            int height, int contactType, ContactPhotoLoaderListener listener)
    {
        mDefaultResourceId = defaultResourceId;
        mContext = context;
        imageWidth = width;
        imageHeight = height;
        type = contactType;
        mListenter = listener;
    }
    
    /**
     * 
     * 异步加载头像<BR>
     * 如果头像已经在缓存中，则直接显示，如果不在，则加入到请求队列中
     * @param view 要设置头像的imageView
     * @param faceUrl 头像的url，如果是联系人，则是头像的ID
     */
    public void loadPhoto(ImageView view, String faceUrl)
    {
        if (StringUtil.isNullOrEmpty(faceUrl)
                || (type == SOURCE_TYPE_CONTACT && Integer.parseInt(faceUrl) == 0))
        {
            // 没有头像，设置为默认
            view.setImageResource(mDefaultResourceId);
            mPendingRequests.remove(view);
        }
        else
        {
            boolean loaded = loadCachedPhoto(view, faceUrl);
            if (loaded)
            {
                mPendingRequests.remove(view);
            }
            else
            {
                mPendingRequests.put(view, faceUrl);
                if (!mPaused)
                {
                    // Send a request to start loading photos
                    requestLoading();
                }
            }
        }
    }
    
    /**
     * Checks if the photo is present in cache. If so, sets the photo on the
     * view, otherwise sets the state of the photo to
     * {@link BitmapHolder#NEEDED} and temporarily set the image to the default
     * resource ID.
     */
    private boolean loadCachedPhoto(ImageView view, String faceUrl)
    {
        // 判断是否有标记不能异步加载
        if (view.getTag() != null
                && (NOT_AVAIABLE_VIEW == (Integer) view.getTag() || type != (Integer) view.getTag()))
        {
            return false;
        }
        BitmapHolder holder = mBitmapCache.get(faceUrl);
        if (holder == null)
        {
            holder = new BitmapHolder();
            mBitmapCache.put(faceUrl, holder);
        }
        else if (holder.state == BitmapHolder.LOADED)
        {
            //没有找到头像数据，设置为默认
            if (holder.drawableRef == null)
            {
                view.setImageResource(mDefaultResourceId);
                return true;
            }
            
            Drawable drawable = holder.drawableRef.get();
            if (drawable != null)
            {
                view.setImageDrawable(drawable);
                return true;
            }
            
            //头像在弱缓存中保存过，但是被GC回收了，需要重新加载
            holder.drawableRef = null;
        }
        
        //头像还没有被加载，需要加载
        view.setImageResource(mDefaultResourceId);
        holder.state = BitmapHolder.NEEDED;
        return false;
    }
    
    /**
     * 
     * 停止加载头像，清除所有缓存<BR>
     * [功能详细描述]
     */
    public void stop()
    {
        pause();
        
        if (mLoaderThread != null)
        {
            mLoaderThread.quit();
            mLoaderThread = null;
        }
        
        mPendingRequests.clear();
        mBitmapCache.clear();
    }
    
    /**
     * 
     * 清空缓存<BR>
     * [功能详细描述]
     */
    public void clear()
    {
        mPendingRequests.clear();
        mBitmapCache.clear();
    }
    
    /**
     * 
     * 暂停加载头像<BR>
     * activity pause 时，可以调用此方法
     */
    public void pause()
    {
        mPaused = true;
    }
    
    /**
     * 
     * 恢复加载头像<BR>
     * activity resume 时，可以调用此方法
     */
    public void resume()
    {
        mPaused = false;
        if (!mPendingRequests.isEmpty())
        {
            requestLoading();
        }
    }
    
    /**
     * 
     * 请求加载头像<BR>
     * [功能详细描述]
     */
    private void requestLoading()
    {
        if (!mLoadingRequested)
        {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }
    
    /**
     * 
     * 发msg给主线程处理<BR>
     * [功能详细描述]
     * @param msg msg
     * @return boolean 
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    public boolean handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case MESSAGE_REQUEST_LOADING:
            {
                mLoadingRequested = false;
                if (!mPaused)
                {
                    if (mLoaderThread == null)
                    {
                        mLoaderThread = new LoaderThread(
                                mContext.getContentResolver());
                        mLoaderThread.start();
                    }
                    
                    mLoaderThread.requestLoading();
                }
                return true;
            }
            
            case MESSAGE_PHOTOS_LOADED:
            {
                if (!mPaused)
                {
                    processLoadedImages();
                }
                return true;
            }
            default:
                break;
        }
        return false;
    }
    
    /**
     * 
     * 开始加载头像<BR>
     * [功能详细描述]
     */
    private void processLoadedImages()
    {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext())
        {
            ImageView view = iterator.next();
            String faceUrl = mPendingRequests.get(view);
            boolean loaded = loadCachedPhoto(view, faceUrl);
            if (loaded)
            {
                iterator.remove();
            }
        }
        
        if (!mPendingRequests.isEmpty())
        {
            requestLoading();
        }
    }
    
    /**
     * 
     * 根据faceUrl 缓存头像数据<BR>
     * [功能详细描述]
     * @param faceUrl 头像URL
     * @param bytes 头像数据
     */
    private void cacheDrawableByBytes(String faceUrl, byte[] bytes)
    {
        if (mPaused)
        {
            return;
        }
        
        BitmapHolder holder = new BitmapHolder();
        holder.state = BitmapHolder.LOADED;
        if (bytes != null)
        {
            try
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,
                        0,
                        bytes.length,
                        null);
                Drawable drawable = null;
                if (bitmap != null)
                {
                    drawable = ImageUtil.drawRoundCornerForDrawable(bitmap,
                            imageWidth,
                            imageHeight,
                            8);
                }
                holder.drawableRef = new SoftReference<Drawable>(drawable);
            }
            catch (OutOfMemoryError e)
            {
                Log.e(TAG, "load failed == " + e.toString());
            }
        }
        mBitmapCache.put(faceUrl, holder);
    }
    
    /**
     * 
     * 根据faceUrl 缓存头像数据<BR>
     * [功能详细描述]
     * @param faceUrl 头像URL
     * @param drawable 头像数据
     */
    private void cacheDrawableByDrawable(String faceUrl, Drawable drawable)
    {
        if (mPaused)
        {
            return;
        }
        
        BitmapHolder holder = new BitmapHolder();
        holder.state = BitmapHolder.LOADED;
        if (drawable != null)
        {
            holder.drawableRef = new SoftReference<Drawable>(drawable);
        }
        mBitmapCache.put(faceUrl, holder);
    }
    
    /**
     * 
     * 根据faceUrl 缓存头像数据<BR>
     * [功能详细描述]
     * @param faceUrl 头像URL
     * @param Bitmap 头像数据
     */
    private void cacheDrawableByBitmap(String faceUrl, Bitmap bitmap)
    {
        if (mPaused)
        {
            return;
        }
        
        BitmapHolder holder = new BitmapHolder();
        holder.state = BitmapHolder.LOADED;
        if (bitmap != null)
        {
            holder.drawableRef = new SoftReference<Drawable>(
                    ImageUtil.drawRoundCornerForDrawable(bitmap,
                            imageWidth,
                            imageHeight,
                            8));
        }
        mBitmapCache.put(faceUrl, holder);
    }
    
    /**
     * 
     * 获取需要加载的url list<BR>
     * [功能详细描述]
     * @param faceUrls 获得的URL list
     */
    private void obtainFaceUrlsToLoad(ArrayList<String> faceUrls)
    {
        faceUrls.clear();
        Iterator<String> iterator = mPendingRequests.values().iterator();
        while (iterator.hasNext())
        {
            String faceUrl = iterator.next();
            BitmapHolder holder = mBitmapCache.get(faceUrl);
            if (holder != null && holder.state == BitmapHolder.NEEDED)
            {
                // Assuming atomic behavior
                holder.state = BitmapHolder.LOADING;
                faceUrls.add(faceUrl);
            }
        }
    }
    
    /**
     * 
     * 获取头像数据的工作线程<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-7]
     */
    private class LoaderThread extends HandlerThread implements Callback
    {
        private final ContentResolver mResolver;
        
        private final StringBuilder mStringBuilder = new StringBuilder();
        
        private final ArrayList<String> mFaceUrls = new ArrayList<String>();
        
        private final ArrayList<String> loadingUrls = new ArrayList<String>();
        
        private final FaceThumbnailDbAdapter mFaceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(mContext);
        
        private final GroupMemberDbAdapter mGroupMemberDbAdapter = GroupMemberDbAdapter.getInstance(mContext);
        
        private Handler mLoaderThreadHandler;
        
        public LoaderThread(ContentResolver resolver)
        {
            super(TAG);
            mResolver = resolver;
        }
        
        /**
         * Sends a message to this thread to load requested photos.
         */
        public void requestLoading()
        {
            if (mLoaderThreadHandler == null)
            {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }
        
        /**
         * Receives the above message, loads photos and then sends a message to
         * the main thread to process them.
         */
        public boolean handleMessage(Message msg)
        {
            if (type == SOURCE_TYPE_CONTACT)
            {
                loadPhotosFromContactDatabase();
            }
            else if (type == SOURCE_TYPE_FRIEND || type == SOURCE_TYPE_GROUP)
            {
                loadPhotoFromFriendDataBase();
            }
            else if (type == SOURCE_TYPE_CHAT_BAR)
            {
                loadPhotoFromGroupMemberDataBase();
            }
            
            mMainThreadHandler.sendEmptyMessage(MESSAGE_PHOTOS_LOADED);
            return true;
        }
        
        private void loadPhotosFromContactDatabase()
        {
            obtainFaceUrlsToLoad(mFaceUrls);
            
            int count = mFaceUrls.size();
            if (count == 0)
            {
                return;
            }
            
            mStringBuilder.setLength(0);
            mStringBuilder.append(Photo._ID + " IN(");
            for (int i = 0; i < count; i++)
            {
                if (i != 0)
                {
                    mStringBuilder.append(',');
                }
                mStringBuilder.append('?');
            }
            mStringBuilder.append(')');
            
            Cursor cursor = null;
            try
            {
                cursor = mResolver.query(Data.CONTENT_URI,
                        CONTACT_PROJECTION,
                        mStringBuilder.toString(),
                        mFaceUrls.toArray(EMPTY_STRING_ARRAY),
                        null);
                
                if (cursor != null)
                {
                    while (cursor.moveToNext())
                    {
                        String faceId = cursor.getString(0);
                        byte[] bytes = cursor.getBlob(1);
                        cacheDrawableByBytes(faceId, bytes);
                        mFaceUrls.remove(faceId);
                    }
                }
            }
            finally
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            }
            
            // Remaining photos were not found in the database - mark the cache
            // accordingly.
            count = mFaceUrls.size();
            for (int i = 0; i < count; i++)
            {
                cacheDrawableByBytes(mFaceUrls.get(i), null);
            }
        }
        
        private void loadPhotoFromGroupMemberDataBase()
        {
            obtainFaceUrlsToLoad(mFaceUrls);
            List<GroupMemberModel> lists = null;
            for (String groupId : mFaceUrls)
            {
                lists = mGroupMemberDbAdapter.queryByGroupId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupId);
                if (null != lists && lists.size() > 0)
                {
                    List<Drawable> drawables = new ArrayList<Drawable>();
                    //获取有多少个成员
                    int size = lists.size();
                    size = size > MIN_FACE_COUNT ? MIN_FACE_COUNT : size;
                    Drawable drawable = null;
                    Drawable defaultDrawable = mContext.getResources()
                            .getDrawable(R.drawable.default_contact_icon);
                    String url = null;
                    byte[] faceBytes = null;
                    for (int i = 0; i < size; i++)
                    {
                        url = lists.get(i).getMemberFaceUrl();
                        faceBytes = lists.get(i).getMemberFaceBytes();
                        if (SystemFacesUtil.isSystemFaceUrl(url))
                        {
                            drawable = mContext.getResources()
                                    .getDrawable(SystemFacesUtil.getFaceImageResourceIdByFaceUrl(url));
                        }
                        else
                        {
                            SoftReference<Bitmap> face = null;
                            if (faceBytes != null)
                            {
                                try
                                {
                                    face = new SoftReference<Bitmap>(
                                            BitmapFactory.decodeByteArray(faceBytes,
                                                    0,
                                                    faceBytes.length));
                                }
                                catch (OutOfMemoryError e)
                                {
                                    Log.e(TAG, "load failed == " + e.toString());
                                }
                            }
                            if (face != null && face.get() != null)
                            {
                                drawable = ImageUtil.drawRoundCornerForDrawable(face.get(),
                                        (imageWidth - 6) / 2,
                                        (imageHeight - 6) / 2,
                                        4);
                            }
                            else
                            {
                                drawable = defaultDrawable;
                            }
                        }
                        drawables.add(drawable);
                    }
                    cacheDrawableByBitmap(groupId,
                            ImageUtil.createChatBarBitmap(R.drawable.chatbar_bg,
                                    drawables,
                                    imageWidth,
                                    imageHeight,
                                    mContext));
                    
                }
                
            }
        }
        
        private void loadPhotoFromFriendDataBase()
        {
            obtainFaceUrlsToLoad(mFaceUrls);
            int count = mFaceUrls.size();
            if (count > 0)
            {
                FaceThumbnailModel ftm = null;
                for (int i = 0; i < count; i++)
                {
                    final String url = mFaceUrls.get(i);
                    //先判断是否是系统头像
                    if (SystemFacesUtil.isSystemFaceUrl(url))
                    {
                        cacheDrawableByDrawable(url,
                                mContext.getResources()
                                        .getDrawable(SystemFacesUtil.getFaceImageResourceIdByFaceUrl(url)));
                    }
                    //不是系统头像，查看数据库中是否有数据
                    else
                    {
                        ftm = mFaceThumbnailDbAdapter.queryByFaceUrl(url);
                        //如果有，判断是否有字节，有，直接缓存，没有，联网获取
                        if (null != ftm)
                        {
                            if (null != ftm.getFaceBytes())
                            {
                                cacheDrawableByBytes(url, ftm.getFaceBytes());
                            }
                            else if (!loadingUrls.contains(url))
                            {
                                Logger.d(TAG, "=====联网获取头像=====" + url);
                                loadingUrls.add(url);
                                final String faceId = ftm.getFaceId();
                                new FaceManager().loadFaceIcon(faceId,
                                        url,
                                        new IHttpListener()
                                        {
                                            
                                            @Override
                                            public void onResult(int action,
                                                    Response response)
                                            {
                                                
                                                loadingUrls.remove(url);
                                                if (response.getResponseCode() == ResponseCode.Succeed
                                                        && response.getByteData() != null
                                                        && response.getByteData().length > 0)
                                                {
                                                    
                                                    if (mFaceThumbnailDbAdapter.queryByFaceId(faceId) != null)
                                                    {
                                                        mFaceThumbnailDbAdapter.updateByFaceId(faceId,
                                                                new FaceThumbnailModel(
                                                                        faceId,
                                                                        url,
                                                                        response.getByteData()));
                                                    }
                                                    else
                                                    {
                                                        mFaceThumbnailDbAdapter.insertFaceThumbnail(new FaceThumbnailModel(
                                                                faceId,
                                                                url,
                                                                response.getByteData()));
                                                    }
                                                    cacheDrawableByBytes(url,
                                                            response.getByteData());
                                                    if (mListenter != null)
                                                    {
                                                        mListenter.onPhotoLoaded();
                                                    }
                                                    mMainThreadHandler.sendEmptyMessage(MESSAGE_PHOTOS_LOADED);
                                                    
                                                }
                                                else
                                                {
                                                    switch (response.getResponseCode())
                                                    {
                                                        case Forbidden:
                                                        case NotFound:
                                                        case InternalError:
                                                            cacheDrawableByDrawable(url,
                                                                    mContext.getResources()
                                                                            .getDrawable(mDefaultResourceId));
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                
                                            }
                                            
                                            @Override
                                            public void onProgress(
                                                    boolean isInProgress)
                                            {
                                                
                                            }
                                        });
                            }
                        }
                        else
                        {
                            cacheDrawableByDrawable(url,
                                    mContext.getResources()
                                            .getDrawable(mDefaultResourceId));
                        }
                        
                    }
                }
            }
        }
    }
    
    /**
     * 
     * 加载头像的Holder<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-7]
     */
    private static class BitmapHolder
    {
        private static final int NEEDED = 0;
        
        private static final int LOADING = 1;
        
        private static final int LOADED = 2;
        
        private int state;
        
        private SoftReference<Drawable> drawableRef;
    }
}
