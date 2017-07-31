package android.security.keystore;

import android.security.keymaster.ExportResult;
import android.security.keymaster.KeyCharacteristics;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.Mac;

public class AndroidKeyStoreProvider
  extends Provider
{
  private static final String PACKAGE_NAME = "android.security.keystore";
  public static final String PROVIDER_NAME = "AndroidKeyStore";
  
  public AndroidKeyStoreProvider()
  {
    super("AndroidKeyStore", 1.0D, "Android KeyStore security provider");
    put("KeyStore.AndroidKeyStore", "android.security.keystore.AndroidKeyStoreSpi");
    put("KeyPairGenerator.EC", "android.security.keystore.AndroidKeyStoreKeyPairGeneratorSpi$EC");
    put("KeyPairGenerator.RSA", "android.security.keystore.AndroidKeyStoreKeyPairGeneratorSpi$RSA");
    putKeyFactoryImpl("EC");
    putKeyFactoryImpl("RSA");
    put("KeyGenerator.AES", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$AES");
    put("KeyGenerator.HmacSHA1", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$HmacSHA1");
    put("KeyGenerator.HmacSHA224", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$HmacSHA224");
    put("KeyGenerator.HmacSHA256", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$HmacSHA256");
    put("KeyGenerator.HmacSHA384", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$HmacSHA384");
    put("KeyGenerator.HmacSHA512", "android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$HmacSHA512");
    putSecretKeyFactoryImpl("AES");
    putSecretKeyFactoryImpl("HmacSHA1");
    putSecretKeyFactoryImpl("HmacSHA224");
    putSecretKeyFactoryImpl("HmacSHA256");
    putSecretKeyFactoryImpl("HmacSHA384");
    putSecretKeyFactoryImpl("HmacSHA512");
  }
  
  public static AndroidKeyStorePrivateKey getAndroidKeyStorePrivateKey(AndroidKeyStorePublicKey paramAndroidKeyStorePublicKey)
  {
    String str = paramAndroidKeyStorePublicKey.getAlgorithm();
    if ("EC".equalsIgnoreCase(str)) {
      return new AndroidKeyStoreECPrivateKey(paramAndroidKeyStorePublicKey.getAlias(), paramAndroidKeyStorePublicKey.getUid(), ((ECKey)paramAndroidKeyStorePublicKey).getParams());
    }
    if ("RSA".equalsIgnoreCase(str)) {
      return new AndroidKeyStoreRSAPrivateKey(paramAndroidKeyStorePublicKey.getAlias(), paramAndroidKeyStorePublicKey.getUid(), ((RSAKey)paramAndroidKeyStorePublicKey).getModulus());
    }
    throw new ProviderException("Unsupported Android Keystore public key algorithm: " + str);
  }
  
  public static AndroidKeyStorePublicKey getAndroidKeyStorePublicKey(String paramString1, int paramInt, String paramString2, byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = KeyFactory.getInstance(paramString2).generatePublic(new X509EncodedKeySpec(paramArrayOfByte));
      if ("EC".equalsIgnoreCase(paramString2)) {
        return new AndroidKeyStoreECPublicKey(paramString1, paramInt, (ECPublicKey)paramArrayOfByte);
      }
    }
    catch (InvalidKeySpecException paramString1)
    {
      throw new ProviderException("Invalid X.509 encoding of public key", paramString1);
    }
    catch (NoSuchAlgorithmException paramString1)
    {
      throw new ProviderException("Failed to obtain " + paramString2 + " KeyFactory", paramString1);
    }
    if ("RSA".equalsIgnoreCase(paramString2)) {
      return new AndroidKeyStoreRSAPublicKey(paramString1, paramInt, (RSAPublicKey)paramArrayOfByte);
    }
    throw new ProviderException("Unsupported Android Keystore public key algorithm: " + paramString2);
  }
  
  public static java.security.KeyStore getKeyStoreForUid(int paramInt)
    throws KeyStoreException, NoSuchProviderException
  {
    java.security.KeyStore localKeyStore = java.security.KeyStore.getInstance("AndroidKeyStore", "AndroidKeyStore");
    try
    {
      localKeyStore.load(new AndroidKeyStoreLoadStoreParameter(paramInt));
      return localKeyStore;
    }
    catch (NoSuchAlgorithmException|CertificateException|IOException localNoSuchAlgorithmException)
    {
      throw new KeyStoreException("Failed to load AndroidKeyStore KeyStore for UID " + paramInt, localNoSuchAlgorithmException);
    }
  }
  
  public static long getKeyStoreOperationHandle(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    Object localObject;
    if ((paramObject instanceof Signature)) {
      localObject = ((Signature)paramObject).getCurrentSpi();
    }
    while (localObject == null)
    {
      throw new IllegalStateException("Crypto primitive not initialized");
      if ((paramObject instanceof Mac)) {
        localObject = ((Mac)paramObject).getCurrentSpi();
      } else if ((paramObject instanceof Cipher)) {
        localObject = ((Cipher)paramObject).getCurrentSpi();
      } else {
        throw new IllegalArgumentException("Unsupported crypto primitive: " + paramObject + ". Supported: Signature, Mac, Cipher");
      }
    }
    if (!(localObject instanceof KeyStoreCryptoOperation)) {
      throw new IllegalArgumentException("Crypto primitive not backed by AndroidKeyStore provider: " + paramObject + ", spi: " + localObject);
    }
    return ((KeyStoreCryptoOperation)localObject).getOperationHandle();
  }
  
  public static void install()
  {
    Object localObject = Security.getProviders();
    int k = -1;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < localObject.length)
      {
        if ("BC".equals(localObject[i].getName())) {
          j = i;
        }
      }
      else
      {
        Security.addProvider(new AndroidKeyStoreProvider());
        localObject = new AndroidKeyStoreBCWorkaroundProvider();
        if (j == -1) {
          break;
        }
        Security.insertProviderAt((Provider)localObject, j + 1);
        return;
      }
      i += 1;
    }
    Security.addProvider((Provider)localObject);
  }
  
  public static KeyPair loadAndroidKeyStoreKeyPairFromKeystore(android.security.KeyStore paramKeyStore, String paramString, int paramInt)
    throws UnrecoverableKeyException
  {
    paramKeyStore = loadAndroidKeyStorePublicKeyFromKeystore(paramKeyStore, paramString, paramInt);
    return new KeyPair(paramKeyStore, getAndroidKeyStorePrivateKey(paramKeyStore));
  }
  
  public static AndroidKeyStorePrivateKey loadAndroidKeyStorePrivateKeyFromKeystore(android.security.KeyStore paramKeyStore, String paramString, int paramInt)
    throws UnrecoverableKeyException
  {
    return (AndroidKeyStorePrivateKey)loadAndroidKeyStoreKeyPairFromKeystore(paramKeyStore, paramString, paramInt).getPrivate();
  }
  
  public static AndroidKeyStorePublicKey loadAndroidKeyStorePublicKeyFromKeystore(android.security.KeyStore paramKeyStore, String paramString, int paramInt)
    throws UnrecoverableKeyException
  {
    Object localObject = new KeyCharacteristics();
    int i = paramKeyStore.getKeyCharacteristics(paramString, null, null, paramInt, (KeyCharacteristics)localObject);
    if (i != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain information about private key").initCause(android.security.KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = paramKeyStore.exportKey(paramString, 0, null, null, paramInt);
    if (paramKeyStore.resultCode != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain X.509 form of public key").initCause(android.security.KeyStore.getKeyStoreException(paramKeyStore.resultCode)));
    }
    paramKeyStore = paramKeyStore.exportData;
    localObject = ((KeyCharacteristics)localObject).getEnum(268435458);
    if (localObject == null) {
      throw new UnrecoverableKeyException("Key algorithm unknown");
    }
    try
    {
      localObject = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(((Integer)localObject).intValue());
      return getAndroidKeyStorePublicKey(paramString, paramInt, (String)localObject, paramKeyStore);
    }
    catch (IllegalArgumentException paramKeyStore)
    {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to load private key").initCause(paramKeyStore));
    }
  }
  
  public static AndroidKeyStoreSecretKey loadAndroidKeyStoreSecretKeyFromKeystore(android.security.KeyStore paramKeyStore, String paramString, int paramInt)
    throws UnrecoverableKeyException
  {
    Object localObject = new KeyCharacteristics();
    int i = paramKeyStore.getKeyCharacteristics(paramString, null, null, paramInt, (KeyCharacteristics)localObject);
    if (i != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain information about key").initCause(android.security.KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = ((KeyCharacteristics)localObject).getEnum(268435458);
    if (paramKeyStore == null) {
      throw new UnrecoverableKeyException("Key algorithm unknown");
    }
    localObject = ((KeyCharacteristics)localObject).getEnums(536870917);
    if (((List)localObject).isEmpty()) {}
    for (i = -1;; i = ((Integer)((List)localObject).get(0)).intValue()) {
      try
      {
        paramKeyStore = KeyProperties.KeyAlgorithm.fromKeymasterSecretKeyAlgorithm(paramKeyStore.intValue(), i);
        return new AndroidKeyStoreSecretKey(paramString, paramInt, paramKeyStore);
      }
      catch (IllegalArgumentException paramKeyStore)
      {
        throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Unsupported secret key type").initCause(paramKeyStore));
      }
    }
  }
  
  private void putKeyFactoryImpl(String paramString)
  {
    put("KeyFactory." + paramString, "android.security.keystore.AndroidKeyStoreKeyFactorySpi");
  }
  
  private void putSecretKeyFactoryImpl(String paramString)
  {
    put("SecretKeyFactory." + paramString, "android.security.keystore.AndroidKeyStoreSecretKeyFactorySpi");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */