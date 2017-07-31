package com.fingerprints.extension.util;

import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;

public class Logger
{
  private static final int DEBUG = 3;
  private static final int DEFAULT_LOG_LEVEL = 5;
  private static final int ERROR = 0;
  private static final int INFO = 2;
  private static final int NONE = -1;
  private static final String PROPERTY_FINGERPRINTS_DEBUG = "persist.fingerprints.dbg.level";
  private static final String TAG = "FpcExtension";
  private static final int TRACE = 5;
  private static final int VERBOSE = 4;
  private static final int WARNING = 1;
  private final String mClassName;
  private final int mLogLevel;
  
  public Logger(String paramString)
  {
    this.mClassName = paramString;
    this.mLogLevel = getLogLevel();
  }
  
  private boolean atDEBUG()
  {
    return 3 <= this.mLogLevel;
  }
  
  private boolean atERROR()
  {
    boolean bool = false;
    if (this.mLogLevel >= 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean atINFO()
  {
    return 2 <= this.mLogLevel;
  }
  
  private boolean atTRACE()
  {
    return 5 <= this.mLogLevel;
  }
  
  private boolean atVERBOSE()
  {
    return 4 <= this.mLogLevel;
  }
  
  private boolean atWARNING()
  {
    return 1 <= this.mLogLevel;
  }
  
  private int getLogLevel()
  {
    if ("user".equals(Build.TYPE)) {
      return 1;
    }
    try
    {
      Object localObject = Class.forName("android.os.SystemProperties");
      if (localObject != null)
      {
        localObject = ((Class)localObject).getMethod("getInt", new Class[] { String.class, Integer.TYPE });
        if (localObject != null)
        {
          int i = ((Integer)((Method)localObject).invoke(null, new Object[] { "persist.fingerprints.dbg.level", Integer.valueOf(5) })).intValue();
          return i;
        }
      }
    }
    catch (Exception localException) {}
    return 5;
  }
  
  public void d(String paramString)
  {
    if (atDEBUG()) {
      Log.d("FpcExtension", this.mClassName + ":" + paramString);
    }
  }
  
  public void d(String paramString, Throwable paramThrowable)
  {
    if (atDEBUG()) {
      Log.d("FpcExtension", this.mClassName + ":" + paramString, paramThrowable);
    }
  }
  
  public void e(String paramString)
  {
    if (atERROR()) {
      Log.e("FpcExtension", this.mClassName + ":" + paramString);
    }
  }
  
  public void e(String paramString, Throwable paramThrowable)
  {
    if (atERROR()) {
      Log.e("FpcExtension", this.mClassName + ":" + paramString, paramThrowable);
    }
  }
  
  public void enter(String paramString)
  {
    if (atTRACE()) {
      Log.v("FpcExtension", this.mClassName + ":" + paramString + " +");
    }
  }
  
  public void exit(String paramString)
  {
    if (atTRACE()) {
      Log.v("FpcExtension", this.mClassName + ":" + paramString + " -");
    }
  }
  
  public void i(String paramString)
  {
    if (atINFO()) {
      Log.i("FpcExtension", this.mClassName + ":" + paramString);
    }
  }
  
  public void i(String paramString, Throwable paramThrowable)
  {
    if (atINFO()) {
      Log.i("FpcExtension", this.mClassName + ":" + paramString, paramThrowable);
    }
  }
  
  public void v(String paramString)
  {
    if (atVERBOSE()) {
      Log.v("FpcExtension", this.mClassName + ":" + paramString);
    }
  }
  
  public void v(String paramString, Throwable paramThrowable)
  {
    if (atVERBOSE()) {
      Log.v("FpcExtension", this.mClassName + ":" + paramString, paramThrowable);
    }
  }
  
  public void w(String paramString)
  {
    if (atWARNING()) {
      Log.w("FpcExtension", this.mClassName + ":" + paramString);
    }
  }
  
  public void w(String paramString, Throwable paramThrowable)
  {
    if (atWARNING()) {
      Log.w("FpcExtension", this.mClassName + ":" + paramString, paramThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/util/Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */