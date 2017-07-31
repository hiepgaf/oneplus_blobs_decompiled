package com.amap.api.location.core;

public class b
{
  private static final char[] a = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static byte[] b = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
  
  public static String a(byte[] paramArrayOfByte)
  {
    for (;;)
    {
      StringBuffer localStringBuffer;
      int j;
      int i;
      try
      {
        localStringBuffer = new StringBuffer();
        j = paramArrayOfByte.length;
        i = 0;
      }
      catch (Throwable paramArrayOfByte)
      {
        int m;
        paramArrayOfByte.printStackTrace();
        return "";
      }
      return localStringBuffer.toString();
      int n = i + 1;
      int k = paramArrayOfByte[i] & 0xFF;
      if (n != j)
      {
        m = n + 1;
        n = paramArrayOfByte[n] & 0xFF;
        if (m != j)
        {
          i = m + 1;
          m = paramArrayOfByte[m] & 0xFF;
          localStringBuffer.append(a[(k >>> 2)]);
          localStringBuffer.append(a[((k & 0x3) << 4 | (n & 0xF0) >>> 4)]);
          localStringBuffer.append(a[((n & 0xF) << 2 | (m & 0xC0) >>> 6)]);
          localStringBuffer.append(a[(m & 0x3F)]);
          break label273;
        }
      }
      else
      {
        localStringBuffer.append(a[(k >>> 2)]);
        localStringBuffer.append(a[((k & 0x3) << 4)]);
        localStringBuffer.append("==");
        continue;
      }
      localStringBuffer.append(a[(k >>> 2)]);
      localStringBuffer.append(a[((k & 0x3) << 4 | (n & 0xF0) >>> 4)]);
      localStringBuffer.append(a[((n & 0xF) << 2)]);
      localStringBuffer.append("=");
      continue;
      label273:
      if (i < j) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */