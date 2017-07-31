package android.security.keystore;

import java.security.interfaces.ECKey;
import java.security.spec.ECParameterSpec;

public class AndroidKeyStoreECPrivateKey
  extends AndroidKeyStorePrivateKey
  implements ECKey
{
  private final ECParameterSpec mParams;
  
  public AndroidKeyStoreECPrivateKey(String paramString, int paramInt, ECParameterSpec paramECParameterSpec)
  {
    super(paramString, paramInt, "EC");
    this.mParams = paramECParameterSpec;
  }
  
  public ECParameterSpec getParams()
  {
    return this.mParams;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreECPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */