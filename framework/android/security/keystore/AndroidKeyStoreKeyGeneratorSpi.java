package android.security.keystore;

import android.security.Credentials;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import java.security.InvalidAlgorithmParameterException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import libcore.util.EmptyArray;

public abstract class AndroidKeyStoreKeyGeneratorSpi
  extends KeyGeneratorSpi
{
  private final int mDefaultKeySizeBits;
  protected int mKeySizeBits;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private final int mKeymasterAlgorithm;
  private int[] mKeymasterBlockModes;
  private final int mKeymasterDigest;
  private int[] mKeymasterDigests;
  private int[] mKeymasterPaddings;
  private int[] mKeymasterPurposes;
  private SecureRandom mRng;
  private KeyGenParameterSpec mSpec;
  
  protected AndroidKeyStoreKeyGeneratorSpi(int paramInt1, int paramInt2)
  {
    this(paramInt1, -1, paramInt2);
  }
  
  protected AndroidKeyStoreKeyGeneratorSpi(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mKeymasterAlgorithm = paramInt1;
    this.mKeymasterDigest = paramInt2;
    this.mDefaultKeySizeBits = paramInt3;
    if (this.mDefaultKeySizeBits <= 0) {
      throw new IllegalArgumentException("Default key size must be positive");
    }
    if ((this.mKeymasterAlgorithm == 128) && (this.mKeymasterDigest == -1)) {
      throw new IllegalArgumentException("Digest algorithm must be specified for HMAC key");
    }
  }
  
  private void resetAll()
  {
    this.mSpec = null;
    this.mRng = null;
    this.mKeySizeBits = -1;
    this.mKeymasterPurposes = null;
    this.mKeymasterPaddings = null;
    this.mKeymasterBlockModes = null;
  }
  
  protected SecretKey engineGenerateKey()
  {
    KeyGenParameterSpec localKeyGenParameterSpec = this.mSpec;
    if (localKeyGenParameterSpec == null) {
      throw new IllegalStateException("Not initialized");
    }
    Object localObject = new KeymasterArguments();
    ((KeymasterArguments)localObject).addUnsignedInt(805306371, this.mKeySizeBits);
    ((KeymasterArguments)localObject).addEnum(268435458, this.mKeymasterAlgorithm);
    ((KeymasterArguments)localObject).addEnums(536870913, this.mKeymasterPurposes);
    ((KeymasterArguments)localObject).addEnums(536870916, this.mKeymasterBlockModes);
    ((KeymasterArguments)localObject).addEnums(536870918, this.mKeymasterPaddings);
    ((KeymasterArguments)localObject).addEnums(536870917, this.mKeymasterDigests);
    if (localKeyGenParameterSpec.isUseSecureProcessor()) {
      ((KeymasterArguments)localObject).addBoolean(1879063192);
    }
    KeymasterUtils.addUserAuthArgs((KeymasterArguments)localObject, localKeyGenParameterSpec.isUserAuthenticationRequired(), localKeyGenParameterSpec.getUserAuthenticationValidityDurationSeconds(), localKeyGenParameterSpec.isUserAuthenticationValidWhileOnBody(), localKeyGenParameterSpec.isInvalidatedByBiometricEnrollment());
    KeymasterUtils.addMinMacLengthAuthorizationIfNecessary((KeymasterArguments)localObject, this.mKeymasterAlgorithm, this.mKeymasterBlockModes, this.mKeymasterDigests);
    ((KeymasterArguments)localObject).addDateIfNotNull(1610613136, localKeyGenParameterSpec.getKeyValidityStart());
    ((KeymasterArguments)localObject).addDateIfNotNull(1610613137, localKeyGenParameterSpec.getKeyValidityForOriginationEnd());
    ((KeymasterArguments)localObject).addDateIfNotNull(1610613138, localKeyGenParameterSpec.getKeyValidityForConsumptionEnd());
    if (((localKeyGenParameterSpec.getPurposes() & 0x1) == 0) || (localKeyGenParameterSpec.isRandomizedEncryptionRequired())) {}
    for (;;)
    {
      byte[] arrayOfByte = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.mRng, (this.mKeySizeBits + 7) / 8);
      String str1 = "USRSKEY_" + localKeyGenParameterSpec.getKeystoreAlias();
      KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
      try
      {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, localKeyGenParameterSpec.getKeystoreAlias(), localKeyGenParameterSpec.getUid());
        int i = this.mKeyStore.generateKey(str1, (KeymasterArguments)localObject, arrayOfByte, localKeyGenParameterSpec.getUid(), 0, localKeyCharacteristics);
        if (i == 1) {
          break;
        }
        throw new ProviderException("Keystore operation failed", KeyStore.getKeyStoreException(i));
      }
      finally
      {
        if (0 == 0) {
          Credentials.deleteAllTypesForAlias(this.mKeyStore, localKeyGenParameterSpec.getKeystoreAlias(), localKeyGenParameterSpec.getUid());
        }
      }
      ((KeymasterArguments)localObject).addBoolean(1879048199);
    }
    try
    {
      localObject = KeyProperties.KeyAlgorithm.fromKeymasterSecretKeyAlgorithm(this.mKeymasterAlgorithm, this.mKeymasterDigest);
      AndroidKeyStoreSecretKey localAndroidKeyStoreSecretKey = new AndroidKeyStoreSecretKey(str2, localKeyGenParameterSpec.getUid(), (String)localObject);
      if (1 == 0) {
        Credentials.deleteAllTypesForAlias(this.mKeyStore, localKeyGenParameterSpec.getKeystoreAlias(), localKeyGenParameterSpec.getUid());
      }
      return localAndroidKeyStoreSecretKey;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ProviderException("Failed to obtain JCA secret key algorithm name", localIllegalArgumentException);
    }
  }
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom)
  {
    throw new UnsupportedOperationException("Cannot initialize without a " + KeyGenParameterSpec.class.getName() + " parameter");
  }
  
  protected void engineInit(SecureRandom paramSecureRandom)
  {
    throw new UnsupportedOperationException("Cannot initialize without a " + KeyGenParameterSpec.class.getName() + " parameter");
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    int j = 0;
    resetAll();
    if (paramAlgorithmParameterSpec != null) {
      try
      {
        if ((paramAlgorithmParameterSpec instanceof KeyGenParameterSpec))
        {
          paramAlgorithmParameterSpec = (KeyGenParameterSpec)paramAlgorithmParameterSpec;
          if (paramAlgorithmParameterSpec.getKeystoreAlias() != null) {
            break label88;
          }
          throw new InvalidAlgorithmParameterException("KeyStore entry alias not provided");
        }
      }
      finally
      {
        if (0 == 0) {
          resetAll();
        }
      }
    }
    throw new InvalidAlgorithmParameterException("Cannot initialize without a " + KeyGenParameterSpec.class.getName() + " parameter");
    label88:
    this.mRng = paramSecureRandom;
    this.mSpec = paramAlgorithmParameterSpec;
    if (paramAlgorithmParameterSpec.getKeySize() != -1) {}
    for (int i = paramAlgorithmParameterSpec.getKeySize();; i = this.mDefaultKeySizeBits)
    {
      this.mKeySizeBits = i;
      if (this.mKeySizeBits > 0) {
        break;
      }
      throw new InvalidAlgorithmParameterException("Key size must be positive: " + this.mKeySizeBits);
    }
    if (this.mKeySizeBits % 8 != 0) {
      throw new InvalidAlgorithmParameterException("Key size must be a multiple of 8: " + this.mKeySizeBits);
    }
    try
    {
      this.mKeymasterPurposes = KeyProperties.Purpose.allToKeymaster(paramAlgorithmParameterSpec.getPurposes());
      this.mKeymasterPaddings = KeyProperties.EncryptionPadding.allToKeymaster(paramAlgorithmParameterSpec.getEncryptionPaddings());
      if (paramAlgorithmParameterSpec.getSignaturePaddings().length > 0) {
        throw new InvalidAlgorithmParameterException("Signature paddings not supported for symmetric key algorithms");
      }
    }
    catch (IllegalStateException|IllegalArgumentException paramAlgorithmParameterSpec)
    {
      throw new InvalidAlgorithmParameterException(paramAlgorithmParameterSpec);
    }
    this.mKeymasterBlockModes = KeyProperties.BlockMode.allToKeymaster(paramAlgorithmParameterSpec.getBlockModes());
    int k;
    if (((paramAlgorithmParameterSpec.getPurposes() & 0x1) != 0) && (paramAlgorithmParameterSpec.isRandomizedEncryptionRequired()))
    {
      paramSecureRandom = this.mKeymasterBlockModes;
      k = paramSecureRandom.length;
      i = j;
    }
    for (;;)
    {
      if (i < k)
      {
        j = paramSecureRandom[i];
        if (!KeymasterUtils.isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(j)) {
          throw new InvalidAlgorithmParameterException("Randomized encryption (IND-CPA) required but may be violated by block mode: " + KeyProperties.BlockMode.fromKeymaster(j) + ". See " + KeyGenParameterSpec.class.getName() + " documentation.");
        }
      }
      else
      {
        if (this.mKeymasterAlgorithm == 128)
        {
          this.mKeymasterDigests = new int[] { this.mKeymasterDigest };
          if (paramAlgorithmParameterSpec.isDigestsSpecified())
          {
            paramSecureRandom = KeyProperties.Digest.allToKeymaster(paramAlgorithmParameterSpec.getDigests());
            if ((paramSecureRandom.length != 1) || (paramSecureRandom[0] != this.mKeymasterDigest)) {
              throw new InvalidAlgorithmParameterException("Unsupported digests specification: " + Arrays.asList(paramAlgorithmParameterSpec.getDigests()) + ". Only " + KeyProperties.Digest.fromKeymaster(this.mKeymasterDigest) + " supported for this HMAC key algorithm");
            }
          }
        }
        else
        {
          if (!paramAlgorithmParameterSpec.isDigestsSpecified()) {
            break label528;
          }
        }
        label528:
        for (this.mKeymasterDigests = KeyProperties.Digest.allToKeymaster(paramAlgorithmParameterSpec.getDigests());; this.mKeymasterDigests = EmptyArray.INT)
        {
          KeymasterUtils.addUserAuthArgs(new KeymasterArguments(), paramAlgorithmParameterSpec.isUserAuthenticationRequired(), paramAlgorithmParameterSpec.getUserAuthenticationValidityDurationSeconds(), paramAlgorithmParameterSpec.isUserAuthenticationValidWhileOnBody(), paramAlgorithmParameterSpec.isInvalidatedByBiometricEnrollment());
          if (1 == 0) {
            resetAll();
          }
          return;
        }
      }
      i += 1;
    }
  }
  
  public static class AES
    extends AndroidKeyStoreKeyGeneratorSpi
  {
    public AES()
    {
      super(128);
    }
    
    protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
      throws InvalidAlgorithmParameterException
    {
      super.engineInit(paramAlgorithmParameterSpec, paramSecureRandom);
      if ((this.mKeySizeBits != 128) && (this.mKeySizeBits != 192) && (this.mKeySizeBits != 256)) {
        throw new InvalidAlgorithmParameterException("Unsupported key size: " + this.mKeySizeBits + ". Supported: 128, 192, 256.");
      }
    }
  }
  
  protected static abstract class HmacBase
    extends AndroidKeyStoreKeyGeneratorSpi
  {
    protected HmacBase(int paramInt)
    {
      super(paramInt, KeymasterUtils.getDigestOutputSizeBits(paramInt));
    }
  }
  
  public static class HmacSHA1
    extends AndroidKeyStoreKeyGeneratorSpi.HmacBase
  {
    public HmacSHA1()
    {
      super();
    }
  }
  
  public static class HmacSHA224
    extends AndroidKeyStoreKeyGeneratorSpi.HmacBase
  {
    public HmacSHA224()
    {
      super();
    }
  }
  
  public static class HmacSHA256
    extends AndroidKeyStoreKeyGeneratorSpi.HmacBase
  {
    public HmacSHA256()
    {
      super();
    }
  }
  
  public static class HmacSHA384
    extends AndroidKeyStoreKeyGeneratorSpi.HmacBase
  {
    public HmacSHA384()
    {
      super();
    }
  }
  
  public static class HmacSHA512
    extends AndroidKeyStoreKeyGeneratorSpi.HmacBase
  {
    public HmacSHA512()
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreKeyGeneratorSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */