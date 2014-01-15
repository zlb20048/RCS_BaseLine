/*
 * 文件名: AccountDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 8, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.AccountColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 用户账号数据操作适配器<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-16]
 */
public class AccountDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "AccountDbAdapter";
    
    /**
     * 表中存储数据，使用数字代替布尔值，值为1时，表示true 
     */
    private static final int TRUE_VALUE = 1;
    
    /**
     * 表中存储数据，使用数字代替布尔值，值为0时，表示false 
     */
    private static final int FALSE_VALUE = 0;
    
    /**
     * AccountAdapter对象
     */
    private static AccountDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private AccountDbAdapter(Context context)
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
    public static synchronized AccountDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new AccountDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入账号信息<BR>
     * 
     * @param account 插入对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertAccount(AccountModel account)
    {
        long result = -1;
        if (null != account)
        {
            Uri uri = URIField.ACCOUNT_URI;
            ContentValues cv = setValues(account);
            
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertAccount, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertAccount fail, account is null...");
        }
        return result;
    }
    
    /**
     * 根据登录账号删除 账号信息<BR>
     * 
     * @param loginAccount 登录账号
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByLoginAccount(String loginAccount)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(loginAccount))
        {
            Uri uri = URIField.ACCOUNT_URI;
            result = mCr.delete(uri,
                    AccountColumns.LOGIN_ACCOUNT + "=?",
                    new String[] { loginAccount });
            
            Logger.i(TAG, "deleteByLoginAccount, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByLoginAccount fail, loginAccount is null...");
        }
        return result;
    }
    
    /**
     * 根据登录账号修改账号信息。<BR>
     * [全量修改] 传入参数account的所有属性都会被修改
     * 
     * @param account 需要修改的账号对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByLoginAccount(AccountModel account)
    {
        int result = -1;
        if (null != account
                && !StringUtil.isNullOrEmpty(account.getLoginAccount()))
        {
            Uri uri = URIField.ACCOUNT_URI;
            
            ContentValues cv = setValues(account);
            
            result = mCr.update(uri,
                    cv,
                    AccountColumns.LOGIN_ACCOUNT + "=?",
                    new String[] { account.getLoginAccount() });
            Logger.i(TAG, "updateAccount, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateAccount fail, loginAccount or params is null...");
        }
        return result;
    }
    
    /**
     * 根据userSysId修改账号信息。<BR>
     * [全量修改] 传入参数account的所有属性都会被修改
     * 
     * @param userSysId 用户系统id
     * @param account 需要修改的账号对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByUserSysId(String userSysId, AccountModel account)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != account)
        {
            Uri uri = URIField.ACCOUNT_URI;
            
            ContentValues cv = setValues(account);
            
            result = mCr.update(uri,
                    cv,
                    AccountColumns.USER_SYSID + "=?",
                    new String[] { userSysId });
            Logger.i(TAG, "updateAccount, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateAccount fail, loginAccount or params is null...");
        }
        return result;
    }
    
    /**
     * 根据loginAccount修改账号信息部分字段<BR>
     * [通用方法]
     * 
     * @param loginAccount 登录账号
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByLoginAccount(String loginAccount, ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(loginAccount) && null != cv
                && cv.size() > 0)
        {
            Uri uri = URIField.ACCOUNT_URI;
            result = mCr.update(uri,
                    cv,
                    AccountColumns.LOGIN_ACCOUNT + "=?",
                    new String[] { loginAccount });
            Logger.i(TAG, "updateAccount, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateAccount fail, loginAccount or params is null...");
        }
        return result;
    }
    
    /**
     * 根据userSysId修改账号信息部分字段<BR>
     * [通用方法]
     * 
     * @param userSysId 用户系统id
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByUserSysId(String userSysId, ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != cv && cv.size() > 0)
        {
            Uri uri = URIField.ACCOUNT_URI;
            result = mCr.update(uri,
                    cv,
                    AccountColumns.USER_SYSID + "=?",
                    new String[] { userSysId });
            Logger.i(TAG, "updateByUserSysId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateAccount fail, loginAccount or params is null...");
        }
        return result;
    }
    
    /**
     * 根据登录账号查询账号信息<BR>
     * 
     * @param loginAccount 登录账号
     * @return 成功：AccountModel对象<br>
     *         失败：null
     */
    public AccountModel queryByLoginAccount(String loginAccount)
    {
        AccountModel account = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(loginAccount))
            {
                Uri uri = URIField.ACCOUNT_URI;
                cursor = mCr.query(uri, null, AccountColumns.LOGIN_ACCOUNT
                        + "=?", new String[] { loginAccount }, null);
                if (null != cursor && cursor.moveToFirst())
                {
                    account = parseCursorToAccount(cursor);
                }
            }
            else
            {
                Logger.w(TAG,
                        "queryByLoginAccount fail, loginAccount is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return account;
    }
    
    /**
     * 根据userSysId查询账号信息<BR>
     * 
     * @param userSysId 用户系统唯一标识
     * @return 成功：AccountModel对象<br>
     *         失败：null
     */
    public AccountModel queryByUserSysId(String userSysId)
    {
        AccountModel account = null;
        Cursor cursor = null;
        try
        {
            cursor = this.queryByUserSysIdWithCursor(userSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                account = parseCursorToAccount(cursor);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return account;
    }
    
    /**
     * 根据登录账号查询账号信息<BR>
     * 
     * @param userSysId 用户JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByUserSysIdWithCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.ACCOUNT_URI;
            cursor = mCr.query(uri,
                    null,
                    AccountColumns.USER_SYSID + "=?",
                    new String[] { userSysId },
                    null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByUserIdWithCursor fail, loginAccount is null...");
        }
        return cursor;
    }
    
    /**
     * 解绑邮箱/手机号<BR>
     * @param unbindInfo 解绑的手机号/邮箱字符串
     * @param userId 用户登陆系统的ID
     * @param userSysId 用户系统ID
     * @return 解绑成功与否
     */
    public boolean unbindAccount(String unbindInfo, String userId,
            String userSysId)
    {
        boolean result = false;
        Cursor cursor = null;
        try
        {
            if (StringUtil.isNullOrEmpty(unbindInfo)
                    || StringUtil.isNullOrEmpty(userId)
                    || StringUtil.isNullOrEmpty(userSysId))
            {
                return result;
            }
            Uri uri = URIField.ACCOUNT_URI;
            String[] projection = new String[] { AccountColumns.LOGIN_ACCOUNT,
                    AccountColumns.PASSWORD };
            String selection = AccountColumns.USER_SYSID + " =? ";
            String[] selectionArgs = new String[] { userSysId };
            cursor = this.mCr.query(uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            String loginAccount = null;
            String password = null;
            if (null != cursor && cursor.moveToFirst())
            {
                loginAccount = cursor.getString(cursor.getColumnIndex(AccountColumns.LOGIN_ACCOUNT));
                password = cursor.getString(cursor.getColumnIndex(AccountColumns.PASSWORD));
                //如果解绑的数据与账号相同才进行更新
                if (StringUtil.equals(unbindInfo, loginAccount))
                {
                    //先将数据库中的密码进行解密，获得未加密的密码字符串
                    password = DecodeUtil.decrypt(loginAccount, password);
                    //将密码与userId组合进行加密获得新密码
                    password = DecodeUtil.encrypt(loginAccount, userId);
                    ContentValues values = new ContentValues();
                    values.put(AccountColumns.LOGIN_ACCOUNT, userId);
                    values.put(AccountColumns.PASSWORD, password);
                    this.mCr.update(uri, values, selection, selectionArgs);
                    result = true;
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return result;
    }
    
    /**
     * 查询所有登录过的账号信息<BR>
     * 
     * @return 成功：账号信息列表 <br>
     *         失败：null
     */
    public List<AccountModel> queryAllAccounts()
    {
        List<AccountModel> accounts = null;
        Cursor cursor = null;
        try
        {
            AccountModel account = null;
            cursor = queryAllAccountsWithCursor();
            
            if (null != cursor && cursor.moveToFirst())
            {
                accounts = new ArrayList<AccountModel>();
                while (!cursor.isAfterLast())
                {
                    account = parseCursorToAccount(cursor);
                    accounts.add(account);
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return accounts;
    }
    
    /**
     * 查询所有登录过的账号信息<BR>
     * 
     * @return 成功：Cursor<br>
     *         失败：null
     */
    public Cursor queryAllAccountsWithCursor()
    {
        Cursor cursor = null;
        Uri uri = URIField.ACCOUNT_URI;
        cursor = mCr.query(uri, null, null, null, AccountColumns.LOGIN_ACCOUNT);
        return cursor;
    }
    
    /**
     * 根据游标解析用户账号信息<BR>
     * 
     * @param cursor 游标对象
     * @return 账号对象
     */
    private AccountModel parseCursorToAccount(Cursor cursor)
    {
        AccountModel account = new AccountModel();
        account.setLoginAccount(cursor.getString(cursor.getColumnIndex(AccountColumns.LOGIN_ACCOUNT)));
        account.setTimestamp(cursor.getString(cursor.getColumnIndex(AccountColumns.TIMESTAMP)));
        account.setPassword(cursor.getString(cursor.getColumnIndex(AccountColumns.PASSWORD)));
        account.setAutoLogin(cursor.getInt(cursor.getColumnIndex(AccountColumns.AUTOLOGIN)) == TRUE_VALUE);
        account.setLastStatus(cursor.getInt(cursor.getColumnIndex(AccountColumns.LAST_STATUS)));
        account.setUserSysId(cursor.getString(cursor.getColumnIndex(AccountColumns.USER_SYSID)));
        account.setUserId(cursor.getString(cursor.getColumnIndex(AccountColumns.USER_ID)));
        account.setUserStatus(cursor.getString(cursor.getColumnIndex(AccountColumns.USER_STATUS)));
        account.setBindMobile(cursor.getString(cursor.getColumnIndex(AccountColumns.BIND_MOBILE)));
        account.setBindEmail(cursor.getString(cursor.getColumnIndex(AccountColumns.BIND_EMAIL)));
        return account;
        
    }
    
    /**
     * 封装账号对象<BR>
     * 
     * @param account 账号
     * @return ContentValues
     */
    private ContentValues setValues(AccountModel account)
    {
        ContentValues cv = new ContentValues();
        cv.put(AccountColumns.LOGIN_ACCOUNT, account.getLoginAccount());
        cv.put(AccountColumns.TIMESTAMP, account.getTimestamp());
        cv.put(AccountColumns.PASSWORD, account.getPassword());
        cv.put(AccountColumns.AUTOLOGIN, account.getAutoLogin() ? TRUE_VALUE
                : FALSE_VALUE);
        cv.put(AccountColumns.LAST_STATUS, account.getLastStatus());
        cv.put(AccountColumns.USER_SYSID, account.getUserSysId());
        cv.put(AccountColumns.USER_ID, account.getUserId());
        cv.put(AccountColumns.USER_STATUS, account.getUserStatus());
        cv.put(AccountColumns.BIND_MOBILE, account.getBindMobile());
        cv.put(AccountColumns.BIND_EMAIL, account.getBindEmail());
        return cv;
    }
    
}
