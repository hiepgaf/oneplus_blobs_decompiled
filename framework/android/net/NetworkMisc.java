package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class NetworkMisc
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkMisc> CREATOR = new Parcelable.Creator()
  {
    public NetworkMisc createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = true;
      NetworkMisc localNetworkMisc = new NetworkMisc();
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        localNetworkMisc.allowBypass = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label88;
        }
        bool1 = true;
        label35:
        localNetworkMisc.explicitlySelected = bool1;
        if (paramAnonymousParcel.readInt() == 0) {
          break label93;
        }
        bool1 = true;
        label50:
        localNetworkMisc.acceptUnvalidated = bool1;
        localNetworkMisc.subscriberId = paramAnonymousParcel.readString();
        if (paramAnonymousParcel.readInt() == 0) {
          break label98;
        }
      }
      label88:
      label93:
      label98:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        localNetworkMisc.provisioningNotificationDisabled = bool1;
        return localNetworkMisc;
        bool1 = false;
        break;
        bool1 = false;
        break label35;
        bool1 = false;
        break label50;
      }
    }
    
    public NetworkMisc[] newArray(int paramAnonymousInt)
    {
      return new NetworkMisc[paramAnonymousInt];
    }
  };
  public boolean acceptUnvalidated;
  public boolean allowBypass;
  public boolean explicitlySelected;
  public boolean provisioningNotificationDisabled;
  public String subscriberId;
  
  public NetworkMisc() {}
  
  public NetworkMisc(NetworkMisc paramNetworkMisc)
  {
    if (paramNetworkMisc != null)
    {
      this.allowBypass = paramNetworkMisc.allowBypass;
      this.explicitlySelected = paramNetworkMisc.explicitlySelected;
      this.acceptUnvalidated = paramNetworkMisc.acceptUnvalidated;
      this.subscriberId = paramNetworkMisc.subscriberId;
      this.provisioningNotificationDisabled = paramNetworkMisc.provisioningNotificationDisabled;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.allowBypass)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.explicitlySelected) {
        break label72;
      }
      paramInt = 1;
      label25:
      paramParcel.writeInt(paramInt);
      if (!this.acceptUnvalidated) {
        break label77;
      }
      paramInt = 1;
      label39:
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.subscriberId);
      if (!this.provisioningNotificationDisabled) {
        break label82;
      }
    }
    label72:
    label77:
    label82:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label25;
      paramInt = 0;
      break label39;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/NetworkMisc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */