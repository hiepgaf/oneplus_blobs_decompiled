package com.android.server;

import android.os.Handler;
import android.os.Looper;

public final class DisplayThread
  extends ServiceThread
{
  private static Handler sHandler;
  private static DisplayThread sInstance;
  
  private DisplayThread()
  {
    super("android.display", -4, false);
  }
  
  private static void ensureThreadLocked()
  {
    if (sInstance == null)
    {
      sInstance = new DisplayThread();
      sInstance.start();
      sInstance.getLooper().setTraceTag(64L);
      sHandler = new Handler(sInstance.getLooper());
    }
  }
  
  public static DisplayThread get()
  {
    try
    {
      ensureThreadLocked();
      DisplayThread localDisplayThread = sInstance;
      return localDisplayThread;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/DisplayThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */