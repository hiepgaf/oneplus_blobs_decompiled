package com.oneplus.camera.capturemode;

import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import com.oneplus.base.Settings;
import com.oneplus.camera.Mode;
import com.oneplus.camera.media.MediaType;

public abstract interface CaptureMode
  extends Mode<CaptureMode>
{
  public static final CaptureMode INVALID = new InvalidCaptureMode();
  
  public abstract MediaType getCaptureModeMediaType();
  
  public abstract Settings getCustomSettings();
  
  public abstract Drawable getImage(ImageUsage paramImageUsage);
  
  public abstract ShortcutInfo getShortcutInfo();
  
  public abstract boolean isSimpleCaptureMode();
  
  public static enum ImageUsage
  {
    CAPTURE_MODES_PANEL_ICON,  SWITCH_MODE_LARGE_ICON;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/CaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */