package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class HealthStatsParceler
  implements Parcelable
{
  public static final Parcelable.Creator<HealthStatsParceler> CREATOR = new Parcelable.Creator()
  {
    public HealthStatsParceler createFromParcel(Parcel paramAnonymousParcel)
    {
      return new HealthStatsParceler(paramAnonymousParcel);
    }
    
    public HealthStatsParceler[] newArray(int paramAnonymousInt)
    {
      return new HealthStatsParceler[paramAnonymousInt];
    }
  };
  private HealthStats mHealthStats;
  private HealthStatsWriter mWriter;
  
  public HealthStatsParceler(Parcel paramParcel)
  {
    this.mHealthStats = new HealthStats(paramParcel);
  }
  
  public HealthStatsParceler(HealthStatsWriter paramHealthStatsWriter)
  {
    this.mWriter = paramHealthStatsWriter;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public HealthStats getHealthStats()
  {
    if (this.mWriter != null)
    {
      Parcel localParcel = Parcel.obtain();
      this.mWriter.flattenToParcel(localParcel);
      localParcel.setDataPosition(0);
      this.mHealthStats = new HealthStats(localParcel);
      localParcel.recycle();
    }
    return this.mHealthStats;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mWriter != null)
    {
      this.mWriter.flattenToParcel(paramParcel);
      return;
    }
    throw new RuntimeException("Can not re-parcel HealthStatsParceler that was constructed from a Parcel");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/HealthStatsParceler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */