package android.security.keystore;

import java.security.PublicKey;
import java.util.Arrays;

public class AndroidKeyStorePublicKey
  extends AndroidKeyStoreKey
  implements PublicKey
{
  private final byte[] mEncoded;
  
  public AndroidKeyStorePublicKey(String paramString1, int paramInt, String paramString2, byte[] paramArrayOfByte)
  {
    super(paramString1, paramInt, paramString2);
    this.mEncoded = ArrayUtils.cloneIfNotEmpty(paramArrayOfByte);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AndroidKeyStorePublicKey)paramObject;
    return Arrays.equals(this.mEncoded, ((AndroidKeyStorePublicKey)paramObject).mEncoded);
  }
  
  public byte[] getEncoded()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mEncoded);
  }
  
  public String getFormat()
  {
    return "X.509";
  }
  
  public int hashCode()
  {
    return super.hashCode() * 31 + Arrays.hashCode(this.mEncoded);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStorePublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */