package android.filterfw.core;

import java.util.Arrays;
import java.util.HashMap;

public class MutableFrameFormat
  extends FrameFormat
{
  public MutableFrameFormat() {}
  
  public MutableFrameFormat(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public void setBaseType(int paramInt)
  {
    this.mBaseType = paramInt;
    this.mBytesPerSample = bytesPerSampleOf(paramInt);
  }
  
  public void setBytesPerSample(int paramInt)
  {
    this.mBytesPerSample = paramInt;
    this.mSize = -1;
  }
  
  public void setDimensionCount(int paramInt)
  {
    this.mDimensions = new int[paramInt];
  }
  
  public void setDimensions(int paramInt)
  {
    this.mDimensions = new int[] { paramInt };
    this.mSize = -1;
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    this.mDimensions = new int[] { paramInt1, paramInt2 };
    this.mSize = -1;
  }
  
  public void setDimensions(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mDimensions = new int[] { paramInt1, paramInt2, paramInt3 };
    this.mSize = -1;
  }
  
  public void setDimensions(int[] paramArrayOfInt)
  {
    Object localObject = null;
    if (paramArrayOfInt == null) {}
    for (paramArrayOfInt = (int[])localObject;; paramArrayOfInt = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length))
    {
      this.mDimensions = paramArrayOfInt;
      this.mSize = -1;
      return;
    }
  }
  
  public void setMetaValue(String paramString, Object paramObject)
  {
    if (this.mMetaData == null) {
      this.mMetaData = new KeyValueMap();
    }
    this.mMetaData.put(paramString, paramObject);
  }
  
  public void setObjectClass(Class paramClass)
  {
    this.mObjectClass = paramClass;
  }
  
  public void setTarget(int paramInt)
  {
    this.mTarget = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/MutableFrameFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */