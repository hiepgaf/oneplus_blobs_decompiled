package android.filterfw.core;

public class NativeBuffer
{
  private Frame mAttachedFrame;
  private long mDataPointer = 0L;
  private boolean mOwnsData = false;
  private int mRefCount = 1;
  private int mSize = 0;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  public NativeBuffer() {}
  
  public NativeBuffer(int paramInt)
  {
    allocate(getElementSize() * paramInt);
    this.mOwnsData = true;
  }
  
  private native boolean allocate(int paramInt);
  
  private native boolean deallocate(boolean paramBoolean);
  
  private native boolean nativeCopyTo(NativeBuffer paramNativeBuffer);
  
  protected void assertReadable()
  {
    if ((this.mDataPointer == 0L) || (this.mSize == 0)) {}
    while ((this.mAttachedFrame != null) && (!this.mAttachedFrame.hasNativeAllocation())) {
      throw new NullPointerException("Attempting to read from null data frame!");
    }
  }
  
  protected void assertWritable()
  {
    if (isReadOnly()) {
      throw new RuntimeException("Attempting to modify read-only native (structured) data!");
    }
  }
  
  void attachToFrame(Frame paramFrame)
  {
    this.mAttachedFrame = paramFrame;
  }
  
  public int count()
  {
    if (this.mDataPointer != 0L) {
      return this.mSize / getElementSize();
    }
    return 0;
  }
  
  public int getElementSize()
  {
    return 1;
  }
  
  public boolean isReadOnly()
  {
    if (this.mAttachedFrame != null) {
      return this.mAttachedFrame.isReadOnly();
    }
    return false;
  }
  
  public NativeBuffer mutableCopy()
  {
    try
    {
      NativeBuffer localNativeBuffer = (NativeBuffer)getClass().newInstance();
      if ((this.mSize <= 0) || (nativeCopyTo(localNativeBuffer))) {
        return localNativeBuffer;
      }
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Unable to allocate a copy of " + getClass() + "! Make " + "sure the class has a default constructor!");
    }
    throw new RuntimeException("Failed to copy NativeBuffer to mutable instance!");
  }
  
  public NativeBuffer release()
  {
    int i = 0;
    if (this.mAttachedFrame != null) {
      if (this.mAttachedFrame.release() == null) {
        i = 1;
      }
    }
    while (i != 0)
    {
      deallocate(this.mOwnsData);
      return null;
      i = 0;
      continue;
      if (this.mOwnsData)
      {
        this.mRefCount -= 1;
        if (this.mRefCount == 0) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    return this;
  }
  
  public NativeBuffer retain()
  {
    if (this.mAttachedFrame != null) {
      this.mAttachedFrame.retain();
    }
    while (!this.mOwnsData) {
      return this;
    }
    this.mRefCount += 1;
    return this;
  }
  
  public int size()
  {
    return this.mSize;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/NativeBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */