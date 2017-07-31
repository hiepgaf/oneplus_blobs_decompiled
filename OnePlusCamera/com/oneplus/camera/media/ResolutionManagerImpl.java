package com.oneplus.camera.media;

import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.MediaResultInfo;
import com.oneplus.camera.ui.Viewfinder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class ResolutionManagerImpl
  extends CameraComponent
  implements ResolutionManager
{
  private static final long MAX_VIDEO_DURATION_4K = 600L;
  private PhotoResolutionSelector m_DefaultPhotoResSelector;
  private VideoResolutionSelector m_DefaultVideoResSelector;
  private final LinkedList<ResolutionSelectorHandle> m_PhotoResSelectorHandles = new LinkedList();
  private Resolution m_PhotoResolution;
  private final LinkedList<ResolutionSelectorHandle> m_VideoResSelectorHandles = new LinkedList();
  private Resolution m_VideoResolution;
  private Viewfinder m_Viewfinder;
  
  ResolutionManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Resolution Manager", paramCameraActivity, false);
    enablePropertyLogs(PROP_PHOTO_PREVIEW_SIZE, 1);
    enablePropertyLogs(PROP_PHOTO_RESOLUTION, 1);
    enablePropertyLogs(PROP_MAX_VIDEO_DURATION_SECONDS, 1);
    enablePropertyLogs(PROP_MAX_VIDEO_FILE_SIZE, 1);
    enablePropertyLogs(PROP_VIDEO_PREVIEW_SIZE, 1);
    enablePropertyLogs(PROP_VIDEO_RESOLUTION, 1);
  }
  
  private PhotoResolutionSelector getPhotoResolutionSelector()
  {
    if (this.m_PhotoResSelectorHandles.isEmpty()) {
      return this.m_DefaultPhotoResSelector;
    }
    return (PhotoResolutionSelector)((ResolutionSelectorHandle)this.m_PhotoResSelectorHandles.getLast()).selector;
  }
  
  private Size getPreviewContainerSize()
  {
    if (this.m_Viewfinder != null) {
      return (Size)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_CONTAINER_SIZE);
    }
    return ((ScreenSize)getCameraActivity().get(CameraActivity.PROP_SCREEN_SIZE)).toSize();
  }
  
  private VideoResolutionSelector getVideoResolutionSelector()
  {
    if (this.m_VideoResSelectorHandles.isEmpty()) {
      return this.m_DefaultVideoResSelector;
    }
    return (VideoResolutionSelector)((ResolutionSelectorHandle)this.m_VideoResSelectorHandles.getLast()).selector;
  }
  
  private void restoreResolutionSelector(ResolutionSelectorHandle paramResolutionSelectorHandle, int paramInt)
  {
    verifyAccess();
    boolean bool2 = false;
    boolean bool3 = false;
    Log.v(this.TAG, "restoreResolutionSelector() - Selector : ", paramResolutionSelectorHandle.selector, ", handle : ", paramResolutionSelectorHandle);
    if ((paramResolutionSelectorHandle.selector instanceof PhotoResolutionSelector))
    {
      if ((paramInt & 0x1) != 0)
      {
        bool1 = true;
        this.m_PhotoResSelectorHandles.remove(paramResolutionSelectorHandle);
        bool2 = bool1;
      }
    }
    else
    {
      bool1 = bool3;
      if ((paramResolutionSelectorHandle.selector instanceof VideoResolutionSelector)) {
        if ((paramInt & 0x1) == 0) {
          break label100;
        }
      }
    }
    label100:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.m_VideoResSelectorHandles.remove(paramResolutionSelectorHandle);
      selectResolutions(bool2, bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  private List<Resolution> selectResolutions(ResolutionSelector paramResolutionSelector, Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
  {
    if (paramCamera == null) {
      return Collections.EMPTY_LIST;
    }
    try
    {
      paramSettings = paramResolutionSelector.selectResolutions(paramCamera, paramSettings, paramRestriction);
      paramCamera = paramSettings;
      if (paramSettings == null)
      {
        Log.e(this.TAG, "selectResolutions() - Got Null resolution list from " + paramResolutionSelector);
        paramCamera = Collections.EMPTY_LIST;
      }
    }
    catch (Throwable paramResolutionSelector)
    {
      for (;;)
      {
        Log.e(this.TAG, "selectResolutions() - Fail to select resolutions", paramResolutionSelector);
        paramCamera = Collections.EMPTY_LIST;
      }
    }
    if (paramCamera.isEmpty()) {
      Log.e(this.TAG, "selectResolutions() - Empty resolution list");
    }
    return paramCamera;
  }
  
  private boolean selectResolutions(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.m_Viewfinder == null)
    {
      Log.w(this.TAG, "selectResolutions() - No Viewfinder");
      return false;
    }
    Log.v(this.TAG, "selectResolutions(", Boolean.valueOf(paramBoolean1), ", ", Boolean.valueOf(paramBoolean2), ")");
    Camera localCamera = (Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA);
    if (localCamera == null)
    {
      Log.e(this.TAG, "selectResolutions() - No camera");
      return false;
    }
    Size localSize = getPreviewContainerSize();
    PhotoResolutionSelector localPhotoResolutionSelector = getPhotoResolutionSelector();
    VideoResolutionSelector localVideoResolutionSelector = getVideoResolutionSelector();
    Settings localSettings = getSettings();
    List localList = selectResolutions(localPhotoResolutionSelector, localCamera, localSettings, null);
    Resolution localResolution2 = this.m_PhotoResolution;
    Object localObject;
    Resolution localResolution1;
    if (!localList.isEmpty()) {
      if (paramBoolean1)
      {
        localObject = localResolution2;
        localResolution1 = localPhotoResolutionSelector.selectResolution(localCamera, localSettings, localList, (Resolution)localObject, null);
        if (localResolution1 == null) {
          break label333;
        }
        localObject = localPhotoResolutionSelector.selectPreviewSize(localCamera, localSettings, localSize, localResolution1);
        label168:
        if (localObject == null) {
          break label372;
        }
        this.m_PhotoResolution = localResolution1;
        setReadOnly(PROP_PHOTO_RESOLUTION_LIST, localList);
        notifyPropertyChanged(PROP_PHOTO_RESOLUTION, localResolution2, localResolution1);
        setReadOnly(PROP_PHOTO_PREVIEW_SIZE, localObject);
        label209:
        localList = selectResolutions(localVideoResolutionSelector, localCamera, localSettings, null);
        localResolution2 = this.m_VideoResolution;
        if (localList.isEmpty()) {
          break label421;
        }
        if (!paramBoolean2) {
          break label403;
        }
        localObject = localResolution2;
        label245:
        localResolution1 = localVideoResolutionSelector.selectResolution(localCamera, localSettings, localList, (Resolution)localObject, null);
        if (localResolution1 == null) {
          break label408;
        }
        localObject = localVideoResolutionSelector.selectPreviewSize(localCamera, localSettings, localSize, localResolution1);
      }
    }
    for (;;)
    {
      this.m_VideoResolution = localResolution1;
      setReadOnly(PROP_VIDEO_RESOLUTION_LIST, localList);
      notifyPropertyChanged(PROP_VIDEO_RESOLUTION, localResolution2, localResolution1);
      setReadOnly(PROP_VIDEO_PREVIEW_SIZE, localObject);
      updateVideoLimitations(localResolution1);
      return true;
      localObject = null;
      break;
      label333:
      localObject = new Size(0, 0);
      break label168;
      Log.e(this.TAG, "selectResolutions() - Empty photo resolution list");
      localResolution1 = null;
      localObject = new Size(0, 0);
      break label168;
      label372:
      Log.e(this.TAG, "selectResolutions() - No matching previewSize for Resolution " + localResolution1);
      break label209;
      label403:
      localObject = null;
      break label245;
      label408:
      localObject = new Size(0, 0);
      continue;
      label421:
      Log.e(this.TAG, "selectResolutions() - Empty video resolution list");
      localResolution1 = null;
      localObject = new Size(0, 0);
    }
  }
  
  private boolean setPhotoResolutionProp(Resolution paramResolution)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setPhotoResolutionProp() - Component is not running");
      return false;
    }
    if (((this.m_PhotoResolution != null) && (this.m_PhotoResolution.equals(paramResolution))) || (this.m_PhotoResolution == paramResolution)) {
      return false;
    }
    if ((paramResolution == null) || (((List)get(PROP_PHOTO_RESOLUTION_LIST)).contains(paramResolution)))
    {
      localObject = (Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA);
      if (localObject == null)
      {
        Log.e(this.TAG, "selectResolutions() - No camera");
        return false;
      }
    }
    else
    {
      Log.e(this.TAG, "setPhotoResolutionProp() - Resolution " + paramResolution + " is not contained in list");
      return false;
    }
    Settings localSettings = getSettings();
    PhotoResolutionSelector localPhotoResolutionSelector = getPhotoResolutionSelector();
    Size localSize = localPhotoResolutionSelector.selectPreviewSize((Camera)localObject, localSettings, getPreviewContainerSize(), paramResolution);
    if (localSize == null)
    {
      Log.e(this.TAG, "setPhotoResolutionProp() - No matching previewSize for Resolution " + paramResolution);
      return false;
    }
    localPhotoResolutionSelector.saveResolution((Camera)localObject, localSettings, paramResolution);
    Object localObject = this.m_PhotoResolution;
    this.m_PhotoResolution = paramResolution;
    notifyPropertyChanged(PROP_PHOTO_RESOLUTION, localObject, paramResolution);
    setReadOnly(PROP_PHOTO_PREVIEW_SIZE, localSize);
    return true;
  }
  
  private boolean setVideoResolutionProp(Resolution paramResolution)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setVideoResolutionProp() - Component is not running");
      return false;
    }
    if ((paramResolution == null) || ((this.m_VideoResolution != null) && (this.m_VideoResolution.equals(paramResolution))) || (this.m_VideoResolution == paramResolution)) {
      return false;
    }
    if ((paramResolution == null) || (((List)get(PROP_VIDEO_RESOLUTION_LIST)).contains(paramResolution)))
    {
      localObject = (Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA);
      if (localObject == null)
      {
        Log.e(this.TAG, "selectResolutions() - No camera");
        return false;
      }
    }
    else
    {
      Log.e(this.TAG, "setVideoResolutionProp() - Resolution " + paramResolution + " is not contained in list");
      return false;
    }
    Settings localSettings = getSettings();
    VideoResolutionSelector localVideoResolutionSelector = getVideoResolutionSelector();
    Size localSize = localVideoResolutionSelector.selectPreviewSize((Camera)localObject, localSettings, getPreviewContainerSize(), paramResolution);
    localVideoResolutionSelector.saveResolution((Camera)localObject, localSettings, paramResolution);
    Object localObject = this.m_VideoResolution;
    this.m_VideoResolution = paramResolution;
    notifyPropertyChanged(PROP_VIDEO_RESOLUTION, localObject, paramResolution);
    setReadOnly(PROP_VIDEO_PREVIEW_SIZE, localSize);
    updateVideoLimitations(paramResolution);
    return true;
  }
  
  private void updateVideoLimitations(Resolution paramResolution)
  {
    long l2 = -1L;
    if (paramResolution != null)
    {
      if (paramResolution.is4kVideo())
      {
        setReadOnly(PROP_MAX_VIDEO_DURATION_SECONDS, Long.valueOf(600L));
        return;
      }
      paramResolution = getCameraActivity().getMediaResultInfo();
      if (paramResolution != null)
      {
        PropertyKey localPropertyKey = PROP_MAX_VIDEO_DURATION_SECONDS;
        if (paramResolution.extraDurationLimit <= 0L)
        {
          l1 = -1L;
          setReadOnly(localPropertyKey, Long.valueOf(l1));
          localPropertyKey = PROP_MAX_VIDEO_FILE_SIZE;
          if (paramResolution.extraSizeLimit > 0L) {
            break label109;
          }
        }
        label109:
        for (long l1 = l2;; l1 = paramResolution.extraSizeLimit)
        {
          setReadOnly(localPropertyKey, Long.valueOf(l1));
          return;
          l1 = paramResolution.extraDurationLimit;
          break;
        }
      }
      setReadOnly(PROP_MAX_VIDEO_DURATION_SECONDS, Long.valueOf(-1L));
      return;
    }
    setReadOnly(PROP_MAX_VIDEO_DURATION_SECONDS, Long.valueOf(0L));
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_PHOTO_RESOLUTION) {
      return this.m_PhotoResolution;
    }
    if (paramPropertyKey == PROP_VIDEO_RESOLUTION) {
      return this.m_VideoResolution;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public String getPhotoResolutionSettingsKey()
  {
    Camera localCamera = (Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA);
    if (localCamera == null)
    {
      Log.e(this.TAG, "selectResolutions() - No camera");
      return null;
    }
    Settings localSettings = getSettings();
    return getPhotoResolutionSelector().getResolutionSettingsKey(localCamera, localSettings);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_DefaultPhotoResSelector = new DefaultPhotoResolutionSelector(localCameraActivity);
    this.m_DefaultVideoResSelector = new DefaultVideoResolutionSelector(localCameraActivity);
    ComponentUtils.findComponent(getCameraActivity(), Viewfinder.class, this, new ComponentSearchCallback()
    {
      public void onComponentFound(Viewfinder paramAnonymousViewfinder)
      {
        ResolutionManagerImpl.-set0(ResolutionManagerImpl.this, paramAnonymousViewfinder);
        if (ResolutionManagerImpl.this.getCameraActivity().get(CameraActivity.PROP_CAMERA) != null) {
          ResolutionManagerImpl.-wrap0(ResolutionManagerImpl.this, true, true);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        ResolutionManagerImpl.-wrap0(ResolutionManagerImpl.this, false, false);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          ResolutionManagerImpl.-wrap0(ResolutionManagerImpl.this, false, false);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_SETTINGS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Settings> paramAnonymousPropertyKey, PropertyChangeEventArgs<Settings> paramAnonymousPropertyChangeEventArgs)
      {
        ResolutionManagerImpl.-wrap0(ResolutionManagerImpl.this, false, false);
      }
    });
    if ((localCameraActivity.get(CameraActivity.PROP_CAMERA) != null) && (this.m_Viewfinder != null)) {
      selectResolutions(false, false);
    }
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_PHOTO_RESOLUTION) {
      return setPhotoResolutionProp((Resolution)paramTValue);
    }
    if (paramPropertyKey == PROP_VIDEO_RESOLUTION) {
      return setVideoResolutionProp((Resolution)paramTValue);
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  public Handle setResolutionSelector(ResolutionSelector paramResolutionSelector, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setResolutionSelector() - Component is not running");
      return null;
    }
    if (paramResolutionSelector == null)
    {
      Log.e(this.TAG, "setResolutionSelector() - No resolution selector");
      return null;
    }
    boolean bool2 = false;
    boolean bool3 = false;
    int i = 0;
    ResolutionSelectorHandle localResolutionSelectorHandle = new ResolutionSelectorHandle(paramResolutionSelector);
    Log.v(this.TAG, "setResolutionSelector() - Selector : ", paramResolutionSelector, ", handle : ", localResolutionSelectorHandle);
    if ((paramResolutionSelector instanceof PhotoResolutionSelector))
    {
      i = 1;
      if ((paramInt & 0x1) != 0)
      {
        bool1 = true;
        this.m_PhotoResSelectorHandles.add(localResolutionSelectorHandle);
        bool2 = bool1;
      }
    }
    else
    {
      bool1 = bool3;
      if ((paramResolutionSelector instanceof VideoResolutionSelector))
      {
        i = 1;
        if ((paramInt & 0x1) == 0) {
          break label159;
        }
      }
    }
    label159:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.m_VideoResSelectorHandles.add(localResolutionSelectorHandle);
      if (i != 0) {
        break label165;
      }
      Log.e(this.TAG, "setResolutionSelector() - Unknown selector type");
      return null;
      bool1 = false;
      break;
    }
    label165:
    selectResolutions(bool2, bool1);
    return localResolutionSelectorHandle;
  }
  
  private final class ResolutionSelectorHandle
    extends Handle
  {
    public final ResolutionSelector selector;
    
    public ResolutionSelectorHandle(ResolutionSelector paramResolutionSelector)
    {
      super();
      this.selector = paramResolutionSelector;
    }
    
    protected void onClose(int paramInt)
    {
      ResolutionManagerImpl.-wrap1(ResolutionManagerImpl.this, this, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/ResolutionManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */