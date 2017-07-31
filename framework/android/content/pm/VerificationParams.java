package android.content.pm;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

@Deprecated
public class VerificationParams
  implements Parcelable
{
  public static final Parcelable.Creator<VerificationParams> CREATOR = new Parcelable.Creator()
  {
    public VerificationParams createFromParcel(Parcel paramAnonymousParcel)
    {
      return new VerificationParams(paramAnonymousParcel, null);
    }
    
    public VerificationParams[] newArray(int paramAnonymousInt)
    {
      return new VerificationParams[paramAnonymousInt];
    }
  };
  public static final int NO_UID = -1;
  private static final String TO_STRING_PREFIX = "VerificationParams{";
  private int mInstallerUid;
  private final Uri mOriginatingURI;
  private final int mOriginatingUid;
  private final Uri mReferrer;
  private final Uri mVerificationURI;
  
  public VerificationParams(Uri paramUri1, Uri paramUri2, Uri paramUri3, int paramInt)
  {
    this.mVerificationURI = paramUri1;
    this.mOriginatingURI = paramUri2;
    this.mReferrer = paramUri3;
    this.mOriginatingUid = paramInt;
    this.mInstallerUid = -1;
  }
  
  private VerificationParams(Parcel paramParcel)
  {
    this.mVerificationURI = ((Uri)paramParcel.readParcelable(Uri.class.getClassLoader()));
    this.mOriginatingURI = ((Uri)paramParcel.readParcelable(Uri.class.getClassLoader()));
    this.mReferrer = ((Uri)paramParcel.readParcelable(Uri.class.getClassLoader()));
    this.mOriginatingUid = paramParcel.readInt();
    this.mInstallerUid = paramParcel.readInt();
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
    if (!(paramObject instanceof VerificationParams)) {
      return false;
    }
    if (this.mVerificationURI == null)
    {
      if (((VerificationParams)paramObject).mVerificationURI != null) {
        return false;
      }
    }
    else if (!this.mVerificationURI.equals(((VerificationParams)paramObject).mVerificationURI)) {
      return false;
    }
    if (this.mOriginatingURI == null)
    {
      if (((VerificationParams)paramObject).mOriginatingURI != null) {
        return false;
      }
    }
    else if (!this.mOriginatingURI.equals(((VerificationParams)paramObject).mOriginatingURI)) {
      return false;
    }
    if (this.mReferrer == null)
    {
      if (((VerificationParams)paramObject).mReferrer != null) {
        return false;
      }
    }
    else if (!this.mReferrer.equals(((VerificationParams)paramObject).mReferrer)) {
      return false;
    }
    if (this.mOriginatingUid != ((VerificationParams)paramObject).mOriginatingUid) {
      return false;
    }
    return this.mInstallerUid == ((VerificationParams)paramObject).mInstallerUid;
  }
  
  public int getInstallerUid()
  {
    return this.mInstallerUid;
  }
  
  public Uri getOriginatingURI()
  {
    return this.mOriginatingURI;
  }
  
  public int getOriginatingUid()
  {
    return this.mOriginatingUid;
  }
  
  public Uri getReferrer()
  {
    return this.mReferrer;
  }
  
  public Uri getVerificationURI()
  {
    return this.mVerificationURI;
  }
  
  public int hashCode()
  {
    int k = 1;
    int i;
    int j;
    if (this.mVerificationURI == null)
    {
      i = 1;
      if (this.mOriginatingURI != null) {
        break label70;
      }
      j = 1;
      label20:
      if (this.mReferrer != null) {
        break label81;
      }
    }
    for (;;)
    {
      return i * 5 + 3 + j * 7 + k * 11 + this.mOriginatingUid * 13 + this.mInstallerUid * 17;
      i = this.mVerificationURI.hashCode();
      break;
      label70:
      j = this.mOriginatingURI.hashCode();
      break label20;
      label81:
      k = this.mReferrer.hashCode();
    }
  }
  
  public void setInstallerUid(int paramInt)
  {
    this.mInstallerUid = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("VerificationParams{");
    localStringBuilder.append("mVerificationURI=");
    localStringBuilder.append(this.mVerificationURI.toString());
    localStringBuilder.append(",mOriginatingURI=");
    localStringBuilder.append(this.mOriginatingURI.toString());
    localStringBuilder.append(",mReferrer=");
    localStringBuilder.append(this.mReferrer.toString());
    localStringBuilder.append(",mOriginatingUid=");
    localStringBuilder.append(this.mOriginatingUid);
    localStringBuilder.append(",mInstallerUid=");
    localStringBuilder.append(this.mInstallerUid);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mVerificationURI, 0);
    paramParcel.writeParcelable(this.mOriginatingURI, 0);
    paramParcel.writeParcelable(this.mReferrer, 0);
    paramParcel.writeInt(this.mOriginatingUid);
    paramParcel.writeInt(this.mInstallerUid);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/VerificationParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */