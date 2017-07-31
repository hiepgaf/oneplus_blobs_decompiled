package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.os.WorkSource;
import android.util.TimeUtils;

public final class LocationRequest
  implements Parcelable
{
  public static final int ACCURACY_BLOCK = 102;
  public static final int ACCURACY_CITY = 104;
  public static final int ACCURACY_FINE = 100;
  public static final Parcelable.Creator<LocationRequest> CREATOR = new Parcelable.Creator()
  {
    public LocationRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool = false;
      LocationRequest localLocationRequest = new LocationRequest();
      localLocationRequest.setQuality(paramAnonymousParcel.readInt());
      localLocationRequest.setFastestInterval(paramAnonymousParcel.readLong());
      localLocationRequest.setInterval(paramAnonymousParcel.readLong());
      localLocationRequest.setExpireAt(paramAnonymousParcel.readLong());
      localLocationRequest.setNumUpdates(paramAnonymousParcel.readInt());
      localLocationRequest.setSmallestDisplacement(paramAnonymousParcel.readFloat());
      if (paramAnonymousParcel.readInt() != 0) {
        bool = true;
      }
      localLocationRequest.setHideFromAppOps(bool);
      String str = paramAnonymousParcel.readString();
      if (str != null) {
        localLocationRequest.setProvider(str);
      }
      paramAnonymousParcel = (WorkSource)paramAnonymousParcel.readParcelable(null);
      if (paramAnonymousParcel != null) {
        localLocationRequest.setWorkSource(paramAnonymousParcel);
      }
      return localLocationRequest;
    }
    
    public LocationRequest[] newArray(int paramAnonymousInt)
    {
      return new LocationRequest[paramAnonymousInt];
    }
  };
  private static final double FASTEST_INTERVAL_FACTOR = 6.0D;
  public static final int POWER_HIGH = 203;
  public static final int POWER_LOW = 201;
  public static final int POWER_NONE = 200;
  private long mExpireAt = Long.MAX_VALUE;
  private boolean mExplicitFastestInterval = false;
  private long mFastestInterval = (this.mInterval / 6.0D);
  private boolean mHideFromAppOps = false;
  private long mInterval = 3600000L;
  private int mNumUpdates = Integer.MAX_VALUE;
  private String mProvider = "fused";
  private int mQuality = 201;
  private float mSmallestDisplacement = 0.0F;
  private WorkSource mWorkSource = null;
  
  public LocationRequest() {}
  
  public LocationRequest(LocationRequest paramLocationRequest)
  {
    this.mQuality = paramLocationRequest.mQuality;
    this.mInterval = paramLocationRequest.mInterval;
    this.mFastestInterval = paramLocationRequest.mFastestInterval;
    this.mExplicitFastestInterval = paramLocationRequest.mExplicitFastestInterval;
    this.mExpireAt = paramLocationRequest.mExpireAt;
    this.mNumUpdates = paramLocationRequest.mNumUpdates;
    this.mSmallestDisplacement = paramLocationRequest.mSmallestDisplacement;
    this.mProvider = paramLocationRequest.mProvider;
    this.mWorkSource = paramLocationRequest.mWorkSource;
    this.mHideFromAppOps = paramLocationRequest.mHideFromAppOps;
  }
  
  private static void checkDisplacement(float paramFloat)
  {
    if (paramFloat < 0.0F) {
      throw new IllegalArgumentException("invalid displacement: " + paramFloat);
    }
  }
  
  private static void checkInterval(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("invalid interval: " + paramLong);
    }
  }
  
  private static void checkProvider(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("invalid provider: " + paramString);
    }
  }
  
  private static void checkQuality(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("invalid quality: " + paramInt);
    }
  }
  
  public static LocationRequest create()
  {
    return new LocationRequest();
  }
  
  public static LocationRequest createFromDeprecatedCriteria(Criteria paramCriteria, long paramLong, float paramFloat, boolean paramBoolean)
  {
    long l = paramLong;
    if (paramLong < 0L) {
      l = 0L;
    }
    float f = paramFloat;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    int i;
    switch (paramCriteria.getAccuracy())
    {
    default: 
      switch (paramCriteria.getPowerRequirement())
      {
      default: 
        i = 201;
      }
      break;
    }
    for (;;)
    {
      paramCriteria = new LocationRequest().setQuality(i).setInterval(l).setFastestInterval(l).setSmallestDisplacement(f);
      if (paramBoolean) {
        paramCriteria.setNumUpdates(1);
      }
      return paramCriteria;
      i = 102;
      continue;
      i = 100;
      continue;
      i = 203;
    }
  }
  
  public static LocationRequest createFromDeprecatedProvider(String paramString, long paramLong, float paramFloat, boolean paramBoolean)
  {
    long l = paramLong;
    if (paramLong < 0L) {
      l = 0L;
    }
    float f = paramFloat;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    int i;
    if ("passive".equals(paramString)) {
      i = 200;
    }
    for (;;)
    {
      paramString = new LocationRequest().setProvider(paramString).setQuality(i).setInterval(l).setFastestInterval(l).setSmallestDisplacement(f);
      if (paramBoolean) {
        paramString.setNumUpdates(1);
      }
      return paramString;
      if ("gps".equals(paramString)) {
        i = 100;
      } else {
        i = 201;
      }
    }
  }
  
  public static String qualityToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "???";
    case 100: 
      return "ACCURACY_FINE";
    case 102: 
      return "ACCURACY_BLOCK";
    case 104: 
      return "ACCURACY_CITY";
    case 200: 
      return "POWER_NONE";
    case 201: 
      return "POWER_LOW";
    }
    return "POWER_HIGH";
  }
  
  public void decrementNumUpdates()
  {
    if (this.mNumUpdates != Integer.MAX_VALUE) {
      this.mNumUpdates -= 1;
    }
    if (this.mNumUpdates < 0) {
      this.mNumUpdates = 0;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getExpireAt()
  {
    return this.mExpireAt;
  }
  
  public long getFastestInterval()
  {
    return this.mFastestInterval;
  }
  
  public boolean getHideFromAppOps()
  {
    return this.mHideFromAppOps;
  }
  
  public long getInterval()
  {
    return this.mInterval;
  }
  
  public int getNumUpdates()
  {
    return this.mNumUpdates;
  }
  
  public String getProvider()
  {
    return this.mProvider;
  }
  
  public int getQuality()
  {
    return this.mQuality;
  }
  
  public float getSmallestDisplacement()
  {
    return this.mSmallestDisplacement;
  }
  
  public WorkSource getWorkSource()
  {
    return this.mWorkSource;
  }
  
  public LocationRequest setExpireAt(long paramLong)
  {
    this.mExpireAt = paramLong;
    if (this.mExpireAt < 0L) {
      this.mExpireAt = 0L;
    }
    return this;
  }
  
  public LocationRequest setExpireIn(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    if (paramLong > Long.MAX_VALUE - l) {}
    for (this.mExpireAt = Long.MAX_VALUE;; this.mExpireAt = (paramLong + l))
    {
      if (this.mExpireAt < 0L) {
        this.mExpireAt = 0L;
      }
      return this;
    }
  }
  
  public LocationRequest setFastestInterval(long paramLong)
  {
    checkInterval(paramLong);
    this.mExplicitFastestInterval = true;
    this.mFastestInterval = paramLong;
    return this;
  }
  
  public void setHideFromAppOps(boolean paramBoolean)
  {
    this.mHideFromAppOps = paramBoolean;
  }
  
  public LocationRequest setInterval(long paramLong)
  {
    checkInterval(paramLong);
    this.mInterval = paramLong;
    if (!this.mExplicitFastestInterval) {
      this.mFastestInterval = ((this.mInterval / 6.0D));
    }
    return this;
  }
  
  public LocationRequest setNumUpdates(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("invalid numUpdates: " + paramInt);
    }
    this.mNumUpdates = paramInt;
    return this;
  }
  
  public LocationRequest setProvider(String paramString)
  {
    checkProvider(paramString);
    this.mProvider = paramString;
    return this;
  }
  
  public LocationRequest setQuality(int paramInt)
  {
    checkQuality(paramInt);
    this.mQuality = paramInt;
    return this;
  }
  
  public LocationRequest setSmallestDisplacement(float paramFloat)
  {
    checkDisplacement(paramFloat);
    this.mSmallestDisplacement = paramFloat;
    return this;
  }
  
  public void setWorkSource(WorkSource paramWorkSource)
  {
    this.mWorkSource = paramWorkSource;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Request[").append(qualityToString(this.mQuality));
    if (this.mProvider != null) {
      localStringBuilder.append(' ').append(this.mProvider);
    }
    if (this.mQuality != 200)
    {
      localStringBuilder.append(" requested=");
      TimeUtils.formatDuration(this.mInterval, localStringBuilder);
    }
    localStringBuilder.append(" fastest=");
    TimeUtils.formatDuration(this.mFastestInterval, localStringBuilder);
    if (this.mExpireAt != Long.MAX_VALUE)
    {
      long l1 = this.mExpireAt;
      long l2 = SystemClock.elapsedRealtime();
      localStringBuilder.append(" expireIn=");
      TimeUtils.formatDuration(l1 - l2, localStringBuilder);
    }
    if (this.mNumUpdates != Integer.MAX_VALUE) {
      localStringBuilder.append(" num=").append(this.mNumUpdates);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mQuality);
    paramParcel.writeLong(this.mFastestInterval);
    paramParcel.writeLong(this.mInterval);
    paramParcel.writeLong(this.mExpireAt);
    paramParcel.writeInt(this.mNumUpdates);
    paramParcel.writeFloat(this.mSmallestDisplacement);
    if (this.mHideFromAppOps) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeString(this.mProvider);
      paramParcel.writeParcelable(this.mWorkSource, 0);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/LocationRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */