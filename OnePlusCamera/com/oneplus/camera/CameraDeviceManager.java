package com.oneplus.camera;

import com.oneplus.base.EventKey;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface CameraDeviceManager
  extends Component
{
  public static final EventKey<CameraIdEventArgs> EVENT_CAMERA_CLOSED;
  public static final EventKey<CameraDeviceEventArgs> EVENT_CAMERA_OPENED = new EventKey("CameraOpened", CameraDeviceEventArgs.class, CameraDeviceManager.class);
  public static final EventKey<CameraIdEventArgs> EVENT_CAMERA_OPENING = new EventKey("CameraOpening", CameraIdEventArgs.class, CameraDeviceManager.class);
  public static final EventKey<CameraIdEventArgs> EVENT_CAMERA_OPEN_CANCELLED;
  public static final EventKey<CameraIdEventArgs> EVENT_CAMERA_OPEN_FAILED;
  public static final PropertyKey<List<Camera>> PROP_AVAILABLE_CAMERAS = new PropertyKey("AvailableCameras", List.class, CameraDeviceManager.class, Collections.EMPTY_LIST);
  
  static
  {
    EVENT_CAMERA_CLOSED = new EventKey("CameraClosed", CameraIdEventArgs.class, CameraDeviceManager.class);
    EVENT_CAMERA_OPEN_CANCELLED = new EventKey("CameraOpenCancelled", CameraIdEventArgs.class, CameraDeviceManager.class);
    EVENT_CAMERA_OPEN_FAILED = new EventKey("CameraOpenFailed", CameraIdEventArgs.class, CameraDeviceManager.class);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraDeviceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */