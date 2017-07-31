package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Size;
import com.android.internal.util.Preconditions;

public final class StreamConfigurationDuration
{
  private final long mDurationNs;
  private final int mFormat;
  private final int mHeight;
  private final int mWidth;
  
  public StreamConfigurationDuration(int paramInt1, int paramInt2, int paramInt3, long paramLong)
  {
    this.mFormat = StreamConfigurationMap.checkArgumentFormatInternal(paramInt1);
    this.mWidth = Preconditions.checkArgumentPositive(paramInt2, "width must be positive");
    this.mHeight = Preconditions.checkArgumentPositive(paramInt3, "height must be positive");
    this.mDurationNs = Preconditions.checkArgumentNonnegative(paramLong, "durationNs must be non-negative");
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof StreamConfigurationDuration))
    {
      paramObject = (StreamConfigurationDuration)paramObject;
      if ((this.mFormat == ((StreamConfigurationDuration)paramObject).mFormat) && (this.mWidth == ((StreamConfigurationDuration)paramObject).mWidth) && (this.mHeight == ((StreamConfigurationDuration)paramObject).mHeight)) {
        return this.mDurationNs == ((StreamConfigurationDuration)paramObject).mDurationNs;
      }
      return false;
    }
    return false;
  }
  
  public long getDuration()
  {
    return this.mDurationNs;
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
    return HashCodeHelpers.hashCode(new int[] { this.mFormat, this.mWidth, this.mHeight, (int)this.mDurationNs, (int)(this.mDurationNs >>> 32) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/StreamConfigurationDuration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */