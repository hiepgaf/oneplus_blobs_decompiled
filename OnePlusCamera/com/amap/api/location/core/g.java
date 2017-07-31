package com.amap.api.location.core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class g
{
  private static final String[] a = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
  
  public static String a(String paramString)
  {
    Object localObject2 = null;
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      if (localMessageDigest == null)
      {
        if (localMessageDigest != null) {
          break label54;
        }
        paramString = (String)localObject2;
        return a(paramString);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      for (;;)
      {
        localNoSuchAlgorithmException.printStackTrace();
        Object localObject1 = null;
        continue;
        try
        {
          ((MessageDigest)localObject1).update(paramString.getBytes("utf-8"));
        }
        catch (UnsupportedEncodingException paramString)
        {
          paramString.printStackTrace();
        }
        continue;
        label54:
        paramString = ((MessageDigest)localObject1).digest();
      }
    }
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder;
    int i;
    if (paramArrayOfByte != null)
    {
      localStringBuilder = new StringBuilder();
      i = 0;
      if (i >= paramArrayOfByte.length) {
        return localStringBuilder.toString();
      }
    }
    else
    {
      return "";
    }
    String str = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
    if (str.length() != 1) {}
    for (;;)
    {
      localStringBuilder.append(str);
      i += 1;
      break;
      str = '0' + str;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/g.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */