package android.filterfw.core;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class Frame
{
  public static final int NO_BINDING = 0;
  public static final long TIMESTAMP_NOT_SET = -2L;
  public static final long TIMESTAMP_UNKNOWN = -1L;
  private long mBindingId = 0L;
  private int mBindingType = 0;
  private FrameFormat mFormat;
  private FrameManager mFrameManager;
  private boolean mReadOnly = false;
  private int mRefCount = 1;
  private boolean mReusable = false;
  private long mTimestamp = -2L;
  
  Frame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    this.mFormat = paramFrameFormat.mutableCopy();
    this.mFrameManager = paramFrameManager;
  }
  
  Frame(FrameFormat paramFrameFormat, FrameManager paramFrameManager, int paramInt, long paramLong)
  {
    this.mFormat = paramFrameFormat.mutableCopy();
    this.mFrameManager = paramFrameManager;
    this.mBindingType = paramInt;
    this.mBindingId = paramLong;
  }
  
  protected static Bitmap convertBitmapToRGBA(Bitmap paramBitmap)
  {
    if (paramBitmap.getConfig() == Bitmap.Config.ARGB_8888) {
      return paramBitmap;
    }
    paramBitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, false);
    if (paramBitmap == null) {
      throw new RuntimeException("Error converting bitmap to RGBA!");
    }
    if (paramBitmap.getRowBytes() != paramBitmap.getWidth() * 4) {
      throw new RuntimeException("Unsupported row byte count in bitmap!");
    }
    return paramBitmap;
  }
  
  protected void assertFrameMutable()
  {
    if (isReadOnly()) {
      throw new RuntimeException("Attempting to modify read-only frame!");
    }
  }
  
  final int decRefCount()
  {
    this.mRefCount -= 1;
    return this.mRefCount;
  }
  
  public long getBindingId()
  {
    return this.mBindingId;
  }
  
  public int getBindingType()
  {
    return this.mBindingType;
  }
  
  public abstract Bitmap getBitmap();
  
  public int getCapacity()
  {
    return getFormat().getSize();
  }
  
  public abstract ByteBuffer getData();
  
  public abstract float[] getFloats();
  
  public FrameFormat getFormat()
  {
    return this.mFormat;
  }
  
  public FrameManager getFrameManager()
  {
    return this.mFrameManager;
  }
  
  public abstract int[] getInts();
  
  public abstract Object getObjectValue();
  
  public int getRefCount()
  {
    return this.mRefCount;
  }
  
  public long getTimestamp()
  {
    return this.mTimestamp;
  }
  
  protected abstract boolean hasNativeAllocation();
  
  final int incRefCount()
  {
    this.mRefCount += 1;
    return this.mRefCount;
  }
  
  public boolean isReadOnly()
  {
    return this.mReadOnly;
  }
  
  final boolean isReusable()
  {
    return this.mReusable;
  }
  
  final void markReadOnly()
  {
    this.mReadOnly = true;
  }
  
  protected void onFrameFetch() {}
  
  protected void onFrameStore() {}
  
  public Frame release()
  {
    if (this.mFrameManager != null) {
      return this.mFrameManager.releaseFrame(this);
    }
    return this;
  }
  
  protected abstract void releaseNativeAllocation();
  
  protected boolean requestResize(int[] paramArrayOfInt)
  {
    return false;
  }
  
  protected void reset(FrameFormat paramFrameFormat)
  {
    this.mFormat = paramFrameFormat.mutableCopy();
    this.mReadOnly = false;
    this.mRefCount = 1;
  }
  
  public Frame retain()
  {
    if (this.mFrameManager != null) {
      return this.mFrameManager.retainFrame(this);
    }
    return this;
  }
  
  public abstract void setBitmap(Bitmap paramBitmap);
  
  public void setData(ByteBuffer paramByteBuffer)
  {
    setData(paramByteBuffer, 0, paramByteBuffer.limit());
  }
  
  public abstract void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2);
  
  public void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    setData(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public void setDataFromFrame(Frame paramFrame)
  {
    setData(paramFrame.getData());
  }
  
  public abstract void setFloats(float[] paramArrayOfFloat);
  
  protected void setFormat(FrameFormat paramFrameFormat)
  {
    this.mFormat = paramFrameFormat.mutableCopy();
  }
  
  protected void setGenericObjectValue(Object paramObject)
  {
    throw new RuntimeException("Cannot set object value of unsupported type: " + paramObject.getClass());
  }
  
  public abstract void setInts(int[] paramArrayOfInt);
  
  public void setObjectValue(Object paramObject)
  {
    assertFrameMutable();
    if ((paramObject instanceof int[]))
    {
      setInts((int[])paramObject);
      return;
    }
    if ((paramObject instanceof float[]))
    {
      setFloats((float[])paramObject);
      return;
    }
    if ((paramObject instanceof ByteBuffer))
    {
      setData((ByteBuffer)paramObject);
      return;
    }
    if ((paramObject instanceof Bitmap))
    {
      setBitmap((Bitmap)paramObject);
      return;
    }
    setGenericObjectValue(paramObject);
  }
  
  protected void setReusable(boolean paramBoolean)
  {
    this.mReusable = paramBoolean;
  }
  
  public void setTimestamp(long paramLong)
  {
    this.mTimestamp = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/Frame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */