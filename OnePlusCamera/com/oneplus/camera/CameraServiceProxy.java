package com.oneplus.camera;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;

public class CameraServiceProxy
  extends UIComponent
  implements CameraService
{
  private static final int BACKLIGHT_BRIGHTNESS_MAX = 0;
  private static final int BACKLIGHT_BRIGHTNESS_RESTORE = 1;
  private static final String CAMERA_SERVICE_CLASS_NAME = "com.oneplus.camera.service.CameraService";
  private static final String CAMERA_SERVICE_PACKAGE = "com.oneplus.camera.service";
  private static final int MSG_CHECK_SUPPORTED_STATE = -1260010;
  private static final int MSG_RESULT_SUPPORTED_STATE = -1260060;
  private static final int MSG_SET_BACKLIGHT_BRIGHTNESS = -1200010;
  private static final int MSG_TORCH_FLASH = -1260100;
  private static final int SUPPORTED_STATE_TORCH = 30100;
  private static final int TORCH_FLASH_OFF = 0;
  private static final int TORCH_FLASH_ON = 1;
  private Messenger m_LocalMessenger;
  private ServiceConnection m_ServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      CameraServiceProxy.-wrap0(CameraServiceProxy.this, paramAnonymousComponentName, paramAnonymousIBinder);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      CameraServiceProxy.-wrap1(CameraServiceProxy.this, paramAnonymousComponentName);
    }
  };
  private Messenger m_ServiceMessenger;
  
  CameraServiceProxy(CameraActivity paramCameraActivity)
  {
    super("Camera System Service Proxy", paramCameraActivity, true);
    enablePropertyLogs(PROP_IS_CONNECTED, 1);
    enablePropertyLogs(PROP_TORCH_FLASH_SUPPORTED_STATE, 1);
  }
  
  private void checkTorchFlashSupportedState()
  {
    Message localMessage = Message.obtain(null, -1260010, 30100, 0, null);
    localMessage.replyTo = this.m_LocalMessenger;
    try
    {
      Log.v(this.TAG, "checkTorchFlashSupportedState()");
      this.m_ServiceMessenger.send(localMessage);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(this.TAG, "checkTorchFlashSupportedState() - Send message failed");
      localRemoteException.printStackTrace();
    }
  }
  
  private void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.m_ServiceMessenger = new Messenger(paramIBinder);
    setReadOnly(PROP_IS_CONNECTED, Boolean.valueOf(true));
    checkTorchFlashSupportedState();
  }
  
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    this.m_ServiceMessenger = null;
    setReadOnly(PROP_IS_CONNECTED, Boolean.valueOf(false));
  }
  
  private void onSupportedStateResult(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default: 
      return;
    }
    PropertyKey localPropertyKey = PROP_TORCH_FLASH_SUPPORTED_STATE;
    if (paramBoolean) {}
    for (SupportedState localSupportedState = SupportedState.SUPPORTED;; localSupportedState = SupportedState.NOT_SUPPORTED)
    {
      setReadOnly(localPropertyKey, localSupportedState);
      return;
    }
  }
  
  private void setBacklightMaxBrightnessRemote(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 1)
    {
      Message localMessage = Message.obtain(null, -1200010, i, 0, null);
      try
      {
        Log.v(this.TAG, "setBacklightMaxBrightnessRemote() - backlight: ", Integer.valueOf(i));
        this.m_ServiceMessenger.send(localMessage);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e(this.TAG, "setBacklightMaxBrightnessRemote() - Send backlight message failed");
        localRemoteException.printStackTrace();
      }
    }
  }
  
  private void torchFlashRemote(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Message localMessage = Message.obtain(null, -1260100, i, 0, null);
      try
      {
        Log.v(this.TAG, "torchFlashRemote() - Torch: ", Integer.valueOf(i));
        this.m_ServiceMessenger.send(localMessage);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e(this.TAG, "torchFlashRemote() - Send flash message failed");
        localRemoteException.printStackTrace();
      }
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool = true;
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    int i = paramMessage.arg1;
    if (paramMessage.arg2 == 1) {}
    for (;;)
    {
      onSupportedStateResult(i, bool);
      return;
      bool = false;
    }
  }
  
  protected void onDeinitialize()
  {
    try
    {
      getCameraActivity().unbindService(this.m_ServiceConnection);
      super.onDeinitialize();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onDeinitialize() - Error when unbind service", localThrowable);
      }
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    Intent localIntent = new Intent();
    localIntent.setClassName("com.oneplus.camera.service", "com.oneplus.camera.service.CameraService");
    try
    {
      localCameraActivity.bindService(localIntent, this.m_ServiceConnection, 1);
      this.m_LocalMessenger = new Messenger(getHandler());
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "onInitialize() - Error when bind service", localThrowable);
    }
  }
  
  public Handle setBacklightMaxBrightness()
  {
    if (!((Boolean)get(PROP_IS_CONNECTED)).booleanValue())
    {
      Log.w(this.TAG, "setBacklightMaxBrightness() - Service is not connected");
      return null;
    }
    Handle local2 = new Handle("Set max backlight brightness")
    {
      protected void onClose(int paramAnonymousInt)
      {
        if (((Boolean)CameraServiceProxy.this.get(CameraServiceProxy.PROP_IS_CONNECTED)).booleanValue()) {
          CameraServiceProxy.-wrap2(CameraServiceProxy.this, false);
        }
      }
    };
    setBacklightMaxBrightnessRemote(true);
    return local2;
  }
  
  public Handle torchFlash()
  {
    if (!((Boolean)get(PROP_IS_CONNECTED)).booleanValue())
    {
      Log.w(this.TAG, "torchFlash() - Service is not connected");
      return null;
    }
    if (get(PROP_TORCH_FLASH_SUPPORTED_STATE) != SupportedState.SUPPORTED)
    {
      Log.w(this.TAG, "torchFlash() - Torch flash is not suppoerted");
      return null;
    }
    Handle local3 = new Handle("Torch Flash Service")
    {
      protected void onClose(int paramAnonymousInt)
      {
        if (((Boolean)CameraServiceProxy.this.get(CameraServiceProxy.PROP_IS_CONNECTED)).booleanValue()) {
          CameraServiceProxy.-wrap3(CameraServiceProxy.this, false);
        }
      }
    };
    torchFlashRemote(true);
    return local3;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraServiceProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */