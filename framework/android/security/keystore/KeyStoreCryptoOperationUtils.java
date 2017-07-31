package android.security.keystore;

import android.security.KeyStore;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import libcore.util.EmptyArray;

abstract class KeyStoreCryptoOperationUtils
{
  private static volatile SecureRandom sRng;
  
  public static GeneralSecurityException getExceptionForCipherInit(KeyStore paramKeyStore, AndroidKeyStoreKey paramAndroidKeyStoreKey, int paramInt)
  {
    if (paramInt == 1) {
      return null;
    }
    switch (paramInt)
    {
    case -54: 
    case -53: 
    default: 
      return getInvalidKeyExceptionForInit(paramKeyStore, paramAndroidKeyStoreKey, paramInt);
    case -52: 
      return new InvalidAlgorithmParameterException("Invalid IV");
    }
    return new InvalidAlgorithmParameterException("Caller-provided IV not permitted");
  }
  
  static InvalidKeyException getInvalidKeyExceptionForInit(KeyStore paramKeyStore, AndroidKeyStoreKey paramAndroidKeyStoreKey, int paramInt)
  {
    if (paramInt == 1) {
      return null;
    }
    paramKeyStore = paramKeyStore.getInvalidKeyException(paramAndroidKeyStoreKey.getAlias(), paramAndroidKeyStoreKey.getUid(), paramInt);
    switch (paramInt)
    {
    }
    do
    {
      return paramKeyStore;
    } while (!(paramKeyStore instanceof UserNotAuthenticatedException));
    return null;
  }
  
  static byte[] getRandomBytesToMixIntoKeystoreRng(SecureRandom paramSecureRandom, int paramInt)
  {
    if (paramInt <= 0) {
      return EmptyArray.BYTE;
    }
    SecureRandom localSecureRandom = paramSecureRandom;
    if (paramSecureRandom == null) {
      localSecureRandom = getRng();
    }
    paramSecureRandom = new byte[paramInt];
    localSecureRandom.nextBytes(paramSecureRandom);
    return paramSecureRandom;
  }
  
  private static SecureRandom getRng()
  {
    if (sRng == null) {
      sRng = new SecureRandom();
    }
    return sRng;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyStoreCryptoOperationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */