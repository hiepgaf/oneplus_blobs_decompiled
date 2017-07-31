package com.oneplus.media;

public class IsoBaseMediaMovieHeader
  extends IsoBaseMediaBox
{
  private final long m_CreationTime;
  private final long m_Duration;
  
  public IsoBaseMediaMovieHeader(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte, true);
    if (getVersion() == 1) {
      this.m_CreationTime = (getLong(paramArrayOfByte, 4) * 1000L - 2081721600000L);
    }
    for (int i = 20;; i = 12)
    {
      double d = 1000.0D / getInteger(paramArrayOfByte, i);
      this.m_Duration = ((getUInteger(paramArrayOfByte, i + 4) * d));
      return;
      this.m_CreationTime = (getUInteger(paramArrayOfByte, 4) * 1000L - 2081721600000L);
    }
  }
  
  public final long getCreationTime()
  {
    return this.m_CreationTime;
  }
  
  public final long getDuration()
  {
    return this.m_Duration;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IsoBaseMediaMovieHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */