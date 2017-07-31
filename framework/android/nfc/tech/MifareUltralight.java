package android.nfc.tech;

import android.nfc.INfcTag;
import android.nfc.Tag;
import android.os.BaseBundle;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

public final class MifareUltralight
  extends BasicTagTechnology
{
  public static final String EXTRA_IS_UL_C = "isulc";
  private static final int MAX_PAGE_COUNT = 256;
  private static final int NXP_MANUFACTURER_ID = 4;
  public static final int PAGE_SIZE = 4;
  private static final String TAG = "NFC";
  public static final int TYPE_ULTRALIGHT = 1;
  public static final int TYPE_ULTRALIGHT_C = 2;
  public static final int TYPE_UNKNOWN = -1;
  private int mType;
  
  public MifareUltralight(Tag paramTag)
    throws RemoteException
  {
    super(paramTag, 9);
    NfcA localNfcA = NfcA.get(paramTag);
    this.mType = -1;
    if ((localNfcA.getSak() == 0) && (paramTag.getId()[0] == 4))
    {
      if (paramTag.getTechExtras(9).getBoolean("isulc")) {
        this.mType = 2;
      }
    }
    else {
      return;
    }
    this.mType = 1;
  }
  
  public static MifareUltralight get(Tag paramTag)
  {
    if (!paramTag.hasTech(9)) {
      return null;
    }
    try
    {
      paramTag = new MifareUltralight(paramTag);
      return paramTag;
    }
    catch (RemoteException paramTag) {}
    return null;
  }
  
  private static void validatePageIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 256)) {
      throw new IndexOutOfBoundsException("page out of bounds: " + paramInt);
    }
  }
  
  public int getMaxTransceiveLength()
  {
    return getMaxTransceiveLengthInternal();
  }
  
  public int getTimeout()
  {
    try
    {
      int i = this.mTag.getTagService().getTimeout(9);
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
  
  public byte[] readPages(int paramInt)
    throws IOException
  {
    validatePageIndex(paramInt);
    checkConnected();
    return transceive(new byte[] { 48, (byte)paramInt }, false);
  }
  
  public void setTimeout(int paramInt)
  {
    try
    {
      if (this.mTag.getTagService().setTimeout(9, paramInt) != 0) {
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
  
  public void writePage(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    validatePageIndex(paramInt);
    checkConnected();
    byte[] arrayOfByte = new byte[paramArrayOfByte.length + 2];
    arrayOfByte[0] = -94;
    arrayOfByte[1] = ((byte)paramInt);
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 2, paramArrayOfByte.length);
    transceive(arrayOfByte, false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/tech/MifareUltralight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */