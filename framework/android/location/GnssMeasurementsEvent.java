package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public final class GnssMeasurementsEvent
  implements Parcelable
{
  public static final Parcelable.Creator<GnssMeasurementsEvent> CREATOR = new Parcelable.Creator()
  {
    public GnssMeasurementsEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      GnssClock localGnssClock = (GnssClock)paramAnonymousParcel.readParcelable(getClass().getClassLoader());
      GnssMeasurement[] arrayOfGnssMeasurement = new GnssMeasurement[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readTypedArray(arrayOfGnssMeasurement, GnssMeasurement.CREATOR);
      return new GnssMeasurementsEvent(localGnssClock, arrayOfGnssMeasurement);
    }
    
    public GnssMeasurementsEvent[] newArray(int paramAnonymousInt)
    {
      return new GnssMeasurementsEvent[paramAnonymousInt];
    }
  };
  public static final int STATUS_GNSS_LOCATION_DISABLED = 2;
  public static final int STATUS_NOT_SUPPORTED = 0;
  public static final int STATUS_READY = 1;
  private final GnssClock mClock;
  private final Collection<GnssMeasurement> mReadOnlyMeasurements;
  
  public GnssMeasurementsEvent(GnssClock paramGnssClock, GnssMeasurement[] paramArrayOfGnssMeasurement)
  {
    if (paramGnssClock == null) {
      throw new InvalidParameterException("Parameter 'clock' must not be null.");
    }
    if ((paramArrayOfGnssMeasurement == null) || (paramArrayOfGnssMeasurement.length == 0)) {}
    for (this.mReadOnlyMeasurements = Collections.emptyList();; this.mReadOnlyMeasurements = Collections.unmodifiableCollection(Arrays.asList(paramArrayOfGnssMeasurement)))
    {
      this.mClock = paramGnssClock;
      return;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public GnssClock getClock()
  {
    return this.mClock;
  }
  
  public Collection<GnssMeasurement> getMeasurements()
  {
    return this.mReadOnlyMeasurements;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("[ GnssMeasurementsEvent:\n\n");
    localStringBuilder.append(this.mClock.toString());
    localStringBuilder.append("\n");
    Iterator localIterator = this.mReadOnlyMeasurements.iterator();
    while (localIterator.hasNext())
    {
      localStringBuilder.append(((GnssMeasurement)localIterator.next()).toString());
      localStringBuilder.append("\n");
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mClock, paramInt);
    int i = this.mReadOnlyMeasurements.size();
    GnssMeasurement[] arrayOfGnssMeasurement = (GnssMeasurement[])this.mReadOnlyMeasurements.toArray(new GnssMeasurement[i]);
    paramParcel.writeInt(arrayOfGnssMeasurement.length);
    paramParcel.writeTypedArray(arrayOfGnssMeasurement, paramInt);
  }
  
  public static abstract class Callback
  {
    public static final int STATUS_LOCATION_DISABLED = 2;
    public static final int STATUS_NOT_SUPPORTED = 0;
    public static final int STATUS_READY = 1;
    
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent paramGnssMeasurementsEvent) {}
    
    public void onStatusChanged(int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssMeasurementsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */