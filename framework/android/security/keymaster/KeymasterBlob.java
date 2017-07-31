package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class KeymasterBlob
  implements Parcelable
{
  public static final Parcelable.Creator<KeymasterBlob> CREATOR = new Parcelable.Creator()
  {
    public KeymasterBlob createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeymasterBlob(paramAnonymousParcel);
    }
    
    public KeymasterBlob[] newArray(int paramAnonymousInt)
    {
      return new KeymasterBlob[paramAnonymousInt];
    }
  };
  public byte[] blob;
  
  protected KeymasterBlob(Parcel paramParcel)
  {
    this.blob = paramParcel.createByteArray();
  }
  
  public KeymasterBlob(byte[] paramArrayOfByte)
  {
    this.blob = paramArrayOfByte;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.blob);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterBlob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */