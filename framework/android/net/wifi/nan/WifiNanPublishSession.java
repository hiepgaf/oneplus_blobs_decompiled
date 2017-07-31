package android.net.wifi.nan;

public class WifiNanPublishSession
  extends WifiNanSession
{
  public WifiNanPublishSession(WifiNanManager paramWifiNanManager, int paramInt)
  {
    super(paramWifiNanManager, paramInt);
  }
  
  public void publish(PublishData paramPublishData, PublishSettings paramPublishSettings)
  {
    this.mManager.publish(this.mSessionId, paramPublishData, paramPublishSettings);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanPublishSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */