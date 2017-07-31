package android.nfc.tech;

import android.nfc.INfcTag;
import android.nfc.Tag;
import android.nfc.TransceiveResult;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

abstract class BasicTagTechnology
  implements TagTechnology
{
  private static final String TAG = "NFC";
  boolean mIsConnected;
  int mSelectedTechnology;
  final Tag mTag;
  
  BasicTagTechnology(Tag paramTag, int paramInt)
    throws RemoteException
  {
    this.mTag = paramTag;
    this.mSelectedTechnology = paramInt;
  }
  
  void checkConnected()
  {
    if ((this.mTag.getConnectedTechnology() != this.mSelectedTechnology) || (this.mTag.getConnectedTechnology() == -1)) {
      throw new IllegalStateException("Call connect() first!");
    }
  }
  
  public void close()
    throws IOException
  {
    try
    {
      this.mTag.getTagService().resetTimeouts();
      this.mTag.getTagService().reconnect(this.mTag.getServiceHandle());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
      return;
    }
    finally
    {
      this.mIsConnected = false;
      this.mTag.setTechnologyDisconnected();
    }
  }
  
  public void connect()
    throws IOException
  {
    try
    {
      int i = this.mTag.getTagService().connect(this.mTag.getServiceHandle(), this.mSelectedTechnology);
      if (i == 0)
      {
        this.mTag.setConnectedTechnology(this.mSelectedTechnology);
        this.mIsConnected = true;
        return;
      }
      if (i == -21) {
        throw new UnsupportedOperationException("Connecting to this technology is not supported by the NFC adapter.");
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
      throw new IOException("NFC service died");
    }
    throw new IOException();
  }
  
  int getMaxTransceiveLengthInternal()
  {
    try
    {
      int i = this.mTag.getTagService().getMaxTransceiveLength(this.mSelectedTechnology);
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
    }
    return 0;
  }
  
  public Tag getTag()
  {
    return this.mTag;
  }
  
  public boolean isConnected()
  {
    if (!this.mIsConnected) {
      return false;
    }
    try
    {
      boolean bool = this.mTag.getTagService().isPresent(this.mTag.getServiceHandle());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("NFC", "NFC service dead", localRemoteException);
    }
    return false;
  }
  
  public void reconnect()
    throws IOException
  {
    if (!this.mIsConnected) {
      throw new IllegalStateException("Technology not connected yet");
    }
    try
    {
      if (this.mTag.getTagService().reconnect(this.mTag.getServiceHandle()) != 0)
      {
        this.mIsConnected = false;
        this.mTag.setTechnologyDisconnected();
        throw new IOException();
      }
    }
    catch (RemoteException localRemoteException)
    {
      this.mIsConnected = false;
      this.mTag.setTechnologyDisconnected();
      Log.e("NFC", "NFC service dead", localRemoteException);
      throw new IOException("NFC service died");
    }
  }
  
  byte[] transceive(byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    checkConnected();
    try
    {
      paramArrayOfByte = this.mTag.getTagService().transceive(this.mTag.getServiceHandle(), paramArrayOfByte, paramBoolean);
      if (paramArrayOfByte == null) {
        throw new IOException("transceive failed");
      }
    }
    catch (RemoteException paramArrayOfByte)
    {
      Log.e("NFC", "NFC service dead", paramArrayOfByte);
      throw new IOException("NFC service died");
    }
    paramArrayOfByte = paramArrayOfByte.getResponseOrThrow();
    return paramArrayOfByte;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/tech/BasicTagTechnology.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */