package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.LinkedList;

public class WhiteBalanceControllerImpl
  extends CameraComponent
  implements WhiteBalanceController
{
  private LinkedList<Handle> m_AwbLockHandles = new LinkedList();
  private final PropertyChangedCallback m_CameraPropertyChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
    {
      WhiteBalanceControllerImpl.this.onCameraPropertyChanged(paramAnonymousPropertyKey, paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  
  WhiteBalanceControllerImpl(CameraThread paramCameraThread)
  {
    super("WhiteBalance Controller", paramCameraThread, true);
  }
  
  private void onAwbLockedChanged(boolean paramBoolean)
  {
    if ((paramBoolean) || (this.m_AwbLockHandles.isEmpty())) {
      return;
    }
    this.m_AwbLockHandles.clear();
    setReadOnly(PROP_IS_AWB_LOCKED, Boolean.valueOf(false));
  }
  
  private void unlockAutoWhiteBalance(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_AwbLockHandles.remove(paramHandle)) {
      return;
    }
    Log.v(this.TAG, "unlockAutoWhiteBalance() - Handle : ", paramHandle, ", handle count : ", Integer.valueOf(this.m_AwbLockHandles.size()));
    if (!this.m_AwbLockHandles.isEmpty()) {
      return;
    }
    paramHandle = getCamera();
    if (paramHandle != null) {
      paramHandle.set(Camera.PROP_IS_AWB_LOCKED, Boolean.valueOf(false));
    }
    setReadOnly(PROP_IS_AWB_LOCKED, Boolean.valueOf(false));
  }
  
  protected void attachToCamera(Camera paramCamera)
  {
    if (paramCamera == null) {
      return;
    }
    paramCamera.set(Camera.PROP_IS_AWB_LOCKED, Boolean.valueOf(false));
    paramCamera.addCallback(Camera.PROP_IS_AWB_LOCKED, this.m_CameraPropertyChangedCallback);
    onCameraPropertyChanged(Camera.PROP_IS_AWB_LOCKED, paramCamera.get(Camera.PROP_IS_AE_LOCKED));
  }
  
  protected void detachFromCamera(Camera paramCamera)
  {
    if (paramCamera == null) {
      return;
    }
    paramCamera.removeCallback(Camera.PROP_IS_AWB_LOCKED, this.m_CameraPropertyChangedCallback);
    this.m_AwbLockHandles.clear();
    setReadOnly(PROP_IS_AWB_LOCKED, Boolean.valueOf(false));
  }
  
  public Handle lockAutoWhiteBalance(int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "lockAutoWhiteBalance() - Component is not running");
      return null;
    }
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "lockAutoWhiteBalance() - No primary camera");
      return null;
    }
    Handle local2 = new Handle("AwbLock")
    {
      protected void onClose(int paramAnonymousInt)
      {
        WhiteBalanceControllerImpl.-wrap0(WhiteBalanceControllerImpl.this, this);
      }
    };
    this.m_AwbLockHandles.add(local2);
    Log.v(this.TAG, "lockAutoWhiteBalance() - Handle : ", local2, ", handle count : ", Integer.valueOf(this.m_AwbLockHandles.size()));
    if (this.m_AwbLockHandles.size() == 1)
    {
      localCamera.set(Camera.PROP_IS_AWB_LOCKED, Boolean.valueOf(true));
      setReadOnly(PROP_IS_AWB_LOCKED, Boolean.valueOf(true));
    }
    return local2;
  }
  
  protected void onCameraPropertyChanged(PropertyKey paramPropertyKey, Object paramObject)
  {
    if (paramPropertyKey == Camera.PROP_IS_AWB_LOCKED) {
      onAwbLockedChanged(((Boolean)paramObject).booleanValue());
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        WhiteBalanceControllerImpl.this.detachFromCamera((Camera)paramAnonymousPropertyChangeEventArgs.getOldValue());
        WhiteBalanceControllerImpl.this.attachToCamera((Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    attachToCamera(getCamera());
  }
  
  protected void onRelease()
  {
    detachFromCamera(getCamera());
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/WhiteBalanceControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */