package com.oneplus.camera;

import com.oneplus.base.BaseApplication;

public abstract class CameraApplication
  extends BaseApplication
{
  public static CameraApplication current()
  {
    return (CameraApplication)BaseApplication.current();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */