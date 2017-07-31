package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Criteria
  implements Parcelable
{
  public static final int ACCURACY_COARSE = 2;
  public static final int ACCURACY_FINE = 1;
  public static final int ACCURACY_HIGH = 3;
  public static final int ACCURACY_LOW = 1;
  public static final int ACCURACY_MEDIUM = 2;
  public static final Parcelable.Creator<Criteria> CREATOR = new Parcelable.Creator()
  {
    public Criteria createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = true;
      Criteria localCriteria = new Criteria();
      Criteria.-set4(localCriteria, paramAnonymousParcel.readInt());
      Criteria.-set8(localCriteria, paramAnonymousParcel.readInt());
      Criteria.-set6(localCriteria, paramAnonymousParcel.readInt());
      Criteria.-set1(localCriteria, paramAnonymousParcel.readInt());
      Criteria.-set5(localCriteria, paramAnonymousParcel.readInt());
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        Criteria.-set0(localCriteria, bool1);
        if (paramAnonymousParcel.readInt() == 0) {
          break label133;
        }
        bool1 = true;
        label86:
        Criteria.-set2(localCriteria, bool1);
        if (paramAnonymousParcel.readInt() == 0) {
          break label138;
        }
        bool1 = true;
        label102:
        Criteria.-set7(localCriteria, bool1);
        if (paramAnonymousParcel.readInt() == 0) {
          break label143;
        }
      }
      label133:
      label138:
      label143:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        Criteria.-set3(localCriteria, bool1);
        return localCriteria;
        bool1 = false;
        break;
        bool1 = false;
        break label86;
        bool1 = false;
        break label102;
      }
    }
    
    public Criteria[] newArray(int paramAnonymousInt)
    {
      return new Criteria[paramAnonymousInt];
    }
  };
  public static final int NO_REQUIREMENT = 0;
  public static final int POWER_HIGH = 3;
  public static final int POWER_LOW = 1;
  public static final int POWER_MEDIUM = 2;
  private boolean mAltitudeRequired = false;
  private int mBearingAccuracy = 0;
  private boolean mBearingRequired = false;
  private boolean mCostAllowed = false;
  private int mHorizontalAccuracy = 0;
  private int mPowerRequirement = 0;
  private int mSpeedAccuracy = 0;
  private boolean mSpeedRequired = false;
  private int mVerticalAccuracy = 0;
  
  public Criteria() {}
  
  public Criteria(Criteria paramCriteria)
  {
    this.mHorizontalAccuracy = paramCriteria.mHorizontalAccuracy;
    this.mVerticalAccuracy = paramCriteria.mVerticalAccuracy;
    this.mSpeedAccuracy = paramCriteria.mSpeedAccuracy;
    this.mBearingAccuracy = paramCriteria.mBearingAccuracy;
    this.mPowerRequirement = paramCriteria.mPowerRequirement;
    this.mAltitudeRequired = paramCriteria.mAltitudeRequired;
    this.mBearingRequired = paramCriteria.mBearingRequired;
    this.mSpeedRequired = paramCriteria.mSpeedRequired;
    this.mCostAllowed = paramCriteria.mCostAllowed;
  }
  
  private static String accuracyToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "???";
    case 0: 
      return "---";
    case 3: 
      return "HIGH";
    case 2: 
      return "MEDIUM";
    }
    return "LOW";
  }
  
  private static String powerToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "???";
    case 0: 
      return "NO_REQ";
    case 1: 
      return "LOW";
    case 2: 
      return "MEDIUM";
    }
    return "HIGH";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAccuracy()
  {
    if (this.mHorizontalAccuracy >= 3) {
      return 1;
    }
    return 2;
  }
  
  public int getBearingAccuracy()
  {
    return this.mBearingAccuracy;
  }
  
  public int getHorizontalAccuracy()
  {
    return this.mHorizontalAccuracy;
  }
  
  public int getPowerRequirement()
  {
    return this.mPowerRequirement;
  }
  
  public int getSpeedAccuracy()
  {
    return this.mSpeedAccuracy;
  }
  
  public int getVerticalAccuracy()
  {
    return this.mVerticalAccuracy;
  }
  
  public boolean isAltitudeRequired()
  {
    return this.mAltitudeRequired;
  }
  
  public boolean isBearingRequired()
  {
    return this.mBearingRequired;
  }
  
  public boolean isCostAllowed()
  {
    return this.mCostAllowed;
  }
  
  public boolean isSpeedRequired()
  {
    return this.mSpeedRequired;
  }
  
  public void setAccuracy(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 2)) {
      throw new IllegalArgumentException("accuracy=" + paramInt);
    }
    if (paramInt == 1)
    {
      this.mHorizontalAccuracy = 3;
      return;
    }
    this.mHorizontalAccuracy = 1;
  }
  
  public void setAltitudeRequired(boolean paramBoolean)
  {
    this.mAltitudeRequired = paramBoolean;
  }
  
  public void setBearingAccuracy(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("accuracy=" + paramInt);
    }
    this.mBearingAccuracy = paramInt;
  }
  
  public void setBearingRequired(boolean paramBoolean)
  {
    this.mBearingRequired = paramBoolean;
  }
  
  public void setCostAllowed(boolean paramBoolean)
  {
    this.mCostAllowed = paramBoolean;
  }
  
  public void setHorizontalAccuracy(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("accuracy=" + paramInt);
    }
    this.mHorizontalAccuracy = paramInt;
  }
  
  public void setPowerRequirement(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("level=" + paramInt);
    }
    this.mPowerRequirement = paramInt;
  }
  
  public void setSpeedAccuracy(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("accuracy=" + paramInt);
    }
    this.mSpeedAccuracy = paramInt;
  }
  
  public void setSpeedRequired(boolean paramBoolean)
  {
    this.mSpeedRequired = paramBoolean;
  }
  
  public void setVerticalAccuracy(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("accuracy=" + paramInt);
    }
    this.mVerticalAccuracy = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Criteria[power=").append(powerToString(this.mPowerRequirement));
    localStringBuilder.append(" acc=").append(accuracyToString(this.mHorizontalAccuracy));
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(this.mHorizontalAccuracy);
    paramParcel.writeInt(this.mVerticalAccuracy);
    paramParcel.writeInt(this.mSpeedAccuracy);
    paramParcel.writeInt(this.mBearingAccuracy);
    paramParcel.writeInt(this.mPowerRequirement);
    if (this.mAltitudeRequired)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mBearingRequired) {
        break label104;
      }
      paramInt = 1;
      label65:
      paramParcel.writeInt(paramInt);
      if (!this.mSpeedRequired) {
        break label109;
      }
      paramInt = 1;
      label79:
      paramParcel.writeInt(paramInt);
      if (!this.mCostAllowed) {
        break label114;
      }
    }
    label104:
    label109:
    label114:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label65;
      paramInt = 0;
      break label79;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Criteria.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */