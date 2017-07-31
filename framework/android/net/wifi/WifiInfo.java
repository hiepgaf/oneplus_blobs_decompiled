package android.net.wifi;

import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Locale;

public class WifiInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WifiInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = true;
      WifiInfo localWifiInfo = new WifiInfo();
      localWifiInfo.setNetworkId(paramAnonymousParcel.readInt());
      localWifiInfo.setRssi(paramAnonymousParcel.readInt());
      localWifiInfo.setLinkSpeed(paramAnonymousParcel.readInt());
      localWifiInfo.setFrequency(paramAnonymousParcel.readInt());
      if (paramAnonymousParcel.readByte() == 1) {}
      try
      {
        localWifiInfo.setInetAddress(InetAddress.getByAddress(paramAnonymousParcel.createByteArray()));
        if (paramAnonymousParcel.readInt() == 1) {
          WifiInfo.-set5(localWifiInfo, (WifiSsid)WifiSsid.CREATOR.createFromParcel(paramAnonymousParcel));
        }
        WifiInfo.-set0(localWifiInfo, paramAnonymousParcel.readString());
        WifiInfo.-set2(localWifiInfo, paramAnonymousParcel.readString());
        if (paramAnonymousParcel.readInt() != 0)
        {
          bool1 = true;
          WifiInfo.-set3(localWifiInfo, bool1);
          if (paramAnonymousParcel.readInt() == 0) {
            break label234;
          }
        }
        label234:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          WifiInfo.-set1(localWifiInfo, bool1);
          localWifiInfo.score = paramAnonymousParcel.readInt();
          localWifiInfo.txSuccessRate = paramAnonymousParcel.readDouble();
          localWifiInfo.txRetriesRate = paramAnonymousParcel.readDouble();
          localWifiInfo.txBadRate = paramAnonymousParcel.readDouble();
          localWifiInfo.rxSuccessRate = paramAnonymousParcel.readDouble();
          localWifiInfo.badRssiCount = paramAnonymousParcel.readInt();
          localWifiInfo.lowRssiCount = paramAnonymousParcel.readInt();
          WifiInfo.-set4(localWifiInfo, (SupplicantState)SupplicantState.CREATOR.createFromParcel(paramAnonymousParcel));
          return localWifiInfo;
          bool1 = false;
          break;
        }
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;) {}
      }
    }
    
    public WifiInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiInfo[paramAnonymousInt];
    }
  };
  public static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";
  public static final String FREQUENCY_UNITS = "MHz";
  public static final int INVALID_RSSI = -127;
  public static final String LINK_SPEED_UNITS = "Mbps";
  public static final int MAX_RSSI = 200;
  public static final int MIN_RSSI = -126;
  private static final String TAG = "WifiInfo";
  private static final EnumMap<SupplicantState, NetworkInfo.DetailedState> stateMap = new EnumMap(SupplicantState.class);
  public int badRssiCount;
  public int linkStuckCount;
  public int lowRssiCount;
  private String mBSSID;
  private boolean mEphemeral;
  private int mFrequency;
  private InetAddress mIpAddress;
  private int mLinkSpeed;
  private String mMacAddress = "02:00:00:00:00:00";
  private boolean mMeteredHint;
  private int mNetworkId;
  private int mRssi;
  private SupplicantState mSupplicantState;
  private WifiSsid mWifiSsid;
  public long rxSuccess;
  public double rxSuccessRate;
  public int score;
  public long txBad;
  public double txBadRate;
  public long txRetries;
  public double txRetriesRate;
  public long txSuccess;
  public double txSuccessRate;
  
  static
  {
    stateMap.put(SupplicantState.DISCONNECTED, NetworkInfo.DetailedState.DISCONNECTED);
    stateMap.put(SupplicantState.INTERFACE_DISABLED, NetworkInfo.DetailedState.DISCONNECTED);
    stateMap.put(SupplicantState.INACTIVE, NetworkInfo.DetailedState.IDLE);
    stateMap.put(SupplicantState.SCANNING, NetworkInfo.DetailedState.SCANNING);
    stateMap.put(SupplicantState.AUTHENTICATING, NetworkInfo.DetailedState.CONNECTING);
    stateMap.put(SupplicantState.ASSOCIATING, NetworkInfo.DetailedState.CONNECTING);
    stateMap.put(SupplicantState.ASSOCIATED, NetworkInfo.DetailedState.CONNECTING);
    stateMap.put(SupplicantState.FOUR_WAY_HANDSHAKE, NetworkInfo.DetailedState.AUTHENTICATING);
    stateMap.put(SupplicantState.GROUP_HANDSHAKE, NetworkInfo.DetailedState.AUTHENTICATING);
    stateMap.put(SupplicantState.COMPLETED, NetworkInfo.DetailedState.OBTAINING_IPADDR);
    stateMap.put(SupplicantState.DORMANT, NetworkInfo.DetailedState.DISCONNECTED);
    stateMap.put(SupplicantState.UNINITIALIZED, NetworkInfo.DetailedState.IDLE);
    stateMap.put(SupplicantState.INVALID, NetworkInfo.DetailedState.FAILED);
  }
  
  public WifiInfo()
  {
    this.mWifiSsid = null;
    this.mBSSID = null;
    this.mNetworkId = -1;
    this.mSupplicantState = SupplicantState.UNINITIALIZED;
    this.mRssi = -127;
    this.mLinkSpeed = -1;
    this.mFrequency = -1;
  }
  
  public WifiInfo(WifiInfo paramWifiInfo)
  {
    if (paramWifiInfo != null)
    {
      this.mSupplicantState = paramWifiInfo.mSupplicantState;
      this.mBSSID = paramWifiInfo.mBSSID;
      this.mWifiSsid = paramWifiInfo.mWifiSsid;
      this.mNetworkId = paramWifiInfo.mNetworkId;
      this.mRssi = paramWifiInfo.mRssi;
      this.mLinkSpeed = paramWifiInfo.mLinkSpeed;
      this.mFrequency = paramWifiInfo.mFrequency;
      this.mIpAddress = paramWifiInfo.mIpAddress;
      this.mMacAddress = paramWifiInfo.mMacAddress;
      this.mMeteredHint = paramWifiInfo.mMeteredHint;
      this.mEphemeral = paramWifiInfo.mEphemeral;
      this.txBad = paramWifiInfo.txBad;
      this.txRetries = paramWifiInfo.txRetries;
      this.txSuccess = paramWifiInfo.txSuccess;
      this.rxSuccess = paramWifiInfo.rxSuccess;
      this.txBadRate = paramWifiInfo.txBadRate;
      this.txRetriesRate = paramWifiInfo.txRetriesRate;
      this.txSuccessRate = paramWifiInfo.txSuccessRate;
      this.rxSuccessRate = paramWifiInfo.rxSuccessRate;
      this.score = paramWifiInfo.score;
      this.badRssiCount = paramWifiInfo.badRssiCount;
      this.lowRssiCount = paramWifiInfo.lowRssiCount;
      this.linkStuckCount = paramWifiInfo.linkStuckCount;
    }
  }
  
  public static NetworkInfo.DetailedState getDetailedStateOf(SupplicantState paramSupplicantState)
  {
    return (NetworkInfo.DetailedState)stateMap.get(paramSupplicantState);
  }
  
  public static String removeDoubleQuotes(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"')) {
      return paramString.substring(1, i - 1);
    }
    return paramString;
  }
  
  static SupplicantState valueOf(String paramString)
  {
    if ("4WAY_HANDSHAKE".equalsIgnoreCase(paramString)) {
      return SupplicantState.FOUR_WAY_HANDSHAKE;
    }
    try
    {
      paramString = SupplicantState.valueOf(paramString.toUpperCase(Locale.ROOT));
      return paramString;
    }
    catch (IllegalArgumentException paramString) {}
    return SupplicantState.INVALID;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getBSSID()
  {
    return this.mBSSID;
  }
  
  public int getFrequency()
  {
    return this.mFrequency;
  }
  
  public boolean getHiddenSSID()
  {
    if (this.mWifiSsid == null) {
      return false;
    }
    return this.mWifiSsid.isHidden();
  }
  
  public int getIpAddress()
  {
    int i = 0;
    if ((this.mIpAddress instanceof Inet4Address)) {
      i = NetworkUtils.inetAddressToInt((Inet4Address)this.mIpAddress);
    }
    return i;
  }
  
  public int getLinkSpeed()
  {
    return this.mLinkSpeed;
  }
  
  public String getMacAddress()
  {
    return this.mMacAddress;
  }
  
  public boolean getMeteredHint()
  {
    return this.mMeteredHint;
  }
  
  public int getNetworkId()
  {
    return this.mNetworkId;
  }
  
  public int getRssi()
  {
    return this.mRssi;
  }
  
  public String getSSID()
  {
    if (this.mWifiSsid != null)
    {
      String str = this.mWifiSsid.toString();
      if (!TextUtils.isEmpty(str)) {
        return "\"" + str + "\"";
      }
      str = this.mWifiSsid.getHexString();
      if (str != null) {
        return str;
      }
      return "<unknown ssid>";
    }
    return "<unknown ssid>";
  }
  
  public SupplicantState getSupplicantState()
  {
    return this.mSupplicantState;
  }
  
  public WifiSsid getWifiSsid()
  {
    return this.mWifiSsid;
  }
  
  public boolean hasRealMacAddress()
  {
    return (this.mMacAddress != null) && (!"02:00:00:00:00:00".equals(this.mMacAddress));
  }
  
  public boolean is24GHz()
  {
    return ScanResult.is24GHz(this.mFrequency);
  }
  
  public boolean is5GHz()
  {
    return ScanResult.is5GHz(this.mFrequency);
  }
  
  public boolean isEphemeral()
  {
    return this.mEphemeral;
  }
  
  public void reset()
  {
    setInetAddress(null);
    setBSSID(null);
    setSSID(null);
    setNetworkId(-1);
    setRssi(-127);
    setLinkSpeed(-1);
    setFrequency(-1);
    setMeteredHint(false);
    setEphemeral(false);
    this.txBad = 0L;
    this.txSuccess = 0L;
    this.rxSuccess = 0L;
    this.txRetries = 0L;
    this.txBadRate = 0.0D;
    this.txSuccessRate = 0.0D;
    this.rxSuccessRate = 0.0D;
    this.txRetriesRate = 0.0D;
    this.lowRssiCount = 0;
    this.badRssiCount = 0;
    this.linkStuckCount = 0;
    this.score = 0;
  }
  
  public void setBSSID(String paramString)
  {
    this.mBSSID = paramString;
  }
  
  public void setEphemeral(boolean paramBoolean)
  {
    this.mEphemeral = paramBoolean;
  }
  
  public void setFrequency(int paramInt)
  {
    this.mFrequency = paramInt;
  }
  
  public void setInetAddress(InetAddress paramInetAddress)
  {
    this.mIpAddress = paramInetAddress;
  }
  
  public void setLinkSpeed(int paramInt)
  {
    this.mLinkSpeed = paramInt;
  }
  
  public void setMacAddress(String paramString)
  {
    this.mMacAddress = paramString;
  }
  
  public void setMeteredHint(boolean paramBoolean)
  {
    this.mMeteredHint = paramBoolean;
  }
  
  public void setNetworkId(int paramInt)
  {
    this.mNetworkId = paramInt;
  }
  
  public void setRssi(int paramInt)
  {
    int i = paramInt;
    if (paramInt < -127) {
      i = -127;
    }
    paramInt = i;
    if (i > 200) {
      paramInt = 200;
    }
    this.mRssi = paramInt;
  }
  
  public void setSSID(WifiSsid paramWifiSsid)
  {
    this.mWifiSsid = paramWifiSsid;
  }
  
  public void setSupplicantState(SupplicantState paramSupplicantState)
  {
    this.mSupplicantState = paramSupplicantState;
  }
  
  void setSupplicantState(String paramString)
  {
    this.mSupplicantState = valueOf(paramString);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer1 = new StringBuffer();
    String str = "<none>";
    StringBuffer localStringBuffer2 = localStringBuffer1.append("SSID: ");
    if (this.mWifiSsid == null)
    {
      localObject = "<unknown ssid>";
      localStringBuffer2 = localStringBuffer2.append(localObject).append(", BSSID: ");
      if (this.mBSSID != null) {
        break label216;
      }
      localObject = "<none>";
      label57:
      localStringBuffer2 = localStringBuffer2.append((String)localObject).append(", MAC: ");
      if (this.mMacAddress != null) {
        break label224;
      }
      localObject = "<none>";
      label82:
      localStringBuffer2 = localStringBuffer2.append((String)localObject).append(", Supplicant state: ");
      if (this.mSupplicantState != null) {
        break label232;
      }
    }
    label216:
    label224:
    label232:
    for (Object localObject = str;; localObject = this.mSupplicantState)
    {
      localStringBuffer2.append(localObject).append(", RSSI: ").append(this.mRssi).append(", Link speed: ").append(this.mLinkSpeed).append("Mbps").append(", Frequency: ").append(this.mFrequency).append("MHz").append(", Net ID: ").append(this.mNetworkId).append(", Metered hint: ").append(this.mMeteredHint).append(", score: ").append(Integer.toString(this.score));
      return localStringBuffer1.toString();
      localObject = this.mWifiSsid;
      break;
      localObject = this.mBSSID;
      break label57;
      localObject = this.mMacAddress;
      break label82;
    }
  }
  
  public void updatePacketRates(long paramLong1, long paramLong2)
  {
    this.txBad = 0L;
    this.txRetries = 0L;
    this.txBadRate = 0.0D;
    this.txRetriesRate = 0.0D;
    if ((this.txSuccess <= paramLong1) && (this.rxSuccess <= paramLong2))
    {
      this.txSuccessRate = (this.txSuccessRate * 0.5D + (paramLong1 - this.txSuccess) * 0.5D);
      this.rxSuccessRate = (this.rxSuccessRate * 0.5D + (paramLong2 - this.rxSuccess) * 0.5D);
    }
    for (;;)
    {
      this.txSuccess = paramLong1;
      this.rxSuccess = paramLong2;
      return;
      this.txBadRate = 0.0D;
      this.txRetriesRate = 0.0D;
    }
  }
  
  public void updatePacketRates(WifiLinkLayerStats paramWifiLinkLayerStats)
  {
    if (paramWifiLinkLayerStats != null)
    {
      long l1 = paramWifiLinkLayerStats.txmpdu_be + paramWifiLinkLayerStats.txmpdu_bk + paramWifiLinkLayerStats.txmpdu_vi + paramWifiLinkLayerStats.txmpdu_vo;
      long l2 = paramWifiLinkLayerStats.retries_be + paramWifiLinkLayerStats.retries_bk + paramWifiLinkLayerStats.retries_vi + paramWifiLinkLayerStats.retries_vo;
      long l3 = paramWifiLinkLayerStats.rxmpdu_be + paramWifiLinkLayerStats.rxmpdu_bk + paramWifiLinkLayerStats.rxmpdu_vi + paramWifiLinkLayerStats.rxmpdu_vo;
      long l4 = paramWifiLinkLayerStats.lostmpdu_be + paramWifiLinkLayerStats.lostmpdu_bk + paramWifiLinkLayerStats.lostmpdu_vi + paramWifiLinkLayerStats.lostmpdu_vo;
      if ((this.txBad <= l4) && (this.txSuccess <= l1) && (this.rxSuccess <= l3) && (this.txRetries <= l2))
      {
        this.txBadRate = (this.txBadRate * 0.5D + (l4 - this.txBad) * 0.5D);
        this.txSuccessRate = (this.txSuccessRate * 0.5D + (l1 - this.txSuccess) * 0.5D);
        this.rxSuccessRate = (this.rxSuccessRate * 0.5D + (l3 - this.rxSuccess) * 0.5D);
      }
      for (this.txRetriesRate = (this.txRetriesRate * 0.5D + (l2 - this.txRetries) * 0.5D);; this.txRetriesRate = 0.0D)
      {
        this.txBad = l4;
        this.txSuccess = l1;
        this.rxSuccess = l3;
        this.txRetries = l2;
        return;
        this.txBadRate = 0.0D;
        this.txSuccessRate = 0.0D;
        this.rxSuccessRate = 0.0D;
      }
    }
    this.txBad = 0L;
    this.txSuccess = 0L;
    this.rxSuccess = 0L;
    this.txRetries = 0L;
    this.txBadRate = 0.0D;
    this.txSuccessRate = 0.0D;
    this.rxSuccessRate = 0.0D;
    this.txRetriesRate = 0.0D;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeInt(this.mNetworkId);
    paramParcel.writeInt(this.mRssi);
    paramParcel.writeInt(this.mLinkSpeed);
    paramParcel.writeInt(this.mFrequency);
    if (this.mIpAddress != null)
    {
      paramParcel.writeByte((byte)1);
      paramParcel.writeByteArray(this.mIpAddress.getAddress());
      if (this.mWifiSsid == null) {
        break label198;
      }
      paramParcel.writeInt(1);
      this.mWifiSsid.writeToParcel(paramParcel, paramInt);
      label79:
      paramParcel.writeString(this.mBSSID);
      paramParcel.writeString(this.mMacAddress);
      if (!this.mMeteredHint) {
        break label206;
      }
      i = 1;
      label104:
      paramParcel.writeInt(i);
      if (!this.mEphemeral) {
        break label211;
      }
    }
    label198:
    label206:
    label211:
    for (int i = j;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.score);
      paramParcel.writeDouble(this.txSuccessRate);
      paramParcel.writeDouble(this.txRetriesRate);
      paramParcel.writeDouble(this.txBadRate);
      paramParcel.writeDouble(this.rxSuccessRate);
      paramParcel.writeInt(this.badRssiCount);
      paramParcel.writeInt(this.lowRssiCount);
      this.mSupplicantState.writeToParcel(paramParcel, paramInt);
      return;
      paramParcel.writeByte((byte)0);
      break;
      paramParcel.writeInt(0);
      break label79;
      i = 0;
      break label104;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */