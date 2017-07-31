package com.oneplus.camera;

import android.os.Message;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.media.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

final class FlashControllerImpl
  extends CameraComponent
  implements FlashController
{
  private static final int FLAG_IGNORE_UPDATE_FLASH_STATE = 1;
  private static final int MSG_CLOSE_TORCH_FLASH = 10001;
  private static final int MSG_SCREEN_FLASH_STATE_CHANGED = 10010;
  private static final FlashMode[] PERMITTED_FLASH_MODES_DEFAULT = { FlashMode.OFF };
  private static final FlashMode[] PERMITTED_FLASH_MODES_PHOTO_BACK = { FlashMode.AUTO, FlashMode.OFF, FlashMode.ON };
  private static final FlashMode[] PERMITTED_FLASH_MODES_PHOTO_FRONT = PERMITTED_FLASH_MODES_PHOTO_BACK;
  private static final FlashMode[] PERMITTED_FLASH_MODES_VIDEO_BACK = { FlashMode.OFF, FlashMode.TORCH };
  private static final FlashMode[] PERMITTED_FLASH_MODES_VIDEO_FRONT = PERMITTED_FLASH_MODES_DEFAULT;
  @Deprecated
  private static final String SETTINGS_KEY_FLASH_MODE_BACK = "FlashMode.Back";
  @Deprecated
  private static final String SETTINGS_KEY_FLASH_MODE_FRONT = "FlashMode.Front";
  private static final String SETTINGS_KEY_FLASH_MODE_PHOTO_BACK = "FlashMode.Photo.Back";
  private static final String SETTINGS_KEY_FLASH_MODE_PHOTO_FRONT = "FlashMode.Photo.Front";
  private static final String SETTINGS_KEY_FLASH_MODE_VIDEO_BACK = "FlashMode.Video.Back";
  private static final String SETTINGS_KEY_FLASH_MODE_VIDEO_FRONT = "FlashMode.Video.Front";
  private static final FlashTableKey TABLE_KEY_PHOTO_BACK = new FlashTableKey(Camera.LensFacing.BACK, MediaType.PHOTO);
  private static final FlashTableKey TABLE_KEY_PHOTO_FRONT = new FlashTableKey(Camera.LensFacing.FRONT, MediaType.PHOTO);
  private static final FlashTableKey TABLE_KEY_VIDEO_BACK = new FlashTableKey(Camera.LensFacing.BACK, MediaType.VIDEO);
  private static final FlashTableKey TABLE_KEY_VIDEO_FRONT = new FlashTableKey(Camera.LensFacing.FRONT, MediaType.VIDEO);
  private static final int THRESHOLD_LOW_BATTERY = 15;
  private Handle m_BacklightBrightnessHandle;
  private Handle m_BurstFlashDisableHandle;
  private CameraService m_CameraSystemService;
  private ExposureController m_ExposureController;
  private final LinkedList<FlashDisableHandle> m_FlashDisableHandle = new LinkedList();
  private Map<FlashTableKey, FlashMode> m_FlashModeTable = new HashMap();
  private boolean m_IsDisabledByHwLimitation;
  private final PropertyChangedCallback<Boolean> m_IsScreenFlashNeededChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      paramAnonymousPropertySource = FlashControllerImpl.this;
      if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {}
      for (int i = 1;; i = 0)
      {
        HandlerUtils.sendMessage(paramAnonymousPropertySource, 10010, i, 0, null);
        return;
      }
    }
  };
  private View m_ScreenFlashView;
  private Map<FlashTableKey, List<FlashMode>> m_SupportedFlashModesTable = new HashMap();
  private Handle m_TorchFlashHandle;
  private Handle m_TorchFlashRemoteHandle;
  private final PropertyChangedCallback<SupportedState> m_TorchFlashSupportedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<SupportedState> paramAnonymousPropertyKey, PropertyChangeEventArgs<SupportedState> paramAnonymousPropertyChangeEventArgs)
    {
      if (Handle.isValid(FlashControllerImpl.-get5(FlashControllerImpl.this))) {
        switch (-getcom-oneplus-camera-SupportedStateSwitchesValues()[((SupportedState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        }
      }
      for (;;)
      {
        FlashControllerImpl.-get2(FlashControllerImpl.this).removeCallback(CameraService.PROP_TORCH_FLASH_SUPPORTED_STATE, this);
        return;
        FlashControllerImpl.-wrap5(FlashControllerImpl.this, true);
        continue;
        FlashControllerImpl.-wrap0(FlashControllerImpl.this, FlashMode.TORCH);
      }
    }
  };
  
  static
  {
    Settings.setGlobalDefaultValue("FlashMode.Photo.Back", FlashMode.AUTO);
    Settings.setGlobalDefaultValue("FlashMode.Photo.Front", FlashMode.AUTO);
    Settings.setGlobalDefaultValue("FlashMode.Video.Back", FlashMode.OFF);
    Settings.setGlobalDefaultValue("FlashMode.Video.Front", FlashMode.OFF);
  }
  
  FlashControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Flash Controller", paramCameraActivity, true);
  }
  
  private void attachToCamera(final Camera paramCamera)
  {
    HandlerUtils.post(paramCamera, new Runnable()
    {
      public void run()
      {
        paramCamera.addCallback(Camera.PROP_IS_SCREEN_FLASH_NEEDED, FlashControllerImpl.-get4(FlashControllerImpl.this));
      }
    });
  }
  
  private void detachFromCamera(final Camera paramCamera)
  {
    HandlerUtils.post(paramCamera, new Runnable()
    {
      public void run()
      {
        paramCamera.removeCallback(Camera.PROP_IS_SCREEN_FLASH_NEEDED, FlashControllerImpl.-get4(FlashControllerImpl.this));
      }
    });
  }
  
  private void enableFlash(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_FlashDisableHandle.remove(paramHandle)) {
      return;
    }
    updateFlashState();
  }
  
  private FlashMode getFlashModeFromSettings(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    paramLensFacing = selectSettingsKey(paramLensFacing, paramMediaType);
    if (paramLensFacing == null) {
      return null;
    }
    return (FlashMode)getSettings().getEnum(paramLensFacing, FlashMode.class);
  }
  
  private FlashMode getFlashModeFromTable(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    paramLensFacing = selectTableKey(paramLensFacing, paramMediaType);
    if (paramLensFacing == null) {
      return null;
    }
    return (FlashMode)this.m_FlashModeTable.get(paramLensFacing);
  }
  
  private FlashMode[] getPermittedFlashModes(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    if ((paramLensFacing == null) || (paramMediaType == null)) {
      return null;
    }
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[paramLensFacing.ordinal()])
    {
    default: 
      return PERMITTED_FLASH_MODES_DEFAULT;
    case 1: 
    case 2: 
    case 3: 
      if (paramMediaType == MediaType.PHOTO) {
        return PERMITTED_FLASH_MODES_PHOTO_BACK;
      }
      if (paramMediaType == MediaType.VIDEO) {
        return PERMITTED_FLASH_MODES_VIDEO_BACK;
      }
      break;
    case 4: 
      if (paramMediaType == MediaType.PHOTO) {
        return PERMITTED_FLASH_MODES_PHOTO_FRONT;
      }
      if (paramMediaType == MediaType.VIDEO) {
        return PERMITTED_FLASH_MODES_VIDEO_FRONT;
      }
      break;
    }
    return null;
  }
  
  private List<FlashMode> getSupportedFlashModes(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    paramLensFacing = selectTableKey(paramLensFacing, paramMediaType);
    if (paramLensFacing == null) {
      return null;
    }
    return (List)this.m_SupportedFlashModesTable.get(paramLensFacing);
  }
  
  private void onScreenFlashStateChanged(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      if (this.m_ScreenFlashView != null) {
        this.m_ScreenFlashView.setVisibility(8);
      }
      Handle.close(this.m_BacklightBrightnessHandle);
      return;
    }
    if (this.m_ScreenFlashView == null)
    {
      this.m_ScreenFlashView = getCameraActivity().findViewById(2131361834);
      if (this.m_ScreenFlashView == null) {
        return;
      }
    }
    Handle.close(this.m_BacklightBrightnessHandle);
    if (this.m_CameraSystemService != null) {
      this.m_BacklightBrightnessHandle = this.m_CameraSystemService.setBacklightMaxBrightness();
    }
    this.m_ScreenFlashView.setAlpha(0.0F);
    this.m_ScreenFlashView.setVisibility(0);
    this.m_ScreenFlashView.animate().alpha(1.0F).setDuration(200L).start();
  }
  
  private void resetToDefaultFlashMode()
  {
    Log.v(this.TAG, "resetToDefaultFlashMode() - Reset flash to default settings");
    Settings localSettings;
    FlashMode localFlashMode;
    if ((!((Boolean)get(PROP_HAS_FLASH)).booleanValue()) || (((Boolean)get(PROP_IS_FLASH_DISABLED)).booleanValue()))
    {
      localSettings = getSettings();
      localFlashMode = (FlashMode)localSettings.getEnum("FlashMode.Photo.Back", FlashMode.class);
      if (localFlashMode != FlashMode.ON) {
        break label211;
      }
      this.m_FlashModeTable.put(TABLE_KEY_PHOTO_BACK, FlashMode.AUTO);
      localSettings.set("FlashMode.Photo.Back", FlashMode.AUTO);
      label91:
      localFlashMode = (FlashMode)localSettings.getEnum("FlashMode.Photo.Front", FlashMode.class);
      if (localFlashMode != FlashMode.ON) {
        break label228;
      }
      this.m_FlashModeTable.put(TABLE_KEY_PHOTO_FRONT, FlashMode.AUTO);
      localSettings.set("FlashMode.Photo.Front", FlashMode.AUTO);
    }
    for (;;)
    {
      this.m_FlashModeTable.put(TABLE_KEY_VIDEO_BACK, FlashMode.OFF);
      this.m_FlashModeTable.put(TABLE_KEY_VIDEO_FRONT, FlashMode.OFF);
      localSettings.set("FlashMode.Video.Back", FlashMode.OFF);
      localSettings.set("FlashMode.Video.Front", FlashMode.OFF);
      return;
      if (get(PROP_FLASH_MODE) != FlashMode.ON) {
        break;
      }
      setFlashMode(FlashMode.AUTO, false, true);
      break;
      label211:
      this.m_FlashModeTable.put(TABLE_KEY_PHOTO_BACK, localFlashMode);
      break label91;
      label228:
      this.m_FlashModeTable.put(TABLE_KEY_PHOTO_FRONT, localFlashMode);
    }
  }
  
  private String selectSettingsKey(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    if ((paramLensFacing == null) || (paramMediaType == null)) {
      return null;
    }
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[paramLensFacing.ordinal()])
    {
    }
    do
    {
      do
      {
        return null;
        if (paramMediaType == MediaType.PHOTO) {
          return "FlashMode.Photo.Back";
        }
      } while (paramMediaType != MediaType.VIDEO);
      return "FlashMode.Video.Back";
      if (paramMediaType == MediaType.PHOTO) {
        return "FlashMode.Photo.Front";
      }
    } while (paramMediaType != MediaType.VIDEO);
    return "FlashMode.Video.Front";
  }
  
  private FlashTableKey selectTableKey(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
  {
    if ((paramLensFacing == null) || (paramMediaType == null)) {
      return null;
    }
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[paramLensFacing.ordinal()])
    {
    }
    do
    {
      do
      {
        return null;
        if (paramMediaType == MediaType.PHOTO) {
          return TABLE_KEY_PHOTO_BACK;
        }
      } while (paramMediaType != MediaType.VIDEO);
      return TABLE_KEY_VIDEO_BACK;
      if (paramMediaType == MediaType.PHOTO) {
        return TABLE_KEY_PHOTO_FRONT;
      }
    } while (paramMediaType != MediaType.VIDEO);
    return TABLE_KEY_VIDEO_FRONT;
  }
  
  private void setFlashMode(FlashMode paramFlashMode, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) || (get(PROP_FLASH_MODE) != paramFlashMode))
    {
      verifyAccess();
      if (paramFlashMode == null) {
        throw new IllegalArgumentException("No flash mode.");
      }
      if ((paramFlashMode != FlashMode.OFF) && ((!((Boolean)get(PROP_HAS_FLASH)).booleanValue()) || ((((Boolean)get(PROP_IS_FLASH_DISABLED)).booleanValue()) && (get(PROP_FLASH_DISABLED_REASON) != FlashController.FlashDisabledReason.AE_LOCKED))))
      {
        Log.e(this.TAG, "setFlashMode() - No flash support");
        return;
      }
      Log.v(this.TAG, "setFlashMode() - Flash mode : ", paramFlashMode);
      setFlashMode(paramFlashMode);
      Object localObject = getCamera();
      if (localObject != null)
      {
        MediaType localMediaType = getMediaType();
        localObject = (Camera.LensFacing)((Camera)localObject).get(Camera.PROP_LENS_FACING);
        setFlashModeToTable((Camera.LensFacing)localObject, localMediaType, paramFlashMode);
        if (paramBoolean2) {
          setFlashModeToSettings((Camera.LensFacing)localObject, localMediaType, paramFlashMode);
        }
      }
      setReadOnly(PROP_FLASH_MODE, paramFlashMode);
    }
  }
  
  private boolean setFlashMode(final FlashMode paramFlashMode)
  {
    final Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.e(this.TAG, "setFlashMode() - No primary camera");
      return false;
    }
    if (!HandlerUtils.post(localCamera, new Runnable()
    {
      public void run()
      {
        try
        {
          localCamera.set(Camera.PROP_FLASH_MODE, paramFlashMode);
          return;
        }
        catch (Throwable localThrowable)
        {
          Log.e(FlashControllerImpl.-get0(FlashControllerImpl.this), "setFlashMode() - Fail to set flash mode", localThrowable);
        }
      }
    }))
    {
      Log.e(this.TAG, "setFlashMode() - Fail to perform cross-thread operation");
      return false;
    }
    return true;
  }
  
  private void setFlashModeToSettings(Camera.LensFacing paramLensFacing, MediaType paramMediaType, FlashMode paramFlashMode)
  {
    String str = selectSettingsKey(paramLensFacing, paramMediaType);
    if (str != null)
    {
      Log.v(this.TAG, "setFlashModeToSettings() - Lens facing: ", paramLensFacing, ", media type: ", paramMediaType, ", flash: ", paramFlashMode);
      getSettings().set(str, paramFlashMode);
    }
  }
  
  private void setFlashModeToTable(Camera.LensFacing paramLensFacing, MediaType paramMediaType, FlashMode paramFlashMode)
  {
    FlashTableKey localFlashTableKey = selectTableKey(paramLensFacing, paramMediaType);
    if (localFlashTableKey != null)
    {
      Log.v(this.TAG, "setFlashModeToTable() - Lens facing: ", paramLensFacing, ", media type: ", paramMediaType, ", flash: ", paramFlashMode);
      this.m_FlashModeTable.put(localFlashTableKey, paramFlashMode);
    }
  }
  
  private void setSupportedFlashModes(Camera.LensFacing paramLensFacing, MediaType paramMediaType, List<FlashMode> paramList)
  {
    FlashTableKey localFlashTableKey = selectTableKey(paramLensFacing, paramMediaType);
    if (localFlashTableKey != null)
    {
      Log.v(this.TAG, "setSupportedFlashModes() - Lens facing: ", paramLensFacing, ", media type: ", paramMediaType, ", supported flash: ", paramList);
      this.m_SupportedFlashModesTable.put(localFlashTableKey, paramList);
    }
  }
  
  private void torchFlashRemote(boolean paramBoolean)
  {
    if (this.m_CameraSystemService == null) {
      return;
    }
    if (this.m_CameraSystemService.get(CameraService.PROP_TORCH_FLASH_SUPPORTED_STATE) != SupportedState.SUPPORTED) {
      return;
    }
    Handle.close(this.m_TorchFlashRemoteHandle);
    if (paramBoolean) {
      this.m_TorchFlashRemoteHandle = this.m_CameraSystemService.torchFlash();
    }
  }
  
  private void updateFlashState()
  {
    HandlerUtils.removeMessages(this, 10001);
    this.m_TorchFlashHandle = Handle.close(this.m_TorchFlashHandle, 1);
    Camera.LensFacing localLensFacing = null;
    Object localObject4 = getCamera();
    if (localObject4 == null) {
      Log.e(this.TAG, "updateFlashState() - No current camera");
    }
    MediaType localMediaType;
    Object localObject1;
    for (;;)
    {
      localMediaType = getMediaType();
      localObject2 = getFlashModeFromTable(localLensFacing, localMediaType);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = getFlashModeFromSettings(localLensFacing, localMediaType);
      }
      Log.v(this.TAG, "updateFlashState() - Flash from table: ", localObject1, ", lens facing: ", localLensFacing, ", media type: ", localMediaType);
      if ((localObject4 == null) || (!((Boolean)((Camera)localObject4).get(Camera.PROP_HAS_FLASH)).booleanValue())) {
        break;
      }
      localObject3 = getSupportedFlashModes(localLensFacing, localMediaType);
      localObject2 = localObject3;
      if (localObject3 != null) {
        break label338;
      }
      localObject2 = new ArrayList();
      localObject3 = getPermittedFlashModes(localLensFacing, localMediaType);
      localObject4 = new ArrayList((Collection)((Camera)localObject4).get(Camera.PROP_FLASH_MODES));
      int i = 0;
      int j = localObject3.length;
      while (i < j)
      {
        Object localObject5 = localObject3[i];
        if (((List)localObject4).contains(localObject5)) {
          ((List)localObject2).add(localObject5);
        }
        i += 1;
      }
      localLensFacing = (Camera.LensFacing)((Camera)localObject4).get(Camera.PROP_LENS_FACING);
    }
    if (localObject1 == null) {
      setFlashModeToTable(localLensFacing, localMediaType, FlashMode.OFF);
    }
    setReadOnly(PROP_HAS_FLASH, Boolean.valueOf(false));
    setReadOnly(PROP_FLASH_MODE, FlashMode.OFF);
    setReadOnly(PROP_FLASH_MODES, Arrays.asList(new FlashMode[] { FlashMode.OFF }));
    setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.UNKNOWN);
    setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(true));
    return;
    setSupportedFlashModes(localLensFacing, localMediaType, (List)localObject2);
    label338:
    setReadOnly(PROP_FLASH_MODES, localObject2);
    if (((List)localObject2).size() <= 1)
    {
      setReadOnly(PROP_HAS_FLASH, Boolean.valueOf(false));
      setReadOnly(PROP_FLASH_MODE, FlashMode.OFF);
      setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.UNKNOWN);
      setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(true));
      return;
    }
    setReadOnly(PROP_HAS_FLASH, Boolean.valueOf(true));
    if (!this.m_FlashDisableHandle.isEmpty())
    {
      if ((((FlashDisableHandle)this.m_FlashDisableHandle.getLast()).reason.ordinal() <= FlashController.FlashDisabledReason.LOW_BATTERY_LEVEL.ordinal()) && (this.m_IsDisabledByHwLimitation)) {
        if (((Integer)getCameraActivity().get(CameraActivity.PROP_BATTERY_LEVEL)).intValue() <= 15) {
          setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.LOW_BATTERY_LEVEL);
        }
      }
      for (;;)
      {
        setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(true));
        setFlashMode(FlashMode.OFF);
        return;
        Log.d(this.TAG, "updateFlashState() - Disable reason: " + ((FlashDisableHandle)this.m_FlashDisableHandle.getLast()).reason);
        setReadOnly(PROP_FLASH_DISABLED_REASON, ((FlashDisableHandle)this.m_FlashDisableHandle.getLast()).reason);
      }
    }
    if (((Integer)getCameraActivity().get(CameraActivity.PROP_BATTERY_LEVEL)).intValue() <= 15)
    {
      this.m_IsDisabledByHwLimitation = true;
      setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.LOW_BATTERY_LEVEL);
      setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(true));
      setFlashMode(FlashMode.OFF);
      return;
    }
    if ((getCameraActivity().get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.PHOTO) && (this.m_ExposureController != null) && (((Boolean)this.m_ExposureController.get(ExposureController.PROP_IS_AE_LOCKED)).booleanValue()))
    {
      setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.AE_LOCKED);
      setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(true));
      setFlashMode(FlashMode.OFF);
      return;
    }
    this.m_IsDisabledByHwLimitation = false;
    setReadOnly(PROP_FLASH_DISABLED_REASON, FlashController.FlashDisabledReason.UNKNOWN);
    setReadOnly(PROP_IS_FLASH_DISABLED, Boolean.valueOf(false));
    Object localObject3 = getSupportedFlashModes(localLensFacing, localMediaType);
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = FlashMode.OFF;
    }
    switch (-getcom-oneplus-camera-FlashModeSwitchesValues()[localObject2.ordinal()])
    {
    default: 
      localObject1 = localObject2;
    }
    for (;;)
    {
      setFlashMode((FlashMode)localObject1);
      setReadOnly(PROP_FLASH_MODE, localObject1);
      Log.d(this.TAG, "updateFlashState() - Lens facing: ", localLensFacing, ", media type: ", localMediaType, ", flash: ", localObject1);
      return;
      localObject1 = localObject2;
      if (!((List)localObject3).contains(FlashMode.AUTO))
      {
        localObject1 = FlashMode.OFF;
        continue;
        localObject1 = localObject2;
        if (!((List)localObject3).contains(FlashMode.ON)) {
          if (((List)localObject3).contains(FlashMode.TORCH))
          {
            localObject1 = FlashMode.TORCH;
          }
          else
          {
            localObject1 = FlashMode.OFF;
            continue;
            localObject1 = localObject2;
            if (!((List)localObject3).contains(FlashMode.TORCH)) {
              if (((List)localObject3).contains(FlashMode.ON)) {
                localObject1 = FlashMode.ON;
              } else {
                localObject1 = FlashMode.OFF;
              }
            }
          }
        }
      }
    }
  }
  
  public Handle disableFlash(FlashController.FlashDisabledReason paramFlashDisabledReason, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "disableFlash() - Component is not running");
      return null;
    }
    FlashController.FlashDisabledReason localFlashDisabledReason = paramFlashDisabledReason;
    if (paramFlashDisabledReason == null) {
      localFlashDisabledReason = FlashController.FlashDisabledReason.UNKNOWN;
    }
    paramFlashDisabledReason = new FlashDisableHandle(localFlashDisabledReason);
    this.m_FlashDisableHandle.add(paramFlashDisabledReason);
    updateFlashState();
    return paramFlashDisabledReason;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool = false;
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10001: 
      this.m_TorchFlashHandle = Handle.close(this.m_TorchFlashHandle);
      return;
    }
    if (paramMessage.arg1 != 0) {
      bool = true;
    }
    onScreenFlashStateChanged(bool);
  }
  
  protected void onDeinitialize()
  {
    detachFromCamera(getCamera());
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    Object localObject = (FlashMode)getSettings().getEnum("FlashMode.Back", FlashMode.class, null);
    if (localObject != null)
    {
      getSettings().set("FlashMode.Photo.Back", localObject);
      getSettings().set("FlashMode.Back", null);
    }
    findComponent(ExposureController.class, new ComponentSearchCallback()
    {
      public void onComponentFound(ExposureController paramAnonymousExposureController)
      {
        FlashControllerImpl.-set1(FlashControllerImpl.this, paramAnonymousExposureController);
        if (FlashControllerImpl.-get3(FlashControllerImpl.this) != null) {
          FlashControllerImpl.-get3(FlashControllerImpl.this).addCallback(ExposureController.PROP_IS_AE_LOCKED, new PropertyChangedCallback()
          {
            public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<Boolean> paramAnonymous2PropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymous2PropertyChangeEventArgs)
            {
              switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)FlashControllerImpl.this.getCameraActivity().get(CameraActivity.PROP_MEDIA_TYPE)).ordinal()])
              {
              default: 
                return;
              case 1: 
                FlashControllerImpl.-wrap6(FlashControllerImpl.this);
                return;
              }
              Log.v(FlashControllerImpl.-get0(FlashControllerImpl.this), "onPropertyChanged() - video mode, do nothing");
            }
          });
        }
      }
    });
    localObject = getCameraActivity();
    PropertyChangedCallback local6 = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        FlashControllerImpl.-wrap6(FlashControllerImpl.this);
      }
    };
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_BATTERY_LEVEL, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Integer)paramAnonymousPropertyChangeEventArgs.getOldValue()).intValue() > 15) {
          if (((Integer)paramAnonymousPropertyChangeEventArgs.getNewValue()).intValue() <= 15)
          {
            Log.w(FlashControllerImpl.-get0(FlashControllerImpl.this), "onPropertyChanged() - Battery level is too low, disable flash");
            FlashControllerImpl.-set2(FlashControllerImpl.this, true);
            if (FlashControllerImpl.this.getCameraActivity().get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.CAPTURING) {
              break label78;
            }
            FlashControllerImpl.-wrap6(FlashControllerImpl.this);
          }
        }
        label78:
        while (((Integer)paramAnonymousPropertyChangeEventArgs.getNewValue()).intValue() <= 15)
        {
          return;
          Log.w(FlashControllerImpl.-get0(FlashControllerImpl.this), "onPropertyChanged() - Capturing photo, disable flash later");
          return;
        }
        Log.w(FlashControllerImpl.-get0(FlashControllerImpl.this), "onPropertyChanged() - Battery level becomes higher, enable flash");
        FlashControllerImpl.-set2(FlashControllerImpl.this, false);
        FlashControllerImpl.-wrap6(FlashControllerImpl.this);
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        FlashControllerImpl.-wrap2(FlashControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue());
        FlashControllerImpl.-wrap1(FlashControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
        FlashControllerImpl.-wrap6(FlashControllerImpl.this);
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_READY_TO_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          FlashControllerImpl.-set0(FlashControllerImpl.this, Handle.close(FlashControllerImpl.-get1(FlashControllerImpl.this)));
          FlashControllerImpl.-wrap6(FlashControllerImpl.this);
        }
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_MEDIA_TYPE, local6);
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_SETTINGS, local6);
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING) {
          FlashControllerImpl.-wrap6(FlashControllerImpl.this);
        }
      }
    });
    if (!((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue()) {
      resetToDefaultFlashMode();
    }
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          FlashControllerImpl.-wrap4(FlashControllerImpl.this);
        }
      }
    });
    ((CameraActivity)localObject).addHandler(CameraActivity.EVENT_CAPTURE_STARTING, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        if (paramAnonymousCaptureEventArgs.isBurstPhotoCapture()) {
          FlashControllerImpl.-set0(FlashControllerImpl.this, FlashControllerImpl.this.disableFlash(FlashController.FlashDisabledReason.NOT_SUPPORTED_IN_CAPTURE, 0));
        }
      }
    });
    this.m_CameraSystemService = ((CameraService)findComponent(CameraService.class));
    if (this.m_CameraSystemService != null) {
      this.m_CameraSystemService.addCallback(CameraService.PROP_TORCH_FLASH_SUPPORTED_STATE, this.m_TorchFlashSupportedCallback);
    }
    attachToCamera(getCamera());
    resetToDefaultFlashMode();
    updateFlashState();
  }
  
  public void setFlashMode(FlashMode paramFlashMode, int paramInt)
  {
    if ((paramInt & 0x1) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      setFlashMode(paramFlashMode, false, bool);
      return;
    }
  }
  
  public Handle torchFlash(long paramLong)
  {
    Object localObject = getCamera();
    if (localObject == null)
    {
      Log.w(this.TAG, "torchFlash() - No camera to torch");
      return null;
    }
    if (!((Boolean)((Camera)localObject).get(Camera.PROP_HAS_FLASH)).booleanValue())
    {
      Log.w(this.TAG, "torchFlash() - No flash");
      return null;
    }
    if ((((Boolean)get(PROP_IS_FLASH_DISABLED)).booleanValue()) && (this.m_IsDisabledByHwLimitation))
    {
      Log.w(this.TAG, "torchFlash() - Flash is disabled");
      return null;
    }
    Handle.close(this.m_TorchFlashHandle);
    this.m_TorchFlashHandle = new Handle("TorchFlashHandle")
    {
      protected void onClose(int paramAnonymousInt)
      {
        Handle.close(FlashControllerImpl.-get6(FlashControllerImpl.this));
        if ((paramAnonymousInt & 0x1) == 0) {
          FlashControllerImpl.-wrap6(FlashControllerImpl.this);
        }
      }
    };
    if (this.m_CameraSystemService == null)
    {
      this.m_CameraSystemService = ((CameraService)findComponent(CameraService.class));
      if (this.m_CameraSystemService != null) {
        this.m_CameraSystemService.addCallback(CameraService.PROP_TORCH_FLASH_SUPPORTED_STATE, this.m_TorchFlashSupportedCallback);
      }
    }
    if (this.m_CameraSystemService != null)
    {
      localObject = (SupportedState)this.m_CameraSystemService.get(CameraService.PROP_TORCH_FLASH_SUPPORTED_STATE);
      switch (-getcom-oneplus-camera-SupportedStateSwitchesValues()[localObject.ordinal()])
      {
      }
    }
    for (;;)
    {
      return this.m_TorchFlashHandle;
      torchFlashRemote(true);
      HandlerUtils.sendMessage(this, 10001, paramLong);
      continue;
      setFlashMode(FlashMode.TORCH);
      HandlerUtils.sendMessage(this, 10001, paramLong);
    }
  }
  
  private final class FlashDisableHandle
    extends Handle
  {
    public final FlashController.FlashDisabledReason reason;
    
    public FlashDisableHandle(FlashController.FlashDisabledReason paramFlashDisabledReason)
    {
      super();
      this.reason = paramFlashDisabledReason;
    }
    
    protected void onClose(int paramInt)
    {
      FlashControllerImpl.-wrap3(FlashControllerImpl.this, this);
    }
    
    public String toString()
    {
      return super.toString() + "{ Reason = " + this.reason + " }";
    }
  }
  
  private static final class FlashTableKey
  {
    private final Camera.LensFacing m_LensFacing;
    private final MediaType m_MediaType;
    
    FlashTableKey(Camera.LensFacing paramLensFacing, MediaType paramMediaType)
    {
      this.m_LensFacing = paramLensFacing;
      this.m_MediaType = paramMediaType;
    }
    
    private Camera.LensFacing getLensFacing()
    {
      return this.m_LensFacing;
    }
    
    private MediaType getMediaType()
    {
      return this.m_MediaType;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FlashControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */