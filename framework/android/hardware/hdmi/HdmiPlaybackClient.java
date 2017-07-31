package android.hardware.hdmi;

import android.os.RemoteException;
import android.util.Log;

public final class HdmiPlaybackClient
  extends HdmiClient
{
  private static final int ADDR_TV = 0;
  private static final String TAG = "HdmiPlaybackClient";
  
  HdmiPlaybackClient(IHdmiControlService paramIHdmiControlService)
  {
    super(paramIHdmiControlService);
  }
  
  private IHdmiControlCallback getCallbackWrapper(final DisplayStatusCallback paramDisplayStatusCallback)
  {
    new IHdmiControlCallback.Stub()
    {
      public void onComplete(int paramAnonymousInt)
      {
        paramDisplayStatusCallback.onComplete(paramAnonymousInt);
      }
    };
  }
  
  private IHdmiControlCallback getCallbackWrapper(final OneTouchPlayCallback paramOneTouchPlayCallback)
  {
    new IHdmiControlCallback.Stub()
    {
      public void onComplete(int paramAnonymousInt)
      {
        paramOneTouchPlayCallback.onComplete(paramAnonymousInt);
      }
    };
  }
  
  public int getDeviceType()
  {
    return 4;
  }
  
  public void oneTouchPlay(OneTouchPlayCallback paramOneTouchPlayCallback)
  {
    try
    {
      this.mService.oneTouchPlay(getCallbackWrapper(paramOneTouchPlayCallback));
      return;
    }
    catch (RemoteException paramOneTouchPlayCallback)
    {
      Log.e("HdmiPlaybackClient", "oneTouchPlay threw exception ", paramOneTouchPlayCallback);
    }
  }
  
  public void queryDisplayStatus(DisplayStatusCallback paramDisplayStatusCallback)
  {
    try
    {
      this.mService.queryDisplayStatus(getCallbackWrapper(paramDisplayStatusCallback));
      return;
    }
    catch (RemoteException paramDisplayStatusCallback)
    {
      Log.e("HdmiPlaybackClient", "queryDisplayStatus threw exception ", paramDisplayStatusCallback);
    }
  }
  
  public void sendStandby()
  {
    try
    {
      this.mService.sendStandby(getDeviceType(), HdmiDeviceInfo.idForCecDevice(0));
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiPlaybackClient", "sendStandby threw exception ", localRemoteException);
    }
  }
  
  public static abstract interface DisplayStatusCallback
  {
    public abstract void onComplete(int paramInt);
  }
  
  public static abstract interface OneTouchPlayCallback
  {
    public abstract void onComplete(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiPlaybackClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */