package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Locale;

public class WifiP2pWfdInfo
  implements Parcelable
{
  private static final int COUPLED_SINK_SUPPORT_AT_SINK = 8;
  private static final int COUPLED_SINK_SUPPORT_AT_SOURCE = 4;
  public static final Parcelable.Creator<WifiP2pWfdInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pWfdInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pWfdInfo localWifiP2pWfdInfo = new WifiP2pWfdInfo();
      localWifiP2pWfdInfo.readFromParcel(paramAnonymousParcel);
      return localWifiP2pWfdInfo;
    }
    
    public WifiP2pWfdInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pWfdInfo[paramAnonymousInt];
    }
  };
  private static final int DEVICE_TYPE = 3;
  public static final int PRIMARY_SINK = 1;
  public static final int SECONDARY_SINK = 2;
  private static final int SESSION_AVAILABLE = 48;
  private static final int SESSION_AVAILABLE_BIT1 = 16;
  private static final int SESSION_AVAILABLE_BIT2 = 32;
  public static final int SOURCE_OR_PRIMARY_SINK = 3;
  private static final String TAG = "WifiP2pWfdInfo";
  public static final int WFD_SOURCE = 0;
  private int mCtrlPort;
  private int mDeviceInfo;
  private int mMaxThroughput;
  private boolean mWfdEnabled;
  
  public WifiP2pWfdInfo() {}
  
  public WifiP2pWfdInfo(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mWfdEnabled = true;
    this.mDeviceInfo = paramInt1;
    this.mCtrlPort = paramInt2;
    this.mMaxThroughput = paramInt3;
  }
  
  public WifiP2pWfdInfo(WifiP2pWfdInfo paramWifiP2pWfdInfo)
  {
    if (paramWifiP2pWfdInfo != null)
    {
      this.mWfdEnabled = paramWifiP2pWfdInfo.mWfdEnabled;
      this.mDeviceInfo = paramWifiP2pWfdInfo.mDeviceInfo;
      this.mCtrlPort = paramWifiP2pWfdInfo.mCtrlPort;
      this.mMaxThroughput = paramWifiP2pWfdInfo.mMaxThroughput;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getControlPort()
  {
    return this.mCtrlPort;
  }
  
  public String getDeviceInfoHex()
  {
    return String.format(Locale.US, "%04x%04x%04x%04x", new Object[] { Integer.valueOf(6), Integer.valueOf(this.mDeviceInfo), Integer.valueOf(this.mCtrlPort), Integer.valueOf(this.mMaxThroughput) });
  }
  
  public int getDeviceType()
  {
    return this.mDeviceInfo & 0x3;
  }
  
  public int getMaxThroughput()
  {
    return this.mMaxThroughput;
  }
  
  public boolean isCoupledSinkSupportedAtSink()
  {
    boolean bool = false;
    if ((this.mDeviceInfo & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isCoupledSinkSupportedAtSource()
  {
    boolean bool = false;
    if ((this.mDeviceInfo & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSessionAvailable()
  {
    boolean bool = false;
    if ((this.mDeviceInfo & 0x30) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isWfdEnabled()
  {
    return this.mWfdEnabled;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    boolean bool = true;
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.mWfdEnabled = bool;
      this.mDeviceInfo = paramParcel.readInt();
      this.mCtrlPort = paramParcel.readInt();
      this.mMaxThroughput = paramParcel.readInt();
      return;
      bool = false;
    }
  }
  
  public void setControlPort(int paramInt)
  {
    this.mCtrlPort = paramInt;
  }
  
  public void setCoupledSinkSupportAtSink(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mDeviceInfo |= 0x8;
      return;
    }
    this.mDeviceInfo &= 0xFFFFFFF7;
  }
  
  public void setCoupledSinkSupportAtSource(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mDeviceInfo |= 0x8;
      return;
    }
    this.mDeviceInfo &= 0xFFFFFFF7;
  }
  
  public boolean setDeviceType(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= 3))
    {
      this.mDeviceInfo &= 0xFFFFFFFC;
      this.mDeviceInfo |= paramInt;
      return true;
    }
    return false;
  }
  
  public void setMaxThroughput(int paramInt)
  {
    this.mMaxThroughput = paramInt;
  }
  
  public void setSessionAvailable(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mDeviceInfo |= 0x10;
      this.mDeviceInfo &= 0xFFFFFFDF;
      return;
    }
    this.mDeviceInfo &= 0xFFFFFFCF;
  }
  
  public void setWfdEnabled(boolean paramBoolean)
  {
    this.mWfdEnabled = paramBoolean;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("WFD enabled: ").append(this.mWfdEnabled);
    localStringBuffer.append("WFD DeviceInfo: ").append(this.mDeviceInfo);
    localStringBuffer.append("\n WFD CtrlPort: ").append(this.mCtrlPort);
    localStringBuffer.append("\n WFD MaxThroughput: ").append(this.mMaxThroughput);
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mWfdEnabled) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.mDeviceInfo);
      paramParcel.writeInt(this.mCtrlPort);
      paramParcel.writeInt(this.mMaxThroughput);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pWfdInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */