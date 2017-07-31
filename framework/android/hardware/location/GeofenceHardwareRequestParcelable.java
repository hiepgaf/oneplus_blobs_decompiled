package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class GeofenceHardwareRequestParcelable
  implements Parcelable
{
  public static final Parcelable.Creator<GeofenceHardwareRequestParcelable> CREATOR = new Parcelable.Creator()
  {
    public GeofenceHardwareRequestParcelable createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (i != 0)
      {
        Log.e("GeofenceHardwareRequest", String.format("Invalid Geofence type: %d", new Object[] { Integer.valueOf(i) }));
        return null;
      }
      GeofenceHardwareRequest localGeofenceHardwareRequest = GeofenceHardwareRequest.createCircularGeofence(paramAnonymousParcel.readDouble(), paramAnonymousParcel.readDouble(), paramAnonymousParcel.readDouble());
      localGeofenceHardwareRequest.setLastTransition(paramAnonymousParcel.readInt());
      localGeofenceHardwareRequest.setMonitorTransitions(paramAnonymousParcel.readInt());
      localGeofenceHardwareRequest.setUnknownTimer(paramAnonymousParcel.readInt());
      localGeofenceHardwareRequest.setNotificationResponsiveness(paramAnonymousParcel.readInt());
      localGeofenceHardwareRequest.setSourceTechnologies(paramAnonymousParcel.readInt());
      return new GeofenceHardwareRequestParcelable(paramAnonymousParcel.readInt(), localGeofenceHardwareRequest);
    }
    
    public GeofenceHardwareRequestParcelable[] newArray(int paramAnonymousInt)
    {
      return new GeofenceHardwareRequestParcelable[paramAnonymousInt];
    }
  };
  private int mId;
  private GeofenceHardwareRequest mRequest;
  
  public GeofenceHardwareRequestParcelable(int paramInt, GeofenceHardwareRequest paramGeofenceHardwareRequest)
  {
    this.mId = paramInt;
    this.mRequest = paramGeofenceHardwareRequest;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getLastTransition()
  {
    return this.mRequest.getLastTransition();
  }
  
  public double getLatitude()
  {
    return this.mRequest.getLatitude();
  }
  
  public double getLongitude()
  {
    return this.mRequest.getLongitude();
  }
  
  public int getMonitorTransitions()
  {
    return this.mRequest.getMonitorTransitions();
  }
  
  public int getNotificationResponsiveness()
  {
    return this.mRequest.getNotificationResponsiveness();
  }
  
  public double getRadius()
  {
    return this.mRequest.getRadius();
  }
  
  int getSourceTechnologies()
  {
    return this.mRequest.getSourceTechnologies();
  }
  
  int getType()
  {
    return this.mRequest.getType();
  }
  
  public int getUnknownTimer()
  {
    return this.mRequest.getUnknownTimer();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("id=");
    localStringBuilder.append(this.mId);
    localStringBuilder.append(", type=");
    localStringBuilder.append(this.mRequest.getType());
    localStringBuilder.append(", latitude=");
    localStringBuilder.append(this.mRequest.getLatitude());
    localStringBuilder.append(", longitude=");
    localStringBuilder.append(this.mRequest.getLongitude());
    localStringBuilder.append(", radius=");
    localStringBuilder.append(this.mRequest.getRadius());
    localStringBuilder.append(", lastTransition=");
    localStringBuilder.append(this.mRequest.getLastTransition());
    localStringBuilder.append(", unknownTimer=");
    localStringBuilder.append(this.mRequest.getUnknownTimer());
    localStringBuilder.append(", monitorTransitions=");
    localStringBuilder.append(this.mRequest.getMonitorTransitions());
    localStringBuilder.append(", notificationResponsiveness=");
    localStringBuilder.append(this.mRequest.getNotificationResponsiveness());
    localStringBuilder.append(", sourceTechnologies=");
    localStringBuilder.append(this.mRequest.getSourceTechnologies());
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(getType());
    paramParcel.writeDouble(getLatitude());
    paramParcel.writeDouble(getLongitude());
    paramParcel.writeDouble(getRadius());
    paramParcel.writeInt(getLastTransition());
    paramParcel.writeInt(getMonitorTransitions());
    paramParcel.writeInt(getUnknownTimer());
    paramParcel.writeInt(getNotificationResponsiveness());
    paramParcel.writeInt(getSourceTechnologies());
    paramParcel.writeInt(getId());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/GeofenceHardwareRequestParcelable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */