package com.android.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

public final class UiThread
  extends ServiceThread
{
  private static Handler sHandler;
  private static UiThread sInstance;
  
  private UiThread()
  {
    super("android.ui", -2, false);
    Process.setThreadGroup(Process.myTid(), 5);
  }
  
  private static void ensureThreadLocked()
  {
    if (sInstance == null)
    {
      sInstance = new UiThread();
      sInstance.start();
      sInstance.getLooper().setTraceTag(64L);
      sHandler = new Handler(sInstance.getLooper());
    }
  }
  
  public static UiThread get()
  {
    try
    {
      ensureThreadLocked();
      UiThread localUiThread = sInstance;
      return localUiThread;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static Handler getHandler()
  {
    try
    {
      ensureThreadLocked();
      Handler localHandler = sHandler;
      return localHandler;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/UiThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */