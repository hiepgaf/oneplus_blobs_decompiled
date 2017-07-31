package android.filterfw.core;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class NativeFrame
  extends Frame
{
  private int nativeFrameId = -1;
  
  static
  {
    System.loadLibrary("filterfw");
  }
  
  NativeFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    super(paramFrameFormat, paramFrameManager);
    int i = paramFrameFormat.getSize();
    nativeAllocate(i);
    if (i != 0) {
      bool = true;
    }
    setReusable(bool);
  }
  
  private native boolean getNativeBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2);
  
  private native boolean getNativeBuffer(NativeBuffer paramNativeBuffer);
  
  private native int getNativeCapacity();
  
  private native byte[] getNativeData(int paramInt);
  
  private native float[] getNativeFloats(int paramInt);
  
  private native int[] getNativeInts(int paramInt);
  
  private native boolean nativeAllocate(int paramInt);
  
  private native boolean nativeCopyFromGL(GLFrame paramGLFrame);
  
  private native boolean nativeCopyFromNative(NativeFrame paramNativeFrame);
  
  private native boolean nativeDeallocate();
  
  private static native int nativeFloatSize();
  
  private static native int nativeIntSize();
  
  private native boolean setNativeBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2);
  
  private native boolean setNativeData(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native boolean setNativeFloats(float[] paramArrayOfFloat);
  
  private native boolean setNativeInts(int[] paramArrayOfInt);
  
  public Bitmap getBitmap()
  {
    if (getFormat().getNumberOfDimensions() != 2) {
      throw new RuntimeException("Attempting to get Bitmap for non 2-dimensional native frame!");
    }
    Bitmap localBitmap = Bitmap.createBitmap(getFormat().getWidth(), getFormat().getHeight(), Bitmap.Config.ARGB_8888);
    if (!getNativeBitmap(localBitmap, localBitmap.getByteCount(), getFormat().getBytesPerSample())) {
      throw new RuntimeException("Could not get bitmap data from native frame!");
    }
    return localBitmap;
  }
  
  public int getCapacity()
  {
    return getNativeCapacity();
  }
  
  public ByteBuffer getData()
  {
    byte[] arrayOfByte = getNativeData(getFormat().getSize());
    if (arrayOfByte == null) {
      return null;
    }
    return ByteBuffer.wrap(arrayOfByte);
  }
  
  public float[] getFloats()
  {
    return getNativeFloats(getFormat().getSize());
  }
  
  public int[] getInts()
  {
    return getNativeInts(getFormat().getSize());
  }
  
  public Object getObjectValue()
  {
    if (getFormat().getBaseType() != 8) {
      return getData();
    }
    Class localClass = getFormat().getObjectClass();
    if (localClass == null) {
      throw new RuntimeException("Attempting to get object data from frame that does not specify a structure object class!");
    }
    if (!NativeBuffer.class.isAssignableFrom(localClass)) {
      throw new RuntimeException("NativeFrame object class must be a subclass of NativeBuffer!");
    }
    try
    {
      NativeBuffer localNativeBuffer = (NativeBuffer)localClass.newInstance();
      if (!getNativeBuffer(localNativeBuffer)) {
        throw new RuntimeException("Could not get the native structured data for frame!");
      }
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Could not instantiate new structure instance of type '" + localClass + "'!");
    }
    localException.attachToFrame(this);
    return localException;
  }
  
  /* Error */
  protected boolean hasNativeAllocation()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 23	android/filterfw/core/NativeFrame:nativeFrameId	I
    //   6: istore_1
    //   7: iload_1
    //   8: iconst_m1
    //   9: if_icmpeq +9 -> 18
    //   12: iconst_1
    //   13: istore_2
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_2
    //   17: ireturn
    //   18: iconst_0
    //   19: istore_2
    //   20: goto -6 -> 14
    //   23: astore_3
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	NativeFrame
    //   6	4	1	i	int
    //   13	7	2	bool	boolean
    //   23	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	23	finally
  }
  
  protected void releaseNativeAllocation()
  {
    try
    {
      nativeDeallocate();
      this.nativeFrameId = -1;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    assertFrameMutable();
    if (getFormat().getNumberOfDimensions() != 2) {
      throw new RuntimeException("Attempting to set Bitmap for non 2-dimensional native frame!");
    }
    if ((getFormat().getWidth() != paramBitmap.getWidth()) || (getFormat().getHeight() != paramBitmap.getHeight())) {
      throw new RuntimeException("Bitmap dimensions do not match native frame dimensions!");
    }
    paramBitmap = convertBitmapToRGBA(paramBitmap);
    if (!setNativeBitmap(paramBitmap, paramBitmap.getByteCount(), getFormat().getBytesPerSample())) {
      throw new RuntimeException("Could not set native frame bitmap data!");
    }
  }
  
  public void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    byte[] arrayOfByte = paramByteBuffer.array();
    if (paramInt2 + paramInt1 > paramByteBuffer.limit()) {
      throw new RuntimeException("Offset and length exceed buffer size in native setData: " + (paramInt2 + paramInt1) + " bytes given, but only " + paramByteBuffer.limit() + " bytes available!");
    }
    if (getFormat().getSize() != paramInt2) {
      throw new RuntimeException("Data size in setData does not match native frame size: Frame size is " + getFormat().getSize() + " bytes, but " + paramInt2 + " bytes given!");
    }
    if (!setNativeData(arrayOfByte, paramInt1, paramInt2)) {
      throw new RuntimeException("Could not set native frame data!");
    }
  }
  
  public void setDataFromFrame(Frame paramFrame)
  {
    if (getFormat().getSize() < paramFrame.getFormat().getSize()) {
      throw new RuntimeException("Attempting to assign frame of size " + paramFrame.getFormat().getSize() + " to " + "smaller native frame of size " + getFormat().getSize() + "!");
    }
    if ((paramFrame instanceof NativeFrame))
    {
      nativeCopyFromNative(paramFrame);
      return;
    }
    if ((paramFrame instanceof GLFrame))
    {
      nativeCopyFromGL(paramFrame);
      return;
    }
    if ((paramFrame instanceof SimpleFrame))
    {
      setObjectValue(paramFrame.getObjectValue());
      return;
    }
    super.setDataFromFrame(paramFrame);
  }
  
  public void setFloats(float[] paramArrayOfFloat)
  {
    assertFrameMutable();
    if (paramArrayOfFloat.length * nativeFloatSize() > getFormat().getSize()) {
      throw new RuntimeException("NativeFrame cannot hold " + paramArrayOfFloat.length + " floats. (Can only hold " + getFormat().getSize() / nativeFloatSize() + " floats).");
    }
    if (!setNativeFloats(paramArrayOfFloat)) {
      throw new RuntimeException("Could not set int values for native frame!");
    }
  }
  
  public void setInts(int[] paramArrayOfInt)
  {
    assertFrameMutable();
    if (paramArrayOfInt.length * nativeIntSize() > getFormat().getSize()) {
      throw new RuntimeException("NativeFrame cannot hold " + paramArrayOfInt.length + " integers. (Can only hold " + getFormat().getSize() / nativeIntSize() + " integers).");
    }
    if (!setNativeInts(paramArrayOfInt)) {
      throw new RuntimeException("Could not set int values for native frame!");
    }
  }
  
  public String toString()
  {
    return "NativeFrame id: " + this.nativeFrameId + " (" + getFormat() + ") of size " + getCapacity();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/NativeFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */