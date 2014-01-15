package com.huawei.basic.android.im.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import android.os.Build;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 全局异常处理
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-5-9]
 */
public final class GlobalExceptionHandler implements
        Thread.UncaughtExceptionHandler
{
    private static final String TAG = "GlobalExceptionHandler";
    
    private static final boolean SEND_MAIL = true;
    
    private static final String EMAIL_FROM_ONE = "rcsbaseline1@yahoo.cn";
    
    private static final String EMAIL_FROM_TWO = "rcsbaseline2@yahoo.cn";
    
    private static final String EMAIL_FROM_THREE = "rcsbaseline3@yahoo.cn";
    
    private static final String EMAIL_FROM_FOUR = "rcsbaseline4@yahoo.cn";
    
    private static final String EMAIL_FROM_FIVE = "rcsbaseline5@yahoo.cn";
    
    private static final String EMAIL_FROM_SIX = "rcsbaseline6@yahoo.cn";
    
    private static final String EMAIL_FROM_SEVEN = "rcsbaseline7@yahoo.cn";
    
    private static final String EMAIL_FROM_EIGTH = "rcsbaseline8@yahoo.cn";
    
    private static final String EMAIL_FROM_NINE = "rcsbaseline9@yahoo.cn";
    
    private static final String EMAIL_FROM_TEN = "rcsbaseline10@yahoo.cn";
    
    private static final String EMAIL_FROM_ELEVEN = "rcsbaseline11@yahoo.cn";
    
    private static final String EMAIL_FROM_TWELVE = "rcsbaseline12@yahoo.cn";
    
    private static final String EMAIL_FROM_THIRTEEN = "rcsbaseline13@yahoo.cn";
    
    private static final String EMAIL_FROM_FOURTEEN = "rcsbaseline14@yahoo.cn";
    
    private static final String EMAIL_FROM_FIFTEEN = "rcsbaseline15@yahoo.cn";
    
    private static final String EMAIL_FROM_SIXTEEN = "rcsbaseline16@yahoo.cn";
    
    private static final String EMAIL_FROM_SEVENTEEN = "rcsbaseline19@yahoo.cn";
    
    private static final String EMAIL_FROM_EIGHTTEEN = "rcsbaseline18@yahoo.cn";
    
    private static final String EMAIL_SMTP = "smtp.mail.yahoo.com.cn";
    
    private static final String EMAIL_PASSWORD = "HiTalk";
    
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private static final String[] EMAIL_TO_LIST = new String[] {
            "huoxiangming@huawei.com", "pierceyang@cienet.com.cn",
            "zaf@huawei.com", "qlzhou@cienet.com.cn", "gaihe@cienet.com.cn",
            "lidan@cienet.com.cn", "tlmao@cienet.com.cn",
            "fanniu@cienet.com.cn", "raulxiao@cienet.com.cn",
            "kuaiduoci@huawei.com", "siwei.shen@huawei.com",
            "qinyangwang@cienet.com.cn", "i0332@cienet.com.cn",
            "xuesongzhou@cienet.com.cn",
            "zhaozeyang@cienet.com.cn" };
    
    private String[] emailAddrs = { EMAIL_FROM_ONE, EMAIL_FROM_TWO,
            EMAIL_FROM_THREE, EMAIL_FROM_FOUR, EMAIL_FROM_FIVE, EMAIL_FROM_SIX,
            EMAIL_FROM_SEVEN, EMAIL_FROM_EIGTH, EMAIL_FROM_NINE,
            EMAIL_FROM_TEN, EMAIL_FROM_ELEVEN, EMAIL_FROM_TWELVE,
            EMAIL_FROM_THIRTEEN, EMAIL_FROM_FOURTEEN, EMAIL_FROM_FIFTEEN,
            EMAIL_FROM_SIXTEEN, EMAIL_FROM_SEVENTEEN, EMAIL_FROM_EIGHTTEEN };
    
    private boolean caughtException;
    
    private Thread.UncaughtExceptionHandler defaultHandler;
    
    private String clientInfo;
    
    /**
     * 全局错误处理构造函数
     */
    public GlobalExceptionHandler()
    {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 收集客户端信息
        clientInfo = collectClientInfo();
    }
    
    /**
     * 捕获到异常
     * 
     * @param thread 异常线程
     * @param throwable 异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
        Logger.e(TAG, "Caught Global Exception", throwable);
        if (caughtException)
        {
            Logger.i(TAG, "not send email");
            defaultHandler.uncaughtException(thread, throwable);
            return;
        }
        Logger.i(TAG, "send email");
        caughtException = true;
        if (SEND_MAIL)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            throwable.printStackTrace(ps);
            final String errorMsg = new String(baos.toByteArray());
            ps.close();
            try
            {
                baos.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            final String mailContent = errorMsg + LINE_SEPARATOR
                    + LINE_SEPARATOR + clientInfo;
            String subject = "RcsBaseline Crash Report" + " (" + "AppVersion: "
                    + FusionConfig.getInstance().getClientVersion()
                    + "), Account("
                    + FusionConfig.getInstance().getAasResult().getUserID()
                    + ")";
            final MailUtil.MailInfo mailInfo = new MailUtil.MailInfo();
            mailInfo.setFrom(emailAddrs[new Random().nextInt(emailAddrs.length)]);
            mailInfo.setPassword(EMAIL_PASSWORD);
            mailInfo.setSmtpHost(EMAIL_SMTP);
            mailInfo.setNeedAuth(true);
            mailInfo.setToList(EMAIL_TO_LIST);
            mailInfo.setSubject(subject);
            mailInfo.setContent(mailContent);
            // 跑线程, 将报错信息发送到指定的邮箱
            Thread sendMailThread = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        MailUtil.sendMail(mailInfo);
                        Logger.d(TAG, "Send mail successful");
                    }
                    catch (Exception e)
                    {
                        Logger.w(TAG, "Send mail failed", e);
                    }
                    
                }
            };
            sendMailThread.start();
        }
        defaultHandler.uncaughtException(thread, throwable);
    }
    
    private String collectClientInfo()
    {
        StringBuilder systemInfo = new StringBuilder();
        systemInfo.append("CLIENT-INFO");
        systemInfo.append(LINE_SEPARATOR);
        
        systemInfo.append("Id: ");
        systemInfo.append(Build.ID);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Display: ");
        systemInfo.append(Build.DISPLAY);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Product: ");
        systemInfo.append(Build.PRODUCT);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Device: ");
        systemInfo.append(Build.DEVICE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Board: ");
        systemInfo.append(Build.BOARD);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("CpuAbility: ");
        systemInfo.append(Build.CPU_ABI);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Manufacturer: ");
        systemInfo.append(Build.MANUFACTURER);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Brand: ");
        systemInfo.append(Build.BRAND);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Model: ");
        systemInfo.append(Build.MODEL);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Type: ");
        systemInfo.append(Build.TYPE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Tags: ");
        systemInfo.append(Build.TAGS);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("FingerPrint: ");
        systemInfo.append(Build.FINGERPRINT);
        systemInfo.append(LINE_SEPARATOR);
        
        systemInfo.append("Version.Incremental: ");
        systemInfo.append(Build.VERSION.INCREMENTAL);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Version.Release: ");
        systemInfo.append(Build.VERSION.RELEASE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SDK: ");
        systemInfo.append(Build.VERSION.SDK);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SDKInt: ");
        systemInfo.append(Build.VERSION.SDK_INT);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Version.CodeName: ");
        systemInfo.append(Build.VERSION.CODENAME);
        systemInfo.append(LINE_SEPARATOR);
        String clientInfomation = systemInfo.toString();
        systemInfo.delete(0, systemInfo.length());
        return clientInfomation;
    }
    
}
