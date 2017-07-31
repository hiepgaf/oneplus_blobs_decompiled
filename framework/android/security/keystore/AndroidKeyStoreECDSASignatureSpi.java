package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import libcore.util.EmptyArray;

abstract class AndroidKeyStoreECDSASignatureSpi
  extends AndroidKeyStoreSignatureSpiBase
{
  private int mGroupSizeBits = -1;
  private final int mKeymasterDigest;
  
  AndroidKeyStoreECDSASignatureSpi(int paramInt)
  {
    this.mKeymasterDigest = paramInt;
  }
  
  protected final void addAlgorithmSpecificParametersToBegin(KeymasterArguments paramKeymasterArguments)
  {
    paramKeymasterArguments.addEnum(268435458, 3);
    paramKeymasterArguments.addEnum(536870917, this.mKeymasterDigest);
  }
  
  protected final int getAdditionalEntropyAmountForSign()
  {
    return (this.mGroupSizeBits + 7) / 8;
  }
  
  protected final int getGroupSizeBits()
  {
    if (this.mGroupSizeBits == -1) {
      throw new IllegalStateException("Not initialized");
    }
    return this.mGroupSizeBits;
  }
  
  protected final void initKey(AndroidKeyStoreKey paramAndroidKeyStoreKey)
    throws InvalidKeyException
  {
    if (!"EC".equalsIgnoreCase(paramAndroidKeyStoreKey.getAlgorithm())) {
      throw new InvalidKeyException("Unsupported key algorithm: " + paramAndroidKeyStoreKey.getAlgorithm() + ". Only" + "EC" + " supported");
    }
    KeyCharacteristics localKeyCharacteristics = new KeyCharacteristics();
    int i = getKeyStore().getKeyCharacteristics(paramAndroidKeyStoreKey.getAlias(), null, null, paramAndroidKeyStoreKey.getUid(), localKeyCharacteristics);
    if (i != 1) {
      throw getKeyStore().getInvalidKeyException(paramAndroidKeyStoreKey.getAlias(), paramAndroidKeyStoreKey.getUid(), i);
    }
    long l = localKeyCharacteristics.getUnsignedInt(805306371, -1L);
    if (l == -1L) {
      throw new InvalidKeyException("Size of key not known");
    }
    if (l > 2147483647L) {
      throw new InvalidKeyException("Key too large: " + l + " bits");
    }
    this.mGroupSizeBits = ((int)l);
    super.initKey(paramAndroidKeyStoreKey);
  }
  
  protected final void resetAll()
  {
    this.mGroupSizeBits = -1;
    super.resetAll();
  }
  
  protected final void resetWhilePreservingInitState()
  {
    super.resetWhilePreservingInitState();
  }
  
  public static final class NONE
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public NONE()
    {
      super();
    }
    
    protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStore paramKeyStore, IBinder paramIBinder)
    {
      return new TruncateToFieldSizeMessageStreamer(super.createMainDataStreamer(paramKeyStore, paramIBinder), getGroupSizeBits(), null);
    }
    
    private static class TruncateToFieldSizeMessageStreamer
      implements KeyStoreCryptoOperationStreamer
    {
      private long mConsumedInputSizeBytes;
      private final KeyStoreCryptoOperationStreamer mDelegate;
      private final int mGroupSizeBits;
      private final ByteArrayOutputStream mInputBuffer = new ByteArrayOutputStream();
      
      private TruncateToFieldSizeMessageStreamer(KeyStoreCryptoOperationStreamer paramKeyStoreCryptoOperationStreamer, int paramInt)
      {
        this.mDelegate = paramKeyStoreCryptoOperationStreamer;
        this.mGroupSizeBits = paramInt;
      }
      
      public byte[] doFinal(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
        throws KeyStoreException
      {
        if (paramInt2 > 0)
        {
          this.mConsumedInputSizeBytes += paramInt2;
          this.mInputBuffer.write(paramArrayOfByte1, paramInt1, paramInt2);
        }
        paramArrayOfByte1 = this.mInputBuffer.toByteArray();
        this.mInputBuffer.reset();
        return this.mDelegate.doFinal(paramArrayOfByte1, 0, Math.min(paramArrayOfByte1.length, (this.mGroupSizeBits + 7) / 8), paramArrayOfByte2, paramArrayOfByte3);
      }
      
      public long getConsumedInputSizeBytes()
      {
        return this.mConsumedInputSizeBytes;
      }
      
      public long getProducedOutputSizeBytes()
      {
        return this.mDelegate.getProducedOutputSizeBytes();
      }
      
      public byte[] update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws KeyStoreException
      {
        if (paramInt2 > 0)
        {
          this.mInputBuffer.write(paramArrayOfByte, paramInt1, paramInt2);
          this.mConsumedInputSizeBytes += paramInt2;
        }
        return EmptyArray.BYTE;
      }
    }
  }
  
  public static final class SHA1
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public SHA1()
    {
      super();
    }
  }
  
  public static final class SHA224
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public SHA224()
    {
      super();
    }
  }
  
  public static final class SHA256
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public SHA256()
    {
      super();
    }
  }
  
  public static final class SHA384
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public SHA384()
    {
      super();
    }
  }
  
  public static final class SHA512
    extends AndroidKeyStoreECDSASignatureSpi
  {
    public SHA512()
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keystore/AndroidKeyStoreECDSASignatureSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */