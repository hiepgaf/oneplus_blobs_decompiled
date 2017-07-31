package com.oneplus.media;

import android.graphics.Bitmap;
import java.nio.IntBuffer;

public class HslBitmapBuffer
  extends BitmapBuffer<short[]>
{
  private static final double MAX_COMPONENT_VALUE = 65535.0D;
  
  public HslBitmapBuffer(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public HslBitmapBuffer(Bitmap paramBitmap)
  {
    super(paramBitmap);
  }
  
  public HslBitmapBuffer(ArgbBitmapBuffer paramArgbBitmapBuffer)
  {
    super(paramArgbBitmapBuffer.getWidth(), paramArgbBitmapBuffer.getHeight());
    argbToHslNative(paramArgbBitmapBuffer.getWidth(), paramArgbBitmapBuffer.getHeight(), (int[])paramArgbBitmapBuffer.getData(), (short[])getData());
  }
  
  private static final void argbToHsl(int paramInt1, int paramInt2, int[] paramArrayOfInt, short[] paramArrayOfShort)
  {
    int i1 = paramInt1 * 3;
    int j = paramInt2;
    int i = paramInt1 * (paramInt2 - 1);
    paramInt2 = i1 * (paramInt2 - 1);
    while (j > 0)
    {
      int n = paramInt1;
      int m = i + (paramInt1 - 1);
      int k = paramInt2 + (paramInt1 - 1) * 3;
      if (n > 0)
      {
        int i2 = paramArrayOfInt[m];
        double d1 = ((0xFF0000 & i2) >> 16) / 255.0D;
        double d5 = ((0xFF00 & i2) >> 8) / 255.0D;
        double d6 = (i2 & 0xFF) / 255.0D;
        double d3 = Math.max(Math.max(d6, d5), d1);
        double d4 = Math.min(Math.min(d6, d5), d1);
        double d2 = d3 - d4;
        if (Math.abs(d2) < 1.0E-4D)
        {
          d1 = 0.0D;
          label151:
          d3 = (d3 + d4) / 2.0D;
          if (d2 != 0.0D) {
            break label318;
          }
        }
        label318:
        for (d2 = 0.0D;; d2 /= (1.0D - Math.abs(2.0D * d3 - 1.0D)))
        {
          paramArrayOfShort[k] = ((short)(int)(65535.0D * d1 / 6.0D));
          paramArrayOfShort[(k + 1)] = ((short)(int)(65535.0D * d2));
          paramArrayOfShort[(k + 2)] = ((short)(int)(65535.0D * d3));
          n -= 1;
          m -= 1;
          k -= 3;
          break;
          if (Math.abs(d3 - d6) < 1.0E-4D)
          {
            d1 = (d5 - d1) / d2 % 6.0D;
            break label151;
          }
          if (Math.abs(d3 - d5) < 1.0E-4D)
          {
            d1 = (d1 - d6) / d2 + 2.0D;
            break label151;
          }
          d1 = (d6 - d5) / d2 + 4.0D;
          break label151;
        }
      }
      j -= 1;
      i -= paramInt1;
      paramInt2 -= i1;
    }
  }
  
  private static final native void argbToHslNative(int paramInt1, int paramInt2, int[] paramArrayOfInt, short[] paramArrayOfShort);
  
  private static final void hslToArgb(int paramInt1, int paramInt2, short[] paramArrayOfShort, int[] paramArrayOfInt)
  {
    int i1 = paramInt1 * 3;
    int j = paramInt2;
    int i = paramInt1 * (paramInt2 - 1);
    paramInt2 = i1 * (paramInt2 - 1);
    while (j > 0)
    {
      int n = paramInt1;
      int m = i + (paramInt1 - 1);
      int k = paramInt2 + (paramInt1 - 1) * 3;
      if (n > 0)
      {
        double d2 = paramArrayOfShort[k];
        double d3 = paramArrayOfShort[(k + 1)];
        double d4 = paramArrayOfShort[(k + 2)];
        double d1 = d2;
        if (d2 < 0.0D) {
          d1 = d2 + 65536.0D;
        }
        d2 = d3;
        if (d3 < 0.0D) {
          d2 = d3 + 65536.0D;
        }
        d3 = d4;
        if (d4 < 0.0D) {
          d3 = d4 + 65536.0D;
        }
        d1 /= 65535.0D;
        d2 /= 65535.0D;
        d4 = d3 / 65535.0D;
        d3 = d1 * 6.0D;
        d1 = (1.0D - Math.abs(2.0D * d4 - 1.0D)) * d2;
        double d6 = d4 - d1 / 2.0D;
        d2 = d1 * (1.0D - Math.abs(d3 % 2.0D - 1.0D));
        label231:
        double d5;
        if (d3 < 1.0D)
        {
          d4 = 0.0D;
          d3 = d1;
          d1 = d4;
          d5 = d3 + d6;
          d3 = d2 + d6;
          d4 = d1 + d6;
          if (d5 >= 0.0D) {
            break label450;
          }
          d1 = 0.0D;
          label262:
          if (d3 >= 0.0D) {
            break label467;
          }
          d2 = 0.0D;
          label272:
          if (d4 >= 0.0D) {
            break label484;
          }
          d3 = 0.0D;
        }
        for (;;)
        {
          paramArrayOfInt[m] = (paramArrayOfInt[m] & 0xFF000000 | (int)(255.0D * d3) << 16 | (int)(255.0D * d2) << 8 | (int)(255.0D * d1));
          n -= 1;
          m -= 1;
          k -= 3;
          break;
          if (d3 < 2.0D)
          {
            d3 = d2;
            d2 = d1;
            d1 = 0.0D;
            break label231;
          }
          if (d3 < 3.0D)
          {
            d4 = 0.0D;
            d3 = d1;
            d1 = d2;
            d2 = d3;
            d3 = d4;
            break label231;
          }
          if (d3 < 4.0D)
          {
            d3 = 0.0D;
            break label231;
          }
          if (d3 < 5.0D)
          {
            d3 = d2;
            d2 = 0.0D;
            break label231;
          }
          d3 = d1;
          d4 = 0.0D;
          d1 = d2;
          d2 = d4;
          break label231;
          label450:
          d1 = d5;
          if (d5 <= 1.0D) {
            break label262;
          }
          d1 = 1.0D;
          break label262;
          label467:
          d2 = d3;
          if (d3 <= 1.0D) {
            break label272;
          }
          d2 = 1.0D;
          break label272;
          label484:
          d3 = d4;
          if (d4 > 1.0D) {
            d3 = 1.0D;
          }
        }
      }
      j -= 1;
      i -= paramInt1;
      paramInt2 -= i1;
    }
  }
  
  private static final native void hslToArgbNative(int paramInt1, int paramInt2, short[] paramArrayOfShort, int[] paramArrayOfInt);
  
  public void copyFromBitmap(Bitmap paramBitmap)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    int[] arrayOfInt = new int[i * j];
    paramBitmap.copyPixelsToBuffer(IntBuffer.wrap(arrayOfInt));
    argbToHslNative(i, j, arrayOfInt, (short[])getData());
  }
  
  public void copyToBitmap(Bitmap paramBitmap)
  {
    int[] arrayOfInt = new int[paramBitmap.getWidth() * paramBitmap.getHeight()];
    IntBuffer localIntBuffer = IntBuffer.wrap(arrayOfInt);
    paramBitmap.copyPixelsToBuffer(localIntBuffer);
    hslToArgbNative(paramBitmap.getWidth(), paramBitmap.getHeight(), (short[])getData(), arrayOfInt);
    localIntBuffer.position(0);
    paramBitmap.copyPixelsFromBuffer(localIntBuffer);
  }
  
  protected short[] createBuffer(int paramInt1, int paramInt2)
  {
    return new short[paramInt1 * paramInt2 * 3];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/HslBitmapBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */