package com.oneplus.camera.scene;

import android.graphics.drawable.Drawable;
import com.oneplus.camera.Mode;

public abstract interface Scene
  extends Mode<Scene>
{
  public static final Scene NO_SCENE = new NoScene();
  
  public abstract Drawable getImage(ImageUsage paramImageUsage);
  
  public static enum ImageUsage
  {
    SECOND_LAYER_BAR_ICON,  TOAST_ICON;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/Scene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */