package android.os;

public class BatteryProperty
  implements Parcelable
{
  public static final Parcelable.Creator<BatteryProperty> CREATOR = new Parcelable.Creator()
  {
    public BatteryProperty createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BatteryProperty(paramAnonymousParcel, null);
    }
    
    public BatteryProperty[] newArray(int paramAnonymousInt)
    {
      return new BatteryProperty[paramAnonymousInt];
    }
  };
  private long mValueLong;
  
  public BatteryProperty()
  {
    this.mValueLong = Long.MIN_VALUE;
  }
  
  private BatteryProperty(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getLong()
  {
    return this.mValueLong;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mValueLong = paramParcel.readLong();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mValueLong);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BatteryProperty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */