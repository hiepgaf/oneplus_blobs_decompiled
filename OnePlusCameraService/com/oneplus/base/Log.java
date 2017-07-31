package com.oneplus.base;

public final class Log
{
  private static volatile boolean m_PrintDebugLogs = true;
  private static volatile boolean m_PrintVerboseLogs = true;
  
  public static void d(String paramString1, String paramString2)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object paramObject)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2 + paramObject);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object paramObject1, Object paramObject2)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2 + paramObject1 + paramObject2);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3 + paramObject4);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    if (m_PrintDebugLogs) {
      android.util.Log.d(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3 + paramObject4 + paramObject5);
    }
  }
  
  public static void d(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (m_PrintDebugLogs)
    {
      paramString2 = new StringBuilder(paramString2);
      int i = 0;
      while (i < paramVarArgs.length)
      {
        paramString2.append(paramVarArgs[i]);
        i += 1;
      }
      android.util.Log.d(paramString1, paramString2.toString());
    }
  }
  
  public static void disableDebugLogs()
  {
    m_PrintDebugLogs = false;
  }
  
  public static void disableVerboseLogs()
  {
    m_PrintVerboseLogs = false;
  }
  
  public static void e(String paramString1, String paramString2)
  {
    android.util.Log.e(paramString1, paramString2);
  }
  
  public static void e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    android.util.Log.e(paramString1, paramString2, paramThrowable);
  }
  
  public static void enableDebugLogs()
  {
    m_PrintDebugLogs = true;
  }
  
  public static void enableVerboseLogs()
  {
    m_PrintVerboseLogs = true;
  }
  
  public static String formatStackTraceElement(StackTraceElement paramStackTraceElement)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramStackTraceElement.getClassName());
    localStringBuilder.append('.');
    localStringBuilder.append(paramStackTraceElement.getMethodName());
    if (paramStackTraceElement.isNativeMethod()) {
      localStringBuilder.append(" (Native method)");
    }
    String str = paramStackTraceElement.getFileName();
    if (str != null)
    {
      localStringBuilder.append(" (");
      localStringBuilder.append(str);
      int i = paramStackTraceElement.getLineNumber();
      if (i > 0)
      {
        localStringBuilder.append(':');
        localStringBuilder.append(i);
      }
      localStringBuilder.append(')');
    }
    return localStringBuilder.toString();
  }
  
  public static void printStackTrace(String paramString)
  {
    printStackTrace(paramString, Thread.currentThread().getStackTrace(), 3, -1);
  }
  
  public static void printStackTrace(String paramString, int paramInt)
  {
    printStackTrace(paramString, Thread.currentThread().getStackTrace(), 3, paramInt);
  }
  
  public static void printStackTrace(String paramString, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    printStackTrace(paramString, paramArrayOfStackTraceElement, 0, paramArrayOfStackTraceElement.length);
  }
  
  private static void printStackTrace(String paramString, StackTraceElement[] paramArrayOfStackTraceElement, int paramInt1, int paramInt2)
  {
    if (paramArrayOfStackTraceElement != null)
    {
      if (paramInt2 < 0) {
        paramInt2 = paramArrayOfStackTraceElement.length;
      }
      while (paramInt1 < paramInt2)
      {
        w(paramString, "  -> " + formatStackTraceElement(paramArrayOfStackTraceElement[paramInt1]));
        paramInt1 += 1;
        continue;
        int i = paramInt1 + paramInt2;
        paramInt2 = i;
        if (i > paramArrayOfStackTraceElement.length) {
          paramInt2 = paramArrayOfStackTraceElement.length;
        }
      }
    }
  }
  
  public static void v(String paramString1, String paramString2)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object paramObject)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2 + paramObject);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object paramObject1, Object paramObject2)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2 + paramObject1 + paramObject2);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3 + paramObject4);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    if (m_PrintVerboseLogs) {
      android.util.Log.v(paramString1, paramString2 + paramObject1 + paramObject2 + paramObject3 + paramObject4 + paramObject5);
    }
  }
  
  public static void v(String paramString1, String paramString2, Object... paramVarArgs)
  {
    if (m_PrintVerboseLogs)
    {
      paramString2 = new StringBuilder(paramString2);
      int i = 0;
      while (i < paramVarArgs.length)
      {
        paramString2.append(paramVarArgs[i]);
        i += 1;
      }
      android.util.Log.v(paramString1, paramString2.toString());
    }
  }
  
  public static void w(String paramString1, String paramString2)
  {
    android.util.Log.w(paramString1, paramString2);
  }
  
  public static void w(String paramString1, String paramString2, Throwable paramThrowable)
  {
    android.util.Log.w(paramString1, paramString2, paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */