/*
 * 文件名: CheckUpdateInfoModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周雪松
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Root;

/**
 * 检查更新的model<BR>
 * @author 周雪松
 * @version [RCS Client V100R001C03, Mar 21, 2012]
 */
@Root(name = "checkUpdate", strict = false)
public class CheckUpdateInfoModel
{
    /**
     * ChangeLog对象
     */
    private CheckUpdateInfoModel.ChangeLog changeLog = new CheckUpdateInfoModel.ChangeLog();
    /**
     * FileList对象
     */
    private CheckUpdateInfoModel.Filelist filelist = new CheckUpdateInfoModel.Filelist();

    /**
     * 状态值
     */
    private String status;
    /**
     * 版本的名称，来自服务器管理页面“Version Name”
     */
    private String name;
    /**
     * 版本号，对应服务器管理页面“Version Number”
     */
    private String version;
    /**
     * 版本的唯一标识。升级服务器内部唯一标识。
     */
    private String versionID;
    /**
     * 版本描述，对应服务器管理页面“Description” 
     */
    private String description;
    /**
     * 客户端下载文件的地址
     */
    private String url;
    /**
     * 是否强制升级 0：不强制升级 1：强制升级
     */
    private String forceupdate;

    /**
     * 客户端需要将该url拼接上“/full”作为下载文件所在目录路径
     */
    public static final String MID_URL = "/full/";
    
    /**
     * FileList对应的文件名称
     */
    public static final String FILELIST_NAME = "filelist.xml";
    
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersionID()
    {
        return versionID;
    }

    public void setVersionID(String versionID)
    {
        this.versionID = versionID;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getForceupdate()
    {
        return forceupdate;
    }

    public void setForceupdate(String forceupdate)
    {
        this.forceupdate = forceupdate;
    }
    
    public CheckUpdateInfoModel.ChangeLog getChangeLog()
    {
        return changeLog;
    }

    public void setChangeLog(CheckUpdateInfoModel.ChangeLog changeLog)
    {
        this.changeLog = changeLog;
    }

    public CheckUpdateInfoModel.Filelist getFilelist()
    {
        return filelist;
    }

    public void setFilelist(CheckUpdateInfoModel.Filelist filelist)
    {
        this.filelist = filelist;
    }


    public static class ChangeLog
    {
        /**
         * 客户端组件名称
         */
        private String componentName;
        /**
         * 客户端组件新版本号
         */
        private String componentVersion;
        /**
         * 客户端界面缺省语言 (目前只支持中文)
         */
        private String defaultLanguageName;
        /**
         * 客户端界面语言编码（目前只支持中文）
         */
        private String defaultLanguageCode;
        /**
         * 客户端界面语言名称 目前只支持中文
         */
        private String languageName;
        /**
         * 客户端界面语言编码 目前只支持中文
         */
        private String languageCode;
        /**
         * 客户端组件新版本特性
         */
        private List<String> features = new ArrayList<String>();
        //private String feature;

        public String getComponentName()
        {
            return componentName;
        }

        public void setComponentName(String componentName)
        {
            this.componentName = componentName;
        }

        public String getComponentNersion()
        {
            return componentVersion;
        }

        public void setComponentNersion(String pComponentVersion)
        {
            this.componentVersion = pComponentVersion;
        }

        public String getComponentVersion()
        {
            return componentVersion;
        }

        public void setComponentVersion(String componentVersion)
        {
            this.componentVersion = componentVersion;
        }

        public String getDefaultLanguageName()
        {
            return defaultLanguageName;
        }

        public void setDefaultLanguageName(String defaultLanguageName)
        {
            this.defaultLanguageName = defaultLanguageName;
        }

        public String getDefaultLanguageCode()
        {
            return defaultLanguageCode;
        }

        public void setDefaultLanguageCode(String defaultLanguageCode)
        {
            this.defaultLanguageCode = defaultLanguageCode;
        }

        public String getLanguageName()
        {
            return languageName;
        }

        public void setLanguageName(String languageName)
        {
            this.languageName = languageName;
        }

        public String getLanguageCode()
        {
            return languageCode;
        }

        public void setLanguageCode(String languageCode)
        {
            this.languageCode = languageCode;
        }

//        public String getFeature()
//        {
//            return feature;
//        }

        public void addFeatures(String feature)
        {
            this.features.add(feature);
        }

        @Override
        public String toString()
        {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer
                .append(componentName)
                    .append(componentVersion)
                    .append(defaultLanguageName)
                    .append(defaultLanguageCode);
            stringBuffer.append(languageName).append(languageCode);
            return stringBuffer.toString();
        }
    }

    public static class Filelist
    {
        /**
         * 客户端组件名称
         */
        public String name;
        /**
         * 客户端组件待下载文件是否进行过压缩的标志位 ? 0: 否 ? 1: 是
         */
        public String compress;
        /**
         * 客户端已下载文件源路径，相对客户端本地下载文件的存储目录。
         */
        public String spath;
        /**
         * 客户端已下载文件需要拷贝到的目标路径，相对客户端本地下载文件的存储目录。
         */
        public String dpath;
        /**
         * 客户端将组件文件下载到本地之后需要进一步执行的操作类型。 ? c: 拷贝到目标路径 ? cr: 拷贝到目标路径并注册 ? ce:
         * 拷贝到目标路径并运行 对于全量升级，则所有的“c”标志位失效，即全量升级时不需要执行拷贝操作
         */
        public String operation;
        /**
         * 客户端将组件文件下载之后对该文件生成MD5摘要并与该字段值比较，如果相同则表示下载文件正确
         */
        public String md5;
        /**
         * 客户端组件待下载文件大小。单位：Byte
         */
        public String size;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getCompress()
        {
            return compress;
        }

        public void setCompress(String compress)
        {
            this.compress = compress;
        }

        public String getSpath()
        {
            return spath;
        }

        public void setSpath(String spath)
        {
            this.spath = spath;
        }

        public String getDpath()
        {
            return dpath;
        }

        public void setDpath(String dpath)
        {
            this.dpath = dpath;
        }

        public String getOperation()
        {
            return operation;
        }

        public void setOperation(String operation)
        {
            this.operation = operation;
        }

        public String getMd5()
        {
            return md5;
        }

        public void setMd5(String md5)
        {
            this.md5 = md5;
        }

        public String getSize()
        {
            return size;
        }

        public void setSize(String size)
        {
            this.size = size;
        }

        @Override
        public String toString()
        {
            StringBuffer outBuffer = new StringBuffer();
            outBuffer.append(name).append(compress).append(spath).append(dpath);
            outBuffer.append(operation).append(md5).append(size);
            return outBuffer.toString();
        }
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer
            .append(status)
                .append(name)
                .append(version)
                .append(versionID)
                .append(description)
                .append(url)
                .append(forceupdate)
                .append(changeLog.toString())
                .append(filelist.toString());
        return super.toString();
    }
    
    public interface UpdateStatus
    {
        /**
         * 0:成功,有新版本 
         */
        String HAVE_NEW_VERSION = "0";
        /**
         *  1:无新版本
         */
        String NO_NEW_VERSION = "1";
        /**
         * 2:服务器忙
         */
        String SERVER_BUSY = "2";
        /**
         * 3:客户端强制升级
         */
        String FORCE_UPDATE = "3";
        /**
         * -1:系统错误
         */
        String SYSTEM_ERROR = "-1";   
    }
    
    public interface ForceUpdate
    {
        /**
         * 0：不强制升级 
         */
        
        String DO_NOT_UPDATE = "0";
        
        /**
         * 1：强制升级
         */
        String DO_UPDATE = "1";
    }
}
