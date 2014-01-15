package com.huawei.basic.android.im.ui.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.BasicDialog;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 通讯录上传进度展示<BR>
 * 
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-5-22]
 */
public abstract class ContactBaseActivity extends BasicActivity implements
        OnClickListener, OnItemClickListener
{
    /**
     * TAG:用于打印Log
     */
    private static final String TAG = "ContactUpload";
    
    /**
     * 重新上传
     */
    private static final String UPLOAD_AGAIN_DIALOG = "upload_again";
    
    /**
     * 取消上传
     */
    private static final String UPLOAD_CONTINUE_DIALOG = "upload_cancel";
    
    /**
     * 上传中
     */
    private static final String UPLOAD_PROGRESSING_DIALOG = "upload_progressing";
    
    /**
     * KEY_OF_DIALOG
     */
    private static final String KEY_OF_DIALOG = "dialogFlag";
    
    /**
     * 当前处于哪个阶段的DIALOG
     */
    private String mDialogFlag;
    
    /**
     * 通讯录Logic
     */
    private IContactLogic mContactLogic;
    
    /**
     * 上传通讯录dialog初始化
     */
    private LayoutInflater inflater;
    
    private View layout;
    
    private TextView textView;
    
    private BasicDialog mBasicDialog;
    
    /**
     * 
     * 获取ContactLogic<BR>
     * @return mContactLogic
     */
    public IContactLogic getContactLogic()
    {
        return mContactLogic;
    }

    /**
     * 
     * 赋值ContactLogic<BR>
     * @param contactLogic contactLogic
     */
    public void setContactLogic(IContactLogic contactLogic)
    {
        this.mContactLogic = contactLogic;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 处理Activity被强行关闭后提示框消失的问题
        if (null != savedInstanceState
                && !StringUtil.isNullOrEmpty(savedInstanceState.getString(KEY_OF_DIALOG)))
        {
            String sDialogFlag = savedInstanceState.getString(KEY_OF_DIALOG);
            if (UPLOAD_PROGRESSING_DIALOG.equals(sDialogFlag))
            {
                initProgressDialog();
            }
            else if (UPLOAD_AGAIN_DIALOG.equals(sDialogFlag))
            {
                uploadContactAgain();
            }
            else if (UPLOAD_CONTINUE_DIALOG.equals(sDialogFlag))
            {
                uploadContactContinue();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int what = msg.what;
        switch (what)
        {
            case ConversationMessageType.UPLOAD_CONTACTS_RUNNING:
                uploadingContact(msg.obj.toString());
                break;
            case ConversationMessageType.UPLOAD_CONTACTS_FINISH:
                if (null != mBasicDialog)
                {
                    closeDialog();
                }
                break;
            case ConversationMessageType.UPLOAD_PROGRESS_FAIL:
                if (null != mBasicDialog)
                {
                    closeDialog();
                    uploadContactAgain();
                    
                }
                break;
            case ConversationMessageType.UPLOAD_CONTACTS_STOPPED:
                if (null != mBasicDialog
                        && mDialogFlag != UPLOAD_CONTINUE_DIALOG)
                {
                    uploadContactContinue();
                }
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * 关闭Dialog<BR>
     * [功能详细描述]
     */
    private void closeDialog()
    {
        if (null != mBasicDialog)
        {
            mBasicDialog.dismiss();
            mBasicDialog = null;
            mDialogFlag = null;
        }
    }
    
    /**
     * 上传通讯录：上传||下次再说<BR>
     * @param msg 提示信息
     */
    protected void uploadFirstContact(Integer msg)
    {
        int msgId = msg == null ? R.string.upload_contacts_msg : msg;
        mBasicDialog = showMessageDialog(R.string.upload_mobile_contacts,
                R.drawable.icon_warning,
                msgId,
                R.string.upload,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface,
                            int id)
                    {
                        // 关闭dialog
                        closeDialog();
                        
                        // 确认上传，初始化进度框
                        initProgressDialog();
                        // 将是否上传通讯录标志插入数据库
                        mContactLogic.insertOrUpdateUploadFlag(true);
                        mContactLogic.beginUpload(true);
                        
                    }
                },
                R.string.upload_contacts_next,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // 关闭dialog
                        closeDialog();
                        // 将是否上传通讯录标志插入数据库
                        mContactLogic.insertOrUpdateUploadFlag(false);
                    }
                });
    }
    
    /**
     * 再次上传：重新上传||下次再说<BR>
     */
    protected void uploadContactAgain()
    {
        
        mDialogFlag = UPLOAD_AGAIN_DIALOG;
        
        mBasicDialog = showMessageDialog(R.string.upload_mobile_contacts,
                R.drawable.icon_warning,
                R.string.upload_contacts_fail_msg,
                R.string.upload_contacts_again,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface,
                            int paramInt)
                    {
                        // 关闭dialog
                        closeDialog();
                        // 确认上传，初始化进度框
                        initProgressDialog();
                        // 将是否上传通讯录标志插入数据库
                        mContactLogic.beginUpload(true);
                    }
                },
                R.string.upload_contacts_next,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface,
                            int paramInt)
                    {
                        // 关闭dialog
                        closeDialog();
                    }
                });
    }
    
    /**
     * 取消再次提示：继续上传||取消<BR>
     */
    protected void uploadContactContinue()
    {
        mDialogFlag = UPLOAD_CONTINUE_DIALOG;
        
        mBasicDialog = showMessageDialog(R.string.upload_mobile_contacts,
                R.drawable.icon_warning,
                R.string.upload_contacts_cancel_msg,
                R.string.upload_contacts_continue,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface,
                            int paramInt)
                    {
                        // 关闭dialog
                        closeDialog();
                        // 将是否上传通讯录标志插入数据库
                        mContactLogic.updateUploadFlag(true);
                        mContactLogic.resumUpload();
                        
                        // 确认上传，初始化进度框
                        initProgressDialog();
                    }
                },
                R.string.upload_contacts_cancel,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // 关闭dialog
                        closeDialog();
                        // 将是否上传通讯录标志插入数据库
                        mContactLogic.updateUploadFlag(false);
                        // 删除服务器上通讯录信息
                        mContactLogic.cancelUpload();
                    }
                });
    }
    
    /**
     * 上传通讯录进度条<BR>
     * 
     */
    private void uploadingContact(String msg)
    {
        msg = getResources().getString(R.string.uploading_contacts, msg + "%");
        
        if (null != mBasicDialog)
        {
            textView.setText(msg);
        }
    }
    
    /**
     * 
     * 初始化ProgressDialog<BR>
     * 
     */
    protected synchronized void initProgressDialog()
    {
        mDialogFlag = UPLOAD_PROGRESSING_DIALOG;
        
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.upload_pogress_dialog, null);
        
        textView = (TextView) layout.findViewById(R.id.progress_percent);
        
        String msg = getResources().getString(R.string.uploading_contacts, "");
        textView.setText(msg);
        
        mBasicDialog = new BasicDialog.Builder(getParent()).setTitle(R.string.upload_mobile_contacts)
                .setIcon(R.drawable.icon_warning)
                .setContentView(layout)
                .setPositiveButton(R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int i)
                            {
                                // 关闭dialog
                                closeDialog();
                                uploadContactContinue();
                                mContactLogic.paushUpload();
                            }
                        })
                .create();
        mBasicDialog.show();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Logger.d(TAG, "=======onSaveInstanceState=======");
        outState.putString(KEY_OF_DIALOG, mDialogFlag);
        
        super.onSaveInstanceState(outState);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mContactLogic = (IContactLogic) getLogicByInterfaceClass(IContactLogic.class);
    }
}
