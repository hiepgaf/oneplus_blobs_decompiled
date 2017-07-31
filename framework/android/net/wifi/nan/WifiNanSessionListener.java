package android.net.wifi.nan;

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class WifiNanSessionListener
{
  private static final boolean DBG = false;
  public static final int FAIL_REASON_INVALID_ARGS = 1;
  public static final int FAIL_REASON_NO_MATCH_SESSION = 2;
  public static final int FAIL_REASON_NO_RESOURCES = 0;
  public static final int FAIL_REASON_OTHER = 3;
  public static final int LISTEN_HIDDEN_FLAGS = 245;
  public static final int LISTEN_MATCH = 16;
  public static final int LISTEN_MESSAGE_RECEIVED = 128;
  public static final int LISTEN_MESSAGE_SEND_FAIL = 64;
  public static final int LISTEN_MESSAGE_SEND_SUCCESS = 32;
  public static final int LISTEN_PUBLISH_FAIL = 1;
  public static final int LISTEN_PUBLISH_TERMINATED = 2;
  public static final int LISTEN_SUBSCRIBE_FAIL = 4;
  public static final int LISTEN_SUBSCRIBE_TERMINATED = 8;
  private static final String MESSAGE_BUNDLE_KEY_MESSAGE = "message";
  private static final String MESSAGE_BUNDLE_KEY_MESSAGE2 = "message2";
  private static final String MESSAGE_BUNDLE_KEY_PEER_ID = "peer_id";
  private static final String TAG = "WifiNanSessionListener";
  public static final int TERMINATE_REASON_DONE = 0;
  public static final int TERMINATE_REASON_FAIL = 1;
  private static final boolean VDBG = false;
  public IWifiNanSessionListener callback = new IWifiNanSessionListener.Stub()
  {
    public void onMatch(int paramAnonymousInt1, byte[] paramAnonymousArrayOfByte1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte2, int paramAnonymousInt3)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("peer_id", paramAnonymousInt1);
      localBundle.putByteArray("message", paramAnonymousArrayOfByte1);
      localBundle.putByteArray("message2", paramAnonymousArrayOfByte2);
      paramAnonymousArrayOfByte1 = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(16);
      paramAnonymousArrayOfByte1.arg1 = paramAnonymousInt2;
      paramAnonymousArrayOfByte1.arg2 = paramAnonymousInt3;
      paramAnonymousArrayOfByte1.setData(localBundle);
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(paramAnonymousArrayOfByte1);
    }
    
    public void onMessageReceived(int paramAnonymousInt1, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt2)
    {
      Bundle localBundle = new Bundle();
      localBundle.putByteArray("message", paramAnonymousArrayOfByte);
      paramAnonymousArrayOfByte = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(128);
      paramAnonymousArrayOfByte.arg1 = paramAnonymousInt2;
      paramAnonymousArrayOfByte.arg2 = paramAnonymousInt1;
      paramAnonymousArrayOfByte.setData(localBundle);
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(paramAnonymousArrayOfByte);
    }
    
    public void onMessageSendFail(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(64);
      localMessage.arg1 = paramAnonymousInt1;
      localMessage.arg2 = paramAnonymousInt2;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
    
    public void onMessageSendSuccess(int paramAnonymousInt)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(32);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
    
    public void onPublishFail(int paramAnonymousInt)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(1);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
    
    public void onPublishTerminated(int paramAnonymousInt)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(2);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
    
    public void onSubscribeFail(int paramAnonymousInt)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(4);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
    
    public void onSubscribeTerminated(int paramAnonymousInt)
    {
      Message localMessage = WifiNanSessionListener.-get0(WifiNanSessionListener.this).obtainMessage(8);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanSessionListener.-get0(WifiNanSessionListener.this).sendMessage(localMessage);
    }
  };
  private final Handler mHandler;
  
  public WifiNanSessionListener()
  {
    this(Looper.myLooper());
  }
  
  public WifiNanSessionListener(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          return;
        case 1: 
          WifiNanSessionListener.this.onPublishFail(paramAnonymousMessage.arg1);
          return;
        case 2: 
          WifiNanSessionListener.this.onPublishTerminated(paramAnonymousMessage.arg1);
          return;
        case 4: 
          WifiNanSessionListener.this.onSubscribeFail(paramAnonymousMessage.arg1);
          return;
        case 8: 
          WifiNanSessionListener.this.onSubscribeTerminated(paramAnonymousMessage.arg1);
          return;
        case 16: 
          WifiNanSessionListener.this.onMatch(paramAnonymousMessage.getData().getInt("peer_id"), paramAnonymousMessage.getData().getByteArray("message"), paramAnonymousMessage.arg1, paramAnonymousMessage.getData().getByteArray("message2"), paramAnonymousMessage.arg2);
          return;
        case 32: 
          WifiNanSessionListener.this.onMessageSendSuccess(paramAnonymousMessage.arg1);
          return;
        case 64: 
          WifiNanSessionListener.this.onMessageSendFail(paramAnonymousMessage.arg1, paramAnonymousMessage.arg2);
          return;
        }
        WifiNanSessionListener.this.onMessageReceived(paramAnonymousMessage.arg2, paramAnonymousMessage.getData().getByteArray("message"), paramAnonymousMessage.arg1);
      }
    };
  }
  
  public void onMatch(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) {}
  
  public void onMessageReceived(int paramInt1, byte[] paramArrayOfByte, int paramInt2) {}
  
  public void onMessageSendFail(int paramInt1, int paramInt2) {}
  
  public void onMessageSendSuccess(int paramInt) {}
  
  public void onPublishFail(int paramInt) {}
  
  public void onPublishTerminated(int paramInt)
  {
    Log.w("WifiNanSessionListener", "onPublishTerminated: called in stub - override if interested or disable");
  }
  
  public void onSubscribeFail(int paramInt) {}
  
  public void onSubscribeTerminated(int paramInt)
  {
    Log.w("WifiNanSessionListener", "onSubscribeTerminated: called in stub - override if interested or disable");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanSessionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */