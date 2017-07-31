package com.oneplus.io;

import java.io.IOException;
import java.io.InputStream;

public class BufferedInputStream
  extends java.io.BufferedInputStream
{
  private final boolean m_OwnsSourceStream;
  
  public BufferedInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, true);
  }
  
  public BufferedInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean)
  {
    super(paramInputStream, paramInt);
    this.m_OwnsSourceStream = paramBoolean;
  }
  
  public BufferedInputStream(InputStream paramInputStream, boolean paramBoolean)
  {
    super(paramInputStream);
    this.m_OwnsSourceStream = paramBoolean;
  }
  
  public void close()
    throws IOException
  {
    if (!this.m_OwnsSourceStream) {
      this.in = null;
    }
    super.close();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/BufferedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */