package android.location;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class Address
  implements Parcelable
{
  public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator()
  {
    public Address createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = false;
      Object localObject = paramAnonymousParcel.readString();
      String str = paramAnonymousParcel.readString();
      if (str.length() > 0) {}
      for (localObject = new Locale((String)localObject, str);; localObject = new Locale((String)localObject))
      {
        localObject = new Address((Locale)localObject);
        int j = paramAnonymousParcel.readInt();
        if (j <= 0) {
          break;
        }
        Address.-set0((Address)localObject, new HashMap(j));
        int i = 0;
        while (i < j)
        {
          int k = paramAnonymousParcel.readInt();
          str = paramAnonymousParcel.readString();
          Address.-get0((Address)localObject).put(Integer.valueOf(k), str);
          Address.-set11((Address)localObject, Math.max(Address.-get3((Address)localObject), k));
          i += 1;
        }
      }
      Address.-set0((Address)localObject, null);
      Address.-set11((Address)localObject, -1);
      Address.-set5((Address)localObject, paramAnonymousParcel.readString());
      Address.-set1((Address)localObject, paramAnonymousParcel.readString());
      Address.-set15((Address)localObject, paramAnonymousParcel.readString());
      Address.-set9((Address)localObject, paramAnonymousParcel.readString());
      Address.-set16((Address)localObject, paramAnonymousParcel.readString());
      Address.-set18((Address)localObject, paramAnonymousParcel.readString());
      Address.-set17((Address)localObject, paramAnonymousParcel.readString());
      Address.-set14((Address)localObject, paramAnonymousParcel.readString());
      Address.-set13((Address)localObject, paramAnonymousParcel.readString());
      Address.-set2((Address)localObject, paramAnonymousParcel.readString());
      Address.-set3((Address)localObject, paramAnonymousParcel.readString());
      if (paramAnonymousParcel.readInt() == 0)
      {
        bool1 = false;
        Address.-set6((Address)localObject, bool1);
        if (Address.-get1((Address)localObject)) {
          Address.-set8((Address)localObject, paramAnonymousParcel.readDouble());
        }
        if (paramAnonymousParcel.readInt() != 0) {
          break label378;
        }
      }
      label378:
      for (boolean bool1 = bool2;; bool1 = true)
      {
        Address.-set7((Address)localObject, bool1);
        if (Address.-get2((Address)localObject)) {
          Address.-set10((Address)localObject, paramAnonymousParcel.readDouble());
        }
        Address.-set12((Address)localObject, paramAnonymousParcel.readString());
        Address.-set19((Address)localObject, paramAnonymousParcel.readString());
        Address.-set4((Address)localObject, paramAnonymousParcel.readBundle());
        return (Address)localObject;
        bool1 = true;
        break;
      }
    }
    
    public Address[] newArray(int paramAnonymousInt)
    {
      return new Address[paramAnonymousInt];
    }
  };
  private HashMap<Integer, String> mAddressLines;
  private String mAdminArea;
  private String mCountryCode;
  private String mCountryName;
  private Bundle mExtras = null;
  private String mFeatureName;
  private boolean mHasLatitude = false;
  private boolean mHasLongitude = false;
  private double mLatitude;
  private Locale mLocale;
  private String mLocality;
  private double mLongitude;
  private int mMaxAddressLineIndex = -1;
  private String mPhone;
  private String mPostalCode;
  private String mPremises;
  private String mSubAdminArea;
  private String mSubLocality;
  private String mSubThoroughfare;
  private String mThoroughfare;
  private String mUrl;
  
  public Address(Locale paramLocale)
  {
    this.mLocale = paramLocale;
  }
  
  public void clearLatitude()
  {
    this.mHasLatitude = false;
  }
  
  public void clearLongitude()
  {
    this.mHasLongitude = false;
  }
  
  public int describeContents()
  {
    if (this.mExtras != null) {
      return this.mExtras.describeContents();
    }
    return 0;
  }
  
  public String getAddressLine(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index = " + paramInt + " < 0");
    }
    if (this.mAddressLines == null) {
      return null;
    }
    return (String)this.mAddressLines.get(Integer.valueOf(paramInt));
  }
  
  public String getAdminArea()
  {
    return this.mAdminArea;
  }
  
  public String getCountryCode()
  {
    return this.mCountryCode;
  }
  
  public String getCountryName()
  {
    return this.mCountryName;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public String getFeatureName()
  {
    return this.mFeatureName;
  }
  
  public double getLatitude()
  {
    if (this.mHasLatitude) {
      return this.mLatitude;
    }
    throw new IllegalStateException();
  }
  
  public Locale getLocale()
  {
    return this.mLocale;
  }
  
  public String getLocality()
  {
    return this.mLocality;
  }
  
  public double getLongitude()
  {
    if (this.mHasLongitude) {
      return this.mLongitude;
    }
    throw new IllegalStateException();
  }
  
  public int getMaxAddressLineIndex()
  {
    return this.mMaxAddressLineIndex;
  }
  
  public String getPhone()
  {
    return this.mPhone;
  }
  
  public String getPostalCode()
  {
    return this.mPostalCode;
  }
  
  public String getPremises()
  {
    return this.mPremises;
  }
  
  public String getSubAdminArea()
  {
    return this.mSubAdminArea;
  }
  
  public String getSubLocality()
  {
    return this.mSubLocality;
  }
  
  public String getSubThoroughfare()
  {
    return this.mSubThoroughfare;
  }
  
  public String getThoroughfare()
  {
    return this.mThoroughfare;
  }
  
  public String getUrl()
  {
    return this.mUrl;
  }
  
  public boolean hasLatitude()
  {
    return this.mHasLatitude;
  }
  
  public boolean hasLongitude()
  {
    return this.mHasLongitude;
  }
  
  public void setAddressLine(int paramInt, String paramString)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index = " + paramInt + " < 0");
    }
    if (this.mAddressLines == null) {
      this.mAddressLines = new HashMap();
    }
    this.mAddressLines.put(Integer.valueOf(paramInt), paramString);
    if (paramString == null)
    {
      this.mMaxAddressLineIndex = -1;
      paramString = this.mAddressLines.keySet().iterator();
      while (paramString.hasNext())
      {
        Integer localInteger = (Integer)paramString.next();
        this.mMaxAddressLineIndex = Math.max(this.mMaxAddressLineIndex, localInteger.intValue());
      }
    }
    this.mMaxAddressLineIndex = Math.max(this.mMaxAddressLineIndex, paramInt);
  }
  
  public void setAdminArea(String paramString)
  {
    this.mAdminArea = paramString;
  }
  
  public void setCountryCode(String paramString)
  {
    this.mCountryCode = paramString;
  }
  
  public void setCountryName(String paramString)
  {
    this.mCountryName = paramString;
  }
  
  public void setExtras(Bundle paramBundle)
  {
    Object localObject = null;
    if (paramBundle == null) {}
    for (paramBundle = (Bundle)localObject;; paramBundle = new Bundle(paramBundle))
    {
      this.mExtras = paramBundle;
      return;
    }
  }
  
  public void setFeatureName(String paramString)
  {
    this.mFeatureName = paramString;
  }
  
  public void setLatitude(double paramDouble)
  {
    this.mLatitude = paramDouble;
    this.mHasLatitude = true;
  }
  
  public void setLocality(String paramString)
  {
    this.mLocality = paramString;
  }
  
  public void setLongitude(double paramDouble)
  {
    this.mLongitude = paramDouble;
    this.mHasLongitude = true;
  }
  
  public void setPhone(String paramString)
  {
    this.mPhone = paramString;
  }
  
  public void setPostalCode(String paramString)
  {
    this.mPostalCode = paramString;
  }
  
  public void setPremises(String paramString)
  {
    this.mPremises = paramString;
  }
  
  public void setSubAdminArea(String paramString)
  {
    this.mSubAdminArea = paramString;
  }
  
  public void setSubLocality(String paramString)
  {
    this.mSubLocality = paramString;
  }
  
  public void setSubThoroughfare(String paramString)
  {
    this.mSubThoroughfare = paramString;
  }
  
  public void setThoroughfare(String paramString)
  {
    this.mThoroughfare = paramString;
  }
  
  public void setUrl(String paramString)
  {
    this.mUrl = paramString;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Address[addressLines=[");
    int i = 0;
    if (i <= this.mMaxAddressLineIndex)
    {
      if (i > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append(i);
      localStringBuilder.append(':');
      String str = (String)this.mAddressLines.get(Integer.valueOf(i));
      if (str == null) {
        localStringBuilder.append("null");
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append('"');
        localStringBuilder.append(str);
        localStringBuilder.append('"');
      }
    }
    localStringBuilder.append(']');
    localStringBuilder.append(",feature=");
    localStringBuilder.append(this.mFeatureName);
    localStringBuilder.append(",admin=");
    localStringBuilder.append(this.mAdminArea);
    localStringBuilder.append(",sub-admin=");
    localStringBuilder.append(this.mSubAdminArea);
    localStringBuilder.append(",locality=");
    localStringBuilder.append(this.mLocality);
    localStringBuilder.append(",thoroughfare=");
    localStringBuilder.append(this.mThoroughfare);
    localStringBuilder.append(",postalCode=");
    localStringBuilder.append(this.mPostalCode);
    localStringBuilder.append(",countryCode=");
    localStringBuilder.append(this.mCountryCode);
    localStringBuilder.append(",countryName=");
    localStringBuilder.append(this.mCountryName);
    localStringBuilder.append(",hasLatitude=");
    localStringBuilder.append(this.mHasLatitude);
    localStringBuilder.append(",latitude=");
    localStringBuilder.append(this.mLatitude);
    localStringBuilder.append(",hasLongitude=");
    localStringBuilder.append(this.mHasLongitude);
    localStringBuilder.append(",longitude=");
    localStringBuilder.append(this.mLongitude);
    localStringBuilder.append(",phone=");
    localStringBuilder.append(this.mPhone);
    localStringBuilder.append(",url=");
    localStringBuilder.append(this.mUrl);
    localStringBuilder.append(",extras=");
    localStringBuilder.append(this.mExtras);
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.mLocale.getLanguage());
    paramParcel.writeString(this.mLocale.getCountry());
    if (this.mAddressLines == null)
    {
      paramParcel.writeInt(0);
      paramParcel.writeString(this.mFeatureName);
      paramParcel.writeString(this.mAdminArea);
      paramParcel.writeString(this.mSubAdminArea);
      paramParcel.writeString(this.mLocality);
      paramParcel.writeString(this.mSubLocality);
      paramParcel.writeString(this.mThoroughfare);
      paramParcel.writeString(this.mSubThoroughfare);
      paramParcel.writeString(this.mPremises);
      paramParcel.writeString(this.mPostalCode);
      paramParcel.writeString(this.mCountryCode);
      paramParcel.writeString(this.mCountryName);
      if (!this.mHasLatitude) {
        break label292;
      }
      paramInt = 1;
      label133:
      paramParcel.writeInt(paramInt);
      if (this.mHasLatitude) {
        paramParcel.writeDouble(this.mLatitude);
      }
      if (!this.mHasLongitude) {
        break label297;
      }
    }
    label292:
    label297:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.mHasLongitude) {
        paramParcel.writeDouble(this.mLongitude);
      }
      paramParcel.writeString(this.mPhone);
      paramParcel.writeString(this.mUrl);
      paramParcel.writeBundle(this.mExtras);
      return;
      Object localObject = this.mAddressLines.entrySet();
      paramParcel.writeInt(((Set)localObject).size());
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        paramParcel.writeInt(((Integer)localEntry.getKey()).intValue());
        paramParcel.writeString((String)localEntry.getValue());
      }
      break;
      paramInt = 0;
      break label133;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/Address.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */