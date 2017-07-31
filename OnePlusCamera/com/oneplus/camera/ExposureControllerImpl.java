package com.oneplus.camera;

import android.util.Range;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.LinkedList;
import java.util.List;

final class ExposureControllerImpl
  extends CameraComponent
  implements ExposureController
{
  private final LinkedList<Handle> m_AELockHandles = new LinkedList();
  private final PropertyChangedCallback m_CameraPropertyChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
    {
      ExposureControllerImpl.-wrap2(ExposureControllerImpl.this, paramAnonymousPropertyKey, paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback m_ExposureConditionChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
    {
      ExposureControllerImpl.-wrap3(ExposureControllerImpl.this);
    }
  };
  
  ExposureControllerImpl(CameraThread paramCameraThread)
  {
    super("Exposure Controller", paramCameraThread, true);
  }
  
  private void attachToCamera(Camera paramCamera)
  {
    if (paramCamera == null) {
      return;
    }
    paramCamera.set(Camera.PROP_IS_AE_LOCKED, Boolean.valueOf(false));
    paramCamera.addCallback(Camera.PROP_AE_REGIONS, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_AE_STATE, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_EXPOSURE_COMPENSATION, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_EXPOSURE_COMPENSATION_RANGE, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_EXPOSURE_COMPENSATION_STEP, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_IS_AE_LOCKED, this.m_CameraPropertyChangedCallback);
    paramCamera.addCallback(Camera.PROP_EXPOSURE_TIME_NANOS, this.m_ExposureConditionChangedCallback);
    paramCamera.addCallback(Camera.PROP_ISO, this.m_ExposureConditionChangedCallback);
    onCameraPropertyChanged(Camera.PROP_AE_REGIONS, paramCamera.get(Camera.PROP_AE_REGIONS));
    onCameraPropertyChanged(Camera.PROP_AE_STATE, paramCamera.get(Camera.PROP_AE_STATE));
    onCameraPropertyChanged(Camera.PROP_EXPOSURE_COMPENSATION, paramCamera.get(Camera.PROP_EXPOSURE_COMPENSATION));
    onCameraPropertyChanged(Camera.PROP_EXPOSURE_COMPENSATION_RANGE, paramCamera.get(Camera.PROP_EXPOSURE_COMPENSATION_RANGE));
    onCameraPropertyChanged(Camera.PROP_EXPOSURE_COMPENSATION_STEP, paramCamera.get(Camera.PROP_EXPOSURE_COMPENSATION_STEP));
    onCameraPropertyChanged(Camera.PROP_IS_AE_LOCKED, paramCamera.get(Camera.PROP_IS_AE_LOCKED));
    onExposureConditionsChanged();
  }
  
  private void detachFromCamera(Camera paramCamera)
  {
    if (paramCamera == null) {
      return;
    }
    paramCamera.removeCallback(Camera.PROP_AE_REGIONS, this.m_CameraPropertyChangedCallback);
    paramCamera.removeCallback(Camera.PROP_EXPOSURE_COMPENSATION, this.m_CameraPropertyChangedCallback);
    paramCamera.removeCallback(Camera.PROP_EXPOSURE_COMPENSATION_RANGE, this.m_CameraPropertyChangedCallback);
    paramCamera.removeCallback(Camera.PROP_EXPOSURE_COMPENSATION_STEP, this.m_CameraPropertyChangedCallback);
    paramCamera.removeCallback(Camera.PROP_IS_AE_LOCKED, this.m_CameraPropertyChangedCallback);
    paramCamera.removeCallback(Camera.PROP_EXPOSURE_TIME_NANOS, this.m_ExposureConditionChangedCallback);
    paramCamera.removeCallback(Camera.PROP_ISO, this.m_ExposureConditionChangedCallback);
    this.m_AELockHandles.clear();
    setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(false));
  }
  
  private void onAELockedChanged(boolean paramBoolean)
  {
    if ((paramBoolean) || (this.m_AELockHandles.isEmpty())) {
      return;
    }
    Log.w(this.TAG, "onAELockedChanged() - AE unlocked by camera");
    this.m_AELockHandles.clear();
    setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(false));
  }
  
  private void onCameraPropertyChanged(PropertyKey<?> paramPropertyKey, Object paramObject)
  {
    if (paramPropertyKey == Camera.PROP_AE_REGIONS) {
      super.set(PROP_AE_REGIONS, (List)paramObject);
    }
    do
    {
      return;
      if (paramPropertyKey == Camera.PROP_AE_STATE)
      {
        super.setReadOnly(PROP_AE_STATE, (AutoExposureState)paramObject);
        return;
      }
      if (paramPropertyKey == Camera.PROP_EXPOSURE_COMPENSATION)
      {
        super.set(PROP_EXPOSURE_COMPENSATION, (Float)paramObject);
        return;
      }
      if (paramPropertyKey == Camera.PROP_EXPOSURE_COMPENSATION_RANGE)
      {
        super.setReadOnly(PROP_EXPOSURE_COMPENSATION_RANGE, (Range)paramObject);
        return;
      }
      if (paramPropertyKey == Camera.PROP_EXPOSURE_COMPENSATION_STEP)
      {
        super.setReadOnly(PROP_EXPOSURE_COMPENSATION_STEP, (Float)paramObject);
        return;
      }
    } while (paramPropertyKey != Camera.PROP_IS_AE_LOCKED);
    onAELockedChanged(((Boolean)paramObject).booleanValue());
  }
  
  private void onExposureConditionsChanged()
  {
    boolean bool2 = true;
    Camera localCamera = getCamera();
    boolean bool1 = bool2;
    if (localCamera != null)
    {
      if (((Long)localCamera.get(Camera.PROP_EXPOSURE_TIME_NANOS)).longValue() == -1L) {
        break label60;
      }
      bool1 = false;
    }
    for (;;)
    {
      if ((setReadOnly(PROP_IS_AE_ON, Boolean.valueOf(bool1))) && (!bool1)) {
        unlockAutoExposure();
      }
      return;
      label60:
      bool1 = bool2;
      if (((Integer)localCamera.get(Camera.PROP_ISO)).intValue() != -1) {
        bool1 = false;
      }
    }
  }
  
  private boolean setAERegionsProp(List<Camera.MeteringRect> paramList)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setAERegionsProp() - Component is not running");
      return false;
    }
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.e(this.TAG, "setAERegionsProp() - No primary camera");
      return false;
    }
    return localCamera.set(Camera.PROP_AE_REGIONS, paramList);
  }
  
  private boolean setExposureCompensationProp(float paramFloat)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setExposureCompensationProp() - Component is not running");
      return false;
    }
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.e(this.TAG, "setExposureCompensationProp() - No primary camera");
      return false;
    }
    return localCamera.set(Camera.PROP_EXPOSURE_COMPENSATION, Float.valueOf(paramFloat));
  }
  
  private void unlockAutoExposure()
  {
    if (this.m_AELockHandles.isEmpty()) {
      return;
    }
    Log.w(this.TAG, "unlockAutoExposure()");
    Handle[] arrayOfHandle = new Handle[this.m_AELockHandles.size()];
    this.m_AELockHandles.toArray(arrayOfHandle);
    int i = arrayOfHandle.length - 1;
    while (i >= 0)
    {
      Handle.close(arrayOfHandle[i]);
      i -= 1;
    }
  }
  
  private void unlockAutoExposure(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_AELockHandles.remove(paramHandle)) {
      return;
    }
    Log.v(this.TAG, "unlockAutoExposure() - Handle : ", paramHandle, ", handle count : ", Integer.valueOf(this.m_AELockHandles.size()));
    if (!this.m_AELockHandles.isEmpty()) {
      return;
    }
    paramHandle = getCamera();
    if (paramHandle != null) {
      paramHandle.set(Camera.PROP_IS_AE_LOCKED, Boolean.valueOf(false));
    }
    setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(false));
  }
  
  public Handle lockAutoExposure(int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "lockAutoExposure() - Component is not running");
      return null;
    }
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "lockAutoExposure() - No primary camera");
      return null;
    }
    if (!((Boolean)get(PROP_IS_AE_ON)).booleanValue())
    {
      Log.w(this.TAG, "lockAutoExposure() - AE is not on, ignore lock");
      return null;
    }
    Handle local3 = new Handle("AELock")
    {
      protected void onClose(int paramAnonymousInt)
      {
        ExposureControllerImpl.-wrap4(ExposureControllerImpl.this, this);
      }
    };
    this.m_AELockHandles.add(local3);
    Log.v(this.TAG, "lockAutoExposure() - Handle : ", local3, ", handle count : ", Integer.valueOf(this.m_AELockHandles.size()));
    if (this.m_AELockHandles.size() == 1)
    {
      localCamera.set(Camera.PROP_IS_AE_LOCKED, Boolean.valueOf(true));
      setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(true));
    }
    return local3;
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        ExposureControllerImpl.-wrap1(ExposureControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue());
        ExposureControllerImpl.-wrap0(ExposureControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    attachToCamera(getCamera());
  }
  
  protected void onRelease()
  {
    detachFromCamera(getCamera());
    super.onRelease();
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_AE_REGIONS) {
      return setAERegionsProp((List)paramTValue);
    }
    if (paramPropertyKey == PROP_EXPOSURE_COMPENSATION) {
      return setExposureCompensationProp(((Float)paramTValue).floatValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ExposureControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */