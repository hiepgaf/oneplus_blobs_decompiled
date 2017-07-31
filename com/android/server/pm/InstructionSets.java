package com.android.server.pm;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArraySet;
import dalvik.system.VMRuntime;
import java.util.ArrayList;
import java.util.List;

public class InstructionSets
{
  private static final String PREFERRED_INSTRUCTION_SET = VMRuntime.getInstructionSet(Build.SUPPORTED_ABIS[0]);
  
  public static String[] getAllDexCodeInstructionSets()
  {
    String[] arrayOfString = new String[Build.SUPPORTED_ABIS.length];
    int i = 0;
    while (i < arrayOfString.length)
    {
      arrayOfString[i] = VMRuntime.getInstructionSet(Build.SUPPORTED_ABIS[i]);
      i += 1;
    }
    return getDexCodeInstructionSets(arrayOfString);
  }
  
  public static List<String> getAllInstructionSets()
  {
    String[] arrayOfString = Build.SUPPORTED_ABIS;
    ArrayList localArrayList = new ArrayList(arrayOfString.length);
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = VMRuntime.getInstructionSet(arrayOfString[i]);
      if (!localArrayList.contains(str)) {
        localArrayList.add(str);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public static String[] getAppDexInstructionSets(ApplicationInfo paramApplicationInfo)
  {
    if (paramApplicationInfo.primaryCpuAbi != null)
    {
      if (paramApplicationInfo.secondaryCpuAbi != null) {
        return new String[] { VMRuntime.getInstructionSet(paramApplicationInfo.primaryCpuAbi), VMRuntime.getInstructionSet(paramApplicationInfo.secondaryCpuAbi) };
      }
      return new String[] { VMRuntime.getInstructionSet(paramApplicationInfo.primaryCpuAbi) };
    }
    return new String[] { getPreferredInstructionSet() };
  }
  
  public static String[] getAppDexInstructionSets(PackageSetting paramPackageSetting)
  {
    if (paramPackageSetting.primaryCpuAbiString != null)
    {
      if (paramPackageSetting.secondaryCpuAbiString != null) {
        return new String[] { VMRuntime.getInstructionSet(paramPackageSetting.primaryCpuAbiString), VMRuntime.getInstructionSet(paramPackageSetting.secondaryCpuAbiString) };
      }
      return new String[] { VMRuntime.getInstructionSet(paramPackageSetting.primaryCpuAbiString) };
    }
    return new String[] { getPreferredInstructionSet() };
  }
  
  public static String getDexCodeInstructionSet(String paramString)
  {
    String str = SystemProperties.get("ro.dalvik.vm.isa." + paramString);
    if (TextUtils.isEmpty(str)) {
      return paramString;
    }
    return str;
  }
  
  public static String[] getDexCodeInstructionSets(String[] paramArrayOfString)
  {
    ArraySet localArraySet = new ArraySet(paramArrayOfString.length);
    int i = 0;
    int j = paramArrayOfString.length;
    while (i < j)
    {
      localArraySet.add(getDexCodeInstructionSet(paramArrayOfString[i]));
      i += 1;
    }
    return (String[])localArraySet.toArray(new String[localArraySet.size()]);
  }
  
  public static String getPreferredInstructionSet()
  {
    return PREFERRED_INSTRUCTION_SET;
  }
  
  public static String getPrimaryInstructionSet(ApplicationInfo paramApplicationInfo)
  {
    if (paramApplicationInfo.primaryCpuAbi == null) {
      return getPreferredInstructionSet();
    }
    return VMRuntime.getInstructionSet(paramApplicationInfo.primaryCpuAbi);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/InstructionSets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */