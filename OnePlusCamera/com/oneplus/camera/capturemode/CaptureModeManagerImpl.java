package com.oneplus.camera.capturemode;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.Mode.State;
import com.oneplus.camera.scene.Scene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class CaptureModeManagerImpl
  extends CameraComponent
  implements CaptureModeManager
{
  private static final String SETTINGS_KEY_CAPTURE_MODE = "CaptureMode.Current";
  private final List<CaptureMode> m_ActiveCaptureModes = new ArrayList();
  private final List<CaptureMode> m_AllCaptureModes = new ArrayList();
  private Handle m_CameraLockHandle;
  private CaptureMode m_CaptureMode = CaptureMode.INVALID;
  private final List<CaptureModeBuilder> m_CaptureModeBuilders = new ArrayList();
  private Handle m_CaptureModeChangeCUDHandle;
  private final PropertyChangedCallback<Mode.State> m_CaptureModeStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Mode.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<Mode.State> paramAnonymousPropertyChangeEventArgs)
    {
      switch (-getcom-oneplus-camera-Mode$StateSwitchesValues()[((Mode.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
      {
      default: 
        if (paramAnonymousPropertyChangeEventArgs.getOldValue() == Mode.State.DISABLED) {
          CaptureModeManagerImpl.-wrap1(CaptureModeManagerImpl.this, (CaptureMode)paramAnonymousPropertySource);
        }
        return;
      case 1: 
        CaptureModeManagerImpl.-wrap0(CaptureModeManagerImpl.this, (CaptureMode)paramAnonymousPropertySource);
        return;
      }
      CaptureModeManagerImpl.-wrap2(CaptureModeManagerImpl.this, (CaptureMode)paramAnonymousPropertySource);
    }
  };
  private boolean m_IsInitCaptureModeSet;
  private Camera.LensFacing m_LockedCameraLensFacing;
  private CaptureMode m_PreviousCaptureMode;
  private final List<CaptureMode> m_ReadOnlyActiveCaptureModes;
  private Handle m_SettingsHandle;
  
  CaptureModeManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Capture Mode Manager", paramCameraActivity, true);
    enablePropertyLogs(PROP_CAPTURE_MODE, 1);
    this.m_ReadOnlyActiveCaptureModes = Collections.unmodifiableList(this.m_ActiveCaptureModes);
    setReadOnly(PROP_CAPTURE_MODES, this.m_ReadOnlyActiveCaptureModes);
  }
  
  private boolean createCaptureMode(CaptureModeBuilder paramCaptureModeBuilder)
  {
    try
    {
      CaptureMode localCaptureMode = paramCaptureModeBuilder.createCaptureMode(getCameraActivity());
      if (localCaptureMode == null)
      {
        Log.w(this.TAG, "createCaptureMode() - No capture mode created by " + paramCaptureModeBuilder);
        return false;
      }
      Log.v(this.TAG, "createCaptureMode() - Create '", localCaptureMode, "'");
      this.m_AllCaptureModes.add(localCaptureMode);
      if (localCaptureMode.get(CaptureMode.PROP_STATE) != Mode.State.DISABLED)
      {
        localCaptureMode.addCallback(Scene.PROP_STATE, this.m_CaptureModeStateChangedCallback);
        this.m_ActiveCaptureModes.add(localCaptureMode);
        raise(EVENT_CAPTURE_MODE_ADDED, new CaptureModeEventArgs(localCaptureMode));
      }
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "createCaptureMode() - Fail to create capture mode by " + paramCaptureModeBuilder, localThrowable);
    }
    return false;
  }
  
  private int getInitialModeIndex()
  {
    int k = 0;
    int j;
    if (getCameraActivity().isVideoServiceMode())
    {
      i = this.m_ActiveCaptureModes.size() - 1;
      for (;;)
      {
        j = k;
        if (i >= 0)
        {
          if (((String)((CaptureMode)this.m_ActiveCaptureModes.get(i)).get(CaptureMode.PROP_ID)).equals("Video")) {
            j = i;
          }
        }
        else {
          return j;
        }
        i -= 1;
      }
    }
    int i = this.m_ActiveCaptureModes.size() - 1;
    for (;;)
    {
      j = k;
      if (i < 0) {
        break;
      }
      if (((String)((CaptureMode)this.m_ActiveCaptureModes.get(i)).get(CaptureMode.PROP_ID)).equals("Photo")) {
        return i;
      }
      i -= 1;
    }
  }
  
  private void onCaptureModeDisabled(CaptureMode paramCaptureMode)
  {
    if (this.m_ActiveCaptureModes.remove(paramCaptureMode))
    {
      if (this.m_CaptureMode == paramCaptureMode)
      {
        Log.w(this.TAG, "onCaptureModeDisabled() - Capture mode '" + paramCaptureMode + "' has been disabled when using, exit from this capture mode");
        switchToPreviousCaptureMode();
      }
      raise(EVENT_CAPTURE_MODE_REMOVED, new CaptureModeEventArgs(paramCaptureMode));
    }
  }
  
  private void onCaptureModeEnabled(CaptureMode paramCaptureMode)
  {
    int j = this.m_AllCaptureModes.indexOf(paramCaptureMode);
    if (j < 0) {
      return;
    }
    int i = 0;
    int k = this.m_ActiveCaptureModes.size();
    for (;;)
    {
      if (i <= k)
      {
        if (i < k)
        {
          CaptureMode localCaptureMode = (CaptureMode)this.m_ActiveCaptureModes.get(i);
          if (localCaptureMode == paramCaptureMode) {
            return;
          }
          if (this.m_AllCaptureModes.indexOf(localCaptureMode) <= j) {
            break label116;
          }
          this.m_ActiveCaptureModes.add(i, paramCaptureMode);
        }
      }
      else
      {
        raise(EVENT_CAPTURE_MODE_ADDED, new CaptureModeEventArgs(paramCaptureMode));
        return;
      }
      this.m_ActiveCaptureModes.add(paramCaptureMode);
      label116:
      i += 1;
    }
  }
  
  private void onCaptureModeReleased(CaptureMode paramCaptureMode)
  {
    if (this.m_ActiveCaptureModes.remove(paramCaptureMode))
    {
      if (this.m_CaptureMode == paramCaptureMode)
      {
        Log.w(this.TAG, "onCaptureModeReleased() - Capture mode '" + paramCaptureMode + "' has been released when using, exit from this capture mode");
        switchToPreviousCaptureMode();
      }
      raise(EVENT_CAPTURE_MODE_REMOVED, new CaptureModeEventArgs(paramCaptureMode));
    }
    if (this.m_AllCaptureModes.remove(paramCaptureMode)) {
      paramCaptureMode.removeCallback(Scene.PROP_STATE, this.m_CaptureModeStateChangedCallback);
    }
  }
  
  private boolean switchToPreviousCaptureMode()
  {
    if ((this.m_PreviousCaptureMode != null) && (setCaptureMode(this.m_PreviousCaptureMode, 0))) {
      return true;
    }
    if ((!this.m_ActiveCaptureModes.isEmpty()) && (setCaptureMode((CaptureMode)this.m_ActiveCaptureModes.get(0), 0))) {
      return true;
    }
    Log.e(this.TAG, "switchToPreviousCaptureMode() - No capture mode to switch");
    return false;
  }
  
  public boolean addBuilder(CaptureModeBuilder paramCaptureModeBuilder, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "addBuilder() - Component is not running");
      return false;
    }
    if (paramCaptureModeBuilder == null)
    {
      Log.e(this.TAG, "addBuilder() - No builder to add");
      return false;
    }
    this.m_CaptureModeBuilders.add(paramCaptureModeBuilder);
    createCaptureMode(paramCaptureModeBuilder);
    return true;
  }
  
  public boolean changeToInitialCaptureMode(int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "changeToInitialCaptureMode() - Component is not running");
      return false;
    }
    if (this.m_IsInitCaptureModeSet) {
      return true;
    }
    int i = getInitialModeIndex();
    String str = getSettings().getString("CaptureMode.Current");
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (str != null) {
      paramInt = this.m_ActiveCaptureModes.size() - 1;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (paramInt >= 0)
      {
        localObject1 = (CaptureMode)this.m_ActiveCaptureModes.get(paramInt);
        if (!str.equals(((CaptureMode)localObject1).get(CaptureMode.PROP_ID))) {}
      }
      else
      {
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = localObject1;
          if (!this.m_ActiveCaptureModes.isEmpty()) {
            localObject2 = (CaptureMode)this.m_ActiveCaptureModes.get(i);
          }
        }
        if (localObject2 == null) {
          break;
        }
        Log.v(this.TAG, "changeToInitialCaptureMode() - Initial capture mode : ", localObject2);
        setCaptureMode((CaptureMode)localObject2, 0);
        this.m_IsInitCaptureModeSet = true;
        return true;
      }
      paramInt -= 1;
    }
    Log.e(this.TAG, "changeToInitialCaptureMode() - No initial capture mode");
    return false;
  }
  
  public CaptureMode findCaptureMode(Class<?> paramClass)
  {
    verifyAccess();
    Iterator localIterator = ((List)get(PROP_CAPTURE_MODES)).iterator();
    while (localIterator.hasNext())
    {
      CaptureMode localCaptureMode = (CaptureMode)localIterator.next();
      if (localCaptureMode.getClass().isAssignableFrom(paramClass)) {
        return localCaptureMode;
      }
    }
    return null;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_CAPTURE_MODE) {
      return this.m_CaptureMode;
    }
    if (paramPropertyKey == PROP_CAPTURE_MODES) {
      return this.m_ReadOnlyActiveCaptureModes;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    int i = 0;
    int j = this.m_CaptureModeBuilders.size();
    while (i < j)
    {
      createCaptureMode((CaptureModeBuilder)this.m_CaptureModeBuilders.get(i));
      i += 1;
    }
  }
  
  protected void onRelease()
  {
    int i = this.m_AllCaptureModes.size() - 1;
    while (i >= 0)
    {
      CaptureMode localCaptureMode = (CaptureMode)this.m_AllCaptureModes.get(i);
      localCaptureMode.removeCallback(CaptureMode.PROP_STATE, this.m_CaptureModeStateChangedCallback);
      localCaptureMode.release();
      i -= 1;
    }
    this.m_AllCaptureModes.clear();
    this.m_SettingsHandle = Handle.close(this.m_SettingsHandle);
    super.onRelease();
  }
  
  public boolean setCaptureMode(CaptureMode paramCaptureMode, int paramInt)
  {
    Log.v(this.TAG, "setCaptureMode() - Capture mode : ", paramCaptureMode);
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setCaptureMode() - Component is not running");
      return false;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((com.oneplus.camera.PhotoCaptureState)localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "setCaptureMode() - Photo capture state is " + localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE));
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((com.oneplus.camera.VideoCaptureState)localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "setCaptureMode() - Video capture state is " + localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE));
      return false;
    }
    if (paramCaptureMode == null)
    {
      Log.e(this.TAG, "setCaptureMode() - No capture mode");
      return false;
    }
    if (!((List)get(PROP_CAPTURE_MODES)).contains(paramCaptureMode))
    {
      Log.e(this.TAG, "setCaptureMode() - Capture mode '" + paramCaptureMode + "' is not contained in list");
      return false;
    }
    CaptureMode localCaptureMode = this.m_CaptureMode;
    if (localCaptureMode == paramCaptureMode)
    {
      Log.v(this.TAG, "setCaptureMode() - Change to same capture mode");
      return true;
    }
    setReadOnly(PROP_IS_CAPTURE_MODE_SWITCHING, Boolean.valueOf(true));
    this.m_IsInitCaptureModeSet = true;
    if (!Handle.isValid(this.m_CaptureModeChangeCUDHandle)) {
      this.m_CaptureModeChangeCUDHandle = localCameraActivity.disableCaptureUI("CaptureModeSwitch");
    }
    Camera.LensFacing localLensFacing;
    if (Handle.isValid(this.m_CameraLockHandle))
    {
      localObject1 = this.m_LockedCameraLensFacing;
      localLensFacing = (Camera.LensFacing)paramCaptureMode.get(CaptureMode.PROP_TARGET_CAMERA_LENS_FACING);
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((com.oneplus.camera.OperationState)localCameraActivity.get(CameraActivity.PROP_CAMERA_PREVIEW_STATE)).ordinal()])
      {
      default: 
        paramInt = 0;
        label398:
        localCaptureMode.exit(paramCaptureMode, 1);
        if ((Handle.isValid(this.m_CameraLockHandle)) && (localObject1 != localLensFacing)) {
          this.m_CameraLockHandle = Handle.close(this.m_CameraLockHandle);
        }
        localObject2 = paramCaptureMode.getCustomSettings();
        if (localObject2 == null) {
          break;
        }
      }
    }
    for (Object localObject2 = localCameraActivity.setSettings((Settings)localObject2);; localObject2 = null)
    {
      try
      {
        if (paramCaptureMode.enter(localCaptureMode, 1)) {
          break label705;
        }
        Log.e(this.TAG, "setCaptureMode() - Fail to enter '" + paramCaptureMode + "', back to '" + localCaptureMode + "'");
        Handle.close((Handle)localObject2);
        if ((localObject1 != null) && (localObject1 != localLensFacing)) {
          this.m_CameraLockHandle = localCameraActivity.lockCamera((Camera.LensFacing)localObject1);
        }
        if (localCaptureMode.enter(CaptureMode.INVALID, 1)) {
          break label660;
        }
        Log.e(this.TAG, "setCaptureMode() - Fail to enter '" + localCaptureMode + "'");
        throw new RuntimeException("Fail to Change capture mode");
      }
      finally
      {
        if (paramInt != 0)
        {
          Log.v(this.TAG, "setCaptureMode() - Restart preview");
          localCameraActivity.startCameraPreview();
        }
        this.m_CaptureModeChangeCUDHandle = Handle.close(this.m_CaptureModeChangeCUDHandle);
      }
      localObject1 = null;
      break;
      Log.v(this.TAG, "setCaptureMode() - Stop preview to change capture mode");
      paramInt = 1;
      localCameraActivity.stopCameraPreview();
      break label398;
    }
    label660:
    setReadOnly(PROP_IS_CAPTURE_MODE_SWITCHING, Boolean.valueOf(false));
    if (paramInt != 0)
    {
      Log.v(this.TAG, "setCaptureMode() - Restart preview");
      localCameraActivity.startCameraPreview();
    }
    this.m_CaptureModeChangeCUDHandle = Handle.close(this.m_CaptureModeChangeCUDHandle);
    return false;
    label705:
    if ((localLensFacing != null) && (localObject1 == null)) {
      this.m_CameraLockHandle = localCameraActivity.lockCamera(localLensFacing);
    }
    Handle.close(this.m_SettingsHandle);
    this.m_SettingsHandle = ((Handle)localObject2);
    if ((localCaptureMode != CaptureMode.INVALID) && (this.m_ActiveCaptureModes.contains(localCaptureMode))) {}
    for (Object localObject1 = localCaptureMode;; localObject1 = null)
    {
      this.m_PreviousCaptureMode = ((CaptureMode)localObject1);
      this.m_CaptureMode = paramCaptureMode;
      this.m_LockedCameraLensFacing = localLensFacing;
      getSettings().set("CaptureMode.Current", paramCaptureMode.get(CaptureMode.PROP_ID));
      notifyPropertyChanged(PROP_CAPTURE_MODE, localCaptureMode, paramCaptureMode);
      if (paramInt != 0)
      {
        Log.v(this.TAG, "setCaptureMode() - Restart preview");
        localCameraActivity.startCameraPreview();
      }
      this.m_CaptureModeChangeCUDHandle = Handle.close(this.m_CaptureModeChangeCUDHandle);
      setReadOnly(PROP_IS_CAPTURE_MODE_SWITCHING, Boolean.valueOf(false));
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/CaptureModeManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */