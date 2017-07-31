package com.android.server;

import android.os.HandlerThread;
import android.os.Process;
import android.os.StrictMode;
import android.util.Slog;

public class ServiceThread
  extends HandlerThread
{
  private static final String TAG = "ServiceThread";
  private final boolean mAllowIo;
  
  public ServiceThread(String paramString, int paramInt, boolean paramBoolean)
  {
    super(paramString, paramInt);
    this.mAllowIo = paramBoolean;
  }
  
  public void run()
  {
    Process.setCanSelfBackground(false);
    if ((!this.mAllowIo) && (StrictMode.conditionallyEnableDebugLogging())) {
      Slog.i("ServiceThread", "Enabled StrictMode logging for " + getName() + " looper.");
    }
    super.run();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/ServiceThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */