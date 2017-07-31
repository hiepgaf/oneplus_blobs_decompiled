package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.OperationResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.EmptyArray;

abstract class AndroidKeyStoreAuthenticatedAESCipherSpi
  extends AndroidKeyStoreCipherSpiBase
{
  private static final int BLOCK_SIZE_BYTES = 16;
  private byte[] mIv;
  private boolean mIvHasBeenUsed;
  private final int mKeymasterBlockMode;
  private final int mKeymasterPadding;
  
  AndroidKeyStoreAuthenticatedAESCipherSpi(int paramInt1, int paramInt2)
  {
    this.mKeymasterBlockMode = paramInt1;
    this.mKeymasterPadding = paramInt2;
  }
  
  protected void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
  {
    if ((isEncrypting()) && (this.mIvHasBeenUsed)) {
      throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
    }
    paramKeymasterArguments.addEnum(268435458, 32);
    paramKeymasterArguments.addEnum(536870916, this.mKeymasterBlockMode);
    paramKeymasterArguments.addEnum(536870918, this.mKeymasterPadding);
    if (this.mIv != null) {
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
  
  protected byte[] getIv()
  {
    return this.mIv;
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
    if (this.mIv == null) {
      this.mIv = paramKeymasterArguments;
    }
    while ((paramKeymasterArguments == null) || (Arrays.equals(paramKeymasterArguments, this.mIv))) {
      return;
    }
    throw new ProviderException("IV in use differs from provided IV");
  }
  
  protected void resetAll()
  {
    this.mIv = null;
    this.mIvHasBeenUsed = false;
    super.resetAll();
  }
  
  protected void setIv(byte[] paramArrayOfByte)
  {
    this.mIv = paramArrayOfByte;
  }
  
  private static class AdditionalAuthenticationDataStream
    implements KeyStoreCryptoOperationChunkedStreamer.Stream
  {
    private final KeyStore mKeyStore;
    private final IBinder mOperationToken;
    
    private AdditionalAuthenticationDataStream(KeyStore paramKeyStore, IBinder paramIBinder)
    {
      this.mKeyStore = paramKeyStore;
      this.mOperationToken = paramIBinder;
    }
    
    public OperationResult finish(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      if ((paramArrayOfByte2 != null) && (paramArrayOfByte2.length > 0)) {
        throw new ProviderException("AAD stream does not support additional entropy");
      }
      return new OperationResult(1, this.mOperationToken, 0L, 0, EmptyArray.BYTE, new KeymasterArguments());
    }
    
    public OperationResult update(byte[] paramArrayOfByte)
    {
      Object localObject = new KeymasterArguments();
      ((KeymasterArguments)localObject).addBytes(-1879047192, paramArrayOfByte);
      localObject = this.mKeyStore.update(this.mOperationToken, (KeymasterArguments)localObject, null);
      if (((OperationResult)localObject).resultCode == 1) {
        return new OperationResult(((OperationResult)localObject).resultCode, ((OperationResult)localObject).token, ((OperationResult)localObject).operationHandle, paramArrayOfByte.length, ((OperationResult)localObject).output, ((OperationResult)localObject).outParams);
      }
      return (OperationResult)localObject;
    }
  }
  
  private static class BufferAllOutputUntilDoFinalStreamer
    implements KeyStoreCryptoOperationStreamer
  {
    private ByteArrayOutputStream mBufferedOutput = new ByteArrayOutputStream();
    private final KeyStoreCryptoOperationStreamer mDelegate;
    private long mProducedOutputSizeBytes;
    
    private BufferAllOutputUntilDoFinalStreamer(KeyStoreCryptoOperationStreamer paramKeyStoreCryptoOperationStreamer)
    {
      this.mDelegate = paramKeyStoreCryptoOperationStreamer;
    }
    
    public byte[] doFinal(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
      throws KeyStoreException
    {
      paramArrayOfByte1 = this.mDelegate.doFinal(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramArrayOfByte3);
      if (paramArrayOfByte1 != null) {}
      try
      {
        this.mBufferedOutput.write(paramArrayOfByte1);
        paramArrayOfByte1 = this.mBufferedOutput.toByteArray();
        this.mBufferedOutput.reset();
        this.mProducedOutputSizeBytes += paramArrayOfByte1.length;
        return paramArrayOfByte1;
      }
      catch (IOException paramArrayOfByte1)
      {
        throw new ProviderException("Failed to buffer output", paramArrayOfByte1);
      }
    }
    
    public long getConsumedInputSizeBytes()
    {
      return this.mDelegate.getConsumedInputSizeBytes();
    }
    
    public long getProducedOutputSizeBytes()
    {
      return this.mProducedOutputSizeBytes;
    }
    
    public byte[] update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws KeyStoreException
    {
      paramArrayOfByte = this.mDelegate.update(paramArrayOfByte, paramInt1, paramInt2);
      if (paramArrayOfByte != null) {}
      try
      {
        this.mBufferedOutput.write(paramArrayOfByte);
        return EmptyArray.BYTE;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new ProviderException("Failed to buffer output", paramArrayOfByte);
      }
    }
  }
  
  static abstract class GCM
    extends AndroidKeyStoreAuthenticatedAESCipherSpi
  {
    private static final int DEFAULT_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int MAX_SUPPORTED_TAG_LENGTH_BITS = 128;
    static final int MIN_SUPPORTED_TAG_LENGTH_BITS = 96;
    private int mTagLengthBits = 128;
    
    GCM(int paramInt)
    {
      super(paramInt);
    }
    
    protected final void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
    {
      super.addAlgorithmSpecificParametersToBegin(paramKeymasterArguments);
      paramKeymasterArguments.addUnsignedInt(805307371, this.mTagLengthBits);
    }
    
    protected final KeyStoreCryptoOperationStreamer createAdditionalAuthenticationDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
    {
      return new KeyStoreCryptoOperationChunkedStreamer(new AndroidKeyStoreAuthenticatedAESCipherSpi.AdditionalAuthenticationDataStream(paramKeyStore, paramIBinder, null));
    }
    
    protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
    {
      paramKeyStore = new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(paramKeyStore, paramIBinder));
      if (isEncrypting()) {
        return paramKeyStore;
      }
      return new AndroidKeyStoreAuthenticatedAESCipherSpi.BufferAllOutputUntilDoFinalStreamer(paramKeyStore, null);
    }
    
    protected final AlgorithmParameters engineGetParameters()
    {
      byte[] arrayOfByte = getIv();
      if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
        try
        {
          AlgorithmParameters localAlgorithmParameters = AlgorithmParameters.getInstance("GCM");
          localAlgorithmParameters.init(new GCMParameterSpec(this.mTagLengthBits, arrayOfByte));
          return localAlgorithmParameters;
        }
        catch (InvalidParameterSpecException localInvalidParameterSpecException)
        {
          throw new ProviderException("Failed to initialize GCM AlgorithmParameters", localInvalidParameterSpecException);
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
          throw new ProviderException("Failed to obtain GCM AlgorithmParameters", localNoSuchAlgorithmException);
        }
      }
      return null;
    }
    
    protected final int getAdditionalEntropyAmountForBegin()
    {
      if ((getIv() == null) && (isEncrypting())) {
        return 12;
      }
      return 0;
    }
    
    protected final int getAdditionalEntropyAmountForFinish()
    {
      return 0;
    }
    
    protected final int getTagLengthBits()
    {
      return this.mTagLengthBits;
    }
    
    protected final void initAlgorithmSpecificParameters()
      throws InvalidKeyException
    {
      if (!isEncrypting()) {
        throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
      }
    }
    
    protected final void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameters == null)
      {
        if (!isEncrypting()) {
          throw new InvalidAlgorithmParameterException("IV required when decrypting. Use GCMParameterSpec or GCM AlgorithmParameters to provide it.");
        }
        return;
      }
      if (!"GCM".equalsIgnoreCase(paramAlgorithmParameters.getAlgorithm())) {
        throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + paramAlgorithmParameters.getAlgorithm() + ". Supported: GCM");
      }
      try
      {
        GCMParameterSpec localGCMParameterSpec = (GCMParameterSpec)paramAlgorithmParameters.getParameterSpec(GCMParameterSpec.class);
        initAlgorithmSpecificParameters(localGCMParameterSpec);
        return;
      }
      catch (InvalidParameterSpecException localInvalidParameterSpecException)
      {
        if (!isEncrypting()) {
          throw new InvalidAlgorithmParameterException("IV and tag length required when decrypting, but not found in parameters: " + paramAlgorithmParameters, localInvalidParameterSpecException);
        }
        setIv(null);
      }
    }
    
    protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      if (paramAlgorithmParameterSpec == null)
      {
        if (!isEncrypting()) {
          throw new InvalidAlgorithmParameterException("GCMParameterSpec must be provided when decrypting");
        }
        return;
      }
      if (!(paramAlgorithmParameterSpec instanceof GCMParameterSpec)) {
        throw new InvalidAlgorithmParameterException("Only GCMParameterSpec supported");
      }
      byte[] arrayOfByte = paramAlgorithmParameterSpec.getIV();
      if (arrayOfByte == null) {
        throw new InvalidAlgorithmParameterException("Null IV in GCMParameterSpec");
      }
      if (arrayOfByte.length != 12) {
        throw new InvalidAlgorithmParameterException("Unsupported IV length: " + arrayOfByte.length + " bytes. Only " + 12 + " bytes long IV supported");
      }
      int i = paramAlgorithmParameterSpec.getTLen();
      if ((i < 96) || (i > 128)) {}
      while (i % 8 != 0) {
        throw new InvalidAlgorithmParameterException("Unsupported tag length: " + i + " bits" + ". Supported lengths: 96, 104, 112, 120, 128");
      }
      setIv(arrayOfByte);
      this.mTagLengthBits = i;
    }
    
    protected final void resetAll()
    {
      this.mTagLengthBits = 128;
      super.resetAll();
    }
    
    protected final void resetWhilePreservingInitState()
    {
      super.resetWhilePreservingInitState();
    }
    
    public static final class NoPadding
      extends AndroidKeyStoreAuthenticatedAESCipherSpi.GCM
    {
      public NoPadding()
      {
        super();
      }
      
      protected final int engineGetOutputSize(int paramInt)
      {
        int i = (getTagLengthBits() + 7) / 8;
        if (isEncrypting()) {}
        for (long l = getConsumedInputSizeBytes() - getProducedOutputSizeBytes() + paramInt + i; l < 0L; l = getConsumedInputSizeBytes() - getProducedOutputSizeBytes() + paramInt - i) {
          return 0;
        }
        if (l > 2147483647L) {
          return Integer.MAX_VALUE;
        }
        return (int)l;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreAuthenticatedAESCipherSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */