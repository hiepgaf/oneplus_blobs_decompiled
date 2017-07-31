package android.net.wifi.p2p.nsd;

import android.net.wifi.p2p.WifiP2pDevice;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiP2pUpnpServiceResponse
  extends WifiP2pServiceResponse
{
  private List<String> mUniqueServiceNames;
  private int mVersion;
  
  protected WifiP2pUpnpServiceResponse(int paramInt1, int paramInt2, WifiP2pDevice paramWifiP2pDevice, byte[] paramArrayOfByte)
  {
    super(2, paramInt1, paramInt2, paramWifiP2pDevice, paramArrayOfByte);
    if (!parse()) {
      throw new IllegalArgumentException("Malformed upnp service response");
    }
  }
  
  static WifiP2pUpnpServiceResponse newInstance(int paramInt1, int paramInt2, WifiP2pDevice paramWifiP2pDevice, byte[] paramArrayOfByte)
  {
    if (paramInt1 != 0) {
      return new WifiP2pUpnpServiceResponse(paramInt1, paramInt2, paramWifiP2pDevice, null);
    }
    try
    {
      paramWifiP2pDevice = new WifiP2pUpnpServiceResponse(paramInt1, paramInt2, paramWifiP2pDevice, paramArrayOfByte);
      return paramWifiP2pDevice;
    }
    catch (IllegalArgumentException paramWifiP2pDevice)
    {
      paramWifiP2pDevice.printStackTrace();
    }
    return null;
  }
  
  private boolean parse()
  {
    int i = 0;
    if (this.mData == null) {
      return true;
    }
    if (this.mData.length < 1) {
      return false;
    }
    this.mVersion = (this.mData[0] & 0xFF);
    String[] arrayOfString = new String(this.mData, 1, this.mData.length - 1).split(",");
    this.mUniqueServiceNames = new ArrayList();
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      this.mUniqueServiceNames.add(str);
      i += 1;
    }
    return true;
  }
  
  public List<String> getUniqueServiceNames()
  {
    return this.mUniqueServiceNames;
  }
  
  public int getVersion()
  {
    return this.mVersion;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("serviceType:UPnP(").append(this.mServiceType).append(")");
    localStringBuffer.append(" status:").append(WifiP2pServiceResponse.Status.toString(this.mStatus));
    localStringBuffer.append(" srcAddr:").append(this.mDevice.deviceAddress);
    localStringBuffer.append(" version:").append(String.format("%02x", new Object[] { Integer.valueOf(this.mVersion) }));
    if (this.mUniqueServiceNames != null)
    {
      Iterator localIterator = this.mUniqueServiceNames.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localStringBuffer.append(" usn:").append(str);
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pUpnpServiceResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */