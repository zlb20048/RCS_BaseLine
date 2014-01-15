/*
 * 文件名: FileUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 盛兴亚
 * 创建时间:Feb 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.log.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.huawei.basic.android.im.component.log.Logger;

import android.content.Context;

/**
 * 
 * 文件工具<BR>
 * [功能详细描述]
 * @author 盛兴亚
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class FileUtil
{
    private static final String TAG = "FileUtil";
    
    private FileUtil()
    {
    }
    
    /**
     * fore delete a file,thread safe.
     * @param file file
     * @return del result
     */
    public static boolean forceDeleteFile(File file)
    {
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < 10)
        {
            result = file.delete();
            if (!result)
            {
                try
                {
                    synchronized (file)
                    {
                        file.wait(200);
                    }
                }
                catch (InterruptedException e)
                {
                    Logger.e("FileUtil.forceDeleteFile", "", e);
                }
            }
        }
        Logger.v("FileUtil.forceDeleteFile", "tryCount = " + tryCount);
        return result;
    }
    
    /**
     * read strings for a file  in /data/data/package/filename
     * @param context context
     * @param file file
     * @return strings for a file in /data/data/package/filename
     */
    public static String read(Context context, String file)
    {
        String data = "";
        try
        {
            FileInputStream stream = context.openFileInput(file);
            StringBuffer sb = new StringBuffer();
            int c;
            while ((c = stream.read()) != -1)
            {
                sb.append((char) c);
            }
            stream.close();
            data = sb.toString();
            
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG, e.getMessage());
        }
        catch (IOException e)
        {
            Logger.e(TAG, e.getMessage());
        }
        return data;
    }
    
    /**
     * write strings to a file  in /data/data/package/filename
     * @param context context
     * @param file file
     * @param msg msg
     */
    public static void write(Context context, String file, String msg)
    {
        try
        {
            FileOutputStream stream = context.openFileOutput(file,
                    Context.MODE_WORLD_WRITEABLE);
            stream.write(msg.getBytes());
            stream.flush();
            stream.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.e(TAG, e.getMessage());
        }
        catch (IOException e)
        {
            Logger.e(TAG, e.getMessage());
        }
    }
    
}
