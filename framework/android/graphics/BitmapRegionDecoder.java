package android.graphics;

import android.content.res.AssetManager.AssetInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public final class BitmapRegionDecoder
{
  private long mNativeBitmapRegionDecoder;
  private final Object mNativeLock = new Object();
  private boolean mRecycled;
  
  private BitmapRegionDecoder(long paramLong)
  {
    this.mNativeBitmapRegionDecoder = paramLong;
    this.mRecycled = false;
  }
  
  private void checkRecycled(String paramString)
  {
    if (this.mRecycled) {
      throw new IllegalStateException(paramString);
    }
  }
  
  private static native void nativeClean(long paramLong);
  
  private static native Bitmap nativeDecodeRegion(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BitmapFactory.Options paramOptions);
  
  private static native int nativeGetHeight(long paramLong);
  
  private static native int nativeGetWidth(long paramLong);
  
  private static native BitmapRegionDecoder nativeNewInstance(long paramLong, boolean paramBoolean);
  
  private static native BitmapRegionDecoder nativeNewInstance(FileDescriptor paramFileDescriptor, boolean paramBoolean);
  
  private static native BitmapRegionDecoder nativeNewInstance(InputStream paramInputStream, byte[] paramArrayOfByte, boolean paramBoolean);
  
  private static native BitmapRegionDecoder nativeNewInstance(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public static BitmapRegionDecoder newInstance(FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException
  {
    return nativeNewInstance(paramFileDescriptor, paramBoolean);
  }
  
  public static BitmapRegionDecoder newInstance(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    if ((paramInputStream instanceof AssetManager.AssetInputStream)) {
      return nativeNewInstance(((AssetManager.AssetInputStream)paramInputStream).getNativeAsset(), paramBoolean);
    }
    return nativeNewInstance(paramInputStream, new byte['䀀'], paramBoolean);
  }
  
  /* Error */
  public static BitmapRegionDecoder newInstance(String paramString, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 59	java/io/FileInputStream
    //   5: dup
    //   6: aload_0
    //   7: invokespecial 60	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   10: astore_0
    //   11: aload_0
    //   12: iload_1
    //   13: invokestatic 62	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
    //   16: astore_2
    //   17: aload_0
    //   18: ifnull +7 -> 25
    //   21: aload_0
    //   22: invokevirtual 67	java/io/InputStream:close	()V
    //   25: aload_2
    //   26: areturn
    //   27: astore_0
    //   28: aload_2
    //   29: areturn
    //   30: astore_0
    //   31: aload_2
    //   32: ifnull +7 -> 39
    //   35: aload_2
    //   36: invokevirtual 67	java/io/InputStream:close	()V
    //   39: aload_0
    //   40: athrow
    //   41: astore_2
    //   42: goto -3 -> 39
    //   45: astore_3
    //   46: aload_0
    //   47: astore_2
    //   48: aload_3
    //   49: astore_0
    //   50: goto -19 -> 31
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	paramString	String
    //   0	53	1	paramBoolean	boolean
    //   1	35	2	localBitmapRegionDecoder	BitmapRegionDecoder
    //   41	1	2	localIOException	IOException
    //   47	1	2	str	String
    //   45	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	25	27	java/io/IOException
    //   2	11	30	finally
    //   35	39	41	java/io/IOException
    //   11	17	45	finally
  }
  
  public static BitmapRegionDecoder newInstance(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    if (((paramInt1 | paramInt2) < 0) || (paramArrayOfByte.length < paramInt1 + paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return nativeNewInstance(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
  }
  
  public Bitmap decodeRegion(Rect paramRect, BitmapFactory.Options paramOptions)
  {
    synchronized (this.mNativeLock)
    {
      checkRecycled("decodeRegion called on recycled region decoder");
      if ((paramRect.right <= 0) || (paramRect.bottom <= 0)) {
        throw new IllegalArgumentException("rectangle is outside the image");
      }
    }
    return paramRect;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      recycle();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getHeight()
  {
    synchronized (this.mNativeLock)
    {
      checkRecycled("getHeight called on recycled region decoder");
      int i = nativeGetHeight(this.mNativeBitmapRegionDecoder);
      return i;
    }
  }
  
  public int getWidth()
  {
    synchronized (this.mNativeLock)
    {
      checkRecycled("getWidth called on recycled region decoder");
      int i = nativeGetWidth(this.mNativeBitmapRegionDecoder);
      return i;
    }
  }
  
  public final boolean isRecycled()
  {
    return this.mRecycled;
  }
  
  public void recycle()
  {
    synchronized (this.mNativeLock)
    {
      if (!this.mRecycled)
      {
        nativeClean(this.mNativeBitmapRegionDecoder);
        this.mRecycled = true;
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/BitmapRegionDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */