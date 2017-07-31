package com.oneplus.camera.media;

import android.media.Image.Plane;
import java.nio.ByteBuffer;

public class ImagePlane
  implements Cloneable
{
  private final byte[] m_Data;
  private final int m_PixelStride;
  private final int m_RowStride;
  
  public ImagePlane(Image.Plane paramPlane)
  {
    ByteBuffer localByteBuffer = paramPlane.getBuffer();
    this.m_Data = new byte[localByteBuffer.capacity()];
    this.m_PixelStride = paramPlane.getPixelStride();
    this.m_RowStride = paramPlane.getRowStride();
    localByteBuffer.get(this.m_Data);
  }
  
  public ImagePlane(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.m_Data = paramArrayOfByte;
    this.m_PixelStride = paramInt1;
    this.m_RowStride = paramInt2;
  }
  
  public ImagePlane clone()
  {
    return new ImagePlane((byte[])this.m_Data.clone(), this.m_PixelStride, this.m_RowStride);
  }
  
  public final byte[] getData()
  {
    return this.m_Data;
  }
  
  public final int getPixelStride()
  {
    return this.m_PixelStride;
  }
  
  public final int getRowStride()
  {
    return this.m_RowStride;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/ImagePlane.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */