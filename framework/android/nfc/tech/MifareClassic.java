package android.nfc.tech;

import android.nfc.INfcTag;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class MifareClassic
  extends BasicTagTechnology
{
  public static final int BLOCK_SIZE = 16;
  public static final byte[] KEY_DEFAULT = { -1, -1, -1, -1, -1, -1 };
  public static final byte[] KEY_MIFARE_APPLICATION_DIRECTORY = { -96, -95, -94, -93, -92, -91 };
  public static final byte[] KEY_NFC_FORUM = { -45, -9, -45, -9, -45, -9 };
  private static final int MAX_BLOCK_COUNT = 256;
  private static final int MAX_SECTOR_COUNT = 40;
  public static final int SIZE_1K = 1024;
  public static final int SIZE_2K = 2048;
  public static final int SIZE_4K = 4096;
  public static final int SIZE_MINI = 320;
  private static final String TAG = "NFC";
  public static final int TYPE_CLASSIC = 0;
  public static final int TYPE_PLUS = 1;
  public static final int TYPE_PRO = 2;
  public static final int TYPE_UNKNOWN = -1;
  private boolean mIsEmulated;
  private int mSize;
  private int mType;
  
  public MifareClassic(Tag paramTag)
    throws RemoteException
  {
    super(paramTag, 8);
    paramTag = NfcA.get(paramTag);
    this.mIsEmulated = false;
    switch (paramTag.getSak())
    {
    default: 
      throw new RuntimeException("Tag incorrectly enumerated as MIFARE Classic, SAK = " + paramTag.getSak());
    case 1: 
    case 8: 
      this.mType = 0;
      this.mSize = 1024;
      return;
    case 9: 
      this.mType = 0;
      this.mSize = 320;
      return;
    case 16: 
      this.mType = 1;
      this.mSize = 2048;
      return;
    case 17: 
      this.mType = 1;
      this.mSize = 4096;
      return;
    case 24: 
      this.mType = 0;
      this.mSize = 4096;
      return;
    case 25: 
      this.mType = 0;
      this.mSize = 2048;
      return;
    case 40: 
      this.mType = 0;
      this.mSize = 1024;
      this.mIsEmulated = true;
      return;
    case 56: 
      this.mType = 0;
      this.mSize = 4096;
      this.mIsEmulated = true;
      return;
    case 136: 
      this.mType = 0;
      this.mSize = 1024;
      return;
    }
    this.mType = 2;
    this.mSize = 4096;
  }
  
  private boolean authenticate(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    validateSector(paramInt);
    checkConnected();
    byte[] arrayOfByte1 = new byte[12];
    if (paramBoolean) {
      arrayOfByte1[0] = 96;
    }
    for (;;)
    {
      arrayOfByte1[1] = ((byte)sectorToBlock(paramInt));
      byte[] arrayOfByte2 = getTag().getId();
      System.arraycopy(arrayOfByte2, arrayOfByte2.length - 4, arrayOfByte1, 2, 4);
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 6, 6);
      try
      {
        paramArrayOfByte = transceive(arrayOfByte1, false);
        if (paramArrayOfByte == null) {
          break;
        }
        return true;
      }
      catch (IOException paramArrayOfByte)
      {
        return false;
      }
      catch (TagLostException paramArrayOfByte)
      {
        throw paramArrayOfByte;
      }
      arrayOfByte1[0] = 97;
    }
  }
  
  public static MifareClassic get(Tag paramTag)
  {
    if (!paramTag.hasTech(8)) {
      return null;
    }
    try
    {
      paramTag = new MifareClassic(paramTag);
      return paramTag;
    }
    catch (RemoteException paramTag) {}
    return null;
  }
  
  private static void validateBlock(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 256)) {
      throw new IndexOutOfBoundsException("block out of bounds: " + paramInt);
    }
  }
  
  private static void validateSector(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 40)) {
      throw new IndexOutOfBoundsException("sector out of bounds: " + paramInt);
    }
  }
  
  private static void validateValueOperand(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("value operand negative");
    }
  }
  
  public boolean authenticateSectorWithKeyA(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    return authenticate(paramInt, paramArrayOfByte, true);
  }
  
  public boolean authenticateSectorWithKeyB(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    return authenticate(paramInt, paramArrayOfByte, false);
  }
  
  public int blockToSector(int paramInt)
  {
    validateBlock(paramInt);
    if (paramInt < 128) {
      return paramInt / 4;
    }
    return (paramInt - 128) / 16 + 32;
  }
  
  public void decrement(int paramInt1, int paramInt2)
    throws IOException
  {
    validateBlock(paramInt1);
    validateValueOperand(paramInt2);
    checkConnected();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
    localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    localByteBuffer.put((byte)-64);
    localByteBuffer.put((byte)paramInt1);
    localByteBuffer.putInt(paramInt2);
    transceive(localByteBuffer.array(), false);
  }
  
  public int getBlockCount()
  {
    return this.mSize / 16;
  }
  
  public int getBlockCountInSector(int paramInt)
  {
    validateSector(paramInt);
    if (paramInt < 32) {
      return 4;
    }
    return 16;
  }
  
  public int getMaxTransceiveLength()
  {
    return getMaxTransceiveLengthInternal();
  }
  
  public int getSectorCount()
  {
    switch (this.mSize)
    {
    default: 
      return 0;
    case 1024: 
      return 16;
    case 2048: 
      return 32;
    case 4096: 
      return 40;
    }
    return 5;
  }
  
  public int getSize()
  {
    return this.mSize;
  }
  
  public int getTimeout()
  {
    try
    {
      int i = this.mTag.getTagService().getTimeout(8);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
    }
    return 0;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void increment(int paramInt1, int paramInt2)
    throws IOException
  {
    validateBlock(paramInt1);
    validateValueOperand(paramInt2);
    checkConnected();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
    localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    localByteBuffer.put((byte)-63);
    localByteBuffer.put((byte)paramInt1);
    localByteBuffer.putInt(paramInt2);
    transceive(localByteBuffer.array(), false);
  }
  
  public boolean isEmulated()
  {
    return this.mIsEmulated;
  }
  
  public byte[] readBlock(int paramInt)
    throws IOException
  {
    validateBlock(paramInt);
    checkConnected();
    return transceive(new byte[] { 48, (byte)paramInt }, false);
  }
  
  public void restore(int paramInt)
    throws IOException
  {
    validateBlock(paramInt);
    checkConnected();
    transceive(new byte[] { -62, (byte)paramInt }, false);
  }
  
  public int sectorToBlock(int paramInt)
  {
    if (paramInt < 32) {
      return paramInt * 4;
    }
    return (paramInt - 32) * 16 + 128;
  }
  
  public void setTimeout(int paramInt)
  {
    try
    {
      if (this.mTag.getTagService().setTimeout(8, paramInt) != 0) {
        throw new IllegalArgumentException("The supplied timeout is not valid");
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
    }
  }
  
  public byte[] transceive(byte[] paramArrayOfByte)
    throws IOException
  {
    return transceive(paramArrayOfByte, true);
  }
  
  public void transfer(int paramInt)
    throws IOException
  {
    validateBlock(paramInt);
    checkConnected();
    transceive(new byte[] { -80, (byte)paramInt }, false);
  }
  
  public void writeBlock(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    validateBlock(paramInt);
    checkConnected();
    if (paramArrayOfByte.length != 16) {
      throw new IllegalArgumentException("must write 16-bytes");
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 2];
    arrayOfByte[0] = -96;
    arrayOfByte[1] = ((byte)paramInt);
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 2, paramArrayOfByte.length);
    transceive(arrayOfByte, false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/tech/MifareClassic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */