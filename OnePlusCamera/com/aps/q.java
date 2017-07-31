package com.aps;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class q
{
  private static q a = null;
  
  static String a(Object paramObject, String paramString)
  {
    DecimalFormat localDecimalFormat = new DecimalFormat("#", new DecimalFormatSymbols(Locale.US));
    localDecimalFormat.applyPattern(paramString);
    return localDecimalFormat.format(paramObject);
  }
  
  static byte[] a(int paramInt)
  {
    return new byte[] { (byte)(byte)(paramInt & 0xFF), (byte)(byte)(paramInt >> 8 & 0xFF), (byte)(byte)(paramInt >> 16 & 0xFF), (byte)(byte)(paramInt >> 24 & 0xFF) };
  }
  
  public static byte[] a(long paramLong)
  {
    byte[] arrayOfByte = new byte[8];
    int i = 0;
    for (;;)
    {
      if (i >= arrayOfByte.length) {
        return arrayOfByte;
      }
      arrayOfByte[i] = ((byte)(byte)(int)(paramLong >> i * 8 & 0xFF));
      i += 1;
    }
  }
  
  static byte[] a(String paramString)
  {
    return a(Integer.parseInt(paramString));
  }
  
  static byte[] b(int paramInt)
  {
    return new byte[] { (byte)(byte)(paramInt & 0xFF), (byte)(byte)(paramInt >> 8 & 0xFF) };
  }
  
  static byte[] b(String paramString)
  {
    return b(Integer.parseInt(paramString));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/q.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */