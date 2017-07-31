package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.OperationResult;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import libcore.util.EmptyArray;

abstract class AndroidKeyStoreCipherSpiBase
  extends CipherSpi
  implements KeyStoreCryptoOperation
{
  private KeyStoreCryptoOperationStreamer mAdditionalAuthenticationDataStreamer;
  private boolean mAdditionalAuthenticationDataStreamerClosed;
  private Exception mCachedException;
  private boolean mEncrypting;
  private AndroidKeyStoreKey mKey;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private int mKeymasterPurposeOverride = -1;
  private KeyStoreCryptoOperationStreamer mMainDataStreamer;
  private long mOperationHandle;
  private IBinder mOperationToken;
  private SecureRandom mRng;
  
  private void ensureKeystoreOperationInitialized()
    throws InvalidKeyException, InvalidAlgorithmParameterException
  {
    if (this.mMainDataStreamer != null) {
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
    Object localObject2 = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.mRng, getAdditionalEntropyAmountForBegin());
    int i;
    if (this.mKeymasterPurposeOverride != -1) {
      i = this.mKeymasterPurposeOverride;
    }
    for (;;)
    {
      localObject1 = this.mKeyStore.begin(this.mKey.getAlias(), i, true, (KeymasterArguments)localObject1, (byte[])localObject2, this.mKey.getUid());
      if (localObject1 != null) {
        break;
      }
      throw new KeyStoreConnectException();
      if (this.mEncrypting) {
        i = 0;
      } else {
        i = 1;
      }
    }
    this.mOperationToken = ((OperationResult)localObject1).token;
    this.mOperationHandle = ((OperationResult)localObject1).operationHandle;
    localObject2 = KeyStoreCryptoOperationUtils.getExceptionForCipherInit(this.mKeyStore, this.mKey, ((OperationResult)localObject1).resultCode);
    if (localObject2 != null)
    {
      if ((localObject2 instanceof InvalidKeyException)) {
        throw ((Throwable)localObject2);
      }
      if ((localObject2 instanceof InvalidAlgorithmParameterException)) {
        throw ((Throwable)localObject2);
      }
      throw new ProviderException("Unexpected exception type", (Throwable)localObject2);
    }
    if (this.mOperationToken == null) {
      throw new ProviderException("Keystore returned null operation token");
    }
    if (this.mOperationHandle == 0L) {
      throw new ProviderException("Keystore returned invalid operation handle");
    }
    loadAlgorithmSpecificParametersFromBeginResult(((OperationResult)localObject1).outParams);
    this.mMainDataStreamer = createMainDataStreamer(this.mKeyStore, ((OperationResult)localObject1).token);
    this.mAdditionalAuthenticationDataStreamer = createAdditionalAuthenticationDataStreamer(this.mKeyStore, ((OperationResult)localObject1).token);
    this.mAdditionalAuthenticationDataStreamerClosed = false;
  }
  
  private void flushAAD()
    throws KeyStoreException
  {
    if ((this.mAdditionalAuthenticationDataStreamer == null) || (this.mAdditionalAuthenticationDataStreamerClosed)) {}
    for (;;)
    {
      return;
      try
      {
        byte[] arrayOfByte = this.mAdditionalAuthenticationDataStreamer.doFinal(EmptyArray.BYTE, 0, 0, null, null);
        this.mAdditionalAuthenticationDataStreamerClosed = true;
        if ((arrayOfByte == null) || (arrayOfByte.length <= 0)) {
          continue;
        }
        throw new ProviderException("AAD update unexpectedly returned data: " + arrayOfByte.length + " bytes");
      }
      finally
      {
        this.mAdditionalAuthenticationDataStreamerClosed = true;
      }
    }
  }
  
  private void init(int paramInt, Key paramKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    switch (paramInt)
    {
    default: 
      throw new InvalidParameterException("Unsupported opmode: " + paramInt);
    }
    for (this.mEncrypting = true;; this.mEncrypting = false)
    {
      initKey(paramInt, paramKey);
      if (this.mKey != null) {
        break;
      }
      throw new ProviderException("initKey did not initialize the key");
    }
    this.mRng = paramSecureRandom;
  }
  
  static String opmodeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return String.valueOf(paramInt);
    case 1: 
      return "ENCRYPT_MODE";
    case 2: 
      return "DECRYPT_MODE";
    case 3: 
      return "WRAP_MODE";
    }
    return "UNWRAP_MODE";
  }
  
  protected abstract void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments);
  
  protected KeyStoreCryptoOperationStreamer createAdditionalAuthenticationDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
  {
    return null;
  }
  
  protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
  {
    return new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(paramKeyStore, paramIBinder));
  }
  
  protected final int engineDoFinal(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
  {
    i = 0;
    if (paramByteBuffer1 == null) {
      throw new NullPointerException("input == null");
    }
    if (paramByteBuffer2 == null) {
      throw new NullPointerException("output == null");
    }
    j = paramByteBuffer1.remaining();
    byte[] arrayOfByte;
    if (paramByteBuffer1.hasArray())
    {
      arrayOfByte = engineDoFinal(paramByteBuffer1.array(), paramByteBuffer1.arrayOffset() + paramByteBuffer1.position(), j);
      paramByteBuffer1.position(paramByteBuffer1.position() + j);
    }
    for (paramByteBuffer1 = arrayOfByte;; paramByteBuffer1 = engineDoFinal(arrayOfByte, 0, j))
    {
      if (paramByteBuffer1 != null) {
        i = paramByteBuffer1.length;
      }
      if (i > 0) {
        j = paramByteBuffer2.remaining();
      }
      try
      {
        paramByteBuffer2.put(paramByteBuffer1);
        return i;
      }
      catch (BufferOverflowException paramByteBuffer1)
      {
        throw new ShortBufferException("Output buffer too small. Produced: " + i + ", available: " + j);
      }
      arrayOfByte = new byte[j];
      paramByteBuffer1.get(arrayOfByte);
    }
  }
  
  protected final int engineDoFinal(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
    throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
  {
    paramArrayOfByte1 = engineDoFinal(paramArrayOfByte1, paramInt1, paramInt2);
    if (paramArrayOfByte1 == null) {
      return 0;
    }
    paramInt1 = paramArrayOfByte2.length - paramInt3;
    if (paramArrayOfByte1.length > paramInt1) {
      throw new ShortBufferException("Output buffer too short. Produced: " + paramArrayOfByte1.length + ", available: " + paramInt1);
    }
    System.arraycopy(paramArrayOfByte1, 0, paramArrayOfByte2, paramInt3, paramArrayOfByte1.length);
    return paramArrayOfByte1.length;
  }
  
  protected final byte[] engineDoFinal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IllegalBlockSizeException, BadPaddingException
  {
    if (this.mCachedException != null) {
      throw ((IllegalBlockSizeException)new IllegalBlockSizeException().initCause(this.mCachedException));
    }
    try
    {
      ensureKeystoreOperationInitialized();
      byte[] arrayOfByte;
      throw ((IllegalBlockSizeException)new IllegalBlockSizeException().initCause(paramArrayOfByte));
    }
    catch (InvalidKeyException|InvalidAlgorithmParameterException paramArrayOfByte)
    {
      try
      {
        flushAAD();
        arrayOfByte = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.mRng, getAdditionalEntropyAmountForFinish());
        paramArrayOfByte = this.mMainDataStreamer.doFinal(paramArrayOfByte, paramInt1, paramInt2, null, arrayOfByte);
        resetWhilePreservingInitState();
        return paramArrayOfByte;
      }
      catch (KeyStoreException paramArrayOfByte)
      {
        switch (paramArrayOfByte.getErrorCode())
        {
        default: 
          throw ((IllegalBlockSizeException)new IllegalBlockSizeException().initCause(paramArrayOfByte));
        }
      }
      paramArrayOfByte = paramArrayOfByte;
      throw ((IllegalBlockSizeException)new IllegalBlockSizeException().initCause(paramArrayOfByte));
    }
    throw ((BadPaddingException)new BadPaddingException().initCause(paramArrayOfByte));
    throw ((AEADBadTagException)new AEADBadTagException().initCause(paramArrayOfByte));
  }
  
  protected final int engineGetKeySize(Key paramKey)
    throws InvalidKeyException
  {
    throw new UnsupportedOperationException();
  }
  
  protected abstract AlgorithmParameters engineGetParameters();
  
  protected final void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom)
    throws InvalidKeyException, InvalidAlgorithmParameterException
  {
    resetAll();
    try
    {
      init(paramInt, paramKey, paramSecureRandom);
      initAlgorithmSpecificParameters(paramAlgorithmParameters);
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
  
  /* Error */
  protected final void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 317	android/security/keystore/AndroidKeyStoreCipherSpiBase:resetAll	()V
    //   4: aload_0
    //   5: iload_1
    //   6: aload_2
    //   7: aload_3
    //   8: invokespecial 319	android/security/keystore/AndroidKeyStoreCipherSpiBase:init	(ILjava/security/Key;Ljava/security/SecureRandom;)V
    //   11: aload_0
    //   12: invokevirtual 325	android/security/keystore/AndroidKeyStoreCipherSpiBase:initAlgorithmSpecificParameters	()V
    //   15: aload_0
    //   16: invokespecial 290	android/security/keystore/AndroidKeyStoreCipherSpiBase:ensureKeystoreOperationInitialized	()V
    //   19: iconst_1
    //   20: ifne +7 -> 27
    //   23: aload_0
    //   24: invokevirtual 317	android/security/keystore/AndroidKeyStoreCipherSpiBase:resetAll	()V
    //   27: return
    //   28: astore_2
    //   29: new 44	java/security/InvalidKeyException
    //   32: dup
    //   33: aload_2
    //   34: invokespecial 328	java/security/InvalidKeyException:<init>	(Ljava/lang/Throwable;)V
    //   37: athrow
    //   38: astore_2
    //   39: iconst_0
    //   40: ifne +7 -> 47
    //   43: aload_0
    //   44: invokevirtual 317	android/security/keystore/AndroidKeyStoreCipherSpiBase:resetAll	()V
    //   47: aload_2
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	AndroidKeyStoreCipherSpiBase
    //   0	49	1	paramInt	int
    //   0	49	2	paramKey	Key
    //   0	49	3	paramSecureRandom	SecureRandom
    // Exception table:
    //   from	to	target	type
    //   15	19	28	java/security/InvalidAlgorithmParameterException
    //   4	15	38	finally
    //   15	19	38	finally
    //   29	38	38	finally
  }
  
  protected final void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidKeyException, InvalidAlgorithmParameterException
  {
    resetAll();
    try
    {
      init(paramInt, paramKey, paramSecureRandom);
      initAlgorithmSpecificParameters(paramAlgorithmParameterSpec);
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
  
  protected final void engineSetMode(String paramString)
    throws NoSuchAlgorithmException
  {
    throw new UnsupportedOperationException();
  }
  
  protected final void engineSetPadding(String paramString)
    throws NoSuchPaddingException
  {
    throw new UnsupportedOperationException();
  }
  
  protected final Key engineUnwrap(byte[] paramArrayOfByte, String paramString, int paramInt)
    throws InvalidKeyException, NoSuchAlgorithmException
  {
    if (this.mKey == null) {
      throw new IllegalStateException("Not initilized");
    }
    if (isEncrypting()) {
      throw new IllegalStateException("Cipher must be initialized in Cipher.WRAP_MODE to wrap keys");
    }
    if (paramArrayOfByte == null) {
      throw new NullPointerException("wrappedKey == null");
    }
    try
    {
      paramArrayOfByte = engineDoFinal(paramArrayOfByte, 0, paramArrayOfByte.length);
      switch (paramInt)
      {
      default: 
        throw new InvalidParameterException("Unsupported wrappedKeyType: " + paramInt);
      }
    }
    catch (IllegalBlockSizeException|BadPaddingException paramArrayOfByte)
    {
      throw new InvalidKeyException("Failed to unwrap key", paramArrayOfByte);
    }
    return new SecretKeySpec(paramArrayOfByte, paramString);
    paramString = KeyFactory.getInstance(paramString);
    try
    {
      paramArrayOfByte = paramString.generatePrivate(new PKCS8EncodedKeySpec(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (InvalidKeySpecException paramArrayOfByte)
    {
      throw new InvalidKeyException("Failed to create private key from its PKCS#8 encoded form", paramArrayOfByte);
    }
    paramString = KeyFactory.getInstance(paramString);
    try
    {
      paramArrayOfByte = paramString.generatePublic(new X509EncodedKeySpec(paramArrayOfByte));
      return paramArrayOfByte;
    }
    catch (InvalidKeySpecException paramArrayOfByte)
    {
      throw new InvalidKeyException("Failed to create public key from its X.509 encoded form", paramArrayOfByte);
    }
  }
  
  protected final int engineUpdate(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws ShortBufferException
  {
    i = 0;
    if (paramByteBuffer1 == null) {
      throw new NullPointerException("input == null");
    }
    if (paramByteBuffer2 == null) {
      throw new NullPointerException("output == null");
    }
    j = paramByteBuffer1.remaining();
    byte[] arrayOfByte;
    if (paramByteBuffer1.hasArray())
    {
      arrayOfByte = engineUpdate(paramByteBuffer1.array(), paramByteBuffer1.arrayOffset() + paramByteBuffer1.position(), j);
      paramByteBuffer1.position(paramByteBuffer1.position() + j);
    }
    for (paramByteBuffer1 = arrayOfByte;; paramByteBuffer1 = engineUpdate(arrayOfByte, 0, j))
    {
      if (paramByteBuffer1 != null) {
        i = paramByteBuffer1.length;
      }
      if (i > 0) {
        j = paramByteBuffer2.remaining();
      }
      try
      {
        paramByteBuffer2.put(paramByteBuffer1);
        return i;
      }
      catch (BufferOverflowException paramByteBuffer1)
      {
        throw new ShortBufferException("Output buffer too small. Produced: " + i + ", available: " + j);
      }
      arrayOfByte = new byte[j];
      paramByteBuffer1.get(arrayOfByte);
    }
  }
  
  protected final int engineUpdate(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
    throws ShortBufferException
  {
    paramArrayOfByte1 = engineUpdate(paramArrayOfByte1, paramInt1, paramInt2);
    if (paramArrayOfByte1 == null) {
      return 0;
    }
    paramInt1 = paramArrayOfByte2.length - paramInt3;
    if (paramArrayOfByte1.length > paramInt1) {
      throw new ShortBufferException("Output buffer too short. Produced: " + paramArrayOfByte1.length + ", available: " + paramInt1);
    }
    System.arraycopy(paramArrayOfByte1, 0, paramArrayOfByte2, paramInt3, paramArrayOfByte1.length);
    return paramArrayOfByte1.length;
  }
  
  protected final byte[] engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.mCachedException != null) {
      return null;
    }
    try
    {
      ensureKeystoreOperationInitialized();
      if (paramInt2 == 0) {
        return null;
      }
    }
    catch (InvalidKeyException|InvalidAlgorithmParameterException paramArrayOfByte)
    {
      this.mCachedException = paramArrayOfByte;
      return null;
    }
    try
    {
      flushAAD();
      paramArrayOfByte = this.mMainDataStreamer.update(paramArrayOfByte, paramInt1, paramInt2);
      if (paramArrayOfByte.length == 0) {
        return null;
      }
    }
    catch (KeyStoreException paramArrayOfByte)
    {
      this.mCachedException = paramArrayOfByte;
      return null;
    }
    return paramArrayOfByte;
  }
  
  protected final void engineUpdateAAD(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer == null) {
      throw new IllegalArgumentException("src == null");
    }
    if (!paramByteBuffer.hasRemaining()) {
      return;
    }
    byte[] arrayOfByte;
    int j;
    int i;
    if (paramByteBuffer.hasArray())
    {
      arrayOfByte = paramByteBuffer.array();
      j = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
      i = paramByteBuffer.remaining();
      paramByteBuffer.position(paramByteBuffer.limit());
    }
    for (paramByteBuffer = arrayOfByte;; paramByteBuffer = arrayOfByte)
    {
      engineUpdateAAD(paramByteBuffer, j, i);
      return;
      arrayOfByte = new byte[paramByteBuffer.remaining()];
      j = 0;
      i = arrayOfByte.length;
      paramByteBuffer.get(arrayOfByte);
    }
  }
  
  protected final void engineUpdateAAD(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.mCachedException != null) {
      return;
    }
    try
    {
      ensureKeystoreOperationInitialized();
      if (this.mAdditionalAuthenticationDataStreamerClosed) {
        throw new IllegalStateException("AAD can only be provided before Cipher.update is invoked");
      }
    }
    catch (InvalidKeyException|InvalidAlgorithmParameterException paramArrayOfByte)
    {
      this.mCachedException = paramArrayOfByte;
      return;
    }
    if (this.mAdditionalAuthenticationDataStreamer == null) {
      throw new IllegalStateException("This cipher does not support AAD");
    }
    try
    {
      paramArrayOfByte = this.mAdditionalAuthenticationDataStreamer.update(paramArrayOfByte, paramInt1, paramInt2);
      if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0)) {
        throw new ProviderException("AAD update unexpectedly produced output: " + paramArrayOfByte.length + " bytes");
      }
    }
    catch (KeyStoreException paramArrayOfByte)
    {
      this.mCachedException = paramArrayOfByte;
      return;
    }
  }
  
  protected final byte[] engineWrap(Key paramKey)
    throws IllegalBlockSizeException, InvalidKeyException
  {
    if (this.mKey == null) {
      throw new IllegalStateException("Not initilized");
    }
    if (!isEncrypting()) {
      throw new IllegalStateException("Cipher must be initialized in Cipher.WRAP_MODE to wrap keys");
    }
    if (paramKey == null) {
      throw new NullPointerException("key == null");
    }
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    if ((paramKey instanceof SecretKey))
    {
      if ("RAW".equalsIgnoreCase(paramKey.getFormat())) {
        localObject2 = paramKey.getEncoded();
      }
      localObject1 = localObject2;
      if (localObject2 != null) {}
    }
    for (;;)
    {
      try
      {
        localObject1 = ((SecretKeySpec)SecretKeyFactory.getInstance(paramKey.getAlgorithm()).getKeySpec((SecretKey)paramKey, SecretKeySpec.class)).getEncoded();
        if (localObject1 != null) {
          break label334;
        }
        throw new InvalidKeyException("Failed to wrap key because it does not export its key material");
      }
      catch (NoSuchAlgorithmException|InvalidKeySpecException paramKey)
      {
        throw new InvalidKeyException("Failed to wrap key because it does not export its key material", paramKey);
      }
      if ((paramKey instanceof PrivateKey))
      {
        localObject2 = localObject1;
        if ("PKCS8".equalsIgnoreCase(paramKey.getFormat())) {
          localObject2 = paramKey.getEncoded();
        }
        localObject1 = localObject2;
        if (localObject2 == null) {
          try
          {
            localObject1 = ((PKCS8EncodedKeySpec)KeyFactory.getInstance(paramKey.getAlgorithm()).getKeySpec(paramKey, PKCS8EncodedKeySpec.class)).getEncoded();
          }
          catch (NoSuchAlgorithmException|InvalidKeySpecException paramKey)
          {
            throw new InvalidKeyException("Failed to wrap key because it does not export its key material", paramKey);
          }
        }
      }
      else if ((paramKey instanceof PublicKey))
      {
        localObject2 = localObject3;
        if ("X.509".equalsIgnoreCase(paramKey.getFormat())) {
          localObject2 = paramKey.getEncoded();
        }
        localObject1 = localObject2;
        if (localObject2 != null) {
          continue;
        }
        try
        {
          localObject1 = ((X509EncodedKeySpec)KeyFactory.getInstance(paramKey.getAlgorithm()).getKeySpec(paramKey, X509EncodedKeySpec.class)).getEncoded();
        }
        catch (NoSuchAlgorithmException|InvalidKeySpecException paramKey)
        {
          throw new InvalidKeyException("Failed to wrap key because it does not export its key material", paramKey);
        }
      }
    }
    throw new InvalidKeyException("Unsupported key type: " + paramKey.getClass().getName());
    try
    {
      label334:
      paramKey = engineDoFinal((byte[])localObject1, 0, localObject1.length);
      return paramKey;
    }
    catch (BadPaddingException paramKey)
    {
      throw ((IllegalBlockSizeException)new IllegalBlockSizeException().initCause(paramKey));
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
  
  protected abstract int getAdditionalEntropyAmountForBegin();
  
  protected abstract int getAdditionalEntropyAmountForFinish();
  
  protected final long getConsumedInputSizeBytes()
  {
    if (this.mMainDataStreamer == null) {
      throw new IllegalStateException("Not initialized");
    }
    return this.mMainDataStreamer.getConsumedInputSizeBytes();
  }
  
  protected final KeyStore getKeyStore()
  {
    return this.mKeyStore;
  }
  
  protected final int getKeymasterPurposeOverride()
  {
    return this.mKeymasterPurposeOverride;
  }
  
  public final long getOperationHandle()
  {
    return this.mOperationHandle;
  }
  
  protected final long getProducedOutputSizeBytes()
  {
    if (this.mMainDataStreamer == null) {
      throw new IllegalStateException("Not initialized");
    }
    return this.mMainDataStreamer.getProducedOutputSizeBytes();
  }
  
  protected abstract void initAlgorithmSpecificParameters()
    throws InvalidKeyException;
  
  protected abstract void initAlgorithmSpecificParameters(AlgorithmParameters paramAlgorithmParameters)
    throws InvalidAlgorithmParameterException;
  
  protected abstract void initAlgorithmSpecificParameters(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException;
  
  protected abstract void initKey(int paramInt, Key paramKey)
    throws InvalidKeyException;
  
  protected final boolean isEncrypting()
  {
    return this.mEncrypting;
  }
  
  protected abstract void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments paramKeymasterArguments);
  
  protected void resetAll()
  {
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null) {
      this.mKeyStore.abort(localIBinder);
    }
    this.mEncrypting = false;
    this.mKeymasterPurposeOverride = -1;
    this.mKey = null;
    this.mRng = null;
    this.mOperationToken = null;
    this.mOperationHandle = 0L;
    this.mMainDataStreamer = null;
    this.mAdditionalAuthenticationDataStreamer = null;
    this.mAdditionalAuthenticationDataStreamerClosed = false;
    this.mCachedException = null;
  }
  
  protected void resetWhilePreservingInitState()
  {
    IBinder localIBinder = this.mOperationToken;
    if (localIBinder != null) {
      this.mKeyStore.abort(localIBinder);
    }
    this.mOperationToken = null;
    this.mOperationHandle = 0L;
    this.mMainDataStreamer = null;
    this.mAdditionalAuthenticationDataStreamer = null;
    this.mAdditionalAuthenticationDataStreamerClosed = false;
    this.mCachedException = null;
  }
  
  protected final void setKey(AndroidKeyStoreKey paramAndroidKeyStoreKey)
  {
    this.mKey = paramAndroidKeyStoreKey;
  }
  
  protected final void setKeymasterPurposeOverride(int paramInt)
  {
    this.mKeymasterPurposeOverride = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreCipherSpiBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */