package com.oneplus.camera.capturemode;

import android.content.pm.ShortcutInfo;
import com.oneplus.base.Settings;
import com.oneplus.camera.BasicMode;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.Mode.State;
import com.oneplus.camera.media.MediaType;

public abstract class BasicCaptureMode
  extends BasicMode<CaptureMode>
  implements CaptureMode
{
  private Settings m_CustomSettings;
  private final String m_CustomSettingsName;
  private boolean m_IsCustomSettingsReady;
  private MediaType m_MediaType;
  
  protected BasicCaptureMode(CameraActivity paramCameraActivity, String paramString1, String paramString2, MediaType paramMediaType)
  {
    super(paramCameraActivity, paramString1);
    this.m_CustomSettingsName = paramString2;
    this.m_MediaType = paramMediaType;
  }
  
  public MediaType getCaptureModeMediaType()
  {
    return this.m_MediaType;
  }
  
  public Settings getCustomSettings()
  {
    verifyAccess();
    if ((!this.m_IsCustomSettingsReady) && (get(PROP_STATE) != Mode.State.RELEASED))
    {
      this.m_CustomSettings = onCreateCustomSettings(this.m_CustomSettingsName);
      this.m_IsCustomSettingsReady = true;
    }
    return this.m_CustomSettings;
  }
  
  public ShortcutInfo getShortcutInfo()
  {
    return null;
  }
  
  public boolean isSimpleCaptureMode()
  {
    return false;
  }
  
  protected Settings onCreateCustomSettings(String paramString)
  {
    if (paramString != null) {
      return new Settings(getCameraActivity(), paramString, false);
    }
    return null;
  }
  
  protected void onRelease()
  {
    if (this.m_CustomSettings != null)
    {
      this.m_CustomSettings.release();
      this.m_CustomSettings = null;
    }
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/BasicCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */