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

public abstract class HostNfcFService
  extends Service
{
  public static final int DEACTIVATION_LINK_LOSS = 0;
  public static final String KEY_DATA = "data";
  public static final String KEY_MESSENGER = "messenger";
  public static final int MSG_COMMAND_PACKET = 0;
  public static final int MSG_DEACTIVATED = 2;
  public static final int MSG_RESPONSE_PACKET = 1;
  public static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.HOST_NFCF_SERVICE";
  public static final String SERVICE_META_DATA = "android.nfc.cardemulation.host_nfcf_service";
  static final String TAG = "NfcFService";
  final Messenger mMessenger = new Messenger(new MsgHandler());
  Messenger mNfcService = null;
  
  public final IBinder onBind(Intent paramIntent)
  {
    return this.mMessenger.getBinder();
  }
  
  public abstract void onDeactivated(int paramInt);
  
  public abstract byte[] processNfcFPacket(byte[] paramArrayOfByte, Bundle paramBundle);
  
  public final void sendResponsePacket(byte[] paramArrayOfByte)
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
        return;
      case 0: 
        Object localObject = paramMessage.getData();
        if (localObject == null) {
          return;
        }
        if (HostNfcFService.this.mNfcService == null) {
          HostNfcFService.this.mNfcService = paramMessage.replyTo;
        }
        paramMessage = ((Bundle)localObject).getByteArray("data");
        if (paramMessage != null)
        {
          paramMessage = HostNfcFService.this.processNfcFPacket(paramMessage, null);
          if (HostNfcFService.this.mNfcService == null)
          {
            Log.e("NfcFService", "Response not sent; service was deactivated.");
            return;
          }
          localObject = Message.obtain(null, 1);
          Bundle localBundle = new Bundle();
          localBundle.putByteArray("data", paramMessage);
          ((Message)localObject).setData(localBundle);
          ((Message)localObject).replyTo = HostNfcFService.this.mMessenger;
          try
          {
            HostNfcFService.this.mNfcService.send((Message)localObject);
            return;
          }
          catch (RemoteException paramMessage)
          {
            Log.e("TAG", "Response not sent; RemoteException calling into NfcService.");
            return;
          }
        }
        Log.e("NfcFService", "Received MSG_COMMAND_PACKET without data.");
        return;
      case 1: 
        if (HostNfcFService.this.mNfcService == null)
        {
          Log.e("NfcFService", "Response not sent; service was deactivated.");
          return;
        }
        try
        {
          paramMessage.replyTo = HostNfcFService.this.mMessenger;
          HostNfcFService.this.mNfcService.send(paramMessage);
          return;
        }
        catch (RemoteException paramMessage)
        {
          Log.e("NfcFService", "RemoteException calling into NfcService.");
          return;
        }
      }
      HostNfcFService.this.mNfcService = null;
      HostNfcFService.this.onDeactivated(paramMessage.arg1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/cardemulation/HostNfcFService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */