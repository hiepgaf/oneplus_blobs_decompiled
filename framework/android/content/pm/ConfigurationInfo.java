package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ConfigurationInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ConfigurationInfo> CREATOR = new Parcelable.Creator()
  {
    public ConfigurationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConfigurationInfo(paramAnonymousParcel, null);
    }
    
    public ConfigurationInfo[] newArray(int paramAnonymousInt)
    {
      return new ConfigurationInfo[paramAnonymousInt];
    }
  };
  public static final int GL_ES_VERSION_UNDEFINED = 0;
  public static final int INPUT_FEATURE_FIVE_WAY_NAV = 2;
  public static final int INPUT_FEATURE_HARD_KEYBOARD = 1;
  public int reqGlEsVersion;
  public int reqInputFeatures = 0;
  public int reqKeyboardType;
  public int reqNavigation;
  public int reqTouchScreen;
  
  public ConfigurationInfo() {}
  
  public ConfigurationInfo(ConfigurationInfo paramConfigurationInfo)
  {
    this.reqTouchScreen = paramConfigurationInfo.reqTouchScreen;
    this.reqKeyboardType = paramConfigurationInfo.reqKeyboardType;
    this.reqNavigation = paramConfigurationInfo.reqNavigation;
    this.reqInputFeatures = paramConfigurationInfo.reqInputFeatures;
    this.reqGlEsVersion = paramConfigurationInfo.reqGlEsVersion;
  }
  
  private ConfigurationInfo(Parcel paramParcel)
  {
    this.reqTouchScreen = paramParcel.readInt();
    this.reqKeyboardType = paramParcel.readInt();
    this.reqNavigation = paramParcel.readInt();
    this.reqInputFeatures = paramParcel.readInt();
    this.reqGlEsVersion = paramParcel.readInt();
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
    return "ConfigurationInfo{" + Integer.toHexString(System.identityHashCode(this)) + " touchscreen = " + this.reqTouchScreen + " inputMethod = " + this.reqKeyboardType + " navigation = " + this.reqNavigation + " reqInputFeatures = " + this.reqInputFeatures + " reqGlEsVersion = " + this.reqGlEsVersion + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.reqTouchScreen);
    paramParcel.writeInt(this.reqKeyboardType);
    paramParcel.writeInt(this.reqNavigation);
    paramParcel.writeInt(this.reqInputFeatures);
    paramParcel.writeInt(this.reqGlEsVersion);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/ConfigurationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */