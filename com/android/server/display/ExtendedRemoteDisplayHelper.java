package com.android.server.display;

import android.content.Context;
import android.media.RemoteDisplay.Listener;
import android.os.Handler;
import android.util.Slog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ExtendedRemoteDisplayHelper
{
  private static final String TAG = "ExtendedRemoteDisplayHelper";
  private static Class sExtRemoteDisplayClass;
  private static Method sExtRemoteDisplayDispose;
  private static Method sExtRemoteDisplayListen;
  
  static
  {
    try
    {
      sExtRemoteDisplayClass = Class.forName("com.qualcomm.wfd.ExtendedRemoteDisplay");
      if (sExtRemoteDisplayClass != null) {
        Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay Is available. Find Methods");
      }
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        sExtRemoteDisplayListen = sExtRemoteDisplayClass.getDeclaredMethod("listen", new Class[] { String.class, RemoteDisplay.Listener.class, Handler.class, Context.class });
        try
        {
          sExtRemoteDisplayDispose = sExtRemoteDisplayClass.getDeclaredMethod("dispose", new Class[0]);
          return;
        }
        catch (Throwable localThrowable3)
        {
          Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.dispose Not available.");
        }
        localThrowable1 = localThrowable1;
        Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay Not available.");
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.listen Not available.");
        }
      }
    }
  }
  
  public static void dispose(Object paramObject)
  {
    Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.dispose");
    try
    {
      sExtRemoteDisplayDispose.invoke(paramObject, new Object[0]);
      return;
    }
    catch (IllegalAccessException paramObject)
    {
      Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.dispose-IllegalAccessException");
      ((IllegalAccessException)paramObject).printStackTrace();
      return;
    }
    catch (InvocationTargetException paramObject)
    {
      Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.dispose - InvocationTargetException");
      Throwable localThrowable = ((InvocationTargetException)paramObject).getCause();
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new RuntimeException((Throwable)paramObject);
    }
  }
  
  public static boolean isAvailable()
  {
    if ((sExtRemoteDisplayClass != null) && (sExtRemoteDisplayDispose != null) && (sExtRemoteDisplayListen != null))
    {
      Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay isAvailable() : Available.");
      return true;
    }
    Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay isAvailable() : Not Available.");
    return false;
  }
  
  public static Object listen(String paramString, RemoteDisplay.Listener paramListener, Handler paramHandler, Context paramContext)
  {
    Object localObject2 = null;
    Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.listen");
    Object localObject1 = localObject2;
    if (sExtRemoteDisplayListen != null)
    {
      localObject1 = localObject2;
      if (sExtRemoteDisplayDispose == null) {}
    }
    try
    {
      localObject1 = sExtRemoteDisplayListen.invoke(null, new Object[] { paramString, paramListener, paramHandler, paramContext });
      return localObject1;
    }
    catch (IllegalAccessException paramString)
    {
      Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.listen -IllegalAccessException");
      paramString.printStackTrace();
      return null;
    }
    catch (InvocationTargetException paramString)
    {
      Slog.i("ExtendedRemoteDisplayHelper", "ExtendedRemoteDisplay.listen - InvocationTargetException");
      paramListener = paramString.getCause();
      if ((paramListener instanceof RuntimeException)) {
        throw ((RuntimeException)paramListener);
      }
      if ((paramListener instanceof Error)) {
        throw ((Error)paramListener);
      }
      throw new RuntimeException(paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/ExtendedRemoteDisplayHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */