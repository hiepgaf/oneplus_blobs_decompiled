package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiKey
  implements Parcelable
{
  private static final Pattern BSSID_PATTERN = Pattern.compile("([\\p{XDigit}]{2}:){5}[\\p{XDigit}]{2}");
  public static final Parcelable.Creator<WifiKey> CREATOR = new Parcelable.Creator()
  {
    public WifiKey createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WifiKey(paramAnonymousParcel, null);
    }
    
    public WifiKey[] newArray(int paramAnonymousInt)
    {
      return new WifiKey[paramAnonymousInt];
    }
  };
  private static final Pattern SSID_PATTERN = Pattern.compile("(\".*\")|(0x[\\p{XDigit}]+)", 32);
  public final String bssid;
  public final String ssid;
  
  private WifiKey(Parcel paramParcel)
  {
    this.ssid = paramParcel.readString();
    this.bssid = paramParcel.readString();
  }
  
  public WifiKey(String paramString1, String paramString2)
  {
    if (!SSID_PATTERN.matcher(paramString1).matches()) {
      throw new IllegalArgumentException("Invalid ssid: " + paramString1);
    }
    if (!BSSID_PATTERN.matcher(paramString2).matches()) {
      throw new IllegalArgumentException("Invalid bssid: " + paramString2);
    }
    this.ssid = paramString1;
    this.bssid = paramString2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (WifiKey)paramObject;
    if (Objects.equals(this.ssid, ((WifiKey)paramObject).ssid)) {
      bool = Objects.equals(this.bssid, ((WifiKey)paramObject).bssid);
    }
    return bool;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.ssid, this.bssid });
  }
  
  public String toString()
  {
    return "WifiKey[SSID=" + this.ssid + ",BSSID=" + this.bssid + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.ssid);
    paramParcel.writeString(this.bssid);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/WifiKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */