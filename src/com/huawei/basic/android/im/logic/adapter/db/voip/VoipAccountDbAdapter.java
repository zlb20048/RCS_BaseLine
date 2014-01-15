/*
 * 文件名: VoipAccountDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db.voip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.Tables;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.VoipAccountColumns;
import com.huawei.basic.android.im.logic.model.voip.VoipAccount;
import com.huawei.basic.android.im.utils.DecodeUtil;

/**
 * Voip账号数据操作适配器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 15, 2012] 
 */
public class VoipAccountDbAdapter extends BaseDbAdapter
{
    /**
     * 单例对象
     */
    private static VoipAccountDbAdapter mInstance = null;
    
    /**
     * 构造方法
     * @param context
     *      Context对象
     */
    protected VoipAccountDbAdapter(Context context)
    {
        super(context);
    }
    
    /**
     * 获取单例的方法
     * @param context
     *      Context对象
     * @return
     *      当前唯一实例
     */
    public static synchronized VoipAccountDbAdapter getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new VoipAccountDbAdapter(context);
        }
        return mInstance;
    }
    
    /**
     * 获取表名
     * @return
     *      表名
     * @see com.huawei.basic.android.im.logic.adapter.db.voip.BaseDbAdapter#getTableName()
     */
    @Override
    protected String getTableName()
    {
        return Tables.VOIP_ACCOUNT;
    }
    
    /**
     * 保存账号
     * @param voipAccount
     *      Voip账号对象
     */
    public void save(VoipAccount voipAccount)
    {
        super.insert(getContentValues(voipAccount));
    }
    
    /**
     * 根据userSysId查询Voip账号
     * @param ownerUserId
     *      用户的sysId
     * @return
     *      Voip账号
     */
    public VoipAccount findVoipAccountByOwnerUserId(String ownerUserId)
    {
        Cursor cursor = super.query(null, VoipAccountColumns.OWNER_USER_ID
                + "=?", new String[] { ownerUserId }, null);
        try
        {
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    VoipAccount voipAccount = parseCursorToVoipAccount(cursor);
                    return voipAccount;
                }
            }
            return null;
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
            return null;
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
            
        }
    }
    
    /**
     * 根据ownerUserId删除Voip账号
     * @param ownerUserId
     *      用户的sysId
     */
    public void deleteVoipAccountByOwnerUserId(String ownerUserId)
    {
        super.delete(VoipAccountColumns.OWNER_USER_ID + "=?",
                new String[] { ownerUserId });
    }
    
    /**
     * 读取游标的数据封装成VOIP账号对象
     * @param cursor
     *      游标对象
     * @return
     *      VOIP账号对象
     */
    private static VoipAccount parseCursorToVoipAccount(Cursor cursor)
    {
        VoipAccount voipAccount = new VoipAccount();
        voipAccount.setId(cursor.getInt(cursor.getColumnIndex(VoipAccountColumns.ID)));
        voipAccount.setOwnerUserId(cursor.getString(cursor.getColumnIndex(VoipAccountColumns.OWNER_USER_ID)));
        String password = cursor.getString(cursor.getColumnIndex(VoipAccountColumns.PASSWORD));
        String aor = DecodeUtil.decrypt(password, cursor.getString(cursor.getColumnIndex(VoipAccountColumns.ACCOUNT)));
        voipAccount.setAccount(aor);
        voipAccount.setPassword(password);
        voipAccount.setCreatedDate(getDateFormCursor(cursor,
                VoipAccountColumns.CREATED_DATE));
        return voipAccount;
    }
    
    /**
    * 封装VoipAccount对象为ContentValues
    * @param voipAccount
    *       VOIP账号对象
    * @return
    *       ContentValues对象
    */
    private static ContentValues getContentValues(VoipAccount voipAccount)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VoipAccountColumns.OWNER_USER_ID,
                voipAccount.getOwnerUserId());
        contentValues.put(VoipAccountColumns.ACCOUNT, voipAccount.getAccount());
        contentValues.put(VoipAccountColumns.PASSWORD,
                voipAccount.getPassword());
        contentValues.put(VoipAccountColumns.CREATED_DATE,
                changeDateToString(voipAccount.getCreatedDate()));
        return contentValues;
    }
    
}
