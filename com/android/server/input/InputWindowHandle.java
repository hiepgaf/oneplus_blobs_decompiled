package com.android.server.input;

import android.graphics.Region;
import android.view.InputChannel;

public final class InputWindowHandle
{
  public boolean canReceiveKeys;
  public long dispatchingTimeoutNanos;
  public final int displayId;
  public int frameBottom;
  public int frameLeft;
  public int frameRight;
  public int frameTop;
  public boolean hasFocus;
  public boolean hasWallpaper;
  public final InputApplicationHandle inputApplicationHandle;
  public InputChannel inputChannel;
  public int inputFeatures;
  public int layer;
  public int layoutParamsFlags;
  public int layoutParamsType;
  public String name;
  public int ownerPid;
  public int ownerUid;
  public boolean paused;
  private long ptr;
  public float scaleFactor;
  public final Region touchableRegion = new Region();
  public boolean visible;
  public final Object windowState;
  
  public InputWindowHandle(InputApplicationHandle paramInputApplicationHandle, Object paramObject, int paramInt)
  {
    this.inputApplicationHandle = paramInputApplicationHandle;
    this.windowState = paramObject;
    this.displayId = paramInt;
  }
  
  private native void nativeDispose();
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public String toString()
  {
    return this.name + ", layer=" + this.layer + ", frame=[" + this.frameLeft + "," + this.frameTop + "," + this.frameRight + "," + this.frameBottom + "]" + ", touchableRegion=" + this.touchableRegion + ", visible=" + this.visible;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/input/InputWindowHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */