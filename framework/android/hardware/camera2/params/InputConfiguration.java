package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;

public final class InputConfiguration
{
  private final int mFormat;
  private final int mHeight;
  private final int mWidth;
  
  public InputConfiguration(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mFormat = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof InputConfiguration)) {
      return false;
    }
    paramObject = (InputConfiguration)paramObject;
    return (((InputConfiguration)paramObject).getWidth() == this.mWidth) && (((InputConfiguration)paramObject).getHeight() == this.mHeight) && (((InputConfiguration)paramObject).getFormat() == this.mFormat);
  }
  
  public int getFormat()
  {
    return this.mFormat;
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCode(new int[] { this.mWidth, this.mHeight, this.mFormat });
  }
  
  public String toString()
  {
    return String.format("InputConfiguration(w:%d, h:%d, format:%d)", new Object[] { Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mFormat) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/InputConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */