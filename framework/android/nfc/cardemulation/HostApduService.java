package android.nfc.cardemulation;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public abstract class HostApduService
  extends Service
{
  public static final int DEACTIVATION_DESELECTED = 1;
  public static final int DEACTIVATION_LINK_LOSS = 0;
  public static final String KEY_DATA = "data";
  public static final int MSG_COMMAND_APDU = 0;
  public static final int MSG_DEACTIVATED = 2;
  public static final int MSG_RESPONSE_APDU = 1;
  public static final int MSG_UNHANDLED = 3;
  public static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.HOST_APDU_SERVICE";
  public static final String SERVICE_META_DATA = "android.nfc.cardemulation.host_apdu_service";
  static final String TAG = "ApduService";
  final Messenger mMessenger = new Messenger(new MsgHandler());
  Messenger mNfcService = null;
  
  public final void notifyUnhandled()
  {
    Message localMessage = Message.obtain(null, 3);
    try
    {
      this.mMessenger.send(localMessage);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("TAG", "Local messenger has died.");
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return this.mMessenger.getBinder();
  }
  
  public abstract void onDeactivated(int paramInt);
  
  public abstract byte[] processCommandApdu(byte[] paramArrayOfByte, Bundle paramBundle);
  
  public final void sendResponseApdu(byte[] paramArrayOfByte)
  {
    Message localMessage = Message.obtain(null, 1);
    Bundle localBundle = new Bundle();
    localBundle.putByteArray("data", paramArrayOfByte);
    localMessage.setData(localBundle);
    try
    {
      this.mMessenger.send(localMessage);
      return;
    }
    catch (RemoteException paramArrayOfByte)
    {
      Log.e("TAG", "Local messenger has died.");
    }
  }
  
  final class MsgHandler
    extends Handler
  {
    MsgHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        super.handleMessage(paramMessage);
      case 0: 
        do
        {
          return;
          localObject = paramMessage.getData();
          if (localObject == null) {
            return;
          }
          if (HostApduService.this.mNfcService == null) {
            HostApduService.this.mNfcService = paramMessage.replyTo;
          }
          paramMessage = ((Bundle)localObject).getByteArray("data");
          if (paramMessage == null) {
            break;
          }
          paramMessage = HostApduService.this.processCommandApdu(paramMessage, null);
        } while (paramMessage == null);
        if (HostApduService.this.mNfcService == null)
        {
          Log.e("ApduService", "Response not sent; service was deactivated.");
          return;
        }
        Object localObject = Message.obtain(null, 1);
        Bundle localBundle = new Bundle();
        localBundle.putByteArray("data", paramMessage);
        ((Message)localObject).setData(localBundle);
        ((Message)localObject).replyTo = HostApduService.this.mMessenger;
        try
        {
          HostApduService.this.mNfcService.send((Message)localObject);
          return;
        }
        catch (RemoteException paramMessage)
        {
          Log.e("TAG", "Response not sent; RemoteException calling into NfcService.");
          return;
        }
        Log.e("ApduService", "Received MSG_COMMAND_APDU without data.");
        return;
      case 1: 
        if (HostApduService.this.mNfcService == null)
        {
          Log.e("ApduService", "Response not sent; service was deactivated.");
          return;
        }
        try
        {
          paramMessage.replyTo = HostApduService.this.mMessenger;
          HostApduService.this.mNfcService.send(paramMessage);
          return;
        }
        catch (RemoteException paramMessage)
        {
          Log.e("ApduService", "RemoteException calling into NfcService.");
          return;
        }
      case 2: 
        HostApduService.this.mNfcService = null;
        HostApduService.this.onDeactivated(paramMessage.arg1);
        return;
      }
      if (HostApduService.this.mNfcService == null)
      {
        Log.e("ApduService", "notifyUnhandled not sent; service was deactivated.");
        return;
      }
      try
      {
        paramMessage.replyTo = HostApduService.this.mMessenger;
        HostApduService.this.mNfcService.send(paramMessage);
        return;
      }
      catch (RemoteException paramMessage)
      {
        Log.e("ApduService", "RemoteException calling into NfcService.");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/HostApduService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */