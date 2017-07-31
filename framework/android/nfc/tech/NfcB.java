package android.nfc.tech;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import java.io.IOException;

public final class NfcB
  extends BasicTagTechnology
{
  public static final String EXTRA_APPDATA = "appdata";
  public static final String EXTRA_PROTINFO = "protinfo";
  private byte[] mAppData;
  private byte[] mProtInfo;
  
  public NfcB(Tag paramTag)
    throws RemoteException
  {
    super(paramTag, 2);
    paramTag = paramTag.getTechExtras(2);
    this.mAppData = paramTag.getByteArray("appdata");
    this.mProtInfo = paramTag.getByteArray("protinfo");
  }
  
  public static NfcB get(Tag paramTag)
  {
    if (!paramTag.hasTech(2)) {
      return null;
    }
    try
    {
      paramTag = new NfcB(paramTag);
      return paramTag;
    }
    catch (RemoteException paramTag) {}
    return null;
  }
  
  public byte[] getApplicationData()
  {
    return this.mAppData;
  }
  
  public int getMaxTransceiveLength()
  {
    return getMaxTransceiveLengthInternal();
  }
  
  public byte[] getProtocolInfo()
  {
    return this.mProtInfo;
  }
  
  public byte[] transceive(byte[] paramArrayOfByte)
    throws IOException
  {
    return transceive(paramArrayOfByte, true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/tech/NfcB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */