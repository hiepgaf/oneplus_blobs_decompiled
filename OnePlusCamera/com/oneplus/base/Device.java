package com.oneplus.base;

import java.lang.reflect.Method;

public class Device
{
  private static final String TAG = Device.class.getSimpleName();
  private static Boolean m_IsHydrogenOS;
  private static Boolean m_IsOnePlus;
  private static Boolean m_IsOxygenOS;
  
  public static String getSystemProperty(String paramString)
  {
    try
    {
      paramString = (String)Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[] { String.class }).invoke(null, new Object[] { paramString });
      return paramString;
    }
    catch (Throwable paramString)
    {
      Log.e(TAG, "getSystemProperty() - Error when get system property", paramString);
    }
    return null;
  }
  
  public static boolean isHydrogenOS()
  {
    if (m_IsHydrogenOS != null) {
      return m_IsHydrogenOS.booleanValue();
    }
    String str = getSystemProperty("ro.build.version.ota");
    if ((str != null) && ((str.contains("Hydrogen")) || (str.contains(".H.")))) {}
    for (m_IsHydrogenOS = Boolean.valueOf(true);; m_IsHydrogenOS = Boolean.valueOf(false)) {
      return m_IsHydrogenOS.booleanValue();
    }
  }
  
  public static boolean isOnePlus()
  {
    if (m_IsOnePlus != null) {
      return m_IsOnePlus.booleanValue();
    }
    String str = getSystemProperty("ro.product.brand");
    int i;
    if ((str != null) && (str.contains("OnePlus")))
    {
      i = 1;
      if ((!isHydrogenOS()) && (!isOxygenOS()) && (i == 0)) {
        break label69;
      }
    }
    label69:
    for (m_IsOnePlus = Boolean.valueOf(true);; m_IsOnePlus = Boolean.valueOf(false))
    {
      return m_IsOnePlus.booleanValue();
      i = 0;
      break;
    }
  }
  
  public static boolean isOxygenOS()
  {
    if (m_IsOxygenOS != null) {
      return m_IsOxygenOS.booleanValue();
    }
    String str = getSystemProperty("ro.build.version.ota");
    if ((str != null) && ((str.contains("Oxygen")) || (str.contains(".O.")))) {}
    for (m_IsOxygenOS = Boolean.valueOf(true);; m_IsOxygenOS = Boolean.valueOf(false)) {
      return m_IsOxygenOS.booleanValue();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/Device.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */