package com.oneplus.camera.scene;

import com.oneplus.camera.CameraActivity;

public final class HdrSceneBuilder
  implements SceneBuilder
{
  public Scene createScene(CameraActivity paramCameraActivity)
  {
    if (!paramCameraActivity.isServiceMode()) {
      return new HdrScene(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/HdrSceneBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */