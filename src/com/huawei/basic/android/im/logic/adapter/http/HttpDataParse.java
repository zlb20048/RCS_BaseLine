/*
 * 文件名: PresenceNotification.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * presence组件的通知<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-12]
 */
public class HttpDataParse
{
    /**
     * 
     * InviteFiendParse 呈现<BR>
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-4-21]
     */
    @Root(name = "result", strict = false)
    public static class InviteFiendParse
    {
        
        //    <result>
        //        <retCode>0</retCode>
        //        <retDesc>成功</retDesc>
        //        <inviteRetInfos>
        //            <InviteRetInfo>
        //                <destUser>15524125266</destUser>
        //                <retCode>209002013</retCode>
        //                <retDesc>邀请短信发送失败</retDesc>
        //            </InviteRetInfo>
        //        </inviteRetInfos>
        //    </result>
        
        /**
         * 操作标志
         */
        @Element(name = "retCode", required = false)
        private String retCode;
        
        /**
         * 描述
         */
        @Element(name = "retDesc", required = false)
        private String retDesc;
        
        /**
         * 包含inviteRetInfos节点
         */
        @ElementList(name = "inviteRetInfos", inline = false)
        private ArrayList<InviteRetInfo> inviteRetInfos;
        
        public String getRetCode()
        {
            return retCode;
        }
        
        public void setRetCode(String retCode)
        {
            this.retCode = retCode;
        }
        
        public String getRetDesc()
        {
            return retDesc;
        }
        
        public void setRetDesc(String retDesc)
        {
            this.retDesc = retDesc;
        }
        
        
        public ArrayList<InviteRetInfo> getInviteRetInfos()
        {
            return inviteRetInfos;
        }

        /**
         * 
         * inviteRetInfo<BR>
         * 
         * @author 马波
         */
        @Root(name = "InviteRetInfo", strict = false)
        public static class InviteRetInfo
        {
            /**
             * 操作标志
             */
            @Element(name = "destUser", required = false)
            private String destUser;
            
            /**
             * 返回码
             */
            @Element(name = "retCode", required = false)
            private String retCode;
            
            /**
             * 返回信息
             */
            @Element(name = "retDesc", required = false)
            private String retDesc;
            
            public String getDestUser()
            {
                return destUser;
            }
            
            public void setDestUser(String destUser)
            {
                this.destUser = destUser;
            }
            
            public String getRetCode()
            {
                return retCode;
            }
            
            public void setRetCode(String retCode)
            {
                this.retCode = retCode;
            }
            
            public String getRetDesc()
            {
                return retDesc;
            }
            
            public void setRetDesc(String retDesc)
            {
                this.retDesc = retDesc;
            }
            
        }
        
    }
}
