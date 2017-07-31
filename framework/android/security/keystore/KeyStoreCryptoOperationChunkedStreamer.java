package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.OperationResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.ProviderException;
import libcore.util.EmptyArray;

class KeyStoreCryptoOperationChunkedStreamer
  implements KeyStoreCryptoOperationStreamer
{
  private static final int DEFAULT_MAX_CHUNK_SIZE = 65536;
  private byte[] mBuffered = EmptyArray.BYTE;
  private int mBufferedLength;
  private int mBufferedOffset;
  private long mConsumedInputSizeBytes;
  private final Stream mKeyStoreStream;
  private final int mMaxChunkSize;
  private long mProducedOutputSizeBytes;
  
  public KeyStoreCryptoOperationChunkedStreamer(Stream paramStream)
  {
    this(paramStream, 65536);
  }
  
  public KeyStoreCryptoOperationChunkedStreamer(Stream paramStream, int paramInt)
  {
    this.mKeyStoreStream = paramStream;
    this.mMaxChunkSize = paramInt;
  }
  
  public byte[] doFinal(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws KeyStoreException
  {
    if (paramInt2 == 0)
    {
      paramArrayOfByte1 = EmptyArray.BYTE;
      paramInt1 = 0;
    }
    paramArrayOfByte1 = ArrayUtils.concat(update(paramArrayOfByte1, paramInt1, paramInt2), flush());
    paramArrayOfByte2 = this.mKeyStoreStream.finish(paramArrayOfByte2, paramArrayOfByte3);
    if (paramArrayOfByte2 == null) {
      throw new KeyStoreConnectException();
    }
    if (paramArrayOfByte2.resultCode != 1) {
      throw KeyStore.getKeyStoreException(paramArrayOfByte2.resultCode);
    }
    this.mProducedOutputSizeBytes += paramArrayOfByte2.output.length;
    return ArrayUtils.concat(paramArrayOfByte1, paramArrayOfByte2.output);
  }
  
  public byte[] flush()
    throws KeyStoreException
  {
    if (this.mBufferedLength <= 0) {
      return EmptyArray.BYTE;
    }
    Object localObject1 = null;
    Object localObject3;
    OperationResult localOperationResult;
    if (this.mBufferedLength > 0)
    {
      localObject3 = ArrayUtils.subarray(this.mBuffered, this.mBufferedOffset, this.mBufferedLength);
      localOperationResult = this.mKeyStoreStream.update((byte[])localObject3);
      if (localOperationResult == null) {
        throw new KeyStoreConnectException();
      }
      if (localOperationResult.resultCode != 1) {
        throw KeyStore.getKeyStoreException(localOperationResult.resultCode);
      }
      if (localOperationResult.inputConsumed > 0) {}
    }
    else
    {
      if (this.mBufferedLength <= 0) {
        break label348;
      }
      localObject3 = new StringBuilder().append("Keystore failed to consume last ");
      if (this.mBufferedLength == 1) {
        break label342;
      }
    }
    label342:
    for (localObject1 = this.mBufferedLength + " bytes";; localObject2 = "byte")
    {
      throw new KeyStoreException(-21, (String)localObject1 + " of input");
      if (localOperationResult.inputConsumed >= localObject3.length)
      {
        this.mBuffered = EmptyArray.BYTE;
        this.mBufferedOffset = 0;
      }
      for (this.mBufferedLength = 0; localOperationResult.inputConsumed > localObject3.length; this.mBufferedLength = (localObject3.length - localOperationResult.inputConsumed))
      {
        throw new KeyStoreException(64536, "Keystore consumed more input than provided. Provided: " + localObject3.length + ", consumed: " + localOperationResult.inputConsumed);
        this.mBuffered = ((byte[])localObject3);
        this.mBufferedOffset = localOperationResult.inputConsumed;
      }
      if ((localOperationResult.output == null) || (localOperationResult.output.length <= 0)) {
        break;
      }
      localObject3 = localObject1;
      if (localObject1 == null)
      {
        if (this.mBufferedLength == 0)
        {
          this.mProducedOutputSizeBytes += localOperationResult.output.length;
          return localOperationResult.output;
        }
        localObject3 = new ByteArrayOutputStream();
      }
      try
      {
        ((OutputStream)localObject3).write(localOperationResult.output);
        localObject1 = localObject3;
      }
      catch (IOException localIOException)
      {
        throw new ProviderException("Failed to buffer output", localIOException);
      }
    }
    label348:
    if (localObject2 != null) {}
    for (Object localObject2 = ((ByteArrayOutputStream)localObject2).toByteArray();; localObject2 = EmptyArray.BYTE)
    {
      this.mProducedOutputSizeBytes += localObject2.length;
      return (byte[])localObject2;
    }
  }
  
  public long getConsumedInputSizeBytes()
  {
    return this.mConsumedInputSizeBytes;
  }
  
  public long getProducedOutputSizeBytes()
  {
    return this.mProducedOutputSizeBytes;
  }
  
  public byte[] update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws KeyStoreException
  {
    if (paramInt2 == 0) {
      return EmptyArray.BYTE;
    }
    ByteArrayOutputStream localByteArrayOutputStream = null;
    int i = paramInt1;
    if (paramInt2 > 0)
    {
      byte[] arrayOfByte;
      if (this.mBufferedLength + paramInt2 > this.mMaxChunkSize)
      {
        paramInt1 = this.mMaxChunkSize - this.mBufferedLength;
        arrayOfByte = ArrayUtils.concat(this.mBuffered, this.mBufferedOffset, this.mBufferedLength, paramArrayOfByte, i, paramInt1);
      }
      int j;
      int k;
      OperationResult localOperationResult;
      for (;;)
      {
        j = i + paramInt1;
        k = paramInt2 - paramInt1;
        this.mConsumedInputSizeBytes += paramInt1;
        localOperationResult = this.mKeyStoreStream.update(arrayOfByte);
        if (localOperationResult != null) {
          break;
        }
        throw new KeyStoreConnectException();
        if ((this.mBufferedLength == 0) && (i == 0) && (paramInt2 == paramArrayOfByte.length))
        {
          arrayOfByte = paramArrayOfByte;
          paramInt1 = paramArrayOfByte.length;
        }
        else
        {
          paramInt1 = paramInt2;
          arrayOfByte = ArrayUtils.concat(this.mBuffered, this.mBufferedOffset, this.mBufferedLength, paramArrayOfByte, i, paramInt1);
        }
      }
      if (localOperationResult.resultCode != 1) {
        throw KeyStore.getKeyStoreException(localOperationResult.resultCode);
      }
      if (localOperationResult.inputConsumed == arrayOfByte.length)
      {
        this.mBuffered = EmptyArray.BYTE;
        this.mBufferedOffset = 0;
        this.mBufferedLength = 0;
      }
      for (;;)
      {
        i = j;
        paramInt2 = k;
        if (localOperationResult.output == null) {
          break;
        }
        i = j;
        paramInt2 = k;
        if (localOperationResult.output.length <= 0) {
          break;
        }
        if (k <= 0) {
          break label456;
        }
        i = j;
        paramInt2 = k;
        if (localByteArrayOutputStream != null) {
          break;
        }
        localByteArrayOutputStream = new ByteArrayOutputStream();
        try
        {
          localByteArrayOutputStream.write(localOperationResult.output);
          i = j;
          paramInt2 = k;
        }
        catch (IOException paramArrayOfByte)
        {
          throw new ProviderException("Failed to buffer output", paramArrayOfByte);
        }
        if (localOperationResult.inputConsumed <= 0)
        {
          if (k > 0) {
            throw new KeyStoreException(64536, "Keystore consumed nothing from max-sized chunk: " + arrayOfByte.length + " bytes");
          }
          this.mBuffered = arrayOfByte;
          this.mBufferedOffset = 0;
          this.mBufferedLength = arrayOfByte.length;
        }
        else
        {
          if (localOperationResult.inputConsumed >= arrayOfByte.length) {
            break label411;
          }
          this.mBuffered = arrayOfByte;
          this.mBufferedOffset = localOperationResult.inputConsumed;
          this.mBufferedLength = (arrayOfByte.length - localOperationResult.inputConsumed);
        }
      }
      label411:
      throw new KeyStoreException(64536, "Keystore consumed more input than provided. Provided: " + arrayOfByte.length + ", consumed: " + localOperationResult.inputConsumed);
      label456:
      if (localByteArrayOutputStream == null) {
        paramArrayOfByte = localOperationResult.output;
      }
      for (;;)
      {
        this.mProducedOutputSizeBytes += paramArrayOfByte.length;
        return paramArrayOfByte;
        try
        {
          localByteArrayOutputStream.write(localOperationResult.output);
          paramArrayOfByte = localByteArrayOutputStream.toByteArray();
        }
        catch (IOException paramArrayOfByte)
        {
          throw new ProviderException("Failed to buffer output", paramArrayOfByte);
        }
      }
    }
    if (localByteArrayOutputStream == null) {}
    for (paramArrayOfByte = EmptyArray.BYTE;; paramArrayOfByte = localByteArrayOutputStream.toByteArray())
    {
      this.mProducedOutputSizeBytes += paramArrayOfByte.length;
      return paramArrayOfByte;
    }
  }
  
  public static class MainDataStream
    implements KeyStoreCryptoOperationChunkedStreamer.Stream
  {
    private final KeyStore mKeyStore;
    private final IBinder mOperationToken;
    
    public MainDataStream(KeyStore paramKeyStore, IBinder paramIBinder)
    {
      this.mKeyStore = paramKeyStore;
      this.mOperationToken = paramIBinder;
    }
    
    public OperationResult finish(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      return this.mKeyStore.finish(this.mOperationToken, null, paramArrayOfByte1, paramArrayOfByte2);
    }
    
    public OperationResult update(byte[] paramArrayOfByte)
    {
      return this.mKeyStore.update(this.mOperationToken, null, paramArrayOfByte);
    }
  }
  
  static abstract interface Stream
  {
    public abstract OperationResult finish(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
    
    public abstract OperationResult update(byte[] paramArrayOfByte);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/KeyStoreCryptoOperationChunkedStreamer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */