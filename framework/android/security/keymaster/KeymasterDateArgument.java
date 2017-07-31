package android.security.keymaster;

import android.os.Parcel;
import java.util.Date;

class KeymasterDateArgument
  extends KeymasterArgument
{
  public final Date date;
  
  public KeymasterDateArgument(int paramInt, Parcel paramParcel)
  {
    super(paramInt);
    this.date = new Date(paramParcel.readLong());
  }
  
  public KeymasterDateArgument(int paramInt, Date paramDate)
  {
    super(paramInt);
    switch (KeymasterDefs.getTagType(paramInt))
    {
    default: 
      throw new IllegalArgumentException("Bad date tag " + paramInt);
    }
    this.date = paramDate;
  }
  
  public void writeValue(Parcel paramParcel)
  {
    paramParcel.writeLong(this.date.getTime());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/KeymasterDateArgument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */