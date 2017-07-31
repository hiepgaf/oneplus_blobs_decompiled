package android.location;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Locale;

public class GeocoderParams
  implements Parcelable
{
  public static final Parcelable.Creator<GeocoderParams> CREATOR = new Parcelable.Creator()
  {
    public GeocoderParams createFromParcel(Parcel paramAnonymousParcel)
    {
      GeocoderParams localGeocoderParams = new GeocoderParams(null);
      GeocoderParams.-set0(localGeocoderParams, new Locale(paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString()));
      GeocoderParams.-set1(localGeocoderParams, paramAnonymousParcel.readString());
      return localGeocoderParams;
    }
    
    public GeocoderParams[] newArray(int paramAnonymousInt)
    {
      return new GeocoderParams[paramAnonymousInt];
    }
  };
  private Locale mLocale;
  private String mPackageName;
  
  private GeocoderParams() {}
  
  public GeocoderParams(Context paramContext, Locale paramLocale)
  {
    this.mLocale = paramLocale;
    this.mPackageName = paramContext.getPackageName();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getClientPackage()
  {
    return this.mPackageName;
  }
  
  public Locale getLocale()
  {
    return this.mLocale;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mLocale.getLanguage());
    paramParcel.writeString(this.mLocale.getCountry());
    paramParcel.writeString(this.mLocale.getVariant());
    paramParcel.writeString(this.mPackageName);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GeocoderParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */