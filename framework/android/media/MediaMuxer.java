package android.media;

import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class MediaMuxer
{
  private static final int MUXER_STATE_INITIALIZED = 0;
  private static final int MUXER_STATE_STARTED = 1;
  private static final int MUXER_STATE_STOPPED = 2;
  private static final int MUXER_STATE_UNINITIALIZED = -1;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private int mLastTrackIndex = -1;
  private long mNativeObject;
  private int mState = -1;
  
  static
  {
    System.loadLibrary("media_jni");
  }
  
  public MediaMuxer(String paramString, int paramInt)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("path must not be null");
    }
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("format is invalid");
    }
    Object localObject3 = null;
    try
    {
      paramString = new RandomAccessFile(paramString, "rws");
      if (paramString == null) {
        break label115;
      }
    }
    finally
    {
      try
      {
        this.mNativeObject = nativeSetup(paramString.getFD(), paramInt);
        this.mState = 0;
        this.mCloseGuard.open("release");
        if (paramString != null) {
          paramString.close();
        }
        return;
      }
      finally {}
      localObject1 = finally;
      paramString = (String)localObject3;
    }
    paramString.close();
    label115:
    throw ((Throwable)localObject1);
  }
  
  private static native int nativeAddTrack(long paramLong, String[] paramArrayOfString, Object[] paramArrayOfObject);
  
  private static native void nativeRelease(long paramLong);
  
  private static native void nativeSetLocation(long paramLong, int paramInt1, int paramInt2);
  
  private static native void nativeSetOrientationHint(long paramLong, int paramInt);
  
  private static native long nativeSetup(FileDescriptor paramFileDescriptor, int paramInt);
  
  private static native void nativeStart(long paramLong);
  
  private static native void nativeStop(long paramLong);
  
  private static native void nativeWriteSampleData(long paramLong1, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3, long paramLong2, int paramInt4);
  
  public int addTrack(MediaFormat paramMediaFormat)
  {
    if (paramMediaFormat == null) {
      throw new IllegalArgumentException("format must not be null.");
    }
    if (this.mState != 0) {
      throw new IllegalStateException("Muxer is not initialized.");
    }
    if (this.mNativeObject == 0L) {
      throw new IllegalStateException("Muxer has been released!");
    }
    Object localObject = paramMediaFormat.getMap();
    int i = ((Map)localObject).size();
    if (i > 0)
    {
      paramMediaFormat = new String[i];
      Object[] arrayOfObject = new Object[i];
      i = 0;
      localObject = ((Map)localObject).entrySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        paramMediaFormat[i] = ((String)localEntry.getKey());
        arrayOfObject[i] = localEntry.getValue();
        i += 1;
      }
      i = nativeAddTrack(this.mNativeObject, paramMediaFormat, arrayOfObject);
      if (this.mLastTrackIndex >= i) {
        throw new IllegalArgumentException("Invalid format.");
      }
    }
    else
    {
      throw new IllegalArgumentException("format must not be empty.");
    }
    this.mLastTrackIndex = i;
    return i;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mCloseGuard != null) {
        this.mCloseGuard.warnIfOpen();
      }
      if (this.mNativeObject != 0L)
      {
        nativeRelease(this.mNativeObject);
        this.mNativeObject = 0L;
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void release()
  {
    if (this.mState == 1) {
      stop();
    }
    if (this.mNativeObject != 0L)
    {
      nativeRelease(this.mNativeObject);
      this.mNativeObject = 0L;
      this.mCloseGuard.close();
    }
    this.mState = -1;
  }
  
  public void setLocation(float paramFloat1, float paramFloat2)
  {
    int i = (int)(paramFloat1 * 10000.0F + 0.5D);
    int j = (int)(paramFloat2 * 10000.0F + 0.5D);
    if ((i > 900000) || (i < -900000)) {
      throw new IllegalArgumentException("Latitude: " + paramFloat1 + " out of range.");
    }
    if ((j > 1800000) || (j < -1800000)) {
      throw new IllegalArgumentException("Longitude: " + paramFloat2 + " out of range");
    }
    if ((this.mState == 0) && (this.mNativeObject != 0L))
    {
      nativeSetLocation(this.mNativeObject, i, j);
      return;
    }
    throw new IllegalStateException("Can't set location due to wrong state.");
  }
  
  public void setOrientationHint(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 90) && (paramInt != 180) && (paramInt != 270)) {
      throw new IllegalArgumentException("Unsupported angle: " + paramInt);
    }
    if (this.mState == 0)
    {
      nativeSetOrientationHint(this.mNativeObject, paramInt);
      return;
    }
    throw new IllegalStateException("Can't set rotation degrees due to wrong state.");
  }
  
  public void start()
  {
    if (this.mNativeObject == 0L) {
      throw new IllegalStateException("Muxer has been released!");
    }
    if (this.mState == 0)
    {
      nativeStart(this.mNativeObject);
      this.mState = 1;
      return;
    }
    throw new IllegalStateException("Can't start due to wrong state.");
  }
  
  public void stop()
  {
    if (this.mState == 1)
    {
      nativeStop(this.mNativeObject);
      this.mState = 2;
      return;
    }
    throw new IllegalStateException("Can't stop due to wrong state.");
  }
  
  public void writeSampleData(int paramInt, ByteBuffer paramByteBuffer, MediaCodec.BufferInfo paramBufferInfo)
  {
    if ((paramInt < 0) || (paramInt > this.mLastTrackIndex)) {
      throw new IllegalArgumentException("trackIndex is invalid");
    }
    if (paramByteBuffer == null) {
      throw new IllegalArgumentException("byteBuffer must not be null");
    }
    if (paramBufferInfo == null) {
      throw new IllegalArgumentException("bufferInfo must not be null");
    }
    if ((paramBufferInfo.size < 0) || (paramBufferInfo.offset < 0)) {}
    while ((paramBufferInfo.offset + paramBufferInfo.size > paramByteBuffer.capacity()) || (paramBufferInfo.presentationTimeUs < 0L)) {
      throw new IllegalArgumentException("bufferInfo must specify a valid buffer offset, size and presentation time");
    }
    if (this.mNativeObject == 0L) {
      throw new IllegalStateException("Muxer has been released!");
    }
    if (this.mState != 1) {
      throw new IllegalStateException("Can't write, muxer is not started");
    }
    nativeWriteSampleData(this.mNativeObject, paramInt, paramByteBuffer, paramBufferInfo.offset, paramBufferInfo.size, paramBufferInfo.presentationTimeUs, paramBufferInfo.flags);
  }
  
  public static final class OutputFormat
  {
    public static final int MUXER_OUTPUT_MPEG_4 = 0;
    public static final int MUXER_OUTPUT_WEBM = 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaMuxer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */