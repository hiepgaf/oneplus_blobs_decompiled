package com.oneplus.camera.scene;

import com.oneplus.base.EventArgs;

public class SceneEventArgs
  extends EventArgs
{
  private final Scene m_Scene;
  
  public SceneEventArgs(Scene paramScene)
  {
    this.m_Scene = paramScene;
  }
  
  public final Scene getScene()
  {
    return this.m_Scene;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/SceneEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */