package com.huawei.basic.android.im.logic.adapter.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.huawei.basic.android.im.component.database.URIField;

/**
 * 获取短信内容的 DbAdapter
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-5-17]
 */
public class SMSDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "SMSDbAdapter";
    
    /**
     * AccountAdapter对象
     */
    private static SMSDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 短信数据库中最大的短信id
     */
    private int mCurMaxId;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private SMSDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取AccountAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return AccountAdapter
     */
    public static synchronized SMSDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new SMSDbAdapter(context);
            sInstance.init();
        }
        return sInstance;
    }
    
    /**
     * 初始化短信数据库中最大的短信id
     */
    private void init()
    {
        String phoneNumber = "1252015195878834";
        mCurMaxId = getSMSMaxId(phoneNumber);
    }
    
    /**
     * 引起短信数据库变化的原因可能会有：接收、删除、保存至草稿箱、读未读的短信等；
     * 获取短信数据库中最大的短信id
     * 以此判断监听的变化是否为：接收的短信 
    * @param phoneNumber  服务器地址
    * @return  短信数据库中与给定服务器号码匹配的最大id
    */
    public int getSMSMaxId(String phoneNumber)
    {
        
        Cursor cursor = null;
        int maxId = 0;
        Uri uri = URIField.SMS_URI;
        String[] projection = new String[] { "MAX(_id)", "address" };
        cursor = mCr.query(uri,
                projection,
                " address like ? ",
                new String[] { phoneNumber },
                "date desc");
        if (null != cursor && cursor.moveToFirst())
        {
            maxId = cursor.getInt(0);
            Log.d(TAG, "getSMSMaxId  " + maxId);
            cursor.close();
        }
        return maxId;
    }
    
    /**
     * 
     * 获取短信内容
     * @param phoneNumber 服务器地址
     * @return 短信内容
     */
    public String getSMSBody(String phoneNumber)
    {
        Uri uri = URIField.SMS_URI;
        String[] projection = new String[] { "_id", "body" };
        Cursor cursor = mCr.query(uri,
                projection,
                " address like ? ",
                new String[] { phoneNumber },
                "date desc");
        
        if (null != cursor && cursor.moveToFirst())
        {
            int id = cursor.getInt(0);
            
            //短信数据库变的原因可能有：
            // 读未读短信修改短信的状态、或是删除短信、保存到草稿箱等
            //如果mCurMaxId>id 表示数据变化不是由接收到短信引起的不作操作
            if (mCurMaxId >= id)
            {
                return null;
            }
            mCurMaxId = id;
            return cursor.getString(1);
        }
        return null;
        
    }
}
