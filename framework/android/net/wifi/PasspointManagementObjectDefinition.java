package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PasspointManagementObjectDefinition
  implements Parcelable
{
  public static final Parcelable.Creator<PasspointManagementObjectDefinition> CREATOR = new Parcelable.Creator()
  {
    public PasspointManagementObjectDefinition createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PasspointManagementObjectDefinition(paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString());
    }
    
    public PasspointManagementObjectDefinition[] newArray(int paramAnonymousInt)
    {
      return new PasspointManagementObjectDefinition[paramAnonymousInt];
    }
  };
  private final String mBaseUri;
  private final String mMoTree;
  private final String mUrn;
  
  public PasspointManagementObjectDefinition(String paramString1, String paramString2, String paramString3)
  {
    this.mBaseUri = paramString1;
    this.mUrn = paramString2;
    this.mMoTree = paramString3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getBaseUri()
  {
    return this.mBaseUri;
  }
  
  public String getMoTree()
  {
    return this.mMoTree;
  }
  
  public String getUrn()
  {
    return this.mUrn;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mBaseUri);
    paramParcel.writeString(this.mUrn);
    paramParcel.writeString(this.mMoTree);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/PasspointManagementObjectDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */