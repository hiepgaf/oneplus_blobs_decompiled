package com.oneplus.sdk.utils;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public class OpAppTracker
{
  private static final String APP_TRACKER = "net.oneplus.odm.insight.tracker.AppTracker";
  public static final String DATA_MESSAGE = "message";
  private static final boolean DBG = true;
  public static final int RESULT_FAIL = -1;
  public static final int RESULT_SUCCESS = 0;
  private static final String TAG = "OpAppTracker";
  private Object mAppTrackerInst = null;
  private Method mOnEventFunc = null;
  
  public OpAppTracker(Context paramContext)
  {
    try
    {
      Class localClass = Class.forName("net.oneplus.odm.insight.tracker.AppTracker");
      this.mAppTrackerInst = localClass.getConstructor(new Class[] { Context.class }).newInstance(new Object[] { paramContext });
      Log.e("OpAppTracker", "Exception " + paramContext);
    }
    catch (Exception paramContext)
    {
      try
      {
        this.mOnEventFunc = localClass.getDeclaredMethod("onEvent", new Class[] { String.class, Map.class });
        MyLog.-wrap0("OpAppTracker", "mOnEventFunc method = " + this.mOnEventFunc);
        return;
      }
      catch (Exception paramContext)
      {
        for (;;) {}
      }
      paramContext = paramContext;
    }
    paramContext.printStackTrace();
  }
  
  public int onEvent(String paramString, Map<String, String> paramMap)
  {
    try
    {
      this.mOnEventFunc.invoke(this.mAppTrackerInst, new Object[] { paramString, paramMap });
      return 0;
    }
    catch (Exception paramString)
    {
      Log.e("OpAppTracker", "Exception " + paramString);
      paramString.printStackTrace();
    }
    return -1;
  }
  
  private static class MyLog
  {
    private static void v(String paramString1, String paramString2)
    {
      Log.e(paramString1, paramString2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/sdk/utils/OpAppTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */