package com.oneplus.camera.watermark;

import android.graphics.Bitmap;
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
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.CaptureHandle;
import com.oneplus.camera.PictureProcessService;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.bokeh.BokehCaptureMode;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.panorama.PanoramaCaptureMode;
import com.oneplus.io.FileUtils;
import java.util.HashMap;
import java.util.Map;

public class WatermarkUI
  extends UIComponent
{
  public static final PropertyKey<Watermark> PROP_WATERMARK = new PropertyKey("Watermark", Watermark.class, WatermarkUI.class, Watermark.NONE);
  public static final String SETTINGS_KEY_IS_SLOGAN_AUTHOR_ENABLED = "Watermark.Slogan.Author.Enabled";
  public static final String SETTINGS_KEY_SLOGAN_AUTHOR = "Watermark.Slogan.Author";
  public static final String SETTINGS_KEY_WATERMARK = "Watermark";
  private CaptureModeManager m_CaptureModeManager;
  private final Map<CaptureHandle, ExcludingCaptureHandle> m_ExcludingCaptureHandles = new HashMap();
  private boolean m_IsSloganAuthorNameEnabled;
  private boolean m_IsUpdateOnlineWatermarkScheduled;
  private OnlineWatermarkController m_OnlineWatermarkController;
  private PictureProcessService m_PictureProcessService;
  private String m_SloganAuthorName;
  private final Runnable m_UpdateOnlineWatermarkEnableStateRunnable = new Runnable()
  {
    public void run()
    {
      WatermarkUI.-set0(WatermarkUI.this, false);
      WatermarkUI.-wrap3(WatermarkUI.this);
    }
  };
  
  WatermarkUI(CameraActivity paramCameraActivity)
  {
    super("Watermark UI", paramCameraActivity, true);
  }
  
  private boolean isOfflineWatermarkNeeded(CaptureHandle paramCaptureHandle, String paramString)
  {
    if ((paramCaptureHandle.getCamera() == null) || (paramCaptureHandle.getCamera().get(Camera.PROP_LENS_FACING) != Camera.LensFacing.BACK)) {
      return false;
    }
    if (paramCaptureHandle.getMediaType() != MediaType.PHOTO) {
      return false;
    }
    if (isOnlineWatermarkControllerSupported()) {
      return false;
    }
    if (paramCaptureHandle.isVideoSnapshot()) {
      return false;
    }
    if (FileUtils.isRawFilePath(paramString)) {
      return false;
    }
    if (this.m_ExcludingCaptureHandles.containsKey(paramCaptureHandle))
    {
      Log.d(this.TAG, "isWatermarkNeeded() - Capture : " + paramCaptureHandle + " is excluded");
      return false;
    }
    if (getCameraActivity().isServiceMode()) {
      return false;
    }
    updateWatermarkFromSettings();
    paramCaptureHandle = (Watermark)get(PROP_WATERMARK);
    switch (-getcom-oneplus-camera-watermark-WatermarkSwitchesValues()[paramCaptureHandle.ordinal()])
    {
    default: 
      return false;
    }
    return true;
  }
  
  private boolean isOnlineWatermarkControllerSupported()
  {
    return (this.m_OnlineWatermarkController != null) && (((Boolean)this.m_OnlineWatermarkController.get(OnlineWatermarkController.PROP_IS_SUPPORTED)).booleanValue());
  }
  
  private boolean linkToPictureProcessService()
  {
    if (this.m_PictureProcessService != null) {
      return true;
    }
    this.m_PictureProcessService = ((PictureProcessService)findComponent(PictureProcessService.class));
    return this.m_PictureProcessService != null;
  }
  
  private void onMediaSaved(CaptureHandle paramCaptureHandle, String paramString1, String paramString2)
  {
    if (!isOfflineWatermarkNeeded(paramCaptureHandle, paramString2)) {
      return;
    }
    if (!linkToPictureProcessService()) {
      return;
    }
    paramCaptureHandle = (Watermark)get(PROP_WATERMARK);
    switch (-getcom-oneplus-camera-watermark-WatermarkSwitchesValues()[paramCaptureHandle.ordinal()])
    {
    default: 
      return;
    }
    this.m_PictureProcessService.scheduleProcessWatermark(paramString1, paramString2, paramCaptureHandle, null, this.m_SloganAuthorName);
  }
  
  private void onOnlineWatermarkControllerFound(OnlineWatermarkControllerImpl paramOnlineWatermarkControllerImpl)
  {
    if (paramOnlineWatermarkControllerImpl == null) {
      return;
    }
    this.m_OnlineWatermarkController = paramOnlineWatermarkControllerImpl;
    HandlerUtils.post(this.m_OnlineWatermarkController, new Runnable()
    {
      public void run()
      {
        WatermarkUI.-get2(WatermarkUI.this).addCallback(OnlineWatermarkControllerImpl.PROP_IS_SUPPORTED, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<Boolean> paramAnonymous2PropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymous2PropertyChangeEventArgs)
          {
            WatermarkUI.-wrap4(WatermarkUI.this);
            WatermarkUI.-wrap3(WatermarkUI.this);
          }
        });
      }
    });
    updateOnlineWatermark();
    updateOnlineWatermarkEnableState();
  }
  
  private void updateOnlineWatermark()
  {
    if (!isOnlineWatermarkControllerSupported()) {
      return;
    }
    Log.v(this.TAG, "updateOnlineWatermark()");
    OnlineWatermarkController localOnlineWatermarkController = this.m_OnlineWatermarkController;
    Watermark localWatermark = (Watermark)get(PROP_WATERMARK);
    if (this.m_IsSloganAuthorNameEnabled) {}
    for (String str = this.m_SloganAuthorName;; str = null)
    {
      localOnlineWatermarkController.setWatermark(localWatermark, str);
      return;
    }
  }
  
  private void updateOnlineWatermarkEnableState()
  {
    if (!isOnlineWatermarkControllerSupported()) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    Camera localCamera = getCamera();
    if ((this.m_CaptureModeManager == null) || ((this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE) instanceof PanoramaCaptureMode))) {}
    while ((getMediaType() != MediaType.PHOTO) || (localCamera == null) || (localCamera.get(Camera.PROP_LENS_FACING) != Camera.LensFacing.BACK) || (localCameraActivity.isServiceMode()) || (get(PROP_WATERMARK) == Watermark.NONE))
    {
      Log.v(this.TAG, "updateOnlineWatermarkEnableState() - Exit");
      this.m_OnlineWatermarkController.exit(0);
      return;
    }
    int i = 0;
    if (((CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE) instanceof BokehCaptureMode)) {
      i = 1;
    }
    Log.v(this.TAG, "updateOnlineWatermarkEnableState() - Enter : ", Integer.valueOf(i));
    this.m_OnlineWatermarkController.enter(i);
  }
  
  private void updateWatermarkFromSettings()
  {
    Watermark localWatermark = (Watermark)getSettings().getEnum("Watermark", Watermark.class, Watermark.NONE);
    switch (-getcom-oneplus-camera-watermark-WatermarkSwitchesValues()[localWatermark.ordinal()])
    {
    }
    for (;;)
    {
      setReadOnly(PROP_WATERMARK, localWatermark);
      return;
      this.m_IsSloganAuthorNameEnabled = getSettings().getBoolean("Watermark.Slogan.Author.Enabled", false);
      if (this.m_IsSloganAuthorNameEnabled) {
        this.m_SloganAuthorName = getSettings().getString("Watermark.Slogan.Author", "");
      } else {
        this.m_SloganAuthorName = null;
      }
    }
  }
  
  public Bitmap applyWatermarkIfNeeded(Bitmap paramBitmap, CaptureHandle paramCaptureHandle)
  {
    if (paramBitmap == null) {
      return null;
    }
    if (!isOfflineWatermarkNeeded(paramCaptureHandle, null)) {
      return paramBitmap;
    }
    paramCaptureHandle = paramBitmap;
    if (!paramBitmap.isMutable()) {
      paramCaptureHandle = paramBitmap.copy(paramBitmap.getConfig(), true);
    }
    paramBitmap = (Watermark)get(PROP_WATERMARK);
    switch (-getcom-oneplus-camera-watermark-WatermarkSwitchesValues()[paramBitmap.ordinal()])
    {
    default: 
      return paramCaptureHandle;
    }
    paramBitmap = new SloganWatermarkDrawable();
    paramBitmap.setSubtitleText(this.m_SloganAuthorName);
    paramBitmap.apply(paramCaptureHandle);
    return paramCaptureHandle;
  }
  
  public Handle excludeCapture(CaptureHandle paramCaptureHandle)
  {
    verifyAccess();
    if (!isRunningOrInitializing()) {
      return null;
    }
    if (paramCaptureHandle == null) {
      return null;
    }
    Log.d(this.TAG, "excludeCapture() - Capture handle : " + paramCaptureHandle);
    ExcludingCaptureHandle localExcludingCaptureHandle1 = new ExcludingCaptureHandle(paramCaptureHandle);
    ExcludingCaptureHandle localExcludingCaptureHandle2 = (ExcludingCaptureHandle)this.m_ExcludingCaptureHandles.put(paramCaptureHandle, localExcludingCaptureHandle1);
    if (localExcludingCaptureHandle2 != null)
    {
      this.m_ExcludingCaptureHandles.put(paramCaptureHandle, localExcludingCaptureHandle2);
      return localExcludingCaptureHandle2;
    }
    return localExcludingCaptureHandle1;
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    ComponentUtils.findComponent(getCameraThread(), OnlineWatermarkControllerImpl.class, this, new ComponentSearchCallback()
    {
      public void onComponentFound(OnlineWatermarkControllerImpl paramAnonymousOnlineWatermarkControllerImpl)
      {
        WatermarkUI.-wrap2(WatermarkUI.this, paramAnonymousOnlineWatermarkControllerImpl);
      }
    });
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    updateWatermarkFromSettings();
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING)
        {
          WatermarkUI.-wrap5(WatermarkUI.this);
          WatermarkUI.-wrap4(WatermarkUI.this);
          WatermarkUI.-wrap3(WatermarkUI.this);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!WatermarkUI.-wrap0(WatermarkUI.this)) {
          return;
        }
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          WatermarkUI.-get2(WatermarkUI.this).exit(0);
        }
        while (WatermarkUI.-get1(WatermarkUI.this)) {
          return;
        }
        WatermarkUI.-set0(WatermarkUI.this, true);
        HandlerUtils.post(WatermarkUI.this, WatermarkUI.-get3(WatermarkUI.this));
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
      {
        WatermarkUI.-wrap1(WatermarkUI.this, paramAnonymousMediaEventArgs.getCaptureHandle(), paramAnonymousMediaEventArgs.getPictureId(), paramAnonymousMediaEventArgs.getFilePath());
      }
    });
    localCameraActivity.addHandler(CameraActivity.EVENT_CAPTURE_STARTED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        WatermarkUI.-get0(WatermarkUI.this).clear();
      }
    });
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_IS_CAPTURE_MODE_SWITCHING, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          if (!WatermarkUI.-wrap0(WatermarkUI.this)) {
            return;
          }
          if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
            WatermarkUI.-get2(WatermarkUI.this).exit(0);
          }
          while (WatermarkUI.-get1(WatermarkUI.this)) {
            return;
          }
          WatermarkUI.-set0(WatermarkUI.this, true);
          HandlerUtils.post(WatermarkUI.this, WatermarkUI.-get3(WatermarkUI.this));
        }
      });
    }
    for (;;)
    {
      updateOnlineWatermark();
      updateOnlineWatermarkEnableState();
      return;
      Log.e(this.TAG, "onInitialize() - No capture mode manager");
    }
  }
  
  private final class ExcludingCaptureHandle
    extends Handle
  {
    public final CaptureHandle captureHandle;
    
    public ExcludingCaptureHandle(CaptureHandle paramCaptureHandle)
    {
      super();
      this.captureHandle = paramCaptureHandle;
    }
    
    protected void onClose(int paramInt)
    {
      WatermarkUI.-wrap6(WatermarkUI.this);
      WatermarkUI.-get0(WatermarkUI.this).remove(this.captureHandle);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/WatermarkUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */