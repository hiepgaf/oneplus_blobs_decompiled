package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.InvalidParameterException;

public final class GnssNavigationMessageEvent
  implements Parcelable
{
  public static final Parcelable.Creator<GnssNavigationMessageEvent> CREATOR = new Parcelable.Creator()
  {
    public GnssNavigationMessageEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GnssNavigationMessageEvent((GnssNavigationMessage)paramAnonymousParcel.readParcelable(getClass().getClassLoader()));
    }
    
    public GnssNavigationMessageEvent[] newArray(int paramAnonymousInt)
    {
      return new GnssNavigationMessageEvent[paramAnonymousInt];
    }
  };
  public static final int STATUS_GNSS_LOCATION_DISABLED = 2;
  public static final int STATUS_NOT_SUPPORTED = 0;
  public static final int STATUS_READY = 1;
  private final GnssNavigationMessage mNavigationMessage;
  
  public GnssNavigationMessageEvent(GnssNavigationMessage paramGnssNavigationMessage)
  {
    if (paramGnssNavigationMessage == null) {
      throw new InvalidParameterException("Parameter 'message' must not be null.");
    }
    this.mNavigationMessage = paramGnssNavigationMessage;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public GnssNavigationMessage getNavigationMessage()
  {
    return this.mNavigationMessage;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("[ GnssNavigationMessageEvent:\n\n");
    localStringBuilder.append(this.mNavigationMessage.toString());
    localStringBuilder.append("\n]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mNavigationMessage, paramInt);
  }
  
  public static abstract class Callback
  {
    public void onGnssNavigationMessageReceived(GnssNavigationMessageEvent paramGnssNavigationMessageEvent) {}
    
    public void onStatusChanged(int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssNavigationMessageEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */