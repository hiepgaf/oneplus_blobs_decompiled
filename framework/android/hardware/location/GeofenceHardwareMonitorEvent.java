package android.hardware.location;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GeofenceHardwareMonitorEvent
  implements Parcelable
{
  public static final Parcelable.Creator<GeofenceHardwareMonitorEvent> CREATOR = new Parcelable.Creator()
  {
    public GeofenceHardwareMonitorEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      ClassLoader localClassLoader = GeofenceHardwareMonitorEvent.class.getClassLoader();
      return new GeofenceHardwareMonitorEvent(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), (Location)paramAnonymousParcel.readParcelable(localClassLoader));
    }
    
    public GeofenceHardwareMonitorEvent[] newArray(int paramAnonymousInt)
    {
      return new GeofenceHardwareMonitorEvent[paramAnonymousInt];
    }
  };
  private final Location mLocation;
  private final int mMonitoringStatus;
  private final int mMonitoringType;
  private final int mSourceTechnologies;
  
  public GeofenceHardwareMonitorEvent(int paramInt1, int paramInt2, int paramInt3, Location paramLocation)
  {
    this.mMonitoringType = paramInt1;
    this.mMonitoringStatus = paramInt2;
    this.mSourceTechnologies = paramInt3;
    this.mLocation = paramLocation;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Location getLocation()
  {
    return this.mLocation;
  }
  
  public int getMonitoringStatus()
  {
    return this.mMonitoringStatus;
  }
  
  public int getMonitoringType()
  {
    return this.mMonitoringType;
  }
  
  public int getSourceTechnologies()
  {
    return this.mSourceTechnologies;
  }
  
  public String toString()
  {
    return String.format("GeofenceHardwareMonitorEvent: type=%d, status=%d, sources=%d, location=%s", new Object[] { Integer.valueOf(this.mMonitoringType), Integer.valueOf(this.mMonitoringStatus), Integer.valueOf(this.mSourceTechnologies), this.mLocation });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mMonitoringType);
    paramParcel.writeInt(this.mMonitoringStatus);
    paramParcel.writeInt(this.mSourceTechnologies);
    paramParcel.writeParcelable(this.mLocation, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareMonitorEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */