package com.fingerprints.extension.engineering;

public class SensorImage
{
  private BitsPerPixel mBitsPerPixel;
  private int mHeight;
  private byte[] mPixels;
  private int mWidth;
  
  public SensorImage(BitsPerPixel paramBitsPerPixel, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    this.mBitsPerPixel = paramBitsPerPixel;
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mPixels = paramArrayOfByte;
  }
  
  public BitsPerPixel getBitsPerPixel()
  {
    return this.mBitsPerPixel;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public byte[] getPixels()
  {
    return this.mPixels;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public static enum BitsPerPixel
  {
    BPP_8;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/engineering/SensorImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */