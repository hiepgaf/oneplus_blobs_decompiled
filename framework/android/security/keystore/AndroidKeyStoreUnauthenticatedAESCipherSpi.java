package android.security.keystore;

import android.security.keymaster.KeymasterArguments;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.spec.IvParameterSpec;

class AndroidKeyStoreUnauthenticatedAESCipherSpi
  extends AndroidKeyStoreCipherSpiBase
{
  private static final int BLOCK_SIZE_BYTES = 16;
  private byte[] mIv;
  private boolean mIvHasBeenUsed;
  private final boolean mIvRequired;
  private final int mKeymasterBlockMode;
  private final int mKeymasterPadding;
  
  AndroidKeyStoreUnauthenticatedAESCipherSpi(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mKeymasterBlockMode = paramInt1;
    this.mKeymasterPadding = paramInt2;
    this.mIvRequired = paramBoolean;
  }
  
  protected final void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
  {
    if ((isEncrypting()) && (this.mIvRequired) && (this.mIvHasBeenUsed)) {
      throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
    }
    paramKeymasterArguments.addEnum(268435458, 32);
    paramKeymasterArguments.addEnum(536870916, this.mKeymasterBlockMode);
    paramKeymasterArguments.addEnum(536870918, this.mKeymasterPadding);
    if ((this.mIvRequired) && (this.mIv != null)) {
      paramKeymasterArguments.addBytes(-1879047191, this.mIv);
    }
  }
  
  protected final int engineGetBlockSize()
  {
    return 16;
  }
  
  protected final byte[] engineGetIV()
  {
    return ArrayUtils.cloneIfNotEmpty(this.mIv);
  }
  
  protected final int engineGetOutputSize(int paramInt)
  {
    return paramInt + 48;
  }
  
  protected final AlgorithmParameters engineGetParameters()
  {
    if (!this.mIvRequired) {
      return null;
    }
    if ((this.mIv != null) && (this.mIv.length > 0)) {
      try
      {
        AlgorithmParameters localAlgorithmParameters = AlgorithmParameters.getInstance("AES");
        localAlgorithmParameters.init(new IvParameterSpec(this.mIv));
        return localAlgorithmParameters;
      }
      catch (InvalidParameterSpecException localInvalidParameterSpecException)
      {
        throw new ProviderException("Failed to initialize AES AlgorithmParameters with an IV", localInvalidParameterSpecException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new ProviderException("Failed to obtain AES AlgorithmParameters", localNoSuchAlgorithmException);
      }
    }
    return null;
  }
  
  protected final int getAdditionalEntropyAmountForBegin()
  {
    if ((this.mIvRequired) && (this.mIv == null) && (isEncrypting())) {
      return 16;
    }
    return 0;
  }
  
  protected final int getAdditionalEntropyAmountForFinish()
  {
    return 0;
  }
  
  protected final void initAlgorithmSpecificParameters()
    throws InvalidKeyException
  {
    if (!this.mIvRequired) {
      return;
    }
    if (!isEncrypting()) {
      throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
    }
  }
  
  protected final void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
    throws InvalidAlgorithmParameterException
  {
    if (!this.mIvRequired)
    {
      if (paramAlgorithmParameters != null) {
        throw new InvalidAlgorithmParameterException("Unsupported parameters: " + paramAlgorithmParameters);
      }
      return;
    }
    if (paramAlgorithmParameters == null)
    {
      if (!isEncrypting()) {
        throw new InvalidAlgorithmParameterException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
      }
      return;
    }
    if (!"AES".equalsIgnoreCase(paramAlgorithmParameters.getAlgorithm())) {
      throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + paramAlgorithmParameters.getAlgorithm() + ". Supported: AES");
    }
    try
    {
      IvParameterSpec localIvParameterSpec = (IvParameterSpec)paramAlgorithmParameters.getParameterSpec(IvParameterSpec.class);
      this.mIv = localIvParameterSpec.getIV();
      if (this.mIv == null) {
        throw new InvalidAlgorithmParameterException("Null IV in AlgorithmParameters");
      }
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException)
    {
      if (!isEncrypting()) {
        throw new InvalidAlgorithmParameterException("IV required when decrypting, but not found in parameters: " + paramAlgorithmParameters, localInvalidParameterSpecException);
      }
      this.mIv = null;
      return;
    }
  }
  
  protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (!this.mIvRequired)
    {
      if (paramAlgorithmParameterSpec != null) {
        throw new InvalidAlgorithmParameterException("Unsupported parameters: " + paramAlgorithmParameterSpec);
      }
      return;
    }
    if (paramAlgorithmParameterSpec == null)
    {
      if (!isEncrypting()) {
        throw new InvalidAlgorithmParameterException("IvParameterSpec must be provided when decrypting");
      }
      return;
    }
    if (!(paramAlgorithmParameterSpec instanceof IvParameterSpec)) {
      throw new InvalidAlgorithmParameterException("Only IvParameterSpec supported");
    }
    this.mIv = paramAlgorithmParameterSpec.getIV();
    if (this.mIv == null) {
      throw new InvalidAlgorithmParameterException("Null IV in IvParameterSpec");
    }
  }
  
  protected final void initKey(int paramInt, Key paramKey)
    throws InvalidKeyException
  {
    if (!(paramKey instanceof AndroidKeyStoreSecretKey))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Unsupported key: ");
      if (paramKey != null) {}
      for (paramKey = paramKey.getClass().getName();; paramKey = "null") {
        throw new InvalidKeyException(paramKey);
      }
    }
    if (!"AES".equalsIgnoreCase(paramKey.getAlgorithm())) {
      throw new InvalidKeyException("Unsupported key algorithm: " + paramKey.getAlgorithm() + ". Only " + "AES" + " supported");
    }
    setKey(paramKey);
  }
  
  protected final void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments paramKeymasterArguments)
  {
    this.mIvHasBeenUsed = true;
    byte[] arrayOfByte = paramKeymasterArguments.getBytes(-1879047191, null);
    paramKeymasterArguments = arrayOfByte;
    if (arrayOfByte != null)
    {
      paramKeymasterArguments = arrayOfByte;
      if (arrayOfByte.length == 0) {
        paramKeymasterArguments = null;
      }
    }
    if (this.mIvRequired) {
      if (this.mIv == null) {
        this.mIv = paramKeymasterArguments;
      }
    }
    while (paramKeymasterArguments == null)
    {
      do
      {
        return;
      } while ((paramKeymasterArguments == null) || (Arrays.equals(paramKeymasterArguments, this.mIv)));
      throw new ProviderException("IV in use differs from provided IV");
    }
    throw new ProviderException("IV in use despite IV not being used by this transformation");
  }
  
  protected final void resetAll()
  {
    this.mIv = null;
    this.mIvHasBeenUsed = false;
    super.resetAll();
  }
  
  protected final void resetWhilePreservingInitState()
  {
    super.resetWhilePreservingInitState();
  }
  
  static abstract class CBC
    extends AndroidKeyStoreUnauthenticatedAESCipherSpi
  {
    protected CBC(int paramInt)
    {
      super(paramInt, true);
    }
    
    public static class NoPadding
      extends AndroidKeyStoreUnauthenticatedAESCipherSpi.CBC
    {
      public NoPadding()
      {
        super();
      }
    }
    
    public static class PKCS7Padding
      extends AndroidKeyStoreUnauthenticatedAESCipherSpi.CBC
    {
      public PKCS7Padding()
      {
        super();
      }
    }
  }
  
  static abstract class CTR
    extends AndroidKeyStoreUnauthenticatedAESCipherSpi
  {
    protected CTR(int paramInt)
    {
      super(paramInt, true);
    }
    
    public static class NoPadding
      extends AndroidKeyStoreUnauthenticatedAESCipherSpi.CTR
    {
      public NoPadding()
      {
        super();
      }
    }
  }
  
  static abstract class ECB
    extends AndroidKeyStoreUnauthenticatedAESCipherSpi
  {
    protected ECB(int paramInt)
    {
      super(paramInt, false);
    }
    
    public static class NoPadding
      extends AndroidKeyStoreUnauthenticatedAESCipherSpi.ECB
    {
      public NoPadding()
      {
        super();
      }
    }
    
    public static class PKCS7Padding
      extends AndroidKeyStoreUnauthenticatedAESCipherSpi.ECB
    {
      public PKCS7Padding()
      {
        super();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreUnauthenticatedAESCipherSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */