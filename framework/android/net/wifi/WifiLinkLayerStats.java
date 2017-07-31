package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class WifiLinkLayerStats
  implements Parcelable
{
  public static final Parcelable.Creator<WifiLinkLayerStats> CREATOR = new Parcelable.Creator()
  {
    public WifiLinkLayerStats createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiLinkLayerStats localWifiLinkLayerStats = new WifiLinkLayerStats();
      localWifiLinkLayerStats.SSID = paramAnonymousParcel.readString();
      localWifiLinkLayerStats.BSSID = paramAnonymousParcel.readString();
      localWifiLinkLayerStats.on_time = paramAnonymousParcel.readInt();
      localWifiLinkLayerStats.tx_time = paramAnonymousParcel.readInt();
      localWifiLinkLayerStats.tx_time_per_level = paramAnonymousParcel.createIntArray();
      localWifiLinkLayerStats.rx_time = paramAnonymousParcel.readInt();
      localWifiLinkLayerStats.on_time_scan = paramAnonymousParcel.readInt();
      return localWifiLinkLayerStats;
    }
    
    public WifiLinkLayerStats[] newArray(int paramAnonymousInt)
    {
      return new WifiLinkLayerStats[paramAnonymousInt];
    }
  };
  private static final String TAG = "WifiLinkLayerStats";
  public String BSSID;
  public String SSID;
  public int beacon_rx;
  public long lostmpdu_be;
  public long lostmpdu_bk;
  public long lostmpdu_vi;
  public long lostmpdu_vo;
  public int on_time;
  public int on_time_scan;
  public long retries_be;
  public long retries_bk;
  public long retries_vi;
  public long retries_vo;
  public int rssi_mgmt;
  public int rx_time;
  public long rxmpdu_be;
  public long rxmpdu_bk;
  public long rxmpdu_vi;
  public long rxmpdu_vo;
  public int status;
  public int tx_time;
  public int[] tx_time_per_level;
  public long txmpdu_be;
  public long txmpdu_bk;
  public long txmpdu_vi;
  public long txmpdu_vo;
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getPrintableSsid()
  {
    if (this.SSID == null) {
      return "";
    }
    int i = this.SSID.length();
    if ((i > 2) && (this.SSID.charAt(0) == '"') && (this.SSID.charAt(i - 1) == '"')) {
      return this.SSID.substring(1, i - 1);
    }
    if ((i > 3) && (this.SSID.charAt(0) == 'P') && (this.SSID.charAt(1) == '"') && (this.SSID.charAt(i - 1) == '"')) {
      return WifiSsid.createFromAsciiEncoded(this.SSID.substring(2, i - 1)).toString();
    }
    return this.SSID;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(" WifiLinkLayerStats: ").append('\n');
    if (this.SSID != null) {
      localStringBuilder.append(" SSID: ").append(this.SSID).append('\n');
    }
    if (this.BSSID != null) {
      localStringBuilder.append(" BSSID: ").append(this.BSSID).append('\n');
    }
    localStringBuilder.append(" my bss beacon rx: ").append(Integer.toString(this.beacon_rx)).append('\n');
    localStringBuilder.append(" RSSI mgmt: ").append(Integer.toString(this.rssi_mgmt)).append('\n');
    localStringBuilder.append(" BE : ").append(" rx=").append(Long.toString(this.rxmpdu_be)).append(" tx=").append(Long.toString(this.txmpdu_be)).append(" lost=").append(Long.toString(this.lostmpdu_be)).append(" retries=").append(Long.toString(this.retries_be)).append('\n');
    localStringBuilder.append(" BK : ").append(" rx=").append(Long.toString(this.rxmpdu_bk)).append(" tx=").append(Long.toString(this.txmpdu_bk)).append(" lost=").append(Long.toString(this.lostmpdu_bk)).append(" retries=").append(Long.toString(this.retries_bk)).append('\n');
    localStringBuilder.append(" VI : ").append(" rx=").append(Long.toString(this.rxmpdu_vi)).append(" tx=").append(Long.toString(this.txmpdu_vi)).append(" lost=").append(Long.toString(this.lostmpdu_vi)).append(" retries=").append(Long.toString(this.retries_vi)).append('\n');
    localStringBuilder.append(" VO : ").append(" rx=").append(Long.toString(this.rxmpdu_vo)).append(" tx=").append(Long.toString(this.txmpdu_vo)).append(" lost=").append(Long.toString(this.lostmpdu_vo)).append(" retries=").append(Long.toString(this.retries_vo)).append('\n');
    localStringBuilder.append(" on_time : ").append(Integer.toString(this.on_time)).append(" rx_time=").append(Integer.toString(this.rx_time)).append(" scan_time=").append(Integer.toString(this.on_time_scan)).append('\n').append(" tx_time=").append(Integer.toString(this.tx_time)).append(" tx_time_per_level=").append(Arrays.toString(this.tx_time_per_level));
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.SSID);
    paramParcel.writeString(this.BSSID);
    paramParcel.writeInt(this.on_time);
    paramParcel.writeInt(this.tx_time);
    paramParcel.writeIntArray(this.tx_time_per_level);
    paramParcel.writeInt(this.rx_time);
    paramParcel.writeInt(this.on_time_scan);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiLinkLayerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */