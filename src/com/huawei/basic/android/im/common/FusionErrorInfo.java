/*
 * 文件名: FusionErrorInfo.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Feb 24, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 将错误码转换成对应的错误描述字符串
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Feb 24, 2012]
 */
public class FusionErrorInfo
{
    /**
     * 错误码map
     */
    private static Map<String, Integer> mErrCodeMap = null;
    
    /**
     * 登录过程中的错误码
     */
    private static Map<String, Integer> mLoginErrCodeMap = null;
    
    /**
     * Xmpp交互中的错误码
     */
    private static Map<String, Integer> mXmppErrCodeMap = null;
    
    /**
     * 自动重连的错误码
     */
    private static Set<String> mRetryCode = null;
    
    /**
     * Http的错误码
     */
    private static Set<String> mRetryHttpCode = null;
    
    /**
     * 初始化错误码Map
     */
    private static void initErrCodeMap()
    {
        mErrCodeMap = new HashMap<String, Integer>();
        
        mErrCodeMap.put("203002000", R.string.error_code_203002000);
        mErrCodeMap.put("203022002", R.string.error_code_203022002);
        mErrCodeMap.put("203020303", R.string.error_code_203020303);
        
        mErrCodeMap.put("203002309", R.string.error_code_203002309);
        mErrCodeMap.put("203002338", R.string.error_code_203002338);
        mErrCodeMap.put("0", R.string.error_code_0);
        mErrCodeMap.put("2", R.string.error_code_2);
        mErrCodeMap.put("11", R.string.error_code_1);
        mErrCodeMap.put("12", R.string.error_code_10);
        mErrCodeMap.put("200044005", R.string.error_code_200044005);
        mErrCodeMap.put("200044006", R.string.error_code_200044006);
        mErrCodeMap.put("200049101", R.string.error_code_200049101);
        mErrCodeMap.put("200049102", R.string.error_code_200049102);
        mErrCodeMap.put("200049103", R.string.error_code_200049103);
        mErrCodeMap.put("200049104", R.string.error_code_200049104);
        mErrCodeMap.put("200049105", R.string.error_code_200049105);
        mErrCodeMap.put("200049106", R.string.error_code_200049106);
        mErrCodeMap.put("200049107", R.string.error_code_200049107);
        mErrCodeMap.put("200049108", R.string.error_code_200049108);
        mErrCodeMap.put("200049109", R.string.error_code_200049109);
        mErrCodeMap.put("200049430", R.string.error_code_200049430);
        mErrCodeMap.put("200049431", R.string.error_code_200049431);
        mErrCodeMap.put("200049432", R.string.error_code_200049432);
        mErrCodeMap.put("200049433", R.string.error_code_200049433);
        mErrCodeMap.put("200049434", R.string.error_code_200049434);
        mErrCodeMap.put("200049436", R.string.error_code_200049436);
        mErrCodeMap.put("200049437", R.string.error_code_200049437);
        mErrCodeMap.put("200049438", R.string.error_code_200049438);
        mErrCodeMap.put("200049439", R.string.error_code_200049439);
        mErrCodeMap.put("200049440", R.string.error_code_200049440);
        mErrCodeMap.put("200049441", R.string.error_code_200049441);
        mErrCodeMap.put("200049442", R.string.error_code_200049442);
        mErrCodeMap.put("200049443", R.string.error_code_200049443);
        mErrCodeMap.put("200049444", R.string.error_code_200049444);
        mErrCodeMap.put("200049445", R.string.error_code_200049445);
        mErrCodeMap.put("200049446", R.string.error_code_200049446);
        mErrCodeMap.put("200049447", R.string.error_code_200049447);
        mErrCodeMap.put("200049448", R.string.error_code_200049448);
        mErrCodeMap.put("200049500", R.string.error_code_200049500);
        mErrCodeMap.put("200049501", R.string.error_code_200049501);
        mErrCodeMap.put("200049502", R.string.error_code_200049502);
        mErrCodeMap.put("200049503", R.string.error_code_200049503);
        mErrCodeMap.put("200049999", R.string.error_code_200049999);
        mErrCodeMap.put("200059504", R.string.error_code_200059504);
        mErrCodeMap.put("200059505", R.string.error_code_200059505);
        mErrCodeMap.put("202010001", R.string.error_code_202010001);
        mErrCodeMap.put("202010005", R.string.error_code_202010005);
        mErrCodeMap.put("202010007", R.string.error_code_202010007);
        mErrCodeMap.put("202010008", R.string.error_code_202010008);
        mErrCodeMap.put("202010010", R.string.error_code_202010010);
        mErrCodeMap.put("202010012", R.string.error_code_202010012);
        mErrCodeMap.put("202010013", R.string.error_code_202010013);
        mErrCodeMap.put("202010014", R.string.error_code_202010014);
        mErrCodeMap.put("202010015", R.string.error_code_202010015);
        mErrCodeMap.put("202010016", R.string.error_code_202010016);
        mErrCodeMap.put("202010017", R.string.error_code_202010017);
        mErrCodeMap.put("202010018", R.string.error_code_202010018);
        mErrCodeMap.put("202010019", R.string.error_code_202010019);
        mErrCodeMap.put("202010022", R.string.error_code_202010022);
        mErrCodeMap.put("202100001", R.string.error_code_202100001);
        mErrCodeMap.put("202100002", R.string.error_code_202100002);
        mErrCodeMap.put("202100003", R.string.error_code_202100003);
        mErrCodeMap.put("202100004", R.string.error_code_202100004);
        mErrCodeMap.put("202100005", R.string.error_code_202100005);
        mErrCodeMap.put("202100006", R.string.error_code_202100006);
        mErrCodeMap.put("202100007", R.string.error_code_202100007);
        mErrCodeMap.put("202200002", R.string.error_code_202200002);
        mErrCodeMap.put("202200003", R.string.error_code_202200003);
        mErrCodeMap.put("202200004", R.string.error_code_202200004);
        
        mErrCodeMap.put("209001000", R.string.error_code_209001000);
        mErrCodeMap.put("209001001", R.string.error_code_209001001);
        mErrCodeMap.put("209001002", R.string.error_code_209001002);
        mErrCodeMap.put("209001003", R.string.error_code_209001003);
        mErrCodeMap.put("209001004", R.string.error_code_209001004);
        mErrCodeMap.put("209001005", R.string.error_code_209001005);
        mErrCodeMap.put("209001006", R.string.error_code_209001006);
        mErrCodeMap.put("209001007", R.string.error_code_209001007);
        mErrCodeMap.put("209001008", R.string.error_code_209001008);
        mErrCodeMap.put("209001009", R.string.error_code_209001009);
        mErrCodeMap.put("209001010", R.string.error_code_209001010);
        mErrCodeMap.put("209001011", R.string.error_code_209001011);
        mErrCodeMap.put("209001012", R.string.error_code_209001012);
        mErrCodeMap.put("209001014", R.string.error_code_209001014);
        mErrCodeMap.put("209001015", R.string.error_code_209001015);
        mErrCodeMap.put("209001019", R.string.error_code_209001019);
        mErrCodeMap.put("209002001", R.string.error_code_209002001);
        mErrCodeMap.put("209002002", R.string.error_code_209002002);
        mErrCodeMap.put("209002003", R.string.error_code_209002003);
        mErrCodeMap.put("209002004", R.string.error_code_209002004);
        mErrCodeMap.put("209002005", R.string.error_code_209002005);
        mErrCodeMap.put("209002006", R.string.error_code_209002006);
        mErrCodeMap.put("209002007", R.string.error_code_209002007);
        mErrCodeMap.put("209002008", R.string.error_code_209002008);
        mErrCodeMap.put("209002009", R.string.error_code_209002009);
        mErrCodeMap.put("209002010", R.string.error_code_209002010);
        mErrCodeMap.put("209002011", R.string.error_code_209002011);
        mErrCodeMap.put("209002012", R.string.error_code_209002012);
        mErrCodeMap.put("209002013", R.string.error_code_209002013);
        mErrCodeMap.put("209002014", R.string.error_code_209002014);
        mErrCodeMap.put("209002101", R.string.error_code_209002101);
        mErrCodeMap.put("209002309", R.string.error_code_209002309);
        mErrCodeMap.put("209002500", R.string.error_code_209002500);
        mErrCodeMap.put("209002600", R.string.error_code_209002600);
        mErrCodeMap.put("209002601", R.string.error_code_209002601);
        mErrCodeMap.put("209002602", R.string.error_code_209002602);
        mErrCodeMap.put("209003001", R.string.error_code_209003001);
        mErrCodeMap.put("209003002", R.string.error_code_209003002);
        mErrCodeMap.put("209003003", R.string.error_code_209003003);
        mErrCodeMap.put("209003004", R.string.error_code_209003004);
        mErrCodeMap.put("209003005", R.string.error_code_209003005);
        mErrCodeMap.put("209003006", R.string.error_code_209003006);
        mErrCodeMap.put("209003007", R.string.error_code_209003007);
        mErrCodeMap.put("209004001", R.string.error_code_209004001);
        mErrCodeMap.put("209004002", R.string.error_code_209004002);
        mErrCodeMap.put("209004003", R.string.error_code_209004003);
        mErrCodeMap.put("209004004", R.string.error_code_209004004);
        mErrCodeMap.put("209004005", R.string.error_code_209004005);
        mErrCodeMap.put("209004006", R.string.error_code_209004006);
        mErrCodeMap.put("209004007", R.string.error_code_209004007);
        mErrCodeMap.put("209004008", R.string.error_code_209004008);
        mErrCodeMap.put("209004009", R.string.error_code_209004009);
        mErrCodeMap.put("209005005", R.string.error_code_209005005);
        mErrCodeMap.put("209005008", R.string.error_code_209005008);
        mErrCodeMap.put("209005011", R.string.error_code_209005011);
        mErrCodeMap.put("209005015", R.string.error_code_209005015);
        mErrCodeMap.put("209006004", R.string.error_code_209006004);
        mErrCodeMap.put("209006005", R.string.error_code_209006005);
        mErrCodeMap.put("209009001", R.string.error_code_209009001);
        mErrCodeMap.put("209009002", R.string.error_code_209009002);
        mErrCodeMap.put("209009003", R.string.error_code_209009003);
        mErrCodeMap.put("209009004", R.string.error_code_209009004);
        mErrCodeMap.put("209010001", R.string.error_code_209010001);
        mErrCodeMap.put("209010002", R.string.error_code_209010002);
        mErrCodeMap.put("209010003", R.string.error_code_209010003);
        mErrCodeMap.put("209010004", R.string.error_code_209010004);
        mErrCodeMap.put("209010005", R.string.error_code_209010005);
        mErrCodeMap.put("209010006", R.string.error_code_209010006);
        mErrCodeMap.put("209010007", R.string.error_code_209010007);
        mErrCodeMap.put("209010008", R.string.error_code_209010008);
        mErrCodeMap.put("209012001", R.string.error_code_209012001);
        mErrCodeMap.put("209012002", R.string.error_code_209012002);
        mErrCodeMap.put("209012003", R.string.error_code_209012003);
        mErrCodeMap.put("209012004", R.string.error_code_209012004);
        mErrCodeMap.put("209012005", R.string.error_code_209012005);
        mErrCodeMap.put("209012006", R.string.error_code_209012006);
        mErrCodeMap.put("209012007", R.string.error_code_209012007);
        mErrCodeMap.put("209012008", R.string.error_code_209012008);
        mErrCodeMap.put("209012009", R.string.error_code_209012009);
        mErrCodeMap.put("209012010", R.string.error_code_209012010);
        mErrCodeMap.put("209013001", R.string.error_code_209013001);
        mErrCodeMap.put("209013002", R.string.error_code_209013002);
        mErrCodeMap.put("209013003", R.string.error_code_209013003);
        mErrCodeMap.put("209016001", R.string.error_code_209016001);
        mErrCodeMap.put("209016002", R.string.error_code_209016002);
        mErrCodeMap.put("209016003", R.string.error_code_209016003);
        mErrCodeMap.put("209016004", R.string.error_code_209016004);
        mErrCodeMap.put("209016005", R.string.error_code_209016005);
        mErrCodeMap.put("210000000", R.string.error_code_210000000);
        
        mErrCodeMap.put("207000001", R.string.remove_error1);
        mErrCodeMap.put("207000005", R.string.remove_error2);
        mErrCodeMap.put("207000007", R.string.remove_error3);
        
        //- 网络错误码
        mErrCodeMap.put(FusionCode.Common.KEY_TIMEOUT,
                R.string.error_code_timeout);
        mErrCodeMap.put(FusionCode.Common.KEY_NETWORKERROR,
                R.string.error_code_network_error);
        mErrCodeMap.put(FusionCode.Common.KEY_AUTHERROR,
                R.string.error_code_auth_error);
        mErrCodeMap.put(FusionCode.Common.KEY_PARAMERROR,
                R.string.error_code_param_error);
        mErrCodeMap.put(FusionCode.Common.KEY_FAILED,
                R.string.error_code_failed);
    }
    
    /**
     * 
     * 加载XMPP 错误码
     */
    private static void initXmppErrCodeMap()
    {
        mXmppErrCodeMap = new HashMap<String, Integer>();
        
        // xmpp错误码
        mXmppErrCodeMap.put("206100101", R.string.xmpp_error_code_206100101);
        mXmppErrCodeMap.put("206100102", R.string.xmpp_error_code_206100102);
        mXmppErrCodeMap.put("206100104", R.string.xmpp_error_code_206100104);
        mXmppErrCodeMap.put("206100105", R.string.xmpp_error_code_206100105);
        mXmppErrCodeMap.put("206100106", R.string.xmpp_error_code_206100106);
        mXmppErrCodeMap.put("206100107", R.string.xmpp_error_code_206100107);
        mXmppErrCodeMap.put("206100108", R.string.xmpp_error_code_206100108);
        mXmppErrCodeMap.put("206100109", R.string.xmpp_error_code_206100109);
        mXmppErrCodeMap.put("206100110", R.string.xmpp_error_code_206100110);
        mXmppErrCodeMap.put("206100111", R.string.xmpp_error_code_206100111);
        mXmppErrCodeMap.put("206100112", R.string.xmpp_error_code_206100112);
        mXmppErrCodeMap.put("206100113", R.string.xmpp_error_code_206100113);
        mXmppErrCodeMap.put("206100114", R.string.xmpp_error_code_206100114);
        mXmppErrCodeMap.put("206100115", R.string.xmpp_error_code_206100115);
        mXmppErrCodeMap.put("206100116", R.string.xmpp_error_code_206100116);
        mXmppErrCodeMap.put("206100117", R.string.xmpp_error_code_206100117);
        mXmppErrCodeMap.put("206100118", R.string.xmpp_error_code_206100118);
        mXmppErrCodeMap.put("206100119", R.string.xmpp_error_code_206100119);
        mXmppErrCodeMap.put("206100120", R.string.xmpp_error_code_206100120);
        mXmppErrCodeMap.put("206100121", R.string.xmpp_error_code_206100121);
        mXmppErrCodeMap.put("206100122", R.string.xmpp_error_code_206100122);
        mXmppErrCodeMap.put("206100123", R.string.xmpp_error_code_206100123);
        mXmppErrCodeMap.put("206100124", R.string.xmpp_error_code_206100124);
        mXmppErrCodeMap.put("206100125", R.string.xmpp_error_code_206100125);
        mXmppErrCodeMap.put("206100126", R.string.xmpp_error_code_206100126);
        mXmppErrCodeMap.put("206100127", R.string.xmpp_error_code_206100127);
        mXmppErrCodeMap.put("206400103", R.string.xmpp_error_code_206400103);
        mXmppErrCodeMap.put("206400104", R.string.xmpp_error_code_206400104);
        mXmppErrCodeMap.put("100", R.string.xmpp_error_code_100);
        mXmppErrCodeMap.put("1104", R.string.xmpp_error_code_1104);
        mXmppErrCodeMap.put("10001", R.string.xmpp_error_code_10001);
        
    }
    
    /**
     * 
     * 错误码信息
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码
     * @return 对应的字符串
     */
    public static String getXmppErrInfo(Context context, String errorCode)
    {
        if (!StringUtil.isNullOrEmpty(errorCode))
        {
            if (mXmppErrCodeMap == null)
            {
                initXmppErrCodeMap();
            }
            
            return getErrorText(context, mXmppErrCodeMap, errorCode);
        }
        
        return null;
    }
    
    /**
     * 通过错误码获取错误信息文字字符串
     * 
     * @param errorCode
     *            错误码，全部由数字组成
     * @param context
     *            程序Context
     * @return 根据错误码获取到的对应的错误信息字符串， 如果错误码有问题或对应信息字符串未找到，则返回null
     */
    public static String getErrorInfo(Context context, String errorCode)
    {
        if (!StringUtil.isNullOrEmpty(errorCode))
        {
            if (mErrCodeMap == null)
            {
                initErrCodeMap();
            }
            
            String retmsg = getErrorText(context, mErrCodeMap, errorCode);
            if (retmsg != null)
            {
                return retmsg;
            }
        }
        
        return "未知错误(" + errorCode + ")";
    }
    
    /**
     * 初始化错误码Map
     */
    private static void initLoginErrCodeMap()
    {
        mLoginErrCodeMap = new HashMap<String, Integer>();
        
        mLoginErrCodeMap.put("200044005", R.string.login_error_code_200044005);
        mLoginErrCodeMap.put("200044006", R.string.login_error_code_200044006);
        mLoginErrCodeMap.put("200049106", R.string.login_error_code_200049106);
        mLoginErrCodeMap.put("200049107", R.string.login_error_code_200049107);
        mLoginErrCodeMap.put("200049108", R.string.login_error_code_200049108);
        mLoginErrCodeMap.put("200049109", R.string.login_error_code_200049109);
        mLoginErrCodeMap.put("200049430", R.string.login_error_code_200049430);
        mLoginErrCodeMap.put("200049431", R.string.login_error_code_200049431);
        mLoginErrCodeMap.put("200049432", R.string.login_error_code_200049432);
        mLoginErrCodeMap.put("200049433", R.string.login_error_code_200049433);
        mLoginErrCodeMap.put("200049434", R.string.login_error_code_200049434);
        mLoginErrCodeMap.put("200049441", R.string.login_error_code_200049441);
        mLoginErrCodeMap.put("200049442", R.string.login_error_code_200049442);
        mLoginErrCodeMap.put("200049443", R.string.login_error_code_200049443);
        mLoginErrCodeMap.put("200049444", R.string.login_error_code_200049444);
        mLoginErrCodeMap.put("200049445", R.string.login_error_code_200049445);
        mLoginErrCodeMap.put("200049446", R.string.login_error_code_200049446);
        mLoginErrCodeMap.put("200049447", R.string.login_error_code_200049447);
        mLoginErrCodeMap.put("200049448", R.string.error_code_200049448);
        mLoginErrCodeMap.put("200049500", R.string.error_code_200049500);
        mLoginErrCodeMap.put("200049501", R.string.error_code_200049501);
        mLoginErrCodeMap.put("200049502", R.string.error_code_200049502);
        mLoginErrCodeMap.put("200049503", R.string.login_error_code_200049503);
        mLoginErrCodeMap.put("200049999", R.string.error_code_200049999);
        mLoginErrCodeMap.put("200059504", R.string.error_code_200059504);
        mLoginErrCodeMap.put("200059505", R.string.login_error_code_200059505);
        
        mLoginErrCodeMap.put("203", R.string.login_http_error_code_203);
        
        mLoginErrCodeMap.put("1003", R.string.login_xmpp_error_code_1003);
        mLoginErrCodeMap.put("1004", R.string.login_xmpp_error_code_1004);
        mLoginErrCodeMap.put("1010", R.string.login_xmpp_error_code_1010);
        mLoginErrCodeMap.put("1011", R.string.login_xmpp_error_code_1011);
        mLoginErrCodeMap.put("1101", R.string.login_xmpp_error_code_1101);
        mLoginErrCodeMap.put("1102", R.string.login_xmpp_error_code_1102);
        
        mLoginErrCodeMap.put("Timeout", R.string.error_code_timeout);
        mLoginErrCodeMap.put("NetworkError", R.string.error_code_network_error);
        mLoginErrCodeMap.put("AuthError", R.string.error_code_auth_error);
        mLoginErrCodeMap.put("ParamError", R.string.error_code_param_error);
        mLoginErrCodeMap.put("Failed", R.string.error_code_failed);
    }
    
    /**
     * 通过错误码获取错误信息文字字符串
     * 
     * @param errorCode
     *            错误码，全部由数字组成
     * @param context
     *            程序Context
     * @return 根据错误码获取到的对应的错误信息字符串， 如果错误码有问题或对应信息字符串未找到，则返回null
     */
    public static String getLoginErrorInfo(Context context, String errorCode)
    {
        if (!StringUtil.isNullOrEmpty(errorCode))
        {
            if (mLoginErrCodeMap == null)
            {
                initLoginErrCodeMap();
            }
            String retMsg = getErrorText(context, mLoginErrCodeMap, errorCode);
            
            if (retMsg != null)
            {
                return retMsg;
            }
        }
        
        return "系统错误(" + errorCode + ")";
    }
    
    /**
     * 
     * 获取对应错误码的文字描述
     * @param context 上下文
     * @param errInfoMap 错误码集合
     * @param errCode 错误码
     * @return 文字描述
     */
    private static String getErrorText(Context context,
            Map<String, Integer> errInfoMap, String errCode)
    {
        if (errInfoMap.containsKey(errCode))
        {
            try
            {
                return context.getResources()
                        .getString(errInfoMap.get(errCode));
            }
            catch (Exception e)
            {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * 自动重连的错误码
     */
    private static void initRetryCodeSet()
    {
        mRetryCode = new HashSet<String>();
        mRetryCode.add("1");
        mRetryCode.add("2");
        mRetryCode.add("200044005");
        mRetryCode.add("200044006");
        mRetryCode.add("200049101");
        mRetryCode.add("200049102");
        mRetryCode.add("200049103");
        mRetryCode.add("200049104");
        mRetryCode.add("200049106");
        mRetryCode.add("200049500");
        mRetryCode.add("200049501");
        mRetryCode.add("200049502");
        mRetryCode.add("200049503");
        mRetryCode.add("200049999");
    }
    
    /**
     * 
     * 是否需要重试
     * 
     * @param o
     *            Object
     * @return 是否需要重试
     */
    public static boolean isRetryCode(Object o)
    {
        if (mRetryCode == null)
        {
            initRetryCodeSet();
        }
        return mRetryCode.contains(o);
    }
    
    /**
     * 
     * 加载HTTP code
     */
    private static void initRetryHttpCodeSet()
    {
        mRetryHttpCode = new HashSet<String>();
        mRetryHttpCode.add("100");
        mRetryHttpCode.add("101");
        mRetryHttpCode.add("201");
        mRetryHttpCode.add("202");
        mRetryHttpCode.add("204");
        mRetryHttpCode.add("205");
        mRetryHttpCode.add("206");
        mRetryHttpCode.add("300");
        mRetryHttpCode.add("301");
        mRetryHttpCode.add("302");
        mRetryHttpCode.add("303");
        mRetryHttpCode.add("304");
        mRetryHttpCode.add("305");
        mRetryHttpCode.add("306");
        mRetryHttpCode.add("307");
        mRetryHttpCode.add("400");
        mRetryHttpCode.add("402");
        mRetryHttpCode.add("408");
        mRetryHttpCode.add("410");
        mRetryHttpCode.add("411");
        mRetryHttpCode.add("412");
        mRetryHttpCode.add("413");
        mRetryHttpCode.add("414");
        mRetryHttpCode.add("415");
        mRetryHttpCode.add("416");
        mRetryHttpCode.add("417");
        mRetryHttpCode.add("500");
        mRetryHttpCode.add("501");
        mRetryHttpCode.add("502");
        mRetryHttpCode.add("503");
        mRetryHttpCode.add("504");
    }
    
    /**
     * 
     * 是否需要重试HTTP
     * 
     * @param o
     *            Object
     * @return HTTPcode
     */
    public static boolean isRetryHttpCode(Object o)
    {
        if (mRetryHttpCode == null)
        {
            initRetryHttpCodeSet();
        }
        return mRetryHttpCode.contains(o);
    }
    
    /**
     * 
     * 登陆错误需要的返回码
     * @author tlmao
     * @version [RCS Client V100R001C03, Jun 15, 2012]
     */
    public interface LoginErrorCode
    {
        /**
         * 需要图形验证码的返回码
         */
        String NEED_VERIFY_CODE = "200059504";
        
        /**
         * 账号密码错误的返回码
         */
        String ACOUNT_ERROR_CODE = "200049431";
        
        /**
         * 账号密码错误的返回码
         */
        String PASSWORD_ERROR_CODE = "200049432";
        
        /**
         * 电话号码已绑定的返回码
         */
        String PHONE_IS_BOUNDED = "209009002";
        
        /**
         * 邮箱地址已绑定的返回码
         */
        String EMAIL_IS_BOUNDED = "209013002";
        /**
         * 成功的返回码
         */
        String SUCCESS = "0";
        
        /**
         * 错误的验证码的返回码
         */
        String INCORRECT_VERIFY_CODE = "209005011";
        
        /**
         * 邮箱地址未绑定的返回码
         */
        String EMAIL_NOT_BOUNDED = "209005005";
        
        /**
         * 邮件发送失败的返回码
         */
        String EMAIL_SEND_FAILED = "209005015";
        
        /**
         * 重试的返回码
         */
        String TRY_AGAIN = "209005008";
        
        /**
         * 被踢的返回码
         */
        String KICK_OUT = "1102";
    }
}
