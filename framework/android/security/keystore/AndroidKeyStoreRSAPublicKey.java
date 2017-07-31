package android.security.keystore;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public class AndroidKeyStoreRSAPublicKey
  extends AndroidKeyStorePublicKey
  implements RSAPublicKey
{
  private final BigInteger mModulus;
  private final BigInteger mPublicExponent;
  
  public AndroidKeyStoreRSAPublicKey(String paramString, int paramInt, RSAPublicKey paramRSAPublicKey)
  {
    this(paramString, paramInt, paramRSAPublicKey.getEncoded(), paramRSAPublicKey.getModulus(), paramRSAPublicKey.getPublicExponent());
    if (!"X.509".equalsIgnoreCase(paramRSAPublicKey.getFormat())) {
      throw new IllegalArgumentException("Unsupported key export format: " + paramRSAPublicKey.getFormat());
    }
  }
  
  public AndroidKeyStoreRSAPublicKey(String paramString, int paramInt, byte[] paramArrayOfByte, BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    super(paramString, paramInt, "RSA", paramArrayOfByte);
    this.mModulus = paramBigInteger1;
    this.mPublicExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus()
  {
    return this.mModulus;
  }
  
  public BigInteger getPublicExponent()
  {
    return this.mPublicExponent;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreRSAPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */