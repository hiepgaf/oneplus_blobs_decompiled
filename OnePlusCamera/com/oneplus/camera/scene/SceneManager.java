package com.oneplus.camera.scene;

import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface SceneManager
  extends Component
{
  public static final EventKey<SceneEventArgs> EVENT_SCENE_ADDED = new EventKey("SceneAdded", SceneEventArgs.class, SceneManager.class);
  public static final EventKey<SceneEventArgs> EVENT_SCENE_REMOVED = new EventKey("SceneRemoved", SceneEventArgs.class, SceneManager.class);
  public static final int FLAG_FROM_USER = 4;
  public static final int FLAG_LOCK_SCENE = 2;
  public static final int FLAG_PRESERVE_CURRENT_SCENE = 1;
  public static final PropertyKey<Scene> PROP_SCENE = new PropertyKey("Scene", Scene.class, SceneManager.class, Scene.NO_SCENE);
  public static final PropertyKey<List<Scene>> PROP_SCENES;
  public static final PropertyKey<Scene> PROP_SCENE_USER_SELECTED = new PropertyKey("SceneUserSelected", Scene.class, SceneManager.class, 1, null);
  
  static
  {
    PROP_SCENES = new PropertyKey("Scenes", List.class, SceneManager.class, Collections.EMPTY_LIST);
  }
  
  public abstract boolean addBuilder(SceneBuilder paramSceneBuilder, int paramInt);
  
  public abstract Handle setDefaultScene(Scene paramScene, int paramInt);
  
  public abstract boolean setScene(Scene paramScene, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/SceneManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */