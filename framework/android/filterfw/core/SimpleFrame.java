package android.filterfw.core;

import android.filterfw.format.ObjectFormat;
import android.graphics.Bitmap;
import java.nio.ByteBuffer;

public class SimpleFrame
  extends Frame
{
  private Object mObject;
  
  SimpleFrame(FrameFormat paramFrameFormat, FrameManager paramFrameManager)
  {
    super(paramFrameFormat, paramFrameManager);
    initWithFormat(paramFrameFormat);
    setReusable(false);
  }
  
  private void initWithFormat(FrameFormat paramFrameFormat)
  {
    int i = paramFrameFormat.getLength();
    switch (paramFrameFormat.getBaseType())
    {
    default: 
      this.mObject = null;
      return;
    case 2: 
      this.mObject = new byte[i];
      return;
    case 3: 
      this.mObject = new short[i];
      return;
    case 4: 
      this.mObject = new int[i];
      return;
    case 5: 
      this.mObject = new float[i];
      return;
    }
    this.mObject = new double[i];
  }
  
  private void setFormatObjectClass(Class paramClass)
  {
    MutableFrameFormat localMutableFrameFormat = getFormat().mutableCopy();
    localMutableFrameFormat.setObjectClass(paramClass);
    setFormat(localMutableFrameFormat);
  }
  
  static SimpleFrame wrapObject(Object paramObject, FrameManager paramFrameManager)
  {
    paramFrameManager = new SimpleFrame(ObjectFormat.fromObject(paramObject, 1), paramFrameManager);
    paramFrameManager.setObjectValue(paramObject);
    return paramFrameManager;
  }
  
  public Bitmap getBitmap()
  {
    if ((this.mObject instanceof Bitmap)) {
      return (Bitmap)this.mObject;
    }
    return null;
  }
  
  public ByteBuffer getData()
  {
    if ((this.mObject instanceof ByteBuffer)) {
      return (ByteBuffer)this.mObject;
    }
    return null;
  }
  
  public float[] getFloats()
  {
    if ((this.mObject instanceof float[])) {
      return (float[])this.mObject;
    }
    return null;
  }
  
  public int[] getInts()
  {
    if ((this.mObject instanceof int[])) {
      return (int[])this.mObject;
    }
    return null;
  }
  
  public Object getObjectValue()
  {
    return this.mObject;
  }
  
  protected boolean hasNativeAllocation()
  {
    return false;
  }
  
  protected void releaseNativeAllocation() {}
  
  public void setBitmap(Bitmap paramBitmap)
  {
    assertFrameMutable();
    setGenericObjectValue(paramBitmap);
  }
  
  public void setData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    assertFrameMutable();
    setGenericObjectValue(ByteBuffer.wrap(paramByteBuffer.array(), paramInt1, paramInt2));
  }
  
  public void setFloats(float[] paramArrayOfFloat)
  {
    assertFrameMutable();
    setGenericObjectValue(paramArrayOfFloat);
  }
  
  protected void setGenericObjectValue(Object paramObject)
  {
    FrameFormat localFrameFormat = getFormat();
    if (localFrameFormat.getObjectClass() == null) {
      setFormatObjectClass(paramObject.getClass());
    }
    while (localFrameFormat.getObjectClass().isAssignableFrom(paramObject.getClass()))
    {
      this.mObject = paramObject;
      return;
    }
    throw new RuntimeException("Attempting to set object value of type '" + paramObject.getClass() + "' on " + "SimpleFrame of type '" + localFrameFormat.getObjectClass() + "'!");
  }
  
  public void setInts(int[] paramArrayOfInt)
  {
    assertFrameMutable();
    setGenericObjectValue(paramArrayOfInt);
  }
  
  public String toString()
  {
    return "SimpleFrame (" + getFormat() + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/SimpleFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */