package com.oneplus.camera;

import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import java.util.LinkedList;
import java.util.List;

final class UIExposureControllerImpl
  extends ProxyComponent<ExposureController>
  implements ExposureController
{
  private final LinkedList<AELockHandle> m_AELockHandles = new LinkedList();
  
  UIExposureControllerImpl(CameraActivity paramCameraActivity)
  {
    super("UI Exposure Controller", paramCameraActivity, paramCameraActivity.getCameraThread(), ExposureController.class);
  }
  
  private boolean setAERegionsProp(final List<Camera.MeteringRect> paramList)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setAERegionsProp() - Component is not running");
      return false;
    }
    if (!isTargetBound()) {
      return super.set(PROP_AE_REGIONS, paramList);
    }
    if (!HandlerUtils.post(getTargetOwner(), new Runnable()
    {
      public void run()
      {
        ((ExposureController)UIExposureControllerImpl.this.getTarget()).set(UIExposureControllerImpl.PROP_AE_REGIONS, paramList);
      }
    }))
    {
      Log.e(this.TAG, "setAERegionsProp() - Fail to perform cross-thread operation");
      return false;
    }
    return true;
  }
  
  private boolean setExposureCompensationProp(final float paramFloat)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setExposureCompensationProp() - Component is not running");
      return false;
    }
    if (!isTargetBound()) {
      return super.set(PROP_EXPOSURE_COMPENSATION, Float.valueOf(paramFloat));
    }
    if (!HandlerUtils.post(getTargetOwner(), new Runnable()
    {
      public void run()
      {
        ((ExposureController)UIExposureControllerImpl.this.getTarget()).set(UIExposureControllerImpl.PROP_EXPOSURE_COMPENSATION, Float.valueOf(paramFloat));
      }
    }))
    {
      Log.e(this.TAG, "setExposureCompensationProp() - Fail to perform cross-thread operation");
      return false;
    }
    return true;
  }
  
  private void unlockAutoExposure()
  {
    if (this.m_AELockHandles.isEmpty()) {
      return;
    }
    Log.w(this.TAG, "unlockAutoExposure()");
    AELockHandle[] arrayOfAELockHandle = new AELockHandle[this.m_AELockHandles.size()];
    this.m_AELockHandles.toArray(arrayOfAELockHandle);
    int i = arrayOfAELockHandle.length - 1;
    while (i >= 0)
    {
      Handle.close(arrayOfAELockHandle[i]);
      i -= 1;
    }
  }
  
  private void unlockAutoExposure(AELockHandle paramAELockHandle)
  {
    verifyAccess();
    if (!this.m_AELockHandles.remove(paramAELockHandle)) {
      return;
    }
    if (this.m_AELockHandles.isEmpty()) {
      setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(false));
    }
    Handle.close(paramAELockHandle.internalHandle);
    HandlerUtils.post(getTarget(), new Runnable()
    {
      public void run()
      {
        final boolean bool = ((Boolean)((ExposureController)UIExposureControllerImpl.this.getTarget()).get(UIExposureControllerImpl.PROP_IS_AE_LOCKED)).booleanValue();
        HandlerUtils.post(UIExposureControllerImpl.this, new Runnable()
        {
          public void run()
          {
            UIExposureControllerImpl.-wrap0(UIExposureControllerImpl.this, UIExposureControllerImpl.PROP_IS_AE_LOCKED, Boolean.valueOf(bool));
          }
        });
      }
    });
  }
  
  public Handle lockAutoExposure(int paramInt)
  {
    verifyAccess();
    if (!((Boolean)get(PROP_IS_AE_ON)).booleanValue())
    {
      Log.w(this.TAG, "lockAutoExposure() - Exposure is not auto, ignore");
      return null;
    }
    Object localObject = callTargetMethod("lockAutoExposure", new Class[] { Integer.TYPE }, new Object[] { Integer.valueOf(paramInt) });
    if (Handle.isValid((Handle)localObject))
    {
      localObject = new AELockHandle((Handle)localObject);
      this.m_AELockHandles.add(localObject);
      if (this.m_AELockHandles.size() == 1) {
        setReadOnly(PROP_IS_AE_LOCKED, Boolean.valueOf(true));
      }
      return (Handle)localObject;
    }
    return null;
  }
  
  protected void onBindingToTargetProperties(List<PropertyKey<?>> paramList)
  {
    super.onBindingToTargetProperties(paramList);
    paramList.add(PROP_AE_REGIONS);
    paramList.add(PROP_AE_STATE);
    paramList.add(PROP_EXPOSURE_COMPENSATION);
    paramList.add(PROP_EXPOSURE_COMPENSATION_RANGE);
    paramList.add(PROP_EXPOSURE_COMPENSATION_STEP);
    paramList.add(PROP_IS_AE_LOCKED);
    paramList.add(PROP_IS_AE_ON);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CaptureModeManager localCaptureModeManager = (CaptureModeManager)findComponent(CaptureModeManager.class);
    final CameraActivity localCameraActivity = getCameraActivity();
    PropertyChangedCallback local1 = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        UIExposureControllerImpl.-wrap1(UIExposureControllerImpl.this);
      }
    };
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, local1);
    localCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, local1);
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.PAUSING) {
          UIExposureControllerImpl.-wrap1(UIExposureControllerImpl.this);
        }
      }
    });
    ResolutionManager localResolutionManager = (ResolutionManager)localCameraActivity.findComponent(ResolutionManager.class);
    if (localResolutionManager != null)
    {
      localResolutionManager.addCallback(ResolutionManager.PROP_PHOTO_RESOLUTION, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Resolution> paramAnonymousPropertyKey, PropertyChangeEventArgs<Resolution> paramAnonymousPropertyChangeEventArgs)
        {
          if (localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.PHOTO) {
            UIExposureControllerImpl.-wrap1(UIExposureControllerImpl.this);
          }
        }
      });
      localResolutionManager.addCallback(ResolutionManager.PROP_VIDEO_RESOLUTION, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Resolution> paramAnonymousPropertyKey, PropertyChangeEventArgs<Resolution> paramAnonymousPropertyChangeEventArgs)
        {
          if (localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.VIDEO) {
            UIExposureControllerImpl.-wrap1(UIExposureControllerImpl.this);
          }
        }
      });
    }
    for (;;)
    {
      if (localCaptureModeManager != null) {
        localCaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, local1);
      }
      return;
      Log.e(this.TAG, "getResolutionManager() - No ResolutionManager");
    }
  }
  
  protected void onTargetBound(ExposureController paramExposureController)
  {
    super.onTargetBound(paramExposureController);
  }
  
  protected void onTargetPropertyChanged(long paramLong, PropertyKey<?> paramPropertyKey, PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    if ((paramPropertyKey == PROP_IS_AE_ON) && (!((Boolean)paramPropertyChangeEventArgs.getNewValue()).booleanValue())) {
      unlockAutoExposure();
    }
    super.onTargetPropertyChanged(paramLong, paramPropertyKey, paramPropertyChangeEventArgs);
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
  
  private final class AELockHandle
    extends Handle
  {
    public final Handle internalHandle;
    
    public AELockHandle(Handle paramHandle)
    {
      super();
      this.internalHandle = paramHandle;
    }
    
    protected void onClose(int paramInt)
    {
      UIExposureControllerImpl.-wrap2(UIExposureControllerImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UIExposureControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */