package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class NetworkState
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkState> CREATOR = new Parcelable.Creator()
  {
    public NetworkState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkState(paramAnonymousParcel);
    }
    
    public NetworkState[] newArray(int paramAnonymousInt)
    {
      return new NetworkState[paramAnonymousInt];
    }
  };
  public static final NetworkState EMPTY = new NetworkState(null, null, null, null, null, null);
  public final LinkProperties linkProperties;
  public final Network network;
  public final NetworkCapabilities networkCapabilities;
  public final String networkId;
  public final NetworkInfo networkInfo;
  public final String subscriberId;
  
  public NetworkState(NetworkInfo paramNetworkInfo, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, Network paramNetwork, String paramString1, String paramString2)
  {
    this.networkInfo = paramNetworkInfo;
    this.linkProperties = paramLinkProperties;
    this.networkCapabilities = paramNetworkCapabilities;
    this.network = paramNetwork;
    this.subscriberId = paramString1;
    this.networkId = paramString2;
  }
  
  public NetworkState(Parcel paramParcel)
  {
    this.networkInfo = ((NetworkInfo)paramParcel.readParcelable(null));
    this.linkProperties = ((LinkProperties)paramParcel.readParcelable(null));
    this.networkCapabilities = ((NetworkCapabilities)paramParcel.readParcelable(null));
    this.network = ((Network)paramParcel.readParcelable(null));
    this.subscriberId = paramParcel.readString();
    this.networkId = paramParcel.readString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.networkInfo, paramInt);
    paramParcel.writeParcelable(this.linkProperties, paramInt);
    paramParcel.writeParcelable(this.networkCapabilities, paramInt);
    paramParcel.writeParcelable(this.network, paramInt);
    paramParcel.writeString(this.subscriberId);
    paramParcel.writeString(this.networkId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */