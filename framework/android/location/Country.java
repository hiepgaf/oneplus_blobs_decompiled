package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import java.util.Locale;

public class Country
  implements Parcelable
{
  public static final int COUNTRY_SOURCE_LOCALE = 3;
  public static final int COUNTRY_SOURCE_LOCATION = 1;
  public static final int COUNTRY_SOURCE_NETWORK = 0;
  public static final int COUNTRY_SOURCE_SIM = 2;
  public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator()
  {
    public Country createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Country(paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readLong(), null);
    }
    
    public Country[] newArray(int paramAnonymousInt)
    {
      return new Country[paramAnonymousInt];
    }
  };
  private final String mCountryIso;
  private int mHashCode;
  private final int mSource;
  private final long mTimestamp;
  
  public Country(Country paramCountry)
  {
    this.mCountryIso = paramCountry.mCountryIso;
    this.mSource = paramCountry.mSource;
    this.mTimestamp = paramCountry.mTimestamp;
  }
  
  public Country(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramInt < 0)) {}
    while (paramInt > 3) {
      throw new IllegalArgumentException();
    }
    this.mCountryIso = paramString.toUpperCase(Locale.US);
    this.mSource = paramInt;
    this.mTimestamp = SystemClock.elapsedRealtime();
  }
  
  private Country(String paramString, int paramInt, long paramLong)
  {
    if ((paramString == null) || (paramInt < 0)) {}
    while (paramInt > 3) {
      throw new IllegalArgumentException();
    }
    this.mCountryIso = paramString.toUpperCase(Locale.US);
    this.mSource = paramInt;
    this.mTimestamp = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Country))
    {
      paramObject = (Country)paramObject;
      return (this.mCountryIso.equals(((Country)paramObject).getCountryIso())) && (this.mSource == ((Country)paramObject).getSource());
    }
    return false;
  }
  
  public boolean equalsIgnoreSource(Country paramCountry)
  {
    if (paramCountry != null) {
      return this.mCountryIso.equals(paramCountry.getCountryIso());
    }
    return false;
  }
  
  public final String getCountryIso()
  {
    return this.mCountryIso;
  }
  
  public final int getSource()
  {
    return this.mSource;
  }
  
  public final long getTimestamp()
  {
    return this.mTimestamp;
  }
  
  public int hashCode()
  {
    if (this.mHashCode == 0) {
      this.mHashCode = ((this.mCountryIso.hashCode() + 221) * 13 + this.mSource);
    }
    return this.mHashCode;
  }
  
  public String toString()
  {
    return "Country {ISO=" + this.mCountryIso + ", source=" + this.mSource + ", time=" + this.mTimestamp + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mCountryIso);
    paramParcel.writeInt(this.mSource);
    paramParcel.writeLong(this.mTimestamp);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Country.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */