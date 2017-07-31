package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.OperationResult;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import libcore.util.EmptyArray;

abstract class AndroidKeyStoreSignatureSpiBase
  extends SignatureSpi
  implements KeyStoreCryptoOperation
{
  private Exception mCachedException;
  private AndroidKeyStoreKey mKey;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private KeyStoreCryptoOperationStreamer mMessageStreamer;
  private long mOperationHandle;
  private IBinder mOperationToken;
  private boolean mSigning;
  
  private void ensureKeystoreOperationInitialized()
    throws InvalidKeyException
  {
    if (this.mMessageStreamer != null) {
      return;
    }
    if (this.mCachedException != null) {
      return;
    }
    if (this.mKey == null) {
      throw new IllegalStateException("Not initialized");
    }
    Object localObject1 = new KeymasterArguments();
    addAlgorithmSpecificParametersToBegin((KeymasterArguments)localObject1);
    Object localObject2 = this.mKeyStore;
    String str = this.mKey.getAlias();
    if (this.mSigning) {}
    for (int i = 2;; i = 3)
    {
      localObject1 = ((KeyStore)localObject2).begin(str, i, true, (KeymasterArguments)localObject1, null, this.mKey.getUid());
      if (localObject1 != null) {
        break;
      }
      throw new KeyStoreConnectException();
    }
    this.mOperationToken = ((OperationResult)localObject1).token;
    this.mOperationHandle = ((OperationResult)localObject1).operationHandle;
    localObject2 = KeyStoreCryptoOperationUtils.getInvalidKeyExceptionForInit(this.mKeyStore, this.mKey, ((OperationResult)localObject1).resultCode);
    if (localObject2 != null) {
      throw ((Throwable)localObject2);
    }
    if (this.mOperationToken == null) {
      throw new ProviderException("Keystore returned null operation token");
    }
    if (this.mOperationHandle == 0L) {
      throw new ProviderException("Keystore returned invalid operation handle");
    }
    this.mMessageStreamer = createMainDataStreamer(this.mKeyStore, ((OperationResult)localObject1).token);
  }
  
  protected abstract void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments);
  
  protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
  {
    return new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(paramKeyStore, paramIBinder));
  }
  
  @Deprecated
  protected final Object engineGetParameter(String paramString)
    throws InvalidParameterException
  {
    throw new InvalidParameterException();
  }
  
  protected final void engineInitSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException
  {
    engineInitSign(paramPrivateKey, null);
  }
  
  protected final void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    resetAll();
    if (paramPrivateKey == null) {
      try
      {
        throw new InvalidKeyException("Unsupported key: null");
      }
      finally
      {
        if (0 == 0) {
          resetAll();
        }
      }
    }
    if ((paramPrivateKey instanceof AndroidKeyStorePrivateKey))
    {
      this.mSigning = true;
      initKey(paramPrivateKey);
      this.appRandom = paramSecureRandom;
      ensureKeystoreOperationInitialized();
      if (1 == 0) {
        resetAll();
      }
      return;
    }
    throw new InvalidKeyException("Unsupported private key type: " + paramPrivateKey);
  }
  
  protected final void engineInitVerify(PublicKey paramPublicKey)
    throws InvalidKeyException
  {
    resetAll();
    if (paramPublicKey == null) {
      try
      {
        throw new InvalidKeyException("Unsupported key: null");
      }
      finally
      {
        if (0 == 0) {
          resetAll();
        }
      }
    }
    if ((paramPublicKey instanceof AndroidKeyStorePublicKey))
    {
      this.mSigning = false;
      initKey(paramPublicKey);
      this.appRandom = null;
      ensureKeystoreOperationInitialized();
      if (1 == 0) {
        resetAll();
      }
      return;
    }
    throw new InvalidKeyException("Unsupported public key type: " + paramPublicKey);
  }
  
  @Deprecated
  protected final void engineSetParameter(String paramString, Object paramObject)
    throws InvalidParameterException
  {
    throw new InvalidParameterException();
  }
  
  protected final int engineSign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    return super.engineSign(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  protected final byte[] engineSign()
    throws SignatureException
  {
    if (this.mCachedException != null) {
      throw new SignatureException(this.mCachedException);
    }
    try
    {
      ensureKeystoreOperationInitialized();
      byte[] arrayOfByte = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.appRandom, getAdditionalEntropyAmountForSign());
      arrayOfByte = this.mMessageStreamer.doFinal(EmptyArray.BYTE, 0, 0, null, arrayOfByte);
      resetWhilePreservingInitState();
      return arrayOfByte;
    }
    catch (InvalidKeyException|KeyStoreException localInvalidKeyException)
    {
      throw new SignatureException(localInvalidKeyException);
    }
  }
  
  protected final void engineUpdate(byte paramByte)
    throws SignatureException
  {
    engineUpdate(new byte[] { paramByte }, 0, 1);
  }
  
  protected final void engineUpdate(ByteBuffer paramByteBuffer)
  {
    int j = paramByteBuffer.remaining();
    byte[] arrayOfByte;
    int i;
    if (paramByteBuffer.hasArray())
    {
      arrayOfByte = paramByteBuffer.array();
      i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      paramByteBuffer.position(paramByteBuffer.limit());
    }
    for (paramByteBuffer = arrayOfByte;; paramByteBuffer = arrayOfByte)
    {
      try
      {
        engineUpdate(paramByteBuffer, i, j);
        return;
      }
      catch (SignatureException paramByteBuffer)
      {
        this.mCachedException = paramByteBuffer;
      }
      arrayOfByte = new byte[j];
      i = 0;
      paramByteBuffer.get(arrayOfByte);
    }
  }
  
  protected final void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    if (this.mCachedException != null) {
      throw new SignatureException(this.mCachedException);
    }
    try
    {
      ensureKeystoreOperationInitialized();
      if (paramInt2 == 0) {
        return;
      }
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      throw new SignatureException(paramArrayOfByte);
    }
    try
    {
      paramArrayOfByte = this.mMessageStreamer.update(paramArrayOfByte, paramInt1, paramInt2);
      if (paramArrayOfByte.length != 0) {
        throw new ProviderException("Update operation unexpectedly produced output: " + paramArrayOfByte.length + " bytes");
      }
    }
    catch (KeyStoreException paramArrayOfByte)
    {
      throw new SignatureException(paramArrayOfByte);
    }
  }
  
  protected final boolean engineVerify(byte[] paramArrayOfByte)
    throws SignatureException
  {
    if (this.mCachedException != null) {
      throw new SignatureException(this.mCachedException);
    }
    try
    {
      ensureKeystoreOperationInitialized();
      try
      {
        paramArrayOfByte = this.mMessageStreamer.doFinal(EmptyArray.BYTE, 0, 0, paramArrayOfByte, null);
        if (paramArrayOfByte.length != 0) {
          throw new ProviderException("Signature verification unexpected produced output: " + paramArrayOfByte.length + " bytes");
        }
      }
      catch (KeyStoreException paramArrayOfByte)
      {
        switch (paramArrayOfByte.getErrorCode())
        {
        default: 
          throw new SignatureException(paramArrayOfByte);
        }
      }
      bool = true;
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      throw new SignatureException(paramArrayOfByte);
    }
    for (;;)
    {
      resetWhilePreservingInitState();
      return bool;
      boolean bool = false;
    }
  }
  
  protected final boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    return engineVerify(ArrayUtils.subarray(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  protected abstract int getAdditionalEntropyAmountForSign();
  
  protected final KeyStore getKeyStore()
  {
    return this.mKeyStore;
  }
  
  public final long getOperationHandle()
  {
    return this.mOperationHandle;
  }
  
  protected void initKey(AndroidKeyStoreKey paramAndroidKeyStoreKey)
    throws InvalidKeyException
  {
    this.mKey = paramAndroidKeyStoreKey;
  }
  
  protected final boolean isSigning()
  {
    return this.mSigning;
  }
  
  protected void resetAll()
  {
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null)
    {
      this.mOperationToken = null;
      this.mKeyStore.abort(localIBinder);
    }
    this.mSigning = false;
    this.mKey = null;
    this.appRandom = null;
    this.mOperationToken = null;
    this.mOperationHandle = 0L;
    this.mMessageStreamer = null;
    this.mCachedException = null;
  }
  
  protected void resetWhilePreservingInitState()
  {
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null)
    {
      this.mOperationToken = null;
      this.mKeyStore.abort(localIBinder);
    }
    this.mOperationHandle = 0L;
    this.mMessageStreamer = null;
    this.mCachedException = null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreSignatureSpiBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */