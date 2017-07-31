package com.oneplus.camera;

import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
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

final class UIFocusControllerImpl
  extends ProxyComponent<FocusController>
  implements FocusController
{
  private final LinkedList<FocusLockHandle> m_FocusLockHandles = new LinkedList();
  
  UIFocusControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Focus Controller (UI)", paramCameraActivity, paramCameraActivity.getCameraThread(), FocusController.class);
  }
  
  private void unlockFocus()
  {
    if (this.m_FocusLockHandles.isEmpty()) {
      return;
    }
    Log.w(this.TAG, "unlockFocus()");
    FocusLockHandle[] arrayOfFocusLockHandle = new FocusLockHandle[this.m_FocusLockHandles.size()];
    this.m_FocusLockHandles.toArray(arrayOfFocusLockHandle);
    int i = arrayOfFocusLockHandle.length - 1;
    while (i >= 0)
    {
      Handle.close(arrayOfFocusLockHandle[i]);
      i -= 1;
    }
  }
  
  private void unlockFocus(FocusLockHandle paramFocusLockHandle)
  {
    if (!this.m_FocusLockHandles.remove(paramFocusLockHandle)) {
      return;
    }
    Handle.close(paramFocusLockHandle.internalHandle);
  }
  
  public Handle lockFocus(int paramInt)
  {
    verifyAccess();
    if (get(PROP_FOCUS_MODE) == FocusMode.MANUAL)
    {
      Log.w(this.TAG, "lockFocus() - Focus mode is manual, ignore");
      return null;
    }
    Object localObject = callTargetMethod("lockFocus", new Class[] { Integer.TYPE }, new Object[] { Integer.valueOf(paramInt) });
    if (Handle.isValid((Handle)localObject))
    {
      localObject = new FocusLockHandle((Handle)localObject);
      this.m_FocusLockHandles.add(localObject);
      if (this.m_FocusLockHandles.size() == 1) {
        setReadOnly(PROP_IS_FOCUS_LOCKED, Boolean.valueOf(true));
      }
      return (Handle)localObject;
    }
    return null;
  }
  
  protected void onBindingToTargetProperties(List<PropertyKey<?>> paramList)
  {
    super.onBindingToTargetProperties(paramList);
    paramList.add(PROP_AF_REGIONS);
    paramList.add(PROP_CAN_CHANGE_FOCUS);
    paramList.add(PROP_FOCUS_MODE);
    paramList.add(PROP_FOCUS_STATE);
    paramList.add(PROP_IS_FOCUS_LOCKED);
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
        UIFocusControllerImpl.-wrap0(UIFocusControllerImpl.this);
      }
    };
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, local1);
    localCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, local1);
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.PAUSING) {
          UIFocusControllerImpl.-wrap0(UIFocusControllerImpl.this);
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
            UIFocusControllerImpl.-wrap0(UIFocusControllerImpl.this);
          }
        }
      });
      localResolutionManager.addCallback(ResolutionManager.PROP_VIDEO_RESOLUTION, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Resolution> paramAnonymousPropertyKey, PropertyChangeEventArgs<Resolution> paramAnonymousPropertyChangeEventArgs)
        {
          if (localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.VIDEO) {
            UIFocusControllerImpl.-wrap0(UIFocusControllerImpl.this);
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
  
  protected void onTargetPropertyChanged(long paramLong, PropertyKey<?> paramPropertyKey, PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    if ((paramPropertyKey == PROP_FOCUS_MODE) && ((FocusMode)paramPropertyChangeEventArgs.getNewValue() == FocusMode.MANUAL)) {
      unlockFocus();
    }
    super.onTargetPropertyChanged(paramLong, paramPropertyKey, paramPropertyChangeEventArgs);
  }
  
  public Handle startAutoFocus(List<Camera.MeteringRect> paramList, int paramInt)
  {
    verifyAccess();
    return callTargetMethod("startAutoFocus", new Class[] { List.class, Integer.TYPE }, new Object[] { paramList, Integer.valueOf(paramInt) });
  }
  
  private final class FocusLockHandle
    extends Handle
  {
    public final Handle internalHandle;
    
    public FocusLockHandle(Handle paramHandle)
    {
      super();
      this.internalHandle = paramHandle;
    }
    
    protected void onClose(int paramInt)
    {
      UIFocusControllerImpl.-wrap1(UIFocusControllerImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UIFocusControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */