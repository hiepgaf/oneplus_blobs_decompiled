package com.oneplus.camera.media;

import android.util.Size;
import com.oneplus.base.Log;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultVideoResolutionSelector
  extends DefaultResolutionSelector
  implements VideoResolutionSelector
{
  private static final Comparator<Resolution> RESOLUTION_COMPARATOR = new Comparator()
  {
    public int compare(Resolution paramAnonymousResolution1, Resolution paramAnonymousResolution2)
    {
      int i = paramAnonymousResolution1.getWidth() * paramAnonymousResolution1.getHeight() - paramAnonymousResolution2.getWidth() * paramAnonymousResolution2.getHeight();
      if (i != 0) {
        return i;
      }
      i = paramAnonymousResolution1.getFps() - paramAnonymousResolution2.getFps();
      if (i != 0) {
        return i;
      }
      return paramAnonymousResolution1.hashCode() - paramAnonymousResolution2.hashCode();
    }
  };
  public static final String SETTINGS_KEY_RESOLUTION_BACK = "Resolution.Video.Back";
  public static final String SETTINGS_KEY_RESOLUTION_FRONT = "Resolution.Video.Front";
  
  static
  {
    Settings.addPrivateKey("Resolution.Video.Back");
    Settings.addPrivateKey("Resolution.Video.Front");
  }
  
  protected DefaultVideoResolutionSelector(CameraActivity paramCameraActivity)
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
      return "Resolution.Video.Back";
    }
    return "Resolution.Video.Front";
  }
  
  public void saveResolution(Camera paramCamera, Settings paramSettings, Resolution paramResolution)
  {
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((com.oneplus.camera.Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "saveResolution() - Unknown camera lens facing : " + paramCamera.get(Camera.PROP_LENS_FACING));
      return;
    case 1: 
      paramSettings.set("Resolution.Video.Back", paramResolution.getKey());
      return;
    }
    paramSettings.set("Resolution.Video.Front", paramResolution.getKey());
  }
  
  public Resolution selectResolution(Camera paramCamera, Settings paramSettings, List<Resolution> paramList, Resolution paramResolution, ResolutionSelector.Restriction paramRestriction)
  {
    paramRestriction = paramResolution;
    if (paramResolution == null) {}
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
          paramRestriction = Resolution.fromKey(paramSettings.getString("Resolution.Video.Back"));
          break;
          paramRestriction = Resolution.fromKey(paramSettings.getString("Resolution.Video.Front"));
          break;
        }
        i -= 1;
      }
    }
    label174:
    paramCamera = ((ScreenSize)getCameraActivity().get(CameraActivity.PROP_SCREEN_SIZE)).toSize();
    int n = paramCamera.getWidth();
    int i1 = paramCamera.getHeight();
    int j = 0;
    paramSettings = null;
    int i = paramList.size() - 1;
    if (i >= 0)
    {
      paramResolution = (Resolution)paramList.get(i);
      int m = Math.abs(n * i1 - paramResolution.getWidth() * paramResolution.getHeight());
      int k;
      if (paramSettings != null)
      {
        k = j;
        paramCamera = paramSettings;
        if (m > j) {}
      }
      else
      {
        if ((paramSettings != null) && (m >= j)) {
          break label308;
        }
        paramCamera = paramResolution;
      }
      for (;;)
      {
        k = m;
        i -= 1;
        j = k;
        paramSettings = paramCamera;
        break;
        label308:
        paramCamera = paramSettings;
        if (paramResolution.getFps() < paramSettings.getFps()) {
          paramCamera = paramResolution;
        }
      }
    }
    if (paramSettings != null) {
      return paramSettings;
    }
    Log.e(this.TAG, "selectResolution() - Empty resolution list");
    return null;
  }
  
  public List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, ResolutionSelector.Restriction paramRestriction)
  {
    List localList1 = (List)paramCamera.get(Camera.PROP_VIDEO_SIZES);
    List localList2 = (List)paramCamera.get(Camera.PROP_VIDEO_60FPS_SIZES);
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    int k = 0;
    paramSettings = null;
    paramCamera = null;
    int i = localList1.size() - 1;
    if (i >= 0)
    {
      Size localSize = (Size)localList1.get(i);
      Camera localCamera = paramCamera;
      Object localObject = paramSettings;
      int m = k;
      int n = j;
      int i1;
      int i2;
      if (ResolutionSelector.Restriction.match(paramRestriction, localSize))
      {
        i1 = localSize.getWidth();
        i2 = localSize.getHeight();
        if ((j != 0) || ((i1 != 4096) && (i1 != 3840)) || (i2 != 2160)) {
          break label199;
        }
        localArrayList.add(new Resolution(MediaType.VIDEO, i1, i2));
        n = 1;
        m = k;
        localObject = paramSettings;
        localCamera = paramCamera;
      }
      for (;;)
      {
        i -= 1;
        paramCamera = localCamera;
        paramSettings = (Settings)localObject;
        k = m;
        j = n;
        break;
        label199:
        if ((k == 0) && (i1 == 1920) && ((i2 == 1080) || (i2 == 1088)))
        {
          localObject = new Resolution(MediaType.VIDEO, i1, i2);
          localArrayList.add(localObject);
          boolean bool = false;
          if (localList2 != null) {
            bool = localList2.contains(localSize);
          }
          if (bool)
          {
            Log.d(this.TAG, "selectResolutions() - " + localSize + " support 60fps.");
            paramSettings = new Resolution(MediaType.VIDEO, i1, i2, 60);
            paramCamera = paramSettings;
            if (!ResolutionSelector.Restriction.match(paramRestriction, paramSettings)) {
              paramCamera = null;
            }
          }
          m = 1;
          localCamera = paramCamera;
          n = j;
        }
        else
        {
          localCamera = paramCamera;
          localObject = paramSettings;
          m = k;
          n = j;
          if (i1 == 1280)
          {
            localCamera = paramCamera;
            localObject = paramSettings;
            m = k;
            n = j;
            if (i2 == 720)
            {
              localArrayList.add(new Resolution(MediaType.VIDEO, i1, i2));
              localCamera = paramCamera;
              localObject = paramSettings;
              m = k;
              n = j;
            }
          }
        }
      }
    }
    Collections.sort(localArrayList, RESOLUTION_COMPARATOR);
    if ((paramSettings != null) && (paramCamera != null)) {
      localArrayList.add(localArrayList.indexOf(paramSettings) + 1, paramCamera);
    }
    return localArrayList;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/DefaultVideoResolutionSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */