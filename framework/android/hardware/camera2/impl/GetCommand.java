package android.hardware.camera2.impl;

public abstract interface GetCommand
{
  public abstract <T> T getValue(CameraMetadataNative paramCameraMetadataNative, CameraMetadataNative.Key<T> paramKey);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/GetCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */