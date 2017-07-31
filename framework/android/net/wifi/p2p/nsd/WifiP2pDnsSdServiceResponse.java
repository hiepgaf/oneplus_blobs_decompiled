package android.net.wifi.p2p.nsd;

import android.net.wifi.p2p.WifiP2pDevice;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WifiP2pDnsSdServiceResponse
  extends WifiP2pServiceResponse
{
  private static final Map<Integer, String> sVmpack = new HashMap();
  private String mDnsQueryName;
  private int mDnsType;
  private String mInstanceName;
  private final HashMap<String, String> mTxtRecord = new HashMap();
  private int mVersion;
  
  static
  {
    sVmpack.put(Integer.valueOf(12), "_tcp.local.");
    sVmpack.put(Integer.valueOf(17), "local.");
    sVmpack.put(Integer.valueOf(28), "_udp.local.");
  }
  
  protected WifiP2pDnsSdServiceResponse(int paramInt1, int paramInt2, WifiP2pDevice paramWifiP2pDevice, byte[] paramArrayOfByte)
  {
    super(1, paramInt1, paramInt2, paramWifiP2pDevice, paramArrayOfByte);
    if (!parse()) {
      throw new IllegalArgumentException("Malformed bonjour service response");
    }
  }
  
  static WifiP2pDnsSdServiceResponse newInstance(int paramInt1, int paramInt2, WifiP2pDevice paramWifiP2pDevice, byte[] paramArrayOfByte)
  {
    if (paramInt1 != 0) {
      return new WifiP2pDnsSdServiceResponse(paramInt1, paramInt2, paramWifiP2pDevice, null);
    }
    try
    {
      paramWifiP2pDevice = new WifiP2pDnsSdServiceResponse(paramInt1, paramInt2, paramWifiP2pDevice, paramArrayOfByte);
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
    if (this.mData == null) {
      return true;
    }
    Object localObject = new DataInputStream(new ByteArrayInputStream(this.mData));
    this.mDnsQueryName = readDnsName((DataInputStream)localObject);
    if (this.mDnsQueryName == null) {
      return false;
    }
    try
    {
      this.mDnsType = ((DataInputStream)localObject).readUnsignedShort();
      this.mVersion = ((DataInputStream)localObject).readUnsignedByte();
      if (this.mDnsType != 12) {
        break label131;
      }
      localObject = readDnsName((DataInputStream)localObject);
      if (localObject == null) {
        return false;
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      return false;
    }
    if (localIOException.length() <= this.mDnsQueryName.length()) {
      return false;
    }
    this.mInstanceName = localIOException.substring(0, localIOException.length() - this.mDnsQueryName.length() - 1);
    return true;
    label131:
    if (this.mDnsType == 16) {
      return readTxtData(localIOException);
    }
    return false;
  }
  
  private String readDnsName(DataInputStream paramDataInputStream)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    HashMap localHashMap = new HashMap(sVmpack);
    if (this.mDnsQueryName != null) {
      localHashMap.put(Integer.valueOf(39), this.mDnsQueryName);
    }
    try
    {
      for (;;)
      {
        int i = paramDataInputStream.readUnsignedByte();
        if (i == 0) {
          return localStringBuffer.toString();
        }
        if (i == 192)
        {
          paramDataInputStream = (String)localHashMap.get(Integer.valueOf(paramDataInputStream.readUnsignedByte()));
          if (paramDataInputStream == null) {
            return null;
          }
          localStringBuffer.append(paramDataInputStream);
          return localStringBuffer.toString();
        }
        byte[] arrayOfByte = new byte[i];
        paramDataInputStream.readFully(arrayOfByte);
        localStringBuffer.append(new String(arrayOfByte));
        localStringBuffer.append(".");
      }
      return null;
    }
    catch (IOException paramDataInputStream)
    {
      paramDataInputStream.printStackTrace();
    }
  }
  
  private boolean readTxtData(DataInputStream paramDataInputStream)
  {
    try
    {
      while (paramDataInputStream.available() > 0)
      {
        int i = paramDataInputStream.readUnsignedByte();
        if (i == 0) {
          return true;
        }
        Object localObject = new byte[i];
        paramDataInputStream.readFully((byte[])localObject);
        localObject = new String((byte[])localObject).split("=");
        if (localObject.length != 2) {
          return false;
        }
        this.mTxtRecord.put(localObject[0], localObject[1]);
      }
      return true;
    }
    catch (IOException paramDataInputStream)
    {
      paramDataInputStream.printStackTrace();
      return false;
    }
  }
  
  public String getDnsQueryName()
  {
    return this.mDnsQueryName;
  }
  
  public int getDnsType()
  {
    return this.mDnsType;
  }
  
  public String getInstanceName()
  {
    return this.mInstanceName;
  }
  
  public Map<String, String> getTxtRecord()
  {
    return this.mTxtRecord;
  }
  
  public int getVersion()
  {
    return this.mVersion;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("serviceType:DnsSd(").append(this.mServiceType).append(")");
    localStringBuffer.append(" status:").append(WifiP2pServiceResponse.Status.toString(this.mStatus));
    localStringBuffer.append(" srcAddr:").append(this.mDevice.deviceAddress);
    localStringBuffer.append(" version:").append(String.format("%02x", new Object[] { Integer.valueOf(this.mVersion) }));
    localStringBuffer.append(" dnsName:").append(this.mDnsQueryName);
    localStringBuffer.append(" TxtRecord:");
    Iterator localIterator = this.mTxtRecord.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuffer.append(" key:").append(str).append(" value:").append((String)this.mTxtRecord.get(str));
    }
    if (this.mInstanceName != null) {
      localStringBuffer.append(" InsName:").append(this.mInstanceName);
    }
    return localStringBuffer.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pDnsSdServiceResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */