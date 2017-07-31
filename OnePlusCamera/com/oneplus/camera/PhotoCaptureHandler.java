package com.oneplus.camera;

import com.oneplus.base.EventKey;
import com.oneplus.base.component.Component;

public abstract interface PhotoCaptureHandler
  extends Component
{
  public static final EventKey<CaptureEventArgs> EVENT_SHUTTER = new EventKey("Shutter", CaptureEventArgs.class, PhotoCaptureHandler.class);
  
  public abstract boolean capture(Camera paramCamera, CaptureHandle paramCaptureHandle, int paramInt);
  
  public abstract boolean stopCapture(Camera paramCamera, CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/PhotoCaptureHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */