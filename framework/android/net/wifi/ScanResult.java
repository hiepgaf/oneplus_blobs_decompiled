package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class ScanResult
  implements Parcelable
{
  public static final int CHANNEL_WIDTH_160MHZ = 3;
  public static final int CHANNEL_WIDTH_20MHZ = 0;
  public static final int CHANNEL_WIDTH_40MHZ = 1;
  public static final int CHANNEL_WIDTH_80MHZ = 2;
  public static final int CHANNEL_WIDTH_80MHZ_PLUS_MHZ = 4;
  public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator()
  {
    public ScanResult createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = null;
      if (paramAnonymousParcel.readInt() == 1) {
        localObject = (WifiSsid)WifiSsid.CREATOR.createFromParcel(paramAnonymousParcel);
      }
      localObject = new ScanResult((WifiSsid)localObject, paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readLong(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readLong(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), false);
      ((ScanResult)localObject).seen = paramAnonymousParcel.readLong();
      if (paramAnonymousParcel.readInt() != 0) {}
      int i;
      int k;
      for (boolean bool = true;; bool = false)
      {
        ((ScanResult)localObject).untrusted = bool;
        ((ScanResult)localObject).numConnection = paramAnonymousParcel.readInt();
        ((ScanResult)localObject).numUsage = paramAnonymousParcel.readInt();
        ((ScanResult)localObject).numIpConfigFailures = paramAnonymousParcel.readInt();
        ((ScanResult)localObject).isAutoJoinCandidate = paramAnonymousParcel.readInt();
        ((ScanResult)localObject).venueName = paramAnonymousParcel.readString();
        ((ScanResult)localObject).operatorFriendlyName = paramAnonymousParcel.readString();
        ((ScanResult)localObject).flags = paramAnonymousParcel.readLong();
        j = paramAnonymousParcel.readInt();
        if (j == 0) {
          break;
        }
        ((ScanResult)localObject).informationElements = new ScanResult.InformationElement[j];
        i = 0;
        while (i < j)
        {
          ((ScanResult)localObject).informationElements[i] = new ScanResult.InformationElement();
          localObject.informationElements[i].id = paramAnonymousParcel.readInt();
          k = paramAnonymousParcel.readInt();
          localObject.informationElements[i].bytes = new byte[k];
          paramAnonymousParcel.readByteArray(localObject.informationElements[i].bytes);
          i += 1;
        }
      }
      int j = paramAnonymousParcel.readInt();
      if (j != 0)
      {
        ((ScanResult)localObject).anqpLines = new ArrayList();
        i = 0;
        while (i < j)
        {
          ((ScanResult)localObject).anqpLines.add(paramAnonymousParcel.readString());
          i += 1;
        }
      }
      j = paramAnonymousParcel.readInt();
      if (j != 0)
      {
        ((ScanResult)localObject).anqpElements = new AnqpInformationElement[j];
        i = 0;
        while (i < j)
        {
          k = paramAnonymousParcel.readInt();
          int m = paramAnonymousParcel.readInt();
          byte[] arrayOfByte = new byte[paramAnonymousParcel.readInt()];
          paramAnonymousParcel.readByteArray(arrayOfByte);
          ((ScanResult)localObject).anqpElements[i] = new AnqpInformationElement(k, m, arrayOfByte);
          i += 1;
        }
      }
      return (ScanResult)localObject;
    }
    
    public ScanResult[] newArray(int paramAnonymousInt)
    {
      return new ScanResult[paramAnonymousInt];
    }
  };
  public static final long FLAG_80211mc_RESPONDER = 2L;
  public static final long FLAG_PASSPOINT_NETWORK = 1L;
  public static final int UNSPECIFIED = -1;
  public String BSSID;
  public String SSID;
  public int anqpDomainId;
  public AnqpInformationElement[] anqpElements;
  public List<String> anqpLines;
  public long blackListTimestamp;
  public byte[] bytes;
  public String capabilities;
  public int centerFreq0;
  public int centerFreq1;
  public int channelWidth;
  public int distanceCm;
  public int distanceSdCm;
  public long flags;
  public int frequency;
  public long hessid;
  public InformationElement[] informationElements;
  public boolean is80211McRTTResponder;
  public int isAutoJoinCandidate;
  public int level;
  public int numConnection;
  public int numIpConfigFailures;
  public int numUsage;
  public CharSequence operatorFriendlyName;
  public long seen;
  public long timestamp;
  public boolean untrusted;
  public CharSequence venueName;
  public WifiSsid wifiSsid;
  
  public ScanResult() {}
  
  public ScanResult(ScanResult paramScanResult)
  {
    if (paramScanResult != null)
    {
      this.wifiSsid = paramScanResult.wifiSsid;
      this.SSID = paramScanResult.SSID;
      this.BSSID = paramScanResult.BSSID;
      this.hessid = paramScanResult.hessid;
      this.anqpDomainId = paramScanResult.anqpDomainId;
      this.informationElements = paramScanResult.informationElements;
      this.anqpElements = paramScanResult.anqpElements;
      this.capabilities = paramScanResult.capabilities;
      this.level = paramScanResult.level;
      this.frequency = paramScanResult.frequency;
      this.channelWidth = paramScanResult.channelWidth;
      this.centerFreq0 = paramScanResult.centerFreq0;
      this.centerFreq1 = paramScanResult.centerFreq1;
      this.timestamp = paramScanResult.timestamp;
      this.distanceCm = paramScanResult.distanceCm;
      this.distanceSdCm = paramScanResult.distanceSdCm;
      this.seen = paramScanResult.seen;
      this.untrusted = paramScanResult.untrusted;
      this.numConnection = paramScanResult.numConnection;
      this.numUsage = paramScanResult.numUsage;
      this.numIpConfigFailures = paramScanResult.numIpConfigFailures;
      this.isAutoJoinCandidate = paramScanResult.isAutoJoinCandidate;
      this.venueName = paramScanResult.venueName;
      this.operatorFriendlyName = paramScanResult.operatorFriendlyName;
      this.flags = paramScanResult.flags;
    }
  }
  
  public ScanResult(WifiSsid paramWifiSsid, String paramString1, long paramLong1, int paramInt1, byte[] paramArrayOfByte, String paramString2, int paramInt2, int paramInt3, long paramLong2)
  {
    this.wifiSsid = paramWifiSsid;
    if (paramWifiSsid != null) {}
    for (paramWifiSsid = paramWifiSsid.toString();; paramWifiSsid = "<unknown ssid>")
    {
      this.SSID = paramWifiSsid;
      this.BSSID = paramString1;
      this.hessid = paramLong1;
      this.anqpDomainId = paramInt1;
      if (paramArrayOfByte != null)
      {
        this.anqpElements = new AnqpInformationElement[1];
        this.anqpElements[0] = new AnqpInformationElement(5271450, 8, paramArrayOfByte);
      }
      this.capabilities = paramString2;
      this.level = paramInt2;
      this.frequency = paramInt3;
      this.timestamp = paramLong2;
      this.distanceCm = -1;
      this.distanceSdCm = -1;
      this.channelWidth = -1;
      this.centerFreq0 = -1;
      this.centerFreq1 = -1;
      this.flags = 0L;
      return;
    }
  }
  
  public ScanResult(WifiSsid paramWifiSsid, String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4)
  {
    this.wifiSsid = paramWifiSsid;
    if (paramWifiSsid != null) {}
    for (paramWifiSsid = paramWifiSsid.toString();; paramWifiSsid = "<unknown ssid>")
    {
      this.SSID = paramWifiSsid;
      this.BSSID = paramString1;
      this.capabilities = paramString2;
      this.level = paramInt1;
      this.frequency = paramInt2;
      this.timestamp = paramLong;
      this.distanceCm = paramInt3;
      this.distanceSdCm = paramInt4;
      this.channelWidth = -1;
      this.centerFreq0 = -1;
      this.centerFreq1 = -1;
      this.flags = 0L;
      return;
    }
  }
  
  public ScanResult(WifiSsid paramWifiSsid, String paramString1, String paramString2, long paramLong1, int paramInt1, String paramString3, int paramInt2, int paramInt3, long paramLong2, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    this(paramString1, paramString2, paramLong1, paramInt1, paramString3, paramInt2, paramInt3, paramLong2, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
    this.wifiSsid = paramWifiSsid;
  }
  
  public ScanResult(String paramString1, String paramString2, long paramLong1, int paramInt1, String paramString3, int paramInt2, int paramInt3, long paramLong2, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    this.SSID = paramString1;
    this.BSSID = paramString2;
    this.hessid = paramLong1;
    this.anqpDomainId = paramInt1;
    this.capabilities = paramString3;
    this.level = paramInt2;
    this.frequency = paramInt3;
    this.timestamp = paramLong2;
    this.distanceCm = paramInt4;
    this.distanceSdCm = paramInt5;
    this.channelWidth = paramInt6;
    this.centerFreq0 = paramInt7;
    this.centerFreq1 = paramInt8;
    if (paramBoolean)
    {
      this.flags = 2L;
      return;
    }
    this.flags = 0L;
  }
  
  public static boolean is24GHz(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt > 2400)
    {
      bool1 = bool2;
      if (paramInt < 2500) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean is5GHz(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt > 4900)
    {
      bool1 = bool2;
      if (paramInt < 5900) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void averageRssi(int paramInt1, long paramLong, int paramInt2)
  {
    if (this.seen == 0L) {
      this.seen = System.currentTimeMillis();
    }
    long l = this.seen - paramLong;
    if ((paramLong > 0L) && (l > 0L) && (l < paramInt2 / 2))
    {
      double d = 0.5D - l / paramInt2;
      this.level = ((int)(this.level * (1.0D - d) + paramInt1 * d));
    }
  }
  
  public void clearFlag(long paramLong)
  {
    this.flags &= paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean is24GHz()
  {
    return is24GHz(this.frequency);
  }
  
  public boolean is5GHz()
  {
    return is5GHz(this.frequency);
  }
  
  public boolean is80211mcResponder()
  {
    return (this.flags & 0x2) != 0L;
  }
  
  public boolean isPasspointNetwork()
  {
    return (this.flags & 1L) != 0L;
  }
  
  public void setFlag(long paramLong)
  {
    this.flags |= paramLong;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer1 = new StringBuffer();
    Object localObject2 = "<none>";
    StringBuffer localStringBuffer2 = localStringBuffer1.append("SSID: ");
    if (this.wifiSsid == null)
    {
      localObject1 = "<unknown ssid>";
      localStringBuffer2 = localStringBuffer2.append(localObject1).append(", BSSID: ");
      if (this.BSSID != null) {
        break label296;
      }
      localObject1 = "<none>";
      label52:
      localStringBuffer2 = localStringBuffer2.append((String)localObject1).append(", capabilities: ");
      if (this.capabilities != null) {
        break label304;
      }
      localObject1 = localObject2;
      label74:
      localStringBuffer2.append((String)localObject1).append(", level: ").append(this.level).append(", frequency: ").append(this.frequency).append(", timestamp: ").append(this.timestamp);
      localObject2 = localStringBuffer1.append(", distance: ");
      if (this.distanceCm == -1) {
        break label312;
      }
      localObject1 = Integer.valueOf(this.distanceCm);
      label140:
      ((StringBuffer)localObject2).append(localObject1).append("(cm)");
      localObject2 = localStringBuffer1.append(", distanceSd: ");
      if (this.distanceSdCm == -1) {
        break label318;
      }
      localObject1 = Integer.valueOf(this.distanceSdCm);
      label174:
      ((StringBuffer)localObject2).append(localObject1).append("(cm)");
      localStringBuffer1.append(", passpoint: ");
      if ((this.flags & 1L) == 0L) {
        break label324;
      }
      localObject1 = "yes";
      label206:
      localStringBuffer1.append((String)localObject1);
      localStringBuffer1.append(", ChannelBandwidth: ").append(this.channelWidth);
      localStringBuffer1.append(", centerFreq0: ").append(this.centerFreq0);
      localStringBuffer1.append(", centerFreq1: ").append(this.centerFreq1);
      localStringBuffer1.append(", 80211mcResponder: ");
      if ((this.flags & 0x2) == 0L) {
        break label330;
      }
    }
    label296:
    label304:
    label312:
    label318:
    label324:
    label330:
    for (Object localObject1 = "is supported";; localObject1 = "is not supported")
    {
      localStringBuffer1.append((String)localObject1);
      return localStringBuffer1.toString();
      localObject1 = this.wifiSsid;
      break;
      localObject1 = this.BSSID;
      break label52;
      localObject1 = this.capabilities;
      break label74;
      localObject1 = "?";
      break label140;
      localObject1 = "?";
      break label174;
      localObject1 = "no";
      break label206;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    int i = 0;
    if (this.wifiSsid != null)
    {
      paramParcel.writeInt(1);
      this.wifiSsid.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.SSID);
      paramParcel.writeString(this.BSSID);
      paramParcel.writeLong(this.hessid);
      paramParcel.writeInt(this.anqpDomainId);
      paramParcel.writeString(this.capabilities);
      paramParcel.writeInt(this.level);
      paramParcel.writeInt(this.frequency);
      paramParcel.writeLong(this.timestamp);
      paramParcel.writeInt(this.distanceCm);
      paramParcel.writeInt(this.distanceSdCm);
      paramParcel.writeInt(this.channelWidth);
      paramParcel.writeInt(this.centerFreq0);
      paramParcel.writeInt(this.centerFreq1);
      paramParcel.writeLong(this.seen);
      if (!this.untrusted) {
        break label323;
      }
      paramInt = j;
      label148:
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.numConnection);
      paramParcel.writeInt(this.numUsage);
      paramParcel.writeInt(this.numIpConfigFailures);
      paramParcel.writeInt(this.isAutoJoinCandidate);
      if (this.venueName == null) {
        break label328;
      }
      localObject1 = this.venueName.toString();
      label203:
      paramParcel.writeString((String)localObject1);
      if (this.operatorFriendlyName == null) {
        break label336;
      }
    }
    label323:
    label328:
    label336:
    for (Object localObject1 = this.operatorFriendlyName.toString();; localObject1 = "")
    {
      paramParcel.writeString((String)localObject1);
      paramParcel.writeLong(this.flags);
      if (this.informationElements == null) {
        break label344;
      }
      paramParcel.writeInt(this.informationElements.length);
      paramInt = 0;
      while (paramInt < this.informationElements.length)
      {
        paramParcel.writeInt(this.informationElements[paramInt].id);
        paramParcel.writeInt(this.informationElements[paramInt].bytes.length);
        paramParcel.writeByteArray(this.informationElements[paramInt].bytes);
        paramInt += 1;
      }
      paramParcel.writeInt(0);
      break;
      paramInt = 0;
      break label148;
      localObject1 = "";
      break label203;
    }
    label344:
    paramParcel.writeInt(0);
    if (this.anqpLines != null)
    {
      paramParcel.writeInt(this.anqpLines.size());
      paramInt = 0;
      while (paramInt < this.anqpLines.size())
      {
        paramParcel.writeString((String)this.anqpLines.get(paramInt));
        paramInt += 1;
      }
    }
    paramParcel.writeInt(0);
    if (this.anqpElements != null)
    {
      paramParcel.writeInt(this.anqpElements.length);
      localObject1 = this.anqpElements;
      j = localObject1.length;
      paramInt = i;
      while (paramInt < j)
      {
        Object localObject2 = localObject1[paramInt];
        paramParcel.writeInt(((AnqpInformationElement)localObject2).getVendorId());
        paramParcel.writeInt(((AnqpInformationElement)localObject2).getElementId());
        paramParcel.writeInt(((AnqpInformationElement)localObject2).getPayload().length);
        paramParcel.writeByteArray(((AnqpInformationElement)localObject2).getPayload());
        paramInt += 1;
      }
    }
    paramParcel.writeInt(0);
  }
  
  public static class InformationElement
  {
    public static final int EID_BSS_LOAD = 11;
    public static final int EID_ERP = 42;
    public static final int EID_EXTENDED_CAPS = 127;
    public static final int EID_EXTENDED_SUPPORTED_RATES = 50;
    public static final int EID_HT_OPERATION = 61;
    public static final int EID_INTERWORKING = 107;
    public static final int EID_ROAMING_CONSORTIUM = 111;
    public static final int EID_RSN = 48;
    public static final int EID_SSID = 0;
    public static final int EID_SUPPORTED_RATES = 1;
    public static final int EID_TIM = 5;
    public static final int EID_VHT_OPERATION = 192;
    public static final int EID_VSA = 221;
    public byte[] bytes;
    public int id;
    
    public InformationElement() {}
    
    public InformationElement(InformationElement paramInformationElement)
    {
      this.id = paramInformationElement.id;
      this.bytes = ((byte[])paramInformationElement.bytes.clone());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/ScanResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */