package android.graphics;

import android.content.res.AssetManager.AssetInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Movie
{
  private long mNativeMovie;
  
  private Movie(long paramLong)
  {
    if (paramLong == 0L) {
      throw new RuntimeException("native movie creation failed");
    }
    this.mNativeMovie = paramLong;
  }
  
  public static native Movie decodeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public static Movie decodeFile(String paramString)
  {
    try
    {
      paramString = new FileInputStream(paramString);
      return decodeTempStream(paramString);
    }
    catch (FileNotFoundException paramString) {}
    return null;
  }
  
  public static Movie decodeStream(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      return null;
    }
    if ((paramInputStream instanceof AssetManager.AssetInputStream)) {
      return nativeDecodeAsset(((AssetManager.AssetInputStream)paramInputStream).getNativeAsset());
    }
    return nativeDecodeStream(paramInputStream);
  }
  
  private static Movie decodeTempStream(InputStream paramInputStream)
  {
    Object localObject = null;
    try
    {
      Movie localMovie = decodeStream(paramInputStream);
      localObject = localMovie;
      paramInputStream.close();
      return localMovie;
    }
    catch (IOException paramInputStream) {}
    return (Movie)localObject;
  }
  
  private native void nDraw(long paramLong1, float paramFloat1, float paramFloat2, long paramLong2);
  
  private static native Movie nativeDecodeAsset(long paramLong);
  
  private static native Movie nativeDecodeStream(InputStream paramInputStream);
  
  private static native void nativeDestructor(long paramLong);
  
  public void draw(Canvas paramCanvas, float paramFloat1, float paramFloat2)
  {
    nDraw(paramCanvas.getNativeCanvasWrapper(), paramFloat1, paramFloat2, 0L);
  }
  
  public void draw(Canvas paramCanvas, float paramFloat1, float paramFloat2, Paint paramPaint)
  {
    long l2 = paramCanvas.getNativeCanvasWrapper();
    if (paramPaint != null) {}
    for (long l1 = paramPaint.getNativeInstance();; l1 = 0L)
    {
      nDraw(l2, paramFloat1, paramFloat2, l1);
      return;
    }
  }
  
  public native int duration();
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDestructor(this.mNativeMovie);
      this.mNativeMovie = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public native int height();
  
  public native boolean isOpaque();
  
  public native boolean setTime(int paramInt);
  
  public native int width();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Movie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */