package android.hardware.display;

import android.graphics.Rect;

public final class DisplayViewport
{
  public int deviceHeight;
  public int deviceWidth;
  public int displayId;
  public final Rect logicalFrame = new Rect();
  public int orientation;
  public final Rect physicalFrame = new Rect();
  public boolean valid;
  
  public void copyFrom(DisplayViewport paramDisplayViewport)
  {
    this.valid = paramDisplayViewport.valid;
    this.displayId = paramDisplayViewport.displayId;
    this.orientation = paramDisplayViewport.orientation;
    this.logicalFrame.set(paramDisplayViewport.logicalFrame);
    this.physicalFrame.set(paramDisplayViewport.physicalFrame);
    this.deviceWidth = paramDisplayViewport.deviceWidth;
    this.deviceHeight = paramDisplayViewport.deviceHeight;
  }
  
  public String toString()
  {
    return "DisplayViewport{valid=" + this.valid + ", displayId=" + this.displayId + ", orientation=" + this.orientation + ", logicalFrame=" + this.logicalFrame + ", physicalFrame=" + this.physicalFrame + ", deviceWidth=" + this.deviceWidth + ", deviceHeight=" + this.deviceHeight + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/DisplayViewport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */