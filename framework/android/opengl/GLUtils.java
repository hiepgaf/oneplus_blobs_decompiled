package android.opengl;

import android.graphics.Bitmap;

public final class GLUtils
{
  public static String getEGLErrorString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "0x" + Integer.toHexString(paramInt);
    case 12288: 
      return "EGL_SUCCESS";
    case 12289: 
      return "EGL_NOT_INITIALIZED";
    case 12290: 
      return "EGL_BAD_ACCESS";
    case 12291: 
      return "EGL_BAD_ALLOC";
    case 12292: 
      return "EGL_BAD_ATTRIBUTE";
    case 12293: 
      return "EGL_BAD_CONFIG";
    case 12294: 
      return "EGL_BAD_CONTEXT";
    case 12295: 
      return "EGL_BAD_CURRENT_SURFACE";
    case 12296: 
      return "EGL_BAD_DISPLAY";
    case 12297: 
      return "EGL_BAD_MATCH";
    case 12298: 
      return "EGL_BAD_NATIVE_PIXMAP";
    case 12299: 
      return "EGL_BAD_NATIVE_WINDOW";
    case 12300: 
      return "EGL_BAD_PARAMETER";
    case 12301: 
      return "EGL_BAD_SURFACE";
    }
    return "EGL_CONTEXT_LOST";
  }
  
  public static int getInternalFormat(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("getInternalFormat can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    int i = native_getInternalFormat(paramBitmap);
    if (i < 0) {
      throw new IllegalArgumentException("Unknown internalformat");
    }
    return i;
  }
  
  public static int getType(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("getType can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    int i = native_getType(paramBitmap);
    if (i < 0) {
      throw new IllegalArgumentException("Unknown type");
    }
    return i;
  }
  
  private static native int native_getInternalFormat(Bitmap paramBitmap);
  
  private static native int native_getType(Bitmap paramBitmap);
  
  private static native int native_texImage2D(int paramInt1, int paramInt2, int paramInt3, Bitmap paramBitmap, int paramInt4, int paramInt5);
  
  private static native int native_texSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Bitmap paramBitmap, int paramInt5, int paramInt6);
  
  public static void texImage2D(int paramInt1, int paramInt2, int paramInt3, Bitmap paramBitmap, int paramInt4)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("texImage2D can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    if (native_texImage2D(paramInt1, paramInt2, paramInt3, paramBitmap, -1, paramInt4) != 0) {
      throw new IllegalArgumentException("invalid Bitmap format");
    }
  }
  
  public static void texImage2D(int paramInt1, int paramInt2, int paramInt3, Bitmap paramBitmap, int paramInt4, int paramInt5)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("texImage2D can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    if (native_texImage2D(paramInt1, paramInt2, paramInt3, paramBitmap, paramInt4, paramInt5) != 0) {
      throw new IllegalArgumentException("invalid Bitmap format");
    }
  }
  
  public static void texImage2D(int paramInt1, int paramInt2, Bitmap paramBitmap, int paramInt3)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("texImage2D can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    if (native_texImage2D(paramInt1, paramInt2, -1, paramBitmap, -1, paramInt3) != 0) {
      throw new IllegalArgumentException("invalid Bitmap format");
    }
  }
  
  public static void texSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("texSubImage2D can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    if (native_texSubImage2D(paramInt1, paramInt2, paramInt3, paramInt4, paramBitmap, -1, getType(paramBitmap)) != 0) {
      throw new IllegalArgumentException("invalid Bitmap format");
    }
  }
  
  public static void texSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Bitmap paramBitmap, int paramInt5, int paramInt6)
  {
    if (paramBitmap == null) {
      throw new NullPointerException("texSubImage2D can't be used with a null Bitmap");
    }
    if (paramBitmap.isRecycled()) {
      throw new IllegalArgumentException("bitmap is recycled");
    }
    if (native_texSubImage2D(paramInt1, paramInt2, paramInt3, paramInt4, paramBitmap, paramInt5, paramInt6) != 0) {
      throw new IllegalArgumentException("invalid Bitmap format");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/GLUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */