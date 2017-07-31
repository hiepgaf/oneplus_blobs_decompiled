package android.net.wifi.nan;

import android.util.Log;

public class WifiNanSession
{
  private static final boolean DBG = false;
  private static final String TAG = "WifiNanSession";
  private static final boolean VDBG = false;
  private boolean mDestroyed;
  protected WifiNanManager mManager;
  protected int mSessionId;
  
  public WifiNanSession(WifiNanManager paramWifiNanManager, int paramInt)
  {
    this.mManager = paramWifiNanManager;
    this.mSessionId = paramInt;
    this.mDestroyed = false;
  }
  
  public void destroy()
  {
    this.mManager.destroySession(this.mSessionId);
    this.mDestroyed = true;
  }
  
  protected void finalize()
    throws Throwable
  {
    if (!this.mDestroyed) {
      Log.w("WifiNanSession", "WifiNanSession mSessionId=" + this.mSessionId + " was not explicitly destroyed. The session may use resources until " + "destroyed so step should be done explicitly");
    }
    destroy();
  }
  
  public void sendMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    this.mManager.sendMessage(this.mSessionId, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
  }
  
  public void stop()
  {
    this.mManager.stopSession(this.mSessionId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/WifiNanSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */