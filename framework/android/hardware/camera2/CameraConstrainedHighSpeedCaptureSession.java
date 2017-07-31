package android.hardware.camera2;

import java.util.List;

public abstract class CameraConstrainedHighSpeedCaptureSession
  extends CameraCaptureSession
{
  public abstract List<CaptureRequest> createHighSpeedRequestList(CaptureRequest paramCaptureRequest)
    throws CameraAccessException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CameraConstrainedHighSpeedCaptureSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */