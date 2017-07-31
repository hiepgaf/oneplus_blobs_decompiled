package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StaticIpConfiguration
  implements Parcelable
{
  public static Parcelable.Creator<StaticIpConfiguration> CREATOR = new Parcelable.Creator()
  {
    public StaticIpConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      StaticIpConfiguration localStaticIpConfiguration = new StaticIpConfiguration();
      StaticIpConfiguration.readFromParcel(localStaticIpConfiguration, paramAnonymousParcel);
      return localStaticIpConfiguration;
    }
    
    public StaticIpConfiguration[] newArray(int paramAnonymousInt)
    {
      return new StaticIpConfiguration[paramAnonymousInt];
    }
  };
  public final ArrayList<InetAddress> dnsServers = new ArrayList();
  public String domains;
  public InetAddress gateway;
  public LinkAddress ipAddress;
  
  public StaticIpConfiguration() {}
  
  public StaticIpConfiguration(StaticIpConfiguration paramStaticIpConfiguration)
  {
    this();
    if (paramStaticIpConfiguration != null)
    {
      this.ipAddress = paramStaticIpConfiguration.ipAddress;
      this.gateway = paramStaticIpConfiguration.gateway;
      this.dnsServers.addAll(paramStaticIpConfiguration.dnsServers);
      this.domains = paramStaticIpConfiguration.domains;
    }
  }
  
  protected static void readFromParcel(StaticIpConfiguration paramStaticIpConfiguration, Parcel paramParcel)
  {
    paramStaticIpConfiguration.ipAddress = ((LinkAddress)paramParcel.readParcelable(null));
    paramStaticIpConfiguration.gateway = NetworkUtils.unparcelInetAddress(paramParcel);
    paramStaticIpConfiguration.dnsServers.clear();
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      paramStaticIpConfiguration.dnsServers.add(NetworkUtils.unparcelInetAddress(paramParcel));
      i += 1;
    }
    paramStaticIpConfiguration.domains = paramParcel.readString();
  }
  
  public void clear()
  {
    this.ipAddress = null;
    this.gateway = null;
    this.dnsServers.clear();
    this.domains = null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StaticIpConfiguration)) {
      return false;
    }
    paramObject = (StaticIpConfiguration)paramObject;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if (Objects.equals(this.ipAddress, ((StaticIpConfiguration)paramObject).ipAddress))
      {
        bool1 = bool2;
        if (Objects.equals(this.gateway, ((StaticIpConfiguration)paramObject).gateway))
        {
          bool1 = bool2;
          if (this.dnsServers.equals(((StaticIpConfiguration)paramObject).dnsServers)) {
            bool1 = Objects.equals(this.domains, ((StaticIpConfiguration)paramObject).domains);
          }
        }
      }
    }
    return bool1;
  }
  
  public List<RouteInfo> getRoutes(String paramString)
  {
    ArrayList localArrayList = new ArrayList(3);
    if (this.ipAddress != null)
    {
      RouteInfo localRouteInfo = new RouteInfo(this.ipAddress, null, paramString);
      localArrayList.add(localRouteInfo);
      if ((this.gateway != null) && (!localRouteInfo.matches(this.gateway))) {
        break label88;
      }
    }
    for (;;)
    {
      if (this.gateway != null) {
        localArrayList.add(new RouteInfo((IpPrefix)null, this.gateway, paramString));
      }
      return localArrayList;
      label88:
      localArrayList.add(RouteInfo.makeHostRoute(this.gateway, paramString));
    }
  }
  
  public int hashCode()
  {
    int k = 0;
    int i;
    int j;
    if (this.ipAddress == null)
    {
      i = 0;
      if (this.gateway != null) {
        break label65;
      }
      j = 0;
      label20:
      if (this.domains != null) {
        break label76;
      }
    }
    for (;;)
    {
      return (((i + 611) * 47 + j) * 47 + k) * 47 + this.dnsServers.hashCode();
      i = this.ipAddress.hashCode();
      break;
      label65:
      j = this.gateway.hashCode();
      break label20;
      label76:
      k = this.domains.hashCode();
    }
  }
  
  public LinkProperties toLinkProperties(String paramString)
  {
    LinkProperties localLinkProperties = new LinkProperties();
    localLinkProperties.setInterfaceName(paramString);
    if (this.ipAddress != null) {
      localLinkProperties.addLinkAddress(this.ipAddress);
    }
    paramString = getRoutes(paramString).iterator();
    while (paramString.hasNext()) {
      localLinkProperties.addRoute((RouteInfo)paramString.next());
    }
    paramString = this.dnsServers.iterator();
    while (paramString.hasNext()) {
      localLinkProperties.addDnsServer((InetAddress)paramString.next());
    }
    localLinkProperties.setDomains(this.domains);
    return localLinkProperties;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("IP address ");
    if (this.ipAddress != null) {
      localStringBuffer.append(this.ipAddress).append(" ");
    }
    localStringBuffer.append("Gateway ");
    if (this.gateway != null) {
      localStringBuffer.append(this.gateway.getHostAddress()).append(" ");
    }
    localStringBuffer.append(" DNS servers: [");
    Iterator localIterator = this.dnsServers.iterator();
    while (localIterator.hasNext())
    {
      InetAddress localInetAddress = (InetAddress)localIterator.next();
      localStringBuffer.append(" ").append(localInetAddress.getHostAddress());
    }
    localStringBuffer.append(" ] Domains ");
    if (this.domains != null) {
      localStringBuffer.append(this.domains);
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.ipAddress, paramInt);
    NetworkUtils.parcelInetAddress(paramParcel, this.gateway, paramInt);
    paramParcel.writeInt(this.dnsServers.size());
    Iterator localIterator = this.dnsServers.iterator();
    while (localIterator.hasNext()) {
      NetworkUtils.parcelInetAddress(paramParcel, (InetAddress)localIterator.next(), paramInt);
    }
    paramParcel.writeString(this.domains);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/StaticIpConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */