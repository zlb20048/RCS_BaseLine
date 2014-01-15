/*
 * 文件名: BaseRetData.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 执行命令返回数据基类<BR>
 *
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-13]
 */
public class BaseRetData
{
    
    /**
     * 执行命令返回解析对象<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class CmdReturnData
    {
        /**
         * 部分命令中会携带原id返回
         */
        @Element(name = "id", required = false)
        private String id;
        
        /**
         * 服务器返回的信息 0 表示成功 其他表示失败
         */
        @Element(name = "ret", required = true)
        private int ret;
        
        /**
         * getId<BR>
         * @return id
         */
        public String getId()
        {
            return id;
        }
        
        /**
         * setId<BR>
         * @param id id
         */
        public void setId(String id)
        {
            this.id = id;
        }
        
        /**
         * getRet<BR>
         * @return ret
         */
        public int getRet()
        {
            return ret;
        }
        
        /**
         * setRet<BR>
         * @param ret ret
         */
        public void setRet(int ret)
        {
            this.ret = ret;
        }
        
    }
    
    /**
     * 发送消息执行结果<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    @Root(name = "message", strict = false)
    public static class MessageSend extends CmdReturnData
    {
        /**
         * toString 方法<BR>
         *
         * @return 描述的关键信息
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "MessageSend ret:" + getRet();
        }
        
    }
    
    /**
     * 状态报告<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    @Root(name = "report", strict = false)
    public static class MessageReport extends CmdReturnData
    {
        /**
         * toString 方法<BR>
         *
         * @return 描述的关键信息
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "MessageReport ret:" + getRet();
        }
    }
    
    /**
     * presence组件<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-17]
     */
    @Root(name = "ps", strict = false)
    public static class Presence extends CmdReturnData
    {
        
        /**
         * toString 方法<BR>
         *
         * @return 描述的关键信息
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Presence ret:" + getRet();
        }
    }
    
    /**
     * register组件<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-17]
     */
    @Root(name = "reg", strict = false)
    public static class Register extends CmdReturnData
    {
        /**
         * toString 方法<BR>
         *
         * @return 描述的关键信息
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Register ret:" + getRet();
        }
    }
    
    /**
     *
     * 群组组件<BR>
     * [功能详细描述]
     *
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2011-11-9]
     */
    @Root(name = "group", strict = false)
    public static class Group extends CmdReturnData
    {
        /**
         * toString 方法<BR>
         *
         * @return 描述的关键信息
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "Group ret:" + getRet();
        }
    }
    
}
