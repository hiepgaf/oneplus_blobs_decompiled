package android.security.keystore;

import java.util.Date;

abstract class Utils
{
  static Date cloneIfNotNull(Date paramDate)
  {
    Date localDate = null;
    if (paramDate != null) {
      localDate = (Date)paramDate.clone();
    }
    return localDate;
  }
  
  static byte[] cloneIfNotNull(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = null;
    if (paramArrayOfByte != null) {
      arrayOfByte = (byte[])paramArrayOfByte.clone();
    }
    return arrayOfByte;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */