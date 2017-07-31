package com.oneplus.camera.manual;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.IntentEventArgs;
import com.oneplus.camera.Mode.State;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.capturemode.CaptureMode.ImageUsage;
import com.oneplus.camera.capturemode.ComponentBasedCaptureMode;
import com.oneplus.camera.media.MediaType;
import java.util.Iterator;
import java.util.List;

public final class ManualCaptureMode
  extends ComponentBasedCaptureMode<ManualModeUI>
{
  public static final boolean ENABLE_MANUAL_MODE = true;
  public static final String MANUAL_CAPTURE_MODE_SETTING_NAME = "manual";
  private ManualModeUI m_ManualModeUI;
  
  ManualCaptureMode(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "Manual", null, ManualModeUI.class, MediaType.PHOTO);
    setReadOnly(PROP_TARGET_CAMERA_LENS_FACING, Camera.LensFacing.BACK);
    paramCameraActivity.addCallback(CameraActivity.PROP_AVAILABLE_CAMERAS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera>> paramAnonymousPropertyChangeEventArgs)
      {
        paramAnonymousPropertySource = (List)paramAnonymousPropertyChangeEventArgs.getNewValue();
        int i = paramAnonymousPropertySource.size() - 1;
        if (i >= 0)
        {
          if ((Camera.LensFacing)((Camera)paramAnonymousPropertySource.get(i)).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {}
          for (;;)
          {
            i -= 1;
            break;
            if (!((Boolean)((Camera)paramAnonymousPropertySource.get(i)).get(Camera.PROP_IS_MANUAL_CONTROL_SUPPORTED)).booleanValue()) {
              ManualCaptureMode.-wrap0(ManualCaptureMode.this);
            }
          }
        }
      }
    });
    paramCameraActivity.addHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<IntentEventArgs> paramAnonymousEventKey, IntentEventArgs paramAnonymousIntentEventArgs)
      {
        if (ManualCaptureMode.this.get(ManualCaptureMode.PROP_STATE) != Mode.State.DISABLED)
        {
          paramAnonymousIntentEventArgs.getIntent().putExtra("IsManualCaptureOptionsVisible", true);
          paramAnonymousEventSource = ((List)ManualCaptureMode.this.getCameraActivity().get(CameraActivity.PROP_AVAILABLE_CAMERAS)).iterator();
          while (paramAnonymousEventSource.hasNext())
          {
            paramAnonymousEventKey = (Camera)paramAnonymousEventSource.next();
            if ((paramAnonymousEventKey.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) && (((Boolean)paramAnonymousEventKey.get(Camera.PROP_IS_ACTIVE_PICTURE_INFO_SUPPORTED)).booleanValue())) {
              paramAnonymousIntentEventArgs.getIntent().putExtra("IsActivePictureInfoOptionsVisible", true);
            }
          }
        }
      }
    });
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558471);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    }
    return getCameraActivity().getDrawable(2130837537);
  }
  
  protected Settings onCreateCustomSettings(String paramString)
  {
    if (this.m_ManualModeUI == null) {
      this.m_ManualModeUI = ((ManualModeUI)getCameraActivity().findComponent(ManualModeUI.class));
    }
    if (this.m_ManualModeUI != null) {
      return this.m_ManualModeUI.getManualSettings();
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ManualCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */