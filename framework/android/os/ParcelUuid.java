package android.os;

import java.util.UUID;

public final class ParcelUuid
  implements Parcelable
{
  public static final Parcelable.Creator<ParcelUuid> CREATOR = new Parcelable.Creator()
  {
    public ParcelUuid createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ParcelUuid(new UUID(paramAnonymousParcel.readLong(), paramAnonymousParcel.readLong()));
    }
    
    public ParcelUuid[] newArray(int paramAnonymousInt)
    {
      return new ParcelUuid[paramAnonymousInt];
    }
  };
  private final UUID mUuid;
  
  public ParcelUuid(UUID paramUUID)
  {
    this.mUuid = paramUUID;
  }
  
  public static ParcelUuid fromString(String paramString)
  {
    return new ParcelUuid(UUID.fromString(paramString));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ParcelUuid)) {
      return false;
    }
    paramObject = (ParcelUuid)paramObject;
    return this.mUuid.equals(((ParcelUuid)paramObject).mUuid);
  }
  
  public UUID getUuid()
  {
    return this.mUuid;
  }
  
  public int hashCode()
  {
    return this.mUuid.hashCode();
  }
  
  public String toString()
  {
    return this.mUuid.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mUuid.getMostSignificantBits());
    paramParcel.writeLong(this.mUuid.getLeastSignificantBits());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/ParcelUuid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */