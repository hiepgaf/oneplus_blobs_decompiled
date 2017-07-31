package android.hardware.camera2.impl;

public abstract interface CameraCaptureSessionCore
{
  public abstract CameraDeviceImpl.StateCallbackKK getDeviceStateCallback();
  
  public abstract boolean isAborting();
  
  public abstract void replaceSessionClose();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CameraCaptureSessionCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */