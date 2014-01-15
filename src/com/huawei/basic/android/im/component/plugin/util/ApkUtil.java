/*
 * 文件名: ApkUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 10, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Apk工具类<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 10, 2012] 
 */
public class ApkUtil
{
    /**
     * 查找外置apk是否安装
     * 
     * @param context context
     * @param packageName 包名
     * @return apk是否安装
     */
    public static boolean isApkInstalled(Context context, String packageName)
    {
        PackageManager pkgManager = context.getPackageManager();
        
        List<PackageInfo> pkgs = pkgManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pkg : pkgs)
        {
            if (packageName.equals(pkg.packageName))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取某一路径下的apk包信息，如果为空，则此apk不存在<BR>
     * @param context context上下文
     * @param archiveFilePath apk路径
     * @return PackageInfo包信息
     */
    public static PackageInfo getPackageInfo(Context context,
            String archiveFilePath)
    {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_ACTIVITIES);
    }
    
    /**
     * 判断apk是否已安装<BR>
     * @param context context
     * @param archiveFilePath apk所在的路径
     * @return 是否已安装 
     */
    public static boolean isApkFileInstalled(Context context,
            String archiveFilePath)
    {
        if (null == archiveFilePath)
        {
            throw new IllegalArgumentException("archiveFilePath cannot be null");
        }
        PackageInfo info = getPackageInfo(context, archiveFilePath);
        if (info == null)
        {
            //apk不存在
            return false;
        }
        return isApkInstalled(context, info.packageName);
    }
    
    /**
     * 跳转到安装apk的路径<BR>
     * @param context context上下文
     * @param archiveFilePath apk路径
     * @return 是否安装成功
     */
    public static boolean installApk(Context context, String archiveFilePath)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file:///" + archiveFilePath),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * 判断apk是否已安装<BR>
     * @param context context上下文
     * @param packageName 包名
     * @param version 版本
     * @return 是否已安装的
     */
    public static boolean isNewApkInstalled(Context context,
            String packageName, int version)
    {
        PackageManager pkgManager = context.getPackageManager();
        
        List<PackageInfo> pkgs = pkgManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pkg : pkgs)
        {
            if (packageName.equals(pkg.packageName)
                    && pkg.versionCode == version)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取已安装的插件包列表<BR>
     * @param context context上下文
     * @return 插件信息列表
     */
    public static List<PackageInfo> getInstalledPackages(Context context)
    {
        PackageManager pkgManager = context.getPackageManager();
        return pkgManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    }
    
    /**
     * 卸载插件<BR>
     * @param context context上下文
     * @param packageName 包名
     */
    public static void unInstallApk(Context context, String packageName)
    {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(uninstallIntent);
    }
}
