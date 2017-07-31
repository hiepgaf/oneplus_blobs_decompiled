package com.oneplus.media;

import android.graphics.Bitmap;
import java.nio.IntBuffer;

public class ArgbBitmapBuffer
  extends BitmapBuffer<int[]>
{
  public ArgbBitmapBuffer(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public ArgbBitmapBuffer(Bitmap paramBitmap)
  {
    super(paramBitmap);
  }
  
  public void copyFromBitmap(Bitmap paramBitmap)
  {
    paramBitmap.copyPixelsToBuffer(IntBuffer.wrap((int[])getData()));
  }
  
  public void copyToBitmap(Bitmap paramBitmap)
  {
    paramBitmap.copyPixelsFromBuffer(IntBuffer.wrap((int[])getData()));
  }
  
  protected int[] createBuffer(int paramInt1, int paramInt2)
  {
    return new int[paramInt1 * paramInt2];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/ArgbBitmapBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */