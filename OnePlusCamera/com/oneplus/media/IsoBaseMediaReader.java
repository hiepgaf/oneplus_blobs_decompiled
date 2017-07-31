package com.oneplus.media;

import com.oneplus.io.StreamState;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IsoBaseMediaReader
  implements AutoCloseable
{
  public static final int BOX_TYPE_FILE_TYPE = 1718909296;
  public static final int BOX_TYPE_MEDIA = 1835297121;
  public static final int BOX_TYPE_MEDIA_INFO = 1835626086;
  public static final int BOX_TYPE_MOVIE = 1836019574;
  public static final int BOX_TYPE_MOVIE_HEADER = 1836476516;
  public static final int BOX_TYPE_SAMPLE_DESCRIPTION = 1937011556;
  public static final int BOX_TYPE_SAMPLE_TABLE = 1937007212;
  public static final int BOX_TYPE_TRACK = 1953653099;
  public static final int BOX_TYPE_TRACK_HEADER = 1953196132;
  public static final int BOX_TYPE_VIDEO_MEDIA_HEADER = 1986881636;
  private final byte[] m_Buffer8 = new byte[8];
  private byte[] m_CurrentBoxData;
  private long m_CurrentBoxSize;
  private int m_CurrentBoxType;
  private boolean m_IsCompleted;
  private final InputStream m_Stream;
  private StreamState m_StreamInitState;
  
  public IsoBaseMediaReader(InputStream paramInputStream)
  {
    this(paramInputStream, false);
  }
  
  private IsoBaseMediaReader(InputStream paramInputStream, boolean paramBoolean)
  {
    this.m_Stream = paramInputStream;
    if (paramBoolean) {}
    try
    {
      this.m_StreamInitState = new StreamState(paramInputStream);
      return;
    }
    catch (IOException paramInputStream)
    {
      throw new RuntimeException("Fail to save stream state", paramInputStream);
    }
  }
  
  private Integer readInteger()
  {
    try
    {
      if (this.m_Stream.read(this.m_Buffer8, 0, 4) < 4) {
        return null;
      }
      int i = this.m_Buffer8[0];
      int j = this.m_Buffer8[1];
      int k = this.m_Buffer8[2];
      int m = this.m_Buffer8[3];
      return Integer.valueOf(i << 24 | j << 16 & 0xFF0000 | k << 8 & 0xFF00 | m & 0xFF);
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private Long readLong()
  {
    try
    {
      if (this.m_Stream.read(this.m_Buffer8, 0, 8) < 8) {
        return null;
      }
      long l1 = this.m_Buffer8[0];
      long l2 = this.m_Buffer8[1];
      long l3 = this.m_Buffer8[2];
      long l4 = this.m_Buffer8[3];
      long l5 = this.m_Buffer8[4] << 24;
      long l6 = this.m_Buffer8[5] << 16;
      long l7 = this.m_Buffer8[6] << 8;
      long l8 = this.m_Buffer8[7];
      return Long.valueOf(l1 << 56 | l2 << 48 & 0xFF000000000000 | l3 << 40 & 0xFF0000000000 | l4 << 32 & 0xFF00000000 | l5 & 0xFF000000 | l6 & 0xFF0000 | l7 & 0xFF00 | l8 & 0xFF);
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  public void close()
  {
    this.m_IsCompleted = true;
    try
    {
      if (this.m_StreamInitState != null) {
        this.m_StreamInitState.close();
      }
      return;
    }
    catch (Throwable localThrowable) {}
  }
  
  public long currentBoxDataSize()
  {
    return this.m_CurrentBoxSize;
  }
  
  public int currentBoxType()
  {
    return this.m_CurrentBoxType;
  }
  
  public byte[] getBoxData()
  {
    int i;
    byte[] arrayOfByte;
    if (this.m_CurrentBoxData == null)
    {
      if (this.m_CurrentBoxSize == 0L)
      {
        this.m_IsCompleted = true;
        return null;
      }
      if (this.m_CurrentBoxSize > 2147483647L) {
        return null;
      }
      i = (int)this.m_CurrentBoxSize;
      arrayOfByte = new byte[i];
    }
    try
    {
      if (this.m_Stream.read(arrayOfByte) < i)
      {
        this.m_IsCompleted = true;
        return null;
      }
      this.m_CurrentBoxData = arrayOfByte;
      return this.m_CurrentBoxData;
    }
    catch (Throwable localThrowable)
    {
      this.m_IsCompleted = true;
    }
    return null;
  }
  
  public IsoBaseMediaReader getBoxDataReader()
  {
    if (this.m_IsCompleted) {
      throw new RuntimeException("No box to read");
    }
    if (this.m_CurrentBoxData != null) {
      return new IsoBaseMediaReader(new ByteArrayInputStream(this.m_CurrentBoxData));
    }
    if (this.m_CurrentBoxSize == 0L) {
      return new IsoBaseMediaReader(this.m_Stream);
    }
    if (this.m_CurrentBoxSize <= 2147483647L) {
      return new IsoBaseMediaReader(this.m_Stream, true);
    }
    throw new RuntimeException("Size of box data is too large : " + this.m_CurrentBoxSize);
  }
  
  public boolean read()
  {
    if (this.m_IsCompleted) {
      return false;
    }
    if (this.m_CurrentBoxType != 0) {
      if (this.m_CurrentBoxSize > 0L)
      {
        if (this.m_CurrentBoxData == null) {
          try
          {
            if (this.m_Stream.skip(this.m_CurrentBoxSize) >= this.m_CurrentBoxSize) {
              break label73;
            }
            this.m_IsCompleted = true;
            return false;
          }
          catch (Throwable localThrowable)
          {
            this.m_IsCompleted = true;
            return false;
          }
        }
      }
      else
      {
        this.m_IsCompleted = true;
        return false;
      }
    }
    label73:
    this.m_CurrentBoxSize = 0L;
    this.m_CurrentBoxType = 0;
    this.m_CurrentBoxData = null;
    Object localObject = readInteger();
    if (localObject == null)
    {
      this.m_IsCompleted = true;
      return false;
    }
    Integer localInteger = readInteger();
    if (localInteger == null)
    {
      this.m_IsCompleted = true;
      return false;
    }
    if (((Integer)localObject).intValue() == 1)
    {
      localObject = readLong();
      if (localObject == null)
      {
        this.m_IsCompleted = true;
        return false;
      }
    }
    for (this.m_CurrentBoxSize = (((Long)localObject).longValue() - 16L); this.m_CurrentBoxSize < 0L; this.m_CurrentBoxSize = (((Integer)localObject).intValue() - 8))
    {
      this.m_CurrentBoxSize = 0L;
      this.m_IsCompleted = true;
      return false;
    }
    this.m_CurrentBoxType = localInteger.intValue();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IsoBaseMediaReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */