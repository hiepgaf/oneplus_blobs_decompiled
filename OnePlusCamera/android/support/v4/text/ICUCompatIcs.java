package android.support.v4.text;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ICUCompatIcs
{
  private static final String TAG = "ICUCompatIcs";
  private static Method sAddLikelySubtagsMethod;
  private static Method sGetScriptMethod;
  
  static
  {
    try
    {
      Class localClass = Class.forName("libcore.icu.ICU");
      if (localClass == null) {
        return;
      }
      sGetScriptMethod = localClass.getMethod("getScript", new Class[] { String.class });
      sAddLikelySubtagsMethod = localClass.getMethod("addLikelySubtags", new Class[] { String.class });
      return;
    }
    catch (Exception localException)
    {
      Log.w("ICUCompatIcs", localException);
    }
  }
  
  public static String addLikelySubtags(String paramString)
  {
    try
    {
      if (sAddLikelySubtagsMethod == null) {
        return paramString;
      }
      String str = (String)sAddLikelySubtagsMethod.invoke(null, new Object[] { paramString });
      return str;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Log.w("ICUCompatIcs", localIllegalAccessException);
      return paramString;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Log.w("ICUCompatIcs", localInvocationTargetException);
    }
    return paramString;
  }
  
  public static String getScript(String paramString)
  {
    try
    {
      if (sGetScriptMethod == null) {
        return null;
      }
      paramString = (String)sGetScriptMethod.invoke(null, new Object[] { paramString });
      return paramString;
    }
    catch (IllegalAccessException paramString)
    {
      Log.w("ICUCompatIcs", paramString);
      return null;
    }
    catch (InvocationTargetException paramString)
    {
      Log.w("ICUCompatIcs", paramString);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/text/ICUCompatIcs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */