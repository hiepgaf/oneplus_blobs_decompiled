package com.oneplus.camera.bokeh;

import android.os.Message;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.BokehState;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.Camera.State;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.ModeController;

public final class BokehController
  extends ModeController<BokehUI>
{
  static final int MSG_DISABLE_DEBUG_INFO = 10002;
  static final int MSG_ENABLE_DEBUG_INFO = 10001;
  public static final String SETTINGS_KEY_BOKEH_ORIGINAL = "BokehOriginalPicture";
  private Camera m_ActiveCamera;
  private final PropertyChangedCallback<BokehState> m_BokehStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BokehState> paramAnonymousPropertyKey, PropertyChangeEventArgs<BokehState> paramAnonymousPropertyChangeEventArgs)
    {
      if (BokehController.-wrap0(BokehController.this)) {
        HandlerUtils.sendMessage(BokehController.-wrap2(BokehController.this), 10001, paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    }
  };
  private final PropertyChangedCallback<Camera.State> m_CameraStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera.State> paramAnonymousPropertyChangeEventArgs)
    {
      switch (-getcom-oneplus-camera-Camera$StateSwitchesValues()[((Camera.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
      {
      }
      do
      {
        return;
      } while (BokehController.-get0(BokehController.this) == null);
      if ((((Boolean)BokehController.-get0(BokehController.this).get(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED)).booleanValue()) && (BokehController.-wrap1(BokehController.this).getBoolean("BokehOriginalPicture", false)))
      {
        BokehController.-get0(BokehController.this).set(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_ENABLED, Boolean.valueOf(true));
        return;
      }
      BokehController.-get0(BokehController.this).set(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_ENABLED, Boolean.valueOf(false));
    }
  };
  private final EventHandler<EventArgs> m_DebugInfoUpdatedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      if (!BokehController.-get1(BokehController.this))
      {
        BokehController.-set0(BokehController.this, true);
        HandlerUtils.post(BokehController.this, BokehController.-get2(BokehController.this));
      }
    }
  };
  private boolean m_IsDebugInfoEnabled;
  private boolean m_IsDebugInfoUpdateScheduled;
  private final Runnable m_UpdateDebugInfoRunnable = new Runnable()
  {
    public void run()
    {
      BokehController.-set0(BokehController.this, false);
      BokehController.-wrap3(BokehController.this);
    }
  };
  
  static
  {
    Settings.setGlobalDefaultValue("BokehOriginalPicture", Boolean.valueOf(false));
  }
  
  BokehController(CameraThread paramCameraThread)
  {
    super("Portrait controller", paramCameraThread);
  }
  
  private void clearCameraParameters()
  {
    if (this.m_ActiveCamera != null)
    {
      Log.d(this.TAG, "clearCameraParameters()");
      this.m_ActiveCamera.removeCallback(Camera.PROP_BOKEH_STATE, this.m_BokehStateChangedCB);
      this.m_ActiveCamera.removeCallback(Camera.PROP_STATE, this.m_CameraStateChangedCB);
      this.m_ActiveCamera.removeHandler(Camera.EVENT_BOKEH_DEBUG_INFO_UPDATED, this.m_DebugInfoUpdatedHandler);
      if (((Boolean)this.m_ActiveCamera.get(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED)).booleanValue()) {
        this.m_ActiveCamera.set(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_ENABLED, Boolean.valueOf(false));
      }
      this.m_ActiveCamera.set(Camera.PROP_IS_BOKEH_ENABLED, Boolean.valueOf(false));
      this.m_ActiveCamera.set(Camera.PROP_IS_BOKEH_DEBUG_ENABLED, Boolean.valueOf(false));
      this.m_ActiveCamera = null;
    }
  }
  
  private void setupCameraParameters(Camera paramCamera)
  {
    if (paramCamera == null) {
      return;
    }
    if (this.m_ActiveCamera == paramCamera) {
      return;
    }
    if (paramCamera.get(Camera.PROP_LENS_FACING) != Camera.LensFacing.BACK) {
      return;
    }
    clearCameraParameters();
    Log.d(this.TAG, "setupCameraParameters()");
    this.m_ActiveCamera = paramCamera;
    if ((((Boolean)paramCamera.get(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_SUPPORTED)).booleanValue()) && (getSettings().getBoolean("BokehOriginalPicture", false))) {
      paramCamera.set(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_ENABLED, Boolean.valueOf(true));
    }
    for (;;)
    {
      paramCamera.addCallback(Camera.PROP_STATE, this.m_CameraStateChangedCB);
      paramCamera.addCallback(Camera.PROP_BOKEH_STATE, this.m_BokehStateChangedCB);
      paramCamera.addHandler(Camera.EVENT_BOKEH_DEBUG_INFO_UPDATED, this.m_DebugInfoUpdatedHandler);
      paramCamera.set(Camera.PROP_BOKEH_STRENGTH, Float.valueOf(0.5F));
      paramCamera.set(Camera.PROP_IS_BOKEH_DEBUG_ENABLED, Boolean.valueOf(this.m_IsDebugInfoEnabled));
      paramCamera.set(Camera.PROP_IS_BOKEH_ENABLED, Boolean.valueOf(true));
      return;
      paramCamera.set(Camera.PROP_IS_BOKEH_ORIGINAL_PICTURE_ENABLED, Boolean.valueOf(false));
    }
  }
  
  private void updateDebugInfo()
  {
    if ((!this.m_IsDebugInfoEnabled) || (!isUILinked()) || (this.m_ActiveCamera == null)) {
      return;
    }
    HandlerUtils.sendMessage(getUI(), 10010, this.m_ActiveCamera.getBokehDebugInfo());
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    }
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return;
              } while (!this.m_IsDebugInfoEnabled);
              this.m_IsDebugInfoEnabled = false;
            } while (this.m_ActiveCamera == null);
            this.m_ActiveCamera.set(Camera.PROP_IS_BOKEH_DEBUG_ENABLED, Boolean.valueOf(false));
          } while (!this.m_IsDebugInfoUpdateScheduled);
          this.m_IsDebugInfoUpdateScheduled = false;
          HandlerUtils.removeCallbacks(this, this.m_UpdateDebugInfoRunnable);
          return;
        } while (this.m_IsDebugInfoEnabled);
        this.m_IsDebugInfoEnabled = true;
      } while (this.m_ActiveCamera == null);
      this.m_ActiveCamera.set(Camera.PROP_IS_BOKEH_DEBUG_ENABLED, Boolean.valueOf(true));
    } while (this.m_IsDebugInfoUpdateScheduled);
    this.m_IsDebugInfoUpdateScheduled = true;
    HandlerUtils.post(this, this.m_UpdateDebugInfoRunnable);
  }
  
  protected void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    super.onCameraChanged(paramCamera1, paramCamera2);
    if (isEntered()) {
      setupCameraParameters(paramCamera2);
    }
  }
  
  protected boolean onEnter(int paramInt)
  {
    if (!super.onEnter(paramInt)) {
      return false;
    }
    setupCameraParameters(getCamera());
    if ((isUILinked()) && (this.m_ActiveCamera != null)) {
      HandlerUtils.sendMessage(getUI(), 10001, this.m_ActiveCamera.get(Camera.PROP_BOKEH_STATE));
    }
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    clearCameraParameters();
    if (this.m_IsDebugInfoUpdateScheduled)
    {
      this.m_IsDebugInfoUpdateScheduled = false;
      HandlerUtils.removeCallbacks(this, this.m_UpdateDebugInfoRunnable);
    }
    super.onExit(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/bokeh/BokehController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */