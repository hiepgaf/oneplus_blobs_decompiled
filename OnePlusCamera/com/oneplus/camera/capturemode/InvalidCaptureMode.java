package com.oneplus.camera.capturemode;

import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import com.oneplus.base.Settings;
import com.oneplus.camera.InvalidMode;
import com.oneplus.camera.media.MediaType;

class InvalidCaptureMode
  extends InvalidMode<CaptureMode>
  implements CaptureMode
{
  public MediaType getCaptureModeMediaType()
  {
    return null;
  }
  
  public Settings getCustomSettings()
  {
    return null;
  }
  
  public String getDisplayName()
  {
    return null;
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    return null;
  }
  
  public ShortcutInfo getShortcutInfo()
  {
    return null;
  }
  
  public boolean isSimpleCaptureMode()
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/InvalidCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */