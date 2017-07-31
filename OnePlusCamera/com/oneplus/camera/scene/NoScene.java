package com.oneplus.camera.scene;

import android.graphics.drawable.Drawable;
import com.oneplus.camera.InvalidMode;

final class NoScene
  extends InvalidMode<Scene>
  implements Scene
{
  public String getDisplayName()
  {
    return null;
  }
  
  public Drawable getImage(Scene.ImageUsage paramImageUsage)
  {
    return null;
  }
  
  public String toString()
  {
    return "(No scene)";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/NoScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */