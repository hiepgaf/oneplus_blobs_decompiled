package android.telecom;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class GatewayInfo
  implements Parcelable
{
  public static final Parcelable.Creator<GatewayInfo> CREATOR = new Parcelable.Creator()
  {
    public GatewayInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GatewayInfo(paramAnonymousParcel.readString(), (Uri)Uri.CREATOR.createFromParcel(paramAnonymousParcel), (Uri)Uri.CREATOR.createFromParcel(paramAnonymousParcel));
    }
    
    public GatewayInfo[] newArray(int paramAnonymousInt)
    {
      return new GatewayInfo[paramAnonymousInt];
    }
  };
  private final Uri mGatewayAddress;
  private final String mGatewayProviderPackageName;
  private final Uri mOriginalAddress;
  
  public GatewayInfo(String paramString, Uri paramUri1, Uri paramUri2)
  {
    this.mGatewayProviderPackageName = paramString;
    this.mGatewayAddress = paramUri1;
    this.mOriginalAddress = paramUri2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Uri getGatewayAddress()
  {
    return this.mGatewayAddress;
  }
  
  public String getGatewayProviderPackageName()
  {
    return this.mGatewayProviderPackageName;
  }
  
  public Uri getOriginalAddress()
  {
    return this.mOriginalAddress;
  }
  
  public boolean isEmpty()
  {
    return (TextUtils.isEmpty(this.mGatewayProviderPackageName)) || (this.mGatewayAddress == null);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mGatewayProviderPackageName);
    this.mGatewayAddress.writeToParcel(paramParcel, 0);
    this.mOriginalAddress.writeToParcel(paramParcel, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/GatewayInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */