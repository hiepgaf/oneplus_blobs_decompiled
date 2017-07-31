package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KeyCharacteristics
  implements Parcelable
{
  public static final Parcelable.Creator<KeyCharacteristics> CREATOR = new Parcelable.Creator()
  {
    public KeyCharacteristics createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeyCharacteristics(paramAnonymousParcel);
    }
    
    public KeyCharacteristics[] newArray(int paramAnonymousInt)
    {
      return new KeyCharacteristics[paramAnonymousInt];
    }
  };
  public KeymasterArguments hwEnforced;
  public KeymasterArguments swEnforced;
  
  public KeyCharacteristics() {}
  
  protected KeyCharacteristics(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean getBoolean(int paramInt)
  {
    if (this.hwEnforced.containsTag(paramInt)) {
      return this.hwEnforced.getBoolean(paramInt);
    }
    return this.swEnforced.getBoolean(paramInt);
  }
  
  public Date getDate(int paramInt)
  {
    Date localDate = this.swEnforced.getDate(paramInt, null);
    if (localDate != null) {
      return localDate;
    }
    return this.hwEnforced.getDate(paramInt, null);
  }
  
  public Integer getEnum(int paramInt)
  {
    if (this.hwEnforced.containsTag(paramInt)) {
      return Integer.valueOf(this.hwEnforced.getEnum(paramInt, -1));
    }
    if (this.swEnforced.containsTag(paramInt)) {
      return Integer.valueOf(this.swEnforced.getEnum(paramInt, -1));
    }
    return null;
  }
  
  public List<Integer> getEnums(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.hwEnforced.getEnums(paramInt));
    localArrayList.addAll(this.swEnforced.getEnums(paramInt));
    return localArrayList;
  }
  
  public long getUnsignedInt(int paramInt, long paramLong)
  {
    if (this.hwEnforced.containsTag(paramInt)) {
      return this.hwEnforced.getUnsignedInt(paramInt, paramLong);
    }
    return this.swEnforced.getUnsignedInt(paramInt, paramLong);
  }
  
  public List<BigInteger> getUnsignedLongs(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.hwEnforced.getUnsignedLongs(paramInt));
    localArrayList.addAll(this.swEnforced.getUnsignedLongs(paramInt));
    return localArrayList;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.swEnforced = ((KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel));
    this.hwEnforced = ((KeymasterArguments)KeymasterArguments.CREATOR.createFromParcel(paramParcel));
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.swEnforced.writeToParcel(paramParcel, paramInt);
    this.hwEnforced.writeToParcel(paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeyCharacteristics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */