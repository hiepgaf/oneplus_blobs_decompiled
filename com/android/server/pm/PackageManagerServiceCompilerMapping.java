package com.android.server.pm;

import android.os.SystemProperties;
import dalvik.system.DexFile;

class PackageManagerServiceCompilerMapping
{
  static final String[] REASON_STRINGS = { "first-boot", "boot", "install", "bg-dexopt", "ab-ota", "nsys-library", "shared-apk", "forced-dexopt", "core-app" };
  
  static
  {
    if (9 != REASON_STRINGS.length) {
      throw new IllegalStateException("REASON_STRINGS not correct");
    }
  }
  
  static void checkProperties()
  {
    Object localObject1 = null;
    int i = 0;
    if (i <= 8)
    {
      try
      {
        localObject2 = getSystemPropertyName(i);
        if ((localObject2 == null) || (((String)localObject2).isEmpty()) || (((String)localObject2).length() > 31)) {
          throw new IllegalStateException("Reason system property name \"" + (String)localObject2 + "\" for reason " + REASON_STRINGS[i]);
        }
      }
      catch (Exception localException)
      {
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new IllegalStateException("PMS compiler filter settings are bad.");
        }
        ((RuntimeException)localObject2).addSuppressed(localException);
        localObject1 = localObject2;
      }
      for (;;)
      {
        i += 1;
        break;
        getAndCheckValidity(i);
      }
    }
    if (localObject1 != null) {
      throw ((Throwable)localObject1);
    }
  }
  
  private static String getAndCheckValidity(int paramInt)
  {
    String str = SystemProperties.get(getSystemPropertyName(paramInt));
    if ((str != null) && (!str.isEmpty()) && (DexFile.isValidCompilerFilter(str))) {
      switch (paramInt)
      {
      }
    }
    do
    {
      return str;
      throw new IllegalStateException("Value \"" + str + "\" not valid " + "(reason " + REASON_STRINGS[paramInt] + ")");
    } while (!DexFile.isProfileGuidedCompilerFilter(str));
    throw new IllegalStateException("\"" + str + "\" is profile-guided, " + "but not allowed for " + REASON_STRINGS[paramInt]);
  }
  
  public static String getCompilerFilterForReason(int paramInt)
  {
    return getAndCheckValidity(paramInt);
  }
  
  public static String getFullCompilerFilter()
  {
    String str = SystemProperties.get("dalvik.vm.dex2oat-filter");
    if ((str == null) || (str.isEmpty())) {
      return "speed";
    }
    if ((!DexFile.isValidCompilerFilter(str)) || (DexFile.isProfileGuidedCompilerFilter(str))) {
      return "speed";
    }
    return str;
  }
  
  public static String getNonProfileGuidedCompilerFilter(String paramString)
  {
    return DexFile.getNonProfileGuidedCompilerFilter(paramString);
  }
  
  private static String getSystemPropertyName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= REASON_STRINGS.length)) {
      throw new IllegalArgumentException("reason " + paramInt + " invalid");
    }
    return "pm.dexopt." + REASON_STRINGS[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageManagerServiceCompilerMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */