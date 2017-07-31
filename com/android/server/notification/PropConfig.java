package com.android.server.notification;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;

public class PropConfig
{
  private static final String UNSET = "UNSET";
  
  public static int getInt(Context paramContext, String paramString, int paramInt)
  {
    return SystemProperties.getInt(paramString, paramContext.getResources().getInteger(paramInt));
  }
  
  public static String[] getStringArray(Context paramContext, String paramString, int paramInt)
  {
    paramString = SystemProperties.get(paramString, "UNSET");
    if (!"UNSET".equals(paramString)) {
      return paramString.split(",");
    }
    return paramContext.getResources().getStringArray(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/PropConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */