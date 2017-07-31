package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Size;
import com.android.internal.util.Preconditions;

public final class StreamConfiguration
{
  private final int mFormat;
  private final int mHeight;
  private final boolean mInput;
  private final int mWidth;
  
  public StreamConfiguration(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    this.mFormat = StreamConfigurationMap.checkArgumentFormatInternal(paramInt1);
    this.mWidth = Preconditions.checkArgumentPositive(paramInt2, "width must be positive");
    this.mHeight = Preconditions.checkArgumentPositive(paramInt3, "height must be positive");
    this.mInput = paramBoolean;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof StreamConfiguration))
    {
      paramObject = (StreamConfiguration)paramObject;
      if ((this.mFormat == ((StreamConfiguration)paramObject).mFormat) && (this.mWidth == ((StreamConfiguration)paramObject).mWidth) && (this.mHeight == ((StreamConfiguration)paramObject).mHeight)) {
        return this.mInput == ((StreamConfiguration)paramObject).mInput;
      }
      return false;
    }
    return false;
  }
  
  public final int getFormat()
  {
    return this.mFormat;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public Size getSize()
  {
    return new Size(this.mWidth, this.mHeight);
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public int hashCode()
  {
    int i = 1;
    int j = this.mFormat;
    int k = this.mWidth;
    int m = this.mHeight;
    if (this.mInput) {}
    for (;;)
    {
      return HashCodeHelpers.hashCode(new int[] { j, k, m, i });
      i = 0;
    }
  }
  
  public boolean isInput()
  {
    return this.mInput;
  }
  
  public boolean isOutput()
  {
    return !this.mInput;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/StreamConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */