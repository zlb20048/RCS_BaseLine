/*
 * 文件名: SetHeadUtilActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tjzhang
 * 创建时间:2012-3-27
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Window;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionAction.CropImageAction;
import com.huawei.basic.android.im.common.FusionAction.SetHeadUtilAction;
import com.huawei.basic.android.im.common.FusionAction.SetSystemHeadAction;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.basic.android.im.utils.UriUtil.FromType;
import com.huawei.basic.android.im.utils.UriUtil.LocalDirType;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-27] 
 */
public class SetHeadUtilActivity extends BasicActivity
{
    /**
     * TAG
     */
    public static final String TAG = "SetHeadUtilActivtiy";
    
    /**
     * 跳转到裁剪页面请求的code
     */
    private static final int PHOTO_RESOULT = 100;
    
    /**
     * 跳转到拍照的Action
     */
    private static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    
    /**
     * 存放到bundle中的key
     */
    private static final String KEY_IMAGE_FILE = "imageFile";
    /**
     * 存放到bundle中的key
     */
    private static final String KEY_PHOTO_BYTES = "mPhotoBytes";
    
    /**
     * logic对象
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 头像数据
     */
    private byte[] mPhotoBytes;
    
    /**
     * 用于保存从系统获得的头像图片
     */
    private File imageFile;
    
    /**
     * 保存的bitmap
     */
    private Bitmap bitmap;
    
    private ProgressDialog mProgressDialog;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (null == savedInstanceState)
        {
            dispatch(getIntent().getExtras());
        }
        else
        {
            imageFile = (File) savedInstanceState.getSerializable(KEY_IMAGE_FILE);
            mPhotoBytes = savedInstanceState.getByteArray(KEY_PHOTO_BYTES);
        }
    }
    
    /**
     * 
     * 根据不同的模式跳转到不同的页面<BR>
     * [功能详细描述]
     * @param bundle 从前一个页面传进来的参数
     */
    private void dispatch(Bundle bundle)
    {
        int mode = bundle.getInt(SetHeadUtilAction.EXTRA_MODE);
        Logger.d(TAG, "  mode === " + mode);
        Intent intent = new Intent();
        switch (mode)
        {
            //用户选择系统头像
            case SetHeadUtilAction.MODE_SYSTEM:
                int systemMode = bundle.getInt(SetHeadUtilAction.EXTRA_SYSTEM_HEAD_MODE,
                        SetSystemHeadAction.MODE_PERSON);
                intent.setAction(SetSystemHeadAction.ACTION);
                intent.putExtra(SetSystemHeadAction.EXTRA_MODE, systemMode);
                startActivityForResult(intent, mode);
                break;
            //用户从图库中选择头像
            case SetHeadUtilAction.MODE_FILE:
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, mode);
                break;
            //调用照相机拍照
            case SetHeadUtilAction.MODE_CAMERA:
                if (Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED))
                {
                    //给刚要拍照的照片命名
                    String fileName = DateUtil.getCurrentDateString() + ".jpg";
                    String path = UriUtil.getLocalStorageDir(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserID(),
                            FromType.SEND,
                            LocalDirType.IMAGE);
                    //当sd卡空间小于某个值，还没有到达最低值时,path满足以下条件
                    if (StringUtil.isNullOrEmpty(path))
                    {
                        showToast(R.string.sdcard_not_enougth_space);
                        finishActivity();
                    }
                    else
                    {
                        File picDir = new File(path);
                        imageFile = new File(picDir, fileName);
                        //跳转到拍照界面
                        intent.setAction(ACTION_IMAGE_CAPTURE);
                        // Samsung的系统相机，版式是横板的,同时此activity不要设置单例模式
                        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(imageFile));
                        // 调用系统拍照
                        startActivityForResult(intent, mode);
                    }
                }
                else
                {
                    // 给出提示:请插入SD卡，再进行操作
                    showToast(R.string.setting_insert_SD_card);
                    finishActivity();
                }
                break;
            default:
                finishActivity();
                break;
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.d(TAG, "onActivityResult = " + requestCode + " :  " + resultCode);
        if (RESULT_OK == resultCode)
        {
            switch (requestCode)
            {
                case SetHeadUtilAction.MODE_SYSTEM:
                {
                    String url = data.getStringExtra(SetSystemHeadAction.EXTRA_HEAD_URL);
                    Logger.d(TAG, "url = " + url);
                    Intent intent = new Intent();
                    intent.putExtra(SetHeadUtilAction.EXTRA_URL, url);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }
                case SetHeadUtilAction.MODE_FILE:
                {
                    Uri selectedContentUri = data.getData();
                    String selectedContentPath = null;
                    if (selectedContentUri.toString().startsWith("content"))
                    {
                        selectedContentPath = getPath(selectedContentUri);
                    }
                    else
                    {
                        selectedContentPath = selectedContentUri.toString()
                                .substring(selectedContentUri.toString()
                                        .indexOf(":") + 3);
                    }
                    Logger.d(TAG, "解码前文件的路径:" + selectedContentPath);
                    String decoded = null;
                    try
                    {
                        //TODO:这边暂时使用UTF-8解码方式，以后可能会有机型适配问题[等出了再修改]
                        decoded = URLDecoder.decode(selectedContentPath,
                                "UTF-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        Log.e(TAG,
                                "onActivityResult()解码发生异常....使用android中不被推荐的方法解码!");
                        //e.printStackTrace();
                        //TODO:使用不被推荐方法来解码
                        decoded = URLDecoder.decode(selectedContentPath);
                    }
                    Logger.d(TAG, "解码后文件的路径:" + decoded);
                    File file = new File(decoded);
                    if (!FileUtil.isPictureType(file.getName()))
                    {
                        showToast(R.string.setting_select_type_not_allow);
                        finishActivity();
                    }
                    else
                    {
                        startPhotoResoult(decoded);
                    }
                    break;
                }
                case SetHeadUtilAction.MODE_CAMERA:
                {
                    // 机型适配（不同手机返回的地址不一样）
                    if (null != data && null != data.getData())
                    {
                        startPhotoResoult(getPath(data.getData()));
                    }
                    else
                    {
                        if (imageFile != null)
                        {
                            startPhotoResoult(imageFile.getPath());
                        }
                    }
                    break;
                }
                case PHOTO_RESOULT:
                {
                    if (data != null)
                    {
                        mPhotoBytes = data.getByteArrayExtra("data");
                        // 开始上传头像
                        uploadUserIcon("icon.png", mPhotoBytes);
                    }
                    else
                    {
                        finishActivity();
                    }
                    break;
                }
                    
                default:
                    break;
            }
        }
        else
        {
            finishActivity();
        }
    }
    
    /**
     * 操作不成功 回调
     */
    private void finishActivity()
    {
        closeProgressDialog();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    
    /**
     * TODO 上传用户头像
     */
    private void uploadUserIcon(String contentName, byte[] photoBytes)
    {
        Logger.d(TAG, "uploadUserIcon(),显示进度对话框，准备上传头像");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.prompt));
        mProgressDialog.setMessage(getString(R.string.setting_upload_head));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        //调用logic上传头像
        mSettingsLogic.uploadUserFace(contentName, photoBytes);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        //super.initLogics();
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed()
    {
        finishActivity();
    }
    
    /**
     * 
     * 跳转到图片裁剪页面<BR>
     * 
     * @param uri 跳转请求的Uri
     */
    private void startPhotoResoult(String path)
    {
        Intent intent = new Intent(CropImageAction.ACTION);
        intent.putExtra(CropImageAction.EXTRA_MODE, CropImageAction.MODE_CROP);
        intent.putExtra(CropImageAction.EXTRA_PATH, path);
        startActivityForResult(intent, PHOTO_RESOULT);
    }
    
    /**
     * 取得文件所在的路径
     * 通过给定的文件URI得到文件所在的路径
     * @param uri 指定的文件URL
     * @return 文件所在的路径
     */
    private String getPath(Uri uri)
    {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        cursor.close();
        cursor = null;
        return path;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        if (null != bitmap && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
        if (null != imageFile)
        {
            //删除图片文件
            imageFile.delete();
        }
        super.onDestroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int what;
        what = msg.what;
        
        //TODO:在这边关闭Dialog
        if (null != mProgressDialog && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
        switch (what)
        {
            case SettingsMessageType.FACE_UPLOAD_SUCCESS:
                String downloadUrl = (String) msg.obj;
                if (null != downloadUrl)
                {
                    Intent intent = new Intent();
                    intent.putExtra(SetHeadUtilAction.EXTRA_BYTES, mPhotoBytes);
                    intent.putExtra(SetHeadUtilAction.EXTRA_URL, downloadUrl);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    finishActivity();
                }
                break;
            case SettingsMessageType.FACE_UPLOAD_FAILED:
                //上传头像失败
                Logger.d(TAG, "handleStateMessage() 上传头像失败");
                //TODO:显示Toast
                showToast(R.string.setting_upload_failure);
                finishActivity();
                break;
            default:
                break;
        }
        
        super.handleStateMessage(msg);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //在某些型号手机中【MOTOME863中】拍完照返回的时候会重新启动Activity
        outState.putSerializable(KEY_IMAGE_FILE, imageFile);
        outState.putByteArray(KEY_PHOTO_BYTES, mPhotoBytes);
        super.onSaveInstanceState(outState);
    }
}
