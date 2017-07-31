package com.oneplus.camera;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.base.Device;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.bokeh.BokehCaptureMode;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.capturemode.PhotoCaptureMode;
import com.oneplus.camera.capturemode.VideoCaptureMode;
import com.oneplus.camera.manual.ManualCaptureMode;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.CameraGallery;
import com.oneplus.camera.ui.CameraGallery.GalleryState;
import com.oneplus.camera.ui.Viewfinder;
import com.oneplus.camera.util.StringUtils;
import java.util.Iterator;
import java.util.List;

public class OPCameraActivity
  extends CameraActivity
{
  private static final long DURATION_REMOVE_PREVIEW_BACKGROUND_DELAY = 1000L;
  public static final EventKey<IntentEventArgs> EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE;
  static final String EXTRA_AUTO_TEST_SERVICE_ID = "com.oneplus.camera.OPCameraActivity.AutoTestServiceId";
  private static final int MSG_REMOVE_PREVIEW_BACKGROUND = 10010;
  public static final PropertyKey<Boolean> PROP_IS_CAPTURE_UI_INFLATED = new PropertyKey("IsCaptureUIInflated", Boolean.class, OPCameraActivity.class, Boolean.valueOf(false));
  private static final int REQUEST_CODE_ADV_SETTINGS = 1000;
  private static final String SETTINGS_KEY_H2_PREMISSIONS_REQUESTED = "IsH2PermissionsRequested";
  private AutoTestService m_AutoTestService;
  private CameraGallery m_CameraGallery;
  private TextView m_CameraInfoView;
  private CaptureModeManager m_CaptureModeManager;
  private ViewGroup m_CaptureUIContainer;
  private PropertyChangedCallback<Camera> m_DebugModeCameraCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
    {
      OPCameraActivity.-wrap4(OPCameraActivity.this);
    }
  };
  private boolean m_IsFirstCameraPreviewReceived = true;
  private SurfaceView m_PreviewBackgroundSurfaceView;
  private SceneManager m_SceneManager;
  
  static
  {
    EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE = new EventKey("PrepareAdvancedSettingActivityExtraBundle", IntentEventArgs.class, CameraActivity.class);
    if (BuildFlags.ROM_VERSION == 1) {
      REQUIRED_PERMISSION_LIST.add("android.permission.READ_PHONE_STATE");
    }
  }
  
  public OPCameraActivity()
  {
    addComponentBuilders(ComponentBuilders.BUILDERS_MAIN_ACTIVITY);
  }
  
  private void bindAutoTestService(Intent paramIntent)
  {
    if ((paramIntent != null) && (paramIntent.hasExtra("com.oneplus.camera.OPCameraActivity.AutoTestServiceId")))
    {
      this.m_AutoTestService = AutoTestService.fromId(paramIntent.getIntExtra("com.oneplus.camera.OPCameraActivity.AutoTestServiceId", 0));
      if (this.m_AutoTestService != null) {
        Log.w(this.TAG, "bindAutoTestService() - Bind auto-test service : " + this.m_AutoTestService);
      }
    }
    else
    {
      return;
    }
    Log.w(this.TAG, "bindAutoTestService() - Auto-test service not found");
  }
  
  private boolean linkToCaptureModeManager()
  {
    if (this.m_CaptureModeManager == null)
    {
      this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
      if (this.m_CaptureModeManager == null) {
        return false;
      }
    }
    return true;
  }
  
  private void onBackFromAdvancedSettings(int paramInt, Intent paramIntent)
  {
    Log.v(this.TAG, "onBackFromAdvancedSettings()");
    paramIntent = getResolutionManager();
    if (paramIntent != null)
    {
      Object localObject = paramIntent.getPhotoResolutionSettingsKey();
      if (localObject != null)
      {
        localObject = Resolution.fromKey(((Settings)get(PROP_SETTINGS)).getString((String)localObject));
        if (localObject == null) {
          break label75;
        }
        Log.v(this.TAG, "onBackFromAdvancedSettings() - Selected photo resolution : ", localObject);
        paramIntent.set(ResolutionManager.PROP_PHOTO_RESOLUTION, localObject);
      }
    }
    return;
    label75:
    Log.w(this.TAG, "onBackFromAdvancedSettings() - No selected photo resolution");
  }
  
  private void onCameraGalleryStateChanged(CameraGallery.GalleryState paramGalleryState)
  {
    switch (-getcom-oneplus-camera-ui-CameraGallery$GalleryStateSwitchesValues()[paramGalleryState.ordinal()])
    {
    default: 
      disableBeam();
      return;
    }
    enableBeam();
  }
  
  private void onCameraPreviewReceived()
  {
    HandlerUtils.sendMessage(this, 10010, true, 1000L);
    if (this.m_IsFirstCameraPreviewReceived)
    {
      this.m_IsFirstCameraPreviewReceived = false;
      if (this.m_AutoTestService != null)
      {
        this.m_AutoTestService.notifyActivityReady(this);
        this.m_AutoTestService = null;
      }
    }
  }
  
  private void onElapsedRecordingTimeChanged(long paramLong)
  {
    if ((paramLong > 0L) && (((Long)get(PROP_MAX_VIDEO_DURATION_SECONDS)).longValue() >= 0L) && (paramLong == 1L)) {
      showMaxVideoDurationMessage();
    }
  }
  
  private void onLaunchCompleted()
  {
    Log.v(this.TAG, "onLaunchCompleted() - Inflate capture UI [start]");
    this.m_CaptureUIContainer = ((ViewGroup)((ViewStub)findViewById(2131361828)).inflate());
    Log.v(this.TAG, "onLaunchCompleted() - Inflate capture UI [end]");
    setReadOnly(PROP_IS_CAPTURE_UI_INFLATED, Boolean.valueOf(true));
  }
  
  private Intent prepareAgentActivityIntent(Intent paramIntent, int paramInt)
  {
    Intent localIntent = new Intent(paramIntent);
    localIntent.putExtra("com.oneplus.camera.agent.intent.extra.AGENT_TYPE", paramInt);
    paramIntent = paramIntent.getComponent();
    if (paramIntent != null) {
      localIntent.putExtra("com.oneplus.camera.agent.intent.extra.COMPONENT", paramIntent);
    }
    switch (-getcom-oneplus-base-RotationSwitchesValues()[((Rotation)get(PROP_ROTATION)).ordinal()])
    {
    default: 
      localIntent.setClass(getApplicationContext(), AgentActivity.class);
      return localIntent;
    case 3: 
      localIntent.setClass(getApplicationContext(), LandscapeAgentActivity.class);
      return localIntent;
    case 2: 
      localIntent.setClass(getApplicationContext(), InversePortraitAgentActivity.class);
      return localIntent;
    }
    localIntent.setClass(getApplicationContext(), InverseLandscapeAgentActivity.class);
    return localIntent;
  }
  
  private void setInitCaptureMode()
  {
    if (!linkToCaptureModeManager()) {
      return;
    }
    int j = 0;
    Object localObject = getStartMode();
    int i;
    switch (-getcom-oneplus-camera-StartModeSwitchesValues()[localObject.ordinal()])
    {
    default: 
      localObject = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
      i = j;
      if (!((Boolean)get(PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue())
      {
        if (!(localObject instanceof ManualCaptureMode)) {
          break label289;
        }
        i = j;
      }
      break;
    }
    while (i != 0)
    {
      localObject = this.m_CaptureModeManager.findCaptureMode(PhotoCaptureMode.class);
      if (localObject == null) {
        break;
      }
      this.m_CaptureModeManager.setCaptureMode((CaptureMode)localObject, 0);
      return;
      i = 1;
      continue;
      localObject = this.m_CaptureModeManager.findCaptureMode(VideoCaptureMode.class);
      if (localObject != null)
      {
        this.m_CaptureModeManager.setCaptureMode((CaptureMode)localObject, 0);
        return;
      }
      Log.w(this.TAG, "setInitCaptureMode() - Cannot find video capture mode");
      i = j;
      continue;
      localObject = this.m_CaptureModeManager.findCaptureMode(BokehCaptureMode.class);
      if (localObject != null)
      {
        this.m_CaptureModeManager.setCaptureMode((CaptureMode)localObject, 0);
        return;
      }
      Log.w(this.TAG, "setInitCaptureMode() - Cannot find bokeh capture mode");
      i = j;
      continue;
      localObject = this.m_CaptureModeManager.findCaptureMode(ManualCaptureMode.class);
      if (localObject != null)
      {
        this.m_CaptureModeManager.setCaptureMode((CaptureMode)localObject, 0);
        return;
      }
      Log.w(this.TAG, "setInitCaptureMode() - Cannot find manual capture mode");
      i = j;
      continue;
      label289:
      i = 1;
    }
    this.m_CaptureModeManager.changeToInitialCaptureMode(0);
  }
  
  private void showMaxVideoDurationMessage()
  {
    Object localObject = (Resolution)getResolutionManager().get(ResolutionManager.PROP_VIDEO_RESOLUTION);
    long l = ((Long)get(PROP_MAX_VIDEO_DURATION_SECONDS)).longValue();
    if ((localObject != null) && (l >= 0L))
    {
      if (!((Resolution)localObject).is4kVideo()) {
        break label86;
      }
      localObject = getString(2131558506);
    }
    for (;;)
    {
      showToast(String.format(getString(2131558519), new Object[] { localObject, StringUtils.formatTime(this, l) }));
      return;
      label86:
      if (((Resolution)localObject).is1080pVideo())
      {
        localObject = getString(2131558505);
      }
      else
      {
        if (!((Resolution)localObject).is720pVideo()) {
          break;
        }
        localObject = getString(2131558507);
      }
    }
  }
  
  private void showPreviewBackground()
  {
    HandlerUtils.removeMessages(this, 10010);
    if ((this.m_PreviewBackgroundSurfaceView != null) && (this.m_PreviewBackgroundSurfaceView.getVisibility() != 0))
    {
      Log.v(this.TAG, "showPreviewBackground()");
      this.m_PreviewBackgroundSurfaceView.setVisibility(0);
    }
  }
  
  private void updateCameraInfoText()
  {
    if ((((Boolean)get(PROP_IS_CAPTURE_UI_INFLATED)).booleanValue()) && (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue()))
    {
      if (this.m_CameraInfoView == null)
      {
        this.m_CameraInfoView = ((TextView)findViewById(2131361960));
        if (this.m_CameraInfoView != null) {}
      }
    }
    else
    {
      if (this.m_CameraInfoView != null) {
        this.m_CameraInfoView.setVisibility(8);
      }
      return;
    }
    Object localObject = (Camera)get(PROP_CAMERA);
    if (localObject == null) {
      return;
    }
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((Camera.LensFacing)localObject.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      return;
    case 1: 
      localObject = (List)get(PROP_AVAILABLE_CAMERAS);
      if ((CameraUtils.findCamera((List)localObject, Camera.LensFacing.BACK_WIDE, false) != null) && (CameraUtils.findCamera((List)localObject, Camera.LensFacing.BACK_TELE, false) != null)) {
        localObject = getString(2131558589);
      }
      break;
    }
    for (;;)
    {
      this.m_CameraInfoView.setText(getString(2131558587, new Object[] { localObject }));
      this.m_CameraInfoView.setVisibility(0);
      return;
      localObject = getString(2131558588);
      continue;
      localObject = getString(2131558590);
      continue;
      localObject = getString(2131558591);
      continue;
      localObject = getString(2131558592);
    }
  }
  
  protected Uri[] getBeamUris()
  {
    if (this.m_CameraGallery == null) {
      return null;
    }
    CameraGallery.GalleryState localGalleryState = (CameraGallery.GalleryState)this.m_CameraGallery.get(CameraGallery.PROP_GALLERY_STATE);
    switch (-getcom-oneplus-camera-ui-CameraGallery$GalleryStateSwitchesValues()[localGalleryState.ordinal()])
    {
    default: 
      return null;
    }
    return new Uri[] { (Uri)this.m_CameraGallery.get(CameraGallery.PROP_CURRENT_CONTENT_URI) };
  }
  
  public final ViewGroup getCaptureUIContainer()
  {
    return this.m_CaptureUIContainer;
  }
  
  public final SceneManager getSceneManager()
  {
    return this.m_SceneManager;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    }
    do
    {
      return;
    } while ((this.m_PreviewBackgroundSurfaceView == null) || (this.m_PreviewBackgroundSurfaceView.getVisibility() != 0));
    Log.v(this.TAG, "handleMessage() - Remove preview background");
    this.m_PreviewBackgroundSurfaceView.setVisibility(4);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    }
    for (;;)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      return;
      onBackFromAdvancedSettings(paramInt2, paramIntent);
    }
  }
  
  protected void onCameraOpenFailedError(CameraOpenFailedEventArgs paramCameraOpenFailedEventArgs)
  {
    int i;
    if (get(PROP_CAMERA) == paramCameraOpenFailedEventArgs.getCamera()) {
      switch (paramCameraOpenFailedEventArgs.getErrorCode())
      {
      case -1: 
      case 0: 
      default: 
        i = 2131558528;
      }
    }
    for (;;)
    {
      Toast.makeText(this, i, 1).show();
      super.onCameraOpenFailedError(paramCameraOpenFailedEventArgs);
      return;
      i = 2131558523;
      continue;
      i = 2131558524;
      continue;
      i = 2131558526;
      continue;
      i = 2131558527;
      continue;
      i = 2131558529;
      continue;
      i = 2131558525;
    }
  }
  
  protected void onCaptureCompleted(CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason)
  {
    super.onCaptureCompleted(paramCaptureHandle, paramCaptureCompleteReason);
    switch (-getcom-oneplus-camera-CaptureCompleteReasonSwitchesValues()[paramCaptureCompleteReason.ordinal()])
    {
    }
    do
    {
      do
      {
        do
        {
          return;
        } while (paramCaptureHandle.getMediaType() != MediaType.VIDEO);
        showToast(2131558520);
        return;
        showMaxVideoDurationMessage();
        return;
        showToast(2131558530);
        return;
        showToast(2131558531);
        return;
      } while (paramCaptureHandle.getMediaType() != MediaType.VIDEO);
      showToast(2131558522);
      return;
    } while (paramCaptureHandle.getMediaType() != MediaType.VIDEO);
    showToast(2131558521);
  }
  
  protected void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (((Boolean)get(PROP_IS_MULTI_WINDOW_MODE)).booleanValue())
    {
      Toast.makeText(this, 2131558532, 1).show();
      finish();
      return;
    }
    OPCameraApplication.notifyInstanceCreated(this);
    bindAutoTestService(getIntent());
    addCallback(PROP_IS_LAUNCHING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          OPCameraActivity.-wrap3(OPCameraActivity.this);
          if (((Boolean)OPCameraActivity.this.get(OPCameraActivity.PROP_IS_DEBUG_MODE)).booleanValue()) {
            OPCameraActivity.-wrap4(OPCameraActivity.this);
          }
        }
      }
    });
    if (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue()) {
      onDebugModeEnabled();
    }
    addCallback(PROP_IS_DEBUG_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          OPCameraActivity.this.onDebugModeEnabled();
          return;
        }
        OPCameraActivity.this.onDebugModeDisabled();
      }
    });
    int i;
    int j;
    if (linkToCaptureModeManager())
    {
      i = 0;
      j = CaptureModeBuilders.BUILDERS.length;
      while (i < j)
      {
        this.m_CaptureModeManager.addBuilder(CaptureModeBuilders.BUILDERS[i], 0);
        i += 1;
      }
      setInitCaptureMode();
    }
    for (;;)
    {
      paramBundle = new CameraThread.ResourceIdTable();
      localObject = getCameraThread();
      ((CameraThread)localObject).addComponentBuilders(ComponentBuilders.BUILDERS_CAMERA_THREAD);
      paramBundle.burstShutterSound = 2131165188;
      paramBundle.burstShutterSoundEnd = 2131165189;
      paramBundle.photoShutterSound = 2131165190;
      paramBundle.videoStartSound = 2131165187;
      paramBundle.videoStopSound = 2131165186;
      paramBundle.cameraTimerSound = 2131165184;
      paramBundle.cameraTimer2SecSound = 2131165185;
      ((CameraThread)localObject).setResourceIdTable(paramBundle);
      ((CameraThread)localObject).start((MediaType)get(PROP_MEDIA_TYPE));
      this.m_SceneManager = ((SceneManager)findComponent(SceneManager.class));
      if (this.m_SceneManager == null) {
        break;
      }
      i = 0;
      j = SceneBuilders.BUILDERS.length;
      while (i < j)
      {
        this.m_SceneManager.addBuilder(SceneBuilders.BUILDERS[i], 0);
        i += 1;
      }
      Log.e(this.TAG, "onCreate() - Cannot link to capture mode manager");
    }
    Log.e(this.TAG, "onCreate() - No SceneManager interface");
    addCallback(PROP_ELAPSED_RECORDING_SECONDS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Long> paramAnonymousPropertyKey, PropertyChangeEventArgs<Long> paramAnonymousPropertyChangeEventArgs)
      {
        OPCameraActivity.-wrap2(OPCameraActivity.this, ((Long)paramAnonymousPropertyChangeEventArgs.getNewValue()).longValue());
      }
    });
    addCallback(PROP_IS_CAMERA_PREVIEW_RECEIVED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          OPCameraActivity.-wrap1(OPCameraActivity.this);
        }
      }
    });
    paramBundle = getWindow();
    Object localObject = paramBundle.getAttributes();
    ((WindowManager.LayoutParams)localObject).rotationAnimation = 2;
    paramBundle.setAttributes((WindowManager.LayoutParams)localObject);
    this.m_StorageToast = 2131558595;
    this.m_StorageStopRecordToast = 2131558596;
    setContentView(2130903047);
    this.m_PreviewBackgroundSurfaceView = ((SurfaceView)findViewById(2131361827));
    if (this.m_PreviewBackgroundSurfaceView != null) {
      this.m_PreviewBackgroundSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
      {
        public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          Canvas localCanvas = paramAnonymousSurfaceHolder.lockCanvas();
          localCanvas.drawColor(-16777216);
          paramAnonymousSurfaceHolder.unlockCanvasAndPost(localCanvas);
        }
        
        public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder) {}
        
        public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder) {}
      });
    }
    ComponentUtils.findComponent(this, CameraGallery.class, this, new ComponentSearchCallback()
    {
      public void onComponentFound(CameraGallery paramAnonymousCameraGallery)
      {
        OPCameraActivity.-set0(OPCameraActivity.this, paramAnonymousCameraGallery);
        paramAnonymousCameraGallery.addCallback(CameraGallery.PROP_GALLERY_STATE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<CameraGallery.GalleryState> paramAnonymous2PropertyKey, PropertyChangeEventArgs<CameraGallery.GalleryState> paramAnonymous2PropertyChangeEventArgs)
          {
            OPCameraActivity.-wrap0(OPCameraActivity.this, (CameraGallery.GalleryState)paramAnonymous2PropertyChangeEventArgs.getNewValue());
          }
        });
      }
    });
    if ((!Device.isHydrogenOS()) || (((Settings)get(PROP_SETTINGS)).getBoolean("IsH2PermissionsRequested", false))) {
      return;
    }
    ((Settings)get(PROP_SETTINGS)).set("Location.Save", Boolean.valueOf(false));
    paramBundle = lockRotation(Rotation.PORTRAIT);
    new AlertDialog.Builder(this).setTitle(2131558492).setView(2130903049).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ((Settings)OPCameraActivity.this.get(OPCameraActivity.PROP_SETTINGS)).set("IsH2PermissionsRequested", Boolean.valueOf(true));
        ((Settings)OPCameraActivity.this.get(OPCameraActivity.PROP_SETTINGS)).set("Location.Save", Boolean.valueOf(true));
        paramAnonymousDialogInterface.dismiss();
      }
    }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
        OPCameraActivity.this.finishAndRemoveTask();
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        Handle.close(paramBundle);
      }
    }).create().show();
  }
  
  protected void onDebugModeDisabled()
  {
    removeCallback(PROP_CAMERA, this.m_DebugModeCameraCB);
    updateCameraInfoText();
  }
  
  protected void onDebugModeEnabled()
  {
    addCallback(PROP_CAMERA, this.m_DebugModeCameraCB);
    updateCameraInfoText();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    OPCameraApplication.notifyInstanceDestroyed(this);
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    this.m_IsFirstCameraPreviewReceived = true;
    super.onNewIntent(paramIntent);
    bindAutoTestService(paramIntent);
    setInitCaptureMode();
  }
  
  protected void onResume()
  {
    int j = getResources().getDimensionPixelSize(2131296581);
    int k = getResources().getDimensionPixelSize(2131296580);
    int i = j;
    if (k > 0) {
      i = j + k;
    }
    RectF localRectF = new RectF(0.0F, k, ((ScreenSize)get(PROP_SCREEN_SIZE)).getWidth(), i);
    getViewfinder().setPreferredPreviewBounds(localRectF, 0);
    super.onResume();
  }
  
  protected void onStop()
  {
    showPreviewBackground();
    super.onStop();
  }
  
  public boolean setDebugMode(boolean paramBoolean)
  {
    if (super.setDebugMode(paramBoolean))
    {
      if (paramBoolean) {
        showToast(2131558593);
      }
      for (;;)
      {
        return true;
        showToast(2131558594);
      }
    }
    return false;
  }
  
  public final boolean showAdvancedSettings()
  {
    try
    {
      Object localObject = (Settings)get(PROP_SETTINGS);
      Intent localIntent = new Intent(getApplicationContext(), AdvancedSettingsActivity.class);
      raise(EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, new IntentEventArgs(localIntent));
      localIntent.putExtra("Settings.Name", ((Settings)localObject).getName());
      localIntent.putExtra("Settings.IsVolatile", ((Settings)localObject).isVolatile());
      localIntent.putExtra("StartMode", getStartMode());
      localIntent.putExtra("IsServiceMode", isServiceMode());
      localObject = ((List)get(PROP_AVAILABLE_CAMERAS)).iterator();
      while (((Iterator)localObject).hasNext()) {
        if (((Boolean)((Camera)((Iterator)localObject).next()).get(Camera.PROP_IS_MIRROR_SUPPORTED)).booleanValue()) {
          localIntent.putExtra("IsMirrorSupported", true);
        }
      }
      localObject = getMediaResultInfo();
      if ((localObject != null) && (((MediaResultInfo)localObject).extraOutput != null)) {
        localIntent.putExtra("OutputUri", ((MediaResultInfo)localObject).extraOutput.toString());
      }
      startActivityForResult(localIntent, 1000);
      return true;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e(this.TAG, "showAdvancedSettings() - Fail to start activity", localActivityNotFoundException);
    }
    return false;
  }
  
  public void startActivityByAgent(Intent paramIntent)
  {
    startActivity(prepareAgentActivityIntent(paramIntent, 0));
  }
  
  public Handle startActivityForResultByAgent(Intent paramIntent, CameraActivity.ActivityResultCallback paramActivityResultCallback)
  {
    return startActivityForResult(prepareAgentActivityIntent(paramIntent, 1), paramActivityResultCallback);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/OPCameraActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */