package android.security.keystore;

import android.security.KeyStore;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class SoterKeyStoreSpi
  extends AndroidKeyStoreSpi
{
  private KeyStore mKeyStore = null;
  
  private boolean isPrivateKeyEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    return this.mKeyStore.contains("USRPKEY_" + paramString);
  }
  
  private boolean isSecretKeyEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    return this.mKeyStore.contains("USRSKEY_" + paramString);
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("alias == null");
    }
    if (!this.mKeyStore.contains("USRPKEY_" + paramString)) {
      return this.mKeyStore.contains("USRCERT_" + paramString);
    }
    return true;
  }
  
  public void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    if (!engineContainsAlias(paramString)) {
      return;
    }
    if (!(this.mKeyStore.delete("USRPKEY_" + paramString) | this.mKeyStore.delete("USRCERT_" + paramString))) {
      throw new KeyStoreException("Failed to delete entry: " + paramString);
    }
  }
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    if (isPrivateKeyEntry(paramString))
    {
      paramString = "USRPKEY_" + paramString;
      if ((paramArrayOfChar != null) && ("from_soter_ui".equals(String.valueOf(paramArrayOfChar)))) {
        return SoterKeyStoreProvider.loadJsonPublicKeyFromKeystore(this.mKeyStore, paramString);
      }
      return SoterKeyStoreProvider.loadAndroidKeyStorePrivateKeyFromKeystore(this.mKeyStore, paramString);
    }
    if (isSecretKeyEntry(paramString))
    {
      paramString = "USRSKEY_" + paramString;
      return AndroidKeyStoreProvider.loadAndroidKeyStoreSecretKeyFromKeystore(this.mKeyStore, paramString, -1);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/SoterKeyStoreSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */