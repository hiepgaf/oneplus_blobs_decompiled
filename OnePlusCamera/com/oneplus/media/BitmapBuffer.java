package com.oneplus.media;

import android.graphics.Bitmap;

public abstract class BitmapBuffer<TBuffer>
{
  private volatile TBuffer m_Data;
  private final int m_Height;
  private final int m_Width;
  
  protected BitmapBuffer(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("Invalid size : " + paramInt1 + "x" + paramInt2);
    }
    this.m_Width = paramInt1;
    this.m_Height = paramInt2;
    this.m_Data = createBuffer(paramInt1, paramInt2);
  }
  
  protected BitmapBuffer(Bitmap paramBitmap)
  {
    this.m_Width = paramBitmap.getWidth();
    this.m_Height = paramBitmap.getHeight();
    this.m_Data = createBuffer(this.m_Width, this.m_Height);
    copyFromBitmap(paramBitmap);
  }
  
  public abstract void copyFromBitmap(Bitmap paramBitmap);
  
  public abstract void copyToBitmap(Bitmap paramBitmap);
  
  protected abstract TBuffer createBuffer(int paramInt1, int paramInt2);
  
  public final TBuffer getData()
  {
    return (TBuffer)this.m_Data;
  }
  
  public final int getHeight()
  {
    return this.m_Height;
  }
  
  public final int getWidth()
  {
    return this.m_Width;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/BitmapBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */