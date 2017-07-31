package com.adobe.xmp.impl;

public class Base64
{
  private static final byte EQUAL = -3;
  private static final byte INVALID = -1;
  private static final byte WHITESPACE = -2;
  private static byte[] ascii;
  private static byte[] base64;
  
  static
  {
    int k = 0;
    base64 = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
    ascii = new byte['ÿ'];
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i >= 255) {
        break;
      }
      ascii[i] = -1;
      i += 1;
    }
    while (j < base64.length)
    {
      ascii[base64[j]] = ((byte)(byte)j);
      j += 1;
    }
    ascii[9] = -2;
    ascii[10] = -2;
    ascii[13] = -2;
    ascii[32] = -2;
    ascii[61] = -3;
  }
  
  public static final String decode(String paramString)
  {
    return new String(decode(paramString.getBytes()));
  }
  
  public static final byte[] decode(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    int m = 0;
    int j = 0;
    int i = 0;
    int k = i;
    if (j < paramArrayOfByte.length)
    {
      k = ascii[paramArrayOfByte[j]];
      if (k < 0)
      {
        if (k == -1) {}
      }
      else {
        for (;;)
        {
          j += 1;
          break;
          paramArrayOfByte[i] = ((byte)k);
          i += 1;
        }
      }
      throw new IllegalArgumentException("Invalid base 64 string");
    }
    while (k > 0)
    {
      if (paramArrayOfByte[(k - 1)] != -3) {
        break;
      }
      k -= 1;
    }
    byte[] arrayOfByte = new byte[k * 3 / 4];
    i = 0;
    j = m;
    while (j < arrayOfByte.length - 2)
    {
      arrayOfByte[j] = ((byte)(byte)(paramArrayOfByte[i] << 2 & 0xFF | paramArrayOfByte[(i + 1)] >>> 4 & 0x3));
      arrayOfByte[(j + 1)] = ((byte)(byte)(paramArrayOfByte[(i + 1)] << 4 & 0xFF | paramArrayOfByte[(i + 2)] >>> 2 & 0xF));
      arrayOfByte[(j + 2)] = ((byte)(byte)(paramArrayOfByte[(i + 2)] << 6 & 0xFF | paramArrayOfByte[(i + 3)] & 0x3F));
      i += 4;
      j += 3;
    }
    if (j >= arrayOfByte.length) {}
    for (;;)
    {
      j += 1;
      if (j < arrayOfByte.length) {
        break;
      }
      return arrayOfByte;
      arrayOfByte[j] = ((byte)(byte)(paramArrayOfByte[i] << 2 & 0xFF | paramArrayOfByte[(i + 1)] >>> 4 & 0x3));
    }
    k = paramArrayOfByte[(i + 1)];
    arrayOfByte[j] = ((byte)(byte)(paramArrayOfByte[(i + 2)] >>> 2 & 0xF | k << 4 & 0xFF));
    return arrayOfByte;
  }
  
  public static final String encode(String paramString)
  {
    return new String(encode(paramString.getBytes()));
  }
  
  public static final byte[] encode(byte[] paramArrayOfByte)
  {
    return encode(paramArrayOfByte, 0);
  }
  
  public static final byte[] encode(byte[] paramArrayOfByte, int paramInt)
  {
    int k = 0;
    int i = paramInt / 4 * 4;
    if (i >= 0)
    {
      j = (paramArrayOfByte.length + 2) / 3 * 4;
      if (i > 0) {
        break label280;
      }
    }
    byte[] arrayOfByte;
    int m;
    for (;;)
    {
      arrayOfByte = new byte[j];
      m = 0;
      paramInt = 0;
      while (m + 3 <= paramArrayOfByte.length)
      {
        int n = m + 1;
        m = paramArrayOfByte[m];
        int i1 = n + 1;
        int i2 = paramArrayOfByte[n];
        n = i1 + 1;
        m = (i2 & 0xFF) << 8 | (m & 0xFF) << 16 | (paramArrayOfByte[i1] & 0xFF) << 0;
        i1 = paramInt + 1;
        arrayOfByte[paramInt] = ((byte)base64[((m & 0xFC0000) >> 18)]);
        paramInt = i1 + 1;
        arrayOfByte[i1] = ((byte)base64[((m & 0x3F000) >> 12)]);
        i2 = paramInt + 1;
        arrayOfByte[paramInt] = ((byte)base64[((m & 0xFC0) >> 6)]);
        i1 = i2 + 1;
        arrayOfByte[i2] = ((byte)base64[(m & 0x3F)]);
        i2 = k + 4;
        k = i2;
        m = n;
        paramInt = i1;
        if (i1 < j)
        {
          k = i2;
          m = n;
          paramInt = i1;
          if (i > 0)
          {
            k = i2;
            m = n;
            paramInt = i1;
            if (i2 % i == 0)
            {
              arrayOfByte[i1] = 10;
              paramInt = i1 + 1;
              k = i2;
              m = n;
            }
          }
        }
      }
      i = 0;
      break;
      label280:
      j += (j - 1) / i;
    }
    if (paramArrayOfByte.length - m != 2)
    {
      if (paramArrayOfByte.length - m != 1) {
        return arrayOfByte;
      }
    }
    else
    {
      i = (paramArrayOfByte[m] & 0xFF) << 16 | (paramArrayOfByte[(m + 1)] & 0xFF) << 8;
      j = paramInt + 1;
      arrayOfByte[paramInt] = ((byte)base64[((i & 0xFC0000) >> 18)]);
      paramInt = j + 1;
      arrayOfByte[j] = ((byte)base64[((i & 0x3F000) >> 12)]);
      j = paramInt + 1;
      arrayOfByte[paramInt] = ((byte)base64[((i & 0xFC0) >> 6)]);
      arrayOfByte[j] = 61;
      return arrayOfByte;
    }
    i = (paramArrayOfByte[m] & 0xFF) << 16;
    int j = paramInt + 1;
    arrayOfByte[paramInt] = ((byte)base64[((i & 0xFC0000) >> 18)]);
    paramInt = j + 1;
    arrayOfByte[j] = ((byte)base64[((i & 0x3F000) >> 12)]);
    i = paramInt + 1;
    arrayOfByte[paramInt] = 61;
    arrayOfByte[i] = 61;
    return arrayOfByte;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */