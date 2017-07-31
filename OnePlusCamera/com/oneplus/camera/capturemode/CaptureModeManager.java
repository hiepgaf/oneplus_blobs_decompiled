package com.oneplus.camera.capturemode;

import com.oneplus.base.EventKey;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface CaptureModeManager
  extends Component
{
  public static final EventKey<CaptureModeEventArgs> EVENT_CAPTURE_MODE_ADDED = new EventKey("CaptureModeAdded", CaptureModeEventArgs.class, CaptureModeManager.class);
  public static final EventKey<CaptureModeEventArgs> EVENT_CAPTURE_MODE_REMOVED = new EventKey("CaptureModeRemoved", CaptureModeEventArgs.class, CaptureModeManager.class);
  public static final PropertyKey<CaptureMode> PROP_CAPTURE_MODE = new PropertyKey("CaptureMode", CaptureMode.class, CaptureModeManager.class, CaptureMode.INVALID);
  public static final PropertyKey<List<CaptureMode>> PROP_CAPTURE_MODES = new PropertyKey("CaptureModes", List.class, CaptureModeManager.class, Collections.EMPTY_LIST);
  public static final PropertyKey<Boolean> PROP_IS_CAPTURE_MODE_SWITCHING = new PropertyKey("IsCaptureModeSwitching", Boolean.class, CaptureModeManager.class, Boolean.valueOf(false));
  
  public abstract boolean addBuilder(CaptureModeBuilder paramCaptureModeBuilder, int paramInt);
  
  public abstract boolean changeToInitialCaptureMode(int paramInt);
  
  public abstract CaptureMode findCaptureMode(Class<?> paramClass);
  
  public abstract boolean setCaptureMode(CaptureMode paramCaptureMode, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/CaptureModeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */