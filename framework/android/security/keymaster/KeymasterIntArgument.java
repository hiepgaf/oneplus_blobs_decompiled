package android.security.keymaster;

import android.os.Parcel;

class KeymasterIntArgument
  extends KeymasterArgument
{
  public final int value;
  
  public KeymasterIntArgument(int paramInt1, int paramInt2)
  {
    super(paramInt1);
    switch (KeymasterDefs.getTagType(paramInt1))
    {
    default: 
      throw new IllegalArgumentException("Bad int tag " + paramInt1);
    }
    this.value = paramInt2;
  }
  
  public KeymasterIntArgument(int paramInt, Parcel paramParcel)
  {
    super(paramInt);
    this.value = paramParcel.readInt();
  }
  
  public void writeValue(Parcel paramParcel)
  {
    paramParcel.writeInt(this.value);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterIntArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */