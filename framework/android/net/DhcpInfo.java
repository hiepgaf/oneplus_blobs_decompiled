package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.net.InetAddress;

public class DhcpInfo
  implements Parcelable
{
  public static final Parcelable.Creator<DhcpInfo> CREATOR = new Parcelable.Creator()
  {
    public DhcpInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      DhcpInfo localDhcpInfo = new DhcpInfo();
      localDhcpInfo.ipAddress = paramAnonymousParcel.readInt();
      localDhcpInfo.gateway = paramAnonymousParcel.readInt();
      localDhcpInfo.netmask = paramAnonymousParcel.readInt();
      localDhcpInfo.dns1 = paramAnonymousParcel.readInt();
      localDhcpInfo.dns2 = paramAnonymousParcel.readInt();
      localDhcpInfo.serverAddress = paramAnonymousParcel.readInt();
      localDhcpInfo.leaseDuration = paramAnonymousParcel.readInt();
      return localDhcpInfo;
    }
    
    public DhcpInfo[] newArray(int paramAnonymousInt)
    {
      return new DhcpInfo[paramAnonymousInt];
    }
  };
  public int dns1;
  public int dns2;
  public int gateway;
  public int ipAddress;
  public int leaseDuration;
  public int netmask;
  public int serverAddress;
  
  public DhcpInfo() {}
  
  public DhcpInfo(DhcpInfo paramDhcpInfo)
  {
    if (paramDhcpInfo != null)
    {
      this.ipAddress = paramDhcpInfo.ipAddress;
      this.gateway = paramDhcpInfo.gateway;
      this.netmask = paramDhcpInfo.netmask;
      this.dns1 = paramDhcpInfo.dns1;
      this.dns2 = paramDhcpInfo.dns2;
      this.serverAddress = paramDhcpInfo.serverAddress;
      this.leaseDuration = paramDhcpInfo.leaseDuration;
    }
  }
  
  private static void putAddress(StringBuffer paramStringBuffer, int paramInt)
  {
    paramStringBuffer.append(NetworkUtils.intToInetAddress(paramInt).getHostAddress());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("ipaddr ");
    putAddress(localStringBuffer, this.ipAddress);
    localStringBuffer.append(" gateway ");
    putAddress(localStringBuffer, this.gateway);
    localStringBuffer.append(" netmask ");
    putAddress(localStringBuffer, this.netmask);
    localStringBuffer.append(" dns1 ");
    putAddress(localStringBuffer, this.dns1);
    localStringBuffer.append(" dns2 ");
    putAddress(localStringBuffer, this.dns2);
    localStringBuffer.append(" DHCP server ");
    putAddress(localStringBuffer, this.serverAddress);
    localStringBuffer.append(" lease ").append(this.leaseDuration).append(" seconds");
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.ipAddress);
    paramParcel.writeInt(this.gateway);
    paramParcel.writeInt(this.netmask);
    paramParcel.writeInt(this.dns1);
    paramParcel.writeInt(this.dns2);
    paramParcel.writeInt(this.serverAddress);
    paramParcel.writeInt(this.leaseDuration);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/DhcpInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */