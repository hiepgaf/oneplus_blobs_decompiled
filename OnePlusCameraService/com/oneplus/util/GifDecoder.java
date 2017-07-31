package com.oneplus.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import com.oneplus.base.NativeLibrary;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GifDecoder
{
  private static final String TAG = GifDecoder.class.getSimpleName();
  private Bitmap m_Bitmap = null;
  private long m_NativeDecoder;
  
  public GifDecoder()
  {
    NativeLibrary.load();
    nativeBegin();
  }
  
  private Bitmap createBitmap(int paramInt1, int paramInt2)
  {
    this.m_Bitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    return this.m_Bitmap;
  }
  
  private native void nativeBegin();
  
  private native void nativeRelease();
  
  private void setBitmapPixels(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    this.m_Bitmap.setPixels(paramArrayOfInt, 0, paramInt1, 0, 0, paramInt2, paramInt3);
  }
  
  public native int frameCount();
  
  public native long geDuration(int paramInt);
  
  public native Bitmap getFrame(int paramInt);
  
  public void read(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("inputstream is null");
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Object localObject = null;
    try
    {
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      read(paramInputStream);
    }
    catch (Exception paramInputStream)
    {
      Log.w(TAG, "read() -e:" + paramInputStream);
      paramInputStream = (InputStream)localObject;
    }
    for (;;)
    {
      return;
      paramInputStream = localByteArrayOutputStream.toByteArray();
    }
  }
  
  public native void read(String paramString);
  
  public native void read(byte[] paramArrayOfByte);
  
  public void release()
  {
    nativeRelease();
    this.m_Bitmap = null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/GifDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */