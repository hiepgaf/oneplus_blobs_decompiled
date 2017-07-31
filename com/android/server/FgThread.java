package com.android.server;

import android.os.Handler;
import android.os.Looper;

public final class FgThread
  extends ServiceThread
{
  private static Handler sHandler;
  private static FgThread sInstance;
  
  private FgThread()
  {
    super("android.fg", 0, true);
  }
  
  private static void ensureThreadLocked()
  {
    if (sInstance == null)
    {
      sInstance = new FgThread();
      sInstance.start();
      sInstance.getLooper().setTraceTag(64L);
      sHandler = new Handler(sInstance.getLooper());
    }
  }
  
  public static FgThread get()
  {
    try
    {
      ensureThreadLocked();
      FgThread localFgThread = sInstance;
      return localFgThread;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/FgThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */