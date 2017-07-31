package android.net;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Objects;

public class DhcpResults
  extends StaticIpConfiguration
{
  public static final Parcelable.Creator<DhcpResults> CREATOR = new Parcelable.Creator()
  {
    public DhcpResults createFromParcel(Parcel paramAnonymousParcel)
    {
      DhcpResults localDhcpResults = new DhcpResults();
      DhcpResults.-wrap0(localDhcpResults, paramAnonymousParcel);
      return localDhcpResults;
    }
    
    public DhcpResults[] newArray(int paramAnonymousInt)
    {
      return new DhcpResults[paramAnonymousInt];
    }
  };
  private static final String TAG = "DhcpResults";
  public int leaseDuration;
  public int mtu;
  public Inet4Address serverAddress;
  public String vendorInfo;
  
  public DhcpResults() {}
  
  public DhcpResults(DhcpResults paramDhcpResults)
  {
    super(paramDhcpResults);
    if (paramDhcpResults != null)
    {
      this.serverAddress = paramDhcpResults.serverAddress;
      this.vendorInfo = paramDhcpResults.vendorInfo;
      this.leaseDuration = paramDhcpResults.leaseDuration;
      this.mtu = paramDhcpResults.mtu;
    }
  }
  
  public DhcpResults(StaticIpConfiguration paramStaticIpConfiguration)
  {
    super(paramStaticIpConfiguration);
  }
  
  private static void readFromParcel(DhcpResults paramDhcpResults, Parcel paramParcel)
  {
    StaticIpConfiguration.readFromParcel(paramDhcpResults, paramParcel);
    paramDhcpResults.leaseDuration = paramParcel.readInt();
    paramDhcpResults.mtu = paramParcel.readInt();
    paramDhcpResults.serverAddress = ((Inet4Address)NetworkUtils.unparcelInetAddress(paramParcel));
    paramDhcpResults.vendorInfo = paramParcel.readString();
  }
  
  public boolean addDns(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {}
    try
    {
      this.dnsServers.add(NetworkUtils.numericToInetAddress(paramString));
      return false;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.e("DhcpResults", "addDns failed with addrString " + paramString);
    }
    return true;
  }
  
  public void clear()
  {
    super.clear();
    this.vendorInfo = null;
    this.leaseDuration = 0;
    this.mtu = 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DhcpResults)) {
      return false;
    }
    DhcpResults localDhcpResults = (DhcpResults)paramObject;
    if ((super.equals((StaticIpConfiguration)paramObject)) && (Objects.equals(this.serverAddress, localDhcpResults.serverAddress)) && (Objects.equals(this.vendorInfo, localDhcpResults.vendorInfo)) && (this.leaseDuration == localDhcpResults.leaseDuration)) {
      return this.mtu == localDhcpResults.mtu;
    }
    return false;
  }
  
  public boolean hasMeteredHint()
  {
    if (this.vendorInfo != null) {
      return this.vendorInfo.contains("ANDROID_METERED");
    }
    return false;
  }
  
  public void setDomains(String paramString)
  {
    this.domains = paramString;
  }
  
  public boolean setGateway(String paramString)
  {
    try
    {
      this.gateway = NetworkUtils.numericToInetAddress(paramString);
      return false;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.e("DhcpResults", "setGateway failed with addrString " + paramString);
    }
    return true;
  }
  
  public boolean setIpAddress(String paramString, int paramInt)
  {
    try
    {
      this.ipAddress = new LinkAddress((Inet4Address)NetworkUtils.numericToInetAddress(paramString), paramInt);
      return false;
    }
    catch (IllegalArgumentException|ClassCastException localIllegalArgumentException)
    {
      Log.e("DhcpResults", "setIpAddress failed with addrString " + paramString + "/" + paramInt);
    }
    return true;
  }
  
  public void setLeaseDuration(int paramInt)
  {
    this.leaseDuration = paramInt;
  }
  
  public boolean setServerAddress(String paramString)
  {
    try
    {
      this.serverAddress = ((Inet4Address)NetworkUtils.numericToInetAddress(paramString));
      return false;
    }
    catch (IllegalArgumentException|ClassCastException localIllegalArgumentException)
    {
      Log.e("DhcpResults", "setServerAddress failed with addrString " + paramString);
    }
    return true;
  }
  
  public void setVendorInfo(String paramString)
  {
    this.vendorInfo = paramString;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(super.toString());
    localStringBuffer.append(" DHCP server ").append(this.serverAddress);
    localStringBuffer.append(" Vendor info ").append(this.vendorInfo);
    localStringBuffer.append(" lease ").append(this.leaseDuration).append(" seconds");
    if (this.mtu != 0) {
      localStringBuffer.append(" MTU ").append(this.mtu);
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.leaseDuration);
    paramParcel.writeInt(this.mtu);
    NetworkUtils.parcelInetAddress(paramParcel, this.serverAddress, paramInt);
    paramParcel.writeString(this.vendorInfo);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/DhcpResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */