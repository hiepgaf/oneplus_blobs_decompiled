package com.oneplus.camera;

import com.oneplus.base.Log;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;

public abstract class ModeUI<TController extends ModeController<?>>
  extends UIComponent
{
  private TController m_Controller;
  private final Class<? extends TController> m_ControllerClass;
  private final ComponentSearchCallback<TController> m_ControllerSearchCallback = new ComponentSearchCallback()
  {
    public void onComponentFound(TController paramAnonymousTController)
    {
      ModeUI.-wrap0(ModeUI.this, paramAnonymousTController);
    }
  };
  private int m_EnterFlags;
  private boolean m_IsEntered;
  
  protected ModeUI(String paramString, CameraActivity paramCameraActivity, Class<? extends TController> paramClass)
  {
    super(paramString, paramCameraActivity, true);
    if (paramClass == null) {
      throw new IllegalArgumentException("No controller type.");
    }
    this.m_ControllerClass = paramClass;
  }
  
  private void onControllerFound(TController paramTController)
  {
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "onControllerFound() - Component is not running");
      return;
    }
    paramTController.link(this);
    this.m_Controller = paramTController;
    onControllerLinked(paramTController);
  }
  
  public final boolean enter(int paramInt)
  {
    verifyAccess();
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
  
  public final void exit(int paramInt)
  {
    verifyAccess();
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
  
  protected final TController getController()
  {
    return this.m_Controller;
  }
  
  protected final boolean isControllerLinked()
  {
    return this.m_Controller != null;
  }
  
  protected final boolean isEntered()
  {
    return this.m_IsEntered;
  }
  
  protected void onCameraThreadStarted()
  {
    super.onCameraThreadStarted();
    ComponentUtils.findComponent(getCameraThread(), this.m_ControllerClass, this, this.m_ControllerSearchCallback);
  }
  
  protected void onControllerLinked(TController paramTController)
  {
    if (this.m_IsEntered)
    {
      Log.w(this.TAG, "onControllerLinked() - Enter mode again");
      if (!paramTController.enter(this.m_EnterFlags))
      {
        Log.e(this.TAG, "onControllerLinked() - Fail to enter mode");
        exit(0);
      }
    }
  }
  
  protected boolean onEnter(int paramInt)
  {
    if (this.m_Controller == null) {
      Log.w(this.TAG, "onEnter() - Enter mode later when controller linked");
    }
    while (this.m_Controller.enter(paramInt)) {
      return true;
    }
    Log.e(this.TAG, "onEnter() - Fail to enter mode");
    return false;
  }
  
  protected void onExit(int paramInt)
  {
    if (this.m_Controller != null) {
      this.m_Controller.exit(paramInt);
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    if (isCameraThreadStarted()) {
      ComponentUtils.findComponent(getCameraThread(), this.m_ControllerClass, this, this.m_ControllerSearchCallback);
    }
  }
  
  protected void onRelease()
  {
    this.m_Controller = null;
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ModeUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */