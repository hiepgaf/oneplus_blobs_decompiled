package android.speech.tts;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Voice
  implements Parcelable
{
  public static final Parcelable.Creator<Voice> CREATOR = new Parcelable.Creator()
  {
    public Voice createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Voice(paramAnonymousParcel, null);
    }
    
    public Voice[] newArray(int paramAnonymousInt)
    {
      return new Voice[paramAnonymousInt];
    }
  };
  public static final int LATENCY_HIGH = 400;
  public static final int LATENCY_LOW = 200;
  public static final int LATENCY_NORMAL = 300;
  public static final int LATENCY_VERY_HIGH = 500;
  public static final int LATENCY_VERY_LOW = 100;
  public static final int QUALITY_HIGH = 400;
  public static final int QUALITY_LOW = 200;
  public static final int QUALITY_NORMAL = 300;
  public static final int QUALITY_VERY_HIGH = 500;
  public static final int QUALITY_VERY_LOW = 100;
  private final Set<String> mFeatures;
  private final int mLatency;
  private final Locale mLocale;
  private final String mName;
  private final int mQuality;
  private final boolean mRequiresNetworkConnection;
  
  private Voice(Parcel paramParcel)
  {
    this.mName = paramParcel.readString();
    this.mLocale = ((Locale)paramParcel.readSerializable());
    this.mQuality = paramParcel.readInt();
    this.mLatency = paramParcel.readInt();
    if (paramParcel.readByte() == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mRequiresNetworkConnection = bool;
      this.mFeatures = new HashSet();
      Collections.addAll(this.mFeatures, paramParcel.readStringArray());
      return;
    }
  }
  
  public Voice(String paramString, Locale paramLocale, int paramInt1, int paramInt2, boolean paramBoolean, Set<String> paramSet)
  {
    this.mName = paramString;
    this.mLocale = paramLocale;
    this.mQuality = paramInt1;
    this.mLatency = paramInt2;
    this.mRequiresNetworkConnection = paramBoolean;
    this.mFeatures = paramSet;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (Voice)paramObject;
    if (this.mFeatures == null)
    {
      if (((Voice)paramObject).mFeatures != null) {
        return false;
      }
    }
    else if (!this.mFeatures.equals(((Voice)paramObject).mFeatures)) {
      return false;
    }
    if (this.mLatency != ((Voice)paramObject).mLatency) {
      return false;
    }
    if (this.mLocale == null)
    {
      if (((Voice)paramObject).mLocale != null) {
        return false;
      }
    }
    else if (!this.mLocale.equals(((Voice)paramObject).mLocale)) {
      return false;
    }
    if (this.mName == null)
    {
      if (((Voice)paramObject).mName != null) {
        return false;
      }
    }
    else if (!this.mName.equals(((Voice)paramObject).mName)) {
      return false;
    }
    if (this.mQuality != ((Voice)paramObject).mQuality) {
      return false;
    }
    return this.mRequiresNetworkConnection == ((Voice)paramObject).mRequiresNetworkConnection;
  }
  
  public Set<String> getFeatures()
  {
    return this.mFeatures;
  }
  
  public int getLatency()
  {
    return this.mLatency;
  }
  
  public Locale getLocale()
  {
    return this.mLocale;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getQuality()
  {
    return this.mQuality;
  }
  
  public int hashCode()
  {
    int k = 0;
    int i;
    int n;
    int j;
    label26:
    label33:
    int i1;
    if (this.mFeatures == null)
    {
      i = 0;
      n = this.mLatency;
      if (this.mLocale != null) {
        break label97;
      }
      j = 0;
      if (this.mName != null) {
        break label108;
      }
      i1 = this.mQuality;
      if (!this.mRequiresNetworkConnection) {
        break label119;
      }
    }
    label97:
    label108:
    label119:
    for (int m = 1231;; m = 1237)
    {
      return (((((i + 31) * 31 + n) * 31 + j) * 31 + k) * 31 + i1) * 31 + m;
      i = this.mFeatures.hashCode();
      break;
      j = this.mLocale.hashCode();
      break label26;
      k = this.mName.hashCode();
      break label33;
    }
  }
  
  public boolean isNetworkConnectionRequired()
  {
    return this.mRequiresNetworkConnection;
  }
  
  public String toString()
  {
    return 64 + "Voice[Name: " + this.mName + ", locale: " + this.mLocale + ", quality: " + this.mQuality + ", latency: " + this.mLatency + ", requiresNetwork: " + this.mRequiresNetworkConnection + ", features: " + this.mFeatures.toString() + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeSerializable(this.mLocale);
    paramParcel.writeInt(this.mQuality);
    paramParcel.writeInt(this.mLatency);
    if (this.mRequiresNetworkConnection) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeStringList(new ArrayList(this.mFeatures));
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/Voice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */