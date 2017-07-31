package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class NetworkKey
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkKey> CREATOR = new Parcelable.Creator()
  {
    public NetworkKey createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkKey(paramAnonymousParcel, null);
    }
    
    public NetworkKey[] newArray(int paramAnonymousInt)
    {
      return new NetworkKey[paramAnonymousInt];
    }
  };
  public static final int TYPE_WIFI = 1;
  public final int type;
  public final WifiKey wifiKey;
  
  public NetworkKey(WifiKey paramWifiKey)
  {
    this.type = 1;
    this.wifiKey = paramWifiKey;
  }
  
  private NetworkKey(Parcel paramParcel)
  {
    this.type = paramParcel.readInt();
    switch (this.type)
    {
    default: 
      throw new IllegalArgumentException("Parcel has unknown type: " + this.type);
    }
    this.wifiKey = ((WifiKey)WifiKey.CREATOR.createFromParcel(paramParcel));
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
    paramObject = (NetworkKey)paramObject;
    if (this.type == ((NetworkKey)paramObject).type) {
      bool = Objects.equals(this.wifiKey, ((NetworkKey)paramObject).wifiKey);
    }
    return bool;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.type), this.wifiKey });
  }
  
  public String toString()
  {
    switch (this.type)
    {
    default: 
      return "InvalidKey";
    }
    return this.wifiKey.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.type);
    switch (this.type)
    {
    default: 
      throw new IllegalStateException("NetworkKey has unknown type " + this.type);
    }
    this.wifiKey.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */