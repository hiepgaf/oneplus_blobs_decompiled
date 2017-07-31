package android.graphics;

public class PixelFormat
{
  @Deprecated
  public static final int A_8 = 8;
  @Deprecated
  public static final int JPEG = 256;
  @Deprecated
  public static final int LA_88 = 10;
  @Deprecated
  public static final int L_8 = 9;
  public static final int OPAQUE = -1;
  @Deprecated
  public static final int RGBA_4444 = 7;
  @Deprecated
  public static final int RGBA_5551 = 6;
  public static final int RGBA_8888 = 1;
  public static final int RGBX_8888 = 2;
  @Deprecated
  public static final int RGB_332 = 11;
  public static final int RGB_565 = 4;
  public static final int RGB_888 = 3;
  public static final int TRANSLUCENT = -3;
  public static final int TRANSPARENT = -2;
  public static final int UNKNOWN = 0;
  @Deprecated
  public static final int YCbCr_420_SP = 17;
  @Deprecated
  public static final int YCbCr_422_I = 20;
  @Deprecated
  public static final int YCbCr_422_SP = 16;
  public int bitsPerPixel;
  public int bytesPerPixel;
  
  public static boolean formatHasAlpha(int paramInt)
  {
    switch (paramInt)
    {
    case -1: 
    case 0: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 9: 
    default: 
      return false;
    }
    return true;
  }
  
  public static void getPixelFormatInfo(int paramInt, PixelFormat paramPixelFormat)
  {
    switch (paramInt)
    {
    case 5: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 18: 
    case 19: 
    default: 
      throw new IllegalArgumentException("unknown pixel format " + paramInt);
    case 1: 
    case 2: 
      paramPixelFormat.bitsPerPixel = 32;
      paramPixelFormat.bytesPerPixel = 4;
      return;
    case 3: 
      paramPixelFormat.bitsPerPixel = 24;
      paramPixelFormat.bytesPerPixel = 3;
      return;
    case 4: 
    case 6: 
    case 7: 
    case 10: 
      paramPixelFormat.bitsPerPixel = 16;
      paramPixelFormat.bytesPerPixel = 2;
      return;
    case 8: 
    case 9: 
    case 11: 
      paramPixelFormat.bitsPerPixel = 8;
      paramPixelFormat.bytesPerPixel = 1;
      return;
    case 16: 
    case 20: 
      paramPixelFormat.bitsPerPixel = 16;
      paramPixelFormat.bytesPerPixel = 1;
      return;
    }
    paramPixelFormat.bitsPerPixel = 12;
    paramPixelFormat.bytesPerPixel = 1;
  }
  
  public static boolean isPublicFormat(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PixelFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */