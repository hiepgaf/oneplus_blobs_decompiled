package com.android.server.display;

import android.graphics.Rect;
import android.hardware.display.DisplayViewport;
import android.os.IBinder;
import android.view.Surface;
import android.view.SurfaceControl;
import java.io.PrintWriter;

abstract class DisplayDevice
{
  private Rect mCurrentDisplayRect;
  private int mCurrentLayerStack = -1;
  private Rect mCurrentLayerStackRect;
  private int mCurrentOrientation = -1;
  private Surface mCurrentSurface;
  DisplayDeviceInfo mDebugLastLoggedDeviceInfo;
  private final DisplayAdapter mDisplayAdapter;
  private final IBinder mDisplayToken;
  private final String mUniqueId;
  
  public DisplayDevice(DisplayAdapter paramDisplayAdapter, IBinder paramIBinder, String paramString)
  {
    this.mDisplayAdapter = paramDisplayAdapter;
    this.mDisplayToken = paramIBinder;
    this.mUniqueId = paramString;
  }
  
  public void applyPendingDisplayDeviceInfoChangesLocked() {}
  
  public void dumpLocked(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("mAdapter=" + this.mDisplayAdapter.getName());
    paramPrintWriter.println("mUniqueId=" + this.mUniqueId);
    paramPrintWriter.println("mDisplayToken=" + this.mDisplayToken);
    paramPrintWriter.println("mCurrentLayerStack=" + this.mCurrentLayerStack);
    paramPrintWriter.println("mCurrentOrientation=" + this.mCurrentOrientation);
    paramPrintWriter.println("mCurrentLayerStackRect=" + this.mCurrentLayerStackRect);
    paramPrintWriter.println("mCurrentDisplayRect=" + this.mCurrentDisplayRect);
    paramPrintWriter.println("mCurrentSurface=" + this.mCurrentSurface);
  }
  
  public final DisplayAdapter getAdapterLocked()
  {
    return this.mDisplayAdapter;
  }
  
  public abstract DisplayDeviceInfo getDisplayDeviceInfoLocked();
  
  public final IBinder getDisplayTokenLocked()
  {
    return this.mDisplayToken;
  }
  
  public final String getNameLocked()
  {
    return getDisplayDeviceInfoLocked().name;
  }
  
  public final String getUniqueId()
  {
    return this.mUniqueId;
  }
  
  public abstract boolean hasStableUniqueId();
  
  public void performTraversalInTransactionLocked() {}
  
  public final void populateViewportLocked(DisplayViewport paramDisplayViewport)
  {
    paramDisplayViewport.orientation = this.mCurrentOrientation;
    label44:
    label62:
    DisplayDeviceInfo localDisplayDeviceInfo;
    int j;
    if (this.mCurrentLayerStackRect != null)
    {
      paramDisplayViewport.logicalFrame.set(this.mCurrentLayerStackRect);
      if (this.mCurrentDisplayRect == null) {
        break label109;
      }
      paramDisplayViewport.physicalFrame.set(this.mCurrentDisplayRect);
      if (this.mCurrentOrientation == 1) {
        break label119;
      }
      if (this.mCurrentOrientation != 3) {
        break label124;
      }
      i = 1;
      localDisplayDeviceInfo = getDisplayDeviceInfoLocked();
      if (i == 0) {
        break label129;
      }
      j = localDisplayDeviceInfo.height;
      label78:
      paramDisplayViewport.deviceWidth = j;
      if (i == 0) {
        break label138;
      }
    }
    label109:
    label119:
    label124:
    label129:
    label138:
    for (int i = localDisplayDeviceInfo.width;; i = localDisplayDeviceInfo.height)
    {
      paramDisplayViewport.deviceHeight = i;
      return;
      paramDisplayViewport.logicalFrame.setEmpty();
      break;
      paramDisplayViewport.physicalFrame.setEmpty();
      break label44;
      i = 1;
      break label62;
      i = 0;
      break label62;
      j = localDisplayDeviceInfo.width;
      break label78;
    }
  }
  
  public void requestDisplayModesInTransactionLocked(int paramInt1, int paramInt2) {}
  
  public Runnable requestDisplayStateLocked(int paramInt1, int paramInt2)
  {
    return null;
  }
  
  public final void setLayerStackInTransactionLocked(int paramInt)
  {
    if (this.mCurrentLayerStack != paramInt)
    {
      this.mCurrentLayerStack = paramInt;
      SurfaceControl.setDisplayLayerStack(this.mDisplayToken, paramInt);
    }
  }
  
  public final void setProjectionInTransactionLocked(int paramInt, Rect paramRect1, Rect paramRect2)
  {
    if ((this.mCurrentOrientation != paramInt) || (this.mCurrentLayerStackRect == null)) {}
    while ((!this.mCurrentLayerStackRect.equals(paramRect1)) || (this.mCurrentDisplayRect == null) || (!this.mCurrentDisplayRect.equals(paramRect2)))
    {
      this.mCurrentOrientation = paramInt;
      if (this.mCurrentLayerStackRect == null) {
        this.mCurrentLayerStackRect = new Rect();
      }
      this.mCurrentLayerStackRect.set(paramRect1);
      if (this.mCurrentDisplayRect == null) {
        this.mCurrentDisplayRect = new Rect();
      }
      this.mCurrentDisplayRect.set(paramRect2);
      SurfaceControl.setDisplayProjection(this.mDisplayToken, paramInt, paramRect1, paramRect2);
      return;
    }
  }
  
  public final void setSurfaceInTransactionLocked(Surface paramSurface)
  {
    if (this.mCurrentSurface != paramSurface)
    {
      this.mCurrentSurface = paramSurface;
      SurfaceControl.setDisplaySurface(this.mDisplayToken, paramSurface);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */