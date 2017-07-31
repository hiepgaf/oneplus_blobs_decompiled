package android.security.keystore;

import java.math.BigInteger;
import java.security.interfaces.RSAKey;

public class AndroidKeyStoreRSAPrivateKey
  extends AndroidKeyStorePrivateKey
  implements RSAKey
{
  private final BigInteger mModulus;
  
  public AndroidKeyStoreRSAPrivateKey(String paramString, int paramInt, BigInteger paramBigInteger)
  {
    super(paramString, paramInt, "RSA");
    this.mModulus = paramBigInteger;
  }
  
  public BigInteger getModulus()
  {
    return this.mModulus;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreRSAPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */