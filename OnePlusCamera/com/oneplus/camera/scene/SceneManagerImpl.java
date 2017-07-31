package com.oneplus.camera.scene;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.Mode.State;
import com.oneplus.util.ListUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SceneManagerImpl
  extends CameraComponent
  implements SceneManager
{
  private final List<Scene> m_ActiveScenes = new ArrayList();
  private Scene m_DefaultScene = Scene.NO_SCENE;
  private final List<DefaultSceneHandle> m_DefaultSceneHandles = new ArrayList();
  private boolean m_IsSceneLocked;
  private Scene m_Scene = Scene.NO_SCENE;
  private final List<SceneBuilder> m_SceneBuilders = new ArrayList();
  private final PropertyChangedCallback<Mode.State> m_SceneStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Mode.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<Mode.State> paramAnonymousPropertyChangeEventArgs)
    {
      switch (-getcom-oneplus-camera-Mode$StateSwitchesValues()[((Mode.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
      {
      default: 
        if (paramAnonymousPropertyChangeEventArgs.getOldValue() == Mode.State.DISABLED) {
          SceneManagerImpl.-wrap1(SceneManagerImpl.this, (Scene)paramAnonymousPropertySource);
        }
        return;
      case 1: 
        SceneManagerImpl.-wrap0(SceneManagerImpl.this, (Scene)paramAnonymousPropertySource);
        return;
      }
      SceneManagerImpl.-wrap2(SceneManagerImpl.this, (Scene)paramAnonymousPropertySource);
    }
  };
  private Scene m_SceneUserSelected;
  private final List<Scene> m_Scenes = new ArrayList();
  
  SceneManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Scene Manager", paramCameraActivity, false);
    setReadOnly(PROP_SCENES, Collections.unmodifiableList(this.m_ActiveScenes));
  }
  
  private boolean createScene(SceneBuilder paramSceneBuilder)
  {
    try
    {
      Scene localScene = paramSceneBuilder.createScene(getCameraActivity());
      if (localScene != null)
      {
        Log.v(this.TAG, "createScene() - Scene : ", localScene);
        localScene.addCallback(Scene.PROP_STATE, this.m_SceneStateChangedCallback);
        this.m_Scenes.add(localScene);
        if ((localScene.get(Scene.PROP_STATE) == Mode.State.DISABLED) || (this.m_IsSceneLocked)) {
          break label161;
        }
        this.m_ActiveScenes.add(localScene);
        raise(EVENT_SCENE_ADDED, new SceneEventArgs(localScene));
      }
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "createScene() - Fail to create scene by " + paramSceneBuilder, localThrowable);
      return false;
    }
    Log.e(this.TAG, "createScene() - No scene created by " + paramSceneBuilder);
    return false;
    label161:
    return true;
  }
  
  private void onSceneDisabled(Scene paramScene)
  {
    if (this.m_ActiveScenes.remove(paramScene))
    {
      if ((!this.m_DefaultSceneHandles.isEmpty()) && (((DefaultSceneHandle)this.m_DefaultSceneHandles.get(this.m_DefaultSceneHandles.size() - 1)).scene == paramScene))
      {
        Log.w(this.TAG, "onSceneDisabled() - Default scene '" + paramScene + "' disabled");
        updateDefaultScene();
      }
      if (this.m_Scene == paramScene)
      {
        Log.w(this.TAG, "onSceneDisabled() - Scene '" + paramScene + "' has been disabled when using, exit from this scene");
        setScene(Scene.NO_SCENE, 0);
      }
      raise(EVENT_SCENE_REMOVED, new SceneEventArgs(paramScene));
    }
  }
  
  private void onSceneEnabled(Scene paramScene)
  {
    int j = this.m_Scenes.indexOf(paramScene);
    if (j < 0) {
      return;
    }
    int i = 0;
    int k = this.m_ActiveScenes.size();
    for (;;)
    {
      if (i <= k)
      {
        if (i < k)
        {
          Scene localScene = (Scene)this.m_ActiveScenes.get(i);
          if (localScene == paramScene) {
            return;
          }
          if (this.m_Scenes.indexOf(localScene) <= j) {
            break label195;
          }
          this.m_ActiveScenes.add(i, paramScene);
        }
      }
      else
      {
        if ((!this.m_DefaultSceneHandles.isEmpty()) && (((DefaultSceneHandle)this.m_DefaultSceneHandles.get(this.m_DefaultSceneHandles.size() - 1)).scene == paramScene))
        {
          Log.w(this.TAG, "onSceneEnabled() - Default scene '" + paramScene + "' enabled");
          updateDefaultScene();
        }
        raise(EVENT_SCENE_ADDED, new SceneEventArgs(paramScene));
        return;
      }
      this.m_ActiveScenes.add(paramScene);
      label195:
      i += 1;
    }
  }
  
  private void onSceneReleased(Scene paramScene)
  {
    if (this.m_ActiveScenes.remove(paramScene))
    {
      if ((!this.m_DefaultSceneHandles.isEmpty()) && (((DefaultSceneHandle)this.m_DefaultSceneHandles.get(this.m_DefaultSceneHandles.size() - 1)).scene == paramScene))
      {
        Log.w(this.TAG, "onSceneReleased() - Default scene '" + paramScene + "' released");
        updateDefaultScene();
      }
      if (this.m_Scene == paramScene)
      {
        Log.w(this.TAG, "onSceneReleased() - Scene '" + paramScene + "' has been released when using, exit from this scene");
        setScene(Scene.NO_SCENE, 0);
      }
      raise(EVENT_SCENE_REMOVED, new SceneEventArgs(paramScene));
    }
    if (this.m_Scenes.remove(paramScene)) {
      paramScene.removeCallback(Scene.PROP_STATE, this.m_SceneStateChangedCallback);
    }
  }
  
  private void refreshActiveScenes()
  {
    int i = this.m_Scenes.size() - 1;
    while (i >= 0)
    {
      Scene localScene = (Scene)this.m_Scenes.get(i);
      switch (-getcom-oneplus-camera-Mode$StateSwitchesValues()[((Mode.State)localScene.get(Scene.PROP_STATE)).ordinal()])
      {
      default: 
        onSceneEnabled(localScene);
      }
      i -= 1;
    }
  }
  
  private void restoreDefaultScene(DefaultSceneHandle paramDefaultSceneHandle, int paramInt)
  {
    verifyAccess();
    boolean bool = ListUtils.isLastObject(this.m_DefaultSceneHandles, paramDefaultSceneHandle);
    if (!this.m_DefaultSceneHandles.remove(paramDefaultSceneHandle)) {
      return;
    }
    if (bool)
    {
      if ((paramDefaultSceneHandle.flags & 0x2) != 0)
      {
        if ((!this.m_DefaultSceneHandles.isEmpty()) && ((((DefaultSceneHandle)this.m_DefaultSceneHandles.get(this.m_DefaultSceneHandles.size() - 1)).flags & 0x2) == 0)) {}
        this.m_IsSceneLocked = false;
        refreshActiveScenes();
      }
      updateDefaultScene();
      if ((paramInt & 0x1) == 0) {
        setScene(this.m_DefaultScene, 0);
      }
    }
  }
  
  private void updateDefaultScene()
  {
    if (!this.m_DefaultSceneHandles.isEmpty())
    {
      this.m_DefaultScene = ((DefaultSceneHandle)this.m_DefaultSceneHandles.get(this.m_DefaultSceneHandles.size() - 1)).scene;
      if ((this.m_DefaultScene != Scene.NO_SCENE) && (!this.m_ActiveScenes.contains(this.m_DefaultScene))) {}
    }
    for (;;)
    {
      Log.v(this.TAG, "updateDefaultScene() - Default scene : ", this.m_DefaultScene);
      return;
      Log.e(this.TAG, "updateDefaultScene() - Scene : " + this.m_DefaultScene + " is not contained in active list");
      this.m_DefaultScene = Scene.NO_SCENE;
      continue;
      this.m_DefaultScene = Scene.NO_SCENE;
    }
  }
  
  public boolean addBuilder(SceneBuilder paramSceneBuilder, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "addBuilder() - Component is not running");
      return false;
    }
    if (paramSceneBuilder == null)
    {
      Log.e(this.TAG, "addBuilder() - No builder to add");
      return false;
    }
    this.m_SceneBuilders.add(paramSceneBuilder);
    createScene(paramSceneBuilder);
    return true;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_SCENE) {
      return this.m_Scene;
    }
    if (paramPropertyKey == PROP_SCENE_USER_SELECTED) {
      return this.m_SceneUserSelected;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
  }
  
  public Handle setDefaultScene(Scene paramScene, int paramInt)
  {
    boolean bool = true;
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    if (paramScene == null)
    {
      Log.e(this.TAG, "setDefaultScene() - No scene specified");
      return null;
    }
    Log.v(this.TAG, "setDefaultScene() - Scene : ", paramScene);
    paramScene = new DefaultSceneHandle(paramScene, paramInt);
    this.m_DefaultSceneHandles.add(paramScene);
    if ((paramInt & 0x2) != 0) {}
    for (;;)
    {
      this.m_IsSceneLocked = bool;
      updateDefaultScene();
      if (((paramInt & 0x1) == 0) || (this.m_IsSceneLocked)) {
        setScene(this.m_DefaultScene, 0);
      }
      return paramScene;
      bool = false;
    }
  }
  
  public boolean setScene(Scene paramScene, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "setScene() - Component is not running");
      return false;
    }
    if (paramScene == null)
    {
      Log.e(this.TAG, "setScene() - No scene to change");
      return false;
    }
    if ((paramScene == Scene.NO_SCENE) || (this.m_ActiveScenes.contains(paramScene)))
    {
      if (this.m_Scene == paramScene) {
        return true;
      }
    }
    else
    {
      Log.e(this.TAG, "setScene() - Scene '" + paramScene + "' is not contained in list");
      return false;
    }
    if ((this.m_IsSceneLocked) && (paramScene != this.m_DefaultScene))
    {
      Log.e(this.TAG, "setScene() - SceneLocked is locked and target scene is not defaultScene");
      return false;
    }
    Log.v(this.TAG, "setScene() - Exit from '", this.m_Scene, "'");
    this.m_Scene.exit(paramScene, 1);
    try
    {
      Log.v(this.TAG, "setScene() - Enter to '", paramScene, "'");
      if ((paramScene == Scene.NO_SCENE) || (paramScene.enter(this.m_Scene, 1)))
      {
        Scene localScene = this.m_Scene;
        this.m_Scene = paramScene;
        notifyPropertyChanged(PROP_SCENE, localScene, paramScene);
        if ((paramInt & 0x4) == 0) {
          break label317;
        }
        Log.v(this.TAG, "setScene() - Change selected scene from user '", paramScene, "'");
        localScene = this.m_SceneUserSelected;
        this.m_SceneUserSelected = paramScene;
        notifyPropertyChanged(PROP_SCENE_USER_SELECTED, localScene, paramScene);
        return true;
      }
      Log.e(this.TAG, "setScene() - Fail to enter '" + paramScene + "', go back to previous scene");
      if (!setScene(this.m_Scene, 0)) {
        throw new RuntimeException("Fail to change scene.");
      }
    }
    finally {}
    return false;
    label317:
    return true;
  }
  
  private final class DefaultSceneHandle
    extends Handle
  {
    public final int flags;
    public final Scene scene;
    
    public DefaultSceneHandle(Scene paramScene, int paramInt)
    {
      super();
      this.scene = paramScene;
      this.flags = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      SceneManagerImpl.-wrap3(SceneManagerImpl.this, this, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/SceneManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */