package com.oneplus.camera.bokeh;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.IntentEventArgs;
import com.oneplus.camera.Mode.State;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.capturemode.CaptureMode.ImageUsage;
import com.oneplus.camera.capturemode.ComponentBasedCaptureMode;
import com.oneplus.camera.media.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BokehCaptureMode
  extends ComponentBasedCaptureMode<BokehUI>
{
  private static final String ID = "Bokeh";
  private ShortcutInfo m_ShortcutInfo;
  
  BokehCaptureMode(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "Bokeh", "bokeh", BokehUI.class, MediaType.PHOTO);
    setReadOnly(PROP_TARGET_CAMERA_LENS_FACING, Camera.LensFacing.BACK);
    paramCameraActivity.addCallback(CameraActivity.PROP_AVAILABLE_CAMERAS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera>> paramAnonymousPropertyChangeEventArgs)
      {
        paramAnonymousPropertySource = (List)paramAnonymousPropertyChangeEventArgs.getNewValue();
        int i = paramAnonymousPropertySource.size() - 1;
        while (i >= 0)
        {
          paramAnonymousPropertyKey = (Camera)paramAnonymousPropertySource.get(i);
          if (paramAnonymousPropertyKey.get(Camera.PROP_LENS_FACING) != Camera.LensFacing.BACK) {
            i -= 1;
          } else if (!((Boolean)paramAnonymousPropertyKey.get(Camera.PROP_IS_BOKEH_SUPPORTED)).booleanValue()) {
            BokehCaptureMode.-wrap1(BokehCaptureMode.this);
          }
        }
      }
    });
    paramCameraActivity.addHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<IntentEventArgs> paramAnonymousEventKey, IntentEventArgs paramAnonymousIntentEventArgs)
      {
        if (BokehCaptureMode.this.get(BokehCaptureMode.PROP_STATE) != Mode.State.DISABLED)
        {
          paramAnonymousEventSource = BokehCaptureMode.-wrap0(BokehCaptureMode.this);
          if ((paramAnonymousEventSource != null) && (((Boolean)paramAnonymousEventSource.get(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED)).booleanValue())) {
            paramAnonymousIntentEventArgs.getIntent().putExtra("IsBokehOriginalPictureSupported", true);
          }
        }
      }
    });
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558470);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837536);
    }
    return getCameraActivity().getDrawable(2130837546);
  }
  
  public ShortcutInfo getShortcutInfo()
  {
    if (this.m_ShortcutInfo != null) {
      return this.m_ShortcutInfo;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    Intent localIntent = new Intent("com.oneplus.camera.action.LAUNCH_IN_BOKEH");
    localIntent.setClass(localCameraActivity, OPCameraActivity.class);
    HashSet localHashSet = new HashSet();
    localHashSet.add("android.shortcut.conversation");
    PersistableBundle localPersistableBundle = new PersistableBundle();
    localPersistableBundle.putInt("LongLabelResId", 2131558627);
    localPersistableBundle.putInt("ShortLabelResId", 2131558626);
    this.m_ShortcutInfo = new ShortcutInfo.Builder(localCameraActivity, "Bokeh").setShortLabel(localCameraActivity.getString(2131558626)).setLongLabel(localCameraActivity.getString(2131558627)).setIcon(Icon.createWithResource(localCameraActivity, 2130838117)).setIntent(localIntent).setCategories(localHashSet).setExtras(localPersistableBundle).build();
    return this.m_ShortcutInfo;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */