package com.android.server.hdmi;

import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import java.util.HashMap;

final class HdmiLogger
{
  private static final boolean DEBUG = Log.isLoggable("HDMI", 3);
  private static final long ERROR_LOG_DURATTION_MILLIS = 20000L;
  private static final boolean IS_USER_BUILD = "user".equals(Build.TYPE);
  private static final String TAG = "HDMI";
  private static final ThreadLocal<HdmiLogger> sLogger = new ThreadLocal();
  private final HashMap<String, Pair<Long, Integer>> mErrorTimingCache = new HashMap();
  private final HashMap<String, Pair<Long, Integer>> mWarningTimingCache = new HashMap();
  
  private static String buildMessage(String paramString, Pair<Long, Integer> paramPair)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("[");
    if (paramPair == null) {}
    for (int i = 1;; i = ((Integer)paramPair.second).intValue()) {
      return i + "]:" + paramString;
    }
  }
  
  static final void debug(String paramString, Object... paramVarArgs)
  {
    getLogger().debugInternal(toLogString(paramString, paramVarArgs));
  }
  
  private void debugInternal(String paramString)
  {
    if (DEBUG) {
      Slog.d("HDMI", paramString);
    }
  }
  
  static final void error(String paramString, Object... paramVarArgs)
  {
    getLogger().errorInternal(toLogString(paramString, paramVarArgs));
  }
  
  private void errorInternal(String paramString)
  {
    paramString = updateLog(this.mErrorTimingCache, paramString);
    if (!paramString.isEmpty()) {
      Slog.e("HDMI", paramString);
    }
  }
  
  private static HdmiLogger getLogger()
  {
    HdmiLogger localHdmiLogger2 = (HdmiLogger)sLogger.get();
    HdmiLogger localHdmiLogger1 = localHdmiLogger2;
    if (localHdmiLogger2 == null)
    {
      localHdmiLogger1 = new HdmiLogger();
      sLogger.set(localHdmiLogger1);
    }
    return localHdmiLogger1;
  }
  
  private static void increaseLogCount(HashMap<String, Pair<Long, Integer>> paramHashMap, String paramString)
  {
    Pair localPair = (Pair)paramHashMap.get(paramString);
    if (localPair != null) {
      paramHashMap.put(paramString, new Pair((Long)localPair.first, Integer.valueOf(((Integer)localPair.second).intValue() + 1)));
    }
  }
  
  private static boolean shouldLogNow(Pair<Long, Integer> paramPair, long paramLong)
  {
    return (paramPair == null) || (paramLong - ((Long)paramPair.first).longValue() > 20000L);
  }
  
  private static final String toLogString(String paramString, Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length > 0) {
      return String.format(paramString, paramArrayOfObject);
    }
    return paramString;
  }
  
  private static String updateLog(HashMap<String, Pair<Long, Integer>> paramHashMap, String paramString)
  {
    long l = SystemClock.uptimeMillis();
    Object localObject = (Pair)paramHashMap.get(paramString);
    if (shouldLogNow((Pair)localObject, l))
    {
      localObject = buildMessage(paramString, (Pair)localObject);
      paramHashMap.put(paramString, new Pair(Long.valueOf(l), Integer.valueOf(1)));
      return (String)localObject;
    }
    increaseLogCount(paramHashMap, paramString);
    return "";
  }
  
  static final void warning(String paramString, Object... paramVarArgs)
  {
    getLogger().warningInternal(toLogString(paramString, paramVarArgs));
  }
  
  private void warningInternal(String paramString)
  {
    paramString = updateLog(this.mWarningTimingCache, paramString);
    if (!paramString.isEmpty()) {
      Slog.w("HDMI", paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */