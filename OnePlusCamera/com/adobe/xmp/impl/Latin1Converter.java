package com.adobe.xmp.impl;

import java.io.UnsupportedEncodingException;

public class Latin1Converter
{
  private static final int STATE_START = 0;
  private static final int STATE_UTF8CHAR = 11;
  
  public static ByteBuffer convert(ByteBuffer paramByteBuffer)
  {
    int i1 = 0;
    if (!"UTF-8".equals(paramByteBuffer.getEncoding())) {
      return paramByteBuffer;
    }
    byte[] arrayOfByte = new byte[8];
    ByteBuffer localByteBuffer = new ByteBuffer(paramByteBuffer.length() * 4 / 3);
    int k = 0;
    int j = 0;
    int m = 0;
    int i = 0;
    if (k < paramByteBuffer.length())
    {
      int n = paramByteBuffer.charAt(k);
      switch (j)
      {
      case 0: 
      default: 
        if (n >= 127)
        {
          if (n >= 192) {
            break label143;
          }
          localByteBuffer.append(convertToUTF8((byte)n));
          n = k;
        }
        break;
      }
      for (;;)
      {
        k = n + 1;
        break;
        localByteBuffer.append((byte)n);
        n = k;
        continue;
        label143:
        j = -1;
        m = n;
        for (;;)
        {
          if (j >= 8) {}
          while ((m & 0x80) != 128)
          {
            arrayOfByte[i] = ((byte)(byte)n);
            i += 1;
            m = j;
            j = 11;
            n = k;
            break;
          }
          m <<= 1;
          j += 1;
        }
        if (m <= 0) {}
        while ((n & 0xC0) != 128)
        {
          localByteBuffer.append(convertToUTF8(arrayOfByte[0]));
          n = k - i;
          j = 0;
          i = 0;
          break;
        }
        int i2 = i + 1;
        arrayOfByte[i] = ((byte)(byte)n);
        int i3 = m - 1;
        n = k;
        m = i3;
        i = i2;
        if (i3 == 0)
        {
          localByteBuffer.append(arrayOfByte, 0, i2);
          j = 0;
          i = 0;
          n = k;
          m = i3;
        }
      }
    }
    k = i1;
    if (j != 11) {
      return localByteBuffer;
    }
    for (;;)
    {
      localByteBuffer.append(convertToUTF8(arrayOfByte[k]));
      k += 1;
      if (k >= i) {
        break;
      }
    }
  }
  
  private static byte[] convertToUTF8(byte paramByte)
  {
    int i = paramByte & 0xFF;
    if (i < 128) {}
    for (;;)
    {
      return new byte[] { (byte)paramByte };
      if (i == 129) {}
      for (;;)
      {
        try
        {
          return new byte[] { 32 };
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
        if ((i != 141) && (i != 143) && (i != 144) && (i != 157))
        {
          byte[] arrayOfByte = new String(new byte[] { (byte)paramByte }, "cp1252").getBytes("UTF-8");
          return arrayOfByte;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/Latin1Converter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */