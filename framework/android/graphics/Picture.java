package android.graphics;

import java.io.InputStream;
import java.io.OutputStream;

public class Picture
{
  private static final int WORKING_STREAM_STORAGE = 16384;
  private long mNativePicture;
  private Canvas mRecordingCanvas;
  
  public Picture()
  {
    this(nativeConstructor(0L));
  }
  
  private Picture(long paramLong)
  {
    if (paramLong == 0L) {
      throw new RuntimeException();
    }
    this.mNativePicture = paramLong;
  }
  
  public Picture(Picture paramPicture) {}
  
  @Deprecated
  public static Picture createFromStream(InputStream paramInputStream)
  {
    return new Picture(nativeCreateFromStream(paramInputStream, new byte['䀀']));
  }
  
  private static native long nativeBeginRecording(long paramLong, int paramInt1, int paramInt2);
  
  private static native long nativeConstructor(long paramLong);
  
  private static native long nativeCreateFromStream(InputStream paramInputStream, byte[] paramArrayOfByte);
  
  private static native void nativeDestructor(long paramLong);
  
  private static native void nativeDraw(long paramLong1, long paramLong2);
  
  private static native void nativeEndRecording(long paramLong);
  
  private static native int nativeGetHeight(long paramLong);
  
  private static native int nativeGetWidth(long paramLong);
  
  private static native boolean nativeWriteToStream(long paramLong, OutputStream paramOutputStream, byte[] paramArrayOfByte);
  
  public Canvas beginRecording(int paramInt1, int paramInt2)
  {
    this.mRecordingCanvas = new RecordingCanvas(this, nativeBeginRecording(this.mNativePicture, paramInt1, paramInt2));
    return this.mRecordingCanvas;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mRecordingCanvas != null) {
      endRecording();
    }
    nativeDraw(paramCanvas.getNativeCanvasWrapper(), this.mNativePicture);
  }
  
  public void endRecording()
  {
    if (this.mRecordingCanvas != null)
    {
      this.mRecordingCanvas = null;
      nativeEndRecording(this.mNativePicture);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDestructor(this.mNativePicture);
      this.mNativePicture = 0L;
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getHeight()
  {
    return nativeGetHeight(this.mNativePicture);
  }
  
  public int getWidth()
  {
    return nativeGetWidth(this.mNativePicture);
  }
  
  @Deprecated
  public void writeToStream(OutputStream paramOutputStream)
  {
    if (paramOutputStream == null) {
      throw new NullPointerException();
    }
    if (!nativeWriteToStream(this.mNativePicture, paramOutputStream, new byte['䀀'])) {
      throw new RuntimeException();
    }
  }
  
  private static class RecordingCanvas
    extends Canvas
  {
    private final Picture mPicture;
    
    public RecordingCanvas(Picture paramPicture, long paramLong)
    {
      super();
      this.mPicture = paramPicture;
    }
    
    public void drawPicture(Picture paramPicture)
    {
      if (this.mPicture == paramPicture) {
        throw new RuntimeException("Cannot draw a picture into its recording canvas");
      }
      super.drawPicture(paramPicture);
    }
    
    public void setBitmap(Bitmap paramBitmap)
    {
      throw new RuntimeException("Cannot call setBitmap on a picture canvas");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Picture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */