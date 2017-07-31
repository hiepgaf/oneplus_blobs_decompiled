package android.hardware.camera2;

import android.hardware.camera2.params.OutputConfiguration;
import android.os.Handler;
import android.view.Surface;
import java.util.List;

public abstract class CameraCaptureSession
  implements AutoCloseable
{
  public static final int SESSION_ID_NONE = -1;
  
  public abstract void abortCaptures()
    throws CameraAccessException;
  
  public abstract int capture(CaptureRequest paramCaptureRequest, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract int captureBurst(List<CaptureRequest> paramList, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract void close();
  
  public abstract void finishDeferredConfiguration(List<OutputConfiguration> paramList)
    throws CameraAccessException;
  
  public abstract CameraDevice getDevice();
  
  public abstract Surface getInputSurface();
  
  public abstract boolean isReprocessable();
  
  public abstract void prepare(int paramInt, Surface paramSurface)
    throws CameraAccessException;
  
  public abstract void prepare(Surface paramSurface)
    throws CameraAccessException;
  
  public abstract int setRepeatingBurst(List<CaptureRequest> paramList, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract int setRepeatingRequest(CaptureRequest paramCaptureRequest, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException;
  
  public abstract void stopRepeating()
    throws CameraAccessException;
  
  public abstract void tearDown(Surface paramSurface)
    throws CameraAccessException;
  
  public static abstract class CaptureCallback
  {
    public static final int NO_FRAMES_CAPTURED = -1;
    
    public void onCaptureBufferLost(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, Surface paramSurface, long paramLong) {}
    
    public void onCaptureCompleted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, TotalCaptureResult paramTotalCaptureResult) {}
    
    public void onCaptureFailed(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, CaptureFailure paramCaptureFailure) {}
    
    public void onCapturePartial(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult) {}
    
    public void onCaptureProgressed(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult) {}
    
    public void onCaptureSequenceAborted(CameraCaptureSession paramCameraCaptureSession, int paramInt) {}
    
    public void onCaptureSequenceCompleted(CameraCaptureSession paramCameraCaptureSession, int paramInt, long paramLong) {}
    
    public void onCaptureStarted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, long paramLong) {}
    
    public void onCaptureStarted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, long paramLong1, long paramLong2)
    {
      onCaptureStarted(paramCameraCaptureSession, paramCaptureRequest, paramLong1);
    }
  }
  
  public static abstract class CaptureListener
    extends CameraCaptureSession.CaptureCallback
  {}
  
  public static abstract class StateCallback
  {
    public void onActive(CameraCaptureSession paramCameraCaptureSession) {}
    
    public void onClosed(CameraCaptureSession paramCameraCaptureSession) {}
    
    public abstract void onConfigureFailed(CameraCaptureSession paramCameraCaptureSession);
    
    public abstract void onConfigured(CameraCaptureSession paramCameraCaptureSession);
    
    public void onReady(CameraCaptureSession paramCameraCaptureSession) {}
    
    public void onSurfacePrepared(CameraCaptureSession paramCameraCaptureSession, Surface paramSurface) {}
  }
  
  public static abstract class StateListener
    extends CameraCaptureSession.StateCallback
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CameraCaptureSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */