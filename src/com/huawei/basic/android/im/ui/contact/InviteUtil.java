package com.huawei.basic.android.im.ui.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.BasicDialog;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 短信邀请<BR>
 * 
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-4-20]
 */
public class InviteUtil
{
    /**
     * 提示Toast
     */
    private static Toast sToast;
    
    /**
     * 受邀请号码
     */
    private static TextView inviteNumerView;
    
    /**
     * 邀请签名
     */
    private static LimitedEditText inviteDisplayName;
    
    /**
     * 邀请用户方式 :0 手机
     */
    private static final String PHONE_INVITE_FRIEND = "0";
    
    /**
     * 默认国家码为：中国
     */
    private static final String CONTURY_CODE = "+86";
    
    /**
     * 电话号码最小长度11
     */
    private static final int ELEVEN = 11;
    
    /**
     * 电话号码最大长度15
     */
    private static final int FIFTEEN = 15;
    
    /**
     * 
     * 邀请联系人<BR>
     * 提供选择号码、以及弹出发送的短信内容说明对话框及发送短信功能
     * @param act 上下文
     * @param contactLogic 通讯录处理Logic
     * @param phoneContactIndex 数据对象
     * @param friendUserId 好友ID
     */
    public static void invite(final BasicActivity act,
            final IContactLogic contactLogic,
            final PhoneContactIndexModel phoneContactIndex,
            final String friendUserId)
    {
        // 判断有效性
        final List<String> validNumberList = filterPhoneNo(phoneContactIndex.getPhoneNumbers());
        if (validNumberList == null || validNumberList.size() == 0)
        {
            if (sToast == null)
            {
                sToast = Toast.makeText(act,
                        R.string.phone_form_error,
                        Toast.LENGTH_LONG);
            }
            sToast.show();
            return;
        }
        
        // 用户号码大于1时，显示对话框，让用户选择号码
        if (validNumberList.size() > 1)
        {
            DialogInterface.OnClickListener chooseNumberListener = new DialogInterface.OnClickListener()
            {
                
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    inviteFriend(act,
                            contactLogic,
                            friendUserId,
                            validNumberList.get(which));
                }
            };
            BasicDialog.Builder builder = getBuilder(act);
            builder.setTitle(phoneContactIndex.getDisplayName());
            builder.setSingleChoiceItems(validNumberList.toArray(new String[validNumberList.size()]),
                    0,
                    chooseNumberListener);
            BasicDialog alert = builder.create();
            alert.show();
        }
        else
        {
            inviteFriend(act,
                    contactLogic,
                    friendUserId,
                    validNumberList.get(0));
        }
    }
    
    /**
     * 
     * 邀请好友<BR>
     * @param act BasicActivity对象
     * @param contactLogic 通讯录处理Logic
     * @param friendUserId 好友Id
     * @param phoneNo 电话号码
     */
    public static void inviteFriend(final BasicActivity act,
            final IContactLogic contactLogic, final String friendUserId,
            final String phoneNo)
    {
        BasicDialog.Builder builder = getBuilder(act);
        builder.setTitle(R.string.prompt);
        builder.setAutoClosed(false);
        builder.setNegativeButton(R.string.cancel, new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 取消按钮
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.confirm,
                new Dialog.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 确定按钮
                        String displayName = inviteDisplayName.getText()
                                .toString();
                        if (StringUtil.isNullOrEmpty(displayName))
                        {
                            Toast.makeText(act,
                                    R.string.please_input_signature,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String number = inviteNumerView.getText().toString();
                        Map<String, Object> sendData = new HashMap<String, Object>();
                        sendData.put("destUser", InviteUtil.CONTURY_CODE
                                + number);
                        sendData.put("displayName", displayName);
                        sendData.put("inviteType", PHONE_INVITE_FRIEND);
                        sendData.put("account", friendUserId);
                        contactLogic.inviteFriend(sendData);
                        dialog.dismiss();
                    }
                });
        // 判空电话号码
        if (!StringUtil.isNullOrEmpty(phoneNo) && phoneNo.length() > 10)
        {
            String number = null;
            String resetInfo = phoneNo;
            boolean isMobile = false;
            if (resetInfo.length() >= ELEVEN && resetInfo.length() <= FIFTEEN)
            {
                isMobile = StringUtil.isPhoneNumber(resetInfo);
            }
            if (isMobile)
            {
                // 保留后11位做为手机号码
                number = resetInfo.substring(resetInfo.length() - ELEVEN);
                
                LayoutInflater inflater = (LayoutInflater) act.getApplicationContext()
                        .getSystemService(BasicActivity.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.friend_phone_invite, null);
                initInviteView(view);
                final String inviteNumber = number;
                inviteNumerView.setText(inviteNumber);
                inviteDisplayName.setText("");
                //限制输入长度
                inviteDisplayName.setMaxCharLength(15);
                builder.setContentView(view);
                final BasicDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
    
    /**
     * 
     * 初始化View<BR>
     * @param view view
     */
    private static void initInviteView(View view)
    {
        inviteNumerView = (TextView) view.findViewById(R.id.friend_invite_number);
        inviteDisplayName = (LimitedEditText) view.findViewById(R.id.invite_displayName);
    }
    
    /**
     * 
     * 号码过滤器<BR>
     * 过滤掉不符合规则的号码
     * @param phoneNos 号码列表
     * @return 号码列表
     */
    private static List<String> filterPhoneNo(List<List<String>> phoneNos)
    {
        List<String> tmpList = null;
        if (phoneNos != null)
        {
            // 过滤掉无效号码
            tmpList = new ArrayList<String>();
            for (List<String> phone : phoneNos)
            {
                String phoneNo = null;
                if (phone != null)
                {
                    phoneNo = (phone.get(0)).replaceAll("[^0-9]", "");
                }
                if (StringUtil.isMobile(phoneNo))
                {
                    tmpList.add(phoneNo);
                }
            }
        }
        return tmpList;
    }
    
    /**
     * 
     * 创建Builder<BR>
     * @param BasicActivity对象
     * @return Builder
     */
    private static BasicDialog.Builder getBuilder(final BasicActivity act)
    {
        if (act.getParent() != null)
        {
            return new BasicDialog.Builder(act.getParent());
        }
        else
        {
            return new BasicDialog.Builder(act);
        }
    }
    
}
