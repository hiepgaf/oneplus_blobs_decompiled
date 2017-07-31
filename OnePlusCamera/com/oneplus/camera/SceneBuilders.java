package com.oneplus.camera;

import com.oneplus.camera.scene.AutoHdrSceneBuilder;
import com.oneplus.camera.scene.ClearShotBuilder;
import com.oneplus.camera.scene.HdrSceneBuilder;
import com.oneplus.camera.scene.SceneBuilder;

class SceneBuilders
{
  static final SceneBuilder[] BUILDERS = { new AutoHdrSceneBuilder(), new HdrSceneBuilder(), new ClearShotBuilder() };
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/SceneBuilders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */