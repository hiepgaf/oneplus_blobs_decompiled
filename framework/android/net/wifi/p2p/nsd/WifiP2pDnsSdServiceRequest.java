package android.net.wifi.p2p.nsd;

public class WifiP2pDnsSdServiceRequest
  extends WifiP2pServiceRequest
{
  private WifiP2pDnsSdServiceRequest()
  {
    super(1, null);
  }
  
  private WifiP2pDnsSdServiceRequest(String paramString)
  {
    super(1, paramString);
  }
  
  private WifiP2pDnsSdServiceRequest(String paramString, int paramInt1, int paramInt2)
  {
    super(1, WifiP2pDnsSdServiceInfo.createRequest(paramString, paramInt1, paramInt2));
  }
  
  public static WifiP2pDnsSdServiceRequest newInstance()
  {
    return new WifiP2pDnsSdServiceRequest();
  }
  
  public static WifiP2pDnsSdServiceRequest newInstance(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("service type cannot be null");
    }
    return new WifiP2pDnsSdServiceRequest(paramString + ".local.", 12, 1);
  }
  
  public static WifiP2pDnsSdServiceRequest newInstance(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new IllegalArgumentException("instance name or service type cannot be null");
    }
    return new WifiP2pDnsSdServiceRequest(paramString1 + "." + paramString2 + ".local.", 16, 1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pDnsSdServiceRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */