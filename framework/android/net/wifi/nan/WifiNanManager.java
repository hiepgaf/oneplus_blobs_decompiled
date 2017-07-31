package android.net.wifi.nan;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

public class WifiNanManager
{
  private static final boolean DBG = false;
  private static final String TAG = "WifiNanManager";
  private static final boolean VDBG = false;
  private IBinder mBinder;
  private IWifiNanManager mService;
  
  public WifiNanManager(IWifiNanManager paramIWifiNanManager)
  {
    this.mService = paramIWifiNanManager;
  }
  
  public void connect(WifiNanEventListener paramWifiNanEventListener, int paramInt)
  {
    if (paramWifiNanEventListener == null) {
      try
      {
        throw new IllegalArgumentException("Invalid listener - must not be null");
      }
      catch (RemoteException paramWifiNanEventListener)
      {
        throw paramWifiNanEventListener.rethrowFromSystemServer();
      }
    }
    if (this.mBinder == null) {
      this.mBinder = new Binder();
    }
    this.mService.connect(this.mBinder, paramWifiNanEventListener.callback, paramInt);
  }
  
  public void destroySession(int paramInt)
  {
    try
    {
      this.mService.destroySession(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void disconnect()
  {
    try
    {
      this.mService.disconnect(this.mBinder);
      this.mBinder = null;
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiNanPublishSession publish(PublishData paramPublishData, PublishSettings paramPublishSettings, WifiNanSessionListener paramWifiNanSessionListener, int paramInt)
  {
    return publishRaw(paramPublishData, paramPublishSettings, paramWifiNanSessionListener, paramInt | 0xF5);
  }
  
  public void publish(int paramInt, PublishData paramPublishData, PublishSettings paramPublishSettings)
  {
    if ((paramPublishSettings.mPublishType == 0) && (paramPublishData.mRxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid publish data & settings: UNSOLICITED publishes (active) can't have an Rx filter");
    }
    if ((paramPublishSettings.mPublishType == 1) && (paramPublishData.mTxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid publish data & settings: SOLICITED publishes (passive) can't have a Tx filter");
    }
    try
    {
      this.mService.publish(paramInt, paramPublishData, paramPublishSettings);
      return;
    }
    catch (RemoteException paramPublishData)
    {
      throw paramPublishData.rethrowFromSystemServer();
    }
  }
  
  public WifiNanPublishSession publishRaw(PublishData paramPublishData, PublishSettings paramPublishSettings, WifiNanSessionListener paramWifiNanSessionListener, int paramInt)
  {
    if ((paramPublishSettings.mPublishType == 0) && (paramPublishData.mRxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid publish data & settings: UNSOLICITED publishes (active) can't have an Rx filter");
    }
    if ((paramPublishSettings.mPublishType == 1) && (paramPublishData.mTxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid publish data & settings: SOLICITED publishes (passive) can't have a Tx filter");
    }
    if (paramWifiNanSessionListener == null) {
      throw new IllegalArgumentException("Invalid listener - must not be null");
    }
    try
    {
      paramInt = this.mService.createSession(paramWifiNanSessionListener.callback, paramInt);
      this.mService.publish(paramInt, paramPublishData, paramPublishSettings);
      return new WifiNanPublishSession(this, paramInt);
    }
    catch (RemoteException paramPublishData)
    {
      throw paramPublishData.rethrowFromSystemServer();
    }
  }
  
  public void requestConfig(ConfigRequest paramConfigRequest)
  {
    try
    {
      this.mService.requestConfig(paramConfigRequest);
      return;
    }
    catch (RemoteException paramConfigRequest)
    {
      throw paramConfigRequest.rethrowFromSystemServer();
    }
  }
  
  public void sendMessage(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4)
  {
    try
    {
      this.mService.sendMessage(paramInt1, paramInt2, paramArrayOfByte, paramInt3, paramInt4);
      return;
    }
    catch (RemoteException paramArrayOfByte)
    {
      throw paramArrayOfByte.rethrowFromSystemServer();
    }
  }
  
  public void stopSession(int paramInt)
  {
    try
    {
      this.mService.stopSession(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public WifiNanSubscribeSession subscribe(SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings, WifiNanSessionListener paramWifiNanSessionListener, int paramInt)
  {
    return subscribeRaw(paramSubscribeData, paramSubscribeSettings, paramWifiNanSessionListener, paramInt | 0xF5);
  }
  
  public void subscribe(int paramInt, SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings)
  {
    if ((paramSubscribeSettings.mSubscribeType == 1) && (paramSubscribeData.mRxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid subscribe data & settings: ACTIVE subscribes can't have an Rx filter");
    }
    if ((paramSubscribeSettings.mSubscribeType == 0) && (paramSubscribeData.mTxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid subscribe data & settings: PASSIVE subscribes can't have a Tx filter");
    }
    try
    {
      this.mService.subscribe(paramInt, paramSubscribeData, paramSubscribeSettings);
      return;
    }
    catch (RemoteException paramSubscribeData)
    {
      throw paramSubscribeData.rethrowFromSystemServer();
    }
  }
  
  public WifiNanSubscribeSession subscribeRaw(SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings, WifiNanSessionListener paramWifiNanSessionListener, int paramInt)
  {
    if ((paramSubscribeSettings.mSubscribeType == 1) && (paramSubscribeData.mRxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid subscribe data & settings: ACTIVE subscribes can't have an Rx filter");
    }
    if ((paramSubscribeSettings.mSubscribeType == 0) && (paramSubscribeData.mTxFilterLength != 0)) {
      throw new IllegalArgumentException("Invalid subscribe data & settings: PASSIVE subscribes can't have a Tx filter");
    }
    try
    {
      paramInt = this.mService.createSession(paramWifiNanSessionListener.callback, paramInt);
      this.mService.subscribe(paramInt, paramSubscribeData, paramSubscribeSettings);
      return new WifiNanSubscribeSession(this, paramInt);
    }
    catch (RemoteException paramSubscribeData)
    {
      throw paramSubscribeData.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */