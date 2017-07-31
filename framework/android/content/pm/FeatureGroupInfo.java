package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeatureGroupInfo
  implements Parcelable
{
  public static final Parcelable.Creator<FeatureGroupInfo> CREATOR = new Parcelable.Creator()
  {
    public FeatureGroupInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      FeatureGroupInfo localFeatureGroupInfo = new FeatureGroupInfo();
      localFeatureGroupInfo.features = ((FeatureInfo[])paramAnonymousParcel.createTypedArray(FeatureInfo.CREATOR));
      return localFeatureGroupInfo;
    }
    
    public FeatureGroupInfo[] newArray(int paramAnonymousInt)
    {
      return new FeatureGroupInfo[paramAnonymousInt];
    }
  };
  public FeatureInfo[] features;
  
  public FeatureGroupInfo() {}
  
  public FeatureGroupInfo(FeatureGroupInfo paramFeatureGroupInfo)
  {
    this.features = paramFeatureGroupInfo.features;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedArray(this.features, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/FeatureGroupInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */