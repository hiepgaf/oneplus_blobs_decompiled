package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.PublicKey;

public class VerifierInfo
  implements Parcelable
{
  public static final Parcelable.Creator<VerifierInfo> CREATOR = new Parcelable.Creator()
  {
    public VerifierInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new VerifierInfo(paramAnonymousParcel, null);
    }
    
    public VerifierInfo[] newArray(int paramAnonymousInt)
    {
      return new VerifierInfo[paramAnonymousInt];
    }
  };
  public final String packageName;
  public final PublicKey publicKey;
  
  private VerifierInfo(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.publicKey = ((PublicKey)paramParcel.readSerializable());
  }
  
  public VerifierInfo(String paramString, PublicKey paramPublicKey)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("packageName must not be null or empty");
    }
    if (paramPublicKey == null) {
      throw new IllegalArgumentException("publicKey must not be null");
    }
    this.packageName = paramString;
    this.publicKey = paramPublicKey;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.packageName);
    paramParcel.writeSerializable(this.publicKey);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/VerifierInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */