package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class FeatureInfo
  implements Parcelable
{
  public static final Parcelable.Creator<FeatureInfo> CREATOR = new Parcelable.Creator()
  {
    public FeatureInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new FeatureInfo(paramAnonymousParcel, null);
    }
    
    public FeatureInfo[] newArray(int paramAnonymousInt)
    {
      return new FeatureInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_REQUIRED = 1;
  public static final int GL_ES_VERSION_UNDEFINED = 0;
  public int flags;
  public String name;
  public int reqGlEsVersion;
  public int version;
  
  public FeatureInfo() {}
  
  public FeatureInfo(FeatureInfo paramFeatureInfo)
  {
    this.name = paramFeatureInfo.name;
    this.version = paramFeatureInfo.version;
    this.reqGlEsVersion = paramFeatureInfo.reqGlEsVersion;
    this.flags = paramFeatureInfo.flags;
  }
  
  private FeatureInfo(Parcel paramParcel)
  {
    this.name = paramParcel.readString();
    this.version = paramParcel.readInt();
    this.reqGlEsVersion = paramParcel.readInt();
    this.flags = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getGlEsVersion()
  {
    int i = this.reqGlEsVersion;
    int j = this.reqGlEsVersion;
    return String.valueOf((i & 0xFFFF0000) >> 16) + "." + String.valueOf(j & 0xFFFF);
  }
  
  public String toString()
  {
    if (this.name != null) {
      return "FeatureInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + " v=" + this.version + " fl=0x" + Integer.toHexString(this.flags) + "}";
    }
    return "FeatureInfo{" + Integer.toHexString(System.identityHashCode(this)) + " glEsVers=" + getGlEsVersion() + " fl=0x" + Integer.toHexString(this.flags) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.name);
    paramParcel.writeInt(this.version);
    paramParcel.writeInt(this.reqGlEsVersion);
    paramParcel.writeInt(this.flags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/FeatureInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */