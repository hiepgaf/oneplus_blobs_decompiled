package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.drawable.ShadowDrawable;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.scene.AutoHdrScene;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.Scene.ImageUsage;
import com.oneplus.camera.scene.SceneEventArgs;
import com.oneplus.camera.scene.SceneManager;
import java.util.List;

final class SceneToast
  extends CameraComponent
{
  private static final long DURATION_UPDATE_TOAST_DELAY = 150L;
  private boolean m_IsAutoHdrSceneReady;
  private OnScreenHint m_OnScreenHint;
  private SceneManager m_SceneManager;
  private Handle m_ToastHandle;
  private final Runnable m_UpdateToastRunnable = new Runnable()
  {
    public void run()
    {
      SceneToast.-wrap0(SceneToast.this);
    }
  };
  
  SceneToast(CameraActivity paramCameraActivity)
  {
    super("Scene toast", paramCameraActivity, true);
  }
  
  private void updateToast()
  {
    getHandler().removeCallbacks(this.m_UpdateToastRunnable);
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (getMediaType() == MediaType.PHOTO)
    {
      localObject1 = localObject2;
      if (this.m_SceneManager != null) {
        localObject1 = ((Scene)this.m_SceneManager.get(SceneManager.PROP_SCENE)).getImage(Scene.ImageUsage.TOAST_ICON);
      }
    }
    if (localObject1 != null)
    {
      if (this.m_OnScreenHint == null) {
        this.m_OnScreenHint = ((OnScreenHint)findComponent(OnScreenHint.class));
      }
      if (this.m_OnScreenHint != null)
      {
        localObject1 = new ShadowDrawable(getCameraActivity(), (Drawable)localObject1, 2131492925);
        int i = getCameraActivity().getResources().getDimensionPixelSize(2131296474);
        ((ShadowDrawable)localObject1).setPaddings(i, i, i, i + getCameraActivity().getResources().getInteger(2131427348));
        if (Handle.isValid(this.m_ToastHandle))
        {
          this.m_OnScreenHint.updateHint(this.m_ToastHandle, (Drawable)localObject1, 0);
          return;
        }
        this.m_ToastHandle = this.m_OnScreenHint.showHint((Drawable)localObject1, null, 17);
        return;
      }
      Log.w(this.TAG, "updateToast() - No OnScreenHint");
      return;
    }
    this.m_ToastHandle = Handle.close(this.m_ToastHandle);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    Object localObject1 = (CaptureModeManager)findComponent(CaptureModeManager.class);
    this.m_SceneManager = ((SceneManager)findComponent(SceneManager.class));
    Object localObject2 = getCameraActivity();
    PropertyChangedCallback local2 = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        SceneToast.-wrap0(SceneToast.this);
      }
    };
    final PropertyChangedCallback local3 = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        SceneToast.this.getHandler().removeCallbacks(SceneToast.-get2(SceneToast.this));
        SceneToast.this.getHandler().postDelayed(SceneToast.-get2(SceneToast.this), 150L);
      }
    };
    ((CameraActivity)localObject2).addCallback(CameraActivity.PROP_MEDIA_TYPE, local2);
    ((CameraActivity)localObject2).addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.STOPPED) {
          SceneToast.-set1(SceneToast.this, Handle.close(SceneToast.-get1(SceneToast.this)));
        }
        while (paramAnonymousPropertyChangeEventArgs.getNewValue() != BaseActivity.State.RESUMING) {
          return;
        }
        SceneToast.-wrap0(SceneToast.this);
      }
    });
    if (localObject1 != null) {
      ((CaptureModeManager)localObject1).addCallback(CaptureModeManager.PROP_CAPTURE_MODE, local2);
    }
    if (this.m_SceneManager != null) {
      this.m_SceneManager.addCallback(SceneManager.PROP_SCENE, local3);
    }
    int i;
    if (this.m_SceneManager != null)
    {
      localObject1 = (List)this.m_SceneManager.get(SceneManager.PROP_SCENES);
      i = ((List)localObject1).size() - 1;
    }
    for (;;)
    {
      if (i >= 0)
      {
        localObject2 = (Scene)((List)localObject1).get(i);
        if ((localObject2 instanceof AutoHdrScene))
        {
          this.m_IsAutoHdrSceneReady = true;
          ((Scene)localObject2).addCallback(AutoHdrScene.PROP_IS_HDR_ACTIVE, local3);
        }
      }
      else
      {
        if (!this.m_IsAutoHdrSceneReady) {
          this.m_SceneManager.addHandler(SceneManager.EVENT_SCENE_ADDED, new EventHandler()
          {
            public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<SceneEventArgs> paramAnonymousEventKey, SceneEventArgs paramAnonymousSceneEventArgs)
            {
              paramAnonymousEventSource = paramAnonymousSceneEventArgs.getScene();
              if ((!(paramAnonymousEventSource instanceof AutoHdrScene)) || (SceneToast.-get0(SceneToast.this))) {
                return;
              }
              SceneToast.-set0(SceneToast.this, true);
              paramAnonymousEventSource.addCallback(AutoHdrScene.PROP_IS_HDR_ACTIVE, local3);
            }
          });
        }
        updateToast();
        return;
      }
      i -= 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SceneToast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */