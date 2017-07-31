package android.net.wifi.nan;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class WifiNanEventListener
{
  private static final boolean DBG = false;
  public static final int LISTEN_CONFIG_COMPLETED = 1;
  public static final int LISTEN_CONFIG_FAILED = 2;
  public static final int LISTEN_IDENTITY_CHANGED = 8;
  public static final int LISTEN_NAN_DOWN = 4;
  private static final String TAG = "WifiNanEventListener";
  private static final boolean VDBG = false;
  public IWifiNanEventListener callback = new IWifiNanEventListener.Stub()
  {
    public void onConfigCompleted(ConfigRequest paramAnonymousConfigRequest)
    {
      Message localMessage = WifiNanEventListener.-get0(WifiNanEventListener.this).obtainMessage(1);
      localMessage.obj = paramAnonymousConfigRequest;
      WifiNanEventListener.-get0(WifiNanEventListener.this).sendMessage(localMessage);
    }
    
    public void onConfigFailed(ConfigRequest paramAnonymousConfigRequest, int paramAnonymousInt)
    {
      Message localMessage = WifiNanEventListener.-get0(WifiNanEventListener.this).obtainMessage(2);
      localMessage.arg1 = paramAnonymousInt;
      localMessage.obj = paramAnonymousConfigRequest;
      WifiNanEventListener.-get0(WifiNanEventListener.this).sendMessage(localMessage);
    }
    
    public void onIdentityChanged()
    {
      Message localMessage = WifiNanEventListener.-get0(WifiNanEventListener.this).obtainMessage(8);
      WifiNanEventListener.-get0(WifiNanEventListener.this).sendMessage(localMessage);
    }
    
    public void onNanDown(int paramAnonymousInt)
    {
      Message localMessage = WifiNanEventListener.-get0(WifiNanEventListener.this).obtainMessage(4);
      localMessage.arg1 = paramAnonymousInt;
      WifiNanEventListener.-get0(WifiNanEventListener.this).sendMessage(localMessage);
    }
  };
  private final Handler mHandler;
  
  public WifiNanEventListener()
  {
    this(Looper.myLooper());
  }
  
  public WifiNanEventListener(Looper paramLooper)
  {
    this.mHandler = new Handler(paramLooper)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        case 3: 
        case 5: 
        case 6: 
        case 7: 
        default: 
          return;
        case 1: 
          WifiNanEventListener.this.onConfigCompleted((ConfigRequest)paramAnonymousMessage.obj);
          return;
        case 2: 
          WifiNanEventListener.this.onConfigFailed((ConfigRequest)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
          return;
        case 4: 
          WifiNanEventListener.this.onNanDown(paramAnonymousMessage.arg1);
          return;
        }
        WifiNanEventListener.this.onIdentityChanged();
      }
    };
  }
  
  public void onConfigCompleted(ConfigRequest paramConfigRequest)
  {
    Log.w("WifiNanEventListener", "onConfigCompleted: called in stub - override if interested or disable");
  }
  
  public void onConfigFailed(ConfigRequest paramConfigRequest, int paramInt)
  {
    Log.w("WifiNanEventListener", "onConfigFailed: called in stub - override if interested or disable");
  }
  
  public void onIdentityChanged() {}
  
  public void onNanDown(int paramInt)
  {
    Log.w("WifiNanEventListener", "onNanDown: called in stub - override if interested or disable");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */