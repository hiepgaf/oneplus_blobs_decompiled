package android.security.keystore;

import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.ProtectionParameter;

class AndroidKeyStoreLoadStoreParameter
  implements KeyStore.LoadStoreParameter
{
  private final int mUid;
  
  AndroidKeyStoreLoadStoreParameter(int paramInt)
  {
    this.mUid = paramInt;
  }
  
  public KeyStore.ProtectionParameter getProtectionParameter()
  {
    return null;
  }
  
  int getUid()
  {
    return this.mUid;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreLoadStoreParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */