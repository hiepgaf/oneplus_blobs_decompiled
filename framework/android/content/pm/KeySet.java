package android.content.pm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class KeySet
  implements Parcelable
{
  public static final Parcelable.Creator<KeySet> CREATOR = new Parcelable.Creator()
  {
    public KeySet createFromParcel(Parcel paramAnonymousParcel)
    {
      return KeySet.-wrap0(paramAnonymousParcel);
    }
    
    public KeySet[] newArray(int paramAnonymousInt)
    {
      return new KeySet[paramAnonymousInt];
    }
  };
  private IBinder token;
  
  public KeySet(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      throw new NullPointerException("null value for KeySet IBinder token");
    }
    this.token = paramIBinder;
  }
  
  private static KeySet readFromParcel(Parcel paramParcel)
  {
    return new KeySet(paramParcel.readStrongBinder());
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof KeySet))
    {
      paramObject = (KeySet)paramObject;
      if (this.token == ((KeySet)paramObject).token) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public IBinder getToken()
  {
    return this.token;
  }
  
  public int hashCode()
  {
    return this.token.hashCode();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.token);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/KeySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */