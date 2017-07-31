package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class FocusControllerImpl
  extends CameraComponent
  implements FocusController
{
  private final PropertyChangedCallback<List<Camera.MeteringRect>> m_AfRegionsChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.MeteringRect>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.MeteringRect>> paramAnonymousPropertyChangeEventArgs)
    {
      FocusControllerImpl.-wrap0(FocusControllerImpl.this, (Camera)paramAnonymousPropertySource, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private AfHandle m_CurrentAfHandle;
  private final List<Handle> m_FocusLockHandles = new ArrayList();
  private final PropertyChangedCallback<FocusMode> m_FocusModeChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusMode> paramAnonymousPropertyChangeEventArgs)
    {
      FocusControllerImpl.-wrap2(FocusControllerImpl.this, (Camera)paramAnonymousPropertySource, (FocusMode)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback<FocusState> m_FocusStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusState> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusState> paramAnonymousPropertyChangeEventArgs)
    {
      FocusControllerImpl.-wrap3(FocusControllerImpl.this, (Camera)paramAnonymousPropertySource, (FocusState)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private AfHandle m_PendingAfHandle;
  
  FocusControllerImpl(CameraThread paramCameraThread)
  {
    super("Focus Controller", paramCameraThread, true);
  }
  
  private void cancelAutoFocus() {}
  
  private void onAfRegionsChanged(Camera paramCamera, List<Camera.MeteringRect> paramList)
  {
    setReadOnly(PROP_AF_REGIONS, paramList);
  }
  
  private void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    cancelAutoFocus();
    if (paramCamera1 != null)
    {
      paramCamera1.removeCallback(Camera.PROP_AF_REGIONS, this.m_AfRegionsChangedCallback);
      paramCamera1.removeCallback(Camera.PROP_FOCUS_MODE, this.m_FocusModeChangedCallback);
      paramCamera1.removeCallback(Camera.PROP_FOCUS_STATE, this.m_FocusStateChangedCallback);
    }
    if (paramCamera2 != null)
    {
      paramCamera2.addCallback(Camera.PROP_AF_REGIONS, this.m_AfRegionsChangedCallback);
      paramCamera2.addCallback(Camera.PROP_FOCUS_MODE, this.m_FocusModeChangedCallback);
      paramCamera2.addCallback(Camera.PROP_FOCUS_STATE, this.m_FocusStateChangedCallback);
      onFocusStateChanged(paramCamera2, (FocusState)paramCamera2.get(Camera.PROP_FOCUS_STATE));
      onFocusModeChanged(paramCamera2, (FocusMode)paramCamera2.get(Camera.PROP_FOCUS_MODE));
      return;
    }
    onFocusStateChanged(paramCamera2, FocusState.INACTIVE);
    onFocusModeChanged(paramCamera2, FocusMode.DISABLED);
  }
  
  private void onFocusModeChanged(Camera paramCamera, FocusMode paramFocusMode)
  {
    if ((setReadOnly(PROP_FOCUS_MODE, paramFocusMode)) && (paramFocusMode == FocusMode.MANUAL)) {
      unlockFocus();
    }
  }
  
  private void onFocusStateChanged(Camera paramCamera, FocusState paramFocusState)
  {
    setReadOnly(PROP_FOCUS_STATE, paramFocusState);
    if (paramFocusState != FocusState.SCANNING)
    {
      if (Handle.isValid(this.m_CurrentAfHandle))
      {
        this.m_CurrentAfHandle.complete();
        this.m_CurrentAfHandle = null;
      }
      if (this.m_PendingAfHandle != null)
      {
        paramFocusState = this.m_PendingAfHandle;
        this.m_PendingAfHandle = null;
        Log.v(this.TAG, "onFocusStateChanged() - Start pending AF, handle : ", paramFocusState);
        startAutoFocus(paramCamera, paramFocusState);
      }
    }
  }
  
  private boolean startAutoFocus(Camera paramCamera, AfHandle paramAfHandle)
  {
    List localList = (List)paramCamera.get(Camera.PROP_FOCUS_MODES);
    int i;
    if ((paramAfHandle.flags & 0x2) != 0)
    {
      i = 1;
      if (!localList.contains(FocusMode.CONTINUOUS_AF))
      {
        Log.e(this.TAG, "startAutoFocus() - Continuous AF is unsupported");
        return false;
      }
    }
    else if ((paramAfHandle.flags & 0x1) != 0)
    {
      i = 0;
      if (!localList.contains(FocusMode.NORMAL_AF))
      {
        Log.e(this.TAG, "startAutoFocus() - Single AF is unsupported");
        return false;
      }
    }
    else
    {
      boolean bool = localList.contains(FocusMode.CONTINUOUS_AF);
      i = bool;
      if (!bool)
      {
        if (!localList.contains(FocusMode.NORMAL_AF)) {
          break label168;
        }
        i = bool;
      }
    }
    Log.v(this.TAG, "startAutoFocus() - Handle : ", paramAfHandle);
    label168:
    do
    {
      try
      {
        paramCamera.set(Camera.PROP_AF_REGIONS, paramAfHandle.regions);
        if (i == 0) {}
      }
      catch (Throwable paramCamera)
      {
        Log.e(this.TAG, "startAutoFocus() - Fail to set AF regions", paramCamera);
        return false;
      }
      try
      {
        paramCamera.set(Camera.PROP_FOCUS_MODE, FocusMode.CONTINUOUS_AF);
        this.m_CurrentAfHandle = paramAfHandle;
        return true;
      }
      catch (Throwable paramCamera)
      {
        Log.e(this.TAG, "startAutoFocus() - Fail to start AF", paramCamera);
      }
      Log.e(this.TAG, "startAutoFocus() - Both single and continuous AF is unsupported");
      return false;
      paramCamera.set(Camera.PROP_FOCUS_MODE, FocusMode.NORMAL_AF);
    } while (paramCamera.startAutoFocus(0));
    Log.e(this.TAG, "startAutoFocus() - Fail to start single AF");
    return false;
    return false;
  }
  
  private void unlockFocus()
  {
    if (this.m_FocusLockHandles.isEmpty()) {
      return;
    }
    Log.w(this.TAG, "unlockFocus()");
    Handle[] arrayOfHandle = new Handle[this.m_FocusLockHandles.size()];
    this.m_FocusLockHandles.toArray(arrayOfHandle);
    int i = arrayOfHandle.length - 1;
    while (i >= 0)
    {
      Handle.close(arrayOfHandle[i]);
      i -= 1;
    }
  }
  
  private void unlockFocus(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_FocusLockHandles.remove(paramHandle)) {
      return;
    }
    Log.v(this.TAG, "unlockFocus() - Handle : ", paramHandle, ", handle count : ", Integer.valueOf(this.m_FocusLockHandles.size()));
    if (!this.m_FocusLockHandles.isEmpty()) {
      return;
    }
    setReadOnly(PROP_IS_FOCUS_LOCKED, Boolean.valueOf(false));
    paramHandle = getCamera();
    if (paramHandle == null) {
      return;
    }
    if (paramHandle.get(Camera.PROP_FOCUS_MODE) == FocusMode.CONTINUOUS_AF)
    {
      Log.v(this.TAG, "unlockFocus() - Cancel AF");
      paramHandle.cancelAutoFocus(0);
    }
  }
  
  public Handle lockFocus(int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "lockFocus() - Component is not running");
      return null;
    }
    if (get(PROP_FOCUS_MODE) == FocusMode.MANUAL)
    {
      Log.w(this.TAG, "lockFocus() - Focus mode is manual, ignore");
      return null;
    }
    Handle local4 = new Handle("FocusLock")
    {
      protected void onClose(int paramAnonymousInt)
      {
        FocusControllerImpl.-wrap4(FocusControllerImpl.this, this);
      }
    };
    this.m_FocusLockHandles.add(local4);
    Log.v(this.TAG, "lockFocus() - Handle : ", local4, ", handle count : ", Integer.valueOf(this.m_FocusLockHandles.size()));
    if (this.m_PendingAfHandle != null)
    {
      this.m_PendingAfHandle.complete();
      this.m_PendingAfHandle = null;
    }
    if (this.m_FocusLockHandles.size() == 1)
    {
      setReadOnly(PROP_IS_FOCUS_LOCKED, Boolean.valueOf(true));
      if (get(PROP_FOCUS_MODE) == FocusMode.CONTINUOUS_AF)
      {
        Camera localCamera = getCamera();
        if (localCamera != null)
        {
          Log.w(this.TAG, "lockFocus() - Trigger AF to lock focus");
          localCamera.startAutoFocus(0);
        }
      }
    }
    return local4;
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        FocusControllerImpl.-wrap1(FocusControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    onCameraChanged(null, getCamera());
  }
  
  public Handle startAutoFocus(List<Camera.MeteringRect> paramList, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "startAutoFocus() - Component is not running");
      return null;
    }
    if (((Boolean)get(PROP_IS_FOCUS_LOCKED)).booleanValue())
    {
      Log.w(this.TAG, "startAutoFocus() - Focus is locked");
      return null;
    }
    if (get(PROP_FOCUS_MODE) == FocusMode.MANUAL)
    {
      Log.v(this.TAG, "startAutoFocus() - Focus is manual");
      return null;
    }
    if ((paramInt & 0x3) == 3)
    {
      Log.e(this.TAG, "startAutoFocus() - Invalid flags : " + paramInt);
      return null;
    }
    Object localObject = paramList;
    if (paramList == null) {
      localObject = Collections.EMPTY_LIST;
    }
    paramList = getCamera();
    if (paramList == null)
    {
      Log.e(this.TAG, "startAutoFocus() - No primary camera");
      return null;
    }
    localObject = new AfHandle((List)localObject, paramInt);
    Log.v(this.TAG, "startAutoFocus() - Create handle : ", localObject);
    if (!startAutoFocus(paramList, (AfHandle)localObject)) {
      return null;
    }
    return (Handle)localObject;
  }
  
  private final class AfHandle
    extends Handle
  {
    public final int flags;
    public final List<Camera.MeteringRect> regions;
    
    public AfHandle(int paramInt)
    {
      super();
      this.regions = paramInt;
      int i;
      this.flags = i;
    }
    
    public void complete()
    {
      closeDirectly();
    }
    
    protected void onClose(int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FocusControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */