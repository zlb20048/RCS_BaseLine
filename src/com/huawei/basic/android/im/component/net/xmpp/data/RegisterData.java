/*
 * 文件名: RegisterData.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

/**
 * 注册组件的参数封装<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-18]
 */
public class RegisterData
{
    /**
     * jid节点数据<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-6]
     */
    public static class RegisterBaseData
    {
        /**
         * 发送者jid
         */
        private String jid;
        
        public String getJid()
        {
            return jid;
        }
        
        public void setJid(String jid)
        {
            this.jid = jid;
        }
        
        /**
         * 获取节点内容字符串
         * @return BodyString
         */
        protected String getBodyString()
        {
            return "";
        }
        
        /**
         * 获取当前命令的xml字符串
         * @return CmdData
         */
        public String makeCmdData()
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("<reg>");
            if (jid != null && !"".equals(jid.trim()))
            {
                //jid是可选字段，有时候可能不会设置进来
                sb.append("<jid>").append(jid).append("</jid>");
            }
            
            sb.append(getBodyString());
            sb.append("</reg>");
            
            return sb.toString();
        }
        
    }
    
    /**
     * 注册命令
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-6]
     */
    public static class RegisterCmdData extends RegisterBaseData implements
            XmlCmdData
    {
        
        /**
         * 客户端类型枚举<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-6]
         */
        public static enum ClientType
        {
            /**
             * PC 的值为 0,Mobile为1
             */
            PC("PC"), MOBILE("mobile");
            
            private final String value;
            
            ClientType(String value)
            {
                this.value = value;
            }
            
            public String getValue()
            {
                return value;
            }
        }
        
        /**
         * 必选 Xmpp服务器地址及端口
         */
        private String serverIp = null;
        
        /**
         * 必选 Xmpp服务器地址及端口
         */
        private String serverPort = null;;
        
        /**
         * 必选 鉴权需要token值
         */
        private String authToken = null;
        
        /**
         * pc or mobile
         */
        private ClientType clientType = null;
        
        /**
         * 可选 是否启用TLS
         */
        private boolean useTls = false;
        
        /**
         * 必选 最大心跳次数
         */
        private String maxHeartbeatCount = null;
        
        /**
         * 可选 是否启用zlib压缩
         */
        private boolean useZlib = false;
        
        /**
         * 可选 设置代理服务器
         */
        private Proxy proxy = null;
        
        /**
         * 设置客户端类型
         * 
         * @param clientType 0 表示 pc 1 表示 mobile
         */
        public void setClientType(ClientType clientType)
        {
            this.clientType = clientType;
        }
        
        /**
         * 设置服务器ip地址
         * 
         * @param serverIp 服务器的ip地址或域名
         */
        public void setServerIp(String serverIp)
        {
            this.serverIp = serverIp;
        }
        
        /**
         * 设置服务器端口
         * 
         * @param serverPort 服务器的端口
         *            <p>
         *            默认端口为5222
         *            </p>
         */
        public void setServerPort(String serverPort)
        {
            this.serverPort = serverPort;
        }
        
        /**
         * 设置是否启用tls
         * 
         * @param useTls 0 不启用 1 启用
         */
        public void setUseTls(boolean useTls)
        {
            this.useTls = useTls;
        }
        
        /**
         * 设置最大心跳数
         * <p>
         * 在服务器未响应心跳包的最大心跳次数。 
         * 客户端如果连续发送X个心跳均未收到服务器的回应，
         * 应主动断开连接并重连。
         * </p>
         * 
         * @param maxHeartbeatCount 最大心跳数
         */
        public void setMaxHeartbeatCount(String maxHeartbeatCount)
        {
            this.maxHeartbeatCount = maxHeartbeatCount;
        }
        
        /**
         * 设置是否启用zlib压缩
         * 
         * @param useZlib 0 不启用 1 启用
         */
        public void setUseZlib(boolean useZlib)
        {
            this.useZlib = useZlib;
        }
        
        /**
         * 设置鉴权需要token值
         * 
         * @param authToken 鉴权需要token值
         */
        public void setAuthToken(String authToken)
        {
            this.authToken = authToken;
        }
        
        /**
         * 设置代理服务器
         * 
         * @param proxy 代理服务器
         */
        public void setProxy(Proxy proxy)
        {
            this.proxy = proxy;
        }
        
        /**
         * 获得对象的xml格式串<BR>
         * 
         * @return 对象的xml串
         * @see java.lang.Object#toString()
         */
        @Override
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<server-ip>");
            sb.append(serverIp == null ? "" : serverIp);
            sb.append("</server-ip>");
            
            sb.append("<server-port>");
            sb.append(serverPort == null ? "" : serverPort);
            sb.append("</server-port>");
            
            sb.append("<auth-token>");
            sb.append(authToken == null ? "" : authToken);
            sb.append("</auth-token>");
            
            sb.append("<client-type>");
            sb.append(clientType == null ? "" : clientType.getValue());
            sb.append("</client-type>");
            
            sb.append("<use-tls>");
            sb.append(useTls ? 1 : 0);
            sb.append("</use-tls>");
            
            sb.append("<use-zlib>");
            sb.append(useZlib ? 1 : 0);
            sb.append("</use-zlib>");
            
            //心跳默认值为5次
            sb.append("<max-heartbeat-count>");
            sb.append(maxHeartbeatCount == null ? "5" : maxHeartbeatCount);
            sb.append("</max-heartbeat-count>");
            
            if (proxy != null)
            {
                sb.append("<proxy>");
                
                if (proxy.type != null)
                {
                    sb.append("<type>").append(proxy.type).append("</type>");
                }
                
                if (proxy.host != null)
                {
                    sb.append("<host>").append(proxy.host).append("</host>");
                }
                
                if (proxy.port != null)
                {
                    sb.append("<port>").append(proxy.port).append("</port>");
                }
                
                if (proxy.user != null)
                {
                    sb.append("<user>").append(proxy.user).append("</user>");
                }
                
                if (proxy.pwd != null)
                {
                    sb.append("<pwd>").append(proxy.pwd).append("</pwd>");
                }
                
                sb.append("</proxy>");
            }
            
            return sb.toString();
        }
        
        /**
         * 代理服务器
         * 
         * @author 周庆龙
         */
        public static class Proxy
        {
            /**
             * Type<BR>
             * @author qlzhou
             * @version [RCS Client V100R001C03, Mar 1, 2012]
             */
            public static enum Type
            {
                /**
                 * 代理类型
                 */
                HTTP("http"), SOCKET("socket");
                
                private final String value;
                
                Type(String type)
                {
                    this.value = type;
                }
                
                public String getValue()
                {
                    return value;
                }
                
            }
            
            /**
             * 可选 代理服务器类型 支持 [http,socket5]
             */
            private Type type = null;
            
            /**
             * 必选 代理服务器主机
             */
            private String host = null;
            
            /**
             * 可选 代理服务器端口
             */
            private String port = null;
            
            /**
             * 可选 代理服务器账号
             */
            private String user = null;
            
            /**
             * 可选 代理服务器密码
             */
            private String pwd = null;
            
            /**
             * 设置代理服务器类型
             * <p>
             * 支持 [http,socket5]
             * </p>
             * 
             * @param type 类型
             */
            public void setType(Type type)
            {
                this.type = type;
            }
            
            /**
             * 设置代理服务器主机地址
             * 
             * @param host 主机地址
             */
            public void setHost(String host)
            {
                this.host = host;
            }
            
            public void setPort(String port)
            {
                this.port = port;
            }
            
            /**
             * 设置代理服务器用户名
             * 
             * @param user 代理服务器用户名
             */
            public void setUser(String user)
            {
                this.user = user;
            }
            
            /**
             * 设置代理服务器密码
             * 
             * @param pwd 代理服务器密码
             */
            public void setPwd(String pwd)
            {
                this.pwd = pwd;
            }
        }
    }
    
    /**
     * 注销命令<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-6]
     */
    public static class DeregisterCmdData extends RegisterBaseData implements
            XmlCmdData
    {
        //DeregisterCmdData的内容与基类相同，不需要做任何操作
        
    }
}
