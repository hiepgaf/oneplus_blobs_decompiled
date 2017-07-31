package com.oneplus.camera.media;

import android.util.Size;
import com.oneplus.base.Log;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.util.AspectRatio;
import com.oneplus.util.SizeComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DefaultResolutionSelector
  implements ResolutionSelector
{
  protected final String TAG = getClass().getSimpleName();
  private final CameraActivity m_CameraActivity;
  
  protected DefaultResolutionSelector(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity == null) {
      throw new IllegalArgumentException("No camera activity");
    }
    this.m_CameraActivity = paramCameraActivity;
  }
  
  public final CameraActivity getCameraActivity()
  {
    return this.m_CameraActivity;
  }
  
  public Size selectPreviewSize(Camera paramCamera, Settings paramSettings, Size paramSize, Resolution paramResolution)
  {
    AspectRatio localAspectRatio = paramResolution.getAspectRatio();
    paramSettings = paramSize;
    if (paramSize == null) {
      paramSettings = new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    if (paramSettings.getWidth() <= paramResolution.getWidth())
    {
      paramSize = paramSettings;
      if (paramSettings.getHeight() <= paramResolution.getHeight()) {}
    }
    else
    {
      paramSize = new Size(Math.min(paramSettings.getWidth(), paramResolution.getWidth()), Math.min(paramSettings.getHeight(), paramResolution.getHeight()));
    }
    Object localObject1 = null;
    paramSettings = null;
    Object localObject2 = null;
    Size localSize = null;
    List localList = (List)paramCamera.get(Camera.PROP_PREVIEW_SIZES);
    if (localList != null)
    {
      SizeComparator localSizeComparator = SizeComparator.DEFAULT;
      int i = localList.size() - 1;
      paramCamera = localSize;
      localObject1 = paramSettings;
      localObject2 = paramCamera;
      if (i >= 0)
      {
        localSize = (Size)localList.get(i);
        localObject1 = paramSettings;
        localObject2 = paramCamera;
        if (localSize != null)
        {
          localObject1 = paramSettings;
          localObject2 = paramCamera;
          if (AspectRatio.get(localSize) == localAspectRatio)
          {
            int j = localSizeComparator.compare(localSize, paramSize);
            if (j == 0) {
              return localSize;
            }
            if (j <= 0) {
              break label250;
            }
            if (paramSettings != null)
            {
              localObject1 = paramSettings;
              localObject2 = paramCamera;
              if (localSizeComparator.compare(localSize, paramSettings) >= 0) {}
            }
            else
            {
              localObject1 = localSize;
              localObject2 = paramCamera;
            }
          }
        }
        for (;;)
        {
          i -= 1;
          paramSettings = (Settings)localObject1;
          paramCamera = (Camera)localObject2;
          break;
          label250:
          if (paramCamera != null)
          {
            localObject1 = paramSettings;
            localObject2 = paramCamera;
            if (localSizeComparator.compare(localSize, paramCamera) <= 0) {}
          }
          else
          {
            localObject2 = localSize;
            localObject1 = paramSettings;
          }
        }
      }
    }
    if (localObject2 != null) {
      return (Size)localObject2;
    }
    if (localObject1 != null) {
      return (Size)localObject1;
    }
    Log.e(this.TAG, "selectPreviewSize() - No available preview size for " + paramResolution);
    return null;
  }
  
  protected final List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, AspectRatio[] paramArrayOfAspectRatio, int paramInt, ResolutionSelector.Restriction paramRestriction)
  {
    paramCamera = new ArrayList((Collection)paramCamera.get(Camera.PROP_PICTURE_SIZES));
    Collections.sort(paramCamera, SizeComparator.DEFAULT);
    paramSettings = new ArrayList();
    int i = 0;
    while (i < paramArrayOfAspectRatio.length)
    {
      AspectRatio localAspectRatio = paramArrayOfAspectRatio[i];
      int j = paramCamera.size() - 1;
      int m;
      for (int k = paramInt; (j >= 0) && (k > 0); k = m)
      {
        Size localSize = (Size)paramCamera.get(j);
        m = k;
        if (localSize != null)
        {
          m = k;
          if (AspectRatio.get(localSize) == localAspectRatio)
          {
            m = k;
            if (ResolutionSelector.Restriction.match(paramRestriction, localSize))
            {
              paramSettings.add(new Resolution(MediaType.PHOTO, localSize));
              m = k - 1;
            }
          }
        }
        j -= 1;
      }
      i += 1;
    }
    return paramSettings;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/DefaultResolutionSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */