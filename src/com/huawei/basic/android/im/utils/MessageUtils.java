package com.huawei.basic.android.im.utils;

import java.util.UUID;

/**
 * 
 * 会话，信息的工具类<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-20]
 */
public final class MessageUtils
{

    /**
     * 生成唯一的消息Id
     * 
     * @return 唯一的消息Id
     */
    public static String generateMsgId()
    {
        return UUID.randomUUID().toString().replaceAll("-",
            "");
    }

}
