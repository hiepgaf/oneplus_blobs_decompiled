package com.oneplus.camera.media;

import com.oneplus.base.Log;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.util.AspectRatio;
import java.util.List;

public class DefaultPhotoResolutionSelector
  extends DefaultResolutionSelector
  implements PhotoResolutionSelector
{
  private static final AspectRatio[] ASPECT_RATIOS = { AspectRatio.RATIO_4x3, AspectRatio.RATIO_16x9, AspectRatio.RATIO_1x1 };
  public static final String SETTINGS_KEY_RESOLUTION_BACK = "Resolution.Photo.Back";
  public static final String SETTINGS_KEY_RESOLUTION_FRONT = "Resolution.Photo.Front";
  
  static
  {
    Settings.addPrivateKey("Resolution.Photo.Back");
    Settings.addPrivateKey("Resolution.Photo.Front");
  }
  
  protected DefaultPhotoResolutionSelector(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity);
  }
  
  public String getResolutionSettingsKey(Camera paramCamera, Settings paramSettings)
  {
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((com.oneplus.camera.Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "getResolutionSettingsKey() - Unknown camera lens facing : " + paramCamera.get(Camera.PROP_LENS_FACING));
      return null;
    case 1: 
      return "Resolution.Photo.Back";
    }
    return "Resolution.Photo.Front";
  }
  
  public void saveResolution(Camera paramCamera, Settings paramSettings, Resolution paramResolution)
  {
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((com.oneplus.camera.Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "saveResolution() - Unknown camera lens facing : " + paramCamera.get(Camera.PROP_LENS_FACING));
      return;
    case 1: 
      paramSettings.set("Resolution.Photo.Back", paramResolution.getKey());
      return;
    }
    paramSettings.set("Resolution.Photo.Front", paramResolution.getKey());
  }
  
  public Resolution selectResolution(Camera paramCamera, Settings paramSettings, List<Resolution> paramList, Resolution paramResolution, ResolutionSelector.Restriction paramRestriction)
  {
    paramRestriction = paramResolution;
    if (paramResolution == null) {}
    int i;
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((com.oneplus.camera.Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "selectResolution() - Unknown camera lens facing : " + paramCamera.get(Camera.PROP_LENS_FACING));
      paramRestriction = paramResolution;
      if (paramRestriction != null) {
        i = paramList.size() - 1;
      }
      break;
    case 1: 
    case 2: 
      for (;;)
      {
        if (i < 0) {
          break label174;
        }
        paramCamera = (Resolution)paramList.get(i);
        if ((paramCamera != null) && (paramCamera.equals(paramRestriction)))
        {
          return paramCamera;
          paramRestriction = Resolution.fromKey(paramSettings.getString("Resolution.Photo.Back"));
          break;
          paramRestriction = Resolution.fromKey(paramSettings.getString("Resolution.Photo.Front"));
          break;
        }
        i -= 1;
      }
    }
    label174:
    if (paramList.size() > 0) {
      return (Resolution)paramList.get(0);
    }
    Log.e(this.TAG, "selectResolution() - Empty resolution list");
    return null;
  }
  
  public List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
  {
    return selectResolutions(paramCamera, paramSettings, ASPECT_RATIOS, 1, paramRestriction);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/DefaultPhotoResolutionSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */