package com.oneplus.media;

public abstract class IsoBaseMediaBox
{
  protected static final long TIME_DIFF_1904_1970 = 2081721600000L;
  private final int m_Flags;
  private final int m_Version;
  
  protected IsoBaseMediaBox(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.m_Version = paramArrayOfByte[0];
      this.m_Flags = (paramArrayOfByte[1] << 16 & 0xFF0000 | paramArrayOfByte[2] << 8 & 0xFF00 | paramArrayOfByte[3] & 0xFF);
      return;
    }
    this.m_Version = 0;
    this.m_Flags = 0;
  }
  
  public static float getFixedPointNumber(byte[] paramArrayOfByte, int paramInt)
  {
    int k = paramArrayOfByte[paramInt];
    int m = paramArrayOfByte[(paramInt + 1)];
    int j = paramArrayOfByte[(paramInt + 2)] << 8 & 0xFF00 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
    int i = 10;
    paramInt = j;
    while (paramInt >= 10)
    {
      i *= 10;
      paramInt /= 10;
    }
    return ((k << 8 & 0xFF00 | m & 0xFF) << 16 >> 16) + j / i;
  }
  
  public static int getInteger(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 0)] << 24 | paramArrayOfByte[(paramInt + 1)] << 16 & 0xFF0000 | paramArrayOfByte[(paramInt + 2)] << 8 & 0xFF00 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  public static long getLong(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 0)] << 56 | paramArrayOfByte[(paramInt + 1)] << 48 & 0xFF000000000000 | paramArrayOfByte[(paramInt + 2)] << 40 & 0xFF0000000000 | paramArrayOfByte[(paramInt + 3)] << 32 & 0xFF00000000 | paramArrayOfByte[(paramInt + 4)] << 24 & 0xFF000000 | paramArrayOfByte[(paramInt + 5)] << 16 & 0xFF0000 | paramArrayOfByte[(paramInt + 6)] << 8 & 0xFF00 | paramArrayOfByte[(paramInt + 7)] & 0xFF;
  }
  
  public static long getUInteger(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 0)] << 24 & 0xFF000000 | paramArrayOfByte[(paramInt + 1)] << 16 & 0xFF0000 | paramArrayOfByte[(paramInt + 2)] << 8 & 0xFF00 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  public final int getFlags()
  {
    return this.m_Flags;
  }
  
  public final int getVersion()
  {
    return this.m_Version;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/IsoBaseMediaBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */