package android.security;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class KeystoreArguments
  implements Parcelable
{
  public static final Parcelable.Creator<KeystoreArguments> CREATOR = new Parcelable.Creator()
  {
    public KeystoreArguments createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeystoreArguments(paramAnonymousParcel, null);
    }
    
    public KeystoreArguments[] newArray(int paramAnonymousInt)
    {
      return new KeystoreArguments[paramAnonymousInt];
    }
  };
  public byte[][] args;
  
  public KeystoreArguments()
  {
    this.args = null;
  }
  
  private KeystoreArguments(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public KeystoreArguments(byte[][] paramArrayOfByte)
  {
    this.args = paramArrayOfByte;
  }
  
  private void readFromParcel(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    this.args = new byte[j][];
    int i = 0;
    while (i < j)
    {
      this.args[i] = paramParcel.createByteArray();
      i += 1;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = 0;
    if (this.args == null) {
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      return;
      paramParcel.writeInt(this.args.length);
      byte[][] arrayOfByte = this.args;
      int i = arrayOfByte.length;
      while (paramInt < i)
      {
        paramParcel.writeByteArray(arrayOfByte[paramInt]);
        paramInt += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/KeystoreArguments.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */