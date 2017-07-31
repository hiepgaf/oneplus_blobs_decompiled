package com.oneplus.camera;

import com.oneplus.base.Log;
import java.lang.reflect.Field;

public final class BuildFlags
{
  public static final int ROM_VERSION;
  public static final int ROM_VERSION_CHINA = 1;
  public static final int ROM_VERSION_OVERSEAS = 2;
  private static final String TAG = "BuildFlags";
  
  static
  {
    try
    {
      Class localClass = Class.forName("com.oneplus.camera.BuildFlags_RomVersion");
      if (localClass != null)
      {
        ROM_VERSION = ((Integer)readValue(localClass, "VALUE", Integer.valueOf(1))).intValue();
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e("BuildFlags", "No BuildFlags_RomVersion class");
        Object localObject = null;
      }
      ROM_VERSION = 1;
    }
  }
  
  private static <T> T readValue(Class<?> paramClass, String paramString, T paramT)
  {
    try
    {
      Object localObject = paramClass.getDeclaredField(paramString).get(null);
      return (T)localObject;
    }
    catch (Throwable localThrowable)
    {
      Log.e("BuildFlags", "readValue() - Fail to read '" + paramString + "' from '" + paramClass.getSimpleName() + "'", localThrowable);
    }
    return paramT;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/BuildFlags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */