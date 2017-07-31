package com.android.server.pm;

import java.io.PrintWriter;

public class PackageManagerServiceDynamicLogConfig
{
  static void dynamicallyConfigLogTag(PackageManagerService paramPackageManagerService, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt)
  {
    paramPrintWriter.println("dynamicallyConfigLogTag, opti:" + paramInt + ", args.length:" + paramArrayOfString.length);
    paramInt = 0;
    while (paramInt < paramArrayOfString.length)
    {
      paramPrintWriter.println("dynamicallyConfigLogTag, args[" + paramInt + "]:" + paramArrayOfString[paramInt]);
      paramInt += 1;
    }
    if (paramArrayOfString.length != 3)
    {
      paramPrintWriter.println("********** Invalid argument! Get detail help as bellow: **********");
      logoutTagConfigHelp(paramPrintWriter);
      return;
    }
    paramPackageManagerService = paramArrayOfString[1];
    if ("1".equals(paramArrayOfString[2])) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println("dynamicallyConfigLogTag, tag:" + paramPackageManagerService + ", on:" + bool);
      if (!"all".equals(paramPackageManagerService)) {
        break;
      }
      PackageManagerService.DEBUG_SETTINGS = bool;
      PackageManagerService.DEBUG_PREFERRED = bool;
      PackageManagerService.DEBUG_UPGRADE = bool;
      PackageManagerService.DEBUG_DOMAIN_VERIFICATION = bool;
      PackageManagerService.DEBUG_BACKUP = bool;
      PackageManagerService.DEBUG_INSTALL = bool;
      PackageManagerService.DEBUG_REMOVE = bool;
      PackageManagerService.DEBUG_BROADCASTS = bool;
      PackageManagerService.DEBUG_SHOW_INFO = bool;
      PackageManagerService.DEBUG_PACKAGE_INFO = bool;
      PackageManagerService.DEBUG_INTENT_MATCHING = bool;
      PackageManagerService.DEBUG_PACKAGE_SCANNING = bool;
      PackageManagerService.DEBUG_VERIFY = bool;
      PackageManagerService.DEBUG_DEXOPT = bool;
      PackageManagerService.DEBUG_ABI_SELECTION = bool;
      return;
    }
    if ("install".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_INSTALL = bool;
      return;
    }
    if ("remove".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_REMOVE = bool;
      return;
    }
    if ("settings".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_SETTINGS = bool;
      return;
    }
    if ("scan".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_PACKAGE_SCANNING = bool;
      return;
    }
    if ("verify".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_VERIFY = bool;
      return;
    }
    if ("abi".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_ABI_SELECTION = bool;
      return;
    }
    if ("opt".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_DEXOPT = bool;
      return;
    }
    if ("match".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_INTENT_MATCHING = bool;
      return;
    }
    if ("info".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_PACKAGE_INFO = bool;
      return;
    }
    if ("domain".equals(paramPackageManagerService))
    {
      PackageManagerService.DEBUG_DOMAIN_VERIFICATION = bool;
      return;
    }
    paramPrintWriter.println("Failed! Invalid argument! Type cmd for help: dumpsys package log");
  }
  
  static void logoutTagConfigHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("********************** Help begin:**********************");
    paramPrintWriter.println("1 package all:  DEBUG_* ");
    paramPrintWriter.println("cmd: dumpsys package log all 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("2 package install:  DEBUG_INSTALL ");
    paramPrintWriter.println("cmd: dumpsys package log install 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("3 package remove:   DEBUG_REMOVE ");
    paramPrintWriter.println("cmd: dumpsys package log remove 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("4 package settings: DEBUG_SETTINGS ");
    paramPrintWriter.println("cmd: dumpsys package log settings 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("5 package scan:     DEBUG_PACKAGE_SCANNING ");
    paramPrintWriter.println("cmd: dumpsys package log scan 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("6 package verify:   DEBUG_VERIFY ");
    paramPrintWriter.println("cmd: dumpsys package log verify 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("7 package abi:      DEBUG_ABI_SELECTION ");
    paramPrintWriter.println("cmd: dumpsys package log abi 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("8 package opt:      DEBUG_DEXOPT ");
    paramPrintWriter.println("cmd: dumpsys package log opt 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("9 package match:    DEBUG_INTENT_MATCHING ");
    paramPrintWriter.println("cmd: dumpsys package log match 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("10 package info:    DEBUG_PACKAGE_INFO ");
    paramPrintWriter.println("cmd: dumpsys package log info 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("11 package info:    DEBUG_DOMAIN_VERIFICATION ");
    paramPrintWriter.println("cmd: dumpsys package log domain 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("********************** Help end.  **********************");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageManagerServiceDynamicLogConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */