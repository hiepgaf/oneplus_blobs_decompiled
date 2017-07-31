package com.oneplus.camera;

import android.os.Message;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.Component;

public abstract class ModeController<TUI extends Component>
  extends CameraComponent
{
  private static final int MSG_ENTER = -10000;
  private static final int MSG_EXIT = -10001;
  private static final int MSG_LINK = -10002;
  private final PropertyChangedCallback<Camera> m_CameraChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
    {
      ModeController.this.onCameraChanged((Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private boolean m_IsEntered;
  private TUI m_UI;
  
  protected ModeController(String paramString, CameraThread paramCameraThread)
  {
    super(paramString, paramCameraThread, true);
  }
  
  final boolean enter(int paramInt)
  {
    if (!isDependencyThread())
    {
      HandlerUtils.removeMessages(this, 55535);
      return HandlerUtils.sendMessage(this, 55536, paramInt, 0, null, true);
    }
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "enter() - Component is not running");
      return false;
    }
    if (this.m_IsEntered) {
      return true;
    }
    Log.v(this.TAG, "enter()");
    try
    {
      if (!onEnter(paramInt))
      {
        Log.e(this.TAG, "enter() - Fail to enter mode");
        return false;
      }
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "enter() - Fail to enter mode", localThrowable);
      return false;
    }
    this.m_IsEntered = true;
    return true;
  }
  
  final void exit(int paramInt)
  {
    if (!isDependencyThread())
    {
      HandlerUtils.removeMessages(this, 55536);
      HandlerUtils.sendMessage(this, 55535, paramInt, 0, null, true);
      return;
    }
    if (!this.m_IsEntered) {
      return;
    }
    Log.v(this.TAG, "exit()");
    try
    {
      onExit(paramInt);
      this.m_IsEntered = false;
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "exit() - Error occurred while exiting mode", localThrowable);
      }
    }
  }
  
  protected final TUI getUI()
  {
    return this.m_UI;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    case -10000: 
      do
      {
        return;
      } while (enter(paramMessage.arg1));
      Log.e(this.TAG, "handleMessage() - Fail to enter mode asynchronously");
      return;
    case -10001: 
      exit(paramMessage.arg1);
      return;
    }
    link((Component)paramMessage.obj);
  }
  
  protected final boolean isEntered()
  {
    return this.m_IsEntered;
  }
  
  protected final boolean isUILinked()
  {
    return this.m_UI != null;
  }
  
  final void link(TUI paramTUI)
  {
    if (isDependencyThread())
    {
      if (!isRunningOrInitializing())
      {
        Log.e(this.TAG, "link() - Component is not running");
        return;
      }
      this.m_UI = paramTUI;
      onUILinked(paramTUI);
    }
    while (HandlerUtils.sendMessage(this, 55534, 0, 0, paramTUI)) {
      return;
    }
    Log.e(this.TAG, "link() - Fail to perform cross-thread operation");
  }
  
  protected void onCameraChanged(Camera paramCamera1, Camera paramCamera2) {}
  
  protected void onDeinitialize()
  {
    getCameraThread().removeCallback(CameraThread.PROP_CAMERA, this.m_CameraChangedCallback);
    super.onDeinitialize();
  }
  
  protected boolean onEnter(int paramInt)
  {
    return true;
  }
  
  protected void onExit(int paramInt) {}
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_CAMERA, this.m_CameraChangedCallback);
    onCameraChanged(null, (Camera)getCameraThread().get(CameraThread.PROP_CAMERA));
  }
  
  protected void onRelease()
  {
    this.m_UI = null;
    super.onRelease();
  }
  
  protected void onUILinked(TUI paramTUI) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ModeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */