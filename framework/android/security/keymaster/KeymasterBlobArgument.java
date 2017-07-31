package android.security.keymaster;

import android.os.Parcel;

class KeymasterBlobArgument
  extends KeymasterArgument
{
  public final byte[] blob;
  
  public KeymasterBlobArgument(int paramInt, Parcel paramParcel)
  {
    super(paramInt);
    this.blob = paramParcel.createByteArray();
  }
  
  public KeymasterBlobArgument(int paramInt, byte[] paramArrayOfByte)
  {
    super(paramInt);
    switch (KeymasterDefs.getTagType(paramInt))
    {
    default: 
      throw new IllegalArgumentException("Bad blob tag " + paramInt);
    }
    this.blob = paramArrayOfByte;
  }
  
  public void writeValue(Parcel paramParcel)
  {
    paramParcel.writeByteArray(this.blob);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterBlobArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */