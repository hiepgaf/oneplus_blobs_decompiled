package android.hardware.display;

import android.view.Display;
import android.view.Surface;

public final class VirtualDisplay
{
  private final Display mDisplay;
  private final DisplayManagerGlobal mGlobal;
  private Surface mSurface;
  private IVirtualDisplayCallback mToken;
  
  VirtualDisplay(DisplayManagerGlobal paramDisplayManagerGlobal, Display paramDisplay, IVirtualDisplayCallback paramIVirtualDisplayCallback, Surface paramSurface)
  {
    this.mGlobal = paramDisplayManagerGlobal;
    this.mDisplay = paramDisplay;
    this.mToken = paramIVirtualDisplayCallback;
    this.mSurface = paramSurface;
  }
  
  public Display getDisplay()
  {
    return this.mDisplay;
  }
  
  public Surface getSurface()
  {
    return this.mSurface;
  }
  
  public void release()
  {
    if (this.mToken != null)
    {
      this.mGlobal.releaseVirtualDisplay(this.mToken);
      this.mToken = null;
    }
  }
  
  public void resize(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mGlobal.resizeVirtualDisplay(this.mToken, paramInt1, paramInt2, paramInt3);
  }
  
  public void setSurface(Surface paramSurface)
  {
    if (this.mSurface != paramSurface)
    {
      this.mGlobal.setVirtualDisplaySurface(this.mToken, paramSurface);
      this.mSurface = paramSurface;
    }
  }
  
  public String toString()
  {
    return "VirtualDisplay{display=" + this.mDisplay + ", token=" + this.mToken + ", surface=" + this.mSurface + "}";
  }
  
  public static abstract class Callback
  {
    public void onPaused() {}
    
    public void onResumed() {}
    
    public void onStopped() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/VirtualDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */