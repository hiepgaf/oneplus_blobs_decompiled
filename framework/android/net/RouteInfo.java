package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public final class RouteInfo
  implements Parcelable
{
  public static final Parcelable.Creator<RouteInfo> CREATOR = new Parcelable.Creator()
  {
    public RouteInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      IpPrefix localIpPrefix = (IpPrefix)paramAnonymousParcel.readParcelable(null);
      Object localObject1 = null;
      Object localObject2 = paramAnonymousParcel.createByteArray();
      try
      {
        localObject2 = InetAddress.getByAddress((byte[])localObject2);
        localObject1 = localObject2;
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;) {}
      }
      return new RouteInfo(localIpPrefix, (InetAddress)localObject1, paramAnonymousParcel.readString(), paramAnonymousParcel.readInt());
    }
    
    public RouteInfo[] newArray(int paramAnonymousInt)
    {
      return new RouteInfo[paramAnonymousInt];
    }
  };
  public static final int RTN_THROW = 9;
  public static final int RTN_UNICAST = 1;
  public static final int RTN_UNREACHABLE = 7;
  private final IpPrefix mDestination;
  private final InetAddress mGateway;
  private final boolean mHasGateway;
  private final String mInterface;
  private final boolean mIsHost;
  private final int mType;
  
  public RouteInfo(IpPrefix paramIpPrefix)
  {
    this(paramIpPrefix, null, null);
  }
  
  public RouteInfo(IpPrefix paramIpPrefix, int paramInt)
  {
    this(paramIpPrefix, null, null, paramInt);
  }
  
  public RouteInfo(IpPrefix paramIpPrefix, InetAddress paramInetAddress)
  {
    this(paramIpPrefix, paramInetAddress, null);
  }
  
  public RouteInfo(IpPrefix paramIpPrefix, InetAddress paramInetAddress, String paramString)
  {
    this(paramIpPrefix, paramInetAddress, paramString, 1);
  }
  
  public RouteInfo(IpPrefix paramIpPrefix, InetAddress paramInetAddress, String paramString, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unknown route type " + paramInt);
    }
    IpPrefix localIpPrefix = paramIpPrefix;
    if (paramIpPrefix == null)
    {
      if (paramInetAddress == null) {
        break label181;
      }
      if ((paramInetAddress instanceof Inet4Address)) {
        localIpPrefix = new IpPrefix(Inet4Address.ANY, 0);
      }
    }
    else
    {
      paramIpPrefix = paramInetAddress;
      if (paramInetAddress == null)
      {
        if (!(localIpPrefix.getAddress() instanceof Inet4Address)) {
          break label217;
        }
        paramIpPrefix = Inet4Address.ANY;
      }
      label124:
      if (!paramIpPrefix.isAnyLocalAddress()) {
        break label224;
      }
      label131:
      this.mHasGateway = bool;
      if ((!(localIpPrefix.getAddress() instanceof Inet4Address)) || ((paramIpPrefix instanceof Inet4Address))) {
        break label230;
      }
    }
    label181:
    label217:
    label224:
    label230:
    while (((localIpPrefix.getAddress() instanceof Inet6Address)) && (!(paramIpPrefix instanceof Inet6Address)))
    {
      throw new IllegalArgumentException("address family mismatch in RouteInfo constructor");
      localIpPrefix = new IpPrefix(Inet6Address.ANY, 0);
      break;
      throw new IllegalArgumentException("Invalid arguments passed in: " + paramInetAddress + "," + paramIpPrefix);
      paramIpPrefix = Inet6Address.ANY;
      break label124;
      bool = true;
      break label131;
    }
    this.mDestination = localIpPrefix;
    this.mGateway = paramIpPrefix;
    this.mInterface = paramString;
    this.mType = paramInt;
    this.mIsHost = isHost();
  }
  
  public RouteInfo(LinkAddress paramLinkAddress)
  {
    this(paramLinkAddress, null, null);
  }
  
  public RouteInfo(LinkAddress paramLinkAddress, InetAddress paramInetAddress)
  {
    this(paramLinkAddress, paramInetAddress, null);
  }
  
  public RouteInfo(LinkAddress paramLinkAddress, InetAddress paramInetAddress, String paramString) {}
  
  public RouteInfo(InetAddress paramInetAddress)
  {
    this((IpPrefix)null, paramInetAddress, null);
  }
  
  private boolean isHost()
  {
    if (((this.mDestination.getAddress() instanceof Inet4Address)) && (this.mDestination.getPrefixLength() == 32)) {}
    do
    {
      return true;
      if (!(this.mDestination.getAddress() instanceof Inet6Address)) {
        break;
      }
    } while (this.mDestination.getPrefixLength() == 128);
    return false;
    return false;
  }
  
  public static RouteInfo makeHostRoute(InetAddress paramInetAddress, String paramString)
  {
    return makeHostRoute(paramInetAddress, null, paramString);
  }
  
  public static RouteInfo makeHostRoute(InetAddress paramInetAddress1, InetAddress paramInetAddress2, String paramString)
  {
    if (paramInetAddress1 == null) {
      return null;
    }
    if ((paramInetAddress1 instanceof Inet4Address)) {
      return new RouteInfo(new IpPrefix(paramInetAddress1, 32), paramInetAddress2, paramString);
    }
    return new RouteInfo(new IpPrefix(paramInetAddress1, 128), paramInetAddress2, paramString);
  }
  
  public static RouteInfo selectBestRoute(Collection<RouteInfo> paramCollection, InetAddress paramInetAddress)
  {
    if ((paramCollection == null) || (paramInetAddress == null)) {
      return null;
    }
    RouteInfo localRouteInfo = null;
    Iterator localIterator = paramCollection.iterator();
    for (paramCollection = localRouteInfo; localIterator.hasNext(); paramCollection = localRouteInfo)
    {
      label21:
      localRouteInfo = (RouteInfo)localIterator.next();
      if ((!NetworkUtils.addressTypeMatches(localRouteInfo.mDestination.getAddress(), paramInetAddress)) || ((paramCollection != null) && (paramCollection.mDestination.getPrefixLength() >= localRouteInfo.mDestination.getPrefixLength())) || (!localRouteInfo.matches(paramInetAddress))) {
        break label21;
      }
    }
    return paramCollection;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof RouteInfo)) {
      return false;
    }
    paramObject = (RouteInfo)paramObject;
    if ((Objects.equals(this.mDestination, ((RouteInfo)paramObject).getDestination())) && (Objects.equals(this.mGateway, ((RouteInfo)paramObject).getGateway())) && (Objects.equals(this.mInterface, ((RouteInfo)paramObject).getInterface()))) {
      return this.mType == ((RouteInfo)paramObject).getType();
    }
    return false;
  }
  
  public IpPrefix getDestination()
  {
    return this.mDestination;
  }
  
  public LinkAddress getDestinationLinkAddress()
  {
    return new LinkAddress(this.mDestination.getAddress(), this.mDestination.getPrefixLength());
  }
  
  public InetAddress getGateway()
  {
    return this.mGateway;
  }
  
  public String getInterface()
  {
    return this.mInterface;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public boolean hasGateway()
  {
    return this.mHasGateway;
  }
  
  public int hashCode()
  {
    int j = 0;
    int k = this.mDestination.hashCode();
    int i;
    if (this.mGateway == null)
    {
      i = 0;
      if (this.mInterface != null) {
        break label57;
      }
    }
    for (;;)
    {
      return i + k * 41 + j + this.mType * 71;
      i = this.mGateway.hashCode() * 47;
      break;
      label57:
      j = this.mInterface.hashCode() * 67;
    }
  }
  
  public boolean isDefaultRoute()
  {
    return (this.mType == 1) && (this.mDestination.getPrefixLength() == 0);
  }
  
  public boolean isHostRoute()
  {
    return this.mIsHost;
  }
  
  public boolean isIPv4Default()
  {
    if (isDefaultRoute()) {
      return this.mDestination.getAddress() instanceof Inet4Address;
    }
    return false;
  }
  
  public boolean isIPv6Default()
  {
    if (isDefaultRoute()) {
      return this.mDestination.getAddress() instanceof Inet6Address;
    }
    return false;
  }
  
  public boolean matches(InetAddress paramInetAddress)
  {
    return this.mDestination.contains(paramInetAddress);
  }
  
  public String toString()
  {
    Object localObject1 = "";
    if (this.mDestination != null) {
      localObject1 = this.mDestination.toString();
    }
    if (this.mType == 7) {
      localObject1 = (String)localObject1 + " unreachable";
    }
    Object localObject2;
    do
    {
      return (String)localObject1;
      if (this.mType == 9) {
        return (String)localObject1 + " throw";
      }
      localObject2 = (String)localObject1 + " ->";
      localObject1 = localObject2;
      if (this.mGateway != null) {
        localObject1 = (String)localObject2 + " " + this.mGateway.getHostAddress();
      }
      localObject2 = localObject1;
      if (this.mInterface != null) {
        localObject2 = (String)localObject1 + " " + this.mInterface;
      }
      localObject1 = localObject2;
    } while (this.mType == 1);
    return (String)localObject2 + " unknown type " + this.mType;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    byte[] arrayOfByte = null;
    paramParcel.writeParcelable(this.mDestination, paramInt);
    if (this.mGateway == null) {}
    for (;;)
    {
      paramParcel.writeByteArray(arrayOfByte);
      paramParcel.writeString(this.mInterface);
      paramParcel.writeInt(this.mType);
      return;
      arrayOfByte = this.mGateway.getAddress();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/RouteInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */