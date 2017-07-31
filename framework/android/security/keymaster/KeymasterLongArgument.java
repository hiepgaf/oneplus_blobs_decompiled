package android.security.keymaster;

import android.os.Parcel;

class KeymasterLongArgument
  extends KeymasterArgument
{
  public final long value;
  
  public KeymasterLongArgument(int paramInt, long paramLong)
  {
    super(paramInt);
    switch (KeymasterDefs.getTagType(paramInt))
    {
    default: 
      throw new IllegalArgumentException("Bad long tag " + paramInt);
    }
    this.value = paramLong;
  }
  
  public KeymasterLongArgument(int paramInt, Parcel paramParcel)
  {
    super(paramInt);
    this.value = paramParcel.readLong();
  }
  
  public void writeValue(Parcel paramParcel)
  {
    paramParcel.writeLong(this.value);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterLongArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */