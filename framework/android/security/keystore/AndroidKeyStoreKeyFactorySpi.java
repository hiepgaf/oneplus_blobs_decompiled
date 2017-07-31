package android.security.keystore;

import android.security.KeyStore;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AndroidKeyStoreKeyFactorySpi
  extends KeyFactorySpi
{
  private final KeyStore mKeyStore = KeyStore.getInstance();
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec)
    throws InvalidKeySpecException
  {
    throw new InvalidKeySpecException("To generate a key pair in Android Keystore, use KeyPairGenerator initialized with " + KeyGenParameterSpec.class.getName());
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec)
    throws InvalidKeySpecException
  {
    throw new InvalidKeySpecException("To generate a key pair in Android Keystore, use KeyPairGenerator initialized with " + KeyGenParameterSpec.class.getName());
  }
  
  protected <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass)
    throws InvalidKeySpecException
  {
    if (paramKey == null) {
      throw new InvalidKeySpecException("key == null");
    }
    if (((paramKey instanceof AndroidKeyStorePrivateKey)) || ((paramKey instanceof AndroidKeyStorePublicKey)))
    {
      if (paramClass == null) {
        throw new InvalidKeySpecException("keySpecClass == null");
      }
    }
    else {
      throw new InvalidKeySpecException("Unsupported key type: " + paramKey.getClass().getName() + ". This KeyFactory supports only Android Keystore asymmetric keys");
    }
    if (KeyInfo.class.equals(paramClass))
    {
      if (!(paramKey instanceof AndroidKeyStorePrivateKey)) {
        throw new InvalidKeySpecException("Unsupported key type: " + paramKey.getClass().getName() + ". KeyInfo can be obtained only for Android Keystore private keys");
      }
      paramClass = paramKey.getAlias();
      if (paramClass.startsWith("USRPKEY_"))
      {
        String str = paramClass.substring("USRPKEY_".length());
        return AndroidKeyStoreSecretKeyFactorySpi.getKeyInfo(this.mKeyStore, str, paramClass, paramKey.getUid());
      }
      throw new InvalidKeySpecException("Invalid key alias: " + paramClass);
    }
    if (X509EncodedKeySpec.class.equals(paramClass))
    {
      if (!(paramKey instanceof AndroidKeyStorePublicKey)) {
        throw new InvalidKeySpecException("Unsupported key type: " + paramKey.getClass().getName() + ". X509EncodedKeySpec can be obtained only for Android Keystore public" + " keys");
      }
      return new X509EncodedKeySpec(paramKey.getEncoded());
    }
    if (PKCS8EncodedKeySpec.class.equals(paramClass))
    {
      if ((paramKey instanceof AndroidKeyStorePrivateKey)) {
        throw new InvalidKeySpecException("Key material export of Android Keystore private keys is not supported");
      }
      throw new InvalidKeySpecException("Cannot export key material of public key in PKCS#8 format. Only X.509 format (X509EncodedKeySpec) supported for public keys.");
    }
    if (RSAPublicKeySpec.class.equals(paramClass))
    {
      if ((paramKey instanceof AndroidKeyStoreRSAPublicKey)) {
        return new RSAPublicKeySpec(paramKey.getModulus(), paramKey.getPublicExponent());
      }
      paramClass = new StringBuilder().append("Obtaining RSAPublicKeySpec not supported for ").append(paramKey.getAlgorithm()).append(" ");
      if ((paramKey instanceof AndroidKeyStorePrivateKey)) {}
      for (paramKey = "private";; paramKey = "public") {
        throw new InvalidKeySpecException(paramKey + " key");
      }
    }
    if (ECPublicKeySpec.class.equals(paramClass))
    {
      if ((paramKey instanceof AndroidKeyStoreECPublicKey)) {
        return new ECPublicKeySpec(paramKey.getW(), paramKey.getParams());
      }
      paramClass = new StringBuilder().append("Obtaining ECPublicKeySpec not supported for ").append(paramKey.getAlgorithm()).append(" ");
      if ((paramKey instanceof AndroidKeyStorePrivateKey)) {}
      for (paramKey = "private";; paramKey = "public") {
        throw new InvalidKeySpecException(paramKey + " key");
      }
    }
    throw new InvalidKeySpecException("Unsupported key spec: " + paramClass.getName());
  }
  
  protected Key engineTranslateKey(Key paramKey)
    throws InvalidKeyException
  {
    if (paramKey == null) {
      throw new InvalidKeyException("key == null");
    }
    if (((paramKey instanceof AndroidKeyStorePrivateKey)) || ((paramKey instanceof AndroidKeyStorePublicKey))) {
      return paramKey;
    }
    throw new InvalidKeyException("To import a key into Android Keystore, use KeyStore.setEntry");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreKeyFactorySpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */