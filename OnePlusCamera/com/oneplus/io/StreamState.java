package com.oneplus.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class StreamState
  implements AutoCloseable
{
  private boolean m_IsFileInputStream;
  private long m_SavedStreamPosition;
  private final InputStream m_Stream;
  
  public StreamState(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, Integer.MAX_VALUE);
  }
  
  public StreamState(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    this.m_Stream = paramInputStream;
    this.m_IsFileInputStream = (paramInputStream instanceof FileInputStream);
    if (this.m_IsFileInputStream) {
      this.m_SavedStreamPosition = ((FileInputStream)paramInputStream).getChannel().position();
    }
    while (!paramInputStream.markSupported()) {
      return;
    }
    paramInputStream.mark(paramInt);
  }
  
  public void close()
    throws Exception
  {
    if (this.m_IsFileInputStream)
    {
      ((FileInputStream)this.m_Stream).getChannel().position(this.m_SavedStreamPosition);
      return;
    }
    this.m_Stream.reset();
  }
  
  public long getSavedStreamPosition()
  {
    return this.m_SavedStreamPosition;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StreamState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */