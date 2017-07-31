package com.oneplus.camera;

import com.oneplus.base.HandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.media.MediaType;

public abstract class BasicMode<T extends Mode<?>>
  extends HandlerBaseObject
  implements Mode<T>
{
  private final CameraActivity m_CameraActivity;
  private PropertyChangedCallback<Boolean> m_IsCameraThreadStartedCallback;
  private Mode.State m_State = Mode.State.EXITED;
  
  protected BasicMode(CameraActivity paramCameraActivity, String paramString)
  {
    super(true);
    if (paramCameraActivity == null) {
      throw new IllegalArgumentException("No camera activity");
    }
    setReadOnly(PROP_ID, paramString);
    this.m_CameraActivity = paramCameraActivity;
    if (!((Boolean)this.m_CameraActivity.get(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED)).booleanValue())
    {
      this.m_IsCameraThreadStartedCallback = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          BasicMode.-set0(BasicMode.this, null);
          paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
          BasicMode.this.onCameraThreadStarted();
        }
      };
      paramCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
    }
  }
  
  private Mode.State changeState(Mode.State paramState)
  {
    Mode.State localState = this.m_State;
    if (localState != paramState)
    {
      this.m_State = paramState;
      notifyPropertyChanged(PROP_STATE, localState, paramState);
    }
    return this.m_State;
  }
  
  protected final void disable()
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-Mode$StateSwitchesValues()[this.m_State.ordinal()])
    {
    default: 
      Log.w(this.TAG, "disable() - Current state is " + this.m_State);
    }
    for (;;)
    {
      changeState(Mode.State.DISABLED);
      return;
      return;
      Log.w(this.TAG, "disable()");
      continue;
      Log.w(this.TAG, "exit()");
      exit(null, 0);
    }
  }
  
  protected final void enable()
  {
    verifyAccess();
    if (this.m_State != Mode.State.DISABLED) {
      return;
    }
    Log.w(this.TAG, "enable()");
    changeState(Mode.State.EXITED);
  }
  
  public final boolean enter(T paramT, int paramInt)
  {
    verifyAccess();
    if (this.m_State != Mode.State.EXITED)
    {
      Log.e(this.TAG, "enter() - Current state is " + this.m_State);
      return false;
    }
    if (changeState(Mode.State.ENTERING) != Mode.State.ENTERING)
    {
      Log.e(this.TAG, "enter() - Entering process was interrupted");
      return false;
    }
    try
    {
      if (!onEnter(paramT, paramInt))
      {
        Log.e(this.TAG, "enter() - Fail to enter");
        if (this.m_State == Mode.State.ENTERING) {
          changeState(Mode.State.EXITED);
        }
        return false;
      }
    }
    catch (Throwable paramT)
    {
      Log.e(this.TAG, "enter() - Fail to enter", paramT);
      if (this.m_State == Mode.State.ENTERING) {
        changeState(Mode.State.EXITED);
      }
      return false;
    }
    if ((this.m_State != Mode.State.ENTERING) || (changeState(Mode.State.ENTERED) != Mode.State.ENTERED))
    {
      Log.e(this.TAG, "enter() - Entering process was interrupted");
      return false;
    }
    return true;
  }
  
  public final void exit(T paramT, int paramInt)
  {
    verifyAccess();
    if (this.m_State != Mode.State.ENTERED) {
      return;
    }
    if (changeState(Mode.State.EXITING) != Mode.State.EXITING)
    {
      Log.w(this.TAG, "exit() - Exiting process was interrupted");
      return;
    }
    try
    {
      onExit(paramT, paramInt);
      if (this.m_State == Mode.State.EXITING) {
        changeState(Mode.State.EXITED);
      }
      return;
    }
    catch (Throwable paramT)
    {
      for (;;)
      {
        Log.e(this.TAG, "exit() - Unhandled exception occurred while exiting", paramT);
      }
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_STATE) {
      return this.m_State;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected final Camera getCamera()
  {
    return (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
  }
  
  public final CameraActivity getCameraActivity()
  {
    return this.m_CameraActivity;
  }
  
  protected final MediaType getMediaType()
  {
    return (MediaType)this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE);
  }
  
  public final boolean isEntered()
  {
    return this.m_State == Mode.State.ENTERED;
  }
  
  protected void onCameraThreadStarted() {}
  
  protected abstract boolean onEnter(T paramT, int paramInt);
  
  protected abstract void onExit(T paramT, int paramInt);
  
  protected void onRelease()
  {
    changeState(Mode.State.RELEASED);
    if (this.m_IsCameraThreadStartedCallback != null)
    {
      getCameraActivity().removeCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
      this.m_IsCameraThreadStartedCallback = null;
    }
    super.onRelease();
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_STATE) {
      throw new IllegalAccessError("Cannot change mode state.");
    }
    return super.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  public String toString()
  {
    return (String)get(PROP_ID);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/BasicMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */