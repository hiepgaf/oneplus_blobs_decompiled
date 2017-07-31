package android.security.keystore;

import android.security.KeyStore;
import android.security.keymaster.ExportResult;
import android.security.keymaster.KeyCharacteristics;
import android.util.Log;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.json.JSONException;

public class SoterKeyStoreProvider
  extends Provider
{
  private static final String ANDROID_PACKAGE_NAME = "android.security.keystore";
  public static final String PROVIDER_NAME = "SoterKeyStore";
  private static final String SOTER_PACKAGE_NAME = "android.security.keystore";
  
  public SoterKeyStoreProvider()
  {
    super("SoterKeyStore", 1.0D, "provider for soter");
    put("KeyPairGenerator.RSA", "android.security.keystore.SoterKeyStoreKeyPairRSAGeneratorSpi");
    put("KeyStore.SoterKeyStore", "android.security.keystore.SoterKeyStoreSpi");
    putKeyFactoryImpl("RSA");
  }
  
  public static AndroidKeyStorePrivateKey getAndroidKeyStorePrivateKey(AndroidKeyStorePublicKey paramAndroidKeyStorePublicKey)
  {
    String str = paramAndroidKeyStorePublicKey.getAlgorithm();
    if ("EC".equalsIgnoreCase(str)) {
      return new AndroidKeyStoreECPrivateKey(paramAndroidKeyStorePublicKey.getAlias(), -1, ((ECKey)paramAndroidKeyStorePublicKey).getParams());
    }
    if ("RSA".equalsIgnoreCase(str)) {
      return new AndroidKeyStoreRSAPrivateKey(paramAndroidKeyStorePublicKey.getAlias(), -1, ((RSAKey)paramAndroidKeyStorePublicKey).getModulus());
    }
    throw new ProviderException("Unsupported Android Keystore public key algorithm: " + str);
  }
  
  public static AndroidKeyStorePublicKey getAndroidKeyStorePublicKey(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    try
    {
      KeyFactory localKeyFactory = KeyFactory.getInstance(paramString2);
      paramArrayOfByte = SoterUtil.getDataFromRaw(paramArrayOfByte, "pub_key");
      if (paramArrayOfByte != null)
      {
        paramArrayOfByte = localKeyFactory.generatePublic(new X509EncodedKeySpec(paramArrayOfByte));
        if ("EC".equalsIgnoreCase(paramString2))
        {
          Log.d("Soter", "AndroidKeyStoreECPublicKey");
          return new AndroidKeyStoreECPublicKey(paramString1, -1, (ECPublicKey)paramArrayOfByte);
        }
      }
      else
      {
        throw new NullPointerException("invalid soter public key");
      }
    }
    catch (NoSuchAlgorithmException paramString1)
    {
      throw new ProviderException("Failed to obtain " + paramString2 + " KeyFactory", paramString1);
    }
    catch (JSONException paramString1)
    {
      throw new ProviderException("Not in json format");
    }
    catch (InvalidKeySpecException paramString1)
    {
      throw new ProviderException("Invalid X.509 encoding of public key", paramString1);
    }
    if ("RSA".equalsIgnoreCase(paramString2))
    {
      Log.d("Soter", "AndroidKeyStoreRSAPublicKey");
      return new AndroidKeyStoreRSAPublicKey(paramString1, -1, (RSAPublicKey)paramArrayOfByte);
    }
    throw new ProviderException("Unsupported Android Keystore public key algorithm: " + paramString2);
  }
  
  public static AndroidKeyStorePublicKey getJsonPublicKey(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    Object localObject;
    try
    {
      localObject = KeyFactory.getInstance(paramString2);
      byte[] arrayOfByte = SoterUtil.getDataFromRaw(paramArrayOfByte, "pub_key");
      if (arrayOfByte != null)
      {
        localObject = ((KeyFactory)localObject).generatePublic(new X509EncodedKeySpec(arrayOfByte));
        if ("EC".equalsIgnoreCase(paramString2))
        {
          Log.d("Soter", "AndroidKeyStoreECPublicKey");
          return new AndroidKeyStoreECPublicKey(paramString1, -1, (ECPublicKey)localObject);
        }
      }
      else
      {
        throw new NullPointerException("invalid soter public key");
      }
    }
    catch (NoSuchAlgorithmException paramString1)
    {
      throw new ProviderException("Failed to obtain " + paramString2 + " KeyFactory", paramString1);
    }
    catch (JSONException paramString1)
    {
      throw new ProviderException("Not in json format");
    }
    catch (InvalidKeySpecException paramString1)
    {
      throw new ProviderException("Invalid X.509 encoding of public key", paramString1);
    }
    if ("RSA".equalsIgnoreCase(paramString2))
    {
      Log.d("Soter", "getJsonPublicKey");
      paramString2 = (RSAPublicKey)localObject;
      return new AndroidKeyStoreRSAPublicKey(paramString1, -1, paramArrayOfByte, paramString2.getModulus(), paramString2.getPublicExponent());
    }
    throw new ProviderException("Unsupported Android Keystore public key algorithm: " + paramString2);
  }
  
  public static void install()
  {
    Security.addProvider(new SoterKeyStoreProvider());
  }
  
  public static KeyPair loadAndroidKeyStoreKeyPairFromKeystore(KeyStore paramKeyStore, String paramString)
    throws UnrecoverableKeyException
  {
    paramKeyStore = loadAndroidKeyStorePublicKeyFromKeystore(paramKeyStore, paramString);
    return new KeyPair(paramKeyStore, getAndroidKeyStorePrivateKey(paramKeyStore));
  }
  
  public static AndroidKeyStorePrivateKey loadAndroidKeyStorePrivateKeyFromKeystore(KeyStore paramKeyStore, String paramString)
    throws UnrecoverableKeyException
  {
    return (AndroidKeyStorePrivateKey)loadAndroidKeyStoreKeyPairFromKeystore(paramKeyStore, paramString).getPrivate();
  }
  
  public static AndroidKeyStorePublicKey loadAndroidKeyStorePublicKeyFromKeystore(KeyStore paramKeyStore, String paramString)
    throws UnrecoverableKeyException
  {
    Object localObject = new KeyCharacteristics();
    int i = paramKeyStore.getKeyCharacteristics(paramString, null, null, (KeyCharacteristics)localObject);
    if (i != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain information about private key").initCause(KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = paramKeyStore.exportKey(paramString, 0, null, null);
    if (paramKeyStore.resultCode != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain X.509 form of public key").initCause(KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = paramKeyStore.exportData;
    localObject = ((KeyCharacteristics)localObject).getEnum(268435458);
    if (localObject == null) {
      throw new UnrecoverableKeyException("Key algorithm unknown");
    }
    try
    {
      localObject = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(((Integer)localObject).intValue());
      return getAndroidKeyStorePublicKey(paramString, (String)localObject, paramKeyStore);
    }
    catch (IllegalArgumentException paramKeyStore)
    {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to load private key").initCause(paramKeyStore));
    }
  }
  
  public static AndroidKeyStorePublicKey loadJsonPublicKeyFromKeystore(KeyStore paramKeyStore, String paramString)
    throws UnrecoverableKeyException
  {
    Object localObject = new KeyCharacteristics();
    int i = paramKeyStore.getKeyCharacteristics(paramString, null, null, (KeyCharacteristics)localObject);
    if (i != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain information about private key").initCause(KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = paramKeyStore.exportKey(paramString, 0, null, null);
    if (paramKeyStore.resultCode != 1) {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to obtain X.509 form of public key").initCause(KeyStore.getKeyStoreException(i)));
    }
    paramKeyStore = paramKeyStore.exportData;
    localObject = ((KeyCharacteristics)localObject).getEnum(268435458);
    if (localObject == null) {
      throw new UnrecoverableKeyException("Key algorithm unknown");
    }
    try
    {
      localObject = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(((Integer)localObject).intValue());
      return getJsonPublicKey(paramString, (String)localObject, paramKeyStore);
    }
    catch (IllegalArgumentException paramKeyStore)
    {
      throw ((UnrecoverableKeyException)new UnrecoverableKeyException("Failed to load private key").initCause(paramKeyStore));
    }
  }
  
  private void putKeyFactoryImpl(String paramString)
  {
    put("KeyFactory." + paramString, "android.security.keystore.AndroidKeyStoreKeyFactorySpi");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/SoterKeyStoreProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */