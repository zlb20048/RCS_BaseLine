/*
 * 文件名: DecodeUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * 编码转换工具类
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2011-10-17]
 */
public abstract class DecodeUtil
{
    private static final  char[] HEX = "0123456789ABCDEF".toCharArray();
    
    /**
     * 通过SHA256将数据进行加密操作
     * 
     * @param strSrc 原始字符串，即待加密字符串
     * @return 加密后的字符串
     */
    public static String sha256Encode(String strSrc)
    {
        String encName = "SHA-256";
        MessageDigest md = null;
        String strDes = null;
        
        byte[] bt = strSrc.getBytes();
        try
        {
            if (encName == null || encName.equals(""))
            {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
        // Logger.i("main", strDes.toUpperCase());
        // System.out.println(strDes.toUpperCase());
        return strDes;
    }
    
    /**
     * [将byte数组转换为16进制字符串]
     * 
     * @param bts byte数组，即待转换的数组
     * @return 转换后的16进制字符串
     */
    private static String bytes2Hex(byte[] bts)
    {
        StringBuilder sb = new StringBuilder(bts.length * 2);
        for (int i = 0; i < bts.length; i++)
        {
            sb.append(HEX[bts[i] >> 4 & 0xf]);
            sb.append(HEX[bts[i] & 0xf]);
        }
        return sb.toString();
        
        // String des = "";
        // String tmp = null;
        // for (int i = 0; i < bts.length; i++)
        // {
        // tmp = Integer.toHexString(bts[i] & 0xFF);
        // if (tmp.length() == 1)
        // {
        // des += "0";
        // }
        // des += tmp;
        // }
        // return des;
    }
    
    /**
     * [将16进制字符串转换为byte数组]
     * 
     * @param strhex 16进制字符串
     * @return 转换后的字节数组
     */
    public static byte[] hex2byte(String strhex)
    {
        if (strhex == null)
        {
            return null;
        }
        int l = strhex.length();
        if (l % 2 != 0)
        {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++)
        {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }
    
    /**
     * [对字符串进行AES解密]
     * 
     * @param sSrc 要解密的字符串
     * @param sKey 解密密钥
     * @return 解密后的字符串
     */
    public static String decryptAES(String sSrc, String sKey)
    {
        try
        {
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = DecodeUtil.hex2byte(sSrc);
            try
            {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * 加密方法<BR>
     *  
     * @param seed String
     * @param cleartext String
     * @return 加密后生成的字符串
     */
    public static String encrypt(String seed, String cleartext)
    {
        try
        {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            return bytes2Hex(result);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * 解密方法<BR>
     * 
     * @param seed String
     * @param encrypted String
     * @return 解密后的原文
     */
    public static String decrypt(String seed, String encrypted)
    {
        try
        {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] enc = hex2byte(encrypted);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private static byte[] getRawKey(byte[] seed) throws Exception
    {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }
    
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
    
    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
}
