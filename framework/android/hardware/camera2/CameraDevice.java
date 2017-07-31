package android.hardware.camera2;

import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Handler;
import android.view.Surface;
import java.util.List;

public abstract class CameraDevice
  implements AutoCloseable
{
  public static final int TEMPLATE_MANUAL = 6;
  public static final int TEMPLATE_PREVIEW = 1;
  public static final int TEMPLATE_RECORD = 3;
  public static final int TEMPLATE_STILL_CAPTURE = 2;
  public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
  public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;
  
  public abstract void close();
  
  public abstract CaptureRequest.Builder createCaptureRequest(int paramInt)
    throws CameraAccessException;
  
  public abstract void createCaptureSession(List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract void createCaptureSessionByOutputConfigurations(List<OutputConfiguration> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract void createConstrainedHighSpeedCaptureSession(List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract CaptureRequest.Builder createReprocessCaptureRequest(TotalCaptureResult paramTotalCaptureResult)
    throws CameraAccessException;
  
  public abstract void createReprocessableCaptureSession(InputConfiguration paramInputConfiguration, List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract void createReprocessableCaptureSessionByConfigurations(InputConfiguration paramInputConfiguration, List<OutputConfiguration> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract String getId();
  
  public static abstract class StateCallback
  {
    public static final int ERROR_CAMERA_DEVICE = 4;
    public static final int ERROR_CAMERA_DISABLED = 3;
    public static final int ERROR_CAMERA_IN_USE = 1;
    public static final int ERROR_CAMERA_SERVICE = 5;
    public static final int ERROR_MAX_CAMERAS_IN_USE = 2;
    
    public void onClosed(CameraDevice paramCameraDevice) {}
    
    public abstract void onDisconnected(CameraDevice paramCameraDevice);
    
    public abstract void onError(CameraDevice paramCameraDevice, int paramInt);
    
    public abstract void onOpened(CameraDevice paramCameraDevice);
  }
  
  public static abstract class StateListener
    extends CameraDevice.StateCallback
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CameraDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */