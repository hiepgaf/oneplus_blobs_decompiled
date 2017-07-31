package android.hardware.fingerprint;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class Fingerprint
  implements Parcelable
{
  public static final Parcelable.Creator<Fingerprint> CREATOR = new Parcelable.Creator()
  {
    public Fingerprint createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Fingerprint(paramAnonymousParcel, null);
    }
    
    public Fingerprint[] newArray(int paramAnonymousInt)
    {
      return new Fingerprint[paramAnonymousInt];
    }
  };
  private long mDeviceId;
  private int mFingerId;
  private int mGroupId;
  private CharSequence mName;
  
  private Fingerprint(Parcel paramParcel)
  {
    this.mName = paramParcel.readString();
    this.mGroupId = paramParcel.readInt();
    this.mFingerId = paramParcel.readInt();
    this.mDeviceId = paramParcel.readLong();
  }
  
  public Fingerprint(CharSequence paramCharSequence, int paramInt1, int paramInt2, long paramLong)
  {
    this.mName = paramCharSequence;
    this.mGroupId = paramInt1;
    this.mFingerId = paramInt2;
    this.mDeviceId = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getDeviceId()
  {
    return this.mDeviceId;
  }
  
  public int getFingerId()
  {
    return this.mFingerId;
  }
  
  public int getGroupId()
  {
    return this.mGroupId;
  }
  
  public CharSequence getName()
  {
    return this.mName;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName.toString());
    paramParcel.writeInt(this.mGroupId);
    paramParcel.writeInt(this.mFingerId);
    paramParcel.writeLong(this.mDeviceId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/Fingerprint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */