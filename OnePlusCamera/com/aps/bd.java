package com.aps;

public final class bd
{
  protected static boolean a = false;
  protected static final String[] b = { "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_WIFI_STATE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_NETWORK_STATE" };
  
  protected static boolean a(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString == null) {}
    for (;;)
    {
      return false;
      if (paramString != null)
      {
        int i = 0;
        while (i < paramArrayOfString.length)
        {
          if (paramArrayOfString[i].equals(paramString)) {
            break label35;
          }
          i += 1;
        }
      }
    }
    label35:
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/bd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */