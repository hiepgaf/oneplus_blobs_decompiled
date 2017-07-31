package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.OutputStream;

public final class CountOutputStream
  extends OutputStream
{
  private int bytesWritten = 0;
  private final OutputStream out;
  
  CountOutputStream(OutputStream paramOutputStream)
  {
    this.out = paramOutputStream;
  }
  
  public int getBytesWritten()
  {
    return this.bytesWritten;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    this.out.write(paramInt);
    this.bytesWritten += 1;
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    this.out.write(paramArrayOfByte);
    this.bytesWritten += paramArrayOfByte.length;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    this.bytesWritten += paramInt2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/CountOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */