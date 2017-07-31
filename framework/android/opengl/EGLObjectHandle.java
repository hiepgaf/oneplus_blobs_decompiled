package android.opengl;

public abstract class EGLObjectHandle
{
  private final long mHandle;
  
  @Deprecated
  protected EGLObjectHandle(int paramInt)
  {
    this.mHandle = paramInt;
  }
  
  protected EGLObjectHandle(long paramLong)
  {
    this.mHandle = paramLong;
  }
  
  @Deprecated
  public int getHandle()
  {
    if ((this.mHandle & 0xFFFFFFFF) != this.mHandle) {
      throw new UnsupportedOperationException();
    }
    return (int)this.mHandle;
  }
  
  public long getNativeHandle()
  {
    return this.mHandle;
  }
  
  public int hashCode()
  {
    return (int)(this.mHandle ^ this.mHandle >>> 32) + 527;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/opengl/EGLObjectHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */