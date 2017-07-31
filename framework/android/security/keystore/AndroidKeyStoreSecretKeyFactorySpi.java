package android.security.keystore;

import android.security.GateKeeper;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;

public class AndroidKeyStoreSecretKeyFactorySpi
  extends SecretKeyFactorySpi
{
  private final KeyStore mKeyStore = KeyStore.getInstance();
  
  private static BigInteger getGateKeeperSecureUserId()
    throws ProviderException
  {
    try
    {
      BigInteger localBigInteger = BigInteger.valueOf(GateKeeper.getSecureUserId());
      return localBigInteger;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new ProviderException("Failed to get GateKeeper secure user ID", localIllegalStateException);
    }
  }
  
  static KeyInfo getKeyInfo(KeyStore paramKeyStore, String paramString1, String paramString2, int paramInt)
  {
    KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
    paramInt = paramKeyStore.getKeyCharacteristics(paramString2, null, null, paramInt, localKeyCharacteristics);
    if (paramInt != 1) {
      throw new ProviderException("Failed to obtain information about key. Keystore error: " + paramInt);
    }
    boolean bool3;
    long l;
    for (;;)
    {
      try
      {
        if (localKeyCharacteristics.hwEnforced.containsTag(268436158))
        {
          bool3 = true;
          paramInt = KeyProperties.Origin.fromKeymaster(localKeyCharacteristics.hwEnforced.getEnum(268436158, -1));
          l = localKeyCharacteristics.getUnsignedInt(805306371, -1L);
          if (l != -1L) {
            break label170;
          }
          throw new ProviderException("Key size not available");
        }
      }
      catch (IllegalArgumentException paramKeyStore)
      {
        throw new ProviderException("Unsupported key characteristic", paramKeyStore);
      }
      if (!localKeyCharacteristics.swEnforced.containsTag(268436158)) {
        break;
      }
      bool3 = false;
      paramInt = KeyProperties.Origin.fromKeymaster(localKeyCharacteristics.swEnforced.getEnum(268436158, -1));
    }
    throw new ProviderException("Key origin not available");
    label170:
    if (l > 2147483647L) {
      throw new ProviderException("Key too large: " + l + " bits");
    }
    int i = (int)l;
    int j = KeyProperties.Purpose.allFromKeymaster(localKeyCharacteristics.getEnums(536870913));
    paramKeyStore = new ArrayList();
    paramString2 = new ArrayList();
    Object localObject = localKeyCharacteristics.getEnums(536870918).iterator();
    while (((Iterator)localObject).hasNext())
    {
      k = ((Integer)((Iterator)localObject).next()).intValue();
      try
      {
        paramKeyStore.add(KeyProperties.EncryptionPadding.fromKeymaster(k));
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        try
        {
          paramString2.add(KeyProperties.SignaturePadding.fromKeymaster(k));
        }
        catch (IllegalArgumentException paramKeyStore)
        {
          throw new ProviderException("Unsupported encryption padding: " + k);
        }
      }
    }
    paramKeyStore = (String[])paramKeyStore.toArray(new String[paramKeyStore.size()]);
    paramString2 = (String[])paramString2.toArray(new String[paramString2.size()]);
    localObject = KeyProperties.Digest.allFromKeymaster(localKeyCharacteristics.getEnums(536870917));
    String[] arrayOfString = KeyProperties.BlockMode.allFromKeymaster(localKeyCharacteristics.getEnums(536870916));
    int k = localKeyCharacteristics.swEnforced.getEnum(268435960, 0);
    int m = localKeyCharacteristics.hwEnforced.getEnum(268435960, 0);
    List localList = localKeyCharacteristics.getUnsignedLongs(-1610612234);
    Date localDate1 = localKeyCharacteristics.getDate(1610613136);
    Date localDate2 = localKeyCharacteristics.getDate(1610613137);
    Date localDate3 = localKeyCharacteristics.getDate(1610613138);
    if (localKeyCharacteristics.getBoolean(1879048695)) {}
    for (boolean bool4 = false;; bool4 = true)
    {
      l = localKeyCharacteristics.getUnsignedInt(805306873, -1L);
      if (l <= 2147483647L) {
        break;
      }
      throw new ProviderException("User authentication timeout validity too long: " + l + " seconds");
    }
    boolean bool2;
    boolean bool5;
    boolean bool1;
    if ((bool4) && (m != 0)) {
      if (k == 0)
      {
        bool2 = true;
        bool5 = localKeyCharacteristics.hwEnforced.getBoolean(1879048698);
        bool1 = false;
        if ((k == 2) || (m == 2))
        {
          if ((localList != null) && (!localList.isEmpty())) {
            break label656;
          }
          bool1 = false;
        }
      }
    }
    for (;;)
    {
      return new KeyInfo(paramString1, bool3, paramInt, i, localDate1, localDate2, localDate3, j, paramKeyStore, paramString2, (String[])localObject, arrayOfString, bool4, (int)l, bool2, bool5, bool1);
      bool2 = false;
      break;
      bool2 = false;
      break;
      label656:
      if (localList.contains(getGateKeeperSecureUserId())) {
        bool1 = false;
      } else {
        bool1 = true;
      }
    }
  }
  
  protected SecretKey engineGenerateSecret(KeySpec paramKeySpec)
    throws InvalidKeySpecException
  {
    throw new InvalidKeySpecException("To generate secret key in Android Keystore, use KeyGenerator initialized with " + KeyGenParameterSpec.class.getName());
  }
  
  protected KeySpec engineGetKeySpec(SecretKey paramSecretKey, Class paramClass)
    throws InvalidKeySpecException
  {
    if (paramClass == null) {
      throw new InvalidKeySpecException("keySpecClass == null");
    }
    if (!(paramSecretKey instanceof AndroidKeyStoreSecretKey))
    {
      paramClass = new StringBuilder().append("Only Android KeyStore secret keys supported: ");
      if (paramSecretKey != null) {}
      for (paramSecretKey = paramSecretKey.getClass().getName();; paramSecretKey = "null") {
        throw new InvalidKeySpecException(paramSecretKey);
      }
    }
    if (SecretKeySpec.class.isAssignableFrom(paramClass)) {
      throw new InvalidKeySpecException("Key material export of Android KeyStore keys is not supported");
    }
    if (!KeyInfo.class.equals(paramClass)) {
      throw new InvalidKeySpecException("Unsupported key spec: " + paramClass.getName());
    }
    paramClass = paramSecretKey.getAlias();
    if (paramClass.startsWith("USRSKEY_"))
    {
      String str = paramClass.substring("USRSKEY_".length());
      return getKeyInfo(this.mKeyStore, str, paramClass, paramSecretKey.getUid());
    }
    throw new InvalidKeySpecException("Invalid key alias: " + paramClass);
  }
  
  protected SecretKey engineTranslateKey(SecretKey paramSecretKey)
    throws InvalidKeyException
  {
    if (paramSecretKey == null) {
      throw new InvalidKeyException("key == null");
    }
    if (!(paramSecretKey instanceof AndroidKeyStoreSecretKey)) {
      throw new InvalidKeyException("To import a secret key into Android Keystore, use KeyStore.setEntry");
    }
    return paramSecretKey;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreSecretKeyFactorySpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */