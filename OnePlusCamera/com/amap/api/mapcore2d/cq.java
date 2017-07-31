package com.amap.api.mapcore2d;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class cq
{
  private static final char[] a;
  private static final byte[] b;
  
  static
  {
    int i = 0;
    int j = 48;
    a = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    b = new byte[''];
    if (i >= 128)
    {
      i = 65;
      label408:
      if (i <= 90) {
        break label461;
      }
      i = 97;
      label417:
      if (i <= 122) {
        break label479;
      }
      i = j;
    }
    for (;;)
    {
      if (i > 57)
      {
        b[43] = 62;
        b[47] = 63;
        return;
        b[i] = -1;
        i += 1;
        break;
        label461:
        b[i] = ((byte)(byte)(i - 65));
        i += 1;
        break label408;
        label479:
        b[i] = ((byte)(byte)(i - 97 + 26));
        i += 1;
        break label417;
      }
      b[i] = ((byte)(byte)(i - 48 + 52));
      i += 1;
    }
  }
  
  public static String a(String paramString)
  {
    return cv.a(b(paramString));
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = c(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      cy.a(paramArrayOfByte, "Encrypt", "encodeBase64");
    }
    return null;
  }
  
  static byte[] a(byte[] paramArrayOfByte, Key paramKey)
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
  {
    Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    localCipher.init(1, paramKey);
    return localCipher.doFinal(paramArrayOfByte);
  }
  
  static byte[] a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    try
    {
      paramArrayOfByte1 = b(paramArrayOfByte1, paramArrayOfByte2);
      return paramArrayOfByte1;
    }
    catch (Throwable paramArrayOfByte1)
    {
      cy.a(paramArrayOfByte1, "Encrypt", "aesEncrypt");
    }
    return null;
  }
  
  public static String b(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = c(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
    }
    return null;
  }
  
  public static byte[] b(String paramString)
  {
    int i = 0;
    int k;
    ByteArrayOutputStream localByteArrayOutputStream;
    if (paramString != null)
    {
      paramString = cv.a(paramString);
      k = paramString.length;
      localByteArrayOutputStream = new ByteArrayOutputStream(k);
      j = i;
      if (i < k) {
        break label49;
      }
      label30:
      break label205;
      break label152;
    }
    label31:
    int n;
    label49:
    label79:
    int m;
    label126:
    label152:
    while (n == -1)
    {
      do
      {
        do
        {
          return localByteArrayOutputStream.toByteArray();
          return new byte[0];
          do
          {
            if (n != -1) {
              break;
            }
            j = i;
            arrayOfByte = b;
            i = j + 1;
            n = arrayOfByte[paramString[j]];
          } while (i < k);
        } while (n == -1);
        j = i;
        byte[] arrayOfByte = b;
        i = j + 1;
        m = arrayOfByte[paramString[j]];
        if (i < k) {
          break;
        }
      } while (m == -1);
      localByteArrayOutputStream.write(n << 2 | (m & 0x30) >>> 4);
      j = i;
      i = j + 1;
      j = paramString[j];
      if (j == 61) {
        break label238;
      }
      n = b[j];
      if (i < k) {
        break label244;
      }
    }
    localByteArrayOutputStream.write((m & 0xF) << 4 | (n & 0x3C) >>> 2);
    for (int j = i;; j = i)
    {
      i = j + 1;
      j = paramString[j];
      if (j != 61)
      {
        j = b[j];
        if (i < k) {
          break label261;
        }
        label205:
        if (j == -1) {
          break label31;
        }
        localByteArrayOutputStream.write(j | (n & 0x3) << 6);
        break;
        if (m != -1) {
          break label30;
        }
        j = i;
        break label79;
        label238:
        return localByteArrayOutputStream.toByteArray();
        label244:
        if (n != -1) {
          break label30;
        }
        j = i;
        break label126;
      }
      return localByteArrayOutputStream.toByteArray();
      label261:
      if (j != -1) {
        break label30;
      }
    }
  }
  
  private static byte[] b(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
  {
    IvParameterSpec localIvParameterSpec = new IvParameterSpec(cv.a());
    SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
    paramArrayOfByte1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
    try
    {
      paramArrayOfByte1.init(1, localSecretKeySpec, localIvParameterSpec);
      return paramArrayOfByte1.doFinal(paramArrayOfByte2);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      for (;;)
      {
        localInvalidAlgorithmParameterException.printStackTrace();
      }
    }
  }
  
  private static String c(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int j = paramArrayOfByte.length;
    int i = 0;
    if (i >= j) {}
    for (;;)
    {
      return localStringBuffer.toString();
      int n = i + 1;
      int k = paramArrayOfByte[i] & 0xFF;
      if (n != j)
      {
        int m = n + 1;
        n = paramArrayOfByte[n] & 0xFF;
        if (m == j) {
          break label202;
        }
        i = m + 1;
        m = paramArrayOfByte[m] & 0xFF;
        localStringBuffer.append(a[(k >>> 2)]);
        localStringBuffer.append(a[((k & 0x3) << 4 | (n & 0xF0) >>> 4)]);
        localStringBuffer.append(a[((n & 0xF) << 2 | (m & 0xC0) >>> 6)]);
        localStringBuffer.append(a[(m & 0x3F)]);
        break;
      }
      localStringBuffer.append(a[(k >>> 2)]);
      localStringBuffer.append(a[((k & 0x3) << 4)]);
      localStringBuffer.append("==");
      continue;
      label202:
      localStringBuffer.append(a[(k >>> 2)]);
      localStringBuffer.append(a[((k & 0x3) << 4 | (n & 0xF0) >>> 4)]);
      localStringBuffer.append(a[((n & 0xF) << 2)]);
      localStringBuffer.append("=");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */