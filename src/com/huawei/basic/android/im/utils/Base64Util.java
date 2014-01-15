package com.huawei.basic.android.im.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * base64通用操作封装<BR>
 * [功能详细描述]
 *
 * @author 杨凡
 * @version  [RCS Client V100R001C03, Oct 20, 2011]
 */
public abstract class Base64Util
{
    
    @SuppressWarnings("unused")
    private static final String BASE64_CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    @SuppressWarnings("unused")
    private static int[] aux = new int[4];
    
    /**
     * 对文件进行base64编码<BR>
     * [功能详细描述]
     *
     * @param file 要编码的文件
     * @return 编码后字符串
     * @throws Exception Exception
     */
    public static String encodeFile(File file) throws Exception
    {
        byte[] bytes = FileUtil.fileToByte(file);
        String encodedBase64 = encode(bytes);
        return encodedBase64;
    }
    
    /**
     *
     * 对文件进行base64解码
     *
     * @param fileByte 文件的base64格式字符串
     * @param fileName 文件名称
     * @return 解码后的文件
     */
    public static File decodeFile(String fileByte, String fileName)
    {
        byte[] bytes = decode(fileByte);
        return FileUtil.byteToFile(bytes, fileName);
    }
    
    /**
     *
     * 对文件进行流解码
     *
     * @param fileByte 文件的byte[]流信息
     * @param fileName 文件名称
     * @return 解码后的文件
     */
    public static File decodeFile(byte[] fileByte, String fileName)
    {
        byte[] bytes = Base64.decode(fileByte);
        if (bytes != null)
        {
            return FileUtil.byteToFile(bytes, fileName);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 将byte[]进行base64编码
     *
     * @param input 要编码的byte[]
     * @return 编码后字符串
     */
    public static String encode(byte[] input)
    {
        
        byte[] ret = Base64.encode(input);
        return new String(ret);
    }
    
    /**
     * 将stringacod进行base64解码
     *
     * @param stringacod 要解码的字符串
     * @return 解码后byte[]
     */
    public static byte[] decode(String stringacod)
    {
        byte[] ret = Base64.decode(stringacod);
        if (ret == null)
        {
            return null;
        }
        else
        {
            return ret;
        }
    }
    
    /**
     *
     * 将文件进行base64编码
     *
     * @param filePath 文件路径
     * @return 文件的base64字符串
     * @throws Exception 异常信息
     */
    public static String fileToBase64Str(String filePath) throws Exception
    {
        File file = new File(filePath);
        byte[] bytes = FileUtil.fileToByte(file);
        String encodedBase64 = encode(bytes);
        return encodedBase64;
    }
    
    /**
     *
     * 图片的base64解码
     *
     * @param iconBase64 图片base64格式字符串
     * @return 解码后的图片
     */
    public static Bitmap base64StrToBitmap(String iconBase64)
    {
        try
        {
            byte[] bitmapArray = decode(iconBase64);
            return BitmapFactory.decodeByteArray(bitmapArray,
                    0,
                    bitmapArray.length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将byte[]转换成bitmap<BR>
     * [功能详细描述]
     *
     * @param iconByte 要转换的byte[]
     * @return bitmap
     */
    public static Bitmap byteToBitmap(byte[] iconByte)
    {
        return BitmapFactory.decodeByteArray(iconByte, 0, iconByte.length);
    }
    
    /**
     * 对图片进行base64编码<BR>
     * [功能详细描述]
     *
     * @param bitmap 要编码的bitmap
     * @return 编码后字符串
     */
    public static String bitmapToBase64Str(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return encode(bytes);
    }
    
    /**
     * 对图片进行base64编码<BR>
     * [功能详细描述]
     *
     * @param bmp 要编码的bitmap
     * @return 编码后字节流
     */
    public static byte[] bitmapTobyte(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    
}
