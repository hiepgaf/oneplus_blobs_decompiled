package android.security.keymaster;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

abstract class KeymasterArgument
  implements Parcelable
{
  public static final Parcelable.Creator<KeymasterArgument> CREATOR = new Parcelable.Creator()
  {
    public KeymasterArgument createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.dataPosition();
      int j = paramAnonymousParcel.readInt();
      switch (KeymasterDefs.getTagType(j))
      {
      default: 
        throw new ParcelFormatException("Bad tag: " + j + " at " + i);
      case 268435456: 
      case 536870912: 
      case 805306368: 
      case 1073741824: 
        return new KeymasterIntArgument(j, paramAnonymousParcel);
      case 1342177280: 
      case -1610612736: 
        return new KeymasterLongArgument(j, paramAnonymousParcel);
      case 1610612736: 
        return new KeymasterDateArgument(j, paramAnonymousParcel);
      case -2147483648: 
      case -1879048192: 
        return new KeymasterBlobArgument(j, paramAnonymousParcel);
      }
      return new KeymasterBooleanArgument(j, paramAnonymousParcel);
    }
    
    public KeymasterArgument[] newArray(int paramAnonymousInt)
    {
      return new KeymasterArgument[paramAnonymousInt];
    }
  };
  public final int tag;
  
  protected KeymasterArgument(int paramInt)
  {
    this.tag = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.tag);
    writeValue(paramParcel);
  }
  
  public abstract void writeValue(Parcel paramParcel);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */