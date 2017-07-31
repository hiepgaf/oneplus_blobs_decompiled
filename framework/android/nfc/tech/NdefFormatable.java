package android.nfc.tech;

import android.nfc.FormatException;
import android.nfc.INfcTag;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

public final class NdefFormatable
  extends BasicTagTechnology
{
  private static final String TAG = "NFC";
  
  public NdefFormatable(Tag paramTag)
    throws RemoteException
  {
    super(paramTag, 7);
  }
  
  public static NdefFormatable get(Tag paramTag)
  {
    if (!paramTag.hasTech(7)) {
      return null;
    }
    try
    {
      paramTag = new NdefFormatable(paramTag);
      return paramTag;
    }
    catch (RemoteException paramTag) {}
    return null;
  }
  
  public void format(NdefMessage paramNdefMessage)
    throws IOException, FormatException
  {
    format(paramNdefMessage, false);
  }
  
  void format(NdefMessage paramNdefMessage, boolean paramBoolean)
    throws IOException, FormatException
  {
    checkConnected();
    int i;
    INfcTag localINfcTag;
    try
    {
      i = this.mTag.getServiceHandle();
      localINfcTag = this.mTag.getTagService();
      switch (localINfcTag.formatNdef(i, MifareClassic.KEY_DEFAULT))
      {
      case -1: 
        throw new IOException();
      }
    }
    catch (RemoteException paramNdefMessage)
    {
      Log.e("NFC", "NFC service dead", paramNdefMessage);
    }
    label168:
    do
    {
      return;
      throw new IOException();
      throw new FormatException();
      if (!localINfcTag.isNdef(i)) {
        throw new IOException();
      }
      if (paramNdefMessage != null) {
        switch (localINfcTag.ndefWrite(i, paramNdefMessage))
        {
        case -1: 
          throw new IOException();
          throw new IOException();
        case -8: 
          throw new FormatException();
        }
      }
    } while (!paramBoolean);
    switch (localINfcTag.ndefMakeReadOnly(i))
    {
    }
    for (;;)
    {
      throw new IOException();
      throw new IOException();
      throw new IOException();
      break;
      break label168;
    }
  }
  
  public void formatReadOnly(NdefMessage paramNdefMessage)
    throws IOException, FormatException
  {
    format(paramNdefMessage, true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/tech/NdefFormatable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */