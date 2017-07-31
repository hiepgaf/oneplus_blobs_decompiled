package android.net.wifi.nan;

public class WifiNanSubscribeSession
  extends WifiNanSession
{
  public WifiNanSubscribeSession(WifiNanManager paramWifiNanManager, int paramInt)
  {
    super(paramWifiNanManager, paramInt);
  }
  
  public void subscribe(SubscribeData paramSubscribeData, SubscribeSettings paramSubscribeSettings)
  {
    this.mManager.subscribe(this.mSessionId, paramSubscribeData, paramSubscribeSettings);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanSubscribeSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */