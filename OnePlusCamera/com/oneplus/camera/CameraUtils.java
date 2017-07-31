package com.oneplus.camera;

import java.util.List;

public final class CameraUtils
{
  public static Camera findCamera(List<Camera> paramList, Camera.LensFacing paramLensFacing, boolean paramBoolean)
  {
    if (paramList != null)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        Camera localCamera = (Camera)paramList.get(i);
        if ((localCamera != null) && (localCamera.get(Camera.PROP_LENS_FACING) == paramLensFacing) && (isNonRemovableCamera((String)localCamera.get(Camera.PROP_ID)) != paramBoolean)) {
          return localCamera;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public static Camera findCamera(List<Camera> paramList, String paramString)
  {
    if (paramList != null)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        Camera localCamera = (Camera)paramList.get(i);
        if ((localCamera != null) && (((String)localCamera.get(Camera.PROP_ID)).equals(paramString))) {
          return localCamera;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  public static boolean isNonRemovableCamera(String paramString)
  {
    int i = paramString.length() - 1;
    while (i >= 0)
    {
      int j = paramString.charAt(i);
      if ((j < 48) || (j > 57)) {
        return false;
      }
      i -= 1;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */