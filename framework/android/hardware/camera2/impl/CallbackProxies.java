package android.hardware.camera2.impl;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.dispatch.Dispatchable;
import android.hardware.camera2.dispatch.MethodNameInvoker;
import android.view.Surface;
import com.android.internal.util.Preconditions;

public class CallbackProxies
{
  private CallbackProxies()
  {
    throw new AssertionError();
  }
  
  public static class DeviceCaptureCallbackProxy
    extends CameraDeviceImpl.CaptureCallback
  {
    private final MethodNameInvoker<CameraDeviceImpl.CaptureCallback> mProxy;
    
    public DeviceCaptureCallbackProxy(Dispatchable<CameraDeviceImpl.CaptureCallback> paramDispatchable)
    {
      this.mProxy = new MethodNameInvoker((Dispatchable)Preconditions.checkNotNull(paramDispatchable, "dispatchTarget must not be null"), CameraDeviceImpl.CaptureCallback.class);
    }
    
    public void onCaptureCompleted(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, TotalCaptureResult paramTotalCaptureResult)
    {
      this.mProxy.invoke("onCaptureCompleted", new Object[] { paramCameraDevice, paramCaptureRequest, paramTotalCaptureResult });
    }
    
    public void onCaptureFailed(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureFailure paramCaptureFailure)
    {
      this.mProxy.invoke("onCaptureFailed", new Object[] { paramCameraDevice, paramCaptureRequest, paramCaptureFailure });
    }
    
    public void onCapturePartial(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult)
    {
      this.mProxy.invoke("onCapturePartial", new Object[] { paramCameraDevice, paramCaptureRequest, paramCaptureResult });
    }
    
    public void onCaptureProgressed(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult)
    {
      this.mProxy.invoke("onCaptureProgressed", new Object[] { paramCameraDevice, paramCaptureRequest, paramCaptureResult });
    }
    
    public void onCaptureSequenceAborted(CameraDevice paramCameraDevice, int paramInt)
    {
      this.mProxy.invoke("onCaptureSequenceAborted", new Object[] { paramCameraDevice, Integer.valueOf(paramInt) });
    }
    
    public void onCaptureSequenceCompleted(CameraDevice paramCameraDevice, int paramInt, long paramLong)
    {
      this.mProxy.invoke("onCaptureSequenceCompleted", new Object[] { paramCameraDevice, Integer.valueOf(paramInt), Long.valueOf(paramLong) });
    }
    
    public void onCaptureStarted(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, long paramLong1, long paramLong2)
    {
      this.mProxy.invoke("onCaptureStarted", new Object[] { paramCameraDevice, paramCaptureRequest, Long.valueOf(paramLong1), Long.valueOf(paramLong2) });
    }
  }
  
  public static class DeviceStateCallbackProxy
    extends CameraDeviceImpl.StateCallbackKK
  {
    private final MethodNameInvoker<CameraDeviceImpl.StateCallbackKK> mProxy;
    
    public DeviceStateCallbackProxy(Dispatchable<CameraDeviceImpl.StateCallbackKK> paramDispatchable)
    {
      this.mProxy = new MethodNameInvoker((Dispatchable)Preconditions.checkNotNull(paramDispatchable, "dispatchTarget must not be null"), CameraDeviceImpl.StateCallbackKK.class);
    }
    
    public void onActive(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onActive", new Object[] { paramCameraDevice });
    }
    
    public void onBusy(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onBusy", new Object[] { paramCameraDevice });
    }
    
    public void onClosed(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onClosed", new Object[] { paramCameraDevice });
    }
    
    public void onDisconnected(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onDisconnected", new Object[] { paramCameraDevice });
    }
    
    public void onError(CameraDevice paramCameraDevice, int paramInt)
    {
      this.mProxy.invoke("onError", new Object[] { paramCameraDevice, Integer.valueOf(paramInt) });
    }
    
    public void onIdle(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onIdle", new Object[] { paramCameraDevice });
    }
    
    public void onOpened(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onOpened", new Object[] { paramCameraDevice });
    }
    
    public void onUnconfigured(CameraDevice paramCameraDevice)
    {
      this.mProxy.invoke("onUnconfigured", new Object[] { paramCameraDevice });
    }
  }
  
  public static class SessionStateCallbackProxy
    extends CameraCaptureSession.StateCallback
  {
    private final MethodNameInvoker<CameraCaptureSession.StateCallback> mProxy;
    
    public SessionStateCallbackProxy(Dispatchable<CameraCaptureSession.StateCallback> paramDispatchable)
    {
      this.mProxy = new MethodNameInvoker((Dispatchable)Preconditions.checkNotNull(paramDispatchable, "dispatchTarget must not be null"), CameraCaptureSession.StateCallback.class);
    }
    
    public void onActive(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mProxy.invoke("onActive", new Object[] { paramCameraCaptureSession });
    }
    
    public void onClosed(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mProxy.invoke("onClosed", new Object[] { paramCameraCaptureSession });
    }
    
    public void onConfigureFailed(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mProxy.invoke("onConfigureFailed", new Object[] { paramCameraCaptureSession });
    }
    
    public void onConfigured(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mProxy.invoke("onConfigured", new Object[] { paramCameraCaptureSession });
    }
    
    public void onReady(CameraCaptureSession paramCameraCaptureSession)
    {
      this.mProxy.invoke("onReady", new Object[] { paramCameraCaptureSession });
    }
    
    public void onSurfacePrepared(CameraCaptureSession paramCameraCaptureSession, Surface paramSurface)
    {
      this.mProxy.invoke("onSurfacePrepared", new Object[] { paramCameraCaptureSession, paramSurface });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CallbackProxies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */