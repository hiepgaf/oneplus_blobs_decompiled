package android.security.keystore;

import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

public class AndroidKeyStoreECPublicKey
  extends AndroidKeyStorePublicKey
  implements ECPublicKey
{
  private final ECParameterSpec mParams;
  private final ECPoint mW;
  
  public AndroidKeyStoreECPublicKey(String paramString, int paramInt, ECPublicKey paramECPublicKey)
  {
    this(paramString, paramInt, paramECPublicKey.getEncoded(), paramECPublicKey.getParams(), paramECPublicKey.getW());
    if (!"X.509".equalsIgnoreCase(paramECPublicKey.getFormat())) {
      throw new IllegalArgumentException("Unsupported key export format: " + paramECPublicKey.getFormat());
    }
  }
  
  public AndroidKeyStoreECPublicKey(String paramString, int paramInt, byte[] paramArrayOfByte, ECParameterSpec paramECParameterSpec, ECPoint paramECPoint)
  {
    super(paramString, paramInt, "EC", paramArrayOfByte);
    this.mParams = paramECParameterSpec;
    this.mW = paramECPoint;
  }
  
  public ECParameterSpec getParams()
  {
    return this.mParams;
  }
  
  public ECPoint getW()
  {
    return this.mW;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreECPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */