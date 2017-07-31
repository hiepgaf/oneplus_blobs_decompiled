package android.security.keymaster;

import android.os.Parcel;

class KeymasterBooleanArgument
  extends KeymasterArgument
{
  public final boolean value = true;
  
  public KeymasterBooleanArgument(int paramInt)
  {
    super(paramInt);
    switch (KeymasterDefs.getTagType(paramInt))
    {
    default: 
      throw new IllegalArgumentException("Bad bool tag " + paramInt);
    }
  }
  
  public KeymasterBooleanArgument(int paramInt, Parcel paramParcel)
  {
    super(paramInt);
  }
  
  public void writeValue(Parcel paramParcel) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterBooleanArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */