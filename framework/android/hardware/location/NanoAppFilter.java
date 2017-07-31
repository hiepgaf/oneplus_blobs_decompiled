package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class NanoAppFilter
{
  public static final int APP_ANY = -1;
  public static final Parcelable.Creator<NanoAppFilter> CREATOR = new Parcelable.Creator()
  {
    public NanoAppFilter createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NanoAppFilter(paramAnonymousParcel, null);
    }
    
    public NanoAppFilter[] newArray(int paramAnonymousInt)
    {
      return new NanoAppFilter[paramAnonymousInt];
    }
  };
  public static final int FLAGS_VERSION_ANY = -1;
  public static final int FLAGS_VERSION_GREAT_THAN = 2;
  public static final int FLAGS_VERSION_LESS_THAN = 4;
  public static final int FLAGS_VERSION_STRICTLY_EQUAL = 8;
  public static final int HUB_ANY = -1;
  private static final String TAG = "NanoAppFilter";
  public static final int VENDOR_ANY = -1;
  private long mAppId;
  private long mAppIdVendorMask;
  private int mAppVersion;
  private int mContextHubId = -1;
  private int mVersionRestrictionMask;
  
  public NanoAppFilter(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    this.mAppId = paramLong1;
    this.mAppVersion = paramInt1;
    this.mVersionRestrictionMask = paramInt2;
    this.mAppIdVendorMask = paramLong2;
  }
  
  private NanoAppFilter(Parcel paramParcel)
  {
    this.mAppId = paramParcel.readLong();
    this.mAppVersion = paramParcel.readInt();
    this.mVersionRestrictionMask = paramParcel.readInt();
    this.mAppIdVendorMask = paramParcel.readInt();
  }
  
  private boolean versionsMatch(int paramInt1, int paramInt2, int paramInt3)
  {
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean testMatch(NanoAppInstanceInfo paramNanoAppInstanceInfo)
  {
    if (((this.mContextHubId == -1) || (paramNanoAppInstanceInfo.getContexthubId() == this.mContextHubId)) && ((this.mAppId == -1L) || (paramNanoAppInstanceInfo.getAppId() == this.mAppId))) {
      return versionsMatch(this.mVersionRestrictionMask, this.mAppVersion, paramNanoAppInstanceInfo.getAppVersion());
    }
    return false;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mAppId);
    paramParcel.writeInt(this.mAppVersion);
    paramParcel.writeInt(this.mVersionRestrictionMask);
    paramParcel.writeLong(this.mAppIdVendorMask);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/NanoAppFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */