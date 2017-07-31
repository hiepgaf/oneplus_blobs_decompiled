package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class ScoredNetwork
  implements Parcelable
{
  public static final Parcelable.Creator<ScoredNetwork> CREATOR = new Parcelable.Creator()
  {
    public ScoredNetwork createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ScoredNetwork(paramAnonymousParcel, null);
    }
    
    public ScoredNetwork[] newArray(int paramAnonymousInt)
    {
      return new ScoredNetwork[paramAnonymousInt];
    }
  };
  public final boolean meteredHint;
  public final NetworkKey networkKey;
  public final RssiCurve rssiCurve;
  
  public ScoredNetwork(NetworkKey paramNetworkKey, RssiCurve paramRssiCurve)
  {
    this(paramNetworkKey, paramRssiCurve, false);
  }
  
  public ScoredNetwork(NetworkKey paramNetworkKey, RssiCurve paramRssiCurve, boolean paramBoolean)
  {
    this.networkKey = paramNetworkKey;
    this.rssiCurve = paramRssiCurve;
    this.meteredHint = paramBoolean;
  }
  
  private ScoredNetwork(Parcel paramParcel)
  {
    this.networkKey = ((NetworkKey)NetworkKey.CREATOR.createFromParcel(paramParcel));
    if (paramParcel.readByte() == 1)
    {
      this.rssiCurve = ((RssiCurve)RssiCurve.CREATOR.createFromParcel(paramParcel));
      if (paramParcel.readByte() == 0) {
        break label67;
      }
    }
    label67:
    for (boolean bool = true;; bool = false)
    {
      this.meteredHint = bool;
      return;
      this.rssiCurve = null;
      break;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (ScoredNetwork)paramObject;
    boolean bool1 = bool2;
    if (Objects.equals(this.networkKey, ((ScoredNetwork)paramObject).networkKey))
    {
      bool1 = bool2;
      if (Objects.equals(this.rssiCurve, ((ScoredNetwork)paramObject).rssiCurve)) {
        bool1 = Objects.equals(Boolean.valueOf(this.meteredHint), Boolean.valueOf(((ScoredNetwork)paramObject).meteredHint));
      }
    }
    return bool1;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.networkKey, this.rssiCurve, Boolean.valueOf(this.meteredHint) });
  }
  
  public String toString()
  {
    return "ScoredNetwork[key=" + this.networkKey + ",score=" + this.rssiCurve + ",meteredHint=" + this.meteredHint + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    this.networkKey.writeToParcel(paramParcel, paramInt);
    if (this.rssiCurve != null)
    {
      paramParcel.writeByte((byte)1);
      this.rssiCurve.writeToParcel(paramParcel, paramInt);
      if (!this.meteredHint) {
        break label56;
      }
    }
    label56:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
      paramParcel.writeByte((byte)0);
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ScoredNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */