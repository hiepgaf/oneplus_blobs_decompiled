package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiP2pDevice
  implements Parcelable
{
  public static final int AVAILABLE = 3;
  public static final int CONNECTED = 0;
  public static final Parcelable.Creator<WifiP2pDevice> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pDevice createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pDevice localWifiP2pDevice = new WifiP2pDevice();
      localWifiP2pDevice.deviceName = paramAnonymousParcel.readString();
      localWifiP2pDevice.deviceAddress = paramAnonymousParcel.readString();
      localWifiP2pDevice.primaryDeviceType = paramAnonymousParcel.readString();
      localWifiP2pDevice.secondaryDeviceType = paramAnonymousParcel.readString();
      localWifiP2pDevice.wpsConfigMethodsSupported = paramAnonymousParcel.readInt();
      localWifiP2pDevice.deviceCapability = paramAnonymousParcel.readInt();
      localWifiP2pDevice.groupCapability = paramAnonymousParcel.readInt();
      localWifiP2pDevice.status = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readInt() == 1) {
        localWifiP2pDevice.wfdInfo = ((WifiP2pWfdInfo)WifiP2pWfdInfo.CREATOR.createFromParcel(paramAnonymousParcel));
      }
      return localWifiP2pDevice;
    }
    
    public WifiP2pDevice[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pDevice[paramAnonymousInt];
    }
  };
  private static final int DEVICE_CAPAB_CLIENT_DISCOVERABILITY = 2;
  private static final int DEVICE_CAPAB_CONCURRENT_OPER = 4;
  private static final int DEVICE_CAPAB_DEVICE_LIMIT = 16;
  private static final int DEVICE_CAPAB_INFRA_MANAGED = 8;
  private static final int DEVICE_CAPAB_INVITATION_PROCEDURE = 32;
  private static final int DEVICE_CAPAB_SERVICE_DISCOVERY = 1;
  public static final int FAILED = 2;
  private static final int GROUP_CAPAB_CROSS_CONN = 16;
  private static final int GROUP_CAPAB_GROUP_FORMATION = 64;
  private static final int GROUP_CAPAB_GROUP_LIMIT = 4;
  private static final int GROUP_CAPAB_GROUP_OWNER = 1;
  private static final int GROUP_CAPAB_INTRA_BSS_DIST = 8;
  private static final int GROUP_CAPAB_PERSISTENT_GROUP = 2;
  private static final int GROUP_CAPAB_PERSISTENT_RECONN = 32;
  public static final int INVITED = 1;
  private static final String TAG = "WifiP2pDevice";
  public static final int UNAVAILABLE = 4;
  private static final int WPS_CONFIG_DISPLAY = 8;
  private static final int WPS_CONFIG_KEYPAD = 256;
  private static final int WPS_CONFIG_PUSHBUTTON = 128;
  private static final Pattern detailedDevicePattern = Pattern.compile("((?:[0-9a-f]{2}:){5}[0-9a-f]{2}) (\\d+ )?p2p_dev_addr=((?:[0-9a-f]{2}:){5}[0-9a-f]{2}) pri_dev_type=(\\d+-[0-9a-fA-F]+-\\d+) name='(.*)' config_methods=(0x[0-9a-fA-F]+) dev_capab=(0x[0-9a-fA-F]+) group_capab=(0x[0-9a-fA-F]+)( wfd_dev_info=0x([0-9a-fA-F]{12}))?");
  private static final Pattern threeTokenPattern;
  private static final Pattern twoTokenPattern = Pattern.compile("(p2p_dev_addr=)?((?:[0-9a-f]{2}:){5}[0-9a-f]{2})");
  public String deviceAddress = "";
  public int deviceCapability;
  public String deviceName = "";
  public int groupCapability;
  public String primaryDeviceType;
  public String secondaryDeviceType;
  public int status = 4;
  public WifiP2pWfdInfo wfdInfo;
  public int wpsConfigMethodsSupported;
  
  static
  {
    threeTokenPattern = Pattern.compile("(?:[0-9a-f]{2}:){5}[0-9a-f]{2} p2p_dev_addr=((?:[0-9a-f]{2}:){5}[0-9a-f]{2})");
  }
  
  public WifiP2pDevice() {}
  
  public WifiP2pDevice(WifiP2pDevice paramWifiP2pDevice)
  {
    if (paramWifiP2pDevice != null)
    {
      this.deviceName = paramWifiP2pDevice.deviceName;
      this.deviceAddress = paramWifiP2pDevice.deviceAddress;
      this.primaryDeviceType = paramWifiP2pDevice.primaryDeviceType;
      this.secondaryDeviceType = paramWifiP2pDevice.secondaryDeviceType;
      this.wpsConfigMethodsSupported = paramWifiP2pDevice.wpsConfigMethodsSupported;
      this.deviceCapability = paramWifiP2pDevice.deviceCapability;
      this.groupCapability = paramWifiP2pDevice.groupCapability;
      this.status = paramWifiP2pDevice.status;
      this.wfdInfo = new WifiP2pWfdInfo(paramWifiP2pDevice.wfdInfo);
    }
  }
  
  public WifiP2pDevice(String paramString)
    throws IllegalArgumentException
  {
    String[] arrayOfString = paramString.split("[ \n]");
    if (arrayOfString.length < 1) {
      throw new IllegalArgumentException("Malformed supplicant event");
    }
    switch (arrayOfString.length)
    {
    default: 
      paramString = detailedDevicePattern.matcher(paramString);
      if (!paramString.find()) {
        throw new IllegalArgumentException("Malformed supplicant event");
      }
      break;
    case 1: 
      this.deviceAddress = paramString;
      return;
    case 2: 
      paramString = twoTokenPattern.matcher(paramString);
      if (!paramString.find()) {
        throw new IllegalArgumentException("Malformed supplicant event");
      }
      this.deviceAddress = paramString.group(2);
      return;
    case 3: 
      paramString = threeTokenPattern.matcher(paramString);
      if (!paramString.find()) {
        throw new IllegalArgumentException("Malformed supplicant event");
      }
      this.deviceAddress = paramString.group(1);
      return;
    }
    this.deviceAddress = paramString.group(3);
    this.primaryDeviceType = paramString.group(4);
    this.deviceName = paramString.group(5);
    this.wpsConfigMethodsSupported = parseHex(paramString.group(6));
    this.deviceCapability = parseHex(paramString.group(7));
    this.groupCapability = parseHex(paramString.group(8));
    if (paramString.group(9) != null)
    {
      paramString = paramString.group(10);
      this.wfdInfo = new WifiP2pWfdInfo(parseHex(paramString.substring(0, 4)), parseHex(paramString.substring(4, 8)), parseHex(paramString.substring(8, 12)));
    }
    if (arrayOfString[0].startsWith("P2P-DEVICE-FOUND")) {
      this.status = 3;
    }
  }
  
  private int parseHex(String paramString)
  {
    String str;
    if (!paramString.startsWith("0x"))
    {
      str = paramString;
      if (!paramString.startsWith("0X")) {}
    }
    else
    {
      str = paramString.substring(2);
    }
    try
    {
      int i = Integer.parseInt(str, 16);
      return i;
    }
    catch (NumberFormatException paramString)
    {
      Log.e("WifiP2pDevice", "Failed to parse hex string " + str);
    }
    return 0;
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
    if (!(paramObject instanceof WifiP2pDevice)) {
      return false;
    }
    paramObject = (WifiP2pDevice)paramObject;
    if ((paramObject == null) || (((WifiP2pDevice)paramObject).deviceAddress == null)) {
      return this.deviceAddress == null;
    }
    return ((WifiP2pDevice)paramObject).deviceAddress.equals(this.deviceAddress);
  }
  
  public boolean isDeviceLimit()
  {
    boolean bool = false;
    if ((this.deviceCapability & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isGroupLimit()
  {
    boolean bool = false;
    if ((this.groupCapability & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isGroupOwner()
  {
    boolean bool = false;
    if ((this.groupCapability & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isInvitationCapable()
  {
    boolean bool = false;
    if ((this.deviceCapability & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isServiceDiscoveryCapable()
  {
    boolean bool = false;
    if ((this.deviceCapability & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Device: ").append(this.deviceName);
    localStringBuffer.append("\n deviceAddress: ").append(this.deviceAddress);
    localStringBuffer.append("\n primary type: ").append(this.primaryDeviceType);
    localStringBuffer.append("\n secondary type: ").append(this.secondaryDeviceType);
    localStringBuffer.append("\n wps: ").append(this.wpsConfigMethodsSupported);
    localStringBuffer.append("\n grpcapab: ").append(this.groupCapability);
    localStringBuffer.append("\n devcapab: ").append(this.deviceCapability);
    localStringBuffer.append("\n status: ").append(this.status);
    localStringBuffer.append("\n wfdInfo: ").append(this.wfdInfo);
    return localStringBuffer.toString();
  }
  
  public void update(WifiP2pDevice paramWifiP2pDevice)
  {
    updateSupplicantDetails(paramWifiP2pDevice);
    this.status = paramWifiP2pDevice.status;
  }
  
  public void updateSupplicantDetails(WifiP2pDevice paramWifiP2pDevice)
  {
    if (paramWifiP2pDevice == null) {
      throw new IllegalArgumentException("device is null");
    }
    if (paramWifiP2pDevice.deviceAddress == null) {
      throw new IllegalArgumentException("deviceAddress is null");
    }
    if (!this.deviceAddress.equals(paramWifiP2pDevice.deviceAddress)) {
      throw new IllegalArgumentException("deviceAddress does not match");
    }
    this.deviceName = paramWifiP2pDevice.deviceName;
    this.primaryDeviceType = paramWifiP2pDevice.primaryDeviceType;
    this.secondaryDeviceType = paramWifiP2pDevice.secondaryDeviceType;
    this.wpsConfigMethodsSupported = paramWifiP2pDevice.wpsConfigMethodsSupported;
    this.deviceCapability = paramWifiP2pDevice.deviceCapability;
    this.groupCapability = paramWifiP2pDevice.groupCapability;
    this.wfdInfo = paramWifiP2pDevice.wfdInfo;
  }
  
  public boolean wpsDisplaySupported()
  {
    boolean bool = false;
    if ((this.wpsConfigMethodsSupported & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean wpsKeypadSupported()
  {
    boolean bool = false;
    if ((this.wpsConfigMethodsSupported & 0x100) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean wpsPbcSupported()
  {
    boolean bool = false;
    if ((this.wpsConfigMethodsSupported & 0x80) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.deviceName);
    paramParcel.writeString(this.deviceAddress);
    paramParcel.writeString(this.primaryDeviceType);
    paramParcel.writeString(this.secondaryDeviceType);
    paramParcel.writeInt(this.wpsConfigMethodsSupported);
    paramParcel.writeInt(this.deviceCapability);
    paramParcel.writeInt(this.groupCapability);
    paramParcel.writeInt(this.status);
    if (this.wfdInfo != null)
    {
      paramParcel.writeInt(1);
      this.wfdInfo.writeToParcel(paramParcel, paramInt);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */