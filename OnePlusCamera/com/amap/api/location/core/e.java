package com.amap.api.location.core;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class e
{
  private static final char[] a = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  static String a(String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return null;
      try
      {
        if (paramString.length() != 0)
        {
          String str = b("SHA1", paramString);
          paramString = b("MD5", str + paramString);
          return paramString;
        }
      }
      catch (Throwable paramString)
      {
        paramString.printStackTrace();
      }
    }
    return null;
  }
  
  static String a(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = b(paramString1);
      paramString1 = a(paramString1, paramString2);
      if (paramString1 == null) {}
    }
    catch (Exception paramString1)
    {
      for (;;)
      {
        try
        {
          paramString1 = new String(paramString1, "UTF-8");
          return paramString1;
        }
        catch (UnsupportedEncodingException paramString1)
        {
          paramString1.printStackTrace();
        }
        paramString1 = paramString1;
        paramString1.printStackTrace();
        paramString1 = null;
      }
    }
    return null;
    return null;
  }
  
  private static String a(byte[] paramArrayOfByte)
  {
    int j = paramArrayOfByte.length;
    StringBuilder localStringBuilder = new StringBuilder(j * 2);
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append(a[(paramArrayOfByte[i] >> 4 & 0xF)]);
      localStringBuilder.append(a[(paramArrayOfByte[i] & 0xF)]);
      i += 1;
    }
  }
  
  static PublicKey a(Context paramContext)
    throws Exception
  {
    paramContext = paramContext.getAssets();
    try
    {
      paramContext = paramContext.open("location_public_key.der");
      Object localObject = CertificateFactory.getInstance("X.509");
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      localObject = ((CertificateFactory)localObject).generateCertificate(paramContext);
      paramContext.close();
      paramContext = localKeyFactory.generatePublic(new X509EncodedKeySpec(((Certificate)localObject).getPublicKey().getEncoded()));
      return paramContext;
    }
    catch (NoSuchAlgorithmException paramContext)
    {
      throw new Exception("无此算法");
    }
    catch (InvalidKeySpecException paramContext)
    {
      throw new Exception("公钥非法");
    }
    catch (NullPointerException paramContext)
    {
      throw new Exception("公钥数据为空");
    }
    catch (CertificateException paramContext)
    {
      return null;
    }
    catch (IOException paramContext)
    {
      for (;;) {}
    }
  }
  
  public static byte[] a(byte[] paramArrayOfByte, String paramString)
  {
    try
    {
      paramString = c(paramString);
      Cipher localCipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
      localCipher.init(2, paramString);
      paramArrayOfByte = localCipher.doFinal(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Exception paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
    }
    return null;
  }
  
  static byte[] a(byte[] paramArrayOfByte, Key paramKey)
    throws Exception
  {
    Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    localCipher.init(1, paramKey);
    return localCipher.doFinal(paramArrayOfByte);
  }
  
  static byte[] a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      paramArrayOfByte1 = new SecretKeySpec(paramArrayOfByte1, "AES");
      Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      localCipher.init(1, paramArrayOfByte1);
      paramArrayOfByte1 = localCipher.doFinal(paramArrayOfByte2);
      return paramArrayOfByte1;
    }
    catch (NoSuchAlgorithmException paramArrayOfByte1)
    {
      paramArrayOfByte1.printStackTrace();
      return null;
    }
    catch (NoSuchPaddingException paramArrayOfByte1)
    {
      for (;;)
      {
        paramArrayOfByte1.printStackTrace();
      }
    }
    catch (Throwable paramArrayOfByte1)
    {
      paramArrayOfByte1.printStackTrace();
    }
    return null;
  }
  
  private static String b(String paramString1, String paramString2)
  {
    if (paramString2 != null) {}
    try
    {
      paramString1 = MessageDigest.getInstance(paramString1);
      paramString1.update(paramString2.getBytes("utf-8"));
      paramString1 = a(paramString1.digest());
      return paramString1;
    }
    catch (Exception paramString1)
    {
      throw new RuntimeException(paramString1);
    }
    return null;
  }
  
  private static byte[] b(String paramString)
  {
    int i = 0;
    if (paramString == null) {}
    while (paramString.length() < 2) {
      return new byte[0];
    }
    paramString = paramString.toLowerCase();
    int j = paramString.length() / 2;
    byte[] arrayOfByte = new byte[j];
    for (;;)
    {
      if (i >= j) {
        return arrayOfByte;
      }
      arrayOfByte[i] = ((byte)(byte)(Integer.parseInt(paramString.substring(i * 2, i * 2 + 2), 16) & 0xFF));
      i += 1;
    }
  }
  
  private static SecretKeySpec c(String paramString)
  {
    if (paramString != null) {}
    for (;;)
    {
      StringBuffer localStringBuffer = new StringBuffer(16);
      localStringBuffer.append(paramString);
      label20:
      if ((localStringBuffer.length() < 16) || (localStringBuffer.length() <= 16)) {}
      try
      {
        for (;;)
        {
          paramString = localStringBuffer.toString().getBytes("UTF-8");
          return new SecretKeySpec(paramString, "AES");
          paramString = "";
          break;
          localStringBuffer.append("0");
          break label20;
          localStringBuffer.setLength(16);
        }
      }
      catch (UnsupportedEncodingException paramString)
      {
        for (;;)
        {
          paramString.printStackTrace();
          paramString = null;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/e.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */