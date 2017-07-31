package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.OperationResult;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.MacSpi;

public abstract class AndroidKeyStoreHmacSpi
  extends MacSpi
  implements KeyStoreCryptoOperation
{
  private KeyStoreCryptoOperationChunkedStreamer mChunkedStreamer;
  private AndroidKeyStoreSecretKey mKey;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private final int mKeymasterDigest;
  private final int mMacSizeBits;
  private long mOperationHandle;
  private IBinder mOperationToken;
  
  protected AndroidKeyStoreHmacSpi(int paramInt)
  {
    this.mKeymasterDigest = paramInt;
    this.mMacSizeBits = KeymasterUtils.getDigestOutputSizeBits(paramInt);
  }
  
  private void ensureKeystoreOperationInitialized()
    throws InvalidKeyException
  {
    if (this.mChunkedStreamer != null) {
      return;
    }
    if (this.mKey == null) {
      throw new IllegalStateException("Not initialized");
    }
    Object localObject = new KeymasterArguments();
    ((KeymasterArguments)localObject).addEnum(268435458, 128);
    ((KeymasterArguments)localObject).addEnum(536870917, this.mKeymasterDigest);
    ((KeymasterArguments)localObject).addUnsignedInt(805307371, this.mMacSizeBits);
    localObject = this.mKeyStore.begin(this.mKey.getAlias(), 2, true, (KeymasterArguments)localObject, null, this.mKey.getUid());
    if (localObject == null) {
      throw new KeyStoreConnectException();
    }
    this.mOperationToken = ((OperationResult)localObject).token;
    this.mOperationHandle = ((OperationResult)localObject).operationHandle;
    localObject = KeyStoreCryptoOperationUtils.getInvalidKeyExceptionForInit(this.mKeyStore, this.mKey, ((OperationResult)localObject).resultCode);
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
    if (this.mOperationToken == null) {
      throw new ProviderException("Keystore returned null operation token");
    }
    if (this.mOperationHandle == 0L) {
      throw new ProviderException("Keystore returned invalid operation handle");
    }
    this.mChunkedStreamer = new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(this.mKeyStore, this.mOperationToken));
  }
  
  private void init(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidKeyException, InvalidAlgorithmParameterException
  {
    if (paramKey == null) {
      throw new InvalidKeyException("key == null");
    }
    if (!(paramKey instanceof AndroidKeyStoreSecretKey)) {
      throw new InvalidKeyException("Only Android KeyStore secret keys supported. Key: " + paramKey);
    }
    this.mKey = paramKey;
    if (paramAlgorithmParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("Unsupported algorithm parameters: " + paramAlgorithmParameterSpec);
    }
  }
  
  private void resetAll()
  {
    this.mKey = null;
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null) {
      this.mKeyStore.abort(localIBinder);
    }
    this.mOperationToken = null;
    this.mOperationHandle = 0L;
    this.mChunkedStreamer = null;
  }
  
  private void resetWhilePreservingInitState()
  {
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null) {
      this.mKeyStore.abort(localIBinder);
    }
    this.mOperationToken = null;
    this.mOperationHandle = 0L;
    this.mChunkedStreamer = null;
  }
  
  /* Error */
  protected byte[] engineDoFinal()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 181	android/security/keystore/AndroidKeyStoreHmacSpi:ensureKeystoreOperationInitialized	()V
    //   4: aload_0
    //   5: getfield 63	android/security/keystore/AndroidKeyStoreHmacSpi:mChunkedStreamer	Landroid/security/keystore/KeyStoreCryptoOperationChunkedStreamer;
    //   8: aconst_null
    //   9: iconst_0
    //   10: iconst_0
    //   11: aconst_null
    //   12: aconst_null
    //   13: invokevirtual 185	android/security/keystore/KeyStoreCryptoOperationChunkedStreamer:doFinal	([BII[B[B)[B
    //   16: astore_1
    //   17: aload_0
    //   18: invokespecial 187	android/security/keystore/AndroidKeyStoreHmacSpi:resetWhilePreservingInitState	()V
    //   21: aload_1
    //   22: areturn
    //   23: astore_1
    //   24: new 126	java/security/ProviderException
    //   27: dup
    //   28: ldc -67
    //   30: aload_1
    //   31: invokespecial 192	java/security/ProviderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   34: athrow
    //   35: astore_1
    //   36: new 126	java/security/ProviderException
    //   39: dup
    //   40: ldc -62
    //   42: aload_1
    //   43: invokespecial 192	java/security/ProviderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	AndroidKeyStoreHmacSpi
    //   16	6	1	arrayOfByte	byte[]
    //   23	8	1	localInvalidKeyException	InvalidKeyException
    //   35	8	1	localKeyStoreException	KeyStoreException
    // Exception table:
    //   from	to	target	type
    //   0	4	23	java/security/InvalidKeyException
    //   4	17	35	android/security/KeyStoreException
  }
  
  protected int engineGetMacLength()
  {
    return (this.mMacSizeBits + 7) / 8;
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidKeyException, InvalidAlgorithmParameterException
  {
    resetAll();
    try
    {
      init(paramKey, paramAlgorithmParameterSpec);
      ensureKeystoreOperationInitialized();
      if (1 == 0) {
        resetAll();
      }
      return;
    }
    finally
    {
      if (0 == 0) {
        resetAll();
      }
    }
  }
  
  protected void engineReset()
  {
    resetWhilePreservingInitState();
  }
  
  protected void engineUpdate(byte paramByte)
  {
    engineUpdate(new byte[] { paramByte }, 0, 1);
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      ensureKeystoreOperationInitialized();
      return;
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      try
      {
        paramArrayOfByte = this.mChunkedStreamer.update(paramArrayOfByte, paramInt1, paramInt2);
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
          return;
        }
        throw new ProviderException("Update operation unexpectedly produced output");
      }
      catch (KeyStoreException paramArrayOfByte)
      {
        throw new ProviderException("Keystore operation failed", paramArrayOfByte);
      }
      paramArrayOfByte = paramArrayOfByte;
      throw new ProviderException("Failed to reinitialize MAC", paramArrayOfByte);
    }
  }
  
  public void finalize()
    throws Throwable
  {
    try
    {
      IBinder localIBinder = this.mOperationToken;
      if (localIBinder != null) {
        this.mKeyStore.abort(localIBinder);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public long getOperationHandle()
  {
    return this.mOperationHandle;
  }
  
  public static class HmacSHA1
    extends AndroidKeyStoreHmacSpi
  {
    public HmacSHA1()
    {
      super();
    }
  }
  
  public static class HmacSHA224
    extends AndroidKeyStoreHmacSpi
  {
    public HmacSHA224()
    {
      super();
    }
  }
  
  public static class HmacSHA256
    extends AndroidKeyStoreHmacSpi
  {
    public HmacSHA256()
    {
      super();
    }
  }
  
  public static class HmacSHA384
    extends AndroidKeyStoreHmacSpi
  {
    public HmacSHA384()
    {
      super();
    }
  }
  
  public static class HmacSHA512
    extends AndroidKeyStoreHmacSpi
  {
    public HmacSHA512()
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreHmacSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */