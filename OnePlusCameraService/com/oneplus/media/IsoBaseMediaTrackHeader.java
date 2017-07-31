package com.oneplus.media;

public class IsoBaseMediaTrackHeader
  extends IsoBaseMediaBox
{
  private final long m_CreationTime;
  private final long m_Duration;
  private final int m_Height;
  private final long m_ModifiedTime;
  private final int m_Orientation;
  private final int m_TrackId;
  private final int m_Width;
  
  public IsoBaseMediaTrackHeader(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte, true);
    if (getVersion() == 1)
    {
      this.m_CreationTime = getLong(paramArrayOfByte, 4);
      this.m_ModifiedTime = getLong(paramArrayOfByte, 12);
    }
    float f1;
    for (int i = 20;; i = 12)
    {
      this.m_TrackId = getInteger(paramArrayOfByte, i);
      this.m_Duration = getInteger(paramArrayOfByte, i + 8);
      this.m_Width = Math.round(getFixedPointNumber(paramArrayOfByte, i + 64));
      this.m_Height = Math.round(getFixedPointNumber(paramArrayOfByte, i + 68));
      f1 = getFixedPointNumber(paramArrayOfByte, i + 28);
      float f2 = getFixedPointNumber(paramArrayOfByte, i + 32);
      if (Math.abs(f1) >= 0.001F) {
        break label180;
      }
      if (f2 <= 0.0F) {
        break;
      }
      this.m_Orientation = 270;
      return;
      this.m_CreationTime = (getInteger(paramArrayOfByte, 4) & 0xFFFFFFFF);
      this.m_ModifiedTime = (getInteger(paramArrayOfByte, 8) & 0xFFFFFFFF);
    }
    this.m_Orientation = 90;
    return;
    label180:
    if (f1 > 0.0F)
    {
      this.m_Orientation = 0;
      return;
    }
    this.m_Orientation = 180;
  }
  
  public final int getHeight()
  {
    return this.m_Height;
  }
  
  public final int getOrientation()
  {
    return this.m_Orientation;
  }
  
  public final int getWidth()
  {
    return this.m_Width;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IsoBaseMediaTrackHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */