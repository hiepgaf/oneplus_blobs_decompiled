package com.aps;

import android.content.Context;
import android.util.Log;

public final class bg
{
  private static String a = "";
  
  protected static void a(String paramString)
  {
    if (!paramString.equals("GPS_SATELLITE")) {}
  }
  
  protected static boolean a(Context paramContext)
  {
    if (paramContext == null)
    {
      Log.d(a, "Error: No SD Card!");
      return false;
    }
    a = paramContext.getPackageName();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/bg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */