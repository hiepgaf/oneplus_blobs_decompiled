package android.security.keystore;

import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;

abstract class AndroidKeyStoreRSACipherSpi
  extends AndroidKeyStoreCipherSpiBase
{
  private final int mKeymasterPadding;
  private int mKeymasterPaddingOverride;
  private int mModulusSizeBytes = -1;
  
  AndroidKeyStoreRSACipherSpi(int paramInt)
  {
    this.mKeymasterPadding = paramInt;
  }
  
  protected void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
  {
    paramKeymasterArguments.addEnum(268435458, 1);
    int j = getKeymasterPaddingOverride();
    int i = j;
    if (j == -1) {
      i = this.mKeymasterPadding;
    }
    paramKeymasterArguments.addEnum(536870918, i);
    i = getKeymasterPurposeOverride();
    if ((i != -1) && ((i == 2) || (i == 3))) {
      paramKeymasterArguments.addEnum(536870917, 0);
    }
  }
  
  protected boolean adjustConfigForEncryptingWithPrivateKey()
  {
    return false;
  }
  
  protected final int engineGetBlockSize()
  {
    return 0;
  }
  
  protected final byte[] engineGetIV()
  {
    return null;
  }
  
  protected final int engineGetOutputSize(int paramInt)
  {
    return getModulusSizeBytes();
  }
  
  protected final int getKeymasterPaddingOverride()
  {
    return this.mKeymasterPaddingOverride;
  }
  
  protected final int getModulusSizeBytes()
  {
    if (this.mModulusSizeBytes == -1) {
      throw new IllegalStateException("Not initialized");
    }
    return this.mModulusSizeBytes;
  }
  
  protected final void initKey(int paramInt, Key paramKey)
    throws InvalidKeyException
  {
    if (paramKey == null) {
      throw new InvalidKeyException("Unsupported key: null");
    }
    if (!"RSA".equalsIgnoreCase(paramKey.getAlgorithm())) {
      throw new InvalidKeyException("Unsupported key algorithm: " + paramKey.getAlgorithm() + ". Only " + "RSA" + " supported");
    }
    if ((paramKey instanceof AndroidKeyStorePrivateKey)) {}
    while ((paramKey instanceof PrivateKey)) {
      switch (paramInt)
      {
      default: 
        throw new InvalidKeyException("RSA private keys cannot be used with opmode: " + paramInt);
        if (!(paramKey instanceof AndroidKeyStorePublicKey)) {
          throw new InvalidKeyException("Unsupported key type: " + paramKey);
        }
        break;
      case 1: 
      case 3: 
        if (adjustConfigForEncryptingWithPrivateKey()) {
          break label352;
        }
        throw new InvalidKeyException("RSA private keys cannot be used with " + opmodeToString(paramInt) + " and padding " + KeyProperties.EncryptionPadding.fromKeymaster(this.mKeymasterPadding) + ". Only RSA public keys supported for this mode");
      }
    }
    switch (paramInt)
    {
    default: 
      throw new InvalidKeyException("RSA public keys cannot be used with " + opmodeToString(paramInt));
    case 2: 
    case 4: 
      throw new InvalidKeyException("RSA public keys cannot be used with " + opmodeToString(paramInt) + " and padding " + KeyProperties.EncryptionPadding.fromKeymaster(this.mKeymasterPadding) + ". Only RSA private keys supported for this opmode.");
    }
    label352:
    KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
    paramInt = getKeyStore().getKeyCharacteristics(paramKey.getAlias(), null, null, paramKey.getUid(), localKeyCharacteristics);
    if (paramInt != 1) {
      throw getKeyStore().getInvalidKeyException(paramKey.getAlias(), paramKey.getUid(), paramInt);
    }
    long l = localKeyCharacteristics.getUnsignedInt(805306371, -1L);
    if (l == -1L) {
      throw new InvalidKeyException("Size of key not known");
    }
    if (l > 2147483647L) {
      throw new InvalidKeyException("Key too large: " + l + " bits");
    }
    this.mModulusSizeBytes = ((int)((7L + l) / 8L));
    setKey(paramKey);
  }
  
  protected void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments paramKeymasterArguments) {}
  
  protected final void resetAll()
  {
    this.mModulusSizeBytes = -1;
    this.mKeymasterPaddingOverride = -1;
    super.resetAll();
  }
  
  protected final void resetWhilePreservingInitState()
  {
    super.resetWhilePreservingInitState();
  }
  
  protected final void setKeymasterPaddingOverride(int paramInt)
  {
    this.mKeymasterPaddingOverride = paramInt;
  }
  
  public static final class NoPadding
    extends AndroidKeyStoreRSACipherSpi
  {
    public NoPadding()
    {
      super();
    }
    
    protected boolean adjustConfigForEncryptingWithPrivateKey()
    {
      setKeymasterPurposeOverride(2);
      return true;
    }
    
    protected AlgorithmParameters engineGetParameters()
    {
      return null;
    }
    
    protected final int getAdditionalEntropyAmountForBegin()
    {
      return 0;
    }
    
    protected final int getAdditionalEntropyAmountForFinish()
    {
      return 0;
    }
    
    protected void initAlgorithmSpecificParameters()
      throws InvalidKeyException
    {}
    
    protected void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameters != null) {
        throw new InvalidAlgorithmParameterException("Unexpected parameters: " + paramAlgorithmParameters + ". No parameters supported");
      }
    }
    
    protected void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameterSpec != null) {
        throw new InvalidAlgorithmParameterException("Unexpected parameters: " + paramAlgorithmParameterSpec + ". No parameters supported");
      }
    }
  }
  
  static abstract class OAEPWithMGF1Padding
    extends AndroidKeyStoreRSACipherSpi
  {
    private static final String MGF_ALGORITGM_MGF1 = "MGF1";
    private int mDigestOutputSizeBytes;
    private int mKeymasterDigest = -1;
    
    OAEPWithMGF1Padding(int paramInt)
    {
      super();
      this.mKeymasterDigest = paramInt;
      this.mDigestOutputSizeBytes = ((KeymasterUtils.getDigestOutputSizeBits(paramInt) + 7) / 8);
    }
    
    protected final void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
    {
      super.addAlgorithmSpecificParametersToBegin(paramKeymasterArguments);
      paramKeymasterArguments.addEnum(536870917, this.mKeymasterDigest);
    }
    
    protected final AlgorithmParameters engineGetParameters()
    {
      OAEPParameterSpec localOAEPParameterSpec = new OAEPParameterSpec(KeyProperties.Digest.fromKeymaster(this.mKeymasterDigest), "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
      try
      {
        AlgorithmParameters localAlgorithmParameters = AlgorithmParameters.getInstance("OAEP");
        localAlgorithmParameters.init(localOAEPParameterSpec);
        return localAlgorithmParameters;
      }
      catch (InvalidParameterSpecException localInvalidParameterSpecException)
      {
        throw new ProviderException("Failed to initialize OAEP AlgorithmParameters with an IV", localInvalidParameterSpecException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new ProviderException("Failed to obtain OAEP AlgorithmParameters", localNoSuchAlgorithmException);
      }
    }
    
    protected final int getAdditionalEntropyAmountForBegin()
    {
      return 0;
    }
    
    protected final int getAdditionalEntropyAmountForFinish()
    {
      if (isEncrypting()) {
        return this.mDigestOutputSizeBytes;
      }
      return 0;
    }
    
    protected final void initAlgorithmSpecificParameters()
      throws InvalidKeyException
    {}
    
    protected final void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameters == null) {
        return;
      }
      try
      {
        OAEPParameterSpec localOAEPParameterSpec = (OAEPParameterSpec)paramAlgorithmParameters.getParameterSpec(OAEPParameterSpec.class);
        if (localOAEPParameterSpec == null) {
          throw new InvalidAlgorithmParameterException("OAEP parameters required, but not provided in parameters: " + paramAlgorithmParameters);
        }
      }
      catch (InvalidParameterSpecException localInvalidParameterSpecException)
      {
        throw new InvalidAlgorithmParameterException("OAEP parameters required, but not found in parameters: " + paramAlgorithmParameters, localInvalidParameterSpecException);
      }
      initAlgorithmSpecificParameters(localInvalidParameterSpecException);
    }
    
    protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameterSpec == null) {
        return;
      }
      if (!(paramAlgorithmParameterSpec instanceof OAEPParameterSpec)) {
        throw new InvalidAlgorithmParameterException("Unsupported parameter spec: " + paramAlgorithmParameterSpec + ". Only OAEPParameterSpec supported");
      }
      if (!"MGF1".equalsIgnoreCase(paramAlgorithmParameterSpec.getMGFAlgorithm())) {
        throw new InvalidAlgorithmParameterException("Unsupported MGF: " + paramAlgorithmParameterSpec.getMGFAlgorithm() + ". Only " + "MGF1" + " supported");
      }
      Object localObject = paramAlgorithmParameterSpec.getDigestAlgorithm();
      int i;
      try
      {
        i = KeyProperties.Digest.toKeymaster((String)localObject);
        switch (i)
        {
        default: 
          throw new InvalidAlgorithmParameterException("Unsupported digest: " + (String)localObject);
        }
      }
      catch (IllegalArgumentException paramAlgorithmParameterSpec)
      {
        throw new InvalidAlgorithmParameterException("Unsupported digest: " + (String)localObject, paramAlgorithmParameterSpec);
      }
      localObject = paramAlgorithmParameterSpec.getMGFParameters();
      if (localObject == null) {
        throw new InvalidAlgorithmParameterException("MGF parameters must be provided");
      }
      if (!(localObject instanceof MGF1ParameterSpec)) {
        throw new InvalidAlgorithmParameterException("Unsupported MGF parameters: " + localObject + ". Only MGF1ParameterSpec supported");
      }
      localObject = ((MGF1ParameterSpec)localObject).getDigestAlgorithm();
      if (!"SHA-1".equalsIgnoreCase((String)localObject)) {
        throw new InvalidAlgorithmParameterException("Unsupported MGF1 digest: " + (String)localObject + ". Only " + "SHA-1" + " supported");
      }
      paramAlgorithmParameterSpec = paramAlgorithmParameterSpec.getPSource();
      if (!(paramAlgorithmParameterSpec instanceof PSource.PSpecified)) {
        throw new InvalidAlgorithmParameterException("Unsupported source of encoding input P: " + paramAlgorithmParameterSpec + ". Only pSpecifiedEmpty (PSource.PSpecified.DEFAULT) supported");
      }
      localObject = paramAlgorithmParameterSpec.getValue();
      if ((localObject != null) && (localObject.length > 0)) {
        throw new InvalidAlgorithmParameterException("Unsupported source of encoding input P: " + paramAlgorithmParameterSpec + ". Only pSpecifiedEmpty (PSource.PSpecified.DEFAULT) supported");
      }
      this.mKeymasterDigest = i;
      this.mDigestOutputSizeBytes = ((KeymasterUtils.getDigestOutputSizeBits(i) + 7) / 8);
    }
    
    protected final void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments paramKeymasterArguments)
    {
      super.loadAlgorithmSpecificParametersFromBeginResult(paramKeymasterArguments);
    }
  }
  
  public static class OAEPWithSHA1AndMGF1Padding
    extends AndroidKeyStoreRSACipherSpi.OAEPWithMGF1Padding
  {
    public OAEPWithSHA1AndMGF1Padding()
    {
      super();
    }
  }
  
  public static class OAEPWithSHA224AndMGF1Padding
    extends AndroidKeyStoreRSACipherSpi.OAEPWithMGF1Padding
  {
    public OAEPWithSHA224AndMGF1Padding()
    {
      super();
    }
  }
  
  public static class OAEPWithSHA256AndMGF1Padding
    extends AndroidKeyStoreRSACipherSpi.OAEPWithMGF1Padding
  {
    public OAEPWithSHA256AndMGF1Padding()
    {
      super();
    }
  }
  
  public static class OAEPWithSHA384AndMGF1Padding
    extends AndroidKeyStoreRSACipherSpi.OAEPWithMGF1Padding
  {
    public OAEPWithSHA384AndMGF1Padding()
    {
      super();
    }
  }
  
  public static class OAEPWithSHA512AndMGF1Padding
    extends AndroidKeyStoreRSACipherSpi.OAEPWithMGF1Padding
  {
    public OAEPWithSHA512AndMGF1Padding()
    {
      super();
    }
  }
  
  public static final class PKCS1Padding
    extends AndroidKeyStoreRSACipherSpi
  {
    public PKCS1Padding()
    {
      super();
    }
    
    protected boolean adjustConfigForEncryptingWithPrivateKey()
    {
      setKeymasterPurposeOverride(2);
      setKeymasterPaddingOverride(5);
      return true;
    }
    
    protected AlgorithmParameters engineGetParameters()
    {
      return null;
    }
    
    protected final int getAdditionalEntropyAmountForBegin()
    {
      return 0;
    }
    
    protected final int getAdditionalEntropyAmountForFinish()
    {
      if (isEncrypting()) {
        return getModulusSizeBytes();
      }
      return 0;
    }
    
    protected void initAlgorithmSpecificParameters()
      throws InvalidKeyException
    {}
    
    protected void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameters != null) {
        throw new InvalidAlgorithmParameterException("Unexpected parameters: " + paramAlgorithmParameters + ". No parameters supported");
      }
    }
    
    protected void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameterSpec != null) {
        throw new InvalidAlgorithmParameterException("Unexpected parameters: " + paramAlgorithmParameterSpec + ". No parameters supported");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreRSACipherSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */