package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.view.Surface;

public class ICameraDeviceUserWrapper
{
  private final ICameraDeviceUser mRemoteDevice;
  
  public ICameraDeviceUserWrapper(ICameraDeviceUser paramICameraDeviceUser)
  {
    if (paramICameraDeviceUser == null) {
      throw new NullPointerException("Remote device may not be null");
    }
    this.mRemoteDevice = paramICameraDeviceUser;
  }
  
  public void beginConfigure()
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.beginConfigure();
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public long cancelRequest(int paramInt)
    throws CameraAccessException
  {
    try
    {
      long l = this.mRemoteDevice.cancelRequest(paramInt);
      return l;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public CameraMetadataNative createDefaultRequest(int paramInt)
    throws CameraAccessException
  {
    try
    {
      CameraMetadataNative localCameraMetadataNative = this.mRemoteDevice.createDefaultRequest(paramInt);
      return localCameraMetadataNative;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public int createInputStream(int paramInt1, int paramInt2, int paramInt3)
    throws CameraAccessException
  {
    try
    {
      paramInt1 = this.mRemoteDevice.createInputStream(paramInt1, paramInt2, paramInt3);
      return paramInt1;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public int createStream(OutputConfiguration paramOutputConfiguration)
    throws CameraAccessException
  {
    try
    {
      int i = this.mRemoteDevice.createStream(paramOutputConfiguration);
      return i;
    }
    catch (Throwable paramOutputConfiguration)
    {
      CameraManager.throwAsPublicException(paramOutputConfiguration);
      throw new UnsupportedOperationException("Unexpected exception", paramOutputConfiguration);
    }
  }
  
  public void deleteStream(int paramInt)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.deleteStream(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public void disconnect()
  {
    try
    {
      this.mRemoteDevice.disconnect();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void endConfigure(boolean paramBoolean)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.endConfigure(paramBoolean);
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public long flush()
    throws CameraAccessException
  {
    try
    {
      long l = this.mRemoteDevice.flush();
      return l;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public CameraMetadataNative getCameraInfo()
    throws CameraAccessException
  {
    try
    {
      CameraMetadataNative localCameraMetadataNative = this.mRemoteDevice.getCameraInfo();
      return localCameraMetadataNative;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public Surface getInputSurface()
    throws CameraAccessException
  {
    try
    {
      Surface localSurface = this.mRemoteDevice.getInputSurface();
      return localSurface;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public void prepare(int paramInt)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.prepare(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public void prepare2(int paramInt1, int paramInt2)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.prepare2(paramInt1, paramInt2);
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public void setDeferredConfiguration(int paramInt, OutputConfiguration paramOutputConfiguration)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.setDeferredConfiguration(paramInt, paramOutputConfiguration);
      return;
    }
    catch (Throwable paramOutputConfiguration)
    {
      CameraManager.throwAsPublicException(paramOutputConfiguration);
      throw new UnsupportedOperationException("Unexpected exception", paramOutputConfiguration);
    }
  }
  
  public SubmitInfo submitRequest(CaptureRequest paramCaptureRequest, boolean paramBoolean)
    throws CameraAccessException
  {
    try
    {
      paramCaptureRequest = this.mRemoteDevice.submitRequest(paramCaptureRequest, paramBoolean);
      return paramCaptureRequest;
    }
    catch (Throwable paramCaptureRequest)
    {
      CameraManager.throwAsPublicException(paramCaptureRequest);
      throw new UnsupportedOperationException("Unexpected exception", paramCaptureRequest);
    }
  }
  
  public SubmitInfo submitRequestList(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
    throws CameraAccessException
  {
    try
    {
      paramArrayOfCaptureRequest = this.mRemoteDevice.submitRequestList(paramArrayOfCaptureRequest, paramBoolean);
      return paramArrayOfCaptureRequest;
    }
    catch (Throwable paramArrayOfCaptureRequest)
    {
      CameraManager.throwAsPublicException(paramArrayOfCaptureRequest);
      throw new UnsupportedOperationException("Unexpected exception", paramArrayOfCaptureRequest);
    }
  }
  
  public void tearDown(int paramInt)
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.tearDown(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
  
  public void unlinkToDeath(IBinder.DeathRecipient paramDeathRecipient, int paramInt)
  {
    if (this.mRemoteDevice.asBinder() != null) {
      this.mRemoteDevice.asBinder().unlinkToDeath(paramDeathRecipient, paramInt);
    }
  }
  
  public void waitUntilIdle()
    throws CameraAccessException
  {
    try
    {
      this.mRemoteDevice.waitUntilIdle();
      return;
    }
    catch (Throwable localThrowable)
    {
      CameraManager.throwAsPublicException(localThrowable);
      throw new UnsupportedOperationException("Unexpected exception", localThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/ICameraDeviceUserWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */