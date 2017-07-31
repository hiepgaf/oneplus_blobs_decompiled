package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.system.OsConstants;
import android.util.Pair;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

public class LinkAddress
  implements Parcelable
{
  public static final Parcelable.Creator<LinkAddress> CREATOR = new Parcelable.Creator()
  {
    public LinkAddress createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      try
      {
        InetAddress localInetAddress = InetAddress.getByAddress(paramAnonymousParcel.createByteArray());
        localObject = localInetAddress;
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;) {}
      }
      return new LinkAddress((InetAddress)localObject, paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public LinkAddress[] newArray(int paramAnonymousInt)
    {
      return new LinkAddress[paramAnonymousInt];
    }
  };
  private InetAddress address;
  private int flags;
  private int prefixLength;
  private int scope;
  
  public LinkAddress(String paramString)
  {
    this(paramString, 0, 0);
    this.scope = scopeForUnicastAddress(this.address);
  }
  
  public LinkAddress(String paramString, int paramInt1, int paramInt2)
  {
    paramString = NetworkUtils.parseIpAndMask(paramString);
    init((InetAddress)paramString.first, ((Integer)paramString.second).intValue(), paramInt1, paramInt2);
  }
  
  public LinkAddress(InetAddress paramInetAddress, int paramInt)
  {
    this(paramInetAddress, paramInt, 0, 0);
    this.scope = scopeForUnicastAddress(paramInetAddress);
  }
  
  public LinkAddress(InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3)
  {
    init(paramInetAddress, paramInt1, paramInt2, paramInt3);
  }
  
  public LinkAddress(InterfaceAddress paramInterfaceAddress)
  {
    this(paramInterfaceAddress.getAddress(), paramInterfaceAddress.getNetworkPrefixLength());
  }
  
  private void init(InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInetAddress == null) || (paramInetAddress.isMulticastAddress()) || (paramInt1 < 0)) {}
    while ((((paramInetAddress instanceof Inet4Address)) && (paramInt1 > 32)) || (paramInt1 > 128)) {
      throw new IllegalArgumentException("Bad LinkAddress params " + paramInetAddress + "/" + paramInt1);
    }
    this.address = paramInetAddress;
    this.prefixLength = paramInt1;
    this.flags = paramInt2;
    this.scope = paramInt3;
  }
  
  private boolean isIPv6ULA()
  {
    boolean bool = false;
    if ((this.address != null) && ((this.address instanceof Inet6Address)))
    {
      if ((this.address.getAddress()[0] & 0xFFFFFFFE) == -4) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  static int scopeForUnicastAddress(InetAddress paramInetAddress)
  {
    if (paramInetAddress.isAnyLocalAddress()) {
      return OsConstants.RT_SCOPE_HOST;
    }
    if ((paramInetAddress.isLoopbackAddress()) || (paramInetAddress.isLinkLocalAddress())) {
      return OsConstants.RT_SCOPE_LINK;
    }
    if ((!(paramInetAddress instanceof Inet4Address)) && (paramInetAddress.isSiteLocalAddress())) {
      return OsConstants.RT_SCOPE_SITE;
    }
    return OsConstants.RT_SCOPE_UNIVERSE;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof LinkAddress)) {
      return false;
    }
    paramObject = (LinkAddress)paramObject;
    boolean bool1 = bool2;
    if (this.address.equals(((LinkAddress)paramObject).address))
    {
      bool1 = bool2;
      if (this.prefixLength == ((LinkAddress)paramObject).prefixLength)
      {
        bool1 = bool2;
        if (this.flags == ((LinkAddress)paramObject).flags)
        {
          bool1 = bool2;
          if (this.scope == ((LinkAddress)paramObject).scope) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public InetAddress getAddress()
  {
    return this.address;
  }
  
  public int getFlags()
  {
    return this.flags;
  }
  
  public int getNetworkPrefixLength()
  {
    return getPrefixLength();
  }
  
  public int getPrefixLength()
  {
    return this.prefixLength;
  }
  
  public int getScope()
  {
    return this.scope;
  }
  
  public int hashCode()
  {
    return this.address.hashCode() + this.prefixLength * 11 + this.flags * 19 + this.scope * 43;
  }
  
  public boolean isGlobalPreferred()
  {
    boolean bool2 = true;
    boolean bool1;
    if ((this.scope != OsConstants.RT_SCOPE_UNIVERSE) || (isIPv6ULA())) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        if ((this.flags & (OsConstants.IFA_F_DADFAILED | OsConstants.IFA_F_DEPRECATED)) != 0L) {
          break;
        }
        bool1 = bool2;
      } while ((this.flags & OsConstants.IFA_F_TENTATIVE) == 0L);
      bool1 = bool2;
    } while ((this.flags & OsConstants.IFA_F_OPTIMISTIC) != 0L);
    return false;
  }
  
  public boolean isSameAddressAs(LinkAddress paramLinkAddress)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.address.equals(paramLinkAddress.address))
    {
      bool1 = bool2;
      if (this.prefixLength == paramLinkAddress.prefixLength) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    return this.address.getHostAddress() + "/" + this.prefixLength;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.address.getAddress());
    paramParcel.writeInt(this.prefixLength);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.scope);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/LinkAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */