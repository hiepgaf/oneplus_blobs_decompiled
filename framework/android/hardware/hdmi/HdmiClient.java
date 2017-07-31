package android.hardware.hdmi;

import android.os.RemoteException;
import android.util.Log;

public abstract class HdmiClient
{
  private static final String TAG = "HdmiClient";
  private IHdmiVendorCommandListener mIHdmiVendorCommandListener;
  final IHdmiControlService mService;
  
  HdmiClient(IHdmiControlService paramIHdmiControlService)
  {
    this.mService = paramIHdmiControlService;
  }
  
  private static IHdmiVendorCommandListener getListenerWrapper(HdmiControlManager.VendorCommandListener paramVendorCommandListener)
  {
    new IHdmiVendorCommandListener.Stub()
    {
      public void onControlStateChanged(boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        this.val$listener.onControlStateChanged(paramAnonymousBoolean, paramAnonymousInt);
      }
      
      public void onReceived(int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte, boolean paramAnonymousBoolean)
      {
        this.val$listener.onReceived(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousArrayOfByte, paramAnonymousBoolean);
      }
    };
  }
  
  public HdmiDeviceInfo getActiveSource()
  {
    try
    {
      HdmiDeviceInfo localHdmiDeviceInfo = this.mService.getActiveSource();
      return localHdmiDeviceInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiClient", "getActiveSource threw exception ", localRemoteException);
    }
    return null;
  }
  
  abstract int getDeviceType();
  
  public void sendKeyEvent(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.sendKeyEvent(getDeviceType(), paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("HdmiClient", "sendKeyEvent threw exception ", localRemoteException);
    }
  }
  
  public void sendVendorCommand(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    try
    {
      this.mService.sendVendorCommand(getDeviceType(), paramInt, paramArrayOfByte, paramBoolean);
      return;
    }
    catch (RemoteException paramArrayOfByte)
    {
      Log.e("HdmiClient", "failed to send vendor command: ", paramArrayOfByte);
    }
  }
  
  public void setVendorCommandListener(HdmiControlManager.VendorCommandListener paramVendorCommandListener)
  {
    if (paramVendorCommandListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    if (this.mIHdmiVendorCommandListener != null) {
      throw new IllegalStateException("listener was already set");
    }
    try
    {
      paramVendorCommandListener = getListenerWrapper(paramVendorCommandListener);
      this.mService.addVendorCommandListener(paramVendorCommandListener, getDeviceType());
      this.mIHdmiVendorCommandListener = paramVendorCommandListener;
      return;
    }
    catch (RemoteException paramVendorCommandListener)
    {
      Log.e("HdmiClient", "failed to set vendor command listener: ", paramVendorCommandListener);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */