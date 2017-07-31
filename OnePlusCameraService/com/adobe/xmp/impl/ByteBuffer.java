package com.adobe.xmp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteBuffer
{
  private byte[] buffer;
  private String encoding = null;
  private int length;
  
  public ByteBuffer(int paramInt)
  {
    this.buffer = new byte[paramInt];
    this.length = 0;
  }
  
  public ByteBuffer(InputStream paramInputStream)
    throws IOException
  {
    this.length = 0;
    this.buffer = new byte['䀀'];
    for (;;)
    {
      int i = paramInputStream.read(this.buffer, this.length, 16384);
      if (i > 0)
      {
        this.length += i;
        if (i == 16384) {}
      }
      else
      {
        return;
      }
      ensureCapacity(this.length + 16384);
    }
  }
  
  public ByteBuffer(byte[] paramArrayOfByte)
  {
    this.buffer = paramArrayOfByte;
    this.length = paramArrayOfByte.length;
  }
  
  public ByteBuffer(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt <= paramArrayOfByte.length)
    {
      this.buffer = paramArrayOfByte;
      this.length = paramInt;
      return;
    }
    throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
  }
  
  public ByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= paramArrayOfByte.length - paramInt1)
    {
      this.buffer = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, 0, paramInt2);
      this.length = paramInt2;
      return;
    }
    throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt <= this.buffer.length) {
      return;
    }
    byte[] arrayOfByte = this.buffer;
    this.buffer = new byte[arrayOfByte.length * 2];
    System.arraycopy(arrayOfByte, 0, this.buffer, 0, arrayOfByte.length);
  }
  
  public void append(byte paramByte)
  {
    ensureCapacity(this.length + 1);
    byte[] arrayOfByte = this.buffer;
    int i = this.length;
    this.length = (i + 1);
    arrayOfByte[i] = ((byte)paramByte);
  }
  
  public void append(ByteBuffer paramByteBuffer)
  {
    append(paramByteBuffer.buffer, 0, paramByteBuffer.length);
  }
  
  public void append(byte[] paramArrayOfByte)
  {
    append(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void append(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    ensureCapacity(this.length + paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.length, paramInt2);
    this.length += paramInt2;
  }
  
  public byte byteAt(int paramInt)
  {
    if (paramInt >= this.length) {
      throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
    }
    return this.buffer[paramInt];
  }
  
  public int charAt(int paramInt)
  {
    if (paramInt >= this.length) {
      throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
    }
    return this.buffer[paramInt] & 0xFF;
  }
  
  public InputStream getByteStream()
  {
    return new ByteArrayInputStream(this.buffer, 0, this.length);
  }
  
  public String getEncoding()
  {
    if (this.encoding != null) {}
    for (;;)
    {
      return this.encoding;
      if (this.length >= 2)
      {
        if (this.buffer[0] == 0) {
          break label103;
        }
        if ((this.buffer[0] & 0xFF) < 128) {
          break label179;
        }
        if ((this.buffer[0] & 0xFF) == 239) {
          break label232;
        }
        if ((this.buffer[0] & 0xFF) == 254) {
          break label241;
        }
        if (this.length >= 4) {
          break label250;
        }
      }
      label103:
      label179:
      label214:
      label232:
      label241:
      label250:
      while (this.buffer[2] != 0)
      {
        this.encoding = "UTF-16";
        break;
        this.encoding = "UTF-8";
        break;
        if (this.length < 4) {}
        while (this.buffer[1] != 0)
        {
          this.encoding = "UTF-16BE";
          break;
        }
        if ((this.buffer[2] & 0xFF) != 254) {}
        while ((this.buffer[3] & 0xFF) != 255)
        {
          this.encoding = "UTF-32";
          break;
        }
        this.encoding = "UTF-32BE";
        break;
        if (this.buffer[1] == 0) {
          if (this.length >= 4) {
            break label214;
          }
        }
        while (this.buffer[2] != 0)
        {
          this.encoding = "UTF-16LE";
          break;
          this.encoding = "UTF-8";
          break;
        }
        this.encoding = "UTF-32LE";
        break;
        this.encoding = "UTF-8";
        break;
        this.encoding = "UTF-16";
        break;
      }
      this.encoding = "UTF-32";
    }
  }
  
  public int length()
  {
    return this.length;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/ByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */